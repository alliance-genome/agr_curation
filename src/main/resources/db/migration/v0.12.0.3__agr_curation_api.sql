DELETE from note
	WHERE id NOT IN (
		SELECT relatednotes_id FROM diseaseannotation_note
	);
