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
        return ' '.join([entry['column'], entry['scale']])

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
    entries = [
        {'column': 'left', 'type': 'article', 'scale': 'month'},
        {'column': 'left', 'type': 'quote', 'scale': 'month xxx'},
        {'column': 'right', 'type': 'article', 'scale': 'month yyy'},
        {'column': 'left', 'type': 'article', 'scale': 'month'},
        {'column': 'left', 'type': 'infographic', 'scale': 'month'},
        {'column': 'left', 'type': 'gallery', 'scale': 'month'},
    ] * 100


    return render_template('timeline_animated_a2.jade', entries=entries)


@app.route('/api/article', methods=['GET'])
def article_id():
    url = request.args.get('uri', 'NON')
    article = backends.get_article(url)
    ensure_image_urls(article)
    return jsonify(article)


@app.route('/api/article/<article_id>', methods=['GET'])
def article():
    return jsonify({})


@app.route('/api/article/<article_id>', methods=['PUT', 'POST'])
def update_article(article_id):
    print 'update_article'
    article = request.get_json()
    backends.store_article(article)
    return jsonify({"success": True})


@app.route('/img/<entity>/<handle>/<category>')
def image(entity, handle, category):
    img_file = path.join(
        'entities', 
        entity, 
        '{}-{}'.format(handle, category))

    abs_img_file = path.join(
        path.abspath(app.static_folder), img_file
    )

    matching_files = glob(abs_img_file + '*')
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
