import { fireEvent, waitFor } from '@testing-library/react';
import { makeEditorOptions, renderInTable, emptyErrorMessagesRef } from '../../__tests__/editorTestUtils';
import { SearchService } from '../../../../service/SearchService';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';
import { setSpeciesTaxaCache } from '../../../../constants/speciesTaxa';
import { TaxonTableEditor } from '../taxon/TaxonTableEditor';
import { SingleReferenceTableEditor } from '../references/SingleReferenceTableEditor';
import { VocabularyTableEditor } from '../vocabulary/VocabularyTableEditor';
import { VariantTypeTableEditor } from '../variantType/VariantTypeTableEditor';
import { SourceGeneralConsequenceTableEditor } from '../sourceGeneralConsequence/SourceGeneralConsequenceTableEditor';
import { SgdStrainBackgroundTableEditor } from '../agm/SgdStrainBackgroundTableEditor';
import { ResourceDescriptorTableEditor } from '../resourceDescriptor/ResourceDescriptorTableEditor';
import { InCollectionTableEditor } from '../inCollection/InCollectionTableEditor';
import { DiseaseTableEditor } from '../ontology/DiseaseTableEditor';
import { BiologicalEntityTableEditor } from '../biologicalEntity/BiologicalEntityTableEditor';
import { ConditionClassTableEditor } from '../ontology/ConditionClassTableEditor';
import { ConditionIdTableEditor } from '../ontology/ConditionIdTableEditor';
import { ConditionGeneOntologyTableEditor } from '../ontology/ConditionGeneOntologyTableEditor';
import { ConditionChemicalTableEditor } from '../ontology/ConditionChemicalTableEditor';
import { ConditionAnatomyTableEditor } from '../ontology/ConditionAnatomyTableEditor';
import { ConditionTaxonTableEditor } from '../ontology/ConditionTaxonTableEditor';
import { WithTableEditor } from '../gene/WithTableEditor';
import { ReferencesTableEditor } from '../references/ReferencesTableEditor';
import { MemberTermsTableEditor } from '../vocabularyTerm/MemberTermsTableEditor';
import { EvidenceCodesTableEditor } from '../ontology/EvidenceCodesTableEditor';
import { DiseaseGeneticModifierGenesTableEditor } from '../gene/DiseaseGeneticModifierGenesTableEditor';
import { DiseaseGeneticModifierAllelesTableEditor } from '../allele/DiseaseGeneticModifierAllelesTableEditor';
import { DiseaseGeneticModifierAgmsTableEditor } from '../agm/DiseaseGeneticModifierAgmsTableEditor';
import { ConditionsTableEditor } from '../experimentalCondition/ConditionsTableEditor';
import { AssertedGenesTableEditor } from '../gene/AssertedGenesTableEditor';
import { AssertedAllelesTableEditor } from '../allele/AssertedAllelesTableEditor';
import '../../../../tools/jest/setupTests';

vi.mock('../../../../service/SearchService');

// Pins the request each table autocomplete column sends. The backend boosts each
// filter group, and each field within a group, by its position, so key order is
// asserted at both levels as well as the contents.

const QUERY = 'abc';
const FIELD_FILTER = { queryString: QUERY, tokenOperator: 'AND', useKeywordFields: true };
const OBSOLETE = { obsolete: { queryString: false } };
const CURATOR_TAXON = 'NCBITaxon:6239';
const SPECIES_LIST = [
	{ dataProvider: { abbreviation: 'WB' }, taxon: { curie: CURATOR_TAXON } },
	{ dataProvider: { abbreviation: 'MGI' }, taxon: { curie: 'NCBITaxon:10090' } },
];
const SPECIES_FILTER = { 'taxon.curie': { queryString: CURATOR_TAXON, tokenOperator: 'OR', useKeywordFields: true } };

const fieldGroup = (configName) =>
	Object.fromEntries(getAutocompleteFields(AUTOCOMPLETE_CONFIGS[configName]).map((field) => [field, FIELD_FILTER]));

