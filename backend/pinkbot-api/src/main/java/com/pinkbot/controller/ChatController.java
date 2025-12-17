package com.pinkbot.controller;

import com.pinkbot.service.OpenAiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ChatController {

    // 🔴 THIS LINE WAS MISSING
    private final OpenAiService openAiService;

    public ChatController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping(
            value = "/chat",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ChatResponse chat(@RequestBody ChatRequest req) {
        String reply = openAiService.reply(req.message());
        return new ChatResponse(reply);
    }

    public record ChatRequest(String message) {}
    public record ChatResponse(String reply) {}
}
