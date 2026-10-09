import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { variantTypeSearchConfig } from './utils';

export const VariantTypeTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor editorOptions={editorOptions} field="variantType" {...variantTypeSearchConfig} />
);
