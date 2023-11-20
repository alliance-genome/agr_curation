import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';


export const SpeciesTable = () => {

	const [isInEditMode, setIsInEditMode] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const commonNamesBody = (rowData) => {
		if(Array.isArray(rowData.commonNames)) {
			return rowData.commonNames.join(', ');
		}
	};


	const columns = [
		{
			field: "taxon.curie",
			header: "Taxon",
			sortable: isInEditMode,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesTaxonCurieFilterConfig
		},
		{
			field: "fullName",
			header: "Full Name",
			sortable: isInEditMode,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesFullNameFilterConfig
		},
		{
			field: "displayName",
			header: "Display Name",
			sortable: isInEditMode,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesDisplayNameFilterConfig
		},
		{
			field: "abbreviation",
			header: "Abbreviation",
			sortable: isInEditMode,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesAbbreviationFilterConfig
		},
		{
			field: "commonNames",
			header: "Common Names",
			sortable: isInEditMode,
			filter: true,
			body: commonNamesBody,
			filterConfig: FILTER_CONFIGS.speciesCommonNameFilterConfig
		},
		{
			field: "dataProvider.sourceOrganization.abbreviation",
			header: "Data Provider",
			sortable: isInEditMode,
			filter: true,
			filterConfig: FILTER_CONFIGS.speciesDataProviderFilterConfig
		},
		/*{
			field: "dataProvider.sourceOrganization.abbreviation",
			header: "Data Provider Abbreviation",
			sortable: isInEditMode,
			//filterConfig: FILTER_CONFIGS.geneDataProviderFilterConfig
		},*/
		{
			field: "phylogeneticOrder",
			header: "Phylogenetic Order",
			sortable: true
		},
		{
			field: "assembly_curie",
			header: "Assembly",
			sortable: false
			//filterConfig: FILTER_CONFIGS.speciesAssemblyFilterConfig
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
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{toast_topleft, toast_topright }}
					errorObject = {{errorMessages, setErrorMessages}}
					widthsObject={widthsObject}
				/>
			</div>
		</>
	)
}
