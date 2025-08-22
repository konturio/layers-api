# Layers API 

Field: Content

DEV swagger: <https://test-api02.konturlabs.com/layers/v2/doc> 

TEST swagger: <https://test-api.konturlabs.com/layers/v2/doc>

PROD swagger: <https://api.kontur.io/layers/v2/doc> 

Common Rules: 
* Geometry passed as GeoJSON geometry in request body
* All fields have cammelCase names
* All /collections\* endpoints support Authorization header.

API conforms to [OGC API - Features - Part 1: Core](https://ogcapi.ogc.org/features/ "https://ogcapi.ogc.org/features/") with additions:
* Environments:**
* dev <https://test-api02.konturlabs.com/layers/v2/swagger-ui/index.html?configUrl=/layers/v2/v3/api-docs/swagger-config> 
* GET /collections**

The feature collections (Layers) in the dataset. Returns list of public layers and layers owned by authorized user.

Request params:
* limit - int
* offset - int

The response contains the list of collections. For each collection, a link to the items in the collection (path **`/collections/{collectionId}/items`**, link relation **`items`**) as well as key information about the collection. This information includes:
* A local identifier for the collection that is unique for the dataset;
* A list of coordinate reference systems (CRS) in which geometries may be returned by the server. The first CRS is the default coordinate reference system (the default is always WGS 84 with axis order longitude/latitude);
* An optional `title` and `description` for the collection;
* An optional `extent` that can be used to provide an indication of the spatial and temporal extent of the collection - typically derived from the data;
* `itemType` - An optional indicator about the type of the items in the collection (the default value, if the indicator is not provided, is 'feature').
* `links`- list of links with predefined number of relations:
  * `items` - layer have dependent features. Path `/collections/{collectionId}/items`
  * `tiles` - path to MVT tiles. E.g. <https://example.com/tiles/{z}/{x}/{y}.pbf>
* From [DB schema](https://dbdiagram.io/d/61bb2e458c901501c0f38ba5 "https://dbdiagram.io/d/61bb2e458c901501c0f38ba5"):
  * copyrights
  * properties
  * legend - {       type: bivariate | simple,       linkProperty: "link"       steps: \[         {           paramName: status,           paramValue: string | number,           stepName: string,           stepShape: 'square' | 'circle' | 'hex' | 'icon',           style: {             fill-color: 'string'.             text-color: 'string',             ... -- any MapCSS style             }         },       \],     }
  * type {vector-mvt | vector-geojson-url | vector-geojson | maplibre-style-url | maplibre-style | raster-xyz | raster-tms | raster-quadkey | raster-wms | raster-dem}
  * style
  * popup_config
  * group
    * name
    * is_opened
    * mutually_exclusive
  * category
    * name
    * is_opened
    * mutually_exclusive

```
{
  "links": [
    {
      "href": "http://data.example.org/collections.json",
      "rel": "self",
      "type": "application/json",
      "title": "this document"
    }
  ],
  "collections": [
    {
      "id": "buildings",
      "title": "Buildings",
      "description": "Buildings in the city of Bonn.",
      "extent": {
        "spatial": {
          "bbox": [
            [
              7.01,
              50.63,
              7.22,
              50.78
            ]
          ]
        },
        "temporal": {
          "interval": [
            [
              {},
              null
            ]
          ]
        }
      },
      "itemType": "feature",
      "copyrights": "",
      "properties": {},
      "legend": {},
      "type": {},
      "style": {},
      "popupConfig": {},
      "group": {},
      "category": {},
      "links": [
        {
          "href": "http://data.example.org/collections/buildings/items",
          "rel": "items",
          "type": "application/geo+json",
          "title": "Buildings"
        }
      ]
    }
  ]
}
```
* GET /collections/{collectionId}**

Returns a layer by id. Return either public layer or layer owned by authorized user.

```
{
  "id": "buildings",
  "title": "Buildings",
  "description": "Buildings in the city of Bonn.",
  "ownedByUser": false,
  "extent": {
    "spatial": {
      "bbox": [
        [
          7.01,
          50.63,
          7.22,
          50.78
        ]
      ]
    },
    "temporal": {
      "interval": [
        [
          {},
          null
        ]
      ]
    }
  },
  "links": [
    {
      "href": "http://data.example.org/collections/buildings/items",
      "rel": "items",
      "type": "application/geo+json",
      "title": "Buildings"
    }
  ]
}
```
* GET /collections/{collectionId}/items**

Fetch features of the feature collection with id **`collectionId`**.

Request params:
* limit - int
* offset - int
* bbox - \[int\] - Only features that have a geometry that intersects the bounding box are selected. The bounding box is provided as four numbers. The coordinate reference system of the values is WGS 84
* datetime - string - Either a date-time or an interval, open or closed. Date and time expressions adhere to RFC 3339. Open intervals are expressed using double-dots.

  Examples:
  * A date-time: "2018-02-12T23:20:50Z"
  * A closed interval: "2018-02-12T00:00:00Z/2018-03-18T12:31:12Z"
  * Open intervals: "2018-02-12T00:00:00Z/.." or "../2018-03-18T12:31:12Z"

  Only features that have a temporal property that intersects the value of **`datetime`** are selected.

Response:

FeatureCollection GeoJson.

```
{
  "type": "FeatureCollection",
  "links": [
    {
      "href": "http://data.example.com/collections/buildings/items.json",
      "rel": "self",
      "type": "application/geo+json",
      "title": "this document"
    },
    {
      "href": "http://data.example.com/collections/buildings/items.json&offset=10&limit=2",
      "rel": "next",
      "type": "application/geo+json",
      "title": "next page"
    }
  ],
  "timeStamp": {},
  "numberMatched": 123,
  "numberReturned": 2,
  "features": [
    {
      "type": "Feature",
      "id": "123",
      "geometry": {
        "type": "Polygon",
        "coordinates": [
          "..."
        ]
      },
      "properties": {
        "function": "residential",
        "floors": "2"
      }
    }
  ]
}
```
* GET /collections/{collectionId}/items/{featureId}**

Returns feature by layer id and feature id.

```
{
  "type": "Feature",
  "links": [
    {
      "href": "http://data.example.com/collections/buildings/items/123.json",
      "rel": "self",
      "type": "application/geo+json",
      "title": "this document"
    },
    {
      "href": "http://data.example.com/collections/buildings",
      "rel": "collection",
      "type": "application/geo+json",
      "title": "the collection document"
    }
  ],
  "id": "123",
  "geometry": {
    "type": "Polygon",
    "coordinates": [
      "..."
    ]
  },
  "properties": {
    "function": "residential",
    "floors": "2",
    "lastUpdate": {}
  }
}
```
* POST /collections/search**

Retrieve layers matching filters:
* geometry - geojson geometry
* collectionOwner - ANY, ME, NOT_ME - limit to collections created by authenticated user or created by others
* limit - int
* offset - int
* POST /collections/{collectionId}/items/search**

Retrieve features of the collection {collectionId} matching filters
* bbox - \[int\] - Only features that have a geometry that intersects the bounding box are selected. The bounding box is provided as four numbers. The coordinate reference system of the values is WGS 84
* datetime - string - Either a date-time or an interval, open or closed. Date and time expressions adhere to RFC 3339. Open intervals are expressed using double-dots.

  Examples:
  * A date-time: "2018-02-12T23:20:50Z"
  * A closed interval: "2018-02-12T00:00:00Z/2018-03-18T12:31:12Z"
  * Open intervals: "2018-02-12T00:00:00Z/.." or "../2018-03-18T12:31:12Z"

  Only features that have a temporal property that intersects the value of **`datetime`** are selected.
* geometry - geojson geometry
* limit - int
* offset - int
* POST /collections**

Create user layer. Requires authorization

Body:

```
{
    title: string,
    description: string,
    link: {
        rel: 'tiles'
        href: string
        } --optional, for mvt tiles only
    itemType: string,
    copyrights: string,
    properties: {json object},
    geometry: geojson geometry,
    legend: {<TBD>}, -- do we need it for user layers?
    group: {<TBD>}, -- do we need it for user layers?
    category: {<TBD>} -- do we need it for user layers?
}
```
* PUT /collections/{id}**

Update layer. Requires authorization
* DELETE /collections/{id}**

Delete layer. Requires authorization
* PUT /collections/{id}/items/**

Upsert layer features. Requires authorization

Body - FeatureCollection geojson
* DELETE /collections/{id}/items/{id}**

Delete layer feature. Requires authorization
