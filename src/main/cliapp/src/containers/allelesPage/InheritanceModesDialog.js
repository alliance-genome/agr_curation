import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DialogErrorMessageComponent } from '../../components/DialogErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { SearchService } from '../../service/SearchService';
import { ValidationService } from '../../service/ValidationService';
import { autocompleteSearch, buildAutocompleteFilter, defaultAutocompleteOnChange } from "../../utils/utils";
import { evidenceTemplate, evidenceEditorTemplate } from '../../components/EvidenceComponent';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { InputTextAreaEditor } from '../../components/InputTextAreaEditor';

export const InheritanceModesDialog = ({
													originalInheritanceModesData,
													setOriginalInheritanceModesData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalInheritanceModes, isInEdit, dialog, rowIndex, mainRowProps } = originalInheritanceModesData;
	const [localInheritanceModes, setLocalInheritanceModes] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const validationService = new ValidationService();
	const searchService = new SearchService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const inheritanceModeTerms = useControlledVocabularyService('Allele inheritance mode vocabulary');

	const showDialogHandler = () => {
		let _localInheritanceModes = cloneInheritanceModes(originalInheritanceModes);
		setLocalInheritanceModes(_localInheritanceModes);

		if(isInEdit){
			let rowsObject = {};
			if(_localInheritanceModes) {
				_localInheritanceModes.forEach((im) => {
					rowsObject[`${im.dataKey}`] = true;
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
		let _localInheritanceModes = [...localInheritanceModes];//add new note support
		if(originalInheritanceModes && originalInheritanceModes[event.index]){
			let dataKey = _localInheritanceModes[event.index].dataKey;
			_localInheritanceModes[event.index] = global.structuredClone(originalInheritanceModes[event.index]);
			_localInheritanceModes[event.index].dataKey = dataKey;
			setLocalInheritanceModes(_localInheritanceModes);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInInheritanceModes(event.data,event.index);
	};

	const compareChangesInInheritanceModes = (data,index) => {
		if(originalInheritanceModes && originalInheritanceModes[index]) {
			if (data.internal !== originalInheritanceModes[index].internal) {
				rowsEdited.current++;
			}
			if (data.inheritanceMode.name !== originalInheritanceModes[index].inheritanceMode.name) {
				rowsEdited.current++;
			}
			if ((originalInheritanceModes[index].phenotypeTerm && !data.phenotypeTerm) ||
				(!originalInheritanceModes[index].phenotypeTerm && data.phenotypeTerm) ||
				(originalInheritanceModes[index].phenotypeTerm && data.phenotypeTerm &&
					originalInheritanceModes[index].phenotypeTerm.curie !== data.phenotypeTerm.curie)) {
				rowsEdited.current++;
			} 
			if ((originalInheritanceModes[index].phenotypeStatement && !data.phenotypeStatement) ||
				(!originalInheritanceModes[index].phenotypeStatement && data.phenotypeStatement) ||
				(originalInheritanceModes[index].phenotypeStatement && data.phenotypeStatement &&
					originalInheritanceModes[index].phenotypeStatement !== data.phenotypeStatement)) {
				rowsEdited.current++;
			} 
			if ((originalInheritanceModes[index].evidence && !data.evidence) ||
				(!originalInheritanceModes[index].evidence && data.evidence) ||
				(data.evidence && (data.evidence.length !== originalInheritanceModes[index].evidence.length))) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var j = 0; j < data.evidence.length; j++) {
						if (data.evidence[j].curie !== originalInheritanceModes[index].evidence[j].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
		}

		if((localInheritanceModes.length > originalInheritanceModes?.length) || !originalInheritanceModes){
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateInheritanceMode(localInheritanceModes[event.index]);
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
						detail: 'Could not update AlleleInheritanceMode [' + localInheritanceModes[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInInheritanceModes(event.data,event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localInheritanceModes = [...localInheritanceModes];
		_localInheritanceModes[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalInheritanceModes(_localInheritanceModes);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalInheritanceModesData((originalInheritanceModesData) => {
			return {
				...originalInheritanceModesData,
				dialog: false,
			};
		});
		let _localInheritanceModes = [];
		setLocalInheritanceModes(_localInheritanceModes);
	};

	const validateInheritanceMode = async (im) => {
		let _im = global.structuredClone(im);
		delete _im.dataKey;
		const result = await validationService.validate('alleleinheritancemodeslotannotation', _im);
		return result;
	};

	const cloneInheritanceModes = (clonableInheritanceModes) => {
		let _clonableInheritanceModes = global.structuredClone(clonableInheritanceModes);
		if(_clonableInheritanceModes) {
			let counter = 0 ;
			_clonableInheritanceModes.forEach((im) => {
				im.dataKey = counter++;
			});
		} else {
			_clonableInheritanceModes = [];
		};
		return _clonableInheritanceModes;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const im of localInheritanceModes) {
			delete im.dataKey;
		}
		mainRowProps.rowData.alleleInheritanceModes = localInheritanceModes;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleInheritanceModes = localInheritanceModes;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleInheritanceModes"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalInheritanceModesData((originalInheritanceModesData) => {
				return {
					...originalInheritanceModesData,
					dialog: false,
				}
			}
		);
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

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
	
	const onInternalEditorValueChange = (props, event) => {
		let _localInheritanceModes = [...localInheritanceModes];
		_localInheritanceModes[props.rowIndex].internal = event.value.name;
	}

	const onInheritanceModeEditorValueChange = (props, event) => {
		let _localInheritanceModes = [...localInheritanceModes];
		_localInheritanceModes[props.rowIndex].inheritanceMode = event.value;
	};

	const inheritanceModeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="inheritanceMode"
					options={inheritanceModeTerms}
					editorChange={onInheritanceModeEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"inheritanceMode"} />
			</>
		);
	};

	const inheritanceModeTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.inheritanceMode?.name}</EllipsisTableCell>;
	};
	
	const phenotypeTermTemplate = (rowData) => {
		if (rowData?.phenotypeTerm) {
			return <div className='overflow-hidden text-overflow-ellipsis'
				dangerouslySetInnerHTML={{
					__html: rowData.phenotypeTerm.name + ' (' + rowData.phenotypeTerm.curie + ')'
				}}
			/>;
		}
	};

	const phenotypeTermEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={phenotypeTermSearch}
					initialValue={props.rowData.phenotypeTerm?.curie}
					rowProps={props}
					fieldName='phenotypeTerm'
					onValueChangeHandler={onPhenotypeTermValueChange}
				/>
				<DialogErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField={"phenotypeTerm"}
				/>
			</>
		);
	};

	const onPhenotypeTermValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "phenotypeTerm", setFieldValue);
	};

	const phenotypeTermSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["name", "curie"];
		const endpoint = "phenotypeterm";
		const filterName = "phenotypeTermFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const phenotypeStatementTemplate = (rowData) => {
		if (rowData?.phenotypeStatement) {
			return <EllipsisTableCell>{rowData.phenotypeStatement}</EllipsisTableCell>;
		}
	};

	const onPhenotypeStatementEditorValueChange = (event, props) => {
		let _localInheritanceModes = [...localInheritanceModes];
		_localInheritanceModes[props.rowIndex].phenotypeStatement = event.target.value;
	};

	const phenotypeStatementEditor = (props, errorMessages) => {
		if (errorMessages) {
			errorMessages.severity = "error";
		}
		return (
			<>
				<InputTextAreaEditor
					initalValue={props.value}
					editorChange={(e) => onPhenotypeStatementEditorValueChange(e, props)}
					rows={1}
					columns={30}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"phenotypeStatement"} />
			</>
		);
	};

	const footerTemplate = () => {
		if (!isInEdit) {
			return null;
		};
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Inheritance Mode" icon="pi pi-plus" onClick={createNewInheritanceModeHandler}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const createNewInheritanceModeHandler = (event) => {
		let cnt = localInheritanceModes ? localInheritanceModes.length : 0;
		const _localInheritanceModes = global.structuredClone(localInheritanceModes);
		_localInheritanceModes.push({
			dataKey : cnt,
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalInheritanceModes(_localInheritanceModes);
	};

	const handleDeleteInheritanceMode = (event, props) => {
		let _localInheritanceModes = global.structuredClone(localInheritanceModes);
		if(props.dataKey){
			_localInheritanceModes.splice(props.dataKey, 1);
		}else {
			_localInheritanceModes.splice(props.rowIndex, 1);
		}
		setLocalInheritanceModes(_localInheritanceModes);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteInheritanceMode(event, props) }}/>
		);
	}

	let headerGroup = 	<ColumnGroup>
							<Row>
								<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
								<Column header="Inheritance Mode" />
								<Column header="Phenotype Term" />
								<Column header="Phenotype Statement" />
								<Column header="Internal" />
								<Column header="Evidence" />
							</Row>
						</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-6' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Inheritance Modes</h3>
				<DataTable value={localInheritanceModes} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup} 
						editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
							bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={(props) => inheritanceModeEditor(props)} field="inheritanceMode.name" header="Inheritance Mode" headerClassName='surface-0' body={inheritanceModeTemplate}/>
					<Column editor={phenotypeTermEditorTemplate} field="phenotypeTerm.curie" header="Phenotype Term" headerClassName='surface-0' body={phenotypeTermTemplate}/>
					<Column
						editor={(props) => phenotypeStatementEditor(props, errorMessages)}
						field="phenotypeStatement"
						header="Phenotype Statement"
						body={phenotypeStatementTemplate}
						headerClassName='surface-0'
					/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column editor={(props) =>evidenceEditorTemplate(props, errorMessages)} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
				</DataTable>
			</Dialog>
		</div>
	);
};
