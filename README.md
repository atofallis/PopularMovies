# PopularMovies
Udacity training material

## Setup instructions
For the network requests to work you must set your own api_key value in api.xml under the [resources](app/src/main/res/values) directory.
Example api.xml:
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="api_key">YOUR_API_KEY_HERE</string>
</resources>
```
api.xml is listed in the [.gitignore](.gitignore#L10) file so you can safely add this file locally.

If you do not have an api key, first register with themoviedb.org and then request API access from
https://www.themoviedb.org/settings/api

