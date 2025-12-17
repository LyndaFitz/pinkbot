package com.pinkbot.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // allow your "Go Live" frontend to call backend
public class HelloController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "message", "PinkBot backend is alive 💖");
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> body) {
        String userMessage = body.getOrDefault("message", "");
        String reply = "PinkBot heard: " + userMessage + " 💖";
        return Map.of("reply", reply);
    }
}
