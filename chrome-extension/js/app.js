'use strict';

var app = angular.module('xxxApp', ['ngRoute', 'ngResource'])

app.controller('RootCtrl', ['$scope', '$resource',
    function($scope, $resource) {

        var Article = $resource(
            'http://localhost:5000/api/article/:id',
            {
                'id': '@id'
            },
            {
                'getByUrl': { url: 'http://localhost:5000/api/article', method: 'GET' },
                'get': { method: 'GET' },
                'update': { method:'POST' }
            }
        );

        $scope.article = Article.getByUrl({uri: window.location.href})

        Article.getByUrl({uri: window.location.href})
            .$promise.then(function(article) {
                console.log('HERE')
                console.log(article)
                $scope.article = article;
            });

        $scope.facebookMeta = getMeta('og:', 'property')

        $scope.saveArticle = function() {
            console.log('save')
            $scope.article.save()
        }


        // <link rel="canonical" href="http://www.spiegel.de/politik/deutschland/dobrindt-zu-pkw-maut-zugestaendnisse-geplant-a-997878.html">
        // <meta name="keywords" content="Bundesländer, Autobahnen, Widerstand, Maut, Länder, Abgabe, Politik, Deutschland, Pkw-Maut, Alexander Dobrindt, Nordrhein-Westfalen, CDU">
        // <meta name="news_keywords" content="Bundesländer, Autobahnen, Widerstand, Maut, Länder, Abgabe, Politik, Deutschland, Pkw-Maut, Alexander Dobrindt, Nordrhein-Westfalen, CDU">
        // $scope.twitterMeta = getMeta('twitter:', 'name')
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