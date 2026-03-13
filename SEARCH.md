# Search Payload for /search Endpoints

## Overview

The `/search` endpoints query against OpenSearch (via Hibernate Search) and are used by the curation UI for browsing and filtering data. Each entity type exposes its own search endpoint.

## Endpoint

```
POST /api/{object}/search?page={page}&limit={limit}
```

`{object}` is any entity that has a corresponding table in the curation system (e.g., `gene`, `allele`, `disease-annotation`).

## Query Parameters

| Parameter | Type    | Default | Description                                      |
|-----------|---------|---------|--------------------------------------------------|
| `page`    | Integer | `0`     | Zero-based page number.                          |
| `limit`   | Integer | `10`    | Number of results per page.                      |

Results are fetched starting at offset `page * limit` and returning up to `limit` records.

## Request Body

The request body is a JSON object with the following top-level fields:

| Field                   | Required | Type                | Description                                                        |
|-------------------------|----------|---------------------|--------------------------------------------------------------------|
| `searchFilters`         | Yes      | Object              | Named filter groups containing field-level search criteria.        |
| `searchFilterOperator`  | No       | String              | How filters are combined: `"AND"` (default) or `"OR"`.            |
| `sortOrders`            | No       | Array               | Ordered list of sort directives.                                   |
| `aggregations`          | No       | Array of Strings    | Fields to aggregate (facet) in the results.                        |
| `nonNullFieldsTable`    | No       | Array of Strings    | Fields that must be non-null across the entire result set.         |
| `debug`                 | No       | String              | Set to `"true"` to include the generated query in the response.   |

### Full Example

```json
{
    "searchFilters": {
        "nameFilter": {
            "name": {
                "queryString": "pax6 pax7",
                "tokenOperator": "OR",
                "queryType": "matchQuery"
            }
        },
        "obsoleteFilter": {
            "obsolete": {
                "queryString": "false",
                "tokenOperator": "AND"
            },
            "internal": {
                "queryString": "false",
                "tokenOperator": "AND"
            }
        },
        "uniqueidFilter": {
            "uniqueId": {
                "queryString": "wb",
                "tokenOperator": "AND"
            },
            "nonNullFields": ["someRelation.curie"],
            "nullFields": ["deprecatedField"]
        }
    },
    "sortOrders": [
        { "field": "diseaseAnnotationSubject.symbol", "order": 1 },
        { "field": "diseaseAnnotationSubject.name", "order": 1 },
        { "field": "diseaseAnnotationSubject.primaryExternalId", "order": 1 }
    ],
    "aggregations": ["secondaryDataProvider.sourceOrganization.abbreviation"],
    "nonNullFieldsTable": [],
    "debug": "true"
}
```

---

## Search Filters

`searchFilters` is a map of **named filter groups**. Each key is an arbitrary name chosen by the caller (e.g., `"nameFilter"`, `"filter1"`). The names have no meaning to the server -- they only need to be unique within the request.

### How Filters Combine

By default, all filter groups are **AND**'d together:

> nameFilter **AND** obsoleteFilter **AND** uniqueidFilter

To **OR** them instead, set `searchFilterOperator` to `"OR"` at the top level:

> nameFilter **OR** obsoleteFilter **OR** uniqueidFilter

### Fields Within a Filter

Each filter group contains one or more **field entries** (plus optional `nonNullFields` / `nullFields` lists). The field entries within a single filter are **OR**'d together.

For example, given this filter:

```json
"obsoleteFilter": {
    "obsolete": { "queryString": "false", "tokenOperator": "AND" },
    "internal": { "queryString": "false", "tokenOperator": "AND" }
}
```

The logic is: `obsolete matches "false"` **OR** `internal matches "false"`.

#### Field Names

Field names correspond to entity properties and can be dot-separated to traverse nested relationships. For example, if the entity is a disease annotation:

- `"uniqueId"` -- a direct field on the disease annotation
- `"diseaseAnnotationSubject.taxon.curie"` -- traverses subject -> taxon -> curie

### Field Configuration

Each field entry is an object with the following properties:

