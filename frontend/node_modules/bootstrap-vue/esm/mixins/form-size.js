import { makePropsConfigurable } from '../utils/config'; // --- Props ---

export var props = makePropsConfigurable({
  size: {
    type: String // default: null

  }
}, 'formControls'); // --- Mixin ---
// @vue/component

export default {
  props: props,
  computed: {
    sizeFormClass: function sizeFormClass() {
      return [this.size ? "form-control-".concat(this.size) : null];
    }
  }
};