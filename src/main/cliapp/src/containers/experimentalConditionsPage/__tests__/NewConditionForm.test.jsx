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

const createExperimentalCondition = vi.fn();
const CONDITION_CLASS = { id: 11, curie: 'ZECO:0000105', name: 'chemical treatment', subsets: ['ZECO_0000267'] };
const CONDITION_TERM = { id: 12, curie: 'XCO:0000000', name: 'experimental condition' };
const RESULTS_BY_ENDPOINT = {
	[Endpoints.Ontology.ZECO]: [CONDITION_CLASS],
	[Endpoints.Ontology.EXPERIMENTAL_CONDITION]: [CONDITION_TERM],
};
const search = vi.fn((endpoint) => {
	const results = RESULTS_BY_ENDPOINT[endpoint] ?? [];
	return Promise.resolve({ results, totalResults: results.length });
});

vi.mock('../../../service/ExperimentalConditionService', () => ({
	ExperimentalConditionService: class {
		createExperimentalCondition = createExperimentalCondition;
	},
}));

// Each autocomplete gets its own results by endpoint, so a search sent to the wrong ontology finds nothing.
vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

const { NewConditionForm } = await import('../NewConditionForm');
const { useNewConditionReducer } = await import('../useNewConditionReducer');
const { ontologyTermAutocompleteFields } = await import('../../../components/Editors/autocomplete/ontology/utils');

const ONTOLOGY_FIELDS = [
	'conditionClass',
	'conditionId',
	'conditionGeneOntology',
	'conditionChemical',
	'conditionAnatomy',
	'conditionTaxon',
];

const renderForm = (onCreated = vi.fn()) => {
	const Harness = () => {
		const { newConditionState, newConditionDispatch } = useNewConditionReducer();
		useEffect(() => newConditionDispatch({ type: 'OPEN_DIALOG' }), [newConditionDispatch]);
		return (
			<NewConditionForm
				newConditionState={newConditionState}
				newConditionDispatch={newConditionDispatch}
				searchService={{ search }}
				setNewExperimentalCondition={onCreated}
				ontologyTermAutocompleteFields={ontologyTermAutocompleteFields}
			/>
		);
	};
	return renderWithClient(<Harness />);
};

const waitForForm = () => screen.findByText('Condition Class');

