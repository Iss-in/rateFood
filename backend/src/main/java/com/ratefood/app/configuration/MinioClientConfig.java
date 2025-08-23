package com.ratefood.app.configuration;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioClientConfig {
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://31.97.231.63:9000")
                .credentials("user", "bqvPz78Lv2ql")
                .build();
    }
}
