package com.baosight.gl.socket;

import com.baosight.gl.controller.GlController;
import com.baosight.gl.utils.SpringContextUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

/**
 * @ProjectName shiro-service
 * @PackName com.shiro.socket
 * @ClassName WebSocketRealService
 * @Date 2023/3/7 14:57
 * @Author SY
 * @Description
 * @Version 1.0
 */
@Log4j2
@Component
@ServerEndpoint("/websocket/{param}")
public class WebSocketServer   {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private static final Map<String, ScheduledFuture<?>> nameTaskMap = new HashMap<>();
    private static final Map<String, ScheduledFuture<?>> resultIdsTaskMap = new HashMap<>();
    //历史回放初始值
    private int nowresultid=0;
    //倍速
    private int multiple=0;
    //模式
    private String playbackname="";
    private String status="";

    private int retryCount = 0;
    JsonObject jsonObject = new JsonObject();
    Gson gson = new Gson();
    @OnOpen
    public void onOpen(Session session) {
        //设置消息大小最大为10M
        //The default buffer size for text messages is 8192 bytes.消息超过8192b,自动断开连接
        session.setMaxTextMessageBufferSize(10*1024*1024);
        session.setMaxBinaryMessageBufferSize(10*1024*1024);
        System.out.println("WebSocket连接已建立");
        //这样做初始连接大小没有获取，
        log.info("【webSocket连接成功】当前连接人数为：" + nameTaskMap.size()+resultIdsTaskMap.size());
    }
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("收到消息: " + message);
        System.out.println(message);
        // 解析收到的JSON数据
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(message);

            if (jsonNode.has("name")) {
                String name = jsonNode.get("name").asText();
                String sessionId = session.getId();
                scheduleNameRequestHandling(name, sessionId, session);
            } else if (jsonNode.has("resultids")) {
                status = jsonNode.get("status").asText();
                String sessionId = session.getId();
                playbackname = jsonNode.get("playbackname").asText();
                if(status.equals("end")){
                    scheduleNameRequestHandling(playbackname, sessionId, session);
                }else {
                    nowresultid = jsonNode.get("nowresultid").asInt();
                    multiple = jsonNode.get("multiple").asInt();
                    List<Integer> resultIds = Arrays.asList(mapper.convertValue(jsonNode.get("resultids"), Integer[].class));

                    scheduleResultIdsRequestHandling(resultIds, sessionId, session);
                }
            } else {
                session.getBasicRemote().sendText("未知请求"+""+mapper.writeValueAsString(jsonNode));
            }
        } catch (IOException e) {
            e.printStackTrace();
            session.getBasicRemote().sendText("处理消息时发生异常");
        }
    }

    private void scheduleNameRequestHandling(String name, String sessionId, Session session) {
        // 取消之前的名称查询定时器

        if (nameTaskMap.containsKey(sessionId) && !nameTaskMap.get(sessionId).isDone()) {
            nameTaskMap.get(sessionId).cancel(true);
        }
        if (resultIdsTaskMap.containsKey(sessionId) && !resultIdsTaskMap.get(sessionId).isDone()) {
            resultIdsTaskMap.get(sessionId).cancel(true);
        }
        // 定期执行名称查询
        ScheduledFuture<?> nameTask = scheduler.scheduleAtFixedRate(() -> handleNameRequest(name, session), 0, 10, TimeUnit.SECONDS);
        nameTaskMap.put(sessionId, nameTask);
    }

    private void scheduleResultIdsRequestHandling(List<Integer> resultIds, String sessionId, Session session) {
        // 取消之前的结果ID查询定时器
        if (resultIdsTaskMap.containsKey(sessionId) && !resultIdsTaskMap.get(sessionId).isDone()) {
            resultIdsTaskMap.get(sessionId).cancel(true);
        }
        if (nameTaskMap.containsKey(sessionId) && !nameTaskMap.get(sessionId).isDone()) {
            nameTaskMap.get(sessionId).cancel(true);
        }
        // 定期执行结果ID查询
        ScheduledFuture<?> resultIdsTask = scheduler.scheduleAtFixedRate(() -> handleResultIdsRequest(resultIds, session), 0, 1, TimeUnit.SECONDS);
        resultIdsTaskMap.put(sessionId, resultIdsTask);
    }

    private void handleNameRequest(String name, Session session) {
        // 处理名称请求的逻辑
        GlController glController=SpringContextUtil.getBean(GlController.class);
        String response="";
        try {
            if ("等温线".equals(name)) {
                response=glController.queryThermocouple("");
            } else if ("等压线".equals(name)) {
                response=glController.queryThermocouple("");
            }else if ("热力图".equals(name)) {
                response=glController.queryHeatMap("");
            } else if ("热负荷".equals(name)) {
                response=glController.queryThermalLoad("");
            }  else {

            }
            session.getBasicRemote().sendText(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResultIdsRequest(List<Integer> resultIds, Session session) {
        // 处理结果ID请求的逻辑
        try {
//            StringBuilder response = new StringBuilder("查询到的数据：");
            //在websocket里会为每一个类创建一个对象，不符合java的单例要求,不能使用@Autowired注入
            GlController glController=SpringContextUtil.getBean(GlController.class);
            String response="";
//            for (int id : resultIds) {
//                response.append("结果ID ").append(id).append("; ");
//            }
            if(status.equals("start")) {
                if (playbackname.equals("等温线")) {
                    response = glController.queryThermocouple(resultIds.get(nowresultid).toString());
                } else if (playbackname.equals("热力图")) {
                    response = glController.queryHeatMap(resultIds.get(nowresultid).toString());
                } else if (playbackname.equals("热负荷")) {
                    response = glController.queryThermalLoad(resultIds.get(nowresultid).toString());
                }
                jsonObject.addProperty("nowresultid", nowresultid);
                jsonObject.addProperty("name", playbackname);
                // 使用 Gson 将对象转换为 JSON 字符串
                String jsonString = gson.toJson(jsonObject);
//                session.getBasicRemote().sendText(response + "*{"+"nowresultid"+":" + nowresultid + ","+"name"+":"+playbackname+"}");
//                session.getBasicRemote().sendText(response + "*{nowresultid:" + nowresultid + ",name:"+playbackname+"}");
                session.getBasicRemote().sendText(response + "*"+jsonString);
                if (nowresultid < resultIds.size()) {
                    if (nowresultid + multiple < resultIds.size()) {
                        nowresultid = nowresultid + multiple;
                    } else {
                        nowresultid = resultIds.size() - 1;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("【webSocket异常报错】当前异常报错为：" + e.toString());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket连接已关闭");
        // 关闭 scheduler，否则应用程序不会退出
        scheduler.shutdown();
        // 移除关闭的会话的定时器任务
        nameTaskMap.remove(session.getId());
        resultIdsTaskMap.remove(session.getId());
        log.info("【webSocket退出成功】当前连接人数为：" + nameTaskMap.size()+resultIdsTaskMap.size());

//        retryConnect();
    }
//    private void retryConnect() {
//        if (retryCount < 5) {
//            try {
//                Thread.sleep(1000); // 等待1秒后重试连接
//                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//                container.connectToServer(this, URI.create("ws://localhost:7072/websocket/real"));
//                retryCount++;
//            } catch (Exception e) {
//                System.out.println("Error reconnecting: " + e.getMessage());
//                retryConnect(); // 如果连接失败，继续重试连接
//            }
//        } else {
//            System.out.println("Max retries reached, giving up.");
//        }
//    }

}
