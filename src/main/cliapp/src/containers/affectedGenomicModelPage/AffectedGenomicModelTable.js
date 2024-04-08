import React, { useState, useRef } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { NameTemplate } from '../../components/Templates/NameTemplate';
import { TaxonTemplate } from '../../components/Templates/TaxonTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';

export const AffectedGenomicModelTable = () => {

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: true,
			filterConfig: FILTER_CONFIGS.curieFilterConfig
		},
		{
			field: "modEntityId",
			header: "MOD Entity ID",
			body: (rowData) => <IdTemplate id = {rowData.modEntityId}/>,
			sortable:  true,
			filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
		},
		{
			field: "modInternalId",
			header: "MOD Internal ID",
			body: (rowData) => <IdTemplate id = {rowData.modInternalId}/>,
			sortable:  true,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: "name",
			header: "Name",
			body: (rowData) => <NameTemplate name = {rowData.name}/>,
			sortable: true,
			filterConfig: FILTER_CONFIGS.nameFilterConfig
		},
		{ 	
			field: "subtype.name",
			header: "Sub Type",
			sortable: true,
			filterConfig: FILTER_CONFIGS.subtypeFilterConfig
		},
		{
			field: "taxon.name",
			header: "Taxon",
			sortable: true,
			body: (rowData) => <TaxonTemplate taxon = {rowData.taxon}/>,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig
		},
		{
			field: "dataProvider.sourceOrganization.abbreviation",
			header: "Data Provider",
			sortable: true,
			filterConfig: FILTER_CONFIGS.agmDataProviderFilterConfig,
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
			body: (rowData) => <BooleanTemplate value={rowData.internal}/>,
			filter: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: true
		},
		{
			field: "obsolete",
			header: "Obsolete",
			body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: true
		}
 ];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});
	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 100 / columns.length;
	});

	const initialTableState = getDefaultTableState("AffectedGenomicModels", defaultColumnNames, undefined, widthsObject);


	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint="agm"
					tableName="Affected Genomic Models"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					isEditable={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{toast_topleft, toast_topright }}
					errorObject = {{errorMessages, setErrorMessages}}
					widthsObject={widthsObject}
				/>
			</div>
	)
}
