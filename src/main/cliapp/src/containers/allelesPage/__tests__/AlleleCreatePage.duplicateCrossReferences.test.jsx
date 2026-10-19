import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { setLocalStorage } from '../../../tools/jest/setupTests';
import { DUPLICATE_SOURCE_ENTRY, FORM_LOAD_WAIT, renderInRouter, sourceAllele } from './alleleTestUtils';

const createAllele = vi.fn();
const getAllele = vi.fn();
const getCrossReferencesForAllele = vi.fn();
const replaceCrossReferencesForAllele = vi.fn();
const navigate = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ createAllele, getAllele })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
vi.mock('../../../service/CrossReferenceService', async () =>
	(await import('./alleleServiceMocks')).mockCrossReferenceService({
		get: getCrossReferencesForAllele,
		replace: replaceCrossReferencesForAllele,
	})
);
// The source's other sub-resources are read through their own endpoints; this allele has none.
vi.mock('../../../service/ReferenceService', async () => (await import('./alleleServiceMocks')).mockReferenceService());
vi.mock('../../../service/AlleleGeneAssociationService', async () =>
	(await import('./alleleServiceMocks')).mockGeneAssociationService()
);

const RESOURCE_DESCRIPTOR = { id: 30, prefix: 'WB', name: 'WormBase', resourcePages: [{ id: 40, name: 'allele' }] };

vi.mock('../../../service/ResourceDescriptorService', () => ({
	ResourceDescriptorService: class {
		getResourceDescriptor = vi.fn(() => Promise.resolve({ data: { entity: RESOURCE_DESCRIPTOR } }));
	},
}));

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleCreatePage = (await import('../AlleleCreatePage')).default;

const storedCrossReference = {
	id: 50,
	referencedCurie: 'WB:WBVar1',
	displayName: 'WB:WBVar1',
	internal: false,
	obsolete: false,
	dateCreated: '2024-01-01T00:00:00Z',
	resourceDescriptorPage: { id: 40, name: 'allele', resourceDescriptor: { id: 30, prefix: 'WB', name: 'WormBase' } },
};

const renderPage = () => renderInRouter(<AlleleCreatePage />, DUPLICATE_SOURCE_ENTRY);

describe('<AlleleCreatePage /> duplicating cross references', { timeout: 30000 }, () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000002' } } });
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: sourceAllele } });
		getCrossReferencesForAllele.mockReset();
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });
		replaceCrossReferencesForAllele.mockReset();
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		navigate.mockReset();
		window.localStorage.removeItem('AlleleCreateFormSettings');
	});

	it('Copies each cross reference page, leaving the curie and display name for the new allele', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		await waitFor(() => expect(getCrossReferencesForAllele).toHaveBeenCalledWith(1), FORM_LOAD_WAIT);
		await waitFor(
			() => expect(screen.queryByText('Loading allele to duplicate...')).not.toBeInTheDocument(),
			FORM_LOAD_WAIT
		);

		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalled(), FORM_LOAD_WAIT);
		const [alleleId, savedRows] = replaceCrossReferencesForAllele.mock.calls[0];
		expect(alleleId).toEqual(4242);
		expect(savedRows).toHaveLength(1);
		expect(savedRows[0].resourceDescriptorPage).toEqual(storedCrossReference.resourceDescriptorPage);
		expect(savedRows[0].referencedCurie).toEqual('');
		expect(savedRows[0].displayName).toEqual('');
		expect(savedRows[0]).not.toHaveProperty('id');
		expect(savedRows[0]).not.toHaveProperty('dateCreated');
	});

	it('Copies no cross references into a hidden section', async () => {
		setLocalStorage('AlleleCreateFormSettings', {
			selectedFormFields: [],
			orderedFormFields: ['Cross References'],
			formSettingsKeyName: 'AlleleCreateFormSettings',
		});
		const user = userEvent.setup();
		const { container } = await renderPage();

		await waitFor(() => expect(container.querySelector('#displayText')).toHaveValue('abc-1'), FORM_LOAD_WAIT);
		await user.click(screen.getByRole('button', { name: 'Save & Close' }));

		await waitFor(() => expect(createAllele).toHaveBeenCalled(), FORM_LOAD_WAIT);
		expect(getCrossReferencesForAllele).not.toHaveBeenCalled();
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
	});

	it('Still starts from the copy when the cross references cannot be loaded', async () => {
		getCrossReferencesForAllele.mockRejectedValue(new Error('unavailable'));
		const { container } = await renderPage();

		await waitFor(
			() => expect(screen.getByText('Could not load the cross references of allele WB:WBVar1')).toBeInTheDocument(),
			FORM_LOAD_WAIT
		);
		expect(container.querySelector('#displayText')).toHaveValue('abc-1');
	});
});
