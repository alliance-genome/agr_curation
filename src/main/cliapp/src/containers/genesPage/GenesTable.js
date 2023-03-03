import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Toast } from 'primereact/toast';
import { Tooltip } from 'primereact/tooltip';
import { getDefaultTableState } from '../../service/TableStateService';


export const GenesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const nameBodyTemplate = (rowData) => {
		if (rowData?.geneFullName) {
			return (
				<>
					<EllipsisTableCell otherClasses={`a${rowData.curie.replace(':', '')}`}>
						{rowData.geneFullName.displayText}
					</EllipsisTableCell>
					<Tooltip target={`.a${rowData.curie.replace(':', '')}`} content={rowData.geneFullName.displayText} />
				</>
			)
		}
	};

	const taxonBodyTemplate = (rowData) => {
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
	};

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.curieFilterConfig
		},
		{
			field: "geneFullName.displayText",
			header: "Name",
			sortable: isEnabled,
			filter: true,
			body: nameBodyTemplate,
			filterConfig: FILTER_CONFIGS.geneNameFilterConfig
		},
		{
			field: "geneSymbol.displayText",
			header: "Symbol",
			sortable: isEnabled,
			filter: true,
			filterConfig: FILTER_CONFIGS.geneSymbolFilterConfig
		},
		{
			field: "taxon.name",
			header: "Taxon",
			sortable: isEnabled,
			body: taxonBodyTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("Genes", defaultColumnNames);

	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint="gene"
					tableName="Genes"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
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
