package com.example.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.model.TodoBO;
import com.example.service.TodoService;
import com.example.util.SerializationUtility;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Singleton
public class TodoController {

    private final TodoService todoService;
    private final SerializationUtility serializationUtility;

    @Inject
    public TodoController(TodoService todoService, SerializationUtility serializationUtility) {
        this.todoService = todoService;
        this.serializationUtility = serializationUtility;
    }

    public APIGatewayProxyResponseEvent getAllTodos(String userId) {
        log.info("Controller: Fetching all todos for user ID: {}", userId);
        String jsonBody = serializationUtility.serialize(todoService.getAllTodos(userId));
        return createResponse(200, jsonBody);
    }

    public APIGatewayProxyResponseEvent addTodo(String userId, String json) {
        log.info("Controller: Adding new todo for user ID: {}", userId);
        TodoBO todo = serializationUtility.deserialize(json, TodoBO.class);
        TodoBO createdTodo = todoService.addTodo(userId, todo);
        String jsonBody = serializationUtility.serialize(createdTodo);
        return createResponse(201, jsonBody);
    }

    public APIGatewayProxyResponseEvent updateTodo(String userId, String id, String json) {
        log.info("Controller: Updating todo with ID: {} for user ID: {}", id, userId);
        TodoBO todo = serializationUtility.deserialize(json, TodoBO.class);
        TodoBO updatedTodo = todoService.updateTodo(userId, id, todo);
        String jsonBody = serializationUtility.serialize(updatedTodo);
        return createResponse(200, jsonBody);
    }

    public APIGatewayProxyResponseEvent deleteTodo(String userId, String id) {
        log.info("Controller: Deleting todo with ID: {} for user ID: {}", id, userId);
        todoService.deleteTodo(userId, id);
        return createResponse(204, "");
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        return new APIGatewayProxyResponseEvent().withStatusCode(statusCode).withHeaders(headers).withBody(body);
    }
}
