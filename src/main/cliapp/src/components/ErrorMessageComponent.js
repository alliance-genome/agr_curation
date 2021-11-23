import React from 'react';
import { Message }  from "primereact/message";


export function ErrorMessageComponent({ errorField, errorMessages: errorMessagesRow }) {
    if(errorMessagesRow){
        return (
            <div>
                <Message 
                    severity={errorMessagesRow[errorField] ? errorMessagesRow[errorField].severity : ""} 
                    text={errorMessagesRow[errorField] ? errorMessagesRow[errorField].message : ""} 
                />
            </div>
        );
    } else {
        return null;
    }
}
