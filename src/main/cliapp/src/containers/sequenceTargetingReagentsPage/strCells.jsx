import React from 'react';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { TruncatedReferencesTemplate } from '../../components/Templates/reference/TruncatedReferencesTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { TargetedGenesTemplate } from './TargetedGenesTemplate';

/**
 * The cells of a Sequence Targeting Reagent as the STR table shows them, so that other places that
 * show an STR (the Cassettes table's STR Components pop-up, SCRUM-6758) show it the same way.
 *
 * @param {Function} handleTargetedGenesOpen - opens the paged targeted genes of an STR
 * @param {Function} handleRelatedNotesOpen - opens the read-only notes of an STR
 * @returns {Object<string, Function>} a cell renderer per STR field, each taking the STR
 */
export const strCells = ({ handleTargetedGenesOpen, handleRelatedNotesOpen }) => ({
	curie: (str) => <IdTemplate id={str?.curie} />,
	primaryExternalId: (str) => <IdTemplate id={str?.primaryExternalId} />,
	modInternalId: (str) => <IdTemplate id={str?.modInternalId} />,
	name: (str) => <StringTemplate string={str?.name} />,
	synonyms: (str) => <StringListTemplate list={str?.synonyms} />,
	secondaryIdentifiers: (str) => <StringListTemplate list={str?.secondaryIdentifiers} />,
	crossReferences: (str) => <CrossReferencesTemplate list={str?.crossReferences} />,
	taxon: (str) => <OntologyTermTemplate term={str?.taxon} />,
	targetedGenes: (str) => <TargetedGenesTemplate sequenceTargetingReagent={str} handleOpen={handleTargetedGenesOpen} />,
	relatedNotes: (str) => (
		<CountDialogTemplate entities={str?.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
	),
	references: (str) => <TruncatedReferencesTemplate references={str?.references} identifier={str?.primaryExternalId} />,
	dataProvider: (str) => <StringTemplate string={str?.dataProvider?.abbreviation} />,
	updatedBy: (str) => <StringTemplate string={str?.updatedBy?.uniqueId} />,
	dateUpdated: (str) => <StringTemplate string={str?.dateUpdated} />,
	createdBy: (str) => <StringTemplate string={str?.createdBy?.uniqueId} />,
	dateCreated: (str) => <StringTemplate string={str?.dateCreated} />,
	internal: (str) => <BooleanTemplate value={str?.internal} />,
	obsolete: (str) => <BooleanTemplate value={str?.obsolete} />,
});

// The STR columns, in the STR table's order, for tables that show STRs alongside other data
export const STR_COLUMNS = [
	{ key: 'curie', header: 'Curie' },
	{ key: 'primaryExternalId', header: 'Primary External ID' },
	{ key: 'modInternalId', header: 'MOD Internal ID' },
	{ key: 'name', header: 'Name' },
	{ key: 'synonyms', header: 'Synonyms' },
	{ key: 'secondaryIdentifiers', header: 'Secondary IDs' },
	{ key: 'crossReferences', header: 'Cross References' },
	{ key: 'taxon', header: 'Taxon' },
	{ key: 'targetedGenes', header: 'Targeted Genes' },
	{ key: 'relatedNotes', header: 'STR Related Notes' },
	{ key: 'references', header: 'References' },
	{ key: 'dataProvider', header: 'Data Provider' },
	{ key: 'updatedBy', header: 'Updated By' },
	{ key: 'dateUpdated', header: 'Date Updated' },
	{ key: 'createdBy', header: 'Created By' },
	{ key: 'dateCreated', header: 'Date Created' },
	{ key: 'internal', header: 'Internal' },
	{ key: 'obsolete', header: 'Obsolete' },
];
