import { Message } from 'primereact/message';
import { useSubResource } from '../../components/SubResourcesContext';
import { ALLELE_SUB_RESOURCES } from './constants';

/**
 * Says a section's list could not be read, and so cannot be saved. Renders nothing once it has been read.
 *
 * @param {Object} props
 * @param {keyof typeof ALLELE_SUB_RESOURCES} props.type which list
 */
export const SubResourceLoadError = ({ type }) => {
	const { loadError } = useSubResource(type);
	const { label } = ALLELE_SUB_RESOURCES[type];

	if (!loadError) return null;

	return (
		<Message
			severity="error"
			text={`Could not load these ${label}, so they cannot be saved. Reload the page to try again.`}
		/>
	);
};
