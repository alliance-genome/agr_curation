import React from 'react';
import { Card } from 'primereact/card';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { ConfirmButton } from './ConfirmButton';

export function DataTableHeaderFooterTemplate({
	title,
	multiselectComponent,
	buttons,
	resetTableState,
	isInEditMode,
	modReset,
	setToModDefault,
}) {
	return (
		<Card>
			<Splitter className="border-none h-3rem" gutterSize={0}>
				<SplitterPanel size={50} className="flex justify-content-start">
					<h2>{title}</h2>
				</SplitterPanel>
				<SplitterPanel size={60} className="flex justify-content-end">
					{multiselectComponent}&nbsp;
					{buttons}
					<ConfirmButton
						buttonText="Reset Table"
						headerText={`${title} State Reset`}
						messageText={`Are you sure? This will reset the local state of the ${title}.`}
						acceptHandler={resetTableState}
						disabled={isInEditMode}
						buttonClassName="ml-2"
					/>
					{modReset && (
						<ConfirmButton
							buttonText="Set to MOD Defaults"
							headerText={`${title} MOD Default Reset`}
							messageText={`Are you sure? This will reset the local state of the ${title} to the MOD default settings.`}
							acceptHandler={setToModDefault}
							disabled={isInEditMode}
							buttonClassName="ml-3"
						/>
					)}
				</SplitterPanel>
			</Splitter>
		</Card>
	);
}
