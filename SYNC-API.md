# API For sync

To sync with a server you need to provide a unique url which supports GET and POST :
The url is configued in HifzoSyncAction.java

# GET : List talibs

{ "serverKey": "unikePK:can be any string",
  "talibName":"A non empty name for display",
  "talibUrl":"A browser url to open" }

POST : add reviewRanges to talib

{ "serverKey": "",
  "reviewRanges": [
    {
    "date":"Iso timestamp",
    "strength": X,
    "page":X,
    "line": T,
    "startX": U,
    "endX": V,
    "sura":Y,
    "startAya":Z,
    "startWord": W,
    "endAya":Z,
    "endWord": T}
])
