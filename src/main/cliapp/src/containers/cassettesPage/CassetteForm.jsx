import { FormSection } from '../../components/FormFieldVisibility';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { NameSlotAnnotationForm } from '../../components/nameSlotAnnotations/NameSlotAnnotationForm';
import { SynonymsForm } from '../transgenicToolsPage/detail/SynonymsForm';
import { SecondaryIdsForm } from '../transgenicToolsPage/detail/SecondaryIdsForm';
import { UsesForm } from '../transgenicToolsPage/detail/UsesForm';
import { ReferencesForm } from '../transgenicToolsPage/detail/ReferencesForm';
import {
	FREE_TEXT_COMPONENTS,
	GENOMIC_ENTITY_COMPONENTS,
	TRANSGENIC_TOOL_COMPONENTS,
	STR_COMPONENTS,
} from './components/componentConfigs';
import { CassetteComponentsForm } from './detail/CassetteComponentsForm';

const ENTITY_KEY = 'cassette';

const COMPONENT_TITLES = [
	FREE_TEXT_COMPONENTS.title,
	GENOMIC_ENTITY_COMPONENTS.title,
	TRANSGENIC_TOOL_COMPONENTS.title,
	STR_COMPONENTS.title,
];

// The fields of the detail page, in the ticket's order; each can be hidden through the field menu (SCRUM-6747)
export const CASSETTE_DETAIL_TOGGLEABLE_FIELDS = [
	'Unique ID',
	'Curie',
	'Primary External ID',
	'MOD Internal ID',
	'Symbol',
	'Name',
	'Synonyms',
	'Secondary IDs',
	...COMPONENT_TITLES,
	'Uses',
	'References',
	'Data Provider',
	'Updated By',
	'Date Updated',
	'Created By',
	'Date Created',
	'Placeholder',
	'Internal',
	'Obsolete',
];

// The fields of the create page, in the ticket's order (SCRUM-6748): those a curator enters, without the ids, data
// provider and audit fields the API sets
export const CASSETTE_CREATE_TOGGLEABLE_FIELDS = [
	'Symbol',
	'Name',
	'Synonyms',
	'Secondary IDs',
	...COMPONENT_TITLES,
	'Uses',
	'References',
	'Placeholder',
	'Internal',
];

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-4';
const fieldDetailsColumnSize = 'col-5';

const columnSizes = { labelColumnSize, widgetColumnSize, fieldDetailsColumnSize };

/**
 * The sections of a cassette, each of which the curator can hide through the field menu: on the detail page
 * (SCRUM-6747) and, without the fields the API sets, on the create page (SCRUM-6748).
 *
 * @param {Object} props
 * @param {Object} props.state - the useCassetteReducer state
 * @param {Function} props.dispatch - its dispatch
 * @param {Array<{config: Object, components: Object}>} props.componentSections - the component lists, each with
 *   its useCassetteComponentList result
 * @param {(fieldName: string) => boolean} props.isVisible - whether a section is shown
 * @param {'detail'|'create'} [props.mode]
 * @returns {JSX.Element}
 */
export const CassetteForm = ({ state, dispatch, componentSections, isVisible, mode = 'detail' }) => {
	const cassette = state[ENTITY_KEY];
	const isDetail = mode === 'detail';
	const showsField = (field) => (isDetail || CASSETTE_CREATE_TOGGLEABLE_FIELDS.includes(field)) && isVisible(field);

	const onBooleanChange = (field) => (event) => dispatch({ type: 'EDIT', field, value: event.value });

	const booleanField = (field, label) => (
		<BooleanDetailPageEditor
			value={cassette?.[field]}
			name={field}
			label={label}
			onValueChange={onBooleanChange(field)}
			errorMessages={state.errorMessages}
			{...columnSizes}
		/>
	);

	const identifierField = (field, label) => (
		<FormSection isVisible={showsField(label)}>
			<IdentifierDetailPageTemplate
				identifier={cassette?.[field]}
				label={label}
				{...columnSizes}
				showAdditionalData={false}
			/>
		</FormSection>
	);

	const formProps = { state, dispatch, entityKey: ENTITY_KEY };

	return (
		// Saving is the page's buttons; Enter in a table filter or field must not submit and reload the page.
		<form className="mt-8" onSubmit={(event) => event.preventDefault()}>
			{identifierField('uniqueId', 'Unique ID')}
			{identifierField('curie', 'Curie')}
			{identifierField('primaryExternalId', 'Primary External ID')}
			{identifierField('modInternalId', 'MOD Internal ID')}
			<FormSection isVisible={showsField('Symbol')}>
				<NameSlotAnnotationForm
					name={cassette?.cassetteSymbol}
					entityState={state.entityStates.cassetteSymbol}
					dispatch={dispatch}
					entityType="cassetteSymbol"
					isSymbol
					tableName="Symbol"
					addLabel="Add Symbol"
				/>
			</FormSection>
			<FormSection isVisible={showsField('Name')}>
				<NameSlotAnnotationForm
					name={cassette?.cassetteFullName}
					entityState={state.entityStates.cassetteFullName}
					dispatch={dispatch}
					entityType="cassetteFullName"
					tableName="Name"
					addLabel="Add Name"
				/>
			</FormSection>
			<FormSection isVisible={showsField('Synonyms')}>
				<SynonymsForm {...formProps} entityType="cassetteSynonyms" />
			</FormSection>
			<FormSection isVisible={showsField('Secondary IDs')}>
				<SecondaryIdsForm {...formProps} {...columnSizes} />
			</FormSection>
			{componentSections.map(({ config, components }) => (
				<FormSection key={config.list} isVisible={showsField(config.title)}>
					<CassetteComponentsForm components={components} config={config} showPending={isDetail} />
				</FormSection>
			))}
			<FormSection isVisible={showsField('Uses')}>
				<UsesForm {...formProps} entityType="cassetteUses" tableName="Uses" />
			</FormSection>
			<FormSection isVisible={showsField('References')}>
				<ReferencesForm {...formProps} />
			</FormSection>
			<FormSection isVisible={showsField('Data Provider')}>
				<DataProviderDetailPageTemplate dataProvider={cassette?.dataProvider?.abbreviation} {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Updated By')}>
				<UserDetailPageTemplate user={cassette?.updatedBy?.uniqueId} fieldName="Updated By" {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Date Updated')}>
				<DateDetailPageTemplate date={cassette?.dateUpdated} fieldName="Date Updated" {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Created By')}>
				<UserDetailPageTemplate user={cassette?.createdBy?.uniqueId} fieldName="Created By" {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Date Created')}>
				<DateDetailPageTemplate date={cassette?.dateCreated} fieldName="Date Created" {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Placeholder')}>{booleanField('placeholder', 'Placeholder')}</FormSection>
			<FormSection isVisible={showsField('Internal')}>{booleanField('internal', 'Internal')}</FormSection>
			<FormSection isVisible={showsField('Obsolete')}>{booleanField('obsolete', 'Obsolete')}</FormSection>
		</form>
	);
};
