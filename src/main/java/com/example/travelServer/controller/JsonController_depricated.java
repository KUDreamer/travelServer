//package com.example.travelServer.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@RestController
//public class JsonController {
//    String apiKey = "AIzaSyD2hSfrIyKwYrbbh0ssMqIhaVJoeNgOSvo";
//    String input = "Museum%20of%20Contemporary%20Art%20Australia";
//    String fields =  "formatted_address,name,rating,opening_hours,geometry";;
//
//    @PostMapping("/submit")
//    public String acceptJson(@RequestBody String json){
//        String filePath = Paths.get("./data/server_data.json").toAbsolutePath().normalize().toString();
//
//        try{
//            FileWriter fileWriter = new FileWriter(filePath, true); // 덮어쓰기가 아니라 추가 방식
//            fileWriter.write(json);
//            fileWriter.close();
//            return "Json save completed";
//        }catch(IOException e){
//            e.printStackTrace();
//            return "Json save failed";
//        }
//    }
//
//
//}
