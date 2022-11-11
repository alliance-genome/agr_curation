import React from 'react';
import { Message }	from "primereact/message";


export function DialogErrorMessageComponent({ errorField, errorMessages: errorMessagesRow }) {
		if(errorMessagesRow){
				return (
						<div style={{position:"inline-table", paddingTop:"5px"}}>
								<Message
										severity={errorMessagesRow[errorField] ? errorMessagesRow[errorField].severity : ""}
										text={errorMessagesRow[errorField] ? errorMessagesRow[errorField].message : ""}
										style={{position:"inline-table", zIndex: "5"}}
								/>
						</div>
				);
		} else {
				return null;
		}
}
