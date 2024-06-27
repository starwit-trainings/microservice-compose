package de.starwit.example.oauth2resourceserver;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/").permitAll()
                .requestMatchers("/index.html").permitAll()
                .requestMatchers("/public-endpoint").permitAll()
                .requestMatchers("/read-endpoint").hasAnyAuthority("read", "write", "admin")
                .requestMatchers("/write-endpoint").hasAnyAuthority("write", "admin")
                .requestMatchers("/admin-endpoint").hasAuthority("admin")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConverter = jwt -> {
            List<String> tokenRoles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
            return tokenRoles.stream().map(r -> (GrantedAuthority) new SimpleGrantedAuthority(r)).toList();
        };

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    RouterFunction<ServerResponse> indexRouter() {
        ClassPathResource index = new ClassPathResource("static/index.html");
        return RouterFunctions.route().resource(RequestPredicates.path("/"), index).build();
    }

}
