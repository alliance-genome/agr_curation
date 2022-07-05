import { useState, useEffect } from "react";


if(!window.sessionStorage.getItem("globalStateObject")){
	console.log("Object not stored");
	window.sessionStorage.setItem('globalStateObject', JSON.stringify({}));
};

export const resetThemes = (defaultThemes) => {
	const storedObject = getSessionObject();
	const newStateObject = {
		...storedObject,
		themeSettings: defaultThemes
	};
	sessionStorage.setItem("globalStateObject", JSON.stringify(newStateObject));
};

const getSessionObject = () => {
	const saved = sessionStorage.getItem("globalStateObject");
	return JSON.parse(saved);
};

const getSessionValue = (key, defaultValue) => {
	const storedValue = getSessionObject();
	return storedValue[key] || defaultValue;
}


export const useSessionStorage = (key, defaultValue) => {
	const [value, setValue] = useState(() => {
		return getSessionValue(key, defaultValue);
	});


	useEffect(() => {
		const storedObject = getSessionObject();
		const newStateObject = {
			...storedObject,
			[key]: value,
		};
		sessionStorage.setItem("globalStateObject", JSON.stringify(newStateObject));
	}, [key, value]);

	return [value, setValue];
};
