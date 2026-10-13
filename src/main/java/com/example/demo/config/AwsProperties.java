package com.example.demo.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@Component
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
    @NotBlank
    private String region;
    private Credentials credentials;
    private S3 s3;

    @Setter
    @Getter
    public static class Credentials {
        @NotBlank
        private String accessKey;
        @NotBlank
        private String secretKey;
    }

    @Setter
    @Getter
    public static class S3 {
        @NotBlank
        private String bucket;
    }
}

