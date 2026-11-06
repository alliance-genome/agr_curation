import { useRef, useEffect, useMemo } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { AffectedGenomicModelService } from '../../service/AffectedGenomicModelService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { useAGMReducer } from './useAGMReducer';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { processErrors } from './utils';
import { getIdentifier } from '../../utils/utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { AGMForm, AGM_DETAIL_TOGGLEABLE_FIELDS } from './AGMForm';
import { DuplicateAGMButton, NewAGMButton } from './NewAGMButton';
import { useDeleteOrDeprecateDialogs } from '../../components/DeleteOrDeprecateDialogs';

export default function AffectedGenomicModelDetailPage() {
	const { identifier } = useParams();
	const navigate = useNavigate();
	const { agmState, agmDispatch } = useAGMReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'AGMDetail',
		AGM_DETAIL_TOGGLEABLE_FIELDS
	);
	const agmService = useMemo(() => new AffectedGenomicModelService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const { isPending: getRequestIsLoading, data: agmQueryData } = useQuery({
		queryKey: [identifier],
		queryFn: () => agmService.getAGM(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (agmQueryData) {
			agmDispatch({ type: 'SET', value: agmQueryData?.data?.entity });
		}
	}, [agmQueryData, agmDispatch]);

	const { isPending: agmPutRequestIsLoading, mutate: agmMutate } = useMutation({
		mutationFn: (agm) => {
			return agmService.saveAGM(agm);
		},
	});

	const handleSubmit = async (event) => {
		event.preventDefault();
		agmDispatch({
			type: 'SUBMIT',
		});

		agmMutate(agmState.agm, {
			onSuccess: (result) => {
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'AGM Saved' });
				agmDispatch({ type: 'SET', value: result?.data?.entity });
			},
			onError: (error) => {
				let message;
				const data = error?.response?.data;

				if (data?.errorMessage) {
					message = data.errorMessage;
				} else {
					//toast will still display even if 500 error and no errorMessages
					message = `${error.response.status} ${error.response.statusText}`;
				}
				toastError.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: message, sticky: false },
				]);

				try {
					processErrors(data, agmDispatch, agmState.agm);
				} catch (e) {
					console.error(e);
				}
			},
		});
	};

	const deprecateAgm = async () => {
		try {
			const result = await agmService.saveAGM({ ...agmState.agm, obsolete: true });
			agmDispatch({ type: 'SET', value: result?.data?.entity });
		} catch (error) {
			const data = error?.response?.data;
			toastError.current.show([
				{
					life: 7000,
					severity: 'error',
					summary: 'AGM not deprecated: ',
					detail:
						data?.errorMessage ??
						(error?.response ? `${error.response.status} ${error.response.statusText}`.trim() : error?.message),
					sticky: false,
				},
			]);
			return;
		}
		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'AGM Deprecated' });
	};

	const deleteAgm = async (id, agm) => {
		const result = await agmService.deleteAGM(agm);
		if (result.isError) {
			toastError.current.show([
				{ life: 7000, severity: 'error', summary: `Could not delete AGM ${getIdentifier(agm)}`, sticky: false },
			]);
			return result.message ? result.message : null;
		}
		navigate('/agms');
		return null;
	};

	const { openDeleteOrDeprecateDialog, deleteOrDeprecateDialogs } = useDeleteOrDeprecateDialogs({
		deprecateOption: true,
		onDelete: deleteAgm,
		onDeprecate: deprecateAgm,
	});

	if (getRequestIsLoading)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!agmPutRequestIsLoading} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:min-h-5rem" gutterSize={0}>
						<SplitterPanel size={40} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="AGM"
								entity={agmState.agm}
								label={agmState.agm?.agmFullName?.displayText}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={AGM_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-start gap-2 pl-2 py-3">
							<Button label="Save" icon="pi pi-check" className="p-button-text" size="large" onClick={handleSubmit} />
							<NewAGMButton className="p-button-text" />
							<DuplicateAGMButton
								className="p-button-text"
								sourceIdentifier={agmState.agm?.curie || getIdentifier(agmState.agm) || identifier}
							/>
							<Button
								label="Delete"
								icon="pi pi-trash"
								className="p-button-text"
								disabled={!agmState.agm?.id}
								onClick={() => openDeleteOrDeprecateDialog(agmState.agm.id, agmState.agm)}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<AGMForm state={agmState} dispatch={agmDispatch} isVisible={isVisible} />
			</ErrorBoundary>
			{deleteOrDeprecateDialogs}
		</>
	);
}
