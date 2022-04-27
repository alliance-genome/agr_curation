import React from 'react';
import { Message }  from "primereact/message";


export function ErrorMessageComponent({ errorField, errorMessages: errorMessagesRow }) {
    if(errorMessagesRow){
        return (
            <div style={{position:"absolute", paddingTop:"25px"}}>
                <Message
                    severity={errorMessagesRow[errorField] ? errorMessagesRow[errorField].severity : ""}
                    text={errorMessagesRow[errorField] ? errorMessagesRow[errorField].message : ""}
                    style={{position:"absolute", display: "inline-table", zIndex: "5"}}
                />
            </div>
        );
    } else {
        return null;
    }
}
