function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_TABS, NAME_TAB_BUTTON_HELPER } from '../../constants/components';
import { CODE_DOWN, CODE_END, CODE_HOME, CODE_LEFT, CODE_RIGHT, CODE_SPACE, CODE_UP } from '../../constants/key-codes';
import { SLOT_NAME_TITLE } from '../../constants/slot-names';
import { makePropsConfigurable } from '../../utils/config';
import identity from '../../utils/identity';
import looseEqual from '../../utils/loose-equal';
import observeDom from '../../utils/observe-dom';
import stableSort from '../../utils/stable-sort';
import { arrayIncludes, concat } from '../../utils/array';
import { BvEvent } from '../../utils/bv-event.class';
import { attemptFocus, requestAF, selectAll } from '../../utils/dom';
import { stopEvent } from '../../utils/events';
import { isEvent } from '../../utils/inspect';
import { mathMax } from '../../utils/math';
import { toInteger } from '../../utils/number';
import { omit } from '../../utils/object';
import idMixin from '../../mixins/id';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import { BLink } from '../link/link';
import { BNav, props as BNavProps } from '../nav/nav'; // --- Constants ---

var navProps = omit(BNavProps, ['tabs', 'isNavBar', 'cardHeader']); // --- Helper methods ---
// Filter function to filter out disabled tabs

var notDisabled = function notDisabled(tab) {
  return !tab.disabled;
}; // --- Helper components ---
// @vue/component


var BVTabButton = /*#__PURE__*/Vue.extend({
  name: NAME_TAB_BUTTON_HELPER,
  inject: {
    bvTabs: {
      /* istanbul ignore next */
      default: function _default() {
        return {};
      }
    }
  },
  props: makePropsConfigurable({
    // Reference to the child <b-tab> instance
    tab: {
      default: null
    },
    tabs: {
      type: Array,

      /* istanbul ignore next */
      default: function _default() {
        return [];
      }
    },
    id: {
      type: String,
      default: null
    },
    controls: {
      type: String,
      default: null
    },
    tabIndex: {
      type: Number,
      default: null
    },
    posInSet: {
      type: Number,
      default: null
    },
    setSize: {
      type: Number,
      default: null
    },
    noKeyNav: {
      type: Boolean,
      default: false
    }
  }, NAME_TABS),
  methods: {
    focus: function focus() {
      attemptFocus(this.$refs.link);
    },
    handleEvt: function handleEvt(evt) {
      if (this.tab.disabled) {
        /* istanbul ignore next */
        return;
      }

      var type = evt.type,
          keyCode = evt.keyCode,
          shiftKey = evt.shiftKey;

      if (type === 'click') {
        stopEvent(evt);
        this.$emit('click', evt);
      } else if (type === 'keydown' && keyCode === CODE_SPACE) {
        // For ARIA tabs the SPACE key will also trigger a click/select
        // Even with keyboard navigation disabled, SPACE should "click" the button
        // See: https://github.com/bootstrap-vue/bootstrap-vue/issues/4323
        stopEvent(evt);
        this.$emit('click', evt);
      } else if (type === 'keydown' && !this.noKeyNav) {
        // For keyboard navigation
        if ([CODE_UP, CODE_LEFT, CODE_HOME].indexOf(keyCode) !== -1) {
          stopEvent(evt);

          if (shiftKey || keyCode === CODE_HOME) {
            this.$emit('first', evt);
          } else {
            this.$emit('prev', evt);
          }
        } else if ([CODE_DOWN, CODE_RIGHT, CODE_END].indexOf(keyCode) !== -1) {
          stopEvent(evt);

          if (shiftKey || keyCode === CODE_END) {
            this.$emit('last', evt);
          } else {
            this.$emit('next', evt);
          }
        }
      }
    }
  },
  render: function render(h) {
    var id = this.id,
        tabIndex = this.tabIndex,
        setSize = this.setSize,
        posInSet = this.posInSet,
        controls = this.controls,
        handleEvt = this.handleEvt;
    var _this$tab = this.tab,
        title = _this$tab.title,
        localActive = _this$tab.localActive,
        disabled = _this$tab.disabled,
        titleItemClass = _this$tab.titleItemClass,
        titleLinkClass = _this$tab.titleLinkClass,
        titleLinkAttributes = _this$tab.titleLinkAttributes;
    var $link = h(BLink, {
      ref: 'link',
      staticClass: 'nav-link',
      class: [{
        active: localActive && !disabled,
        disabled: disabled
      }, titleLinkClass, // Apply <b-tabs> `activeNavItemClass` styles when the tab is active
      localActive ? this.bvTabs.activeNavItemClass : null],
      props: {
        disabled: disabled
      },
      attrs: _objectSpread(_objectSpread({}, titleLinkAttributes), {}, {
        role: 'tab',
        id: id,
        // Roving tab index when keynav enabled
        tabindex: tabIndex,
        'aria-selected': localActive && !disabled ? 'true' : 'false',
        'aria-setsize': setSize,
        'aria-posinset': posInSet,
        'aria-controls': controls
      }),
      on: {
        click: handleEvt,
        keydown: handleEvt
      }
    }, [this.tab.normalizeSlot(SLOT_NAME_TITLE) || title]);
    return h('li', {
      staticClass: 'nav-item',
      class: [titleItemClass],
      attrs: {
        role: 'presentation'
      }
    }, [$link]);
  }
}); // @vue/component

