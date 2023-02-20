import { useQuery, useMutation } from 'react-query';
import { useState } from "react";
import { PersonSettingsService } from "./PersonSettingsService";
import { FILTER_FIELDS } from '../constants/FilterFields';
import { SORT_FIELDS } from '../constants/SortFields';
import { getInvalidFilterFields, deleteInvalidFilters, getInvalidSortFields, deleteInvalidSorts } from '../utils/utils';


export const useGetUserSettings = (key, defaultValue) => {

	const personSettingsService = new PersonSettingsService();
	const [settings, setSettings] = useState(() => {
		const stickyValue = localStorage.getItem(key);
		const stickyObject = stickyValue !== null ? JSON.parse(stickyValue) : defaultValue;
		if(key !== 'themeSettings'){
			const invalidFilterFields = getInvalidFilterFields(stickyObject.filters, FILTER_FIELDS);
			const newFilters = deleteInvalidFilters(invalidFilterFields, stickyObject.filters);
			stickyObject.filters = newFilters;

			const invalidSortFields = getInvalidSortFields(stickyObject.multiSortMeta, SORT_FIELDS);
			const newSorts = deleteInvalidSorts(invalidSortFields, stickyObject.multiSortMeta);
			stickyObject.multiSortMeta = newSorts;
			
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
				const invalidFilterFields = getInvalidFilterFields(userSettings.filters, FILTER_FIELDS);
				const newFilters = deleteInvalidFilters(invalidFilterFields, userSettings.filters);
				userSettings.filters = newFilters;

				const invalidSortFields = getInvalidSortFields(userSettings.multiSortMeta, SORT_FIELDS);
				const newSorts = deleteInvalidSorts(invalidSortFields, userSettings.multiSortMeta);
				userSettings.multiSortMeta = newSorts;
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
