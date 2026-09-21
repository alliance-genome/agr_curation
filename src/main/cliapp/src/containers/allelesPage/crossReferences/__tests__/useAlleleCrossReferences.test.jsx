import { renderHook, act, waitFor } from '@testing-library/react';

const { getCrossReferencesForAllele, replaceCrossReferencesForAllele, getResourceDescriptor } = vi.hoisted(() => ({
	getCrossReferencesForAllele: vi.fn(),
	replaceCrossReferencesForAllele: vi.fn(),
	getResourceDescriptor: vi.fn(),
}));

vi.mock('../../../../service/CrossReferenceService', () => ({
	CrossReferenceService: class {
		getCrossReferencesForAllele = getCrossReferencesForAllele;
		replaceCrossReferencesForAllele = replaceCrossReferencesForAllele;
	},
}));

vi.mock('../../../../service/ResourceDescriptorService', () => ({
	ResourceDescriptorService: class {
		getResourceDescriptor = getResourceDescriptor;
	},
}));

const { useAlleleCrossReferences } = await import('../useAlleleCrossReferences');

const storedCrossReference = {
	id: 500,
	displayName: 'PMID:1',
	referencedCurie: 'PMID:1',
	resourceDescriptorPage: { id: 1, name: 'default', resourceDescriptor: { id: 9, prefix: 'PMID' } },
};

beforeEach(() => {
	getCrossReferencesForAllele.mockReset();
	replaceCrossReferencesForAllele.mockReset();
	getResourceDescriptor.mockReset();
	getResourceDescriptor.mockResolvedValue({
		data: { entity: { id: 9, prefix: 'PMID', resourcePages: [{ id: 1, name: 'default' }] } },
	});
});

describe('useAlleleCrossReferences', () => {
	it('Reads the allele cross references and keys them for the table', async () => {
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });

		const { result } = renderHook(() => useAlleleCrossReferences(77));

		await waitFor(() => expect(result.current.crossReferences).toHaveLength(1));
		expect(getCrossReferencesForAllele).toHaveBeenCalledWith(77);
		expect(result.current.crossReferences[0].dataKey).toBeTruthy();
		expect(result.current.crossReferences[0].resourceDescriptor.resourcePages).toHaveLength(1);
	});

	it('Reads nothing until there is an allele, which is how create starts', async () => {
		renderHook(() => useAlleleCrossReferences());

		await waitFor(() => expect(getCrossReferencesForAllele).not.toHaveBeenCalled());
	});

	it('Writes the rows without the table fields and re-keys what comes back', async () => {
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });

		const { result } = renderHook(() => useAlleleCrossReferences(77));
		await waitFor(() => expect(result.current.crossReferences).toHaveLength(1));

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome).toEqual({ isSuccess: true });
		const [alleleId, written] = replaceCrossReferencesForAllele.mock.calls[0];
		expect(alleleId).toBe(77);
		expect(written[0]).not.toHaveProperty('dataKey');
		expect(written[0]).not.toHaveProperty('resourceDescriptor');
		expect(result.current.crossReferences[0].dataKey).toBeTruthy();
	});

	it('Writes to the allele it is given, which is how create saves', async () => {
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });

		const { result } = renderHook(() => useAlleleCrossReferences());
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

		const { result } = renderHook(() => useAlleleCrossReferences(77));
		await waitFor(() => expect(result.current.crossReferences).toHaveLength(2));
		const secondRowKey = result.current.crossReferences[1].dataKey;

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome.isSuccess).toBe(false);
		expect(result.current.errorMessages[secondRowKey]).toEqual({
			referencedCurie: { severity: 'error', message: 'Required field is empty' },
		});
		expect(result.current.errorMessages).not.toHaveProperty(result.current.crossReferences[0].dataKey);
	});

	// The section refuses to save while loadError is set. An empty table that failed to load looks
	// exactly like an allele with no cross references, and saving it would delete every one it has.
	it('Reports a failed read instead of looking like an allele with none', async () => {
		vi.spyOn(console, 'warn').mockImplementation(() => {});
		getCrossReferencesForAllele.mockRejectedValue(new Error('network'));

		const { result } = renderHook(() => useAlleleCrossReferences(77));

		await waitFor(() => expect(result.current.isLoading).toBe(false));
		expect(result.current.loadError).toBeTruthy();
		expect(result.current.crossReferences).toEqual([]);

		console.warn.mockRestore();
	});
});
