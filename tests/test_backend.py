from babylon import backends


def test_user_type():
    assert 1 == 2


def setup_module(module):
    backends.redis_conn.flushall()
    backends.mongo_conn.drop_database(backends.MONGO_DB_NAME)
