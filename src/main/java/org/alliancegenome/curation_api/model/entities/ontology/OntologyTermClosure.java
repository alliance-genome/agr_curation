package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(
	indexes = {
		@Index(name = "ontologyclosure_closureSubject_index", columnList = "closureSubject_id"),
		@Index(name = "ontologyclosure_closureObject_index", columnList = "closureObject_id")
	}
)
public class OntologyTermClosure extends AuditedObject {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected Long id;
	
	private Integer distance;
	
	@JdbcTypeCode(SqlTypes.JSON)
	private Set<String> closureTypes = new HashSet<>();

	@ManyToOne
	private OntologyTerm closureSubject;
	
	@ManyToOne
	private OntologyTerm closureObject;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
			return true;
		}
        if (!(o instanceof OntologyTermClosure)) {
			return false;
		}
        OntologyTermClosure pair = (OntologyTermClosure) o;
        return Objects.equals(closureSubject, pair.closureSubject) &&
               Objects.equals(closureObject, pair.closureObject) &&
               Objects.equals(closureTypes, pair.closureTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(closureSubject, closureObject, closureTypes);
    }
	
}
