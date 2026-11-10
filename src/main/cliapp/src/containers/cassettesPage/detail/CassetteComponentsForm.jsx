import { Button } from 'primereact/button';
import { Message } from 'primereact/message';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { ComponentsTable } from '../components/ComponentsTable';
import { prepareComponentRows } from '../components/utils';

/**
 * A cassette detail page section for one of the cassette's component lists (SCRUM-6747). The rows are held by
 * useCassetteComponentList and saved by the page's Save through the list's own endpoint.
 *
 * @param {object} components - the useCassetteComponentList result
 * @param {object} config - the list, from componentConfigs
 * @param {boolean} [showPending] - whether to mark the list while it has unsaved edits; the create page, where
 *        everything is unsaved, does not
 * @returns {JSX.Element}
 */
export const CassetteComponentsForm = ({ components, config, showPending = true }) => {
	const { rows, setRows, errorMessages, setErrorMessages, isDirty, isLoading, loadError } = components;

	const createNewRowHandler = (event) => {
		event.preventDefault();
		setRows((previous) => [...prepareComponentRows([config.newRow()]), ...previous]);
	};

	return (
		<FormTableWrapper
			table={
				<ComponentsTable
					rows={rows}
					setRows={setRows}
					errorMessages={errorMessages}
					setErrorMessages={setErrorMessages}
					isLoading={isLoading}
					config={config}
				/>
			}
			tableName={config.title}
			showTable={rows.length > 0}
			isPending={showPending && isDirty}
			button={
				<div className="flex gap-2">
					<Button
						label="Add Row"
						onClick={createNewRowHandler}
						// Saving replaces the stored list, so rows can only be added once it has been read
						disabled={isLoading || Boolean(loadError)}
						className="p-button-text"
					/>
					{loadError && (
						<Message
							severity="error"
							text="Could not load these components, so they cannot be saved. Reload the page to try again."
						/>
					)}
				</div>
			}
		/>
	);
};
