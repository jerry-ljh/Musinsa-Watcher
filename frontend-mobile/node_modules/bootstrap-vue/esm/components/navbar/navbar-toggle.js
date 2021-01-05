import Vue from '../../vue';
import { NAME_NAVBAR_TOGGLE } from '../../constants/components';
import { SLOT_NAME_DEFAULT } from '../../constants/slot-names';
import { makePropsConfigurable } from '../../utils/config';
import listenOnRootMixin from '../../mixins/listen-on-root';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import { VBToggle, EVENT_STATE, EVENT_STATE_SYNC } from '../../directives/toggle/toggle'; // --- Constants ---

var CLASS_NAME = 'navbar-toggler'; // --- Main component ---
// @vue/component

export var BNavbarToggle = /*#__PURE__*/Vue.extend({
  name: NAME_NAVBAR_TOGGLE,
  directives: {
    VBToggle: VBToggle
  },
  mixins: [listenOnRootMixin, normalizeSlotMixin],
  props: makePropsConfigurable({
    label: {
      type: String,
      default: 'Toggle navigation'
    },
    target: {
      type: [Array, String],
      required: true
    },
    disabled: {
      type: Boolean,
      default: false
    }
  }, NAME_NAVBAR_TOGGLE),
  data: function data() {
    return {
      toggleState: false
    };
  },
  created: function created() {
    this.listenOnRoot(EVENT_STATE, this.handleStateEvt);
    this.listenOnRoot(EVENT_STATE_SYNC, this.handleStateEvt);
  },
  methods: {
    onClick: function onClick(evt) {
      if (!this.disabled) {
        // Emit courtesy `click` event
        this.$emit('click', evt);
      }
    },
    handleStateEvt: function handleStateEvt(id, state) {
      // We listen for state events so that we can pass the
      // boolean expanded state to the default scoped slot
      if (id === this.target) {
        this.toggleState = state;
      }
    }
  },
  render: function render(h) {
    var disabled = this.disabled;
    return h('button', {
      staticClass: CLASS_NAME,
      class: {
        disabled: disabled
      },
      directives: [{
        name: 'VBToggle',
        value: this.target
      }],
      attrs: {
        type: 'button',
        disabled: disabled,
        'aria-label': this.label
      },
      on: {
        click: this.onClick
      }
    }, [this.normalizeSlot(SLOT_NAME_DEFAULT, {
      expanded: this.toggleState
    }) || h('span', {
      staticClass: "".concat(CLASS_NAME, "-icon")
    })]);
  }
});