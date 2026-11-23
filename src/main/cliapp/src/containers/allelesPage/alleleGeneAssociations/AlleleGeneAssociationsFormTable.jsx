import { useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { RelatedNotesDialogEditOnly } from '../../../components/RelatedNotesDialogEditOnly';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { AlleleGeneObjectTableEditor } from '../../../components/Editors/autocomplete/gene/AlleleGeneObjectTableEditor';
import { SingleEvidenceCodeTableEditor } from '../../../components/Editors/autocomplete/ontology/SingleEvidenceCodeTableEditor';
import { DialogTriggerTableEditor } from '../../../components/Editors/dialog/DialogTriggerTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { RelationshipFilterTemplate } from './RelationshipFilterTemplate';

/**
 * Editable gene associations table, every row always in edit mode, paged and filterable. Its
 * editors read and write rows through the table strategy its parent provides; a row's note is
 * edited in a dialog.
 *
 * @param {object[]} alleleGeneAssociations - rows, each carrying a `dataKey`
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @param {Function} onRowEditChange - PrimeReact row-edit handler
 * @param {object} tableRef - ref to the DataTable
 * @param {Function} deletionHandler - `(event, dataKey)`
 * @param {object} errorMessages - row errors, keyed by `dataKey` then field
 * @param {Function} setErrorMessages - their setter
 * @param {(notes: object[], editorOptions: object) => void} relatedNoteOnChangeHandler - stores
 *   the note the dialog hands back
 * @param {object[]} [relationTerms] - the allele_gene_relation term set, undefined until it loads
 * @returns {JSX.Element}
 */
export const AlleleGeneAssociationsFormTable = ({
	alleleGeneAssociations,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	errorMessages,
	setErrorMessages,
	relatedNoteOnChangeHandler,
	relationTerms,
}) => {
	const [rows, setRows] = useState(5);
	const [first, setFirst] = useState(0);
	const [filters, setFilters] = useState({
		'relation.name': { value: null, matchMode: 'in' },
		'alleleGeneAssociationObject.primaryExternalId': { value: null, matchMode: 'contains' },
		'relatedNote.freeText': { value: null, matchMode: 'contains' },
		'evidenceCode.curie': { value: null, matchMode: 'contains' },
		evidenceCurieSearchFilter: { value: null, matchMode: 'contains' },
		'updatedBy.uniqueId': { value: null, matchMode: 'startsWith' },
		dateUpdated: { value: null, matchMode: 'startsWith' },
	});

	// The shape handleRelatedNoteOpenInEdit writes and RelatedNotesDialogEditOnly reads.
	const [relatedNotesData, setRelatedNotesData] = useState({
		dialogIsVisible: false,
		errorMessages,
	});
	const onPage = (options) => {
		setRows(options.rows);
		setFirst(options.first);
	};

	const onFilter = (options) => {
		setFilters(options.filters);
	};

	// PrimeReact keeps the row data it captured when the row opened, so the note is read from the
	// current rows.
	const currentRow = (editorOptions) =>
		alleleGeneAssociations.find((association) => association.dataKey === editorOptions.rowData?.dataKey) ??
		editorOptions.rowData;

	// DialogTriggerTableEditor only reports the open, so the data the note dialog reads is built here.
	const handleRelatedNoteOpenInEdit = (event, editorOptions) => {
		event.preventDefault();
		const relatedNote = currentRow(editorOptions)?.relatedNote;
		setRelatedNotesData({
			originalRelatedNotes: relatedNote ? [relatedNote] : undefined,
			dialogIsVisible: true,
			dataKey: editorOptions.rowData?.dataKey,
			errorMessages,
			rowProps: editorOptions,
		});
	};

	return (
		<>
			<DataTable
				value={alleleGeneAssociations}
				dataKey="dataKey"
				showGridlines
				editMode="row"
				removableSort
				filterDisplay="row"
				onPage={onPage}
				onFilter={onFilter}
				filters={filters}
				size="small"
				editingRows={editingRows}
				resizableColumns
				columnResizeMode="expand"
				onRowEditChange={onRowEditChange}
				cellMemo={false}
				ref={tableRef}
				paginator
				paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
				currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
				rows={rows}
				first={first}
				rowsPerPageOptions={[5, 10, 20, 50]}
			>
				<Column
					editor={(props) => <DeleteAction deletionHandler={deletionHandler} id={props.rowData?.dataKey} />}
					className="max-w-4rem"
					bodyClassName="text-center"
					headerClassName="surface-0"
					frozen
				/>
				<Column
					editor={(editorOptions) => (
						<ControlledVocabularyTableEditor
							editorOptions={editorOptions}
							field="relation"
							options={relationTerms}
							dataKey="id"
							showClear={false}
						/>
					)}
					field="relation"
					header="Relationship"
					headerClassName="surface-0"
					filter
					sortable
					filterField="relation.name"
					sortField="relation.name"
					showFilterMenu={false}
					filterElement={(props) => {
						return <RelationshipFilterTemplate options={props} alleleGeneAssociations={alleleGeneAssociations} />;
					}}
				/>
				<Column
					editor={(editorOptions) => <AlleleGeneObjectTableEditor editorOptions={editorOptions} />}
					field="alleleGeneAssociationObject"
					header="Gene"
					headerClassName="surface-0"
					filter
					sortable
					filterField="alleleGeneAssociationObject.primaryExternalId"
					sortField="alleleGeneAssociationObject.primaryExternalId"
					showFilterMenu={false}
				/>
				<Column
					editor={(editorOptions) => (
						<DialogTriggerTableEditor
							editorOptions={editorOptions}
							onOpenInEdit={handleRelatedNoteOpenInEdit}
							errorField="relatedNote"
							displayText={currentRow(editorOptions)?.relatedNote?.freeText}
							addText="Add Note"
							tooltipObject="allele"
						/>
					)}
					field="relatedNote"
					header="Note"
					headerClassName="surface-0"
					filter
					filterField="relatedNote.freeText"
					showFilterMenu={false}
					sortable
					sortField="relatedNote.freeText"
				/>
				<Column
					editor={(editorOptions) => <SingleEvidenceCodeTableEditor editorOptions={editorOptions} />}
					filter
					sortable
					filterField="evidenceCode.curie"
					sortField="evidenceCode.curie"
					showFilterMenu={false}
					field="evidenceCode"
					header="Evidence Code"
					headerClassName="surface-0"
				/>
				<Column
					editor={(editorOptions) => <EvidenceTableEditor editorOptions={editorOptions} />}
					filter
					filterField="evidenceCurieSearchFilter"
					showFilterMenu={false}
					field="evidence"
					sortable
					sortField="evidenceCurieSearchFilter"
					header="Evidence"
					headerClassName="surface-0"
				/>
				<Column field="updatedBy.uniqueId" header="Updated By" />
				<Column field="dateUpdated" header="Date Updated" />
			</DataTable>
			<RelatedNotesDialogEditOnly
				relatedNotesData={relatedNotesData}
				setRelatedNotesData={setRelatedNotesData}
				tableErrorMessages={errorMessages}
				setTableErrorMessages={setErrorMessages}
				singleValue={true}
				onChange={relatedNoteOnChangeHandler}
				defaultValues={{ noteType: 'comment' }}
				errorField="relatedNote"
				noteTypeVocabType="allele_genomic_entity_association_note_type"
			/>
		</>
	);
};
