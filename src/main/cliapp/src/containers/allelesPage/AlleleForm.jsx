import { TaxonDetailPageEditor } from '../../components/Editors/autocomplete/taxon/TaxonDetailPageEditor';
import { InCollectionDetailPageEditor } from '../../components/Editors/autocomplete/inCollection/InCollectionDetailPageEditor';
import { BooleanDetailPageEditor } from '../../components/Editors/dropdown/boolean/BooleanDetailPageEditor';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { SynonymsForm } from './synonyms/SynonymsForm';
import { FullNameForm } from './fullName/FullNameForm';
import { MutationTypesForm } from './mutationTypes/MutationTypesForm';
import { InheritanceModesForm } from './inheritanceModes/InheritanceModesForm';
import { SecondaryIdsForm } from './secondaryIds/SecondaryIdsForm';
import { FunctionalImpactsForm } from './functionalImpacts/FunctionalImpactsForm';
import { DatabaseStatusForm } from './databaseStatus/DatabaseStatusForm';
import { RelatedNotesForm } from './relatedNotes/RelatedNotesForm';
import { SymbolForm } from './symbol/SymbolForm';
import { GermilineTransmissionStatusForm } from './germlineTransmissionStatus/GermlineTransmissionStatusForm';
import { ReferencesForm } from './referencesTable/ReferencesForm';
import { NomenclatureEventsForm } from './nomenclatureEvents/NomenclatureEventsForm';
import { AlleleGeneAssociationsForm } from './alleleGeneAssociations/AlleleGeneAssociationsForm';
import { FormSection } from '../../components/FormFieldVisibility';

// Every section on the page, in display order.
export const ALLELE_TOGGLEABLE_FIELDS = [
	'Curie',
	'Primary External ID',
	'MOD Internal ID',
	'Name',
	'Symbol',
	'Synonyms',
	'Secondary IDs',
	'Nomenclature Events',
	'Taxon',
	'Mutation Types',
	'Functional Impacts',
	'Germline Transmission Status',
	'Database Status',
	'Inheritance Modes',
	'References',
	'In Collection',
	'Is Extinct',
	'Related Notes',
	'Allele Gene Associations',
	'Data Provider',
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
 * The allele field sections, in display order.
 *
 * @param {Object} props
 * @param {Object} props.state - allele reducer state
 * @param {Function} props.dispatch - allele reducer dispatch
 * @param {(field: string) => boolean} props.isVisible - whether a named section renders
 */
export const AlleleForm = ({ state, dispatch, isVisible }) => {
	const onTaxonValueChange = (event) => {
		let value = {};
		if (typeof event.value === 'object') {
			value = event.value;
		} else {
			value.curie = event.value;
		}
		dispatch({
			type: 'EDIT',
			field: 'taxon',
			value,
		});
	};

	const onInCollectionValueChange = (event) => {
		let value = {};
		if (typeof event.value === 'object') {
			value = event.value;
		} else if (event.value === '') {
			value = undefined;
		} else {
			value.name = event.value;
		}
		dispatch({
			type: 'EDIT',
			field: 'inCollection',
			value,
		});
	};

	const onIsExtinctValueChange = (event) => {
		dispatch({
			type: 'EDIT',
			field: 'isExtinct',
			value: event.value,
		});
	};

	const onInternalValueChange = (event) => {
		dispatch({
			type: 'EDIT',
			field: 'internal',
			value: event.value,
		});
	};

	const onObsoleteValueChange = (event) => {
		dispatch({
			type: 'EDIT',
			field: 'obsolete',
			value: event.value,
		});
	};

	return (
		<form className="mt-8">
			<FormSection isVisible={isVisible('Curie')}>
				<IdentifierDetailPageTemplate
					identifier={state.allele?.curie}
					label="Curie"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Primary External ID')}>
				<IdentifierDetailPageTemplate
					identifier={state.allele?.primaryExternalId}
					label="Primary External ID"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('MOD Internal ID')}>
				<IdentifierDetailPageTemplate
					identifier={state.allele?.modInternalId}
					label="MOD Internal ID"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Name')}>
				<FullNameForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Symbol')}>
				<SymbolForm state={state} dispatch={dispatch} labelColumnSize={labelColumnSize} />
			</FormSection>

			<FormSection isVisible={isVisible('Synonyms')}>
				<SynonymsForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Secondary IDs')}>
				<SecondaryIdsForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Nomenclature Events')}>
				<NomenclatureEventsForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Taxon')}>
				<TaxonDetailPageEditor
					taxon={state.allele?.taxon}
					onTaxonValueChange={onTaxonValueChange}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Mutation Types')}>
				<MutationTypesForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Functional Impacts')}>
				<FunctionalImpactsForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Germline Transmission Status')}>
				<GermilineTransmissionStatusForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Database Status')}>
				<DatabaseStatusForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Inheritance Modes')}>
				<InheritanceModesForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('References')}>
				<ReferencesForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('In Collection')}>
				<InCollectionDetailPageEditor
					inCollection={state.allele?.inCollection}
					onInCollectionValueChange={onInCollectionValueChange}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Is Extinct')}>
				<BooleanDetailPageEditor
					value={state.allele?.isExtinct}
					name={'isExtinct'}
					label={'Is Extinct'}
					onValueChange={onIsExtinctValueChange}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
					showClear={true}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Related Notes')}>
				<RelatedNotesForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Allele Gene Associations')}>
				<AlleleGeneAssociationsForm state={state} dispatch={dispatch} />
			</FormSection>

			<FormSection isVisible={isVisible('Data Provider')}>
				<DataProviderDetailPageTemplate
					dataProvider={state.allele?.dataProvider?.abbreviation}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Updated By')}>
				<UserDetailPageTemplate
					user={state.allele?.updatedBy?.uniqueId}
					fieldName="Updated By"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Date Updated')}>
				<DateDetailPageTemplate
					date={state.allele?.dateUpdated}
					fieldName="Date Updated"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Created By')}>
				<UserDetailPageTemplate
					user={state.allele?.createdBy?.uniqueId}
					fieldName="Created By"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Date Created')}>
				<DateDetailPageTemplate
					date={state.allele?.dateCreated}
					fieldName="Date Created"
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Internal')}>
				<BooleanDetailPageEditor
					value={state.allele?.internal}
					name={'internal'}
					label={'Internal'}
					onValueChange={onInternalValueChange}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>

			<FormSection isVisible={isVisible('Obsolete')}>
				<BooleanDetailPageEditor
					value={state.allele?.obsolete}
					name={'obsolete'}
					label={'Obsolete'}
					onValueChange={onObsoleteValueChange}
					widgetColumnSize={widgetColumnSize}
					labelColumnSize={labelColumnSize}
					fieldDetailsColumnSize={fieldDetailsColumnSize}
					errorMessages={state.errorMessages}
				/>
			</FormSection>
		</form>
	);
};
