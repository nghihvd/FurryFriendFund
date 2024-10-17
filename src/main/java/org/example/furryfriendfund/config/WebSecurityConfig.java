package org.example.furryfriendfund.config;

import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig   {
    @Autowired
    private AccountsService accountsService;

    @Autowired
    @Qualifier("myPasswordEncoder")
    private PasswordEncoder passwordEncoder;
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter()  {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(crsf -> crsf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/accounts/register",
                                "/accounts/login",
                                "/pets/searchByNameAndBreed",
                                "/petHealth/showPetHealth/{petID}",
                                "/events/showEvents",
                                "/pets/showListOfPets",
                                "/accounts/showDonators",
                                "/pets/getByID/",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**",
                                "/images/**").permitAll() // Cho phép tất cả truy cập

//                        .requestMatchers("/notification/otherAdminNoti",
//                                "/notification/showAdminAdoptNoti",
//                                "/notification/{notiID}/status",
//                                "/notification/showRegisNoti",
//                                "/pets/showListAllOfPets").hasAuthority("1")// Chỉ cho phép người có ROLE_ADMIN
                        .requestMatchers("/notification/memberNoti","/notification/showStaffNoti").authenticated()
                        .anyRequest().authenticated() // Các request khác đều phải authenticated
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(accountsService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }


}
