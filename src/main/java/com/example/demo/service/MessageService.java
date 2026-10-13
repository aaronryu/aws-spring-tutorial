package com.example.demo.service;

import com.example.demo.controller.dto.MessageCreateRequestDto;
import com.example.demo.controller.dto.MessageResponseDto;
import com.example.demo.domain.Message;
import com.example.demo.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    // 생성
    public MessageResponseDto create(MessageCreateRequestDto request) {
        Message toCreate = new Message(request.getContent());
        Message created = messageRepository.save(toCreate);

        return new MessageResponseDto(
                created.getId(),
                created.getContent(),
                created.getCreatedAt()
        );
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<MessageResponseDto> findAll() {
        return messageRepository.findAll()
                .stream()
                .map(message -> new MessageResponseDto(
                        message.getId(),
                        message.getContent(),
                        message.getCreatedAt()
                ))
                .toList();
    }
}
