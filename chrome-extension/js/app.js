'use strict';

var app = angular.module('xxxApp', ['ngRoute'])

app.controller('RootCtrl', ['$scope',
    function($scope) {

        var Article = $resource(
            'http://localhost:5000/api/article',
            {
                getCurrent: { method: 'GET' }
                update: { method:'POST' }
            }
        );

        alert('HERE')

        $scope.twitterMeta = getMeta('twitter:', 'name')
        $scope.facebookMeta = getMeta('og:', 'property')

        $scope.data = 'HELLO'

        $scope.markAsCrap = function() {
            alert('Crapy Article!')
        }




        // <link rel="canonical" href="http://www.spiegel.de/politik/deutschland/dobrindt-zu-pkw-maut-zugestaendnisse-geplant-a-997878.html">
        // <meta name="keywords" content="Bundesländer, Autobahnen, Widerstand, Maut, Länder, Abgabe, Politik, Deutschland, Pkw-Maut, Alexander Dobrindt, Nordrhein-Westfalen, CDU">
        // <meta name="news_keywords" content="Bundesländer, Autobahnen, Widerstand, Maut, Länder, Abgabe, Politik, Deutschland, Pkw-Maut, Alexander Dobrindt, Nordrhein-Westfalen, CDU">
    }
]);

function getMeta(prefix, attrName) {
    return $('meta')
        .toArray()
        .filter(metaFilter(prefix, attrName))
        .reduce(metaReducer(prefix, attrName), {});
}

function metaFilter(prefix, attr) {
    return function(el) {
        return ($(el).attr(attr) || '').indexOf(prefix) == 0;
    }
}

function metaReducer(prefix, attr) {
    return function(target, el, index) {
        el = $(el);
        target[el.attr(attr).substr(prefix.length)] = el.attr('content');
        return target;
    }
}