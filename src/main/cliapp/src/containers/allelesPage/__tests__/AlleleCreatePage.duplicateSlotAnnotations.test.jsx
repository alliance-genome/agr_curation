import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { DUPLICATE_SOURCE_ENTRY, FORM_LOAD_WAIT, renderInRouter, sourceAllele } from './alleleTestUtils';

const createAllele = vi.fn();
const getAllele = vi.fn();
const getMutationTypesForAllele = vi.fn();
const replaceMutationTypesForAllele = vi.fn();
const getSecondaryIdsForAllele = vi.fn();
const replaceSecondaryIdsForAllele = vi.fn();
const navigate = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ createAllele, getAllele })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService({
		mutationtypes: { get: getMutationTypesForAllele, replace: replaceMutationTypesForAllele },
		secondaryids: { get: getSecondaryIdsForAllele, replace: replaceSecondaryIdsForAllele },
	})
);

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleCreatePage = (await import('../AlleleCreatePage')).default;

const storedMutationType = {
	id: 10,
	dateCreated: '2024-01-01T00:00:00Z',
	internal: false,
	mutationTypes: [{ curie: 'SO:0001', name: 'point_mutation' }],
	evidence: [{ type: 'Reference', curie: 'AGRKB:101' }],
};

const renderPage = () => renderInRouter(<AlleleCreatePage />, DUPLICATE_SOURCE_ENTRY);

const waitForSourceToLoad = async (container) => {
	await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
	await waitFor(() => expect(getMutationTypesForAllele).toHaveBeenCalledWith(1), FORM_LOAD_WAIT);
	await waitFor(
		() => expect(screen.queryByText('Loading allele to duplicate...')).not.toBeInTheDocument(),
		FORM_LOAD_WAIT
	);
};

describe('<AlleleCreatePage /> duplicating slot annotation lists', { timeout: 30000 }, () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000002' } } });
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: sourceAllele } });
		getMutationTypesForAllele.mockReset();
		getMutationTypesForAllele.mockResolvedValue({ data: { entities: [storedMutationType] } });
		replaceMutationTypesForAllele.mockReset();
		replaceMutationTypesForAllele.mockResolvedValue({ data: { entities: [{ ...storedMutationType, id: 20 }] } });
		getSecondaryIdsForAllele.mockReset();
		getSecondaryIdsForAllele.mockResolvedValue({ data: { entities: [{ id: 8, secondaryId: 'WB:WBVar0' }] } });
		replaceSecondaryIdsForAllele.mockReset();
		replaceSecondaryIdsForAllele.mockResolvedValue({ data: { entities: [] } });
		navigate.mockReset();
		window.localStorage.removeItem('AlleleCreateFormSettings');
	});

	it('Copies the source rows and writes them, as new rows, to the new allele once it exists', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();
		await waitForSourceToLoad(container);
		expect(screen.getByRole('columnheader', { name: 'Mutation Types' })).toBeInTheDocument();

		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		await waitFor(() => expect(replaceMutationTypesForAllele).toHaveBeenCalled(), FORM_LOAD_WAIT);
		expect(replaceMutationTypesForAllele).toHaveBeenCalledWith(4242, [
			{
				internal: false,
				mutationTypes: [{ curie: 'SO:0001', name: 'point_mutation' }],
				evidence: [{ type: 'Reference', curie: 'AGRKB:101' }],
			},
		]);
		expect(createAllele.mock.calls[0][0].alleleMutationTypes ?? []).toEqual([]);
		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000002'), FORM_LOAD_WAIT);
	});

	it('Copies no secondary IDs, which identify the source allele', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();
		await waitForSourceToLoad(container);

		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		await waitFor(() => expect(navigate).toHaveBeenCalled(), FORM_LOAD_WAIT);
		expect(getSecondaryIdsForAllele).not.toHaveBeenCalled();
		expect(replaceSecondaryIdsForAllele).not.toHaveBeenCalled();
	});
});
