import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { alleleDetailData } from '../mockData/mockData.js';

const getAllele = vi.fn();
const saveAlleleDetail = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		getAllele = getAllele;
		saveAlleleDetail = saveAlleleDetail;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('../../../service/CrossReferenceService', () => ({
	CrossReferenceService: class {
		getCrossReferencesForAllele = vi.fn(() => Promise.resolve({ data: { entities: [] } }));
		replaceCrossReferencesForAllele = vi.fn(() => Promise.resolve({ data: { entities: [] } }));
	},
}));

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

// each wait follows a stubbed call through a full render of the form, which can outlast the default under load
const FORM_LOAD_WAIT = { timeout: 5000 };

const loadedAllele = { ...alleleDetailData.entity, id: 9 };
const SAVED_TAXON = loadedAllele.taxon.curie;

const renderLoadedPage = async () => {
	const result = renderWithClient(
		<MemoryRouter initialEntries={['/allele/MGI:5146840']}>
			<Routes>
				<Route path="/allele/:identifier" element={<AlleleDetailPage />} />
			</Routes>
		</MemoryRouter>
	);

	await waitFor(() => expect(getAllele).toHaveBeenCalledWith('MGI:5146840'), FORM_LOAD_WAIT);
	await waitFor(
		() =>
			expect(screen.getAllByDisplayValue(alleleDetailData.entity.alleleSymbol.displayText).length).toBeGreaterThan(0),
		FORM_LOAD_WAIT
	);
	return result;
};

const fieldRow = (fieldName) => screen.getByRole('heading', { name: fieldName }).closest('.grid');

const taxonInput = (container) => container.querySelector('input[name="taxon-input"]');

const changeTaxon = async (user, container, curie) => {
	await user.clear(taxonInput(container));
	await user.type(taxonInput(container), curie);
};

// every test here loads and renders the whole detail form before acting on it
describe('<AlleleDetailPage /> single-value pending edits', { timeout: 30000 }, () => {
	beforeEach(() => {
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: loadedAllele } });
		saveAlleleDetail.mockReset();
		saveAlleleDetail.mockImplementation((allele) => Promise.resolve({ data: { entity: allele } }));
		window.localStorage.removeItem('AlleleDetailFormSettings');
	});

	it('Shows no pending edits on the allele as loaded', async () => {
		await renderLoadedPage();

		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Marks a changed field as having pending edits', async () => {
		const user = userEvent.setup();
		const { container } = await renderLoadedPage();

		await changeTaxon(user, container, 'NCBITaxon:6239');

		expect(within(fieldRow('Taxon')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getAllByText('Pending Edits!')).toHaveLength(1);
	});

	it('Clears the mark when the field is changed back to its saved value', async () => {
		const user = userEvent.setup();
		const { container } = await renderLoadedPage();

		await changeTaxon(user, container, 'NCBITaxon:6239');
		await changeTaxon(user, container, SAVED_TAXON);

		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Clears the mark once the allele is saved', async () => {
		const user = userEvent.setup();
		const { container } = await renderLoadedPage();

		await changeTaxon(user, container, 'NCBITaxon:6239');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(screen.getByText('Allele Saved')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Keeps the mark when the allele is not saved', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValueOnce({ response: { data: { errorMessage: 'Allele could not be saved' } } });
		const { container } = await renderLoadedPage();

		await changeTaxon(user, container, 'NCBITaxon:6239');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(screen.getByText('Allele could not be saved')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(within(fieldRow('Taxon')).getByText('Pending Edits!')).toBeInTheDocument();
	});

	it('Leaves a deprecated allele obsolete without marking it, keeping other pending edits', async () => {
		const user = userEvent.setup();
		const { container } = await renderLoadedPage();

		await changeTaxon(user, container, 'NCBITaxon:6239');
		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(screen.getByRole('dialog')).getByRole('button', { name: 'Deprecate' }));

		await waitFor(() => expect(screen.getByText('Allele Deprecated')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(within(fieldRow('Obsolete')).queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(within(fieldRow('Taxon')).getByText('Pending Edits!')).toBeInTheDocument();
	});
});
