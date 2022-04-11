package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.*;
import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.entity.BaseGeneratedEntity;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import io.quarkiverse.hibernate.types.json.*;
import lombok.*;

@Audited
@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"bulkLoadFileHistory", "exception"}, callSuper = true)
@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
public class BulkLoadFileException extends BaseGeneratedEntity {
    
    @Type(type = JsonTypes.JSON_BIN)
    @JsonView({View.BulkLoadFileHistory.class})
    @Column(columnDefinition = JsonTypes.JSON_BIN)
    private ObjectUpdateExceptionData exception;
    
    @ManyToOne
    private BulkLoadFileHistory bulkLoadFileHistory;
    
}
