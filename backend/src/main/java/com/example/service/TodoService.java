package com.example.service;

import com.example.dao.TodoDao;
import com.example.model.Todo;

import java.util.List;
import java.util.UUID;

public class TodoService {

    private final TodoDao todoDao = new TodoDao();

    public List<Todo> getAllTodos() {
        return todoDao.getAllTodos();
    }

    public Todo addTodo(Todo todo) {
        todo.setId(UUID.randomUUID().toString());
        todo.setCompleted(false);
        todoDao.addTodo(todo);
        return todo;
    }

    public void updateTodo(String id, Todo todo) {
        todoDao.updateTodo(id, todo);
    }

    public void deleteTodo(String id) {
        todoDao.deleteTodo(id);
    }
}
