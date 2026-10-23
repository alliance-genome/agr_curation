import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const getSequenceOfReferenceAccessionNumber = (hgvs) => {
	if (!hgvs || !hgvs.includes(':')) return null;
	return `RefSeq:${hgvs.split(':')[0]}`;
};

const soTermText = (term) => {
	if (!term) return null;
	if (term.name && term.curie) return `${term.name} (${term.curie})`;
	return term.name || term.curie;
};

const textTemplate = (value) => <EllipsisTableCell>{value}</EllipsisTableCell>;

export const GenomicLocationsReadOnlyDialog = ({ genomicLocationsData, setGenomicLocationsData }) => {
	const { genomicLocations, dialog } = genomicLocationsData;

	const hideDialog = () => {
		setGenomicLocationsData((prev) => ({
			...prev,
			dialog: false,
		}));
	};

	return (
		<Dialog visible={dialog} className="w-11" modal onHide={hideDialog} closable header="Variant Genomic Locations">
			<DataTable value={genomicLocations} dataKey="id" showGridlines scrollable>
				<Column field="relation.name" header="Relation" body={(rowData) => textTemplate(rowData.relation?.name)} />
				<Column
					field="variantGenomicLocationAssociationObject.name"
					header="Chromosome"
					body={(rowData) => textTemplate(rowData.variantGenomicLocationAssociationObject?.name)}
				/>
				<Column
					field="variantGenomicLocationAssociationObject.genomeAssembly.primaryExternalId"
					header="Assembly"
					body={(rowData) =>
						textTemplate(rowData.variantGenomicLocationAssociationObject?.genomeAssembly?.primaryExternalId)
					}
				/>
				<Column
					field="variationStrand"
					header="Variation Strand"
					body={(rowData) => textTemplate(rowData.variationStrand)}
				/>
				<Column field="start" header="Start" body={(rowData) => textTemplate(rowData.start)} />
				<Column field="end" header="End" body={(rowData) => textTemplate(rowData.end)} />
				<Column field="hgvs" header="HGVS Name" body={(rowData) => textTemplate(rowData.hgvs)} />
				<Column
					header="Sequence of Reference Accession Number"
					body={(rowData) => textTemplate(getSequenceOfReferenceAccessionNumber(rowData.hgvs))}
				/>
				<Column
					field="referenceSequence"
					header="Reference Sequence"
					body={(rowData) => textTemplate(rowData.referenceSequence)}
				/>
				<Column
					field="variantSequence"
					header="Variant Sequence"
					body={(rowData) => textTemplate(rowData.variantSequence)}
				/>
				<Column
					field="consequence.name"
					header="Consequence"
					body={(rowData) => textTemplate(soTermText(rowData.consequence))}
				/>
				<Column
					field="curatedConsequence.name"
					header="Curated Consequence"
					body={(rowData) => textTemplate(soTermText(rowData.curatedConsequence))}
				/>
				<Column
					field="numberAdditionalDnaBasePairs"
					header="Number of additional DNA base pairs"
					body={(rowData) => textTemplate(rowData.numberAdditionalDnaBasePairs)}
				/>
				<Column
					field="numberRemovedDnaBasePairs"
					header="Number of removed DNA base pairs"
					body={(rowData) => textTemplate(rowData.numberRemovedDnaBasePairs)}
				/>
				<Column
					field="insertedSequence"
					header="Inserted Sequence"
					body={(rowData) => textTemplate(rowData.insertedSequence)}
				/>
				<Column
					field="deletedSequence"
					header="Deleted Sequence"
					body={(rowData) => textTemplate(rowData.deletedSequence)}
				/>
				<Column field="paddedBase" header="Padded Base" body={(rowData) => textTemplate(rowData.paddedBase)} />
				<Column
					field="dnaMutationType.name"
					header="DNA Mutation Type"
					body={(rowData) => textTemplate(soTermText(rowData.dnaMutationType))}
				/>
				<Column
					field="geneLocalizationType.name"
					header="Gene Localization Type"
					body={(rowData) => textTemplate(soTermText(rowData.geneLocalizationType))}
				/>
				<Column field="evidence.curie" header="Evidence" body={(rowData) => evidenceTemplate(rowData)} />
			</DataTable>
		</Dialog>
	);
};
