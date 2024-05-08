package com.example.travelServer.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

@RestController
public class JsonController {

    @PostMapping("/submit")
    public String acceptJson(@RequestBody String json){
        String filePath = Paths.get("server_data.json").toAbsolutePath().normalize().toString();

        try{
            FileWriter fileWriter = new FileWriter(filePath, true); // 덮어쓰기가 아니라 추가 방식
            fileWriter.write(json);
            fileWriter.close();
            return "Json save completed";
        }catch(IOException e){
            e.printStackTrace();
            return "Json save failed";
        }
    }
}
