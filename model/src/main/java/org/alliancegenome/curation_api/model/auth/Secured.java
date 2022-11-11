package org.alliancegenome.curation_api.model.auth;

import java.lang.annotation.*;

import javax.ws.rs.NameBinding;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured {

}


