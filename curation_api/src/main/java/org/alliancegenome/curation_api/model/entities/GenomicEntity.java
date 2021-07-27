package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"synonyms", "crossReferences", "secondaryIdentifiers"})
public class GenomicEntity extends BaseEntity {


}
