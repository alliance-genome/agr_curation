package org.alliancegenome.curation_api.services;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class NoteService extends BaseCrudService<Note, NoteDAO> {

    @Inject
    NoteDAO noteDAO;
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    @Inject
    ReferenceDAO referenceDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(noteDAO);
    }
    
    
    public Note validateNoteDTO(NoteDTO dto, String note_type_vocabulary) throws ObjectValidationException {
        Note note = new Note();
        
        if (dto.getFreeText() == null || dto.getNoteType() == null || dto.getInternal() == null) {
            throw new ObjectValidationException(dto, "Note missing required fields");
        }
        note.setFreeText(dto.getFreeText());
        note.setInternal(dto.getInternal());
        
        VocabularyTerm noteType = vocabularyTermDAO.getTermInVocabulary(dto.getNoteType(), note_type_vocabulary);
        if (noteType == null) {
            throw new ObjectValidationException(dto, "Note type '" + dto.getNoteType() + "' not found in vocabulary '" + note_type_vocabulary + "'");
        }
        note.setNoteType(noteType);
        
        List<Reference> noteReferences = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getReferences())) {
            for (String publicationId : dto.getReferences()) {
                Reference reference = referenceDAO.find(publicationId);
                if (reference == null) {
                    reference = new Reference();
                    reference.setCurie(publicationId);
                    //log("Reference: " + reference.toString());
                    // ToDo: need this until references are loaded separately
                    // raise an error when reference cannot be found?
                    referenceDAO.persist(reference);
                }
                noteReferences.add(reference);
            }
            note.setReferences(noteReferences);
        }
        
        return note;
    }
    
}
