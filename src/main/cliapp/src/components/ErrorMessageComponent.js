import React from 'react';
import { Message }  from "primereact/message";


export function ErrorMessageComponent({ errorField, errorMessages }) {
    if(errorMessages){
        return (
            <div>
                <Message 
                    severity={errorMessages[errorField] ? errorMessages[errorField].severity : ""} 
                    text={errorMessages[errorField] ? errorMessages[errorField].message : ""} 
                />
            </div>
        );
    } else {
        return null;
    }
}
