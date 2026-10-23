import React from 'react';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { alleleDetailData } from '../mockData/mockData.js';
import { FORM_LOAD_WAIT, renderAtAlleleRoute } from './alleleTestUtils';

const getAllele = vi.fn();
const saveAlleleDetail = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ getAllele, saveAlleleDetail })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService()
);

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

const loadedAllele = { ...alleleDetailData.entity, id: 9 };
const SAVED_TAXON = loadedAllele.taxon.curie;

const renderLoadedPage = async () => {
	const result = renderAtAlleleRoute(<AlleleDetailPage />);

	await waitFor(() => expect(getAllele).toHaveBeenCalledWith('MGI:5146840'), FORM_LOAD_WAIT);
	await waitFor(
		() =>
			expect(screen.getAllByDisplayValue(alleleDetailData.entity.alleleSymbol.displayText).length).toBeGreaterThan(0),
		FORM_LOAD_WAIT
	);
	return result;
};

const fieldRow = (fieldName) => screen.getByRole('heading', { name: fieldName }).closest('.grid');

// A table section, from its title down to its table.
const sectionOf = (tableName) => screen.getByRole('heading', { name: tableName }).closest('.grid').parentElement;

// The symbol's display text input, found by its saved text, which its format text input also holds.
const symbolDisplayTextInput = () =>
	within(sectionOf('Symbol')).getAllByDisplayValue(loadedAllele.alleleSymbol.displayText)[0];

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

	it('Keeps Save inactive until something is edited', async () => {
		await renderLoadedPage();

		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
	});

	it('Activates Save for a changed single-value field and deactivates it once saved', async () => {
		const user = userEvent.setup();
		const { container } = await renderLoadedPage();

		await changeTaxon(user, container, 'NCBITaxon:6239');
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();

		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(screen.getByText('Allele Saved')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
	});

	it('Deactivates Save when a changed field is changed back', async () => {
		const user = userEvent.setup();
		const { container } = await renderLoadedPage();

		await changeTaxon(user, container, 'NCBITaxon:6239');
		await changeTaxon(user, container, SAVED_TAXON);

		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
	});

	it('Activates Save for an edit to a field it does not mark', async () => {
		const user = userEvent.setup();
		await renderLoadedPage();

		await user.click(screen.getByRole('button', { name: 'Add Related Note' }));

		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Marks an edited single-object section and activates Save, clearing both when changed back', async () => {
		const user = userEvent.setup();
		await renderLoadedPage();

		const displayTextInput = symbolDisplayTextInput();
		await user.type(displayTextInput, '-edited');

		expect(within(sectionOf('Symbol')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getAllByText('Pending Edits!')).toHaveLength(1);
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();

		await user.type(displayTextInput, '{Backspace>7/}');

		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
	});

	it('Clears a single-object section mark once the allele is saved', async () => {
		const user = userEvent.setup();
		await renderLoadedPage();

		await user.type(symbolDisplayTextInput(), '-edited');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(screen.getByText('Allele Saved')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Marks a deleted single-object section', async () => {
		const user = userEvent.setup();
		await renderLoadedPage();

		await user.click(sectionOf('Name').querySelector('.pi-trash').closest('button'));

		expect(within(sectionOf('Name')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getAllByText('Pending Edits!')).toHaveLength(1);
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
	});

	it('Keeps Save active when the allele is not saved', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValueOnce({ response: { data: { errorMessage: 'Allele could not be saved' } } });
		await renderLoadedPage();

		await user.type(symbolDisplayTextInput(), '-edited');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(screen.getByText('Allele could not be saved')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
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
