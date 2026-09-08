import { render } from '@testing-library/react';
import { StringListInput } from '../StringListInput';
import { typeInto } from './widgetTestUtils';

describe('<StringListInput />', () => {
	it('joins the array for display', () => {
		const { container } = render(<StringListInput value={['mouse', 'mice']} onChange={vi.fn()} name="commonNames" />);

		expect(container.querySelector('input')).toHaveValue('mouse, mice');
	});

	it('shows an empty string for a null value', () => {
		const { container } = render(<StringListInput value={null} onChange={vi.fn()} name="commonNames" />);

		expect(container.querySelector('input')).toHaveValue('');
	});

	it('emits an array, trimming entries and dropping empties', () => {
		const onChange = vi.fn();
		const { container } = render(<StringListInput value={[]} onChange={onChange} name="commonNames" />);

		typeInto(container, ' mouse ,, mice , ');

		expect(onChange).toHaveBeenCalledWith(['mouse', 'mice']);
	});

	// This is why the widget keeps its own display state rather than deriving the
	// text from `value` on every render: a trailing separator has no array
	// representation that renders back to itself, so round-tripping would delete
	// the comma the moment it was typed.
	it('keeps a trailing separator visible while emitting the array without it', () => {
		const onChange = vi.fn();
		const { container } = render(<StringListInput value={[]} onChange={onChange} name="commonNames" />);

		typeInto(container, 'mouse, ');

		expect(onChange).toHaveBeenCalledWith(['mouse']);
		expect(container.querySelector('input')).toHaveValue('mouse, ');
	});

	it('renders a textarea when multiline', () => {
		const { container } = render(
			<StringListInput value={['a']} onChange={vi.fn()} name="synonyms" multiline rows={5} />
		);

		expect(container.querySelector('textarea')).toBeInTheDocument();
		expect(container.querySelector('input')).toBeNull();
	});

	it('sets a DOM id so a label can be associated with it', () => {
		const { container } = render(<StringListInput id="displayText" value={['a']} onChange={vi.fn()} />);

		expect(container.querySelector('input')).toHaveAttribute('id', 'displayText');
	});
});
