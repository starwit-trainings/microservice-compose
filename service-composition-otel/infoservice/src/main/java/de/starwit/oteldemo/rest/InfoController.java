package de.starwit.oteldemo.rest;

import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import de.starwit.oteldemo.entities.InfoEntity;

@RestController
@RequestMapping(path = "/api/info")
public class InfoController {

    @Value("${pdf.service.url}")
    String pdfServiceUri;
    
    @GetMapping("/")
    public ResponseEntity<InfoEntity> test() {
        InfoEntity i = new InfoEntity();
        i.setCreationDate(new Date());
        i.setName("test");
        i.setDescription("descrition");
        return ResponseEntity.ok(i);
    }

    @GetMapping("/pdf")
    public ResponseEntity<InfoEntity> testCall() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        final String response = restTemplate.getForObject(pdfServiceUri, String.class);
        System.out.println(response);

        InfoEntity i = new InfoEntity();
        i.setCreationDate(new Date());
        i.setName("test");
        i.setDescription("descrition");
        return ResponseEntity.ok(i);
    }
}
