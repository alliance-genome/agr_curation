import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import AlleleDetailPage from '../AlleleDetailPage';
import { setLocalStorage } from '../../../tools/jest/setupTests';
import {
	setupGetEntityHandler,
	setupSaveSettingsHandler,
	setupSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { alleleDetailData } from '../mockData/mockData.js';

const FORM_SETTINGS_KEY = 'AlleleDetailFormSettings';

const renderPage = () =>
	renderWithClient(
		<BrowserRouter>
			<AlleleDetailPage />
		</BrowserRouter>
	);

describe('<AlleleDetailPage />', () => {
	beforeEach(() => {
		setupGetEntityHandler(alleleDetailData);
		// Registered after setupGetEntityHandler because its */api/:entity/:curie pattern also
		// matches the person settings endpoint, and the later handler takes precedence.
		setupSettingsHandler();
		setupSaveSettingsHandler();
		window.localStorage.removeItem(FORM_SETTINGS_KEY);
	});

	it('Renders without crashing', async () => {
		let result = await renderPage();

		await waitFor(() => {
			expect(result);
		});
	});

	it('Shows every field when no field selection has been saved', async () => {
		await renderPage();

		await waitFor(() => {
			expect(screen.getByRole('heading', { name: 'Synonyms' })).toBeInTheDocument();
		});
		expect(screen.getByRole('heading', { name: 'Date Created' })).toBeInTheDocument();
		expect(screen.getByRole('heading', { name: 'Curie' })).toBeInTheDocument();
		expect(screen.getByRole('heading', { name: 'Taxon' })).toBeInTheDocument();
	});

	it('Hides any field the saved selection omits, identifiers and Taxon included', async () => {
		setLocalStorage(FORM_SETTINGS_KEY, {
			selectedFormFields: ['Symbol'],
			orderedFormFields: ['Curie', 'Primary External ID', 'Symbol', 'Synonyms', 'Taxon'],
			formSettingsKeyName: FORM_SETTINGS_KEY,
		});

		await renderPage();

		await waitFor(() => {
			expect(screen.getByRole('heading', { name: 'Name' })).toBeInTheDocument();
		});
		expect(screen.queryByRole('heading', { name: 'Synonyms' })).not.toBeInTheDocument();
		expect(screen.queryByRole('heading', { name: 'Curie' })).not.toBeInTheDocument();
		expect(screen.queryByRole('heading', { name: 'Primary External ID' })).not.toBeInTheDocument();
		expect(screen.queryByRole('heading', { name: 'Taxon' })).not.toBeInTheDocument();
	});

	it('Shows a field the saved selection has never seen', async () => {
		setLocalStorage(FORM_SETTINGS_KEY, {
			selectedFormFields: ['Name'],
			orderedFormFields: ['Name', 'Synonyms'],
			formSettingsKeyName: FORM_SETTINGS_KEY,
		});

		await renderPage();

		await waitFor(() => {
			expect(screen.getByRole('heading', { name: 'Name' })).toBeInTheDocument();
		});
		// Absent from the saved selection, but also absent from the fields it knew about.
		expect(screen.getByRole('heading', { name: 'Date Created' })).toBeInTheDocument();
		expect(screen.queryByRole('heading', { name: 'Synonyms' })).not.toBeInTheDocument();
	});

	it('Offers the toggleable fields in the visibility menu', async () => {
		const { container } = await renderPage();

		await waitFor(() => {
			expect(container.querySelector('.p-multiselect[aria-label="formFieldToggle"]')).toBeInTheDocument();
		});
	});

	it('Opens the create page in a new tab from the New Allele button', async () => {
		const user = userEvent.setup();
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);

		renderPage();

		const newAllele = await screen.findByRole('button', { name: /New Allele/i });
		await user.click(newAllele);

		expect(open).toHaveBeenCalledWith('/allele/create', '_blank');
		open.mockRestore();
	});
});
