# Find Payload for /find and /findForPublic Endpoints

## Overview

The `/find` and `/findForPublic` endpoints query the **relational database** (PostgreSQL via JPA/Hibernate) using exact field-level matching. These endpoints are used for precise lookups where you know the exact values you're searching for, as opposed to the `/search` endpoints which use OpenSearch for full-text searching.

## Endpoints

```
POST /api/{object}/find?page={page}&limit={limit}
POST /api/{object}/findForPublic?page={page}&limit={limit}&view={view}
```

`{object}` is any entity that has a corresponding table in the curation system (e.g., `gene`, `allele`, `disease-annotation`, `vocabularyterm`).

### /find vs /findForPublic

- **`/find`** -- Returns the full curation view of the data. Intended for internal curation use.
- **`/findForPublic`** -- Returns a filtered view of the data based on the `view` parameter. Intended for public-facing API consumers. The `view` parameter maps to a `CurationView` inner class name (e.g., `ForPublic`, `FieldsOnly`). If the view name is not recognized, it defaults to `ForPublic`.

## Query Parameters

| Parameter | Type    | Default      | Endpoint          | Description                                           |
|-----------|---------|--------------|-------------------|-------------------------------------------------------|
| `page`    | Integer | `0`          | Both              | Zero-based page number.                               |
| `limit`   | Integer | `10`         | Both              | Number of results per page.                           |
| `view`    | String  | `ForPublic`  | `/findForPublic`  | JSON view class name controlling response serialization. |

Results are fetched starting at offset `page * limit` and returning up to `limit` records.

### Count-Only Mode

When `page` is `0` **and** `limit` is `0`, the endpoint returns **only** the `totalResults` count without fetching any entity records. The `results` array will be empty. This is useful for getting a count of matching records without the overhead of loading entities.

When `limit` is greater than `0`, the endpoint returns the `results` array but does **not** populate `totalResults`.

---

## Request Body

The request body is a **flat JSON object** mapping field names to their expected values. All fields are matched using **exact equality** and are **AND**'d together by default.

### Basic Example

```json
{
    "vocabulary.vocabularyLabel": "disease_qualifier"
}
```

This returns all entities where `vocabulary.vocabularyLabel` equals exactly `"disease_qualifier"`.

### Full Example

```json
{
    "vocabulary.vocabularyLabel": "disease_qualifier",
    "obsolete": false,
    "query_operator": "or",
    "debug": "true"
}
```

---

## Fields

Each key in the request body is a field name on the entity. The corresponding value is what that field must equal.

### Field Names

Field names correspond to JPA entity properties. Use dot notation to traverse nested relationships:

- `"obsolete"` -- a direct field on the entity
- `"vocabulary.vocabularyLabel"` -- traverses the `vocabulary` relationship, then matches on `vocabularyLabel`
- `"diseaseAnnotationSubject.taxon.curie"` -- traverses subject -> taxon -> curie

When a field in the traversal path is a collection (List), the system automatically performs a LEFT JOIN to traverse into it.

### Supported Value Types

| JSON Type   | Java Type | Behavior                                                                                              |
|-------------|-----------|-------------------------------------------------------------------------------------------------------|
| String      | String    | Exact string equality.                                                                                |
| Number (int)| Integer   | Exact numeric equality.                                                                               |
| Number (long)| Long     | Exact numeric equality (for values exceeding Integer range).                                          |
| Boolean     | Boolean   | Exact boolean equality (`true` / `false`).                                                            |
| Array       | List      | Exact collection match -- the entity's collection must contain all listed values **and** be the same size. |
| null        | null      | Checks that the collection field is **empty** (has no elements).                                      |

**Note on null values:** Setting a field's value to `null` uses `isEmpty()` on the entity's collection field. This is specifically for checking empty collections, not for checking whether a scalar field is null.

**Note on Arrays:** When you pass a JSON array as a value, the system checks that (1) the entity's collection contains all values in the array, and (2) the collection has exactly the same number of elements. This is an **exact set match**, not a subset check.

### Examples

Exact string match:
```json
{ "curie": "HGNC:11998" }
```

Exact boolean match:
```json
{ "obsolete": false, "internal": false }
```

Nested field match:
```json
{ "vocabulary.vocabularyLabel": "disease_qualifier" }
```

---

## Reserved Keys

The following keys have special meaning and are **not** treated as field names:

### debug (optional)

Set to the string `"true"` to include the generated database queries in the response. The value must be the **string** `"true"`, not a boolean. Default is `"false"`.

When enabled, the response includes:
- `dbQuery` -- The generated HQL query string

```json
{ "debug": "true" }
```

### query_operator (optional)

Controls how fields are combined. By default, all field restrictions are **AND**'d together. Set to `"or"` to **OR** them instead.

```json
{
    "name": "pax6",
    "symbol": "pax6",
    "query_operator": "or"
}
```

This returns entities where `name` equals `"pax6"` **OR** `symbol` equals `"pax6"`.

| Value   | Behavior                        |
|---------|---------------------------------|
| (absent)| Fields are AND'd (default).     |
| `"or"`  | Fields are OR'd.                |
| anything else | Fields are AND'd.         |

---

## Sorting

Results are always sorted in **ascending** order by the entity's primary key (ID field). This ensures consistent ordering across pages.

---

## Response Object

```json
{
    "results": [
        { "...entity..." },
        { "...entity..." }
    ],
    "totalResults": 7,
    "returnedRecords": 2,
    "dbQuery": "..."
}
```

| Field             | Type    | Description                                                                                                             |
|-------------------|---------|-------------------------------------------------------------------------------------------------------------------------|
| `results`         | Array   | The page of entity objects matching the query. Empty when using count-only mode (`page=0`, `limit=0`).                  |
| `totalResults`    | Long    | Total number of matching records. **Only populated in count-only mode** (`page=0`, `limit=0`). Null otherwise.          |
| `returnedRecords` | Integer | Number of records in this page. Will equal `limit` except on the last page.                                             |
| `dbQuery`         | String  | Present only when `debug` is `"true"`. The generated HQL query string.                                                  |

---

## Example

**Request:**

```
POST /api/vocabularyterm/find?limit=10&page=0
```

**Request Body:**

```json
{
    "vocabulary.vocabularyLabel": "disease_qualifier",
    "debug": "true"
}
```

**Response:**

```json
{
    "results": [
        { "...vocabulary term entity..." },
        { "...vocabulary term entity..." }
    ],
    "returnedRecords": 7,
    "debug": "true",
    "dbQuery": "select alias_1764182562 from org.alliancegenome.curation_api.model.entities.VocabularyTerm alias_1764182562 where alias_1764182562.vocabulary.vocabularyLabel = disease_qualifier order by alias_1764182562.id asc nulls last"
}
```

---

## Count-Only Example

**Request:**

```
POST /api/vocabularyterm/find?limit=0&page=0
```

**Request Body:**

```json
{
    "vocabulary.vocabularyLabel": "disease_qualifier"
}
```

**Response:**

```json
{
    "results": [],
    "totalResults": 7,
    "returnedRecords": 0
}
```
