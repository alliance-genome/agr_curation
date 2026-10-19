import { renderHook, act, waitFor } from '@testing-library/react';

const {
	getCrossReferencesForAllele,
	replaceCrossReferencesForAllele,
	getReferencesForAllele,
	replaceReferencesForAllele,
	getGeneAssociationsForAllele,
	replaceGeneAssociationsForAllele,
	getResourceDescriptor,
} = vi.hoisted(() => ({
	getCrossReferencesForAllele: vi.fn(),
	replaceCrossReferencesForAllele: vi.fn(),
	getReferencesForAllele: vi.fn(),
	replaceReferencesForAllele: vi.fn(),
	getGeneAssociationsForAllele: vi.fn(),
	replaceGeneAssociationsForAllele: vi.fn(),
	getResourceDescriptor: vi.fn(),
}));

vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService({
		'cross-references': { get: getCrossReferencesForAllele, replace: replaceCrossReferencesForAllele },
		references: { get: getReferencesForAllele, replace: replaceReferencesForAllele },
		'gene-associations': { get: getGeneAssociationsForAllele, replace: replaceGeneAssociationsForAllele },
	})
);

vi.mock('../../../service/ResourceDescriptorService', () => ({
	ResourceDescriptorService: class {
		getResourceDescriptor = getResourceDescriptor;
	},
}));

const { useAlleleSubResource } = await import('../useAlleleSubResource');

const storedCrossReference = {
	id: 500,
	displayName: 'PMID:1',
	referencedCurie: 'PMID:1',
	resourceDescriptorPage: { id: 1, name: 'default', resourceDescriptor: { id: 9, prefix: 'PMID' } },
};

beforeEach(() => {
	[
		getCrossReferencesForAllele,
		replaceCrossReferencesForAllele,
		getReferencesForAllele,
		replaceReferencesForAllele,
		getGeneAssociationsForAllele,
		replaceGeneAssociationsForAllele,
		getResourceDescriptor,
	].forEach((stub) => stub.mockReset());
	getResourceDescriptor.mockResolvedValue({
		data: { entity: { id: 9, prefix: 'PMID', resourcePages: [{ id: 1, name: 'default' }] } },
	});
});

describe('useAlleleSubResource cross references', () => {
	it('Reads the allele cross references and keys them for the table', async () => {
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });

		const { result } = renderHook(() => useAlleleSubResource('crossReferences', 77));

		await waitFor(() => expect(result.current.rows).toHaveLength(1));
		expect(getCrossReferencesForAllele).toHaveBeenCalledWith(77);
		expect(result.current.rows[0].dataKey).toBeTruthy();
		expect(result.current.rows[0].resourceDescriptor.resourcePages).toHaveLength(1);
	});

	it('Reads nothing until there is an allele, which is how create starts', async () => {
		renderHook(() => useAlleleSubResource('crossReferences'));

		await waitFor(() => expect(getCrossReferencesForAllele).not.toHaveBeenCalled());
	});

	it('Writes the rows without the table fields and re-keys what comes back', async () => {
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });

		const { result } = renderHook(() => useAlleleSubResource('crossReferences', 77));
		await waitFor(() => expect(result.current.rows).toHaveLength(1));

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome).toEqual({ isSuccess: true });
		const [alleleId, written] = replaceCrossReferencesForAllele.mock.calls[0];
		expect(alleleId).toBe(77);
		expect(written[0]).not.toHaveProperty('dataKey');
		expect(written[0]).not.toHaveProperty('resourceDescriptor');
		expect(result.current.rows[0].dataKey).toBeTruthy();
	});

	it('Writes to the allele it is given, which is how create saves', async () => {
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });

		const { result } = renderHook(() => useAlleleSubResource('crossReferences'));
		await act(async () => {
			await result.current.save(123);
		});

		expect(replaceCrossReferencesForAllele).toHaveBeenCalledWith(123, []);
	});

	// The API reports a rejected list by position; the table renders errors against dataKey.
	it('Maps the indexed errors the API returns onto the rows they belong to', async () => {
		getCrossReferencesForAllele.mockResolvedValue({
			data: { entities: [storedCrossReference, { ...storedCrossReference, id: 501 }] },
		});
		replaceCrossReferencesForAllele.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update CrossReferences',
					supplementalData: { errorMap: { crossReferences: { 1: { referencedCurie: 'Required field is empty' } } } },
				},
			},
		});

		const { result } = renderHook(() => useAlleleSubResource('crossReferences', 77));
		await waitFor(() => expect(result.current.rows).toHaveLength(2));
		const secondRowKey = result.current.rows[1].dataKey;

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome.isSuccess).toBe(false);
		expect(result.current.errorMessages[secondRowKey]).toEqual({
			referencedCurie: { severity: 'error', message: 'Required field is empty' },
		});
		expect(result.current.errorMessages).not.toHaveProperty(result.current.rows[0].dataKey);
	});

	// The section refuses to save while loadError is set. An empty table that failed to load looks
	// exactly like an allele with no cross references, and saving it would delete every one it has.
	it('Reports a failed read instead of looking like an allele with none', async () => {
		vi.spyOn(console, 'warn').mockImplementation(() => {});
		getCrossReferencesForAllele.mockRejectedValue(new Error('network'));

		const { result } = renderHook(() => useAlleleSubResource('crossReferences', 77));

		await waitFor(() => expect(result.current.isLoading).toBe(false));
		expect(result.current.loadError).toBeTruthy();
		expect(result.current.rows).toEqual([]);

		console.warn.mockRestore();
	});

	// The page saves these rows only when they were edited, so an allele save leaves rows the curator
	// never touched unwritten, audit trail included.
	it('Reports edits as unsaved until they are written', async () => {
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });

		const { result } = renderHook(() => useAlleleSubResource('crossReferences', 77));
		await waitFor(() => expect(result.current.rows).toHaveLength(1));
		expect(result.current.isDirty).toBe(false);

		act(() => {
			result.current.setRows([]);
		});
		expect(result.current.isDirty).toBe(true);

		await act(async () => {
			await result.current.save();
		});
		expect(result.current.isDirty).toBe(false);
	});

	it('Reports no unsaved edits once the rows are changed back to those last read', async () => {
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });

		const { result } = renderHook(() => useAlleleSubResource('crossReferences', 77));
		await waitFor(() => expect(result.current.rows).toHaveLength(1));
		const loadedRows = result.current.rows;

		act(() => {
			result.current.setRows([...loadedRows, { dataKey: 'new-row', referencedCurie: '' }]);
		});
		expect(result.current.isDirty).toBe(true);

		act(() => {
			result.current.setRows([...loadedRows]);
		});
		expect(result.current.isDirty).toBe(false);

		act(() => {
			result.current.setRows([{ ...loadedRows[0], displayName: 'PMID:1 edited' }]);
		});
		expect(result.current.isDirty).toBe(true);

		act(() => {
			result.current.setRows([{ ...loadedRows[0], displayName: 'PMID:1' }]);
		});
		expect(result.current.isDirty).toBe(false);
	});

	it('Keeps edits unsaved when writing them fails', async () => {
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });
		replaceCrossReferencesForAllele.mockRejectedValue({ response: { data: { errorMessage: 'refused' } } });

		const { result } = renderHook(() => useAlleleSubResource('crossReferences', 77));
		await waitFor(() => expect(result.current.rows).toHaveLength(1));

		act(() => {
			result.current.setRows([]);
		});
		await act(async () => {
			await result.current.save();
		});

		expect(result.current.isDirty).toBe(true);
	});

	// A save replaces the stored list with the rows held here, so saving rows that were never read would
	// submit an empty list and delete every cross reference the allele has.
	it('Refuses to save rows that failed to read', async () => {
		vi.spyOn(console, 'warn').mockImplementation(() => {});
		getCrossReferencesForAllele.mockRejectedValue(new Error('network'));

		const { result } = renderHook(() => useAlleleSubResource('crossReferences', 77));
		await waitFor(() => expect(result.current.loadError).toBeTruthy());

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome.isSuccess).toBe(false);
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
		console.warn.mockRestore();
	});

	it('Refuses to save rows that are still loading', async () => {
		getCrossReferencesForAllele.mockReturnValue(new Promise(() => {}));

		const { result } = renderHook(() => useAlleleSubResource('crossReferences', 77));
		expect(result.current.isLoading).toBe(true);

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome.isSuccess).toBe(false);
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
	});
});

