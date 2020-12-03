function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_FORM_RATING, NAME_FORM_RATING_STAR } from '../../constants/components';
import { CODE_LEFT, CODE_RIGHT, CODE_UP, CODE_DOWN } from '../../constants/key-codes';
import identity from '../../utils/identity';
import { arrayIncludes, concat } from '../../utils/array';
import { makePropsConfigurable } from '../../utils/config';
import { attemptBlur, attemptFocus } from '../../utils/dom';
import { stopEvent } from '../../utils/events';
import { isNull } from '../../utils/inspect';
import { isLocaleRTL } from '../../utils/locale';
import { mathMax, mathMin } from '../../utils/math';
import { toInteger, toFloat } from '../../utils/number';
import { omit } from '../../utils/object';
import { toString } from '../../utils/string';
import formSizeMixin, { props as formSizeProps } from '../../mixins/form-size';
import idMixin from '../../mixins/id';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import { props as formControlProps } from '../../mixins/form-control';
import { BIcon } from '../../icons/icon';
import { BIconStar, BIconStarHalf, BIconStarFill, BIconX } from '../../icons/icons'; // --- Constants ---

var MIN_STARS = 3;
var DEFAULT_STARS = 5; // --- Utility methods ---

var computeStars = function computeStars(stars) {
  return mathMax(MIN_STARS, toInteger(stars, DEFAULT_STARS));
};

var clampValue = function clampValue(value, min, max) {
  return mathMax(mathMin(value, max), min);
}; // --- Private helper components ---
// @vue/component


var BVFormRatingStar = Vue.extend({
  name: NAME_FORM_RATING_STAR,
  mixins: [normalizeSlotMixin],
  props: {
    rating: {
      type: Number,
      default: 0
    },
    star: {
      type: Number,
      default: 0
    },
    focused: {
      // If parent is focused
      type: Boolean,
      default: false
    },
    variant: {
      type: String // default: null

    },
    disabled: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    hasClear: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    onClick: function onClick(evt) {
      if (!this.disabled && !this.readonly) {
        stopEvent(evt, {
          propagation: false
        });
        this.$emit('selected', this.star);
      }
    }
  },
  render: function render(h) {
    var rating = this.rating,
        star = this.star,
        focused = this.focused,
        hasClear = this.hasClear,
        variant = this.variant,
        disabled = this.disabled,
        readonly = this.readonly;
    var minStar = hasClear ? 0 : 1;
    var type = rating >= star ? 'full' : rating >= star - 0.5 ? 'half' : 'empty';
    var slotScope = {
      variant: variant,
      disabled: disabled,
      readonly: readonly
    };
    return h('span', {
      staticClass: 'b-rating-star',
      class: {
        // When not hovered, we use this class to focus the current (or first) star
        focused: focused && rating === star || !toInteger(rating) && star === minStar,
        // We add type classes to we can handle RTL styling
        'b-rating-star-empty': type === 'empty',
        'b-rating-star-half': type === 'half',
        'b-rating-star-full': type === 'full'
      },
      attrs: {
        tabindex: !disabled && !readonly ? '-1' : null
      },
      on: {
        click: this.onClick
      }
    }, [h('span', {
      staticClass: 'b-rating-icon'
    }, [this.normalizeSlot(type, slotScope)])]);
  }
}); // --- Main component ---
// @vue/component

