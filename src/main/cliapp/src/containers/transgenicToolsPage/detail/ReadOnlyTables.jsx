import React, { useEffect, useMemo, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../../components/EvidenceComponent';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';
import { relationName, transgenicToolDisplayValue } from '../../cassettesPage/utils';
import { toolAssociationsFindParams } from '../utils';

// Read-only tables for the list fields of the transgenic tool detail page (SCRUM-6655).

// A tool has a dozen associations at most, so they are fetched in one page.
const ASSOCIATIONS_LIMIT = 100;

const booleanTemplate = (value) => <EllipsisTableCell>{JSON.stringify(value ?? false)}</EllipsisTableCell>;

const auditColumns = [
	<Column key="updatedBy" field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />,
	<Column key="dateUpdated" field="dateUpdated" header="Date Updated" headerClassName="surface-0" />,
	<Column key="createdBy" field="createdBy.uniqueId" header="Created By" headerClassName="surface-0" />,
	<Column key="dateCreated" field="dateCreated" header="Date Created" headerClassName="surface-0" />,
	<Column
		key="internal"
		field="internal"
		header="Internal"
		headerClassName="surface-0"
		body={(rowData) => booleanTemplate(rowData.internal)}
	/>,
	<Column
		key="obsolete"
		field="obsolete"
		header="Obsolete"
		headerClassName="surface-0"
		body={(rowData) => booleanTemplate(rowData.obsolete)}
	/>,
];

/**
 * The tool - tool associations the tool is the subject of. The tool's own JSON carries only a
 * summary of them (associatedTransgenicTools), so the associations are fetched, as the table's
 * pop-up does.
 */
export const TransgenicToolAssociationsTable = ({ transgenicToolId }) => {
	const searchService = useMemo(() => new SearchService(), []);
	const [associations, setAssociations] = useState([]);
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		if (!transgenicToolId) return;
		let cancelled = false;
		setLoading(true);
		searchService
			.find(
				Endpoints.Entity.TRANSGENIC_TOOL_TRANSGENIC_TOOL_ASSOCIATION,
				ASSOCIATIONS_LIMIT,
				0,
				toolAssociationsFindParams(transgenicToolId)
			)
			.then((data) => !cancelled && setAssociations(data?.results || []))
			.catch(() => !cancelled && setAssociations([]))
			.finally(() => !cancelled && setLoading(false));
		return () => {
			cancelled = true;
		};
	}, [transgenicToolId, searchService]);

	return (
		<DataTable value={associations} dataKey="id" showGridlines loading={loading}>
			<Column
				header="Relation"
				headerClassName="surface-0"
				body={(association) => <EllipsisTableCell>{relationName(association)}</EllipsisTableCell>}
			/>
			<Column
				header="Transgenic Tool"
				headerClassName="surface-0"
				body={(association) => (
					<EllipsisTableCell>
						{transgenicToolDisplayValue(association?.transgenicToolTransgenicToolAssociationObject)}
					</EllipsisTableCell>
				)}
			/>
			{auditColumns}
		</DataTable>
	);
};

export const CrossReferencesReadOnlyTable = ({ crossReferences }) => (
	<DataTable value={crossReferences || []} dataKey="id" showGridlines>
		<Column field="referencedCurie" header="Referenced Curie" headerClassName="surface-0" />
		<Column field="displayName" header="Display Name" headerClassName="surface-0" />
		<Column field="resourceDescriptorPage.name" header="Page" headerClassName="surface-0" />
		{auditColumns}
	</DataTable>
);

export const RelatedNotesReadOnlyTable = ({ relatedNotes }) => (
	<DataTable value={relatedNotes || []} dataKey="id" showGridlines>
		<Column field="noteType.name" header="Note Type" headerClassName="surface-0" />
		<Column
			field="freeText"
			header="Text"
			headerClassName="surface-0"
			body={(note) => <div dangerouslySetInnerHTML={{ __html: note.freeText }} />}
		/>
		<Column
			field="references.curie"
			header="References"
			headerClassName="surface-0"
			body={(note) => evidenceTemplate({ evidence: note.references })}
		/>
		{auditColumns}
	</DataTable>
);
