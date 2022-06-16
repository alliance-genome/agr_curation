import React, { useRef, useState } from 'react'
import { Toast } from 'primereact/toast';
import { NameTemplate } from './NameTemplate';
import { DefinitionTemplate } from './DefinitionTemplate';
import { BooleanTemplate } from '../../components/BooleanTemplate';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';

export const SOOntologyComponent = () => {
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
			field: "definition", 
			header: "Definition", 
			body: (rowData) => <DefinitionTemplate rowData={rowData} />,
			filterElement: {type: "input", filterName: "definitionFilter", fields: ["definition"]}
		},
		{ 
			field: "namespace", 
			header: "Name Space",
			filterElement: {type: "input", filterName: "abbreviationFilter", fields: ["abbreviation"]}
		},
		{ 
			field: "obsolete", 
			header: "Obsolete", 
			body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
			filterElement: {type: "input", filterName: "obsoleteFilter", fields: ["obsolete"]}
		},
	]

	return (
		<>
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<GenericDataTable
				endpoint={"soterm"}
				tableName={"SO"}
				columns={columns}
				isEditable={false}
				isEnabled={isEnabled}
				setIsEnabled={setIsEnabled}
				toasts={{toast_topleft, toast_topright }}
				initialColumnWidth={17}
				errorObject = {{errorMessages, setErrorMessages}}
			/>
		</>
	)
}
