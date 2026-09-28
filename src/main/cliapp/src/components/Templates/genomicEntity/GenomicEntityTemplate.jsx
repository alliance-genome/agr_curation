import { Link } from 'react-router-dom';
import { Tooltip } from 'primereact/tooltip';
import { getGenomicEntityName, getGenomicEntityText, getIdentifier } from '../../../utils/utils';

export const GenomicEntityTemplate = ({ genomicEntity, detailPage }) => {
	if (!genomicEntity) return null;

	const targetClass = `a${crypto.randomUUID()}`;
	const subjectText = getGenomicEntityText(genomicEntity);
	const identifier = getIdentifier(genomicEntity);
	const subjectName = getGenomicEntityName(genomicEntity);

	const tooltipTemplate = `Identifier: ${identifier}${subjectName ? `<br/> Name: ${subjectName}` : ''}`;

	const withDetailPageLink = (content) => {
		if (!detailPage || !identifier) return content;
		return (
			<Link to={`/${detailPage.toLowerCase()}/${identifier}`} target="_blank">
				{content}
			</Link>
		);
	};

	if (!subjectText)
		return (
			<>
				{withDetailPageLink(<div className="overflow-hidden text-overflow-ellipsis">{identifier}</div>)}
				<Tooltip target={`.${targetClass}`} mouseTrack position="bottom">
					<div
						dangerouslySetInnerHTML={{
							__html: tooltipTemplate,
						}}
					/>
				</Tooltip>
			</>
		);

	return (
		<>
			{withDetailPageLink(
				<div
					className={`overflow-hidden text-overflow-ellipsis ${targetClass}`}
					dangerouslySetInnerHTML={{
						__html: `${subjectText} (${identifier})`,
					}}
				/>
			)}
			<Tooltip target={`.${targetClass}`} mouseTrack position="bottom">
				<div
					dangerouslySetInnerHTML={{
						__html: tooltipTemplate,
					}}
				/>
			</Tooltip>
		</>
	);
};
