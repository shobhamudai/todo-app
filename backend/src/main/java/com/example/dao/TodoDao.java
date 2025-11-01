package com.example.dao;

import com.example.mapper.TodoMapper;
import com.example.model.TodoBO;
import com.example.model.TodoDto;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Log4j2
@Singleton
public class TodoDao {

    private static final String USER_ID_INDEX = "userId-index";
    private final DynamoDbTable<TodoDto> todoTable;
    private final TodoMapper todoMapper;

    @Inject
    public TodoDao(DynamoDbEnhancedClient enhancedClient, TodoMapper todoMapper) {
        this.todoTable = enhancedClient.table("todos", TableSchema.fromBean(TodoDto.class));
        this.todoMapper = todoMapper;
        log.info("TodoDao initialized. Table name: {}", todoTable.tableName());
    }

    public List<TodoBO> getAllTodosByUserId(String userId) {
        log.info("DAO: Querying userId-index for todos for user ID: {}", userId);
        DynamoDbIndex<TodoDto> userIndex = todoTable.index(USER_ID_INDEX);
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());
        return todoMapper.toBoList(userIndex.query(queryConditional).stream().flatMap(page -> page.items().stream()).toList());
    }

    public void addTodo(TodoBO todo) {
        log.info("DAO: Adding todo with ID: {}", todo.getId());
        todoTable.putItem(todoMapper.toDto(todo));
    }

    public void updateTodo(TodoBO todo) {
        log.info("DAO: Updating todo with ID: {}", todo.getId());
        todoTable.updateItem(todoMapper.toDto(todo));
    }

    public void deleteTodo(String id) {
        log.info("DAO: Deleting todo with ID: {}", id);
        todoTable.deleteItem(Key.builder().partitionValue(id).build());
    }
}
