import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';

const { search } = vi.hoisted(() => ({ search: vi.fn() }));

// Typing fires the autosuggest's debounced search, which builds its own SearchService.
vi.mock('../../../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

const { GeneTypeDetailPageEditor } = await import('../GeneTypeDetailPageEditor');

const proteinCodingGene = { curie: 'SO:0001217', name: 'protein_coding_gene' };
const ncRnaGene = { curie: 'SO:0001263', name: 'ncRNA_gene' };

const renderEditor = (props = {}) => {
	const onChange = vi.fn();
	const result = render(
		<GeneTypeDetailPageEditor
			geneType={proteinCodingGene}
			onChange={onChange}
			widgetColumnSize="col-4"
			labelColumnSize="col-3"
			fieldDetailsColumnSize="col-5"
			errorMessages={{}}
			{...props}
		/>
	);
	return { ...result, onChange, input: screen.getByRole('combobox', { name: 'geneType' }) };
};

describe('<GeneTypeDetailPageEditor />', () => {
	beforeEach(() => {
		search.mockReset();
		search.mockResolvedValue({ results: [ncRnaGene], totalResults: 1 });
	});

	it('shows the gene type it is given', () => {
		const { input } = renderEditor();

		expect(screen.getByText('Gene Type')).toBeInTheDocument();
		expect(input).toHaveValue('SO:0001217');
		expect(screen.getByText('protein_coding_gene (SO:0001217)')).toBeInTheDocument();
	});

	it('searches SO terms, leaving out obsolete ones, and reports the term picked', async () => {
		const { input, onChange } = renderEditor();

		fireEvent.change(input, { target: { value: 'ncRNA' } });
		fireEvent.click(
			await screen.findByText(
				/ncRNA_gene/,
				{ selector: '.p-autocomplete-item, .p-autocomplete-item *' },
				{ timeout: 3000 }
			)
		);

		const [endpoint, , , , filters] = search.mock.calls[0];
		expect(endpoint).toBe('soterm');
		expect(Object.keys(filters)).toEqual(['geneTypeFilter', 'obsoleteFilter']);
		expect(filters.obsoleteFilter).toEqual({ obsolete: { queryString: false } });
		await waitFor(() => expect(onChange).toHaveBeenLastCalledWith(ncRnaGene));
	});

	it('reports typed text keyed by curie', () => {
		const { input, onChange } = renderEditor();

		fireEvent.change(input, { target: { value: 'SO:1' } });

		expect(onChange).toHaveBeenLastCalledWith({ curie: 'SO:1' });
	});

	it('reports null once the input is emptied', () => {
		const { input, onChange } = renderEditor();

		fireEvent.change(input, { target: { value: '' } });

		expect(onChange).toHaveBeenLastCalledWith(null);
	});

	it('shows an error given as an object', () => {
		renderEditor({ errorMessages: { geneType: { severity: 'error', message: 'Invalid' } } });

		expect(screen.getByText('Invalid')).toBeInTheDocument();
	});

	it('shows an error reported for the gene type', () => {
		renderEditor({ errorMessages: { geneType: 'Invalid' } });

		expect(screen.getByText('Invalid')).toBeInTheDocument();
	});

	it('marks the field when it is required or has pending edits', () => {
		const { container } = renderEditor({ required: true, isPending: true });

		expect(screen.getByText('*')).toBeInTheDocument();
		expect(screen.getByText('Pending Edits!')).toBeInTheDocument();
		expect(container.querySelector('.border-yellow-500')).not.toBeNull();
	});
});
