package com.example.travelServer.controller;

import com.example.travelServer.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppConfig appConfig;


    //PLACE_ID를 통해 장소 세부 정보 받기
    @GetMapping("/giveInfo")
    public ResponseEntity<String> getInfo(@RequestParam String PLACE_ID) {
        String api_url = "https://maps.googleapis.com/maps/api/place/details/json";
        String fields = "name,rating,formatted_phone_number,address_components,photos,price_level,opening_hours,secondary_opening_hours";
        String api_key = appConfig.getApiKey();
        String url = String.format("%s?fields=%s&place_id=%s&key=%s",
                api_url,fields,PLACE_ID, api_key);

        // 외부 API 호출
        String result = restTemplate.getForObject(url, String.class);

        // 결과 반환
        return ResponseEntity.ok(result);
    }

    //장소 1개에 대한 검색 결과, 근데 조금 기준을 모르겠어서 보류
    @PostMapping("/searchPlaceInfo")
    public ResponseEntity<String> searchPlaceInfo(@RequestParam String query){
        String api_url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json";
        String fields = "name,formatted_address,rating,photos";
        String language = "ko";
        String input = query;
        String inputtype = "textquery";
        String api_key = appConfig.getApiKey();
        String url = String.format("%s?fields=%s&language=%s&input=%s&inputtype=%s&key=%s",api_url, fields,language,input, inputtype, api_key);

        String result = restTemplate.getForObject(url, String.class);

        return ResponseEntity.ok(result);
    }


    //검색기능.
    //애는 디폴트값으로 50000m를 탐색하고 모든 필드를 반환
    @PostMapping("/searchByTextInfo")
    public ResponseEntity<String> searchByTextInfo(@RequestParam String query){
        String api_url = "https://maps.googleapis.com/maps/api/place/textsearch/json";
        //검색어
        String textQuery = query;

        //디폴트는 50000m 설정을 통해 거맂 조절 가능
        String radius;

        String language = "ko";

        //검색되는 타입 설정 가능
        String type = "restaurant";

        String api_key = appConfig.getApiKey();

        String url = String.format("%s?query=%s&language=%s&type=%s&key=%s",
                api_url,textQuery,language,type,api_key);

        String result = restTemplate.getForObject(url, String.class);

        return ResponseEntity.ok(result);
    }
}