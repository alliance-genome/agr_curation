import { fireEvent } from '@testing-library/react';
import { DialogTriggerTableEditor } from '../DialogTriggerTableEditor';
import { makeEditorOptions, renderInTable } from '../../__tests__/editorTestUtils';

const renderEditor = ({ errorMessages, ...props } = {}) => {
	const editorOptions = makeEditorOptions({ relatedNotes: [] }, 1);
	const onOpenInEdit = vi.fn();
	const result = renderInTable(
		<DialogTriggerTableEditor
			editorOptions={editorOptions}
			onOpenInEdit={onOpenInEdit}
			errorField="relatedNotes"
			addText="Add Note"
			{...props}
		/>,
		{ errorMessages }
	);
	return { ...result, editorOptions, onOpenInEdit };
};

describe('DialogTriggerTableEditor', () => {
	it('should open the dialog in edit mode for its row', () => {
		const { getByRole, editorOptions, onOpenInEdit } = renderEditor();

		fireEvent.click(getByRole('button'));

		expect(onOpenInEdit).toHaveBeenCalledWith(expect.objectContaining({ type: 'click' }), editorOptions, true);
	});

	it('should label the trigger with displayText when given', () => {
		const { getByRole } = renderEditor({ displayText: 'Notes(2) ' });

		expect(getByRole('button')).toHaveTextContent('Notes(2)');
	});

	it("should show the error filed under its errorField for its row, and not another field's", () => {
		const { getByText, queryByText } = renderEditor({
			errorMessages: {
				1: {
					relatedNotes: { severity: 'error', message: 'Note type is required' },
					uniqueId: { severity: 'error', message: 'Not this one' },
				},
			},
		});

		expect(getByText('Note type is required')).toBeInTheDocument();
		expect(queryByText('Not this one')).not.toBeInTheDocument();
	});

	it("should not show another row's error", () => {
		const { queryByText } = renderEditor({
			errorMessages: { 0: { relatedNotes: { severity: 'error', message: 'Note type is required' } } },
		});

		expect(queryByText('Note type is required')).not.toBeInTheDocument();
	});
});
