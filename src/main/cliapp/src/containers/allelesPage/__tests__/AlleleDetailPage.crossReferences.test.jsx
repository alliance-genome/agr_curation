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
	replaceCrossReferencesForAllele,
	validate,
	getResourceDescriptor,
} = vi.hoisted(() => ({
	getAllele: vi.fn(),
	saveAlleleDetail: vi.fn(),
	getCrossReferencesForAllele: vi.fn(),
	replaceCrossReferencesForAllele: vi.fn(),
	validate: vi.fn(),
	getResourceDescriptor: vi.fn(),
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
		replaceCrossReferencesForAllele = replaceCrossReferencesForAllele;
	},
}));

vi.mock('../../../service/ResourceDescriptorService', () => ({
	ResourceDescriptorService: class {
		getResourceDescriptor = getResourceDescriptor;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

// The shared fixture carries no id, and the page reads and writes cross references by the allele's id.
const storedAllele = { ...alleleDetailData.entity, id: 4242 };

const storedCrossReference = {
	id: 500,
	displayName: 'PubMed 1',
	referencedCurie: 'PMID:1',
	resourceDescriptorPage: { id: 1, name: 'default', resourceDescriptor: { id: 9, prefix: 'PMID' } },
	internal: false,
	obsolete: false,
};

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

const renderPage = () =>
	renderWithClient(
		<BrowserRouter>
			<AlleleDetailPage />
		</BrowserRouter>
	);

describe('<AlleleDetailPage /> cross references', () => {
	beforeEach(() => {
		window.localStorage.removeItem('AlleleDetailFormSettings');
		getAllele.mockReset();
		saveAlleleDetail.mockReset();
		getCrossReferencesForAllele.mockReset();
		replaceCrossReferencesForAllele.mockReset();
		validate.mockReset();
		getResourceDescriptor.mockReset();
		getResourceDescriptor.mockResolvedValue({
			data: { entity: { id: 9, prefix: 'PMID', resourcePages: [{ id: 1, name: 'default' }] } },
		});
		getAllele.mockResolvedValue({ data: { entity: storedAllele } });
		saveAlleleDetail.mockResolvedValue({ data: { entity: storedAllele } });
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
	});

	// The page reads the allele's cross references once the allele has loaded, and refuses to save them
	// until that read is done, so the tests wait for it as a curator would.
	const waitForCrossReferencesToLoad = async () => {
		await waitFor(() => expect(getCrossReferencesForAllele).toHaveBeenCalledWith(4242), { timeout: 5000 });
		await act(async () => {
			await new Promise((resolve) => setTimeout(resolve, 0));
		});
	};

	// Other sections of the page have their own editors, so a cross reference's are found in its row,
	// reached from its descriptor cell.
	const crossReferenceRow = () => screen.getByLabelText('resourceDescriptor').closest('tr');

	const editCrossReference = async (user) => {
		await waitForCrossReferencesToLoad();
		await user.type(crossReferenceRow().querySelector('#displayName'), ' edited');
	};

	const editCrossReferenceAndSave = async (user) => {
		await editCrossReference(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));
	};

	const crossReferencesSection = () => screen.getByRole('heading', { name: 'Cross References' }).closest('.grid');

	it('Keeps the section save inactive and marks nothing until the cross references are edited', async () => {
		await renderPage();
		await waitForCrossReferencesToLoad();

		expect(screen.getByRole('button', { name: /Save Cross References/ })).toBeDisabled();
		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Marks edited cross references as pending and offers their save', async () => {
		const user = userEvent.setup();
		await renderPage();

		await editCrossReference(user);

		expect(screen.getByRole('button', { name: /Save Cross References/ })).toBeEnabled();
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
		expect(within(crossReferencesSection()).getByText('Pending Edits!')).toBeInTheDocument();
	});

	it('Clears the mark and the section save when an added row is deleted again', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForCrossReferencesToLoad();

		await user.click(screen.getByRole('button', { name: 'Add Cross Reference' }));
		expect(within(crossReferencesSection()).getByText('Pending Edits!')).toBeInTheDocument();

		const rows = crossReferencesSection().parentElement.querySelectorAll('tbody tr');
		await user.click(rows[rows.length - 1].querySelector('.pi-trash').closest('button'));

		expect(within(crossReferencesSection()).queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save Cross References/ })).toBeDisabled();
		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
	});

	// Also edits the taxon, so it outlasts the default timeout under load.
	it(
		'Clears the cross references mark on their own save, leaving other pending edits marked',
		{ timeout: 30000 },
		async () => {
			const user = userEvent.setup();
			const { container } = await renderPage();

			await editCrossReference(user);
			const taxon = container.querySelector('input[name="taxon-input"]');
			await user.clear(taxon);
			await user.type(taxon, 'NCBITaxon:6239');
			await user.click(screen.getByRole('button', { name: /Save Cross References/ }));

			expect(await screen.findByText('Cross References Saved')).toBeInTheDocument();
			expect(within(crossReferencesSection()).queryByText('Pending Edits!')).not.toBeInTheDocument();
			expect(screen.getByRole('button', { name: /Save Cross References/ })).toBeDisabled();
			const taxonRow = screen.getByRole('heading', { name: 'Taxon' }).closest('.grid');
			expect(within(taxonRow).getByText('Pending Edits!')).toBeInTheDocument();
		}
	);

	// Cross references are written through their own sub-resource, so the page's Save makes that call
	// after the allele's, without sending the rows to the validate endpoint first.
	it('Saves edited cross references along with the allele', async () => {
		const user = userEvent.setup();
		await renderPage();

		await editCrossReferenceAndSave(user);

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalled());
		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(getCrossReferencesForAllele).toHaveBeenCalledWith(4242);
		expect(replaceCrossReferencesForAllele.mock.calls[0][0]).toBe(4242);
		expect(validate).not.toHaveBeenCalled();
		expect(saveAlleleDetail.mock.invocationCallOrder[0]).toBeLessThan(
			replaceCrossReferencesForAllele.mock.invocationCallOrder[0]
		);
	});

	// Every allele save would otherwise rewrite rows the curator never opened, audit trail included. A
	// stored row is loaded so its editors mount, and would mark it edited if any of them reported a change.
	it('Leaves untouched cross references alone when the allele is saved', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForCrossReferencesToLoad();
		// The row's editors are mounted, so any of them reporting a change on mount would count as an edit.
		expect(screen.getByDisplayValue('PMID:1')).toBeInTheDocument();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled());
		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(validate).not.toHaveBeenCalled();
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
	});

	// Saving the rows from their own section leaves them with nothing pending, so the page's Save that
	// follows writes the allele alone rather than the same rows again.
	it('Does not rewrite rows already saved from their own section', async () => {
		const user = userEvent.setup();
		await renderPage();

		await editCrossReference(user);
		await user.click(screen.getByRole('button', { name: /Save Cross References/ }));
		expect(await screen.findByText('Cross References Saved')).toBeInTheDocument();
		expect(replaceCrossReferencesForAllele).toHaveBeenCalledTimes(1);

		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(saveAlleleDetail).toHaveBeenCalledTimes(1);
		expect(replaceCrossReferencesForAllele).toHaveBeenCalledTimes(1);
	});

	// A refused section save leaves the rows pending, so the page's Save tries them again.
	it('Retries rows a failed section save left unsaved', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValueOnce({
			response: { data: { errorMessage: 'Could not update CrossReferences' } },
		});
		await renderPage();

		await editCrossReference(user);
		await user.click(screen.getByRole('button', { name: /Save Cross References/ }));
		expect(await screen.findByText('Could not update CrossReferences')).toBeInTheDocument();
		expect(within(crossReferencesSection()).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save Cross References/ })).toBeEnabled();

		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(replaceCrossReferencesForAllele).toHaveBeenCalledTimes(2);
	});

	it('Says the cross references failed without claiming the whole save did', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValue({
			response: { data: { errorMessage: 'Could not update CrossReferences' } },
		});
		await renderPage();

		await editCrossReferenceAndSave(user);

		expect(await screen.findByText(/Could not update CrossReferences/)).toBeInTheDocument();
		expect(screen.getByText(/The allele's other changes were saved/)).toBeInTheDocument();
		expect(saveAlleleDetail).toHaveBeenCalled();
		expect(screen.queryByText('Allele Saved')).not.toBeInTheDocument();
	});

	const alleleRejection = {
		response: {
			status: 400,
			statusText: 'Bad Request',
			data: { errorMessage: 'Could not update Allele', errorMessages: { taxon: 'Taxon is not valid' } },
		},
	};

	// The cross references are written whether or not the allele saved, so the errors of both show together.
	it('Reports the allele and cross reference errors together when both are rejected', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValue(alleleRejection);
		replaceCrossReferencesForAllele.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update CrossReferences',
					supplementalData: { errorMap: { crossReferences: { 0: { displayName: 'Required field is empty' } } } },
				},
			},
		});
		await renderPage();

		await waitForCrossReferencesToLoad();
		await user.clear(crossReferenceRow().querySelector('#displayName'));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Cross references not saved:')).toBeInTheDocument();
		expect(screen.getByText('Allele not saved:')).toBeInTheDocument();
		expect(screen.getByText('Could not update Allele')).toBeInTheDocument();
		expect(screen.getByText('Taxon is not valid')).toBeInTheDocument();
		expect(within(crossReferenceRow()).getByText('Required field is empty')).toBeInTheDocument();
		expect(replaceCrossReferencesForAllele).toHaveBeenCalledTimes(1);
		expect(screen.queryByText('Allele Saved')).not.toBeInTheDocument();
	});

	it('Says the cross references were saved when only the allele is rejected', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValue(alleleRejection);
		await renderPage();

		await editCrossReferenceAndSave(user);

		expect(await screen.findByText(/The cross references were saved/)).toBeInTheDocument();
		expect(screen.getByText('Allele not saved:')).toBeInTheDocument();
		expect(screen.getByText('Taxon is not valid')).toBeInTheDocument();
		expect(replaceCrossReferencesForAllele).toHaveBeenCalledTimes(1);
	});

	it('Sends no cross references for a rejected allele when the section was not edited', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValue(alleleRejection);
		await renderPage();

		await waitForCrossReferencesToLoad();
		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Could not update Allele')).toBeInTheDocument();
		expect(screen.getByText('Page error:')).toBeInTheDocument();
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
	});

	it('Sends a cleared taxon as no taxon, so the API reports it missing', async () => {
		const user = userEvent.setup();
		getAllele.mockResolvedValue({ data: { entity: { ...storedAllele, taxon: { curie: '' } } } });
		await renderPage();

		await waitForCrossReferencesToLoad();
		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(saveAlleleDetail.mock.calls[0][0].taxon).toBeUndefined();
	});

	it('Sends a chosen taxon as it is', async () => {
		const user = userEvent.setup();
		const taxon = { curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' };
		getAllele.mockResolvedValue({ data: { entity: { ...storedAllele, taxon } } });
		await renderPage();

		await waitForCrossReferencesToLoad();
		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(saveAlleleDetail.mock.calls[0][0].taxon).toEqual(taxon);
	});

	// Incomplete rows are sent as they are, and the API's per-row errors mark the cells it rejected.
	it('Sends a row missing a required field and marks the field the API rejects', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update CrossReferences',
					supplementalData: { errorMap: { crossReferences: { 0: { displayName: 'Required field is empty' } } } },
				},
			},
		});
		await renderPage();

		await waitForCrossReferencesToLoad();
		await user.clear(crossReferenceRow().querySelector('#displayName'));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await within(crossReferenceRow()).findByText('Required field is empty')).toBeInTheDocument();
		expect(saveAlleleDetail).toHaveBeenCalled();
		expect(replaceCrossReferencesForAllele.mock.calls[0][1][0].displayName).toBe('');
	});
});
