import { useState } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { SubResourceSaveButton } from '../../../components/SubResourceSaveButton';
import { SubResourceLoadError } from '../../../components/SubResourceLoadError';
import { CassettesTable } from '../../cassettesPage/CassettesTable';
import { CassettesEditDialog } from '../CassettesEditDialog';
import { constructCassettesFilter } from '../utils';

const TYPE = 'constructCassetteAssociations';

/**
 * The construct detail page's cassettes section: the Cassettes data table, restricted to the construct's cassettes
 * and read only, with a pop-up that adds cassettes picked from the autosuggest and removes those held. Its rows are
 * saved by the page along with the construct, and by this section's own button, which saves them alone.
 */
export const CassettesForm = () => {
	const subResource = useSubResource(TYPE);
	const { rows: associations, setRows, errorMessages, isDirty, isLoading, loadError } = subResource;
	const [dialogData, setDialogData] = useState({ originalCassettes: [], isInEdit: true, dialog: false });
	const fixedFilters = constructCassettesFilter(associations);

	const openDialog = (event) => {
		event.preventDefault();
		setDialogData({ originalCassettes: associations, isInEdit: true, dialog: true });
	};

	return (
		<>
			<FormTableWrapper
				table={<CassettesTable tableName="Construct Cassettes" fixedFilters={fixedFilters} />}
				tableName="Cassettes"
				showTable={!!fixedFilters}
				isPending={isDirty}
				titleAction={<SubResourceSaveButton type={TYPE} />}
				button={
					<>
						<Button
							label="Edit Cassettes"
							onClick={openDialog}
							disabled={isLoading || Boolean(loadError)}
							className="w-4 p-button-text"
						/>
						<SubResourceLoadError type={TYPE} />
					</>
				}
			/>
			<CassettesEditDialog
				originalCassettesData={dialogData}
				setOriginalCassettesData={setDialogData}
				onKeepEdits={setRows}
				initialErrorMessages={errorMessages}
			/>
		</>
	);
};
