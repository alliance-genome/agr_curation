import React from "react";
import { Card } from 'primereact/card';
import { Splitter, SplitterPanel } from "primereact/splitter";
import { ConfirmButton } from "./ConfirmButton";

export function DataTableHeaderFooterTemplate({title, multiselectComponent, buttons, resetTableState, isEnabled, modReset, setToModDefault}){

		return (
						<Card>
								<Splitter className="border-none h-3rem" gutterSize={0}>
										<SplitterPanel size={50} className="text-left">
												<h2>{title}</h2>
										</SplitterPanel>
										<SplitterPanel size={60} className="text-right">
											{multiselectComponent}&nbsp;
											{buttons}&nbsp;
											<ConfirmButton
												buttonText="Reset Table"
												headerText={`${title} State Reset`}
												messageText= {`Are you sure? This will reset the local state of the ${title}.`}
												acceptHandler={resetTableState}
												disabled={!isEnabled}
											/>&nbsp;&nbsp;&nbsp;
											{modReset &&
												<ConfirmButton
													buttonText="Set MOD Defaults"
													headerText={`${title} MOD Default Reset`}
													messageText= {`Are you sure? This will reset the local state of the ${title} to the MOD default settings.`}
													acceptHandler={setToModDefault}
													disabled={!isEnabled}
												/>
											}
										</SplitterPanel>
								</Splitter>
						</Card>
		)
}
