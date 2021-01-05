function _toConsumableArray(arr) { return _arrayWithoutHoles(arr) || _iterableToArray(arr) || _unsupportedIterableToArray(arr) || _nonIterableSpread(); }

function _nonIterableSpread() { throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); }

function _unsupportedIterableToArray(o, minLen) { if (!o) return; if (typeof o === "string") return _arrayLikeToArray(o, minLen); var n = Object.prototype.toString.call(o).slice(8, -1); if (n === "Object" && o.constructor) n = o.constructor.name; if (n === "Map" || n === "Set") return Array.from(o); if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen); }

function _iterableToArray(iter) { if (typeof Symbol !== "undefined" && Symbol.iterator in Object(iter)) return Array.from(iter); }

function _arrayWithoutHoles(arr) { if (Array.isArray(arr)) return _arrayLikeToArray(arr); }

function _arrayLikeToArray(arr, len) { if (len == null || len > arr.length) len = arr.length; for (var i = 0, arr2 = new Array(len); i < len; i++) { arr2[i] = arr[i]; } return arr2; }

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

// Tagged input form control
// Based loosely on https://adamwathan.me/renderless-components-in-vuejs/
import Vue from '../../vue';
import { NAME_FORM_TAGS } from '../../constants/components';
import { CODE_BACKSPACE, CODE_DELETE, CODE_ENTER } from '../../constants/key-codes';
import { EVENT_OPTIONS_PASSIVE } from '../../constants/events';
import { SLOT_NAME_DEFAULT } from '../../constants/slot-names';
import { RX_SPACES } from '../../constants/regex';
import cssEscape from '../../utils/css-escape';
import identity from '../../utils/identity';
import looseEqual from '../../utils/loose-equal';
import { arrayIncludes, concat } from '../../utils/array';
import { makePropsConfigurable } from '../../utils/config';
import { attemptBlur, attemptFocus, closest, isActiveElement, matches, requestAF, select } from '../../utils/dom';
import { eventOn, eventOff, stopEvent } from '../../utils/events';
import { pick } from '../../utils/object';
import { isEvent, isNumber, isString } from '../../utils/inspect';
import { escapeRegExp, toString, trim, trimLeft } from '../../utils/string';
import formControlMixin, { props as formControlProps } from '../../mixins/form-control';
import formSizeMixin, { props as formSizeProps } from '../../mixins/form-size';
import formStateMixin, { props as formStateProps } from '../../mixins/form-state';
import idMixin from '../../mixins/id';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import { BButton } from '../button/button';
import { BFormInvalidFeedback } from '../form/form-invalid-feedback';
import { BFormText } from '../form/form-text';
import { BFormTag } from './form-tag'; // --- Constants ---
// Supported input types (for built in input)

var TYPES = ['text', 'email', 'tel', 'url', 'number']; // --- Utility methods ---
// Escape special chars in string and replace
// contiguous spaces with a whitespace match

var escapeRegExpChars = function escapeRegExpChars(str) {
  return escapeRegExp(str).replace(RX_SPACES, '\\s');
}; // Remove leading/trailing spaces from array of tags and remove duplicates


var cleanTags = function cleanTags(tags) {
  return concat(tags).map(function (tag) {
    return trim(toString(tag));
  }).filter(function (tag, index, arr) {
    return tag.length > 0 && arr.indexOf(tag) === index;
  });
}; // Processes an input/change event, normalizing string or event argument


var processEventValue = function processEventValue(evt) {
  return isString(evt) ? evt : isEvent(evt) ? evt.target.value || '' : '';
}; // Returns a fresh empty `tagsState` object


var cleanTagsState = function cleanTagsState() {
  return {
    all: [],
    valid: [],
    invalid: [],
    duplicate: []
  };
}; // --- Props ---


