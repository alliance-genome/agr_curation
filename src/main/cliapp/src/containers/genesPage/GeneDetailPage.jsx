import { useEffect, useMemo, useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { GeneService } from '../../service/GeneService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { DetailPageNotFound } from '../../components/DetailPageNotFound';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { SaveProgressOverlay, SaveProgressProvider } from '../../components/SaveProgressOverlay';
import { SubResourcesProvider } from '../../components/SubResourcesContext';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { useSaveProgress } from '../../hooks/useSaveProgress';
import { capitalize, errorMessageOf } from '../../utils/utils';
import { processErrors } from '../allelesPage/utils';
import { seedResourceDescriptors } from '../allelesPage/crossReferences/utils';
import { useGeneReducer } from './useGeneReducer';
import { useGeneSubResources } from './useGeneSubResources';
import { buildGeneDetailPayload, getPendingGeneFields } from './utils';
import { GeneForm, GENE_DETAIL_TOGGLEABLE_FIELDS } from './GeneForm';

export default function GeneDetailPage() {
	const { identifier } = useParams();
	const { geneState, geneDispatch } = useGeneReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'GeneDetail',
		GENE_DETAIL_TOGGLEABLE_FIELDS
	);
	const geneService = useMemo(() => new GeneService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const {
		subResources,
		saveDirty: saveSubResources,
		isDirty: subResourcesAreDirty,
		dirtySteps: dirtySubResourceSteps,
	} = useGeneSubResources(geneState.gene?.id);
	const saveProgress = useSaveProgress();
	// The gene as the API last returned it, without the form's unsaved edits.
	const savedGene = useRef(null);

	const {
		isPending: getRequestIsLoading,
		data: geneQueryData,
		error: geneQueryError,
		isPlaceholderData: showsPreviousResponse,
	} = useQuery({
		queryKey: ['gene', identifier],
		queryFn: () => geneService.getGene(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	/**
	 * Shows a gene the API returned. Its GCRP cross reference is first given its descriptor, so the page dropdown can
	 * offer the descriptor's pages.
	 *
	 * @param {Object} [entity] the gene as the API returned it
	 */
	const showGene = async (entity) => {
		if (!entity) return;
		const [gcrpCrossReference = null] = entity.gcrpCrossReference
			? await seedResourceDescriptors([entity.gcrpCrossReference])
			: [];
		const gene = { ...entity, gcrpCrossReference };
		savedGene.current = gene;
		geneDispatch({ type: 'SET', value: gene });
	};

	useEffect(() => {
		showGene(geneQueryData?.data?.entity);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [geneQueryData]);

	const { isPending: genePutRequestIsLoading, mutateAsync: saveGene } = useMutation({
		mutationFn: (gene) => geneService.saveGeneDetail(gene),
	});

	/**
	 * Marks a rejected gene's field errors on the form.
	 *
	 * @param {Object} error the rejected request's error
	 * @returns {string} the message describing the rejection
	 */
	const reportGeneErrors = (error) => {
		try {
			processErrors(error?.response?.data, geneDispatch, geneState.gene);
		} catch (processingError) {
			console.error(processingError);
		}

		return errorMessageOf(error);
	};

	const handleSubmit = async (event) => {
		event.preventDefault();
		geneDispatch({ type: 'SUBMIT' });

		saveProgress.begin([{ key: 'gene', label: 'Gene' }, ...dirtySubResourceSteps]);
		let geneError = null;
		let subResourceOutcomes;
		try {
			saveProgress.start('gene');
			try {
				const geneResult = await saveGene(buildGeneDetailPayload(geneState.gene));
				await showGene(geneResult?.data?.entity);
			} catch (error) {
				geneError = error;
			}
			saveProgress.settle('gene', { isSuccess: !geneError });

			// The lists are written whether or not the gene saved, so the errors of all of them are reported together.
			subResourceOutcomes = await saveSubResources(undefined, {
				onStart: saveProgress.start,
				onSettle: saveProgress.settle,
			});
		} finally {
			// Anything the save stopped before writing shows as not saved, and the overlay always closes.
			saveProgress.skipRemaining();
			saveProgress.finish();
		}
		const savedLabels = subResourceOutcomes.filter(({ outcome }) => outcome.isSuccess).map(({ label }) => label);
		const failedSubResources = subResourceOutcomes.filter(({ outcome }) => !outcome.isSuccess);
		const failureToast = ({ label }, detail) => ({
			life: 10000,
			severity: 'error',
			summary: `${capitalize(label)} not saved: `,
			detail,
			sticky: false,
		});

		if (geneError) {
			const geneMessage = reportGeneErrors(geneError);

			if (subResourceOutcomes.length === 0) {
				toastError.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: geneMessage, sticky: false },
				]);
			} else {
				toastError.current.show([
					{
						life: 10000,
						severity: 'error',
						summary: 'Gene not saved: ',
						detail:
							savedLabels.length > 0
								? `${geneMessage}. The ${new Intl.ListFormat('en').format(savedLabels)} were saved.`
								: geneMessage,
						sticky: false,
					},
					...failedSubResources.map((failure) => failureToast(failure, failure.outcome.message)),
				]);
			}
			return;
		}

		if (failedSubResources.length > 0) {
			toastError.current.show(
				failedSubResources.map((failure) =>
					failureToast(failure, `${failure.outcome.message}. The gene's other changes were saved.`)
				)
			);
			return;
		}

		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Gene Saved' });
	};

	const pendingFields = getPendingGeneFields(geneState.gene, savedGene.current);
	const hasPendingEdits = pendingFields.size > 0 || subResourcesAreDirty;

	const loadedGene = geneQueryData?.data?.entity;

	// The gene is set once its GCRP cross reference has its descriptor, so the form waits for that too.
	if (
		getRequestIsLoading ||
		(showsPreviousResponse && !loadedGene) ||
		(loadedGene && geneState.gene?.id !== loadedGene.id)
	)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	// The API answers an identifier no gene has with an empty response rather than an error. A failed refetch keeps
	// the gene already loaded, so the form stays up.
	if (!loadedGene)
		return <DetailPageNotFound entityType="Gene" identifier={identifier} listPath="/genes" error={geneQueryError} />;

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<SaveProgressOverlay isOpen={saveProgress.isOpen} steps={saveProgress.steps} />
			<LoadingOverlay isLoading={!!genePutRequestIsLoading && !saveProgress.isOpen} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={50} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Gene"
								entity={geneState.gene}
								label={geneState.gene?.geneSymbol?.displayText}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={GENE_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
						<SplitterPanel size={20} className="flex align-items-center justify-content-start gap-2 pl-2 py-3">
							<Button
								label="Save"
								icon="pi pi-check"
								severity="success"
								onClick={handleSubmit}
								disabled={!hasPendingEdits || saveProgress.isSaving}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<SaveProgressProvider value={saveProgress}>
					<SubResourcesProvider value={subResources}>
						<GeneForm state={geneState} dispatch={geneDispatch} isVisible={isVisible} pendingFields={pendingFields} />
					</SubResourcesProvider>
				</SaveProgressProvider>
			</ErrorBoundary>
		</>
	);
}
