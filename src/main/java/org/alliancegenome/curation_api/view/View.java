package org.alliancegenome.curation_api.view;

public class View {

	
	public static class FieldsOnly { }
	public static class FieldsAndLists extends FieldsOnly { }
	public static class ConditionRelationUpdateView extends FieldsOnly { }

	public static class VocabularyTermView extends FieldsAndLists { }
	public static class VocabularyView extends FieldsOnly { }
	public static class VocabularyTermUpdate extends FieldsOnly { }
	public static class VocabularyTermSetView extends FieldsAndLists { }
	
	public static class NoteView extends FieldsAndLists { }
	
	public static class BulkLoadFileHistory extends FieldsOnly { }
	public static class ReportHistory extends FieldsOnly { }
	
	public static class DiseaseAnnotation extends FieldsOnly { }
	public static class DiseaseAnnotationUpdate extends DiseaseAnnotation { }
	public static class DiseaseAnnotationCreate extends DiseaseAnnotation { }
	

	public static class Allele extends FieldsOnly { }
	public static class AlleleUpdate extends Allele { }
	public static class AlleleCreate extends Allele { }
	
	public static class PersonSettingView { }

}
