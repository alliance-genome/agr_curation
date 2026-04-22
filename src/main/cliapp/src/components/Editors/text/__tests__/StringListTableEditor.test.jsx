import { render, fireEvent } from '@testing-library/react';
import { StringListTableEditor } from '../StringListTableEditor';
import { makeEditorOptions, emptyErrorMessagesRef } from '../../../../tools/jest/editorTestUtils';
import '../../../../tools/jest/setupTests';

describe('StringListTableEditor', () => {
	it('should render an input joining the array with commas', () => {
		const editorOptions = makeEditorOptions({ commonNames: ['zebrafish', 'fish', 'dre'] });
		const result = render(
			<StringListTableEditor
				editorOptions={editorOptions}
				field="commonNames"
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const input = result.container.querySelector('input');
		expect(input.value).toBe('zebrafish, fish, dre');
	});

	it('should call editorCallback with the parsed array on change', () => {
		const editorOptions = makeEditorOptions({ commonNames: [] });
		const result = render(
			<StringListTableEditor
				editorOptions={editorOptions}
				field="commonNames"
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const input = result.container.querySelector('input');
		fireEvent.change(input, { target: { value: 'foo, bar , baz' } });

		expect(editorOptions.editorCallback).toHaveBeenCalledWith(['foo', 'bar', 'baz']);
	});

	it('should call editorCallback with empty array when cleared', () => {
		const editorOptions = makeEditorOptions({ commonNames: ['existing'] });
		const result = render(
			<StringListTableEditor
				editorOptions={editorOptions}
				field="commonNames"
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const input = result.container.querySelector('input');
		fireEvent.change(input, { target: { value: '' } });

		expect(editorOptions.editorCallback).toHaveBeenCalledWith([]);
	});

	it('should render empty input when field value is not an array', () => {
		const editorOptions = makeEditorOptions({ commonNames: null });
		const result = render(
			<StringListTableEditor
				editorOptions={editorOptions}
				field="commonNames"
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const input = result.container.querySelector('input');
		expect(input.value).toBe('');
	});

	it('should display error messages when present', () => {
		const editorOptions = makeEditorOptions({ commonNames: ['x'] });
		const errorRef = {
			current: { 0: { commonNames: { severity: 'error', message: 'List invalid' } } },
		};
		const result = render(
			<StringListTableEditor editorOptions={editorOptions} field="commonNames" errorMessagesRef={errorRef} />
		);

		expect(result.getByText('List invalid')).toBeInTheDocument();
	});
});
