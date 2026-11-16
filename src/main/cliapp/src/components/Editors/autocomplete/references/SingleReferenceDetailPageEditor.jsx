import { LiteratureAutocompleteTemplate } from '../base/templates/LiteratureAutocompleteTemplate';
import { SingleAutocomplete } from '../../widgets/SingleAutocomplete';
import { FieldErrors } from '../../fields/FieldErrors';
import { normalizeError } from '../../fields/normalizeError';
import { singleReferenceSearch } from './utils';

/**
 * An unlabelled reference autosuggest, with the error reported under `references` beneath it.
 *
 * @param {object|null} reference - the picked reference, or `{curie}` holding text typed while none is picked
 * @param {(value: object|null) => void} onChange - called with the picked reference, `{curie: <text>}` for
 *   typed text, or null when the input is emptied
 * @param {object} [errorMessages] - errors keyed by field, read under `references`
 * @returns {JSX.Element}
 */
export const SingleReferenceDetailPageEditor = ({ reference, onChange, errorMessages }) => {
	const error = normalizeError(errorMessages?.references);

	return (
		<>
			<SingleAutocomplete
				value={reference}
				onChange={onChange}
				search={singleReferenceSearch}
				name="singleReference"
				invalid={error !== null}
				inputClassName="w-20rem"
				valueDisplay={(item, setHoverItem, tooltip, query) => (
					<LiteratureAutocompleteTemplate
						item={item}
						setAutocompleteHoverItem={setHoverItem}
						op={tooltip}
						query={query}
					/>
				)}
			/>
			<FieldErrors messages={[error]} layout="text" />
		</>
	);
};
