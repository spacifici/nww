#!/usr/bin/env python
def template(title, desc='', source_url='', people='', translation={}, editors={}):
    template = {
        "entity_type": "topic",
        "translations": translation,
        "editors": {},
        "title": title,
        "description": desc,
        "people": {source_url},
        "source_url": {people},
    }
    return template
