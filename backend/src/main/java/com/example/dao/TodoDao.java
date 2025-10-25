package com.example.dao;

import com.example.model.TodoBO;
import com.example.model.TodoDto;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class TodoDao {

    private final DynamoDbTable<TodoDto> todoTable;

    @Inject
    public TodoDao(DynamoDbEnhancedClient enhancedClient) {
        this.todoTable = enhancedClient.table("todos", TableSchema.fromBean(TodoDto.class));
    }

    public List<TodoBO> getAllTodos() {
        return todoTable.scan().items().stream()
                .map(this::toBo)
                .collect(Collectors.toList());
    }

    public void addTodo(TodoBO todo) {
        todoTable.putItem(toDto(todo));
    }

    public void updateTodo(TodoBO todo) {
        todoTable.updateItem(toDto(todo));
    }

    public void deleteTodo(String id) {
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
