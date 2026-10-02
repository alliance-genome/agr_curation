import { TaxonDetailPageEditor } from '../../components/Editors/autocomplete/taxon/TaxonDetailPageEditor';
import { ControlledVocabularyDetailPageEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyDetailPageEditor';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { DetailPageFieldWrapper } from '../../components/DetailPageFieldWrapper';
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
 */
export const AGMForm = ({ state, dispatch, isVisible }) => {
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
					identifier={state.agm?.curie}
					label="Curie"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Primary External ID')}>
				<IdentifierDetailPageTemplate
					identifier={state.agm?.primaryExternalId}
					label="Primary External ID"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('MOD Internal ID')}>
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
			<FormSection isVisible={isVisible('Cross References')}>
				<DetailPageFieldWrapper
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					widgetColumnSize={widgetColumnSize}
					fieldName="Cross References"
					formField={<CrossReferencesTemplate list={state.agm?.crossReferences} />}
					showAdditionalData={false}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Updated By')}>
				<UserDetailPageTemplate
					user={state.agm?.updatedBy?.uniqueId}
					fieldName="Updated By"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Date Updated')}>
				<DateDetailPageTemplate
					date={state.agm?.dateUpdated}
					fieldName="Date Updated"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Created By')}>
				<UserDetailPageTemplate
					user={state.agm?.createdBy?.uniqueId}
					fieldName="Created By"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Date Created')}>
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
