import React from 'react';


export function FormErrorMessageComponent({ errorField, errorMessages }){
		if(errorMessages){
				return <small className="text-lg p-error">{errorMessages[errorField]}</small>;
		} else {
				return null;
		}
}
