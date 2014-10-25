nww - news world wide

# Entities

- [Timeline](#timeline)
- [Article](#article)
- [Topic](#topic)
- [Person](#person)
- [Summary](#summary)
- [Picture Gallery](#picture-gallery)


## NWW Query

```
@merkel @putin #gas 1w +latest

@: select person
#: selcet topic
1d: one day
1w: one week
1m: one month
1y: one year
+latest: add latest news
```

## Curated entities

Persons and topics are curated. Approved editors can take free form tags that anybody created and lift them to become a topic.

## <a id="timeline"></a>Timeline

Timeline is a context, that groups and ranks items together.

## <a id="article"></a>Article

Represents an external web page (news, blog, etc).

```json
{
  "node_id": "node:1",
  "type": "news-article",
  "title": "title",
  "description": "descr"
  "time": "2014-10-10T15:00",
  "source_url": "http://blah.com/article"
  "og": {
    "title": "Російські ЗМІ і терористи в унісон заявили про збитий літак ВПС України",
    "type": "politician", 
    "url": "http://www.pravda.com.ua/news/2014/07/17/7032194/",
    "image": "http://img.pravda.com/images/up_for_fb.gif",
    "site_name": "Українська правда",
    "description": "Російські ЗМІ та терористи повідомили про те, що збито транспортний літак Ан-26 Військово-Повітряних сил України.",
    },
    "quotes": [
      {
        "node_id": "quote:1",
        "type": "quote",
        "text": "hallo quote",
        "time": "2014-10-10T15:00",
        "source_url": "http://blah.com/article"
        "source_id": "site:1",
        "person_name": "Hans Meiser",
        "person_id": "person:1"
      }
  ],
  "people": [
    {
      "node_id": "person:1",
      "type": "person",
      "name": "Putin",
      "handle": "putin",
      "description": "descr"
      "source_url": "http://blah.com/article"
      "position": "President of Russian Federation",
      "birth_year": 1954,
      "curated": true
    }
  ],
  "topics": [
    "maintained"
  ],
  "tags": [
    "free"
  ]
}
```

## <a id="quote"></a>Quote

```json

{
  "node_id": "quote:1",
  "type": "quote",
  "text": "hallo quote",
  "time": "2014-10-10T15:00",
  "source_url": "http://blah.com/article"
  "source_id": "site:1",
  "person_name": "Hans Meiser",
  "person_id": "person:1"
}

```

## <a id="topic"></a>Topic 

Strong group of items. 
Has particular start date. 
Might have short pre-history.

Example: Flight MH17 Crash

```json
{
    "entity_type": "topic",
    
    "translations": {
        "de": "MH17-Crash",
        "ru": "MH17-Катастрофа"
    },

    "editors": { ... },

    "approved": {
        "language": "en",
        "handle": "MH17-Crash",
        "initiation_date": "",
        "title": "Malaysia Airlines Flight 17",
        "description": ""
    
    }
}
```

## <a id="person"></a>Person

```json

{
  "node_id": "person:1",
  "type": "person",
  "name": "Putin",
  "handle": "putin",
  "description": "descr"
  "source_url": "http://blah.com/article"
  "position": "President of Russian Federation",
  "birth_year": 1954,
  "curated": true
}

```

## <a id="summary"></a>Summary

Summary represents a manualy managed item.

```json
{
    "entity_type": "summary",
}
```

## <a id="picture-gallery"></a>Picture Gallery

Picture Gallery

```json
{
    "entity_type": "picture-gallery",
}
```
