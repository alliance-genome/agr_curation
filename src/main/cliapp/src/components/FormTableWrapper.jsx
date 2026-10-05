import { Message } from 'primereact/message';
import { RequiredFieldMarker } from './RequiredFieldMarker';

/**
 * A detail page table section: its title, buttons and table.
 *
 * @param {Object} props
 * @param {boolean} [props.isPending] - highlights the section and says it has pending edits
 */
export const FormTableWrapper = ({
	table,
	tableName,
	showTable,
	button,
	includeField = false,
	required = false,
	isPending = false,
}) => {
	return (
		<div className={isPending ? 'grid bg-yellow-50 border-left-3 border-yellow-500' : 'grid'}>
			<div className="col-12">
				<div className="mb-3 grid">
					{/* The grid's gutter, so the title lines up with the table's left edge below it. */}
					<div className="px-2">
						<h2>
							{required && <RequiredFieldMarker />}
							{tableName}
						</h2>
						{isPending && <Message severity="warn" text="Pending Edits!" />}
					</div>
					<div className={`${showTable ? 'pt-3' : ''} p-field p-col ${includeField ? 'col-12' : 'col-4'} col-4`}>
						{button}
					</div>
				</div>
				{showTable && table}
			</div>
		</div>
	);
};
