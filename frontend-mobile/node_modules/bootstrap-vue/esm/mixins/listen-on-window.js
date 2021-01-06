import { EVENT_OPTIONS_NO_CAPTURE } from '../constants/events';
import { arrayIncludes } from '../utils/array';
import { isBrowser } from '../utils/env';
import { eventOn, eventOff } from '../utils/events';
import { isString, isFunction } from '../utils/inspect';
import { keys } from '../utils/object';
var PROP = '$_bv_windowHandlers_'; // @vue/component

export default {
  beforeCreate: function beforeCreate() {
    // Declare non-reactive property
    // Object of arrays, keyed by event name,
    // where value is an array of handlers
    this[PROP] = {};
  },
  beforeDestroy: function beforeDestroy() {
    if (isBrowser) {
      var items = this[PROP]; // Immediately delete this[PROP] to prevent the
      // listenOn/Off methods from running (which may occur
      // due to requestAnimationFrame delays)

      delete this[PROP]; // Remove all registered event handlers

      keys(items).forEach(function (evtName) {
        var handlers = items[evtName] || [];
        handlers.forEach(function (handler) {
          return eventOff(window, evtName, handler, EVENT_OPTIONS_NO_CAPTURE);
        });
      });
    }
  },
  methods: {
    listenWindow: function listenWindow(on, evtName, handler) {
      on ? this.listenOnWindow(evtName, handler) : this.listenOffWindow(evtName, handler);
    },
    listenOnWindow: function listenOnWindow(evtName, handler) {
      if (isBrowser && this[PROP] && isString(evtName) && isFunction(handler)) {
        this[PROP][evtName] = this[PROP][evtName] || [];

        if (!arrayIncludes(this[PROP][evtName], handler)) {
          this[PROP][evtName].push(handler);
          eventOn(window, evtName, handler, EVENT_OPTIONS_NO_CAPTURE);
        }
      }
    },
    listenOffWindow: function listenOffWindow(evtName, handler) {
      if (isBrowser && this[PROP] && isString(evtName) && isFunction(handler)) {
        eventOff(window, evtName, handler, EVENT_OPTIONS_NO_CAPTURE);
        this[PROP][evtName] = (this[PROP][evtName] || []).filter(function (h) {
          return h !== handler;
        });
      }
    }
  }
};