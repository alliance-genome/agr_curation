import React from 'react';
import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { alleleRejection, FORM_LOAD_WAIT, markAlleleExtinct, renderInRouter, storedAllele } from './alleleTestUtils';

const { getAllele, saveAlleleDetail, getCrossReferencesForAllele, getReferencesForAllele, replaceReferencesForAllele } =
	vi.hoisted(() => ({
		getAllele: vi.fn(),
		saveAlleleDetail: vi.fn(),
		getCrossReferencesForAllele: vi.fn(),
		getReferencesForAllele: vi.fn(),
		replaceReferencesForAllele: vi.fn(),
	}));

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ getAllele, saveAlleleDetail })
);
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService({
		crossreferences: { get: getCrossReferencesForAllele },
		references: { get: getReferencesForAllele, replace: replaceReferencesForAllele },
	})
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

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

const renderPage = () => renderInRouter(<AlleleDetailPage />);

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
		await waitFor(() => expect(getReferencesForAllele).toHaveBeenCalledWith(storedAllele.id), FORM_LOAD_WAIT);
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
		expect(replaceReferencesForAllele).toHaveBeenCalledWith(storedAllele.id, [
			{ type: 'Reference', curie: 'AGRKB:102' },
		]);
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

	const referencesSection = () => screen.getByRole('heading', { name: 'References' }).closest('.grid');
	const sectionOf = (name) => screen.getByRole('heading', { name }).closest('.grid');

	it('Keeps Save References inactive and marks nothing until the references are edited', async () => {
		await renderPage();
		await waitForReferencesToLoad();

		expect(screen.getByRole('button', { name: /Save References/ })).toBeDisabled();
		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Marks edited references as pending and offers their save', async () => {
		const user = userEvent.setup();
		await renderPage();

		await deleteFirstReference(user);

		expect(within(referencesSection()).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save References/ })).toBeEnabled();
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
	});

	// Also edits other fields, so it outlasts the default timeout under load.
	it(
		'Clears the references mark on their own save, leaving other pending edits marked',
		{ timeout: 30000 },
		async () => {
			const user = userEvent.setup();
			await renderPage();

			await deleteFirstReference(user);
			await user.click(screen.getByRole('button', { name: 'Add Cross Reference' }));
			await markAlleleExtinct(user);
			await user.click(screen.getByRole('button', { name: /Save References/ }));

			expect(await screen.findByText('References Saved')).toBeInTheDocument();
			expect(replaceReferencesForAllele).toHaveBeenCalledWith(storedAllele.id, [
				{ type: 'Reference', curie: 'AGRKB:102' },
			]);
			expect(saveAlleleDetail).not.toHaveBeenCalled();
			expect(within(referencesSection()).queryByText('Pending Edits!')).not.toBeInTheDocument();
			expect(screen.getByRole('button', { name: /Save References/ })).toBeDisabled();
			expect(within(sectionOf('Cross References')).getByText('Pending Edits!')).toBeInTheDocument();
			expect(within(sectionOf('Is Extinct')).getByText('Pending Edits!')).toBeInTheDocument();
			expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();
		}
	);

	it('Keeps the mark and shows the rejected row when Save References is refused', async () => {
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
		await user.click(screen.getByRole('button', { name: /Save References/ }));

		expect(await screen.findByText('Could not update References')).toBeInTheDocument();
		expect(within(referencesSection()).getByText('Pending Edits!')).toBeInTheDocument();
		expect(within(referenceRow('AGRKB:102')).getByText('Not a valid entry')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save References/ })).toBeEnabled();
	});

	it('Says the references cannot be saved when they failed to load', async () => {
		getReferencesForAllele.mockRejectedValue(new Error('unavailable'));
		await renderPage();

		expect(
			await screen.findByText(
				'Could not load these references, so they cannot be saved. Reload the page to try again.',
				{},
				FORM_LOAD_WAIT
			)
		).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Save References/ })).toBeDisabled();
	});
});