describe('useAlleleSubResource', () => {
	it.each([
		['crossReferences', getCrossReferencesForAllele, replaceCrossReferencesForAllele],
		['references', getReferencesForAllele, replaceReferencesForAllele],
		['alleleGeneAssociations', getGeneAssociationsForAllele, replaceGeneAssociationsForAllele],
	])('Reads and writes %s through their own sub-resource', async (type, getList, replaceList) => {
		getList.mockResolvedValue({ data: { entities: [] } });
		replaceList.mockResolvedValue({ data: { entities: [] } });

		const { result } = renderHook(() => useAlleleSubResource(type, 77));
		await waitFor(() => expect(result.current.isLoading).toBe(false));
		await act(async () => {
			await result.current.save();
		});

		expect(getList).toHaveBeenCalledWith(77);
		expect(replaceList).toHaveBeenCalledWith(77, []);
	});
});

describe('useAlleleSubResource gene associations', () => {
	const storedAssociation = {
		id: 801,
		relation: { name: 'is_allele_of' },
		alleleGeneAssociationObject: { type: 'Gene', id: 31, primaryExternalId: 'WB:WBGene31' },
	};

	it('Refuses rows whose gene was typed rather than picked, and marks them', async () => {
		getGeneAssociationsForAllele.mockResolvedValue({ data: { entities: [storedAssociation] } });

		const { result } = renderHook(() => useAlleleSubResource('alleleGeneAssociations', 77));
		await waitFor(() => expect(result.current.rows).toHaveLength(1));
		const rowKey = result.current.rows[0].dataKey;

		act(() => {
			result.current.setRows([{ ...result.current.rows[0], alleleGeneAssociationObject: 'WB:WBGene3' }]);
		});
		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome).toEqual({ isSuccess: false, message: 'Select each gene from the autosuggest' });
		expect(replaceGeneAssociationsForAllele).not.toHaveBeenCalled();
		expect(result.current.errorMessages[rowKey]).toHaveProperty('alleleGeneAssociationObject');
	});

	it('Saves rows whose genes were all picked', async () => {
		getGeneAssociationsForAllele.mockResolvedValue({ data: { entities: [storedAssociation] } });
		replaceGeneAssociationsForAllele.mockResolvedValue({ data: { entities: [storedAssociation] } });

		const { result } = renderHook(() => useAlleleSubResource('alleleGeneAssociations', 77));
		await waitFor(() => expect(result.current.rows).toHaveLength(1));
		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome).toEqual({ isSuccess: true });
		expect(replaceGeneAssociationsForAllele).toHaveBeenCalledWith(77, [storedAssociation]);
	});
});
