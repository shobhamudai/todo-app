package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TodoHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
    private final String tableName = System.getenv("TABLE_NAME");
    private final Gson gson = new Gson();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String httpMethod = input.getHttpMethod();
        String path = input.getPath();

        if ("GET".equals(httpMethod) && "/todos".equals(path)) {
            return getAllTodos();
        } else if ("POST".equals(httpMethod) && "/todos".equals(path)) {
            return addTodo(input.getBody());
        } else if ("PUT".equals(httpMethod) && path.matches("/todos/[^/]+")) {
            String id = path.substring(path.lastIndexOf('/') + 1);
            return updateTodo(id, input.getBody());
        } else if ("DELETE".equals(httpMethod) && path.matches("/todos/[^/]+")) {
            String id = path.substring(path.lastIndexOf('/') + 1);
            return deleteTodo(id);
        }

        return new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("Not Found");
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        return new APIGatewayProxyResponseEvent().withStatusCode(statusCode).withHeaders(headers).withBody(body);
    }

    private APIGatewayProxyResponseEvent getAllTodos() {
        ScanRequest scanRequest = ScanRequest.builder().tableName(tableName).build();
        var items = dynamoDbClient.scan(scanRequest).items().stream()
                .map(this::fromDynamoDbItem)
                .collect(Collectors.toList());
        return createResponse(200, gson.toJson(items));
    }

    private APIGatewayProxyResponseEvent addTodo(String json) {
        Todo todo = gson.fromJson(json, Todo.class);
        todo.setId(UUID.randomUUID().toString());
        todo.setCompleted(false);
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(toDynamoDbItem(todo))
                .build());
        return createResponse(201, gson.toJson(todo));
    }

    private APIGatewayProxyResponseEvent updateTodo(String id, String json) {
        Todo todo = gson.fromJson(json, Todo.class);
        Map<String, AttributeValue> itemKey = new HashMap<>();
        itemKey.put("id", AttributeValue.builder().s(id).build());

        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .updateExpression("set completed = :c")
                .expressionAttributeValues(Map.of(":c", AttributeValue.builder().bool(todo.isCompleted()).build()))
                .build());
        return createResponse(200, gson.toJson(todo));
    }

    private APIGatewayProxyResponseEvent deleteTodo(String id) {
        Map<String, AttributeValue> itemKey = new HashMap<>();
        itemKey.put("id", AttributeValue.builder().s(id).build());
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .build());
        return createResponse(204, "");
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

    public static class Todo {
        private String id;
        private String task;
        private boolean completed;

        public Todo(String id, String task, boolean completed) {
            this.id = id;
            this.task = task;
            this.completed = completed;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}
