package com.example.demo.controller.dto;

import com.example.demo.domain.FileMetadata;

import java.time.Instant;
import java.util.UUID;

/**
 * 파일 목록 화면/JSON 응답을 위한 DTO
 * - key: S3 object key
 * - url: 퍼블릭 접근 URL (미리보기/원본열기용)
 * - size: 바이트 크기
 * - lastModified: 마지막 수정 시각
 */
public record FileResponseDto (
        UUID id,
        String key,
        String url,
        long size,
        String contentType,
        String originalFilename,
        Instant uploadedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static FileResponseDto from(FileMetadata entity) {
        return new FileResponseDto(
                entity.getId(),
                entity.getS3Key(),
                entity.getUrl(),
                entity.getSize(),
                entity.getContentType(),
                entity.getOriginalFilename(),
                entity.getUploadedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
