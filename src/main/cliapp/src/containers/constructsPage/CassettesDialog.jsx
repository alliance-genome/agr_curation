import React from 'react';
import { Dialog } from 'primereact/dialog';
import { CassettesTable } from '../cassettesPage/CassettesTable';
import { constructCassettesFilter } from './utils';

// The Cassettes data table, restricted to the cassettes of one construct.
export const CassettesDialog = ({ originalCassettesData, setOriginalCassettesData }) => {
	const { originalCassettes, dialog, isInEdit } = originalCassettesData;
	const fixedFilters = constructCassettesFilter(originalCassettes);

	const hideDialog = () => {
		setOriginalCassettesData((originalCassettesData) => ({
			...originalCassettesData,
			dialog: false,
		}));
	};

	return (
		<Dialog visible={dialog && !isInEdit} className="w-11" modal onHide={hideDialog} closable={true}>
			{fixedFilters && (
				<CassettesTable tableName="Construct Cassettes" fixedFilters={fixedFilters} isEditable={false} />
			)}
		</Dialog>
	);
};
