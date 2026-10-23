import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { AutocompleteSingleTableEditor } from '../../../components/Editors/autocomplete/base/AutocompleteSingleTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { Endpoints } from '../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../constants/FilterFields';
import { transgenicToolDisplayValue } from '../../cassettesPage/utils';

const toolSearchConfig = {
	endpoint: Endpoints.Entity.TRANSGENIC_TOOL,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.transgenicToolAutocompleteConfig),
	filterName: 'transgenicToolAssociationObjectFilter',
	valueDisplay: (item) => <div>{transgenicToolDisplayValue(item)}</div>,
};

/**
 * Editable table of the associations a transgenic tool is the subject of, every row always in edit mode. Its
 * editors read and write rows through the table strategy its parent provides.
 *
 * @param {object[]} associations - rows, each carrying a `dataKey`
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @param {object} tableRef - ref to the DataTable
 * @param {Function} deletionHandler - `(event, dataKey)`
 * @param {object[]} [relationTerms] - the transgenic_tool_association_relation term set, undefined until it loads
 * @returns {JSX.Element}
 */
export const TransgenicToolAssociationsFormTable = ({
	associations,
	editingRows,
	tableRef,
	deletionHandler,
	relationTerms,
}) => (
	<DataTable
		value={associations}
		dataKey="dataKey"
		showGridlines
		editMode="row"
		size="small"
		editingRows={editingRows}
		resizableColumns
		columnResizeMode="expand"
		cellMemo={false}
		onRowEditChange={() => null}
		ref={tableRef}
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
			editor={(editorOptions) => (
				<ControlledVocabularyTableEditor
					editorOptions={editorOptions}
					field="relation"
					options={relationTerms}
					dataKey="id"
					showClear={false}
				/>
			)}
			field="relation"
			header="Relation"
			headerClassName="surface-0"
		/>
		<Column
			editor={(editorOptions) => (
				<AutocompleteSingleTableEditor
					editorOptions={editorOptions}
					field="transgenicToolTransgenicToolAssociationObject"
					subField="primaryExternalId"
					toDisplay={(tool) => (tool?.id ? transgenicToolDisplayValue(tool) : tool?.primaryExternalId ?? '')}
					{...toolSearchConfig}
				/>
			)}
			field="transgenicToolTransgenicToolAssociationObject"
			header="Transgenic Tool"
			headerClassName="surface-0"
		/>
		<Column
			editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />}
			field="internal"
			header="Internal"
			headerClassName="surface-0"
		/>
		<Column
			editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="obsolete" />}
			field="obsolete"
			header="Obsolete"
			headerClassName="surface-0"
		/>
		<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
		<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
		<Column field="createdBy.uniqueId" header="Created By" headerClassName="surface-0" />
		<Column field="dateCreated" header="Date Created" headerClassName="surface-0" />
	</DataTable>
);
