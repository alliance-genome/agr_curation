import { useQuery, useMutation } from '@tanstack/react-query';
import { useState, useEffect } from 'react';
import { PersonSettingsService } from './PersonSettingsService';
import { removeInvalidFilters, removeInvalidSorts } from '../utils/utils';

export const useGetUserSettings = (key, defaultValue, isTable = true) => {
	const personSettingsService = new PersonSettingsService();
	const [settings, setSettings] = useState(() => {
		const stickyValue = localStorage.getItem(key);
		const stickyObject = stickyValue !== null ? JSON.parse(stickyValue) : defaultValue;
		if (isTable) {
			stickyObject.filters = removeInvalidFilters(stickyObject.filters);
			stickyObject.multiSortMeta = removeInvalidSorts(stickyObject.multiSortMeta);
			if (!stickyObject.orderedColumnNames) stickyObject.orderedColumnNames = defaultValue.selectedColumnNames;
			if (!stickyObject.columnWidths) stickyObject.columnWidths = defaultValue.columnWidths;
		}

		return stickyObject;
	});

	const { data, isSuccess } = useQuery({
		queryKey: [`${key}`],
		queryFn: () => personSettingsService.getUserSettings(key),
		refetchOnWindowFocus: false,
	});

	useEffect(() => {
		if (isSuccess && data) {
			let settingsMap = data?.entity?.settingsMap || {};

			while (settingsMap.settingsMap && typeof settingsMap.settingsMap === 'object') {
				settingsMap = settingsMap.settingsMap;
			}

			const serverSettings = { ...data?.entity, ...settingsMap };
			delete serverSettings.settingsMap;

			if (serverSettings && Object.keys(serverSettings).length > 0) {
				let updatedSettings = { ...serverSettings };
				if (isTable) {
					updatedSettings.filters = removeInvalidFilters(updatedSettings.filters);
					updatedSettings.multiSortMeta = removeInvalidSorts(updatedSettings.multiSortMeta);
					if (!updatedSettings.orderedColumnNames)
						updatedSettings.orderedColumnNames = defaultValue.selectedColumnNames;
					if (!updatedSettings.columnWidths) updatedSettings.columnWidths = defaultValue.columnWidths;
				}
				setSettings(updatedSettings);
				localStorage.setItem(key, JSON.stringify(updatedSettings));
			}
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [data, isSuccess, key, isTable]);

	const { mutate } = useMutation({
		mutationFn: (updatedSettings) => {
			setSettings(updatedSettings);
			localStorage.setItem(key, JSON.stringify(updatedSettings));
			return personSettingsService.saveUserSettings(key, updatedSettings);
		},
	});

	return { settings, mutate };
};
