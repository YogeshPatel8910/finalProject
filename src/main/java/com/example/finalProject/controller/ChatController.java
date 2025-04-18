package com.example.finalProject.controller;

import com.example.finalProject.dto.AIResponse;
import com.example.finalProject.service.GenerateContentWithTextInput;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * REST controller for AI chat functionality.
 * Provides endpoints for users to interact with AI-powered chat features.
 * This endpoint is publicly accessible through the auth path.
 */
@RestController
@RequestMapping("/api/auth/chat")
public class ChatController {

    @Autowired
    private GenerateContentWithTextInput generateContentWithTextInput;

    /**
     * Processes a chat message and returns an AI-generated response.
     *
     * @param message The user's chat message
     * @return AI-generated response to the user's message
     * @throws HttpException If an HTTP error occurs during API communication
     * @throws IOException If an I/O error occurs during processing
     */
    @PostMapping("/messagess")
    public AIResponse chat(@RequestBody String message) throws HttpException, IOException {
        return generateContentWithTextInput.getResponse(message);
    }
}