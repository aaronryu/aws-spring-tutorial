package com.example.demo.controller;

import com.example.demo.config.AwsProperties;
import com.example.demo.controller.dto.FileResponseDto;
import com.example.demo.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileApiController {
    private final S3FileService s3FileService;
    private final S3Presigner s3Presigner;
    private final AwsProperties properties;

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

    /**
     * 강제 다운로드 (브라우저 저장 대화상자 유도)
     * 예: GET /files/download?key=images/2025/08/abc.png
     *     GET /files/download?key=...&filename=내파일.png
     */
    @GetMapping("/download")
    public ResponseEntity<Void> download(
            @RequestParam("key") String key,
            @RequestParam(value = "filename", required = false) String filename
    ) {
        String bucket = properties.getS3().getBucket();
        String name = (filename != null && !filename.isBlank())
                ? filename
                : Paths.get(key).getFileName().toString();

        // 응답 헤더(Content-Disposition)를 presign 시점에 주입
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment; filename=\"" + name + "\"")
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(5)) // 유효기간
                .build();

        String signed = s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
        return ResponseEntity.status(302).location(URI.create(signed)).build();
    }
}
