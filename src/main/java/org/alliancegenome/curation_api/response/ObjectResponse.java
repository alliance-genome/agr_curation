package org.alliancegenome.curation_api.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(name="ObjectResponse", description="POJO that represents the ObjectResponse")
public class ObjectResponse<E> extends APIResponse {

    private E object;
    
    public ObjectResponse(E object) {
        this.object = object;
    }
    
}
