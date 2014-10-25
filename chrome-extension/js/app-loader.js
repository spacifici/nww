'use strict';

$('html').attr('ng-scp', '');

function toggleSidebar() {

    var menuItemId;

    function removeAnimation() {
        $('.xxx-app').removeClass('bounceInRight bounceOutRight');
    }

    function toggle() {
        var appWrapper = $('.xxx-app');
        if (appWrapper.is(':visible')) {

            chrome.runtime.sendMessage({
                action: 'disableContextMenu'
            });

            $('.xxx-app .xxx-app-content')
                .removeClass('bounceInRight')
                .addClass('bounceOutRight')
                .one('webkitAnimationEnd', function() {
                    appWrapper.hide();
                });

        } else {
            appWrapper.show(100);
            $('.xxx-app .xxx-app-content')
                .removeClass('bounceOutRight')
                .addClass('bounceInRight');

            chrome.runtime.sendMessage({
                action: 'enableContextMenu'
            });
        }
    }

    if($('.xxx-app').size() > 0) {
        toggle();
        return;
    }

    $.ajax({
        url: chrome.extension.getURL('/partials/root.html'),
        success: function(content) {
            $('body').prepend(content);
            
            angular.element(document).ready(function() {
                angular.bootstrap(document, ['xxxApp']);
            });

            toggle();
        }
    });
}

chrome.runtime.sendMessage({
    action: 'enableSidebarIcon'
});

chrome.runtime.onMessage.addListener(function(message, sender, sendResponse) {
    if (message.action === 'toggleSidebar') {
        toggleSidebar()
    }

    if (message.action === 'addQuote') {
        alert(message.selectionText)
        console.log(message.selectionText)
    }
});