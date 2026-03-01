package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.InformationContentEntityTypeBridge;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.TypeBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.TypeBinding;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@TypeBinding(binder = @TypeBinderRef(type = InformationContentEntityTypeBridge.class))
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "Reference", description = "ExternalDatabaseReference: an external database reference")
@AGRCurationSchemaVersion(min = "2.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {InformationContentEntity.class}, partial = true)
public class ExternalDatabaseReference extends InformationContentEntity {

}
