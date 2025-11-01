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
import java.util.HashMap;
import java.util.Map;

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

        // Extract user identifier from the authorizer context
        String userId = getUserId(input);
        if (userId == null) {
            log.error("Could not find user ID in request context.");
            return createCorsResponse(401, "Unauthorized");
        }

        log.info("User '{}' invoked: {} {}", userId, input.getHttpMethod(), input.getPath());

        APIGatewayProxyResponseEvent response;
        try {
            String httpMethod = input.getHttpMethod();
            String path = input.getPath();

            if ("GET".equals(httpMethod) && "/todos".equals(path)) {
                response = todoController.getAllTodos(userId);
            } else if ("POST".equals(httpMethod) && "/todos".equals(path)) {
                response = todoController.addTodo(userId, input.getBody());
            } else if ("PUT".equals(httpMethod) && path.matches("/todos/[^/]+")) {
                String id = path.substring(path.lastIndexOf('/') + 1);
                response = todoController.updateTodo(userId, id, input.getBody());
            } else if ("DELETE".equals(httpMethod) && path.matches("/todos/[^/]+")) {
                String id = path.substring(path.lastIndexOf('/') + 1);
                response = todoController.deleteTodo(userId, id);
            } else {
                response = createCorsResponse(404, "Not Found");
            }
            log.info("Sending response with status code: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("An error occurred while processing the request for user ID: " + userId, e);
            response = createCorsResponse(500, "Internal Server Error");
        }
        return response;
    }

    private String getUserId(APIGatewayProxyRequestEvent input) {
        try {
            // The context from a Lambda authorizer is a simple map
            Map<String, Object> authorizerContext = (Map<String, Object>) input.getRequestContext().getAuthorizer();
            return (String) authorizerContext.get("sub");
        } catch (Exception e) {
            log.error("Error extracting user ID from request context", e);
            return null;
        }
    }

    private APIGatewayProxyResponseEvent createCorsResponse(int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*"); // Or your specific origin
        headers.put("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        return new APIGatewayProxyResponseEvent().withStatusCode(statusCode).withHeaders(headers).withBody(body);
    }
}
