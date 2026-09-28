import { fireEvent } from '@testing-library/react';
import { StringListTextAreaTableEditor } from '../StringListTextAreaTableEditor';
import { makeEditorOptions, renderInTable } from '../../__tests__/editorTestUtils';
import '../../../../tools/jest/setupTests';

describe('StringListTextAreaTableEditor', () => {
	it('should render a textarea joining the array with commas', () => {
		const editorOptions = makeEditorOptions({ synonyms: ['alpha', 'beta', 'gamma'] });
		const result = renderInTable(<StringListTextAreaTableEditor editorOptions={editorOptions} field="synonyms" />);

		const textarea = result.container.querySelector('textarea');
		expect(textarea.value).toBe('alpha, beta, gamma');
	});

	it('should call editorCallback with parsed array on change', () => {
		const editorOptions = makeEditorOptions({ synonyms: [] });
		const result = renderInTable(<StringListTextAreaTableEditor editorOptions={editorOptions} field="synonyms" />);

		const textarea = result.container.querySelector('textarea');
		fireEvent.change(textarea, { target: { value: 'foo, bar, baz' } });

		expect(editorOptions.editorCallback).toHaveBeenCalledWith(['foo', 'bar', 'baz']);
	});

	it('should respect the rows prop', () => {
		const editorOptions = makeEditorOptions({ synonyms: [] });
		const result = renderInTable(
			<StringListTextAreaTableEditor editorOptions={editorOptions} field="synonyms" rows={8} />
		);

		const textarea = result.container.querySelector('textarea');
		expect(textarea.getAttribute('rows')).toBe('8');
	});

	it('should display error messages when present', () => {
		const editorOptions = makeEditorOptions({ synonyms: ['x'] });
		const result = renderInTable(<StringListTextAreaTableEditor editorOptions={editorOptions} field="synonyms" />, {
			errorMessages: { 0: { synonyms: { severity: 'error', message: 'Invalid synonym' } } },
		});

		expect(result.getByText('Invalid synonym')).toBeInTheDocument();
	});
});
