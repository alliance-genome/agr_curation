import React, { useRef, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { DialogErrorMessageComponent } from '../../components/DialogErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";
import {ExConAutocompleteTemplate} from "../../components/Autocomplete/ExConAutocompleteTemplate";
import {AutocompleteMultiEditor} from "../../components/Autocomplete/AutocompleteMultiEditor";
import {autocompleteSearch, buildAutocompleteFilter} from "../../utils/utils";

export const ConditionRelationsForm = ({ newAnnotationDispatch, conditionRelations, showConditionRelations, errorMessages, searchService, buttonIsDisabled }) => {
	const [editingRows, setEditingRows] = useState({});
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const conditionRelationTypeTerms = useControlledVocabularyService('Condition relation types');
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const onRowEditChange = (e) => {
		setEditingRows(e.data);
	}

	const createNewRelationHandler = (event) => {
		event.preventDefault();

		let count = conditionRelations ? conditionRelations.length : 0;
		newAnnotationDispatch({type: "ADD_NEW_RELATION", count})
		let _editingRows = { ...editingRows, ...{ [`${count}`]: true } };
		setEditingRows(_editingRows);

	};


	const onConditionRelationTypeEditorValueChange = (props, event) => {
		newAnnotationDispatch({
			type: "EDIT_ROW",
			tableType: "conditionRelations",
			field: "conditionRelationType",
			index: props.rowIndex,
			value: event.target.value
		})
	};

	const conditionRelationTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="conditionRelationType"
					options={conditionRelationTypeTerms}
					editorChange={onConditionRelationTypeEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"conditionRelationType"} />
			</>
		);
	};

	const onConditionsEditorValueChange = (event, setValue, props) => {
		setValue(event.target.value);
		newAnnotationDispatch({
			type: "EDIT_ROW",
			tableType: "conditionRelations",
			field: "conditions",
			index: props.rowIndex,
			value: event.target.value
		})
	}

	const conditionSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["conditionSummary"];
		const endpoint = "experimental-condition";
		const filterName = "conditionSummaryFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const conditionsEditorTemplate = (props) => {
		 return (
			 <>
				 <AutocompleteMultiEditor
					 search={conditionSearch}
					 initialValue={props.rowData.conditions}
					 rowProps={props}
					 fieldName='conditions'
					 subField='conditionSummary'
					 valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						 <ExConAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					 onValueChangeHandler={onConditionsEditorValueChange}
				 />
				 <DialogErrorMessageComponent
						errorMessages={errorMessages[props.rowIndex]}
						errorField={"conditions"}
				 />
			 </>
		 );
	};

	const onInternalEditorValueChange = (props, event) => {
		newAnnotationDispatch({
			type: "EDIT_ROW",
			tableType: "conditionRelations",
			field: "internal",
			index: props.rowIndex,
			value: event.value.name
		})
	}

	const internalEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onInternalEditorValueChange}
					props={props}
					field={"internal"}
				/>
				<FormErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"internal"} />
			</>
		);
	};

	const handleDeleteRelation = (event, props) => {
		newAnnotationDispatch({type: "DELETE_ROW", tableType: "conditionRelations", showType: "showConditionRelations", index: props.rowIndex})
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteRelation(event, props) }}/>
		);
	}

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			{/*<h3>Experimental Conditions</h3>*/}
			{showConditionRelations &&
				<DataTable value={conditionRelations} dataKey="dataKey" showGridlines editMode='row'
							 editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef}>
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={conditionRelationTypeEditor} field="conditionRelationType.name" header="Relation" headerClassName='surface-0' />
					<Column editor={conditionsEditorTemplate} field="conditions.conditionSummary" header="Conditions" headerClassName='surface-0' />
					<Column editor={internalEditor} field="internal" header="Internal" headerClassName='surface-0'/>
				</DataTable>
			}
			<div className={`${showConditionRelations ? "pt-3" : ""} p-field p-col`}>
				<Button label="Add Experimental Condition" onClick={createNewRelationHandler} style={{width:"50%"}} disabled={buttonIsDisabled} style={{width: '300px', padding: '5px'}}/>
			</div>
		</div>
	);
};
