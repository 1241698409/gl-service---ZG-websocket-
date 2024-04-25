package com.baosight.gl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @ProjectName shiro-service
 * @PackName com.shiro.config
 * @ClassName WebSocketConfig
 * @Date 2023/3/1 15:12
 * @Author SY
 * @Description
 * @Version 1.0
 */
@Component
public class WebSocketConfig {
    /**
     * 这个bean的注册,用于扫描带有@ServerEndpoint的注解成为websocket,如果你使用外置的tomcat就不需要该配置文件
     */

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}