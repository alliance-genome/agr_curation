package org.alliancegenome.curation_api.view;

public class View {

	public static class FieldsOnly {
	}

	public static class FieldsAndLists extends FieldsOnly {
	}

	public static class ConditionRelationView extends FieldsOnly {
	}
	
	public static class ConditionRelationCreateView extends ConditionRelationView {
	}
	
	public static class ConditionRelationUpdateView extends ConditionRelationView {
	}

	public static class VocabularyTermView extends FieldsAndLists {
	}

	public static class VocabularyView extends FieldsOnly {
	}

	public static class VocabularyTermUpdate extends FieldsOnly {
	}

	public static class VocabularyTermSetView extends FieldsOnly {
	}
	
	public static class ResourceDescriptorView extends FieldsOnly {
	}
	
	public static class ResourceDescriptorPageView extends FieldsOnly {
	}

	public static class NoteView extends FieldsAndLists {
	}

	public static class ReportHistory extends FieldsOnly {
	}
	
	public static class ConstructView extends FieldsOnly {
	}

	public static class DiseaseAnnotation extends FieldsOnly {
	}

	public static class DiseaseAnnotationUpdate extends DiseaseAnnotation {
	}

	public static class DiseaseAnnotationCreate extends DiseaseAnnotation {
	}

	public static class AlleleView extends FieldsOnly {
	}

	public static class AlleleUpdate extends AlleleView {
	}

	public static class AlleleCreate extends AlleleView {
	}

	public static class GeneView extends FieldsOnly {
	}

	public static class GeneUpdate extends GeneView {
	}

	public static class GeneCreate extends GeneView {
	}

	public static class VariantView extends FieldsOnly {
	}

	public static class VariantUpdate extends GeneView {
	}

	public static class VariantCreate extends GeneView {
	}

	public static class PersonSettingView {
	}

	public static class PrivateOnlyView {
	}

}
