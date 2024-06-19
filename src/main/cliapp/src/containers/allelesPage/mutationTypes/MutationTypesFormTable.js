import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { MutationTypesEditor } from '../../../components/Editors/MutationTypesEditor';

export const MutationTypesFormTable = ({
	mutationTypes,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	errorMessages,
	internalOnChangeHandler,
	mutationTypesOnChangeHandler,
	evidenceOnChangeHandler,
}) => {
	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Mutation Types" />
				<Column header="Internal" />
				<Column header="Evidence" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={mutationTypes}
			dataKey="dataKey"
			showGridlines
			editMode="row"
			headerColumnGroup={headerGroup}
			size="small"
			editingRows={editingRows}
			resizableColumns
			columnResizeMode="expand"
			onRowEditChange={onRowEditChange}
			ref={tableRef}
		>
			<Column
				editor={(props) => <DeleteAction deletionHandler={deletionHandler} id={props?.rowData?.dataKey} />}
				className="max-w-4rem"
				bodyClassName="text-center"
				headerClassName="surface-0"
				frozen
			/>
			<Column
				editor={(props) => {
					return (
						<MutationTypesEditor
							props={props}
							errorMessages={errorMessages}
							dataKey={props?.rowData?.dataKey}
							onChange={mutationTypesOnChangeHandler}
						/>
					);
				}}
				field="mutationTypes"
				header="Mutation Types"
				headerClassName="surface-0"
			/>
			<Column
				editor={(props) => {
					return (
						<InternalEditor
							props={props}
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
		</DataTable>
	);
};
