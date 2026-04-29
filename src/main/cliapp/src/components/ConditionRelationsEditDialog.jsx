import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { ValidationService } from '../service/ValidationService';
import { DeleteAction } from './Actions/DeletionAction';
import { InternalEditor } from './Editors/InternalEditor';
import { ControlledVocabularyEditor } from './Editors/ControlledVocabularyEditor';
import { DialogErrorMessageComponent } from './Error/DialogErrorMessageComponent';
import { ExConAutocompleteTemplate } from './Editors/autocomplete/base/templates/ExConAutocompleteTemplate';
import { SearchService } from '../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter, multipleAutocompleteOnChange } from '../utils/utils';
import { AutocompleteMultiEditor } from './Editors/autocomplete/base/AutocompleteMultiEditor';
import { Endpoints } from '../constants/Endpoints';

export const ConditionRelationsEditDialog = ({
	originalConditionRelationsData,
	setOriginalConditionRelationsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalConditionRelations, isInEdit, dialog, rowIndex, mainRowProps } = originalConditionRelationsData;
	const [localConditionRelations, setLocalConditionRelations] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const searchService = new SearchService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localConditionRelations = cloneRelations(originalConditionRelations);
		setLocalConditionRelations(_localConditionRelations);

		let rowsObject = {};
		if (_localConditionRelations) {
			_localConditionRelations.forEach((relation) => {
				rowsObject[`${relation.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const onRowEditChange = () => {
		return null;
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalConditionRelationsData((originalConditionRelationsData) => {
			return {
				...originalConditionRelationsData,
				dialog: false,
			};
		});
		setLocalConditionRelations([]);
	};

	const cloneRelations = (clonableRelations) => {
		let _clonableRelations = structuredClone(clonableRelations);
		if (_clonableRelations) {
			_clonableRelations.forEach((relation) => {
				relation.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableRelations = [];
		}
		return _clonableRelations;
	};

	const conditionRelationTypeOnChangeHandler = (editorOptions, event) => {
		editorOptions.editorCallback(event.value);
		setLocalConditionRelations((prev) => {
			const updated = [...prev];
			updated[editorOptions.rowIndex] = { ...updated[editorOptions.rowIndex], conditionRelationType: event.value };
			return updated;
		});
	};

	const onConditionRelationValueChange = (event, setFieldValue, editorOptions) => {
		multipleAutocompleteOnChange(editorOptions, event, 'conditions', setFieldValue);
		setLocalConditionRelations((prev) => {
			const updated = [...prev];
			updated[editorOptions.rowIndex] = { ...updated[editorOptions.rowIndex], conditions: event.target.value };
			return updated;
		});
	};

	const conditionSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = [
			'conditionSummary',
			'conditionId.curie',
			'conditionClass.curie',
			'conditionTaxon.curie',
			'conditionGeneOntology.curie',
			'conditionChemical.curie',
			'conditionAnatomy.curie',
		];
		const endpoint = Endpoints.Annotation.EXPERIMENTAL_CONDITION;
		const filterName = 'conditionSummaryFilter';
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	};

	const internalOnChangeHandler = (editorOptions, event) => {
		editorOptions.editorCallback(event.target.value?.name);
		setLocalConditionRelations((prev) => {
			const updated = [...prev];
			updated[editorOptions.rowIndex] = { ...updated[editorOptions.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const createNewRelationHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localConditionRelations = structuredClone(localConditionRelations);
		_localConditionRelations.push({
			dataKey: newKey,
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalConditionRelations(_localConditionRelations);
	};

	const handleDeleteConditionRelation = (e, dataKey) => {
		let _localConditionRelations = structuredClone(localConditionRelations);
		_localConditionRelations = _localConditionRelations.filter((relation) => relation.dataKey !== dataKey);
		setLocalConditionRelations(_localConditionRelations);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localConditionRelations = structuredClone(localConditionRelations);
		for (const relation of _localConditionRelations) {
			delete relation.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localConditionRelations);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['conditionRelations'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalConditionRelationsData((originalConditionRelationsData) => {
			return {
				...originalConditionRelationsData,
				dialog: false,
			};
		});
	};

	const cleanForValidation = (obj) => {
		const _obj = structuredClone(obj);
		delete _obj.dataKey;
		delete _obj.updatedBy;
		delete _obj.createdBy;
		delete _obj.dateUpdated;
		delete _obj.dateCreated;
		return _obj;
	};

	const validateAndSave = async () => {
		setIsValidating(true);
		try {
			let hasErrors = false;
			const newErrorMessages = {};

			for (let i = 0; i < localConditionRelations.length; i++) {
				const _relation = cleanForValidation(localConditionRelations[i]);
				const result = await validationService.validate(Endpoints.Annotation.CONDITION_RELATION, _relation);
				const dataKey = localConditionRelations[i].dataKey;
				if (result.isError) {
					hasErrors = true;
					newErrorMessages[dataKey] = {};
					if (result.data) {
						Object.keys(result.data).forEach((field) => {
							newErrorMessages[dataKey][field] = {
								severity: 'error',
								message: result.data[field],
							};
						});
					}
				}
			}

			if (hasErrors) {
				setErrorMessages(newErrorMessages);
				toast_topright.current.show([
					{
						life: 7000,
						severity: 'error',
						summary: 'Validation error',
						detail: 'Please fix validation errors before saving',
						sticky: false,
					},
				]);
			} else {
				saveDataHandler();
			}
		} finally {
			setIsValidating(false);
		}
	};

	const footerTemplate = () => {
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Condition" icon="pi pi-plus" onClick={createNewRelationHandler} />
				<Button
					label="Keep Edits"
					icon={isValidating ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
					onClick={validateAndSave}
					disabled={isValidating}
				/>
			</div>
		);
	};

	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Relation" />
				<Column header="Conditions" />
				<Column header="Internal" />
			</Row>
		</ColumnGroup>
	);

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog
				visible={dialog && isInEdit}
				className="w-10 min-w-40rem"
				modal
				onHide={hideDialog}
				closable={false}
				onShow={showDialogHandler}
				footer={footerTemplate}
			>
				<h3>Experimental Conditions</h3>
				<DataTable
					value={localConditionRelations}
					dataKey="dataKey"
					showGridlines
					editMode="row"
					headerColumnGroup={headerGroup}
					editingRows={editingRows}
					onRowEditChange={onRowEditChange}
					ref={tableRef}
					cellMemo={false}
				>
					<Column
						editor={(editorOptions) => (
							<DeleteAction deletionHandler={handleDeleteConditionRelation} id={editorOptions?.rowData?.dataKey} />
						)}
						className="max-w-4rem"
						bodyClassName="text-center"
						headerClassName="surface-0"
						frozen
					/>
					<Column
						editor={(editorOptions) => {
							return (
								<ControlledVocabularyEditor
									editorOptions={editorOptions}
									onChangeHandler={conditionRelationTypeOnChangeHandler}
									errorMessages={errorMessages}
									dataKey={editorOptions?.rowData?.dataKey}
									rowIndex={editorOptions.rowIndex}
									vocabType="condition_relation"
									field="conditionRelationType"
									showClear={false}
								/>
							);
						}}
						field="conditionRelationType"
						header="Relation"
						headerClassName="surface-0"
					/>
					<Column
						editor={(editorOptions) => {
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
										onValueChangeHandler={onConditionRelationValueChange}
									/>
									<DialogErrorMessageComponent
										errorMessages={errorMessages[editorOptions?.rowData?.dataKey]}
										errorField={'conditions'}
									/>
								</>
							);
						}}
						field="conditions"
						header="Conditions"
						headerClassName="surface-0"
					/>
					<Column
						editor={(editorOptions) => {
							return (
								<InternalEditor
									editorOptions={editorOptions}
									rowIndex={editorOptions.rowIndex}
									errorMessages={errorMessages}
									dataKey={editorOptions?.rowData?.dataKey}
									internalOnChangeHandler={internalOnChangeHandler}
								/>
							);
						}}
						field="internal"
						header="Internal"
						headerClassName="surface-0"
					/>
				</DataTable>
			</Dialog>
		</div>
	);
};
