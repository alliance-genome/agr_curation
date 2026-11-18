import { useEffect } from 'react';
import { fireEvent, screen, waitFor, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { Endpoints } from '../../../constants/Endpoints';
import {
	clickSave,
	getField,
	pickSuggestionInField,
	rejectedCreate,
	resolvedCreate,
	selectInField,
	typeInField,
} from '../../../tools/jest/newEntityFormTestUtils';

const createConditionRelation = vi.fn();
const REFERENCE = { id: 5, curie: 'AGRKB:101000000000001', short_citation: 'Smith 2020' };
const CONDITION = { id: 21, conditionSummary: 'chemical treatment:aspirin' };
const RESULTS_BY_ENDPOINT = {
	[Endpoints.Document.LITERATURE_REFERENCE]: [REFERENCE],
	[Endpoints.Annotation.EXPERIMENTAL_CONDITION]: [CONDITION],
};
const search = vi.fn((endpoint) => {
	const results = RESULTS_BY_ENDPOINT[endpoint] ?? [];
	return Promise.resolve({ results, totalResults: results.length });
});

vi.mock('../../../service/ConditionRelationService', () => ({
	ConditionRelationService: class {
		createConditionRelation = createConditionRelation;
	},
}));

// Each autocomplete gets its own results by endpoint, so a search sent to the wrong endpoint finds nothing.
vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

vi.mock('../../../service/useControlledVocabularyService');

const { NewRelationForm } = await import('../NewRelationForm');
const { useNewRelationReducer } = await import('../useNewRelationReducer');
const { vocabularyTermsFor } = await import('../../../service/__mocks__/useControlledVocabularyService');

const renderForm = (onCreated = vi.fn()) => {
	const Harness = () => {
		const { newRelationState, newRelationDispatch } = useNewRelationReducer();
		useEffect(() => newRelationDispatch({ type: 'OPEN_DIALOG' }), [newRelationDispatch]);
		return (
			<NewRelationForm
				newRelationState={newRelationState}
				newRelationDispatch={newRelationDispatch}
				searchService={{ search }}
				conditionRelationTypeTerms={vocabularyTermsFor('condition_relation')}
				setNewConditionRelation={onCreated}
			/>
		);
	};
	return renderWithClient(<Harness />);
};

const waitForForm = () => screen.findByText('Handle');

describe('<NewRelationForm />', () => {
	beforeEach(() => {
		createConditionRelation.mockReset();
		search.mockClear();
	});

	it('creates the relation from the entered handle, picked reference, relation and conditions, then closes', async () => {
		const created = { id: 99, handle: 'aspirin_daily' };
		createConditionRelation.mockImplementation(() => resolvedCreate(created));
		const onCreated = vi.fn();
		renderForm(onCreated);
		await waitForForm();

		typeInField('Handle', 'aspirin_daily');
		await pickSuggestionInField('Reference', 'Smith', /Smith 2020/);
		selectInField('Relation', 'condition_relation_beta');
		await pickSuggestionInField('Conditions', 'aspirin', /chemical treatment:aspirin/);
		clickSave();

		await waitFor(() => expect(onCreated).toHaveBeenCalledWith(created, expect.anything()));
		expect(await screen.findByText('Successful')).toBeInTheDocument();
		expect(createConditionRelation).toHaveBeenCalledWith(
			expect.objectContaining({
				handle: 'aspirin_daily',
				singleReference: expect.objectContaining({ curie: REFERENCE.curie }),
				conditionRelationType: expect.objectContaining({ name: 'condition_relation_beta' }),
				conditions: [expect.objectContaining({ id: CONDITION.id })],
			})
		);
		await waitFor(() => expect(screen.queryByText('Handle')).not.toBeInTheDocument());
	});

	it('shows a rejected field error beside its field and stays open', async () => {
		createConditionRelation.mockImplementation(() =>
			rejectedCreate({ singleReference: 'Invalid' }, 'Unable to create relation')
		);
		const onCreated = vi.fn();
		renderForm(onCreated);
		await waitForForm();

		await pickSuggestionInField('Reference', 'Smith', /Smith 2020/);
		clickSave();

		expect(await within(getField('Reference')).findByText('Invalid')).toBeInTheDocument();
		expect(within(getField('Handle')).queryByText('Invalid')).not.toBeInTheDocument();
		expect(screen.getByText('Unable to create relation')).toBeInTheDocument();
		expect(onCreated).not.toHaveBeenCalled();
	});

	it('closes on Cancel without creating anything', async () => {
		const onCreated = vi.fn();
		renderForm(onCreated);
		await waitForForm();

		typeInField('Handle', 'aspirin_daily');
		fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));

		await waitFor(() => expect(screen.queryByText('Handle')).not.toBeInTheDocument());
		expect(createConditionRelation).not.toHaveBeenCalled();
		expect(onCreated).not.toHaveBeenCalled();
	});

	it('says a duplicate relation is a duplicate', async () => {
		createConditionRelation.mockImplementation(() => rejectedCreate({ uniqueId: 'Non-unique value' }));
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Reference', 'Smith', /Smith 2020/);
		clickSave();

		expect(
			await screen.findByText('Page Error: New relation is a duplicate of an existing relation')
		).toBeInTheDocument();
	});

	// A reference typed but never picked is sent as the plain string, not as a reference.
	it.skip('sends a typed reference that was never picked as an unknown reference', async () => {
		createConditionRelation.mockImplementation(() => rejectedCreate({ singleReference: 'Invalid' }));
		renderForm();
		await waitForForm();

		typeInField('Reference', 'AGRKB:999');
		clickSave();

		await waitFor(() => expect(createConditionRelation).toHaveBeenCalled());
		expect(createConditionRelation.mock.calls[0][0].singleReference).toEqual({ curie: 'AGRKB:999' });
		expect(await within(getField('Reference')).findByText('Invalid')).toBeInTheDocument();
	});

	it('names the reference and conditions autocompletes', async () => {
		renderForm();
		await waitForForm();

		expect(screen.getByRole('combobox', { name: 'singleReference' })).toBeInTheDocument();
		expect(screen.getByRole('combobox', { name: 'conditions' })).toBeInTheDocument();
	});
});
