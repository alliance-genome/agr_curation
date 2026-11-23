import { useEffect, useMemo, useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { ConstructService } from '../../service/ConstructService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { DetailPageNotFound } from '../../components/DetailPageNotFound';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { SaveProgressOverlay, SaveProgressProvider } from '../../components/SaveProgressOverlay';
import { SubResourcesProvider } from '../../components/SubResourcesContext';
import { useSaveProgress } from '../../hooks/useSaveProgress';
import { processErrors } from '../../utils/processErrors';
import { capitalize, errorMessageOf } from '../../utils/utils';
import { useConstructReducer } from './useConstructReducer';
import { useConstructSubResources } from './useConstructSubResources';
import { getPendingConstructFields } from './utils';
import { ConstructForm } from './ConstructForm';

export default function ConstructDetailPage() {
	const { identifier } = useParams();
	const { constructState, constructDispatch } = useConstructReducer();
	const constructService = useMemo(() => new ConstructService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const {
		subResources,
		saveDirty: saveSubResources,
		isDirty: subResourcesAreDirty,
		dirtySteps: dirtySubResourceSteps,
	} = useConstructSubResources(constructState.construct?.id);
	const saveProgress = useSaveProgress();
	// The construct as the API last returned it, without the form's unsaved edits.
	const savedConstruct = useRef(null);

	const {
		isPending: getRequestIsLoading,
		data: constructQueryData,
		error: constructQueryError,
		isPlaceholderData: showsPreviousResponse,
	} = useQuery({
		queryKey: ['construct', identifier],
		queryFn: () => constructService.getConstruct(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	/**
	 * Shows a construct the API returned.
	 *
	 * @param {Object} [entity] the construct as the API returned it
	 */
	const showConstruct = (entity) => {
		if (!entity) return;
		savedConstruct.current = entity;
		constructDispatch({ type: 'SET', value: entity });
	};

	useEffect(() => {
		showConstruct(constructQueryData?.data?.entity);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [constructQueryData]);

	const { isPending: constructPutRequestIsLoading, mutateAsync: saveConstruct } = useMutation({
		mutationFn: (construct) => constructService.saveConstructDetail(construct),
	});

	/**
	 * Marks a rejected construct's field errors on the form.
	 *
	 * @param {Object} error the rejected request's error
	 * @returns {string} the message describing the rejection
	 */
	const reportConstructErrors = (error) => {
		try {
			processErrors(error?.response?.data, constructDispatch, constructState.construct);
		} catch (processingError) {
			console.error(processingError);
		}

		return errorMessageOf(error);
	};

	const handleSubmit = async (event) => {
		event.preventDefault();
		constructDispatch({ type: 'SUBMIT' });

		saveProgress.begin([{ key: 'construct', label: 'Construct' }, ...dirtySubResourceSteps]);
		let constructError = null;
		let subResourceOutcomes;
		try {
			saveProgress.start('construct');
			try {
				const constructResult = await saveConstruct(constructState.construct);
				showConstruct(constructResult?.data?.entity);
			} catch (error) {
				constructError = error;
			}
			saveProgress.settle('construct', { isSuccess: !constructError });

			// The lists are written whether or not the construct saved, so the errors of all of them are reported
			// together.
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

		if (constructError) {
			const constructMessage = reportConstructErrors(constructError);

			if (subResourceOutcomes.length === 0) {
				toastError.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: constructMessage, sticky: false },
				]);
			} else {
				toastError.current.show([
					{
						life: 10000,
						severity: 'error',
						summary: 'Construct not saved: ',
						detail:
							savedLabels.length > 0
								? `${constructMessage}. The ${new Intl.ListFormat('en').format(savedLabels)} were saved.`
								: constructMessage,
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
					failureToast(failure, `${failure.outcome.message}. The construct's other changes were saved.`)
				)
			);
			return;
		}

		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Construct Saved' });
	};

	const pendingFields = getPendingConstructFields(constructState.construct, savedConstruct.current);
	const hasPendingEdits = pendingFields.size > 0 || subResourcesAreDirty;

	const loadedConstruct = constructQueryData?.data?.entity;

	if (
		getRequestIsLoading ||
		(showsPreviousResponse && !loadedConstruct) ||
		(loadedConstruct && constructState.construct?.id !== loadedConstruct.id)
	)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	// The API answers an identifier no construct has with an empty response rather than an error. A failed refetch
	// keeps the construct already loaded, so the form stays up.
	if (!loadedConstruct)
		return (
			<DetailPageNotFound
				entityType="Construct"
				identifier={identifier}
				listPath="/constructs"
				error={constructQueryError}
			/>
		);

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<SaveProgressOverlay isOpen={saveProgress.isOpen} steps={saveProgress.steps} />
			<LoadingOverlay isLoading={!!constructPutRequestIsLoading && !saveProgress.isOpen} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={70} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Construct"
								entity={constructState.construct}
								label={constructState.construct?.constructSymbol?.displayText}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-start gap-2 pl-2 py-3">
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
						<ConstructForm state={constructState} dispatch={constructDispatch} pendingFields={pendingFields} />
					</SubResourcesProvider>
				</SaveProgressProvider>
			</ErrorBoundary>
		</>
	);
}
