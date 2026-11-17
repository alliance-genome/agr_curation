import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';

/**
 * Read-only, always-expanded table for a sample's BioSampleAge record - same columns as
 * BioSampleAgeReadOnlyDialog, but shown inline on the detail page rather than behind a pop-up.
 */
export const BioSampleAgeTable = ({ entities }) => {
	return (
		<DataTable value={entities || []} dataKey="id" showGridlines emptyMessage="No data available">
			<Column field="age" header="Age" body={(rowData) => <StringTemplate string={rowData.age} />} />
			<Column
				field="stage.developmentalStageStart.name"
				header="Stage"
				body={(rowData) => <OntologyTermTemplate term={rowData.stage?.developmentalStageStart} />}
			/>
			<Column
				field="whenExpressedStageName"
				header="When Expressed Stage Name"
				body={(rowData) => <StringTemplate string={rowData.whenExpressedStageName} />}
			/>
			<Column field="updatedBy.uniqueId" header="Updated By" />
			<Column field="dateUpdated" header="Date Updated" />
			<Column field="createdBy.uniqueId" header="Created By" />
			<Column field="dateCreated" header="Date Created" />
			<Column field="internal" header="Internal" body={(rowData) => <BooleanTemplate value={rowData.internal} />} />
			<Column field="obsolete" header="Obsolete" body={(rowData) => <BooleanTemplate value={rowData.obsolete} />} />
		</DataTable>
	);
};
