# -*- coding: utf-8 -*-
import logging
import sys

from flask import Flask, render_template, url_for, jsonify, request
from babylon import backends
from glob import glob
from os import path


app = Flask(__name__)
app.jinja_env.add_extension('pyjade.ext.jinja.PyJadeExtension')

# Configure logging.
app.logger.setLevel(logging.DEBUG)
del app.logger.handlers[:]

handler = logging.StreamHandler(stream=sys.stdout)
handler.setLevel(logging.DEBUG)
handler.formatter = logging.Formatter(
    fmt=u"%(asctime)s level=%(levelname)s %(message)s",
    datefmt="%Y-%m-%dT%H:%M:%SZ",
)
app.logger.addHandler(handler)


@app.context_processor
def class_extractors():
    def timeline_entry_classes(entry):
        # return ' '.join([entry['column'], entry['scale']])

        res = ['rating-{}'.format(entry['rating'])]

        if entry.get('type') == 'gallery':
            if len(entry['images']) == 1:
                res.append('huge-images')
            elif len(entry['images']) < 3:
                res.append('large-images')

        return ' '.join(res)

    return dict(tl_entry_classes=timeline_entry_classes)


@app.route('/')
def landing():
    entries = [
        {
            'url': url_for('.timeline')
        },
        {
            'url': url_for('.timeline')
        },
        {
            'url': url_for('.timeline')
        },
    ]
    return render_template('landing.jade', entries=entries)


@app.route('/person/<handle>')
def person(handle):
    return ''


@app.route('/timeline')
def timeline():

    scale = int(request.args.get('scale', '3'))
    # backends.query(people=people, topic)

    def gen_urls(entry):
        if entry.get('type') == 'quote':
            entry['img_url'] = url_for('.image', 
                entity='person', 
                handle=entry['person']['handle'], 
                category='quote', 
                _external=True)

        if entry.get('type') == 'gallery':
            images = []
            for index, img_url in enumerate(entry['images']):
                url = url_for('.image', 
                    entity='gallery', 
                    handle=img_url, 
                    category=str(index + 1))
                images.append(url)
            print images
            entry['images'] = images

        return entry

    entries = [
        {
            "node_id": "quote:1",
            "type": "quote",
            "text": "Trying to seal off an entire region of the world - if that where event possible - could actually make the situation worse",
            "time": "2014-10-10T15:00",
            "source": "Associated Press",
            "source_url": "http://blah.com/article",
            "source_id": "site:1",
            "topics": [
                "mh17", 
                "maintained"
            ],
            "tags": [
                "free"
            ],
            "person": {
                "id": "person:1",
                "type": "person",
                "name": "Barack Obama",
                "handle": "barack-obama",
                "source_url": "",
                "position": "US President",
                "birth_year": 0,
                "curated": True
            },
            # 'img_url': url_for('.image', entity='person', handle='barack-obama', category='quote', _external=True)
        },

        {
            "type": "article",
            "date_str": "3 hours ago",
            "og": {
                "title": u"Російські ЗМІ і терористи в унісон заявили про збитий літак ВПС України",
                "type": u"politician",
                "url": u"http://www.pravda.com.ua/news/2014/07/17/7032194/",
                "image": u"http://img.pravda.com/images/up_for_fb.gif",
                "site_name": u"Українська правда",
                "description": u"Російські ЗМІ та терористи повідомили про те, що збито транспортний літак Ан-26 Військово-Повітряних сил України.",
            }
        },

        {
            "type": "gallery",
            "images": [
                'g1-ebola', 'g1-ebola', 'g1-ebola'
                # url_for('.image', entity='gallery', handle='g1-ebola', category='1'),
                # url_for('.image', entity='gallery', handle='g1-ebola', category='2'),
                # url_for('.image', entity='gallery', handle='g1-ebola', category='3')
            ]
        },

        {
            "type": "gallery",
            "images": [
                'g1-ebola', 'g1-ebola'
                # url_for('.image', entity='gallery', handle='g1-ebola', category='1'),
                # url_for('.image', entity='gallery', handle='g1-ebola', category='2')
            ],
            "description": "Ebola xxxx",
            "date_str": "5 Hours ago"
        },

        {
            "type": "gallery",
            "images": [
                'g1-ebola'
                # url_for('.image', entity='gallery', handle='g2-ebola', category='1'),
            ],
            "description": "Ebola xxxx",
            "date_str": "5 Hours ago"
        }
    
    ]

    entries = map(gen_urls, entries)

    def patch(entry, rating):
        entry = dict(entry)
        entry['rating'] = rating
        return entry

    entries = [
        patch(entries[0], 3),
        patch(entries[1], 3),
        patch(entries[1], 4),
        patch(entries[2], 2),
        patch(entries[1], 1),
        patch(entries[0], 2),
        patch(entries[3], 3),
        patch(entries[4], 4),
        patch(entries[0], 5),
        patch(entries[0], 2),
        patch(entries[4], 1),
        patch(entries[3], 3),
    ]

    return render_template('timeline_animated_a2.jade', entries=entries)


