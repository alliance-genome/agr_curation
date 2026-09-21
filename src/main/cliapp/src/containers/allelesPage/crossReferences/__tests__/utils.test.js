const { getResourceDescriptor } = vi.hoisted(() => ({ getResourceDescriptor: vi.fn() }));

vi.mock('../../../../service/ResourceDescriptorService', () => ({
	ResourceDescriptorService: class {
		getResourceDescriptor = getResourceDescriptor;
	},
}));

import {
	applyCrossReferenceFieldChange,
	buildNewCrossReference,
	findRow,
	seedResourceDescriptor,
	seedResourceDescriptors,
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
