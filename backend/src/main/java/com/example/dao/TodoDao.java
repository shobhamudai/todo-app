package com.example.dao;

import com.example.model.Todo;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TodoDao {

    private final DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
    private final String tableName = System.getenv("TABLE_NAME");

    public List<Todo> getAllTodos() {
        ScanRequest scanRequest = ScanRequest.builder().tableName(tableName).build();
        return dynamoDbClient.scan(scanRequest).items().stream()
                .map(this::fromDynamoDbItem)
                .collect(Collectors.toList());
    }

    public void addTodo(Todo todo) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(toDynamoDbItem(todo))
                .build());
    }

    public void updateTodo(String id, Todo todo) {
        Map<String, AttributeValue> itemKey = new HashMap<>();
        itemKey.put("id", AttributeValue.builder().s(id).build());

        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .updateExpression("set completed = :c")
                .expressionAttributeValues(Map.of(":c", AttributeValue.builder().bool(todo.isCompleted()).build()))
                .build());
    }

    public void deleteTodo(String id) {
        Map<String, AttributeValue> itemKey = new HashMap<>();
        itemKey.put("id", AttributeValue.builder().s(id).build());
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .build());
    }

    private Map<String, AttributeValue> toDynamoDbItem(Todo todo) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(todo.getId()).build());
        item.put("task", AttributeValue.builder().s(todo.getTask()).build());
        item.put("completed", AttributeValue.builder().bool(todo.isCompleted()).build());
        return item;
    }

    private Todo fromDynamoDbItem(Map<String, AttributeValue> item) {
        return new Todo(
                item.get("id").s(),
                item.get("task").s(),
                item.get("completed").bool()
        );
    }
}
