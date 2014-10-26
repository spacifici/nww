import datetime
import os
import pymongo
import redis
import urlparse
from bson.objectid import ObjectId

import article
import meta
import person
import qs
import tag
import topics
import json

import hashlib


from operator import itemgetter


redis_url = os.environ['REDISCLOUD_URL']
redis_url = urlparse.urlparse(redis_url)
redis_conn = redis.Redis(
    host=redis_url.hostname,
    port=redis_url.port,
    password=redis_url.password)

mongo_url = os.environ['MONGOHQ_URL']
mongo_conn = pymongo.MongoClient(mongo_url)


MONGO_DB_NAME = 'app30995050'

ARTICLES_COLLECTION = 'articles'
PERSONS_COLLECTION = 'persons'
TAGS_COLLECTION = 'tags'
QUOTES_COLLECTION = 'quotes'
TOPICS_COLLECTION = 'topics'
GALLERIES_COLLECTION = 'galleries'

db = mongo_conn[MONGO_DB_NAME]
articles = db[ARTICLES_COLLECTION]
persons = db[PERSONS_COLLECTION]
tags = db[TAGS_COLLECTION]
quote_collections = db[QUOTES_COLLECTION]
topic_collections = db[TOPICS_COLLECTION]
galleries_collection = db[GALLERIES_COLLECTION]


def save_content(obj):
    obj['_id'] = ObjectId(obj['id'])
    articles.save(obj)  # article should already be there
    # remove all quotes of this arsticle
    quote_collections.remove({'source_id': obj['id']})
    for q in obj['quotes']:
        q['type'] = 'quote'
        q['source_id'] = obj['id']
        if 'rating' in obj:
            q['rating'] = obj['rating'] + 1
        # q['time'] = obj['time']
        q['topics'] = obj['topics']
        # q['person'] = obj['people']
        quote_collections.insert(q)
        
        del q['_id']
    json_ready(obj)



def find(people=[], topics=[], scale=None):

    entities = (
        list(articles.find()) + 
        list(quote_collections.find()) +
        list(galleries_collection.find())
    )

    if not people and not topics:
        return entities

    result = []
    for entity in entities:
        if people:
            if entity['type'] == 'quote':
                if entity['person']['handle'] in people:
                    result.append(entity)
                    continue
            else:
                handles = entity.get('people', [])
                handles = map(itemgetter('handle'), handles)
                contains = set(handles).intersection(people)
                if contains:
                    result.append(entity)
                    continue

        if topics:
            handles = entity.get('topics', [])
            handles = map(itemgetter('handle'), handles)
            contains = set(handles).intersection(topics)
            if contains:
                result.append(entity)
                continue

    if scale:
        result = filter(lambda r: r.get('rating', 3) >= scale, result)

    return result


def query(people=None, topic=None):
    assert people or topic
    people_key = 'people.handle'
    topic_key = 'topics.handle'
        
    # query for people
    kws = []
    if people:
        for k in people:
            kws.append({people_key: k})
    if topic:
        for k in topic:
            kws.append({topic_key: k})
    res = {'articles': [], 'quotes': []}
    query = {'$or': kws}
    for a in articles.find(query).sort('time', -1):
        res['articles'].append(a)
    for q in quote_collections.find(query).sort('time', -1):
        res['quotes'].append(q)
    return res
    

def time():
    return datetime.datetime.utcnow().isoformat()


def link(collection, id1, relation, id2):
    oid1 = ObjectId(id1)
    collection.update({'_id': oid1}, {'$addToSet': {relation: id2}})


def get_article(url):
    return get_or_create(articles, {"source_url": url}, article.template)


def store_article(article):
    articles.save(with_mongo_id(article))
    article['id'] = unicode(article['_id'])
    del article['_id']
    return article


def get_person(handle):
    return get_or_create(persons, {"handle": handle}, person.template)


def store_person(person):
    persons.save(with_mongo_id(person))


def get_tag(name):
    return get_or_create(tags, {"name": name}, tag.template)


def store_tag(tag):
    tags.save(with_mongo_id(tag))


def with_mongo_id(obj):
    obj['_id'] = ObjectId(obj['id'])
    return obj


def get_or_create(collection, spec, template):
    a = collection.find_one(spec)
    if not a:
        assert len(spec.values()) == 1
        a = template(spec.values()[0])
        oid = ObjectId()
        a['_id'] = oid
        a['id'] = unicode(oid)
        collection.save(a)
    return json_ready(a)


def json_ready(article):
    article.update(dict(id=unicode(article['_id']), meta=get_meta()))
    article.pop('_id')
    return article


def get_meta():
    return meta.get_meta()

def get_quote(_id=None, person_id=None, source_id=None):
    if _id:
        query = {'_id': _id}
    if person_id:
        query = {'person_id': person_id}
    if source_id:
        query = {'source_id': source_id}
    for q in quote_collections.find(query):
            yield q


# def store_quote(quote_content, time, source_url, person_name,
#                 source_id=None, person_id=None, topic, tags=[]):
#     content = qs.template(
#         quote_content, time, source_url, person_name,
#         source_id, person_id, topics, tags)
#     return quote_collections.insert(content)


# topics
def store_topic(title, desc='', url='', people='', translation={}, editors={}):
    # if topic exist, update
    if topic_collections.find({'title': title}):
        return topic_collections.update(
            {'title': title},
            {'$addToSet': {"url": url, "people": people}})
    else:
        content = topics.template(title, desc, url, people, translation, editors)
        return topic_collections.insert(content)


# def get_articals_by_topic(_id=None, title=None):
#     for t in get_topic(_id, title):
#         u in url
        
    
def get_topic(_id=None, title=None):
    if _id:
        query = {'_id': _id}
    if title:
        query = {'title': title}
    for t in topic_collections.find(query):
        yield t
