-- SCRUM-6535: the relation vocabularies and term sets the new Constructs model validates against,
-- following Chris Grove's layout on the ticket (comment of 2026-09-18).
--
-- Chris asked for the two existing vocabularies to be repurposed: "Construct Relation" renamed to
-- "Cassette Relation", and "New Construct Relation" renamed to "Construct Relation". This migration
-- deliberately does neither. construct_relation is what 236,981 existing constructs validate their
-- components and genomic entity associations against, through construct_genomic_entity_relation;
-- renaming it, or hanging the cassette sets off it, couples the new model to data the old one still
-- depends on. New vocabularies with their own terms keep the two models independent, at the cost of
-- a handful of duplicated term names.
--
--   cassette_relation                    Cassette Relation
--   construct_cassette_relation          Construct Cassette Relation
--   transgenic_tool_association_relation Transgenic Tool Association Relation
--
-- Term sets, and the memberships Chris specified:
--
--   cassette_free_text_component_relation  expresses, is_regulated_by, targets, contains
--   cassette_genomic_entity_relation       expresses, is_regulated_by, targets, contains
--   cassette_transgenic_tool_relation      expresses, is_regulated_by, targets, tagged_with, contains
--   construct_cassette_relation            has_component, has_functional_unit,
--                                          has_selectable_marker, has_transcriptional_unit
--   cassette_str_relation                  expresses
--
-- Two departures from the comment, both flagged on the ticket:
--
--   * expresses_3'_UTR_of is not in Chris's list either, but 1,177 construct genomic entity
--     associations use it, all WormBase, across 1,160 constructs. It is a term of the existing
--     construct_relation vocabulary, so its absence reads as an oversight rather than a
--     retirement. It is added to cassette_relation and to cassette_genomic_entity_relation only:
--     no component, transgenic tool or STR record uses it. Carrying the term preserves a
--     distinction that collapsing it into expresses would destroy irreversibly; if the model does
--     intend expresses to subsume it, the remapping belongs in the data migration, with WormBase's
--     agreement, not here.
--
--   * cassette_str_relation is not in Chris's list, but the FlyBase 2026_03 submission carries
--     64,511 cassette_str_association records, every one of them using expresses. Without the set
--     that load fails outright, so it is created here in the same shape as its siblings.
--
--   * Chris writes the transgenic tool relation as is_compatible_with. The existing
--     transgenic_tool_relation vocabulary holds compatible_with, and FlyBase submits
--     compatible_tool - three spellings for one relation. is_compatible_with is used here as the
--     stated intent; nothing consumes it yet, since TransgenicToolTransgenicToolAssociation is not
--     implemented, so correcting it later costs nothing.
--
-- Guarded on NOT EXISTS throughout, so it is a no-op for anything already present.

-- ---------------------------------------------------------------------------------------------
-- Vocabularies
-- ---------------------------------------------------------------------------------------------

INSERT INTO vocabulary (id, name, vocabularylabel, vocabularydescription)
	SELECT nextval('vocabulary_seq'), v.name, v.label, v.description
	FROM (VALUES
		('Cassette Relation',                    'cassette_relation',                    'Relations between a cassette and its components'),
		('Construct Cassette Relation',          'construct_cassette_relation',          'Relations between a construct and the cassettes that are part of it'),
		('Transgenic Tool Association Relation', 'transgenic_tool_association_relation', 'Relations between two compatible transgenic tools')
	) AS v(name, label, description)
	WHERE NOT EXISTS (SELECT 1 FROM vocabulary existing WHERE existing.vocabularylabel = v.label);

-- ---------------------------------------------------------------------------------------------
-- Terms
-- ---------------------------------------------------------------------------------------------

INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('vocabularyterm_seq'), t.term, v.id
	FROM (VALUES
		('cassette_relation',                    'expresses'),
		('cassette_relation',                    'is_regulated_by'),
		('cassette_relation',                    'targets'),
		('cassette_relation',                    'contains'),
		('cassette_relation',                    'tagged_with'),
		('cassette_relation',                    'expresses_3''_UTR_of'),
		('construct_cassette_relation',          'has_component'),
		('construct_cassette_relation',          'has_functional_unit'),
		('construct_cassette_relation',          'has_selectable_marker'),
		('construct_cassette_relation',          'has_transcriptional_unit'),
		('transgenic_tool_association_relation', 'is_compatible_with')
	) AS t(vocab_label, term)
	JOIN vocabulary v ON v.vocabularylabel = t.vocab_label
	WHERE NOT EXISTS (
		SELECT 1 FROM vocabularyterm existing
		WHERE existing.vocabulary_id = v.id AND existing.name = t.term);

