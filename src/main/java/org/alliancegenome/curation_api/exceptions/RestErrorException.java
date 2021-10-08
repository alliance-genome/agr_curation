package org.alliancegenome.curation_api.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.alliancegenome.curation_api.response.ObjectResponse;

@Setter
@Getter
public class RestErrorException extends RuntimeException {

    private ObjectResponse objectResponse;

    public RestErrorException(String message) {
        super();
    }

    public RestErrorException(ObjectResponse error) {
        //super(String.join(", ", error.getErrors()));
        this.objectResponse = error;
    }

    public ObjectResponse getObjectResponse() {
        return objectResponse;
    }
}
