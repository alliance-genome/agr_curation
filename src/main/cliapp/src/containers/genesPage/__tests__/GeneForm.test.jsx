import React, { useEffect } from 'react';
import { screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';

// Autosuggests search as they open, and the cross reference columns read descriptors through a search.
vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const { GeneForm, GENE_CREATE_TOGGLEABLE_FIELDS, GENE_DETAIL_TOGGLEABLE_FIELDS } = await import('../GeneForm');
const { useGeneReducer } = await import('../useGeneReducer');
const { useGeneSubResources } = await import('../useGeneSubResources');
const { SubResourcesProvider } = await import('../../../components/SubResourcesContext');
const { geneDetailData } = await import('../mockData/mockData');

const alwaysVisible = () => true;

// The form reads reducer state and the list sections read their sub-resources, so both are driven for real. With no
// gene id the lists are not read.
const GeneFormHarness = ({ isVisible = alwaysVisible, pendingFields }) => {
	const { geneState, geneDispatch } = useGeneReducer();
	const { subResources } = useGeneSubResources();
	useEffect(() => geneDispatch({ type: 'SET', value: geneDetailData.entity }), [geneDispatch]);
	return (
		<SubResourcesProvider value={subResources}>
			<GeneForm state={geneState} dispatch={geneDispatch} isVisible={isVisible} pendingFields={pendingFields} />
		</SubResourcesProvider>
	);
};

// The create form starts from the reducer's blank gene rather than a loaded one.
const GeneCreateFormHarness = () => {
	const { geneState, geneDispatch } = useGeneReducer('create');
	const { subResources } = useGeneSubResources();
	return (
		<SubResourcesProvider value={subResources}>
			<GeneForm state={geneState} dispatch={geneDispatch} isVisible={alwaysVisible} mode="create" />
		</SubResourcesProvider>
	);
};

const heading = (name) => screen.queryByRole('heading', { name });

describe('<GeneForm />', () => {
	it('renders every section, in the order the field menu lists them', async () => {
		renderWithClient(<GeneFormHarness />);

		await waitFor(() => expect(heading('Curie')).toBeInTheDocument());
		const sectionHeadings = screen
			.getAllByRole('heading')
			// The required marker is hidden from assistive technology, so it is left out of the name.
			.map((element) => element.textContent.replace('*', '').trim())
			.filter((text) => GENE_DETAIL_TOGGLEABLE_FIELDS.includes(text));
		expect(sectionHeadings).toEqual(GENE_DETAIL_TOGGLEABLE_FIELDS);
	});

	it("shows the gene's values", async () => {
		const { container } = renderWithClient(<GeneFormHarness />);

		await waitFor(() => expect(container.querySelector('input[name="geneType-input"]')).toHaveValue('SO:0001217'));
		expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('NCBITaxon:7227');
		expect(screen.getAllByText('AGRKB:105000000356681').length).toBeGreaterThan(0);
		expect(screen.getAllByText('FB:FBgn0019932').length).toBeGreaterThan(0);
		// A name shows its display text and its format text.
		expect(screen.getAllByDisplayValue('SamDC')).toHaveLength(2);
		expect(screen.getAllByDisplayValue('CG5029')).toHaveLength(2);
		expect(container.querySelector('[aria-label="referencedCurie"]')).toHaveValue('UniProtKB:P91931');
	});

	it('leaves out a section the field menu hides', async () => {
		renderWithClient(<GeneFormHarness isVisible={(field) => field !== 'Gene Type'} />);

		await waitFor(() => expect(heading('Taxon')).toBeInTheDocument());
		expect(heading('Gene Type')).not.toBeInTheDocument();
		expect(heading('Systematic Name')).toBeInTheDocument();
	});

	it('marks the sections with pending edits', async () => {
		renderWithClient(<GeneFormHarness pendingFields={new Set(['geneType', 'gcrpCrossReference'])} />);

		await waitFor(() => expect(screen.getAllByText('Pending Edits!')).toHaveLength(2));
	});

	describe('create mode', () => {
		afterEach(() => localStorage.removeItem('cognito-token-storage'));

		it('renders every section but the ones the server assigns, in the order the field menu lists them', async () => {
			renderWithClient(<GeneCreateFormHarness />);

			await waitFor(() => expect(heading('Data Provider')).toBeInTheDocument());
			const sectionHeadings = screen
				.getAllByRole('heading')
				.map((element) => element.textContent.replace('*', '').trim())
				.filter((text) => GENE_DETAIL_TOGGLEABLE_FIELDS.includes(text));
			expect(sectionHeadings).toEqual([
				'Name',
				'Symbol',
				'Synonyms',
				'Secondary IDs',
				'Systematic Name',
				'Gene Type',
				'Taxon',
				'Related Notes',
				'Data Provider',
				'Cross References',
				'GCRP Cross Reference',
				'Internal',
				'Obsolete',
			]);
		});

		it("falls back to 'Alliance' when the curator has no MOD group", async () => {
			renderWithClient(<GeneCreateFormHarness />);

			await waitFor(() => expect(heading('Data Provider')).toBeInTheDocument());
			expect(screen.getAllByText('Alliance').length).toBeGreaterThan(0);
		});

		it('opens a blank symbol, marked required, with no button to add another', async () => {
			renderWithClient(<GeneCreateFormHarness />);

			await waitFor(() => expect(screen.getByRole('heading', { name: /Symbol/ })).toBeInTheDocument());
			expect(screen.getByRole('heading', { name: /Symbol/ }).textContent).toContain('*');
			expect(screen.queryByRole('button', { name: 'Add Symbol' })).not.toBeInTheDocument();
			expect(screen.getByRole('button', { name: 'Add Full Name' })).toBeInTheDocument();
		});

		it("shows the data provider the curator's affiliation will assign", async () => {
			localStorage.setItem(
				'cognito-token-storage',
				JSON.stringify({ accessToken: { payload: { 'cognito:groups': ['FBStaff'] } } })
			);

			renderWithClient(<GeneCreateFormHarness />);

			await waitFor(() => expect(heading('Data Provider')).toBeInTheDocument());
			expect(screen.getAllByText('FB').length).toBeGreaterThan(0);
		});

		it('does not let the menu hide the fields the API requires or offer the ones the server assigns', () => {
			expect(GENE_CREATE_TOGGLEABLE_FIELDS).toEqual([
				'Name',
				'Synonyms',
				'Secondary IDs',
				'Systematic Name',
				'Related Notes',
				'Data Provider',
				'Cross References',
				'GCRP Cross Reference',
				'Internal',
				'Obsolete',
			]);
		});
	});
});
