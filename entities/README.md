nww - news world wide

# Entity types

- [Timeline](#timeline)
- [Article](#article)
- [Topic](#topic)
- [Person](#person)
- [Summary](#summary)
- [Picture Gallery](#picture-gallery)

## <a id="timeline"></a>Timeline

Timeline is a context, that groups and ranks items together.

## <a id="article"></a>Article

Represents an external web page (news, blog, etc).

```json
{
    "item_type": "article",
    "url": "", // Resource URL
    "discovery_date": "",

    "extracted": { // Data extracted from HTML
        "canonical_url": "", // Canonical URL
        "language": "ukr", // Article language
        "title": "", // Page Title
        "description": "", 
        "og": { ... }, // Facebook Open Graph data
        "twitter": { ... }, // Twitter meta data
    },

    "editors": { // Editor data
        "@editor_name": { // Editor's handle
            "initial_submitter": true,
            "tags": [""], // List of tags
            "topics": [""], // List of topics
            "event_date": 
        },
    }

    "approved": { // Approved life-information, based on editors
        // same, as editor entry
    }
}
```

## <a id="topic"></a>Topic 

Strong groups for item. Has particular beginning. Might have short pre-history

Example: Flight MH17 Crash

```json
{
    "item_type": "topic",
    
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

Person's timeline

```json
{
    "item_type": "person",
}
```

## <a id="summary"></a>Summary

Summary represents a manualy managed item.

```json
{
"item_type": "summary",
}
```

## <a id="picture-gallery"></a>Picture Gallery

Picture Gallery

```json
{
    "item_type": "picture-gallery",
}
```