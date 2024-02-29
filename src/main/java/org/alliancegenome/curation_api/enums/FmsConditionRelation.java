package org.alliancegenome.curation_api.enums;

public enum FmsConditionRelation {
	ameliorates("ameliorated_by"),
	exacerbates("exacerbated_by"),
	has_condition("has_condition"),
	induces("induced_by");

	public String agrRelation;
	
	private FmsConditionRelation(String agrRelation) {
		this.agrRelation = agrRelation;
	}

	public static FmsConditionRelation findByName(String name) {
		
		for (FmsConditionRelation relation : values()) {
			if (relation.name().equals(name))
				return relation;
		}
		
		return null;
	}
}
