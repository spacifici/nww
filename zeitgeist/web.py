# -*- coding: utf-8 -*-
from flask import Flask, render_template, url_for


app = Flask(__name__)
app.jinja_env.add_extension('pyjade.ext.jinja.PyJadeExtension')


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


@app.route('/timeline')
def timeline():
    entries = [
    ]
    return render_template('timeline.jade', entries=entries)


# @app.route('/timeline/animated')
# def timeline_animated():
#     entries = [
#         {'column': 'left', 'type': 'article', 'scale': 'month'},
#         {'column': 'left', 'type': 'quote', 'scale': 'month xxx'},
#         {'column': 'right', 'type': 'article', 'scale': 'month yyy'},
#         {'column': 'left', 'type': 'article', 'scale': 'month'},
#         {'column': 'left', 'type': 'infographic', 'scale': 'month'},
#         {'column': 'left', 'type': 'gallery', 'scale': 'month'},
#     ] * 100


#     return render_template('timeline_animated_a2.jade', entries=entries)


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
