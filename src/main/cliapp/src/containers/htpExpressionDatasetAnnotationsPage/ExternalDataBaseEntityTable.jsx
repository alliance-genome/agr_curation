import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { CrossReferenceTemplate } from '../../components/Templates/CrossReferenceTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';

/**
 * Read-only, always-expanded table for one or more ExternalDataBaseEntity records (the dataset's
 * own identity, or its subseries) - same columns as ExternalDataBaseEntityReadOnlyDialog, but
 * shown inline on the detail page rather than behind a pop-up.
 */
export const ExternalDataBaseEntityTable = ({ entities }) => {
	return (
		<DataTable value={entities || []} dataKey="id" showGridlines emptyMessage="No data available">
			<Column field="curie" header="Curie" />
			<Column
				field="preferredCrossReference"
				header="Preferred Cross Reference"
				body={(rowData) => <CrossReferenceTemplate xref={rowData.preferredCrossReference} />}
			/>
			<Column
				field="crossReferences"
				header="Cross References"
				body={(rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />}
			/>
			<Column
				field="secondaryIdentifiers"
				header="Secondary Identifiers"
				body={(rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />}
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
