import { generateCrossRefSearchField, generateCrossRefSearchFields, processErrors } from '../utils';

describe('variantsPage utils', () => {
	describe('generateCrossRefSearchField', () => {
		it('Joins the referenced curies of a loaded reference', () => {
			const reference = {
				crossReferences: [{ referencedCurie: 'PMID:1' }, { referencedCurie: 'WB:WBPaper1' }],
			};

			expect(generateCrossRefSearchField(reference)).toEqual('PMID:1,WB:WBPaper1');
		});

		it('Joins the curies of a reference straight from the search index', () => {
			const reference = { cross_references: [{ curie: 'PMID:2' }] };

			expect(generateCrossRefSearchField(reference)).toEqual('PMID:2');
		});

		it('Returns an empty string for a reference carrying neither shape', () => {
			expect(generateCrossRefSearchField({ curie: 'PMID:3' })).toEqual('');
		});
	});

	describe('generateCrossRefSearchFields', () => {
		it('Attaches a filter field to every reference', () => {
			const references = [{ crossReferences: [{ referencedCurie: 'PMID:1' }] }, { cross_references: [] }];

			generateCrossRefSearchFields(references);

			expect(references[0].crossReferencesFilter).toEqual('PMID:1');
			expect(references[1].crossReferencesFilter).toEqual('');
		});

		it('Ignores a variant with no references', () => {
			expect(() => generateCrossRefSearchFields(undefined)).not.toThrow();
		});
	});

	describe('processErrors', () => {
		it('Dispatches the flat error messages', () => {
			const dispatch = vi.fn();

			processErrors({ errorMessages: { variantType: 'Required' } }, dispatch, {});

			expect(dispatch).toHaveBeenCalledWith({
				type: 'UPDATE_ERROR_MESSAGES',
				errorMessages: { variantType: 'Required' },
			});
		});

		it('Keys row errors off the row dataKey', () => {
			const dispatch = vi.fn();
			const variant = { relatedNotes: [{ dataKey: 'row-1' }] };

			processErrors(
				{ supplementalData: { errorMap: { relatedNotes: { 0: { freeText: 'Required' } } } } },
				dispatch,
				variant
			);

			expect(dispatch).toHaveBeenCalledWith({
				type: 'UPDATE_TABLE_ERROR_MESSAGES',
				entityType: 'relatedNotes',
				errorMessages: { 'row-1': { freeText: { severity: 'error', message: 'Required' } } },
			});
		});

		it('Leaves an error for an entity the variant does not carry in the flat messages', () => {
			const dispatch = vi.fn();

			processErrors({ supplementalData: { errorMap: { unknownField: { 0: { name: 'Bad' } } } } }, dispatch, {});

			expect(dispatch).toHaveBeenCalledTimes(1);
			expect(dispatch).toHaveBeenCalledWith({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: {} });
		});

		it('Does not throw when the response carries no errors at all', () => {
			const dispatch = vi.fn();

			expect(() => processErrors(undefined, dispatch, {})).not.toThrow();
		});
	});
});
