import { render } from '@testing-library/react';
import { AutocompleteMultiTableEditor } from '../AutocompleteMultiTableEditor';
import { makeEditorOptions, emptyErrorMessagesRef } from '../../../../tools/jest/editorTestUtils';
import '../../../../tools/jest/setupTests';

describe('AutocompleteMultiTableEditor', () => {
	it('should render an autocomplete component', () => {
		const editorOptions = makeEditorOptions({ evidenceCodes: [{ id: 1, curie: 'ECO:001', name: 'evidence' }] });
		const result = render(
			<AutocompleteMultiTableEditor
				editorOptions={editorOptions}
				field="evidenceCodes"
				endpoint="eco"
				autocompleteFields={['curie', 'name']}
				filterName="evidenceFilter"
				errorMessagesRef={emptyErrorMessagesRef}
			/>
		);

		const autocomplete = result.container.querySelector('.p-autocomplete');
		expect(autocomplete).toBeInTheDocument();
	});

	it('should display error messages when present', () => {
		const editorOptions = makeEditorOptions({ evidenceCodes: [] });
		const errorRef = {
			current: { 0: { evidenceCodes: { severity: 'error', message: 'Required field' } } },
		};

		const result = render(
			<AutocompleteMultiTableEditor
				editorOptions={editorOptions}
				field="evidenceCodes"
				endpoint="eco"
				autocompleteFields={['curie']}
				filterName="evidenceFilter"
				errorMessagesRef={errorRef}
			/>
		);

		expect(result.getByText('Required field')).toBeInTheDocument();
	});

	it('should display UI error messages when uiErrorMessagesRef is provided', () => {
		const editorOptions = makeEditorOptions({ evidenceCodes: [] });
		const uiErrorRef = {
			current: { 0: { evidenceCodes: { severity: 'warn', message: 'UI warning' } } },
		};

		const result = render(
			<AutocompleteMultiTableEditor
				editorOptions={editorOptions}
				field="evidenceCodes"
				endpoint="eco"
				autocompleteFields={['curie']}
				filterName="evidenceFilter"
				errorMessagesRef={emptyErrorMessagesRef}
				uiErrorMessagesRef={uiErrorRef}
			/>
		);

		expect(result.getByText('UI warning')).toBeInTheDocument();
	});
});
