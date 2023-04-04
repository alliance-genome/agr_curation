import React from 'react';
import { Message } from 'primereact/message';
import 'primeflex/primeflex.css';


export function ErrorComponent({ errorMessage }) {

  const contentTemplate = () => {
    
    return (
      <div>
      <h4>
        <div><strong>A problem displaying this page{errorMessage ? ':' : null}</strong></div>
        <div>{errorMessage}</div>
        <br />
        <div>
        <p>
          Please reach out to the A-Team or add a ticket to
          <a
            href="https://agr-jira.atlassian.net/jira/software/c/projects/SCRUM/boards/66/backlog"
            target="_blank"
            rel="noreferrer"> this board</a>
        </p>
        </div>
      </h4>
      </div>
    );

  };

  return <Message severity="error" className="w-screen h-15rem flex align-items-center justify-content-start" content={contentTemplate} />;
}