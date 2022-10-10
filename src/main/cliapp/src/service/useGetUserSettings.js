import { useQuery, useMutation, useQueryClient } from 'react-query';
import { useState } from "react";
import { PersonSettingsService } from "./PersonSettingsService";

export const useGetUserSettings = (key, defaultValue) => {

	const personSettingsService = new PersonSettingsService();
	const queryClient = useQueryClient();
	const [settings, setSettings] = useState(defaultValue);


	const { isLoading } = useQuery(`${key}`, () => personSettingsService.getUserSettings(key), {
		onSuccess: (data) => {
			const returnData = Object.keys(data).length === 0 ? defaultValue : data.entity.settingsMap;
			setSettings(returnData);
		},
		refetchOnWindowFocus: false,
	});

	const { mutate } = useMutation(updatedSettings => {
		return personSettingsService.saveUserSettings(key, updatedSettings);
	},{
		onMutate: async (updatedSettings) => {
			setSettings(updatedSettings);
		},
		onSuccess: (data) => {
			queryClient.invalidateQueries(`${key}`);
		}
	} );

	return {settings, mutate, isLoading}
}
