package com.example.messenger.service;

import com.example.messenger.model.Smileys;
import com.example.messenger.model.SmileysCategory;
import com.example.messenger.service.SmileysService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class SmileysServiceImpl implements SmileysService {

    public SmileysCategory[] getSmileysCategoriesList(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Smileys smileys = objectMapper.readValue(new File("E:\\messenger_spring_boot\\src\\main\\resources\\static\\smileys.json"), Smileys.class);
            return smileys.getSmileysCategories();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return null;
    }
}
