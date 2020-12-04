function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../../vue';
import { NAME_TOOLTIP_TEMPLATE } from '../../../constants/components';
import scopedStyleAttrsMixin from '../../../mixins/scoped-style-attrs';
import { isFunction, isUndefinedOrNull } from '../../../utils/inspect';
import { BVPopper } from './bv-popper'; // @vue/component

export var BVTooltipTemplate = /*#__PURE__*/Vue.extend({
  name: NAME_TOOLTIP_TEMPLATE,
  extends: BVPopper,
  mixins: [scopedStyleAttrsMixin],
  props: {
    // Other non-reactive (while open) props are pulled in from BVPopper
    id: {
      type: String // default: null

    },
    html: {
      // Used only by the directive versions
      type: Boolean // default: false

    }
  },
  data: function data() {
    // We use data, rather than props to ensure reactivity
    // Parent component will directly set this data
    return {
      title: '',
      content: '',
      variant: null,
      customClass: null,
      interactive: true
    };
  },
  computed: {
    templateType: function templateType() {
      return 'tooltip';
    },
    templateClasses: function templateClasses() {
      var _ref;

      return [(_ref = {
        // Disables pointer events to hide the tooltip when the user
        // hovers over its content
        noninteractive: !this.interactive
      }, _defineProperty(_ref, "b-".concat(this.templateType, "-").concat(this.variant), this.variant), _defineProperty(_ref, "bs-".concat(this.templateType, "-").concat(this.attachment), this.attachment), _ref), this.customClass];
    },
    templateAttributes: function templateAttributes() {
      return _objectSpread(_objectSpread({}, this.$parent.$parent.$attrs), {}, {
        id: this.id,
        role: 'tooltip',
        tabindex: '-1'
      }, this.scopedStyleAttrs);
    },
    templateListeners: function templateListeners() {
      var _this = this;

      // Used for hover/focus trigger listeners
      return {
        mouseenter
        /* istanbul ignore next */
        : function mouseenter(evt) {
          /* istanbul ignore next: difficult to test in JSDOM */
          _this.$emit('mouseenter', evt);
        },
        mouseleave
        /* istanbul ignore next */
        : function mouseleave(evt) {
          /* istanbul ignore next: difficult to test in JSDOM */
          _this.$emit('mouseleave', evt);
        },
        focusin
        /* istanbul ignore next */
        : function focusin(evt) {
          /* istanbul ignore next: difficult to test in JSDOM */
          _this.$emit('focusin', evt);
        },
        focusout
        /* istanbul ignore next */
        : function focusout(evt) {
          /* istanbul ignore next: difficult to test in JSDOM */
          _this.$emit('focusout', evt);
        }
      };
    }
  },
  methods: {
    renderTemplate: function renderTemplate(h) {
      // Title can be a scoped slot function
      var $title = isFunction(this.title) ? this.title({}) : isUndefinedOrNull(this.title) ?
      /* istanbul ignore next */
      h() : this.title; // Directive versions only

      var domProps = this.html && !isFunction(this.title) ? {
        innerHTML: this.title
      } : {};
      return h('div', {
        staticClass: 'tooltip b-tooltip',
        class: this.templateClasses,
        attrs: this.templateAttributes,
        on: this.templateListeners
      }, [h('div', {
        ref: 'arrow',
        staticClass: 'arrow'
      }), h('div', {
        staticClass: 'tooltip-inner',
        domProps: domProps
      }, [$title])]);
    }
  }
});