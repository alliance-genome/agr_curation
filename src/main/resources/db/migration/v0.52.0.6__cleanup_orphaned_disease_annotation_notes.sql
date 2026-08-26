-- SCRUM-6342: Clean up orphaned disease annotation notes.
-- These are internal disease_note rows left behind by an early-2023 load whose
-- owning disease annotations have since been deleted, so they're referenced by
-- none of the note-owner tables and are unreachable dead data.

DELETE FROM note_reference
WHERE note_id IN (
	SELECT n.id
	FROM note n
	JOIN vocabularyterm vt ON vt.id = n.notetype_id
	WHERE n.internal = true
		AND n.obsolete = false
		AND vt.name = 'disease_note'
		AND NOT EXISTS (SELECT 1 FROM alleleconstructassociation x WHERE x.relatednote_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM allelegeneassociation x WHERE x.relatednote_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM allelevariantassociation x WHERE x.relatednote_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM biologicalentity_note x WHERE x.relatednotes_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM constructgenomicentityassociation_note x WHERE x.relatednotes_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM diseaseannotation_note x WHERE x.relatednotes_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM slotannotation_note x WHERE x.relatednotes_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM cassetteassociation_note x WHERE x.relatednotes_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM geneexpressionannotation_note x WHERE x.relatednotes_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM geneexpressionexperiment_note x WHERE x.relatednotes_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM htpexpressiondatasetsampleannotation_note x WHERE x.relatednotes_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM htpexpressiondatasetannotation x WHERE x.relatednote_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM phenotypeannotation_note x WHERE x.relatednotes_id = n.id)
		AND NOT EXISTS (SELECT 1 FROM reagent_note x WHERE x.relatednotes_id = n.id)
);

DELETE FROM note n
USING vocabularyterm vt
WHERE vt.id = n.notetype_id
	AND n.internal = true
	AND n.obsolete = false
	AND vt.name = 'disease_note'
	AND NOT EXISTS (SELECT 1 FROM alleleconstructassociation x WHERE x.relatednote_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM allelegeneassociation x WHERE x.relatednote_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM allelevariantassociation x WHERE x.relatednote_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM biologicalentity_note x WHERE x.relatednotes_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM constructgenomicentityassociation_note x WHERE x.relatednotes_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM diseaseannotation_note x WHERE x.relatednotes_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM slotannotation_note x WHERE x.relatednotes_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM cassetteassociation_note x WHERE x.relatednotes_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM geneexpressionannotation_note x WHERE x.relatednotes_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM geneexpressionexperiment_note x WHERE x.relatednotes_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM htpexpressiondatasetsampleannotation_note x WHERE x.relatednotes_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM htpexpressiondatasetannotation x WHERE x.relatednote_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM phenotypeannotation_note x WHERE x.relatednotes_id = n.id)
	AND NOT EXISTS (SELECT 1 FROM reagent_note x WHERE x.relatednotes_id = n.id);
