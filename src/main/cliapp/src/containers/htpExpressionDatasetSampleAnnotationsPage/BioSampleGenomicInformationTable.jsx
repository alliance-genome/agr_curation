import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';

/**
 * Read-only, always-expanded table for a sample's BioSampleGenomicInformation record - same
 * columns as BioSampleGenomicInformationReadOnlyDialog, but shown inline on the detail page
 * rather than behind a pop-up.
 */
export const BioSampleGenomicInformationTable = ({ entities }) => {
	return (
		<DataTable value={entities || []} dataKey="id" showGridlines emptyMessage="No data available">
			<Column
				field="bioSampleAgm"
				header="BioSample AGM"
				body={(rowData) => <GenomicEntityTemplate genomicEntity={rowData.bioSampleAgm} />}
			/>
			<Column
				field="bioSampleAgmType.name"
				header="BioSample AGM Type"
				body={(rowData) => <StringTemplate string={rowData.bioSampleAgmType?.name} />}
			/>
			<Column
				field="bioSampleAllele"
				header="BioSample Allele"
				body={(rowData) => <GenomicEntityTemplate genomicEntity={rowData.bioSampleAllele} detailPage="allele" />}
			/>
			<Column
				field="bioSampleText"
				header="BioSample Text"
				body={(rowData) => <StringTemplate string={rowData.bioSampleText} />}
			/>
			<Column field="updatedBy.uniqueId" header="Updated By" />
			<Column field="dateUpdated" header="Date Updated" />
			<Column field="createdBy.uniqueId" header="Created By" />
			<Column field="dateCreated" header="Date Created" />
			<Column field="internal" header="Internal" body={(rowData) => <BooleanTemplate value={rowData.internal} />} />
			<Column field="obsolete" header="Obsolete" body={(rowData) => <BooleanTemplate value={rowData.obsolete} />} />
		</DataTable>
	);
};
