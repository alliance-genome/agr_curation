# Find payload for /find endpoints

## Endpoints

Endpoints are in the following structure

POST /api/{object}/find

{object} will be any of the objects that we have tables for in the UI and the database

## Query Parameters

### Page

This will be a zero based page of results that is pulled from the database. 

### Limit 

This will be the size of the page that comes back. 

## POST Search Payload Example

```javascript
{
    "vocabulary.vocabularyLabel": "disease_genetic_modifier_relation",
    "debug": "true"
}
```

### Fields (required)

Fields are just a list of fields and their values to search the datgabase. All are exact matches and they are all ANDed together.

### Debug (optional)

Debug true will turn on some extra debugging in order to see the query getting sent to ElasticSearch and some duration statistics. Default value is false.

## Return Object "SearchResults"

```javascript
{
    "results": [
        { ... },
        { ... },
        { ... },
        { ... },
        { ... }
    ],
    "aggregations": {
        "secondaryDataProvider.sourceOrganization.abbreviation": {
            "rgd": 11297,
            "omim": 200,
            "alliance": 14
        }
    }
    "totalResults": 1163,
    "returnedRecords": 5
}
```

### Results

This is the list of objects coming back from the system.

### Aggregations

Aggregations will be an object containing fields by field name. Each entry will have all the values under, with the counts.

### Total Results

This is the count of the total results found on the whole query.

### Returned Results

This is the count of the page of results, for each page should be the count that is in the result set. May be smaller then limit on the last page.
