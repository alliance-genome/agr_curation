import { useQuery, useMutation } from 'react-query';
import { useState } from "react";
import { PersonSettingsService } from "./PersonSettingsService";

export const useGetUserSettings = (key, defaultValue) => {

	const personSettingsService = new PersonSettingsService();
	const [settings, setSettings] = useState(defaultValue);


	useQuery(`${key}`, () => personSettingsService.getUserSettings(key), {
		onSuccess: (data) => {
			const returnData = Object.keys(data).length === 0 ? defaultValue : data.entity.settingsMap;
			setSettings(returnData);
		},
		refetchOnWindowFocus: false,
	});

	const { mutate } = useMutation(updatedSettings => {
		return personSettingsService.saveUserSettings(key, updatedSettings);
	},{
		onSuccess: (data) => {
			setSettings(data.entity.settingsMap);
		}
	} );

	return {settings, mutate}
}
