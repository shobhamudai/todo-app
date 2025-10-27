import React from 'react';
import { Amplify } from 'aws-amplify';
import { withAuthenticator } from '@aws-amplify/ui-react';
import '@aws-amplify/ui-react/styles.css';
import TodoList from './components/TodoList';
import { cognitoConfig } from './config';

Amplify.configure({
  Auth: {
    Cognito: {
      userPoolId: cognitoConfig.UserPoolId,
      userPoolClientId: cognitoConfig.ClientId,
      region: cognitoConfig.Region,
    }
  }
});

const App = ({ signOut, user }) => {
  return (
    <div>
      <button onClick={signOut}>Sign out</button>
      <TodoList />
    </div>
  );
};

export default withAuthenticator(App, {
  signUpAttributes: ['email'],
});
