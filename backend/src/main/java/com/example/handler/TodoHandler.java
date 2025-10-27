package com.example.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.config.DaggerTodoAppComponent;
import com.example.config.TodoAppComponent;
import com.example.controller.TodoController;
import com.example.util.LoggingContextManager;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class TodoHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    TodoController todoController;

    public TodoHandler() {
        TodoAppComponent component = DaggerTodoAppComponent.create();
        component.inject(this);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LoggingContextManager.setAwsRequestId(context.getAwsRequestId());
        log.info("Received request: {} {}", input.getHttpMethod(), input.getPath());

        APIGatewayProxyResponseEvent response;
        try {
            String httpMethod = input.getHttpMethod();
            String path = input.getPath();

            if ("GET".equals(httpMethod) && "/todos".equals(path)) {
                response = todoController.getAllTodos();
            } else if ("POST".equals(httpMethod) && "/todos".equals(path)) {
                response = todoController.addTodo(input.getBody());
            } else if ("PUT".equals(httpMethod) && path.matches("/todos/[^/]+")) {
                String id = path.substring(path.lastIndexOf('/') + 1);
                response = todoController.updateTodo(id, input.getBody());
            } else if ("DELETE".equals(httpMethod) && path.matches("/todos/[^/]+")) {
                String id = path.substring(path.lastIndexOf('/') + 1);
                response = todoController.deleteTodo(id);
            } else {
                response = new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("Not Found");
            }
            log.info("Sending response with status code: {}", response.getStatusCode());
        } catch (Throwable e) {
            log.error("An error occurred while processing the request", e);
            response = new APIGatewayProxyResponseEvent().withStatusCode(500).withBody("Internal Server Error");
        } finally {
            LoggingContextManager.clearAll();
        }
        return response;
    }
}
