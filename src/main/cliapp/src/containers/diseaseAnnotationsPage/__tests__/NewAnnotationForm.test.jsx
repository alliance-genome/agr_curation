import { useEffect } from 'react';
import { act, fireEvent, screen, waitFor, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { Endpoints } from '../../../constants/Endpoints';
import {
	getField,
	pickSuggestionInField,
	rejectedCreate,
	resolvedCreate,
	selectInField,
	typeInField,
} from '../../../tools/jest/newEntityFormTestUtils';

const createDiseaseAnnotation = vi.fn();
const findExperiments = vi.fn();

const GENE = { id: 1, type: 'Gene', primaryExternalId: 'MGI:101', geneSymbol: { displayText: 'Pax6' } };
const ALLELE = { id: 2, type: 'Allele', primaryExternalId: 'MGI:102', alleleSymbol: { displayText: 'Sey1' } };
const AGM = { id: 3, type: 'AffectedGenomicModel', primaryExternalId: 'MGI:103', agmSymbol: { displayText: 'B6J' } };
const DISEASE = { id: 10, curie: 'DOID:4', name: 'disease' };
const REFERENCE = { id: 5, type: 'LiteratureReference', curie: 'AGRKB:101000000000001', short_citation: 'Smith 2020' };
const EVIDENCE_CODE = { id: 20, curie: 'ECO:0000033', name: 'author statement', abbreviation: 'TAS' };
const STRAIN = {
	id: 30,
	type: 'AffectedGenomicModel',
	primaryExternalId: 'SGD:S000203483',
	agmSymbol: { displayText: 'S288C' },
};
const EXPERIMENT = { id: 40, handle: 'heat_shock', singleReference: REFERENCE, conditions: [] };

const RESULTS_BY_ENDPOINT = {
	[Endpoints.Entity.BIOLOGICAL_ENTITY]: [GENE, ALLELE, AGM],
	[Endpoints.Ontology.DO]: [DISEASE],
	[Endpoints.Document.LITERATURE_REFERENCE]: [REFERENCE],
	[Endpoints.Ontology.ECO]: [EVIDENCE_CODE],
	[Endpoints.Entity.AGM]: [STRAIN],
};
const search = vi.fn((endpoint) => {
	const results = RESULTS_BY_ENDPOINT[endpoint] ?? [];
	return Promise.resolve({ results, totalResults: results.length });
});

vi.mock('../../../service/DiseaseAnnotationService', () => ({
	DiseaseAnnotationService: class {
		createDiseaseAnnotation = createDiseaseAnnotation;
	},
}));

// Each autocomplete gets its own results by endpoint, so a search sent to the wrong endpoint finds nothing.
vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = search;
	},
}));

vi.mock('../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = () => Promise.resolve({ isError: false });
	},
}));

vi.mock('../../../service/ExperimentsSearchService', () => ({
	ExperimentsSearchService: class {
		findExperiments = findExperiments;
	},
}));

vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const { NewAnnotationForm } = await import('../NewAnnotationForm');
const { useNewAnnotationReducer } = await import('../useNewAnnotationReducer');
const { vocabularyTermsFor } = await import('../../../service/__mocks__/useControlledVocabularyService');
const { termSetTermsFor } = await import('../../../service/__mocks__/useVocabularyTermSetService');
const { setSpeciesTaxaCache } = await import('../../../constants/speciesTaxa');

const FORM_SETTINGS_KEY = 'DiseaseAnnotationsFormSettings';
const ALL_OPTIONAL_FIELDS = [
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
];

const signInAs = (staffGroup) =>
	localStorage.setItem(
		'cognito-token-storage',
		JSON.stringify({ accessToken: { payload: { 'cognito:groups': [staffGroup] } } })
	);

const showOnlyFields = (fields) =>
	localStorage.setItem(FORM_SETTINGS_KEY, JSON.stringify({ selectedFormFields: fields }));

