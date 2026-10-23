import { useMemo, useRef } from 'react';
import { Button } from 'primereact/button';
import { Message } from 'primereact/message';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';
import { addDataKey } from '../../allelesPage/utils';
import { TransgenicToolAssociationsFormTable } from './TransgenicToolAssociationsFormTable';

/**
 * The transgenic tool detail page's associations section: the associations the tool is the subject of. They
 * are held by useTransgenicToolAssociations and saved by the page's Save through their own endpoint.
 *
 * @param {object} associations - the useTransgenicToolAssociations result
 * @returns {JSX.Element}
 */
export const TransgenicToolAssociationsForm = ({ associations }) => {
	const tableRef = useRef(null);
	const relationTerms = useVocabularyTermSetService('transgenic_tool_association_relation');
	const { rows, setRows, errorMessages, setErrorMessages, isDirty, loadError } = associations;

	const editingRows = useMemo(() => Object.fromEntries(rows.map((row) => [row.dataKey, true])), [rows]);

	const strategy = useDataKeyRowsStrategy({
		rows,
		errors: errorMessages,
		write: ({ rowKey, field, value }) =>
			setRows((previous) => previous.map((row) => (row.dataKey === rowKey ? { ...row, [field]: value } : row))),
	});

	const createNewAssociationHandler = (event) => {
		event.preventDefault();
		// is_compatible_with is the only relation the term set holds, so a new row starts with it
		const newAssociation = {
			relation: relationTerms?.length === 1 ? relationTerms[0] : null,
			transgenicToolTransgenicToolAssociationObject: null,
			internal: false,
			obsolete: false,
		};
		addDataKey(newAssociation);
		setRows((previous) => [newAssociation, ...previous]);
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		setRows((previous) => previous.filter((row) => row.dataKey !== dataKey));
		const updatedErrorMessages = structuredClone(errorMessages);
		delete updatedErrorMessages[dataKey];
		setErrorMessages(updatedErrorMessages);
	};

	return (
		<FormTableWrapper
			table={
				<TableEditorProvider strategy={strategy}>
					<TransgenicToolAssociationsFormTable
						associations={rows}
						editingRows={editingRows}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						relationTerms={relationTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Transgenic Tool Associations"
			showTable={rows.length > 0}
			isPending={isDirty}
			button={
				<div className="flex gap-2">
					<Button
						label="Add Association"
						onClick={createNewAssociationHandler}
						disabled={Boolean(loadError)}
						className="p-button-text"
					/>
					{loadError && (
						<Message
							severity="error"
							text="Could not load these associations, so they cannot be saved. Reload the page to try again."
						/>
					)}
				</div>
			}
		/>
	);
};
