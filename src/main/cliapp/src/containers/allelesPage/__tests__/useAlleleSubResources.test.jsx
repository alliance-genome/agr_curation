import { renderHook, act, waitFor } from '@testing-library/react';

const { getReferencesForAllele, replaceReferencesForAllele, replaceCrossReferencesForAllele } = vi.hoisted(() => ({
	getReferencesForAllele: vi.fn(),
	replaceReferencesForAllele: vi.fn(),
	replaceCrossReferencesForAllele: vi.fn(),
}));

vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService({
		crossreferences: { replace: replaceCrossReferencesForAllele },
		references: { get: getReferencesForAllele, replace: replaceReferencesForAllele },
	})
);

const { useAlleleSubResources } = await import('../useAlleleSubResources');

const storedReference = { type: 'Reference', id: 901, curie: 'AGRKB:101', crossReferences: [] };

const renderLoaded = async () => {
	const hook = renderHook(() => useAlleleSubResources(77));
	await waitFor(() => expect(hook.result.current.subResources.references.rows).toHaveLength(1));
	return hook;
};

describe('useAlleleSubResources', () => {
	beforeEach(() => {
		[getReferencesForAllele, replaceReferencesForAllele, replaceCrossReferencesForAllele].forEach((stub) =>
			stub.mockReset()
		);
		getReferencesForAllele.mockResolvedValue({ data: { entities: [storedReference] } });
		replaceReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
	});

	it('Provides each list under the name its section reads it by', async () => {
		const { result } = await renderLoaded();

		expect(Object.keys(result.current.subResources)).toEqual([
			'crossReferences',
			'references',
			'alleleGeneAssociations',
			'alleleSynonyms',
			'alleleSecondaryIds',
			'alleleNomenclatureEvents',
			'alleleMutationTypes',
			'alleleFunctionalImpacts',
			'alleleInheritanceModes',
			'relatedNotes',
		]);
		expect(result.current.isDirty).toBe(false);
	});

	it('Writes only the edited lists and reports each by its label', async () => {
		const { result } = await renderLoaded();

		act(() => {
			result.current.subResources.references.setRows([]);
		});
		expect(result.current.isDirty).toBe(true);

		let outcomes;
		await act(async () => {
			outcomes = await result.current.saveDirty();
		});

		expect(outcomes).toEqual([{ label: 'references', outcome: { isSuccess: true } }]);
		expect(replaceReferencesForAllele).toHaveBeenCalledWith(77, []);
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
		expect(result.current.isDirty).toBe(false);
	});

	it('Lists the edited lists as save progress rows and reports each as it is written', async () => {
		const { result } = await renderLoaded();
		const onStart = vi.fn();
		const onSettle = vi.fn();

		act(() => {
			result.current.subResources.references.setRows([]);
		});
		expect(result.current.dirtySteps).toEqual([{ key: 'references', label: 'References' }]);

		await act(async () => {
			await result.current.saveDirty(undefined, { onStart, onSettle });
		});

		expect(onStart).toHaveBeenCalledTimes(1);
		expect(onStart).toHaveBeenCalledWith('references');
		expect(onSettle).toHaveBeenCalledTimes(1);
		expect(onSettle).toHaveBeenCalledWith('references', { isSuccess: true });
		expect(result.current.dirtySteps).toEqual([]);
	});

	it('Writes to the allele it is given, which is how create saves', async () => {
		const { result } = renderHook(() => useAlleleSubResources());

		act(() => {
			result.current.subResources.crossReferences.setRows([{ dataKey: 'new-row', referencedCurie: 'PMID:1' }]);
		});
		await act(async () => {
			await result.current.saveDirty(123);
		});

		expect(replaceCrossReferencesForAllele).toHaveBeenCalledWith(123, [{ referencedCurie: 'PMID:1' }]);
	});

	// An unchanged list holds what is stored, so a mark left on one of its rows points at nothing to fix.
	it('Clears the marks on every list it does not write', async () => {
		const { result } = await renderLoaded();
		const mark = { 'row-1': { curie: { severity: 'error', message: 'refused' } } };

		act(() => {
			Object.values(result.current.subResources).forEach((subResource) => subResource.setErrorMessages(mark));
		});
		await act(async () => {
			await result.current.saveDirty();
		});

		expect(replaceReferencesForAllele).not.toHaveBeenCalled();
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
		Object.values(result.current.subResources).forEach((subResource) => expect(subResource.errorMessages).toEqual({}));
	});

	it('Reports a save in progress while any list is being written', async () => {
		let finishWrite;
		replaceReferencesForAllele.mockReturnValue(new Promise((resolve) => (finishWrite = resolve)));
		const { result } = await renderLoaded();

		act(() => {
			result.current.subResources.references.setRows([]);
		});
		let saving;
		act(() => {
			saving = result.current.saveDirty();
		});
		await waitFor(() => expect(result.current.isSaving).toBe(true));

		await act(async () => {
			finishWrite({ data: { entities: [] } });
			await saving;
		});
		expect(result.current.isSaving).toBe(false);
	});

	it('Empties every list on reset', async () => {
		const { result } = await renderLoaded();

		act(() => {
			result.current.subResources.crossReferences.setRows([{ dataKey: 'new-row' }]);
		});
		act(() => {
			result.current.resetAll();
		});

		Object.values(result.current.subResources).forEach((subResource) => expect(subResource.rows).toEqual([]));
		expect(result.current.isDirty).toBe(false);
	});
});
