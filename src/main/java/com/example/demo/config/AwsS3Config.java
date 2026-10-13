package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class AwsS3Config {
    private final AwsProperties properties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(
                        StringUtils.hasText(properties.getCredentials().getAccessKey())
                                // .env에서 키가 주입된 경우: 정적 자격 증명 사용
                                ? StaticCredentialsProvider.create(
                                        AwsBasicCredentials.create(
                                                properties.getCredentials().getAccessKey(),
                                                properties.getCredentials().getSecretKey()
                                        )
                                )
                                // 그렇지 않으면: 기본 체인(환경변수, 프로파일, IAM Role)을 자동 탐색
                                : DefaultCredentialsProvider.create()
                )
                .build();
    }
}