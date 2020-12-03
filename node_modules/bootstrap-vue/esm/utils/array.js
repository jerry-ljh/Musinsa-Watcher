// --- Static ---
export var from = function from() {
  return Array.from.apply(Array, arguments);
}; // --- Instance ---

export var arrayIncludes = function arrayIncludes(array, value) {
  return array.indexOf(value) !== -1;
};
export var concat = function concat() {
  for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
    args[_key] = arguments[_key];
  }

  return Array.prototype.concat.apply([], args);
}; // --- Utilities ---

export var createAndFillArray = function createAndFillArray(size, value) {
  return Array(size).fill(value);
};
export var flatten = function flatten(array) {
  return array.reduce(function (result, item) {
    return result.concat(item);
  }, []);
};
export var flattenDeep = function flattenDeep(array) {
  return array.reduce(function (result, item) {
    return result.concat(Array.isArray(item) ? flattenDeep(item) : item);
  }, []);
};