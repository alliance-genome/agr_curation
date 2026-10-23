import { fireEvent, waitFor } from '@testing-library/react';
import { SearchService } from '../../../service/SearchService';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../constants/FilterFields';
import { getCellByHeader, renderAlleleForm } from './alleleFormTestUtils';
import { MutationTypesForm } from '../mutationTypes/MutationTypesForm';
import { FunctionalImpactsForm } from '../functionalImpacts/FunctionalImpactsForm';
import { InheritanceModesForm } from '../inheritanceModes/InheritanceModesForm';
import { SecondaryIdsForm } from '../secondaryIds/SecondaryIdsForm';
import { NomenclatureEventsForm } from '../nomenclatureEvents/NomenclatureEventsForm';
import { SynonymsForm } from '../synonyms/SynonymsForm';
import { AlleleGeneAssociationsForm } from '../alleleGeneAssociations/AlleleGeneAssociationsForm';
import { CrossReferencesForm } from '../crossReferences/CrossReferencesForm';
import { SubResourcesProvider } from '../../../components/SubResourcesContext';
import { renderWithClient } from '../../../tools/jest/utils';
import '../../../tools/jest/setupTests';

vi.mock('../../../service/SearchService');
vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');
vi.mock('../crossReferences/useResourceDescriptorsByPrefix', () => ({
	useResourceDescriptorsByPrefix: () => new Map(),
}));

// Pins the request each allele subform autocomplete sends. The backend boosts each filter group,
// and each field within a group, by its position, so key order is asserted at both levels as well
// as the contents.

const QUERY = 'abc';
const FIELD_FILTER = { queryString: QUERY, tokenOperator: 'AND', useKeywordFields: true };
const OBSOLETE = { obsolete: { queryString: false } };

const fieldGroup = (configName) =>
	Object.fromEntries(getAutocompleteFields(AUTOCOMPLETE_CONFIGS[configName]).map((field) => [field, FIELD_FILTER]));

const ROW = { internal: false, evidence: [] };

const renderSubform =
	(Form, entityType, row = {}) =>
	() =>
		renderAlleleForm(Form, { [entityType]: [{ ...ROW, ...row }] });

const subResource = (rowsName, setterName, rows) => ({
	[rowsName]: rows,
	[setterName]: vi.fn(),
	errorMessages: {},
	setErrorMessages: vi.fn(),
	isLoading: false,
	loadError: null,
	isSaving: false,
	isDirty: false,
	save: vi.fn(),
});

const renderGeneAssociations = () =>
	renderWithClient(
		<SubResourcesProvider
			value={{
				alleleGeneAssociations: subResource('alleleGeneAssociations', 'setAlleleGeneAssociations', [
					{ ...ROW, dataKey: 'k1' },
				]),
			}}
		>
			<AlleleGeneAssociationsForm />
		</SubResourcesProvider>
	);

const renderCrossReferences = () =>
	renderWithClient(
		<SubResourcesProvider
			value={{ crossReferences: subResource('crossReferences', 'setCrossReferences', [{ dataKey: 'k1' }]) }}
		>
			<CrossReferencesForm />
		</SubResourcesProvider>
	);

