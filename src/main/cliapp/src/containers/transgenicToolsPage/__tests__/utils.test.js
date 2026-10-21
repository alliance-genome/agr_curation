import { useTermTexts, associatedToolText, toolAssociationsFindParams } from '../utils';
import { data } from '../mockData/mockData';

const tool = data.results[0];

describe('transgenic tool helpers', () => {
	it('lists the terms of a use as name (curie)', () => {
		expect(useTermTexts(tool.transgenicToolUses[0])).toEqual(['green fluorescent protein (FBcv:0005012)']);
		expect(useTermTexts({ uses: [{ curie: 'FBcv:1' }] })).toEqual(['FBcv:1']);
		expect(useTermTexts(undefined)).toEqual([]);
	});

	it('shows an associated tool as relation: symbol, the identifier without a symbol', () => {
		expect(associatedToolText(tool.associatedTransgenicTools[0])).toEqual('is_compatible_with: GAL80');
		expect(associatedToolText({ relation: 'is_compatible_with', identifier: 'FB:FBto0000150' })).toEqual(
			'is_compatible_with: FB:FBto0000150'
		);
		expect(associatedToolText({ relation: 'is_compatible_with (RO:0000001)', symbol: 'FRT' })).toEqual(
			'is_compatible_with: FRT'
		);
	});

	it('finds the associations of one tool by its id', () => {
		expect(toolAssociationsFindParams(42)).toEqual({ 'transgenicToolAssociationSubject.id': 42 });
	});
});
