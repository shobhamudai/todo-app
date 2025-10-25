package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.config.DaggerTodoAppComponent;
import com.example.config.TodoAppComponent;
import com.example.controller.TodoController;

import javax.inject.Inject;

public class TodoHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    TodoController todoController;

    public TodoHandler() {
        TodoAppComponent component = DaggerTodoAppComponent.create();
        component.inject(this);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String httpMethod = input.getHttpMethod();
        String path = input.getPath();

        if ("GET".equals(httpMethod) && "/todos".equals(path)) {
            return todoController.getAllTodos();
        } else if ("POST".equals(httpMethod) && "/todos".equals(path)) {
            return todoController.addTodo(input.getBody());
        } else if ("PUT".equals(httpMethod) && path.matches("/todos/[^/]+")) {
            String id = path.substring(path.lastIndexOf('/') + 1);
            return todoController.updateTodo(id, input.getBody());
        } else if ("DELETE".equals(httpMethod) && path.matches("/todos/[^/]+")) {
            String id = path.substring(path.lastIndexOf('/') + 1);
            return todoController.deleteTodo(id);
        }

        return new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("Not Found");
    }
}
