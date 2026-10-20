export const data = {
	results: [
		{
			type: 'TransgenicTool',
			id: 4001,
			uniqueId: 'FB:FBto0000063',
			primaryExternalId: 'FB:FBto0000063',
			transgenicToolSymbol: { displayText: 'sfGFP', formatText: 'sfGFP' },
			transgenicToolFullName: { displayText: 'superfolder GFP', formatText: 'superfolder GFP' },
			transgenicToolSynonyms: [{ displayText: 'superfolder green fluorescent protein' }],
			transgenicToolUses: [
				{
					id: 5001,
					uses: [{ curie: 'FBcv:0005012', name: 'green fluorescent protein' }],
					evidence: [{ curie: 'AGRKB:101000000720200' }],
				},
			],
			associatedTransgenicTools: [{ relation: 'is_compatible_with', symbol: 'GAL80', identifier: 'FB:FBto0000150' }],
			relatedNotes: [{ id: 7001, freeText: 'A monomeric GFP.', noteType: { name: 'summary' } }],
			dataProvider: { abbreviation: 'FB' },
			placeholder: false,
			internal: false,
			obsolete: false,
		},
	],
	totalResults: 1,
	returnedRecords: 1,
};
