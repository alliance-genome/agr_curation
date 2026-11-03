import React from 'react';
import { Dialog } from 'primereact/dialog';
import { TransgenicToolUsesTable } from './TransgenicToolUsesTable';

// The uses of a transgenic tool, one row per use slot annotation: its terms, evidence and audit fields.
export const TransgenicToolUsesDialog = ({ usesData, setUsesData }) => {
	const { originalUses, dialog } = usesData;

	const hideDialog = () => setUsesData((usesData) => ({ ...usesData, dialog: false }));

	return (
		<Dialog visible={dialog} className="w-8" modal onHide={hideDialog} closable={true}>
			<h3>Transgenic Tool Uses</h3>
			<TransgenicToolUsesTable uses={originalUses} />
		</Dialog>
	);
};
