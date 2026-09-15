import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Dropdown } from 'primereact/dropdown';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { TableInputTextEditor } from '../../../components/Editors/text/TableInputTextEditor';
import { InternalEditor } from '../../../components/Editors/legacyForm/InternalEditor';
import { ObsoleteEditor } from '../../../components/Editors/legacyForm/ObsoleteEditor';
import { AutocompleteEditor } from '../../../components/Editors/autocomplete/base/AutocompleteEditor';
import {
	resourceDescriptorSearch,
	resourceDescriptorSearchConfig,
} from '../../../components/Editors/autocomplete/resourceDescriptor/utils';
import { DialogErrorMessageComponent } from '../../../components/Error/DialogErrorMessageComponent';
import { findRow } from './utils';

/**
 * The editable cross references table, rendered by both the Alleles table dialog and the allele
 * detail page section. They differ only in their change handler and whether Obsolete is offered.
 *
 * @param {Object} props
 * @param {Array<Object>} props.crossReferences rows, each carrying a `dataKey`
 * @param {Object} props.editingRows PrimeReact editing state, keyed by `dataKey`
 * @param {Function} props.onRowEditChange
 * @param {Object} props.tableRef
 * @param {Object} props.errorMessages per row messages, keyed by `dataKey` then field
 * @param {Function} props.deletionHandler `(event, dataKey)`
 * @param {Function} props.onFieldChange `(dataKey, field, value)`
 * @param {boolean} [props.showObsolete] the ticket makes Obsolete editable only, so create passes false
 */
export const CrossReferencesTable = ({
	crossReferences,
	editingRows,
	onRowEditChange,
	tableRef,
	errorMessages,
	deletionHandler,
	onFieldChange,
	showObsolete = false,
}) => {
	// PrimeReact snapshots an editor's rowData when the row opens and never refreshes it while the row
	// stays open, so every editor reads the live row out of the owning state instead. Only the dataKey
	// is taken from the snapshot: it identifies the row and never changes.
	const resolveRow = (editorOptions) =>
		findRow(crossReferences, editorOptions?.rowData?.dataKey) ?? editorOptions?.rowData ?? {};

	const textChangeHandler = (row, field) => (rowIndex, event) => onFieldChange(row.dataKey, field, event.target.value);

	const descriptorChangeHandler = (row) => (event, setFieldValue) => {
		const selected = event.target.value;

		if (!selected) {
			setFieldValue('');
			onFieldChange(row.dataKey, 'resourceDescriptor', null);
			return;
		}

		// A descriptor typed rather than chosen has no resourcePages, so the merge rule clears the page.
		const descriptor = typeof selected === 'object' ? selected : { prefix: selected };
		setFieldValue(descriptor.prefix);
		onFieldChange(row.dataKey, 'resourceDescriptor', descriptor);
	};

	const booleanChangeHandler = (field) => (editorOptions, event) =>
		onFieldChange(editorOptions?.rowData?.dataKey, field, event.target.value?.name);

	return (
		<DataTable
			value={crossReferences}
			dataKey="dataKey"
			showGridlines
			editMode="row"
			size="small"
			editingRows={editingRows}
			onRowEditChange={onRowEditChange}
			ref={tableRef}
			cellMemo={false}
			resizableColumns
			columnResizeMode="expand"
		>
			<Column
				editor={(props) => <DeleteAction deletionHandler={deletionHandler} id={props?.rowData?.dataKey} />}
				header="Actions"
				className="max-w-4rem"
				bodyClassName="text-center"
				headerClassName="surface-0"
				frozen
			/>
			<Column
				editor={(props) => {
					const row = resolveRow(props);
					return (
						<TableInputTextEditor
							value={row.displayName}
							rowIndex={props.rowIndex}
							errorMessages={errorMessages}
							dataKey={row.dataKey}
							textOnChangeHandler={textChangeHandler(row, 'displayName')}
							field="displayName"
						/>
					);
				}}
				field="displayName"
				header="Display Name"
				headerClassName="surface-0"
			/>
			<Column
				editor={(props) => {
					const row = resolveRow(props);
					return (
						<>
							<AutocompleteEditor
								search={resourceDescriptorSearch}
								initialValue={row.resourceDescriptor?.prefix}
								editorOptions={props}
								fieldName="resourceDescriptor"
								subField="prefix"
								valueDisplay={resourceDescriptorSearchConfig.valueDisplay}
								onValueChangeHandler={descriptorChangeHandler(row)}
							/>
							<DialogErrorMessageComponent errorMessages={errorMessages[row.dataKey]} errorField="resourceDescriptor" />
						</>
					);
				}}
				field="resourceDescriptor.prefix"
				header="Resource Descriptor"
				headerClassName="surface-0"
			/>
			<Column
				editor={(props) => {
					const row = resolveRow(props);
					return (
						<TableInputTextEditor
							value={row.referencedCurie}
							rowIndex={props.rowIndex}
							errorMessages={errorMessages}
							dataKey={row.dataKey}
							textOnChangeHandler={textChangeHandler(row, 'referencedCurie')}
							field="referencedCurie"
						/>
					);
				}}
				field="referencedCurie"
				header="Referenced Curie"
				headerClassName="surface-0"
			/>
			<Column
				editor={(props) => {
					const row = resolveRow(props);
					const pages = row.resourceDescriptor?.resourcePages ?? [];
					return (
						<>
							<Dropdown
								aria-label="resourceDescriptorPage"
								value={row.resourceDescriptorPage}
								options={pages}
								optionLabel="name"
								// Matches the stored page against the same page inside the descriptor by id. They are
								// separate objects, so without this an existing page renders as nothing selected.
								dataKey="id"
								filter
								showClear
								placeholder={row.resourceDescriptor ? 'Select a page' : 'Select a resource descriptor first'}
								onChange={(event) => onFieldChange(row.dataKey, 'resourceDescriptorPage', event.value)}
								style={{ width: '100%' }}
							/>
							<DialogErrorMessageComponent
								errorMessages={errorMessages[row.dataKey]}
								errorField="resourceDescriptorPage"
							/>
						</>
					);
				}}
				field="resourceDescriptorPage.name"
				header="Resource Descriptor Page"
				headerClassName="surface-0"
			/>
			<Column
				editor={(props) => {
					const row = resolveRow(props);
					return (
						<InternalEditor
							editorOptions={props}
							rowIndex={props.rowIndex}
							errorMessages={errorMessages}
							dataKey={row.dataKey}
							internalOnChangeHandler={booleanChangeHandler('internal')}
						/>
					);
				}}
				field="internal"
				header="Internal"
				headerClassName="surface-0"
			/>
			{showObsolete && (
				<Column
					editor={(props) => {
						const row = resolveRow(props);
						return (
							<ObsoleteEditor
								editorOptions={props}
								rowIndex={props.rowIndex}
								errorMessages={errorMessages}
								dataKey={row.dataKey}
								obsoleteOnChangeHandler={booleanChangeHandler('obsolete')}
							/>
						);
					}}
					field="obsolete"
					header="Obsolete"
					headerClassName="surface-0"
				/>
			)}
		</DataTable>
	);
};
