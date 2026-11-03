import { FitTextHeading } from './FitTextHeading';
import { getIdentifier } from '../utils/utils';

/**
 * A detail page's heading: the entity type with the entity's label and identifier, or "<entity type> Detail
 * Page" while the entity has no identifier.
 *
 * @param {Object} props
 * @param {string} props.entityType - the entity's type as shown in the heading, e.g. "Allele"
 * @param {Object} props.entity - the entity the page shows
 * @param {string} [props.label] - the entity's symbol or name, as HTML
 */
export const DetailPageHeading = ({ entityType, entity, label }) => {
	const entityIdentifier = getIdentifier(entity);

	let headingHtml = `${entityType} Detail Page`;
	if (label && entityIdentifier) {
		headingHtml = `${entityType}: ${label} (${entityIdentifier})`;
	} else if (entityIdentifier) {
		headingHtml = `${entityType}: ${entityIdentifier}`;
	}

	return <FitTextHeading html={headingHtml} />;
};
