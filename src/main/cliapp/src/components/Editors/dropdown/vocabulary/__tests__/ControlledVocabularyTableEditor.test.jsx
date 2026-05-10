import { render, fireEvent } from '@testing-library/react';
import { ControlledVocabularyTableEditor } from '../ControlledVocabularyTableEditor';
import { makeEditorOptions, emptyErrorMessagesRef } from '../../../__tests__/editorTestUtils';
import '../../../../../tools/jest/setupTests';

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

	it('should not render clear button by default', () => {
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
		expect(clearButton).not.toBeInTheDocument();
	});

	it('should render clear button when showClear is true', () => {
		const editorOptions = makeEditorOptions({ relation: { id: 1, name: 'is_model_of' } });
		const result = render(
			<ControlledVocabularyTableEditor
				editorOptions={editorOptions}
				field="relation"
				options={mockOptions}
				showClear={true}
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const clearButton = result.container.querySelector('.p-dropdown-clear-icon');
		expect(clearButton).toBeInTheDocument();
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

	it('should match the current value by id when dataKey="id" is supplied', () => {
		// Separate object references with the same id — reproduces the SpeciesTable case
		// where rowData.dataProvider and options come from different queries.
		const currentOption = { id: 2, name: 'is_implicated_in' };
		const editorOptions = makeEditorOptions({ relation: { id: 2, name: 'is_implicated_in' } });
		const result = render(
			<ControlledVocabularyTableEditor
				editorOptions={editorOptions}
				field="relation"
				options={[mockOptions[0], currentOption, mockOptions[2]]}
				errorMessagesRef={emptyErrorMessagesRef}
				dataKey="id"
			/>
		);

		const label = result.container.querySelector('.p-dropdown-label');
		expect(label).toHaveTextContent('is_implicated_in');
	});

	it('should use placeholderField to derive placeholder text when no value is selected', () => {
		// placeholder is only visible when the dropdown has no value
		const editorOptions = makeEditorOptions({
			dataProvider: { abbreviation: 'ZFIN', name: 'Zebrafish Information Network' },
		});
		// Intentionally no matching option (no dataKey, different ref) so the dropdown shows placeholder
		const result = render(
			<ControlledVocabularyTableEditor
				editorOptions={editorOptions}
				field="dataProvider"
				options={[]}
				errorMessagesRef={emptyErrorMessagesRef}
				placeholderField="abbreviation"
			/>
		);

		const label = result.container.querySelector('.p-dropdown-label');
		expect(label).toHaveTextContent('ZFIN');
	});
});
