import { useState, useEffect } from "react";
import { QueryClient } from "react-query";

import { LoggedInPersonService } from './LoggedInPersonService';

let userSettingsSessionKeyName = "userSettings";

export const useSessionStorage = (key, defaultValue) => {
	//console.log(key + " -> " + defaultValue);

	const queryClient = new QueryClient();

	const [value, setValue] = useState(() => {

		const data = queryClient.getQueryData('userInfo');
		console.log(data);
		//console.log(key + " Use State");
		const userSettingsObject = getUserSettingsObject(key);
		console.log(key + " Setting initial state: " + userSettingsObject[key]);
		console.log(userSettingsObject);
		return userSettingsObject[key] || defaultValue;
	});

	useEffect(() => {
		async function settingsChanged() {
			//console.trace();
			//console.log(key + " Use Effect");
			const userSettingsObject = await getUserSettingsObject(key);
			console.log("Changes made: " + JSON.stringify(userSettingsObject[key]));
			const newUserSettingsObject = {
				...userSettingsObject,
				[key]: value,
			};
			console.log(userSettingsObject);
			//setValue(userSettingsObject[key]);
			console.log("Changes made after: " + JSON.stringify(userSettingsObject[key]));
			setUserSettingsObject(key, newUserSettingsObject);
		}
		settingsChanged();
	}, [key, value]);

	return [value, setValue];
};

const setUserSettingsObject = (key, newUserSettingsObject) => {
	console.log(key + " setUserSettingsObject: ");
	sessionStorage.setItem(userSettingsSessionKeyName, JSON.stringify(newUserSettingsObject));
	let loggedInPersonService = new LoggedInPersonService();
	loggedInPersonService.saveUserSettings(newUserSettingsObject);
};

const getUserSettingsObject = async (key) => {
	console.log(key + " getUserSettingsObject: ");
	let userSettingsJson = sessionStorage.getItem(userSettingsSessionKeyName);

	if(!userSettingsJson) {
		console.log(key + " Object not found");

		//loggedInPersonService.saveUserSettings(newUserSettingsObject);
		let loggedInPersonService = new LoggedInPersonService();
		await loggedInPersonService.getUserInfo().then(data => {
			console.log("Getting data from backend: " + data);
			console.log(data);
			if(data.userSettings) {
				console.log("Settings from the backend");
				userSettingsJson = data.userSettings;
			} else {
				console.log("No Settings from the backend");
				userSettingsJson = {};
				setUserSettingsObject(key, userSettingsJson);
			}
		}).catch(error => {
			console.log("Settings error from the backend");
			console.log(error);
			userSettingsJson = {};
			setUserSettingsObject(key, userSettingsJson);
		});

		return userSettingsJson;
	} else {
		console.log("Returning object: " + userSettingsJson);
		return JSON.parse(userSettingsJson);
	}

};
