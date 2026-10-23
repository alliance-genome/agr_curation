import { fireEvent, waitFor } from '@testing-library/react';
import { ConditionRelationsForm } from '../ConditionRelationsForm';
import { renderNewAnnotationSubform } from './newAnnotationFormTestUtils';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	selectInColumn,
} from '../../allelesPage/__tests__/alleleFormTestUtils';
import { SearchService } from '../../../service/SearchService';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../constants/FilterFields';
import { vocabularyTermsFor } from '../../../service/__mocks__/useControlledVocabularyService';

vi.mock('../../../service/SearchService');
vi.mock('../../../service/useControlledVocabularyService');

const CONDITION = { id: 51, conditionSummary: 'heat shock', uniqueId: 'ZECO:1' };
const ERROR = { severity: 'error', message: 'Relation is required' };

const renderForm = () =>
	renderNewAnnotationSubform((state, dispatch) => (
		<ConditionRelationsForm
			dispatch={dispatch}
			conditionRelations={state.newAnnotation.conditionRelations}
			showConditionRelations={state.showConditionRelations}
			errorMessages={state.exConErrorMessages}
			buttonIsDisabled={false}
			editingRows={state.conditionRelationsEditingRows}
		/>
	));

const relations = (getState) => getState().newAnnotation.conditionRelations;

const addRelation = (getByRole) => fireEvent.click(getByRole('button', { name: 'Add Experimental Condition' }));

describe('<ConditionRelationsForm />', () => {
	beforeEach(() => {
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [CONDITION] }));
	});

	it('saves the relation type, conditions and internal on the row they were edited in', async () => {
		const { container, getByRole, getState } = renderForm();
		addRelation(getByRole);
		addRelation(getByRole);
		const [, otherType] = vocabularyTermsFor('condition_relation');

		selectInColumn(container, 'Relation', otherType.name, 1);
		selectInColumn(container, 'Internal', 'true', 1);
		await pickSuggestionInColumn(container, 'Conditions', 'heat', /heat shock/, 1);

		expect(relations(getState)[1]).toMatchObject({
			conditionRelationType: otherType,
			internal: true,
			conditions: [CONDITION],
		});
		expect(relations(getState)[0].conditions).toEqual([]);
	});

	// Conditions are found by their chemical, anatomy and other terms as well as their summary.
	it('searches every condition field set', async () => {
		const { container, getByRole } = renderForm();
		addRelation(getByRole);

		fireEvent.change(getCellByHeader(container, 'Conditions', 0).querySelector('input'), {
			target: { value: 'abc' },
		});

		await waitFor(() => expect(SearchService.prototype.search).toHaveBeenCalled(), { timeout: 3000 });
		const [endpoint, , , , params] = SearchService.prototype.search.mock.calls[0];
		expect(endpoint).toEqual('experimental-condition');
		expect(Object.keys(params)).toEqual(['conditionSummaryFilter', 'obsoleteFilter']);
		expect(Object.keys(params.conditionSummaryFilter)).toEqual(
			getAutocompleteFields(AUTOCOMPLETE_CONFIGS.experimentalConditionDetailedAutocompleteConfig)
		);
	});

	it('keeps edits and errors on the right rows after a delete and another add', () => {
		const { container, getByRole, getState, dispatch } = renderForm();
		addRelation(getByRole);
		addRelation(getByRole);
		clickDeleteInRow(container, 0);
		addRelation(getByRole);
		const [, second] = vocabularyTermsFor('condition_relation');

		selectInColumn(container, 'Relation', second.name, 1);
		const [kept, added] = relations(getState);
		dispatch({
			type: 'UPDATE_ERROR_MESSAGES',
			errorType: 'exConErrorMessages',
			errorMessages: { [added.dataKey]: { conditionRelationType: ERROR } },
		});

		expect(relations(getState).map((relation) => relation.conditionRelationType)).toEqual([undefined, second]);
		expect(kept.dataKey).not.toEqual(added.dataKey);
		expect(getCellByHeader(container, 'Relation', 1).textContent).toContain('Relation is required');
		expect(getCellByHeader(container, 'Relation', 0).textContent).not.toContain('Relation is required');
	});
});
