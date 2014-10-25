function pageActionClicked(tab) {
    chrome.tabs.sendMessage(tab.id, {action : 'toggleSidebar'});
}

chrome.pageAction.onClicked.addListener(pageActionClicked);

chrome.runtime.onMessage.addListener(function(message, sender) {
    var tabId = sender.tab.id;

    if (message.action === 'enableSidebarIcon') {
        chrome.pageAction.show(tabId);
    }

    if (message.action === 'enableContextMenu') {
        menuItemId = chrome.contextMenus.create({
            title: "Add quote", 
            contexts:["selection"], 
            onclick: function(info, tab) {
                chrome.tabs.sendMessage(tab.id, {
                    action: 'addQuote',
                    selectionText: info.selectionText
                }); 
                // alert(info.selectionText);
            }
        });
    }

    if (message.action === 'disableContextMenu') {
        chrome.contextMenus.removeAll();
    }
});

// chrome.tabs.onUpdated.addListener(checkUrlEligibility);

