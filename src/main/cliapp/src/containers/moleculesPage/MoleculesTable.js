import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';

export const MoleculesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const inChiBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`a${rowData.curie.replaceAll(':', '')}`}>{rowData.inchi}</EllipsisTableCell>
				<Tooltip target={`.a${rowData.curie.replaceAll(':', '')}`} content={rowData.inchi} />
			</>
		)
	};

	const iupacBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`b${rowData.curie.replaceAll(':', '')}`}>{rowData.iupac}</EllipsisTableCell>
				<Tooltip target={`.b${rowData.curie.replaceAll(':', '')}`} content={rowData.iupac} />
			</>
		)
	};

	const smilesBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`c${rowData.curie.replaceAll(':', '')}`}>{rowData.smiles}</EllipsisTableCell>
				<Tooltip target={`.c${rowData.curie.replaceAll(':', '')}`} content={rowData.smiles} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	};

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.curieFilterConfig, 
		},
		{
			field: "name",
			header: "Name",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.nameFilterConfig, 
		},
		{
			field: "inchi",
			header: "InChi",
			sortable: isEnabled,
			filter: true,
			body: inChiBodyTemplate,
			filterConfig: FILTER_CONFIGS.inchiFilterConfig, 
		},
		{
			field: "inchiKey",
			header: "InChiKey",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.inchiKeyFilterConfig, 
		},
		{
			field: "iupac",
			header: "IUPAC",
			sortable: isEnabled,
			filter: true,
			body: iupacBodyTemplate,
			filterConfig: FILTER_CONFIGS.iupacFilterConfig, 
		},
		{
			field: "formula",
			header: "Formula",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.formulaFilterConfig, 
		},
		{
			field: "smiles",
			header: "SMILES",
			sortable: isEnabled,
			filter: true,
			body: smilesBodyTemplate,
			filterConfig: FILTER_CONFIGS.smilesFilterConfig, 
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 13;
	});

	const initialTableState = getDefaultTableState("Molecule", defaultColumnNames, undefined, widthsObject);

	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable 
					endpoint="molecule" 
					tableName="Molecule" 
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
	)
}
