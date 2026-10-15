import { buildCreatePayload, buildDuplicateAllele, getPendingSingleValueFields, processErrors } from '../utils';

describe('buildCreatePayload', () => {
	it('Drops a taxon with no curie', () => {
		const payload = buildCreatePayload({ taxon: { curie: '' }, internal: false });

		expect(payload).not.toHaveProperty('taxon');
		expect(payload.internal).toBe(false);
	});

	it('Drops an inCollection with no name', () => {
		const payload = buildCreatePayload({ inCollection: { name: '' } });

		expect(payload).not.toHaveProperty('inCollection');
	});

	it('Keeps a populated taxon and inCollection', () => {
		const payload = buildCreatePayload({
			taxon: { curie: 'NCBITaxon:6239' },
			inCollection: { name: 'WB_curated_alleles' },
		});

		expect(payload.taxon).toEqual({ curie: 'NCBITaxon:6239' });
		expect(payload.inCollection).toEqual({ name: 'WB_curated_alleles' });
	});

	it('Tolerates the fields being absent altogether', () => {
		const payload = buildCreatePayload({ primaryExternalId: 'WB:WBVar1' });

		expect(payload).toEqual({ primaryExternalId: 'WB:WBVar1', type: 'Allele' });
	});

	it('Sets the type discriminator the API deserializes on', () => {
		expect(buildCreatePayload({}).type).toEqual('Allele');
	});

	it('Does not mutate the allele it is given', () => {
		const allele = { taxon: { curie: '' }, alleleSynonyms: [{ displayText: 'a' }] };

		const payload = buildCreatePayload(allele);
		payload.alleleSynonyms[0].displayText = 'changed';

		expect(allele.taxon).toEqual({ curie: '' });
		expect(allele.alleleSynonyms[0].displayText).toEqual('a');
	});
});

describe('buildCreatePayload gene associations', () => {
	const withAssociation = () => ({
		alleleGeneAssociations: [
			{
				dataKey: 'row-1',
				relation: { name: 'is_allele_of' },
				alleleGeneAssociationObject: { primaryExternalId: 'GENE:1' },
				// the form seeds this with the allele being edited; on create it has no `type`, which
				// Jackson rejects for the polymorphic BiologicalEntity
				alleleAssociationSubject: { alleleSymbol: { displayText: 'abc-1' } },
			},
		],
	});

	it('Drops the subject the API assigns anyway', () => {
		const payload = buildCreatePayload(withAssociation());

		expect(payload.alleleGeneAssociations[0]).not.toHaveProperty('alleleAssociationSubject');
	});

	it('Keeps the rest of the association intact', () => {
		const payload = buildCreatePayload(withAssociation());

		expect(payload.alleleGeneAssociations[0].relation).toEqual({ name: 'is_allele_of' });
		expect(payload.alleleGeneAssociations[0].alleleGeneAssociationObject).toEqual({ primaryExternalId: 'GENE:1' });
	});

	it('Does not mutate the allele the form holds', () => {
		const allele = withAssociation();

		buildCreatePayload(allele);

		expect(allele.alleleGeneAssociations[0].alleleAssociationSubject).toEqual({
			alleleSymbol: { displayText: 'abc-1' },
		});
	});
});

