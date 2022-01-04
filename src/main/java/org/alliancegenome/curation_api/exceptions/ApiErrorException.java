package org.alliancegenome.curation_api.exceptions;

import org.alliancegenome.curation_api.response.ObjectResponse;

import lombok.*;

@Setter
@Getter
public class ApiErrorException extends RuntimeException {

    private ObjectResponse<?> objectResponse;

    public ApiErrorException(String message) {
        super();
    }

    public ApiErrorException(ObjectResponse<?> error) {
        //super(String.join(", ", error.getErrors()));
        this.objectResponse = error;
    }

    public ObjectResponse<?> getObjectResponse() {
        return objectResponse;
    }
}
