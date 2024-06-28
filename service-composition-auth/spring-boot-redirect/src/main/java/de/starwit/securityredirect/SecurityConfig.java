package de.starwit.securityredirect;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public-endpoint").permitAll()
                .requestMatchers("/index.html").permitAll()
                .requestMatchers("/admin-endpoint").hasAuthority("admin")
                .requestMatchers("/protected-endpoint-with-role").hasAnyAuthority("admin", "write")
                .anyRequest().authenticated()
            )
            .oauth2Login(Customizer.withDefaults())
            .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler()));

        return http.build();
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
		OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
				new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);

		// Sets the location that the End-User's User Agent will be redirected to
		// after the logout has been performed at the Provider
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

		return oidcLogoutSuccessHandler;
	}

    // Taken from https://stackoverflow.com/questions/74939220/classnotfoundexception-org-springframework-security-oauth2-server-resource-web
    @Component
    @RequiredArgsConstructor
    static class GrantedAuthoritiesMapperImpl implements GrantedAuthoritiesMapper {

        @Override
        public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof SimpleGrantedAuthority) {
                    mappedAuthorities.add(authority);
                }
                if (authority instanceof OidcUserAuthority) {
                    try {
                        final var oidcUserAuthority = (OidcUserAuthority) authority;
                        final List<String> roles = (List<String>) oidcUserAuthority.getUserInfo().getClaimAsMap("realm_access").get("roles");

                        mappedAuthorities.addAll(roles.stream().map(SimpleGrantedAuthority::new).toList());
                    } catch (NullPointerException e) {
                        LOG.error("Could not read the roles from claims.realm_access.roles -- Is the Mapper set up correctly?");
                    }
                } else if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {
                    final var userAttributes = oauth2UserAuthority.getAttributes();
                    final Map<String, List<String>> realmAccess = (Map<String, List<String>>) userAttributes.get("realm_access");
                    final List<String> roles = realmAccess.get("roles");

                    mappedAuthorities.addAll(roles.stream().map(SimpleGrantedAuthority::new).toList());
                }
            });

            return mappedAuthorities;
        }

    }
     
}