describe('processErrors', () => {
	// The API reports a required single-object slot annotation as a plain string keyed by the
	// entity, and the entity itself is null when the curator never added one.
	const symbolRequired = {
		errorMessage: 'Could not create Allele',
		errorMessages: { alleleSymbol: 'Required field is empty' },
		supplementalData: { errorMap: { alleleSymbol: 'Required field is empty' } },
	};

	it('Survives a string error for an entity the allele does not have', () => {
		const dispatch = vi.fn();

		expect(() => processErrors(symbolRequired, dispatch, { alleleSymbol: null })).not.toThrow();
		expect(dispatch).toHaveBeenCalledWith({
			type: 'UPDATE_ERROR_MESSAGES',
			errorMessages: { alleleSymbol: 'Required field is empty' },
		});
	});

	it('Survives a string error for an entity the allele does have', () => {
		const dispatch = vi.fn();
		const allele = { alleleSymbol: { dataKey: 0, displayText: 'a' } };

		expect(() => processErrors(symbolRequired, dispatch, allele)).not.toThrow();
	});

	it('Dispatches the flat messages before the ones keyed to a row', () => {
		const dispatch = vi.fn();
		// a row index the allele's table does not have, which cannot be keyed to a row
		const data = {
			errorMessages: { taxon: 'Required field is empty' },
			supplementalData: { errorMap: { alleleSynonyms: { 3: { displayText: 'Required' } } } },
		};

		expect(() => processErrors(data, dispatch, { alleleSynonyms: [] })).toThrow();

		expect(dispatch).toHaveBeenCalledWith({
			type: 'UPDATE_ERROR_MESSAGES',
			errorMessages: { taxon: 'Required field is empty' },
		});
	});

	it('Still routes a per field error map to the row it belongs to', () => {
		const dispatch = vi.fn();
		const allele = { alleleSymbol: { dataKey: 'row-1' } };
		const data = { supplementalData: { errorMap: { alleleSymbol: { nameType: 'Required' } } } };

		processErrors(data, dispatch, allele);

		expect(dispatch).toHaveBeenCalledWith({
			type: 'UPDATE_TABLE_ERROR_MESSAGES',
			entityType: 'alleleSymbol',
			errorMessages: { 'row-1': { nameType: { severity: 'error', message: 'Required' } } },
		});
	});
});

