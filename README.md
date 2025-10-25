Here is a summary of the commands for setting up and updating your application.

 # First-Time Setup
  These are the commands to set up the project from scratch on a new machine.
   1. Install AWS CDK, Maven, and AWS CLI:
       * Install AWS CDK: npm install -g aws-cdk
       * Install Maven: Use a package manager like Homebrew (brew install maven) or download it from the official website.
       * Install AWS CLI: Follow the official AWS documentation to install the AWS CLI v2.
   2. Configure AWS Credentials:
       * Run aws configure and enter your IAM user credentials, or
       * Run aws configure sso to use corporate SSO.
   3. Bootstrap AWS Environment for CDK:
       * Navigate to the cdk directory: cd todo-app/cdk
       * Run: npm install
       * Run: cdk bootstrap
   4. Build backend:
      * Run: mvn clean package
5. Deploy the Application Stack:
   * In the cdk directory, run: cdk deploy --require-approval never
   * Note down the s3 bucket name, cloud distribution name, Api-gateway URL  
6. Build and Deploy the Frontend:
      * Replace Api-gateway URl in API_URL in App.js file
      * Navigate to the frontend directory: cd ../frontend
      * Install dependencies: npm install
      * Build the application: npm run build
      * Deploy to S3: aws s3 sync build s3://{insert S3 bucket name here created during cdk deploy}


 # Updating the Application
  Here are the commands to update your application after making code changes.
  After changing the backend code:
   1. Package the backend:
       * Navigate to the backend directory: cd backend
       * Run: mvn clean package
   2. Redeploy the CDK stack:
       * Navigate to the cdk directory: cd ../cdk
       * Run: cdk deploy --require-approval never
  After changing the frontend code:
   1. Build the frontend:
       * Navigate to the frontend directory: cd frontend
       * Run: npm run build
   2. Deploy the frontend to S3:
       * In the frontend directory, run: aws s3 sync build s3://{insert S3 bucket name here created during cdk deploy}
   3. Invalidate the CloudFront cache:
      * Run: aws cloudfront create-invalidation --distribution-id {insert cloudfront distribution name here created during cdk deploy} --paths "/*"
      * Please replace the bucket name and distribution ID with the actual values from your CDK stack outputs if they are different.

