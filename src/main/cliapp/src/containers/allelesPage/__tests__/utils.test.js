import { buildCreatePayload, processErrors } from '../utils';

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
