import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createAllele = vi.fn();
const getAllele = vi.fn();
const navigate = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		createAllele = createAllele;
		getAllele = getAllele;
		saveAlleleDetail = vi.fn();
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

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleCreatePage = (await import('../AlleleCreatePage')).default;

// each wait follows a stubbed call through a full render of the form, which can outlast the default under load
const FORM_LOAD_WAIT = { timeout: 5000 };

const SOURCE_CURIE = 'AGRKB:101000000000001';

const sourceAllele = {
	type: 'Allele',
	id: 1,
	curie: SOURCE_CURIE,
	primaryExternalId: 'WB:WBVar1',
	dataProvider: { abbreviation: 'WB' },
	dateCreated: '2024-01-01T00:00:00Z',
	internal: false,
	obsolete: false,
	taxon: { curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	alleleSymbol: {
		id: 4,
		displayText: 'abc-1',
		formatText: 'abc-1',
		nameType: { name: 'nomenclature_symbol' },
		internal: false,
	},
};

const renderPage = (entry = `/allele/create?from=${encodeURIComponent(SOURCE_CURIE)}`) =>
	renderWithClient(
		<MemoryRouter initialEntries={[entry]}>
			<AlleleCreatePage />
		</MemoryRouter>
	);

const button = (name) => screen.getByRole('button', { name });
// a populated field carries its own clear button, so the footer's is found by its label
const footerButton = (label) => screen.getByText(label, { selector: '.p-button-label' }).closest('button');

describe('<AlleleCreatePage /> duplicating an allele', () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000002' } } });
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: sourceAllele } });
		navigate.mockReset();
		window.localStorage.removeItem('AlleleCreateFormSettings');
	});

	it('Starts from the allele named in the URL', async () => {
		const { container } = await renderPage();

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		expect(getAllele).toHaveBeenCalledWith(SOURCE_CURIE);
		await waitFor(
			() => expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('NCBITaxon:6239'),
			FORM_LOAD_WAIT
		);
	});

	it('Shows only the sections the copied allele has rows for', async () => {
		getAllele.mockResolvedValue({
			data: {
				entity: {
					...sourceAllele,
					alleleMutationTypes: [
						{ id: 10, internal: false, mutationTypes: [{ curie: 'SO:0001', name: 'point_mutation' }] },
					],
					alleleFunctionalImpacts: [],
				},
			},
		});
		const { container } = await renderPage();

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		expect(screen.getByRole('columnheader', { name: 'Mutation Types' })).toBeInTheDocument();
		expect(screen.queryByRole('columnheader', { name: 'Functional Impacts' })).not.toBeInTheDocument();
		expect(screen.queryByRole('columnheader', { name: 'Inheritance Mode' })).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'Add Functional Impact' })).toBeInTheDocument();
	});

	it('Says the copy is loading, not saving, while the allele is fetched', async () => {
		let resolveAllele;
		getAllele.mockReturnValue(new Promise((resolve) => (resolveAllele = resolve)));
		const { container } = await renderPage();

		await waitFor(
			() => expect(screen.getByRole('heading', { name: 'Loading allele to duplicate...' })).toBeInTheDocument(),
			FORM_LOAD_WAIT
		);
		expect(screen.queryByText('Saving in progress...')).not.toBeInTheDocument();

		resolveAllele({ data: { entity: sourceAllele } });
		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		expect(screen.queryByText('Loading allele to duplicate...')).not.toBeInTheDocument();
	});

	it('Creates a new allele rather than sending the stored one back', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		await user.click(button('Save & Close'));

		await waitFor(() => expect(createAllele).toHaveBeenCalled(), FORM_LOAD_WAIT);
		const payload = createAllele.mock.calls[0][0];
		['id', 'curie', 'primaryExternalId', 'dataProvider', 'dateCreated'].forEach((field) =>
			expect(payload).not.toHaveProperty(field)
		);
		expect(payload.alleleSymbol).not.toHaveProperty('id');
		expect(payload.alleleSymbol.displayText).toEqual('abc-1');
		expect(payload.taxon.curie).toEqual('NCBITaxon:6239');
	});

	it('Shows a duplicate symbol rejection beside the symbol', async () => {
		const user = userEvent.setup();
		createAllele.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: {
					errorMessage: 'Could not create Allele',
					errorMessages: { alleleSymbol: 'displayText - Field value is not unique' },
					supplementalData: { errorMap: { alleleSymbol: { displayText: 'Field value is not unique' } } },
				},
			},
		});
		const { container } = await renderPage();

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		await user.click(button('Save & Close'));

		await waitFor(() => expect(screen.getByText('Field value is not unique')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Stays blank after clearing the copy', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		await user.click(footerButton('Clear'));

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue(''), FORM_LOAD_WAIT);
		expect(getAllele).toHaveBeenCalledTimes(1);
	});

	it('Says so when the allele to duplicate cannot be found', async () => {
		getAllele.mockResolvedValue({ data: {} });
		const { container } = await renderPage('/allele/create?from=AGRKB:missing');

		await waitFor(
			() => expect(screen.getByText('Could not load allele AGRKB:missing to duplicate')).toBeInTheDocument(),
			FORM_LOAD_WAIT
		);
		expect(container.querySelector('#displayText')).toHaveValue('');
	});

	it('Does not look an allele up without one in the URL', async () => {
		const { container } = await renderPage('/allele/create');

		expect(container.querySelector('#displayText')).toHaveValue('');
		expect(getAllele).not.toHaveBeenCalled();
	});
});
