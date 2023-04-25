import React from 'react';
import { ErrorComponent } from './ErrorComponent';

//this is a class component because there is no way to use getDerivedStateFromError in a functional component
//https://reactjs.org/docs/hooks-faq.html#do-hooks-cover-all-use-cases-for-classes
export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      hasError: false,
      errorMessage: false,
    };
  }

  componentDidCatch(error, info) {
    console.error(error, info);
  }

  static getDerivedStateFromError(error) {
    return {
      hasError: true,
      errorMessage: error && error.message,
    };
  }


  render() {
    const { hasError, errorMessage } = this.state;
    if (hasError) return <ErrorComponent errorMessage={errorMessage} />;
    return this.props.children;
  }
}
