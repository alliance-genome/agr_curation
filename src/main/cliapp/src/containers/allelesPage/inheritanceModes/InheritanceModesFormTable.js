import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { PhenotypeTermEditor } from '../../../components/Editors/PhenotypeTermEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/ControlledVocabularyEditor';
import { TableInputTextAreaEditor } from '../../../components/Editors/TableInputTextAreaEditor';

export const InheritanceModesFormTable = ({
	inheritanceModes,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	errorMessages,
	internalOnChangeHandler,
	inheritanceModeOnChangeHandler,
	phenotypeTermOnChangeHandler,
	textOnChangeHandler,
	evidenceOnChangeHandler,
}) => {
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
		<DataTable
			value={inheritanceModes}
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
						<ControlledVocabularyEditor
							props={props}
							onChangeHandler={inheritanceModeOnChangeHandler}
							errorMessages={errorMessages}
							rowIndex={props.rowIndex}
							dataKey={props?.rowData?.dataKey}
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
							dataKey={props?.rowData?.dataKey}
							onChange={phenotypeTermOnChangeHandler}
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
				className="w-4"
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
