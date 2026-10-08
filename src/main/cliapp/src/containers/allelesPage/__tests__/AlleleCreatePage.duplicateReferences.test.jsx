import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createAllele = vi.fn();
const getAllele = vi.fn();
const getReferencesForAllele = vi.fn();
const replaceReferencesForAllele = vi.fn();
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
		getReferencesForAllele = getReferencesForAllele;
		replaceReferencesForAllele = replaceReferencesForAllele;
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

const storedReference = {
	type: 'Reference',
	id: 901,
	curie: 'AGRKB:101',
	shortCitation: 'Doe 2020',
	crossReferences: [{ referencedCurie: 'PMID:101' }],
};

const renderPage = () =>
	renderWithClient(
		<MemoryRouter initialEntries={[`/allele/create?from=${encodeURIComponent(SOURCE_CURIE)}`]}>
			<AlleleCreatePage />
		</MemoryRouter>
	);

const waitForSourceToLoad = async (container) => {
	await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
	await waitFor(() => expect(getReferencesForAllele).toHaveBeenCalledWith(1), FORM_LOAD_WAIT);
	await waitFor(
		() => expect(screen.queryByText('Loading allele to duplicate...')).not.toBeInTheDocument(),
		FORM_LOAD_WAIT
	);
};

describe('<AlleleCreatePage /> duplicating references', { timeout: 30000 }, () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000002' } } });
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: sourceAllele } });
		getReferencesForAllele.mockReset();
		getReferencesForAllele.mockResolvedValue({ data: { entities: [storedReference] } });
		replaceReferencesForAllele.mockReset();
		replaceReferencesForAllele.mockResolvedValue({ data: { entities: [storedReference] } });
		navigate.mockReset();
		saveAlleleDetail.mockReset();
		saveAlleleDetail.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000002' } } });
		replaceCrossReferencesForAllele.mockReset();
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		window.localStorage.removeItem('AlleleCreateFormSettings');
	});

	it('Copies the source references and writes them to the new allele once it exists', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();
		await waitForSourceToLoad(container);
		expect(screen.getByText('AGRKB:101')).toBeInTheDocument();

		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		await waitFor(() => expect(replaceReferencesForAllele).toHaveBeenCalled(), FORM_LOAD_WAIT);
		expect(replaceReferencesForAllele).toHaveBeenCalledWith(4242, [{ type: 'Reference', curie: 'AGRKB:101' }]);
		expect(createAllele.mock.calls[0][0]).not.toHaveProperty('references');
		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000002'), FORM_LOAD_WAIT);
	});

	it('Keeps the curator on the page when the references are refused', async () => {
		replaceReferencesForAllele.mockRejectedValue({
			response: { data: { errorMessage: 'Could not update References' } },
		});
		const user = userEvent.setup();
		const { container } = await renderPage();
		await waitForSourceToLoad(container);

		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		expect(await screen.findByText('References not saved:', {}, FORM_LOAD_WAIT)).toBeInTheDocument();
		expect(navigate).not.toHaveBeenCalled();
	});

	// The allele exists after the first try, so saving again updates it and writes only the lists changed since.
	const saveWithCrossReferencesRefused = async (user) => {
		replaceCrossReferencesForAllele.mockRejectedValueOnce({
			response: { data: { errorMessage: 'Could not update CrossReferences' } },
		});
		const { container } = await renderPage();
		await waitForSourceToLoad(container);
		await user.click(screen.getByRole('button', { name: 'Add Cross Reference' }));

		await user.click(screen.getByRole('button', { name: 'Save & Close' }));
		expect(await screen.findByText('Cross references not saved:', {}, FORM_LOAD_WAIT)).toBeInTheDocument();
		expect(replaceReferencesForAllele).toHaveBeenCalledTimes(1);
	};

	it('Does not rewrite references that saved when saving again', async () => {
		const user = userEvent.setup();
		await saveWithCrossReferencesRefused(user);

		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		await waitFor(() => expect(navigate).toHaveBeenCalled(), FORM_LOAD_WAIT);
		expect(saveAlleleDetail).toHaveBeenCalledTimes(1);
		expect(replaceReferencesForAllele).toHaveBeenCalledTimes(1);
	});

	it('Writes references emptied since they saved when saving again', async () => {
		const user = userEvent.setup();
		await saveWithCrossReferencesRefused(user);

		await user.click(screen.getByText('AGRKB:101').closest('tr').querySelector('.pi-trash').closest('button'));
		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		await waitFor(() => expect(navigate).toHaveBeenCalled(), FORM_LOAD_WAIT);
		expect(replaceReferencesForAllele).toHaveBeenCalledTimes(2);
		expect(replaceReferencesForAllele).toHaveBeenLastCalledWith(4242, []);
	});

	it('Still starts from the copy when the references cannot be loaded', async () => {
		getReferencesForAllele.mockRejectedValue(new Error('unavailable'));
		const { container } = await renderPage();

		await waitFor(
			() => expect(screen.getByText('Could not load the references of allele WB:WBVar1')).toBeInTheDocument(),
			FORM_LOAD_WAIT
		);
		expect(container.querySelector('#displayText')).toHaveValue('abc-1');
	});
});
