import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Dropdown } from 'primereact/dropdown';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { TableField } from '../../../components/Editors/fields/TableField';
import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { TextInput } from '../../../components/Editors/widgets/TextInput';
import { SingleAutocomplete } from '../../../components/Editors/widgets/SingleAutocomplete';
import { InputTextTableEditor } from '../../../components/Editors/text/InputTextTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import {
	resourceDescriptorSearch,
	resourceDescriptorSearchConfig,
} from '../../../components/Editors/autocomplete/resourceDescriptor/utils';
import { RequiredFieldMarker } from '../../../components/RequiredFieldMarker';
import { curiePrefixOf } from './utils';
import { useResourceDescriptorsByPrefix } from './useResourceDescriptorsByPrefix';

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
 * @param {boolean} [props.showObsolete] offers the Obsolete column; the create page leaves it out
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
	// Editors read and write rows by dataKey through the owning state, never through the row data PrimeReact
	// captured when the row opened.
	const strategy = useDataKeyRowsStrategy({
		rows: crossReferences,
		errors: errorMessages,
		write: ({ rowKey, field, value }) => onFieldChange(rowKey, field, value),
	});

	const resourceDescriptorsByPrefix = useResourceDescriptorsByPrefix();

	// Leaving the curie cell sets the descriptor its prefix names, replacing one chosen before, since the
	// two must match to save. It waits for the curator to leave the cell because a half-edited prefix can
	// name another descriptor, and choosing it would replace the page through the merge rule.
	const fillDescriptorFromCurie = (row) => () => {
		const descriptor = resourceDescriptorsByPrefix.get(curiePrefixOf(row.referencedCurie));
		if (descriptor && descriptor.id !== row.resourceDescriptor?.id) {
			onFieldChange(row.dataKey, 'resourceDescriptor', descriptor);
		}
	};

	// The autocomplete reports every keystroke as free text, not only a selection. Half-typed text names no
	// descriptor, and storing it would clear the page through the merge rule - which the curator could not
	// then undo, because the page it cleared is the one they were keeping. So only a picked descriptor, or
	// an emptied input, is stored.
	const storeDescriptor = (onChange) => (descriptor) => {
		if (descriptor && descriptor.id == null) return;
		onChange(descriptor);
	};

	return (
		<TableEditorProvider strategy={strategy}>
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
					editor={(editorOptions) => (
						<DeleteAction deletionHandler={deletionHandler} id={editorOptions?.rowData?.dataKey} />
					)}
					header="Actions"
					className="max-w-4rem"
					bodyClassName="text-center"
					headerClassName="surface-0"
					frozen
				/>
				<Column
					editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="displayName" />}
					field="displayName"
					header={
						<>
							<RequiredFieldMarker />
							Display Name
						</>
					}
					headerClassName="surface-0"
				/>
				<Column
					editor={(editorOptions) => (
						<TableField editorOptions={editorOptions} field="referencedCurie">
							{(binding) => (
								<div onBlur={fillDescriptorFromCurie(binding.row)}>
									<TextInput {...binding} />
								</div>
							)}
						</TableField>
					)}
					field="referencedCurie"
					header={
						<>
							<RequiredFieldMarker />
							Referenced Curie
						</>
					}
					headerClassName="surface-0"
				/>
				<Column
					editor={(editorOptions) => (
						<TableField editorOptions={editorOptions} field="resourceDescriptor">
							{(binding) => (
								<SingleAutocomplete
									{...binding}
									onChange={storeDescriptor(binding.onChange)}
									search={resourceDescriptorSearch}
									subField="prefix"
									valueDisplay={resourceDescriptorSearchConfig.valueDisplay}
								/>
							)}
						</TableField>
					)}
					field="resourceDescriptor.prefix"
					header={
						<>
							<RequiredFieldMarker />
							Resource Descriptor
						</>
					}
					headerClassName="surface-0"
				/>
				<Column
					editor={(editorOptions) => (
						<TableField editorOptions={editorOptions} field="resourceDescriptorPage">
							{({ value, row, onChange, invalid, name }) => {
								// The row's own page is always offered, even when the descriptor cannot list it: a page
								// chosen from this dropdown carries no descriptor of its own, and a descriptor that is
								// absent or failed to load offers nothing. Without it the dropdown finds no match for a
								// page that is set and renders its placeholder, telling the curator there is none.
								const pages = row?.resourceDescriptor?.resourcePages ?? [];
								const options = value && !pages.some((page) => page.id === value.id) ? [value, ...pages] : pages;
								return (
									<Dropdown
										aria-label={name}
										value={value}
										options={options}
										optionLabel="name"
										// Matches the stored page against the same page inside the descriptor by id. They are
										// separate objects, so without this an existing page renders as nothing selected.
										dataKey="id"
										filter
										showClear
										placeholder={row?.resourceDescriptor ? 'Select a page' : 'Select a resource descriptor first'}
										onChange={(event) => onChange(event.value ?? null)}
										className={invalid ? 'p-invalid' : undefined}
										style={{ width: '100%' }}
									/>
								);
							}}
						</TableField>
					)}
					field="resourceDescriptorPage.name"
					header={
						<>
							<RequiredFieldMarker />
							Resource Descriptor Page
						</>
					}
					headerClassName="surface-0"
				/>
				<Column
					editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />}
					field="internal"
					header="Internal"
					headerClassName="surface-0"
				/>
				{showObsolete && (
					<Column
						editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="obsolete" />}
						field="obsolete"
						header="Obsolete"
						headerClassName="surface-0"
					/>
				)}
			</DataTable>
		</TableEditorProvider>
	);
};
