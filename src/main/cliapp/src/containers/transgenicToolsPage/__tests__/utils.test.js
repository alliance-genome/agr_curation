import { useTermTexts, toolAssociationText } from '../utils';
import { data } from '../mockData/mockData';

const tool = data.results[0];

describe('transgenic tool helpers', () => {
	it('lists the terms of a use as name (curie)', () => {
		expect(useTermTexts(tool.transgenicToolUses[0])).toEqual(['green fluorescent protein (FBcv:0005012)']);
		expect(useTermTexts({ uses: [{ curie: 'FBcv:1' }] })).toEqual(['FBcv:1']);
		expect(useTermTexts(undefined)).toEqual([]);
	});

	it('shows an association as relation: symbol, the identifier without a symbol', () => {
		expect(toolAssociationText(tool.transgenicToolTransgenicToolAssociations[0])).toEqual('is_compatible_with: GAL80');
		expect(
			toolAssociationText({
				relation: { name: 'is_compatible_with' },
				transgenicToolTransgenicToolAssociationObject: { primaryExternalId: 'FB:FBto0000150' },
			})
		).toEqual('is_compatible_with: FB:FBto0000150');
	});
});
