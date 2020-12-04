function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_FORM_FILE } from '../../constants/components';
import { EVENT_OPTIONS_PASSIVE } from '../../constants/events';
import { RX_EXTENSION, RX_STAR } from '../../constants/regex';
import cloneDeep from '../../utils/clone-deep';
import identity from '../../utils/identity';
import looseEqual from '../../utils/loose-equal';
import { from as arrayFrom, flatten, flattenDeep } from '../../utils/array';
import { makePropsConfigurable } from '../../utils/config';
import { closest } from '../../utils/dom';
import { hasPromiseSupport } from '../../utils/env';
import { eventOn, eventOff, stopEvent } from '../../utils/events';
import { isArray, isFile, isFunction, isNull, isUndefinedOrNull } from '../../utils/inspect';
import { File } from '../../utils/safe-types';
import { escapeRegExp } from '../../utils/string';
import { warn } from '../../utils/warn';
import attrsMixin from '../../mixins/attrs';
import formControlMixin, { props as formControlProps } from '../../mixins/form-control';
import formCustomMixin, { props as formCustomProps } from '../../mixins/form-custom';
import formStateMixin, { props as formStateProps } from '../../mixins/form-state';
import idMixin from '../../mixins/id';
import normalizeSlotMixin from '../../mixins/normalize-slot';
import { props as formSizeProps } from '../../mixins/form-size'; // --- Constants ---

var VALUE_EMPTY_DEPRECATED_MSG = 'Setting "value"/"v-model" to an empty string for reset is deprecated. Set to "null" instead.'; // --- Helper methods ---

var isValidValue = function isValidValue(value) {
  return isFile(value) || isArray(value) && value.every(function (v) {
    return isValidValue(v);
  });
}; // Helper method to "safely" get the entry from a data-transfer item

/* istanbul ignore next: not supported in JSDOM */


var getDataTransferItemEntry = function getDataTransferItemEntry(item) {
  return isFunction(item.getAsEntry) ? item.getAsEntry() : isFunction(item.webkitGetAsEntry) ? item.webkitGetAsEntry() : null;
}; // Drop handler function to get all files

/* istanbul ignore next: not supported in JSDOM */


var getAllFileEntries = function getAllFileEntries(dataTransferItemList) {
  var traverseDirectories = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : true;
  return Promise.all(arrayFrom(dataTransferItemList).filter(function (item) {
    return item.kind === 'file';
  }).map(function (item) {
    var entry = getDataTransferItemEntry(item);

    if (entry) {
      if (entry.isDirectory && traverseDirectories) {
        return getAllFileEntriesInDirectory(entry.createReader(), "".concat(entry.name, "/"));
      } else if (entry.isFile) {
        return new Promise(function (resolve) {
          entry.file(function (file) {
            file.$path = '';
            resolve(file);
          });
        });
      }
    }

    return null;
  }).filter(identity));
}; // Get all the file entries (recursive) in a directory

/* istanbul ignore next: not supported in JSDOM */


var getAllFileEntriesInDirectory = function getAllFileEntriesInDirectory(directoryReader) {
  var path = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : '';
  return new Promise(function (resolve) {
    var entryPromises = [];

    var readDirectoryEntries = function readDirectoryEntries() {
      directoryReader.readEntries(function (entries) {
        if (entries.length === 0) {
          resolve(Promise.all(entryPromises).then(function (entries) {
            return flatten(entries);
          }));
        } else {
          entryPromises.push(Promise.all(entries.map(function (entry) {
            if (entry) {
              if (entry.isDirectory) {
                return getAllFileEntriesInDirectory(entry.createReader(), "".concat(path).concat(entry.name, "/"));
              } else if (entry.isFile) {
                return new Promise(function (resolve) {
                  entry.file(function (file) {
                    file.$path = "".concat(path).concat(file.name);
                    resolve(file);
                  });
                });
              }
            }

            return null;
          }).filter(identity)));
          readDirectoryEntries();
        }
      });
    };

    readDirectoryEntries();
  });
}; // --- Props ---


