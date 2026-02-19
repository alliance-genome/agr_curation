package org.alliancegenome.curation_api.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;

/**
 * OASFilter that optimizes the generated OpenAPI schema for Swagger UI.
 *
 * This filter performs two optimizations:
 * 1. Breaks circular $ref chains that cause Swagger UI to recurse infinitely.
 * 2. Limits schema nesting depth by truncating $ref properties in schemas
 *    with large expansion trees, preventing browser hangs.
 *
 * Both optimizations work by modifying Schema objects in-place (via addProperty,
 * setItems) rather than modifying the unmodifiable schemas map.
 */
public class OpenApiSchemaFilter implements OASFilter {

	private static final Logger LOG = Logger.getLogger(OpenApiSchemaFilter.class.getName());
	private static final String REF_PREFIX = "#/components/schemas/";

	@Override
	public void filterOpenAPI(OpenAPI openAPI) {
		if (openAPI.getComponents() == null || openAPI.getComponents().getSchemas() == null) {
			return;
		}
		Map<String, Schema> schemas = openAPI.getComponents().getSchemas();

		// Phase 1: Break circular references
		breakCircularRefs(schemas);

		// Phase 2: Limit schema nesting depth
		limitSchemaDepth(schemas);

		// Phase 3: Clean up SmallRye cyclic reference descriptions
		cleanCyclicRefDescriptions(schemas);

		// Phase 4: Add CRUD tags to search/find/findForPublic operations
		propagateCrudTags(openAPI);

		LOG.info("OpenAPI schema filter: processed " + schemas.size() + " schemas");
	}

	/**
	 * For /search, /find, and /findForPublic operations, finds the CRUD tag
	 * from sibling operations under the same base path and adds it so the
	 * endpoint appears under both its browsing category and its entity's
	 * CRUD section in Swagger UI.
	 */
	private void propagateCrudTags(OpenAPI openAPI) {
		if (openAPI.getPaths() == null) {
			return;
		}
		Map<String, PathItem> paths = openAPI.getPaths().getPathItems();
		if (paths == null) {
			return;
		}

		// Build a map of base path -> CRUD tag by looking at non-search/find operations
		Map<String, String> baseCrudTags = new HashMap<>();
		for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
			String path = entry.getKey();
			if (path.endsWith("/search") || path.endsWith("/find") || path.endsWith("/findForPublic")) {
				continue;
			}
			PathItem pathItem = entry.getValue();
			String crudTag = findCrudTag(pathItem);
			if (crudTag != null) {
				baseCrudTags.put(path, crudTag);
			}
		}

