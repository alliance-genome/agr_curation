import { useRef, useState } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { AlleleService } from '../../service/AlleleService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { useAlleleReducer } from './useAlleleReducer';
import { StickyHeader } from '../../components/StickyHeader';
import { StickyFooter } from '../../components/StickyFooter';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { SaveProgressOverlay } from '../../components/SaveProgressOverlay';
import { useSaveProgress } from '../../hooks/useSaveProgress';
import { errorMessageOf, getIdentifier } from '../../utils/utils';
import { buildCreatePayload, buildDuplicateAllele, capitalize, processErrors } from './utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { AlleleForm, ALLELE_CREATE_TOGGLEABLE_FIELDS } from './AlleleForm';
import { useAlleleSubResources } from './useAlleleSubResources';
import { useDuplicateAlleleSource } from './useDuplicateAlleleSource';
import { SubResourcesProvider } from '../../components/SubResourcesContext';

export default function AlleleCreatePage() {
	const navigate = useNavigate();
	const { alleleState, alleleDispatch } = useAlleleReducer('create');
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'AlleleCreate',
		ALLELE_CREATE_TOGGLEABLE_FIELDS
	);
	const alleleService = new AlleleService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const {
		subResources,
		saveDirty: saveDirtySubResources,
		dirtySteps: dirtySubResourceSteps,
		resetAll: resetSubResources,
		carryOverAll: carryOverSubResources,
	} = useAlleleSubResources();
	const saveProgress = useSaveProgress();
	// Set once Save & Close has saved everything, while the page shows that before moving on to the allele.
	const [isLeaving, setIsLeaving] = useState(false);
	// Held so that saving again after a sub-resource failed retries it against the allele that already exists,
	// rather than creating a second one.
	const createdAllele = useRef(null);
	const { isLoading: duplicateSourceIsLoading } = useDuplicateAlleleSource({
		alleleDispatch,
		subResources,
		isVisible,
		toastError,
	});

	const { mutate: alleleMutate } = useMutation({
		mutationFn: (allele) => {
			return alleleService.createAllele(allele);
		},
	});

	// Saving again once the allele exists updates it rather than creating another. updateDetail rather
	// than the plain allele endpoint, because that one manages cross references and references from its payload and
	// this page never carries them there - it would read their absence as an instruction to clear them. Neither
	// endpoint writes gene associations.
	const { mutate: alleleUpdateMutate } = useMutation({
		mutationFn: (allele) => {
			return alleleService.saveAlleleDetail(allele);
		},
	});

	const showSaveError = (error) => {
		saveProgress.settle('allele', { isSuccess: false });
		saveProgress.skipRemaining();
		saveProgress.finish();
		const data = error?.response?.data;
		toastError.current.show([
			{ life: 7000, severity: 'error', summary: 'Page error: ', detail: errorMessageOf(error), sticky: false },
		]);

		try {
			processErrors(data, alleleDispatch, alleleState.allele);
		} catch (e) {
			console.error(e);
		}
	};

	// The allele's sub-resources are written through their own endpoints, so create takes several calls.
	// The allele exists by the time this runs, so a failure here leaves the work recoverable from its detail page
	// rather than lost - which is why it keeps the curator here instead of navigating away. Each list is written
	// only when it differs from what this allele has stored, so saving again skips a list that already saved and
	// writes one the curator has since changed or emptied.
	const saveSubResources = async (allele, closeAfterSubmit) => {
		saveProgress.settle('allele', { isSuccess: true });
		let subResourceOutcomes;
		try {
			subResourceOutcomes = await saveDirtySubResources(allele?.id, {
				onStart: saveProgress.start,
				onSettle: saveProgress.settle,
			});
		} catch (error) {
			// Anything the save stopped before writing shows as not saved, and the overlay still closes.
			saveProgress.skipRemaining();
			saveProgress.finish();
			throw error;
		}
		const failedSubResources = subResourceOutcomes.filter(({ outcome }) => !outcome.isSuccess);

		if (failedSubResources.length > 0) {
			saveProgress.finish();
			// The form takes on the saved allele, so the fields carry the ids that saving again needs
			// and an edit made while fixing the rows is sent as an update rather than dropped.
			alleleDispatch({ type: 'SET', value: allele });
			toastError.current.show(
				failedSubResources.map(({ label, outcome }) => ({
					life: 10000,
					severity: 'error',
					summary: `${capitalize(label)} not saved: `,
					detail: `${outcome.message}. Allele ${getIdentifier(allele)} was created - fix the rows and save again, or finish them on its detail page.`,
					sticky: false,
				}))
			);
			return;
		}

		createdAllele.current = null;

		if (closeAfterSubmit) {
			// The curator sees everything saved before the page moves on; saving again meanwhile is not offered.
			setIsLeaving(true);
			if (await saveProgress.finish()) navigate(`/allele/${getIdentifier(allele)}`);
		} else {
			// The form keeps the saved allele's values and rows, copied as Duplicate copies them, so the next allele
			// can be entered as a small change to this one.
			alleleDispatch({ type: 'SET', value: buildDuplicateAllele(allele) });
			try {
				await carryOverSubResources(isVisible);
			} finally {
				saveProgress.finish();
			}
		}
	};

	const handleSubmit = (event, closeAfterSubmit) => {
		event.preventDefault();
		alleleDispatch({ type: 'SUBMIT' });
		saveProgress.begin([{ key: 'allele', label: 'Allele' }, ...dirtySubResourceSteps]);
		saveProgress.start('allele');

		if (createdAllele.current) {
			alleleUpdateMutate(alleleState.allele, {
				onSuccess: async (result) => {
					const allele = result?.data?.entity;
					createdAllele.current = allele;
					alleleDispatch({ type: 'SET', value: allele });

					await saveSubResources(allele, closeAfterSubmit);
				},
				onError: showSaveError,
			});
			return;
		}

		alleleMutate(buildCreatePayload(alleleState.allele), {
			onSuccess: async (result) => {
				const allele = result?.data?.entity;
				createdAllele.current = allele;
				toastSuccess.current.show({
					severity: 'success',
					summary: 'Successful',
					detail: `Allele ${getIdentifier(allele)} created`,
				});

				await saveSubResources(allele, closeAfterSubmit);
			},
			onError: showSaveError,
		});
	};

	const handleSubmitAndClose = (event) => {
		handleSubmit(event, true);
	};

	const handleSubmitAndAddAnother = (event) => {
		handleSubmit(event, false);
	};

	const handleClear = (event) => {
		event.preventDefault();
		alleleDispatch({ type: 'RESET' });
		resetSubResources();
		createdAllele.current = null;
	};

	const handleCancel = (event) => {
		event.preventDefault();
		navigate('/alleles');
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<SaveProgressOverlay isOpen={saveProgress.isOpen} steps={saveProgress.steps} />
			<LoadingOverlay isLoading={duplicateSourceIsLoading} message="Loading allele to duplicate..." />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1>Add Allele</h1>
						</SplitterPanel>
						<SplitterPanel size={55} className="flex align-items-center justify-content-end gap-2 pr-5 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={ALLELE_CREATE_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<div className="pb-8">
					<SubResourcesProvider value={subResources}>
						<AlleleForm state={alleleState} dispatch={alleleDispatch} isVisible={isVisible} mode="create" />
					</SubResourcesProvider>
				</div>
				<StickyFooter>
					<Splitter className="bg-primary-reverse border-none" gutterSize={0}>
						<SplitterPanel size={50} className="flex justify-content-start ml-5 py-3">
							<Button label="Clear" icon="pi pi-undo" className="p-button-text" onClick={handleClear} />
							<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={handleCancel} />
						</SplitterPanel>
						<SplitterPanel size={50} className="flex justify-content-end mr-5 py-3">
							<Button
								label="Save & Close"
								icon="pi pi-check"
								severity="success"
								onClick={handleSubmitAndClose}
								disabled={saveProgress.isSaving || isLeaving}
							/>
							<Button
								label="Save & Add Another"
								icon="pi pi-check"
								severity="success"
								className="ml-2"
								onClick={handleSubmitAndAddAnother}
								disabled={saveProgress.isSaving || isLeaving}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyFooter>
			</ErrorBoundary>
		</>
	);
}