const EVIDENCE = {
	endpoint: 'literature-reference/document',
	groups: { evidenceFilter: fieldGroup('referenceAutocompleteConfig') },
};
const PHENOTYPE_TERM = {
	endpoint: 'phenotypeterm',
	groups: { phenotypeTermFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
};

// Each case lists its filter groups in the order the request must carry them.
const CASES = [
	{
		name: 'Mutation types',
		renderTable: renderSubform(MutationTypesForm, 'alleleMutationTypes', { mutationTypes: [] }),
		header: 'Mutation Types',
		endpoint: 'soterm',
		groups: { mutationTypeFilter: fieldGroup('ontologyTermAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Mutation types evidence',
		renderTable: renderSubform(MutationTypesForm, 'alleleMutationTypes', { mutationTypes: [] }),
		header: 'Evidence',
		...EVIDENCE,
	},
	{
		name: 'Functional impacts',
		renderTable: renderSubform(FunctionalImpactsForm, 'alleleFunctionalImpacts', { functionalImpacts: [] }),
		header: 'Functional Impacts',
		endpoint: 'vocabularyterm',
		groups: {
			functionalImpactFilter: fieldGroup('nameOnlyAutocompleteConfig'),
			vocabularyFilter: { 'vocabulary.vocabularyLabel': { queryString: 'allele_functional_impact' } },
			obsoleteFilter: OBSOLETE,
		},
	},
	{
		name: 'Functional impacts phenotype term',
		renderTable: renderSubform(FunctionalImpactsForm, 'alleleFunctionalImpacts', { functionalImpacts: [] }),
		header: 'Phenotype Term',
		...PHENOTYPE_TERM,
	},
	{
		name: 'Functional impacts evidence',
		renderTable: renderSubform(FunctionalImpactsForm, 'alleleFunctionalImpacts', { functionalImpacts: [] }),
		header: 'Evidence',
		...EVIDENCE,
	},
	{
		name: 'Inheritance modes phenotype term',
		renderTable: renderSubform(InheritanceModesForm, 'alleleInheritanceModes'),
		header: 'Phenotype Term',
		...PHENOTYPE_TERM,
	},
	{
		name: 'Inheritance modes evidence',
		renderTable: renderSubform(InheritanceModesForm, 'alleleInheritanceModes'),
		header: 'Evidence',
		...EVIDENCE,
	},
	{
		name: 'Secondary IDs evidence',
		renderTable: renderSubform(SecondaryIdsForm, 'alleleSecondaryIds'),
		header: 'Evidence',
		...EVIDENCE,
	},
	{
		name: 'Nomenclature events evidence',
		renderTable: renderSubform(NomenclatureEventsForm, 'alleleNomenclatureEvents'),
		header: 'Evidence',
		...EVIDENCE,
	},
	{
		name: 'Synonyms evidence',
		renderTable: renderSubform(SynonymsForm, 'alleleSynonyms'),
		header: 'Evidence',
		...EVIDENCE,
	},
	{
		name: 'Gene associations gene',
		renderTable: renderGeneAssociations,
		header: 'Gene',
		endpoint: 'gene',
		groups: { objectFilter: fieldGroup('geneAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
	{
		name: 'Gene associations evidence code',
		renderTable: renderGeneAssociations,
		header: 'Evidence Code',
		endpoint: 'ecoterm',
		groups: {
			evidenceFilter: fieldGroup('evidenceCodeAutocompleteConfig'),
			obsoleteFilter: OBSOLETE,
			subsetFilter: { subsets: { queryString: 'agr_eco_terms' } },
		},
	},
	{
		name: 'Gene associations evidence',
		renderTable: renderGeneAssociations,
		header: 'Evidence',
		...EVIDENCE,
	},
	{
		name: 'Cross references resource descriptor',
		renderTable: renderCrossReferences,
		header: 'Resource Descriptor',
		endpoint: 'resourcedescriptor',
		groups: { resourceDescriptorFilter: fieldGroup('resourceDescriptorAutocompleteConfig'), obsoleteFilter: OBSOLETE },
	},
];

describe('allele subform autocomplete search payloads', () => {
	beforeEach(() => {
		SearchService.mockClear();
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [] }));
	});

	it.each(CASES)(
		'$name sends its endpoint and filter groups in order',
		async ({ renderTable, header, endpoint, groups }) => {
			const { container } = renderTable();

			fireEvent.change(getCellByHeader(container, header).querySelector('input'), { target: { value: QUERY } });
			await waitFor(() => expect(SearchService.prototype.search).toHaveBeenCalled(), { timeout: 3000 });
			const [sentEndpoint, limit, page, sorts, params] = SearchService.prototype.search.mock.calls[0];

			expect([sentEndpoint, limit, page, sorts]).toEqual([endpoint, 15, 0, []]);
			expect(Object.keys(params)).toEqual(Object.keys(groups));
			Object.keys(groups).forEach((groupName) =>
				expect(Object.keys(params[groupName])).toEqual(Object.keys(groups[groupName]))
			);
			expect(params).toEqual(groups);
		}
	);
});
