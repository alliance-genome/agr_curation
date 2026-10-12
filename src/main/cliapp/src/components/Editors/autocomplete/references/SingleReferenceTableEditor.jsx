import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { getRefString } from '../../../../utils/utils';
import { singleReferenceSearchConfig } from './utils';

export const SingleReferenceTableEditor = ({ editorOptions, field = 'evidenceItem' }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field={field}
		initialValue={getRefString(editorOptions.rowData[field])}
		{...singleReferenceSearchConfig}
	/>
);
