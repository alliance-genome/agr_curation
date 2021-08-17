package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Audited
@Indexed
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"affected_Genomic_Model"})
public class AffectedGenomicModel extends GenomicEntity {

}

