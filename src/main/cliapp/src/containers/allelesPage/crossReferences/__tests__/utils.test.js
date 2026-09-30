const { getResourceDescriptor } = vi.hoisted(() => ({ getResourceDescriptor: vi.fn() }));

vi.mock('../../../../service/ResourceDescriptorService', () => ({
	ResourceDescriptorService: class {
		getResourceDescriptor = getResourceDescriptor;
	},
}));

import {
	applyCrossReferenceFieldChange,
	buildNewCrossReference,
	findMissingRequiredFields,
	findRow,
	seedResourceDescriptor,
	seedResourceDescriptors,
	stripForValidation,
	stripUiFields,
} from '../utils';

const rowWithPage = (pageId, descriptorId) => ({
	referencedCurie: `PMID:${pageId}`,
	resourceDescriptorPage: { id: pageId, name: 'default', resourceDescriptor: { id: descriptorId, prefix: 'PMID' } },
});

const descriptorWithPages = (id) => ({
	id,
	prefix: 'PMID',
	resourcePages: [
		{ id: 1, name: 'default' },
		{ id: 2, name: 'gene' },
	],
});

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

describe('seedResourceDescriptors', () => {
	beforeEach(() => {
		getResourceDescriptor.mockReset();
	});

	it('Replaces the nested descriptor with the loaded one, so the page dropdown has options', async () => {
		getResourceDescriptor.mockResolvedValue({ data: { entity: descriptorWithPages(9) } });

		const [row] = await seedResourceDescriptors([rowWithPage(1, 9)]);

		expect(row.resourceDescriptor.resourcePages).toHaveLength(2);
		expect(row.resourceDescriptorPage.id).toBe(1);
	});

	it('Reads each descriptor once however many rows share it', async () => {
		getResourceDescriptor.mockResolvedValue({ data: { entity: descriptorWithPages(9) } });

		const rows = await seedResourceDescriptors([rowWithPage(1, 9), rowWithPage(2, 9), rowWithPage(3, 12)]);

		expect(getResourceDescriptor).toHaveBeenCalledTimes(2);
		expect(getResourceDescriptor.mock.calls.map((call) => call[0]).sort((a, b) => a - b)).toEqual([9, 12]);
		expect(rows).toHaveLength(3);
	});

	it('Keeps the nested descriptor when the read fails, rather than losing the row', async () => {
		vi.spyOn(console, 'warn').mockImplementation(() => {});
		getResourceDescriptor.mockRejectedValue(new Error('network'));

		const [row] = await seedResourceDescriptors([rowWithPage(1, 9)]);

		expect(row.resourceDescriptor).toEqual({ id: 9, prefix: 'PMID' });
		expect(row.resourceDescriptorPage.id).toBe(1);

		console.warn.mockRestore();
	});

	it('Reads nothing when no row carries a page', async () => {
		const rows = await seedResourceDescriptors([{ referencedCurie: 'PMID:1' }]);

		expect(getResourceDescriptor).not.toHaveBeenCalled();
		expect(rows[0].resourceDescriptor).toBeNull();
	});

	it('Tolerates there being no rows', async () => {
		expect(await seedResourceDescriptors(undefined)).toEqual([]);
		expect(getResourceDescriptor).not.toHaveBeenCalled();
	});
});

