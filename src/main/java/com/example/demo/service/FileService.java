package com.example.demo.service;

import com.example.demo.controller.dto.FileResponseDto;
import com.example.demo.domain.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final S3FileService s3FileService;
    private final FileMetadataRepository fileMetadataRepository;

    /**
     * 업로드 + DB 저장 (원자적 유스케이스)
     */
    @Transactional
    public FileResponseDto uploadAndSave(MultipartFile file) {
        S3FileService.S3UploadedMeta meta = s3FileService.store(file);

        FileMetadata entity = new FileMetadata(
                meta.getKey(),
                meta.getUrl(),
                meta.getSize(),
                meta.getContentType(),
                meta.getOriginalFilename(),
                meta.getUploadedAt()
        );
        FileMetadata saved = fileMetadataRepository.save(entity);
        return FileResponseDto.from(saved);
    }

    /**
     * 목록 조회
     *  - 접두사(prefix) 존재 시 -> 키워드 검색 조회
     *  - 접두사(prefix) 미존재 시 -> 최신 50건 조회
     * */
    @Transactional(readOnly = true)
    public List<FileResponseDto> list(String prefix) {
        List<FileMetadata> found = StringUtils.isNotBlank(prefix)
                ? fileMetadataRepository.findByS3KeyStartingWithOrderByCreatedAtDesc(prefix)
                : fileMetadataRepository.findTop50ByOrderByCreatedAtDesc();
        return found.stream().map(FileResponseDto::from).toList();
    }

    /** 단건 조회 */
    @Transactional(readOnly = true)
    public FileResponseDto findById(UUID id) {
        return fileMetadataRepository.findById(id)
                .map(FileResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + id));
    }

    /** 삭제 (DB 레코드 + 선택적으로 S3 오브젝트) */
    @Transactional
    public void delete(UUID id) {
        FileResponseDto found = this.findById(id);
        fileMetadataRepository.deleteById(id);
        // ⚠️ 필요 시 보상 로직: S3에서 found.getS3Key() 삭제 (Bucket 정책 고려)
    }
}

