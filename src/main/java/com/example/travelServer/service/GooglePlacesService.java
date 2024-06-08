package com.example.travelServer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;

@Service
public class GooglePlacesService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public GooglePlacesService(RestTemplate restTemplate, @Value("${google.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String getPlaceDetails(String placeId) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=%s", placeId, apiKey);
        return restTemplate.getForObject(url, String.class);
    }

    public Resource getPhoto(String photoReference, int maxWidth) throws MalformedURLException {
        String url = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=%d&photo_reference=%s&key=%s", maxWidth, photoReference, apiKey);
        return new UrlResource(url);
    }
}
