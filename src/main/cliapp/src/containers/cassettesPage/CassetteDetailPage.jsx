import React, { useEffect, useMemo, useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { CassetteService } from '../../service/CassetteService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { processErrors } from '../affectedGenomicModelPage/utils';
import { getIdentifier } from '../../utils/utils';
import {
	FREE_TEXT_COMPONENTS,
	GENOMIC_ENTITY_COMPONENTS,
	TRANSGENIC_TOOL_COMPONENTS,
	STR_COMPONENTS,
} from './components/componentConfigs';
import { useCassetteReducer } from './detail/useCassetteReducer';
import { useCassetteComponentList } from './detail/useCassetteComponentList';
import { buildCassettePayload } from './detail/utils';
import { CassetteForm, CASSETTE_DETAIL_TOGGLEABLE_FIELDS } from './CassetteForm';
import { NewCassetteButton, DuplicateCassetteButton } from './NewCassetteButton';
import { useDeleteOrDeprecateDialogs } from '../../components/DeleteOrDeprecateDialogs';

// The message of a rejected request: the API's, or its status when it gave none.
const errorDetail = (error) =>
	error?.response?.data?.errorMessage ??
	(error?.response ? `${error.response.status} ${error.response.statusText}`.trim() : error?.message);

/**
 * The detail page of one cassette (SCRUM-6747): the fields of the Cassettes table, each in a section the curator can
 * hide through the field menu. Save writes the cassette, then each of its component lists that has been edited
 * through the list's own endpoint. Delete offers to deprecate the cassette, or, once confirmed, deletes it (SCRUM-6749).
 */
export default function CassetteDetailPage() {
	const { identifier } = useParams();
	const navigate = useNavigate();
	const queryClient = useQueryClient();
	const cassetteService = useMemo(() => new CassetteService(), []);
	// The cassette as last read or saved, which deprecation writes rather than the form's unsaved edits
	const savedCassette = useRef(null);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const { cassetteState, cassetteDispatch } = useCassetteReducer();
	const cassette = cassetteState.cassette;
	const freeTextComponents = useCassetteComponentList(cassette?.id, FREE_TEXT_COMPONENTS);
	const genomicEntityComponents = useCassetteComponentList(cassette?.id, GENOMIC_ENTITY_COMPONENTS);
	const transgenicToolComponents = useCassetteComponentList(cassette?.id, TRANSGENIC_TOOL_COMPONENTS);
	const strComponents = useCassetteComponentList(cassette?.id, STR_COMPONENTS);
	const componentSections = [
		{ config: FREE_TEXT_COMPONENTS, components: freeTextComponents },
		{ config: GENOMIC_ENTITY_COMPONENTS, components: genomicEntityComponents },
		{ config: TRANSGENIC_TOOL_COMPONENTS, components: transgenicToolComponents },
		{ config: STR_COMPONENTS, components: strComponents },
	];
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'CassetteDetail',
		CASSETTE_DETAIL_TOGGLEABLE_FIELDS
	);

	const { isPending, data } = useQuery({
		queryKey: ['cassette', identifier],
		queryFn: () => cassetteService.getCassette(identifier),
		refetchOnWindowFocus: false,
	});

	useEffect(() => {
		if (data?.data?.entity) {
			savedCassette.current = data.data.entity;
			cassetteDispatch({ type: 'SET', value: data.data.entity });
		}
	}, [data, cassetteDispatch]);

	const { isPending: isSavingCassette, mutateAsync: saveCassette } = useMutation({
		mutationFn: (payload) => cassetteService.saveCassette(payload),
	});

	const isSavingComponents = componentSections.some(({ components }) => components.isSaving);
	const hasPendingEdits =
		cassetteState.hasPendingEdits || componentSections.some(({ components }) => components.isDirty);

	const handleSubmit = async (event) => {
		event.preventDefault();
		cassetteDispatch({ type: 'SUBMIT' });

		const failures = [];
		if (cassetteState.hasPendingEdits) {
			try {
				const result = await saveCassette(buildCassettePayload(cassette));
				if (result?.data?.entity) {
					savedCassette.current = result.data.entity;
					cassetteDispatch({ type: 'SET', value: result.data.entity });
				}
			} catch (error) {
				failures.push({ summary: 'Cassette not saved: ', detail: errorDetail(error) });
				try {
					processErrors(error?.response?.data, cassetteDispatch, cassette);
				} catch (processingError) {
					console.error(processingError);
				}
			}
		}

		// The component lists are written whether or not the cassette saved, so the errors of all are reported together.
		for (const { config, components } of componentSections) {
			if (!components.isDirty) continue;
			const outcome = await components.save();
			if (!outcome.isSuccess) failures.push({ summary: `${config.title} not saved: `, detail: outcome.message });
		}

		if (failures.length > 0) {
			toastError.current.show(
				failures.map((failure) => ({ life: 10000, severity: 'error', sticky: false, ...failure }))
			);
			return;
		}
		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Cassette Saved' });
	};

	/**
	 * Deprecates the cassette as last saved, so the form's unsaved edits are not written with it. A form without edits
	 * then shows the deprecated cassette; one with edits keeps them, now obsolete. A refused deprecation leaves the form
	 * as it was.
	 */
	const deprecateCassette = async () => {
		try {
			const result = await saveCassette(buildCassettePayload({ ...savedCassette.current, obsolete: true }));
			savedCassette.current = result?.data?.entity;
		} catch (error) {
			toastError.current.show([
				{
					life: 7000,
					severity: 'error',
					summary: 'Cassette not deprecated: ',
					detail: errorDetail(error),
					sticky: false,
				},
			]);
			return;
		}
		if (cassetteState.hasPendingEdits) {
			cassetteDispatch({ type: 'EDIT', field: 'obsolete', value: true });
		} else if (savedCassette.current) {
			cassetteDispatch({ type: 'SET', value: savedCassette.current });
		}
		queryClient.invalidateQueries({ queryKey: ['Cassettes'] });
		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Cassette Deprecated' });
	};

	// Deletes the cassette and returns to the Cassettes table; a refusal returns the API's reason for the dialog to show
	const deleteCassette = async (id, cassetteToDelete) => {
		const result = await cassetteService.deleteCassette(cassetteToDelete);
		if (result.isError) {
			toastError.current.show([
				{
					life: 7000,
					severity: 'error',
					summary: `Could not delete cassette ${cassetteToDelete.curie || getIdentifier(cassetteToDelete)}`,
					sticky: false,
				},
			]);
			return result.message ? result.message : null;
		}
		queryClient.invalidateQueries({ queryKey: ['Cassettes'] });
		navigate('/cassettes');
		return null;
	};

	const { openDeleteOrDeprecateDialog, deleteOrDeprecateDialogs } = useDeleteOrDeprecateDialogs({
		deprecateOption: true,
		onDelete: deleteCassette,
		onDeprecate: deprecateCassette,
	});

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
			<LoadingOverlay isLoading={isSavingCassette || isSavingComponents} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:min-h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Cassette"
								entity={cassette}
								label={cassette?.cassetteSymbol?.displayText}
							/>
						</SplitterPanel>
						<SplitterPanel size={25} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={CASSETTE_DETAIL_TOGGLEABLE_FIELDS}
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
								disabled={!hasPendingEdits || isSavingCassette || isSavingComponents}
							/>
							<NewCassetteButton className="p-button-text" />
							<DuplicateCassetteButton
								className="p-button-text"
								sourceIdentifier={cassette?.curie || getIdentifier(cassette) || identifier}
							/>
							<Button
								label="Delete"
								icon="pi pi-trash"
								className="p-button-text"
								disabled={!cassette?.id}
								onClick={() => openDeleteOrDeprecateDialog(cassette.id, cassette)}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<CassetteForm
					state={cassetteState}
					dispatch={cassetteDispatch}
					componentSections={componentSections}
					isVisible={isVisible}
				/>
			</ErrorBoundary>
			{deleteOrDeprecateDialogs}
		</>
	);
}
