package com.example.messenger.service;

import com.example.messenger.model.SmileysCategory;


/**
 * Provides methods for retrieving smiley categories.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
public interface SmileysService {

    /**
     * Retrieves the list of smiley categories.
     *
     * @return an array of SmileysCategory representing the smiley categories.
     */
    SmileysCategory[] getSmileysCategoriesList();
}