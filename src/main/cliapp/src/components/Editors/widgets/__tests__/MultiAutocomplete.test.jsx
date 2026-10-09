import { render, fireEvent, screen } from '@testing-library/react';
import { MultiAutocomplete } from '../MultiAutocomplete';
import { removeFirstToken, searchReturning, typeInto } from './widgetTestUtils';

const noSearch = vi.fn();

const tokenLabels = (container) =>
	[...container.querySelectorAll('.p-autocomplete-token-label')].map((node) => node.textContent);

describe('<MultiAutocomplete />', () => {
	it('renders a token per entity, labelled by subField', () => {
		const { container } = render(
			<MultiAutocomplete
				value={[
					{ id: 1, curie: 'AGRKB:1' },
					{ id: 2, curie: 'AGRKB:2' },
				]}
				onChange={vi.fn()}
				search={noSearch}
			/>
		);

		expect(tokenLabels(container)).toEqual(['AGRKB:1', 'AGRKB:2']);
	});

	it('appends an entity that differs by id', async () => {
		const onChange = vi.fn();
		const existing = { id: 1, curie: 'AGRKB:1' };
		const other = { id: 2, curie: 'AGRKB:2' };
		const { container } = render(
			<MultiAutocomplete value={[existing]} onChange={onChange} search={searchReturning(other)} />
		);

		typeInto(container, 'AGRKB');
		fireEvent.click(
			await screen.findByText(
				/AGRKB:2/,
				{ selector: '.p-autocomplete-item, .p-autocomplete-item *' },
				{ timeout: 3000 }
			)
		);

		expect(onChange).toHaveBeenLastCalledWith([existing, other]);
	});

	// A search result and a row's entity are serialised differently, so the same entity
	// can arrive with other fields that differ. Only the id identifies it.
	it('does not add a suggestion that shares an id with a selected entity', async () => {
		const onChange = vi.fn();
		const existing = { id: 1, curie: 'AGRKB:1', name: 'stored' };
		const { container } = render(
			<MultiAutocomplete
				value={[existing]}
				onChange={onChange}
				search={searchReturning({ ...existing, name: 'searched' })}
			/>
		);

		typeInto(container, 'AGRKB');
		fireEvent.click(
			await screen.findByText(
				/searched/,
				{ selector: '.p-autocomplete-item, .p-autocomplete-item *' },
				{ timeout: 3000 }
			)
		);

		expect(onChange).toHaveBeenLastCalledWith([existing]);
	});

	it('emits the reduced array when a token is removed', () => {
		const onChange = vi.fn();
		const { container } = render(
			<MultiAutocomplete
				value={[
					{ id: 1, curie: 'AGRKB:1' },
					{ id: 2, curie: 'AGRKB:2' },
				]}
				onChange={onChange}
				search={noSearch}
			/>
		);

		removeFirstToken(container);

		expect(onChange).toHaveBeenCalledWith([{ id: 2, curie: 'AGRKB:2' }]);
	});

	it('emits an empty array, never null, when the last token is removed', () => {
		const onChange = vi.fn();
		const { container } = render(
			<MultiAutocomplete value={[{ id: 1, curie: 'AGRKB:1' }]} onChange={onChange} search={noSearch} />
		);

		removeFirstToken(container);

		expect(onChange).toHaveBeenCalledWith([]);
	});

	it('picks up an externally replaced value', () => {
		const { container, rerender } = render(
			<MultiAutocomplete value={[{ id: 1, curie: 'AGRKB:1' }]} onChange={vi.fn()} search={noSearch} />
		);

		rerender(<MultiAutocomplete value={[{ id: 9, curie: 'AGRKB:9' }]} onChange={vi.fn()} search={noSearch} />);

		expect(tokenLabels(container)).toEqual(['AGRKB:9']);
	});

	it('applies invalid styling', () => {
		const { container } = render(<MultiAutocomplete value={[]} onChange={vi.fn()} search={noSearch} invalid />);

		expect(container.querySelector('.p-autocomplete')).toHaveClass('p-invalid');
	});

	// `id` reaches the input as PrimeReact's inputId, so a `<label htmlFor>` can point at it.
	it('sets a DOM id so a label can be associated with it', () => {
		const { container } = render(<MultiAutocomplete id="with" value={[]} onChange={vi.fn()} search={noSearch} />);

		expect(container.querySelector('input#with')).toBeInTheDocument();
	});

	// `name` is the accessible name only. A DOM name attribute invites the browser to
	// autofill a saved profile over the cell.
	it('exposes name as the accessible name without emitting a name attribute', () => {
		const { container, getByLabelText } = render(
			<MultiAutocomplete value={[]} onChange={vi.fn()} search={noSearch} name="with" />
		);

		expect(getByLabelText('with')).toBeInTheDocument();
		expect(container.querySelector('[name]')).toBeNull();
	});
});
