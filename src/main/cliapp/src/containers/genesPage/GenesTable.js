import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Toast } from 'primereact/toast';
import { Tooltip } from 'primereact/tooltip';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { SecondaryIdsDialog } from './SecondaryIdsDialog';
import { SynonymsDialog } from '../nameSlotAnnotations/dialogs/SynonymsDialog';
import { SymbolDialog } from '../nameSlotAnnotations/dialogs/SymbolDialog';
import { FullNameDialog } from '../nameSlotAnnotations/dialogs/FullNameDialog';
import { SystematicNameDialog } from './SystematicNameDialog';
import { ListTableCell } from '../../components/ListTableCell';
import { Button } from 'primereact/button';
import { internalTemplate, obsoleteTemplate } from '../../components/AuditedObjectComponent';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

import { SearchService } from '../../service/SearchService';

export const GenesTable = () => {

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const [totalRecords, setTotalRecords] = useState(0);
	const [genes, setGenes] = useState([]);

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [synonymsData, setSynonymsData] = useState({
		dialog: false
	});

	const [secondaryIdsData, setSecondaryIdsData] = useState({
		dialog: false
	});

	const [symbolData, setSymbolData] = useState({
		dialog: false
	});

	const [fullNameData, setFullNameData] = useState({
		dialog: false
	});

	const [systematicNameData, setSystematicNameData] = useState({
		dialog: false
	});

	const fullNameTemplate = (rowData) => {
		if (rowData?.geneFullName) {
			return (
				<>
					<Button className="p-button-text" onClick={(event) => { handleFullNameOpen(event, rowData, false) }} >
						<EllipsisTableCell otherClasses={`a${rowData.modEntityId.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{__html: rowData.geneFullName.formatText}}></div>
						</EllipsisTableCell>
						<Tooltip target={`.a${rowData.modEntityId.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{__html: rowData.geneFullName.formatText}}/>
						</Tooltip>
					</Button>	
				</>
			)
		}
	};

	const handleFullNameOpen = (event, rowData) => {
		let _fullNameData = {};
		_fullNameData["originalFullNames"] = [rowData.geneFullName];
		_fullNameData["dialog"] = true;
		setFullNameData(() => ({
			..._fullNameData
		}));
	};

	const synonymsTemplate = (rowData) => {
		if (rowData?.geneSynonyms) {
			const synonymSet = new Set();
			for(var i = 0; i < rowData.geneSynonyms.length; i++){
				if (rowData.geneSynonyms[i].displayText) {
					synonymSet.add(rowData.geneSynonyms[i].displayText);
				}
			}
			if (synonymSet.size > 0) {
				const sortedSynonyms = Array.from(synonymSet).sort();
				const listTemplate = (item) => {
					return (
						<div className='overflow-hidden text-overflow-ellipsis text-left' dangerouslySetInnerHTML={{ __html: item }} />	
					);
				};
				return (
					<>
						<Button className="p-button-text"
							onClick={(event) => { handleSynonymsOpen(event, rowData, false) }} >
							<ListTableCell template={listTemplate} listData={sortedSynonyms}/>
						</Button>
					</>
				);
			}
		}
	};
	
	const handleSynonymsOpen = (event, rowData) => {
		let _synonymsData = {};
		_synonymsData["originalSynonyms"] = rowData.geneSynonyms;
		_synonymsData["dialog"] = true;
		setSynonymsData(() => ({
			..._synonymsData
		}));
	};
	
	const symbolTemplate = (rowData) => {
		if (rowData?.geneSymbol) {
			return (
				<>
					<Button className="p-button-text" 
						onClick={(event) => { handleSymbolOpen(event, rowData, false) }} >
							<EllipsisTableCell otherClasses={`b${rowData.modEntityId.replace(':', '')}`}>
								<div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.geneSymbol.formatText }} />
							</EllipsisTableCell>
							<Tooltip target={`.b${rowData.modEntityId.replace(':', '')}`}>
								<div dangerouslySetInnerHTML={{__html: rowData.geneSymbol.formatText}}/>
							</Tooltip>
					</Button>
				</>
			)
		}
	};

	const handleSymbolOpen = (event, rowData) => {
		let _symbolData = {};
		_symbolData["originalSymbols"] = [rowData.geneSymbol];
		_symbolData["dialog"] = true;
		setSymbolData(() => ({
			..._symbolData
		}));
	};

	const secondaryIdsTemplate = (rowData) => {
		if (rowData?.geneSecondaryIds) {
			const sortedIds = rowData.geneSecondaryIds.map(a => a.secondaryId).sort();
			const listTemplate = (item) => {
				return (
					<span style={{ textDecoration: 'underline' }}>
						{item && item}
					</span>
				);
			};
			return (
				<>
					<Button className="p-button-text"
							onClick={(event) => { handleSecondaryIdsOpen(event, rowData, false) }} >
						<ListTableCell template={listTemplate} listData={sortedIds}/>
					</Button>
				</>
			);
		}
	};

	const handleSecondaryIdsOpen = (event, rowData) => {
		let _secondaryIdsData = {};
		_secondaryIdsData["originalSecondaryIds"] = rowData.geneSecondaryIds;
		_secondaryIdsData["dialog"] = true;
		setSecondaryIdsData(() => ({
			..._secondaryIdsData
		}));
	};
	
	const systematicNameTemplate = (rowData) => {
		if (rowData?.geneSystematicName) {
			return (
				<>
					<Button className="p-button-text" onClick={(event) => { handleSystematicNameOpen(event, rowData, false) }} >
						<EllipsisTableCell otherClasses={`c${rowData.modEntityId.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{__html: rowData.geneSystematicName.formatText}}></div>
						</EllipsisTableCell>
						<Tooltip target={`.c${rowData.modEntityId.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{__html: rowData.geneSystematicName.formatText}}/>
						</Tooltip>
					</Button>	
				</>
			)
		}
	};

	const handleSystematicNameOpen = (event, rowData) => {
		let _systematicNameData = {};
		_systematicNameData["originalSystematicNames"] = [rowData.geneSystematicName];
		_systematicNameData["dialog"] = true;
		setSystematicNameData(() => ({
			..._systematicNameData
		}));
	};

	const taxonBodyTemplate = (rowData) => {
			if (rowData.taxon) {
					return (
							<>
									<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.modEntityId.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`}>
											{rowData.taxon.name} ({rowData.taxon.curie})
									</EllipsisTableCell>
									<Tooltip target={`.${"TAXON_NAME_"}${rowData.modEntityId.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
							</>
					);
			}
	};

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.curieFilterConfig
		},
		{
			field: "modEntityId",
			header: "MOD Entity ID",
			sortable:  true,
			filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
		},
		{
			field: "modInternalId",
			header: "MOD Internal ID",
			sortable:  true,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: "geneFullName.displayText",
			header: "Name",
			sortable: true,
			filter: true,
			body: fullNameTemplate,
			filterConfig: FILTER_CONFIGS.geneNameFilterConfig
		},
		{
			field: "geneSymbol.displayText",
			header: "Symbol",
			sortable: true,
			body: symbolTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.geneSymbolFilterConfig
		},
		{
			field: "geneSynonyms.displayText",
			header: "Synonyms",
			body: synonymsTemplate,
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneSynonymsFilterConfig
		},
		{
			field: "geneSecondaryIds.secondaryId",
			header: "Secondary IDs",
			body: secondaryIdsTemplate,
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneSecondaryIdsFilterConfig,
		},
		{
			field: "geneSystematicName.displayText",
			header: "Systematic Name",
			sortable: true,
			body: systematicNameTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.geneSystematicNameFilterConfig
		},
		{
			field: "taxon.name",
			header: "Taxon",
			sortable: true,
			body: taxonBodyTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig
		},
		{
			field: "dataProvider.sourceOrganization.abbreviation",
			header: "Data Provider",
			sortable: true,
			filterConfig: FILTER_CONFIGS.geneDataProviderFilterConfig,
		},
		{
			field: "crossReferences.displayName",
			header: "Cross References",
			sortable: true,
			filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
			body: (rowData) => <CrossReferencesTemplate xrefs={rowData.crossReferences}/>
		},
		{
			field: "updatedBy.uniqueId",
			header: "Updated By",
			sortable: true,
			filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
		},
		{
			field: "dateUpdated",
			header: "Date Updated",
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig
		},
		{
			field: "createdBy.uniqueId",
			header: "Created By",
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.createdByFilterConfig
		},
		{
			field: "dateCreated",
			header: "Date Created",
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig
		},
		{
			field: "internal",
			header: "Internal",
			body: internalTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: true
		},
		{
			field: "obsolete",
			header: "Obsolete",
			body: obsoleteTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: true
		}
	];

	const DEFAULT_COLUMN_WIDTH = 20; 
	const SEARCH_ENDPOINT = "gene";

	const initialTableState = getDefaultTableState("Genes", columns, DEFAULT_COLUMN_WIDTH);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(initialTableState.tableSettingsKeyName, initialTableState);

	const { isFetching, isLoading } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		setIsInEditMode,
		setEntities: setGenes,
		setTotalRecords,
		toast_topleft,
		searchService
	});

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint={SEARCH_ENDPOINT}
					tableName="Genes"
					entities={genes}
					setEntities={setGenes}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{toast_topleft, toast_topright }}
					errorObject = {{errorMessages, setErrorMessages}}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
				/>
			</div>
			<FullNameDialog
				name="Gene Name"
				field="geneFullName"
				endpoint="genefullnameslotannotation"
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
			/>
			<SymbolDialog
				name="Gene Symbol"
				field="geneSymbol"
				endpoint="genesymbolslotannotation"
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
			/>
			<SynonymsDialog
				name="Gene Synonym"
				field="geneSynonyms"
				endpoint="genesynonymslotannotation"
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
			/>
			<SecondaryIdsDialog
				originalSecondaryIdsData={secondaryIdsData}
				setOriginalSecondaryIdsData={setSecondaryIdsData}
			/>
			<SystematicNameDialog
				originalSystematicNameData={systematicNameData}
				setOriginalSystematicNameData={setSystematicNameData}
			/>
		</>
	)
}
