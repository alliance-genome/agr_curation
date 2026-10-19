import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { DUPLICATE_SOURCE_ENTRY, FORM_LOAD_WAIT, renderInRouter, sourceAllele } from './alleleTestUtils';

const createAllele = vi.fn();
const getAllele = vi.fn();
const getGeneAssociationsForAllele = vi.fn();
const replaceGeneAssociationsForAllele = vi.fn();
const navigate = vi.fn();
const saveAlleleDetail = vi.fn();
const replaceCrossReferencesForAllele = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ createAllele, getAllele, saveAlleleDetail })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService({
		'cross-references': { replace: replaceCrossReferencesForAllele },
		'gene-associations': { get: getGeneAssociationsForAllele, replace: replaceGeneAssociationsForAllele },
	})
);

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleCreatePage = (await import('../AlleleCreatePage')).default;

const storedAssociation = {
	id: 801,
	dateCreated: '2022-03-09T22:10:12Z',
	relation: { name: 'is_allele_of' },
	alleleGeneAssociationObject: { type: 'Gene', id: 31, primaryExternalId: 'WB:WBGene31' },
	relatedNote: { id: 19, freeText: 'a note', noteType: { name: 'comment' }, internal: false },
};

const renderPage = () => renderInRouter(<AlleleCreatePage />, DUPLICATE_SOURCE_ENTRY);

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
