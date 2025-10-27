package com.example.config;

import com.example.handler.PostConfirmationHandler;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DynamoDbModule.class, MapperModule.class})
public interface UserComponent {
    void inject(PostConfirmationHandler handler);
}
