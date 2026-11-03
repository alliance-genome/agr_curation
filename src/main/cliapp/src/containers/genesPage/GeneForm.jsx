import { TaxonDetailPageEditor } from '../../components/Editors/autocomplete/taxon/TaxonDetailPageEditor';
import { GeneTypeDetailPageEditor } from '../../components/Editors/autocomplete/geneType/GeneTypeDetailPageEditor';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { NameSlotAnnotationForm } from '../../components/NameSlotAnnotationForm';
import { FormSection } from '../../components/FormFieldVisibility';
import { SynonymsForm } from '../allelesPage/synonyms/SynonymsForm';
import { SecondaryIdsForm } from '../allelesPage/secondaryIds/SecondaryIdsForm';
import { RelatedNotesForm } from '../../components/relatedNotes/RelatedNotesForm';
import { CrossReferencesForm } from '../../components/crossReferences/CrossReferencesForm';
import { GcrpCrossReferenceForm } from './gcrpCrossReference/GcrpCrossReferenceForm';

// Every section on the page, in display order.
export const GENE_DETAIL_TOGGLEABLE_FIELDS = [
	'Curie',
	'Primary External ID',
	'MOD Internal ID',
	'Name',
	'Symbol',
	'Synonyms',
	'Secondary IDs',
	'Systematic Name',
	'Gene Type',
	'Taxon',
	'Related Notes',
	'Data Provider',
	'Cross References',
	'GCRP Cross Reference',
	'Updated By',
	'Date Updated',
	'Created By',
	'Date Created',
	'Internal',
	'Obsolete',
];

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-4';
const fieldDetailsColumnSize = 'col-5';
const columnSizes = { labelColumnSize, widgetColumnSize, fieldDetailsColumnSize };

// An autosuggest hands back the picked term, or the text typed while none is picked, which is held as the term's
// curie so the field shows it and the API can reject it.
const termFrom = (value) => (typeof value === 'object' ? value : { curie: value });

/**
 * The gene detail page's sections, in display order.
 *
 * @param {Object} props
 * @param {Object} props.state - useGeneReducer state
 * @param {Function} props.dispatch - useGeneReducer dispatch
 * @param {(field: string) => boolean} props.isVisible - whether a named section renders
 * @param {Set<string>} [props.pendingFields] - the single-value fields and single-object sections to mark as
 *   having pending edits
 */
export const GeneForm = ({ state, dispatch, isVisible, pendingFields }) => {
	const gene = state.gene;
	const edit = (field) => (event) => dispatch({ type: 'EDIT', field, value: event.value });

	const nameSection = (entityType, props) => (
		<NameSlotAnnotationForm
			name={gene?.[entityType]}
			entityState={state.entityStates[entityType]}
			dispatch={dispatch}
			entityType={entityType}
			isPending={pendingFields?.has(entityType)}
			{...props}
		/>
	);

	return (
		<form className="mt-8">
			<FormSection isVisible={isVisible('Curie')}>
				<IdentifierDetailPageTemplate identifier={gene?.curie} label="Curie" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={isVisible('Primary External ID')}>
				<IdentifierDetailPageTemplate
					identifier={gene?.primaryExternalId}
					label="Primary External ID"
					{...columnSizes}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('MOD Internal ID')}>
				<IdentifierDetailPageTemplate identifier={gene?.modInternalId} label="MOD Internal ID" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={isVisible('Name')}>
				{nameSection('geneFullName', { tableName: 'Name', addLabel: 'Add Full Name' })}
			</FormSection>

			<FormSection isVisible={isVisible('Symbol')}>
				{nameSection('geneSymbol', { isSymbol: true, tableName: 'Symbol', addLabel: 'Add Symbol' })}
			</FormSection>

			<FormSection isVisible={isVisible('Synonyms')}>
				<SynonymsForm type="geneSynonyms" />
			</FormSection>

			<FormSection isVisible={isVisible('Secondary IDs')}>
				<SecondaryIdsForm type="geneSecondaryIds" />
			</FormSection>

			<FormSection isVisible={isVisible('Systematic Name')}>
				{nameSection('geneSystematicName', {
					nameTypeVocabType: 'systematic_name_type',
					tableName: 'Systematic Name',
					addLabel: 'Add Systematic Name',
				})}
			</FormSection>

			<FormSection isVisible={isVisible('Gene Type')}>
				<GeneTypeDetailPageEditor
					geneType={gene?.geneType}
					onGeneTypeValueChange={(event) => dispatch({ type: 'EDIT', field: 'geneType', value: termFrom(event.value) })}
					required
					isPending={pendingFields?.has('geneType')}
					errorMessages={state.errorMessages}
					{...columnSizes}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Taxon')}>
				<TaxonDetailPageEditor
					taxon={gene?.taxon}
					onTaxonValueChange={(event) => dispatch({ type: 'EDIT', field: 'taxon', value: termFrom(event.value) })}
					required
					isPending={pendingFields?.has('taxon')}
					errorMessages={state.errorMessages}
					{...columnSizes}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Related Notes')}>
				<RelatedNotesForm type="relatedNotes" noteTypeVocabType="gene_note_type" />
			</FormSection>

			<FormSection isVisible={isVisible('Data Provider')}>
				<DataProviderDetailPageTemplate dataProvider={gene?.dataProvider?.abbreviation} {...columnSizes} />
			</FormSection>

			<FormSection isVisible={isVisible('Cross References')}>
				<CrossReferencesForm type="crossReferences" />
			</FormSection>

			<FormSection isVisible={isVisible('GCRP Cross Reference')}>
				<GcrpCrossReferenceForm
					state={state}
					dispatch={dispatch}
					isPending={pendingFields?.has('gcrpCrossReference')}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Updated By')}>
				<UserDetailPageTemplate user={gene?.updatedBy?.uniqueId} fieldName="Updated By" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={isVisible('Date Updated')}>
				<DateDetailPageTemplate date={gene?.dateUpdated} fieldName="Date Updated" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={isVisible('Created By')}>
				<UserDetailPageTemplate user={gene?.createdBy?.uniqueId} fieldName="Created By" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={isVisible('Date Created')}>
				<DateDetailPageTemplate date={gene?.dateCreated} fieldName="Date Created" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={isVisible('Internal')}>
				<BooleanDetailPageEditor
					value={gene?.internal}
					name="internal"
					label="Internal"
					isPending={pendingFields?.has('internal')}
					onValueChange={edit('internal')}
					errorMessages={state.errorMessages}
					{...columnSizes}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Obsolete')}>
				<BooleanDetailPageEditor
					value={gene?.obsolete}
					name="obsolete"
					label="Obsolete"
					isPending={pendingFields?.has('obsolete')}
					onValueChange={edit('obsolete')}
					errorMessages={state.errorMessages}
					{...columnSizes}
				/>
			</FormSection>
		</form>
	);
};
