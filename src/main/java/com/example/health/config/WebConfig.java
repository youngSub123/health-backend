package com.example.health.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174",
                "https://health-frontend-inky.vercel.app"
            ) // 👈 3000(Next)과 5173(Vue) 둘 다 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}

/* node
package com.example.health.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // "이건 설정 파일이야"
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 주소에 대해서
                .allowedOrigins("http://localhost:3000") // 3000번 포트(Next.js)에서 오는 요청은 허락해줘!
                .allowedMethods("GET", "POST", "PUT", "DELETE"); // 이 방식들은 다 괜찮아
    }
}
*/
