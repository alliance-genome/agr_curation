package org.alliancegenome.curation_api.view;

public class View {

    public static class FieldsOnly { }
    public static class FieldsAndLists extends FieldsOnly { }
    
    public static class VocabularyTermView extends FieldsOnly { }
    public static class VocabularyView extends FieldsOnly { }
    
    public static class BulkLoadFileHistory extends FieldsOnly { }
    
}
