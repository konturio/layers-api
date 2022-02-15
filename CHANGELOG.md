# Release notes

## UNRELEASED

#### Added
- Newly added layers are inserted into user_layer group 
- Add layer group and category into POST /collections and PUT /collections/{id} response 
- PUT /collections/{collectionId}/items now removes features that are not present in the request
- Enriched layers with featureProperties field
- Add ownedByUser layer flag
- Layer ID is optional for create operations. Will be generated if missing

#### Changed
- Bug fixes

#### Removed

#### Installation sequence
- Install migrations from Layers DB repo
  - layers.feature_properties field is added 