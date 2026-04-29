import { AutocompleteMultiTableEditor } from '../autocomplete/base/AutocompleteMultiTableEditor';
import { memberTermsSearchConfig, buildMemberTermsOtherFilters } from './utils';

export const MemberTermsTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => {
	const vocabularyName = editorOptions.rowData.vocabularyTermSetVocabulary?.name;
	return (
		<AutocompleteMultiTableEditor
			{...memberTermsSearchConfig}
			editorOptions={editorOptions}
			field="memberTerms"
			subField="name"
			otherFilters={vocabularyName ? buildMemberTermsOtherFilters(vocabularyName) : undefined}
			errorMessagesRef={errorMessagesRef}
			uiErrorMessagesRef={uiErrorMessagesRef}
		/>
	);
};
