# -*- coding: utf-8 -*-
from flask import Flask, render_template


app = Flask(__name__)
app.jinja_env.add_extension('pyjade.ext.jinja.PyJadeExtension')


@app.route('/timeline')
def version():

    w1 = {
        'title': 'Monthly Title',
        'description': 'Description',
        'weight': 'month',
        'importance': 1,
    }

    w2 = {
        'title': 'Daily Title',
        'description': 'Description',
        'weight': 'week',
        'importance': 1,
    }

    items = {
        'scale': 'month',
        'data': [
            {
                'period_start_ts': 'Oct, 2014',
                'entities': [w1, w2, w2, w1, w2, w2, w2, w1, w1, w1],
            },
            {
                'period_start_ts': 'Sep, 2014',
                'entities': [w1, w2, w2, w1, w2, w2, w2, w1, w2],
            },
            {
                'period_start_ts': 'Aug, 2014',
                'entities': [w1, w2, w2, w1, w2, w2, w2, w1],
            },
        ]
    }

    return render_template('timeline.jade', items=items)


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5050, debug=True)
