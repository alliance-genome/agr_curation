export const data = {
	results: [
		{
			type: 'SequenceTargetingReagent',
			id: 2001,
			curie: 'AGRKB:107000000000001',
			primaryExternalId: 'ZFIN:ZDB-TALEN-190405-2',
			name: 'TALEN1-mir430',
			synonyms: ['TALEN1'],
			secondaryIdentifiers: ['ZFIN:ZDB-MRKRSEQ-190405-2'],
			taxon: { curie: 'NCBITaxon:7955', name: 'Danio rerio' },
			dataProvider: { abbreviation: 'ZFIN' },
			targetedGeneCount: 7,
			targetedGenesPreview: [
				{ relation: 'targets', geneSymbol: 'dre-mir-430a-1', geneIdentifier: 'ZFIN:ZDB-MIRNAG-081210-1' },
				{ relation: 'targets', geneSymbol: 'dre-mir-430a-2', geneIdentifier: 'ZFIN:ZDB-MIRNAG-081210-2' },
			],
			internal: false,
			obsolete: false,
		},
	],
	totalResults: 1,
	returnedRecords: 1,
};
