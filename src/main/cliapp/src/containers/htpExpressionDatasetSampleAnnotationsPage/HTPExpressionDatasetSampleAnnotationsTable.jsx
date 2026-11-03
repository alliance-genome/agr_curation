import { useRef, useState, useMemo } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { ListDialogTemplate } from '../../components/Templates/dialog/ListDialogTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { ExternalDataBaseEntityReadOnlyDialog } from '../../components/ExternalDataBaseEntityReadOnlyDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { BioSampleAgeReadOnlyDialog } from './BioSampleAgeReadOnlyDialog';
import { BioSampleGenomicInformationReadOnlyDialog } from './BioSampleGenomicInformationReadOnlyDialog';
import { MicroarraySampleDetailsReadOnlyDialog } from './MicroarraySampleDetailsReadOnlyDialog';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';

const locationText = (site) => {
	const structureName = site?.anatomicalStructure?.name;
	const substructureName = site?.anatomicalSubstructure?.name;
	if (!structureName) return substructureName;
	return substructureName ? `${structureName} (${substructureName})` : structureName;
};

/**
 * Read-only table of HTP expression dataset sample annotations, including the pop-up fields:
 * Expression Sample and Biological Reference are both ExternalDataBaseEntity - the same popup
 * used by the dataset-level table serves both, told apart by title.
 */