// Each case lists its filter groups in the order the request must carry them.
const CASES = [
	{
		name: 'Taxon',
		Editor: TaxonTableEditor,
		endpoint: 'ncbitaxonterm',
		groups: { taxonFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Single reference',
		Editor: SingleReferenceTableEditor,
		endpoint: 'literature-reference/document',
		groups: { singleReferenceFilter: fieldGroup('referenceAutocompleteConfig') },
	},
	{
		name: 'Vocabulary',
		Editor: VocabularyTableEditor,
		endpoint: 'vocabulary',
		groups: { vocabularyFilter: fieldGroup('nameOnlyAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Variant type',
		Editor: VariantTypeTableEditor,
		endpoint: 'soterm',
		groups: { variantTypeFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Source general consequence',
		Editor: SourceGeneralConsequenceTableEditor,
		endpoint: 'soterm',
		groups: {
			sourceGeneralConsequenceFilter: fieldGroup('ontologyTermAutocompleteConfig'),
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'SGD strain background',
		Editor: SgdStrainBackgroundTableEditor,
		row: { type: 'GeneDiseaseAnnotation' },
		endpoint: 'agm',
		groups: {
			sgdStrainBackgroundFilter: fieldGroup('biologicalEntityAutocompleteConfig'),
			taxonFilter: { 'taxon.curie': { queryString: 'NCBITaxon:559292', useKeywordFields: true } },
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Resource descriptor',
		Editor: ResourceDescriptorTableEditor,
		endpoint: 'resourcedescriptor',
		groups: {
			resourceDescriptorFilter: fieldGroup('resourceDescriptorAutocompleteConfig'),
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'In collection',
		Editor: InCollectionTableEditor,
		endpoint: 'vocabularyterm',
		groups: {
			inCollectionFilter: fieldGroup('nameOnlyAutocompleteConfig'),
			vocabularyFilter: { 'vocabulary.vocabularyLabel': { queryString: 'allele_collection' } },
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Disease',
		Editor: DiseaseTableEditor,
		endpoint: 'doterm',
		groups: { diseaseFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	...[
		['gene', 'GeneDiseaseAnnotation', 'gene'],
		['allele', 'AlleleDiseaseAnnotation', 'allele'],
		['AGM', 'AGMDiseaseAnnotation', 'agm'],
		['untyped', undefined, 'biologicalentity'],
	].map(([label, type, endpoint]) => ({
		name: `Subject on ${label} annotations`,
		Editor: BiologicalEntityTableEditor,
		row: { type },
		endpoint,
		speciesFiltered: true,
		groups: {
			diseaseAnnotationSubjectFilter: fieldGroup('biologicalEntityAutocompleteConfig'),
			speciesFilter: SPECIES_FILTER,
			obsoleteFilter: OBSOLETE,
		},
	})),
	{
		name: 'Condition class',
		Editor: ConditionClassTableEditor,
		endpoint: 'zecoterm',
		groups: {
			conditionClassFilter: fieldGroup('ontologyTermAutocompleteConfig'),
			subsetFilter: { subsets: { queryString: 'ZECO_0000267' } },
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Condition term',
		Editor: ConditionIdTableEditor,
		endpoint: 'experimentalconditionontologyterm',
		groups: { conditionIdFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Condition gene ontology',
		Editor: ConditionGeneOntologyTableEditor,
		endpoint: 'goterm',
		groups: { conditionGeneOntologyFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Condition chemical',
		Editor: ConditionChemicalTableEditor,
		endpoint: 'chemicalterm',
		groups: { conditionChemicalFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Condition anatomy',
		Editor: ConditionAnatomyTableEditor,
		endpoint: 'anatomicalterm',
		groups: { conditionAnatomyFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Condition taxon',
		Editor: ConditionTaxonTableEditor,
		endpoint: 'ncbitaxonterm',
		groups: { conditionTaxonFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'With',
		Editor: WithTableEditor,
		endpoint: 'gene',
		groups: {
			withFilter: fieldGroup('geneAutocompleteConfig'),
			taxonFilter: { 'taxon.curie': { queryString: 'NCBITaxon:9606' } },
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'References',
		Editor: ReferencesTableEditor,
		endpoint: 'literature-reference/document',
		groups: { multiReferenceFilter: fieldGroup('referenceAutocompleteConfig') },
	},
	{
		name: 'Member terms with no vocabulary on the row',
		Editor: MemberTermsTableEditor,
		endpoint: 'vocabularyterm',
		groups: { memberTermsFilter: fieldGroup('nameOnlyAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Member terms with a vocabulary on the row',
		Editor: MemberTermsTableEditor,
		row: { vocabularyTermSetVocabulary: { name: 'Disease qualifier' } },
		endpoint: 'vocabularyterm',
		groups: {
			memberTermsFilter: fieldGroup('nameOnlyAutocompleteConfig'),
			vocabularyFilter: { 'vocabulary.name': { queryString: 'Disease qualifier' } },
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Evidence codes',
		Editor: EvidenceCodesTableEditor,
		endpoint: 'ecoterm',
		groups: {
			evidenceFilter: fieldGroup('evidenceCodeAutocompleteConfig'),
			obsoleteFilter: OBSOLETE,
			subsetFilter: { subsets: { queryString: 'agr_eco_terms' } },
		},
	},
	{
		name: 'Genetic modifier genes',
		Editor: DiseaseGeneticModifierGenesTableEditor,
		endpoint: 'gene',
		speciesFiltered: true,
		groups: {
			geneticModifierGenesFilter: fieldGroup('geneAutocompleteConfig'),
			speciesFilter: SPECIES_FILTER,
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Genetic modifier alleles',
		Editor: DiseaseGeneticModifierAllelesTableEditor,
		endpoint: 'allele',
		speciesFiltered: true,
		groups: {
			geneticModifierAllelesFilter: fieldGroup('biologicalEntityAutocompleteConfig'),
			speciesFilter: SPECIES_FILTER,
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Genetic modifier AGMs',
		Editor: DiseaseGeneticModifierAgmsTableEditor,
		endpoint: 'agm',
		speciesFiltered: true,
		groups: {
			geneticModifierAgmsFilter: fieldGroup('biologicalEntityAutocompleteConfig'),
			speciesFilter: SPECIES_FILTER,
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Conditions',
		Editor: ConditionsTableEditor,
		endpoint: 'experimental-condition',
		groups: {
			experimentalConditionFilter: fieldGroup('experimentalConditionAutocompleteConfig'),
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Asserted genes',
		Editor: AssertedGenesTableEditor,
		row: { type: 'AGMDiseaseAnnotation' },
		endpoint: 'gene',
		speciesFiltered: true,
		groups: {
			assertedGenesFilter: fieldGroup('assertedGenesAutocompleteConfig'),
			speciesFilter: SPECIES_FILTER,
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Asserted alleles',
		Editor: AssertedAllelesTableEditor,
		row: { type: 'AGMDiseaseAnnotation' },
		endpoint: 'allele',
		speciesFiltered: true,
		groups: {
			assertedAllelesFilter: fieldGroup('biologicalEntityAutocompleteConfig'),
			speciesFilter: SPECIES_FILTER,
			obsoleteFilter: OBSOLETE,
		},
	},
];

const setCuratorGroups = (groups) =>
	localStorage.setItem(
		'cognito-token-storage',
		JSON.stringify({ accessToken: { payload: { 'cognito:groups': groups } } })
	);

const searchParamsFor = async (Editor, row = {}) => {
	const result = renderInTable(
		<Editor editorOptions={makeEditorOptions(row)} errorMessagesRef={emptyErrorMessagesRef} />
	);
	fireEvent.change(result.container.querySelector('input'), { target: { value: QUERY } });
	await waitFor(() => expect(SearchService.prototype.search).toHaveBeenCalled(), { timeout: 3000 });
	return SearchService.prototype.search.mock.calls[0];
};

describe('table autocomplete search payloads', () => {
	beforeEach(() => {
		SearchService.mockClear();
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [] }));
		setSpeciesTaxaCache(SPECIES_LIST);
		setCuratorGroups(['WBStaff']);
	});

	afterEach(() => localStorage.removeItem('cognito-token-storage'));

	it.each(CASES)('$name sends its endpoint and filter groups in order', async ({ Editor, row, endpoint, groups }) => {
		const [sentEndpoint, limit, page, sorts, params] = await searchParamsFor(Editor, row);

		expect([sentEndpoint, limit, page, sorts]).toEqual([endpoint, 15, 0, []]);
		expect(Object.keys(params)).toEqual(Object.keys(groups));
		Object.keys(groups).forEach((groupName) =>
			expect(Object.keys(params[groupName])).toEqual(Object.keys(groups[groupName]))
		);
		expect(params).toEqual(groups);
	});

	it.each(CASES.filter((testCase) => testCase.speciesFiltered))(
		'$name drops the species filter until the species list has loaded',
		async ({ Editor, row, groups }) => {
			setSpeciesTaxaCache([]);

			const params = (await searchParamsFor(Editor, row))[4];

			expect(Object.keys(params)).toEqual(Object.keys(groups).filter((group) => group !== 'speciesFilter'));
		}
	);
});
