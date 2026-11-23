import { useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { DetailPageFieldWrapper } from '../../components/DetailPageFieldWrapper';
import { FormTableWrapper } from '../../components/FormTableWrapper';
import { FormFieldVisibilityMenu, FormSection, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { ReferencesFormTable } from './ReferencesFormTable';
import { ExternalDataBaseEntityTable } from './ExternalDataBaseEntityTable';
import { RelatedNotesTable } from './RelatedNotesTable';
import { HTPExpressionDatasetSampleAnnotationsTable } from '../htpExpressionDatasetSampleAnnotationsPage/HTPExpressionDatasetSampleAnnotationsTable';
import { HTPExpressionDatasetAnnotationService } from '../../service/HTPExpressionDatasetAnnotationService';

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-9';

export const HTP_EXPRESSION_DATASET_DETAIL_TOGGLEABLE_FIELDS = [
	'Expression Dataset',
	'Dataset Name',
	'Dataset Subseries',
	'Dataset Category Tags',
	'Dataset Number of Channels',
	'Dataset Laboratory of Origin',
	'Dataset Related Notes',
	'Dataset References',
	'Dataset Sample Annotations',
	'Dataset Data Provider',
	'Dataset Updated By',
	'Dataset Date Updated',
	'Dataset Created By',
	'Dataset Date Created',
	'Dataset Internal',
	'Dataset Obsolete',
];

const datasetSampleAnnotationsFilter = (datasetCurie) =>
	datasetCurie
		? { datasetSampleAnnotationsFilter: { 'datasetIds.curie': { queryString: datasetCurie, tokenOperator: 'AND' } } }
		: null;

/**
 * Read-only detail page for a single HTP Expression Dataset Annotation (SCRUM-6174), including
 * its own sample annotations, scoped via fixedFilters on the shared sample annotations table.
 */
export default function HTPExpressionDatasetAnnotationDetailPage() {
	const { identifier } = useParams();
	const htpExpressionDatasetAnnotationService = useMemo(() => new HTPExpressionDatasetAnnotationService(), []);
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'HTPExpressionDatasetDetail',
		HTP_EXPRESSION_DATASET_DETAIL_TOGGLEABLE_FIELDS
	);

	const { isPending, data: queryData } = useQuery({
		queryKey: [identifier],
		queryFn: () => htpExpressionDatasetAnnotationService.getHTPExpressionDatasetAnnotation(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	const dataset = queryData?.data?.entity;

	if (isPending)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	return (
		<>
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={70} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="HTP Expression Dataset"
								entity={dataset}
								label={dataset?.name}
								identifier={dataset?.htpExpressionDataset?.curie}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={HTP_EXPRESSION_DATASET_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<div className="mt-8">
					<FormSection isVisible={isVisible('Expression Dataset')}>
						<FormTableWrapper
							tableName="Expression Dataset"
							showTable={!!dataset?.htpExpressionDataset}
							table={
								<ExternalDataBaseEntityTable
									entities={dataset?.htpExpressionDataset ? [dataset.htpExpressionDataset] : []}
								/>
							}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Name')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Name"
							formField={<StringTemplate string={dataset?.name} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Subseries')}>
						<FormTableWrapper
							tableName="Dataset Subseries"
							showTable={dataset?.subSeries?.length > 0}
							table={<ExternalDataBaseEntityTable entities={dataset?.subSeries} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Category Tags')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Category Tags"
							formField={<StringListTemplate list={dataset?.categoryTags?.map((term) => term.name)} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Number of Channels')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Number of Channels"
							formField={<StringTemplate string={dataset?.numberOfChannels} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Laboratory of Origin')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Laboratory of Origin"
							formField={<StringTemplate string={dataset?.laboratoryOfOrigin} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Related Notes')}>
						<FormTableWrapper
							tableName="Dataset Related Notes"
							showTable={!!dataset?.relatedNote}
							table={<RelatedNotesTable relatedNotes={dataset?.relatedNote ? [dataset.relatedNote] : []} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset References')}>
						<FormTableWrapper
							tableName="Dataset References"
							showTable={dataset?.references?.length > 0}
							table={<ReferencesFormTable references={dataset?.references || []} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Sample Annotations')}>
						<FormTableWrapper
							tableName="Dataset Sample Annotations"
							showTable={!!dataset}
							table={
								<HTPExpressionDatasetSampleAnnotationsTable
									tableName="Dataset Sample Annotations"
									fixedFilters={datasetSampleAnnotationsFilter(dataset?.htpExpressionDataset?.curie)}
								/>
							}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Data Provider')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Data Provider"
							formField={<StringTemplate string={dataset?.dataProvider?.abbreviation} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Updated By')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Updated By"
							formField={<StringTemplate string={dataset?.updatedBy?.uniqueId} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Date Updated')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Date Updated"
							formField={<StringTemplate string={dataset?.dateUpdated} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Created By')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Created By"
							formField={<StringTemplate string={dataset?.createdBy?.uniqueId} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Date Created')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Date Created"
							formField={<StringTemplate string={dataset?.dateCreated} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Internal')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Internal"
							formField={<BooleanTemplate value={dataset?.internal} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Dataset Obsolete')}>
						<DetailPageFieldWrapper
							labelColumnSize={labelColumnSize}
							widgetColumnSize={widgetColumnSize}
							showAdditionalData={false}
							fieldName="Dataset Obsolete"
							formField={<BooleanTemplate value={dataset?.obsolete} />}
						/>
					</FormSection>
				</div>
			</ErrorBoundary>
		</>
	);
}
