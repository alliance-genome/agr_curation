import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../FilterFields';

// SCRUM-6220: autocomplete configs that target single-type entity endpoints
// (/gene, /allele, /agm) must only reference fields that exist on every
// BiologicalEntity subtype index — i.e. native fields (curie, primaryExternalId,
// modInternalId, crossReferences.*) and the bridged flat fields (name, symbol,
// synonyms, secondaryIds). Per-type slot-annotation paths (geneSymbol.*,
// geneSystematicName.*, alleleSymbol.*, agmFullName.*, ...) only exist on one
// type's index; sending them to another type's index throws a Hibernate Search
// "Unknown field" error, which made the allele/AGM autocomplete fields return
// nothing.
const PER_TYPE_SLOT_PATH = /^(gene|allele|agm)[A-Z]/;

describe('AUTOCOMPLETE_CONFIGS index validity (SCRUM-6220)', () => {
	it('biologicalEntityAutocompleteConfig has no per-type slot paths (safe for /gene, /allele, /agm)', () => {
		const fields = getAutocompleteFields(AUTOCOMPLETE_CONFIGS.biologicalEntityAutocompleteConfig);
		expect(fields.filter((f) => PER_TYPE_SLOT_PATH.test(f))).toEqual([]);
	});

	it('assertedGenesAutocompleteConfig has no per-type slot paths', () => {
		const fields = getAutocompleteFields(AUTOCOMPLETE_CONFIGS.assertedGenesAutocompleteConfig);
		expect(fields.filter((f) => PER_TYPE_SLOT_PATH.test(f))).toEqual([]);
	});

	it('geneAutocompleteConfig keeps geneSystematicName (Gene-only; used only against /gene)', () => {
		const fields = getAutocompleteFields(AUTOCOMPLETE_CONFIGS.geneAutocompleteConfig);
		expect(fields).toContain('geneSystematicName.displayText');
	});

	// Ontology-term indexes embed crossReferences at includeDepth=1, so the depth-2
	// path crossReferences.resourceDescriptorPage.name is not indexed and throws
	// "Unknown field" on /doterm etc. (SCRUM-6220).
	it('ontologyTermAutocompleteConfig omits the depth-2 crossReferences.resourceDescriptorPage.name path', () => {
		const fields = getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig);
		expect(fields).not.toContain('crossReferences.resourceDescriptorPage.name');
		expect(fields).toContain('crossReferences.referencedCurie');
	});
});
