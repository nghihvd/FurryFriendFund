package org.example.furryfriendfund.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
/*
cơ chế CORS (Cros-origin resource sharing) cho phép hoặc hạn chế yêu cầu từ nhiều
nguồn khác nhau, đảm bảo tính báo mật vì chỉ cho phép truy cập các nguồn đã setting
 */
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")// áp dụng cho tất cả các đường dẫn
                .allowedOrigins("http://localhost:3000") //cho phép truy cập từ reacjs
                .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS") // các phương thức HTTP cho phép
                .allowedHeaders("*")
                .allowCredentials(true);//cho phép các xác thực cookie

    }
}
