import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { InputTextAreaEditor } from '../../components/InputTextAreaEditor';
import { DialogErrorMessageComponent } from '../../components/DialogErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ValidationService } from '../../service/ValidationService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { autocompleteSearch, buildAutocompleteFilter, multipleAutocompleteOnChange, getRefStrings } from '../../utils/utils';
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { ListTableCell } from '../../components/ListTableCell';
import { AutocompleteMultiEditor } from '../../components/Autocomplete/AutocompleteMultiEditor';
import { SearchService } from '../../service/SearchService';
import '../../assets/layout/layout.scss';

export const RelatedNotesDialog = ({
													originalRelatedNotesData,
													setOriginalRelatedNotesData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalRelatedNotes, isInEdit, dialog, rowIndex, mainRowProps } = originalRelatedNotesData;
	const [localRelatedNotes, setLocalRelatedNotes] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const noteTypeTerms = useControlledVocabularyService('Allele note types');
	const validationService = new ValidationService();
	const searchService = new SearchService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localRelatedNotes = cloneNotes(originalRelatedNotes);
		setLocalRelatedNotes(_localRelatedNotes);

		if(isInEdit){
			let rowsObject = {};
			if(_localRelatedNotes) {
				_localRelatedNotes.forEach((note) => {
					rowsObject[`${note.dataKey}`] = true;
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
		let _localRelatedNotes = [...localRelatedNotes];//add new note support
		if(originalRelatedNotes && originalRelatedNotes[event.index]){
			let dataKey = _localRelatedNotes[event.index].dataKey;
			_localRelatedNotes[event.index] = global.structuredClone(originalRelatedNotes[event.index]);
			_localRelatedNotes[event.index].dataKey = dataKey;
			setLocalRelatedNotes(_localRelatedNotes);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInNotes(event.data,event.index);
	};

	const compareChangesInNotes = (data,index) => {
		if(originalRelatedNotes && originalRelatedNotes[index]) {
			if ((originalRelatedNotes[index].references && !data.references) ||
					(!originalRelatedNotes[index].references && data.references) ||
					(data.references && (data.references.length !== originalRelatedNotes[index].references.length))
				) {
				rowsEdited.current++;
			} else {
				if (data.references) {
					for (var i = 0; i < data.references.length; i++) {
						if (data.references[i].curie !== originalRelatedNotes[index].references[i].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
			if (data.noteType.name !== originalRelatedNotes[index].noteType.name) {
				rowsEdited.current++;
			}
			if (data.internal !== originalRelatedNotes[index].internal) {
				rowsEdited.current++;
			}
			if (data.freeText !== originalRelatedNotes[index].freeText) {
				rowsEdited.current++;
			}
		}
		
		if((localRelatedNotes.length > originalRelatedNotes?.length) || !originalRelatedNotes){
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateNote(localRelatedNotes[event.index]);
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
						detail: 'Could not update note [' + localRelatedNotes[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInNotes(event.data,event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localRelatedNotes = [...localRelatedNotes];
		_localRelatedNotes[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalRelatedNotes(_localRelatedNotes);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalRelatedNotesData((originalRelatedNotesData) => {
			return {
				...originalRelatedNotesData,
				dialog: false,
			};
		});
		let _localRelatedNotes = [];
		setLocalRelatedNotes(_localRelatedNotes);
	};

	const validateNote = async (note) => {
		let _note = global.structuredClone(note);
		delete _note.dataKey;
		const result = await validationService.validate('note', _note);
		return result;
	};

	const cloneNotes = (clonableNotes) => {
		let _clonableNotes = global.structuredClone(clonableNotes);
		if(_clonableNotes) {
			let counter = 0 ;
			_clonableNotes.forEach((note) => {
				note.dataKey = counter++;
			});
		} else {
			_clonableNotes = [];
		};
		return _clonableNotes;
	};

	const createNewNoteHandler = (event) => {
		let cnt = localRelatedNotes ? localRelatedNotes.length : 0;
		localRelatedNotes.push({
			dataKey : cnt,
			noteType: {
				name : ""
			},
			internal : false
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const note of localRelatedNotes) {
			delete note.dataKey;
		}
		mainRowProps.rowData.relatedNotes = localRelatedNotes;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].relatedNotes = localRelatedNotes;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["relatedNotes"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});
		
		setOriginalRelatedNotesData((originalRelatedNotesData) => {
				return {
					...originalRelatedNotesData,
					dialog: false,
				}
			}
		);
	};

	const noteTypeTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.noteType.name}</EllipsisTableCell>;
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const textTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.freeText}</EllipsisTableCell>;
	};

	const onInternalEditorValueChange = (props, event) => {
		let _localRelatedNotes = [...localRelatedNotes];
		_localRelatedNotes[props.rowIndex].internal = event.value.name;
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

	const onNoteTypeEditorValueChange = (props, event) => {
		let _localRelatedNotes = [...localRelatedNotes];
		_localRelatedNotes[props.rowIndex].noteType = event.value;
	};

	const noteTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="noteType"
					options={noteTypeTerms.sort((a, b) => (a.name > b.name) ? 1 : -1)}
					editorChange={onNoteTypeEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"noteType"} />
			</>
		);
	};

	const onFreeTextEditorValueChange = (event, props) => {
		let _localRelatedNotes = [...localRelatedNotes];
		_localRelatedNotes[props.rowIndex].freeText = event.target.value;
	};

	const freeTextEditor = (props, fieldName, errorMessages) => {
		if (errorMessages) {
			errorMessages.severity = "error";
		}
		return (
			<>
				<InputTextAreaEditor
					initalValue={props.value}
					editorChange={(e) => onFreeTextEditorValueChange(e, props)}
					rows={5}
					columns={30}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={fieldName} />
			</>
		);
	};

	const evidenceTemplate = (rowData) => {
		if (rowData && rowData.references) {
			const refStrings = getRefStrings(rowData.references);
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
	
	const evidenceEditorTemplate = (props, errorMessages) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={evidenceSearch}
					initialValue={props.rowData.references}
					rowProps={props}
					fieldName='references'
					subField='curie'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onEvidenceValueChange}
				/>
				<DialogErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField={"references"}
				/>
			</>
		);
	};
	
	const onEvidenceValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "references", setFieldValue);
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
				<Button label="New Note" icon="pi pi-plus" onClick={createNewNoteHandler}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const handleDeleteRelatedNote = (event, props) => {
		let _localRelatedNotes = global.structuredClone(localRelatedNotes); 
		if(props.dataKey){
			_localRelatedNotes.splice(props.dataKey, 1);
		}else {
			_localRelatedNotes.splice(props.rowIndex, 1);
		}
		setLocalRelatedNotes(_localRelatedNotes);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteRelatedNote(event, props) }}/>
		);
	}

	let headerGroup = 	<ColumnGroup>
						<Row>
							<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
							<Column header="Note Type" />
							<Column header="Text" />
							<Column header="Evidence" />
							<Column header="Internal" />
						</Row>
						</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-8' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Related Notes</h3>
				<DataTable value={localRelatedNotes} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={noteTypeEditor} field="noteType.name" header="Note Type" headerClassName='surface-0' body={noteTypeTemplate}/>
					<Column
						editor={(props) => freeTextEditor(props, "freeText", errorMessages)}
						field="freeText"
						header="Text"
						body={textTemplate}
						headerClassName='surface-0'
						className='wrap-word max-w-35rem'
					/>
					<Column editor={(props) => evidenceEditorTemplate(props, errorMessages)} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)} className='wrap-word max-w-25rem'/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
				</DataTable>
			</Dialog>
		</div>
	);
};

