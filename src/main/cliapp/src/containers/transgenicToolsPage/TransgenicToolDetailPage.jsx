import React, { useEffect, useMemo, useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { TransgenicToolService } from '../../service/TransgenicToolService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { seedResourceDescriptors } from '../../components/crossReferences/utils';
import { processErrors } from '../affectedGenomicModelPage/utils';
import { useTransgenicToolReducer } from './detail/useTransgenicToolReducer';
import { useTransgenicToolAssociations } from './detail/useTransgenicToolAssociations';
import { buildTransgenicToolPayload } from './detail/utils';
import { TransgenicToolForm, TRANSGENIC_TOOL_DETAIL_TOGGLEABLE_FIELDS } from './TransgenicToolForm';
import { NewTransgenicToolButton } from './NewTransgenicToolButton';

// The message of a rejected request: the API's, or its status when it gave none.
const errorDetail = (error) =>
	error?.response?.data?.errorMessage ??
	(error?.response ? `${error.response.status} ${error.response.statusText}`.trim() : error?.message);

/**
 * The detail page of one transgenic tool (SCRUM-6655): the fields of the Transgenic Tools table, each in a
 * section the curator can hide through the field menu. Save writes the tool, and its tool associations
 * through their own endpoint when they have been edited. New Transgenic Tool opens the create page in a new
 * tab (SCRUM-6691).
 */
export default function TransgenicToolDetailPage() {
	const { identifier } = useParams();
	const transgenicToolService = useMemo(() => new TransgenicToolService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const { transgenicToolState, transgenicToolDispatch } = useTransgenicToolReducer();
	const transgenicTool = transgenicToolState.transgenicTool;
	const associations = useTransgenicToolAssociations(transgenicTool?.id);
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'TransgenicToolDetail',
		TRANSGENIC_TOOL_DETAIL_TOGGLEABLE_FIELDS
	);

	const { isPending, data } = useQuery({
		queryKey: ['transgenicTool', identifier],
		queryFn: () => transgenicToolService.getTransgenicTool(identifier),
		refetchOnWindowFocus: false,
	});

	// Shows a tool the API returned. Its cross references are first given their descriptors, so the page
	// dropdown can offer each descriptor's pages.
	const showTransgenicTool = async (entity) => {
		if (!entity) return;
		const crossReferences = await seedResourceDescriptors(entity.crossReferences);
		transgenicToolDispatch({ type: 'SET', value: { ...entity, crossReferences } });
	};

	useEffect(() => {
		showTransgenicTool(data?.data?.entity);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [data]);

	const { isPending: isSavingTool, mutateAsync: saveTransgenicTool } = useMutation({
		mutationFn: (tool) => transgenicToolService.saveTransgenicTool(tool),
	});

	const hasPendingEdits = transgenicToolState.hasPendingEdits || associations.isDirty;

	const handleSubmit = async (event) => {
		event.preventDefault();
		transgenicToolDispatch({ type: 'SUBMIT' });

		let toolError = null;
		if (transgenicToolState.hasPendingEdits) {
			try {
				const result = await saveTransgenicTool(buildTransgenicToolPayload(transgenicTool));
				await showTransgenicTool(result?.data?.entity);
			} catch (error) {
				toolError = error;
				try {
					processErrors(error?.response?.data, transgenicToolDispatch, transgenicTool);
				} catch (processingError) {
					console.error(processingError);
				}
			}
		}

		// The associations are written whether or not the tool saved, so the errors of both are reported together.
		const associationsOutcome = associations.isDirty ? await associations.save() : null;

		const failures = [];
		if (toolError) failures.push({ summary: 'Transgenic tool not saved: ', detail: errorDetail(toolError) });
		if (associationsOutcome && !associationsOutcome.isSuccess) {
			failures.push({ summary: 'Tool associations not saved: ', detail: associationsOutcome.message });
		}

		if (failures.length > 0) {
			toastError.current.show(
				failures.map((failure) => ({ life: 10000, severity: 'error', sticky: false, ...failure }))
			);
			return;
		}
		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Transgenic Tool Saved' });
	};

	if (isPending)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={isSavingTool || associations.isSaving} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:min-h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Transgenic Tool"
								entity={transgenicTool}
								label={transgenicTool?.transgenicToolSymbol?.displayText}
							/>
						</SplitterPanel>
						<SplitterPanel size={25} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={TRANSGENIC_TOOL_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-start gap-2 pl-2 py-3">
							<Button
								label="Save"
								icon="pi pi-check"
								severity="success"
								onClick={handleSubmit}
								disabled={!hasPendingEdits || isSavingTool || associations.isSaving}
							/>
							<NewTransgenicToolButton className="p-button-text" />
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<TransgenicToolForm
					state={transgenicToolState}
					dispatch={transgenicToolDispatch}
					associations={associations}
					isVisible={isVisible}
				/>
			</ErrorBoundary>
		</>
	);
}