describe('applyCrossReferenceFieldChange', () => {
	const pmid = {
		id: 9,
		prefix: 'PMID',
		resourcePages: [
			{ id: 1, name: 'default' },
			{ id: 2, name: 'gene' },
		],
	};
	const withoutDefault = { id: 12, prefix: 'NODEF', resourcePages: [{ id: 30, name: 'homepage' }] };

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

	// A page the curator chose stands, even when the descriptor has a default of its own.
	it('Keeps a page the chosen descriptor owns', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptorPage: { id: 2, name: 'gene' } },
			'resourceDescriptor',
			pmid
		);

		expect(updated.resourceDescriptorPage).toEqual({ id: 2, name: 'gene' });
		expect(updated.resourceDescriptor).toBe(pmid);
	});

	it('Defaults the page when there was none', () => {
		const updated = applyCrossReferenceFieldChange({ resourceDescriptorPage: null }, 'resourceDescriptor', pmid);

		expect(updated.resourceDescriptorPage).toEqual({ id: 1, name: 'default' });
	});

	it('Replaces a page the chosen descriptor does not own with its default', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptorPage: { id: 77, name: 'gene' } },
			'resourceDescriptor',
			pmid
		);

		expect(updated.resourceDescriptorPage).toEqual({ id: 1, name: 'default' });
	});

	it('Leaves no page for a descriptor without a default', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptorPage: { id: 1, name: 'default' } },
			'resourceDescriptor',
			withoutDefault
		);

		expect(updated.resourceDescriptorPage).toBeNull();
	});

	it('Leaves no page for a descriptor typed rather than chosen', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptorPage: { id: 1, name: 'default' } },
			'resourceDescriptor',
			'PMID'
		);

		expect(updated.resourceDescriptorPage).toBeNull();
	});

	// A descriptor object carrying no resourcePages offers no page to keep or to default to.
	it('Leaves no page for a descriptor whose pages did not load', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptorPage: { id: 1, name: 'default' } },
			'resourceDescriptor',
			{ id: 9, prefix: 'PMID' }
		);

		expect(updated.resourceDescriptorPage).toBeNull();
	});

	it('Starts an empty curie with the chosen descriptor prefix', () => {
		const updated = applyCrossReferenceFieldChange({}, 'resourceDescriptor', pmid);

		expect(updated.referencedCurie).toBe('PMID:');
	});

	it('Leaves an empty curie empty for a descriptor with no prefix', () => {
		const updated = applyCrossReferenceFieldChange({ referencedCurie: '' }, 'resourceDescriptor', {
			id: 5,
			resourcePages: [],
		});

		expect(updated.referencedCurie).toBe('');
	});

	it('Keeps a curie the curator typed', () => {
		const updated = applyCrossReferenceFieldChange(
			{ referencedCurie: 'PMID:123' },
			'resourceDescriptor',
			withoutDefault
		);

		expect(updated.referencedCurie).toBe('PMID:123');
	});

	it('Replaces the prefix a previous descriptor left', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptor: pmid, referencedCurie: 'PMID:' },
			'resourceDescriptor',
			withoutDefault
		);

		expect(updated.referencedCurie).toBe('NODEF:');
	});

	it('Empties the prefix a descriptor left when that descriptor is cleared', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptor: pmid, referencedCurie: 'PMID:' },
			'resourceDescriptor',
			null
		);

		expect(updated.referencedCurie).toBe('');
	});

	it('Leaves no page when the descriptor is cleared', () => {
		const updated = applyCrossReferenceFieldChange(
			{ resourceDescriptorPage: { id: 1, name: 'default' } },
			'resourceDescriptor',
			null
		);

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

describe('stripForValidation', () => {
	const row = {
		dataKey: 'row-1',
		resourceDescriptor: { id: 9, prefix: 'PMID' },
		id: 500,
		referencedCurie: 'PMID:1',
		resourceDescriptorPage: { id: 1, name: 'default' },
		createdBy: { uniqueId: 'someone' },
		updatedBy: { uniqueId: 'someone' },
		dateCreated: '2026-01-01',
		dateUpdated: '2026-01-02',
	};

	it('Keeps what the API checks and drops the table and audit fields', () => {
		expect(stripForValidation(row)).toEqual({
			id: 500,
			referencedCurie: 'PMID:1',
			resourceDescriptorPage: { id: 1, name: 'default' },
		});
	});

	it('Does not modify the row it is given', () => {
		stripForValidation(row);

		expect(row.createdBy).toEqual({ uniqueId: 'someone' });
		expect(row.dataKey).toBe('row-1');
	});
});

describe('findMissingRequiredFields', () => {
	const complete = {
		dataKey: 'row-1',
		displayName: 'PMID:1',
		referencedCurie: 'PMID:1',
		resourceDescriptor: { id: 9, prefix: 'PMID' },
		resourceDescriptorPage: { id: 1, name: 'default' },
	};

	it('Leaves out a row with every required field filled', () => {
		expect(findMissingRequiredFields([complete])).toEqual({});
	});

	it('Marks each empty required field against its row', () => {
		const missing = findMissingRequiredFields([
			complete,
			{ ...complete, dataKey: 'row-2', displayName: '', resourceDescriptor: null },
		]);

		expect(Object.keys(missing)).toEqual(['row-2']);
		expect(missing['row-2']).toEqual({
			displayName: { severity: 'error', message: 'Required field is empty' },
			resourceDescriptor: { severity: 'error', message: 'Required field is empty' },
		});
	});

	it('Counts a field holding only spaces as empty', () => {
		const missing = findMissingRequiredFields([{ ...complete, referencedCurie: '   ' }]);

		expect(missing['row-1']).toHaveProperty('referencedCurie');
	});

	it('Reports a curie holding only its prefix as missing its identifier', () => {
		const missing = findMissingRequiredFields([{ ...complete, referencedCurie: 'PMID: ' }]);

		expect(missing['row-1']).toEqual({
			referencedCurie: { severity: 'error', message: 'Identifier after the prefix is missing' },
		});
	});

	it('Marks a new blank row on every required field', () => {
		const missing = findMissingRequiredFields([{ dataKey: 'row-3', displayName: '', referencedCurie: '' }]);

		expect(Object.keys(missing['row-3']).sort()).toEqual(
			['displayName', 'referencedCurie', 'resourceDescriptor', 'resourceDescriptorPage'].sort()
		);
	});
});
