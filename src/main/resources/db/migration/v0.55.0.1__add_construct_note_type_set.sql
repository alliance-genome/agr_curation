INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Construct Note Type', 'construct_note_type', v.id, 'Note types applicable to constructs'
	FROM vocabulary v
	WHERE v.vocabularylabel = 'note_type'
	AND NOT EXISTS (SELECT 1 FROM vocabularytermset existing WHERE existing.vocabularylabel = 'construct_note_type');

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT vts.id, vt.id
	FROM (VALUES ('summary'), ('comment'), ('internal_note')) AS m(term_name)
	JOIN vocabularytermset vts ON vts.vocabularylabel = 'construct_note_type'
	JOIN vocabulary v ON v.vocabularylabel = 'note_type'
	JOIN vocabularyterm vt ON vt.name = m.term_name AND vt.vocabulary_id = v.id
	WHERE NOT EXISTS (
		SELECT 1 FROM vocabularytermset_vocabularyterm existing
		WHERE existing.vocabularytermsets_id = vts.id AND existing.memberterms_id = vt.id);
