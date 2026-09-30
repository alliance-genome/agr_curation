import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { act, screen, waitFor } from '@testing-library/react';
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

vi.mock('../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

// The shared fixture carries no id, and the page reads and writes cross references by the allele's id.
const storedAllele = { ...alleleDetailData.entity, id: 4242 };

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
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
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

	const addCrossReferenceAndSave = async (user) => {
		await waitForCrossReferencesToLoad();
		await user.click(screen.getByRole('button', { name: 'Add Cross Reference' }));
		await user.click(screen.getByRole('button', { name: 'Save' }));
	};

	// Cross references are written through their own sub-resource, so the page's Save makes that call
	// after the allele's, without checking the rows first.
	it('Saves edited cross references along with the allele', async () => {
		const user = userEvent.setup();
		await renderPage();

		await addCrossReferenceAndSave(user);

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
		getCrossReferencesForAllele.mockResolvedValue({
			data: {
				entities: [
					{
						id: 500,
						displayName: 'PubMed 1',
						referencedCurie: 'PMID:1',
						resourceDescriptorPage: { id: 1, name: 'default', resourceDescriptor: { id: 9, prefix: 'PMID' } },
						internal: false,
						obsolete: false,
					},
				],
			},
		});
		await renderPage();
		await waitForCrossReferencesToLoad();
		// The row's editors are mounted, so any of them reporting a change on mount would count as an edit.
		expect(screen.getByDisplayValue('PMID:1')).toBeInTheDocument();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Save' })).toBeInTheDocument());
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(validate).not.toHaveBeenCalled();
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
	});

	it('Says the cross references failed without claiming the whole save did', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValue({
			response: { data: { errorMessage: 'Could not update CrossReferences' } },
		});
		await renderPage();

		await addCrossReferenceAndSave(user);

		expect(await screen.findByText(/Could not update CrossReferences/)).toBeInTheDocument();
		expect(screen.getByText(/The allele's other changes were saved/)).toBeInTheDocument();
		expect(saveAlleleDetail).toHaveBeenCalled();
		expect(screen.queryByText('Allele Saved')).not.toBeInTheDocument();
	});
});
