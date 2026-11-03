import { Button } from 'primereact/button';
import { targetedGeneText, moreTargetedGenesText } from './utils';

// The first few targeted genes of an STR and how many more there are; opens the full, paged list.
export const TargetedGenesTemplate = ({ sequenceTargetingReagent, handleOpen }) => {
	const preview = sequenceTargetingReagent?.targetedGenesPreview || [];
	if (preview.length === 0) return null;
	const more = moreTargetedGenesText(sequenceTargetingReagent.targetedGeneCount, preview.length);

	return (
		<Button className="-my-2 p-0 p-button-text text-left" onClick={() => handleOpen(sequenceTargetingReagent)}>
			<div className="flex flex-column">
				{preview.map((targetedGene, index) => (
					<div
						key={index}
						className="overflow-hidden text-overflow-ellipsis underline"
						dangerouslySetInnerHTML={{ __html: targetedGeneText(targetedGene) }}
					/>
				))}
				{more && <div className="font-italic">{more}</div>}
			</div>
		</Button>
	);
};
