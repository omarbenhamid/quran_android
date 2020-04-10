# API For sync

# List talibs

GET : list talibs

{ "serverKey": "", "talibName":"" }

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
