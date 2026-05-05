import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { TableInputTextEditor } from '../../../components/Editors/text/TableInputTextEditor';
import { InternalEditor } from '../../../components/Editors/legacyForm/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/legacyForm/EvidenceEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/legacyForm/ControlledVocabularyEditor';
import { VocabularyTermSetEditor } from '../../../components/Editors/legacyForm/VocabularyTermSetEditor';

export const SymbolFormTable = ({
	name,
	editingRows,
	onRowEditChange,
	tableRef,
	errorMessages,
	textOnChangeHandler,
	synonymScopeOnChangeHandler,
	nameTypeOnChangeHandler,
	internalOnChangeHandler,
	evidenceOnChangeHandler,
}) => {
	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Display Text" />
				<Column header="Format Text" />
				<Column header="Synonym Scope" />
				<Column header="Name Type" />
				<Column header="Synonym URL" />
				<Column header="Internal" />
				<Column header="Evidence" />
				<Column header="Updated By" />
				<Column header="Date Updated" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={name}
			dataKey="dataKey"
			showGridlines
			editMode="row"
			headerColumnGroup={headerGroup}
			size="small"
			editingRows={editingRows}
			resizableColumns
			columnResizeMode="expand"
			cellMemo={false}
			onRowEditChange={onRowEditChange}
			ref={tableRef}
		>
			<Column
				editor={(props) => {
					return (
						<TableInputTextEditor
							value={props.value}
							rowIndex={props.rowIndex}
							errorMessages={errorMessages}
							dataKey={props?.rowData?.dataKey}
							textOnChangeHandler={textOnChangeHandler}
							field="displayText"
						/>
					);
				}}
				field="displayText"
				header="Display Text"
				headerClassName="surface-0"
			/>
			<Column
				editor={(props) => {
					return (
						<TableInputTextEditor
							value={props.value}
							rowIndex={props.rowIndex}
							errorMessages={errorMessages}
							dataKey={props?.rowData?.dataKey}
							textOnChangeHandler={textOnChangeHandler}
							field="formatText"
						/>
					);
				}}
				field="formatText"
				header="Format Text"
				headerClassName="surface-0"
			/>
			<Column
				editor={(props) => {
					return (
						<ControlledVocabularyEditor
							editorOptions={props}
							onChangeHandler={synonymScopeOnChangeHandler}
							errorMessages={errorMessages}
							rowIndex={props.rowIndex}
							vocabType="synonym_scope"
							dataKey={props?.rowData?.dataKey}
							field="synonymScope"
							showClear={true}
						/>
					);
				}}
				field="synonymScope.name"
				header="Synonym Scope"
				headerClassName="surface-0"
			/>
			<Column
				editor={(props) => {
					return (
						<VocabularyTermSetEditor
							editorOptions={props}
							onChangeHandler={nameTypeOnChangeHandler}
							errorMessages={errorMessages}
							rowIndex={props.rowIndex}
							vocabType="symbol_name_type"
							field="nameType"
							showClear={false}
						/>
					);
				}}
				field="nameType.name"
				header="Name Type"
				headerClassName="surface-0"
			/>
			<Column
				editor={(props) => {
					return (
						<TableInputTextEditor
							value={props.value}
							rowIndex={props.rowIndex}
							errorMessages={errorMessages}
							dataKey={props?.rowData?.dataKey}
							textOnChangeHandler={textOnChangeHandler}
							field="synonymUrl"
						/>
					);
				}}
				field="synonymUrl"
				header="Synonym URL"
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
					return <EvidenceEditor props={props} errorMessages={errorMessages} onChange={evidenceOnChangeHandler} />;
				}}
				field="evidence.curie"
				header="Evidence"
				headerClassName="surface-0"
			/>
			<Column field="updatedBy.uniqueId" header="Updated By" />
			<Column field="dateUpdated" header="Date Updated" />
		</DataTable>
	);
};
