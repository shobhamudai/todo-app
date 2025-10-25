package com.example.config;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Singleton;

@Module
public class DynamoDbModule {

    @Provides
    @Singleton
    public DynamoDbClient provideDynamoDbClient() {
        return DynamoDbClient.builder().build();
    }
}
