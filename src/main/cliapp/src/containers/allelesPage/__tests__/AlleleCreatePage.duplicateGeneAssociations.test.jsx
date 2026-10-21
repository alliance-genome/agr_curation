import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createAllele = vi.fn();
const getAllele = vi.fn();
const getGeneAssociationsForAllele = vi.fn();
const replaceGeneAssociationsForAllele = vi.fn();
const navigate = vi.fn();
const saveAlleleDetail = vi.fn();
const replaceCrossReferencesForAllele = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		createAllele = createAllele;
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
		replaceCrossReferencesForAllele = replaceCrossReferencesForAllele;
	},
}));

vi.mock('../../../service/ReferenceService', () => ({
	ReferenceService: class {
		getReferencesForAllele = vi.fn(() => Promise.resolve({ data: { entities: [] } }));
		replaceReferencesForAllele = vi.fn();
	},
}));

vi.mock('../../../service/AlleleGeneAssociationService', () => ({
	AlleleGeneAssociationService: class {
		getGeneAssociationsForAllele = getGeneAssociationsForAllele;
		replaceGeneAssociationsForAllele = replaceGeneAssociationsForAllele;
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
	internal: false,
	obsolete: false,
	taxon: { curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	alleleSymbol: { id: 4, displayText: 'abc-1', formatText: 'abc-1', nameType: { name: 'nomenclature_symbol' } },
};

const storedAssociation = {
	id: 801,
	dateCreated: '2022-03-09T22:10:12Z',
	relation: { name: 'is_allele_of' },
	alleleGeneAssociationObject: { type: 'Gene', id: 31, primaryExternalId: 'WB:WBGene31' },
	relatedNote: { id: 19, freeText: 'a note', noteType: { name: 'comment' }, internal: false },
};

const renderPage = () =>
	renderWithClient(
		<MemoryRouter initialEntries={[`/allele/create?from=${encodeURIComponent(SOURCE_CURIE)}`]}>
			<AlleleCreatePage />
		</MemoryRouter>
	);

const waitForSourceToLoad = async (container) => {
	await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
	await waitFor(() => expect(getGeneAssociationsForAllele).toHaveBeenCalledWith(1), FORM_LOAD_WAIT);
	await waitFor(
		() => expect(screen.queryByText('Loading allele to duplicate...')).not.toBeInTheDocument(),
		FORM_LOAD_WAIT
	);
};

describe('<AlleleCreatePage /> duplicating gene associations', { timeout: 30000 }, () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000002' } } });
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: sourceAllele } });
		getGeneAssociationsForAllele.mockReset();
		getGeneAssociationsForAllele.mockResolvedValue({ data: { entities: [storedAssociation] } });
		replaceGeneAssociationsForAllele.mockReset();
		replaceGeneAssociationsForAllele.mockResolvedValue({ data: { entities: [storedAssociation] } });
		navigate.mockReset();
		saveAlleleDetail.mockReset();
		saveAlleleDetail.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000002' } } });
		replaceCrossReferencesForAllele.mockReset();
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		window.localStorage.removeItem('AlleleCreateFormSettings');
	});

	it('Copies the source gene associations and writes them, as new ones, to the new allele once it exists', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();
		await waitForSourceToLoad(container);
		expect(screen.getByDisplayValue('WB:WBGene31')).toBeInTheDocument();

		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		await waitFor(() => expect(replaceGeneAssociationsForAllele).toHaveBeenCalled(), FORM_LOAD_WAIT);
		const [alleleId, [written]] = replaceGeneAssociationsForAllele.mock.calls[0];
		expect(alleleId).toEqual(4242);
		expect(written).not.toHaveProperty('id');
		expect(written).not.toHaveProperty('dateCreated');
		expect(written.relatedNote).not.toHaveProperty('id');
		expect(written.alleleGeneAssociationObject).toEqual(storedAssociation.alleleGeneAssociationObject);
		expect(createAllele.mock.calls[0][0]).not.toHaveProperty('alleleGeneAssociations');
		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000002'), FORM_LOAD_WAIT);
	});

	it('Keeps the curator on the page when the gene associations are refused', async () => {
		replaceGeneAssociationsForAllele.mockRejectedValue({
			response: { data: { errorMessage: 'Could not update Gene Associations' } },
		});
		const user = userEvent.setup();
		const { container } = await renderPage();
		await waitForSourceToLoad(container);

		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		expect(await screen.findByText('Gene associations not saved:', {}, FORM_LOAD_WAIT)).toBeInTheDocument();
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Creates the allele but writes no gene associations while a row has no gene picked', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();
		await waitForSourceToLoad(container);

		await user.click(screen.getByRole('button', { name: 'Add Gene Association' }));
		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		expect(await screen.findByText('Gene associations not saved:', {}, FORM_LOAD_WAIT)).toBeInTheDocument();
		expect(createAllele).toHaveBeenCalledTimes(1);
		expect(replaceGeneAssociationsForAllele).not.toHaveBeenCalled();
		expect(screen.getByText('Must select Gene from autosuggest')).toBeInTheDocument();
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Still starts from the copy when the gene associations cannot be loaded', async () => {
		getGeneAssociationsForAllele.mockRejectedValue(new Error('unavailable'));
		const { container } = await renderPage();

		await waitFor(
			() => expect(screen.getByText('Could not load the gene associations of allele WB:WBVar1')).toBeInTheDocument(),
			FORM_LOAD_WAIT
		);
		expect(container.querySelector('#displayText')).toHaveValue('abc-1');
	});
});
