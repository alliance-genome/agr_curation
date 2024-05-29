package org.alliancegenome.curation_api.enums;

public enum ConditionRelationFmsEnum {
	
	ameliorates("ameliorated_by"),
	exacerbates("exacerbated_by"),
	has_condition("has_condition"),
	induces("induced_by");

	public String agrRelation;

	private ConditionRelationFmsEnum(String agrRelation) {
		this.agrRelation = agrRelation;
	}

	public static ConditionRelationFmsEnum findByName(String name) {

		for (ConditionRelationFmsEnum relation : values()) {
			if (relation.name().equals(name)) {
				return relation;
			}
		}

		return null;
	}
}
