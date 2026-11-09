import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { inCollectionSearchConfig } from './utils';

export const InCollectionTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="inCollection"
		subField="name"
		{...inCollectionSearchConfig}
	/>
);
