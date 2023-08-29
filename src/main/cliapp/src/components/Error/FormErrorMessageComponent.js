import React from 'react';


export function FormErrorMessageComponent({ errorField, errorMessages }){
		if(errorMessages){
				return <small className="p-error">{errorMessages[errorField]}</small>;
		} else {
				return null;
		}
}
