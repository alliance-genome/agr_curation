package org.alliancegenome.curation_api.view;

public class View {

	
	public static class FieldsOnly { }
	public static class FieldsAndLists extends FieldsOnly { }
	public static class ConditionRelationUpdateView extends FieldsOnly { }

	public static class VocabularyTermView extends FieldsAndLists { }
	public static class VocabularyView extends FieldsOnly { }
	public static class VocabularyTermUpdate extends FieldsOnly { }
	
	public static class NoteView extends FieldsAndLists { }
	
	public static class BulkLoadFileHistory extends FieldsOnly { }
	public static class ReportHistory extends FieldsOnly { }
	
	public static class DiseaseAnnotationUpdate extends FieldsOnly { }

}
