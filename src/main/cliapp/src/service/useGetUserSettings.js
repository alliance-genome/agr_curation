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
			const returnData = Object.keys(data).length === 0 ? defaultValue : data.entity.settingsMap;
			localStorage.setItem(key, JSON.stringify(returnData));
			setSettings(returnData);
		},
		refetchOnWindowFocus: false,
	});

	const { mutate } = useMutation(updatedSettings => {
		return personSettingsService.saveUserSettings(key, updatedSettings);
	},{
		onSuccess: (data) => {
			localStorage.setItem(key, JSON.stringify(data.entity.settingsMap));
			setSettings(data.entity.settingsMap);
		}
	} );

	return {settings, mutate}
}