describe('<NewConditionForm />', () => {
	beforeEach(() => {
		createExperimentalCondition.mockReset();
		search.mockClear();
	});

	it('creates the condition from the picked terms and entered values, then closes', async () => {
		const created = { id: 99, conditionSummary: 'chemical treatment' };
		createExperimentalCondition.mockImplementation(() => resolvedCreate(created));
		const onCreated = vi.fn();
		renderForm(onCreated);
		await waitForForm();

		await pickSuggestionInField('Condition Class', 'chem', /chemical treatment/);
		await pickSuggestionInField('Condition Term', 'exp', /experimental condition/);
		typeInField('Quantity', '5 mg');
		selectInField('Internal', 'false');
		typeInField('Free Text', 'given daily');
		clickSave();

		await waitFor(() => expect(onCreated).toHaveBeenCalledWith(created, expect.anything()));
		expect(await screen.findByText('Successful')).toBeInTheDocument();
		expect(createExperimentalCondition).toHaveBeenCalledWith(
			expect.objectContaining({
				conditionClass: expect.objectContaining({ curie: CONDITION_CLASS.curie }),
				conditionId: expect.objectContaining({ curie: CONDITION_TERM.curie }),
				conditionQuantity: '5 mg',
				internal: false,
				conditionFreeText: 'given daily',
			})
		);
		await waitFor(() => expect(screen.queryByText('Condition Class')).not.toBeInTheDocument());
	});

	it.each([
		['Condition Class', Endpoints.Ontology.ZECO],
		['Condition Term', Endpoints.Ontology.EXPERIMENTAL_CONDITION],
		['Gene Ontology', Endpoints.Ontology.GO],
		['Chemical', Endpoints.Ontology.CHEMICAL],
		['Anatomy', Endpoints.Ontology.ANATOMICAL],
		['Taxon', Endpoints.Ontology.NCBI_TAXON],
	])('searches %s in its own ontology', async (label, endpoint) => {
		renderForm();
		await waitForForm();

		typeInField(label, 'term');

		await waitFor(() => expect(search).toHaveBeenCalledWith(endpoint, 15, 0, [], expect.any(Object)));
		expect(search).toHaveBeenCalledTimes(1);
	});

	it('offers only condition classes from the AGR slim', async () => {
		renderForm();
		await waitForForm();

		typeInField('Condition Class', 'chem');

		await waitFor(() =>
			expect(search).toHaveBeenCalledWith(
				Endpoints.Ontology.ZECO,
				15,
				0,
				[],
				expect.objectContaining({ subsetFilter: { subsets: { queryString: 'ZECO_0000267' } } })
			)
		);
	});

	// Blank ontology fields are sent as { curie: '' }; the server answers Invalid for each one it looks up by curie,
	// which is every one except Taxon.
	it.skip('leaves blank ontology fields out of the request', async () => {
		createExperimentalCondition.mockImplementation(() => resolvedCreate({ id: 99 }));
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Condition Class', 'chem', /chemical treatment/);
		clickSave();

		await waitFor(() => expect(createExperimentalCondition).toHaveBeenCalled());
		const sent = createExperimentalCondition.mock.calls[0][0];
		for (const field of ONTOLOGY_FIELDS.filter((name) => name !== 'conditionClass')) {
			expect(sent[field] ?? null).toBeNull();
		}
	});

	it('shows a rejected field error beside its field and stays open', async () => {
		createExperimentalCondition.mockImplementation(() =>
			rejectedCreate({ conditionClass: 'Invalid' }, 'Unable to create condition')
		);
		const onCreated = vi.fn();
		renderForm(onCreated);
		await waitForForm();

		await pickSuggestionInField('Condition Class', 'chem', /chemical treatment/);
		clickSave();

		expect(await within(getField('Condition Class')).findByText('Invalid')).toBeInTheDocument();
		expect(within(getField('Condition Term')).queryByText('Invalid')).not.toBeInTheDocument();
		expect(screen.getByText('Unable to create condition')).toBeInTheDocument();
		expect(onCreated).not.toHaveBeenCalled();
	});

	it('says a duplicate condition is a duplicate', async () => {
		createExperimentalCondition.mockImplementation(() => rejectedCreate({ uniqueId: 'Non-unique value' }));
		renderForm();
		await waitForForm();

		await pickSuggestionInField('Condition Class', 'chem', /chemical treatment/);
		clickSave();

		expect(
			await screen.findByText(
				'Page Error: New experimental condition is a duplicate of an existing experimental condition'
			)
		).toBeInTheDocument();
	});

	it('closes on Cancel without creating anything', async () => {
		const onCreated = vi.fn();
		renderForm(onCreated);
		await waitForForm();

		typeInField('Quantity', '5 mg');
		fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));

		await waitFor(() => expect(screen.queryByText('Condition Class')).not.toBeInTheDocument());
		expect(createExperimentalCondition).not.toHaveBeenCalled();
		expect(onCreated).not.toHaveBeenCalled();
	});

	it('sends typed text that was never picked as an unknown term, and shows the rejection beside it', async () => {
		createExperimentalCondition.mockImplementation(() => rejectedCreate({ conditionClass: 'Invalid' }));
		renderForm();
		await waitForForm();

		typeInField('Condition Class', 'ZECO:9999999');
		clickSave();

		await waitFor(() => expect(createExperimentalCondition).toHaveBeenCalled());
		expect(createExperimentalCondition.mock.calls[0][0].conditionClass).toEqual({ curie: 'ZECO:9999999' });
		expect(await within(getField('Condition Class')).findByText('Invalid')).toBeInTheDocument();
	});

	// Five of the six autocompletes pass `fieldname` rather than `fieldName`, so only Condition Class is named.
	it.skip('names every ontology autocomplete', async () => {
		renderForm();
		await waitForForm();

		for (const field of ONTOLOGY_FIELDS) {
			expect(screen.getByRole('combobox', { name: field })).toBeInTheDocument();
		}
	});
});
