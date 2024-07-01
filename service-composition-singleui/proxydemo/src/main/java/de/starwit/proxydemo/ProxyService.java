package de.starwit.proxydemo;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;

@Service
public class ProxyService {
    @Value(value = "${logo-service.host}")
    private String domain;

    private Logger log = LoggerFactory.getLogger(ProxyService.class);

    @Retryable(exclude = {
            HttpStatusCodeException.class}, include = Exception.class, backoff = @Backoff(delay = 5000, multiplier = 4.0), maxAttempts = 4)
    public ResponseEntity<String> processProxyRequest(String body,
                                                      HttpMethod method, 
                                                      HttpServletRequest request, 
                                                      HttpServletResponse response, 
                                                      String traceId) throws URISyntaxException {
        ThreadContext.put("traceId", traceId);
        //remove app context path, TODO automate
        String requestUrl = request.getRequestURI().replace("/api/proxy","");
        URI uri = new URI(domain);

        // replacing context path form urI to match actual gateway URI
        uri = UriComponentsBuilder.fromUri(uri)
                .path(requestUrl)
                .query(request.getQueryString())
                .build(true).toUri();

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.set(headerName, request.getHeader(headerName));
        }
        
        headers.set("TRACE", traceId);
        headers.remove(HttpHeaders.ACCEPT_ENCODING);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);

        try {
            //execute for binary content
            ResponseEntity<String> serverResponse = restTemplate.exchange(uri, method, httpEntity, String.class);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.put(HttpHeaders.CONTENT_TYPE, serverResponse.getHeaders().get(HttpHeaders.CONTENT_TYPE));

            log.info(serverResponse.getStatusCode().toString() + " - " + concatenateStringlist(serverResponse.getHeaders().get(HttpHeaders.CONTENT_TYPE)));
            return serverResponse;
        } catch (HttpStatusCodeException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getRawStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
    }

    @Recover
    public ResponseEntity<String> recoverFromRestClientErrors(Exception e, String body,
                                                              HttpMethod method, HttpServletRequest request, HttpServletResponse response, String traceId) {
        log.error("retry method for the following url " + request.getRequestURI() + " has failed" + e.getMessage());
        log.error(e.getMessage());
        throw new RuntimeException("There was an error trying to process you request. Please try again later");
    }

    private String concatenateStringlist(List<String> strings) {
        StringBuffer sb = new StringBuffer();
        strings.forEach(s -> sb.append(s + "; "));
        return sb.toString();
    }
}
