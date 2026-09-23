import { renderHook, act, waitFor } from '@testing-library/react';

const { getCrossReferencesForAllele, replaceCrossReferencesForAllele, getResourceDescriptor, validate } = vi.hoisted(
	() => ({
		getCrossReferencesForAllele: vi.fn(),
		replaceCrossReferencesForAllele: vi.fn(),
		getResourceDescriptor: vi.fn(),
		validate: vi.fn(),
	})
);

vi.mock('../../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
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

	// The page saves these rows only when they were edited, so an allele save leaves rows the curator
	// never touched unwritten, audit trail included.
	it('Reports edits as unsaved until they are written', async () => {
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });

		const { result } = renderHook(() => useAlleleCrossReferences(77));
		await waitFor(() => expect(result.current.crossReferences).toHaveLength(1));
		expect(result.current.isDirty).toBe(false);

		act(() => {
			result.current.setCrossReferences([]);
		});
		expect(result.current.isDirty).toBe(true);

		await act(async () => {
			await result.current.save();
		});
		expect(result.current.isDirty).toBe(false);
	});

	it('Keeps edits unsaved when writing them fails', async () => {
		getCrossReferencesForAllele.mockResolvedValue({ data: { entities: [storedCrossReference] } });
		replaceCrossReferencesForAllele.mockRejectedValue({ response: { data: { errorMessage: 'refused' } } });

		const { result } = renderHook(() => useAlleleCrossReferences(77));
		await waitFor(() => expect(result.current.crossReferences).toHaveLength(1));

		act(() => {
			result.current.setCrossReferences([]);
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

		const { result } = renderHook(() => useAlleleCrossReferences(77));
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

		const { result } = renderHook(() => useAlleleCrossReferences(77));
		expect(result.current.isLoading).toBe(true);

		let outcome;
		await act(async () => {
			outcome = await result.current.save();
		});

		expect(outcome.isSuccess).toBe(false);
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
	});

	describe('validate', () => {
		beforeEach(() => {
			validate.mockReset();
		});

		const renderWithRows = async (rows) => {
			getCrossReferencesForAllele.mockResolvedValue({ data: { entities: rows } });
			const rendered = renderHook(() => useAlleleCrossReferences(77));
			await waitFor(() => expect(rendered.result.current.crossReferences).toHaveLength(rows.length));
			return rendered;
		};

		it('Sends each row without its audit fields', async () => {
			validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
			const { result } = await renderWithRows([
				{ ...storedCrossReference, createdBy: { uniqueId: 'someone' }, dateCreated: '2026-01-01' },
			]);

			let outcome;
			await act(async () => {
				outcome = await result.current.validate();
			});

			expect(outcome).toEqual({ isValid: true });
			const sent = validate.mock.calls[0][1];
			expect(sent).not.toHaveProperty('createdBy');
			expect(sent).not.toHaveProperty('dateCreated');
			expect(sent).not.toHaveProperty('dataKey');
			expect(sent.referencedCurie).toBe('PMID:1');
		});

		it('Records the errors of a failing row against that row only', async () => {
			validate.mockResolvedValueOnce({ isSuccess: true, isError: false, data: {} }).mockResolvedValueOnce({
				isSuccess: false,
				isError: true,
				data: { referencedCurie: 'Prefix is missing' },
			});
			const { result } = await renderWithRows([storedCrossReference, { ...storedCrossReference, id: 501 }]);
			const [firstKey, secondKey] = result.current.crossReferences.map((row) => row.dataKey);

			let outcome;
			await act(async () => {
				outcome = await result.current.validate();
			});

			expect(outcome.isValid).toBe(false);
			expect(result.current.errorMessages[secondKey]).toEqual({
				referencedCurie: { severity: 'error', message: 'Prefix is missing' },
			});
			expect(result.current.errorMessages).not.toHaveProperty(firstKey);
		});

		it('Reports a check it could not make as a failure', async () => {
			vi.spyOn(console, 'warn').mockImplementation(() => {});
			validate.mockRejectedValue(new TypeError('network'));
			const { result } = await renderWithRows([storedCrossReference]);

			let outcome;
			await act(async () => {
				outcome = await result.current.validate();
			});

			expect(outcome.isValid).toBe(false);
			expect(result.current.isValidating).toBe(false);
			console.warn.mockRestore();
		});
	});
});
