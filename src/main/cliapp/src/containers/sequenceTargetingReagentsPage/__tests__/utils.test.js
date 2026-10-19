import { targetedGeneText, moreTargetedGenesText, targetedGenesFindParams } from '../utils';

describe('targeted gene helpers', () => {
	it('shows relation and symbol, or the identifier without a symbol', () => {
		expect(targetedGeneText({ relation: 'targets', geneSymbol: 'pcdh1a', geneIdentifier: 'ZFIN:1' })).toEqual(
			'targets: pcdh1a'
		);
		expect(targetedGeneText({ relation: 'targets', geneIdentifier: 'ZFIN:1' })).toEqual('targets: ZFIN:1');
		expect(targetedGeneText({ geneSymbol: 'pcdh1a' })).toEqual('pcdh1a');
	});

	it('counts the genes beyond the preview', () => {
		expect(moreTargetedGenesText(627, 5)).toEqual('and 622 more');
		expect(moreTargetedGenesText(3, 3)).toBeNull();
		expect(moreTargetedGenesText(undefined, 0)).toBeNull();
	});

	it('restricts the association find to one STR', () => {
		expect(targetedGenesFindParams(2001)).toEqual({ 'sequenceTargetingReagentAssociationSubject.id': 2001 });
	});
});
