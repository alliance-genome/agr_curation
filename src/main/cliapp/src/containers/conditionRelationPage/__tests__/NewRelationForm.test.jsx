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
				singleReference: REFERENCE,
				conditionRelationType: vocabularyTermsFor('condition_relation')[1],
				conditions: [CONDITION],
			})
		);
		await waitFor(() => expect(screen.queryByText('Handle')).not.toBeInTheDocument());
	});

	it('searches references and conditions at their own endpoints', async () => {
		renderForm();
		await waitForForm();

		typeInField('Reference', 'Smith');
		typeInField('Conditions', 'aspirin');

		await waitFor(() =>
			expect(search).toHaveBeenCalledWith(
				Endpoints.Document.LITERATURE_REFERENCE,
				15,
				0,
				[],
				expect.objectContaining({ singleReferenceFilter: expect.any(Object) })
			)
		);
		await waitFor(() =>
			expect(search).toHaveBeenCalledWith(
				Endpoints.Annotation.EXPERIMENTAL_CONDITION,
				15,
				0,
				[],
				expect.objectContaining({ experimentalConditionFilter: expect.any(Object) })
			)
		);
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
		expect(screen.getByRole('combobox', { name: 'singleReference' }).closest('.p-autocomplete')).toHaveClass(
			'p-invalid'
		);
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

	it('says an empty reference is required, beside it, and sends nothing', async () => {
		renderForm();
		await waitForForm();

		typeInField('Handle', 'aspirin_daily');
		selectInField('Relation', 'condition_relation_beta');
		clickSave();

		expect(await within(getField('Reference')).findByText('Reference is required.')).toBeInTheDocument();
		expect(screen.getByRole('combobox', { name: 'singleReference' }).closest('.p-autocomplete')).toHaveClass(
			'p-invalid'
		);
		expect(createConditionRelation).not.toHaveBeenCalled();
	});

	it('clears the required message once a reference is picked', async () => {
		createConditionRelation.mockImplementation(() => resolvedCreate({ id: 99 }));
		renderForm();
		await waitForForm();

		expect(within(getField('Reference')).queryByText('Reference is required.')).not.toBeInTheDocument();
		clickSave();
		expect(await within(getField('Reference')).findByText('Reference is required.')).toBeInTheDocument();
		await pickSuggestionInField('Reference', 'Smith', /Smith 2020/);

		expect(within(getField('Reference')).queryByText('Reference is required.')).not.toBeInTheDocument();
		clickSave();
		await waitFor(() => expect(createConditionRelation).toHaveBeenCalled());
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

	it('asks for a reference picked from the suggestions when one is only typed, and sends nothing', async () => {
		renderForm();
		await waitForForm();

		typeInField('Reference', 'AGRKB:999');
		clickSave();

		expect(await within(getField('Reference')).findByText('Must select reference from dropdown')).toBeInTheDocument();
		expect(createConditionRelation).not.toHaveBeenCalled();
	});

	it('says a reference of only spaces is required', async () => {
		renderForm();
		await waitForForm();

		typeInField('Reference', '   ');
		clickSave();

		expect(await within(getField('Reference')).findByText('Reference is required.')).toBeInTheDocument();
		expect(createConditionRelation).not.toHaveBeenCalled();
	});

	it('names the reference and conditions autocompletes', async () => {
		renderForm();
		await waitForForm();

		expect(screen.getByRole('combobox', { name: 'singleReference' })).toBeInTheDocument();
		expect(screen.getByRole('combobox', { name: 'conditions' })).toBeInTheDocument();
	});
});
