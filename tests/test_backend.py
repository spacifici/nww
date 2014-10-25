from babylon import backends
from babylon import qs
from babylon import topics



def test_article_get_or_create():
    url = 'fun'
    a = backends.get_article(url)
    assert a['title'] == ''
    assert a['source_url'] == url

def test_quote():
    # insert quote
    qs.template('hi', '110', 'http://cliqz.com/', '123', 'Thomas', 'T')
    qs.store(backends.quote_collections, 'hi', '110', 'http://cliqz.com/', '123', 'Thomas', 'T')
    # retrieve quote
    for q in qs.retrieve(backends.quote_collections, person_id='T'):
        assert q['type'] == "quote"


def test_topics():
    topics.store(backends.topic_collections, 'title', '', {'a':'b'})
    for t in topics.retrieve(backends.topic_collections, title='title'):
        assert t['entity_type'] == 'topic'


def test_store_article():
    url = 'fun'
    assert backends.articles.find({'url': url}).count() == 0
    a = backends.get_article(url)
    backends.store_article(a)
    a = backends.get_article(url)
    assert a['source_url'] == 'fun'

def test_person_get_or_create():
    handle = 'h'
    assert backends.persons.find({'handle': handle}).count() == 0
    p = backends.get_person(handle)
    assert p['handle'] == handle
    assert 'meta' in p

def test_link():
    a = backends.get_article('a')
    t = backends.get_tag('t')
    backends.link(backends.articles, a['id'], 'tags', t['id'])
    a = backends.get_article('a')
    assert a['tags'] == [t['id']]

def setup_module(module):
    backends.redis_conn.flushall()
    backends.mongo_conn.drop_database(backends.MONGO_DB_NAME)
