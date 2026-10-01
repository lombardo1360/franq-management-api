package franq_management_api.infrastructure.dynamodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.dynamodb.endpoint:}")
    private String endpoint;

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient() {

        var builder =
                DynamoDbAsyncClient.builder()
                        .region(Region.of(region));

        if (!endpoint.isBlank()) {
            builder.endpointOverride(
                    URI.create(endpoint)
            );
        }

        return builder.build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(
            DynamoDbAsyncClient dynamoDbAsyncClient) {

        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient)
                .build();
    }
}
