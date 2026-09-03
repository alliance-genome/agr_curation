/**
 * The asterisk marking a field the API will not accept as empty. Hidden from assistive
 * technology so it does not become part of a label's accessible name.
 */
export const RequiredFieldMarker = () => {
	return (
		<span className="p-error" aria-hidden="true">
			*
		</span>
	);
};
