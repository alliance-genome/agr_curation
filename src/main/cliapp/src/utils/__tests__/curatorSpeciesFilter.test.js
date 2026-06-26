import { getCuratorTaxonCuries, buildCuratorSpeciesFilter } from '../utils';
import { setSpeciesTaxaCache } from '../../constants/speciesTaxa';

// SCRUM-6220: disease-annotation entity autocompletes are constrained to the
// curator's MOD species. The MOD -> taxa mapping is derived at runtime from the
// Species table (each species links to its dataProvider/MOD + taxon), cached by
// useSpeciesTaxa(). These tests seed that cache directly.

// Mirrors a /species result list (dataProvider.abbreviation = MOD, taxon.curie).
const SPECIES = [
	{ dataProvider: { abbreviation: 'RGD' }, taxon: { curie: 'NCBITaxon:10116' } }, // rat
	{ dataProvider: { abbreviation: 'RGD' }, taxon: { curie: 'NCBITaxon:9606' } }, // human (RGD curates human too)
	{ dataProvider: { abbreviation: 'MGI' }, taxon: { curie: 'NCBITaxon:10090' } },
	{ dataProvider: { abbreviation: 'SGD' }, taxon: { curie: 'NCBITaxon:559292' } },
	{ dataProvider: { abbreviation: 'WB' }, taxon: { curie: 'NCBITaxon:6239' } },
	{ dataProvider: { abbreviation: 'XB' }, taxon: { curie: 'NCBITaxon:8355' } },
	{ dataProvider: { abbreviation: 'XB' }, taxon: { curie: 'NCBITaxon:8364' } },
];
const CANONICAL = ['NCBITaxon:10116', 'NCBITaxon:9606', 'NCBITaxon:10090', 'NCBITaxon:559292', 'NCBITaxon:6239', 'NCBITaxon:8355', 'NCBITaxon:8364'];

const setCognitoGroups = (groups) => {
	localStorage.setItem('cognito-token-storage', JSON.stringify({ accessToken: { payload: { 'cognito:groups': groups } } }));
};

describe('getCuratorTaxonCuries (SCRUM-6220)', () => {
	beforeEach(() => setSpeciesTaxaCache(SPECIES));
	afterEach(() => localStorage.clear());

	it('maps a single MOD Staff group to its taxon', () => {
		setCognitoGroups(['WBStaff']);
		expect(getCuratorTaxonCuries()).toEqual(['NCBITaxon:6239']);
	});

	it('maps RGD to both rat and human (RGD curates human)', () => {
		setCognitoGroups(['RGDStaff']);
		expect(getCuratorTaxonCuries().sort()).toEqual(['NCBITaxon:10116', 'NCBITaxon:9606'].sort());
	});

	it('maps XB to both Xenopus taxa', () => {
		setCognitoGroups(['XBStaff']);
		expect(getCuratorTaxonCuries()).toEqual(['NCBITaxon:8355', 'NCBITaxon:8364']);
	});

	it('unions taxa across multiple MOD groups (deduped)', () => {
		setCognitoGroups(['WBStaff', 'SGDStaff']);
		expect(getCuratorTaxonCuries().sort()).toEqual(['NCBITaxon:559292', 'NCBITaxon:6239'].sort());
	});

	it('falls back to the canonical species set when no group maps to a MOD', () => {
		setCognitoGroups(['SomeOtherGroup']);
		expect(getCuratorTaxonCuries().sort()).toEqual([...CANONICAL].sort());
	});

	it('falls back to canonical species when there is no cognito token', () => {
		localStorage.removeItem('cognito-token-storage');
		expect(getCuratorTaxonCuries().sort()).toEqual([...CANONICAL].sort());
	});

	it('returns [] when the species cache has not loaded yet', () => {
		setSpeciesTaxaCache([]);
		setCognitoGroups(['RGDStaff']);
		expect(getCuratorTaxonCuries()).toEqual([]);
	});
});

describe('buildCuratorSpeciesFilter (SCRUM-6220)', () => {
	beforeEach(() => setSpeciesTaxaCache(SPECIES));
	afterEach(() => localStorage.clear());

	it('produces a single-value taxon.curie OR keyword filter for a single MOD', () => {
		setCognitoGroups(['WBStaff']);
		expect(buildCuratorSpeciesFilter()).toEqual({
			speciesFilter: { 'taxon.curie': { queryString: 'NCBITaxon:6239', tokenOperator: 'OR', useKeywordFields: true } },
		});
	});

	it('produces a multi-value OR keyword filter for a multi-taxon MOD', () => {
		setCognitoGroups(['XBStaff']);
		expect(buildCuratorSpeciesFilter()).toEqual({
			speciesFilter: { 'taxon.curie': { queryString: 'NCBITaxon:8355 NCBITaxon:8364', tokenOperator: 'OR', useKeywordFields: true } },
		});
	});

	it('omits the species filter entirely when the cache has not loaded', () => {
		setSpeciesTaxaCache([]);
		setCognitoGroups(['RGDStaff']);
		expect(buildCuratorSpeciesFilter()).toEqual({});
	});
});
