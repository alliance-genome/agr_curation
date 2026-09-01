import { Button } from 'primereact/button';
import { Divider } from 'primereact/divider';
import { MultiSelect } from 'primereact/multiselect';
import { getDefaultFormState } from '../service/TableStateService';
import { useGetUserSettings } from '../service/useGetUserSettings';

/**
 * Tracks which of a form's optional field sections are visible, persisting the selection per
 * curator under `${formName}FormSettings`.
 *
 * A field absent from the stored selection but also absent from the stored list of known
 * fields is treated as visible, so fields added to `toggleableFields` appear for curators who
 * already have a saved selection. A stored field no longer in `toggleableFields` is dropped.
 *
 * @param {string} formName - form identifier the person settings key is derived from
 * @param {string[]} toggleableFields - labels the curator may hide, in display order
 * @param {string[]} [defaultVisibleFields] - labels visible before any selection is saved;
 *   defaults to all of `toggleableFields`
 * @returns {{visibleFields: string[], setVisibleFields: (fields: string[]) => void,
 *   showAllFields: () => void, isVisible: (field: string) => boolean}}
 */
export const useFormFieldVisibility = (formName, toggleableFields, defaultVisibleFields) => {
	const defaultSettings = getDefaultFormState(formName, toggleableFields, defaultVisibleFields);
	const { settings, mutate } = useGetUserSettings(defaultSettings.formSettingsKeyName, defaultSettings, false);

	// A saved selection with nothing selected arrives without its selectedFormFields key: the
	// API's REST ObjectMapper serializes with JsonInclude.Include.NON_EMPTY, which omits empty
	// collections. orderedFormFields is never empty for a saved selection, so treat its presence
	// as the marker that a selection exists and a missing selectedFormFields as "none selected".
	const hasSavedSelection = Array.isArray(settings?.orderedFormFields) && settings.orderedFormFields.length > 0;
	const selectedFields = settings?.selectedFormFields ?? (hasSavedSelection ? [] : defaultSettings.selectedFormFields);
	const knownFields = settings?.orderedFormFields ?? [];
	const visibleFields = toggleableFields.filter(
		(field) => selectedFields.includes(field) || !knownFields.includes(field)
	);

	const setVisibleFields = (fields) => {
		mutate({ ...defaultSettings, selectedFormFields: fields });
	};

	const showAllFields = () => {
		setVisibleFields(toggleableFields);
	};

	const isVisible = (field) => !toggleableFields.includes(field) || visibleFields.includes(field);

	return { visibleFields, setVisibleFields, showAllFields, isVisible };
};

/**
 * Field visibility controls for a form header: a menu of the hideable fields and a button that
 * reveals all of them.
 *
 * @param {Object} props
 * @param {string[]} props.toggleableFields - labels the curator may hide
 * @param {string[]} props.visibleFields - labels currently visible
 * @param {(fields: string[]) => void} props.setVisibleFields
 * @param {() => void} props.showAllFields
 */
export const FormFieldVisibilityMenu = ({ toggleableFields, visibleFields, setVisibleFields, showAllFields }) => {
	return (
		<>
			<MultiSelect
				aria-label="formFieldToggle"
				options={toggleableFields}
				value={visibleFields}
				filter
				resetFilterOnHide
				onChange={(e) => setVisibleFields(e.value)}
				className="w-20rem text-center"
				maxSelectedLabels={4}
			/>
			<Button label="Show all fields" onClick={showAllFields} />
		</>
	);
};

/**
 * Renders a form field section and its trailing divider, or nothing when the section is
 * hidden.
 *
 * @param {Object} props
 * @param {boolean} props.isVisible
 * @param {React.ReactNode} props.children
 */
export const FormSection = ({ isVisible, children }) => {
	if (!isVisible) return null;

	return (
		<>
			{children}
			<Divider />
		</>
	);
};
