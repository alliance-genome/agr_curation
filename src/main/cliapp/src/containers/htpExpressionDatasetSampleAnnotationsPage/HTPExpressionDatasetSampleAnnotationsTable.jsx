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
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';

const locationText = (site) => {
	const structureName = site?.anatomicalStructure?.name;
	const substructureName = site?.anatomicalSubstructure?.name;
	if (!structureName) return substructureName;
	return substructureName ? `${structureName} (${substructureName})` : structureName;
};

/**
 * Read-only table of HTP expression dataset sample annotations. First iteration: pop-up fields
 * (Expression Sample, Sample Age, Biological Reference, Genomic Information, Microarray Sample
 * Details, Related Notes) are omitted, per the ticket.
 */
export const HTPExpressionDatasetSampleAnnotationsTable = () => {
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [htpExpressionDatasetSampleAnnotations, setHtpExpressionDatasetSampleAnnotations] = useState([]);

	const searchService = new SearchService();

	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const columns = useMemo(
		() => [
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
				field: 'htpExpressionSampleLocations.anatomicalStructure.name',
				header: 'Sample Locations',
				sortable: false,
				body: (rowData) => <StringListTemplate list={rowData.htpExpressionSampleLocations?.map(locationText)} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationLocationsFilterConfig,
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
				field: 'geneticSex.name',
				header: 'Sample Genetic Sex',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.geneticSex?.name} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationGeneticSexFilterConfig,
			},
			{
				field: 'sequencingFormat.name',
				header: 'Sample Sequencing Format',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.sequencingFormat?.name} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetSampleAnnotationSequencingFormatFilterConfig,
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
	);
};
