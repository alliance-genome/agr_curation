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
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { SearchService } from '../../service/SearchService';
import { ValidationService } from '../../service/ValidationService';
import { autocompleteSearch, buildAutocompleteFilter, defaultAutocompleteOnChange, multipleAutocompleteOnChange } from "../../utils/utils";
import { evidenceTemplate, evidenceEditorTemplate } from '../../components/EvidenceComponent';
import { AutocompleteMultiEditor } from "../../components/Autocomplete/AutocompleteMultiEditor";
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { InputTextAreaEditor } from '../../components/InputTextAreaEditor';
import { VocabTermAutocompleteTemplate } from '../../components/Autocomplete/VocabTermAutocompleteTemplate';

export const FunctionalImpactsDialog = ({
													originalFunctionalImpactsData,
													setOriginalFunctionalImpactsData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalFunctionalImpacts, isInEdit, dialog, rowIndex, mainRowProps } = originalFunctionalImpactsData;
	const [localFunctionalImpacts, setLocalFunctionalImpacts] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const validationService = new ValidationService();
	const searchService = new SearchService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localFunctionalImpacts = cloneFunctionalImpacts(originalFunctionalImpacts);
		setLocalFunctionalImpacts(_localFunctionalImpacts);

		if(isInEdit){
			let rowsObject = {};
			if(_localFunctionalImpacts) {
				_localFunctionalImpacts.forEach((im) => {
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
		let _localFunctionalImpacts = [...localFunctionalImpacts];//add new note support
		if(originalFunctionalImpacts && originalFunctionalImpacts[event.index]){
			let dataKey = _localFunctionalImpacts[event.index].dataKey;
			_localFunctionalImpacts[event.index] = global.structuredClone(originalFunctionalImpacts[event.index]);
			_localFunctionalImpacts[event.index].dataKey = dataKey;
			setLocalFunctionalImpacts(_localFunctionalImpacts);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInFunctionalImpacts(event.data,event.index);
	};

	const compareChangesInFunctionalImpacts = (data,index) => {
		if(originalFunctionalImpacts && originalFunctionalImpacts[index]) {
			if (data.internal !== originalFunctionalImpacts[index].internal) {
				rowsEdited.current++;
			}
			if (!data.functionalImpacts.length !== originalFunctionalImpacts[index].functionalImpacts.length) {
				rowsEdited.current++;
			} else {
				for (var i = 0; i < data.functionalImpacts.length; i++) {
					if (data.functionalImpacts[i].name !== originalFunctionalImpacts[index].functionalImpacts[i].name) {
						rowsEdited.current++;
					}
				}
			}
			if ((originalFunctionalImpacts[index].phenotypeTerm && !data.phenotypeTerm) ||
				(!originalFunctionalImpacts[index].phenotypeTerm && data.phenotypeTerm) ||
				(originalFunctionalImpacts[index].phenotypeTerm && data.phenotypeTerm &&
					originalFunctionalImpacts[index].phenotypeTerm.curie !== data.phenotypeTerm.curie)) {
				rowsEdited.current++;
			} 
			if ((originalFunctionalImpacts[index].phenotypeStatement && !data.phenotypeStatement) ||
				(!originalFunctionalImpacts[index].phenotypeStatement && data.phenotypeStatement) ||
				(originalFunctionalImpacts[index].phenotypeStatement && data.phenotypeStatement &&
					originalFunctionalImpacts[index].phenotypeStatement !== data.phenotypeStatement)) {
				rowsEdited.current++;
			} 
			if ((originalFunctionalImpacts[index].evidence && !data.evidence) ||
				(!originalFunctionalImpacts[index].evidence && data.evidence) ||
				(data.evidence && (data.evidence.length !== originalFunctionalImpacts[index].evidence.length))) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var j = 0; j < data.evidence.length; j++) {
						if (data.evidence[j].curie !== originalFunctionalImpacts[index].evidence[j].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
		}

		if((localFunctionalImpacts.length > originalFunctionalImpacts?.length) || !originalFunctionalImpacts){
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateFunctionalImpact(localFunctionalImpacts[event.index]);
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
						detail: 'Could not update AlleleFunctionalImpact [' + localFunctionalImpacts[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInFunctionalImpacts(event.data,event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localFunctionalImpacts = [...localFunctionalImpacts];
		_localFunctionalImpacts[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalFunctionalImpacts(_localFunctionalImpacts);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalFunctionalImpactsData((originalFunctionalImpactsData) => {
			return {
				...originalFunctionalImpactsData,
				dialog: false,
			};
		});
		let _localFunctionalImpacts = [];
		setLocalFunctionalImpacts(_localFunctionalImpacts);
	};

	const validateFunctionalImpact = async (im) => {
		let _im = global.structuredClone(im);
		delete _im.dataKey;
		const result = await validationService.validate('alleleinheritancemodeslotannotation', _im);
		return result;
	};

	const cloneFunctionalImpacts = (clonableFunctionalImpacts) => {
		let _clonableFunctionalImpacts = global.structuredClone(clonableFunctionalImpacts);
		if(_clonableFunctionalImpacts) {
			let counter = 0 ;
			_clonableFunctionalImpacts.forEach((im) => {
				im.dataKey = counter++;
			});
		} else {
			_clonableFunctionalImpacts = [];
		};
		return _clonableFunctionalImpacts;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const im of localFunctionalImpacts) {
			delete im.dataKey;
		}
		mainRowProps.rowData.alleleFunctionalImpacts = localFunctionalImpacts;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleFunctionalImpacts = localFunctionalImpacts;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleFunctionalImpacts"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalFunctionalImpactsData((originalFunctionalImpactsData) => {
				return {
					...originalFunctionalImpactsData,
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
		let _localFunctionalImpacts = [...localFunctionalImpacts];
		_localFunctionalImpacts[props.rowIndex].internal = event.value.name;
	}

	const onFunctionalImpactsValueChange = (props, setFieldValue, event) => {
		multipleAutocompleteOnChange(props, event, "functionalImpacts", setFieldValue);
	};

	const functionalImpactsEditor = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={functionalImpactSearch}
					initialValue={props.rowData.functionalImpacts}
					rowProps={props}
					fieldName='functionalImpacts'
					subField='name'
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
							<VocabTermAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
					onValueChangeHandler={onFunctionalImpactsValueChange}
				/>
				<DialogErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField={"functionalImpacts"}
				/>
			</>
		);
	};

	const functionalImpactSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["name"];
		const endpoint = "vocabularyterm";
		const filterName = "functionalImpactFilter";
		const otherFilters = {
			vocabularyFilter: {
				"vocabulary.name": {
					queryString: "Allele Functional Impact"
				}
			}
		}
		setQuery(event.query);
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const functionalImpactsTemplate = (rowData) => {
		if (rowData && rowData.functionalImpacts) {
			const sortedFunctionalImpacts = rowData.functionalImpacts.sort((a, b) => (a.name > b.name) ? 1 : -1);
			const listTemplate = (item) => item.name;
			return <ListTableCell template={listTemplate} listData={sortedFunctionalImpacts}/>
		}
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
		let _localFunctionalImpacts = [...localFunctionalImpacts];
		_localFunctionalImpacts[props.rowIndex].phenotypeStatement = event.target.value;
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
				<Button label="New Functional Impact" icon="pi pi-plus" onClick={createNewFunctionalImpactHandler}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const createNewFunctionalImpactHandler = (event) => {
		let cnt = localFunctionalImpacts ? localFunctionalImpacts.length : 0;
		const _localFunctionalImpacts = global.structuredClone(localFunctionalImpacts);
		_localFunctionalImpacts.push({
			dataKey : cnt,
			internal : false,
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalFunctionalImpacts(_localFunctionalImpacts);
	};

	const handleDeleteFunctionalImpact = (event, props) => {
		let _localFunctionalImpacts = global.structuredClone(localFunctionalImpacts);
		if(props.dataKey){
			_localFunctionalImpacts.splice(props.dataKey, 1);
		}else {
			_localFunctionalImpacts.splice(props.rowIndex, 1);
		}
		setLocalFunctionalImpacts(_localFunctionalImpacts);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteFunctionalImpact(event, props) }}/>
		);
	}

	let headerGroup = 	<ColumnGroup>
							<Row>
								<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
								<Column header="Functional Impacts" />
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
				<h3>Functional Impacts</h3>
				<DataTable value={localFunctionalImpacts} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup} 
						editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
							bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={(props) => functionalImpactsEditor(props)} field="functionalImpacts.name" header="Functional Impacts" headerClassName='surface-0' body={functionalImpactsTemplate}/>
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