| Property           | Required | Type    | Default                | Description                                                  |
|--------------------|----------|---------|------------------------|--------------------------------------------------------------|
| `queryString`      | Yes      | String  | --                     | The search text. Will be tokenized by whitespace.            |
| `tokenOperator`    | No       | String  | `"AND"`                | How tokens are matched: `"AND"` or `"OR"`.                   |
| `queryType`        | No       | String  | (simple query string)  | Set to `"matchQuery"` for a plain match query.               |
| `useKeywordFields` | No       | Boolean | `false`                | If `true`, queries against the `_keyword` variant of the field. |

#### queryString

The query string is tokenized by whitespace. Each token is matched according to the `tokenOperator`.

For example, with `"queryString": "pax6 pax7"`:
- `tokenOperator: "OR"` -- matches documents containing **pax6** or **pax7** (or both)
- `tokenOperator: "AND"` -- matches only documents containing **both** pax6 and pax7

#### queryType

Controls which type of OpenSearch query is generated:

- **Default (simple query string)** -- Supports special query syntax characters: `+` (must contain), `-` (must not contain), `"..."` (exact phrase), `*` (prefix wildcard), `(` `)` (grouping). Use this when the caller needs advanced search syntax.
- **`"matchQuery"`** -- A plain text match with no special syntax. All characters are treated as literal search terms. Use this for straightforward text matching.

#### useKeywordFields

When set to `true`, the query runs against the `_keyword` variant of the field (e.g., `uniqueId_keyword` instead of `uniqueId`). Keyword fields are not analyzed/tokenized, so queries match on the exact stored value. This is useful for exact-match filtering on identifiers or controlled vocabulary values.

When using simple query string mode (the default), setting `useKeywordFields: true` will query **both** the keyword field and the analyzed field, with the keyword field receiving a higher relevance boost.

### nonNullFields (optional)

A list of field names that must have a non-null value. This is specified **inside** a filter group and is AND'd with the filter's field criteria. Any documents where these fields are missing or null will be excluded.

```json
"myFilter": {
    "name": { "queryString": "pax6", "tokenOperator": "AND" },
    "nonNullFields": ["symbol", "taxon.curie"]
}
```

Logic: `(name matches "pax6") AND (symbol exists) AND (taxon.curie exists)`

### nullFields (optional)

A list of field names that must be null or empty. This is specified **inside** a filter group and is AND'd with the filter's field criteria. Only documents where these fields are missing or null will be included.

```json
"myFilter": {
    "name": { "queryString": "pax6", "tokenOperator": "AND" },
    "nullFields": ["deprecatedField"]
}
```

Logic: `(name matches "pax6") AND (deprecatedField does NOT exist)`

---

## Sort Orders (optional)

An ordered list of sort directives. Each entry has two fields:

| Property | Type    | Description                                         |
|----------|---------|-----------------------------------------------------|
| `field`  | String  | The entity field to sort on (supports dot notation). |
| `order`  | Integer | `1` for ascending, `-1` for descending.             |

Sorting is applied in the order the entries appear in the array.

**Note:** The system automatically appends `_keyword` to sort field names. When you specify `"field": "diseaseAnnotationSubject.symbol"`, the actual sort is performed on `diseaseAnnotationSubject.symbol_keyword`.

```json
"sortOrders": [
    { "field": "diseaseAnnotationSubject.symbol", "order": 1 },
    { "field": "diseaseAnnotationSubject.name", "order": -1 }
]
```

---

## Non Null Fields Table (optional)

