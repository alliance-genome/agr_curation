import { fireEvent, render, screen } from '@testing-library/react';
import { BooleanDetailPageEditor } from '../BooleanDetailPageEditor';

const renderEditor = (props = {}) =>
	render(<BooleanDetailPageEditor value={false} name="obsolete" label="Obsolete" onChange={vi.fn()} {...props} />);

describe('<BooleanDetailPageEditor />', () => {
	it('reports the chosen value as a boolean', () => {
		const onChange = vi.fn();
		const { container } = renderEditor({ onChange });

		fireEvent.click(container.querySelector('.p-dropdown'));
		fireEvent.click(screen.getByText('true', { selector: '.p-dropdown-item, .p-dropdown-item *' }));

		expect(onChange).toHaveBeenCalledWith(true);
	});

	it('reports false when false is chosen', () => {
		const onChange = vi.fn();
		const { container } = renderEditor({ value: true, onChange });

		fireEvent.click(container.querySelector('.p-dropdown'));
		fireEvent.click(screen.getByText('false', { selector: '.p-dropdown-item, .p-dropdown-item *' }));

		expect(onChange).toHaveBeenCalledWith(false);
	});

	it('offers no clear option unless asked to', () => {
		const { container } = renderEditor();

		expect(container.querySelector('.p-dropdown-clear-icon')).toBeNull();
	});

	it('reports null when cleared', () => {
		const onChange = vi.fn();
		const { container } = renderEditor({ showClear: true, onChange });

		fireEvent.pointerUp(container.querySelector('.p-dropdown-clear-icon'));

		expect(onChange).toHaveBeenCalledWith(null);
	});

	it('shows the value beside the field', () => {
		renderEditor({ value: true });

		expect(screen.getByText('true', { selector: '.p-info' })).toBeInTheDocument();
	});

	it('shows an error given as an object', () => {
		renderEditor({ errorMessages: { obsolete: { severity: 'error', message: 'is bad' } } });

		expect(screen.getByText('is bad')).toBeInTheDocument();
	});
});