var props = makePropsConfigurable(_objectSpread(_objectSpread(_objectSpread(_objectSpread(_objectSpread({}, formControlProps), formCustomProps), formStateProps), formSizeProps), {}, {
  value: {
    type: [File, Array],
    default: null,
    validator: function validator(value) {
      /* istanbul ignore next */
      if (value === '') {
        warn(VALUE_EMPTY_DEPRECATED_MSG, NAME_FORM_FILE);
        return true;
      }

      return isUndefinedOrNull(value) || isValidValue(value);
    }
  },
  accept: {
    type: String,
    default: ''
  },
  // Instruct input to capture from camera
  capture: {
    type: Boolean,
    default: false
  },
  placeholder: {
    type: String,
    default: 'No file chosen'
  },
  browseText: {
    type: String,
    default: 'Browse'
  },
  dropPlaceholder: {
    type: String,
    default: 'Drop files here'
  },
  noDropPlaceholder: {
    type: String,
    default: 'Not allowed'
  },
  multiple: {
    type: Boolean,
    default: false
  },
  directory: {
    type: Boolean,
    default: false
  },
  // TODO:
  //   Should we deprecate this and only support flat file structures?
  //   Nested file structures are only supported when files are dropped
  //   A Chromium "bug" prevents `webkitEntries` from being populated
  //   on the file input's `change` event and is marked as "WontFix"
  //   Mozilla implemented the behavior the same way as Chromium
  //   See: https://bugs.chromium.org/p/chromium/issues/detail?id=138987
  //   See: https://bugzilla.mozilla.org/show_bug.cgi?id=1326031
  noTraverse: {
    type: Boolean,
    default: false
  },
  noDrop: {
    type: Boolean,
    default: false
  },
  fileNameFormatter: {
    type: Function // default: null

  }
}), NAME_FORM_FILE); // @vue/component

