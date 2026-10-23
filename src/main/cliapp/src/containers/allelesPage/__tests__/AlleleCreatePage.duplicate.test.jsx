import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {
	button,
	DUPLICATE_SOURCE_ENTRY,
	FORM_LOAD_WAIT,
	renderInRouter,
	SOURCE_CURIE,
	sourceAllele as sharedSourceAllele,
} from './alleleTestUtils';

const createAllele = vi.fn();
const getAllele = vi.fn();
const navigate = vi.fn();
const getMutationTypesForAllele = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ createAllele, getAllele })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
// The source's sub-resources are read through their own endpoints; this allele has none unless a test gives it
// mutation types.
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService({ mutationtypes: { get: getMutationTypesForAllele } })
);

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleCreatePage = (await import('../AlleleCreatePage')).default;

// The source as the API stores it, with a data provider and creation date for the copy to leave behind.
const sourceAllele = {
	...sharedSourceAllele,
	dataProvider: { abbreviation: 'WB' },
	dateCreated: '2024-01-01T00:00:00Z',
	alleleSymbol: { ...sharedSourceAllele.alleleSymbol, internal: false },
};

const renderPage = (entry = DUPLICATE_SOURCE_ENTRY) => renderInRouter(<AlleleCreatePage />, entry);

// a populated field carries its own clear button, so the footer's is found by its label
const footerButton = (label) => screen.getByText(label, { selector: '.p-button-label' }).closest('button');

describe('<AlleleCreatePage /> duplicating an allele', () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000002' } } });
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: sourceAllele } });
		navigate.mockReset();
		getMutationTypesForAllele.mockReset();
		getMutationTypesForAllele.mockResolvedValue({ data: { entities: [] } });
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
		getMutationTypesForAllele.mockResolvedValue({
			data: {
				entities: [{ id: 10, internal: false, mutationTypes: [{ curie: 'SO:0001', name: 'point_mutation' }] }],
			},
		});
		const { container } = await renderPage();

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		expect(await screen.findByRole('columnheader', { name: 'Mutation Types' }, FORM_LOAD_WAIT)).toBeInTheDocument();
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

	it('Shows a duplicate symbol rejection on both the symbol and the taxon', async () => {
		const user = userEvent.setup();
		const notUniqueInTaxon = 'Another allele is already using this taxon/symbol combination';
		// the shape AlleleValidator returns for a symbol another allele of the taxon already has
		createAllele.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: {
					errorMessage: 'Could not create Allele',
					errorMessages: { alleleSymbol: `displayText - ${notUniqueInTaxon}`, taxon: notUniqueInTaxon },
					supplementalData: {
						errorMap: { alleleSymbol: { displayText: notUniqueInTaxon }, taxon: notUniqueInTaxon },
					},
				},
			},
		});
		const { container } = await renderPage();

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		await user.click(button('Save & Close'));

		// once beside the symbol's display text, once under the taxon
		await waitFor(() => expect(screen.getAllByText(notUniqueInTaxon)).toHaveLength(2), FORM_LOAD_WAIT);
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
