package org.example.furryfriendfund.config;

import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)

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
        HttpSecurity httpSecurity = http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/accounts/register","/accounts/login").permitAll()
                        .requestMatchers(
                                "/pets/searchByNameAndBreed",
                                "/accounts/changePass",
                                "/accounts/forgetpassword",
                                "/petHealth/showPetHealth/{petID}",
                                "/pets/showListOfPets",
                                "/accounts/showDonators",
                                "/donation/getAnonymousDonator",
                                "/donation/getDonateByEvent/{eventID}",
                                "/pets/getByID/",
                                "/accounts/checkAcocunt",
                                "/donation/add",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**",
                                "/images/**","/imageEvent/**","/uploads/imageEvent/**",
                                "/events/showEvents",
                                "/accounts/{accID}/verifyOTP",
                                "/accounts/resendOTP/{accountID}",
                                "/images/**",
                                "/images/{filename}",
                                "/static/**",
                                "/getConfirm/{accountID}",
                                "/donation/searchDonationsByAccountID",
                                "/donation/calculateTotalDonation",
                                "/events/{id}/getEventById").permitAll()
                        .requestMatchers("/notification/memberNoti", "/notification/showStaffNoti").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://fundfe.vercel.app","http://localhost:3000","https://trial-fe.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
