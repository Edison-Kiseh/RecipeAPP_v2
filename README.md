# RecipeAPP_v2

This is the version two of my recipe application. 

The key differences include:
  - Storage of the recipes in firebase and not locally (implying that the app now requires connection to the internet to run)
  - Added notifications to the app
  - Added some unit tests for basic functionality and for the viewModel
  - Images for recipes can no longer be loaded locally from the device by the user. This is because as already mentionned, it now uses firebase and the storage and retrieval of images is a tad too       complicated, as a result, a static image is defined for every recipe.
