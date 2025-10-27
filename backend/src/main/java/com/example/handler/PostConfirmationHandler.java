package com.example.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CognitoUserPoolPostConfirmationEvent;
import com.example.service.UserService;
import com.example.config.DaggerUserComponent;
import com.example.model.UserBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class PostConfirmationHandler implements RequestHandler<CognitoUserPoolPostConfirmationEvent, CognitoUserPoolPostConfirmationEvent> {

    private static final Logger log = LoggerFactory.getLogger(PostConfirmationHandler.class);

    @Inject
    UserService userService;

    public PostConfirmationHandler() {
        DaggerUserComponent.create().inject(this);
    }

    @Override
    public CognitoUserPoolPostConfirmationEvent handleRequest(CognitoUserPoolPostConfirmationEvent event, Context context) {
        String userId = event.getRequest().getUserAttributes().get("sub");
        String email = event.getRequest().getUserAttributes().get("email");
        String username = event.getUserName();

        UserBo userBo = new UserBo();
        userBo.setId(userId);
        userBo.setEmail(email);
        userBo.setUsername(username);
        userBo.setEmailVerified(true);
        userBo.setCreatedAt(System.currentTimeMillis());

        try {
            userService.createUser(userBo);
            log.info("Successfully saved user to DynamoDB: {}", username);
        } catch (Exception e) {
            log.error("Error saving user to DynamoDB", e);
        }

        return event;
    }
}
