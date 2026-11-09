import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { conditionClassSearchConfig } from './utils';

export const ConditionClassTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor editorOptions={editorOptions} field="conditionClass" {...conditionClassSearchConfig} />
);
