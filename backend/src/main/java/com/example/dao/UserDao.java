package com.example.dao;

import com.example.mapper.UserMapper;
import com.example.model.UserDto;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import com.example.model.UserBo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserDao {

    private final DynamoDbTable<UserDto> userTable;
    private final UserMapper userMapper;

    @Inject
    public UserDao(DynamoDbEnhancedClient enhancedClient, UserMapper userMapper) {
        String tableName = System.getenv("USERS_TABLE_NAME");
        this.userTable = enhancedClient.table(tableName, TableSchema.fromBean(UserDto.class));
        this.userMapper = userMapper;
    }

    public void saveUser(UserBo userBo) {
        userTable.putItem(userMapper.toDto(userBo));
    }
}
