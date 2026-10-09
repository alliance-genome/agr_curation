import { fireEvent, screen } from '@testing-library/react';
import { AutocompleteSingleTableEditor } from '../AutocompleteSingleTableEditor';
import { makeEditorOptions, renderInTable } from '../../../__tests__/editorTestUtils';
import { typeInto } from '../../../widgets/__tests__/widgetTestUtils';
import { SearchService } from '../../../../../service/SearchService';
import '../../../../../tools/jest/setupTests';

vi.mock('../../../../../service/SearchService');

const SUGGESTION = { id: 7, curie: 'DOID:14330', name: 'Parkinson' };
const REQUIRED = { 0: { diseaseAnnotationObject: { severity: 'error', message: 'Required field' } } };

const renderEditor = (rowData, { errorMessages, uiErrorMessages, ...props } = {}) => {
	const editorOptions = makeEditorOptions(rowData);
	const result = renderInTable(
		<AutocompleteSingleTableEditor
			editorOptions={editorOptions}
			field="diseaseAnnotationObject"
			endpoint="doterm"
			autocompleteFields={['curie', 'name']}
			filterName="diseaseFilter"
			{...props}
		/>,
		{ errorMessages, uiErrorMessages }
	);
	return { ...result, editorOptions };
};

describe('AutocompleteSingleTableEditor', () => {
	beforeEach(() => {
		SearchService.mockClear();
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [SUGGESTION] }));
	});

	it('should show the row value by subField', () => {
		const result = renderEditor({ diseaseAnnotationObject: { curie: 'DOID:123', name: 'diabetes' } });

		expect(result.container.querySelector('input')).toHaveValue('DOID:123');
	});

	it('should name the control after its field', () => {
		const result = renderEditor({ diseaseAnnotationObject: null });

		expect(result.getByLabelText('diseaseAnnotationObject')).toBeInTheDocument();
	});

	it('should show initialValue in place of the row value when provided', () => {
		const result = renderEditor({ diseaseAnnotationObject: { curie: 'DOID:456' } }, { initialValue: 'Custom Initial' });

		expect(result.container.querySelector('input')).toHaveValue('Custom Initial');
	});

	it('should call editorCallback with the whole entity when a suggestion is picked', async () => {
		const result = renderEditor({ diseaseAnnotationObject: null });

		typeInto(result.container, 'Park');
		fireEvent.click(await screen.findByText(/Parkinson/, {}, { timeout: 3000 }));

		expect(result.editorOptions.editorCallback).toHaveBeenLastCalledWith(SUGGESTION);
		expect(result.container.querySelector('input')).toHaveValue('DOID:14330');
	});

	it('should call editorCallback with typed text keyed by subField', () => {
		const result = renderEditor({ diseaseAnnotationObject: null });

		typeInto(result.container, 'DOID:9');

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith({ curie: 'DOID:9' });
	});

	it('should call editorCallback with null when the input is emptied', () => {
		const result = renderEditor({ diseaseAnnotationObject: { curie: 'DOID:123' } });

		typeInto(result.container, '');

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith(null);
	});

	it('should display error messages when present', () => {
		const result = renderEditor({ diseaseAnnotationObject: null }, { errorMessages: REQUIRED });

		expect(result.getByText('Required field')).toBeInTheDocument();
	});

	it('should display client-side error messages when present', () => {
		const result = renderEditor({ diseaseAnnotationObject: null }, { uiErrorMessages: REQUIRED });

		expect(result.getByText('Required field')).toBeInTheDocument();
	});

	it('should mark the control invalid when an error is present', () => {
		const result = renderEditor({ diseaseAnnotationObject: null }, { errorMessages: REQUIRED });

		expect(result.container.querySelector('.p-autocomplete')).toHaveClass('p-invalid');
	});
});