var props = makePropsConfigurable(_objectSpread(_objectSpread(_objectSpread(_objectSpread({}, formControlProps), formSizeProps), formStateProps), {}, {
  value: {
    // The v-model prop
    type: Array,
    default: function _default() {
      return [];
    }
  },
  placeholder: {
    type: String,
    default: 'Add tag...'
  },
  inputId: {
    type: String // default: null

  },
  inputType: {
    type: String,
    default: 'text',
    validator: function validator(value) {
      return arrayIncludes(TYPES, value);
    }
  },
  inputClass: {
    type: [String, Array, Object] // default: null

  },
  inputAttrs: {
    // Additional attributes to add to the input element
    type: Object,
    default: function _default() {
      return {};
    }
  },
  addButtonText: {
    type: String,
    default: 'Add'
  },
  addButtonVariant: {
    type: String,
    default: 'outline-secondary'
  },
  tagVariant: {
    type: String,
    default: 'secondary'
  },
  tagClass: {
    type: [String, Array, Object] // default: null

  },
  tagPills: {
    type: Boolean,
    default: false
  },
  tagRemoveLabel: {
    type: String,
    default: 'Remove tag'
  },
  tagRemovedLabel: {
    type: String,
    default: 'Tag removed'
  },
  tagValidator: {
    type: Function // default: null

  },
  duplicateTagText: {
    type: String,
    default: 'Duplicate tag(s)'
  },
  invalidTagText: {
    type: String,
    default: 'Invalid tag(s)'
  },
  limitTagsText: {
    type: String,
    default: 'Tag limit reached'
  },
  limit: {
    type: Number // default: null

  },
  separator: {
    // Character (or characters) that trigger adding tags
    type: [String, Array] // default: null

  },
  removeOnDelete: {
    // Enable deleting last tag in list when CODE_BACKSPACE is
    // pressed and input is empty
    type: Boolean,
    default: false
  },
  addOnChange: {
    // Enable change event triggering tag addition
    // Handy if using <select> as the input
    type: Boolean,
    default: false
  },
  noAddOnEnter: {
    // Disable ENTER key from triggering tag addition
    type: Boolean,
    default: false
  },
  noOuterFocus: {
    // Disable the focus ring on the root element
    type: Boolean,
    default: false
  },
  ignoreInputFocusSelector: {
    // Disable the input focus behavior when clicking
    // on element matching the selector (or selectors)
    type: [Array, String],
    default: function _default() {
      return ['.b-form-tag', 'button', 'input', 'select'];
    }
  }
}), NAME_FORM_TAGS); // --- Main component ---
// @vue/component

