package com.example.config;

import com.example.handler.TodoHandler;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DynamoDbModule.class, MapperModule.class, JacksonModule.class})
public interface TodoAppComponent {

    void inject(TodoHandler todoHandler);
}
