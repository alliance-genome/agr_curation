import { TaxonDetailPageEditor } from '../../components/Editors/autocomplete/taxon/TaxonDetailPageEditor';
import { GeneTypeDetailPageEditor } from '../../components/Editors/autocomplete/geneType/GeneTypeDetailPageEditor';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { NameSlotAnnotationForm } from '../../components/nameSlotAnnotations/NameSlotAnnotationForm';
import { FormSection } from '../../components/FormFieldVisibility';
import { SynonymsForm } from '../../components/synonyms/SynonymsForm';
import { SecondaryIdsForm } from '../../components/secondaryIds/SecondaryIdsForm';
import { RelatedNotesForm } from '../../components/relatedNotes/RelatedNotesForm';
import { CrossReferencesForm } from '../../components/crossReferences/CrossReferencesForm';
import { GcrpCrossReferenceForm } from './gcrpCrossReference/GcrpCrossReferenceForm';
import { getEffectiveModAbbreviation } from '../../utils/affiliation';

// The sections create mode does not render, so its menu does not offer them.
const FIELDS_OMITTED_ON_CREATE = [
	'Curie',
	'Primary External ID',
	'MOD Internal ID',
	'Updated By',
	'Date Updated',
	'Created By',
	'Date Created',
];

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

// The fields the API will not accept as empty on create. These carry the required marker in create mode, and the
// create form does not let a curator hide them.
const FIELDS_REQUIRED_ON_CREATE = ['Symbol', 'Gene Type', 'Taxon'];

export const GENE_CREATE_TOGGLEABLE_FIELDS = GENE_DETAIL_TOGGLEABLE_FIELDS.filter(
	(field) => !FIELDS_OMITTED_ON_CREATE.includes(field) && !FIELDS_REQUIRED_ON_CREATE.includes(field)
);

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-4';
const fieldDetailsColumnSize = 'col-5';
const columnSizes = { labelColumnSize, widgetColumnSize, fieldDetailsColumnSize };

// An autosuggest hands back the picked term, or the text typed while none is picked, which is held as the term's
// curie so the field shows it and the API can reject it.
const termFrom = (value) => (typeof value === 'object' ? value : { curie: value });

/**
 * The gene form's sections, in display order.
 *
 * @param {Object} props
 * @param {Object} props.state - useGeneReducer state
 * @param {Function} props.dispatch - useGeneReducer dispatch
 * @param {(field: string) => boolean} props.isVisible - whether a named section renders
 * @param {'detail'|'create'} [props.mode] - 'create' drops the fields the server assigns, marks the symbol required
 *   and shows the data provider the new gene will be given
 * @param {Set<string>} [props.pendingFields] - the single-value fields and single-object sections to mark as
 *   having pending edits
 */
export const GeneForm = ({ state, dispatch, isVisible, mode = 'detail', pendingFields }) => {
	const isCreate = mode === 'create';
	const gene = state.gene;
	const edit = (field) => (value) => dispatch({ type: 'EDIT', field, value });

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
			<FormSection isVisible={!isCreate && isVisible('Curie')}>
				<IdentifierDetailPageTemplate identifier={gene?.curie} label="Curie" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('Primary External ID')}>
				<IdentifierDetailPageTemplate
					identifier={gene?.primaryExternalId}
					label="Primary External ID"
					{...columnSizes}
				/>
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('MOD Internal ID')}>
				<IdentifierDetailPageTemplate identifier={gene?.modInternalId} label="MOD Internal ID" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={isVisible('Name')}>
				{nameSection('geneFullName', { tableName: 'Name', addLabel: 'Add Full Name' })}
			</FormSection>

			<FormSection isVisible={isVisible('Symbol')}>
				{nameSection('geneSymbol', {
					isSymbol: true,
					tableName: 'Symbol',
					addLabel: 'Add Symbol',
					required: isCreate,
					showAddButton: !isCreate,
				})}
			</FormSection>

			<FormSection isVisible={isVisible('Synonyms')}>
				<SynonymsForm type="geneSynonyms" mode={mode} />
			</FormSection>

			<FormSection isVisible={isVisible('Secondary IDs')}>
				<SecondaryIdsForm type="geneSecondaryIds" mode={mode} />
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
				<RelatedNotesForm type="relatedNotes" noteTypeVocabType="gene_note_type" mode={mode} />
			</FormSection>

			<FormSection isVisible={isVisible('Data Provider')}>
				<DataProviderDetailPageTemplate
					dataProvider={isCreate ? getEffectiveModAbbreviation() : gene?.dataProvider?.abbreviation}
					{...columnSizes}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Cross References')}>
				<CrossReferencesForm type="crossReferences" mode={mode} />
			</FormSection>

			<FormSection isVisible={isVisible('GCRP Cross Reference')}>
				<GcrpCrossReferenceForm
					state={state}
					dispatch={dispatch}
					isPending={pendingFields?.has('gcrpCrossReference')}
				/>
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('Updated By')}>
				<UserDetailPageTemplate user={gene?.updatedBy?.uniqueId} fieldName="Updated By" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('Date Updated')}>
				<DateDetailPageTemplate date={gene?.dateUpdated} fieldName="Date Updated" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('Created By')}>
				<UserDetailPageTemplate user={gene?.createdBy?.uniqueId} fieldName="Created By" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('Date Created')}>
				<DateDetailPageTemplate date={gene?.dateCreated} fieldName="Date Created" {...columnSizes} />
			</FormSection>

			<FormSection isVisible={isVisible('Internal')}>
				<BooleanDetailPageEditor
					value={gene?.internal}
					name="internal"
					label="Internal"
					isPending={pendingFields?.has('internal')}
					onChange={edit('internal')}
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
					onChange={edit('obsolete')}
					errorMessages={state.errorMessages}
					{...columnSizes}
				/>
			</FormSection>
		</form>
	);
};
