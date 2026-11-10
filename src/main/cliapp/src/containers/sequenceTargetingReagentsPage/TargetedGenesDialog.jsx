import React, { useEffect, useMemo, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../components/EvidenceComponent';
import { SearchService } from '../../service/SearchService';
import { getIdentifier } from '../../utils/utils';
import { targetedGenesFindParams } from './utils';

const ROWS = 10;

// The targeted genes of one STR, a page at a time: some STRs target several hundred genes. The total
// comes with the STR (targetedGeneCount); each page is fetched from the gene association find.
export const TargetedGenesDialog = ({ targetedGenesData, setTargetedGenesData }) => {
	const { sequenceTargetingReagent, dialog } = targetedGenesData;
	const searchService = useMemo(() => new SearchService(), []);
	const [first, setFirst] = useState(0);
	const [associations, setAssociations] = useState([]);
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		if (!dialog || !sequenceTargetingReagent?.id) return;
		let cancelled = false;
		setLoading(true);
		searchService
			.find('sqtrgeneassociation', ROWS, first / ROWS, targetedGenesFindParams(sequenceTargetingReagent.id))
			.then((data) => !cancelled && setAssociations(data?.results || []))
			.finally(() => !cancelled && setLoading(false));
		return () => {
			cancelled = true;
		};
	}, [dialog, sequenceTargetingReagent, first, searchService]);

	const hideDialog = () => {
		setTargetedGenesData((targetedGenesData) => ({ ...targetedGenesData, dialog: false }));
		setFirst(0);
		setAssociations([]);
	};

	const geneTemplate = (association) => {
		const gene = association.sequenceTargetingReagentGeneAssociationObject;
		const symbol = gene?.geneSymbol?.displayText;
		const text = symbol ? `${symbol} (${getIdentifier(gene)})` : getIdentifier(gene);
		return <div className="overflow-hidden text-overflow-ellipsis" dangerouslySetInnerHTML={{ __html: text }} />;
	};

	const booleanTemplate = (value) => <EllipsisTableCell>{JSON.stringify(value)}</EllipsisTableCell>;

	return (
		<Dialog visible={dialog} className="w-8" modal onHide={hideDialog} closable={true}>
			<h3>Targeted Genes</h3>
			<DataTable
				value={associations}
				dataKey="id"
				showGridlines
				lazy
				paginator
				rows={ROWS}
				first={first}
				totalRecords={sequenceTargetingReagent?.targetedGeneCount || 0}
				onPage={(event) => setFirst(event.first)}
				loading={loading}
			>
				<Column field="relation.name" header="Relation" headerClassName="surface-0" />
				<Column
					field="sequenceTargetingReagentGeneAssociationObject.geneSymbol.displayText"
					header="Gene"
					headerClassName="surface-0"
					body={geneTemplate}
				/>
				<Column
					field="evidence.curie"
					header="Evidence"
					headerClassName="surface-0"
					body={(rowData) => evidenceTemplate(rowData)}
				/>
				<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
				<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
				<Column field="createdBy.uniqueId" header="Created By" headerClassName="surface-0" />
				<Column field="dateCreated" header="Date Created" headerClassName="surface-0" />
				<Column
					field="internal"
					header="Internal"
					headerClassName="surface-0"
					body={(rowData) => booleanTemplate(rowData.internal)}
				/>
				<Column
					field="obsolete"
					header="Obsolete"
					headerClassName="surface-0"
					body={(rowData) => booleanTemplate(rowData.obsolete)}
				/>
			</DataTable>
		</Dialog>
	);
};