/**
 * Renders the New Annotation dialog, open, against the real reducer, the way the Disease Annotations table does.
 *
 * @param {Function} [onCreated] - the table's callback for a created annotation
 * @param {object} [options]
 * @param {boolean} [options.open=true] - open the dialog on render; false leaves it for the test to open, as the
 *   table's Duplicate does after filling the form
 * @returns {object} the render result, plus `dispatch(action)` to act as the table does
 */
const renderForm = (onCreated = vi.fn(), { open = true } = {}) => {
	const handle = {};
	const Harness = () => {
		const { newAnnotationState, newAnnotationDispatch } = useNewAnnotationReducer();
		handle.dispatch = newAnnotationDispatch;
		useEffect(() => {
			if (open) newAnnotationDispatch({ type: 'OPEN_DIALOG' });
		}, [newAnnotationDispatch]);
		return (
			<NewAnnotationForm
				newAnnotationState={newAnnotationState}
				newAnnotationDispatch={newAnnotationDispatch}
				relationsTerms={vocabularyTermsFor('disease_relation')}
				setNewDiseaseAnnotation={onCreated}
			/>
		);
	};
	const result = renderWithClient(<Harness />);
	return { ...result, dispatch: (action) => act(() => handle.dispatch(action)) };
};

const waitForForm = () => screen.findByText('Add Disease Annotation');
const saveAndClose = () => fireEvent.click(screen.getByRole('button', { name: 'Save & Close' }));
const fieldLabels = () =>
	[...document.querySelectorAll('.p-dialog label, .p-dialog h2')].map((label) =>
		label.textContent.replace('*', '').trim()
	);
const relationOptions = () => {
	fireEvent.click(getField('Disease Relation').querySelector('.p-dropdown'));
	const options = [...document.querySelectorAll('.p-dropdown-item')].map((option) => option.textContent);
	fireEvent.click(getField('Disease Relation').querySelector('.p-dropdown'));
	return options;
};
const multiInput = (label) => getField(label).querySelector('input');

/**
 * Picks a suggestion, then waits for its panel to close, so no panel is left closing while the next action runs.
 *
 * @param {string} label - the field's label
 * @param {string} query - the text to search for
 * @param {string|RegExp} suggestionText - matches the suggestion to pick
 */
const pick = async (label, query, suggestionText) => {
	await pickSuggestionInField(label, query, suggestionText);
	await waitFor(() => expect(document.querySelector('.p-autocomplete-panel')).toBeNull());
};

const pickSubject = (subjectText) => pick('Subject', 'MGI', subjectText);

const fillRequiredFields = async (relationName) => {
	selectInField('Disease Relation', relationName);
	await pick('Disease', 'dis', /disease \(DOID:4\)/);
	await pick('Reference', 'Smith', /Smith 2020/);
	await pick('Evidence Code', 'TAS', /TAS - author statement/);
};

