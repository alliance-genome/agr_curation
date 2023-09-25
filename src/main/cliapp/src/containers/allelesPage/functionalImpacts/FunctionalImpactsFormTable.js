import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { FunctionalImpactsEditor } from '../../../components/Editors/FunctionalImpactsEditor';
import { PhenotypeTermEditor } from '../../../components/Editors/PhenotypeTermEditor';
import { TableInputTextEditor } from '../../../components/Editors/TableInputTextEditor';

export const FunctionalImpactsFormTable = ({
  functionalImpacts,
  editingRows,
  onRowEditChange,
  tableRef,
  deletionHandler,
  errorMessages,
  functionalImpactsOnChangeHandler,
  phenotypeTermOnChangeHandler,
  phenotypeStatementOnChangeHandler,
  internalOnChangeHandler,
  evidenceOnChangeHandler,
}) => {

  let headerGroup = <ColumnGroup>
    <Row>
      <Column header="Actions" />
      <Column header="Functional Impacts" />
      <Column header="Phenotype Term" />
      <Column header="Phenotype Statement" />
      <Column header="Internal" />
      <Column header="Evidence" />
    </Row>
  </ColumnGroup>;


  return (
    <DataTable value={functionalImpacts} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
      editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}>
      <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
        className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
      <Column
        editor={(props) => {
          return <FunctionalImpactsEditor
            props={props}
            errorMessages={errorMessages}
            onChange={functionalImpactsOnChangeHandler}
          />;
        }}
        field="functionalImpacts" header="Functional Impacts" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <PhenotypeTermEditor
            props={props}
            errorMessages={errorMessages}
            onChange={phenotypeTermOnChangeHandler}
          />;
        }}
        field="phenotypeTerm" header="Phenotype Term" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <TableInputTextEditor
            value={props.value}
            rowIndex={props.rowIndex}
            errorMessages={errorMessages}
            textOnChangeHandler={phenotypeStatementOnChangeHandler}
            field="phenotypeStatement"
          />;
        }}
        field="phenotypeStatement" header="Phenotype Statement" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <InternalEditor
            props={props}
            rowIndex={props.rowIndex}
            errorMessages={errorMessages}
            internalOnChangeHandler={internalOnChangeHandler}
          />;
        }}
        field="internal" header="Internal" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <EvidenceEditor
            props={props}
            errorMessages={errorMessages}
            onChange={evidenceOnChangeHandler}
          />;
        }}
        field="evidence.curie" header="Evidence" headerClassName='surface-0' />
    </DataTable>
  );
};

