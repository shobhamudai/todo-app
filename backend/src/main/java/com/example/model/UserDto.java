package com.example.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
public class UserDto {

    private String id;
    private String email;
    private String username;
    private Boolean emailVerified;
    private Long createdAt;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
