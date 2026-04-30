import { render, fireEvent } from '@testing-library/react';
import { InputTextTableEditor } from '../InputTextTableEditor';
import { makeEditorOptions, emptyErrorMessagesRef } from '../../__tests__/editorTestUtils';
import '../../../../tools/jest/setupTests';

describe('InputTextTableEditor', () => {
	it('should render an input with the current field value', () => {
		const editorOptions = makeEditorOptions({ name: 'initial value' });
		const result = render(
			<InputTextTableEditor editorOptions={editorOptions} field="name" errorMessagesRef={emptyErrorMessagesRef} />
		);

		const input = result.container.querySelector('input');
		expect(input.value).toBe('initial value');
	});

	it('should call editorCallback with typed value on change', () => {
		const editorOptions = makeEditorOptions({ name: '' });
		const result = render(
			<InputTextTableEditor editorOptions={editorOptions} field="name" errorMessagesRef={emptyErrorMessagesRef} />
		);

		const input = result.container.querySelector('input');
		fireEvent.change(input, { target: { value: 'updated' } });

		expect(editorOptions.editorCallback).toHaveBeenCalledWith('updated');
	});

	it('should default to empty string when field is null or undefined', () => {
		const editorOptions = makeEditorOptions({ name: null });
		const result = render(
			<InputTextTableEditor editorOptions={editorOptions} field="name" errorMessagesRef={emptyErrorMessagesRef} />
		);

		const input = result.container.querySelector('input');
		expect(input.value).toBe('');
	});

	it('should display error messages when present', () => {
		const editorOptions = makeEditorOptions({ name: 'value' });
		const errorRef = {
			current: { 0: { name: { severity: 'error', message: 'Name is required' } } },
		};
		const result = render(
			<InputTextTableEditor editorOptions={editorOptions} field="name" errorMessagesRef={errorRef} />
		);

		expect(result.getByText('Name is required')).toBeInTheDocument();
	});
});
