import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { alleleDetailData } from '../mockData/mockData.js';

const { getAllele, saveAlleleDetail, getCrossReferencesForAllele, getReferencesForAllele, replaceReferencesForAllele } =
	vi.hoisted(() => ({
		getAllele: vi.fn(),
		saveAlleleDetail: vi.fn(),
		getCrossReferencesForAllele: vi.fn(),
		getReferencesForAllele: vi.fn(),
		replaceReferencesForAllele: vi.fn(),
	}));

// BaseAuthService builds no client without a cognito token in local storage, so msw has nothing to
// intercept here; the services are stubbed directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		getAllele = getAllele;
		saveAlleleDetail = saveAlleleDetail;
	},
}));

vi.mock('../../../service/CrossReferenceService', () => ({
	CrossReferenceService: class {
		getCrossReferencesForAllele = getCrossReferencesForAllele;
		replaceCrossReferencesForAllele = vi.fn();
	},
}));

vi.mock('../../../service/ReferenceService', () => ({
	ReferenceService: class {
		getReferencesForAllele = getReferencesForAllele;
		replaceReferencesForAllele = replaceReferencesForAllele;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

// The shared fixture carries no id, and the page reads and writes references by the allele's id.
const storedAllele = { ...alleleDetailData.entity, id: 4242 };

const storedReferences = [
	{
		type: 'Reference',
		id: 901,
		curie: 'AGRKB:101',
		shortCitation: 'Doe 2020',
		crossReferences: [{ referencedCurie: 'PMID:101' }],
	},
	{
		type: 'Reference',
		id: 902,
		curie: 'AGRKB:102',
		shortCitation: 'Roe 2021',
		crossReferences: [{ referencedCurie: 'PMID:102' }],
	},
];

const alleleRejection = {
	response: {
		status: 400,
		statusText: 'Bad Request',
		data: { errorMessage: 'Could not update Allele', errorMessages: { taxon: 'Taxon is not valid' } },
	},
};

const renderPage = () =>
	renderWithClient(
		<BrowserRouter>
			<AlleleDetailPage />
		</BrowserRouter>
	);

// An edit to the allele alone, so the page's Save has something to write.
const markAlleleExtinct = async (user) => {
	const isExtinctRow = screen.getByRole('heading', { name: 'Is Extinct' }).closest('.grid');
	await user.click(isExtinctRow.querySelector('.p-dropdown'));
	const panel = await waitFor(() => {
		const openPanel = document.querySelector('.p-dropdown-panel');
		expect(openPanel).not.toBeNull();
		return openPanel;
	});
	await user.click(within(panel).getByText('true'));
};

describe('<AlleleDetailPage /> references', () => {
	beforeEach(() => {
		window.localStorage.removeItem('AlleleDetailFormSettings');
		getAllele.mockReset();
		saveAlleleDetail.mockReset();
		getCrossReferencesForAllele.mockReset();
		getReferencesForAllele.mockReset();
		replaceReferencesForAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: storedAllele } });
		saveAlleleDetail.mockResolvedValue({ data: { entity: storedAllele } });
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		getReferencesForAllele.mockResolvedValue({ data: { entities: storedReferences } });
		replaceReferencesForAllele.mockResolvedValue({ data: { entities: [storedReferences[1]] } });
	});

	// The page reads the allele's references once the allele has loaded, and refuses to save them until
	// that read is done, so the tests wait for it as a curator would.
	const waitForReferencesToLoad = async () => {
		await waitFor(() => expect(getReferencesForAllele).toHaveBeenCalledWith(4242), { timeout: 5000 });
		await act(async () => {
			await new Promise((resolve) => setTimeout(resolve, 0));
		});
	};

	const referenceRow = (curie) => screen.getByText(curie).closest('tr');

	const deleteFirstReference = async (user) => {
		await waitForReferencesToLoad();
		await user.click(referenceRow('AGRKB:101').querySelector('.pi-trash').closest('button'));
	};

	it('Reads the allele references through their own sub-resource', async () => {
		await renderPage();
		await waitForReferencesToLoad();

		expect(screen.getByText('AGRKB:101')).toBeInTheDocument();
		expect(screen.getByText('Roe 2021')).toBeInTheDocument();
	});

	it('Saves deleted references along with the allele, without sending them in the allele', async () => {
		const user = userEvent.setup();
		await renderPage();

		await deleteFirstReference(user);
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(replaceReferencesForAllele).toHaveBeenCalledWith(4242, [{ type: 'Reference', curie: 'AGRKB:102' }]);
		expect(saveAlleleDetail.mock.calls[0][0]).not.toHaveProperty('references');
	});

	it('Leaves untouched references alone when the allele is saved', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForReferencesToLoad();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled());
		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(replaceReferencesForAllele).not.toHaveBeenCalled();
	});

	it('Says the references were saved when only the allele is rejected', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValue(alleleRejection);
		await renderPage();

		await deleteFirstReference(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Could not update Allele. The references were saved.')).toBeInTheDocument();
	});

	it('Names every list that saved when only the allele is rejected', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValue(alleleRejection);
		await renderPage();

		await deleteFirstReference(user);
		await user.click(screen.getByRole('button', { name: 'Add Cross Reference' }));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(
			await screen.findByText('Could not update Allele. The cross references and references were saved.')
		).toBeInTheDocument();
	});

	it('Says the references failed and marks the rejected row, while the allele saved', async () => {
		const user = userEvent.setup();
		replaceReferencesForAllele.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update References',
					supplementalData: { errorMap: { references: { 0: { curie: 'Not a valid entry' } } } },
				},
			},
		});
		await renderPage();

		await deleteFirstReference(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('References not saved:')).toBeInTheDocument();
		expect(screen.getByText("Could not update References. The allele's other changes were saved.")).toBeInTheDocument();
		expect(within(referenceRow('AGRKB:102')).getByText('Not a valid entry')).toBeInTheDocument();
		expect(screen.queryByText('Allele Saved')).not.toBeInTheDocument();
	});
});
