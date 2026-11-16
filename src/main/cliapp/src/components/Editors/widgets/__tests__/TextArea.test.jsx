import { render } from '@testing-library/react';
import { TextArea } from '../TextArea';
import { typeInto } from './widgetTestUtils';

describe('<TextArea />', () => {
	it('renders the current value in a textarea', () => {
		const { container } = render(<TextArea value="a note" onChange={vi.fn()} name="freeText" />);

		expect(container.querySelector('textarea')).toHaveValue('a note');
	});

	it('emits the raw string', () => {
		const onChange = vi.fn();
		const { container } = render(<TextArea value="" onChange={onChange} name="freeText" />);

		typeInto(container, 'typed note');

		expect(onChange).toHaveBeenCalledWith('typed note');
	});

	// Callers size this either explicitly or with rows plus autoResize.
	it('honours explicit rows and cols', () => {
		const { container } = render(<TextArea value="" onChange={vi.fn()} name="freeText" rows={1} cols={30} />);

		const textarea = container.querySelector('textarea');
		expect(textarea).toHaveAttribute('rows', '1');
		expect(textarea).toHaveAttribute('cols', '30');
	});

	it('picks up an externally replaced value', () => {
		const { container, rerender } = render(<TextArea value="first" onChange={vi.fn()} name="freeText" />);

		rerender(<TextArea value="second" onChange={vi.fn()} name="freeText" />);

		expect(container.querySelector('textarea')).toHaveValue('second');
	});

	it('takes its accessible name from name, without a DOM name', () => {
		const { getByLabelText } = render(<TextArea value="x" onChange={vi.fn()} name="freeText" />);

		expect(getByLabelText('freeText')).not.toHaveAttribute('name');
	});

	it('marks the control invalid', () => {
		const { container } = render(<TextArea value="x" onChange={vi.fn()} name="freeText" invalid />);

		expect(container.querySelector('textarea')).toHaveClass('p-invalid');
	});

	it('sets a DOM id so a label can be associated with it', () => {
		const { container } = render(<TextArea id="displayText" value="x" onChange={vi.fn()} />);

		expect(container.querySelector('textarea')).toHaveAttribute('id', 'displayText');
	});
});
