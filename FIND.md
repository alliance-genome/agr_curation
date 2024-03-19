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
    "vocabulary.vocabularyLabel": "disease_qualifier",
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
    ],
    "totalResults": 7,
    "returnedRecords": 7,
    "dbQuery": "select alias_1764182562 from org.alliancegenome.curation_api.model.entities.VocabularyTerm alias_1764182562 where alias_1764182562.vocabulary.vocabularyLabel = disease_qualifier order by alias_1764182562.id asc nulls last"
}
```

### Results

This is the list of objects coming back from the system.

### Total Results

This is the count of the total results found on the whole query.

### Returned Results

This is the count of the page of results, for each page should be the count that is in the result set. May be smaller then limit on the last page.

### DB Query

This is the JQL query that will be run against the database.