export var BTabs = /*#__PURE__*/Vue.extend({
  name: NAME_TABS,
  mixins: [idMixin, normalizeSlotMixin],
  provide: function provide() {
    return {
      bvTabs: this
    };
  },
  model: {
    prop: 'value',
    event: 'input'
  },
  props: _objectSpread(_objectSpread({}, navProps), {}, {
    tag: {
      type: String,
      default: 'div'
    },
    card: {
      type: Boolean,
      default: false
    },
    end: {
      // Synonym for 'bottom'
      type: Boolean,
      default: false
    },
    noFade: {
      type: Boolean,
      default: false
    },
    noNavStyle: {
      type: Boolean,
      default: false
    },
    noKeyNav: {
      type: Boolean,
      default: false
    },
    lazy: {
      // This prop is sniffed by the <b-tab> child
      type: Boolean,
      default: false
    },
    contentClass: {
      type: [String, Array, Object] // default: null

    },
    navClass: {
      type: [String, Array, Object] // default: null

    },
    navWrapperClass: {
      type: [String, Array, Object] // default: null

    },
    activeNavItemClass: {
      // Only applied to the currently active <b-nav-item>
      type: [String, Array, Object] // default: null

    },
    activeTabClass: {
      // Only applied to the currently active <b-tab>
      // This prop is sniffed by the <b-tab> child
      type: [String, Array, Object] // default: null

    },
    value: {
      // v-model
      type: Number,
      default: null
    }
  }),
  data: function data() {
    return {
      // Index of current tab
      currentTab: toInteger(this.value, -1),
      // Array of direct child <b-tab> instances, in DOM order
      tabs: [],
      // Array of child instances registered (for triggering reactive updates)
      registeredTabs: [],
      // Flag to know if we are mounted or not
      isMounted: false
    };
  },
  computed: {
    fade: function fade() {
      // This computed prop is sniffed by the tab child
      return !this.noFade;
    },
    localNavClass: function localNavClass() {
      var classes = [];

      if (this.card && this.vertical) {
        classes.push('card-header', 'h-100', 'border-bottom-0', 'rounded-0');
      }

      return [].concat(classes, [this.navClass]);
    }
  },
  watch: {
    currentTab: function currentTab(newVal) {
      var index = -1; // Ensure only one tab is active at most

      this.tabs.forEach(function (tab, idx) {
        if (newVal === idx && !tab.disabled) {
          tab.localActive = true;
          index = idx;
        } else {
          tab.localActive = false;
        }
      }); // Update the v-model

      this.$emit('input', index);
    },
    value: function value(newVal, oldVal) {
      if (newVal !== oldVal) {
        newVal = toInteger(newVal, -1);
        oldVal = toInteger(oldVal, 0);
        var tabs = this.tabs;

        if (tabs[newVal] && !tabs[newVal].disabled) {
          this.activateTab(tabs[newVal]);
        } else {
          // Try next or prev tabs
          if (newVal < oldVal) {
            this.previousTab();
          } else {
            this.nextTab();
          }
        }
      }
    },
    registeredTabs: function registeredTabs() {
      var _this = this;

      // Each b-tab will register/unregister itself.
      // We use this to detect when tabs are added/removed
      // to trigger the update of the tabs.
      this.$nextTick(function () {
        requestAF(function () {
          _this.updateTabs();
        });
      });
    },
    tabs: function tabs(newVal, oldVal) {
      var _this2 = this;

      // If tabs added, removed, or re-ordered, we emit a `changed` event.
      // We use `tab._uid` instead of `tab.safeId()`, as the later is changed
      // in a nextTick if no explicit ID is provided, causing duplicate emits.
      if (!looseEqual(newVal.map(function (t) {
        return t._uid;
      }), oldVal.map(function (t) {
        return t._uid;
      }))) {
        // In a nextTick to ensure currentTab has been set first.
        this.$nextTick(function () {
          // We emit shallow copies of the new and old arrays of tabs, to
          // prevent users from potentially mutating the internal arrays.
          _this2.$emit('changed', newVal.slice(), oldVal.slice());
        });
      }
    },
    isMounted: function isMounted(newVal) {
      var _this3 = this;

      // Trigger an update after mounted.  Needed for tabs inside lazy modals.
      if (newVal) {
        requestAF(function () {
          _this3.updateTabs();
        });
      } // Enable or disable the observer


      this.setObserver(newVal);
    }
  },
  created: function created() {
    var _this4 = this;

    // Create private non-reactive props
    this.$_observer = null;
    this.currentTab = toInteger(this.value, -1); // For SSR and to make sure only a single tab is shown on mount
    // We wrap this in a `$nextTick()` to ensure the child tabs have been created

    this.$nextTick(function () {
      _this4.updateTabs();
    });
  },
  mounted: function mounted() {
    var _this5 = this;

    // Call `updateTabs()` just in case...
    this.updateTabs();
    this.$nextTick(function () {
      // Flag we are now mounted and to switch to DOM for tab probing.
      // As this.$slots.default appears to lie about component instances
      // after b-tabs is destroyed and re-instantiated.
      // And this.$children does not respect DOM order.
      _this5.isMounted = true;
    });
  },

  /* istanbul ignore next */
  deactivated: function deactivated() {
    this.isMounted = false;
  },

  /* istanbul ignore next */
  activated: function activated() {
    var _this6 = this;

    this.currentTab = toInteger(this.value, -1);
    this.$nextTick(function () {
      _this6.updateTabs();

      _this6.isMounted = true;
    });
  },
  beforeDestroy: function beforeDestroy() {
    this.isMounted = false;
  },
  destroyed: function destroyed() {
    // Ensure no references to child instances exist
    this.tabs = [];
  },
  methods: {
    registerTab: function registerTab(tab) {
      var _this7 = this;

      if (!arrayIncludes(this.registeredTabs, tab)) {
        this.registeredTabs.push(tab);
        tab.$once('hook:destroyed', function () {
          _this7.unregisterTab(tab);
        });
      }
    },
    unregisterTab: function unregisterTab(tab) {
      this.registeredTabs = this.registeredTabs.slice().filter(function (t) {
        return t !== tab;
      });
    },
    // DOM observer is needed to detect changes in order of tabs
    setObserver: function setObserver(on) {
      this.$_observer && this.$_observer.disconnect();
      this.$_observer = null;

      if (on) {
        var self = this;
        /* istanbul ignore next: difficult to test mutation observer in JSDOM */

        var handler = function handler() {
          // We delay the update to ensure that `tab.safeId()` has
          // updated with the final ID value.
          self.$nextTick(function () {
            requestAF(function () {
              self.updateTabs();
            });
          });
        }; // Watch for changes to <b-tab> sub components


        this.$_observer = observeDom(this.$refs.tabsContainer, handler, {
          childList: true,
          subtree: false,
          attributes: true,
          attributeFilter: ['id']
        });
      }
    },
    getTabs: function getTabs() {
      // We use `registeredTabs` as the source of truth for child tab components
      // We also filter out any `<b-tab>` components that are extended
      // `<b-tab>` with a root child `<b-tab>`
      // See: https://github.com/bootstrap-vue/bootstrap-vue/issues/3260
      var tabs = this.registeredTabs.filter(function (tab) {
        return tab.$children.filter(function (t) {
          return t._isTab;
        }).length === 0;
      }); // DOM Order of Tabs

      var order = [];

      if (this.isMounted && tabs.length > 0) {
        // We rely on the DOM when mounted to get the 'true' order of the `<b-tab>` children
        // `querySelectorAll()` always returns elements in document order, regardless of
        // order specified in the selector
        var selector = tabs.map(function (tab) {
          return "#".concat(tab.safeId());
        }).join(', ');
        order = selectAll(selector, this.$el).map(function (el) {
          return el.id;
        }).filter(identity);
      } // Stable sort keeps the original order if not found in the `order` array,
      // which will be an empty array before mount


      return stableSort(tabs, function (a, b) {
        return order.indexOf(a.safeId()) - order.indexOf(b.safeId());
      });
    },
    // Update list of `<b-tab>` children
    updateTabs: function updateTabs() {
      // Probe tabs
      var tabs = this.getTabs(); // Find *last* active non-disabled tab in current tabs
      // We trust tab state over `currentTab`, in case tabs were added/removed/re-ordered

      var tabIndex = tabs.indexOf(tabs.slice().reverse().find(function (tab) {
        return tab.localActive && !tab.disabled;
      })); // Else try setting to `currentTab`

      if (tabIndex < 0) {
        var currentTab = this.currentTab;

        if (currentTab >= tabs.length) {
          // Handle last tab being removed, so find the last non-disabled tab
          tabIndex = tabs.indexOf(tabs.slice().reverse().find(notDisabled));
        } else if (tabs[currentTab] && !tabs[currentTab].disabled) {
          // Current tab is not disabled
          tabIndex = currentTab;
        }
      } // Else find *first* non-disabled tab in current tabs


      if (tabIndex < 0) {
        tabIndex = tabs.indexOf(tabs.find(notDisabled));
      } // Set the current tab state to active


      tabs.forEach(function (tab) {
        // tab.localActive = idx === tabIndex && !tab.disabled
        tab.localActive = false;
      });

      if (tabs[tabIndex]) {
        tabs[tabIndex].localActive = true;
      } // Update the array of tab children


      this.tabs = tabs; // Set the currentTab index (can be -1 if no non-disabled tabs)

      this.currentTab = tabIndex;
    },
    // Find a button that controls a tab, given the tab reference
    // Returns the button vm instance
    getButtonForTab: function getButtonForTab(tab) {
      return (this.$refs.buttons || []).find(function (btn) {
        return btn.tab === tab;
      });
    },
    // Force a button to re-render its content, given a <b-tab> instance
    // Called by <b-tab> on `update()`
    updateButton: function updateButton(tab) {
      var button = this.getButtonForTab(tab);

      if (button && button.$forceUpdate) {
        button.$forceUpdate();
      }
    },
    // Activate a tab given a `<b-tab>` instance
    // Also accessed by `<b-tab>`
    activateTab: function activateTab(tab) {
      var result = false;

      if (tab) {
        var index = this.tabs.indexOf(tab);

        if (!tab.disabled && index > -1 && index !== this.currentTab) {
          var tabEvt = new BvEvent('activate-tab', {
            cancelable: true,
            vueTarget: this,
            componentId: this.safeId()
          });
          this.$emit(tabEvt.type, index, this.currentTab, tabEvt);

          if (!tabEvt.defaultPrevented) {
            result = true;
            this.currentTab = index;
          }
        }
      } // Couldn't set tab, so ensure v-model is set to `this.currentTab`

      /* istanbul ignore next: should rarely happen */


      if (!result && this.currentTab !== this.value) {
        this.$emit('input', this.currentTab);
      }

      return result;
    },
    // Deactivate a tab given a <b-tab> instance
    // Accessed by <b-tab>
    deactivateTab: function deactivateTab(tab) {
      if (tab) {
        // Find first non-disabled tab that isn't the one being deactivated
        // If no tabs are available, then don't deactivate current tab
        return this.activateTab(this.tabs.filter(function (t) {
          return t !== tab;
        }).find(notDisabled));
      }
      /* istanbul ignore next: should never/rarely happen */


      return false;
    },
    // Focus a tab button given its <b-tab> instance
    focusButton: function focusButton(tab) {
      var _this8 = this;

      // Wrap in `$nextTick()` to ensure DOM has completed rendering/updating before focusing
      this.$nextTick(function () {
        attemptFocus(_this8.getButtonForTab(tab));
      });
    },
    // Emit a click event on a specified <b-tab> component instance
    emitTabClick: function emitTabClick(tab, evt) {
      if (isEvent(evt) && tab && tab.$emit && !tab.disabled) {
        tab.$emit('click', evt);
      }
    },
    // Click handler
    clickTab: function clickTab(tab, evt) {
      this.activateTab(tab);
      this.emitTabClick(tab, evt);
    },
    // Move to first non-disabled tab
    firstTab: function firstTab(focus) {
      var tab = this.tabs.find(notDisabled);

      if (this.activateTab(tab) && focus) {
        this.focusButton(tab);
        this.emitTabClick(tab, focus);
      }
    },
    // Move to previous non-disabled tab
    previousTab: function previousTab(focus) {
      var currentIndex = mathMax(this.currentTab, 0);
      var tab = this.tabs.slice(0, currentIndex).reverse().find(notDisabled);

      if (this.activateTab(tab) && focus) {
        this.focusButton(tab);
        this.emitTabClick(tab, focus);
      }
    },
    // Move to next non-disabled tab
    nextTab: function nextTab(focus) {
      var currentIndex = mathMax(this.currentTab, -1);
      var tab = this.tabs.slice(currentIndex + 1).find(notDisabled);

      if (this.activateTab(tab) && focus) {
        this.focusButton(tab);
        this.emitTabClick(tab, focus);
      }
    },
    // Move to last non-disabled tab
    lastTab: function lastTab(focus) {
      var tab = this.tabs.slice().reverse().find(notDisabled);

      if (this.activateTab(tab) && focus) {
        this.focusButton(tab);
        this.emitTabClick(tab, focus);
      }
    }
  },
  render: function render(h) {
    var _this9 = this;

    var tabs = this.tabs,
        noKeyNav = this.noKeyNav,
        firstTab = this.firstTab,
        previousTab = this.previousTab,
        nextTab = this.nextTab,
        lastTab = this.lastTab; // Currently active tab

    var activeTab = tabs.find(function (tab) {
      return tab.localActive && !tab.disabled;
    }); // Tab button to allow focusing when no active tab found (keynav only)

    var fallbackTab = tabs.find(function (tab) {
      return !tab.disabled;
    }); // For each `<b-tab>` found create the tab buttons

    var buttons = tabs.map(function (tab, index) {
      var tabIndex = null; // Ensure at least one tab button is focusable when keynav enabled (if possible)

      if (!noKeyNav) {
        // Buttons are not in tab index unless active, or a fallback tab
        tabIndex = -1;

        if (activeTab === tab || !activeTab && fallbackTab === tab) {
          // Place tab button in tab sequence
          tabIndex = null;
        }
      }

      return h(BVTabButton, {
        key: tab._uid || index,
        ref: 'buttons',
        // Needed to make `this.$refs.buttons` an array
        refInFor: true,
        props: {
          tab: tab,
          tabs: tabs,
          id: tab.controlledBy || (tab.safeId ? tab.safeId("_BV_tab_button_") : null),
          controls: tab.safeId ? tab.safeId() : null,
          tabIndex: tabIndex,
          setSize: tabs.length,
          posInSet: index + 1,
          noKeyNav: noKeyNav
        },
        on: {
          click: function click(evt) {
            _this9.clickTab(tab, evt);
          },
          first: firstTab,
          prev: previousTab,
          next: nextTab,
          last: lastTab
        }
      });
    }); // Nav

    var nav = h(BNav, {
      ref: 'nav',
      class: this.localNavClass,
      attrs: {
        role: 'tablist',
        id: this.safeId('_BV_tab_controls_')
      },
      props: {
        fill: this.fill,
        justified: this.justified,
        align: this.align,
        tabs: !this.noNavStyle && !this.pills,
        pills: !this.noNavStyle && this.pills,
        vertical: this.vertical,
        small: this.small,
        cardHeader: this.card && !this.vertical
      }
    }, [this.normalizeSlot('tabs-start') || h(), buttons, this.normalizeSlot('tabs-end') || h()]);
    nav = h('div', {
      key: 'bv-tabs-nav',
      class: [{
        'card-header': this.card && !this.vertical && !this.end,
        'card-footer': this.card && !this.vertical && this.end,
        'col-auto': this.vertical
      }, this.navWrapperClass]
    }, [nav]);
    var empty = h();

    if (!tabs || tabs.length === 0) {
      empty = h('div', {
        key: 'bv-empty-tab',
        class: ['tab-pane', 'active', {
          'card-body': this.card
        }]
      }, this.normalizeSlot('empty'));
    } // Main content section


    var content = h('div', {
      ref: 'tabsContainer',
      key: 'bv-tabs-container',
      staticClass: 'tab-content',
      class: [{
        col: this.vertical
      }, this.contentClass],
      attrs: {
        id: this.safeId('_BV_tab_container_')
      }
    }, concat(this.normalizeSlot(), empty)); // Render final output

    return h(this.tag, {
      staticClass: 'tabs',
      class: {
        row: this.vertical,
        'no-gutters': this.vertical && this.card
      },
      attrs: {
        id: this.safeId()
      }
    }, [this.end ? content : h(), [nav], this.end ? h() : content]);
  }
});