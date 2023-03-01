import { useQuery, useMutation } from 'react-query';
import { useState } from "react";
import { PersonSettingsService } from "./PersonSettingsService";
import { deleteInvalidFilters, deleteInvalidSorts } from '../utils/utils';


export const useGetUserSettings = (key, defaultValue) => {

	const personSettingsService = new PersonSettingsService();
	const [settings, setSettings] = useState(() => {
		const stickyValue = localStorage.getItem(key);
		const stickyObject = stickyValue !== null ? JSON.parse(stickyValue) : defaultValue;
		if(key !== 'themeSettings'){
			stickyObject.filters = deleteInvalidFilters(stickyObject.filters);
			stickyObject.multiSortMeta = deleteInvalidSorts(stickyObject.multiSortMeta);
		}

		return stickyObject;
	});

	useQuery(`${key}`, () => personSettingsService.getUserSettings(key), {
		onSuccess: (data) => {
			let userSettings = defaultValue;
			if(Object.keys(data).length === 0) {
				personSettingsService.saveUserSettings(key, userSettings);
			} else {
				userSettings = data.entity.settingsMap;
			}

			if(key !== 'themeSettings'){
				userSettings.filters = deleteInvalidFilters(userSettings.filters);
				userSettings.multiSortMeta = deleteInvalidSorts(userSettings.multiSortMeta);
			}

			setSettings(userSettings)
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
