import { InputText } from 'primereact/inputtext';
import { TaxonDetailPageEditor } from '../../components/Editors/autocomplete/taxon/TaxonDetailPageEditor';
import { ControlledVocabularyDetailPageEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyDetailPageEditor';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { DetailPageFieldWrapper } from '../../components/DetailPageFieldWrapper';
import { FormErrorMessageComponent } from '../../components/Error/FormErrorMessageComponent';
import { AgmFullNameForm } from './agmFullName/AgmFullNameForm';
import { SynonymsForm } from './synonyms/SynonymsForm';
import { SecondaryIdsForm } from './secondaryIds/SecondaryIdsForm';
import { FormSection } from '../../components/FormFieldVisibility';

// Every section on the page, in display order.
export const AGM_DETAIL_TOGGLEABLE_FIELDS = [
	'Curie',
	'Primary External ID',
	'MOD Internal ID',
	'Name',
	'Synonyms',
	'Secondary IDs',
	'Sub Type',
	'Taxon',
	'Data Provider',
	'Cross References',
	'Updated By',
	'Date Updated',
	'Created By',
	'Date Created',
	'Internal',
	'Obsolete',
];

// The sections create mode does not render, so its menu does not offer them. Cross References has
// no editor here or on the table, so a brand new AGM has nothing to show there either. MOD
// Internal ID stays read-only/omitted even on create - Primary External ID covers the server's
// identifier requirement below, and the two are alternatives, not both needed from a curator.
const FIELDS_OMITTED_ON_CREATE = [
	'Curie',
	'MOD Internal ID',
	'Cross References',
	'Updated By',
	'Date Updated',
	'Created By',
	'Date Created',
];

// The fields the API will not accept as empty on create. Sub Type and Taxon are required
// directly; Primary External ID stands in for AffectedGenomicModelValidator's "modInternalId or
// primaryExternalId" identifier check (read-only elsewhere, editable here for that reason - unlike
// Allele, which opts out of this check entirely). These carry the required marker in create mode,
// and the create form does not let a curator hide them.
const FIELDS_REQUIRED_ON_CREATE = ['Primary External ID', 'Sub Type', 'Taxon'];

export const AGM_CREATE_TOGGLEABLE_FIELDS = AGM_DETAIL_TOGGLEABLE_FIELDS.filter(
	(field) => !FIELDS_OMITTED_ON_CREATE.includes(field) && !FIELDS_REQUIRED_ON_CREATE.includes(field)
);

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-4';
const fieldDetailsColumnSize = 'col-5';

/**
 * The AGM field sections, in display order.
 *
 * @param {Object} props
 * @param {Object} props.state - AGM reducer state
 * @param {Function} props.dispatch - AGM reducer dispatch
 * @param {(field: string) => boolean} props.isVisible - whether a named section renders
 * @param {'detail'|'create'} [props.mode] - 'create' drops the fields the server assigns
 */
export const AGMForm = ({ state, dispatch, isVisible, mode = 'detail' }) => {
	const isCreate = mode === 'create';
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
			<FormSection isVisible={!isCreate && isVisible('Curie')}>
				<IdentifierDetailPageTemplate
					identifier={state.agm?.curie}
					label="Curie"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Primary External ID')}>
				{isCreate ? (
					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						widgetColumnSize={widgetColumnSize}
						fieldName="Primary External ID"
						required
						showAdditionalData={false}
						formField={
							<InputText
								value={state.agm?.primaryExternalId || ''}
								onChange={(event) => dispatch({ type: 'EDIT', field: 'primaryExternalId', value: event.target.value })}
							/>
						}
						errorField={
							<FormErrorMessageComponent errorMessages={state.errorMessages} errorField="primaryExternalId" />
						}
					/>
				) : (
					<IdentifierDetailPageTemplate
						identifier={state.agm?.primaryExternalId}
						label="Primary External ID"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>
				)}
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('MOD Internal ID')}>
				<IdentifierDetailPageTemplate
					identifier={state.agm?.modInternalId}
					label="MOD Internal ID"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Name')}>
				<AgmFullNameForm labelColumnSize={labelColumnSize} state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Synonyms')}>
				<SynonymsForm labelColumnSize={labelColumnSize} state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Secondary IDs')}>
				<SecondaryIdsForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Sub Type')}>
				<ControlledVocabularyDetailPageEditor
					value={state.agm?.subtype}
					name="subtype"
					label="Sub Type"
					vocabularyLabel="agm_subtype"
					required={isCreate}
					onValueChange={onFieldValueChange('subtype')}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Taxon')}>
				<TaxonDetailPageEditor
					taxon={state.agm?.taxon}
					required={isCreate}
					onTaxonValueChange={onOntologyTermValueChange('taxon')}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Data Provider')}>
				<DataProviderDetailPageTemplate
					dataProvider={state.agm?.dataProvider?.abbreviation}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			{/* Read only: the curation system has no cross reference editor, here or on the table. */}
			<FormSection isVisible={!isCreate && isVisible('Cross References')}>
				<DetailPageFieldWrapper
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					widgetColumnSize={widgetColumnSize}
					fieldName="Cross References"
					formField={<CrossReferencesTemplate list={state.agm?.crossReferences} />}
					showAdditionalData={false}
				/>
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('Updated By')}>
				<UserDetailPageTemplate
					user={state.agm?.updatedBy?.uniqueId}
					fieldName="Updated By"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('Date Updated')}>
				<DateDetailPageTemplate
					date={state.agm?.dateUpdated}
					fieldName="Date Updated"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('Created By')}>
				<UserDetailPageTemplate
					user={state.agm?.createdBy?.uniqueId}
					fieldName="Created By"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={!isCreate && isVisible('Date Created')}>
				<DateDetailPageTemplate
					date={state.agm?.dateCreated}
					fieldName="Date Created"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Internal')}>
				<BooleanDetailPageEditor
					value={state.agm?.internal}
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
					value={state.agm?.obsolete}
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
