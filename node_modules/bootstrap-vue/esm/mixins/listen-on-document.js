import { EVENT_OPTIONS_NO_CAPTURE } from '../constants/events';
import { arrayIncludes } from '../utils/array';
import { isBrowser } from '../utils/env';
import { eventOn, eventOff } from '../utils/events';
import { isString, isFunction } from '../utils/inspect';
import { keys } from '../utils/object';
var PROP = '$_bv_documentHandlers_'; // @vue/component

export default {
  created: function created() {
    var _this = this;

    /* istanbul ignore next */
    if (!isBrowser) {
      return;
    } // Declare non-reactive property
    // Object of arrays, keyed by event name,
    // where value is an array of handlers
    // Prop will be defined on client only


    this[PROP] = {}; // Set up our beforeDestroy handler (client only)

    this.$once('hook:beforeDestroy', function () {
      var items = _this[PROP] || {}; // Immediately delete this[PROP] to prevent the
      // listenOn/Off methods from running (which may occur
      // due to requestAnimationFrame/transition delays)

      delete _this[PROP]; // Remove all registered event handlers

      keys(items).forEach(function (evtName) {
        var handlers = items[evtName] || [];
        handlers.forEach(function (handler) {
          return eventOff(document, evtName, handler, EVENT_OPTIONS_NO_CAPTURE);
        });
      });
    });
  },
  methods: {
    listenDocument: function listenDocument(on, evtName, handler) {
      on ? this.listenOnDocument(evtName, handler) : this.listenOffDocument(evtName, handler);
    },
    listenOnDocument: function listenOnDocument(evtName, handler) {
      if (this[PROP] && isString(evtName) && isFunction(handler)) {
        this[PROP][evtName] = this[PROP][evtName] || [];

        if (!arrayIncludes(this[PROP][evtName], handler)) {
          this[PROP][evtName].push(handler);
          eventOn(document, evtName, handler, EVENT_OPTIONS_NO_CAPTURE);
        }
      }
    },
    listenOffDocument: function listenOffDocument(evtName, handler) {
      if (this[PROP] && isString(evtName) && isFunction(handler)) {
        eventOff(document, evtName, handler, EVENT_OPTIONS_NO_CAPTURE);
        this[PROP][evtName] = (this[PROP][evtName] || []).filter(function (h) {
          return h !== handler;
        });
      }
    }
  }
};