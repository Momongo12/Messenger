package com.example.messenger.service;

import com.example.messenger.model.Smileys;
import com.example.messenger.model.SmileysCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Implementation of the {@link SmileysService} interface.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Service
@Log4j2
public class SmileysServiceImpl implements SmileysService {

    @Autowired
    private final ResourceLoader resourceLoader;

    public SmileysServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public SmileysCategory[] getSmileysCategoriesList(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Resource resource = resourceLoader.getResource("classpath:static/smileys/smileys.json");
            Smileys smileys = objectMapper.readValue(resource.getInputStream(),  Smileys.class);
            return smileys.getSmileysCategories();
        } catch (IOException e){
            e.printStackTrace();
            log.error(e.getMessage());
        }

        return null;
    }
}
