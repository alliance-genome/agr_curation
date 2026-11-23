import React, { useEffect, useMemo, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { evidenceTemplate } from '../../components/EvidenceComponent';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { SearchService } from '../../service/SearchService';
import { Endpoints } from '../../constants/Endpoints';
import { TargetedGenesDialog } from '../sequenceTargetingReagentsPage/TargetedGenesDialog';
import { strCells } from '../sequenceTargetingReagentsPage/strCells';
import { relationName, strFindParams } from './utils';

// The STR's columns, each named for the STR so it is clear which object the value belongs to. The STR's
// references, data provider and audit fields are left out; the association's audit fields are shown instead.
const STR_POPUP_COLUMNS = [
	{ key: 'curie', header: 'STR Curie' },
	{ key: 'primaryExternalId', header: 'STR Primary External ID' },
	{ key: 'modInternalId', header: 'STR MOD Internal ID' },
	{ key: 'name', header: 'STR Name' },
	{ key: 'synonyms', header: 'STR Synonyms' },
	{ key: 'secondaryIdentifiers', header: 'STR Secondary IDs' },
	{ key: 'crossReferences', header: 'STR Cross References' },
	{ key: 'taxon', header: 'STR Taxon' },
	{ key: 'targetedGenes', header: 'STR Targeted Genes' },
	{ key: 'relatedNotes', header: 'STR Related Notes' },
];

// The cassette - STR association's own audit fields, shown with the same cells as an STR's
const ASSOCIATION_AUDIT_COLUMNS = [
	{ key: 'updatedBy', header: 'Association Updated By' },
	{ key: 'dateUpdated', header: 'Association Date Updated' },
	{ key: 'createdBy', header: 'Association Created By' },
	{ key: 'dateCreated', header: 'Association Date Created' },
	{ key: 'internal', header: 'Association Internal' },
	{ key: 'obsolete', header: 'Association Obsolete' },
];

/**
 * The Sequence Targeting Reagent components of one cassette, read-only (SCRUM-6758): each cassette -
 * STR association's relation, then its STR as the STR table shows it, then the association's evidence,
 * notes and audit fields.
 *
 * The cassette row carries only the STR's own fields, so each STR (a cassette has one or two) is
 * fetched in the STR table's view, with its cross references and targeted genes. Until it arrives,
 * the row's STR is shown.
 */
export const StrComponentsDialog = ({ strComponentsData, setStrComponentsData }) => {
	const { originalComponents, dialog } = strComponentsData;
	const searchService = useMemo(() => new SearchService(), []);
	const [strsById, setStrsById] = useState({});
	const [targetedGenesData, setTargetedGenesData] = useState({ dialog: false });
	const [relatedNotesData, setRelatedNotesData] = useState({
		originalRelatedNotes: [],
		dialog: false,
		isInEdit: false,
	});

	useEffect(() => {
		if (!dialog) return;
		const ids = [
			...new Set((originalComponents || []).map((association) => association?.cassetteStrAssociationObject?.id)),
		].filter(Boolean);
		let cancelled = false;
		Promise.all(
			ids.map((id) =>
				searchService
					.find(Endpoints.Entity.SEQUENCE_TARGETING_REAGENT, 1, 0, strFindParams(id))
					.then((data) => data?.results?.[0])
					.catch(() => undefined)
			)
		).then((strs) => {
			if (!cancelled) setStrsById(Object.fromEntries(strs.filter(Boolean).map((str) => [str.id, str])));
		});
		return () => {
			cancelled = true;
		};
	}, [dialog, originalComponents, searchService]);

	const hideDialog = () => {
		setStrComponentsData((strComponentsData) => ({ ...strComponentsData, dialog: false }));
		setStrsById({});
	};

	// A row per association with its STR, so that a row changes when its STR arrives: the table memoizes
	// its cells by row
	const rows = useMemo(
		() =>
			(originalComponents || []).map((association) => ({
				association,
				str: strsById[association?.cassetteStrAssociationObject?.id] || association?.cassetteStrAssociationObject,
			})),
		[originalComponents, strsById]
	);

	const handleRelatedNotesOpen = (relatedNotes) =>
		setRelatedNotesData({ originalRelatedNotes: relatedNotes, dialog: true, isInEdit: false });
	const handleTargetedGenesOpen = (sequenceTargetingReagent) =>
		setTargetedGenesData({ sequenceTargetingReagent, dialog: true });

	const cells = strCells({ handleTargetedGenesOpen, handleRelatedNotesOpen });

	return (
		<>
			<Dialog visible={dialog} className="w-11" modal onHide={hideDialog} closable={true}>
				<h3>Sequence Targeting Reagent Components</h3>
				<DataTable value={rows} dataKey="association.id" showGridlines scrollable>
					<Column
						header="Relation"
						headerClassName="surface-0"
						body={({ association }) => <StringTemplate string={relationName(association)} />}
					/>
					{STR_POPUP_COLUMNS.map(({ key, header }) => (
						<Column key={key} header={header} headerClassName="surface-0" body={({ str }) => cells[key](str)} />
					))}
					<Column
						header="Association Evidence"
						headerClassName="surface-0"
						body={({ association }) => evidenceTemplate(association)}
					/>
					<Column
						header="Component Related Notes"
						headerClassName="surface-0"
						body={({ association }) => (
							<CountDialogTemplate
								entities={association?.relatedNotes}
								handleOpen={handleRelatedNotesOpen}
								text={'Notes'}
							/>
						)}
					/>
					{ASSOCIATION_AUDIT_COLUMNS.map(({ key, header }) => (
						<Column
							key={key}
							header={header}
							headerClassName="surface-0"
							body={({ association }) => cells[key](association)}
						/>
					))}
				</DataTable>
			</Dialog>
			<TargetedGenesDialog targetedGenesData={targetedGenesData} setTargetedGenesData={setTargetedGenesData} />
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
			/>
		</>
	);
};
