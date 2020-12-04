import { SLOT_NAME_DEFAULT } from '../constants/slot-names';
import { hasNormalizedSlot as _hasNormalizedSlot, normalizeSlot as _normalizeSlot } from '../utils/normalize-slot';
import { concat } from '../utils/array';
export default {
  methods: {
    hasNormalizedSlot: function hasNormalizedSlot() {
      var name = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : SLOT_NAME_DEFAULT;
      // Returns true if the either a $scopedSlot or $slot exists with the specified name
      // `name` can be a string name or an array of names
      return _hasNormalizedSlot(name, this.$scopedSlots, this.$slots);
    },
    normalizeSlot: function normalizeSlot() {
      var name = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : SLOT_NAME_DEFAULT;
      var scope = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

      // Returns an array of rendered VNodes if slot found.
      // Returns undefined if not found.
      // `name` can be a string name or an array of names
      var vNodes = _normalizeSlot(name, scope, this.$scopedSlots, this.$slots);

      return vNodes ? concat(vNodes) : vNodes;
    }
  }
};