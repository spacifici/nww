#!/usr/bin/env python
def template(quote, time, source_url, person_name,
             source_id=None, person_id=None, topics=[], tags=[]):
    if not source_id:
        # get source_id
        pass
    if not person_id:
        # get person_id
        pass
        
    template = {
        # "node_id": "quote:1",
        "type": "quote",
        "text": quote,
        "time": time,
        "source_url": source_url,
        "source_id": source_id,
        "person_name": person_name,
        "person_id": person_id,
        "topics": topics,
        "tags": tags
    }
    return template


