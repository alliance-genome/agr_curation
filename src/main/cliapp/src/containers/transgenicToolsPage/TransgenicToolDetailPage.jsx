import React, { useMemo, useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { TransgenicToolService } from '../../service/TransgenicToolService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { FormFieldVisibilityMenu, FormSection, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { FormTableWrapper } from '../../components/FormTableWrapper';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { DetailPageFieldWrapper } from '../../components/DetailPageFieldWrapper';
import { SymbolFormTable } from '../constructsPage/symbol/SymbolFormTable';
import { FullNameFormTable } from '../constructsPage/fullName/FullNameFormTable';
import { SynonymsFormTable } from '../constructsPage/synonyms/SynonymsFormTable';
import { SecondaryIdsFormTable } from '../constructsPage/secondaryIds/SecondaryIdsFormTable';
import { ReferencesFormTable } from '../constructsPage/references/ReferencesFormTable';
import { generateCrossRefSearchFields } from '../constructsPage/utils';
import { TransgenicToolUsesTable } from './TransgenicToolUsesTable';
import {
	TransgenicToolAssociationsTable,
	CrossReferencesReadOnlyTable,
	RelatedNotesReadOnlyTable,
} from './detail/ReadOnlyTables';

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

// The tool as the page shows it: its references carry the search fields the references table filters on
const toDisplayedTool = (entity) => {
	if (!entity) return {};
	const transgenicTool = structuredClone(entity);
	generateCrossRefSearchFields(transgenicTool.references);
	return transgenicTool;
};

/**
 * The detail page of one transgenic tool, read-only for now (SCRUM-6655): the fields of the
 * Transgenic Tools table, each in a section the curator can hide through the field menu.
 */
export default function TransgenicToolDetailPage() {
	const { identifier } = useParams();
	const transgenicToolService = useMemo(() => new TransgenicToolService(), []);
	const toastError = useRef(null);
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'TransgenicToolDetail',
		TRANSGENIC_TOOL_DETAIL_TOGGLEABLE_FIELDS
	);

	const { isPending, data } = useQuery({
		queryKey: ['transgenicTool', identifier],
		queryFn: () => transgenicToolService.getTransgenicTool(identifier),
		refetchOnWindowFocus: false,
	});

	const tool = useMemo(() => toDisplayedTool(data?.data?.entity), [data]);

	if (isPending)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	const booleanField = (fieldName, value) => (
		<DetailPageFieldWrapper
			{...columnSizes}
			fieldName={fieldName}
			formField={value?.toString() || 'false'}
			additionalDataField={value?.toString() || 'false'}
		/>
	);

	const tableField = (tableName, table) => <FormTableWrapper table={table} tableName={tableName} showTable={true} />;

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:min-h-5rem" gutterSize={0}>
						<SplitterPanel size={60} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Transgenic Tool"
								entity={tool}
								label={tool?.transgenicToolSymbol?.displayText}
							/>
						</SplitterPanel>
						<SplitterPanel size={40} className="flex align-items-center justify-content-end gap-2 pr-5 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={TRANSGENIC_TOOL_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				{/* The page saves nothing yet, and Enter in a table filter must not submit and reload it. */}
				<form className="mt-8" onSubmit={(event) => event.preventDefault()}>
					<FormSection isVisible={isVisible('Unique ID')}>
						<IdentifierDetailPageTemplate
							identifier={tool.uniqueId}
							label="Unique ID"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Curie')}>
						<IdentifierDetailPageTemplate
							identifier={tool.curie}
							label="Curie"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Primary External ID')}>
						<IdentifierDetailPageTemplate
							identifier={tool.primaryExternalId}
							label="Primary External ID"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('MOD Internal ID')}>
						<IdentifierDetailPageTemplate
							identifier={tool.modInternalId}
							label="MOD Internal ID"
							{...columnSizes}
							showAdditionalData={false}
						/>
					</FormSection>
					<FormSection isVisible={isVisible('Full Name')}>
						{tableField(
							'Full Name',
							<FullNameFormTable name={tool.transgenicToolFullName ? [tool.transgenicToolFullName] : []} />
						)}
					</FormSection>
					<FormSection isVisible={isVisible('Symbol')}>
						{tableField(
							'Symbol',
							<SymbolFormTable symbol={tool.transgenicToolSymbol ? [tool.transgenicToolSymbol] : []} />
						)}
					</FormSection>
					<FormSection isVisible={isVisible('Synonyms')}>
						{tableField('Synonyms', <SynonymsFormTable synonyms={tool.transgenicToolSynonyms || []} />)}
					</FormSection>
					<FormSection isVisible={isVisible('Secondary IDs')}>
						{tableField(
							'Secondary IDs',
							<SecondaryIdsFormTable
								secondaryIds={(tool.secondaryIdentifiers || []).map((id, index) => ({ id: index, identifier: id }))}
							/>
						)}
					</FormSection>
					<FormSection isVisible={isVisible('Transgenic Tool Uses')}>
						{tableField('Transgenic Tool Uses', <TransgenicToolUsesTable uses={tool.transgenicToolUses} />)}
					</FormSection>
					<FormSection isVisible={isVisible('Transgenic Tool Associations')}>
						{tableField('Transgenic Tool Associations', <TransgenicToolAssociationsTable transgenicToolId={tool.id} />)}
					</FormSection>
					<FormSection isVisible={isVisible('References')}>
						{tableField('References', <ReferencesFormTable references={tool.references || []} />)}
					</FormSection>
					<FormSection isVisible={isVisible('Related Notes')}>
						{tableField('Related Notes', <RelatedNotesReadOnlyTable relatedNotes={tool.relatedNotes} />)}
					</FormSection>
					<FormSection isVisible={isVisible('Data Provider')}>
						<DataProviderDetailPageTemplate dataProvider={tool.dataProvider?.abbreviation} {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Cross References')}>
						{tableField('Cross References', <CrossReferencesReadOnlyTable crossReferences={tool.crossReferences} />)}
					</FormSection>
					<FormSection isVisible={isVisible('Updated By')}>
						<UserDetailPageTemplate user={tool.updatedBy?.uniqueId} fieldName="Updated By" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Date Updated')}>
						<DateDetailPageTemplate date={tool.dateUpdated} fieldName="Date Updated" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Created By')}>
						<UserDetailPageTemplate user={tool.createdBy?.uniqueId} fieldName="Created By" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Date Created')}>
						<DateDetailPageTemplate date={tool.dateCreated} fieldName="Date Created" {...columnSizes} />
					</FormSection>
					<FormSection isVisible={isVisible('Placeholder')}>
						{booleanField('Placeholder', tool.placeholder)}
					</FormSection>
					<FormSection isVisible={isVisible('Internal')}>{booleanField('Internal', tool.internal)}</FormSection>
					<FormSection isVisible={isVisible('Obsolete')}>{booleanField('Obsolete', tool.obsolete)}</FormSection>
				</form>
			</ErrorBoundary>
		</>
	);
}
