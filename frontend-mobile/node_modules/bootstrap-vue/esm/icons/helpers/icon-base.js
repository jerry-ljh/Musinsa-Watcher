function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue, { mergeData } from '../../vue';
import { NAME_ICON_BASE } from '../../constants/components';
import identity from '../../utils/identity';
import { isUndefinedOrNull } from '../../utils/inspect';
import { mathMax } from '../../utils/math';
import { toFloat } from '../../utils/number'; // Common icon props (should be cloned/spread before using)

export var commonIconProps = {
  title: {
    type: String // default: null

  },
  variant: {
    type: String,
    default: null
  },
  fontScale: {
    type: [Number, String],
    default: 1
  },
  scale: {
    type: [Number, String],
    default: 1
  },
  rotate: {
    type: [Number, String],
    default: 0
  },
  flipH: {
    type: Boolean,
    default: false
  },
  flipV: {
    type: Boolean,
    default: false
  },
  shiftH: {
    type: [Number, String],
    default: 0
  },
  shiftV: {
    type: [Number, String],
    default: 0
  },
  animation: {
    type: String,
    default: null
  }
}; // Base attributes needed on all icons

var baseAttrs = {
  viewBox: '0 0 16 16',
  width: '1em',
  height: '1em',
  focusable: 'false',
  role: 'img',
  'aria-label': 'icon'
}; // Attributes that are nulled out when stacked

var stackedAttrs = {
  width: null,
  height: null,
  focusable: null,
  role: null,
  'aria-label': null
}; // Shared private base component to reduce bundle/runtime size
// @vue/component

export var BVIconBase = /*#__PURE__*/Vue.extend({
  name: NAME_ICON_BASE,
  functional: true,
  props: _objectSpread({
    content: {
      type: String
    },
    stacked: {
      type: Boolean,
      default: false
    }
  }, commonIconProps),
  render: function render(h, _ref) {
    var _class;

    var data = _ref.data,
        props = _ref.props,
        children = _ref.children;
    var fontScale = mathMax(toFloat(props.fontScale, 1), 0) || 1;
    var scale = mathMax(toFloat(props.scale, 1), 0) || 1;
    var rotate = toFloat(props.rotate, 0);
    var shiftH = toFloat(props.shiftH, 0);
    var shiftV = toFloat(props.shiftV, 0);
    var flipH = props.flipH;
    var flipV = props.flipV;
    var animation = props.animation; // Compute the transforms
    // Note that order is important as SVG transforms are applied in order from
    // left to right and we want flipping/scale to occur before rotation
    // Note shifting is applied separately
    // Assumes that the viewbox is `0 0 16 16` (`8 8` is the center)

    var hasScale = flipH || flipV || scale !== 1;
    var hasTransforms = hasScale || rotate;
    var hasShift = shiftH || shiftV;
    var transforms = [hasTransforms ? 'translate(8 8)' : null, hasScale ? "scale(".concat((flipH ? -1 : 1) * scale, " ").concat((flipV ? -1 : 1) * scale, ")") : null, rotate ? "rotate(".concat(rotate, ")") : null, hasTransforms ? 'translate(-8 -8)' : null].filter(identity); // Handling stacked icons

    var isStacked = props.stacked;
    var hasContent = !isUndefinedOrNull(props.content); // We wrap the content in a `<g>` for handling the transforms (except shift)

    var $inner = h('g', {
      attrs: {
        transform: transforms.join(' ') || null
      },
      domProps: hasContent ? {
        innerHTML: props.content || ''
      } : {}
    }, children); // If needed, we wrap in an additional `<g>` in order to handle the shifting

    if (hasShift) {
      $inner = h('g', {
        attrs: {
          transform: "translate(".concat(16 * shiftH / 16, " ").concat(-16 * shiftV / 16, ")")
        }
      }, [$inner]);
    }

    if (isStacked) {
      // Wrap in an additional `<g>` for proper
      // animation handling if stacked
      $inner = h('g', {}, [$inner]);
    }

    var $title = props.title ? h('title', props.title) : null;
    return h('svg', mergeData({
      staticClass: 'b-icon bi',
      class: (_class = {}, _defineProperty(_class, "text-".concat(props.variant), !!props.variant), _defineProperty(_class, "b-icon-animation-".concat(animation), !!animation), _class),
      attrs: baseAttrs,
      style: isStacked ? {} : {
        fontSize: fontScale === 1 ? null : "".concat(fontScale * 100, "%")
      }
    }, // Merge in user supplied data
    data, // If icon is stacked, null out some attrs
    isStacked ? {
      attrs: stackedAttrs
    } : {}, // These cannot be overridden by users
    {
      attrs: {
        xmlns: isStacked ? null : 'http://www.w3.org/2000/svg',
        fill: 'currentColor'
      }
    }), [$title, $inner]);
  }
});