function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_IMG_LAZY } from '../../constants/components';
import identity from '../../utils/identity';
import { concat } from '../../utils/array';
import { makePropsConfigurable } from '../../utils/config';
import { hasIntersectionObserverSupport } from '../../utils/env';
import { toInteger } from '../../utils/number';
import { omit } from '../../utils/object';
import { VBVisible } from '../../directives/visible/visible';
import { BImg, props as BImgProps } from './img';
export var props = makePropsConfigurable(_objectSpread(_objectSpread({}, omit(BImgProps, ['blank'])), {}, {
  blankSrc: {
    // If null, a blank image is generated
    type: String,
    default: null
  },
  blankColor: {
    type: String,
    default: 'transparent'
  },
  blankWidth: {
    type: [Number, String] // default: null

  },
  blankHeight: {
    type: [Number, String] // default: null

  },
  show: {
    type: Boolean,
    default: false
  },
  offset: {
    // Distance away from viewport (in pixels) before being
    // considered "visible"
    type: [Number, String],
    default: 360
  }
}), NAME_IMG_LAZY); // @vue/component

export var BImgLazy = /*#__PURE__*/Vue.extend({
  name: NAME_IMG_LAZY,
  directives: {
    bVisible: VBVisible
  },
  props: props,
  data: function data() {
    return {
      isShown: this.show
    };
  },
  computed: {
    computedSrc: function computedSrc() {
      return !this.blankSrc || this.isShown ? this.src : this.blankSrc;
    },
    computedBlank: function computedBlank() {
      return !(this.isShown || this.blankSrc);
    },
    computedWidth: function computedWidth() {
      return this.isShown ? this.width : this.blankWidth || this.width;
    },
    computedHeight: function computedHeight() {
      return this.isShown ? this.height : this.blankHeight || this.height;
    },
    computedSrcset: function computedSrcset() {
      var srcset = concat(this.srcset).filter(identity).join(',');
      return !this.blankSrc || this.isShown ? srcset : null;
    },
    computedSizes: function computedSizes() {
      var sizes = concat(this.sizes).filter(identity).join(',');
      return !this.blankSrc || this.isShown ? sizes : null;
    }
  },
  watch: {
    show: function show(newVal, oldVal) {
      if (newVal !== oldVal) {
        // If IntersectionObserver support is not available, image is always shown
        var visible = hasIntersectionObserverSupport ? newVal : true;
        this.isShown = visible;

        if (visible !== newVal) {
          // Ensure the show prop is synced (when no IntersectionObserver)
          this.$nextTick(this.updateShowProp);
        }
      }
    },
    isShown: function isShown(newVal, oldVal) {
      if (newVal !== oldVal) {
        // Update synched show prop
        this.updateShowProp();
      }
    }
  },
  mounted: function mounted() {
    // If IntersectionObserver is not available, image is always shown
    this.isShown = hasIntersectionObserverSupport ? this.show : true;
  },
  methods: {
    updateShowProp: function updateShowProp() {
      this.$emit('update:show', this.isShown);
    },
    doShow: function doShow(visible) {
      // If IntersectionObserver is not supported, the callback
      // will be called with `null` rather than `true` or `false`
      if ((visible || visible === null) && !this.isShown) {
        this.isShown = true;
      }
    }
  },
  render: function render(h) {
    var directives = [];

    if (!this.isShown) {
      var _modifiers;

      // We only add the visible directive if we are not shown
      directives.push({
        // Visible directive will silently do nothing if
        // IntersectionObserver is not supported
        name: 'b-visible',
        // Value expects a callback (passed one arg of `visible` = `true` or `false`)
        value: this.doShow,
        modifiers: (_modifiers = {}, _defineProperty(_modifiers, "".concat(toInteger(this.offset, 0)), true), _defineProperty(_modifiers, "once", true), _modifiers)
      });
    }

    return h(BImg, {
      directives: directives,
      props: {
        // Computed value props
        src: this.computedSrc,
        blank: this.computedBlank,
        width: this.computedWidth,
        height: this.computedHeight,
        srcset: this.computedSrcset || null,
        sizes: this.computedSizes || null,
        // Passthrough props
        alt: this.alt,
        blankColor: this.blankColor,
        fluid: this.fluid,
        fluidGrow: this.fluidGrow,
        block: this.block,
        thumbnail: this.thumbnail,
        rounded: this.rounded,
        left: this.left,
        right: this.right,
        center: this.center
      }
    });
  }
});