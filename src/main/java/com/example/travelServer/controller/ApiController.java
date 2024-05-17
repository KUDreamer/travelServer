package com.example.travelServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://maps.googleapis.com/maps/api/place/details/json";

    @Value("${google.api.key}")
    private String API_KEY;
    private static String PLACE_ID = "ChIJN1t_tDeuEmsRUsoyG83frY4";

    @GetMapping("/giveInfo")
    public ResponseEntity<String> getInfo() {
        String url = String.format("%s?fields=name,rating,formatted_phone_number&place_id=%s&key=%s",
                API_URL, PLACE_ID, API_KEY);

        // 외부 API 호출
        String result = restTemplate.getForObject(url, String.class);

        // 결과 반환
        return ResponseEntity.ok(result);
    }
}