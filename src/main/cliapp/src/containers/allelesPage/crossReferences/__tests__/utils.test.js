import {
	applyCrossReferenceFieldChange,
	buildNewCrossReference,
	derivePrefix,
	findRow,
	seedResourceDescriptor,
	stripUiFields,
} from '../utils';

describe('buildNewCrossReference', () => {
	it('Starts blank', () => {
		const crossReference = buildNewCrossReference();

		expect(crossReference.referencedCurie).toBe('');
		expect(crossReference.displayName).toBe('');
		expect(crossReference.resourceDescriptor).toBeNull();
		expect(crossReference.resourceDescriptorPage).toBeNull();
		expect(crossReference.internal).toBe(false);
		expect(crossReference.obsolete).toBe(false);
	});

	it('Keys each row separately', () => {
		const first = buildNewCrossReference();
		const second = buildNewCrossReference();

		expect(first.dataKey).toBeTruthy();
		expect(second.dataKey).not.toBe(first.dataKey);
	});
});

describe('derivePrefix', () => {
	it('Reads the prefix off a curie', () => {
		expect(derivePrefix('PMID:16980395')).toBe('PMID');
	});

	it('Splits on the first colon, not the last', () => {
		expect(derivePrefix('DOI:10.1016/s0896-6273(04)00073-x')).toBe('DOI');
	});

	it('Returns the whole curie when it holds no colon, as the API does', () => {
		expect(derivePrefix('FBrf0195387')).toBe('FBrf0195387');
	});

	it('Tolerates no curie', () => {
		expect(derivePrefix('')).toBe('');
		expect(derivePrefix(undefined)).toBe('');
	});
});

describe('seedResourceDescriptor', () => {
	it('Lifts the descriptor off the stored page', () => {
		const seeded = seedResourceDescriptor({
			referencedCurie: 'PMID:1',
			resourceDescriptorPage: { id: 1, name: 'default', resourceDescriptor: { id: 9, prefix: 'PMID' } },
		});

		expect(seeded.resourceDescriptor).toEqual({ id: 9, prefix: 'PMID' });
		expect(seeded.referencedCurie).toBe('PMID:1');
	});

	it('Leaves the descriptor null when there is no page', () => {
		expect(seedResourceDescriptor({ referencedCurie: 'PMID:1' }).resourceDescriptor).toBeNull();
	});

	it('Does not modify the cross reference it is given', () => {
		const crossReference = { resourceDescriptorPage: { id: 1, resourceDescriptor: { id: 9 } } };

		seedResourceDescriptor(crossReference);

		expect(crossReference).not.toHaveProperty('resourceDescriptor');
	});
});

describe('applyCrossReferenceFieldChange', () => {
	const pmid = { id: 9, prefix: 'PMID', resourcePages: [{ id: 1, name: 'default' }] };

	it('Sets a plain field without touching the rest', () => {
		const updated = applyCrossReferenceFieldChange({ referencedCurie: 'PMID:1', internal: false }, 'internal', true);

		expect(updated.internal).toBe(true);
		expect(updated.referencedCurie).toBe('PMID:1');
	});

	it('Does not modify the cross reference it is given', () => {
		const crossReference = { internal: false };

		applyCrossReferenceFieldChange(crossReference, 'internal', true);

		expect(crossReference.internal).toBe(false);
	});

	it('Keeps a page the chosen descriptor owns', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptorPage: { id: 1, name: 'default' } },
			'resourceDescriptor',
			pmid
		);

		expect(updated.resourceDescriptorPage).toEqual({ id: 1, name: 'default' });
		expect(updated.resourceDescriptor).toBe(pmid);
	});

	it('Clears a page the chosen descriptor does not own', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptorPage: { id: 77, name: 'gene' } },
			'resourceDescriptor',
			pmid
		);

		expect(updated.resourceDescriptorPage).toBeNull();
	});

	it('Clears the page for a descriptor typed rather than chosen', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptorPage: { id: 1, name: 'default' } },
			'resourceDescriptor',
			'PMID'
		);

		expect(updated.resourceDescriptorPage).toBeNull();
	});

	it('Leaves the page null when there was none to keep', () => {
		const updated = applyCrossReferenceFieldChange({ resourceDescriptorPage: null }, 'resourceDescriptor', pmid);

		expect(updated.resourceDescriptorPage).toBeNull();
	});
});

describe('findRow', () => {
	const rows = [{ dataKey: 'a' }, { dataKey: 'b' }];

	it('Finds the row carrying the key', () => {
		expect(findRow(rows, 'b')).toBe(rows[1]);
	});

	it('Returns undefined for a key no row carries', () => {
		expect(findRow(rows, 'c')).toBeUndefined();
	});

	it('Tolerates there being no rows', () => {
		expect(findRow(undefined, 'a')).toBeUndefined();
	});
});

describe('stripUiFields', () => {
	it('Drops the table fields and keeps the rest', () => {
		const stripped = stripUiFields({
			dataKey: 'mock-uuid-1',
			resourceDescriptor: { id: 9, resourcePages: [{ id: 1 }] },
			id: 5,
			referencedCurie: 'PMID:1',
			displayName: 'PMID:1',
			resourceDescriptorPage: { id: 1 },
			internal: false,
			obsolete: false,
		});

		expect(stripped).toEqual({
			id: 5,
			referencedCurie: 'PMID:1',
			displayName: 'PMID:1',
			resourceDescriptorPage: { id: 1 },
			internal: false,
			obsolete: false,
		});
	});

	it('Does not modify the cross reference it is given', () => {
		const crossReference = { dataKey: 'mock-uuid-1', referencedCurie: 'PMID:1' };

		stripUiFields(crossReference);

		expect(crossReference.dataKey).toBe('mock-uuid-1');
	});
});
