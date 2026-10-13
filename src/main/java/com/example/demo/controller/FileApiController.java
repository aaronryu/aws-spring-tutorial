package com.example.demo.controller;

import com.example.demo.controller.dto.FileResponseDto;
import com.example.demo.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileApiController {
    private final S3FileService s3FileService;

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {
        String url = s3FileService.store(file);
        return ResponseEntity.created(URI.create(url)).build();
    }

    /**
     * 파일 목록 JSON
     * 예: GET /files/list?prefix=images/2025/08&max=50
     */
    @GetMapping("/list")
    public List<FileResponseDto> list(
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "max", required = false, defaultValue = "100") int max
    ) {
        return s3FileService.list(prefix, max);
    }
}
