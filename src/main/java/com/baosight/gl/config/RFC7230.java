//package com.baosight.gl.config;
//
//import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RFC7230 {
//    /**
//     * 解决Tomcat RFC7230问题
//     * @return
//     */
//    @Bean
//    public ConfigurableServletWebServerFactory webServerFactory() {
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
//            connector.setProperty("relaxedQueryChars", "|{}[](),/:;<=>?@[\\]{}\\");
//            connector.setProperty("relaxedPathChars", "|{}[](),/:;<=>?@[\\]{}\\");
//            connector.setProperty("rejectIllegalHeader", "false");
//        });
//
//        return factory;
//    }
//}
