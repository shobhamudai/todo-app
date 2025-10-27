package com.example.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuthHandler implements RequestHandler<APIGatewayProxyRequestEvent, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        // Construct the resource ARN for the policy. This is needed for both Allow and Deny cases.
        APIGatewayProxyRequestEvent.ProxyRequestContext requestContext = event.getRequestContext();
        String region = System.getenv("AWS_REGION");
        String resource = String.format("arn:aws:execute-api:%s:%s:%s/%s/*/*",
                region,
                requestContext.getAccountId(),
                requestContext.getApiId(),
                requestContext.getStage());

        String tokenHeader = null;
        if (event.getHeaders() != null) {
            // Search for header case-insensitively
            for (Map.Entry<String, String> header : event.getHeaders().entrySet()) {
                if (header.getKey().equalsIgnoreCase("authorization")) {
                    tokenHeader = header.getValue();
                    break;
                }
            }
        }

        if (tokenHeader == null || !tokenHeader.toLowerCase().startsWith("bearer ")) {
            context.getLogger().log("Invalid or missing Authorization header");
            return generatePolicy("user", "Deny", resource, null);
        }
        String token = tokenHeader.substring(7);

        String userPoolId = System.getenv("USER_POOL_ID");

        try {
            JwkProvider provider = new JwkProviderBuilder(new URL("https://cognito-idp." + region + ".amazonaws.com/" + userPoolId + "/.well-known/jwks.json")).build();
            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = provider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            algorithm.verify(jwt);

            String principalId = jwt.getSubject();
            return generatePolicy(principalId, "Allow", resource, jwt.getSubject());

        } catch (Exception e) {
            context.getLogger().log("Error verifying token: " + e.getMessage());
            return generatePolicy("user", "Deny", resource, null);
        }
    }

    private Map<String, Object> generatePolicy(String principalId, String effect, String resource, String sub) {
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("principalId", principalId);

        Map<String, Object> policyDocument = new HashMap<>();
        policyDocument.put("Version", "2012-10-17");

        Map<String, Object> statement = new HashMap<>();
        statement.put("Action", "execute-api:Invoke");
        statement.put("Effect", effect);
        statement.put("Resource", resource);

        policyDocument.put("Statement", Collections.singletonList(statement));
        authResponse.put("policyDocument", policyDocument);

        if (sub != null) {
            Map<String, String> context = new HashMap<>();
            context.put("sub", sub);
            authResponse.put("context", context);
        }

        return authResponse;
    }
}
