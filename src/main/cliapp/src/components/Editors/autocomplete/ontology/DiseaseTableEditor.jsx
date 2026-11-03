import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { diseaseSearchConfig } from './utils';

export const DiseaseTableEditor = ({ editorOptions, field = 'diseaseAnnotationObject' }) => (
	<AutocompleteSingleTableEditor editorOptions={editorOptions} field={field} {...diseaseSearchConfig} />
);
