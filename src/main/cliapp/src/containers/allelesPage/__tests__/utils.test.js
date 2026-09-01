import { buildCreatePayload, validateRequiredCreateFields } from '../utils';

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

		expect(payload).toEqual({ primaryExternalId: 'WB:WBVar1' });
	});

	it('Does not mutate the allele it is given', () => {
		const allele = { taxon: { curie: '' }, alleleSynonyms: [{ displayText: 'a' }] };

		const payload = buildCreatePayload(allele);
		payload.alleleSynonyms[0].displayText = 'changed';

		expect(allele.taxon).toEqual({ curie: '' });
		expect(allele.alleleSynonyms[0].displayText).toEqual('a');
	});
});

describe('validateRequiredCreateFields', () => {
	const valid = { primaryExternalId: 'WB:WBVar1', taxon: { curie: 'NCBITaxon:6239' } };

	it('Reports both fields when neither is set', () => {
		const dispatch = vi.fn();

		expect(validateRequiredCreateFields({ taxon: { curie: '' } }, dispatch)).toBe(true);
		expect(dispatch).toHaveBeenCalledWith({
			type: 'UPDATE_ERROR_MESSAGES',
			errorMessages: { primaryExternalId: 'Required', taxon: 'Required' },
		});
	});

	it('Reports a whitespace only primary external id', () => {
		const dispatch = vi.fn();

		expect(validateRequiredCreateFields({ ...valid, primaryExternalId: '   ' }, dispatch)).toBe(true);
		expect(dispatch.mock.calls[0][0].errorMessages).toEqual({ primaryExternalId: 'Required' });
	});

	it('Reports a missing taxon', () => {
		const dispatch = vi.fn();

		expect(validateRequiredCreateFields({ primaryExternalId: 'WB:WBVar1' }, dispatch)).toBe(true);
		expect(dispatch.mock.calls[0][0].errorMessages).toEqual({ taxon: 'Required' });
	});

	it('Passes and clears messages when both are set', () => {
		const dispatch = vi.fn();

		expect(validateRequiredCreateFields(valid, dispatch)).toBe(false);
		expect(dispatch).toHaveBeenCalledWith({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: {} });
	});
});
