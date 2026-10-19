import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { DUPLICATE_SOURCE_ENTRY, FORM_LOAD_WAIT, renderInRouter, sourceAllele } from './alleleTestUtils';

const createAllele = vi.fn();
const getAllele = vi.fn();
const getReferencesForAllele = vi.fn();
const replaceReferencesForAllele = vi.fn();
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
		references: { get: getReferencesForAllele, replace: replaceReferencesForAllele },
	})
);

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleCreatePage = (await import('../AlleleCreatePage')).default;

const storedReference = {
	type: 'Reference',
	id: 901,
	curie: 'AGRKB:101',
	shortCitation: 'Doe 2020',
	crossReferences: [{ referencedCurie: 'PMID:101' }],
};

const renderPage = () => renderInRouter(<AlleleCreatePage />, DUPLICATE_SOURCE_ENTRY);

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
