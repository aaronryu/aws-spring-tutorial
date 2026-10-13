package com.example.demo.controller;

import com.example.demo.controller.dto.MessageCreateRequestDto;
import com.example.demo.controller.dto.MessageResponseDto;
import com.example.demo.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // 메시지 생성
    @PostMapping
    public ResponseEntity<MessageResponseDto> create(@Valid @RequestBody MessageCreateRequestDto request) {
        MessageResponseDto created = messageService.create(request);
        return ResponseEntity.created(URI.create("/messages/" + created.getId())).body(created);
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> findAll() {
        return ResponseEntity.ok(messageService.findAll());
    }
}