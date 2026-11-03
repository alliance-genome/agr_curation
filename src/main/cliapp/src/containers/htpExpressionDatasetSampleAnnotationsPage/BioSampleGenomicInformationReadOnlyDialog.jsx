import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';

/**
 * Read-only popup for a sample's BioSampleGenomicInformation record.
 */
export const BioSampleGenomicInformationReadOnlyDialog = ({ originalEntitiesData, setOriginalEntitiesData }) => {
	const { originalEntities, dialog } = originalEntitiesData;
	const [localEntities, setLocalEntities] = useState(null);

	const showDialogHandler = () => {
		setLocalEntities(originalEntities?.length > 0 ? originalEntities : []);
	};

	const hideDialog = () => {
		setOriginalEntitiesData((originalEntitiesData) => {
			return {
				...originalEntitiesData,
				dialog: false,
			};
		});
		setLocalEntities([]);
	};

	return (
		<Dialog visible={!!dialog} className="w-11" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Sample Genomic Information</h3>
			<DataTable value={localEntities} dataKey="id" showGridlines>
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
		</Dialog>
	);
};
