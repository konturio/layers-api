# Release notes

## UNRELEASED

#### Added

#### Changed
- #10168 Layers search query was updated
- #9861 Add prometheus metrics

#### Removed

#### Installation sequence



## Release 1.3.0 - 2022.05.05

#### Added

#### Changed
- #10079 Improve search collection response time 

#### Removed

#### Installation sequence



## Release 1.2.0 - 2022.04.12

#### Added
- #9725 appId parameter into feature search endpoint
- #9819 Pass collections displayRule to API

#### Changed
- PUT empty FeatureCollection into /collections/{collectionId}/items will not lead to an error

#### Removed

#### Installation sequence



## Release 1.1.0 - 2022.04.01

#### Added
- Newly added layers are inserted into user_layer group 
- Add layer group and category into POST /collections and PUT /collections/{id} response 
- PUT /collections/{collectionId}/items now removes features that are not present in the request
- Enriched layers with featureProperties field
- Add ownedByUser layer flag
- Layer ID is optional for create operations. Will be generated if missing
- Layers styles are saved in DB now
- One can search only through collections created by themselves or others users
- New collectionIds parameter limits collections result in search endpoint
- Add CRUD REST for Applications

#### Changed
- Bug fixes

#### Removed

#### Installation sequence
- Install migrations from Layers DB repo
  - layers.feature_properties field is added 
  - apps and apps_layers tables are added