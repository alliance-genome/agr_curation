import { useQuery, useMutation } from 'react-query';
import { useState } from "react";
import { PersonSettingsService } from "./PersonSettingsService";
import { removeInvalidFilters, removeInvalidSorts } from '../utils/utils';


export const useGetUserSettings = (key, defaultValue) => {

	const personSettingsService = new PersonSettingsService();
	const [settings, setSettings] = useState(() => {
		const stickyValue = localStorage.getItem(key);
		const stickyObject = stickyValue !== null ? JSON.parse(stickyValue) : defaultValue;
		if(key !== 'themeSettings') {
		 	stickyObject.filters = removeInvalidFilters(stickyObject.filters);
		 	stickyObject.multiSortMeta = removeInvalidSorts(stickyObject.multiSortMeta);
			if(!stickyObject.orderedColumnNames) stickyObject.orderedColumnNames = defaultValue.selectedColumnNames;
			if(!stickyObject.columnWidths) stickyObject.columnWidths = defaultValue.columnWidths;
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
				userSettings.filters = removeInvalidFilters(userSettings.filters);
				userSettings.multiSortMeta = removeInvalidSorts(userSettings.multiSortMeta);
				if(!userSettings.orderedColumnNames) userSettings.orderedColumnNames = defaultValue.selectedColumnNames;
				if(!userSettings.columnWidths) userSettings.columnWidths = defaultValue.columnWidths;
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
