import { useMemo, useRef, useState } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { GeneService } from '../../service/GeneService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { StickyFooter } from '../../components/StickyFooter';
import { SaveProgressOverlay } from '../../components/SaveProgressOverlay';
import { SubResourcesProvider } from '../../components/SubResourcesContext';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { useSaveProgress } from '../../hooks/useSaveProgress';
import { capitalize, errorMessageOf, getIdentifier } from '../../utils/utils';
import { useGeneReducer } from './useGeneReducer';
import { useGeneSubResources } from './useGeneSubResources';
import {
	buildDuplicateGene,
	buildGeneCreatePayload,
	buildGeneDetailPayload,
	processGeneErrors,
	withSeededGcrpCrossReference,
} from './utils';
import { GeneForm, GENE_CREATE_TOGGLEABLE_FIELDS } from './GeneForm';

export default function GeneCreatePage() {
	const navigate = useNavigate();
	const { geneState, geneDispatch } = useGeneReducer('create');
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'GeneCreate',
		GENE_CREATE_TOGGLEABLE_FIELDS
	);
	const geneService = useMemo(() => new GeneService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const {
		subResources,
		saveDirty: saveDirtySubResources,
		dirtySteps: dirtySubResourceSteps,
		resetAll: resetSubResources,
		carryOverAll: carryOverSubResources,
	} = useGeneSubResources();
	const saveProgress = useSaveProgress();
	// Set once Save & Close has saved everything, while the page shows that before moving on to the gene.
	const [isLeaving, setIsLeaving] = useState(false);
	// Held so that saving again after a list failed retries it against the gene that already exists, rather than
	// creating a second one.
	const createdGene = useRef(null);

	const { mutateAsync: createGene } = useMutation({
		mutationFn: (gene) => geneService.createGene(gene),
	});

	// Saving again once the gene exists updates it through updateDetail, which leaves the lists to their own
	// sub-resources.
	const { mutateAsync: updateGene } = useMutation({
		mutationFn: (gene) => geneService.saveGeneDetail(gene),
	});

	const showSaveError = (error) => {
		toastError.current.show([
			{ life: 7000, severity: 'error', summary: 'Page error: ', detail: errorMessageOf(error), sticky: false },
		]);

		try {
			processGeneErrors(error?.response?.data, geneDispatch, geneState.gene);
		} catch (processingError) {
			console.error(processingError);
		}
	};

	/**
	 * Writes each edited list to the gene, which exists by now, so a list that fails leaves the curator here to fix its
	 * rows and save again, or to finish them on the gene's detail page. Each list is written only while it differs from
	 * what the gene has stored, so saving again skips one that already saved.
	 *
	 * @param {Object} gene the gene as the API returned it
	 * @param {boolean} closeAfterSubmit whether to open the gene's detail page once everything is saved
	 */
	const saveSubResources = async (gene, closeAfterSubmit) => {
		const subResourceOutcomes = await saveDirtySubResources(gene.id, {
			onStart: saveProgress.start,
			onSettle: saveProgress.settle,
		});
		const failedSubResources = subResourceOutcomes.filter(({ outcome }) => !outcome.isSuccess);

		if (failedSubResources.length > 0) {
			// The form takes on the saved gene before saving is offered again, so its single-object sections carry the
			// ids that saving again needs.
			geneDispatch({ type: 'SET', value: await withSeededGcrpCrossReference(gene) });
			saveProgress.finish();
			toastError.current.show(
				failedSubResources.map(({ label, outcome }) => ({
					life: 10000,
					severity: 'error',
					summary: `${capitalize(label)} not saved: `,
					detail: `${outcome.message}. Gene ${getIdentifier(gene)} was created - fix the rows and save again, or finish them on its detail page.`,
					sticky: false,
				}))
			);
			return;
		}

		createdGene.current = null;

		if (closeAfterSubmit) {
			// The curator sees everything saved before the page moves on; saving again meanwhile is not offered.
			setIsLeaving(true);
			if (await saveProgress.finish()) navigate(`/gene/${getIdentifier(gene)}`);
		} else {
			// The form keeps the saved gene's values and rows, so the next gene can be entered as a small change to
			// this one.
			geneDispatch({ type: 'SET', value: await buildDuplicateGene(gene, isVisible) });
			await carryOverSubResources(isVisible);
			saveProgress.finish();
		}
	};

	const handleSubmit = async (event, closeAfterSubmit) => {
		event.preventDefault();
		geneDispatch({ type: 'SUBMIT' });
		saveProgress.begin([{ key: 'gene', label: 'Gene' }, ...dirtySubResourceSteps]);
		saveProgress.start('gene');

		const isRetry = !!createdGene.current;
		let gene;
		try {
			const result = isRetry
				? await updateGene(buildGeneDetailPayload(geneState.gene))
				: await createGene(buildGeneCreatePayload(geneState.gene));
			gene = result?.data?.entity;
		} catch (error) {
			saveProgress.settle('gene', { isSuccess: false });
			saveProgress.skipRemaining();
			saveProgress.finish();
			showSaveError(error);
			return;
		}

		createdGene.current = gene;
		saveProgress.settle('gene', { isSuccess: true });

		try {
			if (isRetry) {
				geneDispatch({ type: 'SET', value: await withSeededGcrpCrossReference(gene) });
			} else {
				toastSuccess.current.show({
					severity: 'success',
					summary: 'Successful',
					detail: `Gene ${getIdentifier(gene)} created`,
				});
			}

			await saveSubResources(gene, closeAfterSubmit);
		} catch (error) {
			// While the created gene is the one saving again will update, the form takes it on. Anything the save
			// stopped before writing shows as not saved, and the overlay still closes.
			if (!isRetry && createdGene.current === gene) geneDispatch({ type: 'SET', value: gene });
			saveProgress.skipRemaining();
			saveProgress.finish();
			toastError.current.show([
				{ life: 10000, severity: 'error', summary: 'Page error: ', detail: errorMessageOf(error), sticky: false },
			]);
		}
	};

	const handleSubmitAndClose = (event) => handleSubmit(event, true);
	const handleSubmitAndAddAnother = (event) => handleSubmit(event, false);

	const handleClear = (event) => {
		event.preventDefault();
		geneDispatch({ type: 'RESET' });
		resetSubResources();
		createdGene.current = null;
	};

	const handleCancel = (event) => {
		event.preventDefault();
		navigate('/genes');
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<SaveProgressOverlay isOpen={saveProgress.isOpen} steps={saveProgress.steps} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1>Add Gene</h1>
						</SplitterPanel>
						<SplitterPanel size={55} className="flex align-items-center justify-content-end gap-2 pr-5 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={GENE_CREATE_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<div className="pb-8">
					<SubResourcesProvider value={subResources}>
						<GeneForm state={geneState} dispatch={geneDispatch} isVisible={isVisible} mode="create" />
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
