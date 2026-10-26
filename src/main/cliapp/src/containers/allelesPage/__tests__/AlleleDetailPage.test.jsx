import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { setLocalStorage } from '../../../tools/jest/setupTests';
import { setupSaveSettingsHandler, setupSettingsHandler } from '../../../tools/jest/commonMswhandlers';
import { alleleDetailData } from '../mockData/mockData.js';
import { renderAtAlleleRoute } from './alleleTestUtils';

const getAllele = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ getAllele })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService()
);

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

const FORM_SETTINGS_KEY = 'AlleleDetailFormSettings';

const renderPage = () => renderAtAlleleRoute(<AlleleDetailPage />);

describe('<AlleleDetailPage />', () => {
	beforeEach(() => {
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: alleleDetailData });
		setupSettingsHandler();
		setupSaveSettingsHandler();
		window.localStorage.removeItem(FORM_SETTINGS_KEY);
	});

	// Prominent by colour, at the same size as the header's Show all fields button.
	it('Shows Save as a prominent action', async () => {
		await renderPage();

		const saveButton = await screen.findByRole('button', { name: 'Save' });
		expect(saveButton).toHaveClass('p-button-success');
		expect(saveButton).not.toHaveClass('p-button-text');
		expect(saveButton).not.toHaveClass('p-button-lg');
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

	it('Opens the create page for a copy by the identifier in the URL when the allele carries none', async () => {
		const user = userEvent.setup();
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);
		getAllele.mockResolvedValue({
			data: {
				entity: {
					...alleleDetailData.entity,
					curie: undefined,
					primaryExternalId: undefined,
					modInternalId: undefined,
				},
			},
		});

		renderPage();

		const duplicate = await screen.findByRole('button', { name: /Clone/i });
		await user.click(duplicate);

		expect(open).toHaveBeenCalledWith('/allele/create?from=MGI%3A5146840', '_blank');
		open.mockRestore();
	});
});
