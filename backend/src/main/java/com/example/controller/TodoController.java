package com.example.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.model.TodoBO;
import com.example.service.TodoService;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Singleton
public class TodoController {

    private final TodoService todoService;
    private final Gson gson;

    @Inject
    public TodoController(TodoService todoService, Gson gson) {
        this.todoService = todoService;
        this.gson = gson;
        log.info("TodoController initialized.");
    }

    public APIGatewayProxyResponseEvent getAllTodos() {
        log.info("Controller: Fetching all todos.");
        return createResponse(200, gson.toJson(todoService.getAllTodos()));
    }

    public APIGatewayProxyResponseEvent addTodo(String json) {
        log.info("Controller: Adding new todo.");
        TodoBO todo = gson.fromJson(json, TodoBO.class);
        return createResponse(201, gson.toJson(todoService.addTodo(todo)));
    }

    public APIGatewayProxyResponseEvent updateTodo(String id, String json) {
        log.info("Controller: Updating todo with ID: {}", id);
        TodoBO todo = gson.fromJson(json, TodoBO.class);
        return createResponse(200, gson.toJson(todoService.updateTodo(id, todo)));
    }

    public APIGatewayProxyResponseEvent deleteTodo(String id) {
        log.info("Controller: Deleting todo with ID: {}", id);
        todoService.deleteTodo(id);
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
