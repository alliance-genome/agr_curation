import { fireEvent } from '@testing-library/react';
import { InputTextAreaTableEditor } from '../InputTextAreaTableEditor';
import { makeEditorOptions, renderInTable } from '../../__tests__/editorTestUtils';

describe('InputTextAreaTableEditor', () => {
	it('should render a text area with the current field value', () => {
		const editorOptions = makeEditorOptions({ phenotypeStatement: 'a statement' });
		const result = renderInTable(<InputTextAreaTableEditor editorOptions={editorOptions} field="phenotypeStatement" />);

		expect(result.container.querySelector('textarea')).toHaveValue('a statement');
	});

	it('should call editorCallback with the typed value', () => {
		const editorOptions = makeEditorOptions({ phenotypeStatement: '' });
		const result = renderInTable(<InputTextAreaTableEditor editorOptions={editorOptions} field="phenotypeStatement" />);

		fireEvent.change(result.container.querySelector('textarea'), { target: { value: 'typed' } });

		expect(editorOptions.editorCallback).toHaveBeenCalledWith('typed');
	});

	it('should pass rows and cols to the text area', () => {
		const editorOptions = makeEditorOptions({ phenotypeStatement: '' });
		const result = renderInTable(
			<InputTextAreaTableEditor editorOptions={editorOptions} field="phenotypeStatement" rows={1} cols={30} />
		);

		expect(result.container.querySelector('textarea')).toHaveAttribute('rows', '1');
		expect(result.container.querySelector('textarea')).toHaveAttribute('cols', '30');
	});

	it('should display the error for its field and mark the control invalid', () => {
		const editorOptions = makeEditorOptions({ phenotypeStatement: '' });
		const result = renderInTable(
			<InputTextAreaTableEditor editorOptions={editorOptions} field="phenotypeStatement" />,
			{
				errorMessages: { 0: { phenotypeStatement: { severity: 'error', message: 'Statement is required' } } },
			}
		);

		expect(result.getByText('Statement is required')).toBeInTheDocument();
		expect(result.container.querySelector('textarea')).toHaveClass('p-invalid');
	});
});
