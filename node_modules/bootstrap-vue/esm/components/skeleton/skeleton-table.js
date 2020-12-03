function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(Object(source), true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import Vue from '../../vue';
import { NAME_SKELETON_TABLE } from '../../constants/components';
import { createAndFillArray } from '../../utils/array';
import { makePropsConfigurable } from '../../utils/config';
import { BSkeleton } from './skeleton';
import { BTableSimple } from '../table'; // @vue/component

export var BSkeletonTable = /*#__PURE__*/Vue.extend({
  name: NAME_SKELETON_TABLE,
  functional: true,
  props: makePropsConfigurable({
    animation: {
      type: String
    },
    rows: {
      type: Number,
      default: 3,
      validator: function validator(value) {
        return value > 0;
      }
    },
    columns: {
      type: Number,
      default: 5,
      validator: function validator(value) {
        return value > 0;
      }
    },
    hideHeader: {
      type: Boolean,
      default: false
    },
    showFooter: {
      type: Boolean,
      default: false
    },
    tableProps: {
      type: Object,
      default: function _default() {}
    }
  }, NAME_SKELETON_TABLE),
  render: function render(h, _ref) {
    var props = _ref.props;
    var animation = props.animation,
        columns = props.columns;
    var $th = h('th', [h(BSkeleton, {
      props: {
        animation: animation
      }
    })]);
    var $thTr = h('tr', createAndFillArray(columns, $th));
    var $td = h('td', [h(BSkeleton, {
      props: {
        width: '75%',
        animation: animation
      }
    })]);
    var $tdTr = h('tr', createAndFillArray(columns, $td));
    var $tbody = h('tbody', createAndFillArray(props.rows, $tdTr));
    var $thead = !props.hideHeader ? h('thead', [$thTr]) : h();
    var $tfoot = props.showFooter ? h('tfoot', [$thTr]) : h();
    return h(BTableSimple, {
      props: _objectSpread({}, props.tableProps)
    }, [$thead, $tbody, $tfoot]);
  }
});