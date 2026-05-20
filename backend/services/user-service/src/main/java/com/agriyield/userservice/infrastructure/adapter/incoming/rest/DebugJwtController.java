package com.agriyield.userservice.infrastructure.adapter.incoming.rest;

import com.agriyield.userservice.core.port.outgoing.JwtTokenPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/debug")
@RequiredArgsConstructor
public class DebugJwtController {

    private final JwtTokenPort jwtTokenPort;

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String auth) {
        Map<String, Object> response = new HashMap<>();
        
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.put("error", "No Bearer token");
            response.put("auth_header", auth);
            return ResponseEntity.badRequest().body(response);
        }
        
        String token = auth.substring(7);
        var result = jwtTokenPort.validateToken(token);
        
        response.put("valid", result.isValid());
        response.put("userId", result.getUserId());
        response.put("role", result.getRole());
        response.put("error", result.getError());
        response.put("token_preview", token.substring(0, Math.min(30, token.length())) + "...");
        
        return ResponseEntity.ok(response);
    }
}
