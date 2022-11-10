package org.alliancegenome.curation_api.auth;

import java.lang.annotation.*;

import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface AuthenticatedUser {

}
