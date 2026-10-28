import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { AutocompleteSingleTableEditor } from '../../../components/Editors/autocomplete/base/AutocompleteSingleTableEditor';
import { AutocompleteMultiTableEditor } from '../../../components/Editors/autocomplete/base/AutocompleteMultiTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { TaxonTableEditor } from '../../../components/Editors/autocomplete/taxon/TaxonTableEditor';
import { InputTextTableEditor } from '../../../components/Editors/text/InputTextTableEditor';
import { DialogTriggerTableEditor } from '../../../components/Editors/dialog/DialogTriggerTableEditor';
import { alleleGeneObjectSearchConfig } from '../../../components/Editors/autocomplete/gene/utils';
import { Endpoints } from '../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../constants/FilterFields';
import { genomicEntityLabel, identifierOf, transgenicToolDisplayValue } from '../utils';

// The configuration of each of a cassette's component pop-ups (SCRUM-6752), for ComponentsEditDialog. Relations and
// note types come from the term sets the cassette load validates against.

const labelled = (label) => (entity) => {
	const identifier = identifierOf(entity);
	const text = label(entity);
	return text && text !== identifier ? `${text} (${identifier})` : identifier;
};

const relationColumn = ({ relationTerms }) => ({
	field: 'relation',
	header: 'Relation',
	editor: (editorOptions) => (
		<ControlledVocabularyTableEditor
			editorOptions={editorOptions}
			field="relation"
			options={relationTerms}
			dataKey="id"
			showClear={false}
		/>
	),
});

const objectColumn = (field, header, searchConfig, toDisplay) => ({
	field,
	header,
	editor: (editorOptions) => (
		<AutocompleteSingleTableEditor
			editorOptions={editorOptions}
			field={field}
			subField="primaryExternalId"
			// A typed entity has no id, and shows what was typed
			toDisplay={(entity) => (entity?.id ? toDisplay(entity) : entity?.primaryExternalId ?? '')}
			{...searchConfig}
		/>
	),
});

const evidenceColumn = () => ({
	field: 'evidence',
	header: 'Evidence',
	editor: (editorOptions) => <EvidenceTableEditor editorOptions={editorOptions} />,
});

const relatedNotesColumn = ({ openRelatedNotes }) => ({
	field: 'relatedNotes',
	header: 'Related Notes',
	editor: (editorOptions) => {
		const count = editorOptions.rowData?.relatedNotes?.length;
		return (
			<DialogTriggerTableEditor
				editorOptions={editorOptions}
				onOpenInEdit={openRelatedNotes}
				errorField="relatedNotes"
				displayText={count ? `Notes(${count}) ` : null}
				addText="Add Note"
				tooltipObject="component"
			/>
		);
	},
});

const newAssociation = (objectField) => () => ({
	relation: null,
	[objectField]: null,
	evidence: [],
	relatedNotes: [],
	internal: false,
	obsolete: false,
});

const entityValueDisplay = (toDisplay) => (item) => <div>{toDisplay(item)}</div>;

const transgenicToolSearchConfig = {
	endpoint: Endpoints.Entity.TRANSGENIC_TOOL,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.transgenicToolAutocompleteConfig),
	filterName: 'cassetteTransgenicToolFilter',
	valueDisplay: entityValueDisplay(transgenicToolDisplayValue),
};

const strLabel = labelled((str) => str?.name);

const strSearchConfig = {
	endpoint: Endpoints.Entity.SEQUENCE_TARGETING_REAGENT,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.sequenceTargetingReagentAutocompleteConfig),
	filterName: 'cassetteStrFilter',
	valueDisplay: entityValueDisplay(strLabel),
};

const componentTypeSearchConfig = {
	endpoint: Endpoints.Ontology.SO,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig),
	filterName: 'componentTypesFilter',
};

export const FREE_TEXT_COMPONENTS = {
	title: 'Free Text Components',
	list: 'components',
	field: 'cassetteComponents',
	relationTermSet: 'cassette_free_text_component_relation',
	noteTypeVocabularyTermSet: 'cassette_component_note_type',
	newRow: () => ({
		relation: null,
		componentSymbol: '',
		taxon: null,
		taxonText: '',
		relatedNotes: [],
		internal: false,
	}),
	columns: (context) => [
		relationColumn(context),
		{
			field: 'componentSymbol',
			header: 'Component Symbol',
			editor: (editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="componentSymbol" />,
		},
		{ field: 'taxon', header: 'Taxon', editor: (editorOptions) => <TaxonTableEditor editorOptions={editorOptions} /> },
		{
			field: 'taxonText',
			header: 'Taxon Text',
			editor: (editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="taxonText" />,
		},
		relatedNotesColumn(context),
	],
};

export const GENOMIC_ENTITY_COMPONENTS = {
	title: 'Genomic Entity Components',
	list: 'genomic-entity-associations',
	field: 'cassetteGenomicEntityAssociations',
	objectField: 'cassetteGenomicEntityAssociationObject',
	relationTermSet: 'cassette_genomic_entity_relation',
	noteTypeVocabularyTermSet: 'cassette_genomic_entity_association_note_type',
	newRow: () => ({ ...newAssociation('cassetteGenomicEntityAssociationObject')(), componentTypes: [] }),
	columns: (context) => [
		relationColumn(context),
		objectColumn(
			'cassetteGenomicEntityAssociationObject',
			'Genomic Entity',
			alleleGeneObjectSearchConfig,
			labelled(genomicEntityLabel)
		),
		{
			field: 'componentTypes',
			header: 'Component Types',
			editor: (editorOptions) => (
				<AutocompleteMultiTableEditor
					editorOptions={editorOptions}
					field="componentTypes"
					{...componentTypeSearchConfig}
				/>
			),
		},
		evidenceColumn(),
		relatedNotesColumn(context),
	],
};

export const TRANSGENIC_TOOL_COMPONENTS = {
	title: 'Transgenic Tool Components',
	list: 'transgenic-tool-associations',
	field: 'cassetteTransgenicToolAssociations',
	objectField: 'cassetteTransgenicToolAssociationObject',
	relationTermSet: 'cassette_transgenic_tool_relation',
	noteTypeVocabularyTermSet: 'cassette_transgenic_tool_association',
	newRow: newAssociation('cassetteTransgenicToolAssociationObject'),
	columns: (context) => [
		relationColumn(context),
		objectColumn(
			'cassetteTransgenicToolAssociationObject',
			'Transgenic Tool',
			transgenicToolSearchConfig,
			transgenicToolDisplayValue
		),
		evidenceColumn(),
		relatedNotesColumn(context),
	],
};

export const STR_COMPONENTS = {
	title: 'Sequence Targeting Reagent Components',
	list: 'str-associations',
	field: 'cassetteStrAssociations',
	objectField: 'cassetteStrAssociationObject',
	relationTermSet: 'cassette_str_relation',
	noteTypeVocabularyTermSet: 'cassette_str_association_note_type',
	newRow: newAssociation('cassetteStrAssociationObject'),
	columns: (context) => [
		relationColumn(context),
		objectColumn('cassetteStrAssociationObject', 'Sequence Targeting Reagent', strSearchConfig, strLabel),
		evidenceColumn(),
		relatedNotesColumn(context),
	],
};
