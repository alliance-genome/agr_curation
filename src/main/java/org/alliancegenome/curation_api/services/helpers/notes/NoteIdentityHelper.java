package org.alliancegenome.curation_api.services.helpers.notes;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

@RequestScoped
public class NoteIdentityHelper {
	
	public static String noteDtoIdentity(NoteDTO note) {
		String identity = StringUtils.isBlank(note.getFreeText()) ? note.getFreeText() : "";
		List<String> evidenceCuries = note.getEvidenceCuries();
		if (CollectionUtils.isNotEmpty(evidenceCuries)) {
			Collections.sort(evidenceCuries);
			identity = identity + "|" + StringUtils.join(evidenceCuries, ":");
		}
		if (note.getNoteTypeName() != null)
			identity = identity + "|" + note.getNoteTypeName();
		if (note.getFreeText() != null)
			identity = identity + "|" + note.getFreeText();
		if (note.getInternal() != null)
			identity = identity + "|" + note.getInternal().toString();
		if (note.getObsolete() != null)
			identity = identity + "|" + note.getObsolete().toString();
		
		return identity;
	}
}
