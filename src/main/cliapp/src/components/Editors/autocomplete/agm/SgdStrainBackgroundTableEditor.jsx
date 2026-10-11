import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { getIdentifier } from '../../../../utils/utils';
import { sgdStrainBackgroundSearchConfig } from './utils';

export const SgdStrainBackgroundTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="sgdStrainBackground"
		subField="primaryExternalId"
		initialValue={getIdentifier(editorOptions.rowData.sgdStrainBackground)}
		{...sgdStrainBackgroundSearchConfig}
	/>
);
