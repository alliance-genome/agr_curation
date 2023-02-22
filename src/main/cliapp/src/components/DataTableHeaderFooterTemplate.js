import React from "react";
import { Card } from 'primereact/card';
import { Splitter, SplitterPanel } from "primereact/splitter";
import { ConfirmButton } from "./ConfirmButton";

export function DataTableHeaderFooterTemplate({title, multiselectComponent, buttons, resetTableState, isEnabled, modReset, resetToModDefault}){

		return (
						<Card>
								<Splitter style={{border:'none', height:'10%'}} gutterSize="0">
										<SplitterPanel size={50} style={{textAlign: 'left'}}>
												<h2>{title}</h2>
										</SplitterPanel>
										<SplitterPanel size={60} style={{textAlign: 'right'}}>
											{multiselectComponent}&nbsp;
											{buttons}&nbsp;
											<ConfirmButton
												buttonText="Reset Table"
												headerText={`${title} State Reset`}
												messageText= {`Are you sure? This will reset the local state of the ${title}.`}
												acceptHandler={resetTableState}
												disabled={!isEnabled}
											/>
											{modReset &&
												<ConfirmButton
													buttonText="Set MOD Defaults"
													headerText={`${title} MOD Default Reset`}
													messageText= {`Are you sure? This will reset the local state of the ${title} to the MOD default settings.`}
													acceptHandler={resetToModDefault}
													disabled={!isEnabled}
												/>
											}
										</SplitterPanel>
								</Splitter>
						</Card>
		)
}
