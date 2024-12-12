package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();  // 使用 BCrypt 演算法加密 ( 密碼加鹽 )
	}
	
	
	// 使用 HttpSecurity 配置請求授權規則
	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.authorizeHttpRequests( authorizeHttpRequests ->
            	authorizeHttpRequests
            	    .requestMatchers("/**")               // 這些 URL 不需要登錄
                	.permitAll() 
                	.anyRequest()
               	    .authenticated()                             // 其他請求需要登錄
	        )
	        .formLogin(formLogin ->                              // 使用表單登錄功能來驗證用戶
	            formLogin
	                .loginPage("/login")                         // 自定義登錄頁面
	                .loginProcessingUrl("/perform_login")        // 處理登錄請求的 URL
	                .defaultSuccessUrl("/home", true)            // 登錄成功後的預設頁面
	                .failureUrl("/login?error=true")             // 登錄失敗後的頁面
	                .permitAll()
	        )
	        .logout(logout -> 
	            logout
	            	.logoutUrl("/bank/logout")                   // 處理登出請求的 URL
	            	.logoutSuccessUrl("/bank/index")             // 登出成功後的頁面
	            	.invalidateHttpSession(true)                 // 清除 session
	            	.permitAll()                                 // 允許所有請求
	        )
	        .csrf(csrf -> csrf.disable());;
        
        	return http.build();         // http.build() 建構配置
    }

	
}
