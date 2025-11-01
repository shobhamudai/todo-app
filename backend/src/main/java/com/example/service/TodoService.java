package com.example.service;

import com.example.dao.TodoDao;
import com.example.model.TodoBO;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Log4j2
@Singleton
public class TodoService {

    private final TodoDao todoDao;

    @Inject
    public TodoService(TodoDao todoDao) {
        this.todoDao = todoDao;
        log.info("TodoService initialized.");
    }

    public List<TodoBO> getAllTodos(String userId) {
        log.info("Service: Fetching all todos for user ID: {}", userId);
        return todoDao.getAllTodosByUserId(userId);
    }

    public TodoBO addTodo(String userId, TodoBO todo) {
        log.info("Service: Adding new todo for user ID: {}", userId);
        todo.setId(UUID.randomUUID().toString());
        todo.setUserId(userId);
        todo.setCompleted(false);
        todo.setCreatedAt(Instant.now().toEpochMilli());
        todoDao.addTodo(todo);
        return todo;
    }

    public TodoBO updateTodo(String userId, String id, TodoBO todo) {
        log.info("Service: Updating todo with ID: {} for user ID: {}", id, userId);
        // You should add a check here to ensure the todo being updated belongs to the user
        todo.setId(id);
        todo.setUserId(userId);
        todoDao.updateTodo(todo);
        return todo;
    }

    public void deleteTodo(String userId, String id) {
        log.info("Service: Deleting todo with ID: {} for user ID: {}", id, userId);
        // You should add a check here to ensure the todo being deleted belongs to the user
        todoDao.deleteTodo(id);
    }
}
