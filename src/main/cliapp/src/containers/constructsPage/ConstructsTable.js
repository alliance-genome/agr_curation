import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { internalTemplate, obsoleteTemplate } from '../../components/AuditedObjectComponent';
import { ComponentsDialog } from './ComponentsDialog';
import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { getRefStrings } from '../../utils/utils';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';

export const ConstructsTable = () => {

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;
	
	const [componentsData, setComponentsData] = useState({
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
	
	const nameTemplate = (rowData) => {
		return (
			<>
				<div className={`overflow-hidden text-overflow-ellipsis ${rowData.id}`} dangerouslySetInnerHTML={{ __html: rowData.name }} />
				<Tooltip target={`.name_${rowData.id}`}>
					<div dangerouslySetInnerHTML={{ __html: rowData.name }} />
				</Tooltip>
			</>
		)
	}

	const taxonTemplate = (rowData) => {
		if (rowData?.taxon) {
			return (
				<>
					<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.id}${rowData.taxon.curie.replace(':', '')}`}>
						{rowData.taxon.name} ({rowData.taxon.curie})
					</EllipsisTableCell>
					<Tooltip target={`.taxon_${rowData.id}`} content={`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
				</>
			);
		}
	}

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
				if (rowData.constructComponents[i].componentSymbol) {
					componentSet.add(rowData.constructComponents[i].componentSymbol);
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

	const columns = [
		{
			field: "uniqueId",
			header: "Unique ID",
			sortable: { isEnabled },
			body: uniqueIdBodyTemplate,
			filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
		},
		{
			field: "modEntityId",
			header: "MOD Entity ID",
			sortable: { isEnabled },
			body: modEntityIdBodyTemplate,
			filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
		},
		{
			field: "modInternalId",
			header: "MOD Internal ID",
			sortable: { isEnabled },
			body: modInternalIdBodyTemplate,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: "name",
			header: "Name",
			body: nameTemplate,
			sortable: { isEnabled },
			filterConfig: FILTER_CONFIGS.taxonFilterConfig
		},
		{
			field: "taxon.name",
			header: "Taxon",
			body: taxonTemplate,
			sortable: { isEnabled },
			filterConfig: FILTER_CONFIGS.taxonFilterConfig,
		},
		{
			field: "constructComponents.componentSymbol",
			header: "Components",
			body: componentsTemplate,
			sortable: { isEnabled },
			filterConfig: FILTER_CONFIGS.constructComponentsFilterConfig,
		},
		{
			field: "references.primaryCrossReferenceCurie",
			header: "References",
			body: referencesTemplate,
			sortable: { isEnabled },
			filterConfig: FILTER_CONFIGS.referencesFilterConfig,
		},
		{
			field: "dataProvider.sourceOrganization.abbreviation",
			header: "Data Provider",
			sortable: { isEnabled },
			filterConfig: FILTER_CONFIGS.constructDataProviderFilterConfig,
		},
		{
			field: "updatedBy.uniqueId",
			header: "Updated By",
			sortable: { isEnabled },
			filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
		},
		{
			field: "dateUpdated",
			header: "Date Updated",
			sortable: { isEnabled },
			filter: true,
			filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig
		},
		{
			field: "createdBy.uniqueId",
			header: "Created By",
			sortable: { isEnabled },
			filter: true,
			filterConfig: FILTER_CONFIGS.createdByFilterConfig
		},
		{
			field: "dateCreated",
			header: "Date Created",
			sortable: { isEnabled },
			filter: true,
			filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig
		},
		{
			field: "internal",
			header: "Internal",
			body: internalTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: { isEnabled }
		},
		{
			field: "obsolete",
			header: "Obsolete",
			body: obsoleteTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: { isEnabled }
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
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					errorObject = {{errorMessages, setErrorMessages}}
					widthsObject={widthsObject}
				/>
			</div>
			<ComponentsDialog
				originalComponentsData={componentsData}
				setOriginalComponentsData={setComponentsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
