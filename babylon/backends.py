import redis
import urlparse
import os
import pymongo


redis_url = os.environ['REDISCLOUD_URL']
redis_url = urlparse.urlparse(redis_url)
redis_conn = redis.Redis(
    host=redis_url.hostname,
    port=redis_url.port,
    password=redis_url.password)

mongo_url = os.environ['MONGOHQ_URL']
mongo_conn = pymongo.MongoClient(mongo_url)


MONGO_DB_NAME = 'babylon'




