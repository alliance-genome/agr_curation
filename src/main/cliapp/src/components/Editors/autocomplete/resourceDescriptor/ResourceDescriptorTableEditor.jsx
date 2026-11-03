import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { resourceDescriptorSearchConfig } from './utils';

export const ResourceDescriptorTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="resourceDescriptor"
		subField="prefix"
		{...resourceDescriptorSearchConfig}
	/>
);
