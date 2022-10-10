import { useState, useEffect } from "react";
import { LoggedInPersonService } from './LoggedInPersonService';


if(!window.sessionStorage.getItem("userSettings")){
	console.log("Object not stored");
	window.sessionStorage.setItem('userSettings', JSON.stringify({}));
};

export const resetThemes = (defaultThemes) => {
	const storedObject = getSessionObject();
	const newStateObject = {
		...storedObject,
		themeSettings: defaultThemes
	};
	sessionStorage.setItem("userSettings", JSON.stringify(newStateObject));
};


const getSessionObject = () => {
	const saved = sessionStorage.getItem("userSettings");
	return JSON.parse(saved);
};

const getSessionValue = (key, defaultValue) => {
	const storedValue = getSessionObject();
	return storedValue[key] || defaultValue;
}

const setUserSettingsObject = (key, newUserSettingsObject) => {
	sessionStorage.setItem(key, JSON.stringify(newUserSettingsObject));
	let loggedInPersonService = new LoggedInPersonService();
	loggedInPersonService.saveUserSettings(newUserSettingsObject);
};

export const useSessionStorage = (key, defaultValue) => {
	const [value, setValue] = useState(defaultValue);

	useEffect(() => {
		const storedObject = getSessionObject();
		console.log(`${key} -- ${JSON.stringify(value)}`);
		const newStateObject = {
			...storedObject,
			[key]: value,
		};
		console.log(newStateObject);
		setUserSettingsObject(key, newStateObject)
	}, [key, value]);

	return [value, setValue];
};

//useUserSettings
	//useQuery for getUserInfo, pass in key and return data[key]
	//setUserSettings that uses a mutation
