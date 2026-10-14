package com.example.demo.repository;

import com.example.demo.domain.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
    Optional<FileMetadata> findByS3Key(String s3Key);
    List<FileMetadata> findTop50ByOrderByCreatedAtDesc();
    List<FileMetadata> findByS3KeyStartingWithOrderByCreatedAtDesc(String prefix);
}