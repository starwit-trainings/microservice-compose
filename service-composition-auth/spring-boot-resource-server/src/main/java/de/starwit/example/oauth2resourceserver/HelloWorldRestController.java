package de.starwit.example.oauth2resourceserver;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class HelloWorldRestController {

    @GetMapping("/public-endpoint")
    public String publicEndpoint() {
        return "Nothing interesting to see here, please continue.";
    }

    @GetMapping("/read-endpoint")
    public String readEndpoint(AbstractOAuth2TokenAuthenticationToken<?> authToken) {
        String username = (String) authToken.getTokenAttributes().get("preferred_username");
        List<String> authorities = authToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return String.format("Hi %s<br><br>P.S.: You have the following roles %s", username, authorities);
    }

    @GetMapping("/write-endpoint")
    public String writeEndpoint() {
        return "You have write privileges!";
    }

    @GetMapping("/admin-endpoint")
    public String adminEndpoint() {
        return "With great power comes great responsibility &#x1F4A5";
    }

}