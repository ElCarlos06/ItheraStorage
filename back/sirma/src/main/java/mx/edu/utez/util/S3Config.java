package mx.edu.utez.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

/**
 * Configuración del cliente AWS S3 SDK v2.
 * Las credenciales se leen desde variables de entorno o archivo .env.
 * Si no se proporcionan credenciales explícitas, recurre a la cadena por defecto de AWS.
 *
 * @author Ithera Team
 */
@Slf4j
@Configuration
public class S3Config {

    @Value("${aws.access-key-id:}")
    private String accessKey;

    @Value("${aws.secret-access-key:}")
    private String secretKey;

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.s3.endpoint:}")
    private String customEndpoint;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region));

        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            log.info("Inicializando cliente S3 con credenciales estáticas para la región {}", region);
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
            ));
        } else {
            log.info("Inicializando cliente S3 con DefaultCredentialsProvider de AWS para la región {}", region);
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        if (customEndpoint != null && !customEndpoint.isBlank()) {
            log.info("Aplicando endpoint personalizado para S3: {}", customEndpoint);
            builder.endpointOverride(URI.create(customEndpoint));
        }

        return builder.build();
    }
}
