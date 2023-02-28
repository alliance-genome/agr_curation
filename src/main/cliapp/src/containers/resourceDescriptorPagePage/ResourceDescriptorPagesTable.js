import React, { useRef, useState } from 'react'
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_FIELDS } from '../../constants/FilterFields';

export const ResourceDescriptorPagesTable = () => {
	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const resourceDescriptorBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`a${rowData.id}`}>{rowData.resourceDescriptor.prefix} ({rowData.resourceDescriptor.name})</EllipsisTableCell>
				<Tooltip target={`.a${rowData.id}`} content={`${rowData.resourceDescriptor.prefix} (${rowData.resourceDescriptor.name})`} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}

	const nameBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`b${rowData.id}`}>{rowData.name}</EllipsisTableCell>
				<Tooltip target={`.b${rowData.id}`} content={rowData.name} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}

	const urlTemplateBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`c${rowData.id}`}>{rowData.urlTemplate}</EllipsisTableCell>
				<Tooltip target={`.c${rowData.id}`} content={rowData.urlTemplate} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}	

	const pageDescriptionBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`d${rowData.id}`}>{rowData.pageDescription}</EllipsisTableCell>
				<Tooltip target={`.d${rowData.id}`} content={rowData.pageDescription} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}

	const columns = [
		{ 
			field: "resourceDescriptor.prefix", 
			header: "Resource Descriptor", 
			body: resourceDescriptorBodyTemplate,
			filterElement: {type: "input", filterName: "resourceDescriptorFilter", fields: FILTER_FIELDS.resourceDescriptorFilter}
		},
		{ 
			field: "name", 
			header: "Name", 
			body: nameBodyTemplate,
			filterElement: {type: "input", filterName: "nameFilter", fields: FILTER_FIELDS.nameFilter}
		},
		{ 
			field: "urlTemplate", 
			header: "URL Template", 
			body: urlTemplateBodyTemplate,
			filterElement: {type: "input", filterName: "urlTemplateFilter", fields: [FILTER_FIELDS.urlTemplateFilter]}
		},
		{ 
			field: "pageDescription", 
			header: "Page Description", 
			body: pageDescriptionBodyTemplate,
			filterElement: {type: "input", filterName: "pageDescriptionFilter", fields: [FILTER_FIELDS.pageDescriptionFilter]}
		}
	]

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("ResourceDescriptorPages", defaultColumnNames);

	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable 
					endpoint="resourcedescriptorpage" 
					tableName="Resource Descriptor Pages" 
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
