import { render, fireEvent, screen, waitFor } from '@testing-library/react';
import { SingleAutocomplete } from '../SingleAutocomplete';
import { searchReturning, typeInto } from './widgetTestUtils';

const noSearch = vi.fn();

describe('<SingleAutocomplete />', () => {
	it('derives its display text from the model value via subField', () => {
		const { container } = render(
			<SingleAutocomplete value={{ curie: 'DOID:14330', name: 'Parkinson' }} onChange={vi.fn()} search={noSearch} />
		);

		expect(container.querySelector('input')).toHaveValue('DOID:14330');
	});

	it('honours a custom subField', () => {
		const { container } = render(
			<SingleAutocomplete
				value={{ primaryExternalId: 'MGI:123', name: 'a gene' }}
				onChange={vi.fn()}
				search={noSearch}
				subField="primaryExternalId"
			/>
		);

		expect(container.querySelector('input')).toHaveValue('MGI:123');
	});

	it('accepts a caller-supplied display derivation', () => {
		const { container } = render(
			<SingleAutocomplete
				value={{ modEntityId: 'WB:xyz' }}
				onChange={vi.fn()}
				search={noSearch}
				toDisplay={(value) => value?.modEntityId ?? ''}
			/>
		);

		expect(container.querySelector('input')).toHaveValue('WB:xyz');
	});

	// Free text becomes { [subField]: text }, a shape the server can resolve or reject
	// with "Not a valid entry".
	it('wraps free text in an object keyed by subField', () => {
		const onChange = vi.fn();
		const { container } = render(<SingleAutocomplete value={null} onChange={onChange} search={noSearch} />);

		typeInto(container, 'AGRKB:0000');

		expect(onChange).toHaveBeenCalledWith({ curie: 'AGRKB:0000' });
	});

	it('emits null when the input is emptied', () => {
		const onChange = vi.fn();
		const { container } = render(
			<SingleAutocomplete value={{ curie: 'DOID:1' }} onChange={onChange} search={noSearch} />
		);

		typeInto(container, '');

		expect(onChange).toHaveBeenCalledWith(null);
	});

	// PrimeReact debounces completeMethod by 300ms, so typing does not produce the
	// suggestion panel synchronously.
	it('emits the whole entity object when a suggestion is selected', async () => {
		const onChange = vi.fn();
		const suggestion = { id: 7, curie: 'DOID:14330', name: 'Parkinson' };
		const { container } = render(
			<SingleAutocomplete value={null} onChange={onChange} search={searchReturning(suggestion)} />
		);

		typeInto(container, 'Park');
		fireEvent.click(await screen.findByText(/Parkinson/, {}, { timeout: 3000 }));

		expect(onChange).toHaveBeenLastCalledWith(suggestion);
	});

	it('renders suggestions through valueDisplay, passing the typed query', async () => {
		const suggestion = { id: 7, curie: 'DOID:14330', name: 'Parkinson' };
		const valueDisplay = vi.fn(() => <span>custom suggestion</span>);
		const { container } = render(
			<SingleAutocomplete
				value={null}
				onChange={vi.fn()}
				search={searchReturning(suggestion)}
				valueDisplay={valueDisplay}
			/>
		);

		typeInto(container, 'Park');
		await screen.findByText('custom suggestion', {}, { timeout: 3000 });

		expect(valueDisplay).toHaveBeenCalledWith(
			suggestion,
			expect.any(Function),
			expect.objectContaining({ current: expect.anything() }),
			'Park'
		);
	});

	it('calls the caller-supplied search with the typed query', async () => {
		const search = searchReturning({ id: 1, curie: 'X:1', name: 'thing' });
		const { container } = render(<SingleAutocomplete value={null} onChange={vi.fn()} search={search} />);

		typeInto(container, 'thi');

		await waitFor(() => expect(search).toHaveBeenCalled(), { timeout: 3000 });
		expect(search.mock.calls[0][0]).toMatchObject({ query: 'thi' });
	});

	it('applies invalid styling', () => {
		const { container } = render(<SingleAutocomplete value={null} onChange={vi.fn()} search={noSearch} invalid />);

		expect(container.querySelector('.p-autocomplete')).toHaveClass('p-invalid');
	});

	// `id` reaches the input as PrimeReact's inputId, so a `<label htmlFor>` can point at it.
	it('sets a DOM id so a label can be associated with it', () => {
		const { container } = render(<SingleAutocomplete id="taxon" value={null} onChange={vi.fn()} search={noSearch} />);

		expect(container.querySelector('input#taxon')).toBeInTheDocument();
	});

	// `name` is the accessible name only. A DOM name attribute invites the browser to
	// autofill a saved profile over the cell.
	it('exposes name as the accessible name without emitting a name attribute', () => {
		const { container, getByLabelText } = render(
			<SingleAutocomplete value={null} onChange={vi.fn()} search={noSearch} name="taxon" />
		);

		expect(getByLabelText('taxon')).toBeInTheDocument();
		expect(container.querySelector('[name]')).toBeNull();
	});
});
