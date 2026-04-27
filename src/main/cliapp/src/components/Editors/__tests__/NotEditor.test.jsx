import { render, fireEvent } from '@testing-library/react';
import { NotEditor } from '../NotEditor';
import '../../../tools/jest/setupTests';

describe('NotEditor', () => {
	it('should display "NOT" as the placeholder text when the initial value is true', () => {
		const editorChange = vi.fn();
		const result = render(<NotEditor value={true} editorChange={editorChange} />);

		expect(result.getAllByText('NOT')).toHaveLength(2);
	});

	it('should render a Dropdown component when value prop is undefined', () => {
		const editorChange = vi.fn();
		const result = render(<NotEditor value={undefined} editorChange={editorChange} />);

		const dropdown = result.container.querySelector('.p-dropdown');
		expect(dropdown).toBeInTheDocument();
	});

	it('should display NOT as the dropdown value after selecting it', () => {
		const editorChange = vi.fn();
		const result = render(<NotEditor value={false} editorChange={editorChange} />);

		const dropdown = result.container.querySelector('.p-dropdown');
		fireEvent.click(dropdown);

		const option = result.getAllByText('NOT');
		fireEvent.click(option[0]);

		const selectedLabel = result.container.querySelector('.p-dropdown-label');
		expect(selectedLabel.textContent).toBe('NOT');
	});

	it('should call editorChange with the new value when an option is selected', () => {
		const editorChange = vi.fn();
		const result = render(<NotEditor value={false} editorChange={editorChange} />);
		const span = result.container.getElementsByTagName('span')[0];

		fireEvent.click(span);

		const option = result.getAllByText('NOT');
		fireEvent.click(option[0]);

		expect(editorChange).toHaveBeenCalledWith(true);
	});
});
