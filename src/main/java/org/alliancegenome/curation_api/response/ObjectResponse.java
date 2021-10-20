package org.alliancegenome.curation_api.response;

import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@Schema(name="ObjectResponse", description="POJO that represents the ObjectResponse")
public class ObjectResponse<E> extends APIResponse {

    @JsonView(View.FieldsOnly.class)
    private E entity;
    
    public ObjectResponse(E object) {
        this.entity = object;
    }

}