describe('buildDuplicateAllele', () => {
	const audit = {
		createdBy: { uniqueId: 'curator' },
		updatedBy: { uniqueId: 'curator' },
		dateCreated: '2024-01-01T00:00:00Z',
		dateUpdated: '2024-01-02T00:00:00Z',
		dbDateCreated: '2024-01-01T00:00:00Z',
		dbDateUpdated: '2024-01-02T00:00:00Z',
	};
	const gene = { id: 9, primaryExternalId: 'WB:WBGene1', type: 'Gene' };
	const reference = { id: 5, curie: 'AGRKB:101000000000005' };
	const source = () => ({
		...audit,
		type: 'Allele',
		id: 1,
		curie: 'AGRKB:101000000000001',
		primaryExternalId: 'WB:WBVar1',
		modInternalId: 'WBVar1',
		dataProvider: { abbreviation: 'WB' },
		dataProviderCrossReference: { id: 2, referencedCurie: 'WB:WBVar1' },
		obsolete: true,
		internal: true,
		isExtinct: true,
		taxon: { curie: 'NCBITaxon:6239' },
		inCollection: { name: 'WB_curated_alleles' },
		references: [reference],
		relatedNotes: [{ ...audit, id: 3, freeText: 'a note' }],
		alleleSymbol: { ...audit, id: 4, displayText: 'abc-1', formatText: 'abc-1' },
		alleleFullName: { ...audit, id: 6, displayText: 'full name', formatText: 'full name' },
		alleleSynonyms: [{ ...audit, id: 7, displayText: 'synonym' }],
		alleleSecondaryIds: [{ ...audit, id: 8, secondaryId: 'WB:WBVar0' }],
		alleleMutationTypes: [{ ...audit, id: 10, mutationTypes: [{ curie: 'SO:0001' }] }],
		alleleInheritanceModes: [{ ...audit, id: 11, inheritanceMode: { name: 'dominant' } }],
		alleleFunctionalImpacts: [{ ...audit, id: 12, functionalImpacts: [{ name: 'amorph' }] }],
		alleleNomenclatureEvents: [{ ...audit, id: 13, nomenclatureEvent: { name: 'symbol_updated' } }],
		alleleGermlineTransmissionStatus: { ...audit, id: 14, germlineTransmissionStatus: { name: 'cell_line' } },
		alleleDatabaseStatus: { ...audit, id: 15, databaseStatus: { name: 'approved' } },
		alleleGeneAssociations: [
			{
				...audit,
				id: 16,
				alleleAssociationSubject: { id: 1 },
				alleleGeneAssociationObject: gene,
				evidence: [reference],
				relatedNote: { ...audit, id: 19, freeText: 'association note' },
			},
		],
		alleleVariantAssociations: [{ id: 17 }],
		alleleConstructAssociations: [{ id: 18 }],
	});

	it('Leaves behind what identifies the stored allele', () => {
		const duplicate = buildDuplicateAllele(source());

		['id', 'curie', 'primaryExternalId', 'modInternalId', 'dataProvider', 'dataProviderCrossReference'].forEach(
			(field) => expect(duplicate).not.toHaveProperty(field)
		);
		Object.keys(audit).forEach((field) => expect(duplicate).not.toHaveProperty(field));
		expect(duplicate.alleleSecondaryIds).toBeUndefined();
		expect(duplicate).not.toHaveProperty('alleleVariantAssociations');
		expect(duplicate).not.toHaveProperty('alleleConstructAssociations');
		expect(duplicate.obsolete).toBe(false);
	});

	it('Copies the fields the create form edits', () => {
		const duplicate = buildDuplicateAllele(source());

		expect(duplicate.type).toEqual('Allele');
		expect(duplicate.taxon).toEqual({ curie: 'NCBITaxon:6239' });
		expect(duplicate.inCollection).toEqual({ name: 'WB_curated_alleles' });
		expect(duplicate.internal).toBe(true);
		expect(duplicate.isExtinct).toBe(true);
		expect(duplicate.references).toEqual([reference]);
		expect(duplicate.alleleSymbol).toEqual({ displayText: 'abc-1', formatText: 'abc-1' });
		expect(duplicate.alleleFullName).toEqual({ displayText: 'full name', formatText: 'full name' });
		expect(duplicate.alleleSynonyms).toEqual([{ displayText: 'synonym' }]);
		expect(duplicate.relatedNotes).toEqual([{ freeText: 'a note' }]);
		expect(duplicate.alleleMutationTypes).toEqual([{ mutationTypes: [{ curie: 'SO:0001' }] }]);
		expect(duplicate.alleleInheritanceModes).toEqual([{ inheritanceMode: { name: 'dominant' } }]);
		expect(duplicate.alleleFunctionalImpacts).toEqual([{ functionalImpacts: [{ name: 'amorph' }] }]);
		expect(duplicate.alleleNomenclatureEvents).toEqual([{ nomenclatureEvent: { name: 'symbol_updated' } }]);
		expect(duplicate.alleleGermlineTransmissionStatus).toEqual({ germlineTransmissionStatus: { name: 'cell_line' } });
		expect(duplicate.alleleDatabaseStatus).toEqual({ databaseStatus: { name: 'approved' } });
	});

	it('Copies gene associations and their notes without ids or the stored allele as subject', () => {
		const [association] = buildDuplicateAllele(source()).alleleGeneAssociations;

		expect(association).toEqual({
			alleleGeneAssociationObject: gene,
			evidence: [reference],
			relatedNote: { freeText: 'association note' },
		});
	});

	it('Starts from the create form defaults for fields the stored allele lacks', () => {
		const duplicate = buildDuplicateAllele({ taxon: null, alleleSymbol: null });

		expect(duplicate.taxon).toEqual({ curie: '' });
		expect(duplicate.inCollection).toEqual({ name: '' });
		expect(duplicate.alleleSymbol).toBeNull();
		expect(duplicate.alleleSynonyms).toBeUndefined();
		expect(duplicate.references).toBeUndefined();
		expect(duplicate.alleleGeneAssociations).toBeUndefined();
	});

	it('Leaves a list with no rows unset, as the detail endpoint does', () => {
		const duplicate = buildDuplicateAllele({
			alleleFunctionalImpacts: [],
			alleleGeneAssociations: [],
			references: [],
			alleleMutationTypes: [{ id: 10, mutationTypes: [{ curie: 'SO:0001' }] }],
		});

		expect(duplicate.alleleFunctionalImpacts).toBeUndefined();
		expect(duplicate.alleleGeneAssociations).toBeUndefined();
		expect(duplicate.references).toBeUndefined();
		expect(duplicate.alleleMutationTypes).toEqual([{ mutationTypes: [{ curie: 'SO:0001' }] }]);
	});

	it('Does not mutate the allele it is given', () => {
		const stored = source();
		buildDuplicateAllele(stored);

		expect(stored).toEqual(source());
	});
});

