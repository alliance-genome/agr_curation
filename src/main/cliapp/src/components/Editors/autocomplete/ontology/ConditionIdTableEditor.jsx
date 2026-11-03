import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { conditionIdSearchConfig } from './utils';

export const ConditionIdTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor editorOptions={editorOptions} field="conditionId" {...conditionIdSearchConfig} />
);
