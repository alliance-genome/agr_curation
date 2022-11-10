import React from "react";
import { Card } from 'primereact/card';
import { Splitter, SplitterPanel } from "primereact/splitter";
import { Button } from 'primereact/button';

export function DataTableHeaderFooterTemplate({title, multiselectComponent, buttons, onclickEvent, isEnabled}){

		return (
						<Card>
								<Splitter style={{border:'none', height:'10%'}} gutterSize="0">
										<SplitterPanel size={50} style={{textAlign: 'left'}}>
												<h2>{title}</h2>
										</SplitterPanel>
										<SplitterPanel size={50} style={{textAlign: 'right'}}>
												{multiselectComponent}&nbsp;&nbsp;&nbsp;&nbsp;
												{buttons}&nbsp;&nbsp;
												<Button disabled={!isEnabled} onClick={onclickEvent}>Reset Table</Button>
										</SplitterPanel>
								</Splitter>
						</Card>
		)
}
