import { getPendingFields, nameSlotAnnotationValue } from '../../utils/pendingFields';
import { processErrors, processTableErrors } from '../../utils/processErrors';
import {
	crossReferenceValue,
	seedResourceDescriptor,
	seedResourceDescriptors,
	stripUiFields,
} from '../../components/crossReferences/utils';

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

/**
 * The gene as the create endpoint takes it: as the detail save sends it, with the `type` BiologicalEntity's
 * polymorphic deserialization reads to build a new gene.
 *
 * @param {Object} gene the gene as the form holds it
 * @returns {Object} a copy to send; the argument is not modified
 */
export const buildGeneCreatePayload = (gene) => ({ ...buildGeneDetailPayload(gene), type: 'Gene' });

/**
 * The gene the API returned, with its GCRP cross reference given its descriptor, so the page dropdown can offer the
 * descriptor's pages.
 *
 * @param {Object} entity the gene as the API returned it
 * @returns {Promise<Object>} a copy for the form to hold
 */
export const withSeededGcrpCrossReference = async (entity) => {
	const [gcrpCrossReference = null] = entity.gcrpCrossReference
		? await seedResourceDescriptors([entity.gcrpCrossReference])
		: [];
	return { ...entity, gcrpCrossReference };
};

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

/**
 * Marks a rejected gene's field errors on the form. The GCRP cross reference's error arrives as one string naming its
 * fields, so it is split onto the row's cells; one that names no field stays with the section.
 *
 * @param {Object} [data] the rejected request's response body
 * @param {Function} dispatch useGeneReducer dispatch
 * @param {Object} gene the gene as the form held it when it was sent
 */
export const processGeneErrors = (data, dispatch, gene) => {
	processErrors(data, dispatch, gene);

	const { gcrpCrossReference: gcrpError, ...otherErrors } = data?.errorMessages ?? {};
	const gcrpFieldErrors = gcrpCrossReferenceFieldErrors(gcrpError);
	if (gcrpFieldErrors && gene.gcrpCrossReference) {
		processTableErrors(gcrpFieldErrors, dispatch, 'gcrpCrossReference', gene.gcrpCrossReference);
		dispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: otherErrors });
	}
};
