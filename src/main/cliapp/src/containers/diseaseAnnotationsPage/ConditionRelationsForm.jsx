import React, { useRef } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { DialogErrorMessageComponent } from '../../components/Error/DialogErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { FormErrorMessageComponent } from '../../components/Error/FormErrorMessageComponent';
import { ExConAutocompleteTemplate } from '../../components/Autocomplete/ExConAutocompleteTemplate';
import { AutocompleteMultiEditor } from '../../components/Autocomplete/AutocompleteMultiEditor';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { Endpoints } from '../../constants/Endpoints';

export const ConditionRelationsForm = ({
	dispatch,
	conditionRelations,
	showConditionRelations,
	errorMessages,
	searchService,
	buttonIsDisabled,
	editingRows,
}) => {
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const conditionRelationTypeTerms = useControlledVocabularyService('condition_relation');
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const onRowEditChange = (e) => {
		console.log(e);
	};

	const createNewRelationHandler = (event) => {
		event.preventDefault();

		let count = conditionRelations ? conditionRelations.length : 0;
		dispatch({ type: 'ADD_NEW_RELATION', count });
	};

	const onConditionRelationTypeEditorValueChange = (editorOptions, event) => {
		dispatch({
			type: 'EDIT_ROW',
			tableType: 'conditionRelations',
			field: 'conditionRelationType',
			index: editorOptions.rowIndex,
			value: event.target.value,
		});
	};

	const conditionRelationTypeEditor = (editorOptions) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="conditionRelationType"
					options={conditionRelationTypeTerms}
					editorChange={onConditionRelationTypeEditorValueChange}
					editorOptions={editorOptions}
					showClear={false}
					dataKey="id"
				/>
				<DialogErrorMessageComponent
					errorMessages={errorMessages[editorOptions.rowIndex]}
					errorField={'conditionRelationType'}
				/>
			</>
		);
	};

	const onConditionsEditorValueChange = (event, setValue, editorOptions) => {
		setValue(event.target.value);
		dispatch({
			type: 'EDIT_ROW',
			tableType: 'conditionRelations',
			field: 'conditions',
			index: editorOptions.rowIndex,
			value: event.target.value,
		});
	};

	const conditionSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ['conditionSummary'];
		const endpoint = Endpoints.Annotation.EXPERIMENTAL_CONDITION;
		const filterName = 'conditionSummaryFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const conditionsEditorTemplate = (editorOptions) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={conditionSearch}
					initialValue={editorOptions.rowData.conditions}
					editorOptions={editorOptions}
					fieldName="conditions"
					subField="conditionSummary"
					valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
						<ExConAutocompleteTemplate
							item={item}
							setAutocompleteHoverItem={setAutocompleteHoverItem}
							op={op}
							query={query}
						/>
					)}
					onValueChangeHandler={onConditionsEditorValueChange}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[editorOptions.rowIndex]} errorField={'conditions'} />
			</>
		);
	};

	const onInternalEditorValueChange = (editorOptions, event) => {
		dispatch({
			type: 'EDIT_ROW',
			tableType: 'conditionRelations',
			field: 'internal',
			index: editorOptions.rowIndex,
			value: event.value.name,
		});
	};

	const internalEditor = (editorOptions) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms?.terms || []}
					editorChange={onInternalEditorValueChange}
					editorOptions={editorOptions}
					field={'internal'}
				/>
				<FormErrorMessageComponent errorMessages={errorMessages[editorOptions.rowIndex]} errorField={'internal'} />
			</>
		);
	};

	const handleDeleteRelation = (event, editorOptions) => {
		event.preventDefault();
		dispatch({
			type: 'DELETE_ROW',
			tableType: 'conditionRelations',
			showType: 'showConditionRelations',
			index: editorOptions.rowIndex,
		});
	};

	const deleteAction = (editorOptions) => {
		return (
			<Button
				icon="pi pi-trash"
				className="p-button-text"
				onClick={(event) => {
					handleDeleteRelation(event, editorOptions);
				}}
			/>
		);
	};

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" className="w-10" />
			{showConditionRelations && (
				<DataTable
					value={conditionRelations}
					dataKey="dataKey"
					showGridlines
					editMode="row"
					editingRows={editingRows}
					onRowEditChange={onRowEditChange}
					ref={tableRef}
					resizableColumns
					columnResizeMode="expand"
				>
					<Column
						editor={(editorOptions) => deleteAction(editorOptions)}
						body={(editorOptions) => deleteAction(editorOptions)}
						style={{ maxWidth: '4rem' }}
						frozen
						headerClassName="surface-0"
						bodyStyle={{ textAlign: 'center' }}
					/>
					<Column
						editor={conditionRelationTypeEditor}
						field="conditionRelationType.name"
						header="Relation"
						headerClassName="surface-0"
					/>
					<Column
						editor={conditionsEditorTemplate}
						field="conditions.condSummary"
						header="Conditions"
						headerClassName="surface-0"
					/>
					<Column editor={internalEditor} field="internal" header="Internal" headerClassName="surface-0" />
				</DataTable>
			)}
			<div className={`${showConditionRelations ? 'pt-3' : ''} p-field p-col`}>
				<Button
					label="Add Experimental Condition"
					onClick={createNewRelationHandler}
					style={{ width: '50%' }}
					disabled={buttonIsDisabled}
				/>
			</div>
		</div>
	);
};
