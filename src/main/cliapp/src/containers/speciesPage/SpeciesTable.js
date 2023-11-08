import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';


export const SpeciesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	/*const sourceOrganizationTemplate = (rowData) => {
		const abbreviation = rowData?.sourceOrganization?.abbreviation;
		const fullName = rowData?.sourceOrganization?.fullName;

		console.log(rowData)
			return (
				<>
					{ fullName } - { abbreviation }
				</>
			);
	};*/

	const commonNamesBody = (rowData) => {
		if(Array.isArray(rowData.commonNames)) {
			return rowData.commonNames.join(', ');
		}
	};


	const columns = [
		{
			field: "taxon.curie",
			header: "Taxon Curie",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig
		},
		{
			field: "fullName",
			header: "Full Name",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig
		},
		{
			field: "shortName",
			header: "Short Name",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesShortNameFilterConfig
		},
		{
			field: "commonNames",
			header: "Common Names",
			sortable: isEnabled,
			filter: true,
			body: commonNamesBody,
			filterConfig: FILTER_CONFIGS.speciesCommonNameFilterConfig
		},
		{
			field: "sourceOrganization.fullName",
			header: "Data Provider Full Name",
			sortable: isEnabled,
			//filter: true,
			//body: sourceOrganizationTemplate,
			//filterConfig: FILTER_CONFIGS.sourceorganizationFilterConfig
		},
		{
			field: "sourceOrganization.abbreviation",
			header: "Data Provider Short Name",
			sortable: isEnabled,
			//filter: true,
			//filterConfig: FILTER_CONFIGS.sourceorganizationFilterConfig
		},
		{
			field: "phylogenicOrder",
			header: "Phylogenic Order",
			sortable: isEnabled,
			filter: true
		},
		{
			field: "assembly",
			header: "Assembly",
			sortable: isEnabled,
			filter: true
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 20;
	});

	const initialTableState = getDefaultTableState("Species", defaultColumnNames, undefined, widthsObject);

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint="species"
					tableName="Species"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					isEditable={false}
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					errorObject = {{errorMessages, setErrorMessages}}
					widthsObject={widthsObject}
				/>
			</div>
		</>
	)
}
