import { FormSection } from '../../components/FormFieldVisibility';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { NameSlotAnnotationForm } from '../../components/nameSlotAnnotations/NameSlotAnnotationForm';
import { SynonymsForm } from './detail/SynonymsForm';
import { SecondaryIdsForm } from './detail/SecondaryIdsForm';
import { UsesForm } from './detail/UsesForm';
import { TransgenicToolAssociationsForm } from './detail/TransgenicToolAssociationsForm';
import { ReferencesForm } from './detail/ReferencesForm';
import { RelatedNotesForm } from './detail/RelatedNotesForm';
import { CrossReferencesForm } from './detail/CrossReferencesForm';

const ENTITY_KEY = 'transgenicTool';

// The fields of the detail page, in order; each can be hidden through the field menu (SCRUM-6655)
export const TRANSGENIC_TOOL_DETAIL_TOGGLEABLE_FIELDS = [
	'Unique ID',
	'Curie',
	'Primary External ID',
	'MOD Internal ID',
	'Full Name',
	'Symbol',
	'Synonyms',
	'Secondary IDs',
	'Transgenic Tool Uses',
	'Transgenic Tool Associations',
	'References',
	'Related Notes',
	'Data Provider',
	'Cross References',
	'Updated By',
	'Date Updated',
	'Created By',
	'Date Created',
	'Placeholder',
	'Internal',
	'Obsolete',
];

// The fields of the create page, in the ticket's order (SCRUM-6691): those a curator enters, without the ids, data
// provider and audit fields the API sets
export const TRANSGENIC_TOOL_CREATE_TOGGLEABLE_FIELDS = [
	'Full Name',
	'Symbol',
	'Synonyms',
	'Secondary IDs',
	'Transgenic Tool Uses',
	'Transgenic Tool Associations',
	'Related Notes',
	'Cross References',
	'References',
	'Placeholder',
	'Internal',
];

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-4';
const fieldDetailsColumnSize = 'col-5';

const columnSizes = { labelColumnSize, widgetColumnSize, fieldDetailsColumnSize };

/**
 * The sections of a transgenic tool, each of which the curator can hide through the field menu: on the detail page
 * (SCRUM-6655) and, without the fields the API sets and in the ticket's order, on the create page (SCRUM-6691).
 *
 * @param {Object} props
 * @param {Object} props.state - the useTransgenicToolReducer state
 * @param {Function} props.dispatch - its dispatch
 * @param {Object} props.associations - the useTransgenicToolAssociations result
 * @param {(fieldName: string) => boolean} props.isVisible - whether a section is shown
 * @param {'detail'|'create'} [props.mode]
 * @returns {JSX.Element}
 */
export const TransgenicToolForm = ({ state, dispatch, associations, isVisible, mode = 'detail' }) => {
	const transgenicTool = state[ENTITY_KEY];
	const isDetail = mode === 'detail';
	const showsField = (field) =>
		(isDetail || TRANSGENIC_TOOL_CREATE_TOGGLEABLE_FIELDS.includes(field)) && isVisible(field);

	const onBooleanChange = (field) => (value) => dispatch({ type: 'EDIT', field, value });

	const booleanField = (field, label) => (
		<BooleanDetailPageEditor
			value={transgenicTool?.[field]}
			name={field}
			label={label}
			onChange={onBooleanChange(field)}
			errorMessages={state.errorMessages}
			{...columnSizes}
		/>
	);

	const identifierField = (field, label) => (
		<FormSection isVisible={showsField(label)}>
			<IdentifierDetailPageTemplate
				identifier={transgenicTool?.[field]}
				label={label}
				{...columnSizes}
				showAdditionalData={false}
			/>
		</FormSection>
	);

	// The detail page shows the references before the related notes; the ticket puts them last on the create page
	const referencesSection = (
		<FormSection isVisible={showsField('References')}>
			<ReferencesForm state={state} dispatch={dispatch} />
		</FormSection>
	);

	return (
		// Saving is the page's buttons; Enter in a table filter or field must not submit and reload the page.
		<form className="mt-8" onSubmit={(event) => event.preventDefault()}>
			{identifierField('uniqueId', 'Unique ID')}
			{identifierField('curie', 'Curie')}
			{identifierField('primaryExternalId', 'Primary External ID')}
			{identifierField('modInternalId', 'MOD Internal ID')}
			<FormSection isVisible={showsField('Full Name')}>
				<NameSlotAnnotationForm
					name={transgenicTool?.transgenicToolFullName}
					entityState={state.entityStates.transgenicToolFullName}
					dispatch={dispatch}
					entityType="transgenicToolFullName"
					tableName="Full Name"
					addLabel="Add Full Name"
				/>
			</FormSection>
			<FormSection isVisible={showsField('Symbol')}>
				<NameSlotAnnotationForm
					name={transgenicTool?.transgenicToolSymbol}
					entityState={state.entityStates.transgenicToolSymbol}
					dispatch={dispatch}
					entityType="transgenicToolSymbol"
					isSymbol
					tableName="Symbol"
					addLabel="Add Symbol"
				/>
			</FormSection>
			<FormSection isVisible={showsField('Synonyms')}>
				<SynonymsForm state={state} dispatch={dispatch} />
			</FormSection>
			<FormSection isVisible={showsField('Secondary IDs')}>
				<SecondaryIdsForm state={state} dispatch={dispatch} {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Transgenic Tool Uses')}>
				<UsesForm state={state} dispatch={dispatch} />
			</FormSection>
			<FormSection isVisible={showsField('Transgenic Tool Associations')}>
				<TransgenicToolAssociationsForm associations={associations} />
			</FormSection>
			{isDetail && referencesSection}
			<FormSection isVisible={showsField('Related Notes')}>
				<RelatedNotesForm state={state} dispatch={dispatch} />
			</FormSection>
			<FormSection isVisible={showsField('Data Provider')}>
				<DataProviderDetailPageTemplate dataProvider={transgenicTool?.dataProvider?.abbreviation} {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Cross References')}>
				<CrossReferencesForm state={state} dispatch={dispatch} />
			</FormSection>
			{!isDetail && referencesSection}
			<FormSection isVisible={showsField('Updated By')}>
				<UserDetailPageTemplate user={transgenicTool?.updatedBy?.uniqueId} fieldName="Updated By" {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Date Updated')}>
				<DateDetailPageTemplate date={transgenicTool?.dateUpdated} fieldName="Date Updated" {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Created By')}>
				<UserDetailPageTemplate user={transgenicTool?.createdBy?.uniqueId} fieldName="Created By" {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Date Created')}>
				<DateDetailPageTemplate date={transgenicTool?.dateCreated} fieldName="Date Created" {...columnSizes} />
			</FormSection>
			<FormSection isVisible={showsField('Placeholder')}>{booleanField('placeholder', 'Placeholder')}</FormSection>
			<FormSection isVisible={showsField('Internal')}>{booleanField('internal', 'Internal')}</FormSection>
			<FormSection isVisible={showsField('Obsolete')}>{booleanField('obsolete', 'Obsolete')}</FormSection>
		</form>
	);
};
