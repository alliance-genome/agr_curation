import { useQuery, useMutation } from 'react-query';
import { useState } from "react";
import { PersonSettingsService } from "./PersonSettingsService";

export const useGetUserSettings = (key, defaultValue) => {

	const personSettingsService = new PersonSettingsService();
	const [settings, setSettings] = useState(() => {
		const stickyValue = localStorage.getItem(key);
		return stickyValue !== null ? JSON.parse(stickyValue) : defaultValue;
	});

	useQuery(`${key}`, () => personSettingsService.getUserSettings(key), {
		onSuccess: (data) => {
			let userSettings = defaultValue;
			if(Object.keys(data).length === 0) {
				console.log("New Key being Saved: " + key);
				personSettingsService.saveUserSettings(key, userSettings);
			} else {
				userSettings = data.entity.settingsMap;
			}
			setSettings(userSettings);
			localStorage.setItem(key, JSON.stringify(userSettings));
		},
		refetchOnWindowFocus: false,
	});

	const { mutate } = useMutation(updatedSettings => {
		setSettings(updatedSettings);
		localStorage.setItem(key, JSON.stringify(updatedSettings));
		return personSettingsService.saveUserSettings(key, updatedSettings);
	});

	return {settings, mutate}
}
