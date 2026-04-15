import { render, fireEvent } from '@testing-library/react';
import { ControlledVocabularyTableEditor } from '../ControlledVocabularyTableEditor';
import { makeEditorOptions, emptyErrorMessagesRef } from '../../../../tools/jest/editorTestUtils';
import '../../../../tools/jest/setupTests';

const mockOptions = [
	{ id: 1, name: 'is_model_of' },
	{ id: 2, name: 'is_implicated_in' },
	{ id: 3, name: 'is_marker_for' },
];

describe('ControlledVocabularyTableEditor', () => {
	it('should render a dropdown with the current value as placeholder', () => {
		const editorOptions = makeEditorOptions({ relation: { id: 1, name: 'is_model_of' } });
		const result = render(
			<ControlledVocabularyTableEditor
				editorOptions={editorOptions}
				field="relation"
				options={mockOptions}
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const dropdown = result.container.querySelector('.p-dropdown');
		expect(dropdown).toBeInTheDocument();
	});

	it('should call editorCallback when an option is selected', () => {
		const editorOptions = makeEditorOptions({ relation: { id: 1, name: 'is_model_of' } });
		const result = render(
			<ControlledVocabularyTableEditor
				editorOptions={editorOptions}
				field="relation"
				options={mockOptions}
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const dropdown = result.container.querySelector('.p-dropdown');
		fireEvent.click(dropdown);

		const option = result.getByText('is_implicated_in');
		fireEvent.click(option);

		expect(editorOptions.editorCallback).toHaveBeenCalledWith(mockOptions[1]);
	});

	it('should render with showClear enabled by default', () => {
		const editorOptions = makeEditorOptions({ relation: { id: 1, name: 'is_model_of' } });
		const result = render(
			<ControlledVocabularyTableEditor
				editorOptions={editorOptions}
				field="relation"
				options={mockOptions}
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const clearButton = result.container.querySelector('.p-dropdown-clear-icon');
		expect(clearButton).toBeInTheDocument();
	});

	it('should not render clear button when showClear is false', () => {
		const editorOptions = makeEditorOptions({ relation: { id: 1, name: 'is_model_of' } });
		const result = render(
			<ControlledVocabularyTableEditor
				editorOptions={editorOptions}
				field="relation"
				options={mockOptions}
				showClear={false}
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const clearButton = result.container.querySelector('.p-dropdown-clear-icon');
		expect(clearButton).not.toBeInTheDocument();
	});

	it('should display error messages when present', () => {
		const editorOptions = makeEditorOptions({ relation: { id: 1, name: 'is_model_of' } });
		const errorRef = {
			current: { 0: { relation: { severity: 'error', message: 'Required field' } } },
		};

		const result = render(
			<ControlledVocabularyTableEditor
				editorOptions={editorOptions}
				field="relation"
				options={mockOptions}
				errorMessagesRef={errorRef}
			/>
		);

		expect(result.getByText('Required field')).toBeInTheDocument();
	});

	it('should handle null field value gracefully', () => {
		const editorOptions = makeEditorOptions({ relation: null });
		const result = render(
			<ControlledVocabularyTableEditor
				editorOptions={editorOptions}
				field="relation"
				options={mockOptions}
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const dropdown = result.container.querySelector('.p-dropdown');
		expect(dropdown).toBeInTheDocument();
	});
});