A list of fields that must be non-null across the **entire** query result set. Unlike `nonNullFields` inside a filter (which is scoped to that filter's boolean context), this applies globally and filters out any record that has a null value in any of the listed fields.

```json
"nonNullFieldsTable": ["diseaseAnnotationSubject.symbol"]
```

---

## Aggregations (optional)

A list of field names to aggregate (facet). The results include term counts for each unique value in the specified fields. Each aggregation returns up to **30 terms** (the values with the highest document counts).

**Note:** Like sort fields, the system automatically appends `_keyword` to aggregation field names.

```json
"aggregations": ["secondaryDataProvider.sourceOrganization.abbreviation"]
```

---

## Debug (optional)

Set to the string `"true"` to include the generated OpenSearch query in the response. This is useful for troubleshooting search behavior. The value must be the **string** `"true"`, not a boolean.

```json
"debug": "true"
```

---

## Relevance Scoring

The system assigns boost values to filters based on their declaration order. Filters listed **first** in `searchFilters` receive a higher relevance boost than those listed later. Within a single filter, fields listed first also receive a higher boost than later fields.

This means the order of filters and fields affects result ranking (but not which results are returned).

---

## Behavior With Empty or Missing Filters

If `searchFilters` is empty (`{}`) or not provided, the query performs a **match all** and returns all documents (subject to `nonNullFieldsTable`, pagination, and sorting).

---

## Response Object

```json
{
    "results": [
        { "...entity..." },
        { "...entity..." }
    ],
    "totalResults": 1163,
    "returnedRecords": 2,
    "aggregations": {
        "secondaryDataProvider.sourceOrganization.abbreviation": {
            "RGD": 11297,
            "OMIM": 200,
            "Alliance": 14
        }
    },
    "esQuery": "...",
    "dbQuery": "..."
}
```

| Field             | Type    | Description                                                                                                    |
|-------------------|---------|----------------------------------------------------------------------------------------------------------------|
| `results`         | Array   | The page of entity objects matching the query.                                                                 |
| `totalResults`    | Long    | Total number of matching documents across all pages.                                                           |
| `returnedRecords` | Integer | Number of records in this page. Will equal `limit` except on the last page.                                    |
| `aggregations`    | Object  | Present only if `aggregations` was requested. Map of field name to value/count pairs.                          |
| `esQuery`         | String  | Present only when `debug` is `"true"`. The raw OpenSearch query that was generated.                            |
| `dbQuery`         | String  | Present only when `debug` is `"true"` on `/find` endpoints. The generated HQL query (not used by `/search`).  |

---

## Example

**Request:**

```
POST /api/disease-annotation/search?limit=1&page=0
```

**Request Body:**

```json
{
    "searchFilters": {
        "internalFilter": {
            "internal": {
                "queryString": "false",
                "tokenOperator": "OR"
            }
        },
        "obsoleteFilter": {
            "obsolete": {
                "queryString": "false",
                "tokenOperator": "OR"
            }
        },
        "uniqueidFilter": {
            "uniqueId": {
                "queryString": "wb",
                "tokenOperator": "AND"
            }
        }
    },
    "sortOrders": [
        { "field": "diseaseAnnotationSubject.symbol", "order": 1 },
        { "field": "diseaseAnnotationSubject.name", "order": 1 },
        { "field": "diseaseAnnotationSubject.primaryExternalId", "order": 1 }
    ],
    "aggregations": [],
    "nonNullFieldsTable": [],
    "debug": "true"
}
```

**Response:**

```json
{
    "results": [
        { "...disease annotation entity..." }
    ],
    "totalResults": 1163,
    "returnedRecords": 1,
    "debug": "true",
    "esQuery": "..."
}
```

When `debug` is enabled, the `esQuery` field contains the raw OpenSearch query. Below is the prettified version of a typical generated query:

```json
{
    "query": {
        "bool": {
            "must": [
                {
                    "simple_query_string": {
                        "boost": 31000.0,
                        "query": "false",
                        "default_operator": "or",
                        "fields": ["internal"]
                    }
                },
                {
                    "simple_query_string": {
                        "boost": 21000.0,
                        "query": "false",
                        "default_operator": "or",
                        "fields": ["obsolete"]
                    }
                },
                {
                    "simple_query_string": {
                        "boost": 11000.0,
                        "query": "wb",
                        "default_operator": "and",
                        "fields": ["uniqueId"]
                    }
                },
                {
                    "match_all": {}
                }
            ],
            "minimum_should_match": "0"
        }
    },
    "sort": [
        {
            "diseaseAnnotationSubject.symbol_keyword": {
                "order": "asc",
                "unmapped_type": "keyword"
            }
        },
        {
            "diseaseAnnotationSubject.name_keyword": {
                "order": "asc",
                "unmapped_type": "keyword"
            }
        },
        {
            "diseaseAnnotationSubject.primaryExternalId_keyword": {
                "order": "asc",
                "unmapped_type": "keyword"
            }
        }
    ]
}
```

Notice that the boost values decrease with each filter (31000, 21000, 11000) reflecting the declaration-order relevance scoring, and that the sort fields have `_keyword` appended automatically.
