package com.example.travelServer.controller;

import com.example.travelServer.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppConfig appConfig;

    private static final String API_URL = "https://maps.googleapis.com/maps/api/place/details/json";

    @GetMapping("/giveInfo")
    public ResponseEntity<String> getInfo(@RequestParam String PLACE_ID) {

        String fields = "name,rating,formatted_phone_number,address_components,photos,price_level,opening_hours,secondary_opening_hours";
        String api_key = appConfig.getApiKey();
        String url = String.format("%s?fields=%s&place_id=%s&key=%s",
                API_URL,fields,PLACE_ID, api_key);

        // 외부 API 호출
        String result = restTemplate.getForObject(url, String.class);

        // 결과 반환
        return ResponseEntity.ok(result);
    }
}