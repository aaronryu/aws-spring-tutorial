package com.example.demo.controller;

import com.example.demo.config.AwsProperties;
import com.example.demo.controller.dto.FileResponseDto;
import com.example.demo.service.FileService;
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
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileApiController {
    private final FileService fileService;
    private final S3Presigner s3Presigner;
    private final AwsProperties properties;

    @PostMapping("/upload")
    public ResponseEntity<FileResponseDto> upload(@RequestParam("file") MultipartFile file) {
        FileResponseDto response = fileService.uploadAndSave(file); // S3 업로드 + DB 저장
        return ResponseEntity
                .created(URI.create("/files/" + response.id()))
                .body(response);
    }

    /**
     * 파일 목록 - 최신 50개 목록을 AWS S3 가 아닌 AWS RDS 데이터베이스로부터 조회
     * 예: GET /files/list
     */
    @GetMapping("/list")
    public List<FileResponseDto> list(
            @RequestParam(value = "prefix", required = false) String prefix
//          @RequestParam(value = "max", required = false, defaultValue = "100") int max
    ) {
        return fileService.list(prefix);
    }

    /** 단건 조회 */
    @GetMapping("/{id}")
    public FileResponseDto get(@PathVariable UUID id) {
        return fileService.findById(id);
    }

    /** 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fileService.delete(id);
        return ResponseEntity.noContent().build();
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
