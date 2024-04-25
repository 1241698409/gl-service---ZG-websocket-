package com.baosight.gl.socket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

/**
 * @ProjectName shiro-service
 * @PackName com.shiro.socket
 * @ClassName AbstractWebSocketService
 * @Date 2023/3/3 15:41
 * @Author SY
 * @Description
 * @Version 1.0
 */
@Log4j2
@SuppressWarnings("all")
public abstract class AbstractWebSocketService {

    /**
     * 声明当前会话变量：session
     */
    private Session session;

    /**
     * 声明当前会话参数：param
     */
    private String param;

    /**
     * 循环给所有页面传递数据的标识
     * true: 循环传递
     * false: 停止循环
     */
    private static Boolean RUN_FLAG = false;


    /**
     * concurrent包：集合线程安全
     * 用来存放每个页面连接对应的WebSocket对象
     */
    protected CopyOnWriteArraySet copyOnWriteArraySet;

    protected ConcurrentHashMap concurrentHashMap;

    /**
     * 循环读取数据间隔,单位ms
     * 默认为1min(60*1000ms),实现类中应该根据自己需要修改这个属性
     */
    public Integer refreshInterval = 60 * 1000;
//    public CountDownLatch countDownLatch=new CountDownLatch(4);
    /**
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("param") String param) {
        // 初始化变量
        init();
        // 初始化当前连接参数
        this.session = session;
        this.param = param;
        // 添加当前连接到copyOnWriteArraySet
        copyOnWriteArraySet.add(this);
        // 判断是否存在活跃页面
        if (copyOnWriteArraySet.size() == 1) {
            RUN_FLAG = !RUN_FLAG;
            // 开始广播
            sendAllMessage();
        } else {
            RUN_FLAG = true;

        }
        // 建立连接时：根据参数，往当前画面推送信息
        /*this.sendMessage(session, query());*/
        log.info("【webSocket连接成功】当前连接人数为：" + copyOnWriteArraySet.size());
    }

    /**
     * @param session
     * @param message
     *
     * * ws接受客户端消息
     *
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        this.param = message;
        log.info("【webSocket接收成功】当前接收内容为：" + message);
    }

    /**
     *
     */
    @OnClose
    public void onClose() {
        copyOnWriteArraySet.remove(this);
        log.info("【webSocket退出成功】当前连接人数为：" + copyOnWriteArraySet.size());
    }

    /**
     * @param session
     * @param e
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("【webSocket异常报错】当前异常报错为：" + e.toString());
    }

    /**
     * @param bean
     * @param data
     */
    public void sendAllMessage() {

        // 启动线程推送数据到web端：利用线程睡眠控制推送频率
        new Thread(() -> {
            // 开启while循环
            while (RUN_FLAG) {
                try {
                    // 遍历copyOnWriteArraySet集合
                    for (Object bean : copyOnWriteArraySet) {
                        // 模拟报错：
                       /* String name = "sy";
                        Integer.parseInt(name);*/

                        // 获取当前连接session字段
                        Field sessionField = AbstractWebSocketService.class.getDeclaredField("session");
                        Session session = (Session) sessionField.get(bean);
                        // 获取当前连接param字段
                        Field paramField = AbstractWebSocketService.class.getDeclaredField("param");
                        String param = (String) paramField.get(bean);
                        Field Field = AbstractWebSocketService.class.getDeclaredField("RUN_FLAG");
                        Boolean RUN_FLAG = (Boolean) Field.get(bean);
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonObject;
                        jsonObject = objectMapper.readTree(param);
                        JsonNode numbersNode = jsonObject.get("resultids");
                        int[] resultids = objectMapper.convertValue(numbersNode, int[].class);
                        // 打印当前连接参数
//                        System.out.println("param:" + param);
                        System.out.println("RUN_FLAG:" + RUN_FLAG);
                        /**
                         * 根据param：获取数据集合
                         * 因为同一幅画面，数据展示不同：
                         * 则需要根据前台param查询对应展示数据集合，可能会导致查询数据集合过慢
                         * 优化意见：
                         * 可以把全部接口数据通过定时器提前获取完毕，放到redis缓存中，从缓存中根据类型获取数据集合
                         */
                        //历史回放和定时刷新只需要参数不一样调用不一样的接口,控制不一样的数据频率
                        //画面刷新转递 画面name
                        //历史回放，前端点击时间选择后查询resultids 并把resultids  name starttime  endtime  multiple (倍数) status 回放状态
                        //nowresultid拖拽进度条之后的resultid

                        String message = query(param);
//                        // sendText发送获取到的数据集合到前台
////                        session.getBasicRemote().sendText(message);
                        session.getAsyncRemote().sendText(message);
                    }
                    // 遍历copyOnWriteArraySet集合：启动线程sleep
                    Thread.sleep(refreshInterval);
                } catch (Exception e) {
                    log.error("sendAllMessage：推送数据失败：" + e.toString());
                    break;
                }
            }
        }).start();
    }

    /**
     * 发送消息
     * <p>
     * session.getBasicRemote().sendText(message);  //同步发送
     * session.getAsyncRemote().sendText(message);  //异步发送
     *
     * @param session 会话
     * @param message 消息
     */
    private void sendMessage(Session session, String message) {
        session.getAsyncRemote().sendText(message);
    }
//private void sendMessage(Session session, ArrayList<Map> message) {
//        session.getAsyncRemote().sendObject(message);
//    }

    public abstract void init();

    public abstract String query(String param);

}
