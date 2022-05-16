package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.entity.UniqueIdAuditedObject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public class Agent extends UniqueIdAuditedObject {

}
