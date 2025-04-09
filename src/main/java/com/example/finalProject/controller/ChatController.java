package com.example.finalProject.controller;



import com.example.finalProject.dto.AIResponse;
import com.example.finalProject.service.GenerateContentWithTextInput;
import org.apache.http.HttpException;
//import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/chat")
public class ChatController {

    @Autowired
    private GenerateContentWithTextInput generateContentWithTextInput;

//    @Autowired
//    private VertexAiGeminiChatModel chatModel;

//    private final ChatClient chatClient;
//
//    public ChatController(ChatClient.Builder builder){
//        this.chatClient = builder.build();
//    }

//    @GetMapping("/message")
//    public String getChat(@RequestParam String message){
//        return chatClient
//                .prompt(message)
//                .call()
//                .content();
//    }

    @PostMapping("/messagess")
    public AIResponse chat(@RequestBody String message) throws HttpException, IOException {
        return generateContentWithTextInput.getResponse(message);
    }
//    @GetMapping("/ai/generate")
//    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
//        return Map.of("generation", this.chatModel.call(message));
//    }
//
//    @GetMapping("/ai/generateStream")
//    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
//        Prompt prompt = new Prompt(new UserMessage(message));
//        return this.chatModel.stream(prompt);
//    }
}