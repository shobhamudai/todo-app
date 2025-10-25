package com.example.config;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Singleton;

@Module
public class DynamoDbModule {

    @Provides
    @Singleton
    public DynamoDbEnhancedClient provideDynamoDbEnhancedClient() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}