-- ---------------------------------------------------------------------------------------------
-- Term sets
-- ---------------------------------------------------------------------------------------------

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), s.set_name, s.set_label, v.id, s.set_description
	FROM (VALUES
		('Cassette Free Text Component Relation',        'cassette_free_text_component_relation', 'cassette_relation',                    'Relations between a cassette and a component named only by symbol'),
		('Cassette Genomic Entity Association Relation', 'cassette_genomic_entity_relation',      'cassette_relation',                    'Relations between a cassette and a genomic entity component'),
		('Cassette Transgenic Tool Association Relation', 'cassette_transgenic_tool_relation',    'cassette_relation',                    'Relations between a cassette and a transgenic tool component'),
		('Cassette STR Association Relation',            'cassette_str_relation',                 'cassette_relation',                    'Relations between a cassette and a sequence targeting reagent component'),
		('Construct Cassette Association Relation',      'construct_cassette_relation',           'construct_cassette_relation',          'Relations between a construct and the cassettes that are part of it'),
		('Transgenic Tool Association Relation',         'transgenic_tool_association_relation',  'transgenic_tool_association_relation',  'Relations between two compatible transgenic tools')
	) AS s(set_name, set_label, on_vocabulary, set_description)
	JOIN vocabulary v ON v.vocabularylabel = s.on_vocabulary
	WHERE NOT EXISTS (SELECT 1 FROM vocabularytermset existing WHERE existing.vocabularylabel = s.set_label);

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT vts.id, vt.id
	FROM (VALUES
		('cassette_free_text_component_relation', 'cassette_relation',                    'expresses'),
		('cassette_free_text_component_relation', 'cassette_relation',                    'is_regulated_by'),
		('cassette_free_text_component_relation', 'cassette_relation',                    'targets'),
		('cassette_free_text_component_relation', 'cassette_relation',                    'contains'),
		('cassette_genomic_entity_relation',      'cassette_relation',                    'expresses'),
		('cassette_genomic_entity_relation',      'cassette_relation',                    'is_regulated_by'),
		('cassette_genomic_entity_relation',      'cassette_relation',                    'targets'),
		('cassette_genomic_entity_relation',      'cassette_relation',                    'contains'),
		('cassette_genomic_entity_relation',      'cassette_relation',                    'expresses_3''_UTR_of'),
		('cassette_transgenic_tool_relation',     'cassette_relation',                    'expresses'),
		('cassette_transgenic_tool_relation',     'cassette_relation',                    'is_regulated_by'),
		('cassette_transgenic_tool_relation',     'cassette_relation',                    'targets'),
		('cassette_transgenic_tool_relation',     'cassette_relation',                    'tagged_with'),
		('cassette_transgenic_tool_relation',     'cassette_relation',                    'contains'),
		('cassette_str_relation',                 'cassette_relation',                    'expresses'),
		('construct_cassette_relation',           'construct_cassette_relation',          'has_component'),
		('construct_cassette_relation',           'construct_cassette_relation',          'has_functional_unit'),
		('construct_cassette_relation',           'construct_cassette_relation',          'has_selectable_marker'),
		('construct_cassette_relation',           'construct_cassette_relation',          'has_transcriptional_unit'),
		('transgenic_tool_association_relation',  'transgenic_tool_association_relation', 'is_compatible_with')
	) AS m(set_label, vocab_label, term_name)
	JOIN vocabularytermset vts ON vts.vocabularylabel = m.set_label
	JOIN vocabulary v ON v.vocabularylabel = m.vocab_label
	JOIN vocabularyterm vt ON vt.name = m.term_name AND vt.vocabulary_id = v.id
	WHERE NOT EXISTS (
		SELECT 1 FROM vocabularytermset_vocabularyterm existing
		WHERE existing.vocabularytermsets_id = vts.id AND existing.memberterms_id = vt.id);
