import React from "react";
import { Card } from 'primereact/card';
import { Splitter, SplitterPanel } from "primereact/splitter";
import { Button } from 'primereact/button';

export function DataTableHeaderFooterTemplate({title, multiselectComponent, buttons, tableStateConfirm, isEnabled, modReset, modResetConfirm}){

		return (
						<Card>
								<Splitter style={{border:'none', height:'10%'}} gutterSize="0">
										<SplitterPanel size={50} style={{textAlign: 'left'}}>
												<h2>{title}</h2>
										</SplitterPanel>
										<SplitterPanel size={60} style={{textAlign: 'right'}}>
											{multiselectComponent}&nbsp;
											{buttons}&nbsp;
											<Button disabled={!isEnabled} onClick={tableStateConfirm}>Reset Table</Button>&nbsp;
											{modReset && 
												<Button disabled={!isEnabled} onClick={modResetConfirm}>Set MOD Defaults</Button>
											}
										</SplitterPanel>
								</Splitter>
						</Card>
		)
}