@app.route('/api/article', methods=['GET'])
def article_by_id():
    url = request.args.get('uri', 'NON')
    article = backends.get_article(url)
    ensure_image_urls(article)
    return jsonify(article)


@app.route('/api/article/<article_id>', methods=['GET'])
def article():
    return jsonify({})


@app.route('/api/article/<article_id>', methods=['PUT', 'POST'])
def update_article(article_id):
    article = request.get_json()
    backends.save_content(article)
    return jsonify(article)


@app.route('/img/<entity>/<handle>/<category>')
def image(entity, handle, category=None):
    img_file = path.join(
        'entities', 
        entity, 
        '-'.join(filter(None, [handle, category]))
    )

    abs_img_file = path.join(
        path.abspath(app.static_folder), img_file
    )

    matching_files = glob(abs_img_file + '*')
    print abs_img_file, matching_files
    assert matching_files

    extension = path.splitext(matching_files[0])[1]
    return app.send_static_file(img_file + extension)


def ensure_image_urls(article):

    if 'meta' not in article:
        return article

    meta = article['meta']

    for person in meta.get('people', []):
        person['img_url'] = url_for('.image',
            entity='person',
            handle=person['handle'],
            category='icon',
            _external=True)

    for topic in meta.get('topics', []):
        topic['img_url'] = url_for('.image',
            entity='topic',
            handle=topic['handle'],
            category='icon',
            _external=True)

    return article


# @app.route('/timeline')
# def version():

#     base = {
#         'thumbnail_url': 'http://cdn1.spiegel.de/images/image-733534-galleryV9-kdyb.jpg',
#         'url': 'http://www.spiegel.de/panorama/justiz/bild-996540-733534.html',
#         'title': u'MH17-Absturz: Opfer-Angehörige fordern Schadensersatz von Niederlanden',
#         'description': u'Hinterbliebene von Opfern der abgestürzten Malaysia-Airlines-Maschine '
#                        u'mit der Flugnummer MH17 streben Schadensersatz vom niederländischen '
#                        u'Staat an. Sie monieren die schleppend vorangehenden Ermittlungen zum '
#                        u'Hergang der Katastrophe.',
#         'source': 'spiegel.de',
#     }

#     w0 = dict({'weight': 'year'}, **base)
#     w1 = dict({'weight': 'month'}, **base)
#     w2 = dict({'weight': 'week'}, **base)
#     w3 = dict({'weight': 'day'}, **base)

#     items = {
#         'scale': 'month',
#         'data': [
#             {
#                 'period_start_ts': 'Oct, 2014',
#                 'entities': [w1, w0, w3, w3, w2, w3, w3, w2, w3, w1, w2, w2, w2, w1, w1, w1],
#             },
#             {
#                 'period_start_ts': 'Sep, 2014',
#                 'entities': [w1, w2, w2, w1, w2, w2, w2, w1, w2],
#             },
#             {
#                 'period_start_ts': 'Aug, 2014',
#                 'entities': [w1, w2, w2, w1, w2, w2, w2, w1],
#             },
#         ]
#     }

#     return render_template('timeline.jade', items=items)


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5050, debug=True)
