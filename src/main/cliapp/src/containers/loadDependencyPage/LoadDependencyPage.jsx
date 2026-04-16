import React, { useState, useMemo } from 'react';
import { Message } from 'primereact/message';

const CATEGORIES = {
	ENTITY: { label: 'Entity', color: '#66BB6A' },
	ASSOCIATION: { label: 'Association', color: '#42A5F5' },
	CROSSREF: { label: 'CrossReference', color: '#FFA726' },
	GFF: { label: 'GFF', color: '#AB47BC' },
	ORTHOLOGY: { label: 'Orthology', color: '#26C6DA' },
	VEP: { label: 'VEP', color: '#EF5350' },
};

const LOADS = [
	{ id: 'agm', label: 'AGM', category: 'ENTITY', deps: [] },
	{ id: 'allele', label: 'Allele', category: 'ENTITY', deps: [] },
	{ id: 'construct', label: 'Construct', category: 'ENTITY', deps: [] },
	{ id: 'gene', label: 'Gene', category: 'ENTITY', deps: [] },
	{ id: 'htp_dataset', label: 'HTP Dataset Ann.', category: 'ENTITY', deps: [] },
	{ id: 'htp_sample', label: 'HTP Sample Ann.', category: 'ENTITY', deps: [] },
	{ id: 'molecule', label: 'Molecule', category: 'ENTITY', deps: [] },
	{ id: 'str', label: 'Seq Targeting Reagent', category: 'ENTITY', deps: ['gene'] },
	{ id: 'variant', label: 'Variant', category: 'ENTITY', deps: [] },

	{ id: 'allele_assoc', label: 'Allele Assoc.', category: 'ASSOCIATION', deps: ['allele', 'gene'] },
	{
		id: 'construct_assoc',
		label: 'Construct Assoc.',
		category: 'ASSOCIATION',
		deps: ['agm', 'allele', 'construct', 'gene'],
	},
	{ id: 'disease_ann', label: 'Disease Ann.', category: 'ASSOCIATION', deps: ['agm', 'allele', 'gene'] },
	{ id: 'expression', label: 'Expression', category: 'ASSOCIATION', deps: ['gene'] },
	{ id: 'gaf', label: 'GAF', category: 'ASSOCIATION', deps: ['gene'] },
	{ id: 'interaction', label: 'Interaction', category: 'ASSOCIATION', deps: ['gene'] },
	{ id: 'phenotype_ann', label: 'Phenotype Ann.', category: 'ASSOCIATION', deps: ['agm', 'allele', 'gene'] },

	{ id: 'biogrid', label: 'BioGrid-ORCS', category: 'CROSSREF', deps: ['gene'], rerunAfterGene: true },
	{ id: 'expr_atlas', label: 'ExpressionAtlas', category: 'CROSSREF', deps: ['gene'], rerunAfterGene: true },
	{ id: 'geo', label: 'GEO CrossRef', category: 'CROSSREF', deps: ['gene'], rerunAfterGene: true },

	{ id: 'gff_gene', label: 'GFF Gene', category: 'GFF', deps: ['gene'] },
	{ id: 'gff_transcript', label: 'GFF Transcript', category: 'GFF', deps: ['gene'] },
	{ id: 'gff_cds', label: 'GFF CDS', category: 'GFF', deps: ['gff_transcript'] },
	{ id: 'gff_exon', label: 'GFF Exon', category: 'GFF', deps: ['gff_transcript'] },

	{ id: 'orthology', label: 'Orthology', category: 'ORTHOLOGY', deps: ['gene'] },
	{ id: 'paralogy', label: 'Paralogy', category: 'ORTHOLOGY', deps: ['gene'] },

	{ id: 'vep_transcript', label: 'VEP Transcript', category: 'VEP', deps: ['variant', 'gff_transcript'] },
	{ id: 'vep_gene', label: 'VEP Gene', category: 'VEP', deps: ['vep_transcript'] },
];

// Row layout - items ordered to minimize arrow crossings.
// Gene is centered; items with more entity deps are placed near those entities.
const ROWS = [
	{ ids: ['htp_dataset', 'htp_sample', 'molecule', 'gene', 'agm', 'allele', 'construct', 'variant'] },
	{
		ids: ['expression', 'gaf', 'interaction', 'str', 'disease_ann', 'phenotype_ann', 'allele_assoc', 'construct_assoc'],
	},
	{ ids: ['biogrid', 'expr_atlas', 'geo', 'gff_gene', 'gff_transcript', 'orthology', 'paralogy'] },
	{ ids: ['gff_cds', 'gff_exon', 'vep_transcript'] },
	{ ids: ['vep_gene'] },
];

// Update this date when the dependency data above is modified
const LAST_UPDATED = '2026-03-25';

const NODE_W = 160;
const NODE_H = 36;
const H_GAP = 14;
const V_GAP = 76;
const PADDING = 40;
const LEGEND_H = 50;

