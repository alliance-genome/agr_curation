import { Message } from 'primereact/message';
import { useSubResource } from './SubResourcesContext';

/**
 * Says a section's list could not be read, and so cannot be saved. Renders nothing once it has been read.
 *
 * @param {Object} props
 * @param {string} props.type the name the page provides the list under
 */
export const SubResourceLoadError = ({ type }) => {
	const { label, loadError } = useSubResource(type);

	if (!loadError) return null;

	return (
		<Message
			severity="error"
			text={`Could not load these ${label}, so they cannot be saved. Reload the page to try again.`}
		/>
	);
};
