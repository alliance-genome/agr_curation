import { Divider } from 'primereact/divider';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { NameSlotAnnotationForm } from '../../components/nameSlotAnnotations/NameSlotAnnotationForm';
import { SynonymsForm } from '../../components/synonyms/SynonymsForm';
import { SecondaryIdsForm } from '../../components/secondaryIds/SecondaryIdsForm';
import { RelatedNotesForm } from '../../components/relatedNotes/RelatedNotesForm';
import { ReferencesForm } from '../allelesPage/referencesTable/ReferencesForm';
import { CassettesForm } from './cassettes/CassettesForm';

const labelColumnSize = 'col-3';
const widgetColumnSize = 'col-4';
const fieldDetailsColumnSize = 'col-5';
const columnSizes = { labelColumnSize, widgetColumnSize, fieldDetailsColumnSize };

/**
 * The construct detail form's sections, in display order.
 *
 * @param {Object} props
 * @param {Object} props.state - useConstructReducer state
 * @param {Function} props.dispatch - useConstructReducer dispatch
 * @param {Set<string>} [props.pendingFields] - the single-value fields and single-object sections to mark as
 *   having pending edits
 */
export const ConstructForm = ({ state, dispatch, pendingFields }) => {
	const construct = state.construct;
	const edit = (field) => (value) => dispatch({ type: 'EDIT', field, value });

	const nameSection = (entityType, props) => (
		<NameSlotAnnotationForm
			name={construct?.[entityType]}
			entityState={state.entityStates[entityType]}
			dispatch={dispatch}
			entityType={entityType}
			isPending={pendingFields?.has(entityType)}
			{...props}
		/>
	);

	const booleanSection = (field, label) => (
		<BooleanDetailPageEditor
			value={construct?.[field]}
			name={field}
			label={label}
			isPending={pendingFields?.has(field)}
			onChange={edit(field)}
			errorMessages={state.errorMessages}
			{...columnSizes}
		/>
	);

	return (
		// Enter in a table's filters must not submit and reload the page.
		<form className="mt-8" onSubmit={(event) => event.preventDefault()}>
			<IdentifierDetailPageTemplate identifier={construct?.curie} label="Curie" {...columnSizes} />
			<Divider />
			<IdentifierDetailPageTemplate
				identifier={construct?.primaryExternalId}
				label="Primary External ID"
				{...columnSizes}
			/>
			<Divider />
			<IdentifierDetailPageTemplate identifier={construct?.modInternalId} label="MOD Internal ID" {...columnSizes} />
			<Divider />
			<IdentifierDetailPageTemplate identifier={construct?.uniqueId} label="Unique ID" {...columnSizes} />
			<Divider />
			{nameSection('constructSymbol', { isSymbol: true, tableName: 'Symbol', addLabel: 'Add Symbol' })}
			<Divider />
			{nameSection('constructFullName', { tableName: 'Name', addLabel: 'Add Name' })}
			<Divider />
			<SynonymsForm type="constructSynonyms" />
			<Divider />
			<SecondaryIdsForm type="secondaryIdentifiers" plainIds />
			<Divider />
			<CassettesForm />
			<Divider />
			<RelatedNotesForm type="relatedNotes" noteTypeVocabType="construct_note_type" />
			<Divider />
			<ReferencesForm />
			<Divider />
			{booleanSection('placeholder', 'Placeholder')}
			<Divider />
			<DataProviderDetailPageTemplate dataProvider={construct?.dataProvider?.abbreviation} {...columnSizes} />
			<Divider />
			<UserDetailPageTemplate user={construct?.createdBy?.uniqueId} fieldName="Created By" {...columnSizes} />
			<Divider />
			<DateDetailPageTemplate date={construct?.dateCreated} fieldName="Date Created" {...columnSizes} />
			<Divider />
			<UserDetailPageTemplate user={construct?.updatedBy?.uniqueId} fieldName="Updated By" {...columnSizes} />
			<Divider />
			<DateDetailPageTemplate date={construct?.dateUpdated} fieldName="Date Updated" {...columnSizes} />
			<Divider />
			{booleanSection('internal', 'Internal')}
			<Divider />
			{booleanSection('obsolete', 'Obsolete')}
		</form>
	);
};
