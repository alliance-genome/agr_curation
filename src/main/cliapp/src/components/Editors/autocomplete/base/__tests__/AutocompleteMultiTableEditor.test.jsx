import { fireEvent, screen } from '@testing-library/react';
import { AutocompleteMultiTableEditor } from '../AutocompleteMultiTableEditor';
import { makeEditorOptions, renderInTable } from '../../../__tests__/editorTestUtils';
import { removeFirstToken, typeInto } from '../../../widgets/__tests__/widgetTestUtils';
import { SearchService } from '../../../../../service/SearchService';
import '../../../../../tools/jest/setupTests';

vi.mock('../../../../../service/SearchService');

const FIRST = { id: 1, curie: 'ECO:001', name: 'first evidence' };
const SECOND = { id: 2, curie: 'ECO:002', name: 'second evidence' };
const REQUIRED = { 0: { evidenceCodes: { severity: 'error', message: 'Required field' } } };

const tokenLabels = (container) =>
	[...container.querySelectorAll('.p-autocomplete-token-label')].map((node) => node.textContent);

const renderEditor = (rowData, { errorMessages, uiErrorMessages, ...props } = {}) => {
	const editorOptions = makeEditorOptions(rowData);
	const result = renderInTable(
		<AutocompleteMultiTableEditor
			editorOptions={editorOptions}
			field="evidenceCodes"
			endpoint="ecoterm"
			autocompleteFields={['curie', 'name']}
			filterName="evidenceFilter"
			{...props}
		/>,
		{ errorMessages, uiErrorMessages }
	);
	return { ...result, editorOptions };
};

describe('AutocompleteMultiTableEditor', () => {
	beforeEach(() => {
		SearchService.mockClear();
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [SECOND] }));
	});

	it('should render a token for each entity in the row value', () => {
		const result = renderEditor({ evidenceCodes: [FIRST, SECOND] });

		expect(tokenLabels(result.container)).toEqual(['ECO:001', 'ECO:002']);
	});

	// Most adapters pass a subField other than the default 'curie'; dropping the
	// pass-through would label their tokens by curie instead.
	it('should label tokens with the supplied subField', () => {
		const result = renderEditor(
			{ evidenceCodes: [{ id: 1, curie: 'HGNC:1', primaryExternalId: 'MGI:98765' }] },
			{ subField: 'primaryExternalId' }
		);

		expect(tokenLabels(result.container)).toEqual(['MGI:98765']);
	});

	it('should name the control after its field', () => {
		const result = renderEditor({ evidenceCodes: [] });

		expect(result.getByLabelText('evidenceCodes')).toBeInTheDocument();
	});

	it('should call editorCallback with the row entities plus the picked one', async () => {
		const result = renderEditor({ evidenceCodes: [FIRST] });

		typeInto(result.container, 'ECO');
		fireEvent.click(
			await screen.findByText(
				/ECO:002/,
				{ selector: '.p-autocomplete-item, .p-autocomplete-item *' },
				{ timeout: 3000 }
			)
		);

		expect(result.editorOptions.editorCallback).toHaveBeenLastCalledWith([FIRST, SECOND]);
	});

	it('should call editorCallback with the remaining entities when a token is removed', () => {
		const result = renderEditor({ evidenceCodes: [FIRST, SECOND] });

		removeFirstToken(result.container);

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith([SECOND]);
	});

	it('should call editorCallback with an empty array when the last token is removed', () => {
		const result = renderEditor({ evidenceCodes: [FIRST] });

		removeFirstToken(result.container);

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith([]);
	});

	it('should display error messages when present', () => {
		const result = renderEditor({ evidenceCodes: [] }, { errorMessages: REQUIRED });

		expect(result.getByText('Required field')).toBeInTheDocument();
	});

	it('should display client-side error messages when present', () => {
		const result = renderEditor({ evidenceCodes: [] }, { uiErrorMessages: REQUIRED });

		expect(result.getByText('Required field')).toBeInTheDocument();
	});

	it('should mark the control invalid when an error is present', () => {
		const result = renderEditor({ evidenceCodes: [] }, { errorMessages: REQUIRED });

		expect(result.container.querySelector('.p-autocomplete')).toHaveClass('p-invalid');
	});
});
