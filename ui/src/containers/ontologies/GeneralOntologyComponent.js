import React, { useRef, useState } from 'react'
import { Toast } from 'primereact/toast';
import { NameTemplate } from './NameTemplate';
import { TabView, TabPanel } from 'primereact/tabview';
import { DefinitionTemplate } from './DefinitionTemplate';
import { BooleanTemplate } from '../../components/BooleanTemplate';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { GenericDataTree } from '../../components/GenericDataTree';

export const GeneralOntologyComponent = ({name, endpoint, showNamespace, showAbbreviation, hideDefinition}) => {
	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const [activeTabIndex, setActiveTabIndex] = useState(0);

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const columns = [];

	columns.push(
		{
			field: "curie",
			header: "Curie",
			filterElement: {type: "input", filterName: "curieFilter", fields: ["curie"]}
		}
	);
	columns.push(
		{
			field: "name",
			header: "Name",
			body: (rowData) => <NameTemplate rowData={rowData}/>,
			filterElement: {type: "input", filterName: "nameFilter", fields: ["name"]}
		},
	);
	if(!hideDefinition) {
		columns.push(
			{
				field: "definition",
				header: "Definition",
				body: (rowData) => <DefinitionTemplate rowData={rowData} />,
				filterElement: {type: "input", filterName: "definitionFilter", fields: ["definition"]}
			},
		);
	}
	if(showAbbreviation) {
		columns.push(
			{
				field: "abbreviation",
				header: "Abbreviation",
				filterElement: {type: "input", filterName: "abbreviationFilter", fields: ["abbreviation"]}
			}
		);
	}
	if(showNamespace) {
		columns.push(
			{
				field: "namespace",
				header: "Name Space",
				filterElement: {type: "input", filterName: "abbreviationFilter", fields: ["abbreviation"]}
			}
		);
	}
	columns.push(
		{
			field: "obsolete",
			header: "Obsolete",
			body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
			filterElement: {type: "input", filterName: "obsoleteFilter", fields: ["obsolete"]}
		}
	);

	return (
		<>
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<TabView activeIndex={activeTabIndex} onTabChange={(e) => setActiveTabIndex(e.index)}>
				<TabPanel header="Table View">
					<GenericDataTable
						endpoint={endpoint}
						tableName={name}
						columns={columns}
						isEditable={false}
						isEnabled={isEnabled}
						setIsEnabled={setIsEnabled}
						toasts={{toast_topleft, toast_topright }}
						initialColumnWidth={17}
						errorObject = {{errorMessages, setErrorMessages}}
					/>
					</TabPanel>
					<TabPanel header="Tree View">
							<GenericDataTree
						endpoint={endpoint}
						treeName={name}
						toasts={{toast_topleft, toast_topright }}
						errorObject = {{errorMessages, setErrorMessages}}
					/>
					</TabPanel>
			</TabView>
		</>
	)
}
