import React, { useState, useRef } from 'react';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from "../../components/EllipsisTableCell";

import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';


export const AffectedGenomicModelTable = () => {
	const defaultVisibleColumns = ["Curie", "Name", "Sub Type", "Taxon"];

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const nameTemplate = (rowData) => {
		return (
			<>
				<div className={`overflow-hidden text-overflow-ellipsis ${rowData.curie.replace(':', '')}`} dangerouslySetInnerHTML={{ __html: rowData.name }} />
				<Tooltip target={`.${rowData.curie.replace(':', '')}`}>
					<div dangerouslySetInnerHTML={{ __html: rowData.name }} />
				</Tooltip>
			</>
		)
	}

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
			filterConfig: FILTER_CONFIGS.curieFilterConfig
		},
		{
			field: "name",
			header: "Name",
			body: nameTemplate,
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.nameFilterConfig
		},
		{ 	
			field: "subtype.name",
			header: "Sub Type",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.subtypeFilterConfig
		},
		{
			field: "taxon.name",
			header: "Taxon",
			sortable: isEnabled,
			body: taxonBodyTemplate,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig
		}
 ];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("AffectedGenomicModels", defaultColumnNames);

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
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					initialColumnWidth={100 / columns.length}
					defaultVisibleColumns={defaultVisibleColumns}
					errorObject = {{errorMessages, setErrorMessages}}
				/>
			</div>
	)
}
