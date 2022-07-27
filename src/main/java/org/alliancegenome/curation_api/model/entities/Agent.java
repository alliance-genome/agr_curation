package org.alliancegenome.curation_api.model.entities;

import javax.persistence.MappedSuperclass;

import org.alliancegenome.curation_api.model.entities.base.UniqueIdAuditedObject;

import lombok.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public class Agent extends UniqueIdAuditedObject {

}
