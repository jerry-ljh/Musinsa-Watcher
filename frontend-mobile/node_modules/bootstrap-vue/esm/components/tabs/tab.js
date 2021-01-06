import Vue from '../../vue';
import { NAME_TAB } from '../../constants/components';
import { SLOT_NAME_TITLE } from '../../constants/slot-names';
import { makePropsConfigurable } from '../../utils/config';
import BVTransition from '../../utils/bv-transition';
import idMixin from '../../mixins/id';
import normalizeSlotMixin from '../../mixins/normalize-slot'; // @vue/component

export var BTab = /*#__PURE__*/Vue.extend({
  name: NAME_TAB,
  mixins: [idMixin, normalizeSlotMixin],
  inject: {
    bvTabs: {
      default: function _default() {
        return {};
      }
    }
  },
  props: makePropsConfigurable({
    active: {
      type: Boolean,
      default: false
    },
    tag: {
      type: String,
      default: 'div'
    },
    buttonId: {
      type: String // default: ''

    },
    title: {
      type: String,
      default: ''
    },
    titleItemClass: {
      // Sniffed by `<b-tabs>` and added to nav `li.nav-item`
      type: [String, Array, Object] // default: null

    },
    titleLinkClass: {
      // Sniffed by `<b-tabs>` and added to nav `a.nav-link`
      type: [String, Array, Object] // default: null

    },
    titleLinkAttributes: {
      type: Object // default: null

    },
    disabled: {
      type: Boolean,
      default: false
    },
    noBody: {
      type: Boolean,
      default: false
    },
    lazy: {
      type: Boolean,
      default: false
    }
  }, NAME_TAB),
  data: function data() {
    return {
      localActive: this.active && !this.disabled,
      show: false
    };
  },
  computed: {
    tabClasses: function tabClasses() {
      return [{
        active: this.localActive,
        disabled: this.disabled,
        'card-body': this.bvTabs.card && !this.noBody
      }, // Apply <b-tabs> `activeTabClass` styles when this tab is active
      this.localActive ? this.bvTabs.activeTabClass : null];
    },
    controlledBy: function controlledBy() {
      return this.buttonId || this.safeId('__BV_tab_button__');
    },
    computedNoFade: function computedNoFade() {
      return !(this.bvTabs.fade || false);
    },
    computedLazy: function computedLazy() {
      return this.bvTabs.lazy || this.lazy;
    },
    // For parent sniffing of child
    _isTab: function _isTab() {
      return true;
    }
  },
  watch: {
    localActive: function localActive(newValue) {
      // Make `active` prop work with `.sync` modifier
      this.$emit('update:active', newValue);
    },
    active: function active(newValue, oldValue) {
      if (newValue !== oldValue) {
        if (newValue) {
          // If activated post mount
          this.activate();
        } else {
          /* istanbul ignore next */
          if (!this.deactivate()) {
            // Tab couldn't be deactivated, so we reset the synced active prop
            // Deactivation will fail if no other tabs to activate
            this.$emit('update:active', this.localActive);
          }
        }
      }
    },
    disabled: function disabled(newValue, oldValue) {
      if (newValue !== oldValue) {
        var firstTab = this.bvTabs.firstTab;

        if (newValue && this.localActive && firstTab) {
          this.localActive = false;
          firstTab();
        }
      }
    }
  },
  mounted: function mounted() {
    // Inform b-tabs of our presence
    this.registerTab(); // Initially show on mount if active and not disabled

    this.show = this.localActive;
  },
  updated: function updated() {
    // Force the tab button content to update (since slots are not reactive)
    // Only done if we have a title slot, as the title prop is reactive
    var updateButton = this.bvTabs.updateButton;

    if (updateButton && this.hasNormalizedSlot(SLOT_NAME_TITLE)) {
      updateButton(this);
    }
  },
  destroyed: function destroyed() {
    // inform b-tabs of our departure
    this.unregisterTab();
  },
  methods: {
    // Private methods
    registerTab: function registerTab() {
      // Inform `<b-tabs>` of our presence
      var registerTab = this.bvTabs.registerTab;

      if (registerTab) {
        registerTab(this);
      }
    },
    unregisterTab: function unregisterTab() {
      // Inform `<b-tabs>` of our departure
      var unregisterTab = this.bvTabs.unregisterTab;

      if (unregisterTab) {
        unregisterTab(this);
      }
    },
    // Public methods
    activate: function activate() {
      // Not inside a `<b-tabs>` component or tab is disabled
      var activateTab = this.bvTabs.activateTab;
      return activateTab && !this.disabled ? activateTab(this) : false;
    },
    deactivate: function deactivate() {
      // Not inside a `<b-tabs>` component or not active to begin with
      var deactivateTab = this.bvTabs.deactivateTab;
      return deactivateTab && this.localActive ? deactivateTab(this) : false;
    }
  },
  render: function render(h) {
    var localActive = this.localActive;
    var $content = h(this.tag, {
      ref: 'panel',
      staticClass: 'tab-pane',
      class: this.tabClasses,
      directives: [{
        name: 'show',
        rawName: 'v-show',
        value: localActive,
        expression: 'localActive'
      }],
      attrs: {
        role: 'tabpanel',
        id: this.safeId(),
        'aria-hidden': localActive ? 'false' : 'true',
        'aria-labelledby': this.controlledBy || null
      }
    }, // Render content lazily if requested
    [localActive || !this.computedLazy ? this.normalizeSlot() : h()]);
    return h(BVTransition, {
      props: {
        mode: 'out-in',
        noFade: this.computedNoFade
      }
    }, [$content]);
  }
});