# Search payload for /search endpoints

## Endpoints

Endpoints are in the following structure

POST /api/{object}/search

{object} will be any of the objects that we have tables for in the UI and the database

## Query Parameters

### Page

This will be a zero based page of results that is pulled from the database. 

### Limit 

This will be the size of the page that comes back. 

## POST Search Payload Example

```javascript
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
                "tokenOperator": "AND",
                "useKeywordFields": false
            }
            "nonNullFields": [],
            "nullFields": [],
        }
    },
    "sortOrders": [
        {
            "field": "diseaseAnnotationSubject.symbol",
            "order": 1
        },
        {
            "field": "diseaseAnnotationSubject.name",
            "order": 1
        },
        {
            "field": "diseaseAnnotationSubject.modEntityId",
            "order": 1
        }
    ],
    "aggregations": [],
    "nonNullFieldsTable": [],
    "debug": "true",
}

```

### Search filters

#### Filters

All search filters need to have a unique name. Names only have meaning to the caller of the endpoint. In the above example "nameFilter" is only the name of the filter. It could be called "filter1" or any other name that makes sense to the caller.

Filters will be "AND"ed together so in the above example "nameFilter" AND "obsoleteFilter" AND "uniqueidFilter".

#### Fields

Inside a single filter there can be multiple items listed. These items are the names of the fields in the linkML model starting with {object} and its field. These fields can be chanined together to get access to nested properties as in if {object} is a disease annotation then a field could be "diseaseAnnotationSubject.taxon.curie", which would query against the tax id that is on the subject of a disease annotation.

Fields inside a single filter are "OR"ed together, in the above example for "obsoleteFilter" we are saying where field "obsolete" is false OR field "internal" is false.

##### Query String

The queryString inside a field will be tokenized and a match will be performed on each token.

##### Query Type

Query type only has one option "matchQuery" this will do a "match" query in Elastic Search vs any other value it will do a simpleQueryString search instead.

##### Use Keyword Fields

This option will run against the _keyword fields vs the regular field names. In the above example for the uniqueId field the uniqueId_keyword field will be used instead.

##### Token Operator

This option controls how the tokens are matched. Values are "AND" or "OR". So in the above example the name field will have to match pax6 OR pax

#### Non Null Fields

In the context of a single filter we can specify that certain fields needs to be populated, this will be AND'ed to the field criteria. As in this list of fields must have a non null value.

#### Null Fields

In the context of a single filter we can specify that certain field needs to be empty or null, this will be AND'ed to the field criteria. As in this list of fields must have a non null value.

### Sort Orders

Sort orders is a list of objects that contain two fields on called "field" and the other called "order" field is the field in elasticSearch that we are going to order on which can also be a nest object field. Order is of two values 1 meaning ascending and -1 descending.

### Non Null Fields Table

This is a list of fields that have to be non null across the whole "table" or query. This will filter out all records that have null's in the fields in this list.

### Aggregations

This is a list of fields that we will aggregate the results on and add to the returned object more about this in the response object below.

### Debug

Debug true will turn on some extra debugging in order to see the query getting sent to ElasticSearch and some duration statistics.

## Return Object "SearchResults"

