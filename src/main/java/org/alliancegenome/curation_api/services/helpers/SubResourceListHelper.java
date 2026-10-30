package org.alliancegenome.curation_api.services.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.hibernate.Hibernate;

/**
 * Reads and replaces an entity's lists that are exposed as sub-resources of their own, such as an allele's synonyms.
 */
public class SubResourceListHelper {
	private SubResourceListHelper() {
		// Hidden from view, as it is a utility class
	}

	/**
	 * Builds the response to a read of one of an entity's lists.
	 *
	 * @param rows the list an entity holds, or null when it holds none
	 * @return a response holding a copy of the list, empty when there is none
	 */
	public static <T> ObjectListResponse<T> listResponse(List<T> rows) {
		return new ObjectListResponse<>(rows == null ? new ArrayList<>() : new ArrayList<>(rows));
	}

	/**
	 * Replaces one of an entity's lists with validated rows.
	 *
	 * @param owner the entity the list belongs to
	 * @param validatedRows the rows the list is to hold
	 * @param getList reads the list from the entity
	 * @param setList sets the list on the entity
	 * @return the rows the list holds
	 */
	public static <O, T> ObjectListResponse<T> replaceList(O owner, List<T> validatedRows, Function<O, List<T>> getList, BiConsumer<O, List<T>> setList) {
		if (getList.apply(owner) == null) {
			setList.accept(owner, new ArrayList<>());
		}
		List<T> rows = getList.apply(owner);
		rows.clear();
		rows.addAll(validatedRows);
		return listResponse(rows);
	}

	/**
	 * Replaces one of an entity's slot annotation lists with validated annotations. The response is serialized after
	 * the caller's transaction closes, so each evidence reference's cross references are loaded while it is open.
	 *
	 * @param owner the entity the annotations belong to
	 * @param validatedAnnotations the annotations the list is to hold, each assigned to the entity
	 * @param getList reads the list from the entity
	 * @param setList sets the list on the entity
	 * @return the annotations the list holds
	 */
	public static <O, S extends SlotAnnotation> ObjectListResponse<S> replaceSlotAnnotations(O owner, List<S> validatedAnnotations, Function<O, List<S>> getList, BiConsumer<O, List<S>> setList) {
		ObjectListResponse<S> response = replaceList(owner, validatedAnnotations, getList, setList);
		for (S annotation : validatedAnnotations) {
			if (annotation.getEvidence() != null) {
				annotation.getEvidence().forEach(evidence -> {
					if (evidence instanceof Reference reference) {
						Hibernate.initialize(reference.getCrossReferences());
					}
				});
			}
		}
		return response;
	}

	/**
	 * Replaces an entity's related notes with validated notes. The response is serialized after the caller's
	 * transaction closes, so each note reference's cross references are loaded while it is open.
	 *
	 * @param owner the entity the notes belong to
	 * @param validatedNotes the notes the entity is to hold
	 * @return the notes the entity holds
	 */
	public static <O extends SubmittedObject> ObjectListResponse<Note> replaceRelatedNotes(O owner, List<Note> validatedNotes) {
		ObjectListResponse<Note> response = replaceList(owner, validatedNotes, SubmittedObject::getRelatedNotes, SubmittedObject::setRelatedNotes);
		for (Note note : validatedNotes) {
			if (note.getReferences() != null) {
				note.getReferences().forEach(reference -> Hibernate.initialize(reference.getCrossReferences()));
			}
		}
		return response;
	}
}
