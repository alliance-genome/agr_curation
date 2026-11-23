import { fireEvent, render, screen } from '@testing-library/react';

const REFERENCE = { curie: 'AGRKB:101', short_citation: 'Doe 2020', cross_references: [{ curie: 'PMID:101' }] };
const { search } = vi.hoisted(() => ({ search: vi.fn() }));

vi.mock('../../../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

const { SingleReferenceDetailPageEditor } = await import('../SingleReferenceDetailPageEditor');

const renderEditor = (props = {}) => {
	const onChange = vi.fn();
	render(<SingleReferenceDetailPageEditor reference={null} onChange={onChange} {...props} />);
	return { onChange, input: screen.getByRole('combobox', { name: 'singleReference' }) };
};

describe('<SingleReferenceDetailPageEditor />', () => {
	beforeEach(() => {
		search.mockReset();
		search.mockResolvedValue({ results: [REFERENCE], totalResults: 1 });
	});

	it('searches literature references and reports the one picked', async () => {
		const { onChange, input } = renderEditor();

		fireEvent.change(input, { target: { value: 'Doe' } });
		fireEvent.click(await screen.findByText(/AGRKB:101/, {}, { timeout: 3000 }));

		expect(onChange).toHaveBeenLastCalledWith(REFERENCE);
		const [endpoint, , , , filters] = search.mock.calls[0];
		expect(endpoint).toBe('literature-reference/document');
		expect(filters).toHaveProperty('singleReferenceFilter');
	});

	it('reports typed text keyed by curie', () => {
		const { onChange, input } = renderEditor();

		fireEvent.change(input, { target: { value: 'AGRKB:999' } });

		expect(onChange).toHaveBeenLastCalledWith({ curie: 'AGRKB:999' });
	});

	it('keeps its fixed input width', () => {
		const { input } = renderEditor();

		expect(input).toHaveClass('w-20rem');
	});

	it.each([
		['text', 'Must select reference from dropdown'],
		['an object', { severity: 'error', message: 'Must select reference from dropdown' }],
	])('shows an error given as %s beneath it', (kind, error) => {
		const { input } = renderEditor({ errorMessages: { references: error } });

		expect(screen.getByText('Must select reference from dropdown')).toBeInTheDocument();
		expect(input.closest('.p-autocomplete')).toHaveClass('p-invalid');
	});
});
