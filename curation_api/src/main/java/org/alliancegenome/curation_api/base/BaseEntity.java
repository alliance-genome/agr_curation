package org.alliancegenome.curation_api.base;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.*;
import org.hibernate.search.annotations.Field;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

public class BaseEntity implements Serializable {
	
}
