import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { ValidationService } from '../../../service/ValidationService';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/legacyForm/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/legacyForm/EvidenceEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/legacyForm/ControlledVocabularyEditor';
import { PhenotypeTermEditor } from '../../../components/Editors/legacyForm/PhenotypeTermEditor';
import { TableInputTextAreaEditor } from '../../../components/Editors/text/TableInputTextAreaEditor';
import { Endpoints } from '../../../constants/Endpoints';
import { defaultAutocompleteOnChange } from '../../../utils/utils';

export const InheritanceModesEditDialog = ({
	originalInheritanceModesData,
	setOriginalInheritanceModesData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalInheritanceModes, isInEdit, dialog, rowIndex, mainRowProps } = originalInheritanceModesData;
	const [localInheritanceModes, setLocalInheritanceModes] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localInheritanceModes = cloneInheritanceModes(originalInheritanceModes);
		setLocalInheritanceModes(_localInheritanceModes);

		let rowsObject = {};
		if (_localInheritanceModes) {
			_localInheritanceModes.forEach((im) => {
				rowsObject[`${im.dataKey}`] = true;
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
		setOriginalInheritanceModesData((originalInheritanceModesData) => {
			return {
				...originalInheritanceModesData,
				dialog: false,
			};
		});
		setLocalInheritanceModes([]);
	};

	const cloneInheritanceModes = (clonableInheritanceModes) => {
		let _clonableInheritanceModes = structuredClone(clonableInheritanceModes);
		if (_clonableInheritanceModes) {
			_clonableInheritanceModes.forEach((im) => {
				im.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableInheritanceModes = [];
		}
		return _clonableInheritanceModes;
	};

	const inheritanceModeOnChangeHandler = (props, event) => {
		props.editorCallback(event.value);
		setLocalInheritanceModes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], inheritanceMode: event.value };
			return updated;
		});
	};

	const onPhenotypeTermValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'phenotypeTerm', setFieldValue);
		setLocalInheritanceModes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], phenotypeTerm: event.value };
			return updated;
		});
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		setLocalInheritanceModes((prev) => {
			const updated = [...prev];
			updated[rowIndex] = { ...updated[rowIndex], [field]: event.target.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalInheritanceModes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalInheritanceModes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const createNewInheritanceModeHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localInheritanceModes = structuredClone(localInheritanceModes);
		_localInheritanceModes.push({
			dataKey: newKey,
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalInheritanceModes(_localInheritanceModes);
	};

	const handleDeleteInheritanceMode = (e, dataKey) => {
		let _localInheritanceModes = structuredClone(localInheritanceModes);
		_localInheritanceModes = _localInheritanceModes.filter((im) => im.dataKey !== dataKey);
		setLocalInheritanceModes(_localInheritanceModes);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localInheritanceModes = structuredClone(localInheritanceModes);
		for (const im of _localInheritanceModes) {
			delete im.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localInheritanceModes);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleInheritanceModes'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalInheritanceModesData((originalInheritanceModesData) => {
			return {
				...originalInheritanceModesData,
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
		if (_obj.evidence) {
			_obj.evidence.forEach((ref) => {
				delete ref.updatedBy;
				delete ref.createdBy;
				delete ref.dateUpdated;
				delete ref.dateCreated;
			});
		}
		return _obj;
	};

	const validateAndSave = async () => {
		setIsValidating(true);
		try {
			let hasErrors = false;
			const newErrorMessages = {};

			for (let i = 0; i < localInheritanceModes.length; i++) {
				const _im = cleanForValidation(localInheritanceModes[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.ALLELE_INHERITANCE_MODE, _im);
				const dataKey = localInheritanceModes[i].dataKey;
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
				<Button label="New Inheritance Mode" icon="pi pi-plus" onClick={createNewInheritanceModeHandler} />
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
				<Column header="Inheritance Mode" />
				<Column header="Phenotype Term" />
				<Column header="Phenotype Statement" />
				<Column header="Internal" />
				<Column header="Evidence" />
			</Row>
		</ColumnGroup>
	);

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog
				visible={dialog && isInEdit}
				className="w-6"
				modal
				onHide={hideDialog}
				closable={false}
				onShow={showDialogHandler}
				footer={footerTemplate}
			>
				<h3>Inheritance Modes</h3>
				<DataTable
					value={localInheritanceModes}
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
						editor={(props) => (
							<DeleteAction deletionHandler={handleDeleteInheritanceMode} id={props?.rowData?.dataKey} />
						)}
						className="max-w-4rem"
						bodyClassName="text-center"
						headerClassName="surface-0"
						frozen
					/>
					<Column
						editor={(props) => {
							return (
								<ControlledVocabularyEditor
									editorOptions={props}
									onChangeHandler={inheritanceModeOnChangeHandler}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									rowIndex={props.rowIndex}
									vocabType="allele_inheritance_mode"
									field="inheritanceMode"
									showClear={false}
								/>
							);
						}}
						field="inheritanceMode"
						header="Inheritance Mode"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<PhenotypeTermEditor
									props={props}
									errorMessages={errorMessages}
									onChange={onPhenotypeTermValueChange}
									dataKey={props?.rowData?.dataKey}
								/>
							);
						}}
						field="phenotypeTerm"
						header="Phenotype Term"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<TableInputTextAreaEditor
									value={props.value}
									rowIndex={props.rowIndex}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									textOnChangeHandler={textOnChangeHandler}
									field="phenotypeStatement"
									rows={1}
									columns={30}
								/>
							);
						}}
						field="phenotypeStatement"
						header="Phenotype Statement"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<InternalEditor
									editorOptions={props}
									rowIndex={props.rowIndex}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									internalOnChangeHandler={internalOnChangeHandler}
								/>
							);
						}}
						field="internal"
						header="Internal"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<EvidenceEditor
									props={props}
									errorMessages={errorMessages}
									onChange={evidenceOnChangeHandler}
									dataKey={props?.rowData?.dataKey}
								/>
							);
						}}
						field="evidence"
						header="Evidence"
						headerClassName="surface-0"
					/>
				</DataTable>
			</Dialog>
		</div>
	);
};
