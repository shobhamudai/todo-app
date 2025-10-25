package com.example.dao;

import com.example.model.TodoBO;
import com.example.model.TodoDto;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Singleton
public class TodoDao {

    private final DynamoDbTable<TodoDto> todoTable;

    @Inject
    public TodoDao(DynamoDbEnhancedClient enhancedClient) {
        this.todoTable = enhancedClient.table("todos", TableSchema.fromBean(TodoDto.class));
        log.info("TodoDao initialized. Table name: {}", todoTable.tableName());
    }

    public List<TodoBO> getAllTodos() {
        log.info("DAO: Scanning for all todos.");
        return todoTable.scan().items().stream()
                .map(this::toBo)
                .collect(Collectors.toList());
    }

    public void addTodo(TodoBO todo) {
        log.info("DAO: Adding todo with ID: {}", todo.getId());
        todoTable.putItem(toDto(todo));
    }

    public void updateTodo(TodoBO todo) {
        log.info("DAO: Updating todo with ID: {}", todo.getId());
        todoTable.updateItem(toDto(todo));
    }

    public void deleteTodo(String id) {
        log.info("DAO: Deleting todo with ID: {}", id);
        todoTable.deleteItem(Key.builder().partitionValue(id).build());
    }

    private TodoDto toDto(TodoBO bo) {
        TodoDto dto = new TodoDto();
        dto.setId(bo.getId());
        dto.setTask(bo.getTask());
        dto.setCompleted(bo.isCompleted());
        return dto;
    }

    private TodoBO toBo(TodoDto dto) {
        return new TodoBO(
                dto.getId(),
                dto.getTask(),
                dto.isCompleted()
        );
    }
}
