import { getPendingFields, nameSlotAnnotationValue } from '../../utils/pendingFields';
import { crossReferenceValue, seedResourceDescriptor, stripUiFields } from '../allelesPage/crossReferences/utils';

// The value each of the gene's single-value fields and single-object sections is compared by, with an unset or
// blank value read as none.
const GENE_FIELD_VALUES = {
	taxon: (gene) => gene?.taxon?.curie || null,
	geneType: (gene) => gene?.geneType?.curie || null,
	internal: (gene) => gene?.internal ?? null,
	obsolete: (gene) => gene?.obsolete ?? null,
	geneSymbol: (gene) => nameSlotAnnotationValue(gene?.geneSymbol),
	geneFullName: (gene) => nameSlotAnnotationValue(gene?.geneFullName),
	geneSystematicName: (gene) => nameSlotAnnotationValue(gene?.geneSystematicName),
	// Compared with its descriptor seeded, as the table holds it, so a gene the API returned compares equal to the
	// same gene on the form.
	gcrpCrossReference: (gene) =>
		gene?.gcrpCrossReference ? crossReferenceValue(seedResourceDescriptor(gene.gcrpCrossReference)) : null,
};

/**
 * The gene's single-value fields and single-object sections whose value on the form differs from the gene as last
 * saved.
 *
 * @param {Object} gene the gene as the form holds it
 * @param {Object} [savedGene] the gene as the API last returned it
 * @returns {Set<string>} the pending fields' names, none while there is no saved gene
 */
export const getPendingGeneFields = (gene, savedGene) => getPendingFields(gene, savedGene, GENE_FIELD_VALUES);

/**
 * The gene as the detail save sends it. A cleared taxon or gene type is held as a term with a blank curie, which the
 * API would read as none without reporting it missing, so it is sent as unset and reported as required. The GCRP
 * cross reference is sent without the fields its table keeps for itself.
 *
 * @param {Object} gene the gene as the form holds it
 * @returns {Object} a copy to send; the argument is not modified
 */
export const buildGeneDetailPayload = (gene) => ({
	...gene,
	taxon: gene.taxon?.curie ? gene.taxon : undefined,
	geneType: gene.geneType?.curie ? gene.geneType : undefined,
	gcrpCrossReference: gene.gcrpCrossReference ? stripUiFields(gene.gcrpCrossReference) : gene.gcrpCrossReference,
});

// The cross reference fields whose cells in the cross references table show an error.
const CROSS_REFERENCE_CELL_FIELDS = ['displayName', 'referencedCurie', 'resourceDescriptor', 'resourceDescriptorPage'];

/**
 * Splits the GCRP cross reference's error, which the API sends as one `field - message | field - message` string,
 * into its messages by field.
 *
 * @param {string} [errorMessage] the GCRP cross reference's error as the API sent it
 * @returns {Object<string, string>|null} the messages by field, or null when the error is not in that form or names a
 *   field with no cell to show it
 */
export const gcrpCrossReferenceFieldErrors = (errorMessage) => {
	if (typeof errorMessage !== 'string') return null;

	const fieldErrors = {};
	for (const part of errorMessage.split(' | ')) {
		const separatorIndex = part.indexOf(' - ');
		const field = part.slice(0, separatorIndex);
		if (separatorIndex <= 0 || !CROSS_REFERENCE_CELL_FIELDS.includes(field)) return null;
		fieldErrors[field] = part.slice(separatorIndex + ' - '.length);
	}

	return fieldErrors;
};