export var BFormTags = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_TAGS,
  mixins: [idMixin, formControlMixin, formSizeMixin, formStateMixin, normalizeSlotMixin],
  model: {
    // Even though this is the default that Vue assumes, we need
    // to add it for the docs to reflect that this is the model
    prop: 'value',
    event: 'input'
  },
  props: props,
  data: function data() {
    return {
      hasFocus: false,
      newTag: '',
      tags: [],
      // Tags that were removed
      removedTags: [],
      // Populated when tags are parsed
      tagsState: cleanTagsState()
    };
  },
  computed: {
    computedInputId: function computedInputId() {
      return this.inputId || this.safeId('__input__');
    },
    computedInputType: function computedInputType() {
      // We only allow certain types
      return arrayIncludes(TYPES, this.inputType) ? this.inputType : 'text';
    },
    computedInputAttrs: function computedInputAttrs() {
      var disabled = this.disabled,
          form = this.form;
      return _objectSpread(_objectSpread({}, this.inputAttrs), {}, {
        // Must have attributes
        id: this.computedInputId,
        value: this.newTag,
        disabled: disabled,
        form: form
      });
    },
    computedInputHandlers: function computedInputHandlers() {
      return {
        input: this.onInputInput,
        change: this.onInputChange,
        keydown: this.onInputKeydown,
        reset: this.reset
      };
    },
    computedSeparator: function computedSeparator() {
      // Merge the array into a string
      return concat(this.separator).filter(isString).filter(identity).join('');
    },
    computedSeparatorRegExp: function computedSeparatorRegExp() {
      // We use a computed prop here to precompile the RegExp
      // The RegExp is a character class RE in the form of `/[abc]+/`
      // where a, b, and c are the valid separator characters
      // -> `tags = str.split(/[abc]+/).filter(t => t)`
      var separator = this.computedSeparator;
      return separator ? new RegExp("[".concat(escapeRegExpChars(separator), "]+")) : null;
    },
    computedJoiner: function computedJoiner() {
      // When tag(s) are invalid or duplicate, we leave them
      // in the input so that the user can see them
      // If there are more than one tag in the input, we use the
      // first separator character as the separator in the input
      // We append a space if the first separator is not a space
      var joiner = this.computedSeparator.charAt(0);
      return joiner !== ' ' ? "".concat(joiner, " ") : joiner;
    },
    computeIgnoreInputFocusSelector: function computeIgnoreInputFocusSelector() {
      // Normalize to an single selector with selectors separated by `,`
      return concat(this.ignoreInputFocusSelector).filter(identity).join(',').trim();
    },
    disableAddButton: function disableAddButton() {
      var _this = this;

      // If 'Add' button should be disabled
      // If the input contains at least one tag that can
      // be added, then the 'Add' button should be enabled
      var newTag = trim(this.newTag);
      return newTag === '' || !this.splitTags(newTag).some(function (t) {
        return !arrayIncludes(_this.tags, t) && _this.validateTag(t);
      });
    },
    duplicateTags: function duplicateTags() {
      return this.tagsState.duplicate;
    },
    hasDuplicateTags: function hasDuplicateTags() {
      return this.duplicateTags.length > 0;
    },
    invalidTags: function invalidTags() {
      return this.tagsState.invalid;
    },
    hasInvalidTags: function hasInvalidTags() {
      return this.invalidTags.length > 0;
    },
    isLimitReached: function isLimitReached() {
      var limit = this.limit;
      return isNumber(limit) && limit >= 0 && this.tags.length >= limit;
    }
  },
  watch: {
    value: function value(newVal) {
      this.tags = cleanTags(newVal);
    },
    tags: function tags(newVal, oldVal) {
      // Update the `v-model` (if it differs from the value prop)
      if (!looseEqual(newVal, this.value)) {
        this.$emit('input', newVal);
      }

      if (!looseEqual(newVal, oldVal)) {
        newVal = concat(newVal).filter(identity);
        oldVal = concat(oldVal).filter(identity);
        this.removedTags = oldVal.filter(function (old) {
          return !arrayIncludes(newVal, old);
        });
      }
    },
    tagsState: function tagsState(newVal, oldVal) {
      // Emit a tag-state event when the `tagsState` object changes
      if (!looseEqual(newVal, oldVal)) {
        this.$emit('tag-state', newVal.valid, newVal.invalid, newVal.duplicate);
      }
    }
  },
  created: function created() {
    // We do this in created to make sure an input event emits
    // if the cleaned tags are not equal to the value prop
    this.tags = cleanTags(this.value);
  },
  mounted: function mounted() {
    var _this2 = this;

    // Listen for form reset events, to reset the tags input
    var $form = closest('form', this.$el);

    if ($form) {
      eventOn($form, 'reset', this.reset, EVENT_OPTIONS_PASSIVE);
      this.$on('hook:beforeDestroy', function () {
        eventOff($form, 'reset', _this2.reset, EVENT_OPTIONS_PASSIVE);
      });
    }
  },
  methods: {
    addTag: function addTag(newTag) {
      newTag = isString(newTag) ? newTag : this.newTag;
      /* istanbul ignore next */

      if (this.disabled || trim(newTag) === '' || this.isLimitReached) {
        // Early exit
        return;
      }

      var parsed = this.parseTags(newTag); // Add any new tags to the `tags` array, or if the
      // array of `allTags` is empty, we clear the input

      if (parsed.valid.length > 0 || parsed.all.length === 0) {
        // Clear the user input element (and leave in any invalid/duplicate tag(s)

        /* istanbul ignore if: full testing to be added later */
        if (matches(this.getInput(), 'select')) {
          // The following is needed to properly
          // work with `<select>` elements
          this.newTag = '';
        } else {
          var invalidAndDuplicates = [].concat(_toConsumableArray(parsed.invalid), _toConsumableArray(parsed.duplicate));
          this.newTag = parsed.all.filter(function (tag) {
            return arrayIncludes(invalidAndDuplicates, tag);
          }).join(this.computedJoiner).concat(invalidAndDuplicates.length > 0 ? this.computedJoiner.charAt(0) : '');
        }
      }

      if (parsed.valid.length > 0) {
        // We add the new tags in one atomic operation
        // to trigger reactivity once (instead of once per tag)
        // We do this after we update the new tag input value
        // `concat()` can be faster than array spread, when both args are arrays
        this.tags = concat(this.tags, parsed.valid);
      }

      this.tagsState = parsed; // Attempt to re-focus the input (specifically for when using the Add
      // button, as the button disappears after successfully adding a tag

      this.focus();
    },
    removeTag: function removeTag(tag) {
      var _this3 = this;

      /* istanbul ignore next */
      if (this.disabled) {
        return;
      } // TODO:
      //   Add `onRemoveTag(tag)` user method, which if returns `false`
      //   will prevent the tag from being removed (i.e. confirmation)
      //   Or emit cancelable `BvEvent`


      this.tags = this.tags.filter(function (t) {
        return t !== tag;
      }); // Return focus to the input (if possible)

      this.$nextTick(function () {
        _this3.focus();
      });
    },
    reset: function reset() {
      var _this4 = this;

      this.newTag = '';
      this.tags = [];
      this.$nextTick(function () {
        _this4.removedTags = [];
        _this4.tagsState = cleanTagsState();
      });
    },
    // --- Input element event handlers ---
    onInputInput: function onInputInput(evt) {
      /* istanbul ignore next: hard to test composition events */
      if (this.disabled || isEvent(evt) && evt.target.composing) {
        // `evt.target.composing` is set by Vue (`v-model` directive)
        // https://github.com/vuejs/vue/blob/dev/src/platforms/web/runtime/directives/model.js
        return;
      }

      var newTag = processEventValue(evt);
      var separatorRe = this.computedSeparatorRegExp;

      if (this.newTag !== newTag) {
        this.newTag = newTag;
      } // We ignore leading whitespace for the following


      newTag = trimLeft(newTag);

      if (separatorRe && separatorRe.test(newTag.slice(-1))) {
        // A trailing separator character was entered, so add the tag(s)
        // Note: More than one tag on input event is possible via copy/paste
        this.addTag();
      } else {
        // Validate (parse tags) on input event
        this.tagsState = newTag === '' ? cleanTagsState() : this.parseTags(newTag);
      }
    },
    onInputChange: function onInputChange(evt) {
      // Change is triggered on `<input>` blur, or `<select>` selected
      // This event is opt-in
      if (!this.disabled && this.addOnChange) {
        var newTag = processEventValue(evt);
        /* istanbul ignore next */

        if (this.newTag !== newTag) {
          this.newTag = newTag;
        }

        this.addTag();
      }
    },
    onInputKeydown: function onInputKeydown(evt) {
      // Early exit

      /* istanbul ignore next */
      if (this.disabled || !isEvent(evt)) {
        return;
      }

      var keyCode = evt.keyCode;
      var value = evt.target.value || '';
      /* istanbul ignore else: testing to be added later */

      if (!this.noAddOnEnter && keyCode === CODE_ENTER) {
        // Attempt to add the tag when user presses enter
        stopEvent(evt, {
          propagation: false
        });
        this.addTag();
      } else if (this.removeOnDelete && (keyCode === CODE_BACKSPACE || keyCode === CODE_DELETE) && value === '') {
        // Remove the last tag if the user pressed backspace/delete and the input is empty
        stopEvent(evt, {
          propagation: false
        });
        this.tags = this.tags.slice(0, -1);
      }
    },
    // --- Wrapper event handlers ---
    onClick: function onClick(evt) {
      var _this5 = this;

      var ignoreFocusSelector = this.computeIgnoreInputFocusSelector;
      var target = evt.target;

      if (!this.disabled && !isActiveElement(target) && (!ignoreFocusSelector || !closest(ignoreFocusSelector, target, true))) {
        this.$nextTick(function () {
          _this5.focus();
        });
      }
    },
    onFocusin: function onFocusin() {
      this.hasFocus = true;
    },
    onFocusout: function onFocusout() {
      this.hasFocus = false;
    },
    handleAutofocus: function handleAutofocus() {
      var _this6 = this;

      this.$nextTick(function () {
        requestAF(function () {
          if (_this6.autofocus && !_this6.disabled) {
            _this6.focus();
          }
        });
      });
    },
    // --- Public methods ---
    focus: function focus() {
      if (!this.disabled) {
        attemptFocus(this.getInput());
      }
    },
    blur: function blur() {
      if (!this.disabled) {
        attemptBlur(this.getInput());
      }
    },
    // --- Private methods ---
    splitTags: function splitTags(newTag) {
      // Split the input into an array of raw tags
      newTag = toString(newTag);
      var separatorRe = this.computedSeparatorRegExp; // Split the tag(s) via the optional separator
      // Normally only a single tag is provided, but copy/paste
      // can enter multiple tags in a single operation

      return (separatorRe ? newTag.split(separatorRe) : [newTag]).map(trim).filter(identity);
    },
    parseTags: function parseTags(newTag) {
      var _this7 = this;

      // Takes `newTag` value and parses it into `validTags`,
      // `invalidTags`, and duplicate tags as an object
      // Split the input into raw tags
      var tags = this.splitTags(newTag); // Base results

      var parsed = {
        all: tags,
        valid: [],
        invalid: [],
        duplicate: []
      }; // Parse the unique tags

      tags.forEach(function (tag) {
        if (arrayIncludes(_this7.tags, tag) || arrayIncludes(parsed.valid, tag)) {
          // Unique duplicate tags
          if (!arrayIncludes(parsed.duplicate, tag)) {
            parsed.duplicate.push(tag);
          }
        } else if (_this7.validateTag(tag)) {
          // We only add unique/valid tags
          parsed.valid.push(tag);
        } else {
          // Unique invalid tags
          if (!arrayIncludes(parsed.invalid, tag)) {
            parsed.invalid.push(tag);
          }
        }
      });
      return parsed;
    },
    validateTag: function validateTag(tag) {
      var tagValidator = this.tagValidator;
      return tagValidator.name !== props.tagValidator.default.name ? tagValidator(tag) : true;
    },
    getInput: function getInput() {
      // Returns the input element reference (or null if not found)
      // We need to escape `computedInputId` since it can be user-provided
      return select("#".concat(cssEscape(this.computedInputId)), this.$el);
    },
    // Default User Interface render
    defaultRender: function defaultRender(_ref) {
      var tags = _ref.tags,
          inputAttrs = _ref.inputAttrs,
          inputType = _ref.inputType,
          inputHandlers = _ref.inputHandlers,
          removeTag = _ref.removeTag,
          addTag = _ref.addTag,
          isInvalid = _ref.isInvalid,
          isDuplicate = _ref.isDuplicate,
          isLimitReached = _ref.isLimitReached,
          disableAddButton = _ref.disableAddButton,
          disabled = _ref.disabled,
          placeholder = _ref.placeholder,
          inputClass = _ref.inputClass,
          tagRemoveLabel = _ref.tagRemoveLabel,
          tagVariant = _ref.tagVariant,
          tagPills = _ref.tagPills,
          tagClass = _ref.tagClass,
          addButtonText = _ref.addButtonText,
          addButtonVariant = _ref.addButtonVariant,
          invalidTagText = _ref.invalidTagText,
          duplicateTagText = _ref.duplicateTagText,
          limitTagsText = _ref.limitTagsText;
      var h = this.$createElement; // Make the list of tags

      var $tags = tags.map(function (tag) {
        tag = toString(tag);
        return h(BFormTag, {
          class: tagClass,
          props: {
            // `BFormTag` will auto generate an ID
            // so we do not need to set the ID prop
            tag: 'li',
            title: tag,
            disabled: disabled,
            variant: tagVariant,
            pill: tagPills,
            removeLabel: tagRemoveLabel
          },
          on: {
            remove: function remove() {
              return removeTag(tag);
            }
          },
          key: "tags_".concat(tag)
        }, tag);
      }); // Feedback IDs if needed

      var invalidFeedbackId = invalidTagText && isInvalid ? this.safeId('__invalid_feedback__') : null;
      var duplicateFeedbackId = duplicateTagText && isDuplicate ? this.safeId('__duplicate_feedback__') : null;
      var limitFeedbackId = limitTagsText && isLimitReached ? this.safeId('__limit_feedback__') : null; // Compute the `aria-describedby` attribute value

      var ariaDescribedby = [inputAttrs['aria-describedby'], invalidFeedbackId, duplicateFeedbackId, limitFeedbackId].filter(identity).join(' '); // Input

      var $input = h('input', {
        ref: 'input',
        // Directive needed to get `evt.target.composing` set (if needed)
        directives: [{
          name: 'model',
          value: inputAttrs.value
        }],
        staticClass: 'b-form-tags-input w-100 flex-grow-1 p-0 m-0 bg-transparent border-0',
        class: inputClass,
        style: {
          outline: 0,
          minWidth: '5rem'
        },
        attrs: _objectSpread(_objectSpread({}, inputAttrs), {}, {
          'aria-describedby': ariaDescribedby || null,
          type: inputType,
          placeholder: placeholder || null
        }),
        domProps: {
          value: inputAttrs.value
        },
        on: inputHandlers
      }); // Add button

      var $button = h(BButton, {
        ref: 'button',
        staticClass: 'b-form-tags-button py-0',
        class: {
          // Only show the button if the tag can be added
          // We use the `invisible` class instead of not rendering
          // the button, so that we maintain layout to prevent
          // the user input from jumping around
          invisible: disableAddButton
        },
        style: {
          fontSize: '90%'
        },
        props: {
          variant: addButtonVariant,
          disabled: disableAddButton || isLimitReached
        },
        on: {
          click: function click() {
            return addTag();
          }
        }
      }, [this.normalizeSlot('add-button-text') || addButtonText]); // ID of the tags + input `<ul>` list
      // Note we could concatenate `inputAttrs.id` with '__tag_list__'
      // but `inputId` may be `null` until after mount
      // `safeId()` returns `null`, if no user provided ID,
      // until after mount when a unique ID is generated

      var tagListId = this.safeId('__tag_list__');
      var $field = h('li', {
        staticClass: 'b-from-tags-field flex-grow-1',
        attrs: {
          role: 'none',
          'aria-live': 'off',
          'aria-controls': tagListId
        },
        key: 'tags_field'
      }, [h('div', {
        staticClass: 'd-flex',
        attrs: {
          role: 'group'
        }
      }, [$input, $button])]); // Wrap in an unordered list element (we use a list for accessibility)

      var $ul = h('ul', {
        staticClass: 'b-form-tags-list list-unstyled mb-0 d-flex flex-wrap align-items-center',
        attrs: {
          id: tagListId
        },
        key: 'tags_list'
      }, [$tags, $field]); // Assemble the feedback

      var $feedback = h();

      if (invalidTagText || duplicateTagText || limitTagsText) {
        // Add an aria live region for the invalid/duplicate tag
        // messages if the user has not disabled the messages
        var joiner = this.computedJoiner; // Invalid tag feedback if needed (error)

        var $invalid = h();

        if (invalidFeedbackId) {
          $invalid = h(BFormInvalidFeedback, {
            props: {
              id: invalidFeedbackId,
              forceShow: true
            },
            key: 'tags_invalid_feedback'
          }, [this.invalidTagText, ': ', this.invalidTags.join(joiner)]);
        } // Duplicate tag feedback if needed (warning, not error)


        var $duplicate = h();

        if (duplicateFeedbackId) {
          $duplicate = h(BFormText, {
            props: {
              id: duplicateFeedbackId
            },
            key: 'tags_duplicate_feedback'
          }, [this.duplicateTagText, ': ', this.duplicateTags.join(joiner)]);
        } // Limit tags feedback if needed (warning, not error)


        var $limit = h();

        if (limitFeedbackId) {
          $limit = h(BFormText, {
            props: {
              id: limitFeedbackId
            },
            key: 'tags_limit_feedback'
          }, [limitTagsText]);
        }

        $feedback = h('div', {
          attrs: {
            'aria-live': 'polite',
            'aria-atomic': 'true'
          },
          key: 'tags_feedback'
        }, [$invalid, $duplicate, $limit]);
      } // Return the content


      return [$ul, $feedback];
    }
  },
  render: function render(h) {
    var name = this.name,
        disabled = this.disabled,
        required = this.required,
        form = this.form,
        tags = this.tags,
        computedInputId = this.computedInputId,
        hasFocus = this.hasFocus,
        noOuterFocus = this.noOuterFocus; // Scoped slot properties

    var scope = _objectSpread({
      // Array of tags (shallow copy to prevent mutations)
      tags: tags.slice(),
      // <input> v-bind:inputAttrs
      inputAttrs: this.computedInputAttrs,
      // We don't include this in the attrs, as users may want to override this
      inputType: this.computedInputType,
      // <input> v-on:inputHandlers
      inputHandlers: this.computedInputHandlers,
      // Methods
      removeTag: this.removeTag,
      addTag: this.addTag,
      reset: this.reset,
      // <input> :id="inputId"
      inputId: computedInputId,
      // Invalid/Duplicate state information
      isInvalid: this.hasInvalidTags,
      invalidTags: this.invalidTags.slice(),
      isDuplicate: this.hasDuplicateTags,
      duplicateTags: this.duplicateTags.slice(),
      isLimitReached: this.isLimitReached,
      // If the 'Add' button should be disabled
      disableAddButton: this.disableAddButton
    }, pick(this.$props, ['disabled', 'required', 'form', 'state', 'size', 'limit', 'separator', 'placeholder', 'inputClass', 'tagRemoveLabel', 'tagVariant', 'tagPills', 'tagClass', 'addButtonText', 'addButtonVariant', 'invalidTagText', 'duplicateTagText', 'limitTagsText'])); // Generate the user interface


    var $content = this.normalizeSlot(SLOT_NAME_DEFAULT, scope) || this.defaultRender(scope); // Generate the `aria-live` region for the current value(s)

    var $output = h('output', {
      staticClass: 'sr-only',
      attrs: {
        id: this.safeId('__selected_tags__'),
        role: 'status',
        for: computedInputId,
        'aria-live': hasFocus ? 'polite' : 'off',
        'aria-atomic': 'true',
        'aria-relevant': 'additions text'
      }
    }, this.tags.join(', ')); // Removed tag live region

    var $removed = h('div', {
      staticClass: 'sr-only',
      attrs: {
        id: this.safeId('__removed_tags__'),
        role: 'status',
        'aria-live': hasFocus ? 'assertive' : 'off',
        'aria-atomic': 'true'
      }
    }, this.removedTags.length > 0 ? "(".concat(this.tagRemovedLabel, ") ").concat(this.removedTags.join(', ')) : ''); // Add hidden inputs for form submission

    var $hidden = h();

    if (name && !disabled) {
      // We add hidden inputs for each tag if a name is provided
      // When there are currently no tags, a visually hidden input
      // with empty value is rendered for proper required handling
      var hasTags = tags.length > 0;
      $hidden = (hasTags ? tags : ['']).map(function (tag) {
        return h('input', {
          class: {
            'sr-only': !hasTags
          },
          attrs: {
            type: hasTags ? 'hidden' : 'text',
            value: tag,
            required: required,
            name: name,
            form: form
          },
          key: "tag_input_".concat(tag)
        });
      });
    } // Return the rendered output


    return h('div', {
      staticClass: 'b-form-tags form-control h-auto',
      class: [{
        focus: hasFocus && !noOuterFocus && !disabled,
        disabled: disabled
      }, this.sizeFormClass, this.stateClass],
      attrs: {
        id: this.safeId(),
        role: 'group',
        tabindex: disabled || noOuterFocus ? null : '-1',
        'aria-describedby': this.safeId('__selected_tags__')
      },
      on: {
        click: this.onClick,
        focusin: this.onFocusin,
        focusout: this.onFocusout
      }
    }, [$output, $removed, $content, $hidden]);
  }
});