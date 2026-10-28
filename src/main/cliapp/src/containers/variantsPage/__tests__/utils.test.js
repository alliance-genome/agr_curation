import {
	buildCreatePayload,
	buildDuplicateVariant,
	buildSavePayload,
	generateCrossRefSearchField,
	generateCrossRefSearchFields,
	processErrors,
} from '../utils';

describe('variantsPage utils', () => {
	describe('buildSavePayload', () => {
		it('Types a reference picked from the literature search, which carries no type', () => {
			const payload = buildSavePayload({ references: [{ curie: 'AGRKB:101000000507617' }] });

			expect(payload.references[0].type).toEqual('Reference');
		});

		it('Types the references on a related note', () => {
			const payload = buildSavePayload({
				relatedNotes: [{ freeText: 'n', references: [{ curie: 'AGRKB:101000000507617' }] }],
			});

			expect(payload.relatedNotes[0].references[0].type).toEqual('Reference');
		});

		it('Keeps a type the reference already has', () => {
			const payload = buildSavePayload({ references: [{ curie: 'AGRKB:1', type: 'LiteratureReference' }] });

			expect(payload.references[0].type).toEqual('LiteratureReference');
		});

		it('Tolerates notes and references being absent or null', () => {
			expect(() => buildSavePayload({ relatedNotes: [{ freeText: 'n', references: null }] })).not.toThrow();
			expect(() => buildSavePayload({})).not.toThrow();
		});
	});

	describe('buildCreatePayload', () => {
		it('Drops a taxon with no curie', () => {
			const payload = buildCreatePayload({ taxon: { curie: '' }, internal: false });

			expect(payload).not.toHaveProperty('taxon');
			expect(payload.internal).toBe(false);
		});

		it('Sets the type the API needs to deserialize a variant', () => {
			expect(buildCreatePayload({}).type).toEqual('Variant');
		});

		it('Keeps a populated taxon', () => {
			const payload = buildCreatePayload({ taxon: { curie: 'NCBITaxon:6239' } });

			expect(payload.taxon).toEqual({ curie: 'NCBITaxon:6239' });
		});

		it('Leaves the variant it was given untouched', () => {
			const variant = { taxon: { curie: '' } };

			buildCreatePayload(variant);

			expect(variant.taxon).toEqual({ curie: '' });
		});
	});

	describe('buildDuplicateVariant', () => {
		const storedVariant = {
			id: 4242,
			curie: 'AGRKB:103000000000001',
			primaryExternalId: 'WB:WBVar00000001',
			type: 'Variant',
			taxon: { id: 9, curie: 'NCBITaxon:6239' },
			variantType: { id: 5, curie: 'SO:1000002' },
			variantStatus: { id: 6, name: 'public' },
			sourceGeneralConsequence: { id: 7, curie: 'SO:0001587' },
			internal: true,
			obsolete: true,
			dataProvider: { id: 8, abbreviation: 'WB' },
			crossReferences: [{ id: 12, referencedCurie: 'WB:WBVar00000001' }],
			dateCreated: '2026-10-23T00:00:00Z',
			createdBy: { uniqueId: 'curator1' },
			relatedNotes: [
				{
					id: 31,
					freeText: 'a note',
					noteType: { name: 'comment' },
					references: [{ id: 41, curie: 'AGRKB:101000000000041' }],
					dateCreated: '2026-10-23T00:00:00Z',
				},
			],
			references: [{ id: 41, curie: 'AGRKB:101000000000041' }],
			synonyms: ['var-1', 'var-one'],
		};

		it('Carries the fields the create form edits', () => {
			const copy = buildDuplicateVariant(storedVariant);

			expect(copy).toEqual({
				type: 'Variant',
				taxon: { id: 9, curie: 'NCBITaxon:6239' },
				variantType: { id: 5, curie: 'SO:1000002' },
				variantStatus: { id: 6, name: 'public' },
				sourceGeneralConsequence: { id: 7, curie: 'SO:0001587' },
				internal: true,
				obsolete: false,
				relatedNotes: [
					{
						freeText: 'a note',
						noteType: { name: 'comment' },
						references: [{ id: 41, curie: 'AGRKB:101000000000041' }],
					},
				],
				references: [{ id: 41, curie: 'AGRKB:101000000000041' }],
				synonyms: ['var-1', 'var-one'],
			});
		});

		it('Copies deeply, leaving the stored variant untouched', () => {
			const copy = buildDuplicateVariant(storedVariant);
			copy.taxon.curie = 'NCBITaxon:10090';
			copy.references[0].curie = 'AGRKB:101000000000042';
			copy.synonyms.push('var-2');

			expect(storedVariant.taxon.curie).toEqual('NCBITaxon:6239');
			expect(storedVariant.references[0].curie).toEqual('AGRKB:101000000000041');
			expect(storedVariant.synonyms).toEqual(['var-1', 'var-one']);
			expect(storedVariant.relatedNotes[0].id).toEqual(31);
		});

		it('Fills what a bare variant lacks with the blank form values, leaving related notes unset', () => {
			expect(buildDuplicateVariant({ id: 4242 })).toEqual({
				type: 'Variant',
				taxon: { curie: '' },
				variantType: null,
				variantStatus: null,
				sourceGeneralConsequence: null,
				internal: false,
				obsolete: false,
				relatedNotes: undefined,
				references: [],
				synonyms: [],
			});
		});
	});

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