		int tagged = 0;
		for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
			String path = entry.getKey();
			if (!path.endsWith("/search") && !path.endsWith("/find") && !path.endsWith("/findForPublic")) {
				continue;
			}
			// Derive the base path by removing the suffix
			String basePath = path.substring(0, path.lastIndexOf('/'));
			// Look for a CRUD tag on any path that starts with the base path
			String crudTag = null;
			for (Map.Entry<String, String> tagEntry : baseCrudTags.entrySet()) {
				if (tagEntry.getKey().startsWith(basePath)) {
					crudTag = tagEntry.getValue();
					break;
				}
			}
			if (crudTag == null) {
				continue;
			}
			// Add the CRUD tag to all operations on this path
			PathItem pathItem = entry.getValue();
			for (Operation op : getOperations(pathItem)) {
				if (op.getTags() != null && !op.getTags().contains(crudTag)) {
					op.addTag(crudTag);
					tagged++;
				}
			}
		}
		LOG.info("OpenAPI schema filter: added CRUD tags to " + tagged + " search/find operations");
	}

	private String findCrudTag(PathItem pathItem) {
		for (Operation op : getOperations(pathItem)) {
			if (op.getTags() != null) {
				for (String tag : op.getTags()) {
					if (tag.startsWith("CRUD")) {
						return tag;
					}
				}
			}
		}
		return null;
	}

	private List<Operation> getOperations(PathItem pathItem) {
		List<Operation> ops = new ArrayList<>();
		if (pathItem.getGET() != null) {
			ops.add(pathItem.getGET());
		}
		if (pathItem.getPOST() != null) {
			ops.add(pathItem.getPOST());
		}
		if (pathItem.getPUT() != null) {
			ops.add(pathItem.getPUT());
		}
		if (pathItem.getDELETE() != null) {
			ops.add(pathItem.getDELETE());
		}
		if (pathItem.getPATCH() != null) {
			ops.add(pathItem.getPATCH());
		}
		return ops;
	}

	/**
	 * Detects and breaks circular $ref chains using DFS back-edge detection.
	 */
	private void breakCircularRefs(Map<String, Schema> schemas) {
		Map<String, Set<String>> graph = buildRefGraph(schemas);

		List<String[]> backEdges = findBackEdges(graph);
		LOG.info("OpenAPI schema filter: found " + backEdges.size() + " circular reference back-edges");

		int broken = 0;
		for (String[] edge : backEdges) {
			Schema srcSchema = schemas.get(edge[0]);
			if (srcSchema != null && breakRefsInSchema(srcSchema, REF_PREFIX + edge[1], edge[1])) {
				broken++;
			}
		}
		LOG.info("OpenAPI schema filter: broke " + broken + " circular references");
	}

	/**
	 * Limits schema nesting depth by globally truncating $ref properties that
	 * point to complex schemas. Any property referencing a schema with more
	 * than MAX_TARGET_REFS outgoing refs is replaced with a placeholder.
	 * This prevents Swagger UI from recursively expanding deep schema trees.
	 */
	private static final int MAX_TARGET_REFS = 3;

	private void limitSchemaDepth(Map<String, Schema> schemas) {
		Map<String, Set<String>> graph = buildRefGraph(schemas);

		int truncated = 0;
		for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
			if (truncateDeepRefs(entry.getValue(), graph)) {
				truncated++;
			}
		}
		LOG.info("OpenAPI schema filter: truncated deep refs in " + truncated + " schemas");
	}

	/**
	 * Cleans up SmallRye's "Cyclic reference to fully.qualified.ClassName"
	 * descriptions by stripping the package path, leaving just the class name.
	 */
	private void cleanCyclicRefDescriptions(Map<String, Schema> schemas) {
		for (Schema schema : schemas.values()) {
			cleanCyclicDescInSchema(schema);
		}
	}

	private void cleanCyclicDescInSchema(Schema schema) {
		if (schema == null) {
			return;
		}

		String desc = schema.getDescription();
		if (desc != null && desc.startsWith("Cyclic reference to ")) {
			String fqcn = desc.substring("Cyclic reference to ".length());
			int lastDot = fqcn.lastIndexOf('.');
			String className = lastDot >= 0 ? fqcn.substring(lastDot + 1) : fqcn;
			schema.setTitle(className);
			schema.setDescription("See " + className + " schema");
		}

		if (schema.getProperties() != null) {
			for (Schema propSchema : schema.getProperties().values()) {
				cleanCyclicDescInSchema(propSchema);
			}
		}
		if (schema.getItems() != null) {
			cleanCyclicDescInSchema(schema.getItems());
		}
		if (schema.getAllOf() != null) {
			for (Schema s : schema.getAllOf()) {
				cleanCyclicDescInSchema(s);
			}
		}
	}

	/**
	 * Replace $ref properties that point to complex schemas (more than
	 * MAX_TARGET_REFS outgoing refs) with type: object placeholders.
	 */
	private boolean truncateDeepRefs(Schema schema, Map<String, Set<String>> graph) {
		boolean truncated = false;

		Map<String, Schema> props = schema.getProperties();
		if (props != null) {
			for (Map.Entry<String, Schema> entry : new ArrayList<>(props.entrySet())) {
				Schema propSchema = entry.getValue();
				String targetName = extractRefName(propSchema);
				String arrayTargetName = propSchema.getItems() != null
					? extractRefName(propSchema.getItems()) : null;

				String refTarget = targetName != null ? targetName : arrayTargetName;
				if (refTarget == null) {
					continue;
				}

				// Truncate refs to schemas that have their own outgoing refs
				Set<String> targetRefs = graph.getOrDefault(refTarget, Collections.emptySet());
				if (targetRefs.size() > MAX_TARGET_REFS) {
					if (targetName != null) {
						schema.addProperty(entry.getKey(), createPlaceholder(targetName));
					} else {
						propSchema.setItems(createPlaceholder(arrayTargetName));
					}
					truncated = true;
				}
			}
		}

		// Also check allOf members
		if (schema.getAllOf() != null) {
			for (Schema allOfMember : schema.getAllOf()) {
				if (allOfMember.getRef() == null) {
					if (truncateDeepRefs(allOfMember, graph)) {
						truncated = true;
					}
				}
			}
		}

		return truncated;
	}

	private String extractRefName(Schema schema) {
		if (schema == null) {
			return null;
		}
		String ref = schema.getRef();
		if (ref != null && ref.startsWith(REF_PREFIX)) {
			return ref.substring(REF_PREFIX.length());
		}
		return null;
	}

	private Map<String, Set<String>> buildRefGraph(Map<String, Schema> schemas) {
		Map<String, Set<String>> graph = new HashMap<>();
		for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
			Set<String> refs = new HashSet<>();
			collectSchemaRefs(entry.getValue(), refs);
			refs.retainAll(schemas.keySet());
			graph.put(entry.getKey(), refs);
		}
		return graph;
	}

	private void collectSchemaRefs(Schema schema, Set<String> refs) {
		if (schema == null) {
			return;
		}

		String ref = schema.getRef();
		if (ref != null && ref.startsWith(REF_PREFIX)) {
			refs.add(ref.substring(REF_PREFIX.length()));
			return;
		}

		if (schema.getProperties() != null) {
			for (Schema propSchema : schema.getProperties().values()) {
				collectSchemaRefs(propSchema, refs);
			}
		}
		if (schema.getItems() != null) {
			collectSchemaRefs(schema.getItems(), refs);
		}
		if (schema.getAllOf() != null) {
			for (Schema s : schema.getAllOf()) {
				collectSchemaRefs(s, refs);
			}
		}
		if (schema.getAnyOf() != null) {
			for (Schema s : schema.getAnyOf()) {
				collectSchemaRefs(s, refs);
			}
		}
		if (schema.getOneOf() != null) {
			for (Schema s : schema.getOneOf()) {
				collectSchemaRefs(s, refs);
			}
		}
		if (schema.getAdditionalPropertiesSchema() != null) {
			collectSchemaRefs(schema.getAdditionalPropertiesSchema(), refs);
		}
	}

	private List<String[]> findBackEdges(Map<String, Set<String>> graph) {
		Map<String, Integer> color = new HashMap<>();
		graph.keySet().forEach(n -> color.put(n, 0));
		List<String[]> backEdges = new ArrayList<>();

		for (String node : graph.keySet()) {
			if (color.get(node) == 0) {
				dfs(node, graph, color, backEdges);
			}
		}
		return backEdges;
	}

	private void dfs(String node, Map<String, Set<String>> graph,
			Map<String, Integer> color, List<String[]> backEdges) {
		color.put(node, 1);
		for (String neighbor : graph.getOrDefault(node, Collections.emptySet())) {
			Integer c = color.get(neighbor);
			if (c == null) {
				continue;
			}
			if (c == 1) {
				backEdges.add(new String[]{node, neighbor});
			} else if (c == 0) {
				dfs(neighbor, graph, color, backEdges);
			}
		}
		color.put(node, 2);
	}

	/**
	 * Breaks $ref references to the target schema within the source schema.
	 * Handles direct property $refs, array items $refs, and allOf member properties.
	 * Does NOT break allOf inheritance $refs (to preserve schema hierarchy).
	 */
	private boolean breakRefsInSchema(Schema schema, String targetRef, String targetName) {
		boolean broken = false;

		Map<String, Schema> props = schema.getProperties();
		if (props != null) {
			for (Map.Entry<String, Schema> entry : new ArrayList<>(props.entrySet())) {
				Schema propSchema = entry.getValue();
				if (targetRef.equals(propSchema.getRef())) {
					schema.addProperty(entry.getKey(), createPlaceholder(targetName));
					broken = true;
				} else if (propSchema.getItems() != null
						&& targetRef.equals(propSchema.getItems().getRef())) {
					propSchema.setItems(createPlaceholder(targetName));
					broken = true;
				}
			}
		}

		if (schema.getAllOf() != null) {
			for (Schema allOfMember : schema.getAllOf()) {
				if (allOfMember.getRef() == null) {
					if (breakRefsInSchema(allOfMember, targetRef, targetName)) {
						broken = true;
					}
				}
			}
		}

		return broken;
	}

	private Schema createPlaceholder(String targetName) {
		String className = targetName.replace("_", "");
		Schema placeholder = OASFactory.createObject(Schema.class);
		placeholder.addType(SchemaType.OBJECT);
		placeholder.setTitle(className);
		placeholder.setDescription("See " + className + " schema");
		return placeholder;
	}
}
