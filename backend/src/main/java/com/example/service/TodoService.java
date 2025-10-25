package com.example.service;

import com.example.dao.TodoDao;
import com.example.model.TodoBO;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
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

    public List<TodoBO> getAllTodos() {
        log.info("Service: Fetching all todos.");
        return todoDao.getAllTodos();
    }

    public TodoBO addTodo(TodoBO todo) {
        log.info("Service: Adding new todo.");
        todo.setId(UUID.randomUUID().toString());
        todo.setCompleted(false);
        todoDao.addTodo(todo);
        return todo;
    }

    public TodoBO updateTodo(String id, TodoBO todo) {
        log.info("Service: Updating todo with ID: {}", id);
        todo.setId(id);
        todoDao.updateTodo(todo);
        return todo;
    }

    public void deleteTodo(String id) {
        log.info("Service: Deleting todo with ID: {}", id);
        todoDao.deleteTodo(id);
    }
}
