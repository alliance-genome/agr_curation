import React, { useRef, useState } from 'react'
import { Toast } from 'primereact/toast';
import { NameTemplate } from './NameTemplate';
import { BooleanTemplate } from '../../components/BooleanTemplate';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';

export const NCBITaxonOntologyComponent = () => {
	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [
		{ 
			field: "curie", 
			header: "Curie", 
			filterElement: {type: "input", filterName: "curieFilter", fields: ["curie"]}
		},
		{ 
			field: "name", 
			header: "Name", 
			body: (rowData) => <NameTemplate rowData={rowData}/>,
			filterElement: {type: "input", filterName: "nameFilter", fields: ["name"]}
		},
		{ 
			field: "obsolete", 
			header: "Obsolete", 
			body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
			filterElement: {type: "input", filterName: "obsoleteFilter", fields: ["obsolete"]}
		}
	]

	return (
		<>
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<GenericDataTable
				endpoint={"ncbitaxonterm"}
				tableName={"NCBITaxon"}
				columns={columns}
				isEditable={false}
				isEnabled={isEnabled}
				setIsEnabled={setIsEnabled}
				toasts={{toast_topleft, toast_topright }}
				initialColumnWidth={25}
				errorObject = {{errorMessages, setErrorMessages}}
			/>
		</>
	)

}