export var BFormRating = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_RATING,
  components: {
    BIconStar: BIconStar,
    BIconStarHalf: BIconStarHalf,
    BIconStarFill: BIconStarFill,
    BIconX: BIconX
  },
  mixins: [idMixin, formSizeMixin],
  model: {
    prop: 'value',
    event: 'change'
  },
  props: makePropsConfigurable(_objectSpread(_objectSpread(_objectSpread({}, omit(formControlProps, ['required', 'autofocus'])), formSizeProps), {}, {
    value: {
      type: [Number, String],
      default: null
    },
    stars: {
      type: [Number, String],
      default: DEFAULT_STARS,
      validator: function validator(value) {
        return toInteger(value) >= MIN_STARS;
      }
    },
    variant: {
      type: String // default: undefined

    },
    color: {
      // CSS color string (overrides variant)
      type: String // default: undefined

    },
    showValue: {
      type: Boolean,
      default: false
    },
    showValueMax: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    noBorder: {
      type: Boolean,
      default: false
    },
    inline: {
      type: Boolean,
      default: false
    },
    precision: {
      type: [Number, String],
      default: null
    },
    iconEmpty: {
      type: String,
      default: 'star'
    },
    iconHalf: {
      type: String,
      default: 'star-half'
    },
    iconFull: {
      type: String,
      default: 'star-fill'
    },
    iconClear: {
      type: String,
      default: 'x'
    },
    locale: {
      // Locale for the formatted value (if shown)
      // Defaults to the browser locale. Falls back to `en`
      type: [String, Array] // default: undefined

    },
    showClear: {
      type: Boolean,
      default: false
    }
  }), NAME_FORM_RATING),
  data: function data() {
    var value = toFloat(this.value, null);
    var stars = computeStars(this.stars);
    return {
      localValue: isNull(value) ? null : clampValue(value, 0, stars),
      hasFocus: false
    };
  },
  computed: {
    computedStars: function computedStars() {
      return computeStars(this.stars);
    },
    computedRating: function computedRating() {
      var value = toFloat(this.localValue, 0);
      var precision = toInteger(this.precision, 3); // We clamp the value between `0` and stars

      return clampValue(toFloat(value.toFixed(precision)), 0, this.computedStars);
    },
    computedLocale: function computedLocale() {
      var locales = concat(this.locale).filter(identity);
      var nf = new Intl.NumberFormat(locales);
      return nf.resolvedOptions().locale;
    },
    isInteractive: function isInteractive() {
      return !this.disabled && !this.readonly;
    },
    isRTL: function isRTL() {
      return isLocaleRTL(this.computedLocale);
    },
    formattedRating: function formattedRating() {
      var precision = toInteger(this.precision);
      var showValueMax = this.showValueMax;
      var locale = this.computedLocale;
      var formatOptions = {
        notation: 'standard',
        minimumFractionDigits: isNaN(precision) ? 0 : precision,
        maximumFractionDigits: isNaN(precision) ? 3 : precision
      };
      var stars = this.computedStars.toLocaleString(locale);
      var value = this.localValue;
      value = isNull(value) ? showValueMax ? '-' : '' : value.toLocaleString(locale, formatOptions);
      return showValueMax ? "".concat(value, "/").concat(stars) : value;
    }
  },
  watch: {
    value: function value(newVal, oldVal) {
      if (newVal !== oldVal) {
        var value = toFloat(newVal, null);
        this.localValue = isNull(value) ? null : clampValue(value, 0, this.computedStars);
      }
    },
    localValue: function localValue(newVal, oldVal) {
      if (newVal !== oldVal && newVal !== (this.value || 0)) {
        this.$emit('change', newVal || null);
      }
    },
    disabled: function disabled(newVal) {
      if (newVal) {
        this.hasFocus = false;
        this.blur();
      }
    }
  },
  methods: {
    // --- Public methods ---
    focus: function focus() {
      if (!this.disabled) {
        attemptFocus(this.$el);
      }
    },
    blur: function blur() {
      if (!this.disabled) {
        attemptBlur(this.$el);
      }
    },
    // --- Private methods ---
    onKeydown: function onKeydown(evt) {
      var keyCode = evt.keyCode;

      if (this.isInteractive && arrayIncludes([CODE_LEFT, CODE_DOWN, CODE_RIGHT, CODE_UP], keyCode)) {
        stopEvent(evt, {
          propagation: false
        });
        var value = toInteger(this.localValue, 0);
        var min = this.showClear ? 0 : 1;
        var stars = this.computedStars; // In RTL mode, LEFT/RIGHT are swapped

        var amountRtl = this.isRTL ? -1 : 1;

        if (keyCode === CODE_LEFT) {
          this.localValue = clampValue(value - amountRtl, min, stars) || null;
        } else if (keyCode === CODE_RIGHT) {
          this.localValue = clampValue(value + amountRtl, min, stars);
        } else if (keyCode === CODE_DOWN) {
          this.localValue = clampValue(value - 1, min, stars) || null;
        } else if (keyCode === CODE_UP) {
          this.localValue = clampValue(value + 1, min, stars);
        }
      }
    },
    onSelected: function onSelected(value) {
      if (this.isInteractive) {
        this.localValue = value;
      }
    },
    onFocus: function onFocus(evt) {
      this.hasFocus = !this.isInteractive ? false : evt.type === 'focus';
    },
    // --- Render methods ---
    renderIcon: function renderIcon(icon) {
      return this.$createElement(BIcon, {
        props: {
          icon: icon,
          variant: this.disabled || this.color ? null : this.variant || null
        }
      });
    },
    iconEmptyFn: function iconEmptyFn() {
      return this.renderIcon(this.iconEmpty);
    },
    iconHalfFn: function iconHalfFn() {
      return this.renderIcon(this.iconHalf);
    },
    iconFullFn: function iconFullFn() {
      return this.renderIcon(this.iconFull);
    },
    iconClearFn: function iconClearFn() {
      return this.$createElement(BIcon, {
        props: {
          icon: this.iconClear
        }
      });
    }
  },
  render: function render(h) {
    var _this = this;

    var disabled = this.disabled,
        readonly = this.readonly,
        name = this.name,
        form = this.form,
        inline = this.inline,
        variant = this.variant,
        color = this.color,
        noBorder = this.noBorder,
        hasFocus = this.hasFocus,
        computedRating = this.computedRating,
        computedStars = this.computedStars,
        formattedRating = this.formattedRating,
        showClear = this.showClear,
        isRTL = this.isRTL,
        isInteractive = this.isInteractive,
        $scopedSlots = this.$scopedSlots;
    var $content = [];

    if (showClear && !disabled && !readonly) {
      var $icon = h('span', {
        staticClass: 'b-rating-icon'
      }, [($scopedSlots['icon-clear'] || this.iconClearFn)()]);
      $content.push(h('span', {
        staticClass: 'b-rating-star b-rating-star-clear flex-grow-1',
        class: {
          focused: hasFocus && computedRating === 0
        },
        attrs: {
          tabindex: isInteractive ? '-1' : null
        },
        on: {
          click: function click() {
            return _this.onSelected(null);
          }
        },
        key: 'clear'
      }, [$icon]));
    }

    for (var index = 0; index < computedStars; index++) {
      var value = index + 1;
      $content.push(h(BVFormRatingStar, {
        staticClass: 'flex-grow-1',
        style: color && !disabled ? {
          color: color
        } : {},
        props: {
          rating: computedRating,
          star: value,
          variant: disabled ? null : variant || null,
          disabled: disabled,
          readonly: readonly,
          focused: hasFocus,
          hasClear: showClear
        },
        on: {
          selected: this.onSelected
        },
        scopedSlots: {
          empty: $scopedSlots['icon-empty'] || this.iconEmptyFn,
          half: $scopedSlots['icon-half'] || this.iconHalfFn,
          full: $scopedSlots['icon-full'] || this.iconFullFn
        },
        key: index
      }));
    }

    if (name) {
      $content.push(h('input', {
        attrs: {
          type: 'hidden',
          value: isNull(this.localValue) ? '' : computedRating,
          name: name,
          form: form || null
        },
        key: 'hidden'
      }));
    }

    if (this.showValue) {
      $content.push(h('b', {
        staticClass: 'b-rating-value flex-grow-1',
        attrs: {
          'aria-hidden': 'true'
        },
        key: 'value'
      }, toString(formattedRating)));
    }

    return h('output', {
      staticClass: 'b-rating form-control align-items-center',
      class: [{
        'd-inline-flex': inline,
        'd-flex': !inline,
        'border-0': noBorder,
        disabled: disabled,
        readonly: !disabled && readonly
      }, this.sizeFormClass],
      attrs: {
        id: this.safeId(),
        dir: isRTL ? 'rtl' : 'ltr',
        tabindex: disabled ? null : '0',
        disabled: disabled,
        role: 'slider',
        'aria-disabled': disabled ? 'true' : null,
        'aria-readonly': !disabled && readonly ? 'true' : null,
        'aria-live': 'off',
        'aria-valuemin': showClear ? '0' : '1',
        'aria-valuemax': toString(computedStars),
        'aria-valuenow': computedRating ? toString(computedRating) : null
      },
      on: {
        keydown: this.onKeydown,
        focus: this.onFocus,
        blur: this.onFocus
      }
    }, $content);
  }
});