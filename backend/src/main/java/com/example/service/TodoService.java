package com.example.service;

import com.example.dao.TodoDao;
import com.example.model.Todo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class TodoService {

    private final TodoDao todoDao;

    @Inject
    public TodoService(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

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
