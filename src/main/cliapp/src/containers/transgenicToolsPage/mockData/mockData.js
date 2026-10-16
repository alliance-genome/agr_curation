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
			transgenicToolTransgenicToolAssociations: [
				{
					id: 6001,
					relation: { name: 'is_compatible_with' },
					transgenicToolTransgenicToolAssociationObject: {
						primaryExternalId: 'FB:FBto0000150',
						transgenicToolSymbol: { displayText: 'GAL80' },
					},
				},
			],
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