export const HTPExpressionDatasetSampleAnnotationsTable = () => {
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [htpExpressionDatasetSampleAnnotations, setHtpExpressionDatasetSampleAnnotations] = useState([]);

	const [expressionSampleData, setExpressionSampleData] = useState({ dialog: false });
	const [sampleAgeData, setSampleAgeData] = useState({ dialog: false });
	const [biologicalReferenceData, setBiologicalReferenceData] = useState({ dialog: false });
	const [genomicInformationData, setGenomicInformationData] = useState({ dialog: false });
	const [microarrayDetailsData, setMicroarrayDetailsData] = useState({ dialog: false });
	const [relatedNotesData, setRelatedNotesData] = useState({
		originalRelatedNotes: [],
		dialog: false,
		isInEdit: false,
	});

	const searchService = new SearchService();

	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const handleExpressionSampleOpen = (entities) =>
		setExpressionSampleData({ originalEntities: entities, dialog: true });
	const handleSampleAgeOpen = (entities) => setSampleAgeData({ originalEntities: entities, dialog: true });
	const handleBiologicalReferenceOpen = (entities) =>
		setBiologicalReferenceData({ originalEntities: entities, dialog: true });
	const handleGenomicInformationOpen = (entities) =>
		setGenomicInformationData({ originalEntities: entities, dialog: true });
	const handleMicroarrayDetailsOpen = (entities) =>
		setMicroarrayDetailsData({ originalEntities: entities, dialog: true });
	const handleRelatedNotesOpen = (entities) =>
		setRelatedNotesData({ originalRelatedNotes: entities, dialog: true, isInEdit: false });

	const columns = useMemo(
		() => [
			{
				field: 'htpExpressionSample.curie',
				header: 'Expression Sample',
				sortable: { isInEditMode },
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.htpExpressionSample ? [rowData.htpExpressionSample] : []}
						handleOpen={handleExpressionSampleOpen}
						getTextField={(entity) => entity?.curie}
						underline={true}
					/>
				),
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationExpressionSampleFilterConfig,
			},
			{
				field: 'curie',
				header: 'Sample Curie',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.curie} />,
				filterConfig: FILTER_CONFIGS.curieFilterConfig,
			},
			{
				field: 'htpExpressionSampleTitle',
				header: 'Sample Title',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.htpExpressionSampleTitle} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationTitleFilterConfig,
			},
			{
				field: 'taxon.name',
				header: 'Sample Taxon',
				sortable: { isInEditMode },
				body: (rowData) => <OntologyTermTemplate term={rowData.taxon} />,
				filterConfig: FILTER_CONFIGS.taxonFilterConfig,
			},
			{
				field: 'htpExpressionSampleType.name',
				header: 'Sample Type',
				sortable: { isInEditMode },
				body: (rowData) => <OntologyTermTemplate term={rowData.htpExpressionSampleType} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationTypeFilterConfig,
			},
			{
				field: 'htpExpressionSampleAge.age',
				header: 'Sample Age',
				sortable: false,
				body: (rowData) => (
					<CountDialogTemplate
						entities={rowData.htpExpressionSampleAge ? [rowData.htpExpressionSampleAge] : []}
						handleOpen={handleSampleAgeOpen}
						text={'Age'}
					/>
				),
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationSampleAgeFilterConfig,
			},
			{
				field: 'htpExpressionSampleLocations.anatomicalStructure.name',
				header: 'Sample Locations',
				sortable: false,
				body: (rowData) => <StringListTemplate list={rowData.htpExpressionSampleLocations?.map(locationText)} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationLocationsFilterConfig,
			},
			{
				field: 'datasetIds.curie',
				header: 'Biological Reference',
				sortable: { isInEditMode },
				body: (rowData) => (
					<ListDialogTemplate
						entities={rowData.datasetIds}
						handleOpen={handleBiologicalReferenceOpen}
						getTextField={(entity) => entity?.curie}
						underline={true}
					/>
				),
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationBiologicalReferenceFilterConfig,
			},
			{
				field: 'assemblyVersions',
				header: 'Sample Assembly Versions',
				sortable: { isInEditMode },
				body: (rowData) => <StringListTemplate list={rowData.assemblyVersions} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationAssemblyVersionsFilterConfig,
			},
			{
				field: 'abundance',
				header: 'Sample Abundance',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.abundance} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationAbundanceFilterConfig,
			},
			{
				field: 'expressionAssayUsed.name',
				header: 'Sample Assay Used',
				sortable: { isInEditMode },
				body: (rowData) => <OntologyTermTemplate term={rowData.expressionAssayUsed} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationAssayUsedFilterConfig,
			},
			{
				field: 'genomicInformation.bioSampleText',
				header: 'Sample Genomic Information',
				sortable: false,
				body: (rowData) => (
					<CountDialogTemplate
						entities={rowData.genomicInformation ? [rowData.genomicInformation] : []}
						handleOpen={handleGenomicInformationOpen}
						text={'Genomic Information'}
					/>
				),
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationGenomicInformationFilterConfig,
			},
			{
				field: 'geneticSex.name',
				header: 'Sample Genetic Sex',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.geneticSex?.name} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationGeneticSexFilterConfig,
			},
			{
				field: 'microarraySampleDetails.channelId',
				header: 'Microarray Sample Details',
				sortable: false,
				body: (rowData) => (
					<CountDialogTemplate
						entities={rowData.microarraySampleDetails ? [rowData.microarraySampleDetails] : []}
						handleOpen={handleMicroarrayDetailsOpen}
						text={'Microarray Details'}
					/>
				),
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationMicroarrayDetailsFilterConfig,
			},
			{
				field: 'sequencingFormat.name',
				header: 'Sample Sequencing Format',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.sequencingFormat?.name} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationSequencingFormatFilterConfig,
			},
			{
				field: 'relatedNotes.freeText',
				header: 'Sample Related Notes',
				sortable: false,
				body: (rowData) => (
					<CountDialogTemplate entities={rowData.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
				),
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationRelatedNotesFilterConfig,
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Sample Data Provider',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.dataProvider?.abbreviation} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationDataProviderFilterConfig,
			},
			{
				field: 'updatedBy.uniqueId',
				header: 'Sample Updated By',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.updatedBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
			},
			{
				field: 'dateUpdated',
				header: 'Sample Date Updated',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
				filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
			},
			{
				field: 'createdBy.uniqueId',
				header: 'Sample Created By',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.createdByFilterConfig,
			},
			{
				field: 'dateCreated',
				header: 'Sample Date Created',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
				filterConfig: FILTER_CONFIGS.dateCreatedFilterConfig,
			},
			{
				field: 'internal',
				header: 'Sample Internal',
				sortable: { isInEditMode },
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
			},
			{
				field: 'obsolete',
				header: 'Sample Obsolete',
				sortable: { isInEditMode },
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Annotation.HTP_EXPRESSION_DATASET_SAMPLE_ANNOTATION;

	const initialTableState = useMemo(
		() => getDefaultTableState('HTPExpressionDatasetSampleAnnotations', columns, DEFAULT_COLUMN_WIDTH),
		[columns]
	);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setHtpExpressionDatasetSampleAnnotations,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					dataKey="id"
					endpoint={SEARCH_ENDPOINT}
					tableName="HTP Expression Dataset Sample Annotations"
					entities={htpExpressionDatasetSampleAnnotations}
					setEntities={setHtpExpressionDatasetSampleAnnotations}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={false}
					hasDetails={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages }}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<ExternalDataBaseEntityReadOnlyDialog
				originalEntitiesData={expressionSampleData}
				setOriginalEntitiesData={setExpressionSampleData}
				title="Expression Sample"
			/>
			<ExternalDataBaseEntityReadOnlyDialog
				originalEntitiesData={biologicalReferenceData}
				setOriginalEntitiesData={setBiologicalReferenceData}
				title="Biological Reference"
			/>
			<BioSampleAgeReadOnlyDialog originalEntitiesData={sampleAgeData} setOriginalEntitiesData={setSampleAgeData} />
			<BioSampleGenomicInformationReadOnlyDialog
				originalEntitiesData={genomicInformationData}
				setOriginalEntitiesData={setGenomicInformationData}
			/>
			<MicroarraySampleDetailsReadOnlyDialog
				originalEntitiesData={microarrayDetailsData}
				setOriginalEntitiesData={setMicroarrayDetailsData}
			/>
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
		</>
	);
};
