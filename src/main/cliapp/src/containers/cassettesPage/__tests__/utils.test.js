import {
	relationName,
	genomicEntityLabel,
	associationStrings,
	useStrings,
	transgenicToolLabel,
	transgenicToolDisplayValue,
} from '../utils';
import { data } from '../mockData/mockData.js';

const cassette = data.results[0];

describe('cassettes table column text', () => {
	it('drops the ontology id from a relation name', () => {
		expect(relationName({ relation: { name: 'expresses (RO:0002292)' } })).toEqual('expresses');
		expect(relationName({ relation: { name: 'tagged_with' } })).toEqual('tagged_with');
		expect(relationName({})).toEqual('');
	});

	it('labels a genomic entity by symbol, then name, then identifier', () => {
		expect(genomicEntityLabel({ geneSymbol: { displayText: 'croc' }, primaryExternalId: 'FB:FBgn0014143' })).toEqual(
			'croc'
		);
		expect(genomicEntityLabel({ alleleSymbol: { displayText: 'w[1118]' } })).toEqual('w[1118]');
		expect(genomicEntityLabel({ name: 'some agm' })).toEqual('some agm');
		expect(genomicEntityLabel({ primaryExternalId: 'FB:FBgn0000001' })).toEqual('FB:FBgn0000001');
	});

	it('lists each association as relation: object', () => {
		expect(
			associationStrings(
				cassette.cassetteGenomicEntityAssociations,
				'cassetteGenomicEntityAssociationObject',
				genomicEntityLabel
			)
		).toEqual(['expresses: croc']);
		expect(
			associationStrings(
				cassette.cassetteTransgenicToolAssociations,
				'cassetteTransgenicToolAssociationObject',
				(tool) => tool?.transgenicToolSymbol?.displayText
			)
		).toEqual(['tagged_with: EGFP']);
		expect(
			associationStrings(cassette.cassetteStrAssociations, 'cassetteStrAssociationObject', (str) => str?.name)
		).toEqual(['expresses: dsRNA-JF02416']);
		expect(associationStrings(undefined, 'cassetteStrAssociationObject', (str) => str?.name)).toEqual([]);
	});

	it('lists each use term with its curie', () => {
		expect(useStrings(cassette.cassetteUses)).toEqual(['promoter trap (FBcv:0005073)']);
		expect(useStrings([{ uses: [{ curie: 'FBcv:0000001' }] }])).toEqual(['FBcv:0000001']);
		expect(useStrings(undefined)).toEqual([]);
	});
});

describe('transgenic tool labels', () => {
	const sfGFP = { primaryExternalId: 'FB:FBto0000063', transgenicToolSymbol: { displayText: 'sfGFP' } };
	const withoutSymbol = { primaryExternalId: 'FB:FBto0000001' };

	it('uses the symbol in the column, and the identifier when there is none', () => {
		expect(transgenicToolLabel(sfGFP)).toEqual('sfGFP');
		expect(transgenicToolLabel(withoutSymbol)).toEqual('FB:FBto0000001');
	});

	it('shows symbol and identifier in the pop-up', () => {
		expect(transgenicToolDisplayValue(sfGFP)).toEqual('sfGFP (FB:FBto0000063)');
		expect(transgenicToolDisplayValue(withoutSymbol)).toEqual('FB:FBto0000001');
	});
});
