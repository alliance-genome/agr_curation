import { fireEvent, render, screen } from '@testing-library/react';

const PICKED = { id: 7, curie: 'SO:0000159', name: 'deletion' };

const { search } = vi.hoisted(() => ({ search: vi.fn() }));

vi.mock('../../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

const { TaxonDetailPageEditor } = await import('../taxon/TaxonDetailPageEditor');
const { InCollectionDetailPageEditor } = await import('../inCollection/InCollectionDetailPageEditor');
const { VariantTypeDetailPageEditor } = await import('../variantType/VariantTypeDetailPageEditor');
const { SourceGeneralConsequenceDetailPageEditor } = await import(
	'../sourceGeneralConsequence/SourceGeneralConsequenceDetailPageEditor'
);

// [heading, editor, the prop holding its value, the key errors are read under, the key typed text is held under,
// the endpoint searched, the search's filter name]
const EDITORS = [
	['Taxon', TaxonDetailPageEditor, 'taxon', 'taxon', 'curie', 'ncbitaxonterm', 'taxonFilter'],
	[
		'In Collection',
		InCollectionDetailPageEditor,
		'inCollection',
		'inCollection',
		'name',
		'vocabularyterm',
		'inCollectionFilter',
	],
	['Variant Type', VariantTypeDetailPageEditor, 'variantType', 'variantType', 'curie', 'soterm', 'variantTypeFilter'],
	[
		'Source General Consequence',
		SourceGeneralConsequenceDetailPageEditor,
		'sourceGeneralConsequence',
		'sourceGeneralConsequence',
		'curie',
		'soterm',
		'sourceGeneralConsequenceFilter',
	],
];

const renderEditor = (Editor, valueProp, props = {}) => {
	const onChange = vi.fn();
	const result = render(<Editor {...{ [valueProp]: null }} onChange={onChange} {...props} />);
	return { ...result, onChange };
};

describe.each(EDITORS)(
	'the %s detail page editor',
	(heading, Editor, valueProp, fieldKey, typedKey, endpoint, filterName) => {
		beforeEach(() => {
			search.mockReset();
			search.mockResolvedValue({ results: [PICKED], totalResults: 1 });
		});

		it('labels the field', () => {
			renderEditor(Editor, valueProp);

			expect(screen.getByRole('heading', { name: heading })).toBeInTheDocument();
		});

		it('reports a picked suggestion', async () => {
			const { onChange } = renderEditor(Editor, valueProp);

			fireEvent.change(screen.getByRole('combobox', { name: fieldKey }), { target: { value: 'del' } });
			fireEvent.click(
				await screen.findByText(
					/deletion/,
					{ selector: '.p-autocomplete-item, .p-autocomplete-item *' },
					{ timeout: 3000 }
				)
			);

			expect(onChange).toHaveBeenLastCalledWith(PICKED);
			const [searchedEndpoint, , , , filters] = search.mock.calls[0];
			expect(searchedEndpoint).toBe(endpoint);
			expect(filters).toHaveProperty(filterName);
		});

		it('reports typed text under its key', () => {
			const { onChange } = renderEditor(Editor, valueProp);

			fireEvent.change(screen.getByRole('combobox', { name: fieldKey }), { target: { value: 'typed' } });

			expect(onChange).toHaveBeenLastCalledWith({ [typedKey]: 'typed' });
		});

		it('reports null once the input is emptied', () => {
			const { onChange } = renderEditor(Editor, valueProp, { [valueProp]: { [typedKey]: 'stored' } });

			fireEvent.change(screen.getByRole('combobox', { name: fieldKey }), { target: { value: '' } });

			expect(onChange).toHaveBeenLastCalledWith(null);
		});

		it('shows an error given as an object', () => {
			renderEditor(Editor, valueProp, { errorMessages: { [fieldKey]: { severity: 'error', message: 'is bad' } } });

			expect(screen.getByText('is bad')).toBeInTheDocument();
			expect(screen.getByRole('combobox', { name: fieldKey }).closest('.p-autocomplete')).toHaveClass('p-invalid');
		});
	}
);
