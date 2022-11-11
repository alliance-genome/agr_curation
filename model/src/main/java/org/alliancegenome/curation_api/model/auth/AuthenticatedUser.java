package org.alliancegenome.curation_api.model.auth;

import java.lang.annotation.*;

import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface AuthenticatedUser {

}
