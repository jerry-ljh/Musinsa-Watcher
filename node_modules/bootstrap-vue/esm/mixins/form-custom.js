import { makePropsConfigurable } from '../utils/config'; // --- Props ---

export var props = makePropsConfigurable({
  plain: {
    type: Boolean,
    default: false
  }
}, 'formControls'); // --- Mixin ---
// @vue/component

export default {
  props: props,
  computed: {
    custom: function custom() {
      return !this.plain;
    }
  }
};