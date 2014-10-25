'use strict'


/**
* My method description.  Like other pieces of your comment blocks, 
* this can span multiple lines.
*
* @method timelineEntriesDependencies
* @param {Array} 
* @param {Array} 
* @param {Function} 
* @param {Function} 
* @return {Object} Object of next->prev mappings
*/
function timelineEntriesDependencies(leftEntries, rightEntries, getId, getOrderProp) {
    var entries = leftEntries.concat(rightEntries),
        result = {},
        getId = getId || function(e) { return e.id },
        getOrderProp = getOrderProp || function(e) { return e.prop };

    entries.sort(function(e1, e2) {
        return getOrderProp(e2) - getOrderProp(e1)
    }).forEach(function(entry, idx, entries) {
        if (idx > 0) {
            result[getId(entry)] = getId(entries[idx - 1])
        }
    });

    return result;
}


var expect = require('chai').expect;


describe('Array', function() {

    var getId = function(el) {
        return el.id
    }

    it('merge is symetrical', function() {
        
        expect({a: 'b'}).to.deep.equal(
            timelineEntriesDependencies(
                [{id: 'b', prop: 2}], 
                [{id: 'a', prop: 1}]
            )
        )

        expect({a: 'b'}).to.deep.equal(
            timelineEntriesDependencies(
                [{id: 'a', prop: 1}], 
                [{id: 'b', prop: 2}]
            )
        );
    });

    it('merge 2 arrays', function() {

        expect({
            1: 2, 
            2: 3, 
            3: 4, 
            4: 5, 
            5: 6
        }).to.deep.equal(
            timelineEntriesDependencies(
                [{id: 6}, {id: 4}, {id: 3}, {id: 1}],
                [{id: 5}, {id: 2}],
                getId, getId
            )
        );
    });
});