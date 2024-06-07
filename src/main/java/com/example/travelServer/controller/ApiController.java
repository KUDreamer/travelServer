package com.example.travelServer.controller;

import com.example.travelServer.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        String fields = "name,rating,formatted_phone_number,address_components,photos,price_level,opening_hours,secondary_opening_hours";
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

    //열차나 도보만 가능한 길찾기 기능
    //포함해야 하는 건 json 파일
    //{
    //  "origin": "Humberto Delgado Airport, Portugal",
    //  "destination": "Basílica of Estrela, Praça da Estrela, 1200-667 Lisboa, Portugal"
    //}
    //위와 같은 형식으로 raw 파일의 json 형식으로 body에 담아서 post 요청해야 함
    //일단은 String 타입의 이름으로 하였으나, 요청하면 placeid와 같이 더 정확한 버전 만들 수 있음
    @PostMapping("/directions")
    public String getDirections(@RequestBody Map<String, String> request) {
        String api_key = appConfig.getApiKey();
        String url = "https://routes.googleapis.com/directions/v2:computeRoutes";


        String origin = request.get("origin");
        String destination = request.get("destination");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-Goog-Api-Key", api_key);
        headers.set("X-Goog-FieldMask", "routes.legs.steps.transitDetails");

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> originMap = new HashMap<>();
        originMap.put("address", origin);
        requestBody.put("origin", originMap);

        Map<String, Object> destinationMap = new HashMap<>();
        destinationMap.put("address", destination);
        requestBody.put("destination", destinationMap);

        requestBody.put("travelMode", "TRANSIT");
        requestBody.put("computeAlternativeRoutes", true);

        Map<String, Object> transitPreferences = new HashMap<>();
        transitPreferences.put("routingPreference", "LESS_WALKING");
        transitPreferences.put("allowedTravelModes", Collections.singletonList("TRAIN"));
        requestBody.put("transitPreferences", transitPreferences);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}