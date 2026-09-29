import { BooleanTableEditor } from '../BooleanTableEditor';
import { makeEditorOptions, renderInTable } from '../../../__tests__/editorTestUtils';
import { pickOption } from '../../../widgets/__tests__/widgetTestUtils';
import '../../../../../tools/jest/setupTests';

// BooleanSelect carries its own true/false options, so no vocabulary mock is needed.
const renderEditor = (rowData, { errorMessages, showClear } = {}) => {
	const editorOptions = makeEditorOptions(rowData);
	const result = renderInTable(
		<BooleanTableEditor editorOptions={editorOptions} field="internal" showClear={showClear} />,
		{ errorMessages }
	);
	return { ...result, editorOptions };
};

describe('BooleanTableEditor', () => {
	it('should render the current value', () => {
		const result = renderEditor({ internal: true });

		expect(result.container.querySelector('.p-dropdown-label')).toHaveTextContent('true');
	});

	// false is a value, not an absence: it has to reach the label like any other.
	it('should render a false value', () => {
		const result = renderEditor({ internal: false });

		expect(result.container.querySelector('.p-dropdown-label')).toHaveTextContent('false');
	});

	it('should render an empty label when the field has no value', () => {
		const result = renderEditor({ internal: null });

		expect(result.container.querySelector('.p-dropdown-label').textContent.trim()).toBe('');
	});

	it('should call editorCallback with a boolean when an option is selected', () => {
		const result = renderEditor({ internal: false });

		pickOption(result.container, 'true');

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith(true);
	});

	// The value is a real boolean, so a truthiness check anywhere in the write path
	// would swallow `false` and lose the edit silently.
	it('should call editorCallback with false rather than dropping it', () => {
		const result = renderEditor({ internal: true });

		pickOption(result.container, 'false');

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith(false);
	});

	it('should display error messages when present', () => {
		const result = renderEditor(
			{ internal: true },
			{ errorMessages: { 0: { internal: { severity: 'error', message: 'Invalid value' } } } }
		);

		expect(result.getByText('Invalid value')).toBeInTheDocument();
	});

	it('should mark the dropdown invalid when an error is present', () => {
		const result = renderEditor(
			{ internal: true },
			{ errorMessages: { 0: { internal: { severity: 'error', message: 'Invalid value' } } } }
		);

		expect(result.container.querySelector('.p-dropdown')).toHaveClass('p-invalid');
	});

	it('should not render clear button by default', () => {
		const result = renderEditor({ internal: true });

		expect(result.container.querySelector('.p-dropdown-clear-icon')).not.toBeInTheDocument();
	});

	it('should render clear button when showClear is true', () => {
		const result = renderEditor({ internal: true }, { showClear: true });

		expect(result.container.querySelector('.p-dropdown-clear-icon')).toBeInTheDocument();
	});
});
