import { fireEvent, waitFor } from '@testing-library/react';
import { ConditionHandleTableEditor } from '../ConditionHandleTableEditor';
import { makeEditorOptions, renderInTable } from '../../../__tests__/editorTestUtils';
import { SearchService } from '../../../../../service/SearchService';
import '../../../../../tools/jest/setupTests';

vi.mock('../../../../../service/SearchService');

// The row embeds a trimmed relation; the search returns the full entity. They share
// only the id, which is what makes dataKey load-bearing.
const RELATION = { id: 1, handle: 'standard', singleReference: { curie: 'AGRKB:101' } };
const FETCHED_RELATION = { id: 1, handle: 'standard', uniqueId: 'CR:1', obsolete: false };
const OTHER_RELATION = { id: 2, handle: 'control', uniqueId: 'CR:2', obsolete: false };

const rowWith = (relations) => ({ conditionRelations: relations });

// The shared pickOption helper clicks twice synchronously; here the options only
// arrive after the panel's onShow fetch resolves, so each test waits in between.

const renderEditor = (rowData, { errorMessages } = {}) => {
	const editorOptions = makeEditorOptions(rowData);
	const result = renderInTable(<ConditionHandleTableEditor editorOptions={editorOptions} />, { errorMessages });
	return { ...result, editorOptions };
};

describe('ConditionHandleTableEditor', () => {
	beforeEach(() => {
		SearchService.mockClear();
		SearchService.prototype.find = vi.fn(() => Promise.resolve({ results: [FETCHED_RELATION, OTHER_RELATION] }));
	});

	it('should render nothing for a row with no condition relations', () => {
		const result = renderEditor({ conditionRelations: null });

		expect(result.container.querySelector('.p-dropdown')).not.toBeInTheDocument();
	});

	it('should render nothing for a relation that has no handle', () => {
		const result = renderEditor(rowWith([{ id: 9, singleReference: { curie: 'AGRKB:101' } }]));

		expect(result.container.querySelector('.p-dropdown')).not.toBeInTheDocument();
	});

	// Options arrive only on open, so until then nothing can match and the row's own
	// handle is what the curator sees.
	it('should show the current handle before any options are loaded', () => {
		const result = renderEditor(rowWith([RELATION]));

		expect(result.container.querySelector('.p-dropdown-label')).toHaveTextContent('standard');
		expect(SearchService.prototype.find).not.toHaveBeenCalled();
	});

	it('should fetch the handles for the row reference when the panel opens', async () => {
		const result = renderEditor(rowWith([RELATION]));

		fireEvent.click(result.container.querySelector('.p-dropdown'));

		await waitFor(() => expect(SearchService.prototype.find).toHaveBeenCalled());
		const [endpoint, , , findOptions] = SearchService.prototype.find.mock.calls[0];
		expect(endpoint).toBe('condition-relation');
		expect(findOptions).toEqual({ 'singleReference.curie': 'AGRKB:101' });
	});

	it('should not fetch when the relation has no reference to search by', async () => {
		const result = renderEditor(rowWith([{ id: 1, handle: 'standard' }]));

		fireEvent.click(result.container.querySelector('.p-dropdown'));

		// The panel has to be open for this to mean anything, and onShow runs after its
		// enter transition, so poll for the fetch rather than asserting on the click.
		await waitFor(() => expect(document.querySelector('.p-dropdown-panel')).toBeInTheDocument());
		await waitFor(() => expect(SearchService.prototype.find).toHaveBeenCalled(), { timeout: 500 }).catch(() => {});

		expect(SearchService.prototype.find).not.toHaveBeenCalled();
	});

	it('should store the whole rebuilt conditionRelations array when a handle is picked', async () => {
		const result = renderEditor(rowWith([RELATION, { id: 3, handle: 'kept' }]));

		fireEvent.click(result.container.querySelector('.p-dropdown'));
		await waitFor(() => expect(result.getByText('control')).toBeInTheDocument());
		fireEvent.click(result.getByText('control'));

		expect(result.editorOptions.editorCallback).toHaveBeenCalledWith([OTHER_RELATION, { id: 3, handle: 'kept' }]);
	});

	// The row's relation and the fetched one share only their id, so dataKey is the
	// only thing that can match them; deep equality would fail on the other fields.
	it('should highlight the current handle among the loaded options', async () => {
		const result = renderEditor(rowWith([RELATION]));

		fireEvent.click(result.container.querySelector('.p-dropdown'));

		await waitFor(() => expect(document.querySelector('.p-dropdown-item.p-highlight')).toBeInTheDocument());
		expect(document.querySelector('.p-dropdown-item.p-highlight')).toHaveTextContent('standard');
	});

	// The server reports this field as conditionRelationHandle, not conditionRelations.
	it('should render an error filed under conditionRelationHandle', () => {
		const result = renderEditor(rowWith([RELATION]), {
			errorMessages: { 0: { conditionRelationHandle: { severity: 'error', message: 'Invalid handle' } } },
		});

		expect(result.getByText('Invalid handle')).toBeInTheDocument();
	});

	it('should mark the dropdown invalid when an error is present', () => {
		const result = renderEditor(rowWith([RELATION]), {
			errorMessages: { 0: { conditionRelationHandle: { severity: 'error', message: 'Invalid handle' } } },
		});

		expect(result.container.querySelector('.p-dropdown')).toHaveClass('p-invalid');
	});

	it('should ignore an error filed under the property it writes', () => {
		const result = renderEditor(rowWith([RELATION]), {
			errorMessages: { 0: { conditionRelations: { severity: 'error', message: 'wrong key' } } },
		});

		expect(result.queryByText('wrong key')).not.toBeInTheDocument();
	});
});
