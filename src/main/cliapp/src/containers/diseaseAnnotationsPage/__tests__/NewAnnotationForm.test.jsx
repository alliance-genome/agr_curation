import { fireEvent, screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { NewAnnotationForm } from '../NewAnnotationForm';

/**
 * CHARACTERIZATION — the new disease annotation form's field wiring.
 *
 * Every field here shares a handful of change handlers, and those handlers decide which
 * field to write by reading `event.target.name` off the PrimeReact event. That is the
 * contract these tests pin: the widget layer this form has yet to adopt hands over a bare
 * value with no name attached, so a swap has to replace the shared handlers with per-field
 * ones. Without these assertions, a swap that wired every column to the same field name —
 * or to none — would still render perfectly.
 *
 * Also pinned: the subject's entity type drives which relation terms are offered and which
 * dependent fields unlock. That logic reads `event.target.value.type` and is the most
 * intricate thing in the file.
 */

// Vocabulary services come from src/service/__mocks__/ — factory-less vi.mock picks up the
// manual mocks, which keep the two services distinguishable on purpose.
vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');
// The array is inline, not a reference: vi.mock factories are hoisted above every const in
// this file, so naming one here would read it in its temporal dead zone. Every optional field
// is on so that a field is never missing merely because of saved settings.
vi.mock('../../../service/useGetUserSettings', () => ({
	useGetUserSettings: () => ({
		settings: {
			selectedFormFields: [
				'Asserted Genes',
				'Asserted Alleles',
				'NOT',
				'With',
				'Related Notes',
				'Experimental Conditions',
				'Experiments',
				'Genetic Sex',
				'Disease Qualifiers',
				'SGD Strain Background',
				'Annotation Type',
				'Genetic Modifier Relation',
				'Genetic Modifier AGMs',
				'Genetic Modifier Alleles',
				'Genetic Modifier Genes',
				'Internal',
			],
		},
		mutate: vi.fn(),
	}),
}));

const RELATIONS_TERMS = [
	{ id: 1, name: 'is_implicated_in' },
	{ id: 2, name: 'is_marker_for' },
];

const DEFAULT_ANNOTATION = {
	diseaseAnnotationSubject: { primaryExternalId: '' },
	assertedGenes: [],
	assertedAlleles: [],
	relation: { name: '' },
	negated: false,
	diseaseAnnotationObject: { curie: '' },
	evidenceItem: { curie: '' },
	evidenceCodes: [],
	with: [],
	relatedNotes: [],
	conditionRelations: [],
	geneticSex: null,
	diseaseQualifiers: null,
	sgdStrainBackground: null,
	annotationType: null,
	diseaseGeneticModifierRelation: null,
	diseaseGeneticModifierAgm: [],
	diseaseGeneticModifierAlleles: [],
	diseaseGeneticModifierGenes: [],
	internal: false,
};

const makeState = ({ annotation = {}, errorMessages = {} } = {}) => ({
	newAnnotation: { ...structuredClone(DEFAULT_ANNOTATION), ...annotation },
	errorMessages,
	relatedNotesErrorMessages: [],
	relatedNotesEditingRows: {},
	exConErrorMessages: [],
	conditionRelationsEditingRows: {},
	submitted: false,
	newAnnotationDialog: true,
	showRelatedNotes: false,
	showConditionRelations: false,
	isEnabled: true,
	isAssertedGeneEnabled: true,
	isAssertedAlleleEnabled: true,
});

const renderForm = ({ state = makeState(), suggestions = [] } = {}) => {
	const dispatch = vi.fn();
	// searchService is injected, so autocompletes need no network at all.
	const searchService = { search: vi.fn(async () => ({ results: suggestions })) };
	const diseaseAnnotationService = {
		createDiseaseAnnotation: vi.fn(async (annotation) => ({ data: { entity: annotation } })),
	};
	// renderWithClient supplies the QueryClientProvider the form's useMutation needs.
	const result = renderWithClient(
		<NewAnnotationForm
			newAnnotationState={state}
			newAnnotationDispatch={dispatch}
			searchService={searchService}
			diseaseAnnotationService={diseaseAnnotationService}
			relationsTerms={RELATIONS_TERMS}
			setNewDiseaseAnnotation={vi.fn()}
		/>
	);
	return { ...result, dispatch, searchService, diseaseAnnotationService };
};

/**
 * The field block whose label matches, so fields are addressed the way a curator sees them.
 *
 * Scoped to the LAST dialog in the document rather than the whole document: PrimeReact's
 * Dialog portals itself onto document.body, outside the container Testing Library cleans up,
 * so earlier tests leave detached dialogs behind. A document-wide query finds their labels
 * first and reads empty text off them.
 */
const getFieldByLabel = (label) => {
	const scope = [...document.querySelectorAll('.p-dialog')].at(-1) ?? document.body;
	const strip = (text) => text.trim().replace(/^\*/, '');
	const labelNode = [...scope.querySelectorAll('label')].find((l) => strip(l.textContent) === label);
	if (!labelNode) {
		const seen = [...scope.querySelectorAll('label')].map((l) => `"${strip(l.textContent)}"`).join(', ');
		throw new Error(`No field labelled "${label}". Saw: ${seen}`);
	}
	return labelNode.closest('.grid') ?? labelNode.parentElement.parentElement;
};

/** Suggestions render the entity's identifier, not its name — see the autocomplete template. */
const pickSuggestion = (identifier) => {
	const panel = [...document.querySelectorAll('.p-autocomplete-panel')].at(-1);
	const option = [...(panel?.querySelectorAll('li') ?? [])].find((li) => li.textContent.includes(identifier));
	if (!option) {
		const seen = [...(panel?.querySelectorAll('li') ?? [])].map((li) => `"${li.textContent.trim()}"`).join(', ');
		throw new Error(`No suggestion matching "${identifier}". Saw: ${seen}`);
	}
	fireEvent.click(option);
};

const editCalls = (dispatch, field) =>
	dispatch.mock.calls.map(([action]) => action).filter((a) => a.type === 'EDIT' && a.field === field);

describe('<NewAnnotationForm />', () => {
	it('renders the dialog with its required fields', () => {
		renderForm();

		expect(screen.getByText('Add Disease Annotation')).toBeTruthy();
		for (const label of ['Subject', 'Disease Relation', 'Disease', 'Reference']) {
			expect(getFieldByLabel(label)).toBeTruthy();
		}
	});

	// Dropdowns are wired to the shared onDropdownFieldChange, which routes by
	// event.target.name. A per-field name is the only thing keeping these apart.
	it('dispatches EDIT under the field name the dropdown carries', () => {
		const { dispatch } = renderForm();

		const dropdown = getFieldByLabel('Disease Relation').querySelector('.p-dropdown');
		fireEvent.click(dropdown);
		fireEvent.click(screen.getByText('is_implicated_in', { selector: '.p-dropdown-item, .p-dropdown-item *' }));

		expect(editCalls(dispatch, 'relation')).toHaveLength(1);
		expect(editCalls(dispatch, 'relation')[0].value).toMatchObject({ name: 'is_implicated_in' });
	});

	/*
	 * A second and third dropdown, deliberately: `relation`, `geneticSex` and `internal` all
	 * route through the same two shared handlers, which pick the field from
	 * `event.target.name`. With only one dropdown asserted, hardcoding a single field name in
	 * a shared handler passes — so these are what actually pin the routing.
	 */
	it.each([
		['Genetic Sex', 'geneticSex', 'vocab_alpha'],
		['Internal', 'internal', 'true'],
	])('dispatches EDIT for %s under its own field name', (label, field, optionLabel) => {
		const { dispatch } = renderForm();

		fireEvent.click(getFieldByLabel(label).querySelector('.p-dropdown'));
		fireEvent.click(screen.getByText(optionLabel, { selector: '.p-dropdown-item, .p-dropdown-item *' }));

		expect(editCalls(dispatch, field)).toHaveLength(1);
		// Nothing else may be written by the same click.
		const otherFields = dispatch.mock.calls
			.map(([action]) => action)
			.filter((action) => action.type === 'EDIT' && action.field !== field)
			.map((action) => action.field);
		expect(otherFields).toEqual([]);
	});

	it('dispatches EDIT for the subject under its own field name', async () => {
		const subject = { type: 'Gene', primaryExternalId: 'HGNC:1100', curie: 'HGNC:1100', name: 'BRCA1' };
		const { dispatch } = renderForm({ suggestions: [subject] });

		fireEvent.change(getFieldByLabel('Subject').querySelector('input'), { target: { value: 'BRCA1' } });
		await waitFor(() => expect(document.querySelector('.p-autocomplete-panel li')).toBeTruthy());
		pickSuggestion('HGNC:1100');

		// Typing dispatches as well — onSubjectChange runs on every change, so the raw string
		// lands before the picked entity does. What matters is which value survives.
		await waitFor(() => expect(editCalls(dispatch, 'diseaseAnnotationSubject').length).toBeGreaterThan(0));
		expect(editCalls(dispatch, 'diseaseAnnotationSubject').at(-1).value).toMatchObject({ curie: 'HGNC:1100' });
	});

	/*
	 * The subject's type unlocks dependent fields. A Gene subject enables the annotation but
	 * leaves the asserted-gene and asserted-allele fields disabled; those are only for Allele
	 * and AffectedGenomicModel subjects. All three decisions read the same event.
	 */
	it.each([
		['Gene', { asserted_gene: false, asserted_allele: false }],
		['Allele', { asserted_gene: true, asserted_allele: false }],
		['AffectedGenomicModel', { asserted_gene: true, asserted_allele: true }],
	])('unlocks the right dependent fields for a %s subject', async (type, expected) => {
		const subject = { type, primaryExternalId: 'X:1', curie: 'X:1', name: 'subject-one' };
		const { dispatch } = renderForm({ suggestions: [subject] });

		fireEvent.change(getFieldByLabel('Subject').querySelector('input'), { target: { value: 'subject' } });
		await waitFor(() => expect(document.querySelector('.p-autocomplete-panel li')).toBeTruthy());
		pickSuggestion('X:1');

		const sent = (actionType) => dispatch.mock.calls.map(([a]) => a).filter((a) => a.type === actionType);
		await waitFor(() => expect(sent('SET_IS_ENABLED')).not.toHaveLength(0));
		expect(sent('SET_IS_ENABLED').at(-1).value).toBe(true);
		expect(sent('SET_IS_ASSERTED_GENE_ENABLED').at(-1).value).toBe(expected.asserted_gene);
		expect(sent('SET_IS_ASSERTED_ALLELE_ENABLED').at(-1).value).toBe(expected.asserted_allele);
	});

	// Two error sources render per field: one from the server, one from client-side checks.
	// Whichever renders them must keep both, and keep them on the field they name.
	it('renders a server error against the field it names', () => {
		renderForm({ state: makeState({ errorMessages: { relation: 'relation is required' } }) });

		expect(getFieldByLabel('Disease Relation').textContent).toContain('relation is required');
		expect(getFieldByLabel('Disease').textContent).not.toContain('relation is required');
	});

	it('submits the annotation it was given', async () => {
		// Every field validateRequiredFields checks has to be non-empty or submit bails before
		// the service is ever called, and the subject needs a curie to look autosuggest-picked.
		const annotation = {
			diseaseAnnotationSubject: { type: 'Gene', curie: 'HGNC:1100' },
			relation: { name: 'is_implicated_in' },
			diseaseAnnotationObject: { curie: 'DOID:162' },
			evidenceItem: { curie: 'AGRKB:1' },
			evidenceCodes: [{ curie: 'ECO:0000315' }],
		};
		const { diseaseAnnotationService } = renderForm({ state: makeState({ annotation }) });

		fireEvent.click(screen.getByRole('button', { name: /Save & Close/i }));

		await waitFor(() => expect(diseaseAnnotationService.createDiseaseAnnotation).toHaveBeenCalled());
		const submitted = diseaseAnnotationService.createDiseaseAnnotation.mock.calls[0][0];
		expect(submitted).toMatchObject({
			relation: { name: 'is_implicated_in' },
			diseaseAnnotationObject: { curie: 'DOID:162' },
		});
	});
});
