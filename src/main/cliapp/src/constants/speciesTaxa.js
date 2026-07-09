// Runtime cache of the curator-MOD -> taxon-curie mapping used to narrow the
// disease-annotation entity autocompletes to a curator's species
//
// Derived from the Species table via /species: each Species links to its dataProvider (the MOD,
// whose `abbreviation` matches the cognito `*Staff` group prefix, e.g. "RGD") and
// to its `taxon` (curie). Populated once by useSpeciesTaxa() and read
// synchronously by getCuratorTaxonCuries() in utils/utils.js.
let modTaxa = {}; // e.g. { RGD: ['NCBITaxon:10116', 'NCBITaxon:9606'], XB: ['NCBITaxon:8355', 'NCBITaxon:8364'] }
let canonicalTaxa = []; // distinct taxon curies across all canonical species (the fallback set)

// Populate the cache from a /species result list. Each entry is expected to carry
// `dataProvider.abbreviation` (MOD code) and `taxon.curie`.
export function setSpeciesTaxaCache(speciesList) {
	const byMod = {};
	const all = new Set();
	(speciesList || []).forEach((species) => {
		const taxon = species?.taxon?.curie;
		if (!taxon) return;
		all.add(taxon);
		const mod = species?.dataProvider?.abbreviation;
		if (mod) {
			if (!byMod[mod]) byMod[mod] = [];
			if (!byMod[mod].includes(taxon)) byMod[mod].push(taxon);
		}
	});
	modTaxa = byMod;
	canonicalTaxa = [...all];
}

// Taxon curies for a MOD code (e.g. 'RGD'); undefined if the MOD is unknown or the
// cache has not loaded yet.
export function getModTaxa(mod) {
	return modTaxa[mod];
}

// All canonical-species taxon curies. Empty until the cache is loaded.
export function getCanonicalTaxa() {
	return canonicalTaxa;
}
