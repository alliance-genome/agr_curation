import { render } from '@testing-library/react';
import { AutocompleteSingleTableEditor } from '../AutocompleteSingleTableEditor';
import { makeEditorOptions, emptyErrorMessagesRef } from '../../../../../tools/jest/editorTestUtils';
import '../../../../../tools/jest/setupTests';

describe('AutocompleteSingleTableEditor', () => {
	it('should render an autocomplete component', () => {
		const editorOptions = makeEditorOptions({ diseaseAnnotationObject: { curie: 'DOID:123', name: 'diabetes' } });
		const result = render(
			<AutocompleteSingleTableEditor
				editorOptions={editorOptions}
				field="diseaseAnnotationObject"
				endpoint="do-ontology"
				autocompleteFields={['curie', 'name']}
				filterName="diseaseFilter"
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const autocomplete = result.container.querySelector('.p-autocomplete');
		expect(autocomplete).toBeInTheDocument();
	});

	it('should display error messages when present', () => {
		const editorOptions = makeEditorOptions({ diseaseAnnotationObject: null });
		const errorRef = {
			current: { 0: { diseaseAnnotationObject: { severity: 'error', message: 'Required field' } } },
		};

		const result = render(
			<AutocompleteSingleTableEditor
				editorOptions={editorOptions}
				field="diseaseAnnotationObject"
				endpoint="do-ontology"
				autocompleteFields={['curie']}
				filterName="diseaseFilter"
				errorMessagesRef={errorRef}
			/>
		);

		expect(result.getByText('Required field')).toBeInTheDocument();
	});

	it('should use custom initialValue when provided', () => {
		const editorOptions = makeEditorOptions({ diseaseAnnotationObject: { curie: 'DOID:456' } });
		const result = render(
			<AutocompleteSingleTableEditor
				editorOptions={editorOptions}
				field="diseaseAnnotationObject"
				endpoint="do-ontology"
				autocompleteFields={['curie']}
				filterName="diseaseFilter"
				errorMessagesRef={emptyErrorMessagesRef}
				initialValue="Custom Initial"
			/>
		);

		const input = result.container.querySelector('input');
		expect(input.value).toBe('Custom Initial');
	});
});
