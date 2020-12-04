function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_JUMBOTRON } from '../../constants/components';
import { SLOT_NAME_DEFAULT, SLOT_NAME_HEADER, SLOT_NAME_LEAD } from '../../constants/slot-names';
import { makePropsConfigurable } from '../../utils/config';
import { htmlOrText } from '../../utils/html';
import { hasNormalizedSlot, normalizeSlot } from '../../utils/normalize-slot';
import { BContainer } from '../layout/container'; // --- Props ---

export var props = makePropsConfigurable({
  fluid: {
    type: Boolean,
    default: false
  },
  containerFluid: {
    type: [Boolean, String],
    default: false
  },
  header: {
    type: String // default: null

  },
  headerHtml: {
    type: String // default: null

  },
  headerTag: {
    type: String,
    default: 'h1'
  },
  headerLevel: {
    type: [Number, String],
    default: '3'
  },
  lead: {
    type: String // default: null

  },
  leadHtml: {
    type: String // default: null

  },
  leadTag: {
    type: String,
    default: 'p'
  },
  tag: {
    type: String,
    default: 'div'
  },
  bgVariant: {
    type: String // default: undefined

  },
  borderVariant: {
    type: String // default: undefined

  },
  textVariant: {
    type: String // default: undefined

  }
}, NAME_JUMBOTRON); // --- Main component ---
// @vue/component

export var BJumbotron = /*#__PURE__*/Vue.extend({
  name: NAME_JUMBOTRON,
  functional: true,
  props: props,
  render: function render(h, _ref) {
    var _class2;

    var props = _ref.props,
        data = _ref.data,
        slots = _ref.slots,
        scopedSlots = _ref.scopedSlots;
    var header = props.header,
        headerHtml = props.headerHtml,
        lead = props.lead,
        leadHtml = props.leadHtml,
        textVariant = props.textVariant,
        bgVariant = props.bgVariant,
        borderVariant = props.borderVariant;
    var $scopedSlots = scopedSlots || {};
    var $slots = slots();
    var slotScope = {};
    var $header = h();
    var hasHeaderSlot = hasNormalizedSlot(SLOT_NAME_HEADER, $scopedSlots, $slots);

    if (hasHeaderSlot || header || headerHtml) {
      var headerLevel = props.headerLevel;
      $header = h(props.headerTag, {
        class: _defineProperty({}, "display-".concat(headerLevel), headerLevel),
        domProps: hasHeaderSlot ? {} : htmlOrText(headerHtml, header)
      }, normalizeSlot(SLOT_NAME_HEADER, slotScope, $scopedSlots, $slots));
    }

    var $lead = h();
    var hasLeadSlot = hasNormalizedSlot(SLOT_NAME_LEAD, $scopedSlots, $slots);

    if (hasLeadSlot || lead || leadHtml) {
      $lead = h(props.leadTag, {
        staticClass: 'lead',
        domProps: hasLeadSlot ? {} : htmlOrText(leadHtml, lead)
      }, normalizeSlot(SLOT_NAME_LEAD, slotScope, $scopedSlots, $slots));
    }

    var $children = [$header, $lead, normalizeSlot(SLOT_NAME_DEFAULT, slotScope, $scopedSlots, $slots)]; // If fluid, wrap content in a container

    if (props.fluid) {
      $children = [h(BContainer, {
        props: {
          fluid: props.containerFluid
        }
      }, $children)];
    }

    return h(props.tag, mergeData(data, {
      staticClass: 'jumbotron',
      class: (_class2 = {
        'jumbotron-fluid': props.fluid
      }, _defineProperty(_class2, "text-".concat(textVariant), textVariant), _defineProperty(_class2, "bg-".concat(bgVariant), bgVariant), _defineProperty(_class2, "border-".concat(borderVariant), borderVariant), _defineProperty(_class2, "border", borderVariant), _class2)
    }), $children);
  }
});