import React, { useRef, useState } from 'react'
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';

export const ResourceDescriptorsTable = () => {
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

	const prefixBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`b${rowData.id}`}>{rowData.prefix}</EllipsisTableCell>
				<Tooltip target={`.b${rowData.id}`} content={rowData.prefix} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}

	const idPatternBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`c${rowData.id}`}>{rowData.idPattern}</EllipsisTableCell>
				<Tooltip target={`.c${rowData.id}`} content={rowData.idPattern} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}	

	const idExampleBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`d${rowData.id}`}>{rowData.idExample}</EllipsisTableCell>
				<Tooltip target={`.d${rowData.id}`} content={rowData.idExample} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}

	const defaultUrlTemplateBodyTemplate = (rowData) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`e${rowData.id}`}>{rowData.defaultUrlTemplate}</EllipsisTableCell>
				<Tooltip target={`.e${rowData.id}`} content={rowData.defaultUrlTemplate} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}

	const synonymsBodyTemplate = (rowData) => {
		if (rowData?.synonyms && rowData.synonyms.length > 0) {
			const sortedSynonyms = rowData.synonyms.sort();
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<div className={`f${rowData.id}${rowData.synonyms[0]}`}>
						<ListTableCell template={listTemplate} listData={sortedSynonyms}/>
					</div>
					<Tooltip target={`.f${rowData.id}${rowData.synonyms[0]}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={sortedSynonyms}/>
					</Tooltip>
				</>
			);
		}
	};
		
	const columns = [
		{ 
			field: "prefix", 
			header: "Prefix", 
			body: prefixBodyTemplate,
			filterElement: {type: "input", filterName: "prefixFilter", fields: ["prefix"]}
		},
		{ 
			field: "name", 
			header: "Name", 
			body: nameBodyTemplate,
			filterElement: {type: "input", filterName: "nameFilter", fields: ["name"]}
		},
		{ 
			field: "synonyms", 
			header: "Synonyms", 
			body: synonymsBodyTemplate,
			filterElement: {type: "input", filterName: "synonymsFilter", fields: ["synonyms"]}
		},
		{ 
			field: "idPattern", 
			header: "ID Pattern", 
			body: idPatternBodyTemplate,
			filterElement: {type: "input", filterName: "idPatternFilter", fields: ["idPattern"]}
		},
		{ 
			field: "idExample", 
			header: "ID Example", 
			body: idExampleBodyTemplate,
			filterElement: {type: "input", filterName: "idExampleFilter", fields: ["idExample"]}
		},
		{ 
			field: "defaultUrlTemplate", 
			header: "Default URL Template", 
			body: defaultUrlTemplateBodyTemplate,
			filterElement: {type: "input", filterName: "defaultUrlTemplateFilter", fields: ["defaultUrlTemplate"]}
		}
	]

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});


	const initialTableState = getDefaultTableState("ResourceDescriptors", defaultColumnNames);

	return (
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable 
					endpoint="resourcedescriptor" 
					tableName="Resource Descriptors" 
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