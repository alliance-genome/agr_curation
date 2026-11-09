import { cassetteLabel, constructCassettesFilter } from '../utils';

describe('cassetteLabel', () => {
	it('uses the symbol, and the identifier when there is none', () => {
		expect(
			cassetteLabel({
				primaryExternalId: 'ZFIN:ZDB-TGCONSTRCT-260422-7_cas',
				cassetteSymbol: { displayText: 'Tg(tnfa:EGFP)' },
			})
		).toEqual('Tg(tnfa:EGFP)');
		expect(cassetteLabel({ modInternalId: 'MGI:6201321_cas' })).toEqual('MGI:6201321_cas');
		expect(cassetteLabel(undefined)).toEqual('');
	});
});

describe('constructCassettesFilter', () => {
	it('restricts the search to the ids of the associated cassettes', () => {
		expect(
			constructCassettesFilter([
				{ constructCassetteAssociationObject: { id: 200096001 } },
				{ constructCassetteAssociationObject: { id: 200096107 } },
			])
		).toEqual({ constructCassettesFilter: { id: { queryString: '200096001 200096107', tokenOperator: 'OR' } } });
	});

	it('is null when there are no cassettes', () => {
		expect(constructCassettesFilter([])).toBeNull();
		expect(constructCassettesFilter(undefined)).toBeNull();
	});
});
