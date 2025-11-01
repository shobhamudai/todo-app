import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as cloudfront from 'aws-cdk-lib/aws-cloudfront';
import * as s3deploy from 'aws-cdk-lib/aws-s3-deployment';
import * as path from 'path';
import * as cognito from 'aws-cdk-lib/aws-cognito';

export class CdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // DynamoDB Table for Todos
    const table = new dynamodb.Table(this, 'TodoTable', {
      partitionKey: { name: 'id', type: dynamodb.AttributeType.STRING },
      tableName: 'todos',
      removalPolicy: cdk.RemovalPolicy.DESTROY, // NOT recommended for production code
    });

    table.addGlobalSecondaryIndex({
      indexName: 'userId-index',
      partitionKey: { name: 'userId', type: dynamodb.AttributeType.STRING },
    });

    // DynamoDB Table for Users
    const usersTable = new dynamodb.Table(this, 'UsersTable', {
      partitionKey: { name: 'id', type: dynamodb.AttributeType.STRING },
      tableName: 'users',
      removalPolicy: cdk.RemovalPolicy.DESTROY,
    });

    // Post-confirmation Lambda
    const postConfirmationLambda = new lambda.Function(this, 'PostConfirmationLambda', {
      runtime: lambda.Runtime.JAVA_17,
      handler: 'com.example.handler.PostConfirmationHandler::handleRequest',
      code: lambda.Code.fromAsset(path.join(__dirname, '../../backend/target/todo-backend-1.0.0.jar')),
      timeout: cdk.Duration.seconds(60),
      environment: {
        USERS_TABLE_NAME: usersTable.tableName,
      },
    });

    // Grant Lambda permissions to write to the Users table
    usersTable.grantWriteData(postConfirmationLambda);

    // Cognito User Pool
    const userPool = new cognito.UserPool(this, 'TodoUserPool', {
      userPoolName: 'TodoUserPool',
      selfSignUpEnabled: true,
      accountRecovery: cognito.AccountRecovery.EMAIL_ONLY,
      userVerification: {
        emailStyle: cognito.VerificationEmailStyle.CODE,
      },
      autoVerify: {
        email: true,
      },
      standardAttributes: {
        email: { required: true, mutable: false },
      },
      lambdaTriggers: {
        postConfirmation: postConfirmationLambda,
      },
    });

    const userPoolClient = new cognito.UserPoolClient(this, "UserPoolClient", {
        userPool,
    });

    // Lambda Function
    const todoLambda = new lambda.Function(this, 'TodoLambda', {
      runtime: lambda.Runtime.JAVA_17,
      handler: 'com.example.handler.TodoHandler::handleRequest',
      code: lambda.Code.fromAsset(path.join(__dirname, '../../backend/target/todo-backend-1.0.0.jar')),
      memorySize: 512,
      timeout: cdk.Duration.seconds(15),
      environment: {
        TABLE_NAME: table.tableName,
      },
    });

    // Grant Lambda permissions to access DynamoDB table
    table.grantReadWriteData(todoLambda);

    // API Gateway
    const api = new apigateway.RestApi(this, 'TodoApi', {
      restApiName: 'Todo Service',
      defaultCorsPreflightOptions: {
        allowOrigins: apigateway.Cors.ALL_ORIGINS,
        allowMethods: apigateway.Cors.ALL_METHODS,
        allowHeaders: apigateway.Cors.DEFAULT_HEADERS.concat(['Authorization']),
      },
    });

    // Authorizer Lambda
    const authLambda = new lambda.Function(this, 'AuthLambda', {
      runtime: lambda.Runtime.JAVA_17,
      handler: 'com.example.handler.AuthHandler::handleRequest',
      code: lambda.Code.fromAsset(path.join(__dirname, '../../backend/target/todo-backend-1.0.0.jar')),
      timeout: cdk.Duration.seconds(60),
      environment: {
        USER_POOL_ID: userPool.userPoolId,
      },
    });


    const authorizer = new apigateway.RequestAuthorizer(this, 'TodoAuthorizer', {
      handler: authLambda,
      identitySources: [apigateway.IdentitySource.header('Authorization')],
      resultsCacheTtl: cdk.Duration.seconds(0),
    });

    const todos = api.root.addResource('todos');
    const todoIntegration = new apigateway.LambdaIntegration(todoLambda);
    todos.addMethod('GET', todoIntegration, { authorizer });
    todos.addMethod('POST', todoIntegration, { authorizer });

    const todo = todos.addResource('{id}');
    todo.addMethod('GET', todoIntegration, { authorizer });
    todo.addMethod('PUT', todoIntegration, { authorizer });
    todo.addMethod('DELETE', todoIntegration, { authorizer });

    // S3 Bucket for Frontend
    const websiteBucket = new s3.Bucket(this, 'WebsiteBucket', {
      websiteIndexDocument: 'index.html',
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      autoDeleteObjects: true,
    });

    // CloudFront Origin Access Identity
    const originAccessIdentity = new cloudfront.OriginAccessIdentity(this, 'OAI');
    websiteBucket.grantRead(originAccessIdentity);

    // CloudFront Distribution
    const distribution = new cloudfront.CloudFrontWebDistribution(this, 'Distribution', {
      originConfigs: [
        {
          s3OriginSource: {
            s3BucketSource: websiteBucket,
            originAccessIdentity: originAccessIdentity,
          },
          behaviors: [{ isDefaultBehavior: true }],
        },
      ],
    });

    // Output the CloudFront URL
    new cdk.CfnOutput(this, 'CloudFrontURL', {
      value: distribution.distributionDomainName,
    });

    // Output the S3 bucket name
    new cdk.CfnOutput(this, 'BucketName', {
      value: websiteBucket.bucketName,
    });

    // Output the CloudFront distribution ID
    new cdk.CfnOutput(this, 'DistributionId', {
      value: distribution.distributionId,
    });

    new cdk.CfnOutput(this, "UserPoolId", {
      value: userPool.userPoolId,
    });

    new cdk.CfnOutput(this, "UserPoolClientId", {
      value: userPoolClient.userPoolClientId,
    });
  }
}
