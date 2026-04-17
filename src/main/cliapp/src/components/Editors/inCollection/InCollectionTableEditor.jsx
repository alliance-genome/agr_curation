import { defaultAutocompleteOnChange } from '../../../utils/utils';
import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { inCollectionSearch } from './utils';
import { VocabTermAutocompleteTemplate } from '../../Autocomplete/VocabTermAutocompleteTemplate';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';

const onInCollectionValueChange = (event, setFieldValue, props) => {
	defaultAutocompleteOnChange(props, event, 'inCollection', setFieldValue, 'name');
};

export const InCollectionTableEditor = ({ editorOptions, errorMessagesRef }) => {
	return (
		<>
			<AutocompleteEditor
				search={inCollectionSearch}
				initialValue={editorOptions.rowData.inCollection?.name}
				editorOptions={editorOptions}
				fieldName="inCollection"
				onValueChangeHandler={onInCollectionValueChange}
				valueDisplay={(item, setAutocompleteSelectedItem, op, query) => (
					<VocabTermAutocompleteTemplate
						item={item}
						op={op}
						query={query}
						setAutocompleteSelectedItem={setAutocompleteSelectedItem}
					/>
				)}
			/>
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[editorOptions.rowIndex]} errorField="inCollection" />
		</>
	);
};
