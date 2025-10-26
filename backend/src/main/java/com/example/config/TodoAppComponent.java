package com.example.config;

import com.example.TodoHandler;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DynamoDbModule.class, MapperModule.class})
public interface TodoAppComponent {

    void inject(TodoHandler todoHandler);
}
