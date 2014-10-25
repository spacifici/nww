from setuptools import setup

setup(
    author='Bogdan Sulima',
    name='zeitgeist',
    version='0.1',
    provides=['zeitgeist'],
    packages=['zeitgeist'],
    platforms='any',
    install_requires=[
        'Flask==0.10.1',
        'pyjade==3.0.0',
        'redis==2.10.3',
        'pymongo==2.7.2',
        'gunicorn'
    ]
)
