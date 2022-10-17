import React, { useRef, useState } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';

import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';

export const AllelesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const symbolTemplate = (rowData) => {
		return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.symbol }} />
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

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: { isEnabled },
			filter: true,
			filterElement: {type: "input", filterName: "curieFilter", fields: ["curie"]}, 
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