export const LoadDependencyPage = () => {
	const [hoveredNode, setHoveredNode] = useState(null);

	const loadMap = useMemo(() => {
		const map = {};
		LOADS.forEach((load) => {
			map[load.id] = load;
		});
		return map;
	}, []);

	const { positions, edges, chartWidth, chartHeight } = useMemo(() => {
		const maxCount = Math.max(...ROWS.map((row) => row.ids.length));
		const totalWidth = maxCount * NODE_W + (maxCount - 1) * H_GAP + PADDING * 2;

		const nodePositions = {};
		ROWS.forEach((row, rowIndex) => {
			const count = row.ids.length;
			const rowWidth = count * NODE_W + (count - 1) * H_GAP;
			const startX = (totalWidth - rowWidth) / 2;
			const rowY = PADDING + LEGEND_H + rowIndex * (NODE_H + V_GAP);

			row.ids.forEach((id, colIndex) => {
				nodePositions[id] = {
					x: startX + colIndex * (NODE_W + H_GAP),
					y: rowY,
				};
			});
		});

		const edgeList = [];
		LOADS.forEach((load) => {
			load.deps.forEach((depId) => {
				if (nodePositions[depId] && nodePositions[load.id]) {
					edgeList.push({
						key: `${depId}->${load.id}`,
						fromId: depId,
						toId: load.id,
						x1: nodePositions[depId].x + NODE_W / 2,
						y1: nodePositions[depId].y + NODE_H,
						x2: nodePositions[load.id].x + NODE_W / 2,
						y2: nodePositions[load.id].y,
					});
				}
			});
		});

		const totalHeight = PADDING + LEGEND_H + ROWS.length * NODE_H + (ROWS.length - 1) * V_GAP + PADDING;
		return { positions: nodePositions, edges: edgeList, chartWidth: totalWidth, chartHeight: totalHeight };
	}, []);

	const hoveredLoad = hoveredNode ? loadMap[hoveredNode] : null;

	const dependents = useMemo(() => {
		if (!hoveredNode) return [];
		return LOADS.filter((load) => load.deps.includes(hoveredNode));
	}, [hoveredNode]);

	const isEdgeHighlighted = (edge) => {
		if (!hoveredNode) return false;
		return edge.fromId === hoveredNode || edge.toId === hoveredNode;
	};

	const categoryEntries = Object.entries(CATEGORIES);
	const legendSpacing = Math.min(175, (chartWidth - PADDING * 2) / (categoryEntries.length + 1));

	return (
		<div className="card">
			<div style={{ display: 'flex', alignItems: 'baseline', gap: '1rem', marginBottom: '0.75rem' }}>
				<h2 style={{ marginTop: 0, marginBottom: 0 }}>Load Order Dependencies</h2>
				<span style={{ color: '#888', fontSize: '0.85rem' }}>Last updated: {LAST_UPDATED}</span>
			</div>
			<Message
				severity="warn"
				text="Gene loads remove cross-references from BioGrid-ORCS, ExpressionAtlas, and GEO CrossReference loads. These three loads must be rerun after every Gene load."
				style={{ marginBottom: '1rem', width: '100%' }}
			/>
			<div
				style={{
					overflowX: 'auto',
					border: '1px solid rgba(255, 255, 255, 0.1)',
					borderRadius: '8px',
					background: 'rgba(0, 0, 0, 0.15)',
				}}
			>
				<svg width={chartWidth} height={chartHeight} style={{ display: 'block' }}>
					<defs>
						{categoryEntries.map(([key, category]) => (
							<marker key={key} id={`arrow-${key}`} markerWidth="8" markerHeight="6" refX="7" refY="3" orient="auto">
								<polygon points="0 0, 8 3, 0 6" fill={category.color} />
							</marker>
						))}
					</defs>

					{/* Legend */}
					{categoryEntries.map(([key, category], index) => {
						const legendX = PADDING + index * legendSpacing;
						return (
							<g key={key}>
								<rect x={legendX} y={PADDING - 5} width="14" height="14" rx="3" fill={category.color} opacity="0.8" />
								<text x={legendX + 20} y={PADDING + 7} fill="#bbb" fontSize="12" fontFamily="sans-serif">
									{category.label}
								</text>
							</g>
						);
					})}
					<g>
						<rect
							x={PADDING + categoryEntries.length * legendSpacing}
							y={PADDING - 8}
							width="18"
							height="18"
							rx="3"
							fill="none"
							stroke="#FFA726"
							strokeWidth="2"
							strokeDasharray="4 2"
						/>
						<text
							x={PADDING + categoryEntries.length * legendSpacing + 24}
							y={PADDING + 7}
							fill="#bbb"
							fontSize="12"
							fontFamily="sans-serif"
						>
							Rerun after Gene
						</text>
					</g>

					{/* Edges */}
					{edges.map((edge) => {
						const highlighted = isEdgeHighlighted(edge);
						const dimmed = hoveredNode && !highlighted;
						const opacity = dimmed ? 0.05 : highlighted ? 0.85 : 0.3;
						const strokeWidth = highlighted ? 2.5 : 1;
						const controlY = (edge.y1 + edge.y2) / 2;
						const categoryKey = loadMap[edge.fromId].category;

						return (
							<path
								key={edge.key}
								d={`M ${edge.x1},${edge.y1} C ${edge.x1},${controlY} ${edge.x2},${controlY} ${edge.x2},${edge.y2}`}
								fill="none"
								stroke={CATEGORIES[categoryKey].color}
								strokeWidth={strokeWidth}
								opacity={opacity}
								markerEnd={`url(#arrow-${categoryKey})`}
								style={{ transition: 'opacity 0.15s, stroke-width 0.15s' }}
							/>
						);
					})}

					{/* Nodes */}
					{LOADS.map((load) => {
						const position = positions[load.id];
						if (!position) return null;
						const category = CATEGORIES[load.category];
						const isHovered = hoveredNode === load.id;
						const isDirectDep = hoveredLoad?.deps.includes(load.id);
						const dependsOnHovered = hoveredNode && load.deps.includes(hoveredNode);
						const isHighlighted = !hoveredNode || isHovered || isDirectDep || dependsOnHovered;

						return (
							<g
								key={load.id}
								onMouseEnter={() => setHoveredNode(load.id)}
								onMouseLeave={() => setHoveredNode(null)}
								style={{ cursor: 'pointer' }}
							>
								{/* Subtle glow for Gene node */}
								{load.id === 'gene' && (
									<rect
										x={position.x - 3}
										y={position.y - 3}
										width={NODE_W + 6}
										height={NODE_H + 6}
										rx={9}
										fill="none"
										stroke={category.color}
										strokeWidth="1"
										opacity="0.25"
									/>
								)}
								<rect
									x={position.x}
									y={position.y}
									width={NODE_W}
									height={NODE_H}
									rx={6}
									fill={isHovered ? category.color : 'rgba(30, 40, 55, 0.9)'}
									fillOpacity={isHovered ? 0.35 : isHighlighted ? 1 : 0.3}
									stroke={category.color}
									strokeWidth={isHovered ? 2.5 : load.id === 'gene' ? 2 : isHighlighted ? 1.5 : 0.5}
									strokeOpacity={isHighlighted ? 0.9 : 0.2}
									strokeDasharray={load.rerunAfterGene ? '6 3' : 'none'}
									style={{ transition: 'all 0.15s' }}
								/>
								<text
									x={position.x + NODE_W / 2}
									y={position.y + NODE_H / 2 + 1}
									textAnchor="middle"
									dominantBaseline="middle"
									fill={isHighlighted ? '#e0e0e0' : '#555'}
									fontSize="11"
									fontFamily="sans-serif"
									fontWeight={isHovered || load.id === 'gene' ? 'bold' : 'normal'}
									style={{ transition: 'fill 0.15s', pointerEvents: 'none' }}
								>
									{load.label}
								</text>
								<title>
									{`${load.label}\nCategory: ${category.label}\nDependencies: ${load.deps.length > 0 ? load.deps.map((depId) => loadMap[depId].label).join(', ') : 'None'}${load.rerunAfterGene ? '\n⚠ Must rerun after Gene load' : ''}`}
								</title>
							</g>
						);
					})}
				</svg>

				{/* Detail panel on hover */}
				{hoveredLoad && (
					<div
						style={{
							padding: '0.75rem 1rem',
							borderTop: '1px solid rgba(255, 255, 255, 0.1)',
							background: 'rgba(0, 0, 0, 0.1)',
							fontSize: '0.9rem',
							display: 'flex',
							gap: '2rem',
							flexWrap: 'wrap',
							alignItems: 'baseline',
						}}
					>
						<span style={{ fontWeight: 'bold', color: CATEGORIES[hoveredLoad.category].color }}>
							{hoveredLoad.label}
						</span>
						<span style={{ color: '#888' }}>{CATEGORIES[hoveredLoad.category].label} Load</span>
						{hoveredLoad.deps.length > 0 && (
							<span style={{ color: '#aaa' }}>
								<strong style={{ color: '#999' }}>Depends on:</strong>{' '}
								{hoveredLoad.deps.map((depId) => loadMap[depId].label).join(', ')}
							</span>
						)}
						{dependents.length > 0 && (
							<span style={{ color: '#aaa' }}>
								<strong style={{ color: '#999' }}>Required by:</strong>{' '}
								{dependents.map((dependent) => dependent.label).join(', ')}
							</span>
						)}
						{hoveredLoad.rerunAfterGene && (
							<span style={{ color: '#FFA726', fontWeight: 'bold' }}>Must rerun after Gene load</span>
						)}
					</div>
				)}
			</div>
			<p style={{ marginTop: '0.5rem', color: '#888', fontSize: '0.85rem' }}>
				Hover over a node to highlight its dependencies and dependents. Dashed borders indicate loads that must be rerun
				after Gene loads. Data flows top-to-bottom: run entity loads first, then dependent loads.
			</p>
		</div>
	);
};
