import React, { useEffect, useState } from 'react';

import { Tree } from 'primereact/tree';
import { Card } from 'primereact/card';

import { OntologyService } from '../service/OntologyService';

export const GenericDataTree = (props) => {

	const [nodes, setNodes] = useState(null);
	const [loading, setLoading] = useState(true);
	const [selectedTerm, setSelectedTerm] = useState();

	const findNodeToModify = (nodes, id) => {
		for(var node of nodes) {
			if(node.children) {
				var found = findNodeToModify(node.children, id);
				if(found !== null) return found;
			} else {
				if(node.curie === id) {
					return node;
				}
			}
		}
		return null;
	}

	const loadOnExpand = (event) => {

		if (!event.node.children) {
			setLoading(true);

			const ontologyService = new OntologyService(props.endpoint);
			let _nodes = [...nodes];

			var modifyNode = findNodeToModify(_nodes, event.node.curie);

			ontologyService.getChildren(event.node.curie).then((res) => {
				if(res.data.entities) {
					modifyNode.children = [];
					for(var node of res.data.entities) {
						node.key = node.curie;
						node.label = node.name + " (" + node.curie + ")";
						if(node?.childCount && node.childCount > 0) {
							node.leaf = false;
						}
						else{
							node.leaf = true;
						}
						modifyNode.children.push(node);
					}
					modifyNode.children.sort((a, b) => (a.label.toLowerCase() > b.label.toLowerCase()) ? 1 : -1);
				} else {
					modifyNode.leaf = true;
				}
				setNodes(_nodes);
				setLoading(false);
			});
		}
	}

	const onNodeSelect = (event) => {
        //console.log(event.node);

        const ontologyService = new OntologyService(props.endpoint);
        ontologyService.getTerm(event.node.curie).then((res) => {
			console.log(res.data.entity);
			setSelectedTerm(res.data.entity);
		});
    }

	useEffect(() => {
		const ontologyService = new OntologyService(props.endpoint);
		ontologyService.getRootNodes().then((res) => {
			var _nodes = [];
			var count = 0;
			for(var node of res.data.entities) {
				if(node.obsolete === true) {
					continue;
				}
				node.key = node.curie;
				node.label = node.name + " (" + node.curie + ")";
				if(node?.childCount && node.childCount > 0) {
					node.leaf = false;
				}
				else{
					node.leaf = true;
				}
				_nodes.push(node);
				count = count + 1;
			}
			_nodes.sort((a, b) => (a.label.toLowerCase() > b.label.toLowerCase()) ? 1 : -1);
			setNodes(_nodes);
			setLoading(false);
		});
	}, []); // eslint-disable-line react-hooks/exhaustive-deps

	return (
		<Card title={props.treeName + " Tree"}>
			<div className="grid">
				<div className="col-6">
					<div className="card">
						<Tree
							value={nodes}
							onExpand={loadOnExpand}
							selectionMode="single"
							//onSelectionChange={onNodeSelect}
							onSelect={onNodeSelect}
							loading={loading}
						/>
					</div>
				</div>
				<div className="col-6">
					<div className="card">
						<div className="fixed"><pre style={{whiteSpace: "pre-wrap"}}>{JSON.stringify(selectedTerm, null, 2) }</pre></div>
					</div>
				</div>
			</div>
		</Card>
	)

}
