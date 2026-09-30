import { render } from '@testing-library/react';
import { BooleanSelect } from '../BooleanSelect';
import { pickOption } from './widgetTestUtils';

describe('<BooleanSelect />', () => {
	it('renders the current value', () => {
		const { container } = render(<BooleanSelect value={true} onChange={vi.fn()} name="internal" />);

		expect(container.querySelector('.p-dropdown-label').textContent).toBe('true');
	});

	it('renders false rather than treating it as empty', () => {
		const { container } = render(<BooleanSelect value={false} onChange={vi.fn()} name="internal" />);

		expect(container.querySelector('.p-dropdown-label').textContent).toBe('false');
	});

	// The options are {name, text} objects so the dropdown can label them, but the
	// field stores a boolean, so the caller receives the option's name and never
	// has to unwrap anything.
	it('emits a real boolean, not the option object', () => {
		const onChange = vi.fn();
		const { container } = render(<BooleanSelect value={null} onChange={onChange} name="internal" />);

		pickOption(container, 'true');

		expect(onChange).toHaveBeenCalledWith(true);
	});

	it('emits boolean false, distinguishable from null', () => {
		const onChange = vi.fn();
		const { container } = render(<BooleanSelect value={true} onChange={onChange} name="internal" />);

		pickOption(container, 'false');

		expect(onChange).toHaveBeenCalledWith(false);
	});

	// PrimeReact renders the clear icon only while the option list is non-empty. The
	// options are held here rather than fetched, so it is there from the first render.
	it('renders a clear affordance when showClear is set', () => {
		const { container } = render(<BooleanSelect value={true} onChange={vi.fn()} name="internal" showClear />);

		expect(container.querySelector('.p-dropdown-clear-icon')).toBeInTheDocument();
	});

	it('offers no clear affordance unless showClear is set', () => {
		const { container } = render(<BooleanSelect value={true} onChange={vi.fn()} name="internal" />);

		expect(container.querySelector('.p-dropdown-clear-icon')).not.toBeInTheDocument();
	});

	it('sets a DOM id so a label can be associated with it', () => {
		const { container } = render(<BooleanSelect id="internal" value={true} onChange={vi.fn()} />);

		expect(container.querySelector('input#internal')).toBeInTheDocument();
	});

	it('applies invalid styling', () => {
		const { container } = render(<BooleanSelect value={true} onChange={vi.fn()} name="internal" invalid />);

		expect(container.querySelector('.p-dropdown')).toHaveClass('p-invalid');
	});
});