describe('<NewAnnotationForm />', () => {
	beforeEach(() => {
		localStorage.clear();
		setSpeciesTaxaCache([]);
		createDiseaseAnnotation.mockReset();
		findExperiments.mockReset();
		findExperiments.mockResolvedValue({ results: [EXPERIMENT] });
		// Drops any one-off result a test queued but didn't use
		search.mockReset();
	});

	describe('saving', () => {
		it.each([
			['a gene', GENE, /Pax6 \(MGI:101\)/, 'gene_disease_relation_alpha'],
			['an allele', ALLELE, /Sey1 \(MGI:102\)/, 'allele_disease_relation_alpha'],
			['an AGM', AGM, /B6J \(MGI:103\)/, 'agm_disease_relation_alpha'],
		])('creates an annotation with %s subject and closes', async (_type, subject, subjectText, relationName) => {
			const created = { id: 99 };
			createDiseaseAnnotation.mockImplementation(() => resolvedCreate(created));
			const onCreated = vi.fn();
			renderForm(onCreated);
			await waitForForm();

			await pickSubject(subjectText);
			await fillRequiredFields(relationName);
			saveAndClose();

			await waitFor(() => expect(onCreated).toHaveBeenCalledWith(created, expect.anything()));
			expect(createDiseaseAnnotation).toHaveBeenCalledWith(
				expect.objectContaining({
					diseaseAnnotationSubject: expect.objectContaining({ id: subject.id, type: subject.type }),
					relation: expect.objectContaining({ name: relationName }),
					diseaseAnnotationObject: expect.objectContaining({ curie: DISEASE.curie }),
					evidenceItem: expect.objectContaining({ curie: REFERENCE.curie }),
					evidenceCodes: [expect.objectContaining({ curie: EVIDENCE_CODE.curie })],
					negated: false,
					internal: false,
				})
			);
			expect(await screen.findByText('New Annotation Added')).toBeInTheDocument();
			await waitFor(() => expect(screen.queryByText('Add Disease Annotation')).not.toBeInTheDocument());
		});

		it('keeps the form open and filled on Save & Add Another', async () => {
			createDiseaseAnnotation.mockImplementation(() => resolvedCreate({ id: 99 }));
			const onCreated = vi.fn();
			renderForm(onCreated);
			await waitForForm();

			await pickSubject(/Pax6/);
			await fillRequiredFields('gene_disease_relation_alpha');
			fireEvent.click(screen.getByRole('button', { name: 'Save & Add Another' }));

			await waitFor(() => expect(onCreated).toHaveBeenCalled());
			expect(screen.getByText('Add Disease Annotation')).toBeInTheDocument();
			expect(getField('Subject').querySelector('input')).toHaveValue('MGI:101');
		});

		it('sends NOT and Internal as chosen, and NOT as false once cleared', async () => {
			createDiseaseAnnotation.mockImplementation(() => resolvedCreate({ id: 99 }));
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			await fillRequiredFields('gene_disease_relation_alpha');
			selectInField('NOT', 'NOT');
			selectInField('Internal', 'true');
			fireEvent.click(screen.getByRole('button', { name: 'Save & Add Another' }));

			await waitFor(() => expect(createDiseaseAnnotation).toHaveBeenCalledTimes(1));
			expect(createDiseaseAnnotation.mock.calls[0][0]).toEqual(
				expect.objectContaining({ negated: true, internal: true })
			);

			fireEvent.pointerUp(getField('NOT').querySelector('.p-dropdown-clear-icon'));
			fireEvent.click(screen.getByRole('button', { name: 'Save & Add Another' }));

			await waitFor(() => expect(createDiseaseAnnotation).toHaveBeenCalledTimes(2));
			expect(createDiseaseAnnotation.mock.calls[1][0].negated).toBe(false);
		});

		it('keeps Save off until a subject is given', async () => {
			renderForm();
			await waitForForm();

			expect(screen.getByRole('button', { name: 'Save & Close' })).toBeDisabled();
			expect(screen.getByRole('button', { name: 'Save & Add Another' })).toBeDisabled();
			await pickSubject(/Pax6/);
			expect(screen.getByRole('button', { name: 'Save & Close' })).toBeEnabled();
		});
	});

	describe('subject type', () => {
		it.each([
			['a gene', /Pax6/, false, false, 'gene_disease_relation'],
			['an allele', /Sey1/, true, false, 'allele_disease_relation'],
			['an AGM', /B6J/, true, true, 'agm_disease_relation'],
		])(
			'with %s subject, sets which asserted fields are open and which relations are offered',
			async (_type, subjectText, genesOpen, allelesOpen, relationTermSet) => {
				renderForm();
				await waitForForm();

				await pickSubject(subjectText);

				expect(multiInput('Asserted Genes').disabled).toBe(!genesOpen);
				expect(multiInput('Asserted Alleles').disabled).toBe(!allelesOpen);
				expect(relationOptions()).toEqual(termSetTermsFor(relationTermSet).map((term) => term.name));
			}
		);

		it("searches the curator's species' genes, alleles and AGMs for the subject", async () => {
			setSpeciesTaxaCache([{ dataProvider: { abbreviation: 'MGI' }, taxon: { curie: 'NCBITaxon:10090' } }]);
			signInAs('MGIStaff');
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);

			expect(search).toHaveBeenCalledWith(
				Endpoints.Entity.BIOLOGICAL_ENTITY,
				15,
				0,
				[],
				expect.objectContaining({
					diseaseAnnotationSubjectFilter: expect.any(Object),
					speciesFilter: {
						'taxon.curie': { queryString: 'NCBITaxon:10090', tokenOperator: 'OR', useKeywordFields: true },
					},
				})
			);
		});

		it('offers every disease relation before a subject is picked', async () => {
			renderForm();
			await waitForForm();

			expect(relationOptions()).toEqual(vocabularyTermsFor('disease_relation').map((term) => term.name));
		});
	});

	describe('errors', () => {
		it('says each empty required field is required, beside it, and sends nothing', async () => {
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			saveAndClose();

			for (const label of ['Disease Relation', 'Disease', 'Reference', 'Evidence Code']) {
				expect(await within(getField(label)).findByText('This is a required field')).toBeInTheDocument();
			}
			expect(createDiseaseAnnotation).not.toHaveBeenCalled();
		});

		it("requires Experiments for ZFIN curators, even when the field isn't selected", async () => {
			signInAs('ZFINStaff');
			showOnlyFields([]);
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			await fillRequiredFields('gene_disease_relation_alpha');
			saveAndClose();

			expect(await within(getField('Experiments')).findByText('This is a required field')).toBeInTheDocument();
			expect(createDiseaseAnnotation).not.toHaveBeenCalled();
		});

		it('shows a rejected field error beside its field and stays open', async () => {
			createDiseaseAnnotation.mockImplementation(() =>
				rejectedCreate({ diseaseAnnotationObject: 'Not a valid entry' }, 'Could not create annotation')
			);
			const onCreated = vi.fn();
			renderForm(onCreated);
			await waitForForm();

			await pickSubject(/Pax6/);
			await fillRequiredFields('gene_disease_relation_alpha');
			saveAndClose();

			expect(await within(getField('Disease')).findByText('Not a valid entry')).toBeInTheDocument();
			expect(within(getField('Reference')).queryByText('Not a valid entry')).not.toBeInTheDocument();
			expect(screen.getByText('Could not create annotation')).toBeInTheDocument();
			expect(onCreated).not.toHaveBeenCalled();
		});

		it('says an annotation duplicating an existing Unique ID is a duplicate', async () => {
			createDiseaseAnnotation.mockImplementation(() => rejectedCreate({ uniqueId: 'Non-unique value' }));
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			await fillRequiredFields('gene_disease_relation_alpha');
			saveAndClose();

			expect(
				await screen.findByText('Page Error: New annotation is a duplicate of an existing annotation')
			).toBeInTheDocument();
		});

		it('refuses a typed subject that was never picked', async () => {
			search.mockImplementationOnce(() => Promise.resolve({ results: [], totalResults: 0 }));
			renderForm();
			await waitForForm();

			typeInField('Subject', 'MGI:999');
			await fillRequiredFields('disease_relation_alpha');
			saveAndClose();

			expect(await within(getField('Subject')).findByText('Must select from autosuggest')).toBeInTheDocument();
			expect(createDiseaseAnnotation).not.toHaveBeenCalled();
		});

		it('reports a typed subject alongside the empty required fields', async () => {
			search.mockImplementationOnce(() => Promise.resolve({ results: [], totalResults: 0 }));
			renderForm();
			await waitForForm();

			typeInField('Subject', 'MGI:999');
			await waitFor(() =>
				expect(search).toHaveBeenCalledWith(
					Endpoints.Entity.BIOLOGICAL_ENTITY,
					expect.anything(),
					expect.anything(),
					expect.anything(),
					expect.any(Object)
				)
			);
			saveAndClose();

			expect(await within(getField('Subject')).findByText('Must select from autosuggest')).toBeInTheDocument();
			expect(within(getField('Disease')).getByText('This is a required field')).toBeInTheDocument();
		});

		it('refuses a typed SGD Strain Background that was never picked', async () => {
			createDiseaseAnnotation.mockImplementation(() => resolvedCreate({ id: 99 }));
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			await fillRequiredFields('gene_disease_relation_alpha');
			typeInField('SGD Strain Background', 'S288');
			saveAndClose();

			expect(
				await within(getField('SGD Strain Background')).findByText('Must select from autosuggest')
			).toBeInTheDocument();
			expect(createDiseaseAnnotation).not.toHaveBeenCalled();
		});

		it('asks for a reference picked from the suggestions when one is only typed', async () => {
			createDiseaseAnnotation.mockImplementation(() => resolvedCreate({ id: 99 }));
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			selectInField('Disease Relation', 'gene_disease_relation_alpha');
			await pick('Disease', 'dis', /disease \(DOID:4\)/);
			search.mockImplementationOnce(() => Promise.resolve({ results: [], totalResults: 0 }));
			typeInField('Reference', 'AGRKB:999');
			await waitFor(() =>
				expect(search).toHaveBeenCalledWith(
					Endpoints.Document.LITERATURE_REFERENCE,
					expect.anything(),
					expect.anything(),
					expect.anything(),
					expect.any(Object)
				)
			);
			await pick('Evidence Code', 'TAS', /TAS - author statement/);
			expect(getField('Experiments').querySelector('.p-dropdown')).toHaveClass('p-disabled');
			saveAndClose();

			expect(await within(getField('Reference')).findByText('Must select reference from dropdown')).toBeInTheDocument();
			expect(createDiseaseAnnotation).not.toHaveBeenCalled();
		});
	});

	describe('experiments', () => {
		it("offers the picked reference's experiments and sends the chosen one", async () => {
			createDiseaseAnnotation.mockImplementation(() => resolvedCreate({ id: 99 }));
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			await fillRequiredFields('gene_disease_relation_alpha');
			fireEvent.click(getField('Experiments').querySelector('.p-dropdown'));
			fireEvent.click(await screen.findByText('heat_shock', { selector: '.p-dropdown-item, .p-dropdown-item *' }));
			saveAndClose();

			await waitFor(() => expect(createDiseaseAnnotation).toHaveBeenCalled());
			expect(findExperiments).toHaveBeenCalledWith(
				Endpoints.Annotation.CONDITION_RELATION,
				expect.anything(),
				expect.anything(),
				{ 'singleReference.curie': REFERENCE.curie }
			);
			expect(createDiseaseAnnotation.mock.calls[0][0].conditionRelations).toEqual([
				expect.objectContaining({ handle: 'heat_shock' }),
			]);
		});

		it('offers no experiments from an earlier reference after Clear', async () => {
			renderForm();
			await waitForForm();

			await pick('Reference', 'Smith', /Smith 2020/);
			fireEvent.click(getField('Experiments').querySelector('.p-dropdown'));
			expect(
				await screen.findByText('heat_shock', { selector: '.p-dropdown-item, .p-dropdown-item *' })
			).toBeInTheDocument();
			fireEvent.click(getField('Experiments').querySelector('.p-dropdown'));
			await waitFor(() => expect(document.querySelector('.p-dropdown-panel')).toBeNull());
			fireEvent.click(screen.getByRole('button', { name: 'Clear' }));
			findExperiments.mockReturnValue(new Promise(() => {}));
			await pick('Reference', 'Smith', /Smith 2020/);
			fireEvent.click(getField('Experiments').querySelector('.p-dropdown'));

			expect(
				screen.queryByText('heat_shock', { selector: '.p-dropdown-item, .p-dropdown-item *' })
			).not.toBeInTheDocument();
		});

		it('keeps Experiments off until a reference is picked', async () => {
			renderForm();
			await waitForForm();

			expect(getField('Experiments').querySelector('.p-dropdown')).toHaveClass('p-disabled');
			await pick('Reference', 'Smith', /Smith 2020/);
			expect(getField('Experiments').querySelector('.p-dropdown')).not.toHaveClass('p-disabled');
		});

		it('sends no condition relation once the chosen experiment is cleared', async () => {
			createDiseaseAnnotation.mockImplementation(() => resolvedCreate({ id: 99 }));
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			await fillRequiredFields('gene_disease_relation_alpha');
			fireEvent.click(getField('Experiments').querySelector('.p-dropdown'));
			fireEvent.click(await screen.findByText('heat_shock', { selector: '.p-dropdown-item, .p-dropdown-item *' }));
			fireEvent.pointerUp(getField('Experiments').querySelector('.p-dropdown-clear-icon'));
			saveAndClose();

			await waitFor(() => expect(createDiseaseAnnotation).toHaveBeenCalled());
			expect(createDiseaseAnnotation.mock.calls[0][0].conditionRelations).toEqual([]);
		});
	});

	describe('field visibility', () => {
		it('lays each field out as a narrow label, the widget, then its additional data', async () => {
			renderForm();
			await waitForForm();

			const columns = [...getField('Disease').children].map((column) => column.className);
			expect(columns).toEqual(['col-2', 'col-4', 'col-5']);
		});

		it('shows every optional field by default', async () => {
			renderForm();
			await waitForForm();

			expect(fieldLabels()).toEqual(expect.arrayContaining(ALL_OPTIONAL_FIELDS));
		});

		it("shows only the curator's selected optional fields, and every field on Show all fields", async () => {
			showOnlyFields(['With', 'Internal']);
			renderForm();
			await waitForForm();

			expect(fieldLabels()).toEqual(
				expect.arrayContaining([
					'Subject',
					'Disease Relation',
					'Disease',
					'Reference',
					'Evidence Code',
					'With',
					'Internal',
				])
			);
			expect(fieldLabels()).not.toContain('Genetic Sex');
			expect(fieldLabels()).not.toContain('Asserted Genes');

			fireEvent.click(screen.getByRole('button', { name: 'Show all fields' }));

			await waitFor(() => expect(fieldLabels()).toEqual(expect.arrayContaining(ALL_OPTIONAL_FIELDS)));
		});

		it("shows the MOD's default fields on Set to MOD Defaults", async () => {
			signInAs('SGDStaff');
			showOnlyFields(['Genetic Sex']);
			renderForm();
			await waitForForm();

			fireEvent.click(screen.getByRole('button', { name: 'Set to MOD Defaults' }));
			fireEvent.click(await screen.findByRole('button', { name: 'Yes' }));

			await waitFor(() =>
				expect(fieldLabels()).toEqual(expect.arrayContaining(['With', 'SGD Strain Background', 'Annotation Type']))
			);
			expect(fieldLabels()).not.toContain('Genetic Sex');
		});
	});

	describe('duplicate and clear', () => {
		const DUPLICATED_ROW = {
			type: 'AlleleDiseaseAnnotation',
			diseaseAnnotationSubject: ALLELE,
			assertedGenes: [GENE],
			relation: { id: 801, name: 'allele_disease_relation_beta' },
			negated: true,
			diseaseAnnotationObject: DISEASE,
			evidenceItem: REFERENCE,
			evidenceCodes: [EVIDENCE_CODE],
			with: [GENE],
			relatedNotes: [{ id: 61, noteType: vocabularyTermsFor('disease_note_type')[0], freeText: 'copied note' }],
			conditionRelations: [EXPERIMENT],
			geneticSex: vocabularyTermsFor('genetic_sex')[0],
			diseaseQualifiers: [vocabularyTermsFor('disease_qualifier')[1]],
			sgdStrainBackground: STRAIN,
			annotationType: vocabularyTermsFor('annotation_type')[0],
			diseaseGeneticModifierRelation: vocabularyTermsFor('disease_genetic_modifier_relation')[0],
			diseaseGeneticModifierAgms: [AGM],
			diseaseGeneticModifierAlleles: [ALLELE],
			diseaseGeneticModifierGenes: [GENE],
			internal: true,
		};

		// Dispatches what the table's Duplicate does, ending with opening the dialog.
		const duplicate = (dispatch) => {
			dispatch({ type: 'DUPLICATE_ROW', rowData: DUPLICATED_ROW });
			dispatch({ type: 'SET_IS_ENABLED', value: true });
			dispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: true });
			dispatch({ type: 'SET_RELATED_NOTES_EDITING_ROWS' });
			dispatch({ type: 'SET_CONDITION_RELATIONS_EDITING_ROWS' });
			dispatch({ type: 'OPEN_DIALOG' });
		};

		const dropdownLabel = (label) => getField(label).querySelector('.p-dropdown-label').textContent;

		it('pre-fills the form from the duplicated annotation and sends its values', async () => {
			createDiseaseAnnotation.mockImplementation(() => resolvedCreate({ id: 99 }));
			const { dispatch } = renderForm(vi.fn(), { open: false });

			duplicate(dispatch);
			await waitForForm();

			expect(getField('Subject').querySelector('input')).toHaveValue('MGI:102');
			expect(multiInput('Asserted Genes').disabled).toBe(false);
			expect(dropdownLabel('Disease Relation')).toBe('allele_disease_relation_beta');
			expect(dropdownLabel('NOT')).toBe('NOT');
			expect(dropdownLabel('Experiments')).toBe('heat_shock');
			expect(dropdownLabel('Genetic Sex')).toBe('genetic_sex_alpha');
			expect(dropdownLabel('Annotation Type')).toBe('annotation_type_alpha');
			expect(dropdownLabel('Genetic Modifier Relation')).toBe('disease_genetic_modifier_relation_alpha');
			expect(dropdownLabel('Internal')).toBe('true');
			expect(getField('Disease Qualifiers').textContent).toContain('disease_qualifier_beta');
			saveAndClose();

			await waitFor(() => expect(createDiseaseAnnotation).toHaveBeenCalled());
			expect(createDiseaseAnnotation.mock.calls[0][0]).toEqual(
				expect.objectContaining({
					diseaseAnnotationSubject: expect.objectContaining({ id: ALLELE.id }),
					relation: expect.objectContaining({ name: 'allele_disease_relation_beta' }),
					negated: true,
					diseaseAnnotationObject: expect.objectContaining({ curie: DISEASE.curie }),
					evidenceItem: expect.objectContaining({ curie: REFERENCE.curie }),
					evidenceCodes: [expect.objectContaining({ curie: EVIDENCE_CODE.curie })],
					geneticSex: expect.objectContaining({ name: 'genetic_sex_alpha' }),
					internal: true,
					assertedGenes: [expect.objectContaining({ id: GENE.id })],
					with: [expect.objectContaining({ id: GENE.id })],
					relatedNotes: [expect.objectContaining({ freeText: 'copied note' })],
					conditionRelations: [expect.objectContaining({ handle: 'heat_shock' })],
					diseaseQualifiers: [expect.objectContaining({ name: 'disease_qualifier_beta' })],
					sgdStrainBackground: expect.objectContaining({ id: STRAIN.id }),
					annotationType: expect.objectContaining({ name: 'annotation_type_alpha' }),
					diseaseGeneticModifierRelation: expect.objectContaining({
						name: 'disease_genetic_modifier_relation_alpha',
					}),
					diseaseGeneticModifierAgms: [expect.objectContaining({ id: AGM.id })],
					diseaseGeneticModifierAlleles: [expect.objectContaining({ id: ALLELE.id })],
					diseaseGeneticModifierGenes: [expect.objectContaining({ id: GENE.id })],
				})
			);
			expect(createDiseaseAnnotation.mock.calls[0][0].relatedNotes[0].id).toBeUndefined();
		});

		it("offers the duplicated subject's relations", async () => {
			const { dispatch } = renderForm(vi.fn(), { open: false });

			duplicate(dispatch);
			await waitForForm();

			expect(relationOptions()).toEqual(termSetTermsFor('allele_disease_relation').map((term) => term.name));
		});

		it('empties the form on Clear, including typed text, and offers every relation again', async () => {
			renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			await pick('Disease', 'dis', /disease \(DOID:4\)/);
			search.mockImplementationOnce(() => Promise.resolve({ results: [], totalResults: 0 }));
			typeInField('Evidence Code', 'TA');
			await waitFor(() => expect(search).toHaveBeenCalledWith(Endpoints.Ontology.ECO, 15, 0, [], expect.any(Object)));
			fireEvent.click(screen.getByRole('button', { name: 'Clear' }));

			await waitFor(() => expect(getField('Subject').querySelector('input')).toHaveValue(''));
			expect(getField('Disease').querySelector('input')).toHaveValue('');
			expect(multiInput('Evidence Code')).toHaveValue('');
			expect(screen.getByRole('button', { name: 'Save & Close' })).toBeDisabled();
			expect(relationOptions()).toEqual(vocabularyTermsFor('disease_relation').map((term) => term.name));
		});

		it('starts empty, offering every relation, when reopened after Cancel', async () => {
			const { dispatch } = renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			fireEvent.click(screen.getByRole('button', { name: 'Cancel' }));
			await waitFor(() => expect(screen.queryByText('Add Disease Annotation')).not.toBeInTheDocument());
			dispatch({ type: 'OPEN_DIALOG' });
			await waitForForm();

			expect(getField('Subject').querySelector('input')).toHaveValue('');
			expect(relationOptions()).toEqual(vocabularyTermsFor('disease_relation').map((term) => term.name));
		});

		it('offers every relation when reopened after Save & Close', async () => {
			createDiseaseAnnotation.mockImplementation(() => resolvedCreate({ id: 99 }));
			const { dispatch } = renderForm();
			await waitForForm();

			await pickSubject(/Pax6/);
			await fillRequiredFields('gene_disease_relation_alpha');
			saveAndClose();
			await waitFor(() => expect(screen.queryByText('Add Disease Annotation')).not.toBeInTheDocument());
			dispatch({ type: 'OPEN_DIALOG' });
			await waitForForm();

			expect(relationOptions()).toEqual(vocabularyTermsFor('disease_relation').map((term) => term.name));
		});
	});

	it('names every field by its key', async () => {
		renderForm();
		await waitForForm();

		for (const fieldKey of [
			'diseaseAnnotationSubject',
			'assertedGenes',
			'assertedAlleles',
			'relation',
			'negated',
			'diseaseAnnotationObject',
			'evidenceItem',
			'evidenceCodes',
			'with',
			'sgdStrainBackground',
			'diseaseGeneticModifierAgms',
			'diseaseGeneticModifierAlleles',
			'diseaseGeneticModifierGenes',
			'experiments',
			'geneticSex',
			'diseaseQualifiers',
			'annotationType',
			'diseaseGeneticModifierRelation',
			'internal',
		]) {
			expect(screen.getAllByLabelText(fieldKey).length).toBeGreaterThan(0);
		}
	});
});
