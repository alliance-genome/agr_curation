import React, { useRef, useState } from 'react'
import { Toast } from 'primereact/toast';
import { BooleanTemplate } from '../../components/BooleanTemplate';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Tooltip } from 'primereact/tooltip';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_FIELDS } from '../../constants/FilterFields';

export const VocabulariesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const nameBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`a${rowData.id}`}>{rowData.name}</EllipsisTableCell>
				<Tooltip target={`.a${rowData.id}`} content={rowData.name} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}

	const descriptionBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`b${rowData.id}`}>{rowData.vocabularyDescription}</EllipsisTableCell>
				<Tooltip target={`.b${rowData.id}`} content={rowData.vocabularyDescription} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}


	const columns = [
		{ 
			field: "name", 
			header: "Name", 
			body: nameBodyTemplate,
			filterElement: {type: "input", fieldSet: "nameFieldSet",}
		},
		{ 
			field: "vocabularyDescription", 
			header: "Description", 
			body: descriptionBodyTemplate,
			filterElement: {type: "input", fieldSet: "descriptionFieldSet"}
		},
		{ 
			field: "obsolete", 
			header: "Obsolete", 
			body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
			filterElement: {type: "dropdown", fieldSet: "obsoleteFieldSet", options: [{ text: "true" }, { text: "false" }], optionField: "text"},
		}
	]

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("Vocabularies", defaultColumnNames);

	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable 
					endpoint="vocabulary" 
					tableName="Vocabularies" 
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
