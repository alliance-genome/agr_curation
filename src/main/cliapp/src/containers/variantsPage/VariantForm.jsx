import { TaxonDetailPageEditor } from '../../components/Editors/autocomplete/taxon/TaxonDetailPageEditor';
import { VariantTypeDetailPageEditor } from '../../components/Editors/autocomplete/variantType/VariantTypeDetailPageEditor';
import { SourceGeneralConsequenceDetailPageEditor } from '../../components/Editors/autocomplete/sourceGeneralConsequence/SourceGeneralConsequenceDetailPageEditor';
import { ControlledVocabularyDetailPageEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyDetailPageEditor';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { DetailPageFieldWrapper } from '../../components/DetailPageFieldWrapper';
import { RelatedNotesForm } from './relatedNotes/RelatedNotesForm';
import { ReferencesForm } from './references/ReferencesForm';
import { SynonymsForm } from './synonyms/SynonymsForm';
import { FormSection } from '../../components/FormFieldVisibility';

// Every section on the page, in display order.
export const VARIANT_DETAIL_TOGGLEABLE_FIELDS = [
	'Curie',
	'Primary External ID',
	'MOD Internal ID',
	'Taxon',
	'Variant Type',
	'Variant Status',
	'Related Notes',
	'References',
	'Source General Consequence',
	'Synonyms',
	'Data Provider',
	'Cross References',
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

/**
 * The variant field sections, in display order.
 *
 * @param {Object} props
 * @param {Object} props.state - variant reducer state
 * @param {Function} props.dispatch - variant reducer dispatch
 * @param {(field: string) => boolean} props.isVisible - whether a named section renders
 */
export const VariantForm = ({ state, dispatch, isVisible }) => {
	// An autosuggest hands back the selected object, or the raw string while the curator is still
	// typing. The string is kept so the field shows what was typed and the API can reject it.
	const onOntologyTermValueChange = (field) => (event) => {
		let value = {};
		if (typeof event.value === 'object') {
			value = event.value;
		} else if (event.value === '') {
			value = undefined;
		} else {
			value.curie = event.value;
		}
		dispatch({ type: 'EDIT', field, value });
	};

	const onFieldValueChange = (field) => (event) => {
		dispatch({ type: 'EDIT', field, value: event.value });
	};

	return (
		<form className="mt-8">
			<FormSection isVisible={isVisible('Curie')}>
				<IdentifierDetailPageTemplate
					identifier={state.variant?.curie}
					label="Curie"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Primary External ID')}>
				<IdentifierDetailPageTemplate
					identifier={state.variant?.primaryExternalId}
					label="Primary External ID"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('MOD Internal ID')}>
				<IdentifierDetailPageTemplate
					identifier={state.variant?.modInternalId}
					label="MOD Internal ID"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Taxon')}>
				<TaxonDetailPageEditor
					taxon={state.variant?.taxon}
					onTaxonValueChange={onOntologyTermValueChange('taxon')}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Variant Type')}>
				<VariantTypeDetailPageEditor
					variantType={state.variant?.variantType}
					required
					onVariantTypeValueChange={onOntologyTermValueChange('variantType')}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Variant Status')}>
				<ControlledVocabularyDetailPageEditor
					value={state.variant?.variantStatus}
					name="variantStatus"
					label="Variant Status"
					vocabularyLabel="variant_status"
					onValueChange={onFieldValueChange('variantStatus')}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Related Notes')}>
				<RelatedNotesForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('References')}>
				<ReferencesForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Source General Consequence')}>
				<SourceGeneralConsequenceDetailPageEditor
					sourceGeneralConsequence={state.variant?.sourceGeneralConsequence}
					onSourceGeneralConsequenceValueChange={onOntologyTermValueChange('sourceGeneralConsequence')}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Synonyms')}>
				<SynonymsForm
					state={state}
					dispatch={dispatch}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Data Provider')}>
				<DataProviderDetailPageTemplate
					dataProvider={state.variant?.dataProvider?.abbreviation}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			{/* Read only: the curation system has no cross reference editor, here or on the table. */}
			<FormSection isVisible={isVisible('Cross References')}>
				<DetailPageFieldWrapper
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					widgetColumnSize={widgetColumnSize}
					fieldName="Cross References"
					formField={<CrossReferencesTemplate list={state.variant?.crossReferences} />}
					showAdditionalData={false}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Updated By')}>
				<UserDetailPageTemplate
					user={state.variant?.updatedBy?.uniqueId}
					fieldName="Updated By"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Date Updated')}>
				<DateDetailPageTemplate
					date={state.variant?.dateUpdated}
					fieldName="Date Updated"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Created By')}>
				<UserDetailPageTemplate
					user={state.variant?.createdBy?.uniqueId}
					fieldName="Created By"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Date Created')}>
				<DateDetailPageTemplate
					date={state.variant?.dateCreated}
					fieldName="Date Created"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Internal')}>
				<BooleanDetailPageEditor
					value={state.variant?.internal}
					name={'internal'}
					label={'Internal'}
					onValueChange={onFieldValueChange('internal')}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Obsolete')}>
				<BooleanDetailPageEditor
					value={state.variant?.obsolete}
					name={'obsolete'}
					label={'Obsolete'}
					onValueChange={onFieldValueChange('obsolete')}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>
		</form>
	);
};
