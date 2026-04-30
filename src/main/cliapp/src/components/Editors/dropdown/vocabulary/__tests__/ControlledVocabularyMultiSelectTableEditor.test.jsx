import { render } from '@testing-library/react';
import { ControlledVocabularyMultiSelectTableEditor } from '../ControlledVocabularyMultiSelectTableEditor';
import { makeEditorOptions, emptyErrorMessagesRef } from '../../../__tests__/editorTestUtils';
import '../../../../../tools/jest/setupTests';

const mockOptions = [
	{ id: 1, name: 'susceptibility' },
	{ id: 2, name: 'ameliorates' },
	{ id: 3, name: 'exacerbates' },
];

describe('ControlledVocabularyMultiSelectTableEditor', () => {
	it('should render a multi-select dropdown', () => {
		const editorOptions = makeEditorOptions({ diseaseQualifiers: [{ id: 1, name: 'susceptibility' }] });
		const result = render(
			<ControlledVocabularyMultiSelectTableEditor
				editorOptions={editorOptions}
				field="diseaseQualifiers"
				options={mockOptions}
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const multiselect = result.container.querySelector('.p-multiselect');
		expect(multiselect).toBeInTheDocument();
	});

	it('should display error messages when present', () => {
		const editorOptions = makeEditorOptions({ diseaseQualifiers: null });
		const errorRef = {
			current: { 0: { diseaseQualifiers: { severity: 'error', message: 'Invalid qualifier' } } },
		};

		const result = render(
			<ControlledVocabularyMultiSelectTableEditor
				editorOptions={editorOptions}
				field="diseaseQualifiers"
				options={mockOptions}
				errorMessagesRef={errorRef}
			/>
		);

		expect(result.getByText('Invalid qualifier')).toBeInTheDocument();
	});

	it('should generate placeholder from current values', () => {
		const editorOptions = makeEditorOptions({
			diseaseQualifiers: [
				{ id: 1, name: 'susceptibility' },
				{ id: 2, name: 'ameliorates' },
			],
		});
		const result = render(
			<ControlledVocabularyMultiSelectTableEditor
				editorOptions={editorOptions}
				field="diseaseQualifiers"
				options={mockOptions}
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const multiselect = result.container.querySelector('.p-multiselect');
		expect(multiselect).toBeInTheDocument();
	});
});
