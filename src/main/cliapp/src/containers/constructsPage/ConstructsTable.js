import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { internalTemplate, obsoleteTemplate } from '../../components/AuditedObjectComponent';
import { ComponentsDialog } from './ComponentsDialog';
import { GenomicComponentsDialog } from './GenomicComponentsDialog';
import { SymbolDialog } from '../nameSlotAnnotations/dialogs/SymbolDialog';
import { FullNameDialog } from '../nameSlotAnnotations/dialogs/FullNameDialog';
import { SynonymsDialog } from '../nameSlotAnnotations/dialogs/SynonymsDialog';
import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { getRefStrings } from '../../utils/utils';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';

export const ConstructsTable = () => {

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [synonymsData, setSynonymsData] = useState({
		dialog: false
	});

	const [symbolData, setSymbolData] = useState({
		dialog: false
	});

	const [fullNameData, setFullNameData] = useState({
		dialog: false
	});

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;
	
	const [componentsData, setComponentsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const [genomicComponentsData, setGenomicComponentsData] = useState({
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});


	const uniqueIdBodyTemplate = (rowData) => {
		return (
			//the 'a' at the start is a hack since css selectors can't start with a number
			<>
				<EllipsisTableCell otherClasses={`c${rowData.id}`}>
					{rowData.uniqueId}
				</EllipsisTableCell>
				<Tooltip target={`.c${rowData.id}`} content={rowData.uniqueId} />
			</>
		)
	};
	
	const modEntityIdBodyTemplate = (rowData) => {
		return (
			//the 'a' at the start is a hack since css selectors can't start with a number
			<>
				<EllipsisTableCell otherClasses={`a${rowData.id}`}>
					{rowData.modEntityId}
				</EllipsisTableCell>
				<Tooltip target={`.a${rowData.id}`} content={rowData.modEntityId} />
			</>
		)
	};

	const modInternalIdBodyTemplate = (rowData) => {
		return (
			//the 'a' at the start is a hack since css selectors can't start with a number
			<>
				<EllipsisTableCell otherClasses={`b${rowData.id}`}>
					{rowData.modInternalId}
				</EllipsisTableCell>
				<Tooltip target={`.b${rowData.id}`} content={rowData.modInternalId} />
			</>
		)
	};

	const fullNameTemplate = (rowData) => {
		if (rowData?.constructFullName) {
			return (
				<>
					<Button className="p-button-text" onClick={(event) => { handleFullNameOpen(event, rowData, false) }} >
						<EllipsisTableCell otherClasses={`fn_${rowData.id}`}>
							<div dangerouslySetInnerHTML={{__html: rowData.constructFullName.displayText}}></div>
						</EllipsisTableCell>
						<Tooltip target={`.fn_${rowData.id}`}>
							<div dangerouslySetInnerHTML={{__html: rowData.constructFullName.displayText}}/>
						</Tooltip>
					</Button>	
				</>
			)
		}
	};

	const handleFullNameOpen = (event, rowData) => {
		let _fullNameData = {};
		_fullNameData["originalFullNames"] = [rowData.constructFullName];
		_fullNameData["dialog"] = true;
		setFullNameData(() => ({
			..._fullNameData
		}));
	};

	const synonymsTemplate = (rowData) => {
		if (rowData?.constructSynonyms) {
			const synonymSet = new Set();
			for(var i = 0; i < rowData.constructSynonyms.length; i++){
				if (rowData.constructSynonyms[i].displayText) {
					synonymSet.add(rowData.constructSynonyms[i].displayText);
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
		_synonymsData["originalSynonyms"] = rowData.constructSynonyms;
		_synonymsData["dialog"] = true;
		setSynonymsData(() => ({
			..._synonymsData
		}));
	};
	
	const symbolTemplate = (rowData) => {
		if (rowData?.constructSymbol) {
			return (
				<>
					<Button className="p-button-text" 
						onClick={(event) => { handleSymbolOpen(event, rowData, false) }} >
							<EllipsisTableCell otherClasses={`sym_${rowData.id}`}>
								<div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.constructSymbol.displayText }} />
							</EllipsisTableCell>
							<Tooltip target={`.sym_${rowData.id}`}>
								<div dangerouslySetInnerHTML={{__html: rowData.constructSymbol.displayText}}/>
							</Tooltip>
					</Button>
				</>
			)
		}
	};

	const handleSymbolOpen = (event, rowData) => {
		let _symbolData = {};
		_symbolData["originalSymbols"] = [rowData.constructSymbol];
		_symbolData["dialog"] = true;
		setSymbolData(() => ({
			..._symbolData
		}));
	};
	
	const secondaryIdsBodyTemplate = (rowData) => {
		if (rowData?.secondaryIdentifiers && rowData.secondaryIdentifiers.length > 0) {
			const sortedSecondaryIdentifiers = rowData.secondaryIdentifiers.sort();
			const listTemplate = (secondaryIdentifier) => {
				return (
					<EllipsisTableCell>
						<div dangerouslySetInnerHTML={{__html: secondaryIdentifier}}/>
					</EllipsisTableCell>
				)
			};
			return (
				<>
					<div className={`sid_${rowData.id}`}>
						<ListTableCell template={listTemplate} listData={sortedSecondaryIdentifiers}/>
					</div>
					<Tooltip target={`.sid_${rowData.id}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={sortedSecondaryIdentifiers}/>
					</Tooltip>
				</>
			);
		}
	};
	
	const referencesTemplate = (rowData) => {
		if (rowData && rowData.references && rowData.references.length > 0) {
			const refStrings = getRefStrings(rowData.references);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<div className={`ref_${rowData.id}${rowData.references[0].curie.replace(':', '')}`}>
						<ListTableCell template={listTemplate} listData={refStrings}/>
					</div>
					<Tooltip target={`.ref_${rowData.id}${rowData.references[0].curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={refStrings}/>
					</Tooltip>
				</>
			);

		}
	};

	const componentsTemplate = (rowData) => {
		if (rowData?.constructComponents) {
			const componentSet = new Set();
			for(var i = 0; i < rowData.constructComponents.length; i++){
				if (rowData.constructComponents[i].componentSymbol && rowData.constructComponents[i].relation) {
					let relationName = "";
					if (rowData.constructComponents[i]?.relation?.name) {
						relationName = rowData.constructComponents[i].relation.name;
						if (relationName.indexOf(' (RO:') !== -1) {
							relationName = relationName.substring(0, relationName.indexOf(' (RO:'))
						}
					}	
					componentSet.add(relationName + ': ' + rowData.constructComponents[i].componentSymbol);
				}
			}
			if (componentSet.size > 0) {
				const sortedComponents = Array.from(componentSet).sort();
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
							onClick={(event) => { handleComponentsOpen(event, rowData) }} >
							<ListTableCell template={listTemplate} listData={sortedComponents}/>
						</Button>
					</>
				);
			}
		}
	};
	
	const handleComponentsOpen = (event, rowData) => {
		let _componentsData = {};
		_componentsData["originalComponents"] = rowData.constructComponents;
		_componentsData["dialog"] = true;
		setComponentsData(() => ({
			..._componentsData
		}));
	};

	const genomicComponentsTemplate = (rowData) => {
		if (rowData?.constructGenomicEntityAssociations) {
			const componentSet = new Set();
			for(var i = 0; i < rowData.constructGenomicEntityAssociations.length; i++){
				let symbolValue = "";
				if (rowData.constructGenomicEntityAssociations[i]?.object.geneSymbol || rowData.constructGenomicEntityAssociations[i]?.object.alleleSymbol) {
					symbolValue = rowData.constructGenomicEntityAssociations[i].object.geneSymbol ? rowData.constructGenomicEntityAssociations[i].object.geneSymbol.displayText : rowData.constructGenomicEntityAssociations[i].object.alleleSymbol.displayText;
				} else if (rowData.constructGenomicEntityAssociations[i]?.object.name) {
					symbolValue = rowData.constructGenomicEntityAssociations[i].object.name;
				} else {
					symbolValue = rowData.constructGenomicEntityAssociations[i].object.curie;
				}
				let relationName = "";
				if (rowData.constructGenomicEntityAssociations[i]?.relation?.name) {
					relationName = rowData.constructGenomicEntityAssociations[i].relation.name;
					if (relationName.indexOf(' (RO:') !== -1) {
						relationName = relationName.substring(0, relationName.indexOf(' (RO:'))
					}
				}	
				componentSet.add(relationName + ': ' + symbolValue);
			}
			if (componentSet.size > 0) {
				const sortedComponents = Array.from(componentSet).sort();
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
							onClick={(event) => { handleGenomicComponentsOpen(event, rowData) }} >
							<ListTableCell template={listTemplate} listData={sortedComponents}/>
						</Button>
					</>
				);
			}
		}
	};
	
	const handleGenomicComponentsOpen = (event, rowData) => {
		let _componentsData = {};
		_componentsData["originalComponents"] = rowData.constructGenomicEntityAssociations;
		_componentsData["dialog"] = true;
		setGenomicComponentsData(() => ({
			..._componentsData
		}));
	};

	const columns = [
		{
			field: "uniqueId",
			header: "Unique ID",
			sortable: { isInEditMode },
			body: uniqueIdBodyTemplate,
			filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
		},
		{
			field: "modEntityId",
			header: "MOD Entity ID",
			sortable: { isInEditMode },
			body: modEntityIdBodyTemplate,
			filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
		},
		{
			field: "modInternalId",
			header: "MOD Internal ID",
			sortable: { isInEditMode },
			body: modInternalIdBodyTemplate,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: "constructSymbol.displayText",
			header: "Symbol",
			sortable: true,
			body: symbolTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.constructSymbolFilterConfig
		},
		{
			field: "constructFullName.displayText",
			header: "Name",
			sortable: true,
			filter: true,
			body: fullNameTemplate,
			filterConfig: FILTER_CONFIGS.constructNameFilterConfig
		},
		{
			field: "constructSynonyms.displayText",
			header: "Synonyms",
			body: synonymsTemplate,
			sortable: true,
			filterConfig: FILTER_CONFIGS.constructSynonymsFilterConfig
		},
		{
			field: "secondaryIdentifiers",
			header: "Secondary IDs",
			sortable: true,
			filterConfig: FILTER_CONFIGS.secondaryIdsFilterConfig,
			body: secondaryIdsBodyTemplate
		},
		{
			field: "constructComponents.componentSymbol",
			header: "Free Text Components",
			body: componentsTemplate,
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.constructComponentsFilterConfig,
		},
		{
			field: "constructGenomicEntityAssociations.object.symbol",
			header: "Component Associations",
			body: genomicComponentsTemplate,
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.constructGenomicComponentsFilterConfig,
		},
		{
			field: "references.primaryCrossReferenceCurie",
			header: "References",
			body: referencesTemplate,
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.referencesFilterConfig,
		},
		{
			field: "dataProvider.sourceOrganization.abbreviation",
			header: "Data Provider",
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.constructDataProviderFilterConfig,
		},
		{
			field: "updatedBy.uniqueId",
			header: "Updated By",
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
		},
		{
			field: "dateUpdated",
			header: "Date Updated",
			sortable: { isInEditMode },
			filter: true,
			filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig
		},
		{
			field: "createdBy.uniqueId",
			header: "Created By",
			sortable: { isInEditMode },
			filter: true,
			filterConfig: FILTER_CONFIGS.createdByFilterConfig
		},
		{
			field: "dateCreated",
			header: "Date Created",
			sortable: { isInEditMode },
			filter: true,
			filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig
		},
		{
			field: "internal",
			header: "Internal",
			body: internalTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: { isInEditMode }
		},
		{
			field: "obsolete",
			header: "Obsolete",
			body: obsoleteTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: { isInEditMode }
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 10;
	});

	const initialTableState = getDefaultTableState("Constructs", defaultColumnNames, undefined, widthsObject);

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					dataKey="id"
					endpoint="construct"
					tableName="Constructs"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					isEditable={false}
					hasDetails={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{toast_topleft, toast_topright }}
					errorObject = {{errorMessages, setErrorMessages}}
					widthsObject={widthsObject}
				/>
			</div>
			<FullNameDialog
				name="Construct Name"
				field="constructFullName"
				endpoint="constructfullnameslotannotation"
				originalFullNameData={fullNameData}
				setOriginalFullNameData={setFullNameData}
			/>
			<SymbolDialog
				name="Construct Symbol"
				field="constructSymbol"
				endpoint="constructsymbolslotannotation"
				originalSymbolData={symbolData}
				setOriginalSymbolData={setSymbolData}
			/>
			<SynonymsDialog
				name="Construct Synonym"
				field="constructSynonyms"
				endpoint="constructsynonymslotannotation"
				originalSynonymsData={synonymsData}
				setOriginalSynonymsData={setSynonymsData}
			/>
			<ComponentsDialog
				originalComponentsData={componentsData}
				setOriginalComponentsData={setComponentsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<GenomicComponentsDialog
				originalComponentsData={genomicComponentsData}
				setOriginalComponentsData={setGenomicComponentsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
