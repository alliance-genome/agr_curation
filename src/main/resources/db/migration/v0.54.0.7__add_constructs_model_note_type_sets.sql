-- SCRUM-6535: the note type vocabulary term sets the new Constructs model validates against.
--
-- These five sets already exist on alpha, but only because curators created them through the
-- curation UI; no migration in this repo ever created them. Every other environment - a fresh
-- developer database, beta, production, and the database each integration test run builds from
-- these migrations - therefore has none of them, and validateNotes rejects every record whose note
-- type it cannot resolve. That is what failed the SCRUM-6535 integration tests.
--
-- The labels below are the ones alpha already carries, deliberately, so this migration adds only
-- what is missing rather than a parallel set under names invented here. Two consequences:
--
--   * cassette_transgenic_tool_association has no _note_type suffix, unlike its four siblings.
--     That looks like a slip when it was created - its name is "Cassette Transgenic Tool
--     Association Note Type" - but matching it is what keeps the lookup working against existing
--     data. Correcting it means renaming the set on alpha and the constant together, not here.
--
--   * There is no cassette_note_type. alpha names one set "Cassette & Transgenic Tool Note Type"
--     under the label transgenic_tool_note_type and uses it for both entities, which is why it is
--     the only one carrying more than a single member term.
--
-- cassette_str_association_note_type is the one set with no counterpart on alpha; it is created
-- here in the same shape as its siblings.
--
-- Every statement is guarded on NOT EXISTS so this is a no-op for whatever an environment already
-- has, and safe to run against alpha.

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), s.set_name, s.set_label, v.id, s.set_description
	FROM (VALUES
		('Cassette & Transgenic Tool Note Type',          'transgenic_tool_note_type',                     'Note types applicable to cassettes and transgenic tools'),
		('Cassette Component Note Type',                  'cassette_component_note_type',                  'Note types applicable to cassette components'),
		('Cassette Genomic Entity Association Note Type', 'cassette_genomic_entity_association_note_type', 'Note types applicable to cassette genomic entity associations'),
		('Cassette Transgenic Tool Association Note Type', 'cassette_transgenic_tool_association',         'Note types applicable to cassette transgenic tool associations'),
		('Cassette STR Association Note Type',            'cassette_str_association_note_type',            'Note types applicable to cassette sequence targeting reagent associations'),
		('Construct Cassette Association Note Type',      'construct_cassette_association_note_type',      'Note types applicable to construct cassette associations')
	) AS s(set_name, set_label, set_description)
	CROSS JOIN vocabulary v
	WHERE v.vocabularylabel = 'note_type'
	AND NOT EXISTS (SELECT 1 FROM vocabularytermset existing WHERE existing.vocabularylabel = s.set_label);

-- Member terms. 'summary' is what alpha gives every one of these sets; the shared cassette and
-- transgenic tool set additionally carries 'comment' and 'internal_note' there, so it gets the
-- same three here. A set with no member terms resolves but accepts nothing, which fails a load
-- just as an absent set does.
INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT vts.id, vt.id
	FROM (VALUES
		('transgenic_tool_note_type',                     'summary'),
		('transgenic_tool_note_type',                     'comment'),
		('transgenic_tool_note_type',                     'internal_note'),
		('cassette_component_note_type',                  'summary'),
		('cassette_genomic_entity_association_note_type', 'summary'),
		('cassette_transgenic_tool_association',          'summary'),
		('cassette_str_association_note_type',            'summary'),
		('construct_cassette_association_note_type',      'summary')
	) AS m(set_label, term_name)
	JOIN vocabularytermset vts ON vts.vocabularylabel = m.set_label
	JOIN vocabulary v ON v.vocabularylabel = 'note_type'
	JOIN vocabularyterm vt ON vt.name = m.term_name AND vt.vocabulary_id = v.id
	WHERE NOT EXISTS (
		SELECT 1 FROM vocabularytermset_vocabularyterm existing
		WHERE existing.vocabularytermsets_id = vts.id AND existing.memberterms_id = vt.id);
