# Layers requirements

* 
  * As **an LM user**, I want **to assemble a layer collection** so that I can work with collected layers 
  * As a user I want to setup access levels to layer collection for 
    * in-house company use and may have several access levels within a company
    * public access
  * As a user I want to create and maintain a layer with sensitive data and restrict access to it (e.g. women crisis centers, centers against people trafficking, commercial data)
  * Do we create a plugin for github to manage layers?
  * 

    \
     
* As **a user of Layer Management app**, I want **create and store layers with personal data** so that **I can solve tasks by reaching people on the spot.** At the same time the personal data will comply with private data policies (<https://gdpr-info.eu/>)

Requirements and ideas from Kontur team:
* Platform: admin configures legends of the layers
  * As **an app admin**, I want **configure** layers' display and its legend so that **a user of the app understands what's shown** and the legend is clear to him.

    Legend configuration objectives are:
    * to set the desired colors or icons for the objects of the layer so that they are easy to read, 
    * to make the legend intuitive 
    * and not repeated
* Platform: admin wants to manage symbol layers - signatures
  * As **an admin**, I want **configure**
    * hexagon layers of contributors on the map
    * layer of locations signatures on the map

     so that **each type of signatures is clear** whom does it belong to
* Platform: admin wants to upload a layer with renewable data
  * as a admin I want to
    * configure layers with renewable data, 
    * set frequency update period, 
    * determine what data to show on the map, 
    * customize the data source, 
    * filter results, 
    * style the data layer.
* Platform: admin wants to upload layers to LM
  * Upload layers to show data in the app for users to see and utilize
  * Do actions with layers
    * Make a layer visible/invisible

      Toggle layer visibility on/off by tapping the switch on the layers folder icon. Switching visibility to off means all features and sublayers will not be displayed on the base map. 
    * Edit Name - To edit a layer's name simply tap on a layer's name in the upper tool bar.
    * Reorder Layers - To reorder layers, tap the 'Edit' button in the upper right corner of the layer panel. You'll see drag bars appear. Tap and drag on these bars to move layers up or down in the hierarchy. 
    * Delete Layers

      To delete layers, tap the 'Edit' button in the upper right corner of the layer panel. You'll see red delete buttons appear next to each layer. Tap the delete button on the layer you want to delete, then confirm your decision.  
    * Add Layer

      This feature allows you to create a new sublayer inside the current layer you're viewing. 
    * Number of Features in Layer

      This displays the number of features housed inside a sublayer. 
    * Share Layers

      To access sharing option for a layer (or entire project if you're viewing the master layer) simply tap Share in the options bar at the bottom of the layers panel. 
    * Import Layers

      To import data to your layers, simply tap Import button in the options bar at the bottom of the layers panel. 
    * Move A Layer

      Use this feature to move a layer inside or out of another layer.
    * Layer Opacity

      Set the opacity of a layer and all it's sublayers. 
    * Lock Layer

      This feature locks a layer, meaning it is visible on the base map but features in that layer are not selectable.
* Platform: admin/user controls the layer zoom:
  * As **and admin of the LM app**, I want **configure and set up layer visibility zoom** so that **a user sees the map at the zoom that allows to read address signatures on the map**
* Platform: I want to manage sequence order to show layers on the map
  * User/admin wants to control the order of the layers (to place important layers on top, unimportant ones below or group them thematically)
* Platform: user wants to upload custom-made layer to the map
  * User wants to upload a layer to compare/validate the data
  * User wants to review it later - after a while - save it/export possible?
  * User wants to upload a layer so that visitors of the user's site can view this information (as part of the download, 
  * User wants to add the name / description of the lay
* Platform: I want to manage layers from Bivariate and Editor-Layer-Index \[no contacts\]
  * AS IS

    User can see the correlation between layers, see B-layer on the map and do these things for the selected polygon and inside BM

    User can turn on/off and select polygons ELI layers inside ELI
  * TO BE

    User can manage access privileges ELI layers through Layer Management

    ? Do we need to add user's layers to ELI collection

    ? What do we need in LM from BM perspective 
* As admin of DN2 I want to be able to add new layers in DN2 \[duplicate\]
  * is is an **internal request** to speed up the process of adding new layers on DN2 for public use

    This request appeared as part of US [[Tasks/User Story: Ukraine response #^7a8452d0-8558-11ea-8035-51799a2fd608/462af9c0-9f7e-11ec-b0f9-0fe9f5eaf7fd]]  
    * Need to determine \~frequency of such requests.
    * Can we do it using some ETL process?
    * To discuss next sync
* 
