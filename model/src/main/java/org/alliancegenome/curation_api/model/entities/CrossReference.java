package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.CurieAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"pageAreas"})
@Schema(name="Cross Reference", description="POJO that represents the Cross Reference")
@AGRCurationSchemaVersion(min="1.2.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={AuditedObject.class})
@Table(indexes = {
	@Index(name = "crossreference_createdby_index", columnList = "createdBy_id"),
	@Index(name = "crossreference_updatedby_index", columnList = "updatedBy_id")
})
public class CrossReference extends CurieAuditedObject {

	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@ElementCollection
	@JoinTable(indexes = @Index( columnList = "crossreference_curie"))
	@JsonView({View.FieldsAndLists.class})
	private List<String> pageAreas;
	
	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@JsonView({View.FieldsOnly.class})
	private String displayName;
	
	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@JsonView({View.FieldsOnly.class})
	private String prefix;
	
}
