import React, { useRef, useState } from 'react'
import { useMutation } from 'react-query';
import { Toast } from 'primereact/toast';
import { BooleanTemplate } from '../../components/BooleanTemplate';
import { VocabularyService } from "../../service/VocabularyService";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ErrorMessageComponent } from "../../components/Error/ErrorMessageComponent";
import { Tooltip } from 'primereact/tooltip';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { InputTextEditor } from "../../components/InputTextEditor";
import { TrueFalseDropdown } from "../../components/TrueFalseDropDownSelector";

export const VocabulariesTable = () => {

	const [isInEditMode, setIsInEditMode] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const obsoleteTerms = useControlledVocabularyService('generic_boolean_terms');

	let vocabularyService = new VocabularyService();

	const mutation = useMutation(updatedVocabulary => {
		if (!vocabularyService) {
			vocabularyService = new VocabularyService();
		}
		return vocabularyService.saveVocabulary(updatedVocabulary);
	});
	
	const stringBodyTemplate = (rowData, field) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`${field}${rowData.id}`}>{rowData[field]}</EllipsisTableCell>
				<Tooltip target={`.${field}${rowData.id}`} content={rowData[field]} style={{ width: '450px', maxWidth: '450px' }} />
			</>
		)
	}

	const stringEditor = (props, field) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={field}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={field}/>
			</>
		);
	};

	const obsoleteEditorTemplate = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={obsoleteTerms}
					editorChange={onObsoleteEditorValueChange}
					props={props}
					field={"obsolete"}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"obsolete"} />
			</>
		);
	};
	
	const onObsoleteEditorValueChange = (props, event) => {
		let updatedTerms = [...props.props.value];
		if (event.value || event.value === '') {
			updatedTerms[props.rowIndex].obsolete = JSON.parse(event.value.name);
		}
	};

	const columns = [
		{ 
			field: "name", 
			header: "Name", 
			body: (rowData) => stringBodyTemplate(rowData, "name"),
			filterConfig: FILTER_CONFIGS.nameFilterConfig,
			editor: (props) => stringEditor(props, "name")
		},
		{ 
			field: "vocabularyDescription", 
			header: "Description", 
			body: (rowData) => stringBodyTemplate(rowData, "vocabularyDescription"),
			filterConfig: FILTER_CONFIGS.vocabularyDescriptionFilterConfig,
			editor: (props) => stringEditor(props, "vocabularyDescription")
		},
		{ 
			field: "obsolete", 
			header: "Obsolete", 
			body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			editor: (props) => obsoleteEditorTemplate(props)
		},
		{
			field: "vocabularyLabel",
			header: "Label",
			filterConfig: FILTER_CONFIGS.vocabularyLabelFilterConfig,
			body: (rowData) => stringBodyTemplate(rowData, "vocabularyLabel")
		}
	]

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 20;
	});

	const initialTableState = getDefaultTableState("Vocabularies", defaultColumnNames, undefined, widthsObject);

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
					isEditable={true}
					mutation={mutation}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{toast_topleft, toast_topright }}
					errorObject = {{errorMessages, setErrorMessages}}
					widthsObject={widthsObject}
					deletionEnabled={true}
					deletionMethod={vocabularyService.deleteVocabulary}
				/>
			</div>
	)

}
