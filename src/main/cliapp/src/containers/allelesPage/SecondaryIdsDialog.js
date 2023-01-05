import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { ListTableCell } from '../../components/ListTableCell';
import { DialogErrorMessageComponent } from '../../components/DialogErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { InputTextEditor } from '../../components/InputTextEditor';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { SearchService } from '../../service/SearchService';
import { ValidationService } from '../../service/ValidationService';
import { AutocompleteMultiEditor } from "../../components/Autocomplete/AutocompleteMultiEditor";
import { autocompleteSearch, buildAutocompleteFilter, multipleAutocompleteOnChange, getRefStrings } from "../../utils/utils";
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';

export const SecondaryIdsDialog = ({
													originalSecondaryIdsData,
													setOriginalSecondaryIdsData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalSecondaryIds, isInEdit, dialog, rowIndex, mainRowProps } = originalSecondaryIdsData;
	const [localSecondaryIds, setLocalSecondaryIds] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const validationService = new ValidationService();
	const searchService = new SearchService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localSecondaryIds = cloneSecondaryIds(originalSecondaryIds);
		setLocalSecondaryIds(_localSecondaryIds);

		if(isInEdit){
			let rowsObject = {};
			if(_localSecondaryIds) {
				_localSecondaryIds.forEach((mt) => {
					rowsObject[`${mt.dataKey}`] = true;
				});
			}
			setEditingRows(rowsObject);
		}else{
			setEditingRows({});
		}
		rowsEdited.current = 0;
	};

	const onRowEditChange = (e) => {
		setEditingRows(e.data);
	}

	const onRowEditCancel = (event) => {
		let _editingRows = { ...editingRows };
		delete _editingRows[event.index];
		setEditingRows(_editingRows);
		let _localSecondaryIds = [...localSecondaryIds];//add new note support
		if(originalSecondaryIds && originalSecondaryIds[event.index]){
			let dataKey = _localSecondaryIds[event.index].dataKey;
			_localSecondaryIds[event.index] = global.structuredClone(originalSecondaryIds[event.index]);
			_localSecondaryIds[event.index].dataKey = dataKey;
			setLocalSecondaryIds(_localSecondaryIds);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInSecondaryIds(event.data,event.index);
	};

	const compareChangesInSecondaryIds = (data,index) => {
		if(originalSecondaryIds && originalSecondaryIds[index]) {
			if (data.internal !== originalSecondaryIds[index].internal) {
				rowsEdited.current++;
			}
			if (data.secondaryId !== originalSecondaryIds[index].secondaryId) {
				rowsEdited.current++;
			}
			if ((originalSecondaryIds[index].evidence && !data.evidence) ||
				(!originalSecondaryIds[index].evidence && data.evidence) ||
				(data.evidence && (data.evidence.length !== originalSecondaryIds[index].evidence.length))) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var j = 0; j < data.evidence.length; j++) {
						if (data.evidence[j].curie !== originalSecondaryIds[index].evidence[j].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
		}

		if((localSecondaryIds.length > originalSecondaryIds?.length) || !originalSecondaryIds){
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateSecondaryId(localSecondaryIds[event.index]);
		const errorMessagesCopy = [...errorMessages];
		errorMessagesCopy[event.index] = {};
		let _editingRows = { ...editingRows };
		if (result.isError) {
			let reported = false;
			Object.keys(result.data).forEach((field) => {
				let messageObject = {
					severity: "error",
					message: result.data[field]
				};
				errorMessagesCopy[event.index][field] = messageObject;
				if(!reported) {
					toast_topright.current.show([
						{ life: 7000, severity: 'error', summary: 'Update error: ',
						detail: 'Could not update AlleleSecondaryId [' + localSecondaryIds[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInSecondaryIds(event.data,event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localSecondaryIds = [...localSecondaryIds];
		_localSecondaryIds[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalSecondaryIds(_localSecondaryIds);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalSecondaryIdsData((originalSecondaryIdsData) => {
			return {
				...originalSecondaryIdsData,
				dialog: false,
			};
		});
		let _localSecondaryIds = [];
		setLocalSecondaryIds(_localSecondaryIds);
	};

	const validateSecondaryId = async (sid) => {
		let _sid = global.structuredClone(sid);
		delete _sid.dataKey;
		const result = await validationService.validate('allelesecondaryidslotannotation', _sid);
		return result;
	};

	const cloneSecondaryIds = (clonableSecondaryIds) => {
		let _clonableSecondaryIds = global.structuredClone(clonableSecondaryIds);
		if(_clonableSecondaryIds) {
			let counter = 0 ;
			_clonableSecondaryIds.forEach((note) => {
				note.dataKey = counter++;
			});
		} else {
			_clonableSecondaryIds = [];
		};
		return _clonableSecondaryIds;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const note of localSecondaryIds) {
			delete note.dataKey;
		}
		mainRowProps.rowData.alleleSecondaryIds = localSecondaryIds;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleSecondaryIds = localSecondaryIds;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleSecondaryIds"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalSecondaryIdsData((originalSecondaryIdsData) => {
				return {
					...originalSecondaryIdsData,
					dialog: false,
				}
			}
		);
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const onInternalEditorValueChange = (props, event) => {
		let _localSecondaryIds = [...localSecondaryIds];
		_localSecondaryIds[props.rowIndex].internal = event.value.name;
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
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"internal"} />
			</>
		);
	};

	const secondaryIdTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.secondaryId}</EllipsisTableCell>;
	};

	const secondaryIdEditorTemplate = (props) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={"secondaryId"}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"secondaryId"} />
			</>
		);
	};

	const evidenceTemplate = (rowData) => {
		if (rowData && rowData.evidence) {
			const refStrings = getRefStrings(rowData.evidence);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item}
					</EllipsisTableCell>
				);
			};
			return <ListTableCell template={listTemplate} listData={refStrings} />
		}
	};

	const evidenceEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={evidenceSearch}
					initialValue={props.rowData.evidence}
					rowProps={props}
					fieldName='evidence'
					subField='curie'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onEvidenceValueChange}
				/>
				<DialogErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField={"evidence"}
				/>
			</>
		);
	};

	const onEvidenceValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "evidence", setFieldValue);
	};

	const evidenceSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["curie", "cross_references.curie"];
		const endpoint = "literature-reference";
		const filterName = "evidenceFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const footerTemplate = () => {
		if (!isInEdit) {
			return null;
		};
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Secondary ID" icon="pi pi-plus" onClick={createNewSecondaryIdHandler}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const createNewSecondaryIdHandler = (event) => {
		let cnt = localSecondaryIds ? localSecondaryIds.length : 0;
		const _localSecondaryIds = global.structuredClone(localSecondaryIds);
		_localSecondaryIds.push({
			dataKey : cnt,
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalSecondaryIds(_localSecondaryIds);
	};

	const handleDeleteSecondaryId = (event, props) => {
		let _localSecondaryIds = global.structuredClone(localSecondaryIds);
		if(props.dataKey){
			_localSecondaryIds.splice(props.dataKey, 1);
		}else {
			_localSecondaryIds.splice(props.rowIndex, 1);
		}
		setLocalSecondaryIds(_localSecondaryIds);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteSecondaryId(event, props) }}/>
		);
	}

	let headerGroup = 
			<ColumnGroup>
				<Row>
					<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
					<Column header="Secondary ID" />
					<Column header="Internal" />
					<Column header="Evidence" />
				</Row>
			</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-6' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Secondary IDs</h3>
				<DataTable value={localSecondaryIds} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={secondaryIdEditorTemplate} field="secondaryId" header="Secondary ID" headerClassName='surface-0' body={secondaryIdTemplate}/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column editor={evidenceEditorTemplate} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={evidenceTemplate}/>
				</DataTable>
			</Dialog>
		</div>
	);
};
