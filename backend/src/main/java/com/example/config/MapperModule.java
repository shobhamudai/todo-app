package com.example.config;

import com.example.mapper.TodoMapper;
import org.mapstruct.factory.Mappers;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class MapperModule {

    @Provides
    @Singleton
    public TodoMapper provideTodoMapper() {
        return Mappers.getMapper(TodoMapper.class);
    }
}
