export const data = {
	results: [
		{
			id: 1,
			uniqueId: 'FB:FBal0220761',
			primaryExternalId: 'FB:FBal0220761',
			internal: false,
			obsolete: false,
			placeholder: false,
			dateCreated: '2010-01-02T00:00:00Z',
			dateUpdated: '2012-08-03T01:00:00+01:00',
			createdBy: { id: 10, uniqueId: 'FB:FB_curator' },
			updatedBy: { id: 10, uniqueId: 'FB:FB_curator' },
			dataProvider: { id: 20, abbreviation: 'FB' },
			cassetteSymbol: { id: 30, displayText: 'croc[JF02416]', formatText: 'croc<sup>JF02416</sup>' },
			cassetteFullName: { id: 31, displayText: 'Test cassette name' },
			cassetteSynonyms: [{ id: 32, displayText: 'cassette synonym 1' }],
			secondaryIdentifiers: ['FB:FBal9999999'],
			cassetteComponents: [{ id: 33, componentSymbol: 'UAS', relation: { name: 'is_regulated_by' } }],
			cassetteGenomicEntityAssociations: [
				{
					id: 40,
					relation: { name: 'expresses' },
					cassetteGenomicEntityAssociationObject: {
						primaryExternalId: 'FB:FBgn0014143',
						geneSymbol: { displayText: 'croc' },
					},
				},
			],
			cassetteTransgenicToolAssociations: [
				{
					id: 41,
					relation: { name: 'tagged_with' },
					cassetteTransgenicToolAssociationObject: {
						primaryExternalId: 'FB:FBto0000001',
						transgenicToolSymbol: { displayText: 'EGFP' },
					},
				},
			],
			cassetteStrAssociations: [
				{
					id: 42,
					relation: { name: 'expresses' },
					cassetteStrAssociationObject: { primaryExternalId: 'FB:FBsf0000086447', name: 'dsRNA-JF02416' },
				},
			],
			cassetteUses: [{ id: 43, uses: [{ curie: 'FBcv:0005073', name: 'promoter trap' }] }],
		},
	],
	totalResults: 1,
	returnedRecords: 1,
};
