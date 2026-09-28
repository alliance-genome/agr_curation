import { render } from '@testing-library/react';
import { TextInput } from '../TextInput';
import { typeInto } from './widgetTestUtils';

describe('<TextInput />', () => {
	it('renders the current value', () => {
		const { container } = render(<TextInput value="brat[11]" onChange={vi.fn()} name="displayText" />);

		expect(container.querySelector('input')).toHaveValue('brat[11]');
	});

	it('emits the raw string, not a DOM event', () => {
		const onChange = vi.fn();
		const { container } = render(<TextInput value="" onChange={onChange} name="displayText" />);

		typeInto(container, 'edited');

		expect(onChange).toHaveBeenCalledWith('edited');
	});

	// useSyncedState, not plain useState: an optimistic row swap or a post-save
	// reconcile replaces the value from outside, and the input has to follow.
	it('picks up an externally replaced value', () => {
		const { container, rerender } = render(<TextInput value="first" onChange={vi.fn()} name="displayText" />);

		rerender(<TextInput value="replaced from outside" onChange={vi.fn()} name="displayText" />);

		expect(container.querySelector('input')).toHaveValue('replaced from outside');
	});

	it('applies invalid styling', () => {
		const { container } = render(<TextInput value="x" onChange={vi.fn()} name="displayText" invalid />);

		expect(container.querySelector('input')).toHaveClass('p-invalid');
	});

	it('sets a DOM id so a label can be associated with it', () => {
		const { container } = render(<TextInput id="displayText" value="x" onChange={vi.fn()} />);

		expect(container.querySelector('input')).toHaveAttribute('id', 'displayText');
	});
});
