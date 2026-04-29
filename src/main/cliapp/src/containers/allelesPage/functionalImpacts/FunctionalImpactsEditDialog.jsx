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
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { FunctionalImpactsEditor } from '../../../components/Editors/FunctionalImpactsEditor';
import { PhenotypeTermEditor } from '../../../components/Editors/PhenotypeTermEditor';
import { TableInputTextAreaEditor } from '../../../components/Editors/text/TableInputTextAreaEditor';
import { Endpoints } from '../../../constants/Endpoints';
import { defaultAutocompleteOnChange, multipleAutocompleteOnChange } from '../../../utils/utils';

export const FunctionalImpactsEditDialog = ({
	originalFunctionalImpactsData,
	setOriginalFunctionalImpactsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalFunctionalImpacts, isInEdit, dialog, rowIndex, mainRowProps } = originalFunctionalImpactsData;
	const [localFunctionalImpacts, setLocalFunctionalImpacts] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localFunctionalImpacts = cloneFunctionalImpacts(originalFunctionalImpacts);
		setLocalFunctionalImpacts(_localFunctionalImpacts);

		let rowsObject = {};
		if (_localFunctionalImpacts) {
			_localFunctionalImpacts.forEach((fi) => {
				rowsObject[`${fi.dataKey}`] = true;
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
		setOriginalFunctionalImpactsData((originalFunctionalImpactsData) => {
			return {
				...originalFunctionalImpactsData,
				dialog: false,
			};
		});
		setLocalFunctionalImpacts([]);
	};

	const cloneFunctionalImpacts = (clonableFunctionalImpacts) => {
		let _clonableFunctionalImpacts = structuredClone(clonableFunctionalImpacts);
		if (_clonableFunctionalImpacts) {
			_clonableFunctionalImpacts.forEach((fi) => {
				fi.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableFunctionalImpacts = [];
		}
		return _clonableFunctionalImpacts;
	};

	const onFunctionalImpactsValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'functionalImpacts', setFieldValue);
		setLocalFunctionalImpacts((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], functionalImpacts: event.target.value };
			return updated;
		});
	};

	const onPhenotypeTermValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'phenotypeTerm', setFieldValue);
		setLocalFunctionalImpacts((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], phenotypeTerm: event.value };
			return updated;
		});
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		setLocalFunctionalImpacts((prev) => {
			const updated = [...prev];
			updated[rowIndex] = { ...updated[rowIndex], [field]: event.target.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalFunctionalImpacts((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalFunctionalImpacts((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const createNewFunctionalImpactHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localFunctionalImpacts = structuredClone(localFunctionalImpacts);
		_localFunctionalImpacts.push({
			dataKey: newKey,
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalFunctionalImpacts(_localFunctionalImpacts);
	};

	const handleDeleteFunctionalImpact = (e, dataKey) => {
		let _localFunctionalImpacts = structuredClone(localFunctionalImpacts);
		_localFunctionalImpacts = _localFunctionalImpacts.filter((fi) => fi.dataKey !== dataKey);
		setLocalFunctionalImpacts(_localFunctionalImpacts);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localFunctionalImpacts = structuredClone(localFunctionalImpacts);
		for (const fi of _localFunctionalImpacts) {
			delete fi.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localFunctionalImpacts);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleFunctionalImpacts'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalFunctionalImpactsData((originalFunctionalImpactsData) => {
			return {
				...originalFunctionalImpactsData,
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

			for (let i = 0; i < localFunctionalImpacts.length; i++) {
				const _fi = cleanForValidation(localFunctionalImpacts[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.ALLELE_FUNCTIONAL_IMPACT, _fi);
				const dataKey = localFunctionalImpacts[i].dataKey;
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
				<Button label="New Functional Impact" icon="pi pi-plus" onClick={createNewFunctionalImpactHandler} />
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
				<Column header="Functional Impacts" />
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
				<h3>Functional Impacts</h3>
				<DataTable
					value={localFunctionalImpacts}
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
							<DeleteAction deletionHandler={handleDeleteFunctionalImpact} id={props?.rowData?.dataKey} />
						)}
						className="max-w-4rem"
						bodyClassName="text-center"
						headerClassName="surface-0"
						frozen
					/>
					<Column
						editor={(props) => {
							return (
								<FunctionalImpactsEditor
									props={props}
									errorMessages={errorMessages}
									onChange={onFunctionalImpactsValueChange}
									dataKey={props?.rowData?.dataKey}
								/>
							);
						}}
						field="functionalImpacts"
						header="Functional Impacts"
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
