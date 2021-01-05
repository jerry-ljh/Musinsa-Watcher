import Vue from '../../vue';
import { NAME_COLLAPSE } from '../../constants/components';
import { EVENT_OPTIONS_NO_CAPTURE } from '../../constants/events';
import { SLOT_NAME_DEFAULT } from '../../constants/slot-names';
import { makePropsConfigurable } from '../../utils/config';
import { BVCollapse } from '../../utils/bv-collapse';
import { addClass, hasClass, removeClass, closest, matches, getCS } from '../../utils/dom';
import { isBrowser } from '../../utils/env';
import { eventOnOff } from '../../utils/events';
import idMixin from '../../mixins/id';
import listenOnRootMixin from '../../mixins/listen-on-root';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import { EVENT_TOGGLE, EVENT_STATE, EVENT_STATE_REQUEST, EVENT_STATE_SYNC } from '../../directives/toggle/toggle'; // --- Constants ---
// Accordion event name we emit on `$root`

var EVENT_ACCORDION = 'bv::collapse::accordion'; // --- Main component ---
// @vue/component

export var BCollapse = /*#__PURE__*/Vue.extend({
  name: NAME_COLLAPSE,
  mixins: [idMixin, listenOnRootMixin, normalizeSlotMixin],
  model: {
    prop: 'visible',
    event: 'input'
  },
  props: makePropsConfigurable({
    isNav: {
      type: Boolean,
      default: false
    },
    accordion: {
      type: String // default: null

    },
    visible: {
      type: Boolean,
      default: false
    },
    tag: {
      type: String,
      default: 'div'
    },
    appear: {
      // If `true` (and `visible` is `true` on mount), animate initially visible
      type: Boolean,
      default: false
    }
  }, NAME_COLLAPSE),
  data: function data() {
    return {
      show: this.visible,
      transitioning: false
    };
  },
  computed: {
    classObject: function classObject() {
      return {
        'navbar-collapse': this.isNav,
        collapse: !this.transitioning,
        show: this.show && !this.transitioning
      };
    }
  },
  watch: {
    visible: function visible(newVal) {
      if (newVal !== this.show) {
        this.show = newVal;
      }
    },
    show: function show(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.emitState();
      }
    }
  },
  created: function created() {
    this.show = this.visible;
  },
  mounted: function mounted() {
    var _this = this;

    this.show = this.visible; // Listen for toggle events to open/close us

    this.listenOnRoot(EVENT_TOGGLE, this.handleToggleEvt); // Listen to other collapses for accordion events

    this.listenOnRoot(EVENT_ACCORDION, this.handleAccordionEvt);

    if (this.isNav) {
      // Set up handlers
      this.setWindowEvents(true);
      this.handleResize();
    }

    this.$nextTick(function () {
      _this.emitState();
    }); // Listen for "Sync state" requests from `v-b-toggle`

    this.listenOnRoot(EVENT_STATE_REQUEST, function (id) {
      if (id === _this.safeId()) {
        _this.$nextTick(_this.emitSync);
      }
    });
  },
  updated: function updated() {
    // Emit a private event every time this component updates to ensure
    // the toggle button is in sync with the collapse's state
    // It is emitted regardless if the visible state changes
    this.emitSync();
  },

  /* istanbul ignore next */
  deactivated: function deactivated() {
    if (this.isNav) {
      this.setWindowEvents(false);
    }
  },

  /* istanbul ignore next */
  activated: function activated() {
    if (this.isNav) {
      this.setWindowEvents(true);
    }

    this.emitSync();
  },
  beforeDestroy: function beforeDestroy() {
    // Trigger state emit if needed
    this.show = false;

    if (this.isNav && isBrowser) {
      this.setWindowEvents(false);
    }
  },
  methods: {
    setWindowEvents: function setWindowEvents(on) {
      eventOnOff(on, window, 'resize', this.handleResize, EVENT_OPTIONS_NO_CAPTURE);
      eventOnOff(on, window, 'orientationchange', this.handleResize, EVENT_OPTIONS_NO_CAPTURE);
    },
    toggle: function toggle() {
      this.show = !this.show;
    },
    onEnter: function onEnter() {
      this.transitioning = true; // This should be moved out so we can add cancellable events

      this.$emit('show');
    },
    onAfterEnter: function onAfterEnter() {
      this.transitioning = false;
      this.$emit('shown');
    },
    onLeave: function onLeave() {
      this.transitioning = true; // This should be moved out so we can add cancellable events

      this.$emit('hide');
    },
    onAfterLeave: function onAfterLeave() {
      this.transitioning = false;
      this.$emit('hidden');
    },
    emitState: function emitState() {
      this.$emit('input', this.show); // Let `v-b-toggle` know the state of this collapse

      this.emitOnRoot(EVENT_STATE, this.safeId(), this.show);

      if (this.accordion && this.show) {
        // Tell the other collapses in this accordion to close
        this.emitOnRoot(EVENT_ACCORDION, this.safeId(), this.accordion);
      }
    },
    emitSync: function emitSync() {
      // Emit a private event every time this component updates to ensure
      // the toggle button is in sync with the collapse's state
      // It is emitted regardless if the visible state changes
      this.emitOnRoot(EVENT_STATE_SYNC, this.safeId(), this.show);
    },
    checkDisplayBlock: function checkDisplayBlock() {
      // Check to see if the collapse has `display: block !important` set
      // We can't set `display: none` directly on `this.$el`, as it would
      // trigger a new transition to start (or cancel a current one)
      var restore = hasClass(this.$el, 'show');
      removeClass(this.$el, 'show');
      var isBlock = getCS(this.$el).display === 'block';

      if (restore) {
        addClass(this.$el, 'show');
      }

      return isBlock;
    },
    clickHandler: function clickHandler(evt) {
      // If we are in a nav/navbar, close the collapse when non-disabled link clicked
      var el = evt.target;

      if (!this.isNav || !el || getCS(this.$el).display !== 'block') {
        /* istanbul ignore next: can't test getComputedStyle in JSDOM */
        return;
      }

      if (matches(el, '.nav-link,.dropdown-item') || closest('.nav-link,.dropdown-item', el)) {
        if (!this.checkDisplayBlock()) {
          // Only close the collapse if it is not forced to be `display: block !important`
          this.show = false;
        }
      }
    },
    handleToggleEvt: function handleToggleEvt(target) {
      if (target !== this.safeId()) {
        return;
      }

      this.toggle();
    },
    handleAccordionEvt: function handleAccordionEvt(openedId, accordion) {
      if (!this.accordion || accordion !== this.accordion) {
        return;
      }

      if (openedId === this.safeId()) {
        // Open this collapse if not shown
        if (!this.show) {
          this.toggle();
        }
      } else {
        // Close this collapse if shown
        if (this.show) {
          this.toggle();
        }
      }
    },
    handleResize: function handleResize() {
      // Handler for orientation/resize to set collapsed state in nav/navbar
      this.show = getCS(this.$el).display === 'block';
    }
  },
  render: function render(h) {
    var _this2 = this;

    var scope = {
      visible: this.show,
      close: function close() {
        return _this2.show = false;
      }
    };
    var content = h(this.tag, {
      class: this.classObject,
      directives: [{
        name: 'show',
        value: this.show
      }],
      attrs: {
        id: this.safeId()
      },
      on: {
        click: this.clickHandler
      }
    }, [this.normalizeSlot(SLOT_NAME_DEFAULT, scope)]);
    return h(BVCollapse, {
      props: {
        appear: this.appear
      },
      on: {
        enter: this.onEnter,
        afterEnter: this.onAfterEnter,
        leave: this.onLeave,
        afterLeave: this.onAfterLeave
      }
    }, [content]);
  }
});