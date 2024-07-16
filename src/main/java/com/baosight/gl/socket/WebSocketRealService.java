package com.baosight.gl.socket;

import com.baosight.gl.controller.GlController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @ProjectName shiro-service
 * @PackName com.shiro.socket
 * @ClassName WebSocketRealService
 * @Date 2023/3/7 14:57
 * @Author SY
 * @Description
 * @Version 1.0
 */
@Component
//@ServerEndpoint("/websocket/{param}")
public class WebSocketRealService extends AbstractWebSocketService {

    static CopyOnWriteArraySet<WebSocketRealService> copyOnWriteArraySet = new CopyOnWriteArraySet<>();

    static ConcurrentHashMap<Session, WebSocketRealService> concurrentHashMap = new ConcurrentHashMap();
//使用了@ServerEndpoint注解的类中使用@Resource或@Autowired注入都会失败，并且报出空指针异常。
//原因是WebSocket服务是线程安全的，那么当我们去发起一个ws连接时，就会创建一个端点对象。
//WebSocket服务是多对象的，不是单例的。
//而我们的Spring的Bean默认就是单例的，在非单例类中注入一个单例的Bean是冲突的。
    @Autowired
    GlController glController;

    @Override
    public void init() {
        super.copyOnWriteArraySet = this.copyOnWriteArraySet;
        super.concurrentHashMap = this.concurrentHashMap;
        super.refreshInterval = 10 * 1000;
    }

    @Override
    public String query(String param) {
        // 获取当前时间
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 根据参数获取json数据项
        String data = "json：" + time + "：" + param;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonObject;
        try {
             jsonObject = objectMapper.readTree(param);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(jsonObject.get("moshi").asText().contains("定时切换")) {
            System.out.println("定时切换-");
            if(jsonObject.get("name").asText().contains("等温线")){
                data=glController.queryThermocouple("");
            }
            if(jsonObject.get("name").asText().contains("等压线")){
                data=glController.queryPressure("");
            }
            if(jsonObject.get("name").asText().contains("热力图")){
                data=glController.queryHeatMap("");
            }
            if(jsonObject.get("name").asText().contains("热负荷")){
                data=glController.queryThermalLoad("");
            }
        } else {
            //历史回放，前端点击时间选择后查询resultids 并把resultids  name starttime  endtime  multiple (倍数) status 回放状态
            //nowresultid拖拽进度条之后的resultid
            JsonNode numbersNode = jsonObject.get("resultids");
            int[] resultids = objectMapper.convertValue(numbersNode, int[].class);
            System.out.println("历史回放");

        }
                return data;
    }

}
