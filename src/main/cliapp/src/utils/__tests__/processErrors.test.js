import { processErrors } from '../processErrors';

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
