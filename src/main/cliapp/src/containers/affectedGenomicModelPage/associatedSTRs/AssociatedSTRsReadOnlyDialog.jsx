import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { IdTemplate } from '../../../components/Templates/IdTemplate';
import { getIdentifier } from '../../../utils/utils';

/**
 * Read-only popup listing the STRs (sequence targeting reagents) associated with an AGM - each
 * row's primary external ID comes from the STR object, not the association itself.
 */
export const AssociatedSTRsReadOnlyDialog = ({ originalAssociatedSTRsData, setOriginalAssociatedSTRsData }) => {
	const { originalAssociatedSTRs, dialog } = originalAssociatedSTRsData;
	const [localAssociatedSTRs, setLocalAssociatedSTRs] = useState(null);

	const showDialogHandler = () => {
		let _localAssociatedSTRs = [];
		if (originalAssociatedSTRs?.length > 0) {
			_localAssociatedSTRs = originalAssociatedSTRs.map(
				(association) => association.agmSequenceTargetingReagentAssociationObject
			);
		}
		setLocalAssociatedSTRs(_localAssociatedSTRs);
	};

	const hideDialog = () => {
		setOriginalAssociatedSTRsData((originalAssociatedSTRsData) => {
			return {
				...originalAssociatedSTRsData,
				dialog: false,
			};
		});
		setLocalAssociatedSTRs([]);
	};

	return (
		<Dialog visible={!!dialog} className="w-6" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Associated STRs</h3>
			<DataTable value={localAssociatedSTRs} dataKey="id" showGridlines>
				<Column field="name" header="Name" />
				<Column
					field="primaryExternalId"
					header="Primary External ID"
					body={(rowData) => <IdTemplate id={getIdentifier(rowData)} />}
				/>
			</DataTable>
		</Dialog>
	);
};
