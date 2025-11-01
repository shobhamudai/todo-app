package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class TodoDto {

    private String id;
    private String task;
    private boolean completed;
    private Long createdAt;
    private String userId;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return this.id;
    }

    @DynamoDbAttribute("task")
    public String getTask() {
        return this.task;
    }

    @DynamoDbAttribute("completed")
    public boolean isCompleted() {
        return this.completed;
    }

    @DynamoDbAttribute("createdAt")
    public Long getCreatedAt() {
        return this.createdAt;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "userId-index")
    @DynamoDbAttribute("userId")
    public String getUserId() {
        return this.userId;
    }
}
