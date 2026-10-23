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
import { FormFieldVisibilityMenu, FormSection, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { seedResourceDescriptors } from '../allelesPage/crossReferences/utils';
import { processErrors } from '../affectedGenomicModelPage/utils';
import { useTransgenicToolReducer } from './detail/useTransgenicToolReducer';
import { useTransgenicToolAssociations } from './detail/useTransgenicToolAssociations';
import { buildTransgenicToolPayload } from './detail/utils';
import { NameSlotAnnotationForm } from './detail/NameSlotAnnotationForm';
import { SynonymsForm } from './detail/SynonymsForm';
import { SecondaryIdsForm } from './detail/SecondaryIdsForm';
import { UsesForm } from './detail/UsesForm';
import { TransgenicToolAssociationsForm } from './detail/TransgenicToolAssociationsForm';
import { ReferencesForm } from './detail/ReferencesForm';
import { RelatedNotesForm } from './detail/RelatedNotesForm';
import { CrossReferencesForm } from './detail/CrossReferencesForm';

// The fields of the page, in order; each can be hidden through the field menu (SCRUM-6655)
export const TRANSGENIC_TOOL_DETAIL_TOGGLEABLE_FIELDS = [
	'Unique ID',
	'Curie',
	'Primary External ID',
	'MOD Internal ID',
	'Full Name',
	'Symbol',
	'Synonyms',
	'Secondary IDs',
	'Transgenic Tool Uses',
	'Transgenic Tool Associations',
	'References',
	'Related Notes',
	'Data Provider',
	'Cross References',
	'Updated By',
	'Date Updated',
	'Created By',
	'Date Created',
	'Placeholder',
	'Internal',
	'Obsolete',
];

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-4';
const fieldDetailsColumnSize = 'col-5';

const columnSizes = { labelColumnSize, widgetColumnSize, fieldDetailsColumnSize };

// The message of a rejected request: the API's, or its status when it gave none.
const errorDetail = (error) =>
	error?.response?.data?.errorMessage ??
	(error?.response ? `${error.response.status} ${error.response.statusText}`.trim() : error?.message);

/**
 * The detail page of one transgenic tool (SCRUM-6655): the fields of the Transgenic Tools table, each in a
 * section the curator can hide through the field menu. Save writes the tool, and its tool associations
 * through their own endpoint when they have been edited.
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

	const onBooleanChange = (field) => (event) => transgenicToolDispatch({ type: 'EDIT', field, value: event.value });

	const booleanField = (field, label) => (
		<BooleanDetailPageEditor
			value={transgenicTool?.[field]}
			name={field}
			label={label}
			onValueChange={onBooleanChange(field)}
			errorMessages={transgenicToolState.errorMessages}
			{...columnSizes}
		/>
	);

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={isSavingTool || associations.isSaving} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:min-h-5rem" gutterSize={0}>
						<SplitterPanel size={50} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Transgenic Tool"
								entity={transgenicTool}
								label={transgenicTool?.transgenicToolSymbol?.displayText}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={TRANSGENIC_TOOL_DETAIL_TOGGLEABLE_FIELDS}
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
								disabled={!hasPendingEdits || isSavingTool || associations.isSaving}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				{/* Saving is the header's Save; Enter in a table filter or field must not submit and reload the page. */}
				<form className="mt-8" onSubmit={(event) => event.preventDefault()}>
					<FormSection isVisible={isVisible('Unique ID')}>
						<IdentifierDetailPageTemplate
							identifier={transgenicTool?.uniqueId}
							label="Unique ID"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Curie')}>
						<IdentifierDetailPageTemplate
							identifier={transgenicTool?.curie}
							label="Curie"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Primary External ID')}>
						<IdentifierDetailPageTemplate
							identifier={transgenicTool?.primaryExternalId}
							label="Primary External ID"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('MOD Internal ID')}>
						<IdentifierDetailPageTemplate
							identifier={transgenicTool?.modInternalId}
							label="MOD Internal ID"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Full Name')}>
						<NameSlotAnnotationForm
							state={transgenicToolState}
							dispatch={transgenicToolDispatch}
							entityType="transgenicToolFullName"
							tableName="Full Name"
							addLabel="Add Full Name"
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Symbol')}>
						<NameSlotAnnotationForm
							state={transgenicToolState}
							dispatch={transgenicToolDispatch}
							entityType="transgenicToolSymbol"
							tableName="Symbol"
							addLabel="Add Symbol"
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Synonyms')}>
						<SynonymsForm state={transgenicToolState} dispatch={transgenicToolDispatch} />
					</FormSection>
					<FormSection isVisible={isVisible('Secondary IDs')}>
						<SecondaryIdsForm state={transgenicToolState} dispatch={transgenicToolDispatch} {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Transgenic Tool Uses')}>
						<UsesForm state={transgenicToolState} dispatch={transgenicToolDispatch} />
					</FormSection>
					<FormSection isVisible={isVisible('Transgenic Tool Associations')}>
						<TransgenicToolAssociationsForm associations={associations} />
					</FormSection>
					<FormSection isVisible={isVisible('References')}>
						<ReferencesForm state={transgenicToolState} dispatch={transgenicToolDispatch} />
					</FormSection>
					<FormSection isVisible={isVisible('Related Notes')}>
						<RelatedNotesForm state={transgenicToolState} dispatch={transgenicToolDispatch} />
					</FormSection>
					<FormSection isVisible={isVisible('Data Provider')}>
						<DataProviderDetailPageTemplate
							dataProvider={transgenicTool?.dataProvider?.abbreviation}
							{...columnSizes}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Cross References')}>
						<CrossReferencesForm state={transgenicToolState} dispatch={transgenicToolDispatch} />
					</FormSection>
					<FormSection isVisible={isVisible('Updated By')}>
						<UserDetailPageTemplate
							user={transgenicTool?.updatedBy?.uniqueId}
							fieldName="Updated By"
							{...columnSizes}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Date Updated')}>
						<DateDetailPageTemplate date={transgenicTool?.dateUpdated} fieldName="Date Updated" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Created By')}>
						<UserDetailPageTemplate
							user={transgenicTool?.createdBy?.uniqueId}
							fieldName="Created By"
							{...columnSizes}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Date Created')}>
						<DateDetailPageTemplate date={transgenicTool?.dateCreated} fieldName="Date Created" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Placeholder')}>{booleanField('placeholder', 'Placeholder')}</FormSection>
					<FormSection isVisible={isVisible('Internal')}>{booleanField('internal', 'Internal')}</FormSection>
					<FormSection isVisible={isVisible('Obsolete')}>{booleanField('obsolete', 'Obsolete')}</FormSection>
				</form>
			</ErrorBoundary>
		</>
	);
}
