import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {
	DUPLICATE_SOURCE_ENTRY,
	FORM_LOAD_WAIT,
	NAVIGATION_WAIT,
	renderInRouter,
	sourceAllele,
} from './alleleTestUtils';

const createAllele = vi.fn();
const getAllele = vi.fn();
const getMutationTypesForAllele = vi.fn();
const replaceMutationTypesForAllele = vi.fn();
const getRelatedNotesForAllele = vi.fn();
const replaceRelatedNotesForAllele = vi.fn();
const navigate = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ createAllele, getAllele })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService({
		mutationtypes: { get: getMutationTypesForAllele, replace: replaceMutationTypesForAllele },
		relatednotes: { get: getRelatedNotesForAllele, replace: replaceRelatedNotesForAllele },
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

const storedNote = {
	id: 11,
	dateCreated: '2024-01-01T00:00:00Z',
	internal: false,
	freeText: 'a note',
	noteType: { name: 'comment' },
};

// An allele as the API returns it from create. A curator-created allele has no primary external ID, so its curie
// names it.
const createdAllele = (id, curie) => {
	/* eslint-disable-next-line no-unused-vars */
	const { primaryExternalId, ...copiedFields } = sourceAllele;
	return { ...copiedFields, id, curie };
};

// The page is opened to duplicate an allele, which is the quickest way to fill its lists.
const renderPage = () => renderInRouter(<AlleleCreatePage />, DUPLICATE_SOURCE_ENTRY);

const waitForSourceToLoad = async (container) => {
	await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
	await waitFor(() => expect(getRelatedNotesForAllele).toHaveBeenCalledWith(1), FORM_LOAD_WAIT);
	await waitFor(
		() => expect(screen.queryByText('Loading allele to duplicate...')).not.toBeInTheDocument(),
		FORM_LOAD_WAIT
	);
};

// Save & Add Another starts the next allele once the save progress overlay closes.
const waitForSaveToFinish = () =>
	waitFor(
		() => expect(screen.queryByRole('status', { name: 'Save progress' })).not.toBeInTheDocument(),
		NAVIGATION_WAIT
	);

describe('<AlleleCreatePage /> save and add another', { timeout: 30000 }, () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele
			.mockResolvedValueOnce({ data: { entity: createdAllele(4242, 'AGRKB:101000000000002') } })
			.mockResolvedValueOnce({ data: { entity: createdAllele(4343, 'AGRKB:101000000000003') } });
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: sourceAllele } });
		getMutationTypesForAllele.mockReset();
		getMutationTypesForAllele.mockResolvedValue({ data: { entities: [storedMutationType] } });
		replaceMutationTypesForAllele.mockReset();
		replaceMutationTypesForAllele.mockResolvedValue({ data: { entities: [{ ...storedMutationType, id: 20 }] } });
		getRelatedNotesForAllele.mockReset();
		getRelatedNotesForAllele.mockResolvedValue({ data: { entities: [storedNote] } });
		replaceRelatedNotesForAllele.mockReset();
		replaceRelatedNotesForAllele.mockResolvedValue({ data: { entities: [{ ...storedNote, id: 21 }] } });
		navigate.mockReset();
		window.localStorage.removeItem('AlleleCreateFormSettings');
	});

	it('Writes the kept rows, as new rows, to the next allele', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();
		await waitForSourceToLoad(container);

		await user.click(screen.getByRole('button', { name: 'Save & Add Another' }));
		await waitFor(
			() => expect(replaceRelatedNotesForAllele).toHaveBeenCalledWith(4242, expect.any(Array)),
			FORM_LOAD_WAIT
		);
		await screen.findByText('Allele AGRKB:101000000000002 created');
		await waitForSaveToFinish();
		expect(screen.getByText('a note')).toBeInTheDocument();
		expect(screen.getByRole('columnheader', { name: 'Mutation Types' })).toBeInTheDocument();

		await user.click(screen.getByRole('button', { name: 'Save & Add Another' }));

		await waitFor(() => expect(createAllele).toHaveBeenCalledTimes(2), FORM_LOAD_WAIT);
		await waitFor(() => expect(replaceRelatedNotesForAllele).toHaveBeenCalledTimes(2), FORM_LOAD_WAIT);
		expect(replaceRelatedNotesForAllele).toHaveBeenLastCalledWith(4343, [
			{ internal: false, freeText: 'a note', noteType: { name: 'comment' } },
		]);
		await waitFor(() => expect(replaceMutationTypesForAllele).toHaveBeenCalledTimes(2), FORM_LOAD_WAIT);
		expect(replaceMutationTypesForAllele).toHaveBeenLastCalledWith(4343, [
			{
				internal: false,
				mutationTypes: [{ curie: 'SO:0001', name: 'point_mutation' }],
				evidence: [{ type: 'Reference', curie: 'AGRKB:101' }],
			},
		]);
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Empties the kept rows on clear', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();
		await waitForSourceToLoad(container);

		await user.click(screen.getByRole('button', { name: 'Save & Add Another' }));
		await screen.findByText('Allele AGRKB:101000000000002 created');
		await waitForSaveToFinish();
		expect(screen.getByText('a note')).toBeInTheDocument();
		expect(container.querySelector('#displayText')).toHaveValue('abc-1');

		// The shown tables carry column filter buttons also named Clear.
		await user.click(screen.getByText('Clear').closest('button'));

		await waitFor(() => expect(screen.queryByText('a note')).not.toBeInTheDocument());
		expect(container.querySelector('#displayText')).toHaveValue('');
	});
});
