import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { TableEditorProvider } from '../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { ControlledVocabularyTableEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { ConditionsTableEditor } from '../../components/Editors/autocomplete/experimentalCondition/ConditionsTableEditor';
import { conditionsDetailedSearchConfig } from '../../components/Editors/autocomplete/experimentalCondition/utils';
import { KEEP_ROWS_IN_EDIT_MODE } from '../allelesPage/utils';

/**
 * The new disease annotation dialog's condition relations table, every row always in edit mode.
 * Rows live in the new annotation reducer and are addressed by their `dataKey`.
 *
 * @param {Function} dispatch - new annotation reducer dispatch
 * @param {object[]} conditionRelations - the annotation's condition relations, each carrying a
 *   `dataKey`
 * @param {boolean} showConditionRelations - whether the table is shown
 * @param {object} errorMessages - row errors, keyed by `dataKey` then field
 * @param {boolean} buttonIsDisabled - whether adding a condition relation is disabled
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @returns {JSX.Element}
 */
export const ConditionRelationsForm = ({
	dispatch,
	conditionRelations,
	showConditionRelations,
	errorMessages,
	buttonIsDisabled,
	editingRows,
}) => {
	const conditionRelationTypeTerms = useControlledVocabularyService('condition_relation');

	const strategy = useDataKeyRowsStrategy({
		rows: conditionRelations,
		errors: errorMessages,
		write: ({ rowKey, field, value }) =>
			dispatch({ type: 'EDIT_ROW', tableType: 'conditionRelations', dataKey: rowKey, field, value }),
	});

	const createNewRelationHandler = (event) => {
		event.preventDefault();
		dispatch({ type: 'ADD_NEW_RELATION' });
	};

	const handleDeleteRelation = (event, editorOptions) => {
		event.preventDefault();
		dispatch({
			type: 'DELETE_ROW',
			tableType: 'conditionRelations',
			showType: 'showConditionRelations',
			dataKey: editorOptions.rowData?.dataKey,
		});
	};

	const deleteAction = (editorOptions) => {
		return (
			<Button
				icon="pi pi-trash"
				className="p-button-text"
				onClick={(event) => {
					handleDeleteRelation(event, editorOptions);
				}}
			/>
		);
	};

	return (
		<div>
			{showConditionRelations && (
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={conditionRelations}
						dataKey="dataKey"
						showGridlines
						editMode="row"
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						resizableColumns
						columnResizeMode="expand"
						cellMemo={false}
					>
						<Column
							editor={(editorOptions) => deleteAction(editorOptions)}
							body={(rowData) => deleteAction({ rowData })}
							style={{ maxWidth: '4rem' }}
							frozen
							headerClassName="surface-0"
							bodyStyle={{ textAlign: 'center' }}
						/>
						<Column
							editor={(editorOptions) => (
								<ControlledVocabularyTableEditor
									editorOptions={editorOptions}
									field="conditionRelationType"
									options={conditionRelationTypeTerms}
									dataKey="id"
									showClear={false}
								/>
							)}
							field="conditionRelationType.name"
							header="Relation"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => (
								<ConditionsTableEditor editorOptions={editorOptions} searchConfig={conditionsDetailedSearchConfig} />
							)}
							field="conditions.condSummary"
							header="Conditions"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />}
							field="internal"
							header="Internal"
							headerClassName="surface-0"
						/>
					</DataTable>
				</TableEditorProvider>
			)}
			<div className={`${showConditionRelations ? 'pt-3' : ''} p-field p-col`}>
				<Button
					label="Add Experimental Condition"
					onClick={createNewRelationHandler}
					style={{ width: '50%' }}
					disabled={buttonIsDisabled}
				/>
			</div>
		</div>
	);
};
