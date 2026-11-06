import { useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ProgressSpinner } from 'primereact/progressspinner';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { DetailPageFieldWrapper } from '../../components/DetailPageFieldWrapper';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { ExternalDataBaseEntityReadOnlyDialog } from '../../components/ExternalDataBaseEntityReadOnlyDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { HTPExpressionDatasetSampleAnnotationsTable } from '../htpExpressionDatasetSampleAnnotationsPage/HTPExpressionDatasetSampleAnnotationsTable';
import { HTPExpressionDatasetAnnotationService } from '../../service/HTPExpressionDatasetAnnotationService';

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-9';

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

	const [expressionDatasetData, setExpressionDatasetData] = useState({ dialog: false });
	const [subseriesData, setSubseriesData] = useState({ dialog: false });
	const [relatedNoteData, setRelatedNoteData] = useState({
		originalRelatedNotes: [],
		dialog: false,
		isInEdit: false,
	});

	const handleExpressionDatasetOpen = (entities) =>
		setExpressionDatasetData({ originalEntities: entities, dialog: true });
	const handleSubseriesOpen = (entities) => setSubseriesData({ originalEntities: entities, dialog: true });
	const handleRelatedNoteOpen = (entities) =>
		setRelatedNoteData({ originalRelatedNotes: entities, dialog: true, isInEdit: false });

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
					<DetailPageHeading entityType="HTP Expression Dataset" entity={dataset} label={dataset?.name} />
				</StickyHeader>
				<div className="card">
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Expression Dataset"
						formField={
							<ListDialogTemplate
								entities={dataset?.htpExpressionDataset ? [dataset.htpExpressionDataset] : []}
								handleOpen={handleExpressionDatasetOpen}
								getTextField={(entity) => entity?.curie}
								underline={true}
							/>
						}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Name"
						formField={<StringTemplate string={dataset?.name} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Subseries"
						formField={
							<ListDialogTemplate
								entities={dataset?.subSeries}
								handleOpen={handleSubseriesOpen}
								getTextField={(entity) => entity?.curie}
								underline={true}
							/>
						}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Category Tags"
						formField={<StringListTemplate list={dataset?.categoryTags?.map((term) => term.name)} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Number of Channels"
						formField={<StringTemplate string={dataset?.numberOfChannels} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Laboratory of Origin"
						formField={<StringTemplate string={dataset?.laboratoryOfOrigin} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Related Notes"
						formField={
							<CountDialogTemplate
								entities={dataset?.relatedNote ? [dataset.relatedNote] : []}
								handleOpen={handleRelatedNoteOpen}
								text={'Notes'}
							/>
						}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset References"
						formField={<TruncatedReferencesTemplate references={dataset?.references} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Data Provider"
						formField={<StringTemplate string={dataset?.dataProvider?.abbreviation} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Updated By"
						formField={<StringTemplate string={dataset?.updatedBy?.uniqueId} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Date Updated"
						formField={<StringTemplate string={dataset?.dateUpdated} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Created By"
						formField={<StringTemplate string={dataset?.createdBy?.uniqueId} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Date Created"
						formField={<StringTemplate string={dataset?.dateCreated} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Internal"
						formField={<BooleanTemplate value={dataset?.internal} />}
					/>
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						widgetColumnSize={widgetColumnSize}
						showAdditionalData={false}
						fieldName="Dataset Obsolete"
						formField={<BooleanTemplate value={dataset?.obsolete} />}
					/>
					<h2>Dataset Sample Annotations</h2>
					{dataset && (
						<HTPExpressionDatasetSampleAnnotationsTable
							tableName="Dataset Sample Annotations"
							fixedFilters={datasetSampleAnnotationsFilter(dataset?.htpExpressionDataset?.curie)}
						/>
					)}
				</div>
			</ErrorBoundary>
			<ExternalDataBaseEntityReadOnlyDialog
				originalEntitiesData={expressionDatasetData}
				setOriginalEntitiesData={setExpressionDatasetData}
				title="Expression Dataset"
			/>
			<ExternalDataBaseEntityReadOnlyDialog
				originalEntitiesData={subseriesData}
				setOriginalEntitiesData={setSubseriesData}
				title="Dataset Subseries"
			/>
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNoteData}
				setOriginalRelatedNotesData={setRelatedNoteData}
			/>
		</>
	);
}
