package com.example.travelServer.controller;

import com.example.travelServer.config.AppConfig;
import com.example.travelServer.service.GooglePlacesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private GooglePlacesService googlePlacesService;


    //PLACE_ID를 통해 장소 세부 정보 받기
    //작성 예: http://localhost:8080/api/giveInfo?PLACE_ID=ChIJN1t_tDeuEmsRUsoyG83frY4%26
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
    //작성예: http://localhost:8080/api/searchPlaceInfo?query=중국 음식
    @PostMapping("/searchPlaceInfo")
    public ResponseEntity<String> searchPlaceInfo(@RequestParam String query){
        String api_url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json";
        String fields = "name,formatted_address,rating,photos,business_status,opening_hours,user_ratings_total";
        String language = "ko";
        String input = query;
        String inputtype = "textquery";
        String api_key = appConfig.getApiKey();
        String url = String.format("%s?fields=%s&language=%s&input=%s&inputtype=%s&key=%s",api_url, fields,language,input, inputtype, api_key);

        String result = restTemplate.getForObject(url, String.class);
        System.out.println(query);

        return ResponseEntity.ok(result);
    }


    //검색기능.
    //애는 디폴트값으로 50000m를 탐색하고 모든 필드를 반환
    //http://localhost:8080/api/searchByTextInfo?query=중국집
    @PostMapping("/searchByTextInfo")
    public ResponseEntity<String> searchByTextInfo(@RequestParam("query") String query, @RequestParam("place_type") String place_type){
        String api_url = "https://maps.googleapis.com/maps/api/place/textsearch/json";
        //검색어
        String textQuery = query;

        //디폴트는 50000m 설정을 통해 거맂 조절 가능
        String radius;

        String language = "ko";

        //검색되는 타입 설정 가능
//        String type = "restaurant";
        String type = place_type;

        String api_key = appConfig.getApiKey();


        String url = String.format("%s?query=%s&language=%s&type=%s&key=%s",
                api_url,textQuery,language,type,api_key);

        String result = restTemplate.getForObject(url, String.class);

        return ResponseEntity.ok(result);
    }

    //열차나 도보만 가능한 길찾기 기능
    //작성예: http://localhost:8080/api/directionsTrain
    //다만 body에 아래 형식의 json 파일을 포함해야 함
    //{
    //  "origin": "Humberto Delgado Airport, Portugal",
    //  "destination": "Basílica of Estrela, Praça da Estrela, 1200-667 Lisboa, Portugal"
    //}
    //위와 같은 형식으로 raw 파일의 json 형식으로 body에 담아서 post 요청해야 함
    //일단은 String 타입의 이름으로 하였으나, 요청하면 placeid와 같이 더 정확한 버전 만들 수 있음
    @PostMapping("/directionsTrain")
    public String getDirectionsTrain(@RequestBody Map<String, String> request) {
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

    //작성예시: http://localhost:8080/api/placePhoto?
    // photoReference=AUGGfZnSHjsfLCLJogZWwe5Jrfwae_VUhGDiYszJmPNNHzWQG58_HwAk122fH0FMhEcs1rz6Ny9cm_KgTbSW6_7g7B3t2KntyM7F53F-ElET4Tr3EzOPpYcfoMj9nGUkl06efNLvbigBzpryktPXF0ZLBYCCm5FcW0HSuLiOkWLcEiT2BXjI
    // &maxWidth=400
    //photoReference에 입력하면 됨.
    @GetMapping("/placePhoto")
    public ResponseEntity<Map<String, Object>> getPlaceInfo(@RequestParam String photoReference, @RequestParam int maxWidth) {
        Map<String, Object> response = new HashMap<>();


        // Get Place Photo
        try {
            Resource photo = googlePlacesService.getPhoto(photoReference, maxWidth);
            response.put("photo", photo.getURL().toString());
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/directions")
    public String getDirections(@RequestBody Map<String, Object> request) {
        try {
            String api_key = appConfig.getApiKey();

            String url = "https://routes.googleapis.com/directions/v2:computeRoutes";

            Map<String, Object> origin = (Map<String, Object>) request.get("origin");
            Map<String, Object> destination = (Map<String, Object>) request.get("destination");
            List<Map<String, Object>> intermediates = (List<Map<String, Object>>) request.get("intermediates");
            String travelMode = (String) request.get("travelMode");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("X-Goog-Api-Key", api_key);
            headers.set("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.legs,geocodingResults");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("origin", origin);
            requestBody.put("destination", destination);
            requestBody.put("travelMode", travelMode);

            List<Map<String, Object>> intermediatePoints = intermediates.stream()
                    .map(intermediate -> {
                        Map<String, Object> point = new HashMap<>();
                        if (intermediate.containsKey("address")) {
                            point.put("address", intermediate.get("address"));
                        } else if (intermediate.containsKey("location")) {
                            point.put("location", intermediate.get("location"));
                        }
                        return point;
                    })
                    .collect(Collectors.toList());

            requestBody.put("intermediates", intermediatePoints);

            String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

            HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);



            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    //입력된 경유지들의 최적화된 경로를 반환해줌
    //작성예: http://localhost:8080/api/directionsDrive
    //포함해야 할 json body는 아래의 형식과 같음
//    {
//        "origin": "Adelaide,SA",
//            "destination": "Adelaide,SA",
//            "intermediates": [
//        "Barossa+Valley,SA",
//                "Clare,SA",
//                "Connawarra,SA",
//                "McLaren+Vale,SA"
//    ]
//    }
//    {
//        "routes": [
//        {
//            "optimizedIntermediateWaypointIndex": [
//            3,
//                    2,
//                    0,
//                    1
//      ]
//        }
//  ]

    //반환된 형식은 위와 같이 반환됨
    @PostMapping("/wayOptimization")
    public String getDirectionsDrive(@RequestBody Map<String, Object> request) {
        String api_key = appConfig.getApiKey();
        String url = "https://routes.googleapis.com/directions/v2:computeRoutes";

        String origin = (String) request.get("origin");
        String destination = (String) request.get("destination");
        List<String> intermediates = (List<String>) request.get("intermediates");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-Goog-Api-Key", api_key);
        headers.set("X-Goog-FieldMask", "routes.optimizedIntermediateWaypointIndex");

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> originMap = new HashMap<>();
        originMap.put("address", origin);
        requestBody.put("origin", originMap);

        Map<String, Object> destinationMap = new HashMap<>();
        destinationMap.put("address", destination);
        requestBody.put("destination", destinationMap);

        List<Map<String, String>> intermediatePoints = intermediates.stream()
                .map(address -> {
                    Map<String, String> point = new HashMap<>();
                    point.put("address", address);
                    return point;
                })
                .collect(Collectors.toList());

        requestBody.put("intermediates", intermediatePoints);
        requestBody.put("travelMode", "DRIVE");
        requestBody.put("optimizeWaypointOrder", "true");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}