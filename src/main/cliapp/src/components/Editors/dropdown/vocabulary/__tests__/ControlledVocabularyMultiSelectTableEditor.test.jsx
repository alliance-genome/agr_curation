import { ControlledVocabularyMultiSelectTableEditor } from '../ControlledVocabularyMultiSelectTableEditor';
import { makeEditorOptions, renderInTable } from '../../../__tests__/editorTestUtils';
import { pickOption } from '../../../widgets/__tests__/widgetTestUtils';
import '../../../../../tools/jest/setupTests';

const TERMS = [
	{ id: 1, name: 'susceptibility' },
	{ id: 2, name: 'ameliorates' },
	{ id: 3, name: 'exacerbates' },
];

const chipLabels = (container) =>
	[...container.querySelectorAll('.p-multiselect-token-label')].map((node) => node.textContent);

const renderEditor = (rowData, { errorMessages } = {}) => {
	const editorOptions = makeEditorOptions(rowData);
	const result = renderInTable(
		<ControlledVocabularyMultiSelectTableEditor
			editorOptions={editorOptions}
			field="diseaseQualifiers"
			options={TERMS}
		/>,
		{ errorMessages }
	);
	return { ...result, editorOptions };
};

describe('ControlledVocabularyMultiSelectTableEditor', () => {
	it('should resolve the row value onto the control', () => {
		const result = renderEditor({ diseaseQualifiers: [TERMS[0], TERMS[1]] });

		expect(chipLabels(result.container)).toEqual(['susceptibility', 'ameliorates']);
	});

	it('should render the control when the field has no value', () => {
		const result = renderEditor({ diseaseQualifiers: null });

		expect(result.container.querySelector('.p-multiselect')).toBeInTheDocument();
		expect(chipLabels(result.container)).toEqual([]);
	});

	it('should call editorCallback with the selected terms', () => {
		const result = renderEditor({ diseaseQualifiers: [] });

		pickOption(result.container, 'ameliorates', { multi: true });

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith([TERMS[1]]);
	});

	it('should call editorCallback with an empty array when the last term is removed', () => {
		const result = renderEditor({ diseaseQualifiers: [TERMS[1]] });

		pickOption(result.container, 'ameliorates', { multi: true });

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith([]);
	});

	it('should display error messages when present', () => {
		const result = renderEditor(
			{ diseaseQualifiers: null },
			{ errorMessages: { 0: { diseaseQualifiers: { severity: 'error', message: 'Invalid qualifier' } } } }
		);

		expect(result.getByText('Invalid qualifier')).toBeInTheDocument();
	});

	it('should mark the control invalid when an error is present', () => {
		const result = renderEditor(
			{ diseaseQualifiers: null },
			{ errorMessages: { 0: { diseaseQualifiers: { severity: 'error', message: 'Invalid qualifier' } } } }
		);

		expect(result.container.querySelector('.p-multiselect')).toHaveClass('p-invalid');
	});
});
