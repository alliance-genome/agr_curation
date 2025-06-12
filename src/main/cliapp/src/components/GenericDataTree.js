import React, { useCallback, useEffect, useState } from 'react';

import { Tree } from 'primereact/tree';
import { Card } from 'primereact/card';
import { Button } from 'primereact/button';

import { OntologyService } from '../service/OntologyService';

export const GenericDataTree = (props) => {
	const [nodes, setNodes] = useState(null);
	const [loading, setLoading] = useState(true);
	const [selectedTerm, setSelectedTerm] = useState();
	const [rootNodeCache, setRootNodeCache] = useState([]);
	const [currentPage, setCurrentPage] = useState(0);
	const [hasMoreRootNodes, setHasMoreRootNodes] = useState(true);
	const [renderingMore, setRenderingMore] = useState(false);

	const PAGE_SIZE = 20; // Number of root items to load at once

	const findNodeToModify = (nodes, id) => {
		for (let node of nodes) {
			if (node.children) {
				let found = findNodeToModify(node.children, id);
				if (found !== null) return found;
			} else {
				if (node.curie === id) {
					return node;
				}
			}
		}
		return null;
	};

	const loadOnExpand = (event) => {
		if (!event.node.children) {
			setLoading(true);

			const ontologyService = new OntologyService(props.endpoint);
			let _nodes = [...nodes];

			let modifyNode = findNodeToModify(_nodes, event.node.curie);

			ontologyService.getChildren(event.node.curie).then((res) => {
				if (res.data.entities) {
					modifyNode.children = [];
					for (let node of res.data.entities) {
						node.key = node.curie;
						node.label = node.name + ' (' + node.curie + ')';
						if (node?.descendantCount && node.descendantCount > 0) {
							node.leaf = false;
						} else {
							node.leaf = true;
						}
						modifyNode.children.push(node);
					}
					modifyNode.children.sort((a, b) => (a.label.toLowerCase() > b.label.toLowerCase() ? 1 : -1));
				} else {
					modifyNode.leaf = true;
				}
				setNodes(_nodes);
				setLoading(false);
			});
		}
	};

	const loadMoreRootNodes = useCallback(() => {
		if (renderingMore || !hasMoreRootNodes) return;

		setRenderingMore(true);

		const nextPage = currentPage + 1;
		const start = nextPage * PAGE_SIZE;
		const end = start + PAGE_SIZE;
		const nextBatch = rootNodeCache.slice(start, end);

		if (nextBatch.length === 0) {
			setHasMoreRootNodes(false);
			setRenderingMore(false);
			return;
		}

		setNodes((prevNodes) => [...prevNodes, ...nextBatch]);

		setCurrentPage(nextPage);
		setHasMoreRootNodes(end < rootNodeCache.length);
		setRenderingMore(false);
	}, [currentPage, hasMoreRootNodes, renderingMore, rootNodeCache]);

	useEffect(() => {
		const handleScroll = () => {
			const scrollTop = window.scrollY || document.documentElement.scrollTop;
			const scrollHeight = document.documentElement.scrollHeight;
			const clientHeight = window.innerHeight;

			// If scrolled to bottom (with a small buffer)
			if (scrollHeight - scrollTop - clientHeight < 100 && hasMoreRootNodes && !renderingMore) {
				loadMoreRootNodes();
			}
		};
		window.addEventListener('scroll', handleScroll);
		return () => window.removeEventListener('scroll', handleScroll);
	}, [hasMoreRootNodes, renderingMore, loadMoreRootNodes]);

	const onNodeSelect = (event) => {
		//console.log(event.node);

		const ontologyService = new OntologyService(props.endpoint);
		ontologyService.getTerm(event.node.curie).then((res) => {
			console.log(res.data.entity);
			setSelectedTerm(res.data.entity);
		});
	};

	useEffect(() => {
		const ontologyService = new OntologyService(props.endpoint);
		ontologyService.getRootNodes().then((res) => {
			let allNodes = [];
			let count = 0;

			// Process and store all root nodes in cache
			for (let node of res.data.entities) {
				if (node.obsolete === true) {
					continue;
				}

				node.key = node.curie;
				node.label = node.name + ' (' + node.curie + ')';
				if (node?.descendantCount && node.descendantCount > 0) {
					node.leaf = false;
				} else {
					node.leaf = true;
				}

				allNodes.push(node);
				count = count + 1;
			}

			// Sort the entire cache
			allNodes.sort((a, b) => (a.label.toLowerCase() > b.label.toLowerCase() ? 1 : -1));

			// Store all nodes in cache
			setRootNodeCache(allNodes);

			// Only display the first batch of nodes
			const firstBatch = allNodes.slice(0, PAGE_SIZE);
			setNodes(firstBatch);
			setCurrentPage(0);
			setHasMoreRootNodes(allNodes.length > PAGE_SIZE);
			setLoading(false);
		});
	}, []); // eslint-disable-line react-hooks/exhaustive-deps

	return (
		<Card title={props.treeName + ' Tree'}>
			<div className="grid">
				<div className="col-6">
					<div className="card">
						<Tree
							value={nodes}
							onExpand={loadOnExpand}
							selectionMode="single"
							onSelect={onNodeSelect}
							loading={loading}
						/>
						<div className="flex justify-content-end mt-3">
							<Button onClick={loadMoreRootNodes} disabled={renderingMore}>
								{renderingMore ? 'loading...' : !hasMoreRootNodes ? 'No more results' : 'Show More'}
							</Button>
						</div>
					</div>
				</div>
				<div className="col-6">
					<div className="card">
						<div className="fixed">
							<pre style={{ whiteSpace: 'pre-wrap' }}>{JSON.stringify(selectedTerm, null, 2)}</pre>
						</div>
					</div>
				</div>
			</div>
		</Card>
	);
};
