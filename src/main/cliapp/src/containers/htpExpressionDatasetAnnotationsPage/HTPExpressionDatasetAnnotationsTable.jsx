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
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';

/**
 * Read-only table of HTP expression dataset annotations. First iteration: no pop-ups - the dataset
 * (ExternalDatabaseEntity), its subseries and its related note are all omitted, per the ticket.
 */
export const HTPExpressionDatasetAnnotationsTable = () => {
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [htpExpressionDatasetAnnotations, setHtpExpressionDatasetAnnotations] = useState([]);

	const searchService = new SearchService();

	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const columns = useMemo(
		() => [
			{
				field: 'name',
				header: 'Dataset Name',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.name} />,
				filterConfig: FILTER_CONFIGS.nameFilterConfig,
			},
			{
				field: 'categoryTags.name',
				header: 'Dataset Category Tags',
				sortable: { isInEditMode },
				body: (rowData) => <StringListTemplate list={rowData.categoryTags?.map((term) => term.name)} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetAnnotationCategoryTagsFilterConfig,
			},
			{
				field: 'numberOfChannels',
				header: 'Dataset Number of Channels',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.numberOfChannels} />,
			},
			{
				field: 'references.primaryCrossReferenceCurie',
				header: 'Dataset References',
				sortable: { isInEditMode },
				body: (rowData) => <TruncatedReferencesTemplate references={rowData.references} />,
				filterConfig: FILTER_CONFIGS.referencesFilterConfig,
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Dataset Data Provider',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.dataProvider?.abbreviation} />,
				filterConfig: FILTER_CONFIGS.htpExpressionDatasetAnnotationDataProviderFilterConfig,
			},
			{
				field: 'updatedBy.uniqueId',
				header: 'Dataset Updated By',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.updatedBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
			},
			{
				field: 'dateUpdated',
				header: 'Dataset Date Updated',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
				filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
			},
			{
				field: 'createdBy.uniqueId',
				header: 'Dataset Created By',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.createdByFilterConfig,
			},
			{
				field: 'dateCreated',
				header: 'Dataset Date Created',
				sortable: { isInEditMode },
				body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
				filterConfig: FILTER_CONFIGS.dateCreatedFilterConfig,
			},
			{
				field: 'internal',
				header: 'Dataset Internal',
				sortable: { isInEditMode },
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
			},
			{
				field: 'obsolete',
				header: 'Dataset Obsolete',
				sortable: { isInEditMode },
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Annotation.HTP_EXPRESSION_DATASET_ANNOTATION;

	const initialTableState = useMemo(
		() => getDefaultTableState('HTPExpressionDatasetAnnotations', columns, DEFAULT_COLUMN_WIDTH),
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
		setEntities: setHtpExpressionDatasetAnnotations,
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
				tableName="HTP Expression Dataset Annotations"
				entities={htpExpressionDatasetAnnotations}
				setEntities={setHtpExpressionDatasetAnnotations}
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
