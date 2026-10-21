import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { alleleDetailData } from '../mockData/mockData.js';

const {
	getAllele,
	saveAlleleDetail,
	getCrossReferencesForAllele,
	getReferencesForAllele,
	replaceReferencesForAllele,
	getGeneAssociationsForAllele,
	replaceGeneAssociationsForAllele,
} = vi.hoisted(() => ({
	getAllele: vi.fn(),
	saveAlleleDetail: vi.fn(),
	getCrossReferencesForAllele: vi.fn(),
	getReferencesForAllele: vi.fn(),
	replaceReferencesForAllele: vi.fn(),
	getGeneAssociationsForAllele: vi.fn(),
	replaceGeneAssociationsForAllele: vi.fn(),
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
		replaceCrossReferencesForAllele = vi.fn(() => Promise.resolve({ data: { entities: [] } }));
	},
}));

vi.mock('../../../service/ReferenceService', () => ({
	ReferenceService: class {
		getReferencesForAllele = getReferencesForAllele;
		replaceReferencesForAllele = replaceReferencesForAllele;
	},
}));

vi.mock('../../../service/AlleleGeneAssociationService', () => ({
	AlleleGeneAssociationService: class {
		getGeneAssociationsForAllele = getGeneAssociationsForAllele;
		replaceGeneAssociationsForAllele = replaceGeneAssociationsForAllele;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

// The shared fixture carries no id, and the page reads and writes gene associations by the allele's id.
const storedAllele = { ...alleleDetailData.entity, id: 4242 };

const storedAssociations = [
	{
		id: 801,
		relation: { name: 'is_allele_of' },
		alleleGeneAssociationObject: { type: 'Gene', id: 31, primaryExternalId: 'WB:WBGene31' },
		evidence: [{ type: 'Reference', id: 905, curie: 'AGRKB:105' }],
	},
	{
		id: 802,
		relation: { name: 'duplication' },
		alleleGeneAssociationObject: { type: 'Gene', id: 32, primaryExternalId: 'WB:WBGene32' },
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

describe('<AlleleDetailPage /> gene associations', { timeout: 30000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('AlleleDetailFormSettings');
		[
			getAllele,
			saveAlleleDetail,
			getCrossReferencesForAllele,
			getReferencesForAllele,
			replaceReferencesForAllele,
			getGeneAssociationsForAllele,
			replaceGeneAssociationsForAllele,
		].forEach((mock) => mock.mockReset());
		getAllele.mockResolvedValue({ data: { entity: storedAllele } });
		saveAlleleDetail.mockResolvedValue({ data: { entity: storedAllele } });
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		getReferencesForAllele.mockResolvedValue({
			data: { entities: [{ type: 'Reference', id: 901, curie: 'AGRKB:101', crossReferences: [] }] },
		});
		replaceReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		getGeneAssociationsForAllele.mockResolvedValue({ data: { entities: storedAssociations } });
		replaceGeneAssociationsForAllele.mockResolvedValue({ data: { entities: [storedAssociations[1]] } });
	});

	// The page reads the allele's gene associations once the allele has loaded, and refuses to save them until
	// that read is done, so the tests wait for it as a curator would.
	const waitForAssociationsToLoad = async () => {
		await waitFor(() => expect(getGeneAssociationsForAllele).toHaveBeenCalledWith(4242), { timeout: 5000 });
		await act(async () => {
			await new Promise((resolve) => setTimeout(resolve, 0));
		});
	};

	const associationRow = (geneIdentifier) => screen.getByDisplayValue(geneIdentifier).closest('tr');

	const deleteFirstAssociation = async (user) => {
		await waitForAssociationsToLoad();
		await user.click(associationRow('WB:WBGene31').querySelector('.pi-trash').closest('button'));
	};

	it('Reads the allele gene associations through their own sub-resource', async () => {
		await renderPage();
		await waitForAssociationsToLoad();

		expect(screen.getByDisplayValue('WB:WBGene31')).toBeInTheDocument();
		expect(screen.getByDisplayValue('WB:WBGene32')).toBeInTheDocument();
	});

	it('Saves a deleted gene association along with the allele, without sending them in the allele', async () => {
		const user = userEvent.setup();
		await renderPage();

		await deleteFirstAssociation(user);
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(replaceGeneAssociationsForAllele).toHaveBeenCalledWith(4242, [storedAssociations[1]]);
		expect(saveAlleleDetail.mock.calls[0][0]).not.toHaveProperty('alleleGeneAssociations');
	});

	it('Leaves untouched gene associations alone when the allele is saved', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForAssociationsToLoad();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled());
		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(replaceGeneAssociationsForAllele).not.toHaveBeenCalled();
	});

	it('Says the gene associations were saved when only the allele is rejected', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValue(alleleRejection);
		await renderPage();

		await deleteFirstAssociation(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Could not update Allele. The gene associations were saved.')).toBeInTheDocument();
	});

	it('Names every list that saved when only the allele is rejected', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValue(alleleRejection);
		await renderPage();

		await deleteFirstAssociation(user);
		await user.click(screen.getByRole('button', { name: 'Add Cross Reference' }));
		await user.click(screen.getByText('AGRKB:101').closest('tr').querySelector('.pi-trash').closest('button'));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(
			await screen.findByText(
				'Could not update Allele. The cross references, references, and gene associations were saved.'
			)
		).toBeInTheDocument();
	});

	it('Says the gene associations failed and marks the rejected row, while the allele saved', async () => {
		const user = userEvent.setup();
		replaceGeneAssociationsForAllele.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update Gene Associations',
					supplementalData: {
						errorMap: { alleleGeneAssociations: { 0: { alleleGeneAssociationObject: 'Not a valid entry' } } },
					},
				},
			},
		});
		await renderPage();

		await deleteFirstAssociation(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Gene associations not saved:')).toBeInTheDocument();
		expect(
			screen.getByText("Could not update Gene Associations. The allele's other changes were saved.")
		).toBeInTheDocument();
		expect(within(associationRow('WB:WBGene32')).getByText('Not a valid entry')).toBeInTheDocument();
		expect(screen.queryByText('Allele Saved')).not.toBeInTheDocument();
	});

	it('Refuses to write a row whose gene was not picked, and marks it', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForAssociationsToLoad();

		await user.click(screen.getByRole('button', { name: 'Add Gene Association' }));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Select each gene from the autosuggest', { exact: false })).toBeInTheDocument();
		expect(screen.getByText('Must select Gene from autosuggest')).toBeInTheDocument();
		expect(replaceGeneAssociationsForAllele).not.toHaveBeenCalled();
	});

	it('Says the gene associations cannot be saved when they failed to load, and refuses to write them', async () => {
		const user = userEvent.setup();
		getGeneAssociationsForAllele.mockRejectedValue(new Error('unavailable'));
		await renderPage();
		await waitForAssociationsToLoad();

		expect(
			screen.getByText('Could not load these gene associations, so they cannot be saved. Reload the page to try again.')
		).toBeInTheDocument();

		await user.click(screen.getByRole('button', { name: 'Add Gene Association' }));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(
			await screen.findByText('These gene associations have not been read, so they were not saved', { exact: false })
		).toBeInTheDocument();
		expect(replaceGeneAssociationsForAllele).not.toHaveBeenCalled();
	});

	const geneAssociationsSection = () =>
		screen.getByRole('heading', { name: 'Allele Gene Associations' }).closest('.grid');
	const sectionOf = (name) => screen.getByRole('heading', { name }).closest('.grid');

	it('Keeps Save Gene Associations inactive and marks nothing until the gene associations are edited', async () => {
		await renderPage();
		await waitForAssociationsToLoad();

		expect(screen.getByRole('button', { name: /Save Gene Associations/ })).toBeDisabled();
		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Marks edited gene associations as pending and offers their save', async () => {
		const user = userEvent.setup();
		await renderPage();

		await deleteFirstAssociation(user);

		expect(within(geneAssociationsSection()).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save Gene Associations/ })).toBeEnabled();
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
	});

	it('Clears the mark and both saves when an added row is deleted again', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForAssociationsToLoad();

		await user.click(screen.getByRole('button', { name: 'Add Gene Association' }));
		expect(within(geneAssociationsSection()).getByText('Pending Edits!')).toBeInTheDocument();

		// A new row is added at the top of the table.
		const addedRow = associationRow('WB:WBGene31').closest('tbody').querySelector('tr');
		expect(within(addedRow).queryByDisplayValue('WB:WBGene31')).not.toBeInTheDocument();
		await user.click(addedRow.querySelector('.pi-trash').closest('button'));

		expect(within(geneAssociationsSection()).queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save Gene Associations/ })).toBeDisabled();
		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
	});

	it('Clears the gene associations mark on their own save, leaving other pending edits marked', async () => {
		const user = userEvent.setup();
		await renderPage();

		await deleteFirstAssociation(user);
		await user.click(screen.getByRole('button', { name: 'Add Cross Reference' }));
		await user.click(screen.getByText('AGRKB:101').closest('tr').querySelector('.pi-trash').closest('button'));
		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: /Save Gene Associations/ }));

		expect(await screen.findByText('Gene Associations Saved')).toBeInTheDocument();
		expect(replaceGeneAssociationsForAllele).toHaveBeenCalledWith(4242, [storedAssociations[1]]);
		expect(saveAlleleDetail).not.toHaveBeenCalled();
		expect(replaceReferencesForAllele).not.toHaveBeenCalled();
		expect(within(geneAssociationsSection()).queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save Gene Associations/ })).toBeDisabled();
		expect(within(sectionOf('Cross References')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(within(sectionOf('References')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(within(sectionOf('Is Extinct')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
	});

	it('Keeps the mark and shows the rejected row when Save Gene Associations is refused', async () => {
		const user = userEvent.setup();
		replaceGeneAssociationsForAllele.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update Gene Associations',
					supplementalData: {
						errorMap: { alleleGeneAssociations: { 0: { alleleGeneAssociationObject: 'Not a valid entry' } } },
					},
				},
			},
		});
		await renderPage();

		await deleteFirstAssociation(user);
		await user.click(screen.getByRole('button', { name: /Save Gene Associations/ }));

		expect(await screen.findByText('Could not update Gene Associations')).toBeInTheDocument();
		expect(within(geneAssociationsSection()).getByText('Pending Edits!')).toBeInTheDocument();
		expect(within(associationRow('WB:WBGene32')).getByText('Not a valid entry')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save Gene Associations/ })).toBeEnabled();
	});

	it('Keeps Save Gene Associations inactive when the gene associations failed to load, even once edited', async () => {
		const user = userEvent.setup();
		getGeneAssociationsForAllele.mockRejectedValue(new Error('unavailable'));
		await renderPage();
		await waitForAssociationsToLoad();

		await user.click(screen.getByRole('button', { name: 'Add Gene Association' }));

		expect(within(geneAssociationsSection()).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save Gene Associations/ })).toBeDisabled();
		expect(replaceGeneAssociationsForAllele).not.toHaveBeenCalled();
	});

	it('Refuses a section save while a row has no gene picked, keeping the mark', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForAssociationsToLoad();

		await user.click(screen.getByRole('button', { name: 'Add Gene Association' }));
		await user.click(screen.getByRole('button', { name: /Save Gene Associations/ }));

		expect(await screen.findByText('Select each gene from the autosuggest', { exact: false })).toBeInTheDocument();
		expect(screen.getByText('Must select Gene from autosuggest')).toBeInTheDocument();
		expect(within(geneAssociationsSection()).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save Gene Associations/ })).toBeEnabled();
		expect(replaceGeneAssociationsForAllele).not.toHaveBeenCalled();
	});

	// Saving the rows from their own section leaves them with nothing pending, so the page's Save that
	// follows writes the allele alone rather than the same rows again.
	it('Does not rewrite rows already saved from their own section', async () => {
		const user = userEvent.setup();
		await renderPage();

		await deleteFirstAssociation(user);
		await user.click(screen.getByRole('button', { name: /Save Gene Associations/ }));
		expect(await screen.findByText('Gene Associations Saved')).toBeInTheDocument();
		expect(replaceGeneAssociationsForAllele).toHaveBeenCalledTimes(1);

		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(saveAlleleDetail).toHaveBeenCalledTimes(1);
		expect(replaceGeneAssociationsForAllele).toHaveBeenCalledTimes(1);
	});

	// A refused section save leaves the rows pending, so the page's Save tries them again.
	it('Retries rows a failed section save left unsaved', async () => {
		const user = userEvent.setup();
		replaceGeneAssociationsForAllele.mockRejectedValueOnce({
			response: { data: { errorMessage: 'Could not update Gene Associations' } },
		});
		await renderPage();

		await deleteFirstAssociation(user);
		await user.click(screen.getByRole('button', { name: /Save Gene Associations/ }));
		expect(await screen.findByText('Could not update Gene Associations')).toBeInTheDocument();
		expect(within(geneAssociationsSection()).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save Gene Associations/ })).toBeEnabled();

		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(replaceGeneAssociationsForAllele).toHaveBeenCalledTimes(2);
	});
});
