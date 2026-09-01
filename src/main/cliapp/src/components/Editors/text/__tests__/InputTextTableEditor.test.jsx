import { fireEvent } from '@testing-library/react';
import { InputTextTableEditor } from '../InputTextTableEditor';
import { makeEditorOptions, renderInTable } from '../../__tests__/editorTestUtils';
import '../../../../tools/jest/setupTests';

describe('InputTextTableEditor', () => {
	it('should render an input with the current field value', () => {
		const editorOptions = makeEditorOptions({ name: 'initial value' });
		const result = renderInTable(<InputTextTableEditor editorOptions={editorOptions} field="name" />);

		const input = result.container.querySelector('input');
		expect(input.value).toBe('initial value');
	});

	it('should call editorCallback with typed value on change', () => {
		const editorOptions = makeEditorOptions({ name: '' });
		const result = renderInTable(<InputTextTableEditor editorOptions={editorOptions} field="name" />);

		const input = result.container.querySelector('input');
		fireEvent.change(input, { target: { value: 'updated' } });

		expect(editorOptions.editorCallback).toHaveBeenCalledWith('updated');
	});

	it('should default to empty string when field is null or undefined', () => {
		const editorOptions = makeEditorOptions({ name: null });
		const result = renderInTable(<InputTextTableEditor editorOptions={editorOptions} field="name" />);

		const input = result.container.querySelector('input');
		expect(input.value).toBe('');
	});

	it('should display error messages when present', () => {
		const editorOptions = makeEditorOptions({ name: 'value' });
		const result = renderInTable(<InputTextTableEditor editorOptions={editorOptions} field="name" />, {
			errorMessages: { 0: { name: { severity: 'error', message: 'Name is required' } } },
		});

		expect(result.getByText('Name is required')).toBeInTheDocument();
	});

	// Main tables position errors absolutely so a message does not reflow the row.
	it('should render its error as an overlay', () => {
		const editorOptions = makeEditorOptions({ name: 'value' });
		const result = renderInTable(<InputTextTableEditor editorOptions={editorOptions} field="name" />, {
			errorMessages: { 0: { name: { severity: 'error', message: 'Name is required' } } },
		});

		expect(result.container.querySelector('.absolute')).toBeInTheDocument();
	});
});
