import { ControlledVocabularyTableEditor } from '../ControlledVocabularyTableEditor';
import { makeEditorOptions, renderInTable } from '../../../__tests__/editorTestUtils';
import { pickOption } from '../../../widgets/__tests__/widgetTestUtils';
import '../../../../../tools/jest/setupTests';

const TERMS = [
	{ id: 1, name: 'is_model_of' },
	{ id: 2, name: 'is_implicated_in' },
	{ id: 3, name: 'is_marker_for' },
];

const renderEditor = (rowData, { options = TERMS, errorMessages, ...props } = {}) => {
	const editorOptions = makeEditorOptions(rowData);
	const result = renderInTable(
		<ControlledVocabularyTableEditor editorOptions={editorOptions} field="relation" options={options} {...props} />,
		{ errorMessages }
	);
	return { ...result, editorOptions };
};

describe('ControlledVocabularyTableEditor', () => {
	it('should render the current term', () => {
		const result = renderEditor({ relation: TERMS[0] });

		expect(result.container.querySelector('.p-dropdown-label')).toHaveTextContent('is_model_of');
	});

	it('should call editorCallback with the whole term when an option is selected', () => {
		const result = renderEditor({ relation: TERMS[0] });

		pickOption(result.container, 'is_implicated_in');

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith(TERMS[1]);
	});

	it('should render without a value', () => {
		const result = renderEditor({ relation: null });

		expect(result.container.querySelector('.p-dropdown-label').textContent.trim()).toBe('');
	});

	it('should display error messages when present', () => {
		const result = renderEditor(
			{ relation: TERMS[0] },
			{ errorMessages: { 0: { relation: { severity: 'error', message: 'Required field' } } } }
		);

		expect(result.getByText('Required field')).toBeInTheDocument();
	});

	it('should mark the dropdown invalid when an error is present', () => {
		const result = renderEditor(
			{ relation: TERMS[0] },
			{ errorMessages: { 0: { relation: { severity: 'error', message: 'Required field' } } } }
		);

		expect(result.container.querySelector('.p-dropdown')).toHaveClass('p-invalid');
	});

	it('should not render clear button by default', () => {
		const result = renderEditor({ relation: TERMS[0] });

		expect(result.container.querySelector('.p-dropdown-clear-icon')).not.toBeInTheDocument();
	});

	it('should render clear button when showClear is true', () => {
		const result = renderEditor({ relation: TERMS[0] }, { showClear: true });

		expect(result.container.querySelector('.p-dropdown-clear-icon')).toBeInTheDocument();
	});

	// The row's term and the option come from separate queries, so they are distinct
	// objects. Only dataKey matches them; the deep-equality fallback would fail on
	// the differing label.
	it('should match the current value by id when dataKey is supplied', () => {
		const result = renderEditor({ relation: { id: 2, name: 'STALE LABEL' } }, { dataKey: 'id' });

		expect(result.container.querySelector('.p-dropdown-label')).toHaveTextContent('is_implicated_in');
	});

	// A vocabulary that has not loaded leaves nothing to match against, so the row's
	// own value supplies the text instead of the cell appearing empty.
	it('should fall back to placeholderField when the value matches no option', () => {
		const result = renderEditor(
			{ relation: { abbreviation: 'ZFIN', name: 'Zebrafish Information Network' } },
			{ options: [], placeholderField: 'abbreviation' }
		);

		expect(result.container.querySelector('.p-dropdown-label')).toHaveTextContent('ZFIN');
	});
});
