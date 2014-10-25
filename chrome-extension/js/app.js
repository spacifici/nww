'use strict';

var app = angular.module('xxxApp', ['ngRoute', 'ngResource'])

app.controller('RootCtrl', ['$scope', '$resource', '$timeout',
    function($scope, $resource, $timeout) {
 
        var Article = $resource(
            'http://localhost:5050/api/article/:id',
            {
                'id': '@id'
            },
            {
                'getByUrl': { url: 'http://localhost:5050/api/article', method: 'GET' },
                // 'get': { method: 'GET' },
                // 'update': { method:'POST' }
            }
        );

        $scope.article = Article.getByUrl({uri: window.location.href})

        $scope.isLoading = true;

        function getHandle(entity) {
            return entity.handle;
        }

        function loadArticle(article) {
            $scope.facebookMeta = getMeta('og:', 'property');
            $scope.article = article;
            $scope.article.og = $scope.facebookMeta; 
            $scope.isLoading = false;

            var selectedPeopleHandles = $scope.article.people.map(getHandle);
            var selectedTopics = $scope.article.topics.map(getHandle);
            var selectedTags = $scope.article.tags.map(getHandle);

            $scope.article.meta.people.forEach(function(entity) {
                entity.isSelected = selectedPeopleHandles.indexOf(entity.handle) > -1;
            });

            $scope.article.meta.topics.forEach(function(entity) {
                entity.isSelected = selectedTopics.indexOf(entity.handle) > -1;
            });

            $scope.article.meta.tags.forEach(function(entity) {
                entity.isSelected = selectedTags.indexOf(entity.handle) > -1;
            });

            // $scope.article.people = [
            //     {
            //         handle: "angela-merkel",
            //         img_url: "http://sleepy-mountain-8434.herokuapp.com/img/person/angela-merkel/icon",
            //         name: "Angela Merkel",
            //         position: "Chancellor of the Federal Republic of Germany"
            //     }
            // ];
            // $scope.article.topics = [
            //     {
            //         handle: "mh-17-crash",
            //         img_url: "http://sleepy-mountain-8434.herokuapp.com/img/topic/mh-17-crash/icon",
            //         name: "MH 17 Crash"
            //     }
            // ];
            // $scope.article.tags = [
            //     {
            //         handle: "economy",
            //         name: "Economy"
            //     }
            // ];
            // $scope.article.quotes = [
            //     {
            //         text: "this is a quote",
            //         source_url: window.location.href,
            //         source_id: article.id,
            //         person_name: "Angela Merkel",
            //         person_handle: "angela-merkel"
            //     }
            // ];
        }

        Article.getByUrl({uri: window.location.href})
            .$promise.then(loadArticle);

        var newQuote = function(text, personName, personHandle) {
            return {
                text: text,
                source_url: window.location.href,
                source_id: $scope.article.id,
                person_name: personName,
                person_handle: personHandle
            }
        }

        var isSelected = function(x) {
            return x.isSelected === true
        }

        $scope.saveArticle = function() {
            $scope.article.people = $scope.article.meta.people.filter(isSelected);
            $scope.article.topics = $scope.article.meta.topics.filter(isSelected);
            $scope.article.tags = $scope.article.meta.tags.filter(isSelected);
            $scope.article.$save(loadArticle);
            // console.log($scope.article)
            // $scope.article.tags = $scope.article.tags.filter(isSelected)
            // $scope.article.persons = $scope.article.persons.filter(isSelected)
            // $timeout(toggleSidebar, 800);
        }

        document.addQuote = function(quote) {
            console.log('pushing qote', quote)
            $scope.article.quotes.push(newQuote(quote, 'Angela Merkel', 'angela-merkel'));
            $scope.$apply();
        }

        // <link rel="canonical" href="http://www.spiegel.de/politik/deutschland/dobrindt-zu-pkw-maut-zugestaendnisse-geplant-a-997878.html">
        // <meta name="keywords" content="Bundesländer, Autobahnen, Widerstand, Maut, Länder, Abgabe, Politik, Deutschland, Pkw-Maut, Alexander Dobrindt, Nordrhein-Westfalen, CDU">
        // <meta name="news_keywords" content="Bundesländer, Autobahnen, Widerstand, Maut, Länder, Abgabe, Politik, Deutschland, Pkw-Maut, Alexander Dobrindt, Nordrhein-Westfalen, CDU">
        // $scope.twitterMeta = getMeta('twitter:', 'name')


        $('.xxx-app-scrollable-content').removeClass('hide-me');
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