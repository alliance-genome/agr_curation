import React from 'react';
import { renderHook, act, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const getUserSettings = vi.fn();
const saveUserSettings = vi.fn(() => Promise.resolve({}));

vi.mock('../../service/PersonSettingsService', () => ({
	PersonSettingsService: class {
		getUserSettings = getUserSettings;
		saveUserSettings = saveUserSettings;
	},
}));

const { useFormFieldVisibility } = await import('../FormFieldVisibility');

const FIELDS = ['Alpha', 'Beta', 'Gamma'];
const SETTINGS_KEY = 'TestFormSettings';

const wrapper = ({ children }) => {
	const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
	return <QueryClientProvider client={client}>{children}</QueryClientProvider>;
};

const renderVisibility = () => renderHook(() => useFormFieldVisibility('Test', FIELDS), { wrapper });

const storedSettings = (selectedFormFields, orderedFormFields = FIELDS) => ({
	selectedFormFields,
	orderedFormFields,
	formSettingsKeyName: SETTINGS_KEY,
});

describe('useFormFieldVisibility', () => {
	beforeEach(() => {
		window.localStorage.removeItem(SETTINGS_KEY);
		getUserSettings.mockReset();
		getUserSettings.mockResolvedValue({ entity: null });
		saveUserSettings.mockClear();
	});

	it('Shows every field when nothing has been saved', async () => {
		const { result } = renderVisibility();

		expect(result.current.visibleFields).toEqual(FIELDS);
		expect(result.current.isVisible('Beta')).toBe(true);
	});

	it('Treats a field outside the toggleable list as always visible', async () => {
		const { result } = renderVisibility();

		expect(result.current.isVisible('Curie')).toBe(true);

		await act(async () => {
			result.current.setVisibleFields([]);
		});

		expect(result.current.visibleFields).toEqual([]);
		expect(result.current.isVisible('Curie')).toBe(true);
		expect(result.current.isVisible('Alpha')).toBe(false);
	});

	it('Writes the selection to local storage and to the person settings endpoint', async () => {
		const { result } = renderVisibility();

		await act(async () => {
			result.current.setVisibleFields(['Alpha']);
		});

		expect(JSON.parse(window.localStorage.getItem(SETTINGS_KEY))).toEqual(storedSettings(['Alpha']));
		expect(saveUserSettings).toHaveBeenCalledWith(SETTINGS_KEY, storedSettings(['Alpha']));
	});

	it('Reads the selection back from local storage on a later mount', async () => {
		window.localStorage.setItem(SETTINGS_KEY, JSON.stringify(storedSettings(['Alpha'])));

		const { result } = renderVisibility();

		expect(result.current.visibleFields).toEqual(['Alpha']);
	});

	it('Applies a selection that exists only on the server', async () => {
		getUserSettings.mockResolvedValue({
			entity: {
				id: 42,
				settingsKey: SETTINGS_KEY,
				internal: false,
				obsolete: false,
				settingsMap: storedSettings(['Alpha']),
			},
		});

		const { result } = renderVisibility();

		await waitFor(() => {
			expect(result.current.visibleFields).toEqual(['Alpha']);
		});
	});

	it('Keeps the stored selection when the server has none', async () => {
		window.localStorage.setItem(SETTINGS_KEY, JSON.stringify(storedSettings(['Beta'])));

		const { result } = renderVisibility();

		await waitFor(() => {
			expect(getUserSettings).toHaveBeenCalled();
		});
		expect(result.current.visibleFields).toEqual(['Beta']);
	});

	it('Keeps every field hidden when the saved selection is empty', async () => {
		// The API serializes with JsonInclude.Include.NON_EMPTY, so an empty selectedFormFields is
		// absent from the response rather than present as [].
		getUserSettings.mockResolvedValue({
			entity: {
				id: 200010205,
				settingsKey: SETTINGS_KEY,
				settingsMap: {
					orderedFormFields: FIELDS,
					formSettingsKeyName: SETTINGS_KEY,
				},
			},
		});

		const { result } = renderVisibility();

		await waitFor(() => {
			expect(result.current.visibleFields).toEqual([]);
		});
		expect(result.current.isVisible('Alpha')).toBe(false);
	});

	it('Keeps an empty selection empty across a later mount', async () => {
		window.localStorage.setItem(
			SETTINGS_KEY,
			JSON.stringify({ orderedFormFields: FIELDS, formSettingsKeyName: SETTINGS_KEY })
		);

		const { result } = renderVisibility();

		expect(result.current.visibleFields).toEqual([]);
	});

	it('Shows a field the stored selection has never seen', async () => {
		window.localStorage.setItem(SETTINGS_KEY, JSON.stringify(storedSettings(['Alpha'], ['Alpha', 'Beta'])));

		const { result } = renderVisibility();

		// Gamma is absent from the selection, but also absent from the fields it knew about.
		expect(result.current.visibleFields).toEqual(['Alpha', 'Gamma']);
	});

	it('Drops a stored field that is no longer toggleable', async () => {
		window.localStorage.setItem(
			SETTINGS_KEY,
			JSON.stringify(storedSettings(['Alpha', 'Retired'], ['Alpha', 'Beta', 'Gamma', 'Retired']))
		);

		const { result } = renderVisibility();

		expect(result.current.visibleFields).toEqual(['Alpha']);
	});

	it('Restores every field via showAllFields', async () => {
		window.localStorage.setItem(SETTINGS_KEY, JSON.stringify(storedSettings(['Alpha'])));
		const { result } = renderVisibility();

		await act(async () => {
			result.current.showAllFields();
		});

		expect(result.current.visibleFields).toEqual(FIELDS);
		expect(saveUserSettings).toHaveBeenCalledWith(SETTINGS_KEY, storedSettings(FIELDS));
	});
});