export var BFormFile = /*#__PURE__*/Vue.extend({
  name: NAME_FORM_FILE,
  mixins: [attrsMixin, idMixin, formControlMixin, formStateMixin, formCustomMixin, normalizeSlotMixin],
  inheritAttrs: false,
  model: {
    prop: 'value',
    event: 'input'
  },
  props: props,
  data: function data() {
    return {
      files: [],
      dragging: false,
      // IE 11 doesn't respect setting `evt.dataTransfer.dropEffect`,
      // so we handle it ourselves as well
      // https://stackoverflow.com/a/46915971/2744776
      dropAllowed: !this.noDrop,
      hasFocus: false
    };
  },
  computed: {
    // Convert `accept` to an array of `[{ RegExpr, isMime }, ...]`
    computedAccept: function computedAccept() {
      var accept = this.accept;
      accept = (accept || '').trim().split(/[,\s]+/).filter(Boolean); // Allow any file type/extension

      if (accept.length === 0) {
        return null;
      }

      return accept.map(function (extOrType) {
        var prop = 'name';
        var startMatch = '^';
        var endMatch = '$';

        if (RX_EXTENSION.test(extOrType)) {
          // File extension /\.ext$/
          startMatch = '';
        } else {
          // MIME type /^mime\/.+$/ or /^mime\/type$/
          prop = 'type';

          if (RX_STAR.test(extOrType)) {
            endMatch = '.+$'; // Remove trailing `*`

            extOrType = extOrType.slice(0, -1);
          }
        } // Escape all RegExp special chars


        extOrType = escapeRegExp(extOrType);
        var rx = new RegExp("".concat(startMatch).concat(extOrType).concat(endMatch));
        return {
          rx: rx,
          prop: prop
        };
      });
    },
    computedCapture: function computedCapture() {
      var capture = this.capture;
      return capture === true || capture === '' ? true : capture || null;
    },
    computedAttrs: function computedAttrs() {
      var name = this.name,
          disabled = this.disabled,
          required = this.required,
          form = this.form,
          computedCapture = this.computedCapture,
          accept = this.accept,
          multiple = this.multiple,
          directory = this.directory;
      return _objectSpread(_objectSpread({}, this.bvAttrs), {}, {
        type: 'file',
        id: this.safeId(),
        name: name,
        disabled: disabled,
        required: required,
        form: form || null,
        capture: computedCapture,
        accept: accept || null,
        multiple: multiple,
        directory: directory,
        webkitdirectory: directory,
        'aria-required': required ? 'true' : null
      });
    },
    computedFileNameFormatter: function computedFileNameFormatter() {
      var fileNameFormatter = this.fileNameFormatter;
      return fileNameFormatter.name !== props.fileNameFormatter.default.name ? fileNameFormatter : this.defaultFileNameFormatter;
    },
    clonedFiles: function clonedFiles() {
      return cloneDeep(this.files);
    },
    flattenedFiles: function flattenedFiles() {
      return flattenDeep(this.files);
    },
    fileNames: function fileNames() {
      return this.flattenedFiles.map(function (file) {
        return file.name;
      });
    },
    labelContent: function labelContent() {
      var h = this.$createElement; // Draging active

      /* istanbul ignore next: used by drag/drop which can't be tested easily */

      if (this.dragging && !this.noDrop) {
        return (// TODO: Add additional scope with file count, and other not-allowed reasons
          this.normalizeSlot('drop-placeholder', {
            allowed: this.dropAllowed
          }) || (this.dropAllowed ? this.dropPlaceholder : h('span', {
            staticClass: 'text-danger'
          }, this.noDropPlaceholder))
        );
      } // No file chosen


      if (this.files.length === 0) {
        return this.normalizeSlot('placeholder') || this.placeholder;
      }

      var flattenedFiles = this.flattenedFiles,
          clonedFiles = this.clonedFiles,
          fileNames = this.fileNames,
          computedFileNameFormatter = this.computedFileNameFormatter; // There is a slot for formatting the files/names

      if (this.hasNormalizedSlot('file-name')) {
        return this.normalizeSlot('file-name', {
          files: flattenedFiles,
          filesTraversed: clonedFiles,
          names: fileNames
        });
      }

      return computedFileNameFormatter(flattenedFiles, clonedFiles, fileNames);
    }
  },
  watch: {
    value: function value(newValue) {
      if (!newValue || isArray(newValue) && newValue.length === 0) {
        this.reset();
      }
    },
    files: function files(newValue, oldValue) {
      if (!looseEqual(newValue, oldValue)) {
        var multiple = this.multiple,
            noTraverse = this.noTraverse;
        var files = !multiple || noTraverse ? flattenDeep(newValue) : newValue;
        this.$emit('input', multiple ? files : files[0] || null);
      }
    }
  },
  mounted: function mounted() {
    var _this = this;

    // Listen for form reset events, to reset the file input
    var $form = closest('form', this.$el);

    if ($form) {
      eventOn($form, 'reset', this.reset, EVENT_OPTIONS_PASSIVE);
      this.$on('hook:beforeDestroy', function () {
        eventOff($form, 'reset', _this.reset, EVENT_OPTIONS_PASSIVE);
      });
    }
  },
  methods: {
    isFileValid: function isFileValid(file) {
      if (!file) {
        return false;
      }

      var accept = this.computedAccept;
      return accept ? accept.some(function (a) {
        return a.rx.test(file[a.prop]);
      }) : true;
    },
    isFilesArrayValid: function isFilesArrayValid(files) {
      var _this2 = this;

      return isArray(files) ? files.every(function (file) {
        return _this2.isFileValid(file);
      }) : this.isFileValid(files);
    },
    defaultFileNameFormatter: function defaultFileNameFormatter(flattenedFiles, clonedFiles, fileNames) {
      return fileNames.join(', ');
    },
    setFiles: function setFiles(files) {
      // Reset the dragging flags
      this.dropAllowed = !this.noDrop;
      this.dragging = false; // Set the selected files

      this.files = this.multiple ? this.directory ? files : flattenDeep(files) : flattenDeep(files).slice(0, 1);
    },

    /* istanbul ignore next: used by Drag/Drop */
    setInputFiles: function setInputFiles(files) {
      // Try an set the file input files array so that `required`
      // constraint works for dropped files (will fail in IE11 though)
      // To be used only when dropping files
      try {
        // Firefox < 62 workaround exploiting https://bugzilla.mozilla.org/show_bug.cgi?id=1422655
        var dataTransfer = new ClipboardEvent('').clipboardData || new DataTransfer(); // Add flattened files to temp `dataTransfer` object to get a true `FileList` array

        flattenDeep(cloneDeep(files)).forEach(function (file) {
          // Make sure to remove the custom `$path` attribute
          delete file.$path;
          dataTransfer.items.add(file);
        });
        this.$refs.input.files = dataTransfer.files;
      } catch (_unused) {}
    },
    reset: function reset() {
      // IE 11 doesn't support setting `$input.value` to `''` or `null`
      // So we use this little extra hack to reset the value, just in case
      // This also appears to work on modern browsers as well
      // Wrapped in try in case IE 11 or mobile Safari crap out
      try {
        var $input = this.$refs.input;
        $input.value = '';
        $input.type = '';
        $input.type = 'file';
      } catch (_unused2) {}

      this.files = [];
    },
    handleFiles: function handleFiles(files) {
      var isDrop = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;

      if (isDrop) {
        // When dropped, make sure to filter files with the internal `accept` logic
        var filteredFiles = files.filter(this.isFilesArrayValid); // Only update files when we have any after filtering

        if (filteredFiles.length > 0) {
          this.setFiles(filteredFiles); // Try an set the file input's files array so that `required`
          // constraint works for dropped files (will fail in IE 11 though)

          this.setInputFiles(filteredFiles);
        }
      } else {
        // We always update the files from the `change` event
        this.setFiles(files);
      }
    },
    focusHandler: function focusHandler(evt) {
      // Bootstrap v4 doesn't have focus styling for custom file input
      // Firefox has a `[type=file]:focus ~ sibling` selector issue,
      // so we add a `focus` class to get around these bugs
      if (this.plain || evt.type === 'focusout') {
        this.hasFocus = false;
      } else {
        // Add focus styling for custom file input
        this.hasFocus = true;
      }
    },
    onChange: function onChange(evt) {
      var _this3 = this;

      var type = evt.type,
          target = evt.target,
          _evt$dataTransfer = evt.dataTransfer,
          dataTransfer = _evt$dataTransfer === void 0 ? {} : _evt$dataTransfer;
      var isDrop = type === 'drop'; // Always emit original event

      this.$emit('change', evt);
      var items = arrayFrom(dataTransfer.items || []);

      if (hasPromiseSupport && items.length > 0 && !isNull(getDataTransferItemEntry(items[0]))) {
        // Drop handling for modern browsers
        // Supports nested directory structures in `directory` mode

        /* istanbul ignore next: not supported in JSDOM */
        getAllFileEntries(items, this.directory).then(function (files) {
          return _this3.handleFiles(files, isDrop);
        });
      } else {
        // Standard file input handling (native file input change event),
        // or fallback drop mode (IE 11 / Opera) which don't support `directory` mode
        var files = arrayFrom(target.files || dataTransfer.files || []).map(function (file) {
          // Add custom `$path` property to each file (to be consistent with drop mode)
          file.$path = file.webkitRelativePath || '';
          return file;
        });
        this.handleFiles(files, isDrop);
      }
    },
    onDragenter: function onDragenter(evt) {
      stopEvent(evt);
      this.dragging = true;
      var _evt$dataTransfer2 = evt.dataTransfer,
          dataTransfer = _evt$dataTransfer2 === void 0 ? {} : _evt$dataTransfer2; // Early exit when the input or dropping is disabled

      if (this.noDrop || this.disabled || !this.dropAllowed) {
        // Show deny feedback

        /* istanbul ignore next: not supported in JSDOM */
        dataTransfer.dropEffect = 'none';
        this.dropAllowed = false;
        return;
      }
      /* istanbul ignore next: not supported in JSDOM */


      dataTransfer.dropEffect = 'copy';
    },
    // Note this event fires repeatedly while the mouse is over the dropzone at
    // intervals in the milliseconds, so avoid doing much processing in here
    onDragover: function onDragover(evt) {
      stopEvent(evt);
      this.dragging = true;
      var _evt$dataTransfer3 = evt.dataTransfer,
          dataTransfer = _evt$dataTransfer3 === void 0 ? {} : _evt$dataTransfer3; // Early exit when the input or dropping is disabled

      if (this.noDrop || this.disabled || !this.dropAllowed) {
        // Show deny feedback

        /* istanbul ignore next: not supported in JSDOM */
        dataTransfer.dropEffect = 'none';
        this.dropAllowed = false;
        return;
      }
      /* istanbul ignore next: not supported in JSDOM */


      dataTransfer.dropEffect = 'copy';
    },
    onDragleave: function onDragleave(evt) {
      var _this4 = this;

      stopEvent(evt);
      this.$nextTick(function () {
        _this4.dragging = false; // Reset `dropAllowed` to default

        _this4.dropAllowed = !_this4.noDrop;
      });
    },
    // Triggered by a file drop onto drop target
    onDrop: function onDrop(evt) {
      var _this5 = this;

      stopEvent(evt);
      this.dragging = false; // Early exit when the input or dropping is disabled

      if (this.noDrop || this.disabled || !this.dropAllowed) {
        this.$nextTick(function () {
          // Reset `dropAllowed` to default
          _this5.dropAllowed = !_this5.noDrop;
        });
        return;
      }

      this.onChange(evt);
    }
  },
  render: function render(h) {
    var custom = this.custom,
        plain = this.plain,
        size = this.size,
        dragging = this.dragging,
        stateClass = this.stateClass; // Form Input

    var $input = h('input', {
      ref: 'input',
      class: [{
        'form-control-file': plain,
        'custom-file-input': custom,
        focus: custom && this.hasFocus
      }, stateClass],
      // With IE 11, the input gets in the "way" of the drop events,
      // so we move it out of the way by putting it behind the label
      // Bootstrap v4 has it in front
      style: custom ? {
        zIndex: -5
      } : {},
      attrs: this.computedAttrs,
      on: {
        change: this.onChange,
        focusin: this.focusHandler,
        focusout: this.focusHandler,
        reset: this.reset
      }
    });

    if (plain) {
      return $input;
    } // Overlay label


    var $label = h('label', {
      staticClass: 'custom-file-label',
      class: {
        dragging: dragging
      },
      attrs: {
        for: this.safeId(),
        // This goes away in Bootstrap v5
        'data-browse': this.browseText || null
      }
    }, [h('span', {
      staticClass: 'd-block form-file-text',
      // `pointer-events: none` is used to make sure
      // the drag events fire only on the label
      style: {
        pointerEvents: 'none'
      }
    }, [this.labelContent])]); // Return rendered custom file input

    return h('div', {
      staticClass: 'custom-file b-form-file',
      class: [_defineProperty({}, "b-custom-control-".concat(size), size), stateClass],
      attrs: {
        id: this.safeId('_BV_file_outer_')
      },
      on: {
        dragenter: this.onDragenter,
        dragover: this.onDragover,
        dragleave: this.onDragleave,
        drop: this.onDrop
      }
    }, [$input, $label]);
  }
});