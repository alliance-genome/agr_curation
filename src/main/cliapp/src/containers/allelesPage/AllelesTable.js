import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { internalTemplate, obsoleteTemplate } from '../../components/AuditedObjectComponent';

import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { getRefStrings } from '../../utils/utils';

export const AllelesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const aggregationFields = [
		'inCollection.name', 'sequencingStatus.name', 'inheritanceMode.name'
	];
	
	const symbolTemplate = (rowData) => {
		return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.symbol }} />
	}
	
	const nameTemplate = (rowData) => {
		return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.name }} />
	}

	const taxonTemplate = (rowData) => {
			if (rowData.taxon) {
					return (
							<>
									<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`}>
											{rowData.taxon.name} ({rowData.taxon.curie})
									</EllipsisTableCell>
									<Tooltip target={`.${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
							</>
					);
			}
	}
	
	const isExtinctTemplate = (rowData) => {
		if (rowData && rowData.isExtinct !== null && rowData.isExtinct !== undefined) {
			return <EllipsisTableCell>{JSON.stringify(rowData.isExtinct)}</EllipsisTableCell>;
		}
	};
	
	const referencesTemplate = (rowData) => {
		if (rowData && rowData.references) {
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
					<div className={`${rowData.curie.replace(':','')}${rowData.references[0].curie.replace(':', '')}`}>
						<ListTableCell template={listTemplate} listData={refStrings}/>
					</div>
					<Tooltip target={`.${rowData.curie.replace(':','')}${rowData.references[0].curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={refStrings}/>
					</Tooltip>
				</>
			);

		}
	};

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: { isEnabled },
			filter: true,
			filterElement: {type: "input", filterName: "curieFilter", fields: ["curie"]}, 
		},
		{
			field: "name",
			header: "Name",
			body: nameTemplate,
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "nameFilter", fields: ["name"]}, 
		},
		{
			field: "symbol",
			header: "Symbol",
			body: symbolTemplate,
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "symbolFilter", fields: ["symbol"]}, 
		},
		{
			field: "taxon.name",
			header: "Taxon",
			body: taxonTemplate,
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "taxonFilter", fields: ["taxon.curie","taxon.name"]}, 
		},
		{
			field: "references.curie",
			header: "References",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "referencesFilter", fields: ["references.curie", "references.crossReferences.curie"]},
			body: referencesTemplate
	},
		{
			field: "inheritanceMode.name",
			header: "Inheritance Mode",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "multiselect", filterName: "inheritanceModeFilter", fields: ["inheritanceMode.name"]},
		},
		{
			field: "inCollection.name",
			header: "In Collection",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "multiselect", filterName: "inCollectionFilter", fields: ["inCollection.name"]},
		},
		{
			field: "sequencingStatus.name",
			header: "Sequencing Status",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "multiselect", filterName: "sequencingStatusFilter", fields: ["sequencingStatus.name"]},
		},
		{
			field: "isExtinct",
			header: "Is Extinct",
			body: isExtinctTemplate,
			filter: true,
			filterElement: {type: "dropdown", filterName: "isExtinctFilter", fields: ["isExtinct"], options: [{ text: "true" }, { text: "false" }], optionField: "text"},
			sortable: isEnabled
		},
		{
			field: "dateUpdated",
			header: "Date Updated",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "dateUpdatedFilter", fields: ["dateUpdated"]},
		},
		{
			field: "createdBy.uniqueId",
			header: "Created By",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "createdByFilter", fields: ["createdBy.uniqueId"]},
		},
		{
			field: "dateCreated",
			header: "Date Created",
			sortable: isEnabled,
			filter: true,
			filterType: "Date",
			filterElement: {type: "input", filterName: "dateCreatedFilter", fields: ["dataCreated"]},
		},
		{
			field: "internal",
			header: "Internal",
			body: internalTemplate,
			filter: true,
			filterElement: {type: "dropdown", filterName: "internalFilter", fields: ["internal"], options: [{ text: "true" }, { text: "false" }], optionField: "text"},
			sortable: isEnabled
		},
		{
			field: "obsolete",
			header: "Obsolete",
			body: obsoleteTemplate,
			filter: true,
			filterElement: {type: "dropdown", filterName: "obsoleteFilter", fields: ["obsolete"], options: [{ text: "true" }, { text: "false" }], optionField: "text"},
			sortable: isEnabled
		}
	];

	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable 
					endpoint="allele" 
					tableName="Alleles" 
					columns={columns}	 
					aggregationFields={aggregationFields}
					isEditable={false}
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					initialColumnWidth={20}
					errorObject = {{errorMessages, setErrorMessages}}
				/>
			</div>
	)
}
