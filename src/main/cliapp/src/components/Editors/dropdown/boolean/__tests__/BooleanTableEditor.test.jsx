import { render, fireEvent } from '@testing-library/react';
import { BooleanTableEditor } from '../BooleanTableEditor';
import { makeEditorOptions, emptyErrorMessagesRef } from '../../../__tests__/editorTestUtils';
import '../../../../../tools/jest/setupTests';

vi.mock('../../../../../service/useControlledVocabularyService', () => ({
	useControlledVocabularyService: () => ({
		terms: [
			{ text: 'true', name: 'true' },
			{ text: 'false', name: 'false' },
		],
	}),
}));

describe('BooleanTableEditor', () => {
	it('should render a dropdown', () => {
		const editorOptions = makeEditorOptions({ internal: true });
		const result = render(
			<BooleanTableEditor editorOptions={editorOptions} field="internal" errorMessagesRef={emptyErrorMessagesRef} />
		);

		const dropdown = result.container.querySelector('.p-dropdown');
		expect(dropdown).toBeInTheDocument();
	});

	it('should call editorCallback with parsed boolean when an option is selected', () => {
		const editorOptions = makeEditorOptions({ internal: false });
		const result = render(
			<BooleanTableEditor editorOptions={editorOptions} field="internal" errorMessagesRef={emptyErrorMessagesRef} />
		);

		const dropdown = result.container.querySelector('.p-dropdown');
		fireEvent.click(dropdown);

		const option = result.getByText('true');
		fireEvent.click(option);

		expect(editorOptions.editorCallback).toHaveBeenCalledWith(true);
	});

	it('should display error messages when present', () => {
		const editorOptions = makeEditorOptions({ internal: true });
		const errorRef = {
			current: { 0: { internal: { severity: 'error', message: 'Invalid value' } } },
		};

		const result = render(
			<BooleanTableEditor editorOptions={editorOptions} field="internal" errorMessagesRef={errorRef} />
		);

		expect(result.getByText('Invalid value')).toBeInTheDocument();
	});

	it('should not render clear button by default', () => {
		const editorOptions = makeEditorOptions({ internal: true });
		const result = render(
			<BooleanTableEditor editorOptions={editorOptions} field="internal" errorMessagesRef={emptyErrorMessagesRef} />
		);

		const clearButton = result.container.querySelector('.p-dropdown-clear-icon');
		expect(clearButton).not.toBeInTheDocument();
	});

	it('should render clear button when showClear is true', () => {
		const editorOptions = makeEditorOptions({ internal: true });
		const result = render(
			<BooleanTableEditor
				editorOptions={editorOptions}
				field="internal"
				errorMessagesRef={emptyErrorMessagesRef}
				showClear={true}
			/>
		);

		const clearButton = result.container.querySelector('.p-dropdown-clear-icon');
		expect(clearButton).toBeInTheDocument();
	});
});
