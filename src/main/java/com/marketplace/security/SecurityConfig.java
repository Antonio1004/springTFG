package com.marketplace.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final RequestFilter requestFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(UserDetailsService userDetailsService,
                          RequestFilter requestFilter,
                          CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.requestFilter = requestFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.addAllowedOrigin("https://mi-app.vercel.app");
                corsConfig.addAllowedMethod("*");
                corsConfig.addAllowedHeader("*");
                corsConfig.setAllowCredentials(true);
                return corsConfig;
            }))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth

            	    // Swagger libre
            	    .requestMatchers(
            	        "/swagger-ui/**",
            	        "/v3/api-docs/**",
            	        "/swagger-ui.html",
            	        "/swagger-resources/**",
            	        "/webjars/**"
            	    ).permitAll()

            	    //  Usuarios: estas rutas son públicas
            	    .requestMatchers(
            	        "/users/registrar",
            	        "/users/login",
            	        "/users/verify",
            	        "/users/forgot-password",
            	        "/users/reset-password"
            	    ).permitAll()
            	    
            	    //  Todo lo demás dentro de /users requiere login
            	    .requestMatchers("/users/list").hasRole("ADMIN")
            	    .requestMatchers("/users/**").authenticated()

            	    //  Productos requiere estar logueado
            	    .requestMatchers("/productos/admin/**").hasRole("ADMIN")
            	    .requestMatchers("/productos/**").authenticated()


            	    //  Mensajes requiere estar logueado
            	    .requestMatchers("/mensajes/**").authenticated()

            	    //  Valoraciones requiere estar logueado
            	    .requestMatchers("/valoracion/**").authenticated()

            	    //  Cualquier otra ruta (si existe) permitida
            	    .anyRequest().permitAll()
            	);

        http.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
