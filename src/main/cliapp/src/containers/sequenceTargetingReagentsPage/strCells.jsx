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
 * @returns {Object<string, Function>} a cell renderer per STR field, each taking the STR. The audit field cells
 *   (updatedBy, dateUpdated, createdBy, dateCreated, internal, obsolete) read only AuditedObject fields, so they
 *   also show any other audited object, such as the pop-up's cassette - STR association.
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
