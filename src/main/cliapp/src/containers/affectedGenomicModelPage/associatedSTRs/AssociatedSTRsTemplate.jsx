import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { IdTemplate } from '../../../components/Templates/IdTemplate';
import { getIdentifier } from '../../../utils/utils';

/**
 * Read-only table of the STRs associated with an AGM. A stand-in for the eventual full
 * association-details table; for now it mirrors the table cell's popup dialog.
 */
export const AssociatedSTRsTemplate = ({ list }) => {
	if (!list || list.length === 0) return null;

	const strs = list
		.map((association) => association?.agmSequenceTargetingReagentAssociationObject)
		.filter(Boolean)
		.sort((a, b) => (a.name || '').localeCompare(b.name || '') || getIdentifier(a).localeCompare(getIdentifier(b)));

	return (
		<DataTable value={strs} dataKey="id" showGridlines>
			<Column field="name" header="Name" />
			<Column
				field="primaryExternalId"
				header="Primary External ID"
				body={(rowData) => <IdTemplate id={getIdentifier(rowData)} />}
			/>
		</DataTable>
	);
};