describe('getPendingSingleValueFields', () => {
	const saved = {
		taxon: { curie: 'NCBITaxon:10090', name: 'Mus musculus' },
		inCollection: { name: 'EUCOMM' },
		isExtinct: false,
		internal: false,
		obsolete: false,
	};

	it('Finds none before the allele has been saved', () => {
		expect(getPendingSingleValueFields({ ...saved, internal: true }, null).size).toBe(0);
	});

	it('Finds none when every field matches the saved allele', () => {
		expect(getPendingSingleValueFields(structuredClone(saved), saved).size).toBe(0);
	});

	it('Finds each changed field', () => {
		const pending = getPendingSingleValueFields(
			{
				...saved,
				taxon: { curie: 'NCBITaxon:6239' },
				inCollection: { name: 'KOMP' },
				isExtinct: null,
				internal: true,
				obsolete: true,
			},
			saved
		);

		expect([...pending]).toEqual(['taxon', 'inCollection', 'isExtinct', 'internal', 'obsolete']);
	});

	it('Compares a taxon and a collection by their curie and name alone', () => {
		const allele = {
			...saved,
			taxon: { curie: 'NCBITaxon:10090', name: 'Mus musculus', obsolete: false },
			inCollection: { name: 'EUCOMM', id: 3 },
		};

		expect(getPendingSingleValueFields(allele, saved).size).toBe(0);
	});

	it('Reads a blank or missing value as none', () => {
		const unset = { taxon: { curie: '' }, inCollection: undefined, isExtinct: undefined };

		expect(getPendingSingleValueFields(unset, { isExtinct: null }).size).toBe(0);
		expect([...getPendingSingleValueFields(unset, saved)]).toEqual(expect.arrayContaining(['taxon', 'inCollection']));
	});
});

describe('getPendingSingleValueFields single-object sections', () => {
	const references = [{ curie: 'AGRKB:101' }, { curie: 'AGRKB:102' }];
	const saved = {
		alleleSymbol: {
			id: 1,
			dataKey: 'saved-symbol',
			displayText: 'Pax6<sup>Sey</sup>',
			formatText: 'Pax6<Sey>',
			synonymUrl: null,
			nameType: { id: 7, name: 'nomenclature_symbol' },
			synonymScope: null,
			internal: false,
			evidence: references,
		},
		alleleFullName: null,
		alleleGermlineTransmissionStatus: {
			id: 2,
			germlineTransmissionStatus: { id: 8, name: 'cell_line' },
			internal: false,
			evidence: [],
		},
		alleleDatabaseStatus: { id: 3, databaseStatus: { id: 9, name: 'approved' }, internal: false },
	};

	it('Finds none when each section matches the saved allele apart from its ids and term objects', () => {
		const allele = structuredClone(saved);
		allele.alleleSymbol.dataKey = 'form-symbol';
		allele.alleleSymbol.synonymUrl = '';
		allele.alleleSymbol.nameType = { name: 'nomenclature_symbol', definition: 'a fuller term' };
		allele.alleleSymbol.evidence = [...references].reverse();
		allele.alleleGermlineTransmissionStatus.evidence = undefined;

		expect(getPendingSingleValueFields(allele, saved).size).toBe(0);
	});

	it('Finds a section with any curator-editable field changed', () => {
		const symbolEdits = [
			{ displayText: 'Pax6' },
			{ formatText: 'Pax6' },
			{ synonymUrl: 'https://example.org' },
			{ nameType: { name: 'systematic_name' } },
			{ synonymScope: { name: 'exact' } },
			{ internal: true },
			{ evidence: [references[0]] },
		];

		symbolEdits.forEach((symbolEdit) => {
			const allele = { ...saved, alleleSymbol: { ...saved.alleleSymbol, ...symbolEdit } };
			expect([...getPendingSingleValueFields(allele, saved)]).toEqual(['alleleSymbol']);
		});

		const allele = {
			...saved,
			alleleGermlineTransmissionStatus: {
				...saved.alleleGermlineTransmissionStatus,
				germlineTransmissionStatus: { name: 'germline' },
			},
			alleleDatabaseStatus: { ...saved.alleleDatabaseStatus, internal: true },
		};
		expect([...getPendingSingleValueFields(allele, saved)]).toEqual([
			'alleleGermlineTransmissionStatus',
			'alleleDatabaseStatus',
		]);
	});

	it('Finds a section that has been added or deleted', () => {
		const allele = {
			...saved,
			alleleFullName: { dataKey: 0, displayText: '', formatText: '', synonymUrl: '', internal: false },
			alleleDatabaseStatus: null,
		};

		expect([...getPendingSingleValueFields(allele, saved)]).toEqual(['alleleFullName', 'alleleDatabaseStatus']);
	});
});
