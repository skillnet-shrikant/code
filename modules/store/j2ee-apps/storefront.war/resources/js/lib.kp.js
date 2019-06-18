(function (global, namespace ) {
	"use strict";

	var smallRange = [0, 767],
			mediumRange = [768, 979],
			largeRange = [980, 1599999984],
			screen ='only screen',
			contextRoot = global[namespace].constants.contextPath,
			config = {
				loggingDebug: false,
				smallMax: smallRange[1],
				mediumMin: mediumRange[0],
				mediumMax: mediumRange[1],
				largeMin: largeRange[0],
				maxWidth: 980,
				landscape: screen + ' and (orientation: landscape)',
				portrait: screen + ' and (orientation: portrait)',
				smallUp: screen,
				smallOnly: screen + ' and (max-width: '+ Math.max.apply(null, smallRange) +'px)',
				mediumUp: screen + ' and (min-width: '+ Math.min.apply(null, mediumRange) +'px)',
				mediumOnly: screen + ' and (min-width:' +  Math.min.apply(null, mediumRange) + 'px) and (max-width: '+ Math.max.apply(null, mediumRange) +'px)',
				largeUp: screen + ' and (min-width: '+ Math.min.apply(null, largeRange) +'px)',
				largeOnly: screen + ' and (min-width:' +  Math.min.apply(null, largeRange) + 'px) and (max-width: '+ Math.max.apply(null, largeRange) +'px)',
				/* list of cached pages or directories */
				cachedPages: [
					/*  set list of regEx rules to use to match the pages cached on Akamai.
					 '^' + contextRoot + '/$',						// home page
					 '^' + contextRoot + '/index.jsp$',	// also home page
					 contextRoot + '/browse/product.jsp'	//PDP
					 */
				]
			};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].config = config;

}(this, "KP"));

/*!
 * jQuery JavaScript Library v1.11.1
 * http://jquery.com/
 *
 * Includes Sizzle.js
 * http://sizzlejs.com/
 *
 * Copyright 2005, 2014 jQuery Foundation, Inc. and other contributors
 * Released under the MIT license
 * http://jquery.org/license
 *
 * Date: 2014-05-01T17:42Z
 */

(function( global, factory ) {

	if ( typeof module === "object" && typeof module.exports === "object" ) {
		// For CommonJS and CommonJS-like environments where a proper window is present,
		// execute the factory and get jQuery
		// For environments that do not inherently posses a window with a document
		// (such as Node.js), expose a jQuery-making factory as module.exports
		// This accentuates the need for the creation of a real window
		// e.g. var jQuery = require("jquery")(window);
		// See ticket #14549 for more info
		module.exports = global.document ?
			factory( global, true ) :
			function( w ) {
				if ( !w.document ) {
					throw new Error( "jQuery requires a window with a document" );
				}
				return factory( w );
			};
	} else {
		factory( global );
	}

// Pass this if window is not defined yet
}(typeof window !== "undefined" ? window : this, function( window, noGlobal ) {

// Can't do this because several apps including ASP.NET trace
// the stack via arguments.caller.callee and Firefox dies if
// you try to trace through "use strict" call chains. (#13335)
// Support: Firefox 18+
//

var deletedIds = [];

var slice = deletedIds.slice;

var concat = deletedIds.concat;

var push = deletedIds.push;

var indexOf = deletedIds.indexOf;

var class2type = {};

var toString = class2type.toString;

var hasOwn = class2type.hasOwnProperty;

var support = {};



var
	version = "1.11.1",

	// Define a local copy of jQuery
	jQuery = function( selector, context ) {
		// The jQuery object is actually just the init constructor 'enhanced'
		// Need init if jQuery is called (just allow error to be thrown if not included)
		return new jQuery.fn.init( selector, context );
	},

	// Support: Android<4.1, IE<9
	// Make sure we trim BOM and NBSP
	rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,

	// Matches dashed string for camelizing
	rmsPrefix = /^-ms-/,
	rdashAlpha = /-([\da-z])/gi,

	// Used by jQuery.camelCase as callback to replace()
	fcamelCase = function( all, letter ) {
		return letter.toUpperCase();
	};

jQuery.fn = jQuery.prototype = {
	// The current version of jQuery being used
	jquery: version,

	constructor: jQuery,

	// Start with an empty selector
	selector: "",

	// The default length of a jQuery object is 0
	length: 0,

	toArray: function() {
		return slice.call( this );
	},

	// Get the Nth element in the matched element set OR
	// Get the whole matched element set as a clean array
	get: function( num ) {
		return num != null ?

			// Return just the one element from the set
			( num < 0 ? this[ num + this.length ] : this[ num ] ) :

			// Return all the elements in a clean array
			slice.call( this );
	},

	// Take an array of elements and push it onto the stack
	// (returning the new matched element set)
	pushStack: function( elems ) {

		// Build a new jQuery matched element set
		var ret = jQuery.merge( this.constructor(), elems );

		// Add the old object onto the stack (as a reference)
		ret.prevObject = this;
		ret.context = this.context;

		// Return the newly-formed element set
		return ret;
	},

	// Execute a callback for every element in the matched set.
	// (You can seed the arguments with an array of args, but this is
	// only used internally.)
	each: function( callback, args ) {
		return jQuery.each( this, callback, args );
	},

	map: function( callback ) {
		return this.pushStack( jQuery.map(this, function( elem, i ) {
			return callback.call( elem, i, elem );
		}));
	},

	slice: function() {
		return this.pushStack( slice.apply( this, arguments ) );
	},

	first: function() {
		return this.eq( 0 );
	},

	last: function() {
		return this.eq( -1 );
	},

	eq: function( i ) {
		var len = this.length,
			j = +i + ( i < 0 ? len : 0 );
		return this.pushStack( j >= 0 && j < len ? [ this[j] ] : [] );
	},

	end: function() {
		return this.prevObject || this.constructor(null);
	},

	// For internal use only.
	// Behaves like an Array's method, not like a jQuery method.
	push: push,
	sort: deletedIds.sort,
	splice: deletedIds.splice
};

jQuery.extend = jQuery.fn.extend = function() {
	var src, copyIsArray, copy, name, options, clone,
		target = arguments[0] || {},
		i = 1,
		length = arguments.length,
		deep = false;

	// Handle a deep copy situation
	if ( typeof target === "boolean" ) {
		deep = target;

		// skip the boolean and the target
		target = arguments[ i ] || {};
		i++;
	}

	// Handle case when target is a string or something (possible in deep copy)
	if ( typeof target !== "object" && !jQuery.isFunction(target) ) {
		target = {};
	}

	// extend jQuery itself if only one argument is passed
	if ( i === length ) {
		target = this;
		i--;
	}

	for ( ; i < length; i++ ) {
		// Only deal with non-null/undefined values
		if ( (options = arguments[ i ]) != null ) {
			// Extend the base object
			for ( name in options ) {
				src = target[ name ];
				copy = options[ name ];

				// Prevent never-ending loop
				if ( target === copy ) {
					continue;
				}

				// Recurse if we're merging plain objects or arrays
				if ( deep && copy && ( jQuery.isPlainObject(copy) || (copyIsArray = jQuery.isArray(copy)) ) ) {
					if ( copyIsArray ) {
						copyIsArray = false;
						clone = src && jQuery.isArray(src) ? src : [];

					} else {
						clone = src && jQuery.isPlainObject(src) ? src : {};
					}

					// Never move original objects, clone them
					target[ name ] = jQuery.extend( deep, clone, copy );

				// Don't bring in undefined values
				} else if ( copy !== undefined ) {
					target[ name ] = copy;
				}
			}
		}
	}

	// Return the modified object
	return target;
};

jQuery.extend({
	// Unique for each copy of jQuery on the page
	expando: "jQuery" + ( version + Math.random() ).replace( /\D/g, "" ),

	// Assume jQuery is ready without the ready module
	isReady: true,

	error: function( msg ) {
		throw new Error( msg );
	},

	noop: function() {},

	// See test/unit/core.js for details concerning isFunction.
	// Since version 1.3, DOM methods and functions like alert
	// aren't supported. They return false on IE (#2968).
	isFunction: function( obj ) {
		return jQuery.type(obj) === "function";
	},

	isArray: Array.isArray || function( obj ) {
		return jQuery.type(obj) === "array";
	},

	isWindow: function( obj ) {
		/* jshint eqeqeq: false */
		return obj != null && obj == obj.window;
	},

	isNumeric: function( obj ) {
		// parseFloat NaNs numeric-cast false positives (null|true|false|"")
		// ...but misinterprets leading-number strings, particularly hex literals ("0x...")
		// subtraction forces infinities to NaN
		return !jQuery.isArray( obj ) && obj - parseFloat( obj ) >= 0;
	},

	isEmptyObject: function( obj ) {
		var name;
		for ( name in obj ) {
			return false;
		}
		return true;
	},

	isPlainObject: function( obj ) {
		var key;

		// Must be an Object.
		// Because of IE, we also have to check the presence of the constructor property.
		// Make sure that DOM nodes and window objects don't pass through, as well
		if ( !obj || jQuery.type(obj) !== "object" || obj.nodeType || jQuery.isWindow( obj ) ) {
			return false;
		}

		try {
			// Not own constructor property must be Object
			if ( obj.constructor &&
				!hasOwn.call(obj, "constructor") &&
				!hasOwn.call(obj.constructor.prototype, "isPrototypeOf") ) {
				return false;
			}
		} catch ( e ) {
			// IE8,9 Will throw exceptions on certain host objects #9897
			return false;
		}

		// Support: IE<9
		// Handle iteration over inherited properties before own properties.
		if ( support.ownLast ) {
			for ( key in obj ) {
				return hasOwn.call( obj, key );
			}
		}

		// Own properties are enumerated firstly, so to speed up,
		// if last one is own, then all properties are own.
		for ( key in obj ) {}

		return key === undefined || hasOwn.call( obj, key );
	},

	type: function( obj ) {
		if ( obj == null ) {
			return obj + "";
		}
		return typeof obj === "object" || typeof obj === "function" ?
			class2type[ toString.call(obj) ] || "object" :
			typeof obj;
	},

	// Evaluates a script in a global context
	// Workarounds based on findings by Jim Driscoll
	// http://weblogs.java.net/blog/driscoll/archive/2009/09/08/eval-javascript-global-context
	globalEval: function( data ) {
		if ( data && jQuery.trim( data ) ) {
			// We use execScript on Internet Explorer
			// We use an anonymous function so that context is window
			// rather than jQuery in Firefox
			( window.execScript || function( data ) {
				window[ "eval" ].call( window, data );
			} )( data );
		}
	},

	// Convert dashed to camelCase; used by the css and data modules
	// Microsoft forgot to hump their vendor prefix (#9572)
	camelCase: function( string ) {
		return string.replace( rmsPrefix, "ms-" ).replace( rdashAlpha, fcamelCase );
	},

	nodeName: function( elem, name ) {
		return elem.nodeName && elem.nodeName.toLowerCase() === name.toLowerCase();
	},

	// args is for internal usage only
	each: function( obj, callback, args ) {
		var value,
			i = 0,
			length = obj.length,
			isArray = isArraylike( obj );

		if ( args ) {
			if ( isArray ) {
				for ( ; i < length; i++ ) {
					value = callback.apply( obj[ i ], args );

					if ( value === false ) {
						break;
					}
				}
			} else {
				for ( i in obj ) {
					value = callback.apply( obj[ i ], args );

					if ( value === false ) {
						break;
					}
				}
			}

		// A special, fast, case for the most common use of each
		} else {
			if ( isArray ) {
				for ( ; i < length; i++ ) {
					value = callback.call( obj[ i ], i, obj[ i ] );

					if ( value === false ) {
						break;
					}
				}
			} else {
				for ( i in obj ) {
					value = callback.call( obj[ i ], i, obj[ i ] );

					if ( value === false ) {
						break;
					}
				}
			}
		}

		return obj;
	},

	// Support: Android<4.1, IE<9
	trim: function( text ) {
		return text == null ?
			"" :
			( text + "" ).replace( rtrim, "" );
	},

	// results is for internal usage only
	makeArray: function( arr, results ) {
		var ret = results || [];

		if ( arr != null ) {
			if ( isArraylike( Object(arr) ) ) {
				jQuery.merge( ret,
					typeof arr === "string" ?
					[ arr ] : arr
				);
			} else {
				push.call( ret, arr );
			}
		}

		return ret;
	},

	inArray: function( elem, arr, i ) {
		var len;

		if ( arr ) {
			if ( indexOf ) {
				return indexOf.call( arr, elem, i );
			}

			len = arr.length;
			i = i ? i < 0 ? Math.max( 0, len + i ) : i : 0;

			for ( ; i < len; i++ ) {
				// Skip accessing in sparse arrays
				if ( i in arr && arr[ i ] === elem ) {
					return i;
				}
			}
		}

		return -1;
	},

	merge: function( first, second ) {
		var len = +second.length,
			j = 0,
			i = first.length;

		while ( j < len ) {
			first[ i++ ] = second[ j++ ];
		}

		// Support: IE<9
		// Workaround casting of .length to NaN on otherwise arraylike objects (e.g., NodeLists)
		if ( len !== len ) {
			while ( second[j] !== undefined ) {
				first[ i++ ] = second[ j++ ];
			}
		}

		first.length = i;

		return first;
	},

	grep: function( elems, callback, invert ) {
		var callbackInverse,
			matches = [],
			i = 0,
			length = elems.length,
			callbackExpect = !invert;

		// Go through the array, only saving the items
		// that pass the validator function
		for ( ; i < length; i++ ) {
			callbackInverse = !callback( elems[ i ], i );
			if ( callbackInverse !== callbackExpect ) {
				matches.push( elems[ i ] );
			}
		}

		return matches;
	},

	// arg is for internal usage only
	map: function( elems, callback, arg ) {
		var value,
			i = 0,
			length = elems.length,
			isArray = isArraylike( elems ),
			ret = [];

		// Go through the array, translating each of the items to their new values
		if ( isArray ) {
			for ( ; i < length; i++ ) {
				value = callback( elems[ i ], i, arg );

				if ( value != null ) {
					ret.push( value );
				}
			}

		// Go through every key on the object,
		} else {
			for ( i in elems ) {
				value = callback( elems[ i ], i, arg );

				if ( value != null ) {
					ret.push( value );
				}
			}
		}

		// Flatten any nested arrays
		return concat.apply( [], ret );
	},

	// A global GUID counter for objects
	guid: 1,

	// Bind a function to a context, optionally partially applying any
	// arguments.
	proxy: function( fn, context ) {
		var args, proxy, tmp;

		if ( typeof context === "string" ) {
			tmp = fn[ context ];
			context = fn;
			fn = tmp;
		}

		// Quick check to determine if target is callable, in the spec
		// this throws a TypeError, but we will just return undefined.
		if ( !jQuery.isFunction( fn ) ) {
			return undefined;
		}

		// Simulated bind
		args = slice.call( arguments, 2 );
		proxy = function() {
			return fn.apply( context || this, args.concat( slice.call( arguments ) ) );
		};

		// Set the guid of unique handler to the same of original handler, so it can be removed
		proxy.guid = fn.guid = fn.guid || jQuery.guid++;

		return proxy;
	},

	now: function() {
		return +( new Date() );
	},

	// jQuery.support is not used in Core but other projects attach their
	// properties to it so it needs to exist.
	support: support
});

// Populate the class2type map
jQuery.each("Boolean Number String Function Array Date RegExp Object Error".split(" "), function(i, name) {
	class2type[ "[object " + name + "]" ] = name.toLowerCase();
});

function isArraylike( obj ) {
	var length = obj.length,
		type = jQuery.type( obj );

	if ( type === "function" || jQuery.isWindow( obj ) ) {
		return false;
	}

	if ( obj.nodeType === 1 && length ) {
		return true;
	}

	return type === "array" || length === 0 ||
		typeof length === "number" && length > 0 && ( length - 1 ) in obj;
}
var Sizzle =
/*!
 * Sizzle CSS Selector Engine v1.10.19
 * http://sizzlejs.com/
 *
 * Copyright 2013 jQuery Foundation, Inc. and other contributors
 * Released under the MIT license
 * http://jquery.org/license
 *
 * Date: 2014-04-18
 */
(function( window ) {

var i,
	support,
	Expr,
	getText,
	isXML,
	tokenize,
	compile,
	select,
	outermostContext,
	sortInput,
	hasDuplicate,

	// Local document vars
	setDocument,
	document,
	docElem,
	documentIsHTML,
	rbuggyQSA,
	rbuggyMatches,
	matches,
	contains,

	// Instance-specific data
	expando = "sizzle" + -(new Date()),
	preferredDoc = window.document,
	dirruns = 0,
	done = 0,
	classCache = createCache(),
	tokenCache = createCache(),
	compilerCache = createCache(),
	sortOrder = function( a, b ) {
		if ( a === b ) {
			hasDuplicate = true;
		}
		return 0;
	},

	// General-purpose constants
	strundefined = typeof undefined,
	MAX_NEGATIVE = 1 << 31,

	// Instance methods
	hasOwn = ({}).hasOwnProperty,
	arr = [],
	pop = arr.pop,
	push_native = arr.push,
	push = arr.push,
	slice = arr.slice,
	// Use a stripped-down indexOf if we can't use a native one
	indexOf = arr.indexOf || function( elem ) {
		var i = 0,
			len = this.length;
		for ( ; i < len; i++ ) {
			if ( this[i] === elem ) {
				return i;
			}
		}
		return -1;
	},

	booleans = "checked|selected|async|autofocus|autoplay|controls|defer|disabled|hidden|ismap|loop|multiple|open|readonly|required|scoped",

	// Regular expressions

	// Whitespace characters http://www.w3.org/TR/css3-selectors/#whitespace
	whitespace = "[\\x20\\t\\r\\n\\f]",
	// http://www.w3.org/TR/css3-syntax/#characters
	characterEncoding = "(?:\\\\.|[\\w-]|[^\\x00-\\xa0])+",

	// Loosely modeled on CSS identifier characters
	// An unquoted value should be a CSS identifier http://www.w3.org/TR/css3-selectors/#attribute-selectors
	// Proper syntax: http://www.w3.org/TR/CSS21/syndata.html#value-def-identifier
	identifier = characterEncoding.replace( "w", "w#" ),

	// Attribute selectors: http://www.w3.org/TR/selectors/#attribute-selectors
	attributes = "\\[" + whitespace + "*(" + characterEncoding + ")(?:" + whitespace +
		// Operator (capture 2)
		"*([*^$|!~]?=)" + whitespace +
		// "Attribute values must be CSS identifiers [capture 5] or strings [capture 3 or capture 4]"
		"*(?:'((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\"|(" + identifier + "))|)" + whitespace +
		"*\\]",

	pseudos = ":(" + characterEncoding + ")(?:\\((" +
		// To reduce the number of selectors needing tokenize in the preFilter, prefer arguments:
		// 1. quoted (capture 3; capture 4 or capture 5)
		"('((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\")|" +
		// 2. simple (capture 6)
		"((?:\\\\.|[^\\\\()[\\]]|" + attributes + ")*)|" +
		// 3. anything else (capture 2)
		".*" +
		")\\)|)",

	// Leading and non-escaped trailing whitespace, capturing some non-whitespace characters preceding the latter
	rtrim = new RegExp( "^" + whitespace + "+|((?:^|[^\\\\])(?:\\\\.)*)" + whitespace + "+$", "g" ),

	rcomma = new RegExp( "^" + whitespace + "*," + whitespace + "*" ),
	rcombinators = new RegExp( "^" + whitespace + "*([>+~]|" + whitespace + ")" + whitespace + "*" ),

	rattributeQuotes = new RegExp( "=" + whitespace + "*([^\\]'\"]*?)" + whitespace + "*\\]", "g" ),

	rpseudo = new RegExp( pseudos ),
	ridentifier = new RegExp( "^" + identifier + "$" ),

	matchExpr = {
		"ID": new RegExp( "^#(" + characterEncoding + ")" ),
		"CLASS": new RegExp( "^\\.(" + characterEncoding + ")" ),
		"TAG": new RegExp( "^(" + characterEncoding.replace( "w", "w*" ) + ")" ),
		"ATTR": new RegExp( "^" + attributes ),
		"PSEUDO": new RegExp( "^" + pseudos ),
		"CHILD": new RegExp( "^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\(" + whitespace +
			"*(even|odd|(([+-]|)(\\d*)n|)" + whitespace + "*(?:([+-]|)" + whitespace +
			"*(\\d+)|))" + whitespace + "*\\)|)", "i" ),
		"bool": new RegExp( "^(?:" + booleans + ")$", "i" ),
		// For use in libraries implementing .is()
		// We use this for POS matching in `select`
		"needsContext": new RegExp( "^" + whitespace + "*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\(" +
			whitespace + "*((?:-\\d)?\\d*)" + whitespace + "*\\)|)(?=[^-]|$)", "i" )
	},

	rinputs = /^(?:input|select|textarea|button)$/i,
	rheader = /^h\d$/i,

	rnative = /^[^{]+\{\s*\[native \w/,

	// Easily-parseable/retrievable ID or TAG or CLASS selectors
	rquickExpr = /^(?:#([\w-]+)|(\w+)|\.([\w-]+))$/,

	rsibling = /[+~]/,
	rescape = /'|\\/g,

	// CSS escapes http://www.w3.org/TR/CSS21/syndata.html#escaped-characters
	runescape = new RegExp( "\\\\([\\da-f]{1,6}" + whitespace + "?|(" + whitespace + ")|.)", "ig" ),
	funescape = function( _, escaped, escapedWhitespace ) {
		var high = "0x" + escaped - 0x10000;
		// NaN means non-codepoint
		// Support: Firefox<24
		// Workaround erroneous numeric interpretation of +"0x"
		return high !== high || escapedWhitespace ?
			escaped :
			high < 0 ?
				// BMP codepoint
				String.fromCharCode( high + 0x10000 ) :
				// Supplemental Plane codepoint (surrogate pair)
				String.fromCharCode( high >> 10 | 0xD800, high & 0x3FF | 0xDC00 );
	};

// Optimize for push.apply( _, NodeList )
try {
	push.apply(
		(arr = slice.call( preferredDoc.childNodes )),
		preferredDoc.childNodes
	);
	// Support: Android<4.0
	// Detect silently failing push.apply
	arr[ preferredDoc.childNodes.length ].nodeType;
} catch ( e ) {
	push = { apply: arr.length ?

		// Leverage slice if possible
		function( target, els ) {
			push_native.apply( target, slice.call(els) );
		} :

		// Support: IE<9
		// Otherwise append directly
		function( target, els ) {
			var j = target.length,
				i = 0;
			// Can't trust NodeList.length
			while ( (target[j++] = els[i++]) ) {}
			target.length = j - 1;
		}
	};
}

function Sizzle( selector, context, results, seed ) {
	var match, elem, m, nodeType,
		// QSA vars
		i, groups, old, nid, newContext, newSelector;

	if ( ( context ? context.ownerDocument || context : preferredDoc ) !== document ) {
		setDocument( context );
	}

	context = context || document;
	results = results || [];

	if ( !selector || typeof selector !== "string" ) {
		return results;
	}

	if ( (nodeType = context.nodeType) !== 1 && nodeType !== 9 ) {
		return [];
	}

	if ( documentIsHTML && !seed ) {

		// Shortcuts
		if ( (match = rquickExpr.exec( selector )) ) {
			// Speed-up: Sizzle("#ID")
			if ( (m = match[1]) ) {
				if ( nodeType === 9 ) {
					elem = context.getElementById( m );
					// Check parentNode to catch when Blackberry 4.6 returns
					// nodes that are no longer in the document (jQuery #6963)
					if ( elem && elem.parentNode ) {
						// Handle the case where IE, Opera, and Webkit return items
						// by name instead of ID
						if ( elem.id === m ) {
							results.push( elem );
							return results;
						}
					} else {
						return results;
					}
				} else {
					// Context is not a document
					if ( context.ownerDocument && (elem = context.ownerDocument.getElementById( m )) &&
						contains( context, elem ) && elem.id === m ) {
						results.push( elem );
						return results;
					}
				}

			// Speed-up: Sizzle("TAG")
			} else if ( match[2] ) {
				push.apply( results, context.getElementsByTagName( selector ) );
				return results;

			// Speed-up: Sizzle(".CLASS")
			} else if ( (m = match[3]) && support.getElementsByClassName && context.getElementsByClassName ) {
				push.apply( results, context.getElementsByClassName( m ) );
				return results;
			}
		}

		// QSA path
		if ( support.qsa && (!rbuggyQSA || !rbuggyQSA.test( selector )) ) {
			nid = old = expando;
			newContext = context;
			newSelector = nodeType === 9 && selector;

			// qSA works strangely on Element-rooted queries
			// We can work around this by specifying an extra ID on the root
			// and working up from there (Thanks to Andrew Dupont for the technique)
			// IE 8 doesn't work on object elements
			if ( nodeType === 1 && context.nodeName.toLowerCase() !== "object" ) {
				groups = tokenize( selector );

				if ( (old = context.getAttribute("id")) ) {
					nid = old.replace( rescape, "\\$&" );
				} else {
					context.setAttribute( "id", nid );
				}
				nid = "[id='" + nid + "'] ";

				i = groups.length;
				while ( i-- ) {
					groups[i] = nid + toSelector( groups[i] );
				}
				newContext = rsibling.test( selector ) && testContext( context.parentNode ) || context;
				newSelector = groups.join(",");
			}

			if ( newSelector ) {
				try {
					push.apply( results,
						newContext.querySelectorAll( newSelector )
					);
					return results;
				} catch(qsaError) {
				} finally {
					if ( !old ) {
						context.removeAttribute("id");
					}
				}
			}
		}
	}

	// All others
	return select( selector.replace( rtrim, "$1" ), context, results, seed );
}

/**
 * Create key-value caches of limited size
 * @returns {Function(string, Object)} Returns the Object data after storing it on itself with
 *	property name the (space-suffixed) string and (if the cache is larger than Expr.cacheLength)
 *	deleting the oldest entry
 */
function createCache() {
	var keys = [];

	function cache( key, value ) {
		// Use (key + " ") to avoid collision with native prototype properties (see Issue #157)
		if ( keys.push( key + " " ) > Expr.cacheLength ) {
			// Only keep the most recent entries
			delete cache[ keys.shift() ];
		}
		return (cache[ key + " " ] = value);
	}
	return cache;
}

/**
 * Mark a function for special use by Sizzle
 * @param {Function} fn The function to mark
 */
function markFunction( fn ) {
	fn[ expando ] = true;
	return fn;
}

/**
 * Support testing using an element
 * @param {Function} fn Passed the created div and expects a boolean result
 */
function assert( fn ) {
	var div = document.createElement("div");

	try {
		return !!fn( div );
	} catch (e) {
		return false;
	} finally {
		// Remove from its parent by default
		if ( div.parentNode ) {
			div.parentNode.removeChild( div );
		}
		// release memory in IE
		div = null;
	}
}

/**
 * Adds the same handler for all of the specified attrs
 * @param {String} attrs Pipe-separated list of attributes
 * @param {Function} handler The method that will be applied
 */
function addHandle( attrs, handler ) {
	var arr = attrs.split("|"),
		i = attrs.length;

	while ( i-- ) {
		Expr.attrHandle[ arr[i] ] = handler;
	}
}

/**
 * Checks document order of two siblings
 * @param {Element} a
 * @param {Element} b
 * @returns {Number} Returns less than 0 if a precedes b, greater than 0 if a follows b
 */
function siblingCheck( a, b ) {
	var cur = b && a,
		diff = cur && a.nodeType === 1 && b.nodeType === 1 &&
			( ~b.sourceIndex || MAX_NEGATIVE ) -
			( ~a.sourceIndex || MAX_NEGATIVE );

	// Use IE sourceIndex if available on both nodes
	if ( diff ) {
		return diff;
	}

	// Check if b follows a
	if ( cur ) {
		while ( (cur = cur.nextSibling) ) {
			if ( cur === b ) {
				return -1;
			}
		}
	}

	return a ? 1 : -1;
}

/**
 * Returns a function to use in pseudos for input types
 * @param {String} type
 */
function createInputPseudo( type ) {
	return function( elem ) {
		var name = elem.nodeName.toLowerCase();
		return name === "input" && elem.type === type;
	};
}

/**
 * Returns a function to use in pseudos for buttons
 * @param {String} type
 */
function createButtonPseudo( type ) {
	return function( elem ) {
		var name = elem.nodeName.toLowerCase();
		return (name === "input" || name === "button") && elem.type === type;
	};
}

/**
 * Returns a function to use in pseudos for positionals
 * @param {Function} fn
 */
function createPositionalPseudo( fn ) {
	return markFunction(function( argument ) {
		argument = +argument;
		return markFunction(function( seed, matches ) {
			var j,
				matchIndexes = fn( [], seed.length, argument ),
				i = matchIndexes.length;

			// Match elements found at the specified indexes
			while ( i-- ) {
				if ( seed[ (j = matchIndexes[i]) ] ) {
					seed[j] = !(matches[j] = seed[j]);
				}
			}
		});
	});
}

/**
 * Checks a node for validity as a Sizzle context
 * @param {Element|Object=} context
 * @returns {Element|Object|Boolean} The input node if acceptable, otherwise a falsy value
 */
function testContext( context ) {
	return context && typeof context.getElementsByTagName !== strundefined && context;
}

// Expose support vars for convenience
support = Sizzle.support = {};

/**
 * Detects XML nodes
 * @param {Element|Object} elem An element or a document
 * @returns {Boolean} True iff elem is a non-HTML XML node
 */
isXML = Sizzle.isXML = function( elem ) {
	// documentElement is verified for cases where it doesn't yet exist
	// (such as loading iframes in IE - #4833)
	var documentElement = elem && (elem.ownerDocument || elem).documentElement;
	return documentElement ? documentElement.nodeName !== "HTML" : false;
};

/**
 * Sets document-related variables once based on the current document
 * @param {Element|Object} [doc] An element or document object to use to set the document
 * @returns {Object} Returns the current document
 */
setDocument = Sizzle.setDocument = function( node ) {
	var hasCompare,
		doc = node ? node.ownerDocument || node : preferredDoc,
		parent = doc.defaultView;

	// If no document and documentElement is available, return
	if ( doc === document || doc.nodeType !== 9 || !doc.documentElement ) {
		return document;
	}

	// Set our document
	document = doc;
	docElem = doc.documentElement;

	// Support tests
	documentIsHTML = !isXML( doc );

	// Support: IE>8
	// If iframe document is assigned to "document" variable and if iframe has been reloaded,
	// IE will throw "permission denied" error when accessing "document" variable, see jQuery #13936
	// IE6-8 do not support the defaultView property so parent will be undefined
	if ( parent && parent !== parent.top ) {
		// IE11 does not have attachEvent, so all must suffer
		if ( parent.addEventListener ) {
			parent.addEventListener( "unload", function() {
				setDocument();
			}, false );
		} else if ( parent.attachEvent ) {
			parent.attachEvent( "onunload", function() {
				setDocument();
			});
		}
	}

	/* Attributes
	---------------------------------------------------------------------- */

	// Support: IE<8
	// Verify that getAttribute really returns attributes and not properties (excepting IE8 booleans)
	support.attributes = assert(function( div ) {
		div.className = "i";
		return !div.getAttribute("className");
	});

	/* getElement(s)By*
	---------------------------------------------------------------------- */

	// Check if getElementsByTagName("*") returns only elements
	support.getElementsByTagName = assert(function( div ) {
		div.appendChild( doc.createComment("") );
		return !div.getElementsByTagName("*").length;
	});

	// Check if getElementsByClassName can be trusted
	support.getElementsByClassName = rnative.test( doc.getElementsByClassName ) && assert(function( div ) {
		div.innerHTML = "<div class='a'></div><div class='a i'></div>";

		// Support: Safari<4
		// Catch class over-caching
		div.firstChild.className = "i";
		// Support: Opera<10
		// Catch gEBCN failure to find non-leading classes
		return div.getElementsByClassName("i").length === 2;
	});

	// Support: IE<10
	// Check if getElementById returns elements by name
	// The broken getElementById methods don't pick up programatically-set names,
	// so use a roundabout getElementsByName test
	support.getById = assert(function( div ) {
		docElem.appendChild( div ).id = expando;
		return !doc.getElementsByName || !doc.getElementsByName( expando ).length;
	});

	// ID find and filter
	if ( support.getById ) {
		Expr.find["ID"] = function( id, context ) {
			if ( typeof context.getElementById !== strundefined && documentIsHTML ) {
				var m = context.getElementById( id );
				// Check parentNode to catch when Blackberry 4.6 returns
				// nodes that are no longer in the document #6963
				return m && m.parentNode ? [ m ] : [];
			}
		};
		Expr.filter["ID"] = function( id ) {
			var attrId = id.replace( runescape, funescape );
			return function( elem ) {
				return elem.getAttribute("id") === attrId;
			};
		};
	} else {
		// Support: IE6/7
		// getElementById is not reliable as a find shortcut
		delete Expr.find["ID"];

		Expr.filter["ID"] =  function( id ) {
			var attrId = id.replace( runescape, funescape );
			return function( elem ) {
				var node = typeof elem.getAttributeNode !== strundefined && elem.getAttributeNode("id");
				return node && node.value === attrId;
			};
		};
	}

	// Tag
	Expr.find["TAG"] = support.getElementsByTagName ?
		function( tag, context ) {
			if ( typeof context.getElementsByTagName !== strundefined ) {
				return context.getElementsByTagName( tag );
			}
		} :
		function( tag, context ) {
			var elem,
				tmp = [],
				i = 0,
				results = context.getElementsByTagName( tag );

			// Filter out possible comments
			if ( tag === "*" ) {
				while ( (elem = results[i++]) ) {
					if ( elem.nodeType === 1 ) {
						tmp.push( elem );
					}
				}

				return tmp;
			}
			return results;
		};

	// Class
	Expr.find["CLASS"] = support.getElementsByClassName && function( className, context ) {
		if ( typeof context.getElementsByClassName !== strundefined && documentIsHTML ) {
			return context.getElementsByClassName( className );
		}
	};

	/* QSA/matchesSelector
	---------------------------------------------------------------------- */

	// QSA and matchesSelector support

	// matchesSelector(:active) reports false when true (IE9/Opera 11.5)
	rbuggyMatches = [];

	// qSa(:focus) reports false when true (Chrome 21)
	// We allow this because of a bug in IE8/9 that throws an error
	// whenever `document.activeElement` is accessed on an iframe
	// So, we allow :focus to pass through QSA all the time to avoid the IE error
	// See http://bugs.jquery.com/ticket/13378
	rbuggyQSA = [];

	if ( (support.qsa = rnative.test( doc.querySelectorAll )) ) {
		// Build QSA regex
		// Regex strategy adopted from Diego Perini
		assert(function( div ) {
			// Select is set to empty string on purpose
			// This is to test IE's treatment of not explicitly
			// setting a boolean content attribute,
			// since its presence should be enough
			// http://bugs.jquery.com/ticket/12359
			div.innerHTML = "<select msallowclip=''><option selected=''></option></select>";

			// Support: IE8, Opera 11-12.16
			// Nothing should be selected when empty strings follow ^= or $= or *=
			// The test attribute must be unknown in Opera but "safe" for WinRT
			// http://msdn.microsoft.com/en-us/library/ie/hh465388.aspx#attribute_section
			if ( div.querySelectorAll("[msallowclip^='']").length ) {
				rbuggyQSA.push( "[*^$]=" + whitespace + "*(?:''|\"\")" );
			}

			// Support: IE8
			// Boolean attributes and "value" are not treated correctly
			if ( !div.querySelectorAll("[selected]").length ) {
				rbuggyQSA.push( "\\[" + whitespace + "*(?:value|" + booleans + ")" );
			}

			// Webkit/Opera - :checked should return selected option elements
			// http://www.w3.org/TR/2011/REC-css3-selectors-20110929/#checked
			// IE8 throws error here and will not see later tests
			if ( !div.querySelectorAll(":checked").length ) {
				rbuggyQSA.push(":checked");
			}
		});

		assert(function( div ) {
			// Support: Windows 8 Native Apps
			// The type and name attributes are restricted during .innerHTML assignment
			var input = doc.createElement("input");
			input.setAttribute( "type", "hidden" );
			div.appendChild( input ).setAttribute( "name", "D" );

			// Support: IE8
			// Enforce case-sensitivity of name attribute
			if ( div.querySelectorAll("[name=d]").length ) {
				rbuggyQSA.push( "name" + whitespace + "*[*^$|!~]?=" );
			}

			// FF 3.5 - :enabled/:disabled and hidden elements (hidden elements are still enabled)
			// IE8 throws error here and will not see later tests
			if ( !div.querySelectorAll(":enabled").length ) {
				rbuggyQSA.push( ":enabled", ":disabled" );
			}

			// Opera 10-11 does not throw on post-comma invalid pseudos
			div.querySelectorAll("*,:x");
			rbuggyQSA.push(",.*:");
		});
	}

	if ( (support.matchesSelector = rnative.test( (matches = docElem.matches ||
		docElem.webkitMatchesSelector ||
		docElem.mozMatchesSelector ||
		docElem.oMatchesSelector ||
		docElem.msMatchesSelector) )) ) {

		assert(function( div ) {
			// Check to see if it's possible to do matchesSelector
			// on a disconnected node (IE 9)
			support.disconnectedMatch = matches.call( div, "div" );

			// This should fail with an exception
			// Gecko does not error, returns false instead
			matches.call( div, "[s!='']:x" );
			rbuggyMatches.push( "!=", pseudos );
		});
	}

	rbuggyQSA = rbuggyQSA.length && new RegExp( rbuggyQSA.join("|") );
	rbuggyMatches = rbuggyMatches.length && new RegExp( rbuggyMatches.join("|") );

	/* Contains
	---------------------------------------------------------------------- */
	hasCompare = rnative.test( docElem.compareDocumentPosition );

	// Element contains another
	// Purposefully does not implement inclusive descendent
	// As in, an element does not contain itself
	contains = hasCompare || rnative.test( docElem.contains ) ?
		function( a, b ) {
			var adown = a.nodeType === 9 ? a.documentElement : a,
				bup = b && b.parentNode;
			return a === bup || !!( bup && bup.nodeType === 1 && (
				adown.contains ?
					adown.contains( bup ) :
					a.compareDocumentPosition && a.compareDocumentPosition( bup ) & 16
			));
		} :
		function( a, b ) {
			if ( b ) {
				while ( (b = b.parentNode) ) {
					if ( b === a ) {
						return true;
					}
				}
			}
			return false;
		};

	/* Sorting
	---------------------------------------------------------------------- */

	// Document order sorting
	sortOrder = hasCompare ?
	function( a, b ) {

		// Flag for duplicate removal
		if ( a === b ) {
			hasDuplicate = true;
			return 0;
		}

		// Sort on method existence if only one input has compareDocumentPosition
		var compare = !a.compareDocumentPosition - !b.compareDocumentPosition;
		if ( compare ) {
			return compare;
		}

		// Calculate position if both inputs belong to the same document
		compare = ( a.ownerDocument || a ) === ( b.ownerDocument || b ) ?
			a.compareDocumentPosition( b ) :

			// Otherwise we know they are disconnected
			1;

		// Disconnected nodes
		if ( compare & 1 ||
			(!support.sortDetached && b.compareDocumentPosition( a ) === compare) ) {

			// Choose the first element that is related to our preferred document
			if ( a === doc || a.ownerDocument === preferredDoc && contains(preferredDoc, a) ) {
				return -1;
			}
			if ( b === doc || b.ownerDocument === preferredDoc && contains(preferredDoc, b) ) {
				return 1;
			}

			// Maintain original order
			return sortInput ?
				( indexOf.call( sortInput, a ) - indexOf.call( sortInput, b ) ) :
				0;
		}

		return compare & 4 ? -1 : 1;
	} :
	function( a, b ) {
		// Exit early if the nodes are identical
		if ( a === b ) {
			hasDuplicate = true;
			return 0;
		}

		var cur,
			i = 0,
			aup = a.parentNode,
			bup = b.parentNode,
			ap = [ a ],
			bp = [ b ];

		// Parentless nodes are either documents or disconnected
		if ( !aup || !bup ) {
			return a === doc ? -1 :
				b === doc ? 1 :
				aup ? -1 :
				bup ? 1 :
				sortInput ?
				( indexOf.call( sortInput, a ) - indexOf.call( sortInput, b ) ) :
				0;

		// If the nodes are siblings, we can do a quick check
		} else if ( aup === bup ) {
			return siblingCheck( a, b );
		}

		// Otherwise we need full lists of their ancestors for comparison
		cur = a;
		while ( (cur = cur.parentNode) ) {
			ap.unshift( cur );
		}
		cur = b;
		while ( (cur = cur.parentNode) ) {
			bp.unshift( cur );
		}

		// Walk down the tree looking for a discrepancy
		while ( ap[i] === bp[i] ) {
			i++;
		}

		return i ?
			// Do a sibling check if the nodes have a common ancestor
			siblingCheck( ap[i], bp[i] ) :

			// Otherwise nodes in our document sort first
			ap[i] === preferredDoc ? -1 :
			bp[i] === preferredDoc ? 1 :
			0;
	};

	return doc;
};

Sizzle.matches = function( expr, elements ) {
	return Sizzle( expr, null, null, elements );
};

Sizzle.matchesSelector = function( elem, expr ) {
	// Set document vars if needed
	if ( ( elem.ownerDocument || elem ) !== document ) {
		setDocument( elem );
	}

	// Make sure that attribute selectors are quoted
	expr = expr.replace( rattributeQuotes, "='$1']" );

	if ( support.matchesSelector && documentIsHTML &&
		( !rbuggyMatches || !rbuggyMatches.test( expr ) ) &&
		( !rbuggyQSA     || !rbuggyQSA.test( expr ) ) ) {

		try {
			var ret = matches.call( elem, expr );

			// IE 9's matchesSelector returns false on disconnected nodes
			if ( ret || support.disconnectedMatch ||
					// As well, disconnected nodes are said to be in a document
					// fragment in IE 9
					elem.document && elem.document.nodeType !== 11 ) {
				return ret;
			}
		} catch(e) {}
	}

	return Sizzle( expr, document, null, [ elem ] ).length > 0;
};

Sizzle.contains = function( context, elem ) {
	// Set document vars if needed
	if ( ( context.ownerDocument || context ) !== document ) {
		setDocument( context );
	}
	return contains( context, elem );
};

Sizzle.attr = function( elem, name ) {
	// Set document vars if needed
	if ( ( elem.ownerDocument || elem ) !== document ) {
		setDocument( elem );
	}

	var fn = Expr.attrHandle[ name.toLowerCase() ],
		// Don't get fooled by Object.prototype properties (jQuery #13807)
		val = fn && hasOwn.call( Expr.attrHandle, name.toLowerCase() ) ?
			fn( elem, name, !documentIsHTML ) :
			undefined;

	return val !== undefined ?
		val :
		support.attributes || !documentIsHTML ?
			elem.getAttribute( name ) :
			(val = elem.getAttributeNode(name)) && val.specified ?
				val.value :
				null;
};

Sizzle.error = function( msg ) {
	throw new Error( "Syntax error, unrecognized expression: " + msg );
};

/**
 * Document sorting and removing duplicates
 * @param {ArrayLike} results
 */
Sizzle.uniqueSort = function( results ) {
	var elem,
		duplicates = [],
		j = 0,
		i = 0;

	// Unless we *know* we can detect duplicates, assume their presence
	hasDuplicate = !support.detectDuplicates;
	sortInput = !support.sortStable && results.slice( 0 );
	results.sort( sortOrder );

	if ( hasDuplicate ) {
		while ( (elem = results[i++]) ) {
			if ( elem === results[ i ] ) {
				j = duplicates.push( i );
			}
		}
		while ( j-- ) {
			results.splice( duplicates[ j ], 1 );
		}
	}

	// Clear input after sorting to release objects
	// See https://github.com/jquery/sizzle/pull/225
	sortInput = null;

	return results;
};

/**
 * Utility function for retrieving the text value of an array of DOM nodes
 * @param {Array|Element} elem
 */
getText = Sizzle.getText = function( elem ) {
	var node,
		ret = "",
		i = 0,
		nodeType = elem.nodeType;

	if ( !nodeType ) {
		// If no nodeType, this is expected to be an array
		while ( (node = elem[i++]) ) {
			// Do not traverse comment nodes
			ret += getText( node );
		}
	} else if ( nodeType === 1 || nodeType === 9 || nodeType === 11 ) {
		// Use textContent for elements
		// innerText usage removed for consistency of new lines (jQuery #11153)
		if ( typeof elem.textContent === "string" ) {
			return elem.textContent;
		} else {
			// Traverse its children
			for ( elem = elem.firstChild; elem; elem = elem.nextSibling ) {
				ret += getText( elem );
			}
		}
	} else if ( nodeType === 3 || nodeType === 4 ) {
		return elem.nodeValue;
	}
	// Do not include comment or processing instruction nodes

	return ret;
};

Expr = Sizzle.selectors = {

	// Can be adjusted by the user
	cacheLength: 50,

	createPseudo: markFunction,

	match: matchExpr,

	attrHandle: {},

	find: {},

	relative: {
		">": { dir: "parentNode", first: true },
		" ": { dir: "parentNode" },
		"+": { dir: "previousSibling", first: true },
		"~": { dir: "previousSibling" }
	},

	preFilter: {
		"ATTR": function( match ) {
			match[1] = match[1].replace( runescape, funescape );

			// Move the given value to match[3] whether quoted or unquoted
			match[3] = ( match[3] || match[4] || match[5] || "" ).replace( runescape, funescape );

			if ( match[2] === "~=" ) {
				match[3] = " " + match[3] + " ";
			}

			return match.slice( 0, 4 );
		},

		"CHILD": function( match ) {
			/* matches from matchExpr["CHILD"]
				1 type (only|nth|...)
				2 what (child|of-type)
				3 argument (even|odd|\d*|\d*n([+-]\d+)?|...)
				4 xn-component of xn+y argument ([+-]?\d*n|)
				5 sign of xn-component
				6 x of xn-component
				7 sign of y-component
				8 y of y-component
			*/
			match[1] = match[1].toLowerCase();

			if ( match[1].slice( 0, 3 ) === "nth" ) {
				// nth-* requires argument
				if ( !match[3] ) {
					Sizzle.error( match[0] );
				}

				// numeric x and y parameters for Expr.filter.CHILD
				// remember that false/true cast respectively to 0/1
				match[4] = +( match[4] ? match[5] + (match[6] || 1) : 2 * ( match[3] === "even" || match[3] === "odd" ) );
				match[5] = +( ( match[7] + match[8] ) || match[3] === "odd" );

			// other types prohibit arguments
			} else if ( match[3] ) {
				Sizzle.error( match[0] );
			}

			return match;
		},

		"PSEUDO": function( match ) {
			var excess,
				unquoted = !match[6] && match[2];

			if ( matchExpr["CHILD"].test( match[0] ) ) {
				return null;
			}

			// Accept quoted arguments as-is
			if ( match[3] ) {
				match[2] = match[4] || match[5] || "";

			// Strip excess characters from unquoted arguments
			} else if ( unquoted && rpseudo.test( unquoted ) &&
				// Get excess from tokenize (recursively)
				(excess = tokenize( unquoted, true )) &&
				// advance to the next closing parenthesis
				(excess = unquoted.indexOf( ")", unquoted.length - excess ) - unquoted.length) ) {

				// excess is a negative index
				match[0] = match[0].slice( 0, excess );
				match[2] = unquoted.slice( 0, excess );
			}

			// Return only captures needed by the pseudo filter method (type and argument)
			return match.slice( 0, 3 );
		}
	},

	filter: {

		"TAG": function( nodeNameSelector ) {
			var nodeName = nodeNameSelector.replace( runescape, funescape ).toLowerCase();
			return nodeNameSelector === "*" ?
				function() { return true; } :
				function( elem ) {
					return elem.nodeName && elem.nodeName.toLowerCase() === nodeName;
				};
		},

		"CLASS": function( className ) {
			var pattern = classCache[ className + " " ];

			return pattern ||
				(pattern = new RegExp( "(^|" + whitespace + ")" + className + "(" + whitespace + "|$)" )) &&
				classCache( className, function( elem ) {
					return pattern.test( typeof elem.className === "string" && elem.className || typeof elem.getAttribute !== strundefined && elem.getAttribute("class") || "" );
				});
		},

		"ATTR": function( name, operator, check ) {
			return function( elem ) {
				var result = Sizzle.attr( elem, name );

				if ( result == null ) {
					return operator === "!=";
				}
				if ( !operator ) {
					return true;
				}

				result += "";

				return operator === "=" ? result === check :
					operator === "!=" ? result !== check :
					operator === "^=" ? check && result.indexOf( check ) === 0 :
					operator === "*=" ? check && result.indexOf( check ) > -1 :
					operator === "$=" ? check && result.slice( -check.length ) === check :
					operator === "~=" ? ( " " + result + " " ).indexOf( check ) > -1 :
					operator === "|=" ? result === check || result.slice( 0, check.length + 1 ) === check + "-" :
					false;
			};
		},

		"CHILD": function( type, what, argument, first, last ) {
			var simple = type.slice( 0, 3 ) !== "nth",
				forward = type.slice( -4 ) !== "last",
				ofType = what === "of-type";

			return first === 1 && last === 0 ?

				// Shortcut for :nth-*(n)
				function( elem ) {
					return !!elem.parentNode;
				} :

				function( elem, context, xml ) {
					var cache, outerCache, node, diff, nodeIndex, start,
						dir = simple !== forward ? "nextSibling" : "previousSibling",
						parent = elem.parentNode,
						name = ofType && elem.nodeName.toLowerCase(),
						useCache = !xml && !ofType;

					if ( parent ) {

						// :(first|last|only)-(child|of-type)
						if ( simple ) {
							while ( dir ) {
								node = elem;
								while ( (node = node[ dir ]) ) {
									if ( ofType ? node.nodeName.toLowerCase() === name : node.nodeType === 1 ) {
										return false;
									}
								}
								// Reverse direction for :only-* (if we haven't yet done so)
								start = dir = type === "only" && !start && "nextSibling";
							}
							return true;
						}

						start = [ forward ? parent.firstChild : parent.lastChild ];

						// non-xml :nth-child(...) stores cache data on `parent`
						if ( forward && useCache ) {
							// Seek `elem` from a previously-cached index
							outerCache = parent[ expando ] || (parent[ expando ] = {});
							cache = outerCache[ type ] || [];
							nodeIndex = cache[0] === dirruns && cache[1];
							diff = cache[0] === dirruns && cache[2];
							node = nodeIndex && parent.childNodes[ nodeIndex ];

							while ( (node = ++nodeIndex && node && node[ dir ] ||

								// Fallback to seeking `elem` from the start
								(diff = nodeIndex = 0) || start.pop()) ) {

								// When found, cache indexes on `parent` and break
								if ( node.nodeType === 1 && ++diff && node === elem ) {
									outerCache[ type ] = [ dirruns, nodeIndex, diff ];
									break;
								}
							}

						// Use previously-cached element index if available
						} else if ( useCache && (cache = (elem[ expando ] || (elem[ expando ] = {}))[ type ]) && cache[0] === dirruns ) {
							diff = cache[1];

						// xml :nth-child(...) or :nth-last-child(...) or :nth(-last)?-of-type(...)
						} else {
							// Use the same loop as above to seek `elem` from the start
							while ( (node = ++nodeIndex && node && node[ dir ] ||
								(diff = nodeIndex = 0) || start.pop()) ) {

								if ( ( ofType ? node.nodeName.toLowerCase() === name : node.nodeType === 1 ) && ++diff ) {
									// Cache the index of each encountered element
									if ( useCache ) {
										(node[ expando ] || (node[ expando ] = {}))[ type ] = [ dirruns, diff ];
									}

									if ( node === elem ) {
										break;
									}
								}
							}
						}

						// Incorporate the offset, then check against cycle size
						diff -= last;
						return diff === first || ( diff % first === 0 && diff / first >= 0 );
					}
				};
		},

		"PSEUDO": function( pseudo, argument ) {
			// pseudo-class names are case-insensitive
			// http://www.w3.org/TR/selectors/#pseudo-classes
			// Prioritize by case sensitivity in case custom pseudos are added with uppercase letters
			// Remember that setFilters inherits from pseudos
			var args,
				fn = Expr.pseudos[ pseudo ] || Expr.setFilters[ pseudo.toLowerCase() ] ||
					Sizzle.error( "unsupported pseudo: " + pseudo );

			// The user may use createPseudo to indicate that
			// arguments are needed to create the filter function
			// just as Sizzle does
			if ( fn[ expando ] ) {
				return fn( argument );
			}

			// But maintain support for old signatures
			if ( fn.length > 1 ) {
				args = [ pseudo, pseudo, "", argument ];
				return Expr.setFilters.hasOwnProperty( pseudo.toLowerCase() ) ?
					markFunction(function( seed, matches ) {
						var idx,
							matched = fn( seed, argument ),
							i = matched.length;
						while ( i-- ) {
							idx = indexOf.call( seed, matched[i] );
							seed[ idx ] = !( matches[ idx ] = matched[i] );
						}
					}) :
					function( elem ) {
						return fn( elem, 0, args );
					};
			}

			return fn;
		}
	},

	pseudos: {
		// Potentially complex pseudos
		"not": markFunction(function( selector ) {
			// Trim the selector passed to compile
			// to avoid treating leading and trailing
			// spaces as combinators
			var input = [],
				results = [],
				matcher = compile( selector.replace( rtrim, "$1" ) );

			return matcher[ expando ] ?
				markFunction(function( seed, matches, context, xml ) {
					var elem,
						unmatched = matcher( seed, null, xml, [] ),
						i = seed.length;

					// Match elements unmatched by `matcher`
					while ( i-- ) {
						if ( (elem = unmatched[i]) ) {
							seed[i] = !(matches[i] = elem);
						}
					}
				}) :
				function( elem, context, xml ) {
					input[0] = elem;
					matcher( input, null, xml, results );
					return !results.pop();
				};
		}),

		"has": markFunction(function( selector ) {
			return function( elem ) {
				return Sizzle( selector, elem ).length > 0;
			};
		}),

		"contains": markFunction(function( text ) {
			return function( elem ) {
				return ( elem.textContent || elem.innerText || getText( elem ) ).indexOf( text ) > -1;
			};
		}),

		// "Whether an element is represented by a :lang() selector
		// is based solely on the element's language value
		// being equal to the identifier C,
		// or beginning with the identifier C immediately followed by "-".
		// The matching of C against the element's language value is performed case-insensitively.
		// The identifier C does not have to be a valid language name."
		// http://www.w3.org/TR/selectors/#lang-pseudo
		"lang": markFunction( function( lang ) {
			// lang value must be a valid identifier
			if ( !ridentifier.test(lang || "") ) {
				Sizzle.error( "unsupported lang: " + lang );
			}
			lang = lang.replace( runescape, funescape ).toLowerCase();
			return function( elem ) {
				var elemLang;
				do {
					if ( (elemLang = documentIsHTML ?
						elem.lang :
						elem.getAttribute("xml:lang") || elem.getAttribute("lang")) ) {

						elemLang = elemLang.toLowerCase();
						return elemLang === lang || elemLang.indexOf( lang + "-" ) === 0;
					}
				} while ( (elem = elem.parentNode) && elem.nodeType === 1 );
				return false;
			};
		}),

		// Miscellaneous
		"target": function( elem ) {
			var hash = window.location && window.location.hash;
			return hash && hash.slice( 1 ) === elem.id;
		},

		"root": function( elem ) {
			return elem === docElem;
		},

		"focus": function( elem ) {
			return elem === document.activeElement && (!document.hasFocus || document.hasFocus()) && !!(elem.type || elem.href || ~elem.tabIndex);
		},

		// Boolean properties
		"enabled": function( elem ) {
			return elem.disabled === false;
		},

		"disabled": function( elem ) {
			return elem.disabled === true;
		},

		"checked": function( elem ) {
			// In CSS3, :checked should return both checked and selected elements
			// http://www.w3.org/TR/2011/REC-css3-selectors-20110929/#checked
			var nodeName = elem.nodeName.toLowerCase();
			return (nodeName === "input" && !!elem.checked) || (nodeName === "option" && !!elem.selected);
		},

		"selected": function( elem ) {
			// Accessing this property makes selected-by-default
			// options in Safari work properly
			if ( elem.parentNode ) {
				elem.parentNode.selectedIndex;
			}

			return elem.selected === true;
		},

		// Contents
		"empty": function( elem ) {
			// http://www.w3.org/TR/selectors/#empty-pseudo
			// :empty is negated by element (1) or content nodes (text: 3; cdata: 4; entity ref: 5),
			//   but not by others (comment: 8; processing instruction: 7; etc.)
			// nodeType < 6 works because attributes (2) do not appear as children
			for ( elem = elem.firstChild; elem; elem = elem.nextSibling ) {
				if ( elem.nodeType < 6 ) {
					return false;
				}
			}
			return true;
		},

		"parent": function( elem ) {
			return !Expr.pseudos["empty"]( elem );
		},

		// Element/input types
		"header": function( elem ) {
			return rheader.test( elem.nodeName );
		},

		"input": function( elem ) {
			return rinputs.test( elem.nodeName );
		},

		"button": function( elem ) {
			var name = elem.nodeName.toLowerCase();
			return name === "input" && elem.type === "button" || name === "button";
		},

		"text": function( elem ) {
			var attr;
			return elem.nodeName.toLowerCase() === "input" &&
				elem.type === "text" &&

				// Support: IE<8
				// New HTML5 attribute values (e.g., "search") appear with elem.type === "text"
				( (attr = elem.getAttribute("type")) == null || attr.toLowerCase() === "text" );
		},

		// Position-in-collection
		"first": createPositionalPseudo(function() {
			return [ 0 ];
		}),

		"last": createPositionalPseudo(function( matchIndexes, length ) {
			return [ length - 1 ];
		}),

		"eq": createPositionalPseudo(function( matchIndexes, length, argument ) {
			return [ argument < 0 ? argument + length : argument ];
		}),

		"even": createPositionalPseudo(function( matchIndexes, length ) {
			var i = 0;
			for ( ; i < length; i += 2 ) {
				matchIndexes.push( i );
			}
			return matchIndexes;
		}),

		"odd": createPositionalPseudo(function( matchIndexes, length ) {
			var i = 1;
			for ( ; i < length; i += 2 ) {
				matchIndexes.push( i );
			}
			return matchIndexes;
		}),

		"lt": createPositionalPseudo(function( matchIndexes, length, argument ) {
			var i = argument < 0 ? argument + length : argument;
			for ( ; --i >= 0; ) {
				matchIndexes.push( i );
			}
			return matchIndexes;
		}),

		"gt": createPositionalPseudo(function( matchIndexes, length, argument ) {
			var i = argument < 0 ? argument + length : argument;
			for ( ; ++i < length; ) {
				matchIndexes.push( i );
			}
			return matchIndexes;
		})
	}
};

Expr.pseudos["nth"] = Expr.pseudos["eq"];

// Add button/input type pseudos
for ( i in { radio: true, checkbox: true, file: true, password: true, image: true } ) {
	Expr.pseudos[ i ] = createInputPseudo( i );
}
for ( i in { submit: true, reset: true } ) {
	Expr.pseudos[ i ] = createButtonPseudo( i );
}

// Easy API for creating new setFilters
function setFilters() {}
setFilters.prototype = Expr.filters = Expr.pseudos;
Expr.setFilters = new setFilters();

tokenize = Sizzle.tokenize = function( selector, parseOnly ) {
	var matched, match, tokens, type,
		soFar, groups, preFilters,
		cached = tokenCache[ selector + " " ];

	if ( cached ) {
		return parseOnly ? 0 : cached.slice( 0 );
	}

	soFar = selector;
	groups = [];
	preFilters = Expr.preFilter;

	while ( soFar ) {

		// Comma and first run
		if ( !matched || (match = rcomma.exec( soFar )) ) {
			if ( match ) {
				// Don't consume trailing commas as valid
				soFar = soFar.slice( match[0].length ) || soFar;
			}
			groups.push( (tokens = []) );
		}

		matched = false;

		// Combinators
		if ( (match = rcombinators.exec( soFar )) ) {
			matched = match.shift();
			tokens.push({
				value: matched,
				// Cast descendant combinators to space
				type: match[0].replace( rtrim, " " )
			});
			soFar = soFar.slice( matched.length );
		}

		// Filters
		for ( type in Expr.filter ) {
			if ( (match = matchExpr[ type ].exec( soFar )) && (!preFilters[ type ] ||
				(match = preFilters[ type ]( match ))) ) {
				matched = match.shift();
				tokens.push({
					value: matched,
					type: type,
					matches: match
				});
				soFar = soFar.slice( matched.length );
			}
		}

		if ( !matched ) {
			break;
		}
	}

	// Return the length of the invalid excess
	// if we're just parsing
	// Otherwise, throw an error or return tokens
	return parseOnly ?
		soFar.length :
		soFar ?
			Sizzle.error( selector ) :
			// Cache the tokens
			tokenCache( selector, groups ).slice( 0 );
};

function toSelector( tokens ) {
	var i = 0,
		len = tokens.length,
		selector = "";
	for ( ; i < len; i++ ) {
		selector += tokens[i].value;
	}
	return selector;
}

function addCombinator( matcher, combinator, base ) {
	var dir = combinator.dir,
		checkNonElements = base && dir === "parentNode",
		doneName = done++;

	return combinator.first ?
		// Check against closest ancestor/preceding element
		function( elem, context, xml ) {
			while ( (elem = elem[ dir ]) ) {
				if ( elem.nodeType === 1 || checkNonElements ) {
					return matcher( elem, context, xml );
				}
			}
		} :

		// Check against all ancestor/preceding elements
		function( elem, context, xml ) {
			var oldCache, outerCache,
				newCache = [ dirruns, doneName ];

			// We can't set arbitrary data on XML nodes, so they don't benefit from dir caching
			if ( xml ) {
				while ( (elem = elem[ dir ]) ) {
					if ( elem.nodeType === 1 || checkNonElements ) {
						if ( matcher( elem, context, xml ) ) {
							return true;
						}
					}
				}
			} else {
				while ( (elem = elem[ dir ]) ) {
					if ( elem.nodeType === 1 || checkNonElements ) {
						outerCache = elem[ expando ] || (elem[ expando ] = {});
						if ( (oldCache = outerCache[ dir ]) &&
							oldCache[ 0 ] === dirruns && oldCache[ 1 ] === doneName ) {

							// Assign to newCache so results back-propagate to previous elements
							return (newCache[ 2 ] = oldCache[ 2 ]);
						} else {
							// Reuse newcache so results back-propagate to previous elements
							outerCache[ dir ] = newCache;

							// A match means we're done; a fail means we have to keep checking
							if ( (newCache[ 2 ] = matcher( elem, context, xml )) ) {
								return true;
							}
						}
					}
				}
			}
		};
}

function elementMatcher( matchers ) {
	return matchers.length > 1 ?
		function( elem, context, xml ) {
			var i = matchers.length;
			while ( i-- ) {
				if ( !matchers[i]( elem, context, xml ) ) {
					return false;
				}
			}
			return true;
		} :
		matchers[0];
}

function multipleContexts( selector, contexts, results ) {
	var i = 0,
		len = contexts.length;
	for ( ; i < len; i++ ) {
		Sizzle( selector, contexts[i], results );
	}
	return results;
}

function condense( unmatched, map, filter, context, xml ) {
	var elem,
		newUnmatched = [],
		i = 0,
		len = unmatched.length,
		mapped = map != null;

	for ( ; i < len; i++ ) {
		if ( (elem = unmatched[i]) ) {
			if ( !filter || filter( elem, context, xml ) ) {
				newUnmatched.push( elem );
				if ( mapped ) {
					map.push( i );
				}
			}
		}
	}

	return newUnmatched;
}

function setMatcher( preFilter, selector, matcher, postFilter, postFinder, postSelector ) {
	if ( postFilter && !postFilter[ expando ] ) {
		postFilter = setMatcher( postFilter );
	}
	if ( postFinder && !postFinder[ expando ] ) {
		postFinder = setMatcher( postFinder, postSelector );
	}
	return markFunction(function( seed, results, context, xml ) {
		var temp, i, elem,
			preMap = [],
			postMap = [],
			preexisting = results.length,

			// Get initial elements from seed or context
			elems = seed || multipleContexts( selector || "*", context.nodeType ? [ context ] : context, [] ),

			// Prefilter to get matcher input, preserving a map for seed-results synchronization
			matcherIn = preFilter && ( seed || !selector ) ?
				condense( elems, preMap, preFilter, context, xml ) :
				elems,

			matcherOut = matcher ?
				// If we have a postFinder, or filtered seed, or non-seed postFilter or preexisting results,
				postFinder || ( seed ? preFilter : preexisting || postFilter ) ?

					// ...intermediate processing is necessary
					[] :

					// ...otherwise use results directly
					results :
				matcherIn;

		// Find primary matches
		if ( matcher ) {
			matcher( matcherIn, matcherOut, context, xml );
		}

		// Apply postFilter
		if ( postFilter ) {
			temp = condense( matcherOut, postMap );
			postFilter( temp, [], context, xml );

			// Un-match failing elements by moving them back to matcherIn
			i = temp.length;
			while ( i-- ) {
				if ( (elem = temp[i]) ) {
					matcherOut[ postMap[i] ] = !(matcherIn[ postMap[i] ] = elem);
				}
			}
		}

		if ( seed ) {
			if ( postFinder || preFilter ) {
				if ( postFinder ) {
					// Get the final matcherOut by condensing this intermediate into postFinder contexts
					temp = [];
					i = matcherOut.length;
					while ( i-- ) {
						if ( (elem = matcherOut[i]) ) {
							// Restore matcherIn since elem is not yet a final match
							temp.push( (matcherIn[i] = elem) );
						}
					}
					postFinder( null, (matcherOut = []), temp, xml );
				}

				// Move matched elements from seed to results to keep them synchronized
				i = matcherOut.length;
				while ( i-- ) {
					if ( (elem = matcherOut[i]) &&
						(temp = postFinder ? indexOf.call( seed, elem ) : preMap[i]) > -1 ) {

						seed[temp] = !(results[temp] = elem);
					}
				}
			}

		// Add elements to results, through postFinder if defined
		} else {
			matcherOut = condense(
				matcherOut === results ?
					matcherOut.splice( preexisting, matcherOut.length ) :
					matcherOut
			);
			if ( postFinder ) {
				postFinder( null, results, matcherOut, xml );
			} else {
				push.apply( results, matcherOut );
			}
		}
	});
}

function matcherFromTokens( tokens ) {
	var checkContext, matcher, j,
		len = tokens.length,
		leadingRelative = Expr.relative[ tokens[0].type ],
		implicitRelative = leadingRelative || Expr.relative[" "],
		i = leadingRelative ? 1 : 0,

		// The foundational matcher ensures that elements are reachable from top-level context(s)
		matchContext = addCombinator( function( elem ) {
			return elem === checkContext;
		}, implicitRelative, true ),
		matchAnyContext = addCombinator( function( elem ) {
			return indexOf.call( checkContext, elem ) > -1;
		}, implicitRelative, true ),
		matchers = [ function( elem, context, xml ) {
			return ( !leadingRelative && ( xml || context !== outermostContext ) ) || (
				(checkContext = context).nodeType ?
					matchContext( elem, context, xml ) :
					matchAnyContext( elem, context, xml ) );
		} ];

	for ( ; i < len; i++ ) {
		if ( (matcher = Expr.relative[ tokens[i].type ]) ) {
			matchers = [ addCombinator(elementMatcher( matchers ), matcher) ];
		} else {
			matcher = Expr.filter[ tokens[i].type ].apply( null, tokens[i].matches );

			// Return special upon seeing a positional matcher
			if ( matcher[ expando ] ) {
				// Find the next relative operator (if any) for proper handling
				j = ++i;
				for ( ; j < len; j++ ) {
					if ( Expr.relative[ tokens[j].type ] ) {
						break;
					}
				}
				return setMatcher(
					i > 1 && elementMatcher( matchers ),
					i > 1 && toSelector(
						// If the preceding token was a descendant combinator, insert an implicit any-element `*`
						tokens.slice( 0, i - 1 ).concat({ value: tokens[ i - 2 ].type === " " ? "*" : "" })
					).replace( rtrim, "$1" ),
					matcher,
					i < j && matcherFromTokens( tokens.slice( i, j ) ),
					j < len && matcherFromTokens( (tokens = tokens.slice( j )) ),
					j < len && toSelector( tokens )
				);
			}
			matchers.push( matcher );
		}
	}

	return elementMatcher( matchers );
}

function matcherFromGroupMatchers( elementMatchers, setMatchers ) {
	var bySet = setMatchers.length > 0,
		byElement = elementMatchers.length > 0,
		superMatcher = function( seed, context, xml, results, outermost ) {
			var elem, j, matcher,
				matchedCount = 0,
				i = "0",
				unmatched = seed && [],
				setMatched = [],
				contextBackup = outermostContext,
				// We must always have either seed elements or outermost context
				elems = seed || byElement && Expr.find["TAG"]( "*", outermost ),
				// Use integer dirruns iff this is the outermost matcher
				dirrunsUnique = (dirruns += contextBackup == null ? 1 : Math.random() || 0.1),
				len = elems.length;

			if ( outermost ) {
				outermostContext = context !== document && context;
			}

			// Add elements passing elementMatchers directly to results
			// Keep `i` a string if there are no elements so `matchedCount` will be "00" below
			// Support: IE<9, Safari
			// Tolerate NodeList properties (IE: "length"; Safari: <number>) matching elements by id
			for ( ; i !== len && (elem = elems[i]) != null; i++ ) {
				if ( byElement && elem ) {
					j = 0;
					while ( (matcher = elementMatchers[j++]) ) {
						if ( matcher( elem, context, xml ) ) {
							results.push( elem );
							break;
						}
					}
					if ( outermost ) {
						dirruns = dirrunsUnique;
					}
				}

				// Track unmatched elements for set filters
				if ( bySet ) {
					// They will have gone through all possible matchers
					if ( (elem = !matcher && elem) ) {
						matchedCount--;
					}

					// Lengthen the array for every element, matched or not
					if ( seed ) {
						unmatched.push( elem );
					}
				}
			}

			// Apply set filters to unmatched elements
			matchedCount += i;
			if ( bySet && i !== matchedCount ) {
				j = 0;
				while ( (matcher = setMatchers[j++]) ) {
					matcher( unmatched, setMatched, context, xml );
				}

				if ( seed ) {
					// Reintegrate element matches to eliminate the need for sorting
					if ( matchedCount > 0 ) {
						while ( i-- ) {
							if ( !(unmatched[i] || setMatched[i]) ) {
								setMatched[i] = pop.call( results );
							}
						}
					}

					// Discard index placeholder values to get only actual matches
					setMatched = condense( setMatched );
				}

				// Add matches to results
				push.apply( results, setMatched );

				// Seedless set matches succeeding multiple successful matchers stipulate sorting
				if ( outermost && !seed && setMatched.length > 0 &&
					( matchedCount + setMatchers.length ) > 1 ) {

					Sizzle.uniqueSort( results );
				}
			}

			// Override manipulation of globals by nested matchers
			if ( outermost ) {
				dirruns = dirrunsUnique;
				outermostContext = contextBackup;
			}

			return unmatched;
		};

	return bySet ?
		markFunction( superMatcher ) :
		superMatcher;
}

compile = Sizzle.compile = function( selector, match /* Internal Use Only */ ) {
	var i,
		setMatchers = [],
		elementMatchers = [],
		cached = compilerCache[ selector + " " ];

	if ( !cached ) {
		// Generate a function of recursive functions that can be used to check each element
		if ( !match ) {
			match = tokenize( selector );
		}
		i = match.length;
		while ( i-- ) {
			cached = matcherFromTokens( match[i] );
			if ( cached[ expando ] ) {
				setMatchers.push( cached );
			} else {
				elementMatchers.push( cached );
			}
		}

		// Cache the compiled function
		cached = compilerCache( selector, matcherFromGroupMatchers( elementMatchers, setMatchers ) );

		// Save selector and tokenization
		cached.selector = selector;
	}
	return cached;
};

/**
 * A low-level selection function that works with Sizzle's compiled
 *  selector functions
 * @param {String|Function} selector A selector or a pre-compiled
 *  selector function built with Sizzle.compile
 * @param {Element} context
 * @param {Array} [results]
 * @param {Array} [seed] A set of elements to match against
 */
select = Sizzle.select = function( selector, context, results, seed ) {
	var i, tokens, token, type, find,
		compiled = typeof selector === "function" && selector,
		match = !seed && tokenize( (selector = compiled.selector || selector) );

	results = results || [];

	// Try to minimize operations if there is no seed and only one group
	if ( match.length === 1 ) {

		// Take a shortcut and set the context if the root selector is an ID
		tokens = match[0] = match[0].slice( 0 );
		if ( tokens.length > 2 && (token = tokens[0]).type === "ID" &&
				support.getById && context.nodeType === 9 && documentIsHTML &&
				Expr.relative[ tokens[1].type ] ) {

			context = ( Expr.find["ID"]( token.matches[0].replace(runescape, funescape), context ) || [] )[0];
			if ( !context ) {
				return results;

			// Precompiled matchers will still verify ancestry, so step up a level
			} else if ( compiled ) {
				context = context.parentNode;
			}

			selector = selector.slice( tokens.shift().value.length );
		}

		// Fetch a seed set for right-to-left matching
		i = matchExpr["needsContext"].test( selector ) ? 0 : tokens.length;
		while ( i-- ) {
			token = tokens[i];

			// Abort if we hit a combinator
			if ( Expr.relative[ (type = token.type) ] ) {
				break;
			}
			if ( (find = Expr.find[ type ]) ) {
				// Search, expanding context for leading sibling combinators
				if ( (seed = find(
					token.matches[0].replace( runescape, funescape ),
					rsibling.test( tokens[0].type ) && testContext( context.parentNode ) || context
				)) ) {

					// If seed is empty or no tokens remain, we can return early
					tokens.splice( i, 1 );
					selector = seed.length && toSelector( tokens );
					if ( !selector ) {
						push.apply( results, seed );
						return results;
					}

					break;
				}
			}
		}
	}

	// Compile and execute a filtering function if one is not provided
	// Provide `match` to avoid retokenization if we modified the selector above
	( compiled || compile( selector, match ) )(
		seed,
		context,
		!documentIsHTML,
		results,
		rsibling.test( selector ) && testContext( context.parentNode ) || context
	);
	return results;
};

// One-time assignments

// Sort stability
support.sortStable = expando.split("").sort( sortOrder ).join("") === expando;

// Support: Chrome<14
// Always assume duplicates if they aren't passed to the comparison function
support.detectDuplicates = !!hasDuplicate;

// Initialize against the default document
setDocument();

// Support: Webkit<537.32 - Safari 6.0.3/Chrome 25 (fixed in Chrome 27)
// Detached nodes confoundingly follow *each other*
support.sortDetached = assert(function( div1 ) {
	// Should return 1, but returns 4 (following)
	return div1.compareDocumentPosition( document.createElement("div") ) & 1;
});

// Support: IE<8
// Prevent attribute/property "interpolation"
// http://msdn.microsoft.com/en-us/library/ms536429%28VS.85%29.aspx
if ( !assert(function( div ) {
	div.innerHTML = "<a href='#'></a>";
	return div.firstChild.getAttribute("href") === "#" ;
}) ) {
	addHandle( "type|href|height|width", function( elem, name, isXML ) {
		if ( !isXML ) {
			return elem.getAttribute( name, name.toLowerCase() === "type" ? 1 : 2 );
		}
	});
}

// Support: IE<9
// Use defaultValue in place of getAttribute("value")
if ( !support.attributes || !assert(function( div ) {
	div.innerHTML = "<input/>";
	div.firstChild.setAttribute( "value", "" );
	return div.firstChild.getAttribute( "value" ) === "";
}) ) {
	addHandle( "value", function( elem, name, isXML ) {
		if ( !isXML && elem.nodeName.toLowerCase() === "input" ) {
			return elem.defaultValue;
		}
	});
}

// Support: IE<9
// Use getAttributeNode to fetch booleans when getAttribute lies
if ( !assert(function( div ) {
	return div.getAttribute("disabled") == null;
}) ) {
	addHandle( booleans, function( elem, name, isXML ) {
		var val;
		if ( !isXML ) {
			return elem[ name ] === true ? name.toLowerCase() :
					(val = elem.getAttributeNode( name )) && val.specified ?
					val.value :
				null;
		}
	});
}

return Sizzle;

})( window );



jQuery.find = Sizzle;
jQuery.expr = Sizzle.selectors;
jQuery.expr[":"] = jQuery.expr.pseudos;
jQuery.unique = Sizzle.uniqueSort;
jQuery.text = Sizzle.getText;
jQuery.isXMLDoc = Sizzle.isXML;
jQuery.contains = Sizzle.contains;



var rneedsContext = jQuery.expr.match.needsContext;

var rsingleTag = (/^<(\w+)\s*\/?>(?:<\/\1>|)$/);



var risSimple = /^.[^:#\[\.,]*$/;

// Implement the identical functionality for filter and not
function winnow( elements, qualifier, not ) {
	if ( jQuery.isFunction( qualifier ) ) {
		return jQuery.grep( elements, function( elem, i ) {
			/* jshint -W018 */
			return !!qualifier.call( elem, i, elem ) !== not;
		});

	}

	if ( qualifier.nodeType ) {
		return jQuery.grep( elements, function( elem ) {
			return ( elem === qualifier ) !== not;
		});

	}

	if ( typeof qualifier === "string" ) {
		if ( risSimple.test( qualifier ) ) {
			return jQuery.filter( qualifier, elements, not );
		}

		qualifier = jQuery.filter( qualifier, elements );
	}

	return jQuery.grep( elements, function( elem ) {
		return ( jQuery.inArray( elem, qualifier ) >= 0 ) !== not;
	});
}

jQuery.filter = function( expr, elems, not ) {
	var elem = elems[ 0 ];

	if ( not ) {
		expr = ":not(" + expr + ")";
	}

	return elems.length === 1 && elem.nodeType === 1 ?
		jQuery.find.matchesSelector( elem, expr ) ? [ elem ] : [] :
		jQuery.find.matches( expr, jQuery.grep( elems, function( elem ) {
			return elem.nodeType === 1;
		}));
};

jQuery.fn.extend({
	find: function( selector ) {
		var i,
			ret = [],
			self = this,
			len = self.length;

		if ( typeof selector !== "string" ) {
			return this.pushStack( jQuery( selector ).filter(function() {
				for ( i = 0; i < len; i++ ) {
					if ( jQuery.contains( self[ i ], this ) ) {
						return true;
					}
				}
			}) );
		}

		for ( i = 0; i < len; i++ ) {
			jQuery.find( selector, self[ i ], ret );
		}

		// Needed because $( selector, context ) becomes $( context ).find( selector )
		ret = this.pushStack( len > 1 ? jQuery.unique( ret ) : ret );
		ret.selector = this.selector ? this.selector + " " + selector : selector;
		return ret;
	},
	filter: function( selector ) {
		return this.pushStack( winnow(this, selector || [], false) );
	},
	not: function( selector ) {
		return this.pushStack( winnow(this, selector || [], true) );
	},
	is: function( selector ) {
		return !!winnow(
			this,

			// If this is a positional/relative selector, check membership in the returned set
			// so $("p:first").is("p:last") won't return true for a doc with two "p".
			typeof selector === "string" && rneedsContext.test( selector ) ?
				jQuery( selector ) :
				selector || [],
			false
		).length;
	}
});


// Initialize a jQuery object


// A central reference to the root jQuery(document)
var rootjQuery,

	// Use the correct document accordingly with window argument (sandbox)
	document = window.document,

	// A simple way to check for HTML strings
	// Prioritize #id over <tag> to avoid XSS via location.hash (#9521)
	// Strict HTML recognition (#11290: must start with <)
	rquickExpr = /^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]*))$/,

	init = jQuery.fn.init = function( selector, context ) {
		var match, elem;

		// HANDLE: $(""), $(null), $(undefined), $(false)
		if ( !selector ) {
			return this;
		}

		// Handle HTML strings
		if ( typeof selector === "string" ) {
			if ( selector.charAt(0) === "<" && selector.charAt( selector.length - 1 ) === ">" && selector.length >= 3 ) {
				// Assume that strings that start and end with <> are HTML and skip the regex check
				match = [ null, selector, null ];

			} else {
				match = rquickExpr.exec( selector );
			}

			// Match html or make sure no context is specified for #id
			if ( match && (match[1] || !context) ) {

				// HANDLE: $(html) -> $(array)
				if ( match[1] ) {
					context = context instanceof jQuery ? context[0] : context;

					// scripts is true for back-compat
					// Intentionally let the error be thrown if parseHTML is not present
					jQuery.merge( this, jQuery.parseHTML(
						match[1],
						context && context.nodeType ? context.ownerDocument || context : document,
						true
					) );

					// HANDLE: $(html, props)
					if ( rsingleTag.test( match[1] ) && jQuery.isPlainObject( context ) ) {
						for ( match in context ) {
							// Properties of context are called as methods if possible
							if ( jQuery.isFunction( this[ match ] ) ) {
								this[ match ]( context[ match ] );

							// ...and otherwise set as attributes
							} else {
								this.attr( match, context[ match ] );
							}
						}
					}

					return this;

				// HANDLE: $(#id)
				} else {
					elem = document.getElementById( match[2] );

					// Check parentNode to catch when Blackberry 4.6 returns
					// nodes that are no longer in the document #6963
					if ( elem && elem.parentNode ) {
						// Handle the case where IE and Opera return items
						// by name instead of ID
						if ( elem.id !== match[2] ) {
							return rootjQuery.find( selector );
						}

						// Otherwise, we inject the element directly into the jQuery object
						this.length = 1;
						this[0] = elem;
					}

					this.context = document;
					this.selector = selector;
					return this;
				}

			// HANDLE: $(expr, $(...))
			} else if ( !context || context.jquery ) {
				return ( context || rootjQuery ).find( selector );

			// HANDLE: $(expr, context)
			// (which is just equivalent to: $(context).find(expr)
			} else {
				return this.constructor( context ).find( selector );
			}

		// HANDLE: $(DOMElement)
		} else if ( selector.nodeType ) {
			this.context = this[0] = selector;
			this.length = 1;
			return this;

		// HANDLE: $(function)
		// Shortcut for document ready
		} else if ( jQuery.isFunction( selector ) ) {
			return typeof rootjQuery.ready !== "undefined" ?
				rootjQuery.ready( selector ) :
				// Execute immediately if ready is not present
				selector( jQuery );
		}

		if ( selector.selector !== undefined ) {
			this.selector = selector.selector;
			this.context = selector.context;
		}

		return jQuery.makeArray( selector, this );
	};

// Give the init function the jQuery prototype for later instantiation
init.prototype = jQuery.fn;

// Initialize central reference
rootjQuery = jQuery( document );


var rparentsprev = /^(?:parents|prev(?:Until|All))/,
	// methods guaranteed to produce a unique set when starting from a unique set
	guaranteedUnique = {
		children: true,
		contents: true,
		next: true,
		prev: true
	};

jQuery.extend({
	dir: function( elem, dir, until ) {
		var matched = [],
			cur = elem[ dir ];

		while ( cur && cur.nodeType !== 9 && (until === undefined || cur.nodeType !== 1 || !jQuery( cur ).is( until )) ) {
			if ( cur.nodeType === 1 ) {
				matched.push( cur );
			}
			cur = cur[dir];
		}
		return matched;
	},

	sibling: function( n, elem ) {
		var r = [];

		for ( ; n; n = n.nextSibling ) {
			if ( n.nodeType === 1 && n !== elem ) {
				r.push( n );
			}
		}

		return r;
	}
});

jQuery.fn.extend({
	has: function( target ) {
		var i,
			targets = jQuery( target, this ),
			len = targets.length;

		return this.filter(function() {
			for ( i = 0; i < len; i++ ) {
				if ( jQuery.contains( this, targets[i] ) ) {
					return true;
				}
			}
		});
	},

	closest: function( selectors, context ) {
		var cur,
			i = 0,
			l = this.length,
			matched = [],
			pos = rneedsContext.test( selectors ) || typeof selectors !== "string" ?
				jQuery( selectors, context || this.context ) :
				0;

		for ( ; i < l; i++ ) {
			for ( cur = this[i]; cur && cur !== context; cur = cur.parentNode ) {
				// Always skip document fragments
				if ( cur.nodeType < 11 && (pos ?
					pos.index(cur) > -1 :

					// Don't pass non-elements to Sizzle
					cur.nodeType === 1 &&
						jQuery.find.matchesSelector(cur, selectors)) ) {

					matched.push( cur );
					break;
				}
			}
		}

		return this.pushStack( matched.length > 1 ? jQuery.unique( matched ) : matched );
	},

	// Determine the position of an element within
	// the matched set of elements
	index: function( elem ) {

		// No argument, return index in parent
		if ( !elem ) {
			return ( this[0] && this[0].parentNode ) ? this.first().prevAll().length : -1;
		}

		// index in selector
		if ( typeof elem === "string" ) {
			return jQuery.inArray( this[0], jQuery( elem ) );
		}

		// Locate the position of the desired element
		return jQuery.inArray(
			// If it receives a jQuery object, the first element is used
			elem.jquery ? elem[0] : elem, this );
	},

	add: function( selector, context ) {
		return this.pushStack(
			jQuery.unique(
				jQuery.merge( this.get(), jQuery( selector, context ) )
			)
		);
	},

	addBack: function( selector ) {
		return this.add( selector == null ?
			this.prevObject : this.prevObject.filter(selector)
		);
	}
});

function sibling( cur, dir ) {
	do {
		cur = cur[ dir ];
	} while ( cur && cur.nodeType !== 1 );

	return cur;
}

jQuery.each({
	parent: function( elem ) {
		var parent = elem.parentNode;
		return parent && parent.nodeType !== 11 ? parent : null;
	},
	parents: function( elem ) {
		return jQuery.dir( elem, "parentNode" );
	},
	parentsUntil: function( elem, i, until ) {
		return jQuery.dir( elem, "parentNode", until );
	},
	next: function( elem ) {
		return sibling( elem, "nextSibling" );
	},
	prev: function( elem ) {
		return sibling( elem, "previousSibling" );
	},
	nextAll: function( elem ) {
		return jQuery.dir( elem, "nextSibling" );
	},
	prevAll: function( elem ) {
		return jQuery.dir( elem, "previousSibling" );
	},
	nextUntil: function( elem, i, until ) {
		return jQuery.dir( elem, "nextSibling", until );
	},
	prevUntil: function( elem, i, until ) {
		return jQuery.dir( elem, "previousSibling", until );
	},
	siblings: function( elem ) {
		return jQuery.sibling( ( elem.parentNode || {} ).firstChild, elem );
	},
	children: function( elem ) {
		return jQuery.sibling( elem.firstChild );
	},
	contents: function( elem ) {
		return jQuery.nodeName( elem, "iframe" ) ?
			elem.contentDocument || elem.contentWindow.document :
			jQuery.merge( [], elem.childNodes );
	}
}, function( name, fn ) {
	jQuery.fn[ name ] = function( until, selector ) {
		var ret = jQuery.map( this, fn, until );

		if ( name.slice( -5 ) !== "Until" ) {
			selector = until;
		}

		if ( selector && typeof selector === "string" ) {
			ret = jQuery.filter( selector, ret );
		}

		if ( this.length > 1 ) {
			// Remove duplicates
			if ( !guaranteedUnique[ name ] ) {
				ret = jQuery.unique( ret );
			}

			// Reverse order for parents* and prev-derivatives
			if ( rparentsprev.test( name ) ) {
				ret = ret.reverse();
			}
		}

		return this.pushStack( ret );
	};
});
var rnotwhite = (/\S+/g);



// String to Object options format cache
var optionsCache = {};

// Convert String-formatted options into Object-formatted ones and store in cache
function createOptions( options ) {
	var object = optionsCache[ options ] = {};
	jQuery.each( options.match( rnotwhite ) || [], function( _, flag ) {
		object[ flag ] = true;
	});
	return object;
}

/*
 * Create a callback list using the following parameters:
 *
 *	options: an optional list of space-separated options that will change how
 *			the callback list behaves or a more traditional option object
 *
 * By default a callback list will act like an event callback list and can be
 * "fired" multiple times.
 *
 * Possible options:
 *
 *	once:			will ensure the callback list can only be fired once (like a Deferred)
 *
 *	memory:			will keep track of previous values and will call any callback added
 *					after the list has been fired right away with the latest "memorized"
 *					values (like a Deferred)
 *
 *	unique:			will ensure a callback can only be added once (no duplicate in the list)
 *
 *	stopOnFalse:	interrupt callings when a callback returns false
 *
 */
jQuery.Callbacks = function( options ) {

	// Convert options from String-formatted to Object-formatted if needed
	// (we check in cache first)
	options = typeof options === "string" ?
		( optionsCache[ options ] || createOptions( options ) ) :
		jQuery.extend( {}, options );

	var // Flag to know if list is currently firing
		firing,
		// Last fire value (for non-forgettable lists)
		memory,
		// Flag to know if list was already fired
		fired,
		// End of the loop when firing
		firingLength,
		// Index of currently firing callback (modified by remove if needed)
		firingIndex,
		// First callback to fire (used internally by add and fireWith)
		firingStart,
		// Actual callback list
		list = [],
		// Stack of fire calls for repeatable lists
		stack = !options.once && [],
		// Fire callbacks
		fire = function( data ) {
			memory = options.memory && data;
			fired = true;
			firingIndex = firingStart || 0;
			firingStart = 0;
			firingLength = list.length;
			firing = true;
			for ( ; list && firingIndex < firingLength; firingIndex++ ) {
				if ( list[ firingIndex ].apply( data[ 0 ], data[ 1 ] ) === false && options.stopOnFalse ) {
					memory = false; // To prevent further calls using add
					break;
				}
			}
			firing = false;
			if ( list ) {
				if ( stack ) {
					if ( stack.length ) {
						fire( stack.shift() );
					}
				} else if ( memory ) {
					list = [];
				} else {
					self.disable();
				}
			}
		},
		// Actual Callbacks object
		self = {
			// Add a callback or a collection of callbacks to the list
			add: function() {
				if ( list ) {
					// First, we save the current length
					var start = list.length;
					(function add( args ) {
						jQuery.each( args, function( _, arg ) {
							var type = jQuery.type( arg );
							if ( type === "function" ) {
								if ( !options.unique || !self.has( arg ) ) {
									list.push( arg );
								}
							} else if ( arg && arg.length && type !== "string" ) {
								// Inspect recursively
								add( arg );
							}
						});
					})( arguments );
					// Do we need to add the callbacks to the
					// current firing batch?
					if ( firing ) {
						firingLength = list.length;
					// With memory, if we're not firing then
					// we should call right away
					} else if ( memory ) {
						firingStart = start;
						fire( memory );
					}
				}
				return this;
			},
			// Remove a callback from the list
			remove: function() {
				if ( list ) {
					jQuery.each( arguments, function( _, arg ) {
						var index;
						while ( ( index = jQuery.inArray( arg, list, index ) ) > -1 ) {
							list.splice( index, 1 );
							// Handle firing indexes
							if ( firing ) {
								if ( index <= firingLength ) {
									firingLength--;
								}
								if ( index <= firingIndex ) {
									firingIndex--;
								}
							}
						}
					});
				}
				return this;
			},
			// Check if a given callback is in the list.
			// If no argument is given, return whether or not list has callbacks attached.
			has: function( fn ) {
				return fn ? jQuery.inArray( fn, list ) > -1 : !!( list && list.length );
			},
			// Remove all callbacks from the list
			empty: function() {
				list = [];
				firingLength = 0;
				return this;
			},
			// Have the list do nothing anymore
			disable: function() {
				list = stack = memory = undefined;
				return this;
			},
			// Is it disabled?
			disabled: function() {
				return !list;
			},
			// Lock the list in its current state
			lock: function() {
				stack = undefined;
				if ( !memory ) {
					self.disable();
				}
				return this;
			},
			// Is it locked?
			locked: function() {
				return !stack;
			},
			// Call all callbacks with the given context and arguments
			fireWith: function( context, args ) {
				if ( list && ( !fired || stack ) ) {
					args = args || [];
					args = [ context, args.slice ? args.slice() : args ];
					if ( firing ) {
						stack.push( args );
					} else {
						fire( args );
					}
				}
				return this;
			},
			// Call all the callbacks with the given arguments
			fire: function() {
				self.fireWith( this, arguments );
				return this;
			},
			// To know if the callbacks have already been called at least once
			fired: function() {
				return !!fired;
			}
		};

	return self;
};


jQuery.extend({

	Deferred: function( func ) {
		var tuples = [
				// action, add listener, listener list, final state
				[ "resolve", "done", jQuery.Callbacks("once memory"), "resolved" ],
				[ "reject", "fail", jQuery.Callbacks("once memory"), "rejected" ],
				[ "notify", "progress", jQuery.Callbacks("memory") ]
			],
			state = "pending",
			promise = {
				state: function() {
					return state;
				},
				always: function() {
					deferred.done( arguments ).fail( arguments );
					return this;
				},
				then: function( /* fnDone, fnFail, fnProgress */ ) {
					var fns = arguments;
					return jQuery.Deferred(function( newDefer ) {
						jQuery.each( tuples, function( i, tuple ) {
							var fn = jQuery.isFunction( fns[ i ] ) && fns[ i ];
							// deferred[ done | fail | progress ] for forwarding actions to newDefer
							deferred[ tuple[1] ](function() {
								var returned = fn && fn.apply( this, arguments );
								if ( returned && jQuery.isFunction( returned.promise ) ) {
									returned.promise()
										.done( newDefer.resolve )
										.fail( newDefer.reject )
										.progress( newDefer.notify );
								} else {
									newDefer[ tuple[ 0 ] + "With" ]( this === promise ? newDefer.promise() : this, fn ? [ returned ] : arguments );
								}
							});
						});
						fns = null;
					}).promise();
				},
				// Get a promise for this deferred
				// If obj is provided, the promise aspect is added to the object
				promise: function( obj ) {
					return obj != null ? jQuery.extend( obj, promise ) : promise;
				}
			},
			deferred = {};

		// Keep pipe for back-compat
		promise.pipe = promise.then;

		// Add list-specific methods
		jQuery.each( tuples, function( i, tuple ) {
			var list = tuple[ 2 ],
				stateString = tuple[ 3 ];

			// promise[ done | fail | progress ] = list.add
			promise[ tuple[1] ] = list.add;

			// Handle state
			if ( stateString ) {
				list.add(function() {
					// state = [ resolved | rejected ]
					state = stateString;

				// [ reject_list | resolve_list ].disable; progress_list.lock
				}, tuples[ i ^ 1 ][ 2 ].disable, tuples[ 2 ][ 2 ].lock );
			}

			// deferred[ resolve | reject | notify ]
			deferred[ tuple[0] ] = function() {
				deferred[ tuple[0] + "With" ]( this === deferred ? promise : this, arguments );
				return this;
			};
			deferred[ tuple[0] + "With" ] = list.fireWith;
		});

		// Make the deferred a promise
		promise.promise( deferred );

		// Call given func if any
		if ( func ) {
			func.call( deferred, deferred );
		}

		// All done!
		return deferred;
	},

	// Deferred helper
	when: function( subordinate /* , ..., subordinateN */ ) {
		var i = 0,
			resolveValues = slice.call( arguments ),
			length = resolveValues.length,

			// the count of uncompleted subordinates
			remaining = length !== 1 || ( subordinate && jQuery.isFunction( subordinate.promise ) ) ? length : 0,

			// the master Deferred. If resolveValues consist of only a single Deferred, just use that.
			deferred = remaining === 1 ? subordinate : jQuery.Deferred(),

			// Update function for both resolve and progress values
			updateFunc = function( i, contexts, values ) {
				return function( value ) {
					contexts[ i ] = this;
					values[ i ] = arguments.length > 1 ? slice.call( arguments ) : value;
					if ( values === progressValues ) {
						deferred.notifyWith( contexts, values );

					} else if ( !(--remaining) ) {
						deferred.resolveWith( contexts, values );
					}
				};
			},

			progressValues, progressContexts, resolveContexts;

		// add listeners to Deferred subordinates; treat others as resolved
		if ( length > 1 ) {
			progressValues = new Array( length );
			progressContexts = new Array( length );
			resolveContexts = new Array( length );
			for ( ; i < length; i++ ) {
				if ( resolveValues[ i ] && jQuery.isFunction( resolveValues[ i ].promise ) ) {
					resolveValues[ i ].promise()
						.done( updateFunc( i, resolveContexts, resolveValues ) )
						.fail( deferred.reject )
						.progress( updateFunc( i, progressContexts, progressValues ) );
				} else {
					--remaining;
				}
			}
		}

		// if we're not waiting on anything, resolve the master
		if ( !remaining ) {
			deferred.resolveWith( resolveContexts, resolveValues );
		}

		return deferred.promise();
	}
});


// The deferred used on DOM ready
var readyList;

jQuery.fn.ready = function( fn ) {
	// Add the callback
	jQuery.ready.promise().done( fn );

	return this;
};

jQuery.extend({
	// Is the DOM ready to be used? Set to true once it occurs.
	isReady: false,

	// A counter to track how many items to wait for before
	// the ready event fires. See #6781
	readyWait: 1,

	// Hold (or release) the ready event
	holdReady: function( hold ) {
		if ( hold ) {
			jQuery.readyWait++;
		} else {
			jQuery.ready( true );
		}
	},

	// Handle when the DOM is ready
	ready: function( wait ) {

		// Abort if there are pending holds or we're already ready
		if ( wait === true ? --jQuery.readyWait : jQuery.isReady ) {
			return;
		}

		// Make sure body exists, at least, in case IE gets a little overzealous (ticket #5443).
		if ( !document.body ) {
			return setTimeout( jQuery.ready );
		}

		// Remember that the DOM is ready
		jQuery.isReady = true;

		// If a normal DOM Ready event fired, decrement, and wait if need be
		if ( wait !== true && --jQuery.readyWait > 0 ) {
			return;
		}

		// If there are functions bound, to execute
		readyList.resolveWith( document, [ jQuery ] );

		// Trigger any bound ready events
		if ( jQuery.fn.triggerHandler ) {
			jQuery( document ).triggerHandler( "ready" );
			jQuery( document ).off( "ready" );
		}
	}
});

/**
 * Clean-up method for dom ready events
 */
function detach() {
	if ( document.addEventListener ) {
		document.removeEventListener( "DOMContentLoaded", completed, false );
		window.removeEventListener( "load", completed, false );

	} else {
		document.detachEvent( "onreadystatechange", completed );
		window.detachEvent( "onload", completed );
	}
}

/**
 * The ready event handler and self cleanup method
 */
function completed() {
	// readyState === "complete" is good enough for us to call the dom ready in oldIE
	if ( document.addEventListener || event.type === "load" || document.readyState === "complete" ) {
		detach();
		jQuery.ready();
	}
}

jQuery.ready.promise = function( obj ) {
	if ( !readyList ) {

		readyList = jQuery.Deferred();

		// Catch cases where $(document).ready() is called after the browser event has already occurred.
		// we once tried to use readyState "interactive" here, but it caused issues like the one
		// discovered by ChrisS here: http://bugs.jquery.com/ticket/12282#comment:15
		if ( document.readyState === "complete" ) {
			// Handle it asynchronously to allow scripts the opportunity to delay ready
			setTimeout( jQuery.ready );

		// Standards-based browsers support DOMContentLoaded
		} else if ( document.addEventListener ) {
			// Use the handy event callback
			document.addEventListener( "DOMContentLoaded", completed, false );

			// A fallback to window.onload, that will always work
			window.addEventListener( "load", completed, false );

		// If IE event model is used
		} else {
			// Ensure firing before onload, maybe late but safe also for iframes
			document.attachEvent( "onreadystatechange", completed );

			// A fallback to window.onload, that will always work
			window.attachEvent( "onload", completed );

			// If IE and not a frame
			// continually check to see if the document is ready
			var top = false;

			try {
				top = window.frameElement == null && document.documentElement;
			} catch(e) {}

			if ( top && top.doScroll ) {
				(function doScrollCheck() {
					if ( !jQuery.isReady ) {

						try {
							// Use the trick by Diego Perini
							// http://javascript.nwbox.com/IEContentLoaded/
							top.doScroll("left");
						} catch(e) {
							return setTimeout( doScrollCheck, 50 );
						}

						// detach all dom ready events
						detach();

						// and execute any waiting functions
						jQuery.ready();
					}
				})();
			}
		}
	}
	return readyList.promise( obj );
};


var strundefined = typeof undefined;



// Support: IE<9
// Iteration over object's inherited properties before its own
var i;
for ( i in jQuery( support ) ) {
	break;
}
support.ownLast = i !== "0";

// Note: most support tests are defined in their respective modules.
// false until the test is run
support.inlineBlockNeedsLayout = false;

// Execute ASAP in case we need to set body.style.zoom
jQuery(function() {
	// Minified: var a,b,c,d
	var val, div, body, container;

	body = document.getElementsByTagName( "body" )[ 0 ];
	if ( !body || !body.style ) {
		// Return for frameset docs that don't have a body
		return;
	}

	// Setup
	div = document.createElement( "div" );
	container = document.createElement( "div" );
	container.style.cssText = "position:absolute;border:0;width:0;height:0;top:0;left:-9999px";
	body.appendChild( container ).appendChild( div );

	if ( typeof div.style.zoom !== strundefined ) {
		// Support: IE<8
		// Check if natively block-level elements act like inline-block
		// elements when setting their display to 'inline' and giving
		// them layout
		div.style.cssText = "display:inline;margin:0;border:0;padding:1px;width:1px;zoom:1";

		support.inlineBlockNeedsLayout = val = div.offsetWidth === 3;
		if ( val ) {
			// Prevent IE 6 from affecting layout for positioned elements #11048
			// Prevent IE from shrinking the body in IE 7 mode #12869
			// Support: IE<8
			body.style.zoom = 1;
		}
	}

	body.removeChild( container );
});




(function() {
	var div = document.createElement( "div" );

	// Execute the test only if not already executed in another module.
	if (support.deleteExpando == null) {
		// Support: IE<9
		support.deleteExpando = true;
		try {
			delete div.test;
		} catch( e ) {
			support.deleteExpando = false;
		}
	}

	// Null elements to avoid leaks in IE.
	div = null;
})();


/**
 * Determines whether an object can have data
 */
jQuery.acceptData = function( elem ) {
	var noData = jQuery.noData[ (elem.nodeName + " ").toLowerCase() ],
		nodeType = +elem.nodeType || 1;

	// Do not set data on non-element DOM nodes because it will not be cleared (#8335).
	return nodeType !== 1 && nodeType !== 9 ?
		false :

		// Nodes accept data unless otherwise specified; rejection can be conditional
		!noData || noData !== true && elem.getAttribute("classid") === noData;
};


var rbrace = /^(?:\{[\w\W]*\}|\[[\w\W]*\])$/,
	rmultiDash = /([A-Z])/g;

function dataAttr( elem, key, data ) {
	// If nothing was found internally, try to fetch any
	// data from the HTML5 data-* attribute
	if ( data === undefined && elem.nodeType === 1 ) {

		var name = "data-" + key.replace( rmultiDash, "-$1" ).toLowerCase();

		data = elem.getAttribute( name );

		if ( typeof data === "string" ) {
			try {
				data = data === "true" ? true :
					data === "false" ? false :
					data === "null" ? null :
					// Only convert to a number if it doesn't change the string
					+data + "" === data ? +data :
					rbrace.test( data ) ? jQuery.parseJSON( data ) :
					data;
			} catch( e ) {}

			// Make sure we set the data so it isn't changed later
			jQuery.data( elem, key, data );

		} else {
			data = undefined;
		}
	}

	return data;
}

// checks a cache object for emptiness
function isEmptyDataObject( obj ) {
	var name;
	for ( name in obj ) {

		// if the public data object is empty, the private is still empty
		if ( name === "data" && jQuery.isEmptyObject( obj[name] ) ) {
			continue;
		}
		if ( name !== "toJSON" ) {
			return false;
		}
	}

	return true;
}

function internalData( elem, name, data, pvt /* Internal Use Only */ ) {
	if ( !jQuery.acceptData( elem ) ) {
		return;
	}

	var ret, thisCache,
		internalKey = jQuery.expando,

		// We have to handle DOM nodes and JS objects differently because IE6-7
		// can't GC object references properly across the DOM-JS boundary
		isNode = elem.nodeType,

		// Only DOM nodes need the global jQuery cache; JS object data is
		// attached directly to the object so GC can occur automatically
		cache = isNode ? jQuery.cache : elem,

		// Only defining an ID for JS objects if its cache already exists allows
		// the code to shortcut on the same path as a DOM node with no cache
		id = isNode ? elem[ internalKey ] : elem[ internalKey ] && internalKey;

	// Avoid doing any more work than we need to when trying to get data on an
	// object that has no data at all
	if ( (!id || !cache[id] || (!pvt && !cache[id].data)) && data === undefined && typeof name === "string" ) {
		return;
	}

	if ( !id ) {
		// Only DOM nodes need a new unique ID for each element since their data
		// ends up in the global cache
		if ( isNode ) {
			id = elem[ internalKey ] = deletedIds.pop() || jQuery.guid++;
		} else {
			id = internalKey;
		}
	}

	if ( !cache[ id ] ) {
		// Avoid exposing jQuery metadata on plain JS objects when the object
		// is serialized using JSON.stringify
		cache[ id ] = isNode ? {} : { toJSON: jQuery.noop };
	}

	// An object can be passed to jQuery.data instead of a key/value pair; this gets
	// shallow copied over onto the existing cache
	if ( typeof name === "object" || typeof name === "function" ) {
		if ( pvt ) {
			cache[ id ] = jQuery.extend( cache[ id ], name );
		} else {
			cache[ id ].data = jQuery.extend( cache[ id ].data, name );
		}
	}

	thisCache = cache[ id ];

	// jQuery data() is stored in a separate object inside the object's internal data
	// cache in order to avoid key collisions between internal data and user-defined
	// data.
	if ( !pvt ) {
		if ( !thisCache.data ) {
			thisCache.data = {};
		}

		thisCache = thisCache.data;
	}

	if ( data !== undefined ) {
		thisCache[ jQuery.camelCase( name ) ] = data;
	}

	// Check for both converted-to-camel and non-converted data property names
	// If a data property was specified
	if ( typeof name === "string" ) {

		// First Try to find as-is property data
		ret = thisCache[ name ];

		// Test for null|undefined property data
		if ( ret == null ) {

			// Try to find the camelCased property
			ret = thisCache[ jQuery.camelCase( name ) ];
		}
	} else {
		ret = thisCache;
	}

	return ret;
}

function internalRemoveData( elem, name, pvt ) {
	if ( !jQuery.acceptData( elem ) ) {
		return;
	}

	var thisCache, i,
		isNode = elem.nodeType,

		// See jQuery.data for more information
		cache = isNode ? jQuery.cache : elem,
		id = isNode ? elem[ jQuery.expando ] : jQuery.expando;

	// If there is already no cache entry for this object, there is no
	// purpose in continuing
	if ( !cache[ id ] ) {
		return;
	}

	if ( name ) {

		thisCache = pvt ? cache[ id ] : cache[ id ].data;

		if ( thisCache ) {

			// Support array or space separated string names for data keys
			if ( !jQuery.isArray( name ) ) {

				// try the string as a key before any manipulation
				if ( name in thisCache ) {
					name = [ name ];
				} else {

					// split the camel cased version by spaces unless a key with the spaces exists
					name = jQuery.camelCase( name );
					if ( name in thisCache ) {
						name = [ name ];
					} else {
						name = name.split(" ");
					}
				}
			} else {
				// If "name" is an array of keys...
				// When data is initially created, via ("key", "val") signature,
				// keys will be converted to camelCase.
				// Since there is no way to tell _how_ a key was added, remove
				// both plain key and camelCase key. #12786
				// This will only penalize the array argument path.
				name = name.concat( jQuery.map( name, jQuery.camelCase ) );
			}

			i = name.length;
			while ( i-- ) {
				delete thisCache[ name[i] ];
			}

			// If there is no data left in the cache, we want to continue
			// and let the cache object itself get destroyed
			if ( pvt ? !isEmptyDataObject(thisCache) : !jQuery.isEmptyObject(thisCache) ) {
				return;
			}
		}
	}

	// See jQuery.data for more information
	if ( !pvt ) {
		delete cache[ id ].data;

		// Don't destroy the parent cache unless the internal data object
		// had been the only thing left in it
		if ( !isEmptyDataObject( cache[ id ] ) ) {
			return;
		}
	}

	// Destroy the cache
	if ( isNode ) {
		jQuery.cleanData( [ elem ], true );

	// Use delete when supported for expandos or `cache` is not a window per isWindow (#10080)
	/* jshint eqeqeq: false */
	} else if ( support.deleteExpando || cache != cache.window ) {
		/* jshint eqeqeq: true */
		delete cache[ id ];

	// When all else fails, null
	} else {
		cache[ id ] = null;
	}
}

jQuery.extend({
	cache: {},

	// The following elements (space-suffixed to avoid Object.prototype collisions)
	// throw uncatchable exceptions if you attempt to set expando properties
	noData: {
		"applet ": true,
		"embed ": true,
		// ...but Flash objects (which have this classid) *can* handle expandos
		"object ": "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
	},

	hasData: function( elem ) {
		elem = elem.nodeType ? jQuery.cache[ elem[jQuery.expando] ] : elem[ jQuery.expando ];
		return !!elem && !isEmptyDataObject( elem );
	},

	data: function( elem, name, data ) {
		return internalData( elem, name, data );
	},

	removeData: function( elem, name ) {
		return internalRemoveData( elem, name );
	},

	// For internal use only.
	_data: function( elem, name, data ) {
		return internalData( elem, name, data, true );
	},

	_removeData: function( elem, name ) {
		return internalRemoveData( elem, name, true );
	}
});

jQuery.fn.extend({
	data: function( key, value ) {
		var i, name, data,
			elem = this[0],
			attrs = elem && elem.attributes;

		// Special expections of .data basically thwart jQuery.access,
		// so implement the relevant behavior ourselves

		// Gets all values
		if ( key === undefined ) {
			if ( this.length ) {
				data = jQuery.data( elem );

				if ( elem.nodeType === 1 && !jQuery._data( elem, "parsedAttrs" ) ) {
					i = attrs.length;
					while ( i-- ) {

						// Support: IE11+
						// The attrs elements can be null (#14894)
						if ( attrs[ i ] ) {
							name = attrs[ i ].name;
							if ( name.indexOf( "data-" ) === 0 ) {
								name = jQuery.camelCase( name.slice(5) );
								dataAttr( elem, name, data[ name ] );
							}
						}
					}
					jQuery._data( elem, "parsedAttrs", true );
				}
			}

			return data;
		}

		// Sets multiple values
		if ( typeof key === "object" ) {
			return this.each(function() {
				jQuery.data( this, key );
			});
		}

		return arguments.length > 1 ?

			// Sets one value
			this.each(function() {
				jQuery.data( this, key, value );
			}) :

			// Gets one value
			// Try to fetch any internally stored data first
			elem ? dataAttr( elem, key, jQuery.data( elem, key ) ) : undefined;
	},

	removeData: function( key ) {
		return this.each(function() {
			jQuery.removeData( this, key );
		});
	}
});


jQuery.extend({
	queue: function( elem, type, data ) {
		var queue;

		if ( elem ) {
			type = ( type || "fx" ) + "queue";
			queue = jQuery._data( elem, type );

			// Speed up dequeue by getting out quickly if this is just a lookup
			if ( data ) {
				if ( !queue || jQuery.isArray(data) ) {
					queue = jQuery._data( elem, type, jQuery.makeArray(data) );
				} else {
					queue.push( data );
				}
			}
			return queue || [];
		}
	},

	dequeue: function( elem, type ) {
		type = type || "fx";

		var queue = jQuery.queue( elem, type ),
			startLength = queue.length,
			fn = queue.shift(),
			hooks = jQuery._queueHooks( elem, type ),
			next = function() {
				jQuery.dequeue( elem, type );
			};

		// If the fx queue is dequeued, always remove the progress sentinel
		if ( fn === "inprogress" ) {
			fn = queue.shift();
			startLength--;
		}

		if ( fn ) {

			// Add a progress sentinel to prevent the fx queue from being
			// automatically dequeued
			if ( type === "fx" ) {
				queue.unshift( "inprogress" );
			}

			// clear up the last queue stop function
			delete hooks.stop;
			fn.call( elem, next, hooks );
		}

		if ( !startLength && hooks ) {
			hooks.empty.fire();
		}
	},

	// not intended for public consumption - generates a queueHooks object, or returns the current one
	_queueHooks: function( elem, type ) {
		var key = type + "queueHooks";
		return jQuery._data( elem, key ) || jQuery._data( elem, key, {
			empty: jQuery.Callbacks("once memory").add(function() {
				jQuery._removeData( elem, type + "queue" );
				jQuery._removeData( elem, key );
			})
		});
	}
});

jQuery.fn.extend({
	queue: function( type, data ) {
		var setter = 2;

		if ( typeof type !== "string" ) {
			data = type;
			type = "fx";
			setter--;
		}

		if ( arguments.length < setter ) {
			return jQuery.queue( this[0], type );
		}

		return data === undefined ?
			this :
			this.each(function() {
				var queue = jQuery.queue( this, type, data );

				// ensure a hooks for this queue
				jQuery._queueHooks( this, type );

				if ( type === "fx" && queue[0] !== "inprogress" ) {
					jQuery.dequeue( this, type );
				}
			});
	},
	dequeue: function( type ) {
		return this.each(function() {
			jQuery.dequeue( this, type );
		});
	},
	clearQueue: function( type ) {
		return this.queue( type || "fx", [] );
	},
	// Get a promise resolved when queues of a certain type
	// are emptied (fx is the type by default)
	promise: function( type, obj ) {
		var tmp,
			count = 1,
			defer = jQuery.Deferred(),
			elements = this,
			i = this.length,
			resolve = function() {
				if ( !( --count ) ) {
					defer.resolveWith( elements, [ elements ] );
				}
			};

		if ( typeof type !== "string" ) {
			obj = type;
			type = undefined;
		}
		type = type || "fx";

		while ( i-- ) {
			tmp = jQuery._data( elements[ i ], type + "queueHooks" );
			if ( tmp && tmp.empty ) {
				count++;
				tmp.empty.add( resolve );
			}
		}
		resolve();
		return defer.promise( obj );
	}
});
var pnum = (/[+-]?(?:\d*\.|)\d+(?:[eE][+-]?\d+|)/).source;

var cssExpand = [ "Top", "Right", "Bottom", "Left" ];

var isHidden = function( elem, el ) {
		// isHidden might be called from jQuery#filter function;
		// in that case, element will be second argument
		elem = el || elem;
		return jQuery.css( elem, "display" ) === "none" || !jQuery.contains( elem.ownerDocument, elem );
	};



// Multifunctional method to get and set values of a collection
// The value/s can optionally be executed if it's a function
var access = jQuery.access = function( elems, fn, key, value, chainable, emptyGet, raw ) {
	var i = 0,
		length = elems.length,
		bulk = key == null;

	// Sets many values
	if ( jQuery.type( key ) === "object" ) {
		chainable = true;
		for ( i in key ) {
			jQuery.access( elems, fn, i, key[i], true, emptyGet, raw );
		}

	// Sets one value
	} else if ( value !== undefined ) {
		chainable = true;

		if ( !jQuery.isFunction( value ) ) {
			raw = true;
		}

		if ( bulk ) {
			// Bulk operations run against the entire set
			if ( raw ) {
				fn.call( elems, value );
				fn = null;

			// ...except when executing function values
			} else {
				bulk = fn;
				fn = function( elem, key, value ) {
					return bulk.call( jQuery( elem ), value );
				};
			}
		}

		if ( fn ) {
			for ( ; i < length; i++ ) {
				fn( elems[i], key, raw ? value : value.call( elems[i], i, fn( elems[i], key ) ) );
			}
		}
	}

	return chainable ?
		elems :

		// Gets
		bulk ?
			fn.call( elems ) :
			length ? fn( elems[0], key ) : emptyGet;
};
var rcheckableType = (/^(?:checkbox|radio)$/i);



(function() {
	// Minified: var a,b,c
	var input = document.createElement( "input" ),
		div = document.createElement( "div" ),
		fragment = document.createDocumentFragment();

	// Setup
	div.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>";

	// IE strips leading whitespace when .innerHTML is used
	support.leadingWhitespace = div.firstChild.nodeType === 3;

	// Make sure that tbody elements aren't automatically inserted
	// IE will insert them into empty tables
	support.tbody = !div.getElementsByTagName( "tbody" ).length;

	// Make sure that link elements get serialized correctly by innerHTML
	// This requires a wrapper element in IE
	support.htmlSerialize = !!div.getElementsByTagName( "link" ).length;

	// Makes sure cloning an html5 element does not cause problems
	// Where outerHTML is undefined, this still works
	support.html5Clone =
		document.createElement( "nav" ).cloneNode( true ).outerHTML !== "<:nav></:nav>";

	// Check if a disconnected checkbox will retain its checked
	// value of true after appended to the DOM (IE6/7)
	input.type = "checkbox";
	input.checked = true;
	fragment.appendChild( input );
	support.appendChecked = input.checked;

	// Make sure textarea (and checkbox) defaultValue is properly cloned
	// Support: IE6-IE11+
	div.innerHTML = "<textarea>x</textarea>";
	support.noCloneChecked = !!div.cloneNode( true ).lastChild.defaultValue;

	// #11217 - WebKit loses check when the name is after the checked attribute
	fragment.appendChild( div );
	div.innerHTML = "<input type='radio' checked='checked' name='t'/>";

	// Support: Safari 5.1, iOS 5.1, Android 4.x, Android 2.3
	// old WebKit doesn't clone checked state correctly in fragments
	support.checkClone = div.cloneNode( true ).cloneNode( true ).lastChild.checked;

	// Support: IE<9
	// Opera does not clone events (and typeof div.attachEvent === undefined).
	// IE9-10 clones events bound via attachEvent, but they don't trigger with .click()
	support.noCloneEvent = true;
	if ( div.attachEvent ) {
		div.attachEvent( "onclick", function() {
			support.noCloneEvent = false;
		});

		div.cloneNode( true ).click();
	}

	// Execute the test only if not already executed in another module.
	if (support.deleteExpando == null) {
		// Support: IE<9
		support.deleteExpando = true;
		try {
			delete div.test;
		} catch( e ) {
			support.deleteExpando = false;
		}
	}
})();


(function() {
	var i, eventName,
		div = document.createElement( "div" );

	// Support: IE<9 (lack submit/change bubble), Firefox 23+ (lack focusin event)
	for ( i in { submit: true, change: true, focusin: true }) {
		eventName = "on" + i;

		if ( !(support[ i + "Bubbles" ] = eventName in window) ) {
			// Beware of CSP restrictions (https://developer.mozilla.org/en/Security/CSP)
			div.setAttribute( eventName, "t" );
			support[ i + "Bubbles" ] = div.attributes[ eventName ].expando === false;
		}
	}

	// Null elements to avoid leaks in IE.
	div = null;
})();


var rformElems = /^(?:input|select|textarea)$/i,
	rkeyEvent = /^key/,
	rmouseEvent = /^(?:mouse|pointer|contextmenu)|click/,
	rfocusMorph = /^(?:focusinfocus|focusoutblur)$/,
	rtypenamespace = /^([^.]*)(?:\.(.+)|)$/;

function returnTrue() {
	return true;
}

function returnFalse() {
	return false;
}

function safeActiveElement() {
	try {
		return document.activeElement;
	} catch ( err ) { }
}

/*
 * Helper functions for managing events -- not part of the public interface.
 * Props to Dean Edwards' addEvent library for many of the ideas.
 */
jQuery.event = {

	global: {},

	add: function( elem, types, handler, data, selector ) {
		var tmp, events, t, handleObjIn,
			special, eventHandle, handleObj,
			handlers, type, namespaces, origType,
			elemData = jQuery._data( elem );

		// Don't attach events to noData or text/comment nodes (but allow plain objects)
		if ( !elemData ) {
			return;
		}

		// Caller can pass in an object of custom data in lieu of the handler
		if ( handler.handler ) {
			handleObjIn = handler;
			handler = handleObjIn.handler;
			selector = handleObjIn.selector;
		}

		// Make sure that the handler has a unique ID, used to find/remove it later
		if ( !handler.guid ) {
			handler.guid = jQuery.guid++;
		}

		// Init the element's event structure and main handler, if this is the first
		if ( !(events = elemData.events) ) {
			events = elemData.events = {};
		}
		if ( !(eventHandle = elemData.handle) ) {
			eventHandle = elemData.handle = function( e ) {
				// Discard the second event of a jQuery.event.trigger() and
				// when an event is called after a page has unloaded
				return typeof jQuery !== strundefined && (!e || jQuery.event.triggered !== e.type) ?
					jQuery.event.dispatch.apply( eventHandle.elem, arguments ) :
					undefined;
			};
			// Add elem as a property of the handle fn to prevent a memory leak with IE non-native events
			eventHandle.elem = elem;
		}

		// Handle multiple events separated by a space
		types = ( types || "" ).match( rnotwhite ) || [ "" ];
		t = types.length;
		while ( t-- ) {
			tmp = rtypenamespace.exec( types[t] ) || [];
			type = origType = tmp[1];
			namespaces = ( tmp[2] || "" ).split( "." ).sort();

			// There *must* be a type, no attaching namespace-only handlers
			if ( !type ) {
				continue;
			}

			// If event changes its type, use the special event handlers for the changed type
			special = jQuery.event.special[ type ] || {};

			// If selector defined, determine special event api type, otherwise given type
			type = ( selector ? special.delegateType : special.bindType ) || type;

			// Update special based on newly reset type
			special = jQuery.event.special[ type ] || {};

			// handleObj is passed to all event handlers
			handleObj = jQuery.extend({
				type: type,
				origType: origType,
				data: data,
				handler: handler,
				guid: handler.guid,
				selector: selector,
				needsContext: selector && jQuery.expr.match.needsContext.test( selector ),
				namespace: namespaces.join(".")
			}, handleObjIn );

			// Init the event handler queue if we're the first
			if ( !(handlers = events[ type ]) ) {
				handlers = events[ type ] = [];
				handlers.delegateCount = 0;

				// Only use addEventListener/attachEvent if the special events handler returns false
				if ( !special.setup || special.setup.call( elem, data, namespaces, eventHandle ) === false ) {
					// Bind the global event handler to the element
					if ( elem.addEventListener ) {
						elem.addEventListener( type, eventHandle, false );

					} else if ( elem.attachEvent ) {
						elem.attachEvent( "on" + type, eventHandle );
					}
				}
			}

			if ( special.add ) {
				special.add.call( elem, handleObj );

				if ( !handleObj.handler.guid ) {
					handleObj.handler.guid = handler.guid;
				}
			}

			// Add to the element's handler list, delegates in front
			if ( selector ) {
				handlers.splice( handlers.delegateCount++, 0, handleObj );
			} else {
				handlers.push( handleObj );
			}

			// Keep track of which events have ever been used, for event optimization
			jQuery.event.global[ type ] = true;
		}

		// Nullify elem to prevent memory leaks in IE
		elem = null;
	},

	// Detach an event or set of events from an element
	remove: function( elem, types, handler, selector, mappedTypes ) {
		var j, handleObj, tmp,
			origCount, t, events,
			special, handlers, type,
			namespaces, origType,
			elemData = jQuery.hasData( elem ) && jQuery._data( elem );

		if ( !elemData || !(events = elemData.events) ) {
			return;
		}

		// Once for each type.namespace in types; type may be omitted
		types = ( types || "" ).match( rnotwhite ) || [ "" ];
		t = types.length;
		while ( t-- ) {
			tmp = rtypenamespace.exec( types[t] ) || [];
			type = origType = tmp[1];
			namespaces = ( tmp[2] || "" ).split( "." ).sort();

			// Unbind all events (on this namespace, if provided) for the element
			if ( !type ) {
				for ( type in events ) {
					jQuery.event.remove( elem, type + types[ t ], handler, selector, true );
				}
				continue;
			}

			special = jQuery.event.special[ type ] || {};
			type = ( selector ? special.delegateType : special.bindType ) || type;
			handlers = events[ type ] || [];
			tmp = tmp[2] && new RegExp( "(^|\\.)" + namespaces.join("\\.(?:.*\\.|)") + "(\\.|$)" );

			// Remove matching events
			origCount = j = handlers.length;
			while ( j-- ) {
				handleObj = handlers[ j ];

				if ( ( mappedTypes || origType === handleObj.origType ) &&
					( !handler || handler.guid === handleObj.guid ) &&
					( !tmp || tmp.test( handleObj.namespace ) ) &&
					( !selector || selector === handleObj.selector || selector === "**" && handleObj.selector ) ) {
					handlers.splice( j, 1 );

					if ( handleObj.selector ) {
						handlers.delegateCount--;
					}
					if ( special.remove ) {
						special.remove.call( elem, handleObj );
					}
				}
			}

			// Remove generic event handler if we removed something and no more handlers exist
			// (avoids potential for endless recursion during removal of special event handlers)
			if ( origCount && !handlers.length ) {
				if ( !special.teardown || special.teardown.call( elem, namespaces, elemData.handle ) === false ) {
					jQuery.removeEvent( elem, type, elemData.handle );
				}

				delete events[ type ];
			}
		}

		// Remove the expando if it's no longer used
		if ( jQuery.isEmptyObject( events ) ) {
			delete elemData.handle;

			// removeData also checks for emptiness and clears the expando if empty
			// so use it instead of delete
			jQuery._removeData( elem, "events" );
		}
	},

	trigger: function( event, data, elem, onlyHandlers ) {
		var handle, ontype, cur,
			bubbleType, special, tmp, i,
			eventPath = [ elem || document ],
			type = hasOwn.call( event, "type" ) ? event.type : event,
			namespaces = hasOwn.call( event, "namespace" ) ? event.namespace.split(".") : [];

		cur = tmp = elem = elem || document;

		// Don't do events on text and comment nodes
		if ( elem.nodeType === 3 || elem.nodeType === 8 ) {
			return;
		}

		// focus/blur morphs to focusin/out; ensure we're not firing them right now
		if ( rfocusMorph.test( type + jQuery.event.triggered ) ) {
			return;
		}

		if ( type.indexOf(".") >= 0 ) {
			// Namespaced trigger; create a regexp to match event type in handle()
			namespaces = type.split(".");
			type = namespaces.shift();
			namespaces.sort();
		}
		ontype = type.indexOf(":") < 0 && "on" + type;

		// Caller can pass in a jQuery.Event object, Object, or just an event type string
		event = event[ jQuery.expando ] ?
			event :
			new jQuery.Event( type, typeof event === "object" && event );

		// Trigger bitmask: & 1 for native handlers; & 2 for jQuery (always true)
		event.isTrigger = onlyHandlers ? 2 : 3;
		event.namespace = namespaces.join(".");
		event.namespace_re = event.namespace ?
			new RegExp( "(^|\\.)" + namespaces.join("\\.(?:.*\\.|)") + "(\\.|$)" ) :
			null;

		// Clean up the event in case it is being reused
		event.result = undefined;
		if ( !event.target ) {
			event.target = elem;
		}

		// Clone any incoming data and prepend the event, creating the handler arg list
		data = data == null ?
			[ event ] :
			jQuery.makeArray( data, [ event ] );

		// Allow special events to draw outside the lines
		special = jQuery.event.special[ type ] || {};
		if ( !onlyHandlers && special.trigger && special.trigger.apply( elem, data ) === false ) {
			return;
		}

		// Determine event propagation path in advance, per W3C events spec (#9951)
		// Bubble up to document, then to window; watch for a global ownerDocument var (#9724)
		if ( !onlyHandlers && !special.noBubble && !jQuery.isWindow( elem ) ) {

			bubbleType = special.delegateType || type;
			if ( !rfocusMorph.test( bubbleType + type ) ) {
				cur = cur.parentNode;
			}
			for ( ; cur; cur = cur.parentNode ) {
				eventPath.push( cur );
				tmp = cur;
			}

			// Only add window if we got to document (e.g., not plain obj or detached DOM)
			if ( tmp === (elem.ownerDocument || document) ) {
				eventPath.push( tmp.defaultView || tmp.parentWindow || window );
			}
		}

		// Fire handlers on the event path
		i = 0;
		while ( (cur = eventPath[i++]) && !event.isPropagationStopped() ) {

			event.type = i > 1 ?
				bubbleType :
				special.bindType || type;

			// jQuery handler
			handle = ( jQuery._data( cur, "events" ) || {} )[ event.type ] && jQuery._data( cur, "handle" );
			if ( handle ) {
				handle.apply( cur, data );
			}

			// Native handler
			handle = ontype && cur[ ontype ];
			if ( handle && handle.apply && jQuery.acceptData( cur ) ) {
				event.result = handle.apply( cur, data );
				if ( event.result === false ) {
					event.preventDefault();
				}
			}
		}
		event.type = type;

		// If nobody prevented the default action, do it now
		if ( !onlyHandlers && !event.isDefaultPrevented() ) {

			if ( (!special._default || special._default.apply( eventPath.pop(), data ) === false) &&
				jQuery.acceptData( elem ) ) {

				// Call a native DOM method on the target with the same name name as the event.
				// Can't use an .isFunction() check here because IE6/7 fails that test.
				// Don't do default actions on window, that's where global variables be (#6170)
				if ( ontype && elem[ type ] && !jQuery.isWindow( elem ) ) {

					// Don't re-trigger an onFOO event when we call its FOO() method
					tmp = elem[ ontype ];

					if ( tmp ) {
						elem[ ontype ] = null;
					}

					// Prevent re-triggering of the same event, since we already bubbled it above
					jQuery.event.triggered = type;
					try {
						elem[ type ]();
					} catch ( e ) {
						// IE<9 dies on focus/blur to hidden element (#1486,#12518)
						// only reproducible on winXP IE8 native, not IE9 in IE8 mode
					}
					jQuery.event.triggered = undefined;

					if ( tmp ) {
						elem[ ontype ] = tmp;
					}
				}
			}
		}

		return event.result;
	},

	dispatch: function( event ) {

		// Make a writable jQuery.Event from the native event object
		event = jQuery.event.fix( event );

		var i, ret, handleObj, matched, j,
			handlerQueue = [],
			args = slice.call( arguments ),
			handlers = ( jQuery._data( this, "events" ) || {} )[ event.type ] || [],
			special = jQuery.event.special[ event.type ] || {};

		// Use the fix-ed jQuery.Event rather than the (read-only) native event
		args[0] = event;
		event.delegateTarget = this;

		// Call the preDispatch hook for the mapped type, and let it bail if desired
		if ( special.preDispatch && special.preDispatch.call( this, event ) === false ) {
			return;
		}

		// Determine handlers
		handlerQueue = jQuery.event.handlers.call( this, event, handlers );

		// Run delegates first; they may want to stop propagation beneath us
		i = 0;
		while ( (matched = handlerQueue[ i++ ]) && !event.isPropagationStopped() ) {
			event.currentTarget = matched.elem;

			j = 0;
			while ( (handleObj = matched.handlers[ j++ ]) && !event.isImmediatePropagationStopped() ) {

				// Triggered event must either 1) have no namespace, or
				// 2) have namespace(s) a subset or equal to those in the bound event (both can have no namespace).
				if ( !event.namespace_re || event.namespace_re.test( handleObj.namespace ) ) {

					event.handleObj = handleObj;
					event.data = handleObj.data;

					ret = ( (jQuery.event.special[ handleObj.origType ] || {}).handle || handleObj.handler )
							.apply( matched.elem, args );

					if ( ret !== undefined ) {
						if ( (event.result = ret) === false ) {
							event.preventDefault();
							event.stopPropagation();
						}
					}
				}
			}
		}

		// Call the postDispatch hook for the mapped type
		if ( special.postDispatch ) {
			special.postDispatch.call( this, event );
		}

		return event.result;
	},

	handlers: function( event, handlers ) {
		var sel, handleObj, matches, i,
			handlerQueue = [],
			delegateCount = handlers.delegateCount,
			cur = event.target;

		// Find delegate handlers
		// Black-hole SVG <use> instance trees (#13180)
		// Avoid non-left-click bubbling in Firefox (#3861)
		if ( delegateCount && cur.nodeType && (!event.button || event.type !== "click") ) {

			/* jshint eqeqeq: false */
			for ( ; cur != this; cur = cur.parentNode || this ) {
				/* jshint eqeqeq: true */

				// Don't check non-elements (#13208)
				// Don't process clicks on disabled elements (#6911, #8165, #11382, #11764)
				if ( cur.nodeType === 1 && (cur.disabled !== true || event.type !== "click") ) {
					matches = [];
					for ( i = 0; i < delegateCount; i++ ) {
						handleObj = handlers[ i ];

						// Don't conflict with Object.prototype properties (#13203)
						sel = handleObj.selector + " ";

						if ( matches[ sel ] === undefined ) {
							matches[ sel ] = handleObj.needsContext ?
								jQuery( sel, this ).index( cur ) >= 0 :
								jQuery.find( sel, this, null, [ cur ] ).length;
						}
						if ( matches[ sel ] ) {
							matches.push( handleObj );
						}
					}
					if ( matches.length ) {
						handlerQueue.push({ elem: cur, handlers: matches });
					}
				}
			}
		}

		// Add the remaining (directly-bound) handlers
		if ( delegateCount < handlers.length ) {
			handlerQueue.push({ elem: this, handlers: handlers.slice( delegateCount ) });
		}

		return handlerQueue;
	},

	fix: function( event ) {
		if ( event[ jQuery.expando ] ) {
			return event;
		}

		// Create a writable copy of the event object and normalize some properties
		var i, prop, copy,
			type = event.type,
			originalEvent = event,
			fixHook = this.fixHooks[ type ];

		if ( !fixHook ) {
			this.fixHooks[ type ] = fixHook =
				rmouseEvent.test( type ) ? this.mouseHooks :
				rkeyEvent.test( type ) ? this.keyHooks :
				{};
		}
		copy = fixHook.props ? this.props.concat( fixHook.props ) : this.props;

		event = new jQuery.Event( originalEvent );

		i = copy.length;
		while ( i-- ) {
			prop = copy[ i ];
			event[ prop ] = originalEvent[ prop ];
		}

		// Support: IE<9
		// Fix target property (#1925)
		if ( !event.target ) {
			event.target = originalEvent.srcElement || document;
		}

		// Support: Chrome 23+, Safari?
		// Target should not be a text node (#504, #13143)
		if ( event.target.nodeType === 3 ) {
			event.target = event.target.parentNode;
		}

		// Support: IE<9
		// For mouse/key events, metaKey==false if it's undefined (#3368, #11328)
		event.metaKey = !!event.metaKey;

		return fixHook.filter ? fixHook.filter( event, originalEvent ) : event;
	},

	// Includes some event props shared by KeyEvent and MouseEvent
	props: "altKey bubbles cancelable ctrlKey currentTarget eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),

	fixHooks: {},

	keyHooks: {
		props: "char charCode key keyCode".split(" "),
		filter: function( event, original ) {

			// Add which for key events
			if ( event.which == null ) {
				event.which = original.charCode != null ? original.charCode : original.keyCode;
			}

			return event;
		}
	},

	mouseHooks: {
		props: "button buttons clientX clientY fromElement offsetX offsetY pageX pageY screenX screenY toElement".split(" "),
		filter: function( event, original ) {
			var body, eventDoc, doc,
				button = original.button,
				fromElement = original.fromElement;

			// Calculate pageX/Y if missing and clientX/Y available
			if ( event.pageX == null && original.clientX != null ) {
				eventDoc = event.target.ownerDocument || document;
				doc = eventDoc.documentElement;
				body = eventDoc.body;

				event.pageX = original.clientX + ( doc && doc.scrollLeft || body && body.scrollLeft || 0 ) - ( doc && doc.clientLeft || body && body.clientLeft || 0 );
				event.pageY = original.clientY + ( doc && doc.scrollTop  || body && body.scrollTop  || 0 ) - ( doc && doc.clientTop  || body && body.clientTop  || 0 );
			}

			// Add relatedTarget, if necessary
			if ( !event.relatedTarget && fromElement ) {
				event.relatedTarget = fromElement === event.target ? original.toElement : fromElement;
			}

			// Add which for click: 1 === left; 2 === middle; 3 === right
			// Note: button is not normalized, so don't use it
			if ( !event.which && button !== undefined ) {
				event.which = ( button & 1 ? 1 : ( button & 2 ? 3 : ( button & 4 ? 2 : 0 ) ) );
			}

			return event;
		}
	},

	special: {
		load: {
			// Prevent triggered image.load events from bubbling to window.load
			noBubble: true
		},
		focus: {
			// Fire native event if possible so blur/focus sequence is correct
			trigger: function() {
				if ( this !== safeActiveElement() && this.focus ) {
					try {
						this.focus();
						return false;
					} catch ( e ) {
						// Support: IE<9
						// If we error on focus to hidden element (#1486, #12518),
						// let .trigger() run the handlers
					}
				}
			},
			delegateType: "focusin"
		},
		blur: {
			trigger: function() {
				if ( this === safeActiveElement() && this.blur ) {
					this.blur();
					return false;
				}
			},
			delegateType: "focusout"
		},
		click: {
			// For checkbox, fire native event so checked state will be right
			trigger: function() {
				if ( jQuery.nodeName( this, "input" ) && this.type === "checkbox" && this.click ) {
					this.click();
					return false;
				}
			},

			// For cross-browser consistency, don't fire native .click() on links
			_default: function( event ) {
				return jQuery.nodeName( event.target, "a" );
			}
		},

		beforeunload: {
			postDispatch: function( event ) {

				// Support: Firefox 20+
				// Firefox doesn't alert if the returnValue field is not set.
				if ( event.result !== undefined && event.originalEvent ) {
					event.originalEvent.returnValue = event.result;
				}
			}
		}
	},

	simulate: function( type, elem, event, bubble ) {
		// Piggyback on a donor event to simulate a different one.
		// Fake originalEvent to avoid donor's stopPropagation, but if the
		// simulated event prevents default then we do the same on the donor.
		var e = jQuery.extend(
			new jQuery.Event(),
			event,
			{
				type: type,
				isSimulated: true,
				originalEvent: {}
			}
		);
		if ( bubble ) {
			jQuery.event.trigger( e, null, elem );
		} else {
			jQuery.event.dispatch.call( elem, e );
		}
		if ( e.isDefaultPrevented() ) {
			event.preventDefault();
		}
	}
};

jQuery.removeEvent = document.removeEventListener ?
	function( elem, type, handle ) {
		if ( elem.removeEventListener ) {
			elem.removeEventListener( type, handle, false );
		}
	} :
	function( elem, type, handle ) {
		var name = "on" + type;

		if ( elem.detachEvent ) {

			// #8545, #7054, preventing memory leaks for custom events in IE6-8
			// detachEvent needed property on element, by name of that event, to properly expose it to GC
			if ( typeof elem[ name ] === strundefined ) {
				elem[ name ] = null;
			}

			elem.detachEvent( name, handle );
		}
	};

jQuery.Event = function( src, props ) {
	// Allow instantiation without the 'new' keyword
	if ( !(this instanceof jQuery.Event) ) {
		return new jQuery.Event( src, props );
	}

	// Event object
	if ( src && src.type ) {
		this.originalEvent = src;
		this.type = src.type;

		// Events bubbling up the document may have been marked as prevented
		// by a handler lower down the tree; reflect the correct value.
		this.isDefaultPrevented = src.defaultPrevented ||
				src.defaultPrevented === undefined &&
				// Support: IE < 9, Android < 4.0
				src.returnValue === false ?
			returnTrue :
			returnFalse;

	// Event type
	} else {
		this.type = src;
	}

	// Put explicitly provided properties onto the event object
	if ( props ) {
		jQuery.extend( this, props );
	}

	// Create a timestamp if incoming event doesn't have one
	this.timeStamp = src && src.timeStamp || jQuery.now();

	// Mark it as fixed
	this[ jQuery.expando ] = true;
};

// jQuery.Event is based on DOM3 Events as specified by the ECMAScript Language Binding
// http://www.w3.org/TR/2003/WD-DOM-Level-3-Events-20030331/ecma-script-binding.html
jQuery.Event.prototype = {
	isDefaultPrevented: returnFalse,
	isPropagationStopped: returnFalse,
	isImmediatePropagationStopped: returnFalse,

	preventDefault: function() {
		var e = this.originalEvent;

		this.isDefaultPrevented = returnTrue;
		if ( !e ) {
			return;
		}

		// If preventDefault exists, run it on the original event
		if ( e.preventDefault ) {
			e.preventDefault();

		// Support: IE
		// Otherwise set the returnValue property of the original event to false
		} else {
			e.returnValue = false;
		}
	},
	stopPropagation: function() {
		var e = this.originalEvent;

		this.isPropagationStopped = returnTrue;
		if ( !e ) {
			return;
		}
		// If stopPropagation exists, run it on the original event
		if ( e.stopPropagation ) {
			e.stopPropagation();
		}

		// Support: IE
		// Set the cancelBubble property of the original event to true
		e.cancelBubble = true;
	},
	stopImmediatePropagation: function() {
		var e = this.originalEvent;

		this.isImmediatePropagationStopped = returnTrue;

		if ( e && e.stopImmediatePropagation ) {
			e.stopImmediatePropagation();
		}

		this.stopPropagation();
	}
};

// Create mouseenter/leave events using mouseover/out and event-time checks
jQuery.each({
	mouseenter: "mouseover",
	mouseleave: "mouseout",
	pointerenter: "pointerover",
	pointerleave: "pointerout"
}, function( orig, fix ) {
	jQuery.event.special[ orig ] = {
		delegateType: fix,
		bindType: fix,

		handle: function( event ) {
			var ret,
				target = this,
				related = event.relatedTarget,
				handleObj = event.handleObj;

			// For mousenter/leave call the handler if related is outside the target.
			// NB: No relatedTarget if the mouse left/entered the browser window
			if ( !related || (related !== target && !jQuery.contains( target, related )) ) {
				event.type = handleObj.origType;
				ret = handleObj.handler.apply( this, arguments );
				event.type = fix;
			}
			return ret;
		}
	};
});

// IE submit delegation
if ( !support.submitBubbles ) {

	jQuery.event.special.submit = {
		setup: function() {
			// Only need this for delegated form submit events
			if ( jQuery.nodeName( this, "form" ) ) {
				return false;
			}

			// Lazy-add a submit handler when a descendant form may potentially be submitted
			jQuery.event.add( this, "click._submit keypress._submit", function( e ) {
				// Node name check avoids a VML-related crash in IE (#9807)
				var elem = e.target,
					form = jQuery.nodeName( elem, "input" ) || jQuery.nodeName( elem, "button" ) ? elem.form : undefined;
				if ( form && !jQuery._data( form, "submitBubbles" ) ) {
					jQuery.event.add( form, "submit._submit", function( event ) {
						event._submit_bubble = true;
					});
					jQuery._data( form, "submitBubbles", true );
				}
			});
			// return undefined since we don't need an event listener
		},

		postDispatch: function( event ) {
			// If form was submitted by the user, bubble the event up the tree
			if ( event._submit_bubble ) {
				delete event._submit_bubble;
				if ( this.parentNode && !event.isTrigger ) {
					jQuery.event.simulate( "submit", this.parentNode, event, true );
				}
			}
		},

		teardown: function() {
			// Only need this for delegated form submit events
			if ( jQuery.nodeName( this, "form" ) ) {
				return false;
			}

			// Remove delegated handlers; cleanData eventually reaps submit handlers attached above
			jQuery.event.remove( this, "._submit" );
		}
	};
}

// IE change delegation and checkbox/radio fix
if ( !support.changeBubbles ) {

	jQuery.event.special.change = {

		setup: function() {

			if ( rformElems.test( this.nodeName ) ) {
				// IE doesn't fire change on a check/radio until blur; trigger it on click
				// after a propertychange. Eat the blur-change in special.change.handle.
				// This still fires onchange a second time for check/radio after blur.
				if ( this.type === "checkbox" || this.type === "radio" ) {
					jQuery.event.add( this, "propertychange._change", function( event ) {
						if ( event.originalEvent.propertyName === "checked" ) {
							this._just_changed = true;
						}
					});
					jQuery.event.add( this, "click._change", function( event ) {
						if ( this._just_changed && !event.isTrigger ) {
							this._just_changed = false;
						}
						// Allow triggered, simulated change events (#11500)
						jQuery.event.simulate( "change", this, event, true );
					});
				}
				return false;
			}
			// Delegated event; lazy-add a change handler on descendant inputs
			jQuery.event.add( this, "beforeactivate._change", function( e ) {
				var elem = e.target;

				if ( rformElems.test( elem.nodeName ) && !jQuery._data( elem, "changeBubbles" ) ) {
					jQuery.event.add( elem, "change._change", function( event ) {
						if ( this.parentNode && !event.isSimulated && !event.isTrigger ) {
							jQuery.event.simulate( "change", this.parentNode, event, true );
						}
					});
					jQuery._data( elem, "changeBubbles", true );
				}
			});
		},

		handle: function( event ) {
			var elem = event.target;

			// Swallow native change events from checkbox/radio, we already triggered them above
			if ( this !== elem || event.isSimulated || event.isTrigger || (elem.type !== "radio" && elem.type !== "checkbox") ) {
				return event.handleObj.handler.apply( this, arguments );
			}
		},

		teardown: function() {
			jQuery.event.remove( this, "._change" );

			return !rformElems.test( this.nodeName );
		}
	};
}

// Create "bubbling" focus and blur events
if ( !support.focusinBubbles ) {
	jQuery.each({ focus: "focusin", blur: "focusout" }, function( orig, fix ) {

		// Attach a single capturing handler on the document while someone wants focusin/focusout
		var handler = function( event ) {
				jQuery.event.simulate( fix, event.target, jQuery.event.fix( event ), true );
			};

		jQuery.event.special[ fix ] = {
			setup: function() {
				var doc = this.ownerDocument || this,
					attaches = jQuery._data( doc, fix );

				if ( !attaches ) {
					doc.addEventListener( orig, handler, true );
				}
				jQuery._data( doc, fix, ( attaches || 0 ) + 1 );
			},
			teardown: function() {
				var doc = this.ownerDocument || this,
					attaches = jQuery._data( doc, fix ) - 1;

				if ( !attaches ) {
					doc.removeEventListener( orig, handler, true );
					jQuery._removeData( doc, fix );
				} else {
					jQuery._data( doc, fix, attaches );
				}
			}
		};
	});
}

jQuery.fn.extend({

	on: function( types, selector, data, fn, /*INTERNAL*/ one ) {
		var type, origFn;

		// Types can be a map of types/handlers
		if ( typeof types === "object" ) {
			// ( types-Object, selector, data )
			if ( typeof selector !== "string" ) {
				// ( types-Object, data )
				data = data || selector;
				selector = undefined;
			}
			for ( type in types ) {
				this.on( type, selector, data, types[ type ], one );
			}
			return this;
		}

		if ( data == null && fn == null ) {
			// ( types, fn )
			fn = selector;
			data = selector = undefined;
		} else if ( fn == null ) {
			if ( typeof selector === "string" ) {
				// ( types, selector, fn )
				fn = data;
				data = undefined;
			} else {
				// ( types, data, fn )
				fn = data;
				data = selector;
				selector = undefined;
			}
		}
		if ( fn === false ) {
			fn = returnFalse;
		} else if ( !fn ) {
			return this;
		}

		if ( one === 1 ) {
			origFn = fn;
			fn = function( event ) {
				// Can use an empty set, since event contains the info
				jQuery().off( event );
				return origFn.apply( this, arguments );
			};
			// Use same guid so caller can remove using origFn
			fn.guid = origFn.guid || ( origFn.guid = jQuery.guid++ );
		}
		return this.each( function() {
			jQuery.event.add( this, types, fn, data, selector );
		});
	},
	one: function( types, selector, data, fn ) {
		return this.on( types, selector, data, fn, 1 );
	},
	off: function( types, selector, fn ) {
		var handleObj, type;
		if ( types && types.preventDefault && types.handleObj ) {
			// ( event )  dispatched jQuery.Event
			handleObj = types.handleObj;
			jQuery( types.delegateTarget ).off(
				handleObj.namespace ? handleObj.origType + "." + handleObj.namespace : handleObj.origType,
				handleObj.selector,
				handleObj.handler
			);
			return this;
		}
		if ( typeof types === "object" ) {
			// ( types-object [, selector] )
			for ( type in types ) {
				this.off( type, selector, types[ type ] );
			}
			return this;
		}
		if ( selector === false || typeof selector === "function" ) {
			// ( types [, fn] )
			fn = selector;
			selector = undefined;
		}
		if ( fn === false ) {
			fn = returnFalse;
		}
		return this.each(function() {
			jQuery.event.remove( this, types, fn, selector );
		});
	},

	trigger: function( type, data ) {
		return this.each(function() {
			jQuery.event.trigger( type, data, this );
		});
	},
	triggerHandler: function( type, data ) {
		var elem = this[0];
		if ( elem ) {
			return jQuery.event.trigger( type, data, elem, true );
		}
	}
});


function createSafeFragment( document ) {
	var list = nodeNames.split( "|" ),
		safeFrag = document.createDocumentFragment();

	if ( safeFrag.createElement ) {
		while ( list.length ) {
			safeFrag.createElement(
				list.pop()
			);
		}
	}
	return safeFrag;
}

var nodeNames = "abbr|article|aside|audio|bdi|canvas|data|datalist|details|figcaption|figure|footer|" +
		"header|hgroup|mark|meter|nav|output|progress|section|summary|time|video",
	rinlinejQuery = / jQuery\d+="(?:null|\d+)"/g,
	rnoshimcache = new RegExp("<(?:" + nodeNames + ")[\\s/>]", "i"),
	rleadingWhitespace = /^\s+/,
	rxhtmlTag = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/gi,
	rtagName = /<([\w:]+)/,
	rtbody = /<tbody/i,
	rhtml = /<|&#?\w+;/,
	rnoInnerhtml = /<(?:script|style|link)/i,
	// checked="checked" or checked
	rchecked = /checked\s*(?:[^=]|=\s*.checked.)/i,
	rscriptType = /^$|\/(?:java|ecma)script/i,
	rscriptTypeMasked = /^true\/(.*)/,
	rcleanScript = /^\s*<!(?:\[CDATA\[|--)|(?:\]\]|--)>\s*$/g,

	// We have to close these tags to support XHTML (#13200)
	wrapMap = {
		option: [ 1, "<select multiple='multiple'>", "</select>" ],
		legend: [ 1, "<fieldset>", "</fieldset>" ],
		area: [ 1, "<map>", "</map>" ],
		param: [ 1, "<object>", "</object>" ],
		thead: [ 1, "<table>", "</table>" ],
		tr: [ 2, "<table><tbody>", "</tbody></table>" ],
		col: [ 2, "<table><tbody></tbody><colgroup>", "</colgroup></table>" ],
		td: [ 3, "<table><tbody><tr>", "</tr></tbody></table>" ],

		// IE6-8 can't serialize link, script, style, or any html5 (NoScope) tags,
		// unless wrapped in a div with non-breaking characters in front of it.
		_default: support.htmlSerialize ? [ 0, "", "" ] : [ 1, "X<div>", "</div>"  ]
	},
	safeFragment = createSafeFragment( document ),
	fragmentDiv = safeFragment.appendChild( document.createElement("div") );

wrapMap.optgroup = wrapMap.option;
wrapMap.tbody = wrapMap.tfoot = wrapMap.colgroup = wrapMap.caption = wrapMap.thead;
wrapMap.th = wrapMap.td;

function getAll( context, tag ) {
	var elems, elem,
		i = 0,
		found = typeof context.getElementsByTagName !== strundefined ? context.getElementsByTagName( tag || "*" ) :
			typeof context.querySelectorAll !== strundefined ? context.querySelectorAll( tag || "*" ) :
			undefined;

	if ( !found ) {
		for ( found = [], elems = context.childNodes || context; (elem = elems[i]) != null; i++ ) {
			if ( !tag || jQuery.nodeName( elem, tag ) ) {
				found.push( elem );
			} else {
				jQuery.merge( found, getAll( elem, tag ) );
			}
		}
	}

	return tag === undefined || tag && jQuery.nodeName( context, tag ) ?
		jQuery.merge( [ context ], found ) :
		found;
}

// Used in buildFragment, fixes the defaultChecked property
function fixDefaultChecked( elem ) {
	if ( rcheckableType.test( elem.type ) ) {
		elem.defaultChecked = elem.checked;
	}
}

// Support: IE<8
// Manipulating tables requires a tbody
function manipulationTarget( elem, content ) {
	return jQuery.nodeName( elem, "table" ) &&
		jQuery.nodeName( content.nodeType !== 11 ? content : content.firstChild, "tr" ) ?

		elem.getElementsByTagName("tbody")[0] ||
			elem.appendChild( elem.ownerDocument.createElement("tbody") ) :
		elem;
}

// Replace/restore the type attribute of script elements for safe DOM manipulation
function disableScript( elem ) {
	elem.type = (jQuery.find.attr( elem, "type" ) !== null) + "/" + elem.type;
	return elem;
}
function restoreScript( elem ) {
	var match = rscriptTypeMasked.exec( elem.type );
	if ( match ) {
		elem.type = match[1];
	} else {
		elem.removeAttribute("type");
	}
	return elem;
}

// Mark scripts as having already been evaluated
function setGlobalEval( elems, refElements ) {
	var elem,
		i = 0;
	for ( ; (elem = elems[i]) != null; i++ ) {
		jQuery._data( elem, "globalEval", !refElements || jQuery._data( refElements[i], "globalEval" ) );
	}
}

function cloneCopyEvent( src, dest ) {

	if ( dest.nodeType !== 1 || !jQuery.hasData( src ) ) {
		return;
	}

	var type, i, l,
		oldData = jQuery._data( src ),
		curData = jQuery._data( dest, oldData ),
		events = oldData.events;

	if ( events ) {
		delete curData.handle;
		curData.events = {};

		for ( type in events ) {
			for ( i = 0, l = events[ type ].length; i < l; i++ ) {
				jQuery.event.add( dest, type, events[ type ][ i ] );
			}
		}
	}

	// make the cloned public data object a copy from the original
	if ( curData.data ) {
		curData.data = jQuery.extend( {}, curData.data );
	}
}

function fixCloneNodeIssues( src, dest ) {
	var nodeName, e, data;

	// We do not need to do anything for non-Elements
	if ( dest.nodeType !== 1 ) {
		return;
	}

	nodeName = dest.nodeName.toLowerCase();

	// IE6-8 copies events bound via attachEvent when using cloneNode.
	if ( !support.noCloneEvent && dest[ jQuery.expando ] ) {
		data = jQuery._data( dest );

		for ( e in data.events ) {
			jQuery.removeEvent( dest, e, data.handle );
		}

		// Event data gets referenced instead of copied if the expando gets copied too
		dest.removeAttribute( jQuery.expando );
	}

	// IE blanks contents when cloning scripts, and tries to evaluate newly-set text
	if ( nodeName === "script" && dest.text !== src.text ) {
		disableScript( dest ).text = src.text;
		restoreScript( dest );

	// IE6-10 improperly clones children of object elements using classid.
	// IE10 throws NoModificationAllowedError if parent is null, #12132.
	} else if ( nodeName === "object" ) {
		if ( dest.parentNode ) {
			dest.outerHTML = src.outerHTML;
		}

		// This path appears unavoidable for IE9. When cloning an object
		// element in IE9, the outerHTML strategy above is not sufficient.
		// If the src has innerHTML and the destination does not,
		// copy the src.innerHTML into the dest.innerHTML. #10324
		if ( support.html5Clone && ( src.innerHTML && !jQuery.trim(dest.innerHTML) ) ) {
			dest.innerHTML = src.innerHTML;
		}

	} else if ( nodeName === "input" && rcheckableType.test( src.type ) ) {
		// IE6-8 fails to persist the checked state of a cloned checkbox
		// or radio button. Worse, IE6-7 fail to give the cloned element
		// a checked appearance if the defaultChecked value isn't also set

		dest.defaultChecked = dest.checked = src.checked;

		// IE6-7 get confused and end up setting the value of a cloned
		// checkbox/radio button to an empty string instead of "on"
		if ( dest.value !== src.value ) {
			dest.value = src.value;
		}

	// IE6-8 fails to return the selected option to the default selected
	// state when cloning options
	} else if ( nodeName === "option" ) {
		dest.defaultSelected = dest.selected = src.defaultSelected;

	// IE6-8 fails to set the defaultValue to the correct value when
	// cloning other types of input fields
	} else if ( nodeName === "input" || nodeName === "textarea" ) {
		dest.defaultValue = src.defaultValue;
	}
}

jQuery.extend({
	clone: function( elem, dataAndEvents, deepDataAndEvents ) {
		var destElements, node, clone, i, srcElements,
			inPage = jQuery.contains( elem.ownerDocument, elem );

		if ( support.html5Clone || jQuery.isXMLDoc(elem) || !rnoshimcache.test( "<" + elem.nodeName + ">" ) ) {
			clone = elem.cloneNode( true );

		// IE<=8 does not properly clone detached, unknown element nodes
		} else {
			fragmentDiv.innerHTML = elem.outerHTML;
			fragmentDiv.removeChild( clone = fragmentDiv.firstChild );
		}

		if ( (!support.noCloneEvent || !support.noCloneChecked) &&
				(elem.nodeType === 1 || elem.nodeType === 11) && !jQuery.isXMLDoc(elem) ) {

			// We eschew Sizzle here for performance reasons: http://jsperf.com/getall-vs-sizzle/2
			destElements = getAll( clone );
			srcElements = getAll( elem );

			// Fix all IE cloning issues
			for ( i = 0; (node = srcElements[i]) != null; ++i ) {
				// Ensure that the destination node is not null; Fixes #9587
				if ( destElements[i] ) {
					fixCloneNodeIssues( node, destElements[i] );
				}
			}
		}

		// Copy the events from the original to the clone
		if ( dataAndEvents ) {
			if ( deepDataAndEvents ) {
				srcElements = srcElements || getAll( elem );
				destElements = destElements || getAll( clone );

				for ( i = 0; (node = srcElements[i]) != null; i++ ) {
					cloneCopyEvent( node, destElements[i] );
				}
			} else {
				cloneCopyEvent( elem, clone );
			}
		}

		// Preserve script evaluation history
		destElements = getAll( clone, "script" );
		if ( destElements.length > 0 ) {
			setGlobalEval( destElements, !inPage && getAll( elem, "script" ) );
		}

		destElements = srcElements = node = null;

		// Return the cloned set
		return clone;
	},

	buildFragment: function( elems, context, scripts, selection ) {
		var j, elem, contains,
			tmp, tag, tbody, wrap,
			l = elems.length,

			// Ensure a safe fragment
			safe = createSafeFragment( context ),

			nodes = [],
			i = 0;

		for ( ; i < l; i++ ) {
			elem = elems[ i ];

			if ( elem || elem === 0 ) {

				// Add nodes directly
				if ( jQuery.type( elem ) === "object" ) {
					jQuery.merge( nodes, elem.nodeType ? [ elem ] : elem );

				// Convert non-html into a text node
				} else if ( !rhtml.test( elem ) ) {
					nodes.push( context.createTextNode( elem ) );

				// Convert html into DOM nodes
				} else {
					tmp = tmp || safe.appendChild( context.createElement("div") );

					// Deserialize a standard representation
					tag = (rtagName.exec( elem ) || [ "", "" ])[ 1 ].toLowerCase();
					wrap = wrapMap[ tag ] || wrapMap._default;

					tmp.innerHTML = wrap[1] + elem.replace( rxhtmlTag, "<$1></$2>" ) + wrap[2];

					// Descend through wrappers to the right content
					j = wrap[0];
					while ( j-- ) {
						tmp = tmp.lastChild;
					}

					// Manually add leading whitespace removed by IE
					if ( !support.leadingWhitespace && rleadingWhitespace.test( elem ) ) {
						nodes.push( context.createTextNode( rleadingWhitespace.exec( elem )[0] ) );
					}

					// Remove IE's autoinserted <tbody> from table fragments
					if ( !support.tbody ) {

						// String was a <table>, *may* have spurious <tbody>
						elem = tag === "table" && !rtbody.test( elem ) ?
							tmp.firstChild :

							// String was a bare <thead> or <tfoot>
							wrap[1] === "<table>" && !rtbody.test( elem ) ?
								tmp :
								0;

						j = elem && elem.childNodes.length;
						while ( j-- ) {
							if ( jQuery.nodeName( (tbody = elem.childNodes[j]), "tbody" ) && !tbody.childNodes.length ) {
								elem.removeChild( tbody );
							}
						}
					}

					jQuery.merge( nodes, tmp.childNodes );

					// Fix #12392 for WebKit and IE > 9
					tmp.textContent = "";

					// Fix #12392 for oldIE
					while ( tmp.firstChild ) {
						tmp.removeChild( tmp.firstChild );
					}

					// Remember the top-level container for proper cleanup
					tmp = safe.lastChild;
				}
			}
		}

		// Fix #11356: Clear elements from fragment
		if ( tmp ) {
			safe.removeChild( tmp );
		}

		// Reset defaultChecked for any radios and checkboxes
		// about to be appended to the DOM in IE 6/7 (#8060)
		if ( !support.appendChecked ) {
			jQuery.grep( getAll( nodes, "input" ), fixDefaultChecked );
		}

		i = 0;
		while ( (elem = nodes[ i++ ]) ) {

			// #4087 - If origin and destination elements are the same, and this is
			// that element, do not do anything
			if ( selection && jQuery.inArray( elem, selection ) !== -1 ) {
				continue;
			}

			contains = jQuery.contains( elem.ownerDocument, elem );

			// Append to fragment
			tmp = getAll( safe.appendChild( elem ), "script" );

			// Preserve script evaluation history
			if ( contains ) {
				setGlobalEval( tmp );
			}

			// Capture executables
			if ( scripts ) {
				j = 0;
				while ( (elem = tmp[ j++ ]) ) {
					if ( rscriptType.test( elem.type || "" ) ) {
						scripts.push( elem );
					}
				}
			}
		}

		tmp = null;

		return safe;
	},

	cleanData: function( elems, /* internal */ acceptData ) {
		var elem, type, id, data,
			i = 0,
			internalKey = jQuery.expando,
			cache = jQuery.cache,
			deleteExpando = support.deleteExpando,
			special = jQuery.event.special;

		for ( ; (elem = elems[i]) != null; i++ ) {
			if ( acceptData || jQuery.acceptData( elem ) ) {

				id = elem[ internalKey ];
				data = id && cache[ id ];

				if ( data ) {
					if ( data.events ) {
						for ( type in data.events ) {
							if ( special[ type ] ) {
								jQuery.event.remove( elem, type );

							// This is a shortcut to avoid jQuery.event.remove's overhead
							} else {
								jQuery.removeEvent( elem, type, data.handle );
							}
						}
					}

					// Remove cache only if it was not already removed by jQuery.event.remove
					if ( cache[ id ] ) {

						delete cache[ id ];

						// IE does not allow us to delete expando properties from nodes,
						// nor does it have a removeAttribute function on Document nodes;
						// we must handle all of these cases
						if ( deleteExpando ) {
							delete elem[ internalKey ];

						} else if ( typeof elem.removeAttribute !== strundefined ) {
							elem.removeAttribute( internalKey );

						} else {
							elem[ internalKey ] = null;
						}

						deletedIds.push( id );
					}
				}
			}
		}
	}
});

jQuery.fn.extend({
	text: function( value ) {
		return access( this, function( value ) {
			return value === undefined ?
				jQuery.text( this ) :
				this.empty().append( ( this[0] && this[0].ownerDocument || document ).createTextNode( value ) );
		}, null, value, arguments.length );
	},

	append: function() {
		return this.domManip( arguments, function( elem ) {
			if ( this.nodeType === 1 || this.nodeType === 11 || this.nodeType === 9 ) {
				var target = manipulationTarget( this, elem );
				target.appendChild( elem );
			}
		});
	},

	prepend: function() {
		return this.domManip( arguments, function( elem ) {
			if ( this.nodeType === 1 || this.nodeType === 11 || this.nodeType === 9 ) {
				var target = manipulationTarget( this, elem );
				target.insertBefore( elem, target.firstChild );
			}
		});
	},

	before: function() {
		return this.domManip( arguments, function( elem ) {
			if ( this.parentNode ) {
				this.parentNode.insertBefore( elem, this );
			}
		});
	},

	after: function() {
		return this.domManip( arguments, function( elem ) {
			if ( this.parentNode ) {
				this.parentNode.insertBefore( elem, this.nextSibling );
			}
		});
	},

	remove: function( selector, keepData /* Internal Use Only */ ) {
		var elem,
			elems = selector ? jQuery.filter( selector, this ) : this,
			i = 0;

		for ( ; (elem = elems[i]) != null; i++ ) {

			if ( !keepData && elem.nodeType === 1 ) {
				jQuery.cleanData( getAll( elem ) );
			}

			if ( elem.parentNode ) {
				if ( keepData && jQuery.contains( elem.ownerDocument, elem ) ) {
					setGlobalEval( getAll( elem, "script" ) );
				}
				elem.parentNode.removeChild( elem );
			}
		}

		return this;
	},

	empty: function() {
		var elem,
			i = 0;

		for ( ; (elem = this[i]) != null; i++ ) {
			// Remove element nodes and prevent memory leaks
			if ( elem.nodeType === 1 ) {
				jQuery.cleanData( getAll( elem, false ) );
			}

			// Remove any remaining nodes
			while ( elem.firstChild ) {
				elem.removeChild( elem.firstChild );
			}

			// If this is a select, ensure that it displays empty (#12336)
			// Support: IE<9
			if ( elem.options && jQuery.nodeName( elem, "select" ) ) {
				elem.options.length = 0;
			}
		}

		return this;
	},

	clone: function( dataAndEvents, deepDataAndEvents ) {
		dataAndEvents = dataAndEvents == null ? false : dataAndEvents;
		deepDataAndEvents = deepDataAndEvents == null ? dataAndEvents : deepDataAndEvents;

		return this.map(function() {
			return jQuery.clone( this, dataAndEvents, deepDataAndEvents );
		});
	},

	html: function( value ) {
		return access( this, function( value ) {
			var elem = this[ 0 ] || {},
				i = 0,
				l = this.length;

			if ( value === undefined ) {
				return elem.nodeType === 1 ?
					elem.innerHTML.replace( rinlinejQuery, "" ) :
					undefined;
			}

			// See if we can take a shortcut and just use innerHTML
			if ( typeof value === "string" && !rnoInnerhtml.test( value ) &&
				( support.htmlSerialize || !rnoshimcache.test( value )  ) &&
				( support.leadingWhitespace || !rleadingWhitespace.test( value ) ) &&
				!wrapMap[ (rtagName.exec( value ) || [ "", "" ])[ 1 ].toLowerCase() ] ) {

				value = value.replace( rxhtmlTag, "<$1></$2>" );

				try {
					for (; i < l; i++ ) {
						// Remove element nodes and prevent memory leaks
						elem = this[i] || {};
						if ( elem.nodeType === 1 ) {
							jQuery.cleanData( getAll( elem, false ) );
							elem.innerHTML = value;
						}
					}

					elem = 0;

				// If using innerHTML throws an exception, use the fallback method
				} catch(e) {}
			}

			if ( elem ) {
				this.empty().append( value );
			}
		}, null, value, arguments.length );
	},

	replaceWith: function() {
		var arg = arguments[ 0 ];

		// Make the changes, replacing each context element with the new content
		this.domManip( arguments, function( elem ) {
			arg = this.parentNode;

			jQuery.cleanData( getAll( this ) );

			if ( arg ) {
				arg.replaceChild( elem, this );
			}
		});

		// Force removal if there was no new content (e.g., from empty arguments)
		return arg && (arg.length || arg.nodeType) ? this : this.remove();
	},

	detach: function( selector ) {
		return this.remove( selector, true );
	},

	domManip: function( args, callback ) {

		// Flatten any nested arrays
		args = concat.apply( [], args );

		var first, node, hasScripts,
			scripts, doc, fragment,
			i = 0,
			l = this.length,
			set = this,
			iNoClone = l - 1,
			value = args[0],
			isFunction = jQuery.isFunction( value );

		// We can't cloneNode fragments that contain checked, in WebKit
		if ( isFunction ||
				( l > 1 && typeof value === "string" &&
					!support.checkClone && rchecked.test( value ) ) ) {
			return this.each(function( index ) {
				var self = set.eq( index );
				if ( isFunction ) {
					args[0] = value.call( this, index, self.html() );
				}
				self.domManip( args, callback );
			});
		}

		if ( l ) {
			fragment = jQuery.buildFragment( args, this[ 0 ].ownerDocument, false, this );
			first = fragment.firstChild;

			if ( fragment.childNodes.length === 1 ) {
				fragment = first;
			}

			if ( first ) {
				scripts = jQuery.map( getAll( fragment, "script" ), disableScript );
				hasScripts = scripts.length;

				// Use the original fragment for the last item instead of the first because it can end up
				// being emptied incorrectly in certain situations (#8070).
				for ( ; i < l; i++ ) {
					node = fragment;

					if ( i !== iNoClone ) {
						node = jQuery.clone( node, true, true );

						// Keep references to cloned scripts for later restoration
						if ( hasScripts ) {
							jQuery.merge( scripts, getAll( node, "script" ) );
						}
					}

					callback.call( this[i], node, i );
				}

				if ( hasScripts ) {
					doc = scripts[ scripts.length - 1 ].ownerDocument;

					// Reenable scripts
					jQuery.map( scripts, restoreScript );

					// Evaluate executable scripts on first document insertion
					for ( i = 0; i < hasScripts; i++ ) {
						node = scripts[ i ];
						if ( rscriptType.test( node.type || "" ) &&
							!jQuery._data( node, "globalEval" ) && jQuery.contains( doc, node ) ) {

							if ( node.src ) {
								// Optional AJAX dependency, but won't run scripts if not present
								if ( jQuery._evalUrl ) {
									jQuery._evalUrl( node.src );
								}
							} else {
								jQuery.globalEval( ( node.text || node.textContent || node.innerHTML || "" ).replace( rcleanScript, "" ) );
							}
						}
					}
				}

				// Fix #11809: Avoid leaking memory
				fragment = first = null;
			}
		}

		return this;
	}
});

jQuery.each({
	appendTo: "append",
	prependTo: "prepend",
	insertBefore: "before",
	insertAfter: "after",
	replaceAll: "replaceWith"
}, function( name, original ) {
	jQuery.fn[ name ] = function( selector ) {
		var elems,
			i = 0,
			ret = [],
			insert = jQuery( selector ),
			last = insert.length - 1;

		for ( ; i <= last; i++ ) {
			elems = i === last ? this : this.clone(true);
			jQuery( insert[i] )[ original ]( elems );

			// Modern browsers can apply jQuery collections as arrays, but oldIE needs a .get()
			push.apply( ret, elems.get() );
		}

		return this.pushStack( ret );
	};
});


var iframe,
	elemdisplay = {};

/**
 * Retrieve the actual display of a element
 * @param {String} name nodeName of the element
 * @param {Object} doc Document object
 */
// Called only from within defaultDisplay
function actualDisplay( name, doc ) {
	var style,
		elem = jQuery( doc.createElement( name ) ).appendTo( doc.body ),

		// getDefaultComputedStyle might be reliably used only on attached element
		display = window.getDefaultComputedStyle && ( style = window.getDefaultComputedStyle( elem[ 0 ] ) ) ?

			// Use of this method is a temporary fix (more like optmization) until something better comes along,
			// since it was removed from specification and supported only in FF
			style.display : jQuery.css( elem[ 0 ], "display" );

	// We don't have any data stored on the element,
	// so use "detach" method as fast way to get rid of the element
	elem.detach();

	return display;
}

/**
 * Try to determine the default display value of an element
 * @param {String} nodeName
 */
function defaultDisplay( nodeName ) {
	var doc = document,
		display = elemdisplay[ nodeName ];

	if ( !display ) {
		display = actualDisplay( nodeName, doc );

		// If the simple way fails, read from inside an iframe
		if ( display === "none" || !display ) {

			// Use the already-created iframe if possible
			iframe = (iframe || jQuery( "<iframe frameborder='0' width='0' height='0'/>" )).appendTo( doc.documentElement );

			// Always write a new HTML skeleton so Webkit and Firefox don't choke on reuse
			doc = ( iframe[ 0 ].contentWindow || iframe[ 0 ].contentDocument ).document;

			// Support: IE
			doc.write();
			doc.close();

			display = actualDisplay( nodeName, doc );
			iframe.detach();
		}

		// Store the correct default display
		elemdisplay[ nodeName ] = display;
	}

	return display;
}


(function() {
	var shrinkWrapBlocksVal;

	support.shrinkWrapBlocks = function() {
		if ( shrinkWrapBlocksVal != null ) {
			return shrinkWrapBlocksVal;
		}

		// Will be changed later if needed.
		shrinkWrapBlocksVal = false;

		// Minified: var b,c,d
		var div, body, container;

		body = document.getElementsByTagName( "body" )[ 0 ];
		if ( !body || !body.style ) {
			// Test fired too early or in an unsupported environment, exit.
			return;
		}

		// Setup
		div = document.createElement( "div" );
		container = document.createElement( "div" );
		container.style.cssText = "position:absolute;border:0;width:0;height:0;top:0;left:-9999px";
		body.appendChild( container ).appendChild( div );

		// Support: IE6
		// Check if elements with layout shrink-wrap their children
		if ( typeof div.style.zoom !== strundefined ) {
			// Reset CSS: box-sizing; display; margin; border
			div.style.cssText =
				// Support: Firefox<29, Android 2.3
				// Vendor-prefix box-sizing
				"-webkit-box-sizing:content-box;-moz-box-sizing:content-box;" +
				"box-sizing:content-box;display:block;margin:0;border:0;" +
				"padding:1px;width:1px;zoom:1";
			div.appendChild( document.createElement( "div" ) ).style.width = "5px";
			shrinkWrapBlocksVal = div.offsetWidth !== 3;
		}

		body.removeChild( container );

		return shrinkWrapBlocksVal;
	};

})();
var rmargin = (/^margin/);

var rnumnonpx = new RegExp( "^(" + pnum + ")(?!px)[a-z%]+$", "i" );



var getStyles, curCSS,
	rposition = /^(top|right|bottom|left)$/;

if ( window.getComputedStyle ) {
	getStyles = function( elem ) {
		return elem.ownerDocument.defaultView.getComputedStyle( elem, null );
	};

	curCSS = function( elem, name, computed ) {
		var width, minWidth, maxWidth, ret,
			style = elem.style;

		computed = computed || getStyles( elem );

		// getPropertyValue is only needed for .css('filter') in IE9, see #12537
		ret = computed ? computed.getPropertyValue( name ) || computed[ name ] : undefined;

		if ( computed ) {

			if ( ret === "" && !jQuery.contains( elem.ownerDocument, elem ) ) {
				ret = jQuery.style( elem, name );
			}

			// A tribute to the "awesome hack by Dean Edwards"
			// Chrome < 17 and Safari 5.0 uses "computed value" instead of "used value" for margin-right
			// Safari 5.1.7 (at least) returns percentage for a larger set of values, but width seems to be reliably pixels
			// this is against the CSSOM draft spec: http://dev.w3.org/csswg/cssom/#resolved-values
			if ( rnumnonpx.test( ret ) && rmargin.test( name ) ) {

				// Remember the original values
				width = style.width;
				minWidth = style.minWidth;
				maxWidth = style.maxWidth;

				// Put in the new values to get a computed value out
				style.minWidth = style.maxWidth = style.width = ret;
				ret = computed.width;

				// Revert the changed values
				style.width = width;
				style.minWidth = minWidth;
				style.maxWidth = maxWidth;
			}
		}

		// Support: IE
		// IE returns zIndex value as an integer.
		return ret === undefined ?
			ret :
			ret + "";
	};
} else if ( document.documentElement.currentStyle ) {
	getStyles = function( elem ) {
		return elem.currentStyle;
	};

	curCSS = function( elem, name, computed ) {
		var left, rs, rsLeft, ret,
			style = elem.style;

		computed = computed || getStyles( elem );
		ret = computed ? computed[ name ] : undefined;

		// Avoid setting ret to empty string here
		// so we don't default to auto
		if ( ret == null && style && style[ name ] ) {
			ret = style[ name ];
		}

		// From the awesome hack by Dean Edwards
		// http://erik.eae.net/archives/2007/07/27/18.54.15/#comment-102291

		// If we're not dealing with a regular pixel number
		// but a number that has a weird ending, we need to convert it to pixels
		// but not position css attributes, as those are proportional to the parent element instead
		// and we can't measure the parent instead because it might trigger a "stacking dolls" problem
		if ( rnumnonpx.test( ret ) && !rposition.test( name ) ) {

			// Remember the original values
			left = style.left;
			rs = elem.runtimeStyle;
			rsLeft = rs && rs.left;

			// Put in the new values to get a computed value out
			if ( rsLeft ) {
				rs.left = elem.currentStyle.left;
			}
			style.left = name === "fontSize" ? "1em" : ret;
			ret = style.pixelLeft + "px";

			// Revert the changed values
			style.left = left;
			if ( rsLeft ) {
				rs.left = rsLeft;
			}
		}

		// Support: IE
		// IE returns zIndex value as an integer.
		return ret === undefined ?
			ret :
			ret + "" || "auto";
	};
}




function addGetHookIf( conditionFn, hookFn ) {
	// Define the hook, we'll check on the first run if it's really needed.
	return {
		get: function() {
			var condition = conditionFn();

			if ( condition == null ) {
				// The test was not ready at this point; screw the hook this time
				// but check again when needed next time.
				return;
			}

			if ( condition ) {
				// Hook not needed (or it's not possible to use it due to missing dependency),
				// remove it.
				// Since there are no other hooks for marginRight, remove the whole object.
				delete this.get;
				return;
			}

			// Hook needed; redefine it so that the support test is not executed again.

			return (this.get = hookFn).apply( this, arguments );
		}
	};
}


(function() {
	// Minified: var b,c,d,e,f,g, h,i
	var div, style, a, pixelPositionVal, boxSizingReliableVal,
		reliableHiddenOffsetsVal, reliableMarginRightVal;

	// Setup
	div = document.createElement( "div" );
	div.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>";
	a = div.getElementsByTagName( "a" )[ 0 ];
	style = a && a.style;

	// Finish early in limited (non-browser) environments
	if ( !style ) {
		return;
	}

	style.cssText = "float:left;opacity:.5";

	// Support: IE<9
	// Make sure that element opacity exists (as opposed to filter)
	support.opacity = style.opacity === "0.5";

	// Verify style float existence
	// (IE uses styleFloat instead of cssFloat)
	support.cssFloat = !!style.cssFloat;

	div.style.backgroundClip = "content-box";
	div.cloneNode( true ).style.backgroundClip = "";
	support.clearCloneStyle = div.style.backgroundClip === "content-box";

	// Support: Firefox<29, Android 2.3
	// Vendor-prefix box-sizing
	support.boxSizing = style.boxSizing === "" || style.MozBoxSizing === "" ||
		style.WebkitBoxSizing === "";

	jQuery.extend(support, {
		reliableHiddenOffsets: function() {
			if ( reliableHiddenOffsetsVal == null ) {
				computeStyleTests();
			}
			return reliableHiddenOffsetsVal;
		},

		boxSizingReliable: function() {
			if ( boxSizingReliableVal == null ) {
				computeStyleTests();
			}
			return boxSizingReliableVal;
		},

		pixelPosition: function() {
			if ( pixelPositionVal == null ) {
				computeStyleTests();
			}
			return pixelPositionVal;
		},

		// Support: Android 2.3
		reliableMarginRight: function() {
			if ( reliableMarginRightVal == null ) {
				computeStyleTests();
			}
			return reliableMarginRightVal;
		}
	});

	function computeStyleTests() {
		// Minified: var b,c,d,j
		var div, body, container, contents;

		body = document.getElementsByTagName( "body" )[ 0 ];
		if ( !body || !body.style ) {
			// Test fired too early or in an unsupported environment, exit.
			return;
		}

		// Setup
		div = document.createElement( "div" );
		container = document.createElement( "div" );
		container.style.cssText = "position:absolute;border:0;width:0;height:0;top:0;left:-9999px";
		body.appendChild( container ).appendChild( div );

		div.style.cssText =
			// Support: Firefox<29, Android 2.3
			// Vendor-prefix box-sizing
			"-webkit-box-sizing:border-box;-moz-box-sizing:border-box;" +
			"box-sizing:border-box;display:block;margin-top:1%;top:1%;" +
			"border:1px;padding:1px;width:4px;position:absolute";

		// Support: IE<9
		// Assume reasonable values in the absence of getComputedStyle
		pixelPositionVal = boxSizingReliableVal = false;
		reliableMarginRightVal = true;

		// Check for getComputedStyle so that this code is not run in IE<9.
		if ( window.getComputedStyle ) {
			pixelPositionVal = ( window.getComputedStyle( div, null ) || {} ).top !== "1%";
			boxSizingReliableVal =
				( window.getComputedStyle( div, null ) || { width: "4px" } ).width === "4px";

			// Support: Android 2.3
			// Div with explicit width and no margin-right incorrectly
			// gets computed margin-right based on width of container (#3333)
			// WebKit Bug 13343 - getComputedStyle returns wrong value for margin-right
			contents = div.appendChild( document.createElement( "div" ) );

			// Reset CSS: box-sizing; display; margin; border; padding
			contents.style.cssText = div.style.cssText =
				// Support: Firefox<29, Android 2.3
				// Vendor-prefix box-sizing
				"-webkit-box-sizing:content-box;-moz-box-sizing:content-box;" +
				"box-sizing:content-box;display:block;margin:0;border:0;padding:0";
			contents.style.marginRight = contents.style.width = "0";
			div.style.width = "1px";

			reliableMarginRightVal =
				!parseFloat( ( window.getComputedStyle( contents, null ) || {} ).marginRight );
		}

		// Support: IE8
		// Check if table cells still have offsetWidth/Height when they are set
		// to display:none and there are still other visible table cells in a
		// table row; if so, offsetWidth/Height are not reliable for use when
		// determining if an element has been hidden directly using
		// display:none (it is still safe to use offsets if a parent element is
		// hidden; don safety goggles and see bug #4512 for more information).
		div.innerHTML = "<table><tr><td></td><td>t</td></tr></table>";
		contents = div.getElementsByTagName( "td" );
		contents[ 0 ].style.cssText = "margin:0;border:0;padding:0;display:none";
		reliableHiddenOffsetsVal = contents[ 0 ].offsetHeight === 0;
		if ( reliableHiddenOffsetsVal ) {
			contents[ 0 ].style.display = "";
			contents[ 1 ].style.display = "none";
			reliableHiddenOffsetsVal = contents[ 0 ].offsetHeight === 0;
		}

		body.removeChild( container );
	}

})();


// A method for quickly swapping in/out CSS properties to get correct calculations.
jQuery.swap = function( elem, options, callback, args ) {
	var ret, name,
		old = {};

	// Remember the old values, and insert the new ones
	for ( name in options ) {
		old[ name ] = elem.style[ name ];
		elem.style[ name ] = options[ name ];
	}

	ret = callback.apply( elem, args || [] );

	// Revert the old values
	for ( name in options ) {
		elem.style[ name ] = old[ name ];
	}

	return ret;
};


var
		ralpha = /alpha\([^)]*\)/i,
	ropacity = /opacity\s*=\s*([^)]*)/,

	// swappable if display is none or starts with table except "table", "table-cell", or "table-caption"
	// see here for display values: https://developer.mozilla.org/en-US/docs/CSS/display
	rdisplayswap = /^(none|table(?!-c[ea]).+)/,
	rnumsplit = new RegExp( "^(" + pnum + ")(.*)$", "i" ),
	rrelNum = new RegExp( "^([+-])=(" + pnum + ")", "i" ),

	cssShow = { position: "absolute", visibility: "hidden", display: "block" },
	cssNormalTransform = {
		letterSpacing: "0",
		fontWeight: "400"
	},

	cssPrefixes = [ "Webkit", "O", "Moz", "ms" ];


// return a css property mapped to a potentially vendor prefixed property
function vendorPropName( style, name ) {

	// shortcut for names that are not vendor prefixed
	if ( name in style ) {
		return name;
	}

	// check for vendor prefixed names
	var capName = name.charAt(0).toUpperCase() + name.slice(1),
		origName = name,
		i = cssPrefixes.length;

	while ( i-- ) {
		name = cssPrefixes[ i ] + capName;
		if ( name in style ) {
			return name;
		}
	}

	return origName;
}

function showHide( elements, show ) {
	var display, elem, hidden,
		values = [],
		index = 0,
		length = elements.length;

	for ( ; index < length; index++ ) {
		elem = elements[ index ];
		if ( !elem.style ) {
			continue;
		}

		values[ index ] = jQuery._data( elem, "olddisplay" );
		display = elem.style.display;
		if ( show ) {
			// Reset the inline display of this element to learn if it is
			// being hidden by cascaded rules or not
			if ( !values[ index ] && display === "none" ) {
				elem.style.display = "";
			}

			// Set elements which have been overridden with display: none
			// in a stylesheet to whatever the default browser style is
			// for such an element
			if ( elem.style.display === "" && isHidden( elem ) ) {
				values[ index ] = jQuery._data( elem, "olddisplay", defaultDisplay(elem.nodeName) );
			}
		} else {
			hidden = isHidden( elem );

			if ( display && display !== "none" || !hidden ) {
				jQuery._data( elem, "olddisplay", hidden ? display : jQuery.css( elem, "display" ) );
			}
		}
	}

	// Set the display of most of the elements in a second loop
	// to avoid the constant reflow
	for ( index = 0; index < length; index++ ) {
		elem = elements[ index ];
		if ( !elem.style ) {
			continue;
		}
		if ( !show || elem.style.display === "none" || elem.style.display === "" ) {
			elem.style.display = show ? values[ index ] || "" : "none";
		}
	}

	return elements;
}

function setPositiveNumber( elem, value, subtract ) {
	var matches = rnumsplit.exec( value );
	return matches ?
		// Guard against undefined "subtract", e.g., when used as in cssHooks
		Math.max( 0, matches[ 1 ] - ( subtract || 0 ) ) + ( matches[ 2 ] || "px" ) :
		value;
}

function augmentWidthOrHeight( elem, name, extra, isBorderBox, styles ) {
	var i = extra === ( isBorderBox ? "border" : "content" ) ?
		// If we already have the right measurement, avoid augmentation
		4 :
		// Otherwise initialize for horizontal or vertical properties
		name === "width" ? 1 : 0,

		val = 0;

	for ( ; i < 4; i += 2 ) {
		// both box models exclude margin, so add it if we want it
		if ( extra === "margin" ) {
			val += jQuery.css( elem, extra + cssExpand[ i ], true, styles );
		}

		if ( isBorderBox ) {
			// border-box includes padding, so remove it if we want content
			if ( extra === "content" ) {
				val -= jQuery.css( elem, "padding" + cssExpand[ i ], true, styles );
			}

			// at this point, extra isn't border nor margin, so remove border
			if ( extra !== "margin" ) {
				val -= jQuery.css( elem, "border" + cssExpand[ i ] + "Width", true, styles );
			}
		} else {
			// at this point, extra isn't content, so add padding
			val += jQuery.css( elem, "padding" + cssExpand[ i ], true, styles );

			// at this point, extra isn't content nor padding, so add border
			if ( extra !== "padding" ) {
				val += jQuery.css( elem, "border" + cssExpand[ i ] + "Width", true, styles );
			}
		}
	}

	return val;
}

function getWidthOrHeight( elem, name, extra ) {

	// Start with offset property, which is equivalent to the border-box value
	var valueIsBorderBox = true,
		val = name === "width" ? elem.offsetWidth : elem.offsetHeight,
		styles = getStyles( elem ),
		isBorderBox = support.boxSizing && jQuery.css( elem, "boxSizing", false, styles ) === "border-box";

	// some non-html elements return undefined for offsetWidth, so check for null/undefined
	// svg - https://bugzilla.mozilla.org/show_bug.cgi?id=649285
	// MathML - https://bugzilla.mozilla.org/show_bug.cgi?id=491668
	if ( val <= 0 || val == null ) {
		// Fall back to computed then uncomputed css if necessary
		val = curCSS( elem, name, styles );
		if ( val < 0 || val == null ) {
			val = elem.style[ name ];
		}

		// Computed unit is not pixels. Stop here and return.
		if ( rnumnonpx.test(val) ) {
			return val;
		}

		// we need the check for style in case a browser which returns unreliable values
		// for getComputedStyle silently falls back to the reliable elem.style
		valueIsBorderBox = isBorderBox && ( support.boxSizingReliable() || val === elem.style[ name ] );

		// Normalize "", auto, and prepare for extra
		val = parseFloat( val ) || 0;
	}

	// use the active box-sizing model to add/subtract irrelevant styles
	return ( val +
		augmentWidthOrHeight(
			elem,
			name,
			extra || ( isBorderBox ? "border" : "content" ),
			valueIsBorderBox,
			styles
		)
	) + "px";
}

jQuery.extend({
	// Add in style property hooks for overriding the default
	// behavior of getting and setting a style property
	cssHooks: {
		opacity: {
			get: function( elem, computed ) {
				if ( computed ) {
					// We should always get a number back from opacity
					var ret = curCSS( elem, "opacity" );
					return ret === "" ? "1" : ret;
				}
			}
		}
	},

	// Don't automatically add "px" to these possibly-unitless properties
	cssNumber: {
		"columnCount": true,
		"fillOpacity": true,
		"flexGrow": true,
		"flexShrink": true,
		"fontWeight": true,
		"lineHeight": true,
		"opacity": true,
		"order": true,
		"orphans": true,
		"widows": true,
		"zIndex": true,
		"zoom": true
	},

	// Add in properties whose names you wish to fix before
	// setting or getting the value
	cssProps: {
		// normalize float css property
		"float": support.cssFloat ? "cssFloat" : "styleFloat"
	},

	// Get and set the style property on a DOM Node
	style: function( elem, name, value, extra ) {
		// Don't set styles on text and comment nodes
		if ( !elem || elem.nodeType === 3 || elem.nodeType === 8 || !elem.style ) {
			return;
		}

		// Make sure that we're working with the right name
		var ret, type, hooks,
			origName = jQuery.camelCase( name ),
			style = elem.style;

		name = jQuery.cssProps[ origName ] || ( jQuery.cssProps[ origName ] = vendorPropName( style, origName ) );

		// gets hook for the prefixed version
		// followed by the unprefixed version
		hooks = jQuery.cssHooks[ name ] || jQuery.cssHooks[ origName ];

		// Check if we're setting a value
		if ( value !== undefined ) {
			type = typeof value;

			// convert relative number strings (+= or -=) to relative numbers. #7345
			if ( type === "string" && (ret = rrelNum.exec( value )) ) {
				value = ( ret[1] + 1 ) * ret[2] + parseFloat( jQuery.css( elem, name ) );
				// Fixes bug #9237
				type = "number";
			}

			// Make sure that null and NaN values aren't set. See: #7116
			if ( value == null || value !== value ) {
				return;
			}

			// If a number was passed in, add 'px' to the (except for certain CSS properties)
			if ( type === "number" && !jQuery.cssNumber[ origName ] ) {
				value += "px";
			}

			// Fixes #8908, it can be done more correctly by specifing setters in cssHooks,
			// but it would mean to define eight (for every problematic property) identical functions
			if ( !support.clearCloneStyle && value === "" && name.indexOf("background") === 0 ) {
				style[ name ] = "inherit";
			}

			// If a hook was provided, use that value, otherwise just set the specified value
			if ( !hooks || !("set" in hooks) || (value = hooks.set( elem, value, extra )) !== undefined ) {

				// Support: IE
				// Swallow errors from 'invalid' CSS values (#5509)
				try {
					style[ name ] = value;
				} catch(e) {}
			}

		} else {
			// If a hook was provided get the non-computed value from there
			if ( hooks && "get" in hooks && (ret = hooks.get( elem, false, extra )) !== undefined ) {
				return ret;
			}

			// Otherwise just get the value from the style object
			return style[ name ];
		}
	},

	css: function( elem, name, extra, styles ) {
		var num, val, hooks,
			origName = jQuery.camelCase( name );

		// Make sure that we're working with the right name
		name = jQuery.cssProps[ origName ] || ( jQuery.cssProps[ origName ] = vendorPropName( elem.style, origName ) );

		// gets hook for the prefixed version
		// followed by the unprefixed version
		hooks = jQuery.cssHooks[ name ] || jQuery.cssHooks[ origName ];

		// If a hook was provided get the computed value from there
		if ( hooks && "get" in hooks ) {
			val = hooks.get( elem, true, extra );
		}

		// Otherwise, if a way to get the computed value exists, use that
		if ( val === undefined ) {
			val = curCSS( elem, name, styles );
		}

		//convert "normal" to computed value
		if ( val === "normal" && name in cssNormalTransform ) {
			val = cssNormalTransform[ name ];
		}

		// Return, converting to number if forced or a qualifier was provided and val looks numeric
		if ( extra === "" || extra ) {
			num = parseFloat( val );
			return extra === true || jQuery.isNumeric( num ) ? num || 0 : val;
		}
		return val;
	}
});

jQuery.each([ "height", "width" ], function( i, name ) {
	jQuery.cssHooks[ name ] = {
		get: function( elem, computed, extra ) {
			if ( computed ) {
				// certain elements can have dimension info if we invisibly show them
				// however, it must have a current display style that would benefit from this
				return rdisplayswap.test( jQuery.css( elem, "display" ) ) && elem.offsetWidth === 0 ?
					jQuery.swap( elem, cssShow, function() {
						return getWidthOrHeight( elem, name, extra );
					}) :
					getWidthOrHeight( elem, name, extra );
			}
		},

		set: function( elem, value, extra ) {
			var styles = extra && getStyles( elem );
			return setPositiveNumber( elem, value, extra ?
				augmentWidthOrHeight(
					elem,
					name,
					extra,
					support.boxSizing && jQuery.css( elem, "boxSizing", false, styles ) === "border-box",
					styles
				) : 0
			);
		}
	};
});

if ( !support.opacity ) {
	jQuery.cssHooks.opacity = {
		get: function( elem, computed ) {
			// IE uses filters for opacity
			return ropacity.test( (computed && elem.currentStyle ? elem.currentStyle.filter : elem.style.filter) || "" ) ?
				( 0.01 * parseFloat( RegExp.$1 ) ) + "" :
				computed ? "1" : "";
		},

		set: function( elem, value ) {
			var style = elem.style,
				currentStyle = elem.currentStyle,
				opacity = jQuery.isNumeric( value ) ? "alpha(opacity=" + value * 100 + ")" : "",
				filter = currentStyle && currentStyle.filter || style.filter || "";

			// IE has trouble with opacity if it does not have layout
			// Force it by setting the zoom level
			style.zoom = 1;

			// if setting opacity to 1, and no other filters exist - attempt to remove filter attribute #6652
			// if value === "", then remove inline opacity #12685
			if ( ( value >= 1 || value === "" ) &&
					jQuery.trim( filter.replace( ralpha, "" ) ) === "" &&
					style.removeAttribute ) {

				// Setting style.filter to null, "" & " " still leave "filter:" in the cssText
				// if "filter:" is present at all, clearType is disabled, we want to avoid this
				// style.removeAttribute is IE Only, but so apparently is this code path...
				style.removeAttribute( "filter" );

				// if there is no filter style applied in a css rule or unset inline opacity, we are done
				if ( value === "" || currentStyle && !currentStyle.filter ) {
					return;
				}
			}

			// otherwise, set new filter values
			style.filter = ralpha.test( filter ) ?
				filter.replace( ralpha, opacity ) :
				filter + " " + opacity;
		}
	};
}

jQuery.cssHooks.marginRight = addGetHookIf( support.reliableMarginRight,
	function( elem, computed ) {
		if ( computed ) {
			// WebKit Bug 13343 - getComputedStyle returns wrong value for margin-right
			// Work around by temporarily setting element display to inline-block
			return jQuery.swap( elem, { "display": "inline-block" },
				curCSS, [ elem, "marginRight" ] );
		}
	}
);

// These hooks are used by animate to expand properties
jQuery.each({
	margin: "",
	padding: "",
	border: "Width"
}, function( prefix, suffix ) {
	jQuery.cssHooks[ prefix + suffix ] = {
		expand: function( value ) {
			var i = 0,
				expanded = {},

				// assumes a single number if not a string
				parts = typeof value === "string" ? value.split(" ") : [ value ];

			for ( ; i < 4; i++ ) {
				expanded[ prefix + cssExpand[ i ] + suffix ] =
					parts[ i ] || parts[ i - 2 ] || parts[ 0 ];
			}

			return expanded;
		}
	};

	if ( !rmargin.test( prefix ) ) {
		jQuery.cssHooks[ prefix + suffix ].set = setPositiveNumber;
	}
});

jQuery.fn.extend({
	css: function( name, value ) {
		return access( this, function( elem, name, value ) {
			var styles, len,
				map = {},
				i = 0;

			if ( jQuery.isArray( name ) ) {
				styles = getStyles( elem );
				len = name.length;

				for ( ; i < len; i++ ) {
					map[ name[ i ] ] = jQuery.css( elem, name[ i ], false, styles );
				}

				return map;
			}

			return value !== undefined ?
				jQuery.style( elem, name, value ) :
				jQuery.css( elem, name );
		}, name, value, arguments.length > 1 );
	},
	show: function() {
		return showHide( this, true );
	},
	hide: function() {
		return showHide( this );
	},
	toggle: function( state ) {
		if ( typeof state === "boolean" ) {
			return state ? this.show() : this.hide();
		}

		return this.each(function() {
			if ( isHidden( this ) ) {
				jQuery( this ).show();
			} else {
				jQuery( this ).hide();
			}
		});
	}
});


function Tween( elem, options, prop, end, easing ) {
	return new Tween.prototype.init( elem, options, prop, end, easing );
}
jQuery.Tween = Tween;

Tween.prototype = {
	constructor: Tween,
	init: function( elem, options, prop, end, easing, unit ) {
		this.elem = elem;
		this.prop = prop;
		this.easing = easing || "swing";
		this.options = options;
		this.start = this.now = this.cur();
		this.end = end;
		this.unit = unit || ( jQuery.cssNumber[ prop ] ? "" : "px" );
	},
	cur: function() {
		var hooks = Tween.propHooks[ this.prop ];

		return hooks && hooks.get ?
			hooks.get( this ) :
			Tween.propHooks._default.get( this );
	},
	run: function( percent ) {
		var eased,
			hooks = Tween.propHooks[ this.prop ];

		if ( this.options.duration ) {
			this.pos = eased = jQuery.easing[ this.easing ](
				percent, this.options.duration * percent, 0, 1, this.options.duration
			);
		} else {
			this.pos = eased = percent;
		}
		this.now = ( this.end - this.start ) * eased + this.start;

		if ( this.options.step ) {
			this.options.step.call( this.elem, this.now, this );
		}

		if ( hooks && hooks.set ) {
			hooks.set( this );
		} else {
			Tween.propHooks._default.set( this );
		}
		return this;
	}
};

Tween.prototype.init.prototype = Tween.prototype;

Tween.propHooks = {
	_default: {
		get: function( tween ) {
			var result;

			if ( tween.elem[ tween.prop ] != null &&
				(!tween.elem.style || tween.elem.style[ tween.prop ] == null) ) {
				return tween.elem[ tween.prop ];
			}

			// passing an empty string as a 3rd parameter to .css will automatically
			// attempt a parseFloat and fallback to a string if the parse fails
			// so, simple values such as "10px" are parsed to Float.
			// complex values such as "rotate(1rad)" are returned as is.
			result = jQuery.css( tween.elem, tween.prop, "" );
			// Empty strings, null, undefined and "auto" are converted to 0.
			return !result || result === "auto" ? 0 : result;
		},
		set: function( tween ) {
			// use step hook for back compat - use cssHook if its there - use .style if its
			// available and use plain properties where available
			if ( jQuery.fx.step[ tween.prop ] ) {
				jQuery.fx.step[ tween.prop ]( tween );
			} else if ( tween.elem.style && ( tween.elem.style[ jQuery.cssProps[ tween.prop ] ] != null || jQuery.cssHooks[ tween.prop ] ) ) {
				jQuery.style( tween.elem, tween.prop, tween.now + tween.unit );
			} else {
				tween.elem[ tween.prop ] = tween.now;
			}
		}
	}
};

// Support: IE <=9
// Panic based approach to setting things on disconnected nodes

Tween.propHooks.scrollTop = Tween.propHooks.scrollLeft = {
	set: function( tween ) {
		if ( tween.elem.nodeType && tween.elem.parentNode ) {
			tween.elem[ tween.prop ] = tween.now;
		}
	}
};

jQuery.easing = {
	linear: function( p ) {
		return p;
	},
	swing: function( p ) {
		return 0.5 - Math.cos( p * Math.PI ) / 2;
	}
};

jQuery.fx = Tween.prototype.init;

// Back Compat <1.8 extension point
jQuery.fx.step = {};




var
	fxNow, timerId,
	rfxtypes = /^(?:toggle|show|hide)$/,
	rfxnum = new RegExp( "^(?:([+-])=|)(" + pnum + ")([a-z%]*)$", "i" ),
	rrun = /queueHooks$/,
	animationPrefilters = [ defaultPrefilter ],
	tweeners = {
		"*": [ function( prop, value ) {
			var tween = this.createTween( prop, value ),
				target = tween.cur(),
				parts = rfxnum.exec( value ),
				unit = parts && parts[ 3 ] || ( jQuery.cssNumber[ prop ] ? "" : "px" ),

				// Starting value computation is required for potential unit mismatches
				start = ( jQuery.cssNumber[ prop ] || unit !== "px" && +target ) &&
					rfxnum.exec( jQuery.css( tween.elem, prop ) ),
				scale = 1,
				maxIterations = 20;

			if ( start && start[ 3 ] !== unit ) {
				// Trust units reported by jQuery.css
				unit = unit || start[ 3 ];

				// Make sure we update the tween properties later on
				parts = parts || [];

				// Iteratively approximate from a nonzero starting point
				start = +target || 1;

				do {
					// If previous iteration zeroed out, double until we get *something*
					// Use a string for doubling factor so we don't accidentally see scale as unchanged below
					scale = scale || ".5";

					// Adjust and apply
					start = start / scale;
					jQuery.style( tween.elem, prop, start + unit );

				// Update scale, tolerating zero or NaN from tween.cur()
				// And breaking the loop if scale is unchanged or perfect, or if we've just had enough
				} while ( scale !== (scale = tween.cur() / target) && scale !== 1 && --maxIterations );
			}

			// Update tween properties
			if ( parts ) {
				start = tween.start = +start || +target || 0;
				tween.unit = unit;
				// If a +=/-= token was provided, we're doing a relative animation
				tween.end = parts[ 1 ] ?
					start + ( parts[ 1 ] + 1 ) * parts[ 2 ] :
					+parts[ 2 ];
			}

			return tween;
		} ]
	};

// Animations created synchronously will run synchronously
function createFxNow() {
	setTimeout(function() {
		fxNow = undefined;
	});
	return ( fxNow = jQuery.now() );
}

// Generate parameters to create a standard animation
function genFx( type, includeWidth ) {
	var which,
		attrs = { height: type },
		i = 0;

	// if we include width, step value is 1 to do all cssExpand values,
	// if we don't include width, step value is 2 to skip over Left and Right
	includeWidth = includeWidth ? 1 : 0;
	for ( ; i < 4 ; i += 2 - includeWidth ) {
		which = cssExpand[ i ];
		attrs[ "margin" + which ] = attrs[ "padding" + which ] = type;
	}

	if ( includeWidth ) {
		attrs.opacity = attrs.width = type;
	}

	return attrs;
}

function createTween( value, prop, animation ) {
	var tween,
		collection = ( tweeners[ prop ] || [] ).concat( tweeners[ "*" ] ),
		index = 0,
		length = collection.length;
	for ( ; index < length; index++ ) {
		if ( (tween = collection[ index ].call( animation, prop, value )) ) {

			// we're done with this property
			return tween;
		}
	}
}

function defaultPrefilter( elem, props, opts ) {
	/* jshint validthis: true */
	var prop, value, toggle, tween, hooks, oldfire, display, checkDisplay,
		anim = this,
		orig = {},
		style = elem.style,
		hidden = elem.nodeType && isHidden( elem ),
		dataShow = jQuery._data( elem, "fxshow" );

	// handle queue: false promises
	if ( !opts.queue ) {
		hooks = jQuery._queueHooks( elem, "fx" );
		if ( hooks.unqueued == null ) {
			hooks.unqueued = 0;
			oldfire = hooks.empty.fire;
			hooks.empty.fire = function() {
				if ( !hooks.unqueued ) {
					oldfire();
				}
			};
		}
		hooks.unqueued++;

		anim.always(function() {
			// doing this makes sure that the complete handler will be called
			// before this completes
			anim.always(function() {
				hooks.unqueued--;
				if ( !jQuery.queue( elem, "fx" ).length ) {
					hooks.empty.fire();
				}
			});
		});
	}

	// height/width overflow pass
	if ( elem.nodeType === 1 && ( "height" in props || "width" in props ) ) {
		// Make sure that nothing sneaks out
		// Record all 3 overflow attributes because IE does not
		// change the overflow attribute when overflowX and
		// overflowY are set to the same value
		opts.overflow = [ style.overflow, style.overflowX, style.overflowY ];

		// Set display property to inline-block for height/width
		// animations on inline elements that are having width/height animated
		display = jQuery.css( elem, "display" );

		// Test default display if display is currently "none"
		checkDisplay = display === "none" ?
			jQuery._data( elem, "olddisplay" ) || defaultDisplay( elem.nodeName ) : display;

		if ( checkDisplay === "inline" && jQuery.css( elem, "float" ) === "none" ) {

			// inline-level elements accept inline-block;
			// block-level elements need to be inline with layout
			if ( !support.inlineBlockNeedsLayout || defaultDisplay( elem.nodeName ) === "inline" ) {
				style.display = "inline-block";
			} else {
				style.zoom = 1;
			}
		}
	}

	if ( opts.overflow ) {
		style.overflow = "hidden";
		if ( !support.shrinkWrapBlocks() ) {
			anim.always(function() {
				style.overflow = opts.overflow[ 0 ];
				style.overflowX = opts.overflow[ 1 ];
				style.overflowY = opts.overflow[ 2 ];
			});
		}
	}

	// show/hide pass
	for ( prop in props ) {
		value = props[ prop ];
		if ( rfxtypes.exec( value ) ) {
			delete props[ prop ];
			toggle = toggle || value === "toggle";
			if ( value === ( hidden ? "hide" : "show" ) ) {

				// If there is dataShow left over from a stopped hide or show and we are going to proceed with show, we should pretend to be hidden
				if ( value === "show" && dataShow && dataShow[ prop ] !== undefined ) {
					hidden = true;
				} else {
					continue;
				}
			}
			orig[ prop ] = dataShow && dataShow[ prop ] || jQuery.style( elem, prop );

		// Any non-fx value stops us from restoring the original display value
		} else {
			display = undefined;
		}
	}

	if ( !jQuery.isEmptyObject( orig ) ) {
		if ( dataShow ) {
			if ( "hidden" in dataShow ) {
				hidden = dataShow.hidden;
			}
		} else {
			dataShow = jQuery._data( elem, "fxshow", {} );
		}

		// store state if its toggle - enables .stop().toggle() to "reverse"
		if ( toggle ) {
			dataShow.hidden = !hidden;
		}
		if ( hidden ) {
			jQuery( elem ).show();
		} else {
			anim.done(function() {
				jQuery( elem ).hide();
			});
		}
		anim.done(function() {
			var prop;
			jQuery._removeData( elem, "fxshow" );
			for ( prop in orig ) {
				jQuery.style( elem, prop, orig[ prop ] );
			}
		});
		for ( prop in orig ) {
			tween = createTween( hidden ? dataShow[ prop ] : 0, prop, anim );

			if ( !( prop in dataShow ) ) {
				dataShow[ prop ] = tween.start;
				if ( hidden ) {
					tween.end = tween.start;
					tween.start = prop === "width" || prop === "height" ? 1 : 0;
				}
			}
		}

	// If this is a noop like .hide().hide(), restore an overwritten display value
	} else if ( (display === "none" ? defaultDisplay( elem.nodeName ) : display) === "inline" ) {
		style.display = display;
	}
}

function propFilter( props, specialEasing ) {
	var index, name, easing, value, hooks;

	// camelCase, specialEasing and expand cssHook pass
	for ( index in props ) {
		name = jQuery.camelCase( index );
		easing = specialEasing[ name ];
		value = props[ index ];
		if ( jQuery.isArray( value ) ) {
			easing = value[ 1 ];
			value = props[ index ] = value[ 0 ];
		}

		if ( index !== name ) {
			props[ name ] = value;
			delete props[ index ];
		}

		hooks = jQuery.cssHooks[ name ];
		if ( hooks && "expand" in hooks ) {
			value = hooks.expand( value );
			delete props[ name ];

			// not quite $.extend, this wont overwrite keys already present.
			// also - reusing 'index' from above because we have the correct "name"
			for ( index in value ) {
				if ( !( index in props ) ) {
					props[ index ] = value[ index ];
					specialEasing[ index ] = easing;
				}
			}
		} else {
			specialEasing[ name ] = easing;
		}
	}
}

function Animation( elem, properties, options ) {
	var result,
		stopped,
		index = 0,
		length = animationPrefilters.length,
		deferred = jQuery.Deferred().always( function() {
			// don't match elem in the :animated selector
			delete tick.elem;
		}),
		tick = function() {
			if ( stopped ) {
				return false;
			}
			var currentTime = fxNow || createFxNow(),
				remaining = Math.max( 0, animation.startTime + animation.duration - currentTime ),
				// archaic crash bug won't allow us to use 1 - ( 0.5 || 0 ) (#12497)
				temp = remaining / animation.duration || 0,
				percent = 1 - temp,
				index = 0,
				length = animation.tweens.length;

			for ( ; index < length ; index++ ) {
				animation.tweens[ index ].run( percent );
			}

			deferred.notifyWith( elem, [ animation, percent, remaining ]);

			if ( percent < 1 && length ) {
				return remaining;
			} else {
				deferred.resolveWith( elem, [ animation ] );
				return false;
			}
		},
		animation = deferred.promise({
			elem: elem,
			props: jQuery.extend( {}, properties ),
			opts: jQuery.extend( true, { specialEasing: {} }, options ),
			originalProperties: properties,
			originalOptions: options,
			startTime: fxNow || createFxNow(),
			duration: options.duration,
			tweens: [],
			createTween: function( prop, end ) {
				var tween = jQuery.Tween( elem, animation.opts, prop, end,
						animation.opts.specialEasing[ prop ] || animation.opts.easing );
				animation.tweens.push( tween );
				return tween;
			},
			stop: function( gotoEnd ) {
				var index = 0,
					// if we are going to the end, we want to run all the tweens
					// otherwise we skip this part
					length = gotoEnd ? animation.tweens.length : 0;
				if ( stopped ) {
					return this;
				}
				stopped = true;
				for ( ; index < length ; index++ ) {
					animation.tweens[ index ].run( 1 );
				}

				// resolve when we played the last frame
				// otherwise, reject
				if ( gotoEnd ) {
					deferred.resolveWith( elem, [ animation, gotoEnd ] );
				} else {
					deferred.rejectWith( elem, [ animation, gotoEnd ] );
				}
				return this;
			}
		}),
		props = animation.props;

	propFilter( props, animation.opts.specialEasing );

	for ( ; index < length ; index++ ) {
		result = animationPrefilters[ index ].call( animation, elem, props, animation.opts );
		if ( result ) {
			return result;
		}
	}

	jQuery.map( props, createTween, animation );

	if ( jQuery.isFunction( animation.opts.start ) ) {
		animation.opts.start.call( elem, animation );
	}

	jQuery.fx.timer(
		jQuery.extend( tick, {
			elem: elem,
			anim: animation,
			queue: animation.opts.queue
		})
	);

	// attach callbacks from options
	return animation.progress( animation.opts.progress )
		.done( animation.opts.done, animation.opts.complete )
		.fail( animation.opts.fail )
		.always( animation.opts.always );
}

jQuery.Animation = jQuery.extend( Animation, {
	tweener: function( props, callback ) {
		if ( jQuery.isFunction( props ) ) {
			callback = props;
			props = [ "*" ];
		} else {
			props = props.split(" ");
		}

		var prop,
			index = 0,
			length = props.length;

		for ( ; index < length ; index++ ) {
			prop = props[ index ];
			tweeners[ prop ] = tweeners[ prop ] || [];
			tweeners[ prop ].unshift( callback );
		}
	},

	prefilter: function( callback, prepend ) {
		if ( prepend ) {
			animationPrefilters.unshift( callback );
		} else {
			animationPrefilters.push( callback );
		}
	}
});

jQuery.speed = function( speed, easing, fn ) {
	var opt = speed && typeof speed === "object" ? jQuery.extend( {}, speed ) : {
		complete: fn || !fn && easing ||
			jQuery.isFunction( speed ) && speed,
		duration: speed,
		easing: fn && easing || easing && !jQuery.isFunction( easing ) && easing
	};

	opt.duration = jQuery.fx.off ? 0 : typeof opt.duration === "number" ? opt.duration :
		opt.duration in jQuery.fx.speeds ? jQuery.fx.speeds[ opt.duration ] : jQuery.fx.speeds._default;

	// normalize opt.queue - true/undefined/null -> "fx"
	if ( opt.queue == null || opt.queue === true ) {
		opt.queue = "fx";
	}

	// Queueing
	opt.old = opt.complete;

	opt.complete = function() {
		if ( jQuery.isFunction( opt.old ) ) {
			opt.old.call( this );
		}

		if ( opt.queue ) {
			jQuery.dequeue( this, opt.queue );
		}
	};

	return opt;
};

jQuery.fn.extend({
	fadeTo: function( speed, to, easing, callback ) {

		// show any hidden elements after setting opacity to 0
		return this.filter( isHidden ).css( "opacity", 0 ).show()

			// animate to the value specified
			.end().animate({ opacity: to }, speed, easing, callback );
	},
	animate: function( prop, speed, easing, callback ) {
		var empty = jQuery.isEmptyObject( prop ),
			optall = jQuery.speed( speed, easing, callback ),
			doAnimation = function() {
				// Operate on a copy of prop so per-property easing won't be lost
				var anim = Animation( this, jQuery.extend( {}, prop ), optall );

				// Empty animations, or finishing resolves immediately
				if ( empty || jQuery._data( this, "finish" ) ) {
					anim.stop( true );
				}
			};
			doAnimation.finish = doAnimation;

		return empty || optall.queue === false ?
			this.each( doAnimation ) :
			this.queue( optall.queue, doAnimation );
	},
	stop: function( type, clearQueue, gotoEnd ) {
		var stopQueue = function( hooks ) {
			var stop = hooks.stop;
			delete hooks.stop;
			stop( gotoEnd );
		};

		if ( typeof type !== "string" ) {
			gotoEnd = clearQueue;
			clearQueue = type;
			type = undefined;
		}
		if ( clearQueue && type !== false ) {
			this.queue( type || "fx", [] );
		}

		return this.each(function() {
			var dequeue = true,
				index = type != null && type + "queueHooks",
				timers = jQuery.timers,
				data = jQuery._data( this );

			if ( index ) {
				if ( data[ index ] && data[ index ].stop ) {
					stopQueue( data[ index ] );
				}
			} else {
				for ( index in data ) {
					if ( data[ index ] && data[ index ].stop && rrun.test( index ) ) {
						stopQueue( data[ index ] );
					}
				}
			}

			for ( index = timers.length; index--; ) {
				if ( timers[ index ].elem === this && (type == null || timers[ index ].queue === type) ) {
					timers[ index ].anim.stop( gotoEnd );
					dequeue = false;
					timers.splice( index, 1 );
				}
			}

			// start the next in the queue if the last step wasn't forced
			// timers currently will call their complete callbacks, which will dequeue
			// but only if they were gotoEnd
			if ( dequeue || !gotoEnd ) {
				jQuery.dequeue( this, type );
			}
		});
	},
	finish: function( type ) {
		if ( type !== false ) {
			type = type || "fx";
		}
		return this.each(function() {
			var index,
				data = jQuery._data( this ),
				queue = data[ type + "queue" ],
				hooks = data[ type + "queueHooks" ],
				timers = jQuery.timers,
				length = queue ? queue.length : 0;

			// enable finishing flag on private data
			data.finish = true;

			// empty the queue first
			jQuery.queue( this, type, [] );

			if ( hooks && hooks.stop ) {
				hooks.stop.call( this, true );
			}

			// look for any active animations, and finish them
			for ( index = timers.length; index--; ) {
				if ( timers[ index ].elem === this && timers[ index ].queue === type ) {
					timers[ index ].anim.stop( true );
					timers.splice( index, 1 );
				}
			}

			// look for any animations in the old queue and finish them
			for ( index = 0; index < length; index++ ) {
				if ( queue[ index ] && queue[ index ].finish ) {
					queue[ index ].finish.call( this );
				}
			}

			// turn off finishing flag
			delete data.finish;
		});
	}
});

jQuery.each([ "toggle", "show", "hide" ], function( i, name ) {
	var cssFn = jQuery.fn[ name ];
	jQuery.fn[ name ] = function( speed, easing, callback ) {
		return speed == null || typeof speed === "boolean" ?
			cssFn.apply( this, arguments ) :
			this.animate( genFx( name, true ), speed, easing, callback );
	};
});

// Generate shortcuts for custom animations
jQuery.each({
	slideDown: genFx("show"),
	slideUp: genFx("hide"),
	slideToggle: genFx("toggle"),
	fadeIn: { opacity: "show" },
	fadeOut: { opacity: "hide" },
	fadeToggle: { opacity: "toggle" }
}, function( name, props ) {
	jQuery.fn[ name ] = function( speed, easing, callback ) {
		return this.animate( props, speed, easing, callback );
	};
});

jQuery.timers = [];
jQuery.fx.tick = function() {
	var timer,
		timers = jQuery.timers,
		i = 0;

	fxNow = jQuery.now();

	for ( ; i < timers.length; i++ ) {
		timer = timers[ i ];
		// Checks the timer has not already been removed
		if ( !timer() && timers[ i ] === timer ) {
			timers.splice( i--, 1 );
		}
	}

	if ( !timers.length ) {
		jQuery.fx.stop();
	}
	fxNow = undefined;
};

jQuery.fx.timer = function( timer ) {
	jQuery.timers.push( timer );
	if ( timer() ) {
		jQuery.fx.start();
	} else {
		jQuery.timers.pop();
	}
};

jQuery.fx.interval = 13;

jQuery.fx.start = function() {
	if ( !timerId ) {
		timerId = setInterval( jQuery.fx.tick, jQuery.fx.interval );
	}
};

jQuery.fx.stop = function() {
	clearInterval( timerId );
	timerId = null;
};

jQuery.fx.speeds = {
	slow: 600,
	fast: 200,
	// Default speed
	_default: 400
};


// Based off of the plugin by Clint Helfers, with permission.
// http://blindsignals.com/index.php/2009/07/jquery-delay/
jQuery.fn.delay = function( time, type ) {
	time = jQuery.fx ? jQuery.fx.speeds[ time ] || time : time;
	type = type || "fx";

	return this.queue( type, function( next, hooks ) {
		var timeout = setTimeout( next, time );
		hooks.stop = function() {
			clearTimeout( timeout );
		};
	});
};


(function() {
	// Minified: var a,b,c,d,e
	var input, div, select, a, opt;

	// Setup
	div = document.createElement( "div" );
	div.setAttribute( "className", "t" );
	div.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>";
	a = div.getElementsByTagName("a")[ 0 ];

	// First batch of tests.
	select = document.createElement("select");
	opt = select.appendChild( document.createElement("option") );
	input = div.getElementsByTagName("input")[ 0 ];

	a.style.cssText = "top:1px";

	// Test setAttribute on camelCase class. If it works, we need attrFixes when doing get/setAttribute (ie6/7)
	support.getSetAttribute = div.className !== "t";

	// Get the style information from getAttribute
	// (IE uses .cssText instead)
	support.style = /top/.test( a.getAttribute("style") );

	// Make sure that URLs aren't manipulated
	// (IE normalizes it by default)
	support.hrefNormalized = a.getAttribute("href") === "/a";

	// Check the default checkbox/radio value ("" on WebKit; "on" elsewhere)
	support.checkOn = !!input.value;

	// Make sure that a selected-by-default option has a working selected property.
	// (WebKit defaults to false instead of true, IE too, if it's in an optgroup)
	support.optSelected = opt.selected;

	// Tests for enctype support on a form (#6743)
	support.enctype = !!document.createElement("form").enctype;

	// Make sure that the options inside disabled selects aren't marked as disabled
	// (WebKit marks them as disabled)
	select.disabled = true;
	support.optDisabled = !opt.disabled;

	// Support: IE8 only
	// Check if we can trust getAttribute("value")
	input = document.createElement( "input" );
	input.setAttribute( "value", "" );
	support.input = input.getAttribute( "value" ) === "";

	// Check if an input maintains its value after becoming a radio
	input.value = "t";
	input.setAttribute( "type", "radio" );
	support.radioValue = input.value === "t";
})();


var rreturn = /\r/g;

jQuery.fn.extend({
	val: function( value ) {
		var hooks, ret, isFunction,
			elem = this[0];

		if ( !arguments.length ) {
			if ( elem ) {
				hooks = jQuery.valHooks[ elem.type ] || jQuery.valHooks[ elem.nodeName.toLowerCase() ];

				if ( hooks && "get" in hooks && (ret = hooks.get( elem, "value" )) !== undefined ) {
					return ret;
				}

				ret = elem.value;

				return typeof ret === "string" ?
					// handle most common string cases
					ret.replace(rreturn, "") :
					// handle cases where value is null/undef or number
					ret == null ? "" : ret;
			}

			return;
		}

		isFunction = jQuery.isFunction( value );

		return this.each(function( i ) {
			var val;

			if ( this.nodeType !== 1 ) {
				return;
			}

			if ( isFunction ) {
				val = value.call( this, i, jQuery( this ).val() );
			} else {
				val = value;
			}

			// Treat null/undefined as ""; convert numbers to string
			if ( val == null ) {
				val = "";
			} else if ( typeof val === "number" ) {
				val += "";
			} else if ( jQuery.isArray( val ) ) {
				val = jQuery.map( val, function( value ) {
					return value == null ? "" : value + "";
				});
			}

			hooks = jQuery.valHooks[ this.type ] || jQuery.valHooks[ this.nodeName.toLowerCase() ];

			// If set returns undefined, fall back to normal setting
			if ( !hooks || !("set" in hooks) || hooks.set( this, val, "value" ) === undefined ) {
				this.value = val;
			}
		});
	}
});

jQuery.extend({
	valHooks: {
		option: {
			get: function( elem ) {
				var val = jQuery.find.attr( elem, "value" );
				return val != null ?
					val :
					// Support: IE10-11+
					// option.text throws exceptions (#14686, #14858)
					jQuery.trim( jQuery.text( elem ) );
			}
		},
		select: {
			get: function( elem ) {
				var value, option,
					options = elem.options,
					index = elem.selectedIndex,
					one = elem.type === "select-one" || index < 0,
					values = one ? null : [],
					max = one ? index + 1 : options.length,
					i = index < 0 ?
						max :
						one ? index : 0;

				// Loop through all the selected options
				for ( ; i < max; i++ ) {
					option = options[ i ];

					// oldIE doesn't update selected after form reset (#2551)
					if ( ( option.selected || i === index ) &&
							// Don't return options that are disabled or in a disabled optgroup
							( support.optDisabled ? !option.disabled : option.getAttribute("disabled") === null ) &&
							( !option.parentNode.disabled || !jQuery.nodeName( option.parentNode, "optgroup" ) ) ) {

						// Get the specific value for the option
						value = jQuery( option ).val();

						// We don't need an array for one selects
						if ( one ) {
							return value;
						}

						// Multi-Selects return an array
						values.push( value );
					}
				}

				return values;
			},

			set: function( elem, value ) {
				var optionSet, option,
					options = elem.options,
					values = jQuery.makeArray( value ),
					i = options.length;

				while ( i-- ) {
					option = options[ i ];

					if ( jQuery.inArray( jQuery.valHooks.option.get( option ), values ) >= 0 ) {

						// Support: IE6
						// When new option element is added to select box we need to
						// force reflow of newly added node in order to workaround delay
						// of initialization properties
						try {
							option.selected = optionSet = true;

						} catch ( _ ) {

							// Will be executed only in IE6
							option.scrollHeight;
						}

					} else {
						option.selected = false;
					}
				}

				// Force browsers to behave consistently when non-matching value is set
				if ( !optionSet ) {
					elem.selectedIndex = -1;
				}

				return options;
			}
		}
	}
});

// Radios and checkboxes getter/setter
jQuery.each([ "radio", "checkbox" ], function() {
	jQuery.valHooks[ this ] = {
		set: function( elem, value ) {
			if ( jQuery.isArray( value ) ) {
				return ( elem.checked = jQuery.inArray( jQuery(elem).val(), value ) >= 0 );
			}
		}
	};
	if ( !support.checkOn ) {
		jQuery.valHooks[ this ].get = function( elem ) {
			// Support: Webkit
			// "" is returned instead of "on" if a value isn't specified
			return elem.getAttribute("value") === null ? "on" : elem.value;
		};
	}
});




var nodeHook, boolHook,
	attrHandle = jQuery.expr.attrHandle,
	ruseDefault = /^(?:checked|selected)$/i,
	getSetAttribute = support.getSetAttribute,
	getSetInput = support.input;

jQuery.fn.extend({
	attr: function( name, value ) {
		return access( this, jQuery.attr, name, value, arguments.length > 1 );
	},

	removeAttr: function( name ) {
		return this.each(function() {
			jQuery.removeAttr( this, name );
		});
	}
});

jQuery.extend({
	attr: function( elem, name, value ) {
		var hooks, ret,
			nType = elem.nodeType;

		// don't get/set attributes on text, comment and attribute nodes
		if ( !elem || nType === 3 || nType === 8 || nType === 2 ) {
			return;
		}

		// Fallback to prop when attributes are not supported
		if ( typeof elem.getAttribute === strundefined ) {
			return jQuery.prop( elem, name, value );
		}

		// All attributes are lowercase
		// Grab necessary hook if one is defined
		if ( nType !== 1 || !jQuery.isXMLDoc( elem ) ) {
			name = name.toLowerCase();
			hooks = jQuery.attrHooks[ name ] ||
				( jQuery.expr.match.bool.test( name ) ? boolHook : nodeHook );
		}

		if ( value !== undefined ) {

			if ( value === null ) {
				jQuery.removeAttr( elem, name );

			} else if ( hooks && "set" in hooks && (ret = hooks.set( elem, value, name )) !== undefined ) {
				return ret;

			} else {
				elem.setAttribute( name, value + "" );
				return value;
			}

		} else if ( hooks && "get" in hooks && (ret = hooks.get( elem, name )) !== null ) {
			return ret;

		} else {
			ret = jQuery.find.attr( elem, name );

			// Non-existent attributes return null, we normalize to undefined
			return ret == null ?
				undefined :
				ret;
		}
	},

	removeAttr: function( elem, value ) {
		var name, propName,
			i = 0,
			attrNames = value && value.match( rnotwhite );

		if ( attrNames && elem.nodeType === 1 ) {
			while ( (name = attrNames[i++]) ) {
				propName = jQuery.propFix[ name ] || name;

				// Boolean attributes get special treatment (#10870)
				if ( jQuery.expr.match.bool.test( name ) ) {
					// Set corresponding property to false
					if ( getSetInput && getSetAttribute || !ruseDefault.test( name ) ) {
						elem[ propName ] = false;
					// Support: IE<9
					// Also clear defaultChecked/defaultSelected (if appropriate)
					} else {
						elem[ jQuery.camelCase( "default-" + name ) ] =
							elem[ propName ] = false;
					}

				// See #9699 for explanation of this approach (setting first, then removal)
				} else {
					jQuery.attr( elem, name, "" );
				}

				elem.removeAttribute( getSetAttribute ? name : propName );
			}
		}
	},

	attrHooks: {
		type: {
			set: function( elem, value ) {
				if ( !support.radioValue && value === "radio" && jQuery.nodeName(elem, "input") ) {
					// Setting the type on a radio button after the value resets the value in IE6-9
					// Reset value to default in case type is set after value during creation
					var val = elem.value;
					elem.setAttribute( "type", value );
					if ( val ) {
						elem.value = val;
					}
					return value;
				}
			}
		}
	}
});

// Hook for boolean attributes
boolHook = {
	set: function( elem, value, name ) {
		if ( value === false ) {
			// Remove boolean attributes when set to false
			jQuery.removeAttr( elem, name );
		} else if ( getSetInput && getSetAttribute || !ruseDefault.test( name ) ) {
			// IE<8 needs the *property* name
			elem.setAttribute( !getSetAttribute && jQuery.propFix[ name ] || name, name );

		// Use defaultChecked and defaultSelected for oldIE
		} else {
			elem[ jQuery.camelCase( "default-" + name ) ] = elem[ name ] = true;
		}

		return name;
	}
};

// Retrieve booleans specially
jQuery.each( jQuery.expr.match.bool.source.match( /\w+/g ), function( i, name ) {

	var getter = attrHandle[ name ] || jQuery.find.attr;

	attrHandle[ name ] = getSetInput && getSetAttribute || !ruseDefault.test( name ) ?
		function( elem, name, isXML ) {
			var ret, handle;
			if ( !isXML ) {
				// Avoid an infinite loop by temporarily removing this function from the getter
				handle = attrHandle[ name ];
				attrHandle[ name ] = ret;
				ret = getter( elem, name, isXML ) != null ?
					name.toLowerCase() :
					null;
				attrHandle[ name ] = handle;
			}
			return ret;
		} :
		function( elem, name, isXML ) {
			if ( !isXML ) {
				return elem[ jQuery.camelCase( "default-" + name ) ] ?
					name.toLowerCase() :
					null;
			}
		};
});

// fix oldIE attroperties
if ( !getSetInput || !getSetAttribute ) {
	jQuery.attrHooks.value = {
		set: function( elem, value, name ) {
			if ( jQuery.nodeName( elem, "input" ) ) {
				// Does not return so that setAttribute is also used
				elem.defaultValue = value;
			} else {
				// Use nodeHook if defined (#1954); otherwise setAttribute is fine
				return nodeHook && nodeHook.set( elem, value, name );
			}
		}
	};
}

// IE6/7 do not support getting/setting some attributes with get/setAttribute
if ( !getSetAttribute ) {

	// Use this for any attribute in IE6/7
	// This fixes almost every IE6/7 issue
	nodeHook = {
		set: function( elem, value, name ) {
			// Set the existing or create a new attribute node
			var ret = elem.getAttributeNode( name );
			if ( !ret ) {
				elem.setAttributeNode(
					(ret = elem.ownerDocument.createAttribute( name ))
				);
			}

			ret.value = value += "";

			// Break association with cloned elements by also using setAttribute (#9646)
			if ( name === "value" || value === elem.getAttribute( name ) ) {
				return value;
			}
		}
	};

	// Some attributes are constructed with empty-string values when not defined
	attrHandle.id = attrHandle.name = attrHandle.coords =
		function( elem, name, isXML ) {
			var ret;
			if ( !isXML ) {
				return (ret = elem.getAttributeNode( name )) && ret.value !== "" ?
					ret.value :
					null;
			}
		};

	// Fixing value retrieval on a button requires this module
	jQuery.valHooks.button = {
		get: function( elem, name ) {
			var ret = elem.getAttributeNode( name );
			if ( ret && ret.specified ) {
				return ret.value;
			}
		},
		set: nodeHook.set
	};

	// Set contenteditable to false on removals(#10429)
	// Setting to empty string throws an error as an invalid value
	jQuery.attrHooks.contenteditable = {
		set: function( elem, value, name ) {
			nodeHook.set( elem, value === "" ? false : value, name );
		}
	};

	// Set width and height to auto instead of 0 on empty string( Bug #8150 )
	// This is for removals
	jQuery.each([ "width", "height" ], function( i, name ) {
		jQuery.attrHooks[ name ] = {
			set: function( elem, value ) {
				if ( value === "" ) {
					elem.setAttribute( name, "auto" );
					return value;
				}
			}
		};
	});
}

if ( !support.style ) {
	jQuery.attrHooks.style = {
		get: function( elem ) {
			// Return undefined in the case of empty string
			// Note: IE uppercases css property names, but if we were to .toLowerCase()
			// .cssText, that would destroy case senstitivity in URL's, like in "background"
			return elem.style.cssText || undefined;
		},
		set: function( elem, value ) {
			return ( elem.style.cssText = value + "" );
		}
	};
}




var rfocusable = /^(?:input|select|textarea|button|object)$/i,
	rclickable = /^(?:a|area)$/i;

jQuery.fn.extend({
	prop: function( name, value ) {
		return access( this, jQuery.prop, name, value, arguments.length > 1 );
	},

	removeProp: function( name ) {
		name = jQuery.propFix[ name ] || name;
		return this.each(function() {
			// try/catch handles cases where IE balks (such as removing a property on window)
			try {
				this[ name ] = undefined;
				delete this[ name ];
			} catch( e ) {}
		});
	}
});

jQuery.extend({
	propFix: {
		"for": "htmlFor",
		"class": "className"
	},

	prop: function( elem, name, value ) {
		var ret, hooks, notxml,
			nType = elem.nodeType;

		// don't get/set properties on text, comment and attribute nodes
		if ( !elem || nType === 3 || nType === 8 || nType === 2 ) {
			return;
		}

		notxml = nType !== 1 || !jQuery.isXMLDoc( elem );

		if ( notxml ) {
			// Fix name and attach hooks
			name = jQuery.propFix[ name ] || name;
			hooks = jQuery.propHooks[ name ];
		}

		if ( value !== undefined ) {
			return hooks && "set" in hooks && (ret = hooks.set( elem, value, name )) !== undefined ?
				ret :
				( elem[ name ] = value );

		} else {
			return hooks && "get" in hooks && (ret = hooks.get( elem, name )) !== null ?
				ret :
				elem[ name ];
		}
	},

	propHooks: {
		tabIndex: {
			get: function( elem ) {
				// elem.tabIndex doesn't always return the correct value when it hasn't been explicitly set
				// http://fluidproject.org/blog/2008/01/09/getting-setting-and-removing-tabindex-values-with-javascript/
				// Use proper attribute retrieval(#12072)
				var tabindex = jQuery.find.attr( elem, "tabindex" );

				return tabindex ?
					parseInt( tabindex, 10 ) :
					rfocusable.test( elem.nodeName ) || rclickable.test( elem.nodeName ) && elem.href ?
						0 :
						-1;
			}
		}
	}
});

// Some attributes require a special call on IE
// http://msdn.microsoft.com/en-us/library/ms536429%28VS.85%29.aspx
if ( !support.hrefNormalized ) {
	// href/src property should get the full normalized URL (#10299/#12915)
	jQuery.each([ "href", "src" ], function( i, name ) {
		jQuery.propHooks[ name ] = {
			get: function( elem ) {
				return elem.getAttribute( name, 4 );
			}
		};
	});
}

// Support: Safari, IE9+
// mis-reports the default selected property of an option
// Accessing the parent's selectedIndex property fixes it
if ( !support.optSelected ) {
	jQuery.propHooks.selected = {
		get: function( elem ) {
			var parent = elem.parentNode;

			if ( parent ) {
				parent.selectedIndex;

				// Make sure that it also works with optgroups, see #5701
				if ( parent.parentNode ) {
					parent.parentNode.selectedIndex;
				}
			}
			return null;
		}
	};
}

jQuery.each([
	"tabIndex",
	"readOnly",
	"maxLength",
	"cellSpacing",
	"cellPadding",
	"rowSpan",
	"colSpan",
	"useMap",
	"frameBorder",
	"contentEditable"
], function() {
	jQuery.propFix[ this.toLowerCase() ] = this;
});

// IE6/7 call enctype encoding
if ( !support.enctype ) {
	jQuery.propFix.enctype = "encoding";
}




var rclass = /[\t\r\n\f]/g;

jQuery.fn.extend({
	addClass: function( value ) {
		var classes, elem, cur, clazz, j, finalValue,
			i = 0,
			len = this.length,
			proceed = typeof value === "string" && value;

		if ( jQuery.isFunction( value ) ) {
			return this.each(function( j ) {
				jQuery( this ).addClass( value.call( this, j, this.className ) );
			});
		}

		if ( proceed ) {
			// The disjunction here is for better compressibility (see removeClass)
			classes = ( value || "" ).match( rnotwhite ) || [];

			for ( ; i < len; i++ ) {
				elem = this[ i ];
				cur = elem.nodeType === 1 && ( elem.className ?
					( " " + elem.className + " " ).replace( rclass, " " ) :
					" "
				);

				if ( cur ) {
					j = 0;
					while ( (clazz = classes[j++]) ) {
						if ( cur.indexOf( " " + clazz + " " ) < 0 ) {
							cur += clazz + " ";
						}
					}

					// only assign if different to avoid unneeded rendering.
					finalValue = jQuery.trim( cur );
					if ( elem.className !== finalValue ) {
						elem.className = finalValue;
					}
				}
			}
		}

		return this;
	},

	removeClass: function( value ) {
		var classes, elem, cur, clazz, j, finalValue,
			i = 0,
			len = this.length,
			proceed = arguments.length === 0 || typeof value === "string" && value;

		if ( jQuery.isFunction( value ) ) {
			return this.each(function( j ) {
				jQuery( this ).removeClass( value.call( this, j, this.className ) );
			});
		}
		if ( proceed ) {
			classes = ( value || "" ).match( rnotwhite ) || [];

			for ( ; i < len; i++ ) {
				elem = this[ i ];
				// This expression is here for better compressibility (see addClass)
				cur = elem.nodeType === 1 && ( elem.className ?
					( " " + elem.className + " " ).replace( rclass, " " ) :
					""
				);

				if ( cur ) {
					j = 0;
					while ( (clazz = classes[j++]) ) {
						// Remove *all* instances
						while ( cur.indexOf( " " + clazz + " " ) >= 0 ) {
							cur = cur.replace( " " + clazz + " ", " " );
						}
					}

					// only assign if different to avoid unneeded rendering.
					finalValue = value ? jQuery.trim( cur ) : "";
					if ( elem.className !== finalValue ) {
						elem.className = finalValue;
					}
				}
			}
		}

		return this;
	},

	toggleClass: function( value, stateVal ) {
		var type = typeof value;

		if ( typeof stateVal === "boolean" && type === "string" ) {
			return stateVal ? this.addClass( value ) : this.removeClass( value );
		}

		if ( jQuery.isFunction( value ) ) {
			return this.each(function( i ) {
				jQuery( this ).toggleClass( value.call(this, i, this.className, stateVal), stateVal );
			});
		}

		return this.each(function() {
			if ( type === "string" ) {
				// toggle individual class names
				var className,
					i = 0,
					self = jQuery( this ),
					classNames = value.match( rnotwhite ) || [];

				while ( (className = classNames[ i++ ]) ) {
					// check each className given, space separated list
					if ( self.hasClass( className ) ) {
						self.removeClass( className );
					} else {
						self.addClass( className );
					}
				}

			// Toggle whole class name
			} else if ( type === strundefined || type === "boolean" ) {
				if ( this.className ) {
					// store className if set
					jQuery._data( this, "__className__", this.className );
				}

				// If the element has a class name or if we're passed "false",
				// then remove the whole classname (if there was one, the above saved it).
				// Otherwise bring back whatever was previously saved (if anything),
				// falling back to the empty string if nothing was stored.
				this.className = this.className || value === false ? "" : jQuery._data( this, "__className__" ) || "";
			}
		});
	},

	hasClass: function( selector ) {
		var className = " " + selector + " ",
			i = 0,
			l = this.length;
		for ( ; i < l; i++ ) {
			if ( this[i].nodeType === 1 && (" " + this[i].className + " ").replace(rclass, " ").indexOf( className ) >= 0 ) {
				return true;
			}
		}

		return false;
	}
});




// Return jQuery for attributes-only inclusion


jQuery.each( ("blur focus focusin focusout load resize scroll unload click dblclick " +
	"mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave " +
	"change select submit keydown keypress keyup error contextmenu").split(" "), function( i, name ) {

	// Handle event binding
	jQuery.fn[ name ] = function( data, fn ) {
		return arguments.length > 0 ?
			this.on( name, null, data, fn ) :
			this.trigger( name );
	};
});

jQuery.fn.extend({
	hover: function( fnOver, fnOut ) {
		return this.mouseenter( fnOver ).mouseleave( fnOut || fnOver );
	},

	bind: function( types, data, fn ) {
		return this.on( types, null, data, fn );
	},
	unbind: function( types, fn ) {
		return this.off( types, null, fn );
	},

	delegate: function( selector, types, data, fn ) {
		return this.on( types, selector, data, fn );
	},
	undelegate: function( selector, types, fn ) {
		// ( namespace ) or ( selector, types [, fn] )
		return arguments.length === 1 ? this.off( selector, "**" ) : this.off( types, selector || "**", fn );
	}
});


var nonce = jQuery.now();

var rquery = (/\?/);



var rvalidtokens = /(,)|(\[|{)|(}|])|"(?:[^"\\\r\n]|\\["\\\/bfnrt]|\\u[\da-fA-F]{4})*"\s*:?|true|false|null|-?(?!0\d)\d+(?:\.\d+|)(?:[eE][+-]?\d+|)/g;

jQuery.parseJSON = function( data ) {
	// Attempt to parse using the native JSON parser first
	if ( window.JSON && window.JSON.parse ) {
		// Support: Android 2.3
		// Workaround failure to string-cast null input
		return window.JSON.parse( data + "" );
	}

	var requireNonComma,
		depth = null,
		str = jQuery.trim( data + "" );

	// Guard against invalid (and possibly dangerous) input by ensuring that nothing remains
	// after removing valid tokens
	return str && !jQuery.trim( str.replace( rvalidtokens, function( token, comma, open, close ) {

		// Force termination if we see a misplaced comma
		if ( requireNonComma && comma ) {
			depth = 0;
		}

		// Perform no more replacements after returning to outermost depth
		if ( depth === 0 ) {
			return token;
		}

		// Commas must not follow "[", "{", or ","
		requireNonComma = open || comma;

		// Determine new depth
		// array/object open ("[" or "{"): depth += true - false (increment)
		// array/object close ("]" or "}"): depth += false - true (decrement)
		// other cases ("," or primitive): depth += true - true (numeric cast)
		depth += !close - !open;

		// Remove this token
		return "";
	}) ) ?
		( Function( "return " + str ) )() :
		jQuery.error( "Invalid JSON: " + data );
};


// Cross-browser xml parsing
jQuery.parseXML = function( data ) {
	var xml, tmp;
	if ( !data || typeof data !== "string" ) {
		return null;
	}
	try {
		if ( window.DOMParser ) { // Standard
			tmp = new DOMParser();
			xml = tmp.parseFromString( data, "text/xml" );
		} else { // IE
			xml = new ActiveXObject( "Microsoft.XMLDOM" );
			xml.async = "false";
			xml.loadXML( data );
		}
	} catch( e ) {
		xml = undefined;
	}
	if ( !xml || !xml.documentElement || xml.getElementsByTagName( "parsererror" ).length ) {
		jQuery.error( "Invalid XML: " + data );
	}
	return xml;
};


var
	// Document location
	ajaxLocParts,
	ajaxLocation,

	rhash = /#.*$/,
	rts = /([?&])_=[^&]*/,
	rheaders = /^(.*?):[ \t]*([^\r\n]*)\r?$/mg, // IE leaves an \r character at EOL
	// #7653, #8125, #8152: local protocol detection
	rlocalProtocol = /^(?:about|app|app-storage|.+-extension|file|res|widget):$/,
	rnoContent = /^(?:GET|HEAD)$/,
	rprotocol = /^\/\//,
	rurl = /^([\w.+-]+:)(?:\/\/(?:[^\/?#]*@|)([^\/?#:]*)(?::(\d+)|)|)/,

	/* Prefilters
	 * 1) They are useful to introduce custom dataTypes (see ajax/jsonp.js for an example)
	 * 2) These are called:
	 *    - BEFORE asking for a transport
	 *    - AFTER param serialization (s.data is a string if s.processData is true)
	 * 3) key is the dataType
	 * 4) the catchall symbol "*" can be used
	 * 5) execution will start with transport dataType and THEN continue down to "*" if needed
	 */
	prefilters = {},

	/* Transports bindings
	 * 1) key is the dataType
	 * 2) the catchall symbol "*" can be used
	 * 3) selection will start with transport dataType and THEN go to "*" if needed
	 */
	transports = {},

	// Avoid comment-prolog char sequence (#10098); must appease lint and evade compression
	allTypes = "*/".concat("*");

// #8138, IE may throw an exception when accessing
// a field from window.location if document.domain has been set
try {
	ajaxLocation = location.href;
} catch( e ) {
	// Use the href attribute of an A element
	// since IE will modify it given document.location
	ajaxLocation = document.createElement( "a" );
	ajaxLocation.href = "";
	ajaxLocation = ajaxLocation.href;
}

// Segment location into parts
ajaxLocParts = rurl.exec( ajaxLocation.toLowerCase() ) || [];

// Base "constructor" for jQuery.ajaxPrefilter and jQuery.ajaxTransport
function addToPrefiltersOrTransports( structure ) {

	// dataTypeExpression is optional and defaults to "*"
	return function( dataTypeExpression, func ) {

		if ( typeof dataTypeExpression !== "string" ) {
			func = dataTypeExpression;
			dataTypeExpression = "*";
		}

		var dataType,
			i = 0,
			dataTypes = dataTypeExpression.toLowerCase().match( rnotwhite ) || [];

		if ( jQuery.isFunction( func ) ) {
			// For each dataType in the dataTypeExpression
			while ( (dataType = dataTypes[i++]) ) {
				// Prepend if requested
				if ( dataType.charAt( 0 ) === "+" ) {
					dataType = dataType.slice( 1 ) || "*";
					(structure[ dataType ] = structure[ dataType ] || []).unshift( func );

				// Otherwise append
				} else {
					(structure[ dataType ] = structure[ dataType ] || []).push( func );
				}
			}
		}
	};
}

// Base inspection function for prefilters and transports
function inspectPrefiltersOrTransports( structure, options, originalOptions, jqXHR ) {

	var inspected = {},
		seekingTransport = ( structure === transports );

	function inspect( dataType ) {
		var selected;
		inspected[ dataType ] = true;
		jQuery.each( structure[ dataType ] || [], function( _, prefilterOrFactory ) {
			var dataTypeOrTransport = prefilterOrFactory( options, originalOptions, jqXHR );
			if ( typeof dataTypeOrTransport === "string" && !seekingTransport && !inspected[ dataTypeOrTransport ] ) {
				options.dataTypes.unshift( dataTypeOrTransport );
				inspect( dataTypeOrTransport );
				return false;
			} else if ( seekingTransport ) {
				return !( selected = dataTypeOrTransport );
			}
		});
		return selected;
	}

	return inspect( options.dataTypes[ 0 ] ) || !inspected[ "*" ] && inspect( "*" );
}

// A special extend for ajax options
// that takes "flat" options (not to be deep extended)
// Fixes #9887
function ajaxExtend( target, src ) {
	var deep, key,
		flatOptions = jQuery.ajaxSettings.flatOptions || {};

	for ( key in src ) {
		if ( src[ key ] !== undefined ) {
			( flatOptions[ key ] ? target : ( deep || (deep = {}) ) )[ key ] = src[ key ];
		}
	}
	if ( deep ) {
		jQuery.extend( true, target, deep );
	}

	return target;
}

/* Handles responses to an ajax request:
 * - finds the right dataType (mediates between content-type and expected dataType)
 * - returns the corresponding response
 */
function ajaxHandleResponses( s, jqXHR, responses ) {
	var firstDataType, ct, finalDataType, type,
		contents = s.contents,
		dataTypes = s.dataTypes;

	// Remove auto dataType and get content-type in the process
	while ( dataTypes[ 0 ] === "*" ) {
		dataTypes.shift();
		if ( ct === undefined ) {
			ct = s.mimeType || jqXHR.getResponseHeader("Content-Type");
		}
	}

	// Check if we're dealing with a known content-type
	if ( ct ) {
		for ( type in contents ) {
			if ( contents[ type ] && contents[ type ].test( ct ) ) {
				dataTypes.unshift( type );
				break;
			}
		}
	}

	// Check to see if we have a response for the expected dataType
	if ( dataTypes[ 0 ] in responses ) {
		finalDataType = dataTypes[ 0 ];
	} else {
		// Try convertible dataTypes
		for ( type in responses ) {
			if ( !dataTypes[ 0 ] || s.converters[ type + " " + dataTypes[0] ] ) {
				finalDataType = type;
				break;
			}
			if ( !firstDataType ) {
				firstDataType = type;
			}
		}
		// Or just use first one
		finalDataType = finalDataType || firstDataType;
	}

	// If we found a dataType
	// We add the dataType to the list if needed
	// and return the corresponding response
	if ( finalDataType ) {
		if ( finalDataType !== dataTypes[ 0 ] ) {
			dataTypes.unshift( finalDataType );
		}
		return responses[ finalDataType ];
	}
}

/* Chain conversions given the request and the original response
 * Also sets the responseXXX fields on the jqXHR instance
 */
function ajaxConvert( s, response, jqXHR, isSuccess ) {
	var conv2, current, conv, tmp, prev,
		converters = {},
		// Work with a copy of dataTypes in case we need to modify it for conversion
		dataTypes = s.dataTypes.slice();

	// Create converters map with lowercased keys
	if ( dataTypes[ 1 ] ) {
		for ( conv in s.converters ) {
			converters[ conv.toLowerCase() ] = s.converters[ conv ];
		}
	}

	current = dataTypes.shift();

	// Convert to each sequential dataType
	while ( current ) {

		if ( s.responseFields[ current ] ) {
			jqXHR[ s.responseFields[ current ] ] = response;
		}

		// Apply the dataFilter if provided
		if ( !prev && isSuccess && s.dataFilter ) {
			response = s.dataFilter( response, s.dataType );
		}

		prev = current;
		current = dataTypes.shift();

		if ( current ) {

			// There's only work to do if current dataType is non-auto
			if ( current === "*" ) {

				current = prev;

			// Convert response if prev dataType is non-auto and differs from current
			} else if ( prev !== "*" && prev !== current ) {

				// Seek a direct converter
				conv = converters[ prev + " " + current ] || converters[ "* " + current ];

				// If none found, seek a pair
				if ( !conv ) {
					for ( conv2 in converters ) {

						// If conv2 outputs current
						tmp = conv2.split( " " );
						if ( tmp[ 1 ] === current ) {

							// If prev can be converted to accepted input
							conv = converters[ prev + " " + tmp[ 0 ] ] ||
								converters[ "* " + tmp[ 0 ] ];
							if ( conv ) {
								// Condense equivalence converters
								if ( conv === true ) {
									conv = converters[ conv2 ];

								// Otherwise, insert the intermediate dataType
								} else if ( converters[ conv2 ] !== true ) {
									current = tmp[ 0 ];
									dataTypes.unshift( tmp[ 1 ] );
								}
								break;
							}
						}
					}
				}

				// Apply converter (if not an equivalence)
				if ( conv !== true ) {

					// Unless errors are allowed to bubble, catch and return them
					if ( conv && s[ "throws" ] ) {
						response = conv( response );
					} else {
						try {
							response = conv( response );
						} catch ( e ) {
							return { state: "parsererror", error: conv ? e : "No conversion from " + prev + " to " + current };
						}
					}
				}
			}
		}
	}

	return { state: "success", data: response };
}

jQuery.extend({

	// Counter for holding the number of active queries
	active: 0,

	// Last-Modified header cache for next request
	lastModified: {},
	etag: {},

	ajaxSettings: {
		url: ajaxLocation,
		type: "GET",
		isLocal: rlocalProtocol.test( ajaxLocParts[ 1 ] ),
		global: true,
		processData: true,
		async: true,
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		/*
		timeout: 0,
		data: null,
		dataType: null,
		username: null,
		password: null,
		cache: null,
		throws: false,
		traditional: false,
		headers: {},
		*/

		accepts: {
			"*": allTypes,
			text: "text/plain",
			html: "text/html",
			xml: "application/xml, text/xml",
			json: "application/json, text/javascript"
		},

		contents: {
			xml: /xml/,
			html: /html/,
			json: /json/
		},

		responseFields: {
			xml: "responseXML",
			text: "responseText",
			json: "responseJSON"
		},

		// Data converters
		// Keys separate source (or catchall "*") and destination types with a single space
		converters: {

			// Convert anything to text
			"* text": String,

			// Text to html (true = no transformation)
			"text html": true,

			// Evaluate text as a json expression
			"text json": jQuery.parseJSON,

			// Parse text as xml
			"text xml": jQuery.parseXML
		},

		// For options that shouldn't be deep extended:
		// you can add your own custom options here if
		// and when you create one that shouldn't be
		// deep extended (see ajaxExtend)
		flatOptions: {
			url: true,
			context: true
		}
	},

	// Creates a full fledged settings object into target
	// with both ajaxSettings and settings fields.
	// If target is omitted, writes into ajaxSettings.
	ajaxSetup: function( target, settings ) {
		return settings ?

			// Building a settings object
			ajaxExtend( ajaxExtend( target, jQuery.ajaxSettings ), settings ) :

			// Extending ajaxSettings
			ajaxExtend( jQuery.ajaxSettings, target );
	},

	ajaxPrefilter: addToPrefiltersOrTransports( prefilters ),
	ajaxTransport: addToPrefiltersOrTransports( transports ),

	// Main method
	ajax: function( url, options ) {

		// If url is an object, simulate pre-1.5 signature
		if ( typeof url === "object" ) {
			options = url;
			url = undefined;
		}

		// Force options to be an object
		options = options || {};

		var // Cross-domain detection vars
			parts,
			// Loop variable
			i,
			// URL without anti-cache param
			cacheURL,
			// Response headers as string
			responseHeadersString,
			// timeout handle
			timeoutTimer,

			// To know if global events are to be dispatched
			fireGlobals,

			transport,
			// Response headers
			responseHeaders,
			// Create the final options object
			s = jQuery.ajaxSetup( {}, options ),
			// Callbacks context
			callbackContext = s.context || s,
			// Context for global events is callbackContext if it is a DOM node or jQuery collection
			globalEventContext = s.context && ( callbackContext.nodeType || callbackContext.jquery ) ?
				jQuery( callbackContext ) :
				jQuery.event,
			// Deferreds
			deferred = jQuery.Deferred(),
			completeDeferred = jQuery.Callbacks("once memory"),
			// Status-dependent callbacks
			statusCode = s.statusCode || {},
			// Headers (they are sent all at once)
			requestHeaders = {},
			requestHeadersNames = {},
			// The jqXHR state
			state = 0,
			// Default abort message
			strAbort = "canceled",
			// Fake xhr
			jqXHR = {
				readyState: 0,

				// Builds headers hashtable if needed
				getResponseHeader: function( key ) {
					var match;
					if ( state === 2 ) {
						if ( !responseHeaders ) {
							responseHeaders = {};
							while ( (match = rheaders.exec( responseHeadersString )) ) {
								responseHeaders[ match[1].toLowerCase() ] = match[ 2 ];
							}
						}
						match = responseHeaders[ key.toLowerCase() ];
					}
					return match == null ? null : match;
				},

				// Raw string
				getAllResponseHeaders: function() {
					return state === 2 ? responseHeadersString : null;
				},

				// Caches the header
				setRequestHeader: function( name, value ) {
					var lname = name.toLowerCase();
					if ( !state ) {
						name = requestHeadersNames[ lname ] = requestHeadersNames[ lname ] || name;
						requestHeaders[ name ] = value;
					}
					return this;
				},

				// Overrides response content-type header
				overrideMimeType: function( type ) {
					if ( !state ) {
						s.mimeType = type;
					}
					return this;
				},

				// Status-dependent callbacks
				statusCode: function( map ) {
					var code;
					if ( map ) {
						if ( state < 2 ) {
							for ( code in map ) {
								// Lazy-add the new callback in a way that preserves old ones
								statusCode[ code ] = [ statusCode[ code ], map[ code ] ];
							}
						} else {
							// Execute the appropriate callbacks
							jqXHR.always( map[ jqXHR.status ] );
						}
					}
					return this;
				},

				// Cancel the request
				abort: function( statusText ) {
					var finalText = statusText || strAbort;
					if ( transport ) {
						transport.abort( finalText );
					}
					done( 0, finalText );
					return this;
				}
			};

		// Attach deferreds
		deferred.promise( jqXHR ).complete = completeDeferred.add;
		jqXHR.success = jqXHR.done;
		jqXHR.error = jqXHR.fail;

		// Remove hash character (#7531: and string promotion)
		// Add protocol if not provided (#5866: IE7 issue with protocol-less urls)
		// Handle falsy url in the settings object (#10093: consistency with old signature)
		// We also use the url parameter if available
		s.url = ( ( url || s.url || ajaxLocation ) + "" ).replace( rhash, "" ).replace( rprotocol, ajaxLocParts[ 1 ] + "//" );

		// Alias method option to type as per ticket #12004
		s.type = options.method || options.type || s.method || s.type;

		// Extract dataTypes list
		s.dataTypes = jQuery.trim( s.dataType || "*" ).toLowerCase().match( rnotwhite ) || [ "" ];

		// A cross-domain request is in order when we have a protocol:host:port mismatch
		if ( s.crossDomain == null ) {
			parts = rurl.exec( s.url.toLowerCase() );
			s.crossDomain = !!( parts &&
				( parts[ 1 ] !== ajaxLocParts[ 1 ] || parts[ 2 ] !== ajaxLocParts[ 2 ] ||
					( parts[ 3 ] || ( parts[ 1 ] === "http:" ? "80" : "443" ) ) !==
						( ajaxLocParts[ 3 ] || ( ajaxLocParts[ 1 ] === "http:" ? "80" : "443" ) ) )
			);
		}

		// Convert data if not already a string
		if ( s.data && s.processData && typeof s.data !== "string" ) {
			s.data = jQuery.param( s.data, s.traditional );
		}

		// Apply prefilters
		inspectPrefiltersOrTransports( prefilters, s, options, jqXHR );

		// If request was aborted inside a prefilter, stop there
		if ( state === 2 ) {
			return jqXHR;
		}

		// We can fire global events as of now if asked to
		fireGlobals = s.global;

		// Watch for a new set of requests
		if ( fireGlobals && jQuery.active++ === 0 ) {
			jQuery.event.trigger("ajaxStart");
		}

		// Uppercase the type
		s.type = s.type.toUpperCase();

		// Determine if request has content
		s.hasContent = !rnoContent.test( s.type );

		// Save the URL in case we're toying with the If-Modified-Since
		// and/or If-None-Match header later on
		cacheURL = s.url;

		// More options handling for requests with no content
		if ( !s.hasContent ) {

			// If data is available, append data to url
			if ( s.data ) {
				cacheURL = ( s.url += ( rquery.test( cacheURL ) ? "&" : "?" ) + s.data );
				// #9682: remove data so that it's not used in an eventual retry
				delete s.data;
			}

			// Add anti-cache in url if needed
			if ( s.cache === false ) {
				s.url = rts.test( cacheURL ) ?

					// If there is already a '_' parameter, set its value
					cacheURL.replace( rts, "$1_=" + nonce++ ) :

					// Otherwise add one to the end
					cacheURL + ( rquery.test( cacheURL ) ? "&" : "?" ) + "_=" + nonce++;
			}
		}

		// Set the If-Modified-Since and/or If-None-Match header, if in ifModified mode.
		if ( s.ifModified ) {
			if ( jQuery.lastModified[ cacheURL ] ) {
				jqXHR.setRequestHeader( "If-Modified-Since", jQuery.lastModified[ cacheURL ] );
			}
			if ( jQuery.etag[ cacheURL ] ) {
				jqXHR.setRequestHeader( "If-None-Match", jQuery.etag[ cacheURL ] );
			}
		}

		// Set the correct header, if data is being sent
		if ( s.data && s.hasContent && s.contentType !== false || options.contentType ) {
			jqXHR.setRequestHeader( "Content-Type", s.contentType );
		}

		// Set the Accepts header for the server, depending on the dataType
		jqXHR.setRequestHeader(
			"Accept",
			s.dataTypes[ 0 ] && s.accepts[ s.dataTypes[0] ] ?
				s.accepts[ s.dataTypes[0] ] + ( s.dataTypes[ 0 ] !== "*" ? ", " + allTypes + "; q=0.01" : "" ) :
				s.accepts[ "*" ]
		);

		// Check for headers option
		for ( i in s.headers ) {
			jqXHR.setRequestHeader( i, s.headers[ i ] );
		}

		// Allow custom headers/mimetypes and early abort
		if ( s.beforeSend && ( s.beforeSend.call( callbackContext, jqXHR, s ) === false || state === 2 ) ) {
			// Abort if not done already and return
			return jqXHR.abort();
		}

		// aborting is no longer a cancellation
		strAbort = "abort";

		// Install callbacks on deferreds
		for ( i in { success: 1, error: 1, complete: 1 } ) {
			jqXHR[ i ]( s[ i ] );
		}

		// Get transport
		transport = inspectPrefiltersOrTransports( transports, s, options, jqXHR );

		// If no transport, we auto-abort
		if ( !transport ) {
			done( -1, "No Transport" );
		} else {
			jqXHR.readyState = 1;

			// Send global event
			if ( fireGlobals ) {
				globalEventContext.trigger( "ajaxSend", [ jqXHR, s ] );
			}
			// Timeout
			if ( s.async && s.timeout > 0 ) {
				timeoutTimer = setTimeout(function() {
					jqXHR.abort("timeout");
				}, s.timeout );
			}

			try {
				state = 1;
				transport.send( requestHeaders, done );
			} catch ( e ) {
				// Propagate exception as error if not done
				if ( state < 2 ) {
					done( -1, e );
				// Simply rethrow otherwise
				} else {
					throw e;
				}
			}
		}

		// Callback for when everything is done
		function done( status, nativeStatusText, responses, headers ) {
			var isSuccess, success, error, response, modified,
				statusText = nativeStatusText;

			// Called once
			if ( state === 2 ) {
				return;
			}

			// State is "done" now
			state = 2;

			// Clear timeout if it exists
			if ( timeoutTimer ) {
				clearTimeout( timeoutTimer );
			}

			// Dereference transport for early garbage collection
			// (no matter how long the jqXHR object will be used)
			transport = undefined;

			// Cache response headers
			responseHeadersString = headers || "";

			// Set readyState
			jqXHR.readyState = status > 0 ? 4 : 0;

			// Determine if successful
			isSuccess = status >= 200 && status < 300 || status === 304;

			// Get response data
			if ( responses ) {
				response = ajaxHandleResponses( s, jqXHR, responses );
			}

			// Convert no matter what (that way responseXXX fields are always set)
			response = ajaxConvert( s, response, jqXHR, isSuccess );

			// If successful, handle type chaining
			if ( isSuccess ) {

				// Set the If-Modified-Since and/or If-None-Match header, if in ifModified mode.
				if ( s.ifModified ) {
					modified = jqXHR.getResponseHeader("Last-Modified");
					if ( modified ) {
						jQuery.lastModified[ cacheURL ] = modified;
					}
					modified = jqXHR.getResponseHeader("etag");
					if ( modified ) {
						jQuery.etag[ cacheURL ] = modified;
					}
				}

				// if no content
				if ( status === 204 || s.type === "HEAD" ) {
					statusText = "nocontent";

				// if not modified
				} else if ( status === 304 ) {
					statusText = "notmodified";

				// If we have data, let's convert it
				} else {
					statusText = response.state;
					success = response.data;
					error = response.error;
					isSuccess = !error;
				}
			} else {
				// We extract error from statusText
				// then normalize statusText and status for non-aborts
				error = statusText;
				if ( status || !statusText ) {
					statusText = "error";
					if ( status < 0 ) {
						status = 0;
					}
				}
			}

			// Set data for the fake xhr object
			jqXHR.status = status;
			jqXHR.statusText = ( nativeStatusText || statusText ) + "";

			// Success/Error
			if ( isSuccess ) {
				deferred.resolveWith( callbackContext, [ success, statusText, jqXHR ] );
			} else {
				deferred.rejectWith( callbackContext, [ jqXHR, statusText, error ] );
			}

			// Status-dependent callbacks
			jqXHR.statusCode( statusCode );
			statusCode = undefined;

			if ( fireGlobals ) {
				globalEventContext.trigger( isSuccess ? "ajaxSuccess" : "ajaxError",
					[ jqXHR, s, isSuccess ? success : error ] );
			}

			// Complete
			completeDeferred.fireWith( callbackContext, [ jqXHR, statusText ] );

			if ( fireGlobals ) {
				globalEventContext.trigger( "ajaxComplete", [ jqXHR, s ] );
				// Handle the global AJAX counter
				if ( !( --jQuery.active ) ) {
					jQuery.event.trigger("ajaxStop");
				}
			}
		}

		return jqXHR;
	},

	getJSON: function( url, data, callback ) {
		return jQuery.get( url, data, callback, "json" );
	},

	getScript: function( url, callback ) {
		return jQuery.get( url, undefined, callback, "script" );
	}
});

jQuery.each( [ "get", "post" ], function( i, method ) {
	jQuery[ method ] = function( url, data, callback, type ) {
		// shift arguments if data argument was omitted
		if ( jQuery.isFunction( data ) ) {
			type = type || callback;
			callback = data;
			data = undefined;
		}

		return jQuery.ajax({
			url: url,
			type: method,
			dataType: type,
			data: data,
			success: callback
		});
	};
});

// Attach a bunch of functions for handling common AJAX events
jQuery.each( [ "ajaxStart", "ajaxStop", "ajaxComplete", "ajaxError", "ajaxSuccess", "ajaxSend" ], function( i, type ) {
	jQuery.fn[ type ] = function( fn ) {
		return this.on( type, fn );
	};
});


jQuery._evalUrl = function( url ) {
	return jQuery.ajax({
		url: url,
		type: "GET",
		dataType: "script",
		async: false,
		global: false,
		"throws": true
	});
};


jQuery.fn.extend({
	wrapAll: function( html ) {
		if ( jQuery.isFunction( html ) ) {
			return this.each(function(i) {
				jQuery(this).wrapAll( html.call(this, i) );
			});
		}

		if ( this[0] ) {
			// The elements to wrap the target around
			var wrap = jQuery( html, this[0].ownerDocument ).eq(0).clone(true);

			if ( this[0].parentNode ) {
				wrap.insertBefore( this[0] );
			}

			wrap.map(function() {
				var elem = this;

				while ( elem.firstChild && elem.firstChild.nodeType === 1 ) {
					elem = elem.firstChild;
				}

				return elem;
			}).append( this );
		}

		return this;
	},

	wrapInner: function( html ) {
		if ( jQuery.isFunction( html ) ) {
			return this.each(function(i) {
				jQuery(this).wrapInner( html.call(this, i) );
			});
		}

		return this.each(function() {
			var self = jQuery( this ),
				contents = self.contents();

			if ( contents.length ) {
				contents.wrapAll( html );

			} else {
				self.append( html );
			}
		});
	},

	wrap: function( html ) {
		var isFunction = jQuery.isFunction( html );

		return this.each(function(i) {
			jQuery( this ).wrapAll( isFunction ? html.call(this, i) : html );
		});
	},

	unwrap: function() {
		return this.parent().each(function() {
			if ( !jQuery.nodeName( this, "body" ) ) {
				jQuery( this ).replaceWith( this.childNodes );
			}
		}).end();
	}
});


jQuery.expr.filters.hidden = function( elem ) {
	// Support: Opera <= 12.12
	// Opera reports offsetWidths and offsetHeights less than zero on some elements
	return elem.offsetWidth <= 0 && elem.offsetHeight <= 0 ||
		(!support.reliableHiddenOffsets() &&
			((elem.style && elem.style.display) || jQuery.css( elem, "display" )) === "none");
};

jQuery.expr.filters.visible = function( elem ) {
	return !jQuery.expr.filters.hidden( elem );
};




var r20 = /%20/g,
	rbracket = /\[\]$/,
	rCRLF = /\r?\n/g,
	rsubmitterTypes = /^(?:submit|button|image|reset|file)$/i,
	rsubmittable = /^(?:input|select|textarea|keygen)/i;

function buildParams( prefix, obj, traditional, add ) {
	var name;

	if ( jQuery.isArray( obj ) ) {
		// Serialize array item.
		jQuery.each( obj, function( i, v ) {
			if ( traditional || rbracket.test( prefix ) ) {
				// Treat each array item as a scalar.
				add( prefix, v );

			} else {
				// Item is non-scalar (array or object), encode its numeric index.
				buildParams( prefix + "[" + ( typeof v === "object" ? i : "" ) + "]", v, traditional, add );
			}
		});

	} else if ( !traditional && jQuery.type( obj ) === "object" ) {
		// Serialize object item.
		for ( name in obj ) {
			buildParams( prefix + "[" + name + "]", obj[ name ], traditional, add );
		}

	} else {
		// Serialize scalar item.
		add( prefix, obj );
	}
}

// Serialize an array of form elements or a set of
// key/values into a query string
jQuery.param = function( a, traditional ) {
	var prefix,
		s = [],
		add = function( key, value ) {
			// If value is a function, invoke it and return its value
			value = jQuery.isFunction( value ) ? value() : ( value == null ? "" : value );
			s[ s.length ] = encodeURIComponent( key ) + "=" + encodeURIComponent( value );
		};

	// Set traditional to true for jQuery <= 1.3.2 behavior.
	if ( traditional === undefined ) {
		traditional = jQuery.ajaxSettings && jQuery.ajaxSettings.traditional;
	}

	// If an array was passed in, assume that it is an array of form elements.
	if ( jQuery.isArray( a ) || ( a.jquery && !jQuery.isPlainObject( a ) ) ) {
		// Serialize the form elements
		jQuery.each( a, function() {
			add( this.name, this.value );
		});

	} else {
		// If traditional, encode the "old" way (the way 1.3.2 or older
		// did it), otherwise encode params recursively.
		for ( prefix in a ) {
			buildParams( prefix, a[ prefix ], traditional, add );
		}
	}

	// Return the resulting serialization
	return s.join( "&" ).replace( r20, "+" );
};

jQuery.fn.extend({
	serialize: function() {
		return jQuery.param( this.serializeArray() );
	},
	serializeArray: function() {
		return this.map(function() {
			// Can add propHook for "elements" to filter or add form elements
			var elements = jQuery.prop( this, "elements" );
			return elements ? jQuery.makeArray( elements ) : this;
		})
		.filter(function() {
			var type = this.type;
			// Use .is(":disabled") so that fieldset[disabled] works
			return this.name && !jQuery( this ).is( ":disabled" ) &&
				rsubmittable.test( this.nodeName ) && !rsubmitterTypes.test( type ) &&
				( this.checked || !rcheckableType.test( type ) );
		})
		.map(function( i, elem ) {
			var val = jQuery( this ).val();

			return val == null ?
				null :
				jQuery.isArray( val ) ?
					jQuery.map( val, function( val ) {
						return { name: elem.name, value: val.replace( rCRLF, "\r\n" ) };
					}) :
					{ name: elem.name, value: val.replace( rCRLF, "\r\n" ) };
		}).get();
	}
});


// Create the request object
// (This is still attached to ajaxSettings for backward compatibility)
jQuery.ajaxSettings.xhr = window.ActiveXObject !== undefined ?
	// Support: IE6+
	function() {

		// XHR cannot access local files, always use ActiveX for that case
		return !this.isLocal &&

			// Support: IE7-8
			// oldIE XHR does not support non-RFC2616 methods (#13240)
			// See http://msdn.microsoft.com/en-us/library/ie/ms536648(v=vs.85).aspx
			// and http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9
			// Although this check for six methods instead of eight
			// since IE also does not support "trace" and "connect"
			/^(get|post|head|put|delete|options)$/i.test( this.type ) &&

			createStandardXHR() || createActiveXHR();
	} :
	// For all other browsers, use the standard XMLHttpRequest object
	createStandardXHR;

var xhrId = 0,
	xhrCallbacks = {},
	xhrSupported = jQuery.ajaxSettings.xhr();

// Support: IE<10
// Open requests must be manually aborted on unload (#5280)
if ( window.ActiveXObject ) {
	jQuery( window ).on( "unload", function() {
		for ( var key in xhrCallbacks ) {
			xhrCallbacks[ key ]( undefined, true );
		}
	});
}

// Determine support properties
support.cors = !!xhrSupported && ( "withCredentials" in xhrSupported );
xhrSupported = support.ajax = !!xhrSupported;

// Create transport if the browser can provide an xhr
if ( xhrSupported ) {

	jQuery.ajaxTransport(function( options ) {
		// Cross domain only allowed if supported through XMLHttpRequest
		if ( !options.crossDomain || support.cors ) {

			var callback;

			return {
				send: function( headers, complete ) {
					var i,
						xhr = options.xhr(),
						id = ++xhrId;

					// Open the socket
					xhr.open( options.type, options.url, options.async, options.username, options.password );

					// Apply custom fields if provided
					if ( options.xhrFields ) {
						for ( i in options.xhrFields ) {
							xhr[ i ] = options.xhrFields[ i ];
						}
					}

					// Override mime type if needed
					if ( options.mimeType && xhr.overrideMimeType ) {
						xhr.overrideMimeType( options.mimeType );
					}

					// X-Requested-With header
					// For cross-domain requests, seeing as conditions for a preflight are
					// akin to a jigsaw puzzle, we simply never set it to be sure.
					// (it can always be set on a per-request basis or even using ajaxSetup)
					// For same-domain requests, won't change header if already provided.
					if ( !options.crossDomain && !headers["X-Requested-With"] ) {
						headers["X-Requested-With"] = "XMLHttpRequest";
					}

					// Set headers
					for ( i in headers ) {
						// Support: IE<9
						// IE's ActiveXObject throws a 'Type Mismatch' exception when setting
						// request header to a null-value.
						//
						// To keep consistent with other XHR implementations, cast the value
						// to string and ignore `undefined`.
						if ( headers[ i ] !== undefined ) {
							xhr.setRequestHeader( i, headers[ i ] + "" );
						}
					}

					// Do send the request
					// This may raise an exception which is actually
					// handled in jQuery.ajax (so no try/catch here)
					xhr.send( ( options.hasContent && options.data ) || null );

					// Listener
					callback = function( _, isAbort ) {
						var status, statusText, responses;

						// Was never called and is aborted or complete
						if ( callback && ( isAbort || xhr.readyState === 4 ) ) {
							// Clean up
							delete xhrCallbacks[ id ];
							callback = undefined;
							xhr.onreadystatechange = jQuery.noop;

							// Abort manually if needed
							if ( isAbort ) {
								if ( xhr.readyState !== 4 ) {
									xhr.abort();
								}
							} else {
								responses = {};
								status = xhr.status;

								// Support: IE<10
								// Accessing binary-data responseText throws an exception
								// (#11426)
								if ( typeof xhr.responseText === "string" ) {
									responses.text = xhr.responseText;
								}

								// Firefox throws an exception when accessing
								// statusText for faulty cross-domain requests
								try {
									statusText = xhr.statusText;
								} catch( e ) {
									// We normalize with Webkit giving an empty statusText
									statusText = "";
								}

								// Filter status for non standard behaviors

								// If the request is local and we have data: assume a success
								// (success with no data won't get notified, that's the best we
								// can do given current implementations)
								if ( !status && options.isLocal && !options.crossDomain ) {
									status = responses.text ? 200 : 404;
								// IE - #1450: sometimes returns 1223 when it should be 204
								} else if ( status === 1223 ) {
									status = 204;
								}
							}
						}

						// Call complete if needed
						if ( responses ) {
							complete( status, statusText, responses, xhr.getAllResponseHeaders() );
						}
					};

					if ( !options.async ) {
						// if we're in sync mode we fire the callback
						callback();
					} else if ( xhr.readyState === 4 ) {
						// (IE6 & IE7) if it's in cache and has been
						// retrieved directly we need to fire the callback
						setTimeout( callback );
					} else {
						// Add to the list of active xhr callbacks
						xhr.onreadystatechange = xhrCallbacks[ id ] = callback;
					}
				},

				abort: function() {
					if ( callback ) {
						callback( undefined, true );
					}
				}
			};
		}
	});
}

// Functions to create xhrs
function createStandardXHR() {
	try {
		return new window.XMLHttpRequest();
	} catch( e ) {}
}

function createActiveXHR() {
	try {
		return new window.ActiveXObject( "Microsoft.XMLHTTP" );
	} catch( e ) {}
}




// Install script dataType
jQuery.ajaxSetup({
	accepts: {
		script: "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
	},
	contents: {
		script: /(?:java|ecma)script/
	},
	converters: {
		"text script": function( text ) {
			jQuery.globalEval( text );
			return text;
		}
	}
});

// Handle cache's special case and global
jQuery.ajaxPrefilter( "script", function( s ) {
	if ( s.cache === undefined ) {
		s.cache = false;
	}
	if ( s.crossDomain ) {
		s.type = "GET";
		s.global = false;
	}
});

// Bind script tag hack transport
jQuery.ajaxTransport( "script", function(s) {

	// This transport only deals with cross domain requests
	if ( s.crossDomain ) {

		var script,
			head = document.head || jQuery("head")[0] || document.documentElement;

		return {

			send: function( _, callback ) {

				script = document.createElement("script");

				script.async = true;

				if ( s.scriptCharset ) {
					script.charset = s.scriptCharset;
				}

				script.src = s.url;

				// Attach handlers for all browsers
				script.onload = script.onreadystatechange = function( _, isAbort ) {

					if ( isAbort || !script.readyState || /loaded|complete/.test( script.readyState ) ) {

						// Handle memory leak in IE
						script.onload = script.onreadystatechange = null;

						// Remove the script
						if ( script.parentNode ) {
							script.parentNode.removeChild( script );
						}

						// Dereference the script
						script = null;

						// Callback if not abort
						if ( !isAbort ) {
							callback( 200, "success" );
						}
					}
				};

				// Circumvent IE6 bugs with base elements (#2709 and #4378) by prepending
				// Use native DOM manipulation to avoid our domManip AJAX trickery
				head.insertBefore( script, head.firstChild );
			},

			abort: function() {
				if ( script ) {
					script.onload( undefined, true );
				}
			}
		};
	}
});




var oldCallbacks = [],
	rjsonp = /(=)\?(?=&|$)|\?\?/;

// Default jsonp settings
jQuery.ajaxSetup({
	jsonp: "callback",
	jsonpCallback: function() {
		var callback = oldCallbacks.pop() || ( jQuery.expando + "_" + ( nonce++ ) );
		this[ callback ] = true;
		return callback;
	}
});

// Detect, normalize options and install callbacks for jsonp requests
jQuery.ajaxPrefilter( "json jsonp", function( s, originalSettings, jqXHR ) {

	var callbackName, overwritten, responseContainer,
		jsonProp = s.jsonp !== false && ( rjsonp.test( s.url ) ?
			"url" :
			typeof s.data === "string" && !( s.contentType || "" ).indexOf("application/x-www-form-urlencoded") && rjsonp.test( s.data ) && "data"
		);

	// Handle iff the expected data type is "jsonp" or we have a parameter to set
	if ( jsonProp || s.dataTypes[ 0 ] === "jsonp" ) {

		// Get callback name, remembering preexisting value associated with it
		callbackName = s.jsonpCallback = jQuery.isFunction( s.jsonpCallback ) ?
			s.jsonpCallback() :
			s.jsonpCallback;

		// Insert callback into url or form data
		if ( jsonProp ) {
			s[ jsonProp ] = s[ jsonProp ].replace( rjsonp, "$1" + callbackName );
		} else if ( s.jsonp !== false ) {
			s.url += ( rquery.test( s.url ) ? "&" : "?" ) + s.jsonp + "=" + callbackName;
		}

		// Use data converter to retrieve json after script execution
		s.converters["script json"] = function() {
			if ( !responseContainer ) {
				jQuery.error( callbackName + " was not called" );
			}
			return responseContainer[ 0 ];
		};

		// force json dataType
		s.dataTypes[ 0 ] = "json";

		// Install callback
		overwritten = window[ callbackName ];
		window[ callbackName ] = function() {
			responseContainer = arguments;
		};

		// Clean-up function (fires after converters)
		jqXHR.always(function() {
			// Restore preexisting value
			window[ callbackName ] = overwritten;

			// Save back as free
			if ( s[ callbackName ] ) {
				// make sure that re-using the options doesn't screw things around
				s.jsonpCallback = originalSettings.jsonpCallback;

				// save the callback name for future use
				oldCallbacks.push( callbackName );
			}

			// Call if it was a function and we have a response
			if ( responseContainer && jQuery.isFunction( overwritten ) ) {
				overwritten( responseContainer[ 0 ] );
			}

			responseContainer = overwritten = undefined;
		});

		// Delegate to script
		return "script";
	}
});




// data: string of html
// context (optional): If specified, the fragment will be created in this context, defaults to document
// keepScripts (optional): If true, will include scripts passed in the html string
jQuery.parseHTML = function( data, context, keepScripts ) {
	if ( !data || typeof data !== "string" ) {
		return null;
	}
	if ( typeof context === "boolean" ) {
		keepScripts = context;
		context = false;
	}
	context = context || document;

	var parsed = rsingleTag.exec( data ),
		scripts = !keepScripts && [];

	// Single tag
	if ( parsed ) {
		return [ context.createElement( parsed[1] ) ];
	}

	parsed = jQuery.buildFragment( [ data ], context, scripts );

	if ( scripts && scripts.length ) {
		jQuery( scripts ).remove();
	}

	return jQuery.merge( [], parsed.childNodes );
};


// Keep a copy of the old load method
var _load = jQuery.fn.load;

/**
 * Load a url into a page
 */
jQuery.fn.load = function( url, params, callback ) {
	if ( typeof url !== "string" && _load ) {
		return _load.apply( this, arguments );
	}

	var selector, response, type,
		self = this,
		off = url.indexOf(" ");

	if ( off >= 0 ) {
		selector = jQuery.trim( url.slice( off, url.length ) );
		url = url.slice( 0, off );
	}

	// If it's a function
	if ( jQuery.isFunction( params ) ) {

		// We assume that it's the callback
		callback = params;
		params = undefined;

	// Otherwise, build a param string
	} else if ( params && typeof params === "object" ) {
		type = "POST";
	}

	// If we have elements to modify, make the request
	if ( self.length > 0 ) {
		jQuery.ajax({
			url: url,

			// if "type" variable is undefined, then "GET" method will be used
			type: type,
			dataType: "html",
			data: params
		}).done(function( responseText ) {

			// Save response for use in complete callback
			response = arguments;

			self.html( selector ?

				// If a selector was specified, locate the right elements in a dummy div
				// Exclude scripts to avoid IE 'Permission Denied' errors
				jQuery("<div>").append( jQuery.parseHTML( responseText ) ).find( selector ) :

				// Otherwise use the full result
				responseText );

		}).complete( callback && function( jqXHR, status ) {
			self.each( callback, response || [ jqXHR.responseText, status, jqXHR ] );
		});
	}

	return this;
};




jQuery.expr.filters.animated = function( elem ) {
	return jQuery.grep(jQuery.timers, function( fn ) {
		return elem === fn.elem;
	}).length;
};





var docElem = window.document.documentElement;

/**
 * Gets a window from an element
 */
function getWindow( elem ) {
	return jQuery.isWindow( elem ) ?
		elem :
		elem.nodeType === 9 ?
			elem.defaultView || elem.parentWindow :
			false;
}

jQuery.offset = {
	setOffset: function( elem, options, i ) {
		var curPosition, curLeft, curCSSTop, curTop, curOffset, curCSSLeft, calculatePosition,
			position = jQuery.css( elem, "position" ),
			curElem = jQuery( elem ),
			props = {};

		// set position first, in-case top/left are set even on static elem
		if ( position === "static" ) {
			elem.style.position = "relative";
		}

		curOffset = curElem.offset();
		curCSSTop = jQuery.css( elem, "top" );
		curCSSLeft = jQuery.css( elem, "left" );
		calculatePosition = ( position === "absolute" || position === "fixed" ) &&
			jQuery.inArray("auto", [ curCSSTop, curCSSLeft ] ) > -1;

		// need to be able to calculate position if either top or left is auto and position is either absolute or fixed
		if ( calculatePosition ) {
			curPosition = curElem.position();
			curTop = curPosition.top;
			curLeft = curPosition.left;
		} else {
			curTop = parseFloat( curCSSTop ) || 0;
			curLeft = parseFloat( curCSSLeft ) || 0;
		}

		if ( jQuery.isFunction( options ) ) {
			options = options.call( elem, i, curOffset );
		}

		if ( options.top != null ) {
			props.top = ( options.top - curOffset.top ) + curTop;
		}
		if ( options.left != null ) {
			props.left = ( options.left - curOffset.left ) + curLeft;
		}

		if ( "using" in options ) {
			options.using.call( elem, props );
		} else {
			curElem.css( props );
		}
	}
};

jQuery.fn.extend({
	offset: function( options ) {
		if ( arguments.length ) {
			return options === undefined ?
				this :
				this.each(function( i ) {
					jQuery.offset.setOffset( this, options, i );
				});
		}

		var docElem, win,
			box = { top: 0, left: 0 },
			elem = this[ 0 ],
			doc = elem && elem.ownerDocument;

		if ( !doc ) {
			return;
		}

		docElem = doc.documentElement;

		// Make sure it's not a disconnected DOM node
		if ( !jQuery.contains( docElem, elem ) ) {
			return box;
		}

		// If we don't have gBCR, just use 0,0 rather than error
		// BlackBerry 5, iOS 3 (original iPhone)
		if ( typeof elem.getBoundingClientRect !== strundefined ) {
			box = elem.getBoundingClientRect();
		}
		win = getWindow( doc );
		return {
			top: box.top  + ( win.pageYOffset || docElem.scrollTop )  - ( docElem.clientTop  || 0 ),
			left: box.left + ( win.pageXOffset || docElem.scrollLeft ) - ( docElem.clientLeft || 0 )
		};
	},

	position: function() {
		if ( !this[ 0 ] ) {
			return;
		}

		var offsetParent, offset,
			parentOffset = { top: 0, left: 0 },
			elem = this[ 0 ];

		// fixed elements are offset from window (parentOffset = {top:0, left: 0}, because it is its only offset parent
		if ( jQuery.css( elem, "position" ) === "fixed" ) {
			// we assume that getBoundingClientRect is available when computed position is fixed
			offset = elem.getBoundingClientRect();
		} else {
			// Get *real* offsetParent
			offsetParent = this.offsetParent();

			// Get correct offsets
			offset = this.offset();
			if ( !jQuery.nodeName( offsetParent[ 0 ], "html" ) ) {
				parentOffset = offsetParent.offset();
			}

			// Add offsetParent borders
			parentOffset.top  += jQuery.css( offsetParent[ 0 ], "borderTopWidth", true );
			parentOffset.left += jQuery.css( offsetParent[ 0 ], "borderLeftWidth", true );
		}

		// Subtract parent offsets and element margins
		// note: when an element has margin: auto the offsetLeft and marginLeft
		// are the same in Safari causing offset.left to incorrectly be 0
		return {
			top:  offset.top  - parentOffset.top - jQuery.css( elem, "marginTop", true ),
			left: offset.left - parentOffset.left - jQuery.css( elem, "marginLeft", true)
		};
	},

	offsetParent: function() {
		return this.map(function() {
			var offsetParent = this.offsetParent || docElem;

			while ( offsetParent && ( !jQuery.nodeName( offsetParent, "html" ) && jQuery.css( offsetParent, "position" ) === "static" ) ) {
				offsetParent = offsetParent.offsetParent;
			}
			return offsetParent || docElem;
		});
	}
});

// Create scrollLeft and scrollTop methods
jQuery.each( { scrollLeft: "pageXOffset", scrollTop: "pageYOffset" }, function( method, prop ) {
	var top = /Y/.test( prop );

	jQuery.fn[ method ] = function( val ) {
		return access( this, function( elem, method, val ) {
			var win = getWindow( elem );

			if ( val === undefined ) {
				return win ? (prop in win) ? win[ prop ] :
					win.document.documentElement[ method ] :
					elem[ method ];
			}

			if ( win ) {
				win.scrollTo(
					!top ? val : jQuery( win ).scrollLeft(),
					top ? val : jQuery( win ).scrollTop()
				);

			} else {
				elem[ method ] = val;
			}
		}, method, val, arguments.length, null );
	};
});

// Add the top/left cssHooks using jQuery.fn.position
// Webkit bug: https://bugs.webkit.org/show_bug.cgi?id=29084
// getComputedStyle returns percent when specified for top/left/bottom/right
// rather than make the css module depend on the offset module, we just check for it here
jQuery.each( [ "top", "left" ], function( i, prop ) {
	jQuery.cssHooks[ prop ] = addGetHookIf( support.pixelPosition,
		function( elem, computed ) {
			if ( computed ) {
				computed = curCSS( elem, prop );
				// if curCSS returns percentage, fallback to offset
				return rnumnonpx.test( computed ) ?
					jQuery( elem ).position()[ prop ] + "px" :
					computed;
			}
		}
	);
});


// Create innerHeight, innerWidth, height, width, outerHeight and outerWidth methods
jQuery.each( { Height: "height", Width: "width" }, function( name, type ) {
	jQuery.each( { padding: "inner" + name, content: type, "": "outer" + name }, function( defaultExtra, funcName ) {
		// margin is only for outerHeight, outerWidth
		jQuery.fn[ funcName ] = function( margin, value ) {
			var chainable = arguments.length && ( defaultExtra || typeof margin !== "boolean" ),
				extra = defaultExtra || ( margin === true || value === true ? "margin" : "border" );

			return access( this, function( elem, type, value ) {
				var doc;

				if ( jQuery.isWindow( elem ) ) {
					// As of 5/8/2012 this will yield incorrect results for Mobile Safari, but there
					// isn't a whole lot we can do. See pull request at this URL for discussion:
					// https://github.com/jquery/jquery/pull/764
					return elem.document.documentElement[ "client" + name ];
				}

				// Get document width or height
				if ( elem.nodeType === 9 ) {
					doc = elem.documentElement;

					// Either scroll[Width/Height] or offset[Width/Height] or client[Width/Height], whichever is greatest
					// unfortunately, this causes bug #3838 in IE6/8 only, but there is currently no good, small way to fix it.
					return Math.max(
						elem.body[ "scroll" + name ], doc[ "scroll" + name ],
						elem.body[ "offset" + name ], doc[ "offset" + name ],
						doc[ "client" + name ]
					);
				}

				return value === undefined ?
					// Get width or height on the element, requesting but not forcing parseFloat
					jQuery.css( elem, type, extra ) :

					// Set width or height on the element
					jQuery.style( elem, type, value, extra );
			}, type, chainable ? margin : undefined, chainable, null );
		};
	});
});


// The number of elements contained in the matched element set
jQuery.fn.size = function() {
	return this.length;
};

jQuery.fn.andSelf = jQuery.fn.addBack;




// Register as a named AMD module, since jQuery can be concatenated with other
// files that may use define, but not via a proper concatenation script that
// understands anonymous AMD modules. A named AMD is safest and most robust
// way to register. Lowercase jquery is used because AMD module names are
// derived from file names, and jQuery is normally delivered in a lowercase
// file name. Do this after creating the global so that if an AMD module wants
// to call noConflict to hide this version of jQuery, it will work.

// Note that for maximum portability, libraries that are not jQuery should
// declare themselves as anonymous modules, and avoid setting a global if an
// AMD loader is present. jQuery is a special case. For more information, see
// https://github.com/jrburke/requirejs/wiki/Updating-existing-libraries#wiki-anon

if ( typeof define === "function" && define.amd ) {
	define( "jquery", [], function() {
		return jQuery;
	});
}




var
	// Map over jQuery in case of overwrite
	_jQuery = window.jQuery,

	// Map over the $ in case of overwrite
	_$ = window.$;

jQuery.noConflict = function( deep ) {
	if ( window.$ === jQuery ) {
		window.$ = _$;
	}

	if ( deep && window.jQuery === jQuery ) {
		window.jQuery = _jQuery;
	}

	return jQuery;
};

// Expose jQuery and $ identifiers, even in
// AMD (#7102#comment:10, https://github.com/jquery/jquery/pull/557)
// and CommonJS for browser emulators (#13566)
if ( typeof noGlobal === strundefined ) {
	window.jQuery = window.$ = jQuery;
}




return jQuery;

}));

/*global jQuery: true, KP: true */

/** @lends Mediator */
(function (global, namespace) {
	"use strict";
	/**
	 * class to contain callbacks
	 * @param callback
	 * @param [receiver]
	 * @constructor
	 */
	function Subscription(callback, receiver) {
		this.callback = callback;
		this.receiver = receiver;
		this.topic = null;
	}
	/**
	 * invokes the callback
	 * @param args
	 * @param topic
	 */
	Subscription.prototype.call = function (args, topic) {
		this.topic = topic;
		this.callback.apply(this.receiver || this, args);
	};
	/**
	 * class to wrap a list of subscriptions
	 * @constructor
	 */
	function Topic() {
		this.subscriptions = [];
	}
	/**
	 * iterate subscriptions with call method
	 * @param args
	 * @param topic
	 */
	Topic.prototype.publish = function (args, topic) {
		var i, len;
		for (i = 0, len = this.subscriptions.length; i < len; i += 1) {
			this.subscriptions[i].call(args, topic);
		}
	};
	/**
	 * register new subscription
	 * @param callback
	 * @param receiver
	 * @returns {Subscription}
	 */
	Topic.prototype.subscribe = function (callback, receiver) {
		var subscription = new Subscription(callback, receiver);
		this.subscriptions.push(subscription);
		return subscription;
	};
	/**
	 * remove a subscription
	 * @param subscription
	 * @returns {Subscription|undefined}
	 */
	Topic.prototype.unsubscribe = function (subscription) {
		var i, len, removed = [];
		for (i = 0, len = this.subscriptions.length; i < len; i += 1) {
			if (this.subscriptions[i] === subscription) {
				removed = this.subscriptions.splice(i, 1);
			}
		}
		return removed[0];
	};
	/**
	 * class to wrap topics
	 * @returns {Mediator}
	 * @constructor
	 */
	function Mediator() {
		if (!(this instanceof Mediator)) {
			return new Mediator();
		}
		this.topics = {};
	}
	/**
	 * retrieve or create a topic
	 * @param topic
	 * @returns {Topic}
	 */
	Mediator.prototype.getTopic = function (topic) {
		this.topics[topic] = this.topics[topic] || new Topic();
		return this.topics[topic];
	};
	/**
	 * adds subscription to topic
	 * @param topic
	 * @param callback
	 * @param receiver
	 * @returns {Subscription}
	 */
	Mediator.prototype.subscribe = function (topic, callback, receiver) {
		return this.getTopic(topic).subscribe(callback, receiver);
	};
	/**
	 * removes a subscription from a topic
	 * @param topic
	 * @param subscription
	 * @returns {Subscription|undefined}
	 */
	Mediator.prototype.unsubscribe = function (topic, subscription) {
		return this.getTopic(topic).unsubscribe(subscription);
	};
	/**
	 * curry a function to publish the topic
	 * @param topic
	 * @returns {Function}
	 */
	Mediator.prototype.publish = function (topic) {
		var self = this.getTopic(topic);
		return function () {
			self.publish(arguments, topic);
		};
	};
	/**
	 * add publish and subscribe methods to a namespace
	 * @param obj
	 * @returns {*}
	 */
	Mediator.prototype.installTo = function (obj) {
		var self = this;
		obj.publish = function () {
			return self.publish.apply(self, arguments);
		};
		obj.subscribe = function () {
			return self.subscribe.apply(self, arguments);
		};
		return obj;
	};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].Mediator = Mediator;

}(this, "KP"));

(function (global, namespace ) {
	"use strict";

	if (!global[namespace]) {
		global[namespace] = {};
	}

	var CONSTANTS = global[namespace].constants;
	var templates = {

		errorMessageTemplate:
			'<div class="alert-box error" role="alert">' +
				'<p>{{#errorMessages}}{{.}}<br/>{{/errorMessages}}</p>' +
			'</div>',

		fprerrorMessageTemplate:
				'<div class="alert-box error" role="alert">' +
					'<p>{{#errorMessages}}{{.}}{{/errorMessages}}You can view more information <a class="fprer" href="https://www.fleetfarm.com/static/faq-contact-us/faq-my-account/#reset-password-security">here</a>.</p>' +
				'</div>',
			
		alertMessageTemplate:
			'<div class="alert-box info" role="alert">' +
				'<p>{{#alertMessages}}{{.}}<br/>{{/alertMessages}}</p>' +
			'</div>',

		errorPromoMessageTemplate:
			'<div class="alert promo-code-msg" role="alert">' +
				'<p>' +
					'<span class="icon icon-error"></span>' +
					'{{#errorMessages}}{{.}}<br/>{{/errorMessages}}' +
				'</p>' +
			'</div>',

		avsTemplate:
			'{{#noMatch}}' +
				'<li>' +
					'<div class="card">' +
						'<div class="card-title">No Match</div>' +
						'<div class="card-content">' +
							'<p>Sorry, we couldn\'t find a match for the address you entered.</p>' +
						'</div>' +
						'<div class="card-links">' +
							'<a href="#" data-dismiss="modal" class="button primary">Edit Address</a>' +
						'</div>' +
					'</div>' +
				'</li>' +
			'{{/noMatch}}' +
			'{{#suggestedAddress}}' +
				'<li>' +
					'<div class="card">' +
						'<div class="card-title">Suggested Address</div>' +
						'<div class="card-content">' +
							'<p>{{address1}}</p>' +
							'{{#address2}}<p>{{.}}</p>{{/address2}}' +
							'<p>{{city}}, {{state}} {{postalCode}}</p>' +
						'</div>' +
						'<div class="card-links">' +
							'<a href="#" class="button primary use-suggested">Use Suggested</a>' +
						'</div>' +
					'</div>' +
				'</li>' +
			'{{/suggestedAddress}}' +
			'{{#enteredAddress}}' +
				'<li>' +
					'<div class="card">' +
						'<div class="card-title">You Entered</div>' +
						'<div class="card-content">' +
							'<p>{{address1}}</p>' +
							'{{#address2}}<p>{{.}}</p>{{/address2}}' +
							'<p>{{{city}}}, {{state}} {{postalCode}}</p>' +
						'</div>' +
						'<div class="card-links">' +
							'<a href="#" class="button secondary use-entered">Use As Entered</a>' +
						'</div>' +
					'</div>' +
				'</li>' +
			'{{/enteredAddress}}',

		bopisNotificationTemplate:
			'{{#current}}' +
				'<div class="bopis-selected-store">' +
					'<div class="address" style="width:100%">' +
						'<h3 class="current-location">Current Pick Up Location</h3>' +
						'<h3>{{city}}, {{stateAddress}}</h3>' +
						'<p>{{address1}}</p>' +
						'<p>{{address2}}</p>' +
						'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
						'<p>{{phoneNumber}}</p>' +
						'<p><strong>Distance:</strong> {{distance}}</p>' +
					'</div>' +
				'</div>' +
			'{{/current}}' +
			'<div class="bopis-item-notifications">' +
				'<ul class="bopis-results-list">' +
					'{{#available}}' +
						'{{#stores}}' +
							'{{#eligible}}' +
								'<li>' +
									'<div class="address">' +
										'<h3>{{city}}, {{stateAddress}}</h3>' +
										'<p>{{address1}}</p>' +
										'<p>{{address2}}</p>' +
										'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
										'<p>{{phoneNumber}}</p>' +
										'<p><strong>Distance:</strong> {{distance}}</p>' +
									'</div>' +
									'<div class="actions">' +
										'<button class="button primary expand choose-this-store" data-bopis-store-id="{{locationId}}">Choose This Store</button>' +
									'</div>' +
								'</li>' +
							'{{/eligible}}' +
						'{{/stores}}' +
					'{{/available}}' +
					'{{^available}}' +
						'<li>' +
							'Sorry, this item isn\'t available at any stores within <strong>{{searchRadius}} miles</strong> of the zip code provided.' +
							'<br/><br/>' +
							'<a href="' + CONSTANTS.contextPath + '/sitewide/storeLocator.jsp">See Store Locations</a>' +
						'</li>' +
					'{{/available}}' +
				'</ul>' +
				'<a href="#" data-dismiss="modal" class="button secondary cancel-button expand">Cancel</a>' +
				'<a href="#" class="ship-my-order centered">Ship My Order Instead</a>' +
			'</div>',

		bopisLocationInfoTemplate:
			'<div class="card">' +
				'<div class="card-title">Store Pick Up Information</div>' +
				'<div class="card-content">' +
					'<h3>{{city}}, {{#state}}{{state}}{{/state}}{{#stateAddress}}{{stateAddress}}{{/stateAddress}}</h3>' +
					'<p>{{#address}}{{address}}{{/address}}{{#address1}}{{address1}}{{/address1}}</p>' +
					'<p>{{city}}, {{#zip}}{{zip}}{{/zip}}{{#postalCode}}{{postalCode}}{{/postalCode}}</p>' +
					'<p>{{#phone}}{{phone}}{{/phone}}{{#phoneNumber}}{{phoneNumber}}{{/phoneNumber}}</p>' +
				'</div>' +
				'<div class="card-links">' +
					'<a href="#" class="change-store">Change Store</a>' +
					'{{#bopisOnly}}' +
						'<a href="#" class="ship-my-order">Ship My Order Instead</a>' +
					'{{/bopisOnly}}' +
				'</div>' +
			'</div>',

		bopisStoreLocationInfoTemplate:
			'<div class="bopis-section {{isActiveTeaser}}">' +
				'<div class="bopis-store-content ">' +
					'{{#eligible}}' +
						'<span class="bopis-store-available">' +
							'Available' +
							'<span class="icon icon-available"></span>' +
						'</span>' +
					'{{/eligible}}' +
					'{{^eligible}}' +
						'<span class="bopis-store-unavailable">' +
							'Not Available' +
							'<span class="icon icon-unavailable"></span>' +
						'</span>' +
					'{{/eligible}}' +
					' <span>at</span> ' +
					'<a class="bopis-store-info update-bopis-store underlined-link" href="#">' +
						'{{city}}, ' +
						'{{#state}}{{state}}{{/state}}' +
						'{{#stateAddress}}{{stateAddress}}{{/stateAddress}}' +
					'</a>' +
					'<div class="card-links">' +
						'<a href="#" class="change-store">Change Store</a>' +
						'{{#displayShipMyOrderLink}}' +
							'<span class="seperator">&nbsp;&nbsp;|&nbsp;&nbsp;</span>' +
							'<a href="#" class="ship-my-order">Ship My Order Instead</a>' +
						'{{/displayShipMyOrderLink}}' +
						'{{^displayShipMyOrderLink}}' +
							'{{#bopisOnly}}' +
								'<span class="seperator">&nbsp;&nbsp;|&nbsp;&nbsp;</span>' +
								'<a href="#" class="ship-my-order">Ship My Order Instead</a>' +
							'{{/bopisOnly}}' +
						'{{/displayShipMyOrderLink}}' +
					'</div>' +
				'</div>' +
			'</div>',

		bopisSearchTemplate:
			'<p class="results-zip-code">' +
				'Showing results for: <strong>{{zip}}</strong> ' +
				'within <strong>{{searchRadius}} miles</strong>' +
			'</p>' +
			'{{^available}}' +
				'<div class="home-results-list scrollbar callout">' +
					'<ul class="bopis-results-list">' +
						'<li>' +
							'Sorry, this item isn\'t available at any stores within ' +
							'<strong>{{searchRadius}} miles</strong> ' +
							'of the zip code provided.<br/><br/>' +
							'<a href="' + CONSTANTS.contextPath + '/sitewide/storeLocator.jsp">See Store Locations</a>' +
						'</li>' +
					'</ul>' +
				'</div>' +
			'{{/available}}' +
			'{{#available}}' +
				'<div class="home-results-list scrollbar" id="style-1">' +
					'<ul class="bopis-results-list">' +
						'{{#stores}}' +
							'<li>' +
								'<div class="address">' +
									'<h3>{{city}}, {{stateAddress}}</h3>' +
									'<p>{{address1}}</p>' +
									'<p>{{address2}}</p>' +
									'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
									'<p>{{phoneNumber}}</p>' +
									'<p><strong>Distance:</strong> {{distance}}</p>' +
								'</div>' +
								'<div class="actions">' +
									'{{#eligible}}' +
										'<button class="button primary expand choose-this-store" data-bopis-store-id="{{locationId}}">Choose This Store</button>' +
									'{{/eligible}}' +
									'{{^eligible}}' +
										'<button class="button primary disabled expand">Not Available</button>' +
									'{{/eligible}}' +
								'</div>' +
							'</li>' +
						'{{/stores}}' +
					'</ul>' +
				'</div>' +
			'{{/available}}',

		storeSearchTemplate:
			'<p class="results-zip-code">' +
				'Showing results for: <strong>{{zip}}</strong> ' +
				'within <strong>{{searchRadius}} miles</strong>' +
			'</p>' +
			'<div class="home-results-list scrollbar" id="style-1">' +
				'<ul class="bopis-results-list">' +
					'{{#stores}}' +
						'<li>' +
							'<div class="address">' +
								'<h3>{{city}}, {{stateAddress}}</h3>' +
								'<p>{{address1}}</p>' +
								'<p>{{address2}}</p>' +
								'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
								'<p>{{phoneNumber}}</p>' +
								'<p><strong>Distance:</strong> {{distance}}</p>' +
							'</div>' +
							'<div class="actions">' +
								'{{^eligible}}' +
									'<button class="button primary expand choose-store" data-store-id="{{locationId}}">Make This My Store</button>' +
								'{{/eligible}}' +
								'{{#eligible}}' +
									'<button class="button primary disabled expand">Not Available</button>' +
								'{{/eligible}}' +
							'</div>' +
						'</li>' +
					'{{/stores}}' +
					'{{^stores}}' +
						'<li>' +
							'Sorry, this item isn\'t available at any stores within <strong>{{searchRadius}} miles</strong> of the zip code provided.' +
							'<br/><br/>' +
							'<a href="' + CONSTANTS.contextPath + '/sitewide/storeLocator.jsp">See Store Locations</a>' +
						'</li>' +
					'{{/stores}}' +
				'</ul>' +
			'</div>',

		bopisStoreSearchTemplate:
			'<p class="results-zip-code">' +
				'Showing results for: <strong>{{zip}}</strong> ' +
				'within <strong>{{searchRadius}} miles</strong>' +
			'</p>' +
			'{{^available}}' +
				'<div class="home-results-list scrollbar callout">' +
					'<ul class="bopis-results-list">' +
						'<li>' +
							'Sorry, this item isn\'t available at any stores within ' +
							'<strong>{{searchRadius}} miles</strong> of the zip code provided.' +
							'<br/><br/>' +
							'<a href="' + CONSTANTS.contextPath + '/sitewide/storeLocator.jsp">See Store Locations</a>' +
						'</li>' +
					'</ul>' +
				'</div>' +
			'{{/available}}' +
			'{{#available}}' +
				'<div class="home-results-list scrollbar" id="style-1">' +
					'<ul class="bopis-results-list">' +
						'{{#stores}}' +
							'<li>' +
								'<div class="address">' +
									'<h3>{{city}}, {{stateAddress}}</h3>' +
									'<p>{{address1}}</p>' +
									'<p>{{address2}}</p>' +
									'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
									'<p>{{phoneNumber}}</p>' +
									'<p><strong>Distance:</strong> {{distance}}</p>' +
								'</div>' +
								'<div class="actions">' +
									'{{#eligible}}' +
										'<button class="button primary expand change-this-store" data-change-bopis-store-id="{{locationId}}">Choose This Store</button>' +
									'{{/eligible}}' +
									'{{^eligible}}' +
										'<button class="button primary disabled expand">Not Available</button>' +
									'{{/eligible}}' +
								'</div>' +
							'</li>' +
						'{{/stores}}' +
					'</ul>' +
				'</div>' +
			'{{/available}}',

		storeHeaderTemplate:
			'<a href="#" class="home-store-toggle">' +
				'<span class="icon icon-locator" aria-hidden="true"></span> ' +
				'{{city}}, {{stateAddress}}' +
			'</a>',

		storeBodyTemplate:
			'<div class="card-title">' +
				'<a href="#">' +
					'<span class="icon icon-locator" aria-hidden="true"></span> ' +
					'My Store' +
				'</a>' +
			'</div>' +
			'<div class="card-content">' +
				'<p class="title">{{city}}, {{stateAddress}}</p>' +
				'<a href="{{website}}" alt="View Store Details" class="view-store">View Store Details</a>' +
			'</div>' +
			'<div class="card-content">' +
				'<p>{{address1}}</p>' +
				'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
				'<p>{{phoneNumber}}</p>' +
				'<a href="{{website}}" alt="View Store Details" class="hide-store">View Store Details</a>' +
			'</div>',

		accountStoreTemplate:
			'<div class="card" style="height:257px;">' +
				'<div class="card-title">My Store</div>' +
				'<div class="card-content home-store">' +
					'<p class="title">{{city}}, {{stateAddress}}</p>' +
					'<p>{{address1}}</p>' +
					'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
					'<p>{{phoneNumber}}</p>' +
					'<a href="{{website}}">View Store Details</a>' +
				'</div>' +
				'<div class="card-links equalized">' +
					'<a href="#" class="button primary outline update-store">Change my store</a>' +
				'</div>' +
			'</div>',

		templatePickerTypeSwatch :
			'<div class="product-selectors group product-selectors-{{type}} {{cssclass}}">'+
				'<div class="product-options" data-typeid="{{typeId}}">'+
					'<label for="product-{{type}}">{{title}}</label>'+
					'<div class="product-option-errors"></div>'+
					'<ul class="option-swatches" id="product-{{type}}">'+
						'{{#availableOptions}}'+
						'<li>'+
							'<a class="option-link swatch {{#isSelected}}active{{/isSelected}}" data-type="{{type}}" data-typeid="{{typeId}}" data-value="{{optionValue}}" data-id="{{optionId}}">'+
								'<img src="{{imageSrc}}" alt="{{optionValue}}" title="{{optionValue}}"/>'+
								'<img src="/images/swatch/unavailable/x-white.png" class="x-overlay"/>'+
							'</a>'+
						'</li>'+
						'{{/availableOptions}}'+
					'</ul>'+
				'</div>'+
			'</div>',

		templatePickerTypeDropdown:
			'<div class="product-selectors group product-selectors-{{type}} {{cssclass}}">' +
				'<div class="product-options" data-typeid="{{typeId}}">' +
					'<label for="product-{{type}}">{{title}}</label>' +
					'<div class="product-option-errors"></div>' +
					'<select class="option-dropdown" id="product-{{type}}">' +
						'{{#availableOptions}}' +
							'<option data-type="{{type}}" data-typeid="{{typeId}}" data-value="{{optionValue}}" data-id="{{optionId}}" class="{{#isSelected}}active{{/isSelected}}" {{#isSelected}}selected{{/isSelected}}>{{optionValue}}</option>' +
						'{{/availableOptions}}' +
					'</select>' +
					'{{#mediaUrl}}' +
						'<a href="${contextPath}/browse/ajax/sizeChartModal.jsp?url={{mediaUrl}}" class="modal-trigger" data-target="size-chart-modal" data-size="small">Size Chart</a>' +
					'{{/mediaUrl}}' +
				'</div>' +
			'</div>',

		templateRegularPrice:
			'<div class="regular-price">' +
				'<span itemprop="price" content="{{regularPrice}}">${{regularPrice}}</span>' +
			'</div>',

		templateSalePrice:
			'<div class="original-price">' +
				'<span itemprop="price" content="{{originalPrice}}">${{originalPrice}}</span>' +
			'</div>' +
			'<div class="sale-price">' +
				'<span itemprop="price" content="{{salePrice}}">${{salePrice}} SALE</span>' +
			'</div>',

		templateClearancePrice:
			'<div class="original-price">' +
				'<span itemprop="price" content="{{originalPrice}}">${{originalPrice}}</span>' +
			'</div>' +
			'<div class="sale-price">' +
				'<span itemprop="price" content="{{salePrice}}">${{salePrice}} CLEARANCE</span>' +
			'</div>' +
			'<div class="discontinued-policy-label">' +
				'<span>Clearance Item Policy</span> ' +
				'<a class="view-details modal-trigger" href="/browse/ajax/discontinuedItemPolicyModal.jsp" data-target="discontinued-item-policy-modal" data-size="small">' +
					'<span class="icon icon-info"></span>' +
				'</a>' +
			'</div>',

		templateHidePrice:
			'<div class="discontinued-map-pricing-label">' +
				'<span>Add to Cart for price</span> ' +
				'<a class="view-details modal-trigger" href="/browse/ajax/addToCartToSeePriceModal.jsp" data-target="discontinued-item-policy-modal" data-size="small">details</a>' +
			'</div>',

		typeaheadSuggestionsTemplate:
			'<h4>{{searchTerm}}</h4>' +
			'<ul>' +
				'{{#results}}' +
					'<li>' +
						'<a href="{{{url}}}" data-detail-url="{{{detailUrl}}}">' +
							'{{{term}}}' +
						'</a>' +
					'</li>' +
				'{{/results}}' +
			'</ul>',

		typeaheadDetailsTopTemplate:
			'<div class="typeahead-details-top">' +
				'<ul class="typeahead-details-top-grid">' +
					'{{#links}}' +
						'<li>' +
							'<a href="{{url}}">' +
								'<div class="product-tile">' +
									'<div class="product-image">' +
										'<img src="{{{image}}}" alt="{{title}}" />' +
									'</div>' +
									'<div class="product-tile-details">' +
										'<div class="product-tile-text">' +
											'<div class="product-name">{{title}}</div>' +
											'<div class="product-brand">{{brand}}</div>' +
										'</div>' +
									'</div>' +
								'</div>' +
							'</a>' +
						'</li>' +
					'{{/links}}' +
				'</ul>' +
			'</div>',

		typeaheadDetailsBottomTemplate:
			'<div class="typeahead-details-bottom">' +
				'<ul class="typeahead-details-bottom-grid">' +
					'{{#.}}' +
						'<li>' +
							'<h4>{{title}}</h4>' +
							'<ul>' +
								'{{#links}}' +
									'<li>' +
										'<a href="{{{url}}}">{{title}}</a>' +
									'</li>' +
								'{{/links}}' +
							'</ul>' +
						'</li>' +
					'{{/.}}' +
				'</ul>' +
			'</div>',

		// NOTE: cart page needs the JSON property isCart to be present
		// so that "*" and "Estimated" are added in the proper places
		orderTotals :
			'<div class="total-row subtotal">' +
				'<div class="total-label">Merchandise Total :</div>' +
				'<div class="total-amount">{{orderSubtotal}}</div>' +
			'</div>' +
			'{{#orderDiscount.length}}' +
				'<div class="total-row subtotal">' +
					'<div class="total-label">' +
						'Discounts ' +
						'<a class="view-details modal-trigger" href="' + CONSTANTS.contextPath + '/checkout/ajax/promoDetailsModal.jsp?p={{promoDispName}}&d={{couponDetails}}" data-target="promo-details-modal" data-size="small">' +
							'<span class="icon icon-info"></span>' +
						'</a> :' +
					'</div>' +
					'<div class="total-amount savings">- {{totalSavings}}</div>' +
				'</div>' +
			'{{/orderDiscount.length}}' +
			'{{#orderDiscount}}' +
				'<div class="total-row">' +
					'<div class="total-label">' +
						'<span class="total-promo">{{promoDispName}}</span>' +
					'</div>' +
				'</div>' +
			'{{/orderDiscount}}' +
			'<div class="total-row shipping">' +
				'<div class="total-label">' +
					'{{#isCart}}Estimated {{/isCart}}Shipping{{#isCart}}&#42;{{/isCart}} :' +
					'{{#orderShippingPromos}}' +
						'<div>' +
							'<span class="total-promo">{{{shipPromoName}}}</span>' +
						'</div>' +
					'{{/orderShippingPromos}}' +
				'</div>' +
				'<div class="total-amount {{#isFreeShipping}}savings {{/isFreeShipping}}">' +
					'{{orderShipping}}' +
				'</div>' +
			'</div>'+
			'<div class="total-row tax">' +
				'<div class="total-label">Tax{{#isCart}}&#42;{{/isCart}} :</div>' +
				'<div class="total-amount">{{orderTax}}</div>' +
			'</div>' +
			'{{^isCart}}' +
				'{{#giftCardTotal}}' +
					'<div class="total-row tax">' +
						'<div class="total-label">{{gcHeaderText}} :</div>' +
						'<div class="total-amount">- {{.}}</div>' +
					'</div>' +
				'{{/giftCardTotal}}' +
			'{{/isCart}}' +
			'<div class="total-row total">' +
				'<div class="total-label">{{#isCart}}Estimated {{/isCart}}Total :</div>' +
				'<div class="total-amount">{{orderTotal}}</div>' +
			'</div>',

		appliedCoupons:
			'{{#appliedCouponPromos}}' +
				'<div class="promo-applied">' +
					'<span class="coupon-code">' +
						'{{couponCode}}' +
					'</span>' +
					'<a class="view-details modal-trigger" href="' + CONSTANTS.contextPath + '/checkout/ajax/lineItemPromoDetailsModal.jsp?p={{couponPromoShortDesc}}&d={{couponDetails}}" data-target="promo-details-modal" data-size="small">View Details</a>' +
					'<a href="#" class="remove-link">' +
						'<span class="icon icon-remove"></span>' +
						'Remove' +
					'</a>' +
				'</div>' +
			'{{/appliedCouponPromos}}',

		lineItemPromotions:
			'{{#shortDescription}}' +
				'<span class="icon icon-check"></span>' +
				'<span class="promo-line-item-desc">{{shortDescription}}</span> ' +
				'discount applied ' +
				'<a class="view-details modal-trigger" href="' + CONSTANTS.contextPath + '/checkout/ajax/lineItemPromoDetailsModal.jsp?p={{displayName}}&d={{shortDescription}}" data-target="promo-details-modal" data-size="small">' +
					'<span class="icon icon-info"></span>' +
				'</a>' +
			'{{/shortDescription}}' +
			'{{#qualName}}' +
				'<a class="view-details modal-trigger upsells-item-msg" href="/browse/ajax/upsellMessageModal.jsp?qualName={{qualName}}&qualInstructions={{upsellInstructions}}" data-target="promo-details-modal" data-size="small">' +
					'<span class="icon icon-upsells"></span>' +
					'<span class="promo-line-item-desc orange">{{qualName}}</span>' +
					'<span class="icon icon-info"></span>' +
				'</a>' +
			'{{/qualName}}',

		appliedGiftCards:
			'<h3>{{gcAppliedHeaderText}}</h3>' +
			'<div class="applied-gift-cards">' +
				'{{#appliedGiftCards}}' +
					'<div class="applied-gift-card">' +
						'<div class="gift-card-number-applied">{{number}}</div>' +
						'<a href="#" class="gift-card-remove" data-number="{{number}}">remove</a>' +
						'<div class="gift-card-amount-applied amount-{{number}}">{{amount}}</div>' +
					'</div>' +
				'{{/appliedGiftCards}}' +
			'</div>',

		appliedWishList:
			'<a href="#" class="add-to-wish-list underlined-link">Add to Wish List</a>',

	};

	global[namespace].templates = templates;

}(this, 'KP'));

/* Degrade console gracefully */
if (window.console === undefined) {
	window.console = {};
	var logHistory = [];
	window.console.log = function () {
		logHistory.push(arguments);
	};
}
if (window.console === undefined || console.debug === undefined) {
	console.debug = console.log;
}

/* Patch missing Date.now */
if (!Date.now) {
	Date.now = function() { return new Date().getTime(); };
}

(function (global, $, namespace ) {
	"use strict";

	var CONSTANTS = global[namespace].constants,
		utilities = {
		form : {
			validate : function ($form) {
				var isValid;
				$form.validate('validateForm');
				isValid = $form.data('validate').isValid;
				return isValid;
			},
			hideErrors : function ($form) {
				$form.find('.alert-box').remove();
				$form.validate('clearFormErrors');
			},
			showErrors : function ($form, errorResponse, $modal, errorTemplate) {
				/* show field errors  */
				if (errorResponse.fieldErrorMessages !== undefined) {
					$form.validate('showFormErrors', errorResponse.fieldErrorMessages);
				}

				/* show form-level errors  */
				if (errorResponse.errorMessages.length > 0) {
					this.showFormErrors($form, errorResponse, errorTemplate);
				}

				if ($modal !== undefined) {
					$modal.modal('show');
				}
			},
			showFormErrors : function($form, errorMessages, errorTemplate){
				var hasfprer=false;
				var errorsLen=0;
				var fprerlink="";
				if(errorMessages.errorMessages.length > 0) {
					errorsLen = errorMessages.errorMessages.length;
					for (var e = 0; e < errorsLen; e++) {
						var errMsg=errorMessages.errorMessages[e];
						var checkString="FPRER";
						if(errMsg.indexOf(checkString)!=-1){
							hasfprer=true;
							var errActualMsgStrs=errMsg.split(";");
							errMsg=errActualMsgStrs[2];
							fprerlink=errActualMsgStrs[1];
							errorMessages.errorMessages[e]=errMsg;
						}
						else {
							var decoded = global[namespace].utilities.decodeHTMLEntities(errorMessages.errorMessages[e]);
							errorMessages.errorMessages[e]= decoded ;
						}
					}
				}
				var template = errorTemplate || global[namespace].templates.errorMessageTemplate;
				if(errorsLen==1){
					if(hasfprer){
						template =global[namespace].templates.fprerrorMessageTemplate;
					}
				}
				var content = Mustache.render(template, errorMessages);
				if(errorTemplate){
					$form.find('.alert').remove().end().append(content);
				}
				else{
					$form.find('.alert-box').remove().end().prepend(content);
					if(hasfprer){
						console.log("change href value to:"+ fprerlink);
						$form.find('.alert-box').find('.fprer').attr("href",fprerlink);
					}
				}
				$form.validate('scrollToError', '.alert-box');
			},
			showInlineErrors : function (errorObj) {
				var $form = $('#' + errorObj.formId);
				if ($form.length > 0) {
					$form.validate('showFormErrors', errorObj.fieldsWithErrors);
				}
			},
			showSuccessMessage : function($form, response, container){
				var successContainer = container || '.js-success-container',
						$successContent =  $(response.successContent),
						$wrappedResponse = $successContent.filter(successContainer).length > 0 ?  $successContent : $successContent.find(successContainer);
				if ($wrappedResponse.length > 0) {
					$form.closest(successContainer).html($wrappedResponse.html());
				} else {
					$form.closest(successContainer).html($successContent);
				}
			},
			toggleFormDisable : function toggleFormDisable ($container, isDisabled) {
				$container.find('input, select, textarea').prop('disabled', isDisabled);
			},
			toggleValidation :  function toggleValidation ($container, isEnabled) {
				$container.find('[data-validation]')[isEnabled ? 'removeClass' : 'addClass']('disabled');
			},
			updateFormUrls : function (updateUrlArray, context) {
				var x = 0,
						arrayLen = updateUrlArray.length;
				for (x; x < arrayLen; x++) {
					if (context) {
						$(updateUrlArray[x][0], context).val(updateUrlArray[x][1]);
					} else {
						$(updateUrlArray[x][0]).val(updateUrlArray[x][1]);
					}

				}
			},
			loadProxyIframe : function (e, proxyFormId, showLoader, errorTemplate) {
				if (showLoader) {
					namespace.utilities.showLoader();
				}

				var $form = $(e.target),
						$loginErrors = $form.find('.form-errors');

				//clear any existing errors and validate
				$loginErrors.empty();
				$form.validate('validateForm');

				//listeners for form response
				pm.unbind("formError");
				pm.bind("formError", function (data) {
					namespace.utilities.form.showErrors($form, data.response, undefined, errorTemplate);
					namespace.utilities.hideLoader();
				});

				pm.unbind("ajaxError");
				pm.bind("ajaxError", function (data) {
					namespace.utilities.form.ajaxError(data.xhr);
				});

				pm.unbind("proxyIsReady");
				pm.bind("proxyIsReady", function (data) {
					namespace.modalProxy.loadingProxy.resolve();
				});

				//if this is valid, send message when loading proxy is ready
				if ($form.data('validate').isValid) {
					namespace.modalProxy.loadingProxy.done(function(){
						namespace.proxy._handleProxySubmit(e, proxyFormId);
					});
				}
				else {
					namespace.utilities.hideLoader();
				}
			},
			ajaxError : function (xhr, statusText, exception, $form) {
				// if (xhr.status == '404') {
				// 	window.location.href = global[namespace].constants.contextPath + "/error_404.jsp";
				// } else if (xhr.status == '500') {
				// 	window.location.href = global[namespace].constants.contextPath + "/error_500.jsp";
				// } else {
				var errors = [];
				errors.push(global[namespace].constants.ajaxError);
				errors.push(exception);
				this.showFormErrors($form, {'errorMessages': errors});
				// }
			}
		},
		sessionTimeoutHandle : null, /* returned from setTimeout, can be used to cancel execution */
		startSessionTimeout : function () {
			if (this.sessionTimeoutHandle) {
				clearTimeout(this.sessionTimeoutHandle);
			}
			this.sessionTimeoutHandle = setTimeout(this.redirectSessionTimeout, CONSTANTS.sessionTimeoutMillis);
		},
		redirectSessionTimeout : function () {
			global[namespace].profileController.resetProfileStatus();
			window.location = CONSTANTS.contextPath + "/";
		},
		showLoader : function(message){
			global[namespace].loader.showLoader(message);
		},
		hideLoader : function(){
			global[namespace].loader.hideLoader();
		},
		imgError : function(image) {
			console.log(['loading error for', image]);
			/* use this to replace the image with not found image when there is an error, if not using an image management
			 service that provides this. */
		},
		getSameProtocolSiteRoot : function () {
			var siteRoot = CONSTANTS.siteRoot;
			if (window.location.protocol == "https:") {
				siteRoot = CONSTANTS.secureSiteRoot;
			}
			return siteRoot;
		},
		removeClass: function (el, className) {
			if (el.classList) {
				el.classList.remove(className);
			} else {
				el.className = el.className.replace(new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
			}
		},
		addClass: function (el, className){
			if (el.classList) {
				el.classList.add(className);
			} else {
				el.className += ' ' + className;
			}
		},
		createModal: function(id, modalSize) {
			var modalTemplate = '<div class="modal" id="' + id + '"><div id="overlay" class="modal-backdrop fade" data-dismiss="modal" /><div class="modal-window fade resize"><div class="modal-content fade in"></div><div class="modal-close" data-dismiss="modal"><span class="icon icon-close" aria-hidden="true"></span><span class="sr-only">close</span></div></div></div></div>';

			if (typeof modalSize !== 'undefined') {
				modalTemplate = '<div class="modal" id="' + id + '"><div id="overlay" class="modal-backdrop fade" data-dismiss="modal" /><div class="modal-window fade resize ' + modalSize + '"><div class="modal-content fade in"></div><div class="modal-close" data-dismiss="modal"><span class="icon icon-close" aria-hidden="true"></span><span class="sr-only">close</span></div></div></div></div>';
			}
			return $(modalTemplate).appendTo('body');
		},
		addURLParameter : function(url, param, value) {
			var val = new RegExp('(\\?|\\&)' + param + '=.*?(?=(&|$))'),
					qstring = /\?.+$/,
					delimiter = (qstring.test(url)) ? '&' : '?';
			if (val.test(url)) {
				//if the parameter exists, replace the value
				return url.replace(val, '$1' + param + '=' + value);
			} else {
				//otherwise append the parameter to the url
				return url + delimiter + param + '=' + value;
			}
		},
		getURLParameter: function(url, param) {
			if (!url) {
				return null;
			}
			var value,
					query = url.toString(),
					querySplit = url.split('?'),
					hashSplit, queryParams;

			/* get just string past '?' */
			if (querySplit.length >= 2) {
				query = querySplit[1];
			}

			/* eliminate any hash values */
			hashSplit = query.split('#');
			if (hashSplit.length > 1) {
				query = hashSplit[0];
			}

			queryParams = query.split('&');
			for (var x = 0; x < queryParams.length; x++) {
				if (queryParams[x].indexOf( param + '=' ) === 0) {
					value = queryParams[x].split('=')[1];
				}
			}
			if (value === undefined) {
				value = '';
			}
			return value;
		},
		stripURLParameters : function (url, allowedParams) {
			var result = url.split('?')[0],
					x = 0, len = allowedParams.length;

			for (x; x < len; x++) {
				var param = global[namespace].utilities.getURLParameter(url, allowedParams[x]);
				if (param !== '') {
					result = global[namespace].utilities.addURLParameter(result, allowedParams[x], param);
				}
			}
			return result;
		},
		getStoreIdURLParam: function() {
			// ex: /detail/product-name/product-id?gStoreId={PARAM}
			var gStoreId = global[namespace].utilities.getURLParameter(window.location.href, 'gStoreId');
			if (gStoreId !== '') {
				return gStoreId;
			}
			// ex: /store/detail/product-name/product-id/{PARAM}
			// 1.) get pathname (minus first slash) and split it into an array
			// 2.) expected results: [0] "store" [1] "detail", [2] product-name, [3] product-id, [4] gstoreid
			var urlParts = window.location.pathname.substr(1).split('/');
			if (urlParts[0] === 'store' && urlParts[1] === 'detail') {
				if (urlParts[4] && !isNaN(urlParts[4])) {
					gStoreId = urlParts[4];
				}
			}
			return gStoreId;
		},
		dedup: function(array) {
			var newArray = [],
					seen = {},
					i;

			for (i = 0; i < array.length; i++ ) {
				if ( seen[ array[i] ] ){
					continue;
				}
				newArray.push( array[i] );
				seen[ array[i] ] = 1;
			}
			return newArray;
		},
		hyphenateZip: function(zip) {
			var hyphenatedZip = zip;
			if (zip) {
				if (zip.length == 9) {
					hyphenatedZip = zip.substr(0, 5) + '-' + zip.substr(5);
				}
				else if (zip.length == 10 && zip.indexOf(' ') > 0) {
					hyphenatedZip = zip.substr(0, 5) + '-' + zip.substr(6);
				}
			}
			return hyphenatedZip;
		},
		decodeHTMLEntities: function(string) {
			// this prevents any overhead from creating the object each time
			var element = document.createElement('div');
			if (string && typeof string === 'string') {
				// strip script/html tags
				string = string.replace(/&amp;/gmi, '&');
				string = string.replace(/<script[^>]*>([\S\s]*?)<\/script>/gmi, '');
				string = string.replace(/<\/?\w(?:[^"'>]|"[^"]*"|'[^']*')*>/gmi, '');
				element.innerHTML = string;
				string = element.textContent;
				element.textContent = '';
			}
			return string;
		}
	};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].utilities = utilities;

}(this, window.jQuery, "KP"));

/*!
 * jQuery Cookie Plugin v1.4.1
 * https://github.com/carhartl/jquery-cookie
 *
 * Copyright 2006, 2014 Klaus Hartl
 * Released under the MIT license
 */
(function (factory) {
	if (typeof define === 'function' && define.amd) {
// AMD
		define(['jquery'], factory);
	} else if (typeof exports === 'object') {
// CommonJS
		factory(require('jquery'));
	} else {
// Browser globals
		factory(jQuery);
	}
}(function ($) {
	var pluses = /\+/g;
	function encode(s) {
		return config.raw ? s : encodeURIComponent(s);
	}
	function decode(s) {
		return config.raw ? s : decodeURIComponent(s);
	}
	function stringifyCookieValue(value) {
		return encode(config.json ? JSON.stringify(value) : String(value));
	}
	function parseCookieValue(s) {
		if (s.indexOf('"') === 0) {
// This is a quoted cookie as according to RFC2068, unescape...
			s = s.slice(1, -1).replace(/\\"/g, '"').replace(/\\\\/g, '\\');
		}
		try {
// Replace server-side written pluses with spaces.
// If we can't decode the cookie, ignore it, it's unusable.
// If we can't parse the cookie, ignore it, it's unusable.
			s = decodeURIComponent(s.replace(pluses, ' '));
			return config.json ? JSON.parse(s) : s;
		} catch(e) {}
	}
	function read(s, converter) {
		var value = config.raw ? s : parseCookieValue(s);
		return $.isFunction(converter) ? converter(value) : value;
	}
	var config = $.cookie = function (key, value, options) {
// Write
		if (arguments.length > 1 && !$.isFunction(value)) {
			options = $.extend({}, config.defaults, options);
			if (typeof options.expires === 'number') {
				var days = options.expires, t = options.expires = new Date();
				t.setTime(+t + days * 864e+5);
			}
			return (document.cookie = [
				encode(key), '=', stringifyCookieValue(value),
				options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
				options.path ? '; path=' + options.path : '',
				options.domain ? '; domain=' + options.domain : '',
				options.secure ? '; secure' : ''
			].join(''));
		}
// Read
		var result = key ? undefined : {};
// To prevent the for loop in the first place assign an empty array
// in case there are no cookies at all. Also prevents odd result when
// calling $.cookie().
		var cookies = document.cookie ? document.cookie.split('; ') : [];
		for (var i = 0, l = cookies.length; i < l; i++) {
			var parts = cookies[i].split('=');
			var name = decode(parts.shift());
			var cookie = parts.join('=');
			if (key && key === name) {
// If second argument (value) is a function it's a converter...
				result = read(cookie, value);
				break;
			}
// Prevent storing a cookie that we couldn't decode.
			if (!key && (cookie = read(cookie)) !== undefined) {
				result[name] = cookie;
			}
		}
		return result;
	};
	config.defaults = {};
	$.removeCookie = function (key, options) {
		if ($.cookie(key) === undefined) {
			return false;
		}
// Must not alter options, thus extending a fresh object...
		$.cookie(key, '', $.extend({}, options, { expires: -1 }));
		return !$.cookie(key);
	};
}));
/*!
 * jQuery Form Plugin
 * version: 3.51.0-2014.06.20
 * Requires jQuery v1.5 or later
 * Copyright (c) 2014 M. Alsup
 * Examples and documentation at: http://malsup.com/jquery/form/
 * Project repository: https://github.com/malsup/form
 * Dual licensed under the MIT and GPL licenses.
 * https://github.com/malsup/form#copyright-and-license
 */
!function(e){"use strict";"function"==typeof define&&define.amd?define(["jquery"],e):e("undefined"!=typeof jQuery?jQuery:window.Zepto)}(function(e){"use strict";function t(t){var r=t.data;t.isDefaultPrevented()||(t.preventDefault(),e(t.target).ajaxSubmit(r))}function r(t){var r=t.target,a=e(r);if(!a.is("[type=submit],[type=image]")){var n=a.closest("[type=submit]");if(0===n.length)return;r=n[0]}var i=this;if(i.clk=r,"image"==r.type)if(void 0!==t.offsetX)i.clk_x=t.offsetX,i.clk_y=t.offsetY;else if("function"==typeof e.fn.offset){var o=a.offset();i.clk_x=t.pageX-o.left,i.clk_y=t.pageY-o.top}else i.clk_x=t.pageX-r.offsetLeft,i.clk_y=t.pageY-r.offsetTop;setTimeout(function(){i.clk=i.clk_x=i.clk_y=null},100)}function a(){if(e.fn.ajaxSubmit.debug){var t="[jquery.form] "+Array.prototype.join.call(arguments,"");window.console&&window.console.log?window.console.log(t):window.opera&&window.opera.postError&&window.opera.postError(t)}}var n={};n.fileapi=void 0!==e("<input type='file'/>").get(0).files,n.formdata=void 0!==window.FormData;var i=!!e.fn.prop;e.fn.attr2=function(){if(!i)return this.attr.apply(this,arguments);var e=this.prop.apply(this,arguments);return e&&e.jquery||"string"==typeof e?e:this.attr.apply(this,arguments)},e.fn.ajaxSubmit=function(t){function r(r){var a,n,i=e.param(r,t.traditional).split("&"),o=i.length,s=[];for(a=0;o>a;a++)i[a]=i[a].replace(/\+/g," "),n=i[a].split("="),s.push([decodeURIComponent(n[0]),decodeURIComponent(n[1])]);return s}function o(a){for(var n=new FormData,i=0;i<a.length;i++)n.append(a[i].name,a[i].value);if(t.extraData){var o=r(t.extraData);for(i=0;i<o.length;i++)o[i]&&n.append(o[i][0],o[i][1])}t.data=null;var s=e.extend(!0,{},e.ajaxSettings,t,{contentType:!1,processData:!1,cache:!1,type:u||"POST"});t.uploadProgress&&(s.xhr=function(){var r=e.ajaxSettings.xhr();return r.upload&&r.upload.addEventListener("progress",function(e){var r=0,a=e.loaded||e.position,n=e.total;e.lengthComputable&&(r=Math.ceil(a/n*100)),t.uploadProgress(e,a,n,r)},!1),r}),s.data=null;var c=s.beforeSend;return s.beforeSend=function(e,r){r.data=t.formData?t.formData:n,c&&c.call(this,e,r)},e.ajax(s)}function s(r){function n(e){var t=null;try{e.contentWindow&&(t=e.contentWindow.document)}catch(r){a("cannot get iframe.contentWindow document: "+r)}if(t)return t;try{t=e.contentDocument?e.contentDocument:e.document}catch(r){a("cannot get iframe.contentDocument: "+r),t=e.document}return t}function o(){function t(){try{var e=n(g).readyState;a("state = "+e),e&&"uninitialized"==e.toLowerCase()&&setTimeout(t,50)}catch(r){a("Server abort: ",r," (",r.name,")"),s(k),j&&clearTimeout(j),j=void 0}}var r=f.attr2("target"),i=f.attr2("action"),o="multipart/form-data",c=f.attr("enctype")||f.attr("encoding")||o;w.setAttribute("target",p),(!u||/post/i.test(u))&&w.setAttribute("method","POST"),i!=m.url&&w.setAttribute("action",m.url),m.skipEncodingOverride||u&&!/post/i.test(u)||f.attr({encoding:"multipart/form-data",enctype:"multipart/form-data"}),m.timeout&&(j=setTimeout(function(){T=!0,s(D)},m.timeout));var l=[];try{if(m.extraData)for(var d in m.extraData)m.extraData.hasOwnProperty(d)&&l.push(e.isPlainObject(m.extraData[d])&&m.extraData[d].hasOwnProperty("name")&&m.extraData[d].hasOwnProperty("value")?e('<input type="hidden" name="'+m.extraData[d].name+'">').val(m.extraData[d].value).appendTo(w)[0]:e('<input type="hidden" name="'+d+'">').val(m.extraData[d]).appendTo(w)[0]);m.iframeTarget||v.appendTo("body"),g.attachEvent?g.attachEvent("onload",s):g.addEventListener("load",s,!1),setTimeout(t,15);try{w.submit()}catch(h){var x=document.createElement("form").submit;x.apply(w)}}finally{w.setAttribute("action",i),w.setAttribute("enctype",c),r?w.setAttribute("target",r):f.removeAttr("target"),e(l).remove()}}function s(t){if(!x.aborted&&!F){if(M=n(g),M||(a("cannot access response document"),t=k),t===D&&x)return x.abort("timeout"),void S.reject(x,"timeout");if(t==k&&x)return x.abort("server abort"),void S.reject(x,"error","server abort");if(M&&M.location.href!=m.iframeSrc||T){g.detachEvent?g.detachEvent("onload",s):g.removeEventListener("load",s,!1);var r,i="success";try{if(T)throw"timeout";var o="xml"==m.dataType||M.XMLDocument||e.isXMLDoc(M);if(a("isXml="+o),!o&&window.opera&&(null===M.body||!M.body.innerHTML)&&--O)return a("requeing onLoad callback, DOM not available"),void setTimeout(s,250);var u=M.body?M.body:M.documentElement;x.responseText=u?u.innerHTML:null,x.responseXML=M.XMLDocument?M.XMLDocument:M,o&&(m.dataType="xml"),x.getResponseHeader=function(e){var t={"content-type":m.dataType};return t[e.toLowerCase()]},u&&(x.status=Number(u.getAttribute("status"))||x.status,x.statusText=u.getAttribute("statusText")||x.statusText);var c=(m.dataType||"").toLowerCase(),l=/(json|script|text)/.test(c);if(l||m.textarea){var f=M.getElementsByTagName("textarea")[0];if(f)x.responseText=f.value,x.status=Number(f.getAttribute("status"))||x.status,x.statusText=f.getAttribute("statusText")||x.statusText;else if(l){var p=M.getElementsByTagName("pre")[0],h=M.getElementsByTagName("body")[0];p?x.responseText=p.textContent?p.textContent:p.innerText:h&&(x.responseText=h.textContent?h.textContent:h.innerText)}}else"xml"==c&&!x.responseXML&&x.responseText&&(x.responseXML=X(x.responseText));try{E=_(x,c,m)}catch(y){i="parsererror",x.error=r=y||i}}catch(y){a("error caught: ",y),i="error",x.error=r=y||i}x.aborted&&(a("upload aborted"),i=null),x.status&&(i=x.status>=200&&x.status<300||304===x.status?"success":"error"),"success"===i?(m.success&&m.success.call(m.context,E,"success",x),S.resolve(x.responseText,"success",x),d&&e.event.trigger("ajaxSuccess",[x,m])):i&&(void 0===r&&(r=x.statusText),m.error&&m.error.call(m.context,x,i,r),S.reject(x,"error",r),d&&e.event.trigger("ajaxError",[x,m,r])),d&&e.event.trigger("ajaxComplete",[x,m]),d&&!--e.active&&e.event.trigger("ajaxStop"),m.complete&&m.complete.call(m.context,x,i),F=!0,m.timeout&&clearTimeout(j),setTimeout(function(){m.iframeTarget?v.attr("src",m.iframeSrc):v.remove(),x.responseXML=null},100)}}}var c,l,m,d,p,v,g,x,y,b,T,j,w=f[0],S=e.Deferred();if(S.abort=function(e){x.abort(e)},r)for(l=0;l<h.length;l++)c=e(h[l]),i?c.prop("disabled",!1):c.removeAttr("disabled");if(m=e.extend(!0,{},e.ajaxSettings,t),m.context=m.context||m,p="jqFormIO"+(new Date).getTime(),m.iframeTarget?(v=e(m.iframeTarget),b=v.attr2("name"),b?p=b:v.attr2("name",p)):(v=e('<iframe name="'+p+'" src="'+m.iframeSrc+'" />'),v.css({position:"absolute",top:"-1000px",left:"-1000px"})),g=v[0],x={aborted:0,responseText:null,responseXML:null,status:0,statusText:"n/a",getAllResponseHeaders:function(){},getResponseHeader:function(){},setRequestHeader:function(){},abort:function(t){var r="timeout"===t?"timeout":"aborted";a("aborting upload... "+r),this.aborted=1;try{g.contentWindow.document.execCommand&&g.contentWindow.document.execCommand("Stop")}catch(n){}v.attr("src",m.iframeSrc),x.error=r,m.error&&m.error.call(m.context,x,r,t),d&&e.event.trigger("ajaxError",[x,m,r]),m.complete&&m.complete.call(m.context,x,r)}},d=m.global,d&&0===e.active++&&e.event.trigger("ajaxStart"),d&&e.event.trigger("ajaxSend",[x,m]),m.beforeSend&&m.beforeSend.call(m.context,x,m)===!1)return m.global&&e.active--,S.reject(),S;if(x.aborted)return S.reject(),S;y=w.clk,y&&(b=y.name,b&&!y.disabled&&(m.extraData=m.extraData||{},m.extraData[b]=y.value,"image"==y.type&&(m.extraData[b+".x"]=w.clk_x,m.extraData[b+".y"]=w.clk_y)));var D=1,k=2,A=e("meta[name=csrf-token]").attr("content"),L=e("meta[name=csrf-param]").attr("content");L&&A&&(m.extraData=m.extraData||{},m.extraData[L]=A),m.forceSync?o():setTimeout(o,10);var E,M,F,O=50,X=e.parseXML||function(e,t){return window.ActiveXObject?(t=new ActiveXObject("Microsoft.XMLDOM"),t.async="false",t.loadXML(e)):t=(new DOMParser).parseFromString(e,"text/xml"),t&&t.documentElement&&"parsererror"!=t.documentElement.nodeName?t:null},C=e.parseJSON||function(e){return window.eval("("+e+")")},_=function(t,r,a){var n=t.getResponseHeader("content-type")||"",i="xml"===r||!r&&n.indexOf("xml")>=0,o=i?t.responseXML:t.responseText;return i&&"parsererror"===o.documentElement.nodeName&&e.error&&e.error("parsererror"),a&&a.dataFilter&&(o=a.dataFilter(o,r)),"string"==typeof o&&("json"===r||!r&&n.indexOf("json")>=0?o=C(o):("script"===r||!r&&n.indexOf("javascript")>=0)&&e.globalEval(o)),o};return S}if(!this.length)return a("ajaxSubmit: skipping submit process - no element selected"),this;var u,c,l,f=this;"function"==typeof t?t={success:t}:void 0===t&&(t={}),u=t.type||this.attr2("method"),c=t.url||this.attr2("action"),l="string"==typeof c?e.trim(c):"",l=l||window.location.href||"",l&&(l=(l.match(/^([^#]+)/)||[])[1]),t=e.extend(!0,{url:l,success:e.ajaxSettings.success,type:u||e.ajaxSettings.type,iframeSrc:/^https/i.test(window.location.href||"")?"javascript:false":"about:blank"},t);var m={};if(this.trigger("form-pre-serialize",[this,t,m]),m.veto)return a("ajaxSubmit: submit vetoed via form-pre-serialize trigger"),this;if(t.beforeSerialize&&t.beforeSerialize(this,t)===!1)return a("ajaxSubmit: submit aborted via beforeSerialize callback"),this;var d=t.traditional;void 0===d&&(d=e.ajaxSettings.traditional);var p,h=[],v=this.formToArray(t.semantic,h);if(t.data&&(t.extraData=t.data,p=e.param(t.data,d)),t.beforeSubmit&&t.beforeSubmit(v,this,t)===!1)return a("ajaxSubmit: submit aborted via beforeSubmit callback"),this;if(this.trigger("form-submit-validate",[v,this,t,m]),m.veto)return a("ajaxSubmit: submit vetoed via form-submit-validate trigger"),this;var g=e.param(v,d);p&&(g=g?g+"&"+p:p),"GET"==t.type.toUpperCase()?(t.url+=(t.url.indexOf("?")>=0?"&":"?")+g,t.data=null):t.data=g;var x=[];if(t.resetForm&&x.push(function(){f.resetForm()}),t.clearForm&&x.push(function(){f.clearForm(t.includeHidden)}),!t.dataType&&t.target){var y=t.success||function(){};x.push(function(r){var a=t.replaceTarget?"replaceWith":"html";e(t.target)[a](r).each(y,arguments)})}else t.success&&x.push(t.success);if(t.success=function(e,r,a){for(var n=t.context||this,i=0,o=x.length;o>i;i++)x[i].apply(n,[e,r,a||f,f])},t.error){var b=t.error;t.error=function(e,r,a){var n=t.context||this;b.apply(n,[e,r,a,f])}}if(t.complete){var T=t.complete;t.complete=function(e,r){var a=t.context||this;T.apply(a,[e,r,f])}}var j=e("input[type=file]:enabled",this).filter(function(){return""!==e(this).val()}),w=j.length>0,S="multipart/form-data",D=f.attr("enctype")==S||f.attr("encoding")==S,k=n.fileapi&&n.formdata;a("fileAPI :"+k);var A,L=(w||D)&&!k;t.iframe!==!1&&(t.iframe||L)?t.closeKeepAlive?e.get(t.closeKeepAlive,function(){A=s(v)}):A=s(v):A=(w||D)&&k?o(v):e.ajax(t),f.removeData("jqxhr").data("jqxhr",A);for(var E=0;E<h.length;E++)h[E]=null;return this.trigger("form-submit-notify",[this,t]),this},e.fn.ajaxForm=function(n){if(n=n||{},n.delegation=n.delegation&&e.isFunction(e.fn.on),!n.delegation&&0===this.length){var i={s:this.selector,c:this.context};return!e.isReady&&i.s?(a("DOM not ready, queuing ajaxForm"),e(function(){e(i.s,i.c).ajaxForm(n)}),this):(a("terminating; zero elements found by selector"+(e.isReady?"":" (DOM not ready)")),this)}return n.delegation?(e(document).off("submit.form-plugin",this.selector,t).off("click.form-plugin",this.selector,r).on("submit.form-plugin",this.selector,n,t).on("click.form-plugin",this.selector,n,r),this):this.ajaxFormUnbind().bind("submit.form-plugin",n,t).bind("click.form-plugin",n,r)},e.fn.ajaxFormUnbind=function(){return this.unbind("submit.form-plugin click.form-plugin")},e.fn.formToArray=function(t,r){var a=[];if(0===this.length)return a;var i,o=this[0],s=this.attr("id"),u=t?o.getElementsByTagName("*"):o.elements;if(u&&!/MSIE [678]/.test(navigator.userAgent)&&(u=e(u).get()),s&&(i=e(':input[form="'+s+'"]').get(),i.length&&(u=(u||[]).concat(i))),!u||!u.length)return a;var c,l,f,m,d,p,h;for(c=0,p=u.length;p>c;c++)if(d=u[c],f=d.name,f&&!d.disabled)if(t&&o.clk&&"image"==d.type)o.clk==d&&(a.push({name:f,value:e(d).val(),type:d.type}),a.push({name:f+".x",value:o.clk_x},{name:f+".y",value:o.clk_y}));else if(m=e.fieldValue(d,!0),m&&m.constructor==Array)for(r&&r.push(d),l=0,h=m.length;h>l;l++)a.push({name:f,value:m[l]});else if(n.fileapi&&"file"==d.type){r&&r.push(d);var v=d.files;if(v.length)for(l=0;l<v.length;l++)a.push({name:f,value:v[l],type:d.type});else a.push({name:f,value:"",type:d.type})}else null!==m&&"undefined"!=typeof m&&(r&&r.push(d),a.push({name:f,value:m,type:d.type,required:d.required}));if(!t&&o.clk){var g=e(o.clk),x=g[0];f=x.name,f&&!x.disabled&&"image"==x.type&&(a.push({name:f,value:g.val()}),a.push({name:f+".x",value:o.clk_x},{name:f+".y",value:o.clk_y}))}return a},e.fn.formSerialize=function(t){return e.param(this.formToArray(t))},e.fn.fieldSerialize=function(t){var r=[];return this.each(function(){var a=this.name;if(a){var n=e.fieldValue(this,t);if(n&&n.constructor==Array)for(var i=0,o=n.length;o>i;i++)r.push({name:a,value:n[i]});else null!==n&&"undefined"!=typeof n&&r.push({name:this.name,value:n})}}),e.param(r)},e.fn.fieldValue=function(t){for(var r=[],a=0,n=this.length;n>a;a++){var i=this[a],o=e.fieldValue(i,t);null===o||"undefined"==typeof o||o.constructor==Array&&!o.length||(o.constructor==Array?e.merge(r,o):r.push(o))}return r},e.fieldValue=function(t,r){var a=t.name,n=t.type,i=t.tagName.toLowerCase();if(void 0===r&&(r=!0),r&&(!a||t.disabled||"reset"==n||"button"==n||("checkbox"==n||"radio"==n)&&!t.checked||("submit"==n||"image"==n)&&t.form&&t.form.clk!=t||"select"==i&&-1==t.selectedIndex))return null;if("select"==i){var o=t.selectedIndex;if(0>o)return null;for(var s=[],u=t.options,c="select-one"==n,l=c?o+1:u.length,f=c?o:0;l>f;f++){var m=u[f];if(m.selected){var d=m.value;if(d||(d=m.attributes&&m.attributes.value&&!m.attributes.value.specified?m.text:m.value),c)return d;s.push(d)}}return s}return e(t).val()},e.fn.clearForm=function(t){return this.each(function(){e("input,select,textarea",this).clearFields(t)})},e.fn.clearFields=e.fn.clearInputs=function(t){var r=/^(?:color|date|datetime|email|month|number|password|range|search|tel|text|time|url|week)$/i;return this.each(function(){var a=this.type,n=this.tagName.toLowerCase();r.test(a)||"textarea"==n?this.value="":"checkbox"==a||"radio"==a?this.checked=!1:"select"==n?this.selectedIndex=-1:"file"==a?/MSIE/.test(navigator.userAgent)?e(this).replaceWith(e(this).clone(!0)):e(this).val(""):t&&(t===!0&&/hidden/.test(a)||"string"==typeof t&&e(this).is(t))&&(this.value="")})},e.fn.resetForm=function(){return this.each(function(){("function"==typeof this.reset||"object"==typeof this.reset&&!this.reset.nodeType)&&this.reset()})},e.fn.enable=function(e){return void 0===e&&(e=!0),this.each(function(){this.disabled=!e})},e.fn.selected=function(t){return void 0===t&&(t=!0),this.each(function(){var r=this.type;if("checkbox"==r||"radio"==r)this.checked=t;else if("option"==this.tagName.toLowerCase()){var a=e(this).parent("select");t&&a[0]&&"select-one"==a[0].type&&a.find("option").selected(!1),this.selected=t}})},e.fn.ajaxSubmit.debug=!1});
/**
 * jquery.mask.js
 * @version: v1.14.0
 * @author: Igor Escobar
 *
 * Created by Igor Escobar on 2012-03-10. Please report any bug at http://blog.igorescobar.com
 *
 * Copyright (c) 2012 Igor Escobar http://blog.igorescobar.com
 *
 * The MIT License (http://www.opensource.org/licenses/mit-license.php)
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

/* jshint laxbreak: true */
/* global define, jQuery, Zepto */

'use strict';

// UMD (Universal Module Definition) patterns for JavaScript modules that work everywhere.
// https://github.com/umdjs/umd/blob/master/jqueryPluginCommonjs.js
(function (factory) {

	if (typeof define === 'function' && define.amd) {
		define(['jquery'], factory);
	} else if (typeof exports === 'object') {
		module.exports = factory(require('jquery'));
	} else {
		factory(jQuery || Zepto);
	}

}(function ($) {

	var Mask = function (el, mask, options) {

		var p = {
			invalid: [],
			getCaret: function () {
				try {
					var sel,
						pos = 0,
						ctrl = el.get(0),
						dSel = document.selection,
						cSelStart = ctrl.selectionStart;

					// IE Support
					if (dSel && navigator.appVersion.indexOf('MSIE 10') === -1) {
						sel = dSel.createRange();
						sel.moveStart('character', -p.val().length);
						pos = sel.text.length;
					}
					// Firefox support
					else if (cSelStart || cSelStart === '0') {
						pos = cSelStart;
					}

					return pos;
				} catch (e) {}
			},
			setCaret: function(pos) {
				try {
					if (el.is(':focus')) {
						var range, ctrl = el.get(0);
						// jjensen: kept only IE conditional because it fixes Samsung Keyboard issue
						range = ctrl.createTextRange();
						range.collapse(true);
						range.moveEnd('character', pos);
						range.moveStart('character', pos);
						range.select();
					}
				} catch (e) {}
			},
			events: function() {
				el
					.on('keydown.mask', function(e) {
						el.data('mask-keycode', e.keyCode || e.which);
					})
					.on($.jMaskGlobals.useInput ? 'input.mask' : 'keyup.mask', p.behaviour)
					.on('paste.mask drop.mask', function() {
						setTimeout(function() {
							el.keydown().keyup();
						}, 100);
					})
					.on('change.mask', function(){
						el.data('changed', true);
					})
					.on('blur.mask', function(){
						if (oldValue !== p.val() && !el.data('changed')) {
							el.trigger('change');
						}
						el.data('changed', false);
					})
					// it's very important that this callback remains in this position
					// otherwhise oldValue it's going to work buggy
					.on('blur.mask', function() {
						oldValue = p.val();
					})
					// select all text on focus
					.on('focus.mask', function (e) {
						if (options.selectOnFocus === true) {
							$(e.target).select();
						}
					})
					// clear the value if it not complete the mask
					.on('focusout.mask', function() {
						if (options.clearIfNotMatch && !regexMask.test(p.val())) {
							p.val('');
						}
					});
			},
			getRegexMask: function() {
				var maskChunks = [], translation, pattern, optional, recursive, oRecursive, r;

				for (var i = 0; i < mask.length; i++) {
					translation = jMask.translation[mask.charAt(i)];

					if (translation) {

						pattern = translation.pattern.toString().replace(/.{1}$|^.{1}/g, '');
						optional = translation.optional;
						recursive = translation.recursive;

						if (recursive) {
							maskChunks.push(mask.charAt(i));
							oRecursive = {digit: mask.charAt(i), pattern: pattern};
						} else {
							maskChunks.push(!optional && !recursive ? pattern : (pattern + '?'));
						}

					} else {
						maskChunks.push(mask.charAt(i).replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'));
					}
				}

				r = maskChunks.join('');

				if (oRecursive) {
					r = r.replace(new RegExp('(' + oRecursive.digit + '(.*' + oRecursive.digit + ')?)'), '($1)?')
						.replace(new RegExp(oRecursive.digit, 'g'), oRecursive.pattern);
				}

				return new RegExp(r);
			},
			destroyEvents: function() {
				el.off(['input', 'keydown', 'keyup', 'paste', 'drop', 'blur', 'focusout', ''].join('.mask '));
			},
			val: function(v) {
				var isInput = el.is('input'),
					method = isInput ? 'val' : 'text',
					r;

				if (arguments.length > 0) {
					if (el[method]() !== v) {
						el[method](v);
					}
					r = el;
				} else {
					r = el[method]();
				}

				return r;
			},
			getMCharsBeforeCount: function(index, onCleanVal) {
				for (var count = 0, i = 0, maskL = mask.length; i < maskL && i < index; i++) {
					if (!jMask.translation[mask.charAt(i)]) {
						index = onCleanVal ? index + 1 : index;
						count++;
					}
				}
				return count;
			},
			caretPos: function (originalCaretPos, oldLength, newLength, maskDif) {
				var translation = jMask.translation[mask.charAt(Math.min(originalCaretPos - 1, mask.length - 1))];

				return !translation ? p.caretPos(originalCaretPos + 1, oldLength, newLength, maskDif)
					: Math.min(originalCaretPos + newLength - oldLength - maskDif, newLength);
			},
			behaviour: function(e) {
				e = e || window.event;
				p.invalid = [];

				var keyCode = el.data('mask-keycode');

				if ($.inArray(keyCode, jMask.byPassKeys) === -1) {
					var caretPos    = p.getCaret(),
						currVal     = p.val(),
						currValL    = currVal.length,
						newVal      = p.getMasked(),
						newValL     = newVal.length,
						maskDif     = p.getMCharsBeforeCount(newValL - 1) - p.getMCharsBeforeCount(currValL - 1),
						changeCaret = caretPos < currValL;

					p.val(newVal);

					if (changeCaret) {
						// Avoid adjusting caret on backspace or delete
						if (!(keyCode === 8 || keyCode === 46)) {
							caretPos = p.caretPos(caretPos, currValL, newValL, maskDif);
						}
						p.setCaret(caretPos);
					}

					return p.callbacks(e);
				}
			},
			getMasked: function(skipMaskChars, val) {
				var buf = [],
					value = val === undefined ? p.val() : val + '',
					m = 0, maskLen = mask.length,
					v = 0, valLen = value.length,
					offset = 1, addMethod = 'push',
					resetPos = -1,
					lastMaskChar,
					check;

				if (options.reverse) {
					addMethod = 'unshift';
					offset = -1;
					lastMaskChar = 0;
					m = maskLen - 1;
					v = valLen - 1;
					check = function () {
						return m > -1 && v > -1;
					};
				} else {
					lastMaskChar = maskLen - 1;
					check = function () {
						return m < maskLen && v < valLen;
					};
				}

				while (check()) {
					var maskDigit = mask.charAt(m),
						valDigit = value.charAt(v),
						translation = jMask.translation[maskDigit];

					if (translation) {
						if (valDigit.match(translation.pattern)) {
							buf[addMethod](valDigit);
							if (translation.recursive) {
								if (resetPos === -1) {
									resetPos = m;
								} else if (m === lastMaskChar) {
									m = resetPos - offset;
								}

								if (lastMaskChar === resetPos) {
									m -= offset;
								}
							}
							m += offset;
						} else if (translation.optional) {
							m += offset;
							v -= offset;
						} else if (translation.fallback) {
							buf[addMethod](translation.fallback);
							m += offset;
							v -= offset;
						} else {
							p.invalid.push({p: v, v: valDigit, e: translation.pattern});
						}
						v += offset;
					} else {
						if (!skipMaskChars) {
							buf[addMethod](maskDigit);
						}

						if (valDigit === maskDigit) {
							v += offset;
						}

						m += offset;
					}
				}

				var lastMaskCharDigit = mask.charAt(lastMaskChar);
				if (maskLen === valLen + 1 && !jMask.translation[lastMaskCharDigit]) {
					buf.push(lastMaskCharDigit);
				}

				return buf.join('');
			},
			callbacks: function (e) {
				var val = p.val(),
					changed = val !== oldValue,
					defaultArgs = [val, e, el, options],
					callback = function(name, criteria, args) {
						if (typeof options[name] === 'function' && criteria) {
							options[name].apply(this, args);
						}
					};

				callback('onChange', changed === true, defaultArgs);
				callback('onKeyPress', changed === true, defaultArgs);
				callback('onComplete', val.length === mask.length, defaultArgs);
				callback('onInvalid', p.invalid.length > 0, [val, e, el, p.invalid, options]);
			}
		};

		el = $(el);
		var jMask = this, oldValue = p.val(), regexMask;

		mask = typeof mask === 'function' ? mask(p.val(), undefined, el,  options) : mask;


		// public methods
		jMask.mask = mask;
		jMask.options = options;
		jMask.remove = function() {
			var caret = p.getCaret();
			p.destroyEvents();
			p.val(jMask.getCleanVal());
			p.setCaret(caret - p.getMCharsBeforeCount(caret));
			return el;
		};

		// get value without mask
		jMask.getCleanVal = function() {
			return p.getMasked(true);
		};

		// get masked value without the value being in the input or element
		jMask.getMaskedVal = function(val) {
			return p.getMasked(false, val);
		};

		jMask.init = function(onlyMask) {
			onlyMask = onlyMask || false;
			options = options || {};

			jMask.clearIfNotMatch  = $.jMaskGlobals.clearIfNotMatch;
			jMask.byPassKeys       = $.jMaskGlobals.byPassKeys;
			jMask.translation      = $.extend({}, $.jMaskGlobals.translation, options.translation);

			jMask = $.extend(true, {}, jMask, options);

			regexMask = p.getRegexMask();

			if (onlyMask === false) {

				if (options.placeholder) {
					el.attr('placeholder' , options.placeholder);
				}

				// this is necessary, otherwise if the user submit the form
				// and then press the "back" button, the autocomplete will erase
				// the data. Works fine on IE9+, FF, Opera, Safari.
				if (el.data('mask')) {
					el.attr('autocomplete', 'off');
				}

				p.destroyEvents();
				p.events();

				var caret = p.getCaret();
				p.val(p.getMasked());
				p.setCaret(caret + p.getMCharsBeforeCount(caret, true));

			} else {
				p.events();
				p.val(p.getMasked());
			}
		};

		jMask.init(!el.is('input'));
	};

	$.maskWatchers = {};
	var HTMLAttributes = function () {
			var input = $(this),
				options = {},
				prefix = 'data-mask-',
				mask = input.attr('data-mask');

			if (input.attr(prefix + 'reverse')) {
				options.reverse = true;
			}

			if (input.attr(prefix + 'clearifnotmatch')) {
				options.clearIfNotMatch = true;
			}

			if (input.attr(prefix + 'selectonfocus') === 'true') {
				options.selectOnFocus = true;
			}

			if (notSameMaskObject(input, mask, options)) {
				return input.data('mask', new Mask(this, mask, options));
			}
		},
		notSameMaskObject = function(field, mask, options) {
			options = options || {};
			var maskObject = $(field).data('mask'),
				stringify = JSON.stringify,
				value = $(field).val() || $(field).text();
			try {
				if (typeof mask === 'function') {
					mask = mask(value);
				}
				return typeof maskObject !== 'object' || stringify(maskObject.options) !== stringify(options) || maskObject.mask !== mask;
			} catch (e) {}
		},
		eventSupported = function(eventName) {
			var el = document.createElement('div'), isSupported;

			eventName = 'on' + eventName;
			isSupported = (eventName in el);

			if ( !isSupported ) {
				el.setAttribute(eventName, 'return;');
				isSupported = typeof el[eventName] === 'function';
			}
			el = null;

			return isSupported;
		};

	$.fn.mask = function(mask, options) {
		options = options || {};
		var selector = this.selector,
			globals = $.jMaskGlobals,
			interval = globals.watchInterval,
			watchInputs = options.watchInputs || globals.watchInputs,
			maskFunction = function() {
				if (notSameMaskObject(this, mask, options)) {
					return $(this).data('mask', new Mask(this, mask, options));
				}
			};

		$(this).each(maskFunction);

		if (selector && selector !== '' && watchInputs) {
			clearInterval($.maskWatchers[selector]);
			$.maskWatchers[selector] = setInterval(function(){
				$(document).find(selector).each(maskFunction);
			}, interval);
		}
		return this;
	};

	$.fn.masked = function(val) {
		return this.data('mask').getMaskedVal(val);
	};

	$.fn.unmask = function() {
		clearInterval($.maskWatchers[this.selector]);
		delete $.maskWatchers[this.selector];
		return this.each(function() {
			var dataMask = $(this).data('mask');
			if (dataMask) {
				dataMask.remove().removeData('mask');
			}
		});
	};

	$.fn.cleanVal = function() {
		return this.data('mask').getCleanVal();
	};

	$.applyDataMask = function(selector) {
		selector = selector || $.jMaskGlobals.maskElements;
		var $selector = (selector instanceof $) ? selector : $(selector);
		$selector.filter($.jMaskGlobals.dataMaskAttr).each(HTMLAttributes);
	};

	var globals = {
		maskElements: 'input,td,span,div',
		dataMaskAttr: '*[data-mask]',
		dataMask: true,
		watchInterval: 300,
		watchInputs: true,
		useInput: eventSupported('input'),
		watchDataMask: false,
		byPassKeys: [9, 16, 17, 18, 36, 37, 38, 39, 40, 91],
		translation: {
			'0': {pattern: /\d/},
			'9': {pattern: /\d/, optional: true},
			'#': {pattern: /\d/, recursive: true},
			'A': {pattern: /[a-zA-Z0-9]/},
			'S': {pattern: /[a-zA-Z]/}
		}
	};

	$.jMaskGlobals = $.jMaskGlobals || {};
	globals = $.jMaskGlobals = $.extend(true, {}, globals, $.jMaskGlobals);

	// looking for inputs with data-mask attribute
	if (globals.dataMask) {
		$.applyDataMask();
	}

	setInterval(function() {
		if ($.jMaskGlobals.watchDataMask) {
			$.applyDataMask();
		}
	}, globals.watchInterval);
}));

/*
 * rwdImageMaps jQuery plugin v1.6
 *
 * Allows image maps to be used in a responsive design by recalculating the area coordinates to match the actual image size on load and window.resize
 *
 * Copyright (c) 2016 Matt Stow
 * https://github.com/stowball/jQuery-rwdImageMaps
 * http://mattstow.com
 * Licensed under the MIT license
 */
;(function($) {
	$.fn.rwdImageMaps = function() {
		var $img = this;

		var rwdImageMap = function() {
			$img.each(function() {
				if (typeof($(this).attr('usemap')) == 'undefined')
					return;

				var that = this,
					$that = $(that);

				// Since WebKit doesn't know the height until after the image has loaded, perform everything in an onload copy
				$('<img />').on('load', function() {
					var attrW = 'width',
						attrH = 'height',
						w = $that.attr(attrW),
						h = $that.attr(attrH);

					if (!w || !h) {
						var temp = new Image();
						temp.src = $that.attr('src');
						if (!w)
							w = temp.width;
						if (!h)
							h = temp.height;
					}

					var wPercent = $that.width()/100,
						hPercent = $that.height()/100,
						map = $that.attr('usemap').replace('#', ''),
						c = 'coords';

					$('map[name="' + map + '"]').find('area').each(function() {
						var $this = $(this);
						if (!$this.data(c))
							$this.data(c, $this.attr(c));

						var coords = $this.data(c).split(','),
							coordsPercent = new Array(coords.length);

						for (var i = 0; i < coordsPercent.length; ++i) {
							if (i % 2 === 0)
								coordsPercent[i] = parseInt(((coords[i]/w)*100)*wPercent);
							else
								coordsPercent[i] = parseInt(((coords[i]/h)*100)*hPercent);
						}
						$this.attr(c, coordsPercent.toString());
					});
				}).attr('src', $that.attr('src'));
			});
		};
		$(window).resize(rwdImageMap).trigger('resize');

		return this;
	};
})(jQuery);

/*
 * jQuery throttle / debounce - v1.1 - 3/7/2010
 * http://benalman.com/projects/jquery-throttle-debounce-plugin/
 *
 * Copyright (c) 2010 "Cowboy" Ben Alman
 * Dual licensed under the MIT and GPL licenses.
 * http://benalman.com/about/license/
 */
(function(b,c){var $=b.jQuery||b.Cowboy||(b.Cowboy={}),a;$.throttle=a=function(e,f,j,i){var h,d=0;if(typeof f!=="boolean"){i=j;j=f;f=c}function g(){var o=this,m=+new Date()-d,n=arguments;function l(){d=+new Date();j.apply(o,n)}function k(){h=c}if(i&&!h){l()}h&&clearTimeout(h);if(i===c&&m>e){l()}else{if(f!==true){h=setTimeout(i?k:l,i===c?e-m:e)}}}if($.guid){g.guid=j.guid=j.guid||$.guid++}return g};$.debounce=function(d,e,f){return f===c?a(d,e,false):a(d,f,e!==false)}})(this);

/**
 * http://www.JSON.org/json2.js
 **/
if (! ("JSON" in window && window.JSON)){JSON={}}(function(){function f(n){return n<10?"0"+n:n}if(typeof Date.prototype.toJSON!=="function"){Date.prototype.toJSON=function(key){return this.getUTCFullYear()+"-"+f(this.getUTCMonth()+1)+"-"+f(this.getUTCDate())+"T"+f(this.getUTCHours())+":"+f(this.getUTCMinutes())+":"+f(this.getUTCSeconds())+"Z"};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(key){return this.valueOf()}}var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},rep;function quote(string){escapable.lastIndex=0;return escapable.test(string)?'"'+string.replace(escapable,function(a){var c=meta[a];return typeof c==="string"?c:"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})+'"':'"'+string+'"'}function str(key,holder){var i,k,v,length,mind=gap,partial,value=holder[key];if(value&&typeof value==="object"&&typeof value.toJSON==="function"){value=value.toJSON(key)}if(typeof rep==="function"){value=rep.call(holder,key,value)}switch(typeof value){case"string":return quote(value);case"number":return isFinite(value)?String(value):"null";case"boolean":case"null":return String(value);case"object":if(!value){return"null"}gap+=indent;partial=[];if(Object.prototype.toString.apply(value)==="[object Array]"){length=value.length;for(i=0;i<length;i+=1){partial[i]=str(i,value)||"null"}v=partial.length===0?"[]":gap?"[\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"]":"["+partial.join(",")+"]";gap=mind;return v}if(rep&&typeof rep==="object"){length=rep.length;for(i=0;i<length;i+=1){k=rep[i];if(typeof k==="string"){v=str(k,value);if(v){partial.push(quote(k)+(gap?": ":":")+v)}}}}else{for(k in value){if(Object.hasOwnProperty.call(value,k)){v=str(k,value);if(v){partial.push(quote(k)+(gap?": ":":")+v)}}}}v=partial.length===0?"{}":gap?"{\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"}":"{"+partial.join(",")+"}";gap=mind;return v}}if(typeof JSON.stringify!=="function"){JSON.stringify=function(value,replacer,space){var i;gap="";indent="";if(typeof space==="number"){for(i=0;i<space;i+=1){indent+=" "}}else{if(typeof space==="string"){indent=space}}rep=replacer;if(replacer&&typeof replacer!=="function"&&(typeof replacer!=="object"||typeof replacer.length!=="number")){throw new Error("JSON.stringify")}return str("",{"":value})}}if(typeof JSON.parse!=="function"){JSON.parse=function(text,reviver){var j;function walk(holder,key){var k,v,value=holder[key];if(value&&typeof value==="object"){for(k in value){if(Object.hasOwnProperty.call(value,k)){v=walk(value,k);if(v!==undefined){value[k]=v}else{delete value[k]}}}}return reviver.call(holder,key,value)}cx.lastIndex=0;if(cx.test(text)){text=text.replace(cx,function(a){return"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})}if(/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""))){j=eval("("+text+")");return typeof reviver==="function"?walk({"":j},""):j}throw new SyntaxError("JSON.parse")}}}());

/**
 * Copyright (c) 2010 Maxim Vasiliev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author Maxim Vasiliev
 * Date: 09.09.2010
 * Time: 19:02:33
 */


var form2js = (function()
{
	"use strict";

	/**
	 * Returns form values represented as Javascript object
	 * "name" attribute defines structure of resulting object
	 *
	 * @param rootNode {Element|String} root form element (or it's id) or array of root elements
	 * @param delimiter {String} structure parts delimiter defaults to '.'
	 * @param skipEmpty {Boolean} should skip empty text values, defaults to true
	 * @param nodeCallback {Function} custom function to get node value
	 * @param useIdIfEmptyName {Boolean} if true value of id attribute of field will be used if name of field is empty
	 */
	function form2js(rootNode, delimiter, skipEmpty, nodeCallback, useIdIfEmptyName)
	{
		if (typeof skipEmpty == 'undefined' || skipEmpty == null) skipEmpty = true;
		if (typeof delimiter == 'undefined' || delimiter == null) delimiter = '.';
		if (arguments.length < 5) useIdIfEmptyName = false;

		rootNode = typeof rootNode == 'string' ? document.getElementById(rootNode) : rootNode;

		var formValues = [],
				currNode,
				i = 0;

		/* If rootNode is array - combine values */
		if (rootNode.constructor == Array || (typeof NodeList != "undefined" && rootNode.constructor == NodeList))
		{
			while(currNode = rootNode[i++])
			{
				formValues = formValues.concat(getFormValues(currNode, nodeCallback, useIdIfEmptyName));
			}
		}
		else
		{
			formValues = getFormValues(rootNode, nodeCallback, useIdIfEmptyName);
		}

		return processNameValues(formValues, skipEmpty, delimiter);
	}

	/**
	 * Processes collection of { name: 'name', value: 'value' } objects.
	 * @param nameValues
	 * @param skipEmpty if true skips elements with value == '' or value == null
	 * @param delimiter
	 */
	function processNameValues(nameValues, skipEmpty, delimiter)
	{
		var result = {},
				arrays = {},
				i, j, k, l,
				value,
				nameParts,
				currResult,
				arrNameFull,
				arrName,
				arrIdx,
				namePart,
				name,
				_nameParts;

		for (i = 0; i < nameValues.length; i++)
		{
			value = nameValues[i].value;

			if (skipEmpty && (value === '' || value === null)) continue;

			name = nameValues[i].name;
			_nameParts = name.split(delimiter);
			nameParts = [];
			currResult = result;
			arrNameFull = '';

			for(j = 0; j < _nameParts.length; j++)
			{
				namePart = _nameParts[j].split('][');
				if (namePart.length > 1)
				{
					for(k = 0; k < namePart.length; k++)
					{
						if (k == 0)
						{
							namePart[k] = namePart[k] + ']';
						}
						else if (k == namePart.length - 1)
						{
							namePart[k] = '[' + namePart[k];
						}
						else
						{
							namePart[k] = '[' + namePart[k] + ']';
						}

						arrIdx = namePart[k].match(/([a-z_]+)?\[([a-z_][a-z0-9_]+?)\]/i);
						if (arrIdx)
						{
							for(l = 1; l < arrIdx.length; l++)
							{
								if (arrIdx[l]) nameParts.push(arrIdx[l]);
							}
						}
						else{
							nameParts.push(namePart[k]);
						}
					}
				}
				else
					nameParts = nameParts.concat(namePart);
			}

			for (j = 0; j < nameParts.length; j++)
			{
				namePart = nameParts[j];

				if (namePart.indexOf('[]') > -1 && j == nameParts.length - 1)
				{
					arrName = namePart.substr(0, namePart.indexOf('['));
					arrNameFull += arrName;

					if (!currResult[arrName]) currResult[arrName] = [];
					currResult[arrName].push(value);
				}
				else if (namePart.indexOf('[') > -1)
				{
					arrName = namePart.substr(0, namePart.indexOf('['));
					arrIdx = namePart.replace(/(^([a-z_]+)?\[)|(\]$)/gi, '');

					/* Unique array name */
					arrNameFull += '_' + arrName + '_' + arrIdx;

					/*
					 * Because arrIdx in field name can be not zero-based and step can be
					 * other than 1, we can't use them in target array directly.
					 * Instead we're making a hash where key is arrIdx and value is a reference to
					 * added array element
					 */

					if (!arrays[arrNameFull]) arrays[arrNameFull] = {};
					if (arrName != '' && !currResult[arrName]) currResult[arrName] = [];

					if (j == nameParts.length - 1)
					{
						if (arrName == '')
						{
							currResult.push(value);
							arrays[arrNameFull][arrIdx] = currResult[currResult.length - 1];
						}
						else
						{
							currResult[arrName].push(value);
							arrays[arrNameFull][arrIdx] = currResult[arrName][currResult[arrName].length - 1];
						}
					}
					else
					{
						if (!arrays[arrNameFull][arrIdx])
						{
							if ((/^[a-z_]+\[?/i).test(nameParts[j+1])) currResult[arrName].push({});
							else currResult[arrName].push([]);

							arrays[arrNameFull][arrIdx] = currResult[arrName][currResult[arrName].length - 1];
						}
					}

					currResult = arrays[arrNameFull][arrIdx];
				}
				else
				{
					arrNameFull += namePart;

					if (j < nameParts.length - 1) /* Not the last part of name - means object */
					{
						if (!currResult[namePart]) currResult[namePart] = {};
						currResult = currResult[namePart];
					}
					else
					{
						currResult[namePart] = value;
					}
				}
			}
		}

		return result;
	}

	function getFormValues(rootNode, nodeCallback, useIdIfEmptyName)
	{
		var result = extractNodeValues(rootNode, nodeCallback, useIdIfEmptyName);
		return result.length > 0 ? result : getSubFormValues(rootNode, nodeCallback, useIdIfEmptyName);
	}

	function getSubFormValues(rootNode, nodeCallback, useIdIfEmptyName)
	{
		var result = [],
				currentNode = rootNode.firstChild;

		while (currentNode)
		{
			result = result.concat(extractNodeValues(currentNode, nodeCallback, useIdIfEmptyName));
			currentNode = currentNode.nextSibling;
		}

		return result;
	}

	function extractNodeValues(node, nodeCallback, useIdIfEmptyName) {
		var callbackResult, fieldValue, result, fieldName = getFieldName(node, useIdIfEmptyName);

		callbackResult = nodeCallback && nodeCallback(node);

		if (callbackResult && callbackResult.name) {
			result = [callbackResult];
		}
		else if (fieldName != '' && node.nodeName.match(/INPUT|TEXTAREA/i)) {
			fieldValue = getFieldValue(node);
			result = [ { name: fieldName, value: fieldValue} ];
		}
		else if (fieldName != '' && node.nodeName.match(/SELECT/i)) {
			fieldValue = getFieldValue(node);
			result = [ { name: fieldName.replace(/\[\]$/, ''), value: fieldValue } ];
		}
		else {
			result = getSubFormValues(node, nodeCallback, useIdIfEmptyName);
		}

		return result;
	}

	function getFieldName(node, useIdIfEmptyName)
	{
		if (node.name && node.name != '') return node.name;
		else if (useIdIfEmptyName && node.id && node.id != '') return node.id;
		else return '';
	}


	function getFieldValue(fieldNode)
	{
		if (fieldNode.disabled) return null;

		switch (fieldNode.nodeName) {
			case 'INPUT':
			case 'TEXTAREA':
				switch (fieldNode.type.toLowerCase()) {
					case 'radio':
					case 'checkbox':
						if (fieldNode.checked && fieldNode.value === "true") return true;
						if (!fieldNode.checked && fieldNode.value === "true") return false;
						if (fieldNode.checked) return fieldNode.value;
						break;

					case 'button':
					case 'reset':
					case 'submit':
					case 'image':
						return '';
						break;

					default:
						return fieldNode.value;
						break;
				}
				break;

			case 'SELECT':
				return getSelectedOptionValue(fieldNode);
				break;

			default:
				break;
		}

		return null;
	}

	function getSelectedOptionValue(selectNode)
	{
		var multiple = selectNode.multiple,
				result = [],
				options,
				i, l;

		if (!multiple) return selectNode.value;

		for (options = selectNode.getElementsByTagName("option"), i = 0, l = options.length; i < l; i++)
		{
			if (options[i].selected) result.push(options[i].value);
		}

		return result;
	}

	return form2js;

})();

/**
 * Copyright (c) 2010 Maxim Vasiliev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author Maxim Vasiliev
 * Date: 19.09.11
 * Time: 23:40
 */

var js2form = (function()
{
	"use strict";

	var _subArrayRegexp = /^\[\d+?\]/,
			_subObjectRegexp = /^[a-zA-Z_][a-zA-Z_0-9]+/,
			_arrayItemRegexp = /\[[0-9]+?\]$/,
			_lastIndexedArrayRegexp = /(.*)(\[)([0-9]*)(\])$/,
			_arrayOfArraysRegexp = /\[([0-9]+)\]\[([0-9]+)\]/g,
			_inputOrTextareaRegexp = /INPUT|TEXTAREA/i;

	/**
	 *
	 * @param rootNode
	 * @param data
	 * @param delimiter
	 * @param nodeCallback
	 * @param useIdIfEmptyName
	 */
	function js2form(rootNode, data, delimiter, nodeCallback, useIdIfEmptyName)
	{
		if (arguments.length < 3) delimiter = '.';
		if (arguments.length < 4) nodeCallback = null;
		if (arguments.length < 5) useIdIfEmptyName = false;

		var fieldValues,
				formFieldsByName;

		fieldValues = object2array(data);
		formFieldsByName = getFields(rootNode, useIdIfEmptyName, delimiter, {}, false);

		for (var i = 0; i < fieldValues.length; i++)
		{
			var fieldName = fieldValues[i].name,
					fieldValue = fieldValues[i].value;

			if (typeof formFieldsByName[fieldName] != 'undefined')
			{
				setValue(formFieldsByName[fieldName], fieldValue);
			}
			else if (typeof formFieldsByName[fieldName.replace(_arrayItemRegexp, '[]')] != 'undefined')
			{
				setValue(formFieldsByName[fieldName.replace(_arrayItemRegexp, '[]')], fieldValue);
			}
		}
	}

	function setValue(field, value)
	{
		var children, i, l;

		if (field instanceof Array)
		{
			for(i = 0; i < field.length; i++)
			{
				if (field[i].value == value || value === true) field[i].checked = true;
			}
		}
		else if (_inputOrTextareaRegexp.test(field.nodeName))
		{
			field.value = value;
		}
		else if (/SELECT/i.test(field.nodeName))
		{
			children = field.getElementsByTagName('option');
			for (i = 0,l = children.length; i < l; i++)
			{
				if (children[i].value == value)
				{
					children[i].selected = true;
					if (field.multiple) break;
				}
				else if (!field.multiple)
				{
					children[i].selected = false;
				}
			}
		}
	}

	function getFields(rootNode, useIdIfEmptyName, delimiter, arrayIndexes, shouldClean)
	{
		if (arguments.length < 4) arrayIndexes = {};

		var result = {},
				currNode = rootNode.firstChild,
				name, nameNormalized,
				subFieldName,
				i, j, l,
				options;

		while (currNode)
		{
			name = '';

			if (currNode.name && currNode.name != '')
			{
				name = currNode.name;
			}
			else if (useIdIfEmptyName && currNode.id && currNode.id != '')
			{
				name = currNode.id;
			}

			if (name == '')
			{
				var subFields = getFields(currNode, useIdIfEmptyName, delimiter, arrayIndexes, shouldClean);
				for (subFieldName in subFields)
				{
					if (typeof result[subFieldName] == 'undefined')
					{
						result[subFieldName] = subFields[subFieldName];
					}
					else
					{
						for (i = 0; i < subFields[subFieldName].length; i++)
						{
							result[subFieldName].push(subFields[subFieldName][i]);
						}
					}
				}
			}
			else
			{
				if (/SELECT/i.test(currNode.nodeName))
				{
					for(j = 0, options = currNode.getElementsByTagName('option'), l = options.length; j < l; j++)
					{
						if (shouldClean)
						{
							options[j].selected = false;
						}

						nameNormalized = normalizeName(name, delimiter, arrayIndexes);
						result[nameNormalized] = currNode;
					}
				}
				else if (/INPUT/i.test(currNode.nodeName) && /CHECKBOX|RADIO/i.test(currNode.type))
				{
					if(shouldClean)
					{
						currNode.checked = false;
					}

					nameNormalized = normalizeName(name, delimiter, arrayIndexes);
					nameNormalized = nameNormalized.replace(_arrayItemRegexp, '[]');
					if (!result[nameNormalized]) result[nameNormalized] = [];
					result[nameNormalized].push(currNode);
				}
				else
				{
					if (shouldClean)
					{
						currNode.value = '';
					}

					nameNormalized = normalizeName(name, delimiter, arrayIndexes);
					result[nameNormalized] = currNode;
				}
			}

			currNode = currNode.nextSibling;
		}

		return result;
	}

	/**
	 * Normalizes names of arrays, puts correct indexes (consecutive and ordered by element appearance in HTML)
	 * @param name
	 * @param delimiter
	 * @param arrayIndexes
	 */
	function normalizeName(name, delimiter, arrayIndexes)
	{
		var nameChunksNormalized = [],
				nameChunks = name.split(delimiter),
				currChunk,
				nameMatches,
				nameNormalized,
				currIndex,
				newIndex,
				i;

		name = name.replace(_arrayOfArraysRegexp, '[$1].[$2]');
		for (i = 0; i < nameChunks.length; i++)
		{
			currChunk = nameChunks[i];
			nameChunksNormalized.push(currChunk);
			nameMatches = currChunk.match(_lastIndexedArrayRegexp);
			if (nameMatches != null)
			{
				nameNormalized = nameChunksNormalized.join(delimiter);
				currIndex = nameNormalized.replace(_lastIndexedArrayRegexp, '$3');
				nameNormalized = nameNormalized.replace(_lastIndexedArrayRegexp, '$1');

				if (typeof (arrayIndexes[nameNormalized]) == 'undefined')
				{
					arrayIndexes[nameNormalized] = {
						lastIndex: -1,
						indexes: {}
					};
				}

				if (currIndex == '' || typeof arrayIndexes[nameNormalized].indexes[currIndex] == 'undefined')
				{
					arrayIndexes[nameNormalized].lastIndex++;
					arrayIndexes[nameNormalized].indexes[currIndex] = arrayIndexes[nameNormalized].lastIndex;
				}

				newIndex = arrayIndexes[nameNormalized].indexes[currIndex];
				nameChunksNormalized[nameChunksNormalized.length - 1] = currChunk.replace(_lastIndexedArrayRegexp, '$1$2' + newIndex + '$4');
			}
		}

		nameNormalized = nameChunksNormalized.join(delimiter);
		nameNormalized = nameNormalized.replace('].[', '][');
		return nameNormalized;
	}

	function object2array(obj, lvl)
	{
		var result = [], i, name;

		if (arguments.length == 1) lvl = 0;

		if (obj == null)
		{
			result = [{ name: "", value: null }];
		}
		else if (typeof obj == 'string' || typeof obj == 'number' || typeof obj == 'date' || typeof obj == 'boolean')
		{
			result = [
				{ name: "", value : obj }
			];
		}
		else if (obj instanceof Array)
		{
			for (i = 0; i < obj.length; i++)
			{
				name = "[" + i + "]";
				result = result.concat(getSubValues(obj[i], name, lvl + 1));
			}
		}
		else
		{
			for (i in obj)
			{
				name = i;
				result = result.concat(getSubValues(obj[i], name, lvl + 1));
			}
		}

		return result;
	}

	function getSubValues(subObj, name, lvl)
	{
		var itemName;
		var result = [], tempResult = object2array(subObj, lvl + 1), i, tempItem;

		for (i = 0; i < tempResult.length; i++)
		{
			itemName = name;
			if (_subArrayRegexp.test(tempResult[i].name))
			{
				itemName += tempResult[i].name;
			}
			else if (_subObjectRegexp.test(tempResult[i].name))
			{
				itemName += '.' + tempResult[i].name;
			}

			tempItem = { name: itemName, value: tempResult[i].value };
			result.push(tempItem);
		}

		return result;
	}

	return js2form;

})();
/*!
 * mustache.js - Logic-less {{mustache}} templates with JavaScript
 * http://github.com/janl/mustache.js
 */

/*global define: false*/

(function (global, factory) {
	if (typeof exports === "object" && exports) {
		factory(exports); // CommonJS
	} else if (typeof define === "function" && define.amd) {
		define(['exports'], factory); // AMD
	} else {
		factory(global.Mustache = {}); // <script>
	}
}(this, function (mustache) {

	var Object_toString = Object.prototype.toString;
	var isArray = Array.isArray || function (object) {
				return Object_toString.call(object) === '[object Array]';
			};

	function isFunction(object) {
		return typeof object === 'function';
	}

	function escapeRegExp(string) {
		return string.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g, "\\$&");
	}

	// Workaround for https://issues.apache.org/jira/browse/COUCHDB-577
	// See https://github.com/janl/mustache.js/issues/189
	var RegExp_test = RegExp.prototype.test;
	function testRegExp(re, string) {
		return RegExp_test.call(re, string);
	}

	var nonSpaceRe = /\S/;
	function isWhitespace(string) {
		return !testRegExp(nonSpaceRe, string);
	}

	var entityMap = {
		"&": "&amp;",
		"<": "&lt;",
		">": "&gt;",
		'"': '&quot;',
		"'": '&#39;',
		"/": '&#x2F;'
	};

	function escapeHtml(string) {
		return String(string).replace(/[&<>"'\/]/g, function (s) {
			return entityMap[s];
		});
	}

	var whiteRe = /\s*/;
	var spaceRe = /\s+/;
	var equalsRe = /\s*=/;
	var curlyRe = /\s*\}/;
	var tagRe = /#|\^|\/|>|\{|&|=|!/;

	/**
	 * Breaks up the given `template` string into a tree of tokens. If the `tags`
	 * argument is given here it must be an array with two string values: the
	 * opening and closing tags used in the template (e.g. [ "<%", "%>" ]). Of
	 * course, the default is to use mustaches (i.e. mustache.tags).
	 *
	 * A token is an array with at least 4 elements. The first element is the
	 * mustache symbol that was used inside the tag, e.g. "#" or "&". If the tag
	 * did not contain a symbol (i.e. {{myValue}}) this element is "name". For
	 * all text that appears outside a symbol this element is "text".
	 *
	 * The second element of a token is its "value". For mustache tags this is
	 * whatever else was inside the tag besides the opening symbol. For text tokens
	 * this is the text itself.
	 *
	 * The third and fourth elements of the token are the start and end indices,
	 * respectively, of the token in the original template.
	 *
	 * Tokens that are the root node of a subtree contain two more elements: 1) an
	 * array of tokens in the subtree and 2) the index in the original template at
	 * which the closing tag for that section begins.
	 */
	function parseTemplate(template, tags) {
		if (!template)
			return [];

		var sections = [];     // Stack to hold section tokens
		var tokens = [];       // Buffer to hold the tokens
		var spaces = [];       // Indices of whitespace tokens on the current line
		var hasTag = false;    // Is there a {{tag}} on the current line?
		var nonSpace = false;  // Is there a non-space char on the current line?

		// Strips all whitespace tokens array for the current line
		// if there was a {{#tag}} on it and otherwise only space.
		function stripSpace() {
			if (hasTag && !nonSpace) {
				while (spaces.length)
					delete tokens[spaces.pop()];
			} else {
				spaces = [];
			}

			hasTag = false;
			nonSpace = false;
		}

		var openingTagRe, closingTagRe, closingCurlyRe;
		function compileTags(tags) {
			if (typeof tags === 'string')
				tags = tags.split(spaceRe, 2);

			if (!isArray(tags) || tags.length !== 2)
				throw new Error('Invalid tags: ' + tags);

			openingTagRe = new RegExp(escapeRegExp(tags[0]) + '\\s*');
			closingTagRe = new RegExp('\\s*' + escapeRegExp(tags[1]));
			closingCurlyRe = new RegExp('\\s*' + escapeRegExp('}' + tags[1]));
		}

		compileTags(tags || mustache.tags);

		var scanner = new Scanner(template);

		var start, type, value, chr, token, openSection;
		while (!scanner.eos()) {
			start = scanner.pos;

			// Match any text between tags.
			value = scanner.scanUntil(openingTagRe);

			if (value) {
				for (var i = 0, valueLength = value.length; i < valueLength; ++i) {
					chr = value.charAt(i);

					if (isWhitespace(chr)) {
						spaces.push(tokens.length);
					} else {
						nonSpace = true;
					}

					tokens.push([ 'text', chr, start, start + 1 ]);
					start += 1;

					// Check for whitespace on the current line.
					if (chr === '\n')
						stripSpace();
				}
			}

			// Match the opening tag.
			if (!scanner.scan(openingTagRe))
				break;

			hasTag = true;

			// Get the tag type.
			type = scanner.scan(tagRe) || 'name';
			scanner.scan(whiteRe);

			// Get the tag value.
			if (type === '=') {
				value = scanner.scanUntil(equalsRe);
				scanner.scan(equalsRe);
				scanner.scanUntil(closingTagRe);
			} else if (type === '{') {
				value = scanner.scanUntil(closingCurlyRe);
				scanner.scan(curlyRe);
				scanner.scanUntil(closingTagRe);
				type = '&';
			} else {
				value = scanner.scanUntil(closingTagRe);
			}

			// Match the closing tag.
			if (!scanner.scan(closingTagRe))
				throw new Error('Unclosed tag at ' + scanner.pos);

			token = [ type, value, start, scanner.pos ];
			tokens.push(token);

			if (type === '#' || type === '^') {
				sections.push(token);
			} else if (type === '/') {
				// Check section nesting.
				openSection = sections.pop();

				if (!openSection)
					throw new Error('Unopened section "' + value + '" at ' + start);

				if (openSection[1] !== value)
					throw new Error('Unclosed section "' + openSection[1] + '" at ' + start);
			} else if (type === 'name' || type === '{' || type === '&') {
				nonSpace = true;
			} else if (type === '=') {
				// Set the tags for the next time around.
				compileTags(value);
			}
		}

		// Make sure there are no open sections when we're done.
		openSection = sections.pop();

		if (openSection)
			throw new Error('Unclosed section "' + openSection[1] + '" at ' + scanner.pos);

		return nestTokens(squashTokens(tokens));
	}

	/**
	 * Combines the values of consecutive text tokens in the given `tokens` array
	 * to a single token.
	 */
	function squashTokens(tokens) {
		var squashedTokens = [];

		var token, lastToken;
		for (var i = 0, numTokens = tokens.length; i < numTokens; ++i) {
			token = tokens[i];

			if (token) {
				if (token[0] === 'text' && lastToken && lastToken[0] === 'text') {
					lastToken[1] += token[1];
					lastToken[3] = token[3];
				} else {
					squashedTokens.push(token);
					lastToken = token;
				}
			}
		}

		return squashedTokens;
	}

	/**
	 * Forms the given array of `tokens` into a nested tree structure where
	 * tokens that represent a section have two additional items: 1) an array of
	 * all tokens that appear in that section and 2) the index in the original
	 * template that represents the end of that section.
	 */
	function nestTokens(tokens) {
		var nestedTokens = [];
		var collector = nestedTokens;
		var sections = [];

		var token, section;
		for (var i = 0, numTokens = tokens.length; i < numTokens; ++i) {
			token = tokens[i];

			switch (token[0]) {
				case '#':
				case '^':
					collector.push(token);
					sections.push(token);
					collector = token[4] = [];
					break;
				case '/':
					section = sections.pop();
					section[5] = token[2];
					collector = sections.length > 0 ? sections[sections.length - 1][4] : nestedTokens;
					break;
				default:
					collector.push(token);
			}
		}

		return nestedTokens;
	}

	/**
	 * A simple string scanner that is used by the template parser to find
	 * tokens in template strings.
	 */
	function Scanner(string) {
		this.string = string;
		this.tail = string;
		this.pos = 0;
	}

	/**
	 * Returns `true` if the tail is empty (end of string).
	 */
	Scanner.prototype.eos = function () {
		return this.tail === "";
	};

	/**
	 * Tries to match the given regular expression at the current position.
	 * Returns the matched text if it can match, the empty string otherwise.
	 */
	Scanner.prototype.scan = function (re) {
		var match = this.tail.match(re);

		if (!match || match.index !== 0)
			return '';

		var string = match[0];

		this.tail = this.tail.substring(string.length);
		this.pos += string.length;

		return string;
	};

	/**
	 * Skips all text until the given regular expression can be matched. Returns
	 * the skipped string, which is the entire tail if no match can be made.
	 */
	Scanner.prototype.scanUntil = function (re) {
		var index = this.tail.search(re), match;

		switch (index) {
			case -1:
				match = this.tail;
				this.tail = "";
				break;
			case 0:
				match = "";
				break;
			default:
				match = this.tail.substring(0, index);
				this.tail = this.tail.substring(index);
		}

		this.pos += match.length;

		return match;
	};

	/**
	 * Represents a rendering context by wrapping a view object and
	 * maintaining a reference to the parent context.
	 */
	function Context(view, parentContext) {
		this.view = view == null ? {} : view;
		this.cache = { '.': this.view };
		this.parent = parentContext;
	}

	/**
	 * Creates a new context using the given view with this context
	 * as the parent.
	 */
	Context.prototype.push = function (view) {
		return new Context(view, this);
	};

	/**
	 * Returns the value of the given name in this context, traversing
	 * up the context hierarchy if the value is absent in this context's view.
	 */
	Context.prototype.lookup = function (name) {
		var cache = this.cache;

		var value;
		if (name in cache) {
			value = cache[name];
		} else {
			var context = this, names, index;

			while (context) {
				if (name.indexOf('.') > 0) {
					value = context.view;
					names = name.split('.');
					index = 0;

					while (value != null && index < names.length)
						value = value[names[index++]];
				} else if (typeof context.view == 'object') {
					value = context.view[name];
				}

				if (value != null)
					break;

				context = context.parent;
			}

			cache[name] = value;
		}

		if (isFunction(value))
			value = value.call(this.view);

		return value;
	};

	/**
	 * A Writer knows how to take a stream of tokens and render them to a
	 * string, given a context. It also maintains a cache of templates to
	 * avoid the need to parse the same template twice.
	 */
	function Writer() {
		this.cache = {};
	}

	/**
	 * Clears all cached templates in this writer.
	 */
	Writer.prototype.clearCache = function () {
		this.cache = {};
	};

	/**
	 * Parses and caches the given `template` and returns the array of tokens
	 * that is generated from the parse.
	 */
	Writer.prototype.parse = function (template, tags) {
		var cache = this.cache;
		var tokens = cache[template];

		if (tokens == null)
			tokens = cache[template] = parseTemplate(template, tags);

		return tokens;
	};

	/**
	 * High-level method that is used to render the given `template` with
	 * the given `view`.
	 *
	 * The optional `partials` argument may be an object that contains the
	 * names and templates of partials that are used in the template. It may
	 * also be a function that is used to load partial templates on the fly
	 * that takes a single argument: the name of the partial.
	 */
	Writer.prototype.render = function (template, view, partials) {
		var tokens = this.parse(template);
		var context = (view instanceof Context) ? view : new Context(view);
		return this.renderTokens(tokens, context, partials, template);
	};

	/**
	 * Low-level method that renders the given array of `tokens` using
	 * the given `context` and `partials`.
	 *
	 * Note: The `originalTemplate` is only ever used to extract the portion
	 * of the original template that was contained in a higher-order section.
	 * If the template doesn't use higher-order sections, this argument may
	 * be omitted.
	 */
	Writer.prototype.renderTokens = function (tokens, context, partials, originalTemplate) {
		var buffer = '';

		// This function is used to render an arbitrary template
		// in the current context by higher-order sections.
		var self = this;
		function subRender(template) {
			return self.render(template, context, partials);
		}

		var token, value;
		for (var i = 0, numTokens = tokens.length; i < numTokens; ++i) {
			token = tokens[i];

			switch (token[0]) {
				case '#':
					value = context.lookup(token[1]);

					if (!value)
						continue;

					if (isArray(value)) {
						for (var j = 0, valueLength = value.length; j < valueLength; ++j) {
							buffer += this.renderTokens(token[4], context.push(value[j]), partials, originalTemplate);
						}
					} else if (typeof value === 'object' || typeof value === 'string') {
						buffer += this.renderTokens(token[4], context.push(value), partials, originalTemplate);
					} else if (isFunction(value)) {
						if (typeof originalTemplate !== 'string')
							throw new Error('Cannot use higher-order sections without the original template');

						// Extract the portion of the original template that the section contains.
						value = value.call(context.view, originalTemplate.slice(token[3], token[5]), subRender);

						if (value != null)
							buffer += value;
					} else {
						buffer += this.renderTokens(token[4], context, partials, originalTemplate);
					}

					break;
				case '^':
					value = context.lookup(token[1]);

					// Use JavaScript's definition of falsy. Include empty arrays.
					// See https://github.com/janl/mustache.js/issues/186
					if (!value || (isArray(value) && value.length === 0))
						buffer += this.renderTokens(token[4], context, partials, originalTemplate);

					break;
				case '>':
					if (!partials)
						continue;

					value = isFunction(partials) ? partials(token[1]) : partials[token[1]];

					if (value != null)
						buffer += this.renderTokens(this.parse(value), context, partials, value);

					break;
				case '&':
					value = context.lookup(token[1]);

					if (value != null)
						buffer += value;

					break;
				case 'name':
					value = context.lookup(token[1]);

					if (value != null)
						buffer += mustache.escape(value);

					break;
				case 'text':
					buffer += token[1];
					break;
			}
		}

		return buffer;
	};

	mustache.name = "mustache.js";
	mustache.version = "1.0.0";
	mustache.tags = [ "{{", "}}" ];

	// All high-level mustache.* functions use this writer.
	var defaultWriter = new Writer();

	/**
	 * Clears all cached templates in the default writer.
	 */
	mustache.clearCache = function () {
		return defaultWriter.clearCache();
	};

	/**
	 * Parses and caches the given template in the default writer and returns the
	 * array of tokens it contains. Doing this ahead of time avoids the need to
	 * parse templates on the fly as they are rendered.
	 */
	mustache.parse = function (template, tags) {
		return defaultWriter.parse(template, tags);
	};

	/**
	 * Renders the `template` with the given `view` and `partials` using the
	 * default writer.
	 */
	mustache.render = function (template, view, partials) {
		return defaultWriter.render(template, view, partials);
	};

	// This is here for backwards compatibility with 0.4.x.
	mustache.to_html = function (template, view, partials, send) {
		var result = mustache.render(template, view, partials);

		if (isFunction(send)) {
			send(result);
		} else {
			return result;
		}
	};

	// Export the escaping function so that the user may override it.
	// See https://github.com/janl/mustache.js/issues/244
	mustache.escape = escapeHtml;

	// Export these mainly for testing, but also for advanced usage.
	mustache.Scanner = Scanner;
	mustache.Context = Context;
	mustache.Writer = Writer;

}));

/*! picturefill - v3.0.2 - 2016-02-12
 * https://scottjehl.github.io/picturefill/
 * Copyright (c) 2016 https://github.com/scottjehl/picturefill/blob/master/Authors.txt; Licensed MIT
 */
/*! Gecko-Picture - v1.0
 * https://github.com/scottjehl/picturefill/tree/3.0/src/plugins/gecko-picture
 * Firefox's early picture implementation (prior to FF41) is static and does
 * not react to viewport changes. This tiny module fixes this.
 */
(function(window) {
	/*jshint eqnull:true */
	var ua = navigator.userAgent;

	if ( window.HTMLPictureElement && ((/ecko/).test(ua) && ua.match(/rv\:(\d+)/) && RegExp.$1 < 45) ) {
		addEventListener("resize", (function() {
			var timer;

			var dummySrc = document.createElement("source");

			var fixRespimg = function(img) {
				var source, sizes;
				var picture = img.parentNode;

				if (picture.nodeName.toUpperCase() === "PICTURE") {
					source = dummySrc.cloneNode();

					picture.insertBefore(source, picture.firstElementChild);
					setTimeout(function() {
						picture.removeChild(source);
					});
				} else if (!img._pfLastSize || img.offsetWidth > img._pfLastSize) {
					img._pfLastSize = img.offsetWidth;
					sizes = img.sizes;
					img.sizes += ",100vw";
					setTimeout(function() {
						img.sizes = sizes;
					});
				}
			};

			var findPictureImgs = function() {
				var i;
				var imgs = document.querySelectorAll("picture > img, img[srcset][sizes]");
				for (i = 0; i < imgs.length; i++) {
					fixRespimg(imgs[i]);
				}
			};
			var onResize = function() {
				clearTimeout(timer);
				timer = setTimeout(findPictureImgs, 99);
			};
			var mq = window.matchMedia && matchMedia("(orientation: landscape)");
			var init = function() {
				onResize();

				if (mq && mq.addListener) {
					mq.addListener(onResize);
				}
			};

			dummySrc.srcset = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";

			if (/^[c|i]|d$/.test(document.readyState || "")) {
				init();
			} else {
				document.addEventListener("DOMContentLoaded", init);
			}

			return onResize;
		})());
	}
})(window);

/*! Picturefill - v3.0.2
 * http://scottjehl.github.io/picturefill
 * Copyright (c) 2015 https://github.com/scottjehl/picturefill/blob/master/Authors.txt;
 *  License: MIT
 */

(function( window, document, undefined ) {
	// Enable strict mode
	"use strict";

	// HTML shim|v it for old IE (IE9 will still need the HTML video tag workaround)
	document.createElement( "picture" );

	var warn, eminpx, alwaysCheckWDescriptor, evalId;
	// local object for method references and testing exposure
	var pf = {};
	var isSupportTestReady = false;
	var noop = function() {};
	var image = document.createElement( "img" );
	var getImgAttr = image.getAttribute;
	var setImgAttr = image.setAttribute;
	var removeImgAttr = image.removeAttribute;
	var docElem = document.documentElement;
	var types = {};
	var cfg = {
		//resource selection:
		algorithm: ""
	};
	var srcAttr = "data-pfsrc";
	var srcsetAttr = srcAttr + "set";
	// ua sniffing is done for undetectable img loading features,
	// to do some non crucial perf optimizations
	var ua = navigator.userAgent;
	var supportAbort = (/rident/).test(ua) || ((/ecko/).test(ua) && ua.match(/rv\:(\d+)/) && RegExp.$1 > 35 );
	var curSrcProp = "currentSrc";
	var regWDesc = /\s+\+?\d+(e\d+)?w/;
	var regSize = /(\([^)]+\))?\s*(.+)/;
	var setOptions = window.picturefillCFG;
	/**
	 * Shortcut property for https://w3c.github.io/webappsec/specs/mixedcontent/#restricts-mixed-content ( for easy overriding in tests )
	 */
	// baseStyle also used by getEmValue (i.e.: width: 1em is important)
	var baseStyle = "position:absolute;left:0;visibility:hidden;display:block;padding:0;border:none;font-size:1em;width:1em;overflow:hidden;clip:rect(0px, 0px, 0px, 0px)";
	var fsCss = "font-size:100%!important;";
	var isVwDirty = true;

	var cssCache = {};
	var sizeLengthCache = {};
	var DPR = window.devicePixelRatio;
	var units = {
		px: 1,
		"in": 96
	};
	var anchor = document.createElement( "a" );
	/**
	 * alreadyRun flag used for setOptions. is it true setOptions will reevaluate
	 * @type {boolean}
	 */
	var alreadyRun = false;

	// Reusable, non-"g" Regexes

	// (Don't use \s, to avoid matching non-breaking space.)
	var regexLeadingSpaces = /^[ \t\n\r\u000c]+/,
	    regexLeadingCommasOrSpaces = /^[, \t\n\r\u000c]+/,
	    regexLeadingNotSpaces = /^[^ \t\n\r\u000c]+/,
	    regexTrailingCommas = /[,]+$/,
	    regexNonNegativeInteger = /^\d+$/,

	    // ( Positive or negative or unsigned integers or decimals, without or without exponents.
	    // Must include at least one digit.
	    // According to spec tests any decimal point must be followed by a digit.
	    // No leading plus sign is allowed.)
	    // https://html.spec.whatwg.org/multipage/infrastructure.html#valid-floating-point-number
	    regexFloatingPoint = /^-?(?:[0-9]+|[0-9]*\.[0-9]+)(?:[eE][+-]?[0-9]+)?$/;

	var on = function(obj, evt, fn, capture) {
		if ( obj.addEventListener ) {
			obj.addEventListener(evt, fn, capture || false);
		} else if ( obj.attachEvent ) {
			obj.attachEvent( "on" + evt, fn);
		}
	};

	/**
	 * simple memoize function:
	 */

	var memoize = function(fn) {
		var cache = {};
		return function(input) {
			if ( !(input in cache) ) {
				cache[ input ] = fn(input);
			}
			return cache[ input ];
		};
	};

	// UTILITY FUNCTIONS

	// Manual is faster than RegEx
	// http://jsperf.com/whitespace-character/5
	function isSpace(c) {
		return (c === "\u0020" || // space
		        c === "\u0009" || // horizontal tab
		        c === "\u000A" || // new line
		        c === "\u000C" || // form feed
		        c === "\u000D");  // carriage return
	}

	/**
	 * gets a mediaquery and returns a boolean or gets a css length and returns a number
	 * @param css mediaqueries or css length
	 * @returns {boolean|number}
	 *
	 * based on: https://gist.github.com/jonathantneal/db4f77009b155f083738
	 */
	var evalCSS = (function() {

		var regLength = /^([\d\.]+)(em|vw|px)$/;
		var replace = function() {
			var args = arguments, index = 0, string = args[0];
			while (++index in args) {
				string = string.replace(args[index], args[++index]);
			}
			return string;
		};

		var buildStr = memoize(function(css) {

			return "return " + replace((css || "").toLowerCase(),
				// interpret `and`
				/\band\b/g, "&&",

				// interpret `,`
				/,/g, "||",

				// interpret `min-` as >=
				/min-([a-z-\s]+):/g, "e.$1>=",

				// interpret `max-` as <=
				/max-([a-z-\s]+):/g, "e.$1<=",

				//calc value
				/calc([^)]+)/g, "($1)",

				// interpret css values
				/(\d+[\.]*[\d]*)([a-z]+)/g, "($1 * e.$2)",
				//make eval less evil
				/^(?!(e.[a-z]|[0-9\.&=|><\+\-\*\(\)\/])).*/ig, ""
			) + ";";
		});

		return function(css, length) {
			var parsedLength;
			if (!(css in cssCache)) {
				cssCache[css] = false;
				if (length && (parsedLength = css.match( regLength ))) {
					cssCache[css] = parsedLength[ 1 ] * units[parsedLength[ 2 ]];
				} else {
					/*jshint evil:true */
					try{
						cssCache[css] = new Function("e", buildStr(css))(units);
					} catch(e) {}
					/*jshint evil:false */
				}
			}
			return cssCache[css];
		};
	})();

	var setResolution = function( candidate, sizesattr ) {
		if ( candidate.w ) { // h = means height: || descriptor.type === 'h' do not handle yet...
			candidate.cWidth = pf.calcListLength( sizesattr || "100vw" );
			candidate.res = candidate.w / candidate.cWidth ;
		} else {
			candidate.res = candidate.d;
		}
		return candidate;
	};

	/**
	 *
	 * @param opt
	 */
	var picturefill = function( opt ) {

		if (!isSupportTestReady) {return;}

		var elements, i, plen;

		var options = opt || {};

		if ( options.elements && options.elements.nodeType === 1 ) {
			if ( options.elements.nodeName.toUpperCase() === "IMG" ) {
				options.elements =  [ options.elements ];
			} else {
				options.context = options.elements;
				options.elements =  null;
			}
		}

		elements = options.elements || pf.qsa( (options.context || document), ( options.reevaluate || options.reselect ) ? pf.sel : pf.selShort );

		if ( (plen = elements.length) ) {

			pf.setupRun( options );
			alreadyRun = true;

			// Loop through all elements
			for ( i = 0; i < plen; i++ ) {
				pf.fillImg(elements[ i ], options);
			}

			pf.teardownRun( options );
		}
	};

	/**
	 * outputs a warning for the developer
	 * @param {message}
	 * @type {Function}
	 */
	warn = ( window.console && console.warn ) ?
		function( message ) {
			console.warn( message );
		} :
		noop
	;

	if ( !(curSrcProp in image) ) {
		curSrcProp = "src";
	}

	// Add support for standard mime types.
	types[ "image/jpeg" ] = true;
	types[ "image/gif" ] = true;
	types[ "image/png" ] = true;

	function detectTypeSupport( type, typeUri ) {
		// based on Modernizr's lossless img-webp test
		// note: asynchronous
		var image = new window.Image();
		image.onerror = function() {
			types[ type ] = false;
			picturefill();
		};
		image.onload = function() {
			types[ type ] = image.width === 1;
			picturefill();
		};
		image.src = typeUri;
		return "pending";
	}

	// test svg support
	types[ "image/svg+xml" ] = document.implementation.hasFeature( "http://www.w3.org/TR/SVG11/feature#Image", "1.1" );

	/**
	 * updates the internal vW property with the current viewport width in px
	 */
	function updateMetrics() {

		isVwDirty = false;
		DPR = window.devicePixelRatio;
		cssCache = {};
		sizeLengthCache = {};

		pf.DPR = DPR || 1;

		units.width = Math.max(window.innerWidth || 0, docElem.clientWidth);
		units.height = Math.max(window.innerHeight || 0, docElem.clientHeight);

		units.vw = units.width / 100;
		units.vh = units.height / 100;

		evalId = [ units.height, units.width, DPR ].join("-");

		units.em = pf.getEmValue();
		units.rem = units.em;
	}

	function chooseLowRes( lowerValue, higherValue, dprValue, isCached ) {
		var bonusFactor, tooMuch, bonus, meanDensity;

		//experimental
		if (cfg.algorithm === "saveData" ){
			if ( lowerValue > 2.7 ) {
				meanDensity = dprValue + 1;
			} else {
				tooMuch = higherValue - dprValue;
				bonusFactor = Math.pow(lowerValue - 0.6, 1.5);

				bonus = tooMuch * bonusFactor;

				if (isCached) {
					bonus += 0.1 * bonusFactor;
				}

				meanDensity = lowerValue + bonus;
			}
		} else {
			meanDensity = (dprValue > 1) ?
				Math.sqrt(lowerValue * higherValue) :
				lowerValue;
		}

		return meanDensity > dprValue;
	}

	function applyBestCandidate( img ) {
		var srcSetCandidates;
		var matchingSet = pf.getSet( img );
		var evaluated = false;
		if ( matchingSet !== "pending" ) {
			evaluated = evalId;
			if ( matchingSet ) {
				srcSetCandidates = pf.setRes( matchingSet );
				pf.applySetCandidate( srcSetCandidates, img );
			}
		}
		img[ pf.ns ].evaled = evaluated;
	}

	function ascendingSort( a, b ) {
		return a.res - b.res;
	}

	function setSrcToCur( img, src, set ) {
		var candidate;
		if ( !set && src ) {
			set = img[ pf.ns ].sets;
			set = set && set[set.length - 1];
		}

		candidate = getCandidateForSrc(src, set);

		if ( candidate ) {
			src = pf.makeUrl(src);
			img[ pf.ns ].curSrc = src;
			img[ pf.ns ].curCan = candidate;

			if ( !candidate.res ) {
				setResolution( candidate, candidate.set.sizes );
			}
		}
		return candidate;
	}

	function getCandidateForSrc( src, set ) {
		var i, candidate, candidates;
		if ( src && set ) {
			candidates = pf.parseSet( set );
			src = pf.makeUrl(src);
			for ( i = 0; i < candidates.length; i++ ) {
				if ( src === pf.makeUrl(candidates[ i ].url) ) {
					candidate = candidates[ i ];
					break;
				}
			}
		}
		return candidate;
	}

	function getAllSourceElements( picture, candidates ) {
		var i, len, source, srcset;

		// SPEC mismatch intended for size and perf:
		// actually only source elements preceding the img should be used
		// also note: don't use qsa here, because IE8 sometimes doesn't like source as the key part in a selector
		var sources = picture.getElementsByTagName( "source" );

		for ( i = 0, len = sources.length; i < len; i++ ) {
			source = sources[ i ];
			source[ pf.ns ] = true;
			srcset = source.getAttribute( "srcset" );

			// if source does not have a srcset attribute, skip
			if ( srcset ) {
				candidates.push( {
					srcset: srcset,
					media: source.getAttribute( "media" ),
					type: source.getAttribute( "type" ),
					sizes: source.getAttribute( "sizes" )
				} );
			}
		}
	}

	/**
	 * Srcset Parser
	 * By Alex Bell |  MIT License
	 *
	 * @returns Array [{url: _, d: _, w: _, h:_, set:_(????)}, ...]
	 *
	 * Based super duper closely on the reference algorithm at:
	 * https://html.spec.whatwg.org/multipage/embedded-content.html#parse-a-srcset-attribute
	 */

	// 1. Let input be the value passed to this algorithm.
	// (TO-DO : Explain what "set" argument is here. Maybe choose a more
	// descriptive & more searchable name.  Since passing the "set" in really has
	// nothing to do with parsing proper, I would prefer this assignment eventually
	// go in an external fn.)
	function parseSrcset(input, set) {

		function collectCharacters(regEx) {
			var chars,
			    match = regEx.exec(input.substring(pos));
			if (match) {
				chars = match[ 0 ];
				pos += chars.length;
				return chars;
			}
		}

		var inputLength = input.length,
		    url,
		    descriptors,
		    currentDescriptor,
		    state,
		    c,

		    // 2. Let position be a pointer into input, initially pointing at the start
		    //    of the string.
		    pos = 0,

		    // 3. Let candidates be an initially empty source set.
		    candidates = [];

		/**
		* Adds descriptor properties to a candidate, pushes to the candidates array
		* @return undefined
		*/
		// (Declared outside of the while loop so that it's only created once.
		// (This fn is defined before it is used, in order to pass JSHINT.
		// Unfortunately this breaks the sequencing of the spec comments. :/ )
		function parseDescriptors() {

			// 9. Descriptor parser: Let error be no.
			var pError = false,

			// 10. Let width be absent.
			// 11. Let density be absent.
			// 12. Let future-compat-h be absent. (We're implementing it now as h)
			    w, d, h, i,
			    candidate = {},
			    desc, lastChar, value, intVal, floatVal;

			// 13. For each descriptor in descriptors, run the appropriate set of steps
			// from the following list:
			for (i = 0 ; i < descriptors.length; i++) {
				desc = descriptors[ i ];

				lastChar = desc[ desc.length - 1 ];
				value = desc.substring(0, desc.length - 1);
				intVal = parseInt(value, 10);
				floatVal = parseFloat(value);

				// If the descriptor consists of a valid non-negative integer followed by
				// a U+0077 LATIN SMALL LETTER W character
				if (regexNonNegativeInteger.test(value) && (lastChar === "w")) {

					// If width and density are not both absent, then let error be yes.
					if (w || d) {pError = true;}

					// Apply the rules for parsing non-negative integers to the descriptor.
					// If the result is zero, let error be yes.
					// Otherwise, let width be the result.
					if (intVal === 0) {pError = true;} else {w = intVal;}

				// If the descriptor consists of a valid floating-point number followed by
				// a U+0078 LATIN SMALL LETTER X character
				} else if (regexFloatingPoint.test(value) && (lastChar === "x")) {

					// If width, density and future-compat-h are not all absent, then let error
					// be yes.
					if (w || d || h) {pError = true;}

					// Apply the rules for parsing floating-point number values to the descriptor.
					// If the result is less than zero, let error be yes. Otherwise, let density
					// be the result.
					if (floatVal < 0) {pError = true;} else {d = floatVal;}

				// If the descriptor consists of a valid non-negative integer followed by
				// a U+0068 LATIN SMALL LETTER H character
				} else if (regexNonNegativeInteger.test(value) && (lastChar === "h")) {

					// If height and density are not both absent, then let error be yes.
					if (h || d) {pError = true;}

					// Apply the rules for parsing non-negative integers to the descriptor.
					// If the result is zero, let error be yes. Otherwise, let future-compat-h
					// be the result.
					if (intVal === 0) {pError = true;} else {h = intVal;}

				// Anything else, Let error be yes.
				} else {pError = true;}
			} // (close step 13 for loop)

			// 15. If error is still no, then append a new image source to candidates whose
			// URL is url, associated with a width width if not absent and a pixel
			// density density if not absent. Otherwise, there is a parse error.
			if (!pError) {
				candidate.url = url;

				if (w) { candidate.w = w;}
				if (d) { candidate.d = d;}
				if (h) { candidate.h = h;}
				if (!h && !d && !w) {candidate.d = 1;}
				if (candidate.d === 1) {set.has1x = true;}
				candidate.set = set;

				candidates.push(candidate);
			}
		} // (close parseDescriptors fn)

		/**
		* Tokenizes descriptor properties prior to parsing
		* Returns undefined.
		* (Again, this fn is defined before it is used, in order to pass JSHINT.
		* Unfortunately this breaks the logical sequencing of the spec comments. :/ )
		*/
		function tokenize() {

			// 8.1. Descriptor tokeniser: Skip whitespace
			collectCharacters(regexLeadingSpaces);

			// 8.2. Let current descriptor be the empty string.
			currentDescriptor = "";

			// 8.3. Let state be in descriptor.
			state = "in descriptor";

			while (true) {

				// 8.4. Let c be the character at position.
				c = input.charAt(pos);

				//  Do the following depending on the value of state.
				//  For the purpose of this step, "EOF" is a special character representing
				//  that position is past the end of input.

				// In descriptor
				if (state === "in descriptor") {
					// Do the following, depending on the value of c:

				  // Space character
				  // If current descriptor is not empty, append current descriptor to
				  // descriptors and let current descriptor be the empty string.
				  // Set state to after descriptor.
					if (isSpace(c)) {
						if (currentDescriptor) {
							descriptors.push(currentDescriptor);
							currentDescriptor = "";
							state = "after descriptor";
						}

					// U+002C COMMA (,)
					// Advance position to the next character in input. If current descriptor
					// is not empty, append current descriptor to descriptors. Jump to the step
					// labeled descriptor parser.
					} else if (c === ",") {
						pos += 1;
						if (currentDescriptor) {
							descriptors.push(currentDescriptor);
						}
						parseDescriptors();
						return;

					// U+0028 LEFT PARENTHESIS (()
					// Append c to current descriptor. Set state to in parens.
					} else if (c === "\u0028") {
						currentDescriptor = currentDescriptor + c;
						state = "in parens";

					// EOF
					// If current descriptor is not empty, append current descriptor to
					// descriptors. Jump to the step labeled descriptor parser.
					} else if (c === "") {
						if (currentDescriptor) {
							descriptors.push(currentDescriptor);
						}
						parseDescriptors();
						return;

					// Anything else
					// Append c to current descriptor.
					} else {
						currentDescriptor = currentDescriptor + c;
					}
				// (end "in descriptor"

				// In parens
				} else if (state === "in parens") {

					// U+0029 RIGHT PARENTHESIS ())
					// Append c to current descriptor. Set state to in descriptor.
					if (c === ")") {
						currentDescriptor = currentDescriptor + c;
						state = "in descriptor";

					// EOF
					// Append current descriptor to descriptors. Jump to the step labeled
					// descriptor parser.
					} else if (c === "") {
						descriptors.push(currentDescriptor);
						parseDescriptors();
						return;

					// Anything else
					// Append c to current descriptor.
					} else {
						currentDescriptor = currentDescriptor + c;
					}

				// After descriptor
				} else if (state === "after descriptor") {

					// Do the following, depending on the value of c:
					// Space character: Stay in this state.
					if (isSpace(c)) {

					// EOF: Jump to the step labeled descriptor parser.
					} else if (c === "") {
						parseDescriptors();
						return;

					// Anything else
					// Set state to in descriptor. Set position to the previous character in input.
					} else {
						state = "in descriptor";
						pos -= 1;

					}
				}

				// Advance position to the next character in input.
				pos += 1;

			// Repeat this step.
			} // (close while true loop)
		}

		// 4. Splitting loop: Collect a sequence of characters that are space
		//    characters or U+002C COMMA characters. If any U+002C COMMA characters
		//    were collected, that is a parse error.
		while (true) {
			collectCharacters(regexLeadingCommasOrSpaces);

			// 5. If position is past the end of input, return candidates and abort these steps.
			if (pos >= inputLength) {
				return candidates; // (we're done, this is the sole return path)
			}

			// 6. Collect a sequence of characters that are not space characters,
			//    and let that be url.
			url = collectCharacters(regexLeadingNotSpaces);

			// 7. Let descriptors be a new empty list.
			descriptors = [];

			// 8. If url ends with a U+002C COMMA character (,), follow these substeps:
			//		(1). Remove all trailing U+002C COMMA characters from url. If this removed
			//         more than one character, that is a parse error.
			if (url.slice(-1) === ",") {
				url = url.replace(regexTrailingCommas, "");
				// (Jump ahead to step 9 to skip tokenization and just push the candidate).
				parseDescriptors();

			//	Otherwise, follow these substeps:
			} else {
				tokenize();
			} // (close else of step 8)

		// 16. Return to the step labeled splitting loop.
		} // (Close of big while loop.)
	}

	/*
	 * Sizes Parser
	 *
	 * By Alex Bell |  MIT License
	 *
	 * Non-strict but accurate and lightweight JS Parser for the string value <img sizes="here">
	 *
	 * Reference algorithm at:
	 * https://html.spec.whatwg.org/multipage/embedded-content.html#parse-a-sizes-attribute
	 *
	 * Most comments are copied in directly from the spec
	 * (except for comments in parens).
	 *
	 * Grammar is:
	 * <source-size-list> = <source-size># [ , <source-size-value> ]? | <source-size-value>
	 * <source-size> = <media-condition> <source-size-value>
	 * <source-size-value> = <length>
	 * http://www.w3.org/html/wg/drafts/html/master/embedded-content.html#attr-img-sizes
	 *
	 * E.g. "(max-width: 30em) 100vw, (max-width: 50em) 70vw, 100vw"
	 * or "(min-width: 30em), calc(30vw - 15px)" or just "30vw"
	 *
	 * Returns the first valid <css-length> with a media condition that evaluates to true,
	 * or "100vw" if all valid media conditions evaluate to false.
	 *
	 */

	function parseSizes(strValue) {

		// (Percentage CSS lengths are not allowed in this case, to avoid confusion:
		// https://html.spec.whatwg.org/multipage/embedded-content.html#valid-source-size-list
		// CSS allows a single optional plus or minus sign:
		// http://www.w3.org/TR/CSS2/syndata.html#numbers
		// CSS is ASCII case-insensitive:
		// http://www.w3.org/TR/CSS2/syndata.html#characters )
		// Spec allows exponential notation for <number> type:
		// http://dev.w3.org/csswg/css-values/#numbers
		var regexCssLengthWithUnits = /^(?:[+-]?[0-9]+|[0-9]*\.[0-9]+)(?:[eE][+-]?[0-9]+)?(?:ch|cm|em|ex|in|mm|pc|pt|px|rem|vh|vmin|vmax|vw)$/i;

		// (This is a quick and lenient test. Because of optional unlimited-depth internal
		// grouping parens and strict spacing rules, this could get very complicated.)
		var regexCssCalc = /^calc\((?:[0-9a-z \.\+\-\*\/\(\)]+)\)$/i;

		var i;
		var unparsedSizesList;
		var unparsedSizesListLength;
		var unparsedSize;
		var lastComponentValue;
		var size;

		// UTILITY FUNCTIONS

		//  (Toy CSS parser. The goals here are:
		//  1) expansive test coverage without the weight of a full CSS parser.
		//  2) Avoiding regex wherever convenient.
		//  Quick tests: http://jsfiddle.net/gtntL4gr/3/
		//  Returns an array of arrays.)
		function parseComponentValues(str) {
			var chrctr;
			var component = "";
			var componentArray = [];
			var listArray = [];
			var parenDepth = 0;
			var pos = 0;
			var inComment = false;

			function pushComponent() {
				if (component) {
					componentArray.push(component);
					component = "";
				}
			}

			function pushComponentArray() {
				if (componentArray[0]) {
					listArray.push(componentArray);
					componentArray = [];
				}
			}

			// (Loop forwards from the beginning of the string.)
			while (true) {
				chrctr = str.charAt(pos);

				if (chrctr === "") { // ( End of string reached.)
					pushComponent();
					pushComponentArray();
					return listArray;
				} else if (inComment) {
					if ((chrctr === "*") && (str[pos + 1] === "/")) { // (At end of a comment.)
						inComment = false;
						pos += 2;
						pushComponent();
						continue;
					} else {
						pos += 1; // (Skip all characters inside comments.)
						continue;
					}
				} else if (isSpace(chrctr)) {
					// (If previous character in loop was also a space, or if
					// at the beginning of the string, do not add space char to
					// component.)
					if ( (str.charAt(pos - 1) && isSpace( str.charAt(pos - 1) ) ) || !component ) {
						pos += 1;
						continue;
					} else if (parenDepth === 0) {
						pushComponent();
						pos +=1;
						continue;
					} else {
						// (Replace any space character with a plain space for legibility.)
						chrctr = " ";
					}
				} else if (chrctr === "(") {
					parenDepth += 1;
				} else if (chrctr === ")") {
					parenDepth -= 1;
				} else if (chrctr === ",") {
					pushComponent();
					pushComponentArray();
					pos += 1;
					continue;
				} else if ( (chrctr === "/") && (str.charAt(pos + 1) === "*") ) {
					inComment = true;
					pos += 2;
					continue;
				}

				component = component + chrctr;
				pos += 1;
			}
		}

		function isValidNonNegativeSourceSizeValue(s) {
			if (regexCssLengthWithUnits.test(s) && (parseFloat(s) >= 0)) {return true;}
			if (regexCssCalc.test(s)) {return true;}
			// ( http://www.w3.org/TR/CSS2/syndata.html#numbers says:
			// "-0 is equivalent to 0 and is not a negative number." which means that
			// unitless zero and unitless negative zero must be accepted as special cases.)
			if ((s === "0") || (s === "-0") || (s === "+0")) {return true;}
			return false;
		}

		// When asked to parse a sizes attribute from an element, parse a
		// comma-separated list of component values from the value of the element's
		// sizes attribute (or the empty string, if the attribute is absent), and let
		// unparsed sizes list be the result.
		// http://dev.w3.org/csswg/css-syntax/#parse-comma-separated-list-of-component-values

		unparsedSizesList = parseComponentValues(strValue);
		unparsedSizesListLength = unparsedSizesList.length;

		// For each unparsed size in unparsed sizes list:
		for (i = 0; i < unparsedSizesListLength; i++) {
			unparsedSize = unparsedSizesList[i];

			// 1. Remove all consecutive <whitespace-token>s from the end of unparsed size.
			// ( parseComponentValues() already omits spaces outside of parens. )

			// If unparsed size is now empty, that is a parse error; continue to the next
			// iteration of this algorithm.
			// ( parseComponentValues() won't push an empty array. )

			// 2. If the last component value in unparsed size is a valid non-negative
			// <source-size-value>, let size be its value and remove the component value
			// from unparsed size. Any CSS function other than the calc() function is
			// invalid. Otherwise, there is a parse error; continue to the next iteration
			// of this algorithm.
			// http://dev.w3.org/csswg/css-syntax/#parse-component-value
			lastComponentValue = unparsedSize[unparsedSize.length - 1];

			if (isValidNonNegativeSourceSizeValue(lastComponentValue)) {
				size = lastComponentValue;
				unparsedSize.pop();
			} else {
				continue;
			}

			// 3. Remove all consecutive <whitespace-token>s from the end of unparsed
			// size. If unparsed size is now empty, return size and exit this algorithm.
			// If this was not the last item in unparsed sizes list, that is a parse error.
			if (unparsedSize.length === 0) {
				return size;
			}

			// 4. Parse the remaining component values in unparsed size as a
			// <media-condition>. If it does not parse correctly, or it does parse
			// correctly but the <media-condition> evaluates to false, continue to the
			// next iteration of this algorithm.
			// (Parsing all possible compound media conditions in JS is heavy, complicated,
			// and the payoff is unclear. Is there ever an situation where the
			// media condition parses incorrectly but still somehow evaluates to true?
			// Can we just rely on the browser/polyfill to do it?)
			unparsedSize = unparsedSize.join(" ");
			if (!(pf.matchesMedia( unparsedSize ) ) ) {
				continue;
			}

			// 5. Return size and exit this algorithm.
			return size;
		}

		// If the above algorithm exhausts unparsed sizes list without returning a
		// size value, return 100vw.
		return "100vw";
	}

	// namespace
	pf.ns = ("pf" + new Date().getTime()).substr(0, 9);

	// srcset support test
	pf.supSrcset = "srcset" in image;
	pf.supSizes = "sizes" in image;
	pf.supPicture = !!window.HTMLPictureElement;

	// UC browser does claim to support srcset and picture, but not sizes,
	// this extended test reveals the browser does support nothing
	if (pf.supSrcset && pf.supPicture && !pf.supSizes) {
		(function(image2) {
			image.srcset = "data:,a";
			image2.src = "data:,a";
			pf.supSrcset = image.complete === image2.complete;
			pf.supPicture = pf.supSrcset && pf.supPicture;
		})(document.createElement("img"));
	}

	// Safari9 has basic support for sizes, but does't expose the `sizes` idl attribute
	if (pf.supSrcset && !pf.supSizes) {

		(function() {
			var width2 = "data:image/gif;base64,R0lGODlhAgABAPAAAP///wAAACH5BAAAAAAALAAAAAACAAEAAAICBAoAOw==";
			var width1 = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";
			var img = document.createElement("img");
			var test = function() {
				var width = img.width;

				if (width === 2) {
					pf.supSizes = true;
				}

				alwaysCheckWDescriptor = pf.supSrcset && !pf.supSizes;

				isSupportTestReady = true;
				// force async
				setTimeout(picturefill);
			};

			img.onload = test;
			img.onerror = test;
			img.setAttribute("sizes", "9px");

			img.srcset = width1 + " 1w," + width2 + " 9w";
			img.src = width1;
		})();

	} else {
		isSupportTestReady = true;
	}

	// using pf.qsa instead of dom traversing does scale much better,
	// especially on sites mixing responsive and non-responsive images
	pf.selShort = "picture>img,img[srcset]";
	pf.sel = pf.selShort;
	pf.cfg = cfg;

	/**
	 * Shortcut property for `devicePixelRatio` ( for easy overriding in tests )
	 */
	pf.DPR = (DPR  || 1 );
	pf.u = units;

	// container of supported mime types that one might need to qualify before using
	pf.types =  types;

	pf.setSize = noop;

	/**
	 * Gets a string and returns the absolute URL
	 * @param src
	 * @returns {String} absolute URL
	 */

	pf.makeUrl = memoize(function(src) {
		anchor.href = src;
		return anchor.href;
	});

	/**
	 * Gets a DOM element or document and a selctor and returns the found matches
	 * Can be extended with jQuery/Sizzle for IE7 support
	 * @param context
	 * @param sel
	 * @returns {NodeList|Array}
	 */
	pf.qsa = function(context, sel) {
		return ( "querySelector" in context ) ? context.querySelectorAll(sel) : [];
	};

	/**
	 * Shortcut method for matchMedia ( for easy overriding in tests )
	 * wether native or pf.mMQ is used will be decided lazy on first call
	 * @returns {boolean}
	 */
	pf.matchesMedia = function() {
		if ( window.matchMedia && (matchMedia( "(min-width: 0.1em)" ) || {}).matches ) {
			pf.matchesMedia = function( media ) {
				return !media || ( matchMedia( media ).matches );
			};
		} else {
			pf.matchesMedia = pf.mMQ;
		}

		return pf.matchesMedia.apply( this, arguments );
	};

	/**
	 * A simplified matchMedia implementation for IE8 and IE9
	 * handles only min-width/max-width with px or em values
	 * @param media
	 * @returns {boolean}
	 */
	pf.mMQ = function( media ) {
		return media ? evalCSS(media) : true;
	};

	/**
	 * Returns the calculated length in css pixel from the given sourceSizeValue
	 * http://dev.w3.org/csswg/css-values-3/#length-value
	 * intended Spec mismatches:
	 * * Does not check for invalid use of CSS functions
	 * * Does handle a computed length of 0 the same as a negative and therefore invalid value
	 * @param sourceSizeValue
	 * @returns {Number}
	 */
	pf.calcLength = function( sourceSizeValue ) {

		var value = evalCSS(sourceSizeValue, true) || false;
		if (value < 0) {
			value = false;
		}

		return value;
	};

	/**
	 * Takes a type string and checks if its supported
	 */

	pf.supportsType = function( type ) {
		return ( type ) ? types[ type ] : true;
	};

	/**
	 * Parses a sourceSize into mediaCondition (media) and sourceSizeValue (length)
	 * @param sourceSizeStr
	 * @returns {*}
	 */
	pf.parseSize = memoize(function( sourceSizeStr ) {
		var match = ( sourceSizeStr || "" ).match(regSize);
		return {
			media: match && match[1],
			length: match && match[2]
		};
	});

	pf.parseSet = function( set ) {
		if ( !set.cands ) {
			set.cands = parseSrcset(set.srcset, set);
		}
		return set.cands;
	};

	/**
	 * returns 1em in css px for html/body default size
	 * function taken from respondjs
	 * @returns {*|number}
	 */
	pf.getEmValue = function() {
		var body;
		if ( !eminpx && (body = document.body) ) {
			var div = document.createElement( "div" ),
				originalHTMLCSS = docElem.style.cssText,
				originalBodyCSS = body.style.cssText;

			div.style.cssText = baseStyle;

			// 1em in a media query is the value of the default font size of the browser
			// reset docElem and body to ensure the correct value is returned
			docElem.style.cssText = fsCss;
			body.style.cssText = fsCss;

			body.appendChild( div );
			eminpx = div.offsetWidth;
			body.removeChild( div );

			//also update eminpx before returning
			eminpx = parseFloat( eminpx, 10 );

			// restore the original values
			docElem.style.cssText = originalHTMLCSS;
			body.style.cssText = originalBodyCSS;

		}
		return eminpx || 16;
	};

	/**
	 * Takes a string of sizes and returns the width in pixels as a number
	 */
	pf.calcListLength = function( sourceSizeListStr ) {
		// Split up source size list, ie ( max-width: 30em ) 100%, ( max-width: 50em ) 50%, 33%
		//
		//                           or (min-width:30em) calc(30% - 15px)
		if ( !(sourceSizeListStr in sizeLengthCache) || cfg.uT ) {
			var winningLength = pf.calcLength( parseSizes( sourceSizeListStr ) );

			sizeLengthCache[ sourceSizeListStr ] = !winningLength ? units.width : winningLength;
		}

		return sizeLengthCache[ sourceSizeListStr ];
	};

	/**
	 * Takes a candidate object with a srcset property in the form of url/
	 * ex. "images/pic-medium.png 1x, images/pic-medium-2x.png 2x" or
	 *     "images/pic-medium.png 400w, images/pic-medium-2x.png 800w" or
	 *     "images/pic-small.png"
	 * Get an array of image candidates in the form of
	 *      {url: "/foo/bar.png", resolution: 1}
	 * where resolution is http://dev.w3.org/csswg/css-values-3/#resolution-value
	 * If sizes is specified, res is calculated
	 */
	pf.setRes = function( set ) {
		var candidates;
		if ( set ) {

			candidates = pf.parseSet( set );

			for ( var i = 0, len = candidates.length; i < len; i++ ) {
				setResolution( candidates[ i ], set.sizes );
			}
		}
		return candidates;
	};

	pf.setRes.res = setResolution;

	pf.applySetCandidate = function( candidates, img ) {
		if ( !candidates.length ) {return;}
		var candidate,
			i,
			j,
			length,
			bestCandidate,
			curSrc,
			curCan,
			candidateSrc,
			abortCurSrc;

		var imageData = img[ pf.ns ];
		var dpr = pf.DPR;

		curSrc = imageData.curSrc || img[curSrcProp];

		curCan = imageData.curCan || setSrcToCur(img, curSrc, candidates[0].set);

		// if we have a current source, we might either become lazy or give this source some advantage
		if ( curCan && curCan.set === candidates[ 0 ].set ) {

			// if browser can abort image request and the image has a higher pixel density than needed
			// and this image isn't downloaded yet, we skip next part and try to save bandwidth
			abortCurSrc = (supportAbort && !img.complete && curCan.res - 0.1 > dpr);

			if ( !abortCurSrc ) {
				curCan.cached = true;

				// if current candidate is "best", "better" or "okay",
				// set it to bestCandidate
				if ( curCan.res >= dpr ) {
					bestCandidate = curCan;
				}
			}
		}

		if ( !bestCandidate ) {

			candidates.sort( ascendingSort );

			length = candidates.length;
			bestCandidate = candidates[ length - 1 ];

			for ( i = 0; i < length; i++ ) {
				candidate = candidates[ i ];
				if ( candidate.res >= dpr ) {
					j = i - 1;

					// we have found the perfect candidate,
					// but let's improve this a little bit with some assumptions ;-)
					if (candidates[ j ] &&
						(abortCurSrc || curSrc !== pf.makeUrl( candidate.url )) &&
						chooseLowRes(candidates[ j ].res, candidate.res, dpr, candidates[ j ].cached)) {

						bestCandidate = candidates[ j ];

					} else {
						bestCandidate = candidate;
					}
					break;
				}
			}
		}

		if ( bestCandidate ) {

			candidateSrc = pf.makeUrl( bestCandidate.url );

			imageData.curSrc = candidateSrc;
			imageData.curCan = bestCandidate;

			if ( candidateSrc !== curSrc ) {
				pf.setSrc( img, bestCandidate );
			}
			pf.setSize( img );
		}
	};

	pf.setSrc = function( img, bestCandidate ) {
		var origWidth;
		img.src = bestCandidate.url;

		// although this is a specific Safari issue, we don't want to take too much different code paths
		if ( bestCandidate.set.type === "image/svg+xml" ) {
			origWidth = img.style.width;
			img.style.width = (img.offsetWidth + 1) + "px";

			// next line only should trigger a repaint
			// if... is only done to trick dead code removal
			if ( img.offsetWidth + 1 ) {
				img.style.width = origWidth;
			}
		}
	};

	pf.getSet = function( img ) {
		var i, set, supportsType;
		var match = false;
		var sets = img [ pf.ns ].sets;

		for ( i = 0; i < sets.length && !match; i++ ) {
			set = sets[i];

			if ( !set.srcset || !pf.matchesMedia( set.media ) || !(supportsType = pf.supportsType( set.type )) ) {
				continue;
			}

			if ( supportsType === "pending" ) {
				set = supportsType;
			}

			match = set;
			break;
		}

		return match;
	};

	pf.parseSets = function( element, parent, options ) {
		var srcsetAttribute, imageSet, isWDescripor, srcsetParsed;

		var hasPicture = parent && parent.nodeName.toUpperCase() === "PICTURE";
		var imageData = element[ pf.ns ];

		if ( imageData.src === undefined || options.src ) {
			imageData.src = getImgAttr.call( element, "src" );
			if ( imageData.src ) {
				setImgAttr.call( element, srcAttr, imageData.src );
			} else {
				removeImgAttr.call( element, srcAttr );
			}
		}

		if ( imageData.srcset === undefined || options.srcset || !pf.supSrcset || element.srcset ) {
			srcsetAttribute = getImgAttr.call( element, "srcset" );
			imageData.srcset = srcsetAttribute;
			srcsetParsed = true;
		}

		imageData.sets = [];

		if ( hasPicture ) {
			imageData.pic = true;
			getAllSourceElements( parent, imageData.sets );
		}

		if ( imageData.srcset ) {
			imageSet = {
				srcset: imageData.srcset,
				sizes: getImgAttr.call( element, "sizes" )
			};

			imageData.sets.push( imageSet );

			isWDescripor = (alwaysCheckWDescriptor || imageData.src) && regWDesc.test(imageData.srcset || "");

			// add normal src as candidate, if source has no w descriptor
			if ( !isWDescripor && imageData.src && !getCandidateForSrc(imageData.src, imageSet) && !imageSet.has1x ) {
				imageSet.srcset += ", " + imageData.src;
				imageSet.cands.push({
					url: imageData.src,
					d: 1,
					set: imageSet
				});
			}

		} else if ( imageData.src ) {
			imageData.sets.push( {
				srcset: imageData.src,
				sizes: null
			} );
		}

		imageData.curCan = null;
		imageData.curSrc = undefined;

		// if img has picture or the srcset was removed or has a srcset and does not support srcset at all
		// or has a w descriptor (and does not support sizes) set support to false to evaluate
		imageData.supported = !( hasPicture || ( imageSet && !pf.supSrcset ) || (isWDescripor && !pf.supSizes) );

		if ( srcsetParsed && pf.supSrcset && !imageData.supported ) {
			if ( srcsetAttribute ) {
				setImgAttr.call( element, srcsetAttr, srcsetAttribute );
				element.srcset = "";
			} else {
				removeImgAttr.call( element, srcsetAttr );
			}
		}

		if (imageData.supported && !imageData.srcset && ((!imageData.src && element.src) ||  element.src !== pf.makeUrl(imageData.src))) {
			if (imageData.src === null) {
				element.removeAttribute("src");
			} else {
				element.src = imageData.src;
			}
		}

		imageData.parsed = true;
	};

	pf.fillImg = function(element, options) {
		var imageData;
		var extreme = options.reselect || options.reevaluate;

		// expando for caching data on the img
		if ( !element[ pf.ns ] ) {
			element[ pf.ns ] = {};
		}

		imageData = element[ pf.ns ];

		// if the element has already been evaluated, skip it
		// unless `options.reevaluate` is set to true ( this, for example,
		// is set to true when running `picturefill` on `resize` ).
		if ( !extreme && imageData.evaled === evalId ) {
			return;
		}

		if ( !imageData.parsed || options.reevaluate ) {
			pf.parseSets( element, element.parentNode, options );
		}

		if ( !imageData.supported ) {
			applyBestCandidate( element );
		} else {
			imageData.evaled = evalId;
		}
	};

	pf.setupRun = function() {
		if ( !alreadyRun || isVwDirty || (DPR !== window.devicePixelRatio) ) {
			updateMetrics();
		}
	};

	// If picture is supported, well, that's awesome.
	if ( pf.supPicture ) {
		picturefill = noop;
		pf.fillImg = noop;
	} else {

		 // Set up picture polyfill by polling the document
		(function() {
			var isDomReady;
			var regReady = window.attachEvent ? /d$|^c/ : /d$|^c|^i/;

			var run = function() {
				var readyState = document.readyState || "";

				timerId = setTimeout(run, readyState === "loading" ? 200 :  999);
				if ( document.body ) {
					pf.fillImgs();
					isDomReady = isDomReady || regReady.test(readyState);
					if ( isDomReady ) {
						clearTimeout( timerId );
					}

				}
			};

			var timerId = setTimeout(run, document.body ? 9 : 99);

			// Also attach picturefill on resize and readystatechange
			// http://modernjavascript.blogspot.com/2013/08/building-better-debounce.html
			var debounce = function(func, wait) {
				var timeout, timestamp;
				var later = function() {
					var last = (new Date()) - timestamp;

					if (last < wait) {
						timeout = setTimeout(later, wait - last);
					} else {
						timeout = null;
						func();
					}
				};

				return function() {
					timestamp = new Date();

					if (!timeout) {
						timeout = setTimeout(later, wait);
					}
				};
			};
			var lastClientWidth = docElem.clientHeight;
			var onResize = function() {
				isVwDirty = Math.max(window.innerWidth || 0, docElem.clientWidth) !== units.width || docElem.clientHeight !== lastClientWidth;
				lastClientWidth = docElem.clientHeight;
				if ( isVwDirty ) {
					pf.fillImgs();
				}
			};

			on( window, "resize", debounce(onResize, 99 ) );
			on( document, "readystatechange", run );
		})();
	}

	pf.picturefill = picturefill;
	//use this internally for easy monkey patching/performance testing
	pf.fillImgs = picturefill;
	pf.teardownRun = noop;

	/* expose methods for testing */
	picturefill._ = pf;

	window.picturefillCFG = {
		pf: pf,
		push: function(args) {
			var name = args.shift();
			if (typeof pf[name] === "function") {
				pf[name].apply(pf, args);
			} else {
				cfg[name] = args[0];
				if (alreadyRun) {
					pf.fillImgs( { reselect: true } );
				}
			}
		}
	};

	while (setOptions && setOptions.length) {
		window.picturefillCFG.push(setOptions.shift());
	}

	/* expose picturefill */
	window.picturefill = picturefill;

	/* expose picturefill */
	if ( typeof module === "object" && typeof module.exports === "object" ) {
		// CommonJS, just export
		module.exports = picturefill;
	} else if ( typeof define === "function" && define.amd ) {
		// AMD support
		define( "picturefill", function() { return picturefill; } );
	}

	// IE8 evals this sync, so it must be the last thing we do
	if ( !pf.supPicture ) {
		types[ "image/webp" ] = detectTypeSupport("image/webp", "data:image/webp;base64,UklGRkoAAABXRUJQVlA4WAoAAAAQAAAAAAAAAAAAQUxQSAwAAAABBxAR/Q9ERP8DAABWUDggGAAAADABAJ0BKgEAAQADADQlpAADcAD++/1QAA==" );
	}

} )( window, document );

/**
 The MIT License

 Copyright (c) 2010 Daniel Park (http://metaweb.com, http://postmessage.freebaseapps.com)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 **/

var NO_JQUERY = {};
(function(window, $, undefined) {

	if (!("console" in window)) {
		var c = window.console = {};
		c.log = c.warn = c.error = c.debug = function(){};
	}

	if ($ === NO_JQUERY) {
		// jQuery is optional
		$ = {
			fn: {},
			extend: function() {
				var a = arguments[0];
				for (var i=1,len=arguments.length; i<len; i++) {
					var b = arguments[i];
					for (var prop in b) {
						a[prop] = b[prop];
					}
				}
				return a;
			}
		};
	}

	$.fn.pm = function() {
		console.log("usage: \nto send:    $.pm(options)\nto receive: $.pm.bind(type, fn, [origin])");
		return this;
	};

	// send postmessage
	$.pm = window.pm = function(options) {
		pm.send(options);
	};

	// bind postmessage handler
	$.pm.bind = window.pm.bind = function(type, fn, origin, hash, async_reply) {
		pm.bind(type, fn, origin, hash, async_reply === true);
	};

	// unbind postmessage handler
	$.pm.unbind = window.pm.unbind = function(type, fn) {
		pm.unbind(type, fn);
	};

	// default postmessage origin on bind
	$.pm.origin = window.pm.origin = null;

	// default postmessage polling if using location hash to pass postmessages
	$.pm.poll = window.pm.poll = 200;

	var pm = {

		send: function(options) {
			var o = $.extend({}, pm.defaults, options),
					target = o.target;
			if (!o.target) {
				console.warn("postmessage target window required");
				return;
			}
			if (!o.type) {
				console.warn("postmessage type required");
				return;
			}
			var msg = {data:o.data, type:o.type};
			if (o.success) {
				msg.callback = pm._callback(o.success);
			}
			if (o.error) {
				msg.errback = pm._callback(o.error);
			}
			if (("postMessage" in target) && !o.hash) {
				pm._bind();
				target.postMessage(JSON.stringify(msg), o.origin || '*');
			}
			else {
				pm.hash._bind();
				pm.hash.send(o, msg);
			}
		},

		bind: function(type, fn, origin, hash, async_reply) {
			pm._replyBind ( type, fn, origin, hash, async_reply );
		},

		_replyBind: function(type, fn, origin, hash, isCallback) {
			if (("postMessage" in window) && !hash) {
				pm._bind();
			}
			else {
				pm.hash._bind();
			}
			var l = pm.data("listeners.postmessage");
			if (!l) {
				l = {};
				pm.data("listeners.postmessage", l);
			}
			var fns = l[type];
			if (!fns) {
				fns = [];
				l[type] = fns;
			}
			fns.push({fn:fn, callback: isCallback, origin:origin || $.pm.origin});
		},

		unbind: function(type, fn) {
			var l = pm.data("listeners.postmessage");
			if (l) {
				if (type) {
					if (fn) {
						// remove specific listener
						var fns = l[type];
						if (fns) {
							var m = [];
							for (var i=0,len=fns.length; i<len; i++) {
								var o = fns[i];
								if (o.fn !== fn) {
									m.push(o);
								}
							}
							l[type] = m;
						}
					}
					else {
						// remove all listeners by type
						delete l[type];
					}
				}
				else {
					// unbind all listeners of all type
					for (var i in l) {
						delete l[i];
					}
				}
			}
		},

		data: function(k, v) {
			if (v === undefined) {
				return pm._data[k];
			}
			pm._data[k] = v;
			return v;
		},

		_data: {},

		_CHARS: '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split(''),

		_random: function() {
			var r = [];
			for (var i=0; i<32; i++) {
				r[i] = pm._CHARS[0 | Math.random() * 32];
			};
			return r.join("");
		},

		_callback: function(fn) {
			var cbs = pm.data("callbacks.postmessage");
			if (!cbs) {
				cbs = {};
				pm.data("callbacks.postmessage", cbs);
			}
			var r = pm._random();
			cbs[r] = fn;
			return r;
		},

		_bind: function() {
			// are we already listening to message events on this w?
			if (!pm.data("listening.postmessage")) {
				if (window.addEventListener) {
					window.addEventListener("message", pm._dispatch, false);
				}
				else if (window.attachEvent) {
					window.attachEvent("onmessage", pm._dispatch);
				}
				pm.data("listening.postmessage", 1);
			}
		},

		_dispatch: function(e) {
			//console.log("$.pm.dispatch", e, this);
			try {
				var msg = JSON.parse(e.data);
			}
			catch (ex) {
				console.warn("postmessage data invalid json: ", ex);
				return;
			}
			if (!msg.type) {
				console.warn("postmessage message type required");
				return;
			}
			var cbs = pm.data("callbacks.postmessage") || {},
					cb = cbs[msg.type];
			if (cb) {
				cb(msg.data);
			}
			else {
				var l = pm.data("listeners.postmessage") || {};
				var fns = l[msg.type] || [];
				for (var i=0,len=fns.length; i<len; i++) {
					var o = fns[i];
					if (o.origin && o.origin !== '*' && e.origin !== o.origin) {
						console.warn("postmessage message origin mismatch", e.origin, o.origin);
						if (msg.errback) {
							// notify post message errback
							var error = {
								message: "postmessage origin mismatch",
								origin: [e.origin, o.origin]
							};
							pm.send({target:e.source, data:error, type:msg.errback});
						}
						continue;
					}

					function sendReply ( data ) {
						if (msg.callback) {
							pm.send({target:e.source, data:data, type:msg.callback});
						}
					}

					try {
						if ( o.callback ) {
							o.fn(msg.data, sendReply, e);
						} else {
							sendReply ( o.fn(msg.data, e) );
						}
					}
					catch (ex) {
						if (msg.errback) {
							// notify post message errback
							pm.send({target:e.source, data:ex, type:msg.errback});
						} else {
							throw ex;
						}
					}
				};
			}
		}
	};

	// location hash polling
	pm.hash = {

		send: function(options, msg) {
			//console.log("hash.send", target_window, options, msg);
			var target_window = options.target,
					target_url = options.url;
			if (!target_url) {
				console.warn("postmessage target window url is required");
				return;
			}
			target_url = pm.hash._url(target_url);
			var source_window,
					source_url = pm.hash._url(window.location.href);
			if (window == target_window.parent) {
				source_window = "parent";
			}
			else {
				try {
					for (var i=0,len=parent.frames.length; i<len; i++) {
						var f = parent.frames[i];
						if (f == window) {
							source_window = i;
							break;
						}
					};
				}
				catch(ex) {
					// Opera: security error trying to access parent.frames x-origin
					// juse use window.name
					source_window = window.name;
				}
			}
			if (source_window == null) {
				console.warn("postmessage windows must be direct parent/child windows and the child must be available through the parent window.frames list");
				return;
			}
			var hashmessage = {
				"x-requested-with": "postmessage",
				source: {
					name: source_window,
					url: source_url
				},
				postmessage: msg
			};
			var hash_id = "#x-postmessage-id=" + pm._random();
			target_window.location = target_url + hash_id + encodeURIComponent(JSON.stringify(hashmessage));
		},

		_regex: /^\#x\-postmessage\-id\=(\w{32})/,

		_regex_len: "#x-postmessage-id=".length + 32,

		_bind: function() {
			// are we already listening to message events on this w?
			if (!pm.data("polling.postmessage")) {
				setInterval(function() {
					var hash = "" + window.location.hash,
							m = pm.hash._regex.exec(hash);
					if (m) {
						var id = m[1];
						if (pm.hash._last !== id) {
							pm.hash._last = id;
							pm.hash._dispatch(hash.substring(pm.hash._regex_len));
						}
					}
				}, $.pm.poll || 200);
				pm.data("polling.postmessage", 1);
			}
		},

		_dispatch: function(hash) {
			if (!hash) {
				return;
			}
			try {
				hash = JSON.parse(decodeURIComponent(hash));
				if (!(hash['x-requested-with'] === 'postmessage' &&
						hash.source && hash.source.name != null && hash.source.url && hash.postmessage)) {
					// ignore since hash could've come from somewhere else
					return;
				}
			}
			catch (ex) {
				// ignore since hash could've come from somewhere else
				return;
			}
			var msg = hash.postmessage,
					cbs = pm.data("callbacks.postmessage") || {},
					cb = cbs[msg.type];
			if (cb) {
				cb(msg.data);
			}
			else {
				var source_window;
				if (hash.source.name === "parent") {
					source_window = window.parent;
				}
				else {
					source_window = window.frames[hash.source.name];
				}
				var l = pm.data("listeners.postmessage") || {};
				var fns = l[msg.type] || [];
				for (var i=0,len=fns.length; i<len; i++) {
					var o = fns[i];
					if (o.origin) {
						var origin = /https?\:\/\/[^\/]*/.exec(hash.source.url)[0];
						if (o.origin !== '*' && origin !== o.origin) {
							console.warn("postmessage message origin mismatch", origin, o.origin);
							if (msg.errback) {
								// notify post message errback
								var error = {
									message: "postmessage origin mismatch",
									origin: [origin, o.origin]
								};
								pm.send({target:source_window, data:error, type:msg.errback, hash:true, url:hash.source.url});
							}
							continue;
						}
					}

					function sendReply ( data ) {
						if (msg.callback) {
							pm.send({target:source_window, data:data, type:msg.callback, hash:true, url:hash.source.url});
						}
					}

					try {
						if ( o.callback ) {
							o.fn(msg.data, sendReply);
						} else {
							sendReply ( o.fn(msg.data) );
						}
					}
					catch (ex) {
						if (msg.errback) {
							// notify post message errback
							pm.send({target:source_window, data:ex, type:msg.errback, hash:true, url:hash.source.url});
						} else {
							throw ex;
						}
					}
				};
			}
		},

		_url: function(url) {
			// url minus hash part
			return (""+url).replace(/#.*$/, "");
		}

	};

	$.extend(pm, {
		defaults: {
			target: null,  /* target window (required) */
			url: null,     /* target window url (required if no window.postMessage or hash == true) */
			type: null,    /* message type (required) */
			data: null,    /* message data (required) */
			success: null, /* success callback (optional) */
			error: null,   /* error callback (optional) */
			origin: "*",   /* postmessage origin (optional) */
			hash: false    /* use location hash for message passing (optional) */
		}
	});

})(this, typeof jQuery === "undefined" ? NO_JQUERY : jQuery);

/*
     _ _      _       _
 ___| (_) ___| | __  (_)___
/ __| | |/ __| |/ /  | / __|
\__ \ | | (__|   < _ | \__ \
|___/_|_|\___|_|\_(_)/ |___/
                   |__/

 Version: 1.6.0
  Author: Ken Wheeler
 Website: http://kenwheeler.github.io
    Docs: http://kenwheeler.github.io/slick
    Repo: http://github.com/kenwheeler/slick
  Issues: http://github.com/kenwheeler/slick/issues

 */
/* global window, document, define, jQuery, setInterval, clearInterval */
(function(factory) {
    'use strict';
    if (typeof define === 'function' && define.amd) {
        define(['jquery'], factory);
    } else if (typeof exports !== 'undefined') {
        module.exports = factory(require('jquery'));
    } else {
        factory(jQuery);
    }

}(function($) {
    'use strict';
    var Slick = window.Slick || {};

    Slick = (function() {

        var instanceUid = 0;

        function Slick(element, settings) {

            var _ = this, dataSettings;

            _.defaults = {
                accessibility: true,
                adaptiveHeight: false,
                appendArrows: $(element),
                appendDots: $(element),
                arrows: true,
                asNavFor: null,
                prevArrow: '<button type="button" data-role="none" class="slick-prev" aria-label="Previous" tabindex="0" role="button">Previous</button>',
                nextArrow: '<button type="button" data-role="none" class="slick-next" aria-label="Next" tabindex="0" role="button">Next</button>',
                autoplay: false,
                autoplaySpeed: 3000,
                centerMode: false,
                centerPadding: '50px',
                cssEase: 'ease',
                customPaging: function(slider, i) {
                    return $('<button type="button" data-role="none" role="button" tabindex="0" />').text(i + 1);
                },
                dots: false,
                dotsClass: 'slick-dots',
                draggable: true,
                easing: 'linear',
                edgeFriction: 0.35,
                fade: false,
                focusOnSelect: false,
                infinite: true,
                initialSlide: 0,
                lazyLoad: 'ondemand',
                mobileFirst: false,
                pauseOnHover: true,
                pauseOnFocus: true,
                pauseOnDotsHover: false,
                respondTo: 'window',
                responsive: null,
                rows: 1,
                rtl: false,
                slide: '',
                slidesPerRow: 1,
                slidesToShow: 1,
                slidesToScroll: 1,
                speed: 500,
                swipe: true,
                swipeToSlide: false,
                touchMove: true,
                touchThreshold: 5,
                useCSS: true,
                useTransform: true,
                variableWidth: false,
                vertical: false,
                verticalSwiping: false,
                waitForAnimate: true,
                zIndex: 1000
            };

            _.initials = {
                animating: false,
                dragging: false,
                autoPlayTimer: null,
                currentDirection: 0,
                currentLeft: null,
                currentSlide: 0,
                direction: 1,
                $dots: null,
                listWidth: null,
                listHeight: null,
                loadIndex: 0,
                $nextArrow: null,
                $prevArrow: null,
                slideCount: null,
                slideWidth: null,
                $slideTrack: null,
                $slides: null,
                sliding: false,
                slideOffset: 0,
                swipeLeft: null,
                $list: null,
                touchObject: {},
                transformsEnabled: false,
                unslicked: false
            };

            $.extend(_, _.initials);

            _.activeBreakpoint = null;
            _.animType = null;
            _.animProp = null;
            _.breakpoints = [];
            _.breakpointSettings = [];
            _.cssTransitions = false;
            _.focussed = false;
            _.interrupted = false;
            _.hidden = 'hidden';
            _.paused = true;
            _.positionProp = null;
            _.respondTo = null;
            _.rowCount = 1;
            _.shouldClick = true;
            _.$slider = $(element);
            _.$slidesCache = null;
            _.transformType = null;
            _.transitionType = null;
            _.visibilityChange = 'visibilitychange';
            _.windowWidth = 0;
            _.windowTimer = null;

            dataSettings = $(element).data('slick') || {};

            _.options = $.extend({}, _.defaults, settings, dataSettings);

            _.currentSlide = _.options.initialSlide;

            _.originalSettings = _.options;

            if (typeof document.mozHidden !== 'undefined') {
                _.hidden = 'mozHidden';
                _.visibilityChange = 'mozvisibilitychange';
            } else if (typeof document.webkitHidden !== 'undefined') {
                _.hidden = 'webkitHidden';
                _.visibilityChange = 'webkitvisibilitychange';
            }

            _.autoPlay = $.proxy(_.autoPlay, _);
            _.autoPlayClear = $.proxy(_.autoPlayClear, _);
            _.autoPlayIterator = $.proxy(_.autoPlayIterator, _);
            _.changeSlide = $.proxy(_.changeSlide, _);
            _.clickHandler = $.proxy(_.clickHandler, _);
            _.selectHandler = $.proxy(_.selectHandler, _);
            _.setPosition = $.proxy(_.setPosition, _);
            _.swipeHandler = $.proxy(_.swipeHandler, _);
            _.dragHandler = $.proxy(_.dragHandler, _);
            _.keyHandler = $.proxy(_.keyHandler, _);

            _.instanceUid = instanceUid++;

            // A simple way to check for HTML strings
            // Strict HTML recognition (must start with <)
            // Extracted from jQuery v1.11 source
            _.htmlExpr = /^(?:\s*(<[\w\W]+>)[^>]*)$/;


            _.registerBreakpoints();
            _.init(true);

        }

        return Slick;

    }());

    Slick.prototype.activateADA = function() {
        var _ = this;

        _.$slideTrack.find('.slick-active').attr({
            'aria-hidden': 'false'
        }).find('a, input, button, select').attr({
            'tabindex': '0'
        });

    };

    Slick.prototype.addSlide = Slick.prototype.slickAdd = function(markup, index, addBefore) {

        var _ = this;

        if (typeof(index) === 'boolean') {
            addBefore = index;
            index = null;
        } else if (index < 0 || (index >= _.slideCount)) {
            return false;
        }

        _.unload();

        if (typeof(index) === 'number') {
            if (index === 0 && _.$slides.length === 0) {
                $(markup).appendTo(_.$slideTrack);
            } else if (addBefore) {
                $(markup).insertBefore(_.$slides.eq(index));
            } else {
                $(markup).insertAfter(_.$slides.eq(index));
            }
        } else {
            if (addBefore === true) {
                $(markup).prependTo(_.$slideTrack);
            } else {
                $(markup).appendTo(_.$slideTrack);
            }
        }

        _.$slides = _.$slideTrack.children(this.options.slide);

        _.$slideTrack.children(this.options.slide).detach();

        _.$slideTrack.append(_.$slides);

        _.$slides.each(function(index, element) {
            $(element).attr('data-slick-index', index);
        });

        _.$slidesCache = _.$slides;

        _.reinit();

    };

    Slick.prototype.animateHeight = function() {
        var _ = this;
        if (_.options.slidesToShow === 1 && _.options.adaptiveHeight === true && _.options.vertical === false) {
            var targetHeight = _.$slides.eq(_.currentSlide).outerHeight(true);
            _.$list.animate({
                height: targetHeight
            }, _.options.speed);
        }
    };

    Slick.prototype.animateSlide = function(targetLeft, callback) {

        var animProps = {},
            _ = this;

        _.animateHeight();

        if (_.options.rtl === true && _.options.vertical === false) {
            targetLeft = -targetLeft;
        }
        if (_.transformsEnabled === false) {
            if (_.options.vertical === false) {
                _.$slideTrack.animate({
                    left: targetLeft
                }, _.options.speed, _.options.easing, callback);
            } else {
                _.$slideTrack.animate({
                    top: targetLeft
                }, _.options.speed, _.options.easing, callback);
            }

        } else {

            if (_.cssTransitions === false) {
                if (_.options.rtl === true) {
                    _.currentLeft = -(_.currentLeft);
                }
                $({
                    animStart: _.currentLeft
                }).animate({
                    animStart: targetLeft
                }, {
                    duration: _.options.speed,
                    easing: _.options.easing,
                    step: function(now) {
                        now = Math.ceil(now);
                        if (_.options.vertical === false) {
                            animProps[_.animType] = 'translate(' +
                                now + 'px, 0px)';
                            _.$slideTrack.css(animProps);
                        } else {
                            animProps[_.animType] = 'translate(0px,' +
                                now + 'px)';
                            _.$slideTrack.css(animProps);
                        }
                    },
                    complete: function() {
                        if (callback) {
                            callback.call();
                        }
                    }
                });

            } else {

                _.applyTransition();
                targetLeft = Math.ceil(targetLeft);

                if (_.options.vertical === false) {
                    animProps[_.animType] = 'translate3d(' + targetLeft + 'px, 0px, 0px)';
                } else {
                    animProps[_.animType] = 'translate3d(0px,' + targetLeft + 'px, 0px)';
                }
                _.$slideTrack.css(animProps);

                if (callback) {
                    setTimeout(function() {

                        _.disableTransition();

                        callback.call();
                    }, _.options.speed);
                }

            }

        }

    };

    Slick.prototype.getNavTarget = function() {

        var _ = this,
            asNavFor = _.options.asNavFor;

        if ( asNavFor && asNavFor !== null ) {
            asNavFor = $(asNavFor).not(_.$slider);
        }

        return asNavFor;

    };

    Slick.prototype.asNavFor = function(index) {

        var _ = this,
            asNavFor = _.getNavTarget();

        if ( asNavFor !== null && typeof asNavFor === 'object' ) {
            asNavFor.each(function() {
                var target = $(this).slick('getSlick');
                if(!target.unslicked) {
                    target.slideHandler(index, true);
                }
            });
        }

    };

    Slick.prototype.applyTransition = function(slide) {

        var _ = this,
            transition = {};

        if (_.options.fade === false) {
            transition[_.transitionType] = _.transformType + ' ' + _.options.speed + 'ms ' + _.options.cssEase;
        } else {
            transition[_.transitionType] = 'opacity ' + _.options.speed + 'ms ' + _.options.cssEase;
        }

        if (_.options.fade === false) {
            _.$slideTrack.css(transition);
        } else {
            _.$slides.eq(slide).css(transition);
        }

    };

    Slick.prototype.autoPlay = function() {

        var _ = this;

        _.autoPlayClear();

        if ( _.slideCount > _.options.slidesToShow ) {
            _.autoPlayTimer = setInterval( _.autoPlayIterator, _.options.autoplaySpeed );
        }

    };

    Slick.prototype.autoPlayClear = function() {

        var _ = this;

        if (_.autoPlayTimer) {
            clearInterval(_.autoPlayTimer);
        }

    };

    Slick.prototype.autoPlayIterator = function() {

        var _ = this,
            slideTo = _.currentSlide + _.options.slidesToScroll;

        if ( !_.paused && !_.interrupted && !_.focussed ) {

            if ( _.options.infinite === false ) {

                if ( _.direction === 1 && ( _.currentSlide + 1 ) === ( _.slideCount - 1 )) {
                    _.direction = 0;
                }

                else if ( _.direction === 0 ) {

                    slideTo = _.currentSlide - _.options.slidesToScroll;

                    if ( _.currentSlide - 1 === 0 ) {
                        _.direction = 1;
                    }

                }

            }

            _.slideHandler( slideTo );

        }

    };

    Slick.prototype.buildArrows = function() {

        var _ = this;

        if (_.options.arrows === true ) {

            _.$prevArrow = $(_.options.prevArrow).addClass('slick-arrow');
            _.$nextArrow = $(_.options.nextArrow).addClass('slick-arrow');

            if( _.slideCount > _.options.slidesToShow ) {

                _.$prevArrow.removeClass('slick-hidden').removeAttr('aria-hidden tabindex');
                _.$nextArrow.removeClass('slick-hidden').removeAttr('aria-hidden tabindex');

                if (_.htmlExpr.test(_.options.prevArrow)) {
                    _.$prevArrow.prependTo(_.options.appendArrows);
                }

                if (_.htmlExpr.test(_.options.nextArrow)) {
                    _.$nextArrow.appendTo(_.options.appendArrows);
                }

                if (_.options.infinite !== true) {
                    _.$prevArrow
                        .addClass('slick-disabled')
                        .attr('aria-disabled', 'true');
                }

            } else {

                _.$prevArrow.add( _.$nextArrow )

                    .addClass('slick-hidden')
                    .attr({
                        'aria-disabled': 'true',
                        'tabindex': '-1'
                    });

            }

        }

    };

    Slick.prototype.buildDots = function() {

        var _ = this,
            i, dot;

        if (_.options.dots === true && _.slideCount > _.options.slidesToShow) {

            _.$slider.addClass('slick-dotted');

            dot = $('<ul />').addClass(_.options.dotsClass);

            for (i = 0; i <= _.getDotCount(); i += 1) {
                dot.append($('<li />').append(_.options.customPaging.call(this, _, i)));
            }

            _.$dots = dot.appendTo(_.options.appendDots);

            _.$dots.find('li').first().addClass('slick-active').attr('aria-hidden', 'false');

        }

    };

    Slick.prototype.buildOut = function() {

        var _ = this;

        _.$slides =
            _.$slider
                .children( _.options.slide + ':not(.slick-cloned)')
                .addClass('slick-slide');

        _.slideCount = _.$slides.length;

        _.$slides.each(function(index, element) {
            $(element)
                .attr('data-slick-index', index)
                .data('originalStyling', $(element).attr('style') || '');
        });

        _.$slider.addClass('slick-slider');

        _.$slideTrack = (_.slideCount === 0) ?
            $('<div class="slick-track"/>').appendTo(_.$slider) :
            _.$slides.wrapAll('<div class="slick-track"/>').parent();

        _.$list = _.$slideTrack.wrap(
            '<div aria-live="polite" class="slick-list"/>').parent();
        _.$slideTrack.css('opacity', 0);

        if (_.options.centerMode === true || _.options.swipeToSlide === true) {
            _.options.slidesToScroll = 1;
        }

        $('img[data-lazy]', _.$slider).not('[src]').addClass('slick-loading');

        _.setupInfinite();

        _.buildArrows();

        _.buildDots();

        _.updateDots();


        _.setSlideClasses(typeof _.currentSlide === 'number' ? _.currentSlide : 0);

        if (_.options.draggable === true) {
            _.$list.addClass('draggable');
        }

    };

    Slick.prototype.buildRows = function() {

        var _ = this, a, b, c, newSlides, numOfSlides, originalSlides,slidesPerSection;

        newSlides = document.createDocumentFragment();
        originalSlides = _.$slider.children();

        if(_.options.rows > 1) {

            slidesPerSection = _.options.slidesPerRow * _.options.rows;
            numOfSlides = Math.ceil(
                originalSlides.length / slidesPerSection
            );

            for(a = 0; a < numOfSlides; a++){
                var slide = document.createElement('div');
                for(b = 0; b < _.options.rows; b++) {
                    var row = document.createElement('div');
                    for(c = 0; c < _.options.slidesPerRow; c++) {
                        var target = (a * slidesPerSection + ((b * _.options.slidesPerRow) + c));
                        if (originalSlides.get(target)) {
                            row.appendChild(originalSlides.get(target));
                        }
                    }
                    slide.appendChild(row);
                }
                newSlides.appendChild(slide);
            }

            _.$slider.empty().append(newSlides);
            _.$slider.children().children().children()
                .css({
                    'width':(100 / _.options.slidesPerRow) + '%',
                    'display': 'inline-block'
                });

        }

    };

    Slick.prototype.checkResponsive = function(initial, forceUpdate) {

        var _ = this,
            breakpoint, targetBreakpoint, respondToWidth, triggerBreakpoint = false;
        var sliderWidth = _.$slider.width();
        var windowWidth = window.innerWidth || $(window).width();

        if (_.respondTo === 'window') {
            respondToWidth = windowWidth;
        } else if (_.respondTo === 'slider') {
            respondToWidth = sliderWidth;
        } else if (_.respondTo === 'min') {
            respondToWidth = Math.min(windowWidth, sliderWidth);
        }

        if ( _.options.responsive &&
            _.options.responsive.length &&
            _.options.responsive !== null) {

            targetBreakpoint = null;

            for (breakpoint in _.breakpoints) {
                if (_.breakpoints.hasOwnProperty(breakpoint)) {
                    if (_.originalSettings.mobileFirst === false) {
                        if (respondToWidth < _.breakpoints[breakpoint]) {
                            targetBreakpoint = _.breakpoints[breakpoint];
                        }
                    } else {
                        if (respondToWidth > _.breakpoints[breakpoint]) {
                            targetBreakpoint = _.breakpoints[breakpoint];
                        }
                    }
                }
            }

            if (targetBreakpoint !== null) {
                if (_.activeBreakpoint !== null) {
                    if (targetBreakpoint !== _.activeBreakpoint || forceUpdate) {
                        _.activeBreakpoint =
                            targetBreakpoint;
                        if (_.breakpointSettings[targetBreakpoint] === 'unslick') {
                            _.unslick(targetBreakpoint);
                        } else {
                            _.options = $.extend({}, _.originalSettings,
                                _.breakpointSettings[
                                    targetBreakpoint]);
                            if (initial === true) {
                                _.currentSlide = _.options.initialSlide;
                            }
                            _.refresh(initial);
                        }
                        triggerBreakpoint = targetBreakpoint;
                    }
                } else {
                    _.activeBreakpoint = targetBreakpoint;
                    if (_.breakpointSettings[targetBreakpoint] === 'unslick') {
                        _.unslick(targetBreakpoint);
                    } else {
                        _.options = $.extend({}, _.originalSettings,
                            _.breakpointSettings[
                                targetBreakpoint]);
                        if (initial === true) {
                            _.currentSlide = _.options.initialSlide;
                        }
                        _.refresh(initial);
                    }
                    triggerBreakpoint = targetBreakpoint;
                }
            } else {
                if (_.activeBreakpoint !== null) {
                    _.activeBreakpoint = null;
                    _.options = _.originalSettings;
                    if (initial === true) {
                        _.currentSlide = _.options.initialSlide;
                    }
                    _.refresh(initial);
                    triggerBreakpoint = targetBreakpoint;
                }
            }

            // only trigger breakpoints during an actual break. not on initialize.
            if( !initial && triggerBreakpoint !== false ) {
                _.$slider.trigger('breakpoint', [_, triggerBreakpoint]);
            }
        }

    };

    Slick.prototype.changeSlide = function(event, dontAnimate) {

        var _ = this,
            $target = $(event.currentTarget),
            indexOffset, slideOffset, unevenOffset;

        // If target is a link, prevent default action.
        if($target.is('a')) {
            event.preventDefault();
        }

        // If target is not the <li> element (ie: a child), find the <li>.
        if(!$target.is('li')) {
            $target = $target.closest('li');
        }

        unevenOffset = (_.slideCount % _.options.slidesToScroll !== 0);
        indexOffset = unevenOffset ? 0 : (_.slideCount - _.currentSlide) % _.options.slidesToScroll;

        switch (event.data.message) {

            case 'previous':
                slideOffset = indexOffset === 0 ? _.options.slidesToScroll : _.options.slidesToShow - indexOffset;
                if (_.slideCount > _.options.slidesToShow) {
                    _.slideHandler(_.currentSlide - slideOffset, false, dontAnimate);
                }
                break;

            case 'next':
                slideOffset = indexOffset === 0 ? _.options.slidesToScroll : indexOffset;
                if (_.slideCount > _.options.slidesToShow) {
                    _.slideHandler(_.currentSlide + slideOffset, false, dontAnimate);
                }
                break;

            case 'index':
                var index = event.data.index === 0 ? 0 :
                    event.data.index || $target.index() * _.options.slidesToScroll;

                _.slideHandler(_.checkNavigable(index), false, dontAnimate);
                $target.children().trigger('focus');
                break;

            default:
                return;
        }

    };

    Slick.prototype.checkNavigable = function(index) {

        var _ = this,
            navigables, prevNavigable;

        navigables = _.getNavigableIndexes();
        prevNavigable = 0;
        if (index > navigables[navigables.length - 1]) {
            index = navigables[navigables.length - 1];
        } else {
            for (var n in navigables) {
                if (index < navigables[n]) {
                    index = prevNavigable;
                    break;
                }
                prevNavigable = navigables[n];
            }
        }

        return index;
    };

    Slick.prototype.cleanUpEvents = function() {

        var _ = this;

        if (_.options.dots && _.$dots !== null) {

            $('li', _.$dots)
                .off('click.slick', _.changeSlide)
                .off('mouseenter.slick', $.proxy(_.interrupt, _, true))
                .off('mouseleave.slick', $.proxy(_.interrupt, _, false));

        }

        _.$slider.off('focus.slick blur.slick');

        if (_.options.arrows === true && _.slideCount > _.options.slidesToShow) {
            _.$prevArrow && _.$prevArrow.off('click.slick', _.changeSlide);
            _.$nextArrow && _.$nextArrow.off('click.slick', _.changeSlide);
        }

        _.$list.off('touchstart.slick mousedown.slick', _.swipeHandler);
        _.$list.off('touchmove.slick mousemove.slick', _.swipeHandler);
        _.$list.off('touchend.slick mouseup.slick', _.swipeHandler);
        _.$list.off('touchcancel.slick mouseleave.slick', _.swipeHandler);

        _.$list.off('click.slick', _.clickHandler);

        $(document).off(_.visibilityChange, _.visibility);

        _.cleanUpSlideEvents();

        if (_.options.accessibility === true) {
            _.$list.off('keydown.slick', _.keyHandler);
        }

        if (_.options.focusOnSelect === true) {
            $(_.$slideTrack).children().off('click.slick', _.selectHandler);
        }

        $(window).off('orientationchange.slick.slick-' + _.instanceUid, _.orientationChange);

        $(window).off('resize.slick.slick-' + _.instanceUid, _.resize);

        $('[draggable!=true]', _.$slideTrack).off('dragstart', _.preventDefault);

        $(window).off('load.slick.slick-' + _.instanceUid, _.setPosition);
        $(document).off('ready.slick.slick-' + _.instanceUid, _.setPosition);

    };

    Slick.prototype.cleanUpSlideEvents = function() {

        var _ = this;

        _.$list.off('mouseenter.slick', $.proxy(_.interrupt, _, true));
        _.$list.off('mouseleave.slick', $.proxy(_.interrupt, _, false));

    };

    Slick.prototype.cleanUpRows = function() {

        var _ = this, originalSlides;

        if(_.options.rows > 1) {
            originalSlides = _.$slides.children().children();
            originalSlides.removeAttr('style');
            _.$slider.empty().append(originalSlides);
        }

    };

    Slick.prototype.clickHandler = function(event) {

        var _ = this;

        if (_.shouldClick === false) {
            event.stopImmediatePropagation();
            event.stopPropagation();
            event.preventDefault();
        }

    };

    Slick.prototype.destroy = function(refresh) {

        var _ = this;

        _.autoPlayClear();

        _.touchObject = {};

        _.cleanUpEvents();

        $('.slick-cloned', _.$slider).detach();

        if (_.$dots) {
            _.$dots.remove();
        }


        if ( _.$prevArrow && _.$prevArrow.length ) {

            _.$prevArrow
                .removeClass('slick-disabled slick-arrow slick-hidden')
                .removeAttr('aria-hidden aria-disabled tabindex')
                .css('display','');

            if ( _.htmlExpr.test( _.options.prevArrow )) {
                _.$prevArrow.remove();
            }
        }

        if ( _.$nextArrow && _.$nextArrow.length ) {

            _.$nextArrow
                .removeClass('slick-disabled slick-arrow slick-hidden')
                .removeAttr('aria-hidden aria-disabled tabindex')
                .css('display','');

            if ( _.htmlExpr.test( _.options.nextArrow )) {
                _.$nextArrow.remove();
            }

        }


        if (_.$slides) {

            _.$slides
                .removeClass('slick-slide slick-active slick-center slick-visible slick-current')
                .removeAttr('aria-hidden')
                .removeAttr('data-slick-index')
                .each(function(){
                    $(this).attr('style', $(this).data('originalStyling'));
                });

            _.$slideTrack.children(this.options.slide).detach();

            _.$slideTrack.detach();

            _.$list.detach();

            _.$slider.append(_.$slides);
        }

        _.cleanUpRows();

        _.$slider.removeClass('slick-slider');
        _.$slider.removeClass('slick-initialized');
        _.$slider.removeClass('slick-dotted');

        _.unslicked = true;

        if(!refresh) {
            _.$slider.trigger('destroy', [_]);
        }

    };

    Slick.prototype.disableTransition = function(slide) {

        var _ = this,
            transition = {};

        transition[_.transitionType] = '';

        if (_.options.fade === false) {
            _.$slideTrack.css(transition);
        } else {
            _.$slides.eq(slide).css(transition);
        }

    };

    Slick.prototype.fadeSlide = function(slideIndex, callback) {

        var _ = this;

        if (_.cssTransitions === false) {

            _.$slides.eq(slideIndex).css({
                zIndex: _.options.zIndex
            });

            _.$slides.eq(slideIndex).animate({
                opacity: 1
            }, _.options.speed, _.options.easing, callback);

        } else {

            _.applyTransition(slideIndex);

            _.$slides.eq(slideIndex).css({
                opacity: 1,
                zIndex: _.options.zIndex
            });

            if (callback) {
                setTimeout(function() {

                    _.disableTransition(slideIndex);

                    callback.call();
                }, _.options.speed);
            }

        }

    };

    Slick.prototype.fadeSlideOut = function(slideIndex) {

        var _ = this;

        if (_.cssTransitions === false) {

            _.$slides.eq(slideIndex).animate({
                opacity: 0,
                zIndex: _.options.zIndex - 2
            }, _.options.speed, _.options.easing);

        } else {

            _.applyTransition(slideIndex);

            _.$slides.eq(slideIndex).css({
                opacity: 0,
                zIndex: _.options.zIndex - 2
            });

        }

    };

    Slick.prototype.filterSlides = Slick.prototype.slickFilter = function(filter) {

        var _ = this;

        if (filter !== null) {

            _.$slidesCache = _.$slides;

            _.unload();

            _.$slideTrack.children(this.options.slide).detach();

            _.$slidesCache.filter(filter).appendTo(_.$slideTrack);

            _.reinit();

        }

    };

    Slick.prototype.focusHandler = function() {

        var _ = this;

        _.$slider
            .off('focus.slick blur.slick')
            .on('focus.slick blur.slick',
                '*:not(.slick-arrow)', function(event) {

            event.stopImmediatePropagation();
            var $sf = $(this);

            setTimeout(function() {

                if( _.options.pauseOnFocus ) {
                    _.focussed = $sf.is(':focus');
                    _.autoPlay();
                }

            }, 0);

        });
    };

    Slick.prototype.getCurrent = Slick.prototype.slickCurrentSlide = function() {

        var _ = this;
        return _.currentSlide;

    };

    Slick.prototype.getDotCount = function() {

        var _ = this;

        var breakPoint = 0;
        var counter = 0;
        var pagerQty = 0;

        if (_.options.infinite === true) {
            while (breakPoint < _.slideCount) {
                ++pagerQty;
                breakPoint = counter + _.options.slidesToScroll;
                counter += _.options.slidesToScroll <= _.options.slidesToShow ? _.options.slidesToScroll : _.options.slidesToShow;
            }
        } else if (_.options.centerMode === true) {
            pagerQty = _.slideCount;
        } else if(!_.options.asNavFor) {
            pagerQty = 1 + Math.ceil((_.slideCount - _.options.slidesToShow) / _.options.slidesToScroll);
        }else {
            while (breakPoint < _.slideCount) {
                ++pagerQty;
                breakPoint = counter + _.options.slidesToScroll;
                counter += _.options.slidesToScroll <= _.options.slidesToShow ? _.options.slidesToScroll : _.options.slidesToShow;
            }
        }

        return pagerQty - 1;

    };

    Slick.prototype.getLeft = function(slideIndex) {

        var _ = this,
            targetLeft,
            verticalHeight,
            verticalOffset = 0,
            targetSlide;

        _.slideOffset = 0;
        verticalHeight = _.$slides.first().outerHeight(true);

        if (_.options.infinite === true) {
            if (_.slideCount > _.options.slidesToShow) {
                _.slideOffset = (_.slideWidth * _.options.slidesToShow) * -1;
                verticalOffset = (verticalHeight * _.options.slidesToShow) * -1;
            }
            if (_.slideCount % _.options.slidesToScroll !== 0) {
                if (slideIndex + _.options.slidesToScroll > _.slideCount && _.slideCount > _.options.slidesToShow) {
                    if (slideIndex > _.slideCount) {
                        _.slideOffset = ((_.options.slidesToShow - (slideIndex - _.slideCount)) * _.slideWidth) * -1;
                        verticalOffset = ((_.options.slidesToShow - (slideIndex - _.slideCount)) * verticalHeight) * -1;
                    } else {
                        _.slideOffset = ((_.slideCount % _.options.slidesToScroll) * _.slideWidth) * -1;
                        verticalOffset = ((_.slideCount % _.options.slidesToScroll) * verticalHeight) * -1;
                    }
                }
            }
        } else {
            if (slideIndex + _.options.slidesToShow > _.slideCount) {
                _.slideOffset = ((slideIndex + _.options.slidesToShow) - _.slideCount) * _.slideWidth;
                verticalOffset = ((slideIndex + _.options.slidesToShow) - _.slideCount) * verticalHeight;
            }
        }

        if (_.slideCount <= _.options.slidesToShow) {
            _.slideOffset = 0;
            verticalOffset = 0;
        }

        if (_.options.centerMode === true && _.options.infinite === true) {
            _.slideOffset += _.slideWidth * Math.floor(_.options.slidesToShow / 2) - _.slideWidth;
        } else if (_.options.centerMode === true) {
            _.slideOffset = 0;
            _.slideOffset += _.slideWidth * Math.floor(_.options.slidesToShow / 2);
        }

        if (_.options.vertical === false) {
            targetLeft = ((slideIndex * _.slideWidth) * -1) + _.slideOffset;
        } else {
            targetLeft = ((slideIndex * verticalHeight) * -1) + verticalOffset;
        }

        if (_.options.variableWidth === true) {

            if (_.slideCount <= _.options.slidesToShow || _.options.infinite === false) {
                targetSlide = _.$slideTrack.children('.slick-slide').eq(slideIndex);
            } else {
                targetSlide = _.$slideTrack.children('.slick-slide').eq(slideIndex + _.options.slidesToShow);
            }

            if (_.options.rtl === true) {
                if (targetSlide[0]) {
                    targetLeft = (_.$slideTrack.width() - targetSlide[0].offsetLeft - targetSlide.width()) * -1;
                } else {
                    targetLeft =  0;
                }
            } else {
                targetLeft = targetSlide[0] ? targetSlide[0].offsetLeft * -1 : 0;
            }

            if (_.options.centerMode === true) {
                if (_.slideCount <= _.options.slidesToShow || _.options.infinite === false) {
                    targetSlide = _.$slideTrack.children('.slick-slide').eq(slideIndex);
                } else {
                    targetSlide = _.$slideTrack.children('.slick-slide').eq(slideIndex + _.options.slidesToShow + 1);
                }

                if (_.options.rtl === true) {
                    if (targetSlide[0]) {
                        targetLeft = (_.$slideTrack.width() - targetSlide[0].offsetLeft - targetSlide.width()) * -1;
                    } else {
                        targetLeft =  0;
                    }
                } else {
                    targetLeft = targetSlide[0] ? targetSlide[0].offsetLeft * -1 : 0;
                }

                targetLeft += (_.$list.width() - targetSlide.outerWidth()) / 2;
            }
        }

        return targetLeft;

    };

    Slick.prototype.getOption = Slick.prototype.slickGetOption = function(option) {

        var _ = this;

        return _.options[option];

    };

    Slick.prototype.getNavigableIndexes = function() {

        var _ = this,
            breakPoint = 0,
            counter = 0,
            indexes = [],
            max;

        if (_.options.infinite === false) {
            max = _.slideCount;
        } else {
            breakPoint = _.options.slidesToScroll * -1;
            counter = _.options.slidesToScroll * -1;
            max = _.slideCount * 2;
        }

        while (breakPoint < max) {
            indexes.push(breakPoint);
            breakPoint = counter + _.options.slidesToScroll;
            counter += _.options.slidesToScroll <= _.options.slidesToShow ? _.options.slidesToScroll : _.options.slidesToShow;
        }

        return indexes;

    };

    Slick.prototype.getSlick = function() {

        return this;

    };

    Slick.prototype.getSlideCount = function() {

        var _ = this,
            slidesTraversed, swipedSlide, centerOffset;

        centerOffset = _.options.centerMode === true ? _.slideWidth * Math.floor(_.options.slidesToShow / 2) : 0;

        if (_.options.swipeToSlide === true) {
            _.$slideTrack.find('.slick-slide').each(function(index, slide) {
                if (slide.offsetLeft - centerOffset + ($(slide).outerWidth() / 2) > (_.swipeLeft * -1)) {
                    swipedSlide = slide;
                    return false;
                }
            });

            slidesTraversed = Math.abs($(swipedSlide).attr('data-slick-index') - _.currentSlide) || 1;

            return slidesTraversed;

        } else {
            return _.options.slidesToScroll;
        }

    };

    Slick.prototype.goTo = Slick.prototype.slickGoTo = function(slide, dontAnimate) {

        var _ = this;

        _.changeSlide({
            data: {
                message: 'index',
                index: parseInt(slide)
            }
        }, dontAnimate);

    };

    Slick.prototype.init = function(creation) {

        var _ = this;

        if (!$(_.$slider).hasClass('slick-initialized')) {

            $(_.$slider).addClass('slick-initialized');

            _.buildRows();
            _.buildOut();
            _.setProps();
            _.startLoad();
            _.loadSlider();
            _.initializeEvents();
            _.updateArrows();
            _.updateDots();
            _.checkResponsive(true);
            _.focusHandler();

        }

        if (creation) {
            _.$slider.trigger('init', [_]);
        }

        if (_.options.accessibility === true) {
            _.initADA();
        }

        if ( _.options.autoplay ) {

            _.paused = false;
            _.autoPlay();

        }

    };

    Slick.prototype.initADA = function() {
        var _ = this;
        _.$slides.add(_.$slideTrack.find('.slick-cloned')).attr({
            'aria-hidden': 'true',
            'tabindex': '-1'
        }).find('a, input, button, select').attr({
            'tabindex': '-1'
        });

        _.$slideTrack.attr('role', 'listbox');

        _.$slides.not(_.$slideTrack.find('.slick-cloned')).each(function(i) {
            $(this).attr({
                'role': 'option',
                'aria-describedby': 'slick-slide' + _.instanceUid + i + ''
            });
        });

        if (_.$dots !== null) {
            _.$dots.attr('role', 'tablist').find('li').each(function(i) {
                $(this).attr({
                    'role': 'presentation',
                    'aria-selected': 'false',
                    'aria-controls': 'navigation' + _.instanceUid + i + '',
                    'id': 'slick-slide' + _.instanceUid + i + ''
                });
            })
                .first().attr('aria-selected', 'true').end()
                .find('button').attr('role', 'button').end()
                .closest('div').attr('role', 'toolbar');
        }
        _.activateADA();

    };

    Slick.prototype.initArrowEvents = function() {

        var _ = this;

        if (_.options.arrows === true && _.slideCount > _.options.slidesToShow) {
            _.$prevArrow
               .off('click.slick')
               .on('click.slick', {
                    message: 'previous'
               }, _.changeSlide);
            _.$nextArrow
               .off('click.slick')
               .on('click.slick', {
                    message: 'next'
               }, _.changeSlide);
        }

    };

    Slick.prototype.initDotEvents = function() {

        var _ = this;

        if (_.options.dots === true && _.slideCount > _.options.slidesToShow) {
            $('li', _.$dots).on('click.slick', {
                message: 'index'
            }, _.changeSlide);
        }

        if ( _.options.dots === true && _.options.pauseOnDotsHover === true ) {

            $('li', _.$dots)
                .on('mouseenter.slick', $.proxy(_.interrupt, _, true))
                .on('mouseleave.slick', $.proxy(_.interrupt, _, false));

        }

    };

    Slick.prototype.initSlideEvents = function() {

        var _ = this;

        if ( _.options.pauseOnHover ) {

            _.$list.on('mouseenter.slick', $.proxy(_.interrupt, _, true));
            _.$list.on('mouseleave.slick', $.proxy(_.interrupt, _, false));

        }

    };

    Slick.prototype.initializeEvents = function() {

        var _ = this;

        _.initArrowEvents();

        _.initDotEvents();
        _.initSlideEvents();

        _.$list.on('touchstart.slick mousedown.slick', {
            action: 'start'
        }, _.swipeHandler);
        _.$list.on('touchmove.slick mousemove.slick', {
            action: 'move'
        }, _.swipeHandler);
        _.$list.on('touchend.slick mouseup.slick', {
            action: 'end'
        }, _.swipeHandler);
        _.$list.on('touchcancel.slick mouseleave.slick', {
            action: 'end'
        }, _.swipeHandler);

        _.$list.on('click.slick', _.clickHandler);

        $(document).on(_.visibilityChange, $.proxy(_.visibility, _));

        if (_.options.accessibility === true) {
            _.$list.on('keydown.slick', _.keyHandler);
        }

        if (_.options.focusOnSelect === true) {
            $(_.$slideTrack).children().on('click.slick', _.selectHandler);
        }

        $(window).on('orientationchange.slick.slick-' + _.instanceUid, $.proxy(_.orientationChange, _));

        $(window).on('resize.slick.slick-' + _.instanceUid, $.proxy(_.resize, _));

        $('[draggable!=true]', _.$slideTrack).on('dragstart', _.preventDefault);

        $(window).on('load.slick.slick-' + _.instanceUid, _.setPosition);
        $(document).on('ready.slick.slick-' + _.instanceUid, _.setPosition);

    };

    Slick.prototype.initUI = function() {

        var _ = this;

        if (_.options.arrows === true && _.slideCount > _.options.slidesToShow) {

            _.$prevArrow.show();
            _.$nextArrow.show();

        }

        if (_.options.dots === true && _.slideCount > _.options.slidesToShow) {

            _.$dots.show();

        }

    };

    Slick.prototype.keyHandler = function(event) {

        var _ = this;
         //Dont slide if the cursor is inside the form fields and arrow keys are pressed
        if(!event.target.tagName.match('TEXTAREA|INPUT|SELECT')) {
            if (event.keyCode === 37 && _.options.accessibility === true) {
                _.changeSlide({
                    data: {
                        message: _.options.rtl === true ? 'next' :  'previous'
                    }
                });
            } else if (event.keyCode === 39 && _.options.accessibility === true) {
                _.changeSlide({
                    data: {
                        message: _.options.rtl === true ? 'previous' : 'next'
                    }
                });
            }
        }

    };

    Slick.prototype.lazyLoad = function() {

        var _ = this,
            loadRange, cloneRange, rangeStart, rangeEnd;

        function loadImages(imagesScope) {

            $('img[data-lazy]', imagesScope).each(function() {

                var image = $(this),
                    imageSource = $(this).attr('data-lazy'),
                    imageToLoad = document.createElement('img');

                imageToLoad.onload = function() {

                    image
                        .animate({ opacity: 0 }, 100, function() {
                            image
                                .attr('src', imageSource)
                                .animate({ opacity: 1 }, 200, function() {
                                    image
                                        .removeAttr('data-lazy')
                                        .removeClass('slick-loading');
                                });
                            _.$slider.trigger('lazyLoaded', [_, image, imageSource]);
                        });

                };

                imageToLoad.onerror = function() {

                    image
                        .removeAttr( 'data-lazy' )
                        .removeClass( 'slick-loading' )
                        .addClass( 'slick-lazyload-error' );

                    _.$slider.trigger('lazyLoadError', [ _, image, imageSource ]);

                };

                imageToLoad.src = imageSource;

            });

        }

        if (_.options.centerMode === true) {
            if (_.options.infinite === true) {
                rangeStart = _.currentSlide + (_.options.slidesToShow / 2 + 1);
                rangeEnd = rangeStart + _.options.slidesToShow + 2;
            } else {
                rangeStart = Math.max(0, _.currentSlide - (_.options.slidesToShow / 2 + 1));
                rangeEnd = 2 + (_.options.slidesToShow / 2 + 1) + _.currentSlide;
            }
        } else {
            rangeStart = _.options.infinite ? _.options.slidesToShow + _.currentSlide : _.currentSlide;
            rangeEnd = Math.ceil(rangeStart + _.options.slidesToShow);
            if (_.options.fade === true) {
                if (rangeStart > 0) rangeStart--;
                if (rangeEnd <= _.slideCount) rangeEnd++;
            }
        }

        loadRange = _.$slider.find('.slick-slide').slice(rangeStart, rangeEnd);
        loadImages(loadRange);

        if (_.slideCount <= _.options.slidesToShow) {
            cloneRange = _.$slider.find('.slick-slide');
            loadImages(cloneRange);
        } else
        if (_.currentSlide >= _.slideCount - _.options.slidesToShow) {
            cloneRange = _.$slider.find('.slick-cloned').slice(0, _.options.slidesToShow);
            loadImages(cloneRange);
        } else if (_.currentSlide === 0) {
            cloneRange = _.$slider.find('.slick-cloned').slice(_.options.slidesToShow * -1);
            loadImages(cloneRange);
        }

    };

    Slick.prototype.loadSlider = function() {

        var _ = this;

        _.setPosition();

        _.$slideTrack.css({
            opacity: 1
        });

        _.$slider.removeClass('slick-loading');

        _.initUI();

        if (_.options.lazyLoad === 'progressive') {
            _.progressiveLazyLoad();
        }

    };

    Slick.prototype.next = Slick.prototype.slickNext = function() {

        var _ = this;

        _.changeSlide({
            data: {
                message: 'next'
            }
        });

    };

    Slick.prototype.orientationChange = function() {

        var _ = this;

        _.checkResponsive();
        _.setPosition();

    };

    Slick.prototype.pause = Slick.prototype.slickPause = function() {

        var _ = this;

        _.autoPlayClear();
        _.paused = true;

    };

    Slick.prototype.play = Slick.prototype.slickPlay = function() {

        var _ = this;

        _.autoPlay();
        _.options.autoplay = true;
        _.paused = false;
        _.focussed = false;
        _.interrupted = false;

    };

    Slick.prototype.postSlide = function(index) {

        var _ = this;

        if( !_.unslicked ) {

            _.$slider.trigger('afterChange', [_, index]);

            _.animating = false;

            _.setPosition();

            _.swipeLeft = null;

            if ( _.options.autoplay ) {
                _.autoPlay();
            }

            if (_.options.accessibility === true) {
                _.initADA();
            }

        }

    };

    Slick.prototype.prev = Slick.prototype.slickPrev = function() {

        var _ = this;

        _.changeSlide({
            data: {
                message: 'previous'
            }
        });

    };

    Slick.prototype.preventDefault = function(event) {

        event.preventDefault();

    };

    Slick.prototype.progressiveLazyLoad = function( tryCount ) {

        tryCount = tryCount || 1;

        var _ = this,
            $imgsToLoad = $( 'img[data-lazy]', _.$slider ),
            image,
            imageSource,
            imageToLoad;

        if ( $imgsToLoad.length ) {

            image = $imgsToLoad.first();
            imageSource = image.attr('data-lazy');
            imageToLoad = document.createElement('img');

            imageToLoad.onload = function() {

                image
                    .attr( 'src', imageSource )
                    .removeAttr('data-lazy')
                    .removeClass('slick-loading');

                if ( _.options.adaptiveHeight === true ) {
                    _.setPosition();
                }

                _.$slider.trigger('lazyLoaded', [ _, image, imageSource ]);
                _.progressiveLazyLoad();

            };

            imageToLoad.onerror = function() {

                if ( tryCount < 3 ) {

                    /**
                     * try to load the image 3 times,
                     * leave a slight delay so we don't get
                     * servers blocking the request.
                     */
                    setTimeout( function() {
                        _.progressiveLazyLoad( tryCount + 1 );
                    }, 500 );

                } else {

                    image
                        .removeAttr( 'data-lazy' )
                        .removeClass( 'slick-loading' )
                        .addClass( 'slick-lazyload-error' );

                    _.$slider.trigger('lazyLoadError', [ _, image, imageSource ]);

                    _.progressiveLazyLoad();

                }

            };

            imageToLoad.src = imageSource;

        } else {

            _.$slider.trigger('allImagesLoaded', [ _ ]);

        }

    };

    Slick.prototype.refresh = function( initializing ) {

        var _ = this, currentSlide, lastVisibleIndex;

        lastVisibleIndex = _.slideCount - _.options.slidesToShow;

        // in non-infinite sliders, we don't want to go past the
        // last visible index.
        if( !_.options.infinite && ( _.currentSlide > lastVisibleIndex )) {
            _.currentSlide = lastVisibleIndex;
        }

        // if less slides than to show, go to start.
        if ( _.slideCount <= _.options.slidesToShow ) {
            _.currentSlide = 0;

        }

        currentSlide = _.currentSlide;

        _.destroy(true);

        $.extend(_, _.initials, { currentSlide: currentSlide });

        _.init();

        if( !initializing ) {

            _.changeSlide({
                data: {
                    message: 'index',
                    index: currentSlide
                }
            }, false);

        }

    };

    Slick.prototype.registerBreakpoints = function() {

        var _ = this, breakpoint, currentBreakpoint, l,
            responsiveSettings = _.options.responsive || null;

        if ( $.type(responsiveSettings) === 'array' && responsiveSettings.length ) {

            _.respondTo = _.options.respondTo || 'window';

            for ( breakpoint in responsiveSettings ) {

                l = _.breakpoints.length-1;
                currentBreakpoint = responsiveSettings[breakpoint].breakpoint;

                if (responsiveSettings.hasOwnProperty(breakpoint)) {

                    // loop through the breakpoints and cut out any existing
                    // ones with the same breakpoint number, we don't want dupes.
                    while( l >= 0 ) {
                        if( _.breakpoints[l] && _.breakpoints[l] === currentBreakpoint ) {
                            _.breakpoints.splice(l,1);
                        }
                        l--;
                    }

                    _.breakpoints.push(currentBreakpoint);
                    _.breakpointSettings[currentBreakpoint] = responsiveSettings[breakpoint].settings;

                }

            }

            _.breakpoints.sort(function(a, b) {
                return ( _.options.mobileFirst ) ? a-b : b-a;
            });

        }

    };

    Slick.prototype.reinit = function() {

        var _ = this;

        _.$slides =
            _.$slideTrack
                .children(_.options.slide)
                .addClass('slick-slide');

        _.slideCount = _.$slides.length;

        if (_.currentSlide >= _.slideCount && _.currentSlide !== 0) {
            _.currentSlide = _.currentSlide - _.options.slidesToScroll;
        }

        if (_.slideCount <= _.options.slidesToShow) {
            _.currentSlide = 0;
        }

        _.registerBreakpoints();

        _.setProps();
        _.setupInfinite();
        _.buildArrows();
        _.updateArrows();
        _.initArrowEvents();
        _.buildDots();
        _.updateDots();
        _.initDotEvents();
        _.cleanUpSlideEvents();
        _.initSlideEvents();

        _.checkResponsive(false, true);

        if (_.options.focusOnSelect === true) {
            $(_.$slideTrack).children().on('click.slick', _.selectHandler);
        }

        _.setSlideClasses(typeof _.currentSlide === 'number' ? _.currentSlide : 0);

        _.setPosition();
        _.focusHandler();

        _.paused = !_.options.autoplay;
        _.autoPlay();

        _.$slider.trigger('reInit', [_]);

    };

    Slick.prototype.resize = function() {

        var _ = this;

        if ($(window).width() !== _.windowWidth) {
            clearTimeout(_.windowDelay);
            _.windowDelay = window.setTimeout(function() {
                _.windowWidth = $(window).width();
                _.checkResponsive();
                if( !_.unslicked ) { _.setPosition(); }
            }, 50);
        }
    };

    Slick.prototype.removeSlide = Slick.prototype.slickRemove = function(index, removeBefore, removeAll) {

        var _ = this;

        if (typeof(index) === 'boolean') {
            removeBefore = index;
            index = removeBefore === true ? 0 : _.slideCount - 1;
        } else {
            index = removeBefore === true ? --index : index;
        }

        if (_.slideCount < 1 || index < 0 || index > _.slideCount - 1) {
            return false;
        }

        _.unload();

        if (removeAll === true) {
            _.$slideTrack.children().remove();
        } else {
            _.$slideTrack.children(this.options.slide).eq(index).remove();
        }

        _.$slides = _.$slideTrack.children(this.options.slide);

        _.$slideTrack.children(this.options.slide).detach();

        _.$slideTrack.append(_.$slides);

        _.$slidesCache = _.$slides;

        _.reinit();

    };

    Slick.prototype.setCSS = function(position) {

        var _ = this,
            positionProps = {},
            x, y;

        if (_.options.rtl === true) {
            position = -position;
        }
        x = _.positionProp == 'left' ? Math.ceil(position) + 'px' : '0px';
        y = _.positionProp == 'top' ? Math.ceil(position) + 'px' : '0px';

        positionProps[_.positionProp] = position;

        if (_.transformsEnabled === false) {
            _.$slideTrack.css(positionProps);
        } else {
            positionProps = {};
            if (_.cssTransitions === false) {
                positionProps[_.animType] = 'translate(' + x + ', ' + y + ')';
                _.$slideTrack.css(positionProps);
            } else {
                positionProps[_.animType] = 'translate3d(' + x + ', ' + y + ', 0px)';
                _.$slideTrack.css(positionProps);
            }
        }

    };

    Slick.prototype.setDimensions = function() {

        var _ = this;

        if (_.options.vertical === false) {
            if (_.options.centerMode === true) {
                _.$list.css({
                    padding: ('0px ' + _.options.centerPadding)
                });
            }
        } else {
            _.$list.height(_.$slides.first().outerHeight(true) * _.options.slidesToShow);
            if (_.options.centerMode === true) {
                _.$list.css({
                    padding: (_.options.centerPadding + ' 0px')
                });
            }
        }

        _.listWidth = _.$list.width();
        _.listHeight = _.$list.height();


        if (_.options.vertical === false && _.options.variableWidth === false) {
            _.slideWidth = Math.ceil(_.listWidth / _.options.slidesToShow);
            _.$slideTrack.width(Math.ceil((_.slideWidth * _.$slideTrack.children('.slick-slide').length)));

        } else if (_.options.variableWidth === true) {
            _.$slideTrack.width(5000 * _.slideCount);
        } else {
            _.slideWidth = Math.ceil(_.listWidth);
            _.$slideTrack.height(Math.ceil((_.$slides.first().outerHeight(true) * _.$slideTrack.children('.slick-slide').length)));
        }

        var offset = _.$slides.first().outerWidth(true) - _.$slides.first().width();
        if (_.options.variableWidth === false) _.$slideTrack.children('.slick-slide').width(_.slideWidth - offset);

    };

    Slick.prototype.setFade = function() {

        var _ = this,
            targetLeft;

        _.$slides.each(function(index, element) {
            targetLeft = (_.slideWidth * index) * -1;
            if (_.options.rtl === true) {
                $(element).css({
                    position: 'relative',
                    right: targetLeft,
                    top: 0,
                    zIndex: _.options.zIndex - 2,
                    opacity: 0
                });
            } else {
                $(element).css({
                    position: 'relative',
                    left: targetLeft,
                    top: 0,
                    zIndex: _.options.zIndex - 2,
                    opacity: 0
                });
            }
        });

        _.$slides.eq(_.currentSlide).css({
            zIndex: _.options.zIndex - 1,
            opacity: 1
        });

    };

    Slick.prototype.setHeight = function() {

        var _ = this;

        if (_.options.slidesToShow === 1 && _.options.adaptiveHeight === true && _.options.vertical === false) {
            var targetHeight = _.$slides.eq(_.currentSlide).outerHeight(true);
            _.$list.css('height', targetHeight);
        }

    };

    Slick.prototype.setOption =
    Slick.prototype.slickSetOption = function() {

        /**
         * accepts arguments in format of:
         *
         *  - for changing a single option's value:
         *     .slick("setOption", option, value, refresh )
         *
         *  - for changing a set of responsive options:
         *     .slick("setOption", 'responsive', [{}, ...], refresh )
         *
         *  - for updating multiple values at once (not responsive)
         *     .slick("setOption", { 'option': value, ... }, refresh )
         */

        var _ = this, l, item, option, value, refresh = false, type;

        if( $.type( arguments[0] ) === 'object' ) {

            option =  arguments[0];
            refresh = arguments[1];
            type = 'multiple';

        } else if ( $.type( arguments[0] ) === 'string' ) {

            option =  arguments[0];
            value = arguments[1];
            refresh = arguments[2];

            if ( arguments[0] === 'responsive' && $.type( arguments[1] ) === 'array' ) {

                type = 'responsive';

            } else if ( typeof arguments[1] !== 'undefined' ) {

                type = 'single';

            }

        }

        if ( type === 'single' ) {

            _.options[option] = value;


        } else if ( type === 'multiple' ) {

            $.each( option , function( opt, val ) {

                _.options[opt] = val;

            });


        } else if ( type === 'responsive' ) {

            for ( item in value ) {

                if( $.type( _.options.responsive ) !== 'array' ) {

                    _.options.responsive = [ value[item] ];

                } else {

                    l = _.options.responsive.length-1;

                    // loop through the responsive object and splice out duplicates.
                    while( l >= 0 ) {

                        if( _.options.responsive[l].breakpoint === value[item].breakpoint ) {

                            _.options.responsive.splice(l,1);

                        }

                        l--;

                    }

                    _.options.responsive.push( value[item] );

                }

            }

        }

        if ( refresh ) {

            _.unload();
            _.reinit();

        }

    };

    Slick.prototype.setPosition = function() {

        var _ = this;

        _.setDimensions();

        _.setHeight();

        if (_.options.fade === false) {
            _.setCSS(_.getLeft(_.currentSlide));
        } else {
            _.setFade();
        }

        _.$slider.trigger('setPosition', [_]);

    };

    Slick.prototype.setProps = function() {

        var _ = this,
            bodyStyle = document.body.style;

        _.positionProp = _.options.vertical === true ? 'top' : 'left';

        if (_.positionProp === 'top') {
            _.$slider.addClass('slick-vertical');
        } else {
            _.$slider.removeClass('slick-vertical');
        }

        if (bodyStyle.WebkitTransition !== undefined ||
            bodyStyle.MozTransition !== undefined ||
            bodyStyle.msTransition !== undefined) {
            if (_.options.useCSS === true) {
                _.cssTransitions = true;
            }
        }

        if ( _.options.fade ) {
            if ( typeof _.options.zIndex === 'number' ) {
                if( _.options.zIndex < 3 ) {
                    _.options.zIndex = 3;
                }
            } else {
                _.options.zIndex = _.defaults.zIndex;
            }
        }

        if (bodyStyle.OTransform !== undefined) {
            _.animType = 'OTransform';
            _.transformType = '-o-transform';
            _.transitionType = 'OTransition';
            if (bodyStyle.perspectiveProperty === undefined && bodyStyle.webkitPerspective === undefined) _.animType = false;
        }
        if (bodyStyle.MozTransform !== undefined) {
            _.animType = 'MozTransform';
            _.transformType = '-moz-transform';
            _.transitionType = 'MozTransition';
            if (bodyStyle.perspectiveProperty === undefined && bodyStyle.MozPerspective === undefined) _.animType = false;
        }
        if (bodyStyle.webkitTransform !== undefined) {
            _.animType = 'webkitTransform';
            _.transformType = '-webkit-transform';
            _.transitionType = 'webkitTransition';
            if (bodyStyle.perspectiveProperty === undefined && bodyStyle.webkitPerspective === undefined) _.animType = false;
        }
        if (bodyStyle.msTransform !== undefined) {
            _.animType = 'msTransform';
            _.transformType = '-ms-transform';
            _.transitionType = 'msTransition';
            if (bodyStyle.msTransform === undefined) _.animType = false;
        }
        if (bodyStyle.transform !== undefined && _.animType !== false) {
            _.animType = 'transform';
            _.transformType = 'transform';
            _.transitionType = 'transition';
        }
        _.transformsEnabled = _.options.useTransform && (_.animType !== null && _.animType !== false);
    };


    Slick.prototype.setSlideClasses = function(index) {

        var _ = this,
            centerOffset, allSlides, indexOffset, remainder;

        allSlides = _.$slider
            .find('.slick-slide')
            .removeClass('slick-active slick-center slick-current')
            .attr('aria-hidden', 'true');

        _.$slides
            .eq(index)
            .addClass('slick-current');

        if (_.options.centerMode === true) {

            centerOffset = Math.floor(_.options.slidesToShow / 2);

            if (_.options.infinite === true) {

                if (index >= centerOffset && index <= (_.slideCount - 1) - centerOffset) {

                    _.$slides
                        .slice(index - centerOffset, index + centerOffset + 1)
                        .addClass('slick-active')
                        .attr('aria-hidden', 'false');

                } else {

                    indexOffset = _.options.slidesToShow + index;
                    allSlides
                        .slice(indexOffset - centerOffset + 1, indexOffset + centerOffset + 2)
                        .addClass('slick-active')
                        .attr('aria-hidden', 'false');

                }

                if (index === 0) {

                    allSlides
                        .eq(allSlides.length - 1 - _.options.slidesToShow)
                        .addClass('slick-center');

                } else if (index === _.slideCount - 1) {

                    allSlides
                        .eq(_.options.slidesToShow)
                        .addClass('slick-center');

                }

            }

            _.$slides
                .eq(index)
                .addClass('slick-center');

        } else {

            if (index >= 0 && index <= (_.slideCount - _.options.slidesToShow)) {

                _.$slides
                    .slice(index, index + _.options.slidesToShow)
                    .addClass('slick-active')
                    .attr('aria-hidden', 'false');

            } else if (allSlides.length <= _.options.slidesToShow) {

                allSlides
                    .addClass('slick-active')
                    .attr('aria-hidden', 'false');

            } else {

                remainder = _.slideCount % _.options.slidesToShow;
                indexOffset = _.options.infinite === true ? _.options.slidesToShow + index : index;

                if (_.options.slidesToShow == _.options.slidesToScroll && (_.slideCount - index) < _.options.slidesToShow) {

                    allSlides
                        .slice(indexOffset - (_.options.slidesToShow - remainder), indexOffset + remainder)
                        .addClass('slick-active')
                        .attr('aria-hidden', 'false');

                } else {

                    allSlides
                        .slice(indexOffset, indexOffset + _.options.slidesToShow)
                        .addClass('slick-active')
                        .attr('aria-hidden', 'false');

                }

            }

        }

        if (_.options.lazyLoad === 'ondemand') {
            _.lazyLoad();
        }

    };

    Slick.prototype.setupInfinite = function() {

        var _ = this,
            i, slideIndex, infiniteCount;

        if (_.options.fade === true) {
            _.options.centerMode = false;
        }

        if (_.options.infinite === true && _.options.fade === false) {

            slideIndex = null;

            if (_.slideCount > _.options.slidesToShow) {

                if (_.options.centerMode === true) {
                    infiniteCount = _.options.slidesToShow + 1;
                } else {
                    infiniteCount = _.options.slidesToShow;
                }

                for (i = _.slideCount; i > (_.slideCount -
                        infiniteCount); i -= 1) {
                    slideIndex = i - 1;
                    $(_.$slides[slideIndex]).clone(true).attr('id', '')
                        .attr('data-slick-index', slideIndex - _.slideCount)
                        .prependTo(_.$slideTrack).addClass('slick-cloned');
                }
                for (i = 0; i < infiniteCount; i += 1) {
                    slideIndex = i;
                    $(_.$slides[slideIndex]).clone(true).attr('id', '')
                        .attr('data-slick-index', slideIndex + _.slideCount)
                        .appendTo(_.$slideTrack).addClass('slick-cloned');
                }
                _.$slideTrack.find('.slick-cloned').find('[id]').each(function() {
                    $(this).attr('id', '');
                });

            }

        }

    };

    Slick.prototype.interrupt = function( toggle ) {

        var _ = this;

        if( !toggle ) {
            _.autoPlay();
        }
        _.interrupted = toggle;

    };

    Slick.prototype.selectHandler = function(event) {

        var _ = this;

        var targetElement =
            $(event.target).is('.slick-slide') ?
                $(event.target) :
                $(event.target).parents('.slick-slide');

        var index = parseInt(targetElement.attr('data-slick-index'));

        if (!index) index = 0;

        if (_.slideCount <= _.options.slidesToShow) {

            _.setSlideClasses(index);
            _.asNavFor(index);
            return;

        }

        _.slideHandler(index);

    };

    Slick.prototype.slideHandler = function(index, sync, dontAnimate) {

        var targetSlide, animSlide, oldSlide, slideLeft, targetLeft = null,
            _ = this, navTarget;

        sync = sync || false;

        if (_.animating === true && _.options.waitForAnimate === true) {
            return;
        }

        if (_.options.fade === true && _.currentSlide === index) {
            return;
        }

        if (_.slideCount <= _.options.slidesToShow) {
            return;
        }

        if (sync === false) {
            _.asNavFor(index);
        }

        targetSlide = index;
        targetLeft = _.getLeft(targetSlide);
        slideLeft = _.getLeft(_.currentSlide);

        _.currentLeft = _.swipeLeft === null ? slideLeft : _.swipeLeft;

        if (_.options.infinite === false && _.options.centerMode === false && (index < 0 || index > _.getDotCount() * _.options.slidesToScroll)) {
            if (_.options.fade === false) {
                targetSlide = _.currentSlide;
                if (dontAnimate !== true) {
                    _.animateSlide(slideLeft, function() {
                        _.postSlide(targetSlide);
                    });
                } else {
                    _.postSlide(targetSlide);
                }
            }
            return;
        } else if (_.options.infinite === false && _.options.centerMode === true && (index < 0 || index > (_.slideCount - _.options.slidesToScroll))) {
            if (_.options.fade === false) {
                targetSlide = _.currentSlide;
                if (dontAnimate !== true) {
                    _.animateSlide(slideLeft, function() {
                        _.postSlide(targetSlide);
                    });
                } else {
                    _.postSlide(targetSlide);
                }
            }
            return;
        }

        if ( _.options.autoplay ) {
            clearInterval(_.autoPlayTimer);
        }

        if (targetSlide < 0) {
            if (_.slideCount % _.options.slidesToScroll !== 0) {
                animSlide = _.slideCount - (_.slideCount % _.options.slidesToScroll);
            } else {
                animSlide = _.slideCount + targetSlide;
            }
        } else if (targetSlide >= _.slideCount) {
            if (_.slideCount % _.options.slidesToScroll !== 0) {
                animSlide = 0;
            } else {
                animSlide = targetSlide - _.slideCount;
            }
        } else {
            animSlide = targetSlide;
        }

        _.animating = true;

        _.$slider.trigger('beforeChange', [_, _.currentSlide, animSlide]);

        oldSlide = _.currentSlide;
        _.currentSlide = animSlide;

        _.setSlideClasses(_.currentSlide);

        if ( _.options.asNavFor ) {

            navTarget = _.getNavTarget();
            navTarget = navTarget.slick('getSlick');

            if ( navTarget.slideCount <= navTarget.options.slidesToShow ) {
                navTarget.setSlideClasses(_.currentSlide);
            }

        }

        _.updateDots();
        _.updateArrows();

        if (_.options.fade === true) {
            if (dontAnimate !== true) {

                _.fadeSlideOut(oldSlide);

                _.fadeSlide(animSlide, function() {
                    _.postSlide(animSlide);
                });

            } else {
                _.postSlide(animSlide);
            }
            _.animateHeight();
            return;
        }

        if (dontAnimate !== true) {
            _.animateSlide(targetLeft, function() {
                _.postSlide(animSlide);
            });
        } else {
            _.postSlide(animSlide);
        }

    };

    Slick.prototype.startLoad = function() {

        var _ = this;

        if (_.options.arrows === true && _.slideCount > _.options.slidesToShow) {

            _.$prevArrow.hide();
            _.$nextArrow.hide();

        }

        if (_.options.dots === true && _.slideCount > _.options.slidesToShow) {

            _.$dots.hide();

        }

        _.$slider.addClass('slick-loading');

    };

    Slick.prototype.swipeDirection = function() {

        var xDist, yDist, r, swipeAngle, _ = this;

        xDist = _.touchObject.startX - _.touchObject.curX;
        yDist = _.touchObject.startY - _.touchObject.curY;
        r = Math.atan2(yDist, xDist);

        swipeAngle = Math.round(r * 180 / Math.PI);
        if (swipeAngle < 0) {
            swipeAngle = 360 - Math.abs(swipeAngle);
        }

        if ((swipeAngle <= 45) && (swipeAngle >= 0)) {
            return (_.options.rtl === false ? 'left' : 'right');
        }
        if ((swipeAngle <= 360) && (swipeAngle >= 315)) {
            return (_.options.rtl === false ? 'left' : 'right');
        }
        if ((swipeAngle >= 135) && (swipeAngle <= 225)) {
            return (_.options.rtl === false ? 'right' : 'left');
        }
        if (_.options.verticalSwiping === true) {
            if ((swipeAngle >= 35) && (swipeAngle <= 135)) {
                return 'down';
            } else {
                return 'up';
            }
        }

        return 'vertical';

    };

    Slick.prototype.swipeEnd = function(event) {

        var _ = this,
            slideCount,
            direction;

        _.dragging = false;
        _.interrupted = false;
        _.shouldClick = ( _.touchObject.swipeLength > 10 ) ? false : true;

        if ( _.touchObject.curX === undefined ) {
            return false;
        }

        if ( _.touchObject.edgeHit === true ) {
            _.$slider.trigger('edge', [_, _.swipeDirection() ]);
        }

        if ( _.touchObject.swipeLength >= _.touchObject.minSwipe ) {

            direction = _.swipeDirection();

            switch ( direction ) {

                case 'left':
                case 'down':

                    slideCount =
                        _.options.swipeToSlide ?
                            _.checkNavigable( _.currentSlide + _.getSlideCount() ) :
                            _.currentSlide + _.getSlideCount();

                    _.currentDirection = 0;

                    break;

                case 'right':
                case 'up':

                    slideCount =
                        _.options.swipeToSlide ?
                            _.checkNavigable( _.currentSlide - _.getSlideCount() ) :
                            _.currentSlide - _.getSlideCount();

                    _.currentDirection = 1;

                    break;

                default:


            }

            if( direction != 'vertical' ) {

                _.slideHandler( slideCount );
                _.touchObject = {};
                _.$slider.trigger('swipe', [_, direction ]);

            }

        } else {

            if ( _.touchObject.startX !== _.touchObject.curX ) {

                _.slideHandler( _.currentSlide );
                _.touchObject = {};

            }

        }

    };

    Slick.prototype.swipeHandler = function(event) {

        var _ = this;

        if ((_.options.swipe === false) || ('ontouchend' in document && _.options.swipe === false)) {
            return;
        } else if (_.options.draggable === false && event.type.indexOf('mouse') !== -1) {
            return;
        }

        _.touchObject.fingerCount = event.originalEvent && event.originalEvent.touches !== undefined ?
            event.originalEvent.touches.length : 1;

        _.touchObject.minSwipe = _.listWidth / _.options
            .touchThreshold;

        if (_.options.verticalSwiping === true) {
            _.touchObject.minSwipe = _.listHeight / _.options
                .touchThreshold;
        }

        switch (event.data.action) {

            case 'start':
                _.swipeStart(event);
                break;

            case 'move':
                _.swipeMove(event);
                break;

            case 'end':
                _.swipeEnd(event);
                break;

        }

    };

    Slick.prototype.swipeMove = function(event) {

        var _ = this,
            edgeWasHit = false,
            curLeft, swipeDirection, swipeLength, positionOffset, touches;

        touches = event.originalEvent !== undefined ? event.originalEvent.touches : null;

        if (!_.dragging || touches && touches.length !== 1) {
            return false;
        }

        curLeft = _.getLeft(_.currentSlide);

        _.touchObject.curX = touches !== undefined ? touches[0].pageX : event.clientX;
        _.touchObject.curY = touches !== undefined ? touches[0].pageY : event.clientY;

        _.touchObject.swipeLength = Math.round(Math.sqrt(
            Math.pow(_.touchObject.curX - _.touchObject.startX, 2)));

        if (_.options.verticalSwiping === true) {
            _.touchObject.swipeLength = Math.round(Math.sqrt(
                Math.pow(_.touchObject.curY - _.touchObject.startY, 2)));
        }

        swipeDirection = _.swipeDirection();

        if (swipeDirection === 'vertical') {
            return;
        }

        if (event.originalEvent !== undefined && _.touchObject.swipeLength > 4) {
            event.preventDefault();
        }

        positionOffset = (_.options.rtl === false ? 1 : -1) * (_.touchObject.curX > _.touchObject.startX ? 1 : -1);
        if (_.options.verticalSwiping === true) {
            positionOffset = _.touchObject.curY > _.touchObject.startY ? 1 : -1;
        }


        swipeLength = _.touchObject.swipeLength;

        _.touchObject.edgeHit = false;

        if (_.options.infinite === false) {
            if ((_.currentSlide === 0 && swipeDirection === 'right') || (_.currentSlide >= _.getDotCount() && swipeDirection === 'left')) {
                swipeLength = _.touchObject.swipeLength * _.options.edgeFriction;
                _.touchObject.edgeHit = true;
            }
        }

        if (_.options.vertical === false) {
            _.swipeLeft = curLeft + swipeLength * positionOffset;
        } else {
            _.swipeLeft = curLeft + (swipeLength * (_.$list.height() / _.listWidth)) * positionOffset;
        }
        if (_.options.verticalSwiping === true) {
            _.swipeLeft = curLeft + swipeLength * positionOffset;
        }

        if (_.options.fade === true || _.options.touchMove === false) {
            return false;
        }

        if (_.animating === true) {
            _.swipeLeft = null;
            return false;
        }

        _.setCSS(_.swipeLeft);

    };

    Slick.prototype.swipeStart = function(event) {

        var _ = this,
            touches;

        _.interrupted = true;

        if (_.touchObject.fingerCount !== 1 || _.slideCount <= _.options.slidesToShow) {
            _.touchObject = {};
            return false;
        }

        if (event.originalEvent !== undefined && event.originalEvent.touches !== undefined) {
            touches = event.originalEvent.touches[0];
        }

        _.touchObject.startX = _.touchObject.curX = touches !== undefined ? touches.pageX : event.clientX;
        _.touchObject.startY = _.touchObject.curY = touches !== undefined ? touches.pageY : event.clientY;

        _.dragging = true;

    };

    Slick.prototype.unfilterSlides = Slick.prototype.slickUnfilter = function() {

        var _ = this;

        if (_.$slidesCache !== null) {

            _.unload();

            _.$slideTrack.children(this.options.slide).detach();

            _.$slidesCache.appendTo(_.$slideTrack);

            _.reinit();

        }

    };

    Slick.prototype.unload = function() {

        var _ = this;

        $('.slick-cloned', _.$slider).remove();

        if (_.$dots) {
            _.$dots.remove();
        }

        if (_.$prevArrow && _.htmlExpr.test(_.options.prevArrow)) {
            _.$prevArrow.remove();
        }

        if (_.$nextArrow && _.htmlExpr.test(_.options.nextArrow)) {
            _.$nextArrow.remove();
        }

        _.$slides
            .removeClass('slick-slide slick-active slick-visible slick-current')
            .attr('aria-hidden', 'true')
            .css('width', '');

    };

    Slick.prototype.unslick = function(fromBreakpoint) {

        var _ = this;
        _.$slider.trigger('unslick', [_, fromBreakpoint]);
        _.destroy();

    };

    Slick.prototype.updateArrows = function() {

        var _ = this,
            centerOffset;

        centerOffset = Math.floor(_.options.slidesToShow / 2);

        if ( _.options.arrows === true &&
            _.slideCount > _.options.slidesToShow &&
            !_.options.infinite ) {

            _.$prevArrow.removeClass('slick-disabled').attr('aria-disabled', 'false');
            _.$nextArrow.removeClass('slick-disabled').attr('aria-disabled', 'false');

            if (_.currentSlide === 0) {

                _.$prevArrow.addClass('slick-disabled').attr('aria-disabled', 'true');
                _.$nextArrow.removeClass('slick-disabled').attr('aria-disabled', 'false');

            } else if (_.currentSlide >= _.slideCount - _.options.slidesToShow && _.options.centerMode === false) {

                _.$nextArrow.addClass('slick-disabled').attr('aria-disabled', 'true');
                _.$prevArrow.removeClass('slick-disabled').attr('aria-disabled', 'false');

            } else if (_.currentSlide >= _.slideCount - 1 && _.options.centerMode === true) {

                _.$nextArrow.addClass('slick-disabled').attr('aria-disabled', 'true');
                _.$prevArrow.removeClass('slick-disabled').attr('aria-disabled', 'false');

            }

        }

    };

    Slick.prototype.updateDots = function() {

        var _ = this;

        if (_.$dots !== null) {

            _.$dots
                .find('li')
                .removeClass('slick-active')
                .attr('aria-hidden', 'true');

            _.$dots
                .find('li')
                .eq(Math.floor(_.currentSlide / _.options.slidesToScroll))
                .addClass('slick-active')
                .attr('aria-hidden', 'false');

        }

    };

    Slick.prototype.visibility = function() {

        var _ = this;

        if ( _.options.autoplay ) {

            if ( document[_.hidden] ) {

                _.interrupted = true;

            } else {

                _.interrupted = false;

            }

        }

    };

    $.fn.slick = function() {
        var _ = this,
            opt = arguments[0],
            args = Array.prototype.slice.call(arguments, 1),
            l = _.length,
            i,
            ret;
        for (i = 0; i < l; i++) {
            if (typeof opt == 'object' || typeof opt == 'undefined')
                _[i].slick = new Slick(_[i], opt);
            else
                ret = _[i].slick[opt].apply(_[i].slick, args);
            if (typeof ret != 'undefined') return ret;
        }
        return _;
    };

}));

/* =========================================================
 * kp.accordion.js
 * =========================================================
 * Accordion behavior for a series of panels with option to allow
 * multiple open at a time. In order to handle nested accordions, markup is strict.
 * <div data-accordion class="accordion">
 *   <div class="accordion-container">
 *     <div class="accordion-title" role="tab" aria-controls="panel1" id="tab1">Section 1></div>
 *     <div class="accordion-body" aria-labelledby="tab1" role="tabpanel" id="panel1">
 *       <div class="accordion-body-content">
 *         <p>Content here</p>
 *       </div>
 *     </div>
 *   </div>
 *   <div class="accordion-container">
 *     <div class="accordion-title" role="tab" aria-controls="panel2" id="tab2">Section 2</div>
 *     <div class="accordion-body" aria-labelledby="tab2" role="tabpanel" id="panel2">
 *       <div class="accordion-body-content">
 *         <p>Content Here</p>
 *       </div>
 *     </div>
 *   </div>
 * </div>
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Accordion = function Accordion(element, options) {
				this.init ('accordion', element, options);
			},
			CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			utilities = global[namespace].utilities;

	//private
	/* given an element, finds the accordion tab and panel. Accepts the accordion container, the accordion trigger or
	 the accordion body (panel). Element can be jquery or a dom element. */
	function getAccordionElements(accordion, element) {
		var elements = {},
				$element, $trigger, $target;

		if (element instanceof jQuery) {
			$element  = element;
		} else {
			$element = $(element);
		}

		if ($element.hasClass(accordion.options.trigger_class_name)) {
			$trigger = $element;
			$target = $('#' + $element.attr('aria-controls'));
		} else if ($element.hasClass(accordion.options.content_class_name)) {
			$target = $element;
			$trigger = $('#' + $element.attr('aria-labelledby'));
		} else if ($element.hasClass(accordion.options.accordion_class_name)) {

			/* Instead of enforcing a strict child relationship. The trigger and content just need to be
			 descendants of this element without being descendants of another accordion container. */
			$trigger = accordion.$triggers.filter(function(index) {
				return $(this).closest('.' + accordion.options.accordion_class_name).is($element);
			});
			$target =  accordion.$panels.filter(function(index) {
				return $(this).closest('.' + accordion.options.accordion_class_name).is($element);
			});
		}
		elements['trigger'] = $trigger;
		elements['target'] = $target;
		return elements;
	}

	function toggleAccordion (accordion, $trigger, $target){
		if ($trigger.hasClass('active')) {
			closeAccordion(accordion, $trigger, $target);
		} else {
			openAccordion(accordion, $trigger, $target);
		}
	}

	function openAccordion(accordion, $trigger, $target){
		if ($trigger.hasClass('active')) {
			return;
		}
		$trigger.addClass('active').attr('aria-expanded', true);
		$target.slideDown().attr('aria-expanded', true);
		$trigger.trigger('open.accordion');
		if (!accordion.options.allow_multi_expand) {
			closeAccordion(accordion, accordion.$triggers.not($trigger), accordion.$panels.not($target));
		}
	}

	function closeAccordion (accordion, $trigger, $target){
		if (!$trigger.hasClass('active')) {
			return;
		}
		$trigger.removeClass('active').attr('aria-expanded', false);
		$target.slideUp().attr('aria-expanded', false);
		$target.trigger('close.accordion');
	}


	//PUBLIC
	Accordion.prototype = {
		constructor: Accordion,
		init: function init(type, element, options) {
			var self = this,
					multiselectable;
			if (loggingDebug) {
				console.debug('init accordion with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			/* Instead of enforcing a strict child relationship. The accordion container and trigger just need to be
			 descendants of the accordion without being descendants of another accordion. */
			this.$accordions = this.$element.find('.' + self.options.accordion_class_name).filter(function(index) {
				return $(this).closest('.' + self.options.accordion_group_class_name).is(self.$element);
			});
			this.$triggers = this.$element.find('.' + self.options.trigger_class_name).filter(function(index) {
				return $(this).closest('.' + self.options.accordion_group_class_name).is(self.$element);
			});
			this.$panels =  this.$element.find('.' + self.options.content_class_name).filter(function(index) {
				return $(this).closest('.' + self.options.accordion_group_class_name).is(self.$element);
			});

			multiselectable = this.$element.attr('aria-multiselectable');
			if (multiselectable !== undefined) {
				this.options.allow_multi_expand = (multiselectable == 'true');
			}

			this.$triggers.on('click', function(e) {
				var $this = $(this);

				// if accordion is in off-canvas nav, make sure to only open when it has a submenu (not a link)
				if ($this.parents('.accordion').hasClass('off-canvas-list')) {
					if ($this.parent('.accordion-container').hasClass('has-submenu')) {
						e.preventDefault();
						self.toggle(this);
					}
				}
				else {
					e.preventDefault();
					self.toggle(this);
				}
			});
		},
		toggle : function toggle(element) {
			var els = getAccordionElements(this, element);
			if (els.trigger !== undefined && els.target !== undefined) {
				toggleAccordion(this, els.trigger, els.target);
			}
		},
		open : function open(element) {
			var els = getAccordionElements(this, element);
			if (els.trigger !== undefined && els.target !== undefined) {
				openAccordion(this, els.trigger, els.target);
			}
		},
		openAll : function closeAll() {
			var $accordion = this.$element;
			this.$triggers.addClass('active').attr('aria-expanded', true);
			this.$panels.slideDown(function(){
				if ($accordion.parents('.modal').length > 0) {
					$('.modal').modal('reposition');
				}
			}).attr('aria-expanded', true);

		},
		close : function close(element) {
			var els = getAccordionElements(this, element);
			if (els.trigger !== undefined && els.target !== undefined) {
				closeAccordion(this, els.trigger, els.target);
			}
		},
		closeAll : function closeAll() {
			this.$triggers.removeClass('active').attr('aria-expanded', false);
			this.$panels.slideUp().attr('aria-expanded', false);
		}
	};

	$.fn.accordion = function accordion(option) {
		var el = this,
				options = $.extend({}, $.fn.accordion.defaults, typeof option === 'object' && option),
				args = Array.prototype.slice.call( arguments, 1 );
		return el.each(function () {
			var data = $.data(this, 'accordion');
			if (!data) {
				$.data(this, 'accordion', (data = new Accordion(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				} else if (typeof option == 'string') {
					data[option].apply(data, args);
				}
			}
		});
	};

	$.fn.accordion.defaults = {
		allow_multi_expand: true,
		trigger_class_name: 'accordion-title',
		content_class_name: 'accordion-body',
		accordion_class_name: 'accordion-container',
		accordion_group_class_name: 'accordion'
	};

	$.fn.accordion.Constructor = Accordion;


	$(function () {
		$('[data-accordion]').accordion();
	});


}(this, window.jQuery, "KP"));



/* ========================
 Google Tag Manager Analytics
 =========================*/

var dataLayer = dataLayer || [];

(function (global, $, digitalData, namespace) {

	"use strict";

var analytics = {},
		viewedPromotions = [];

	function findById(idValue, arr) {
		return findProperty(idValue, arr, "id");
	}
	function findByPid(idValue, arr) {
		return findProperty(idValue, arr, "pid");
	}
	function findProperty(propertyValue, arr, propertyName) {
		if (arr) {
			for (var i=0; i < arr.length; i++) {
				if (arr[i][propertyName] === propertyValue) {
					return i;
				}
			}
		}
		return -1;
	}

	function getCookieConfig(storageType) {
		var cookieConfig = {};
		if (digitalData.checkoutStep === 1 ||
				(storageType && storageType=="cart")) {
			// checkout pages - cart products saved in semi-permanent cookies
			cookieConfig.name = "products";
			cookieConfig.options = {path : '/', expires: 1*365};
		} else {
			// otherwise, products stored only until end of session
			cookieConfig.name = "productsTemp";
			cookieConfig.options = {path : '/', expires: 1};
		}
		return cookieConfig;
	}

	/* ========================
	 getProductCategory()
	 The product category is how the user found the product
	 and is saved in / retrieved from cookie
	 =========================*/
	function getProductCategory(pid, cookieIt) {
		var category, product;
		product = getProductFromStorage(pid, cookieIt);
		if (product) {
			category = product.category;
		}
		return category;
	}
	/* ========================
	 getProductList()
	 The product list is where the user found the product
	 and is saved in / retrieved from cookie
	 =========================*/
	function getProductList(pid, cookieIt) {
		var list, product;
		product = getProductFromStorage(pid, cookieIt);
		if (product) {
			list = product.list;
		}
		return list;
	}
	/* ========================
	 getProductFromStorage()
	 Retrieve the product from cookie.
	 =========================*/
	function getProductFromStorage(pid, cookieConfig) {
		var cookieProducts, product, productIndex;
		pid = pid.toString();
		if (!cookieConfig) {
			cookieConfig = getCookieConfig();
		}
		cookieProducts = decodeURIComponent($.parseJSON($.cookie(cookieConfig.name)));
		productIndex  = findById(pid, cookieProducts);
		if (productIndex > -1) {
			product = cookieProducts[productIndex];
		}
		return product;
	}

	function removeProductFromStorage(pid, cookieConfig) {
		var productIndex, cookieProducts;
		if (!cookieConfig) {
			cookieConfig = getCookieConfig();
		}
		cookieProducts = decodeURIComponent($.parseJSON($.cookie(cookieConfig.name)));
		if (!cookieProducts) {
			return [];
		}
		productIndex  = findById(pid, cookieProducts);
		// replace stored product with latest selection
		if (productIndex > -1) {
			cookieProducts.splice(productIndex,1);
		}
		$.cookies(cookieConfig.name, cookieProducts, cookieConfig.options);
		return cookieProducts;
	}

	/* ========================
	 addProductToStorage(pid, category, list)
	 Store some product data into a cookie. This is for storing browsing data such as category and list that is not
	 available to us in checkout.
	 =========================*/
	function addProductToStorage(productId, category, list, cookieConfig) {
		if (!cookieConfig) {
			cookieConfig = getCookieConfig();
		}
		var cookieProducts = removeProductFromStorage(productId, cookieConfig);
		cookieProducts.push({
			"id": productId,
			"category": encodeURIComponent(category),
			"list": encodeURIComponent(list)
		});
		//housekeeping. Keep a max of 10 products so that cookie doesn't get to big.
		//Really should need 2 but want to cover a little history navigation (browser back/forward)
		if (cookieConfig.name === "productsTemp" && cookieProducts.length > 10) {
			while (cookieConfig.length > 10) {
				cookieConfig.shift();
			}
		}
		$.cookie.set(cookieConfig.name, cookieProducts, cookieConfig.options);
	}

	/* ========================
	 copyProductToCartStorage(productId)
	 used to move cookie data into a longer term cookie.
	 =========================*/
	function copyProductToCartStorage(productId) {
		var category,list, cookieConfig;
		category = getProductCategory(productId);
		list = getProductList(productId);
		cookieConfig = getCookieConfig("cart");
		addProductToStorage(productId, category, list, cookieConfig);
	}

	function sendProductEvent(event, product, action) {
		/*if (!product.category) {
			product.category = getProductCategory(product.id);
		}*/
		/*if (!product.list) {
			product.list = getProductList(product.id);
		}*/
		
		if(event === "removeFromCart"){
			digitalData.events.push({
				event: event,
				ecommerce : {
					'currencyCode': 'USD',
					'remove': {
						'products': [product]
					}
				}
			});
		}else if(event === "addToCart"){
			digitalData.events.push({
				event: event,
				ecommerce : {
					'currencyCode': 'USD',
					'add': {
						'products': [product]
					}
				}
			});
			
			digitalData.events.push({
				event: 'Product Detail',
				eventCategory: 'Product Detail Delivery Method',
				eventAction: $("input[name='order-type']:checked").val(),
				eventLabel : window.location.href
			});
		}else if(event === "productClick"){
			digitalData.events.push({
				event: event,
				ecommerce : {
					'click': {
						'actionField': {'list': product.list},
						'products': [{
							'name': product.name || product.id,
							'id': product.id,
							'price': product.price,
							'brand': product.brand,
							'category': product.category,
							'variant': product.variant,
							'position': product.position
						}]
					}
				},
				eventCallback: function() {
					document.location = action;
				}
			});
		}else {
			digitalData.events.push({
				event: event,
				ecommerce : [product]
			});
		}
		analytics.sendEventsTagManager();
	}

	function initializePromotionData(){

		var promotionLinks = $('[data-promotionname]'),
				$promotionLink,
				isVisible = false;
		if (promotionLinks.length > 0 && !digitalData.promotions) {
			digitalData.promotions = [];
		}

		for (var i = 0; i < promotionLinks.length; i++) {
			$promotionLink = $(promotionLinks[i]);
			$promotionLink.attr('data-promoid', 'promo' + i);
			//items in the mega nav or the flexslider are not considered visible on load, will be tracked using sendPromotionsInContainer
			isVisible = ($promotionLink.parents('#big-menu').length === 0 && $promotionLink.parents('.flexslider').length === 0);
			digitalData.promotions.push({
				id:  'promo' + i,
				name: $promotionLink.data("promotionname"),
				creative: $promotionLink.data("promotionlink"),
				position: $promotionLink.data("promotionposition") + i,
				viewOnLoad: isVisible,
				action: $promotionLink.data("promotionlink")
			});
		}
	}

	analytics.init = function(){
	
		analytics.sendGlobalEvents();
		
		/* Page specific Tags */
		
		if ("pageType" in digitalData.page && (digitalData.page.pageType === "category" || digitalData.page.pageType === "search")) {
			$('.promo-grid-three').on('click', 'a', function(e) {
				var label = $(this).find("strong").text() || "";
				analytics.trackEvent('Product Category Cartridge', 'Product Category', $(this).attr("data-tag-title") || $(".crumb.active").text().trim(), label);
			});
			analytics.sendProductViews();
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "product"){
			analytics.sendProductEvents();
			analytics.sendProductDetailView(digitalData.page.productID);
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "cart"){
			analytics.sendCartEvents();
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "staticContent"){
			analytics.sendStaticPageEvents();
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "storeDetail"){
			analytics.sendStoreDetailPageEvents();
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "storeLocator"){
			analytics.sendStoreLocatorPageEvents();
		}
		
		//listen for promotion clicks.
		$('body').on('click', '[data-promotionname]', function(e) {
			var promoId = this.getAttribute("data-promoid");
			if (promoId) {
				analytics.sendPromotionClick(promoId);
			}
		});
		
		//get the promotion data ready
		initializePromotionData();
		
		//get the promotions we should send immediately
		var visiblePromotions = [];
		if ("promotions" in digitalData) {
			for (var i = 0; i < digitalData.promotions.length; i++) {
				if (digitalData.promotions[i].viewOnLoad === true) {
					visiblePromotions.push(digitalData.promotions[i]);
				}
			}
		}
		
		// send empty keyword search event 
		if ("searchResults" in digitalData.page && digitalData.page.searchResults === 0) {
			analytics.trackEvent("Null Search Result","Null Search Result", "search", digitalData.page.searchTerm);
		}
		
	/* TODO ::::::::::::::::  1) Reassess the need of these lines. 2) Remove unwanted function calls.
	
		if ("order" in digitalData) {
			if (digitalData.checkoutStep === 7) {
				// order completed - remove products  from local storage
				$.cookies.remove('products', { path: '/' });
			}
		}

		// send empty keyword search event 
		if ("searchResults" in digitalData.page && digitalData.page.searchResults === 0) {
			digitalData.events.push({
				event: 'noSearchResults'
			});
		}

		// Store locator and in store availability events
		if ("locatorResults" in digitalData.page) {
			//In store lookup is Ajax in desktop site so tracking is event based.
			if ("pageType" in digitalData.page && digitalData.page.pageType === "InStoreLookup") {
				analytics.sendInStoreLookup('', digitalData.page.locatorZip, digitalData.page.locatorResults);
			} else {
				//Store locator search is not ajax, so only the empty search results is event based.
				if (digitalData.page.locatorResults === 0) {
					digitalData.events.push({
						event: 'noLocationResults'
					});
				}
			}
		}
		// send store directions event 
		if ("locatorStoreId" in digitalData.page) {
			digitalData.events.push({
				event: 'storeDirections'
			});
		}
		// End Store locator and in store availability events
		
		// Listen for promo code removes 
		$("#removeCoupon").on("submit", function(e) {
			var $this = $(this),
					couponId = $this.find('.coupon-id').val();
			analytics.removePromoCode(couponId);
		});

		*/

		analytics.sendPromotionViews(visiblePromotions);
		analytics.sendEventsTagManager();
	};

	analytics.sendEventsTagManager = function() {
		var event;
		if (!digitalData){
			return;
		}
		if (!digitalData.events || digitalData.events.length === 0) {
			return;
		}
		while (digitalData.events.length > 0) {
			event = digitalData.events.splice(0,1);
			dataLayer.push(event[0]);
			console.log(event[0]);
		}
	};

	/* ========================
	 sendPromotionViews()
	 Sends the promotion impression data to google tag manager. Add custom logic in here to remove promotions that are
	 hidden from view when the page loads, these can be sent by sendPromotionsInContainer. This is a cap to how much data
	 can be sent, so this is done in batches.
	 =========================*/
	analytics.sendPromotionViews = function(promotionArray) {
		if (!digitalData.promotions || digitalData.promotions.length === 0 || typeof promotionArray === 'undefined') {
			return;
		}
		var maxToSend = 24,
				promotionsToSend = [];

		for (var i = 0; i < promotionArray.length; i++) {
			if ($.inArray(promotionArray[i].id, viewedPromotions) === -1) {
				viewedPromotions.push(promotionArray[i].id);
				promotionsToSend.push({
					'id': promotionArray[i].id,
					'name': promotionArray[i].name,
					'creative': promotionArray[i].creative,
					'position': promotionArray[i].position
				});
			}
		}
		if (promotionsToSend.length === 0) {
			return;
		}

		while(promotionsToSend.length) {
			digitalData.events.push({
				event: "promotionView",
				eventPromotions: promotionsToSend.splice(0, maxToSend)
			});
		}
		analytics.sendEventsTagManager();
	};

	/* ========================
	 sendPromotionsInContainer(container)
	 Send in promotion data for promotions inside a container. Used for promotions that are hidden initially and not sent
	 in sendPromotionViews. (like promotions in a megamenu)
	 =========================*/
	analytics.sendPromotionsInContainer = function(container){
		if (!digitalData.promotions || digitalData.promotions.length === 0) {
			return;
		}
		var promotionLinks = $('[data-promotionname]', container),
				promotionsToSend = [];
		for (var i = 0; i < promotionLinks.length; i++) {
			var promoId = promotionLinks[i].getAttribute("data-promoid"),
					promoIdx  = findByPid(promoId, digitalData.promotions),
					promotion;
			if (promoIdx > -1){
				promotion = digitalData.promotions[promoIdx];
				promotionsToSend.push(promotion);
			}
		}
		if (promotionsToSend.length === 0) {
			return;
		}
		analytics.sendPromotionViews(promotionsToSend);
	};

	/* ========================
	 sendPromotionClick()
	 Sends the data for a promotion click to Google Tag Manager by looking up the promotion object by the promotionID
	 parameter. This is a cap to how much data can be sent, so this is done in batches.
	 =========================*/
	analytics.sendPromotionClick = function(promoId) {
		var promotionIndex = findById(promoId, digitalData.promotions),
				promotion,
				promotionsToSend = [];
		if (promotionIndex === -1) {
			return;
		}
		promotion = digitalData.promotions[promotionIndex];
		promotionsToSend.push({
			'id': promotion.id,
			'name': promotion.name,
			'creative': promotion.creative,
			'position': promotion.position
		});
		digitalData.events.push({
			event: "promotionClick",
			ecommerce: {
				'promoClick': {
					'promotions':[promotionsToSend]
				}
			},
			eventCallback: function() {
				document.location = promotion.action;
			}
		});
		analytics.sendEventsTagManager();
	};

	/* ========================
	 sendProductViews()
	 Sends the product impression data to google tag manager.
	 First, loops through the digitalData.products array and adds the product objects into a productView event.
	 Optionally caps the number of products in the event in order to keep below the 8k data load cap. Pushes the
	 productView event object into the digitalData.events array to queue up processing. Finally, calls
	 analytics.sendEventsToTagManager to process all the events in the queue. This function should fire only one time on
	 the page, sending all the visible products to Google Tag Manager.
	 =========================*/
	analytics.sendProductViews = function() {
		if (!digitalData.products || digitalData.products.length === 0) {
			return;
		}			
		var maxToSend = 24,
				tmpProds = digitalData.products.slice(0);
		
		while(tmpProds.length) {
			digitalData.events.push({
				event: "productView",
				ecommerce: {
					'currencyCode': 'USD',
					'impressions': [tmpProds.splice(0, maxToSend)]
				}
			});
		}
		analytics.sendEventsTagManager();
	};

	/* ========================
	 sendProductClick(productId)
	 Retrieve the product from digitalData products object and add an entry in temp storage for the product category and
	 list. Send a product click event.
	 =========================*/
	analytics.sendProductClick = function(productId, action) {
		var productIndex, product;
		productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			product = digitalData.products[productIndex];
			//addProductToStorage(productId, product.category, product.list);
			sendProductEvent("productClick", product, action);
		}
	};

	/* ========================
	 sendRemoveProduct(product)
	 Send a remove from cart event
	 =========================*/
	analytics.sendRemoveProduct = function(productId) {
		var productIndex, product;
		productIndex  = findById(productId, digitalData.order.products);
		if (productIndex > -1) {
			product = digitalData.order.products[productIndex];
			//removeProductFromStorage(productId, "cart");
			sendProductEvent("removeFromCart", product);
		}
	};

	/* ========================
	 sendProductDetailView(product)
	 Send a remove from cart event
	 =========================*/
	analytics.sendProductDetailView = function(productId) {
		var productIndex, product;
		productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			product = digitalData.products[productIndex];
			digitalData.products[productIndex].price = $(".product-details span[itemprop='price']:last").attr("content") || "0";
			digitalData.events.push({
				'ecommerce': {
					'detail': {
						'products': [{
							'name': product.name || product.id, // Name or ID is required.
							'id': product.id,
							'price': product.price,
							'brand': product.brand,
							'category': product.category,
							//'variant': analytics.findVariant()
							'variant': product.variant
						}]
					}
				}
			});
			analytics.sendEventsTagManager();
			
		}
	};

	/* ========================
	 sendModalPageView(name, url, pageType)
	 Used to send data for a pageview into GTM. The url should be the ajax url, not the window location. Use this when
	 you open a modal that loads a new page.
	 =========================*/
	analytics.sendModalPageView = function(name, url, pageType) {
		digitalData.events.push({
			"event": 'modalPageView',
			"eventPage" : {
				"pageName" : name,
				"pageURL" : url,
				"pageType" : pageType
			}
		});
		analytics.sendEventsTagManager();
	};

	/* ========================
	 sendQuickViewClick(productId, url)
	 Registers a product click, a modal page view, and a productDetailView.
	 =========================*/
	analytics.sendQuickViewClick = function(productId, url) {
		var product, productIndex;
		productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			analytics.sendProductClick(productId);
			product = digitalData.products[productIndex];
			analytics.sendModalPageView("QuickView : " + product.name, url, "quickView");
			analytics.sendProductDetailView(productId);
		}
	};

	/* ========================
	 sendCheckoutOption(checkoutOption)
	 During checkout, registers options the user has selected; there is a limitation of one option per checkout step.
	 Possible options are the card type the user has selected during payment and the user's interactions with the AVS
	 modal.
	 =========================*/
	analytics.sendCheckoutOption = function(checkoutOption) {
		var checkoutStep = digitalData.checkoutStep;
		digitalData.events.push({
			event: 'checkoutOption',
			//checkoutStep: checkoutStep,
			//checkoutOption : checkoutOption
			ecommerce : {
				'checkout_option' : {
					"actionField" : {'step': checkoutStep, 'option': checkoutOption},
				}
			}
		});
		analytics.sendEventsTagManager();
	};

	/* ========================
	 removePromoCode(promoCodeId)
	 Send a promo Code remove event with the promo code id and name retrieved from order.discounts.
	 =========================*/
	analytics.removePromoCode = function(promoCodeId) {
		if (typeof promoCodeId != "undefined" && "order" in digitalData && "discounts" in digitalData.order) {
			var index = findById(promoCodeId, digitalData.order.discounts);
			if (index != -1) {
				digitalData.events.push({
					event: 'promoCodeRemove',
					eventDiscount: {
						id: digitalData.order.discounts[index].id,
						name: digitalData.order.discounts[index].name
					}
				});
			}
		}
		analytics.sendEventsTagManager();
	};


	/* ========================
	 sendInStoreLookupModal(productId, url)
	 Sends the modal event for when user launches in-store availability modal
	 =========================*/
	analytics.sendInStoreLookupModal = function(productId, url) {
		var product,
				productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			product = digitalData.products[productIndex];
			analytics.sendModalPageView( "In-Store Availability: " + product.name, url, "inStoreAvailability");
		}
	};

	/* ========================
	 sendInStoreLookup(productId, zipCode, resultCount)
	 Sends the results when user searches for in-store availability of a product
	 =========================*/
	analytics.sendInStoreLookup = function(productId, zipCode, resultCount) {
		var productIndex, eventProducts = [], product,
				eventName = (resultCount && resultCount > 0) ? "inStoreLookup" : "noAvailabilityResults";
			productIndex  = findById(productId, digitalData.products);
			if (productIndex > -1) {
				product = digitalData.products[productIndex];
				eventProducts.push(product);
			}
		digitalData.events.push({
			"event" : eventName,
			"eventPage" : {
				"locatorZip" : zipCode,
				"locatorResults" : resultCount
			},
			"eventProducts" : eventProducts
		});
		analytics.sendEventsTagManager();
	};

	/* ========================
	 trackEvent(event, category, action, label)
	 generic tracking function that can used in content-managed areas
	 =========================*/
	analytics.trackEvent = function (event, category, action, label) {
		digitalData.events.push({
			event: event,
			eventCategory: category,
			eventAction: action,
			eventLabel : label
		});
		analytics.sendEventsTagManager();
	};
	
	/* ========================
	 sendHomePageEvents
	 event handlers to track user interactions with home page
	   ========================*/
	analytics.sendHomePageEvents = function () {
		// Home page product slider
		$('div[data-hp-offers-tag]').on('click', 'a', function(e) {
			var label = $(this).attr("data-prod-name-tag") || $(this).text(),
				action = $(this).closest("div[data-hp-offers-tag]").attr("data-hp-offers-tag");
				
			analytics.trackEvent('Product Tile', 'Product Tile', action, label);
		});
		
		// Track Product offers
		$('.PromoGridContainer [data-overlay]').on('click', function(e) {
			var hasOverlay = $(this).attr("data-overlay");
			
			if(hasOverlay == "true"){ 
				return;
			}
			
			var	label = $(this).find(".button").text() ||  $(this).find("img").attr('alt'),
				action = $(this).closest('.PromoGridContainer').attr("data-tag-title");
			
			analytics.trackEvent('Homepage Offers', 'Homepage Offers', action, label);
			
		});
		
		// Track Home Page CTAs 
		$('.hero-slider a, .promo-page-width a, .promo-grid-two a, .promo-grid-three a').on('click', function(e) {
			var action = $(this).find(".button").text(),
				label  = $(this).find("p").text();
				
			if(action.length){
				analytics.trackEvent('Homepage CTA', 'Homepage CTA', action, label);
			}
		});
	};
	
	/* ========================
	 sendHomePageEvents
	 event handlers to track user interactions with browse page
	   ========================*/
	analytics.sendBrowsePageEvents = function () {
	
		// Track click on category links from side bar.
		$('.facet-list[data-dim="category"]').on('click', 'a', function(e) {
			var label = $(this).attr("data-label") || $(this).text(),
				action = $('.breadcrumbs').find("li .active").text() || "";
			
			analytics.trackEvent('Faceted Search', 'Faceted Search Category Filter', action, label);
		});
		
		// Track click to ‘Sort by’, ‘Items to View’ and pagination sections
		$('a[data-sortparam], .pagination .page-num').on('click', function(e) {
			var label = $(this).text(),
				action = $(this).closest("ul").attr("data-action-tag") || "Pagination";
				
			analytics.trackEvent('Faceted Search', 'Faceted Search', action, label);
		});
				
		// Track click to faceted search sections
		$('.facet-list.checkbox input:checkbox').change('click', function(e) {
			var label = $(this).attr("data-label"),
				action = $(this).parent().attr("data-cat");
		
			analytics.trackEvent('Faceted Search', 'Faceted Search', action, label);
		});
				
		// Track clicks on Product tiles
		$('.product-grid').on('click', 'a', function(e) {
			var label = $(this).attr("data-prod-name-tag") || $(this).text(),
				action = "Product Catalog Tile";		
			if($('.category-product-grid').hasClass("notLeafCat")){
				action = "Product Category Page Tile";
			}
			
			analytics.trackEvent('Product Tile', 'Product Tile', action, label);
		});
	};
	 
	analytics.sendGlobalEvents = function () {
		// Track clicks on Header Navigation Bar
		$('.header-masthead').on('click', 'a', function(e) {
			var action = $(this).attr("data-action") || $(this).text(),
				label = $(this).attr("href");
			action = action.trim();
			
			if(action == 'CHOOSE A STORE'){
				label = "Store Select";
			}else if(action.indexOf("Call Us at") == 0){
				label = "Call";
			}
			
			if(action.indexOf("Hi") != 0 && !$(this).hasClass('home-store-toggle')){
				analytics.trackEvent('Header', 'Header', action, label);
			}
		});
		
		// Track clicks on Header Navigation Bar(Mobile)
		$('a[data-tag-header]').on('click', function(e) {
			var action = $(this).attr("data-tag-header") || $(this).text();
			
			analytics.trackEvent('Mobile Header', 'Header', action.trim(), $(this).attr("href"));
		});
		
		$('.masthead-mobile .masthead-logo img').on('click', function(e) {
			var action = $(this).attr("alt");
			analytics.trackEvent('Mobile Header', 'Header', action.trim(), '/');
		});
		
		// Track clicks on Footer Navigation Bar
		$('.footer-links-container, .footer-legal-links').on('click', 'a', function(e) {
			var action = $(this).text().trim(),
				label = $(this).attr("href");
			
			if(action.indexOf("Call Us at") == 0){
				label = "Call";
			}
			analytics.trackEvent('Footer', 'Footer', action, label);
		});
		
		$('.footer-social .social-icons').on('click', 'a', function(e) {
			var label = $(this).find(".sr-only").text().trim() || $(this).attr("href");
			analytics.trackEvent('Footer', 'Footer', 'Social Icons', label);
		});
		
	
		// Track clicks on Site Navigation links
		$('.primary-nav-menu').on('click', 'a', function(e) {
			var action = $(this).attr("data-parent") || "",
				label  = $(this).text();
			
			analytics.trackEvent('Site Navigation', 'Site Navigation', action.trim(), label.trim());
		});
		
		// Track clicks on Site Navigation links(Mobile)
		$('a[data-parent-mobile]').on('click', function(e) {
			var action = $(this).attr("data-parent-mobile") || "",
				label  = $(this).text();
			
			analytics.trackEvent('Mobile Site Navigation', 'Site Navigation', action.trim(), label.trim());
		});
		
		// Track clicks on Global Site Banner
		$('.global-promo-section-container').on('click', 'a', function(e) {
			var label = $(this).attr("href"),
				action  = $(this).text();
			
			analytics.trackEvent('Global Site Banner', 'Global Site Banner', action.trim(), label);
		});
		$('.footer-email-signup').on('click', 'a', function(e) {
			var label = $(this).attr("href"),
				eventLabel = $(this).parent(".footer-email-signup").find('h3').text().trim(),
				action  = $(this).text();
			
			analytics.trackEvent(eventLabel, eventLabel, action.trim(), label);
		});
		
		$('.section-row .gc-product-section').on('click', 'a', function(e) {
			var label =  $(this).text(),
				action = $(this).attr('href');
				
			analytics.trackEvent('CTA Button', 'Button', label, action);
		});
		$('body').on('click', '.back-to-top', function(e) {				
			analytics.trackEvent('Scroll To Top', 'Scroll To Top', 'Click', window.location.href);
		});
		
		$('body').on('click','.ltkmodal-subscribe',function(){
			var label = $(this).attr('title');
			analytics.trackEvent("Email Sign Up Modal","Email Sign Up", label, window.location.href);
		});
		$('body').on('click','.ltkmodal-close',function(){
			var label = $(this).attr('title');
			analytics.trackEvent("Email Sign Up Modal","Email Sign Up", label, window.location.href);
		});
		
		//typeahead event firing
		$('body').on('click', '.typeahead-container .typeahead-details-top a', function(e) {
			var action = $(this).attr('href'),
				label = $(this).find('.product-name').text();
				analytics.trackEvent("Site Search Tile","Site Search",label, label);
		});
		
		$('body').on('click', '.typeahead-container .typeahead-details-bottom a', function(e) {
			var action = $(this).attr('href'),
				label = $(this).text(),
				parent = $(this).parents('li').find('h4').text();
				analytics.trackEvent("Site Search Link","Site Search",parent, label);
		});
		$('body').on('click', '.typeahead-container .typeahead-suggestions a', function(e) {
			var action = $(this).attr('href'),
				label = $(this).text(),
				parent = $(this).parents('div.typeahead-suggestions').find('h4').text();
				analytics.trackEvent("Site Search Link","Site Search",parent, label);
		});
	};

	analytics.sendProductEvents = function () {
	
		// BV write review tracking.
		$(".product ").on('click', '.bv-write-review', function(e) {
			var action = $(this).hasClass('bv-submission-button') ? 'Write Review' : $(this).text().trim();
			
			digitalData.events.push({
				event: 'Product Reviews',
				eventCategory: 'Product Reviews',
				eventAction: 'Write Review',
				eventLabel : window.location.href
			});
			
			analytics.sendEventsTagManager();
		});
		
		// BV submit review tracking
		$(".product ").on('click', '.bv-submission-button-submit', function(e) {
			
			digitalData.events.push({
				event: 'Product Reviews',
				eventCategory: 'Product Reviews',
				eventAction: 'Submit Review',
				eventLabel : window.location.href
			});
			
			analytics.sendEventsTagManager();
		});
		
		// Track links on PDP
		$("#product-info-accordion a, .underlined-link, .bopis-location-info a").on('click', function(){			
			analytics.trackEvent('Product Detail', 'Product Detail Link', $(this).text().trim(), window.location.href);
		});
		
		// Track image selection
		$(".viewer-thumb").on('click', function(){
			var action = $(this).attr('data-slick-index') || 0;
			analytics.trackEvent('Product Detail', 'Product Detail', "Image " + (parseInt(action) + 1), window.location.href);
		});
		
		// Track social icons - pininterest
		$(".social-icons span[data-pin-log]").on('click', function(){
			analytics.trackEvent('Product Detail', 'Product Detail', "Pinterest", window.location.href);
		});
		
		// email and print
		$(".site-functions a, body .email-product-submit input:submit").on('click', function(){
			var action = $(this).hasClass("print") ? "print" : "Email";
			if($(this).hasClass("button")){
				action = "send";
			}
			
			analytics.trackEvent('Product Detail', 'Product Detail', action, window.location.href);
		});
		
		// Track recently viewed product click
		$(".recently-viewed a[data-prod-name-tag]").on('click', function(){
			var label = $(this).attr("data-prod-name-tag").trim();
			
			analytics.trackEvent('Product Tile', 'Product Tile', 'Recently Viewed', label);		
		});
		
		// Track BV reviews
		$(".bv-inline-histogram .bv-inline-histogram-ratings-star-container").on('click', function(){
			var label = $(this).attr("title").trim();
			label = label.replace("Select to filter reviews with ", "").replace(".", "");
			
			analytics.trackEvent('Product Reviews', 'Product Reviews', 'Ratings Snapshot', label);		
		});
		
		$(".product").on('click', '.bv-content-sort-dropdown .bv-dropdown-item', function(){
			analytics.trackEvent('Product Reviews', 'Product Reviews', 'Sort by',  $(this).text().trim());		
		});
		
		$(".product").on('click', '#bv-content-filter-dropdown-Rating .bv-dropdown-item, #bv-content-filter-dropdown-contextdatavalue_Age .bv-dropdown-item, #bv-content-filter-dropdown-contextdatavalue_Gender .bv-dropdown-item, #bv-content-filter-dropdown-contextdatavalue_ShoppingFrequency .bv-dropdown-item', function(){
			var label = $(this).find('span').text().trim(),
				id =  $(this).parent().attr("id"),
				action = "";
				
			switch(id){
				case 'bv-content-filter-dropdown-Rating' : 									action = 'Rating';
																							break;
				case 'bv-content-filter-dropdown-contextdatavalue_Age' : 					action = 'Age';
																							break;
				case 'bv-content-filter-dropdown-contextdatavalue_Gender' : 				action = 'Gender';
																							break;
				case 'bv-content-filter-dropdown-contextdatavalue_ShoppingFrequency' : 		action = 'Shopping Frequency';
																							break;	
			}
			analytics.trackEvent('Product Review Dropdown', 'Product Reviews', action, label);		
		});
		
		
	};
	
	analytics.sendCartEvents = function () {
		// Track remove/move to wishlist clicks
		$(".item-action a").on('click', function(){
			var action = $(this).text().trim(),
				label  = $(this).closest(".item-details").find(".product-name").text().trim();
			analytics.trackEvent('Shopping Cart', 'Shopping Cart', action, label);
		});
		
		// Track qnty +/- clicks
		$(".cart").on('click', '.quantity-group .minus-icon, .quantity-group .plus-icon', function(){
			var label = $(this).hasClass("minus-icon") ? "-" : "+";
			analytics.trackEvent('Shopping Cart', 'Shopping Cart', 'Quantity', label);
		});
		
		// Track Keep shopping/checkout buttons
		$("#moveToPurchaseInfo, .keep-shopping-btn").on('click', function(){
			var label = $(this).hasClass("keep-shopping-btn") ? "Keep Shopping" : "Proceed to Checkout";
			analytics.trackEvent('Shopping Cart', 'Shopping Cart', label, label);
		});
	};
	
	analytics.sendAccountPageEvents = function () {
		// Track CTA button in account section
		$('.site-wrapper').on('click', '.button', function(){
			if($('body').hasClass("login") || $(this).attr("id") == "tax-exemption-submit" || $(this).attr("id") == "address-submit" || $(this).attr("id") == "payment-form-submit"){
				return;
			}

			var label = $(this).text().trim() || $(this).val();
			analytics.trackEvent('My Account CTA', 'My Account', label, label);
		});
		
		// Track action links in account section
		$('.site-wrapper').on('click', 'a:not(.button)', function(){
			var label = $(this).text().trim() || $(this).val();
			analytics.trackEvent('My Account Link', 'My Account', label, label);
		});
		
	};
	
	analytics.sendStaticPageEvents = function () {
		// Track clicks on left nav links
		$('.two-column-left').on('click', 'a', function(){
			var label = $(this).text().trim(),
				action = $(this).closest(".accordion-body").prev().text();
			analytics.trackEvent('FAQ Navigation', 'FAQ Navigation', action.trim(), label);
		});
		
		// Track click on right hand accordion headers.
		$('.two-column-right').on('open.accordion', '.accordion-title', function(){
			var label = $(this).find('.question').text().trim();
			analytics.trackEvent('FAQ Navigation', 'FAQ Navigation', 'Expand', label);
		});
		// Track click on right container static links.
		$('.two-column-right .content-container').on('click', 'a', function(){
			var label = $(this).text().trim();
				action= $(this).attr("href");
			analytics.trackEvent('Link', 'Link', label, action);
		});
		
	};
	
	analytics.sendStoreDetailPageEvents = function (){
		// Track clicks on store details page links
		$('.two-column-left').on('click', '.tertiary', function(){
			var label = $(this).text().trim(),
			action = $(this).attr("href");
			analytics.trackEvent('Store Detail Button', 'Store Locator', label, action.trim());
		});
		
		$('.location-details').on('click', 'a', function(){
			var label = $(this).text(),
			action = $(this).attr("href");
			if(label !== ''){
				label = label.trim().replace(/[<>]/g,'');
			}
			analytics.trackEvent('Store Detail Link', 'Store Locator', label, action);
		});
	};
	
	analytics.sendStoreLocatorPageEvents = function(){
		$('body').on('click', '.view-store-details a', function(){
			var label = $(this).text(),
			action = $(this).attr("href");
			analytics.trackEvent('Store Detail Link', 'Store Locator', label, action);
		});
	};
	
	/* ========================
	 sendAddProduct(product)
	 Send a add to cart event
	 =========================*/
	analytics.sendAddProduct = function(productId) {
		var productIndex, product;
		productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			product = digitalData.products[productIndex];
			sendProductEvent("addToCart", product);
		}
	};
	
		
	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].analytics = analytics;

}(this, window.jQuery, window.digitalData, "KP"));

/* =========================================================
 * kp.backtotop.js
 * =========================================================
 * Sets a "back to top" button, fixed on page for user to click
 * and scroll back to top of window.
 *
 * Requires jquery.throttle plugin
 * Requires Modernizr
 * ========================================================= */
(function (global, $, namespace) {

	"use strict";

	var BackToTop = function (element, options) {
		this.init ('backToTop', element, options);
	};

	BackToTop.prototype = {
		constructor: BackToTop,
		init: function init(type, element, options) {
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.$scroller = $(this.options.scroll_parent);

			var self = this;

			this.$scroller.scroll($.throttle( 250, function(){
				self.toggleDisplay();
			}));

			this.$element.on('click', this.scrollToTop);
		},
		toggleDisplay: function toggleDisplay() {
			if (this.displayTest()) {
				this.show();
			} else {
				this.hide();
			}
		},
		scrollToTop: function scrollToTop() {
			$("html, body").animate({
				scrollTop: 0
			}, 400);
		},
		show: function show() {
			this.$element.fadeIn();
		},
		hide: function hide() {
			this.$element.fadeOut();
		},
		displayTest: function displayTest() {
			//optionally set responsive rules for showing this element here. (use modernizr match media)

			return this.$scroller.scrollTop() > 90;
		}
	};


	$.fn.backToTop = function backToTop(option) {
		var el = this,
				options = $.extend({}, $.fn.backToTop.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'backToTop');
			if (!data) {
				$.data(this, 'backToTop', (data = new BackToTop(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.backToTop.defaults = {
		scroll_parent: window
	};

	$.fn.backToTop.Constructor = BackToTop;


	$(function () {
		$('[data-backtotop]').backToTop();
	});


}(this, window.jQuery, "KP"));
/* =========================================================
 * kp.dropdown.js
 * =========================================================

 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Dropdown = function Dropdown(element, options) {
			this.init ('dropdown', element, options);
		},
		CONSTANTS = global[namespace].constants,
		loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
		maxPageWidth = global[namespace].config.maxWidth;

	function tabOutOfDropdown() {
		$('body').on('keyup', function(e){
			if ($(e.target).parents('.dropdown.active').length === 0) {
				$('.active .dropdown-toggle').trigger('mouseleave').trigger('click');
				$('body').off('keyup');
			}
		});
	}

	function clickOutOfDropdown() {
		$('body').on('click', function(e){
			if ($(e.target).parents('.dropdown.active').length === 0) {
				$('.active .dropdown-toggle').trigger('mouseleave').trigger('click');
				$('body').off('click');
			}
		});
	}

	//PUBLIC
	Dropdown.prototype = {
		constructor: Dropdown,
		init: function init(type, element, options) {
			if (loggingDebug) {
				console.debug('init dropdown with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}

			var self = this;
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.$toggle = this.$element.find('.dropdown-toggle');
			this.$menu = this.$element.find('.dropdown-menu');
			this.url = this.$element.attr('data-url');
			this.nocache = this.$element.attr('data-nocache');
			this.isLoaded = false;

			// pass is_hover through data attributes
			this.is_hover = this.$element.attr('data-is-hover');
			if (typeof this.is_hover !== 'undefined' && this.is_hover) {
				self.options.is_hover = true;
			}

			if (Modernizr.touchevents) {
				self.options.is_hover = false;
			}

			/* Event binding */
			if (self.options.is_hover) {
				this.$toggle
					.off('dropdown')
					.on('mouseenter.' + namespace + '.dropdown', function () {
						clearTimeout(self.timeout);
						self.timeout = setTimeout(function () {
							/* small delay before opening the menu means that if you briefly mouse-over another trigger on your
							 way to the menu contents, you won't close this menu and open a neighboring menu */
							self.open.call(self);
						},250);
					})
					.on('mouseleave.' + namespace + '.dropdown', function () {
						clearTimeout(self.timeout);
						self.timeout = setTimeout(function () {
							self.close.call(self);
						}.bind(this), 150);
					});
				this.$menu
					.on('mouseenter.' + namespace + '.dropdown', function () {
						clearTimeout(self.timeout);
					})
					.on('mouseleave.' + namespace + '.dropdown', function () {
						clearTimeout(self.timeout);
						self.timeout = setTimeout(function () {
							self.close.call(self);
						}.bind(this), 150);
					});
			} else {
				this.$toggle
					.off('dropdown')
					.on('click.' + namespace + '.dropdown', function(e){
						self.toggle();
						e.preventDefault();
					});
				$('body').off('dropdown').on('click.' + namespace + '.dropdown, touchstart.' + namespace + '.dropdown', function (e) {
					//Will close any open item if the click does not originate from this menu
					if ($(e.target).closest(element).length > 0) {
						return;
					}
					self.close();
				});

				/* clicks in the menu-list will close the dropdown */
				this.$menu.off('dropdown').on('click.' + namespace + '.dropdown', '.menu-list', function(e){
					self.close();
				});
			}

			// make keyboard accessible
			this.$toggle.on('keydown', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				// enter and spacebar trigger accordion
				if (keycode == 13 || keycode == 32) {
					e.preventDefault();
					e.stopPropagation();
					self.toggle(this);
				}
			});

			this.$menu.on('keydown', 'a', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				// enter and spacebar trigger accordion
				if (keycode == 13 || keycode == 32) {
					e.preventDefault();
					e.stopPropagation();
					$(this).click();
				}
			});

		},
		toggle: function () {
			// hasClass('active') is false
			if (this.$element.hasClass('active')) {
				this.close.call(this);
			} else {
				// in touch screen, touch primary nav gets you here
				this.open.call(this);
			}
		},
		close : function (){
			this.$toggle.attr('aria-expanded', false);
			this.$menu.hide().attr('aria-expanded', false);
			this.$element.removeClass('active').trigger('closed');
		},
		open : function (){
			// close open dropdowns before opening another.
			if ($('.dropdown.active').length > 0) {
				$('body').off('keyup').off('click');
				$('.dropdown.active .dropdown-toggle').attr('aria-expanded', false);
				$('.dropdown.active .dropdown-menu').hide().attr('aria-expanded', false);
				$('.dropdown.active').removeClass('active').trigger('closed');
			}

			var self = this,
				maxWidth = maxPageWidth,
				windowWidth = window.innerWidth,
				toggleLeft = this.$toggle.offset().left,
				menuWidth = this.$menu.outerWidth(),
				forceLeft = false,
				leftPos, rightPos;

			function insertContent(response){
				self.$menu.html(response);
				self.isLoaded = true;

				// trigger picturefill for ajax requests in browsers that don't support <picture>
				// if (!window.HTMLPictureElement && $(response).find('picture').length > 0) {
				// 	picturefill();
				// }
			}

			//clear out css values
			this.$menu.css("left","").css("right","");

			/* force open left / edge detection */
			if (toggleLeft + menuWidth > windowWidth || toggleLeft + menuWidth > maxWidth || this.$toggle.hasClass('force-left')) {
				forceLeft = true;
			}

			if (!forceLeft) {
				leftPos = '0px';
				rightPos = 'auto';
			}
			else {
				leftPos = 'auto';
				rightPos = '0px';
			}

			// trigger is bigger than menu, lets make the menu at least as wide as the trigger
			if (this.$toggle.outerWidth() >= menuWidth) {
				leftPos = '0px';
				rightPos = '0px';
			}

			if (this.url !== undefined && (this.isLoaded === false || this.nocache !== undefined)) {
				if (loggingDebug) {
					console.log('making ajax request');
				}

				$.ajax({
					url: self.url,
					dataType: 'html',
					success: insertContent
				});
			}
			this.$toggle.attr('aria-expanded', true);
			this.$menu.css({"left" : leftPos, "right" : rightPos}).show().attr('aria-expanded', true);
			this.$element.addClass('active').trigger('opened');

			tabOutOfDropdown();
			clickOutOfDropdown();
		}
	};

	$.fn.dropdown = function dropdown(option) {
		var el = this,
			options = $.extend({}, $.fn.dropdown.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'dropdown');
			if (!data) {
				$.data(this, 'dropdown', (data = new Dropdown(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.dropdown.defaults = {
		is_hover : false
	};

	$.fn.dropdown.Constructor = Dropdown;

	$(function () {
		// accessibility
		$('.dropdown-toggle').attr('tabindex', '0');
		$('.dropdown-toggle a').attr('tabindex', '-1');
		$('[data-is-hover="true"] .dropdown-toggle').on('focus', function(){
			$(this).trigger('mouseenter');
		});

		// init dropdowns
		$('[data-dropdown]').dropdown();
	});


}(this, window.jQuery, "KP"));

/* =========================================================
 /* =========================================================
 * kp.filters.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Accordion-structured sidebar feature that allows users to
 * refine items displayed on a department or category with
 * predermined filter rules.
 * Also handles applied facet UX (any filters currently applied
 * on the page).
 * NOTE: because the links on the filters and the breadcrumbs
 * are generated by Endeca, we are using the response from the
 * backend to update the interface.
 * ========================================================= */

(function (global, $, namespace) {
	"use strict";

	var CONSTANTS = global[namespace].constants,
		loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
		options = {
			applied_filter_container: '.applied-facets-nav',
			product_grid_container : '.category-product-grid',
			pagination_container : '.pagination',
			sort_container : '.category-dropdowns',
			filter_container : '.filters',
			num_results : '#total-num-recs',
			show_more_threshold : 14
		},
	  // dependencies
		addURLParameter = global[namespace].utilities.addURLParameter,
		showLoader = function() {
			global[namespace].loader.showLoader();
		},
		hideLoader = function(){
			global[namespace].loader.hideLoader();
		};

	/* Filter Menu Object */
	function FilterMenu(element) {
		this.$element = $(element);
		this.init();
	}
	FilterMenu.prototype =  {
		init: function () {
			// this.initializeShowMore();

			/* EVENT LISTENERS */
			/* click event for the facet clicks facet menu */
			this.$element.on('click', '.facet', function(e) {

				// if facet is a link (categories) don't do all this
				if (!$(this).parents('.facet-list').hasClass('links')) {

					/* clicking on a label triggers a second click on the checkbox. We only want to handle one click */
					/* compare e.nodeName to this to see if bubbling is taking place*/
					if (e.target.nodeName == 'LABEL') {
						e.stopPropagation();
						return;
					}

					// add class on click event for checkboxes
					$('input:checkbox').change(function(){
						if ($(this).is(':checked')) {
							$(this).parents('.facet').addClass('active');
							$facet.siblings('.clear-filter').addClass('enabled');
						}
						else {
							$(this).parents('.facet').removeClass('active');

							// if nothing is active hide the clear all filter
							if (!$facet.siblings().hasClass('active')) {
								$facet.siblings('.clear-filter').removeClass('enabled');
							}
						}
					});

					var $facet = $(this),
						getid = $facet.attr('data-id'),
						facetnstate = $facet.attr('data-nstate');

					if (!$facet.hasClass('active')) {
						// add highlighting to facet
						$facet.addClass('active');
						$facet.siblings('.clear-filter').addClass('enabled');
					}
					else {
						// turn off active facet state
						$facet.removeClass('active');
						// remove applied facet item from bar
						$('.applied-facet[data-id="' + getid + '"]').remove();

						// if nothing is active hide the clear all filter
						if (!$facet.siblings().hasClass('active')) {
							$facet.siblings('.clear-filter').removeClass('enabled');
						}
					}

					// ajax request to display the refined results.
					window.location.hash = '#' + facetnstate;
				}
				

			})
			/* click event for the clear link within the facet menu */
			.on('click', '.clear-filter', function(e){
				var common = [],
						clearAllURL = '',
						$clearAll = $(this),
						queryString='',
						contextPath='';

				$clearAll.siblings('.facet.active').each(function(ind){
					var removeNstate = this.getAttribute('data-nstate'),
							index;

					//if the clear all link does not have N value, return the removeNstate
					if ((removeNstate.indexOf('Ntt') !== -1) && (removeNstate.indexOf('N-') === -1)){
						clearAllURL = removeNstate;
						return false;
					}
					/* the url could have a query string, or could be an endeca url (ex /store/browse/shoes-casual-shoes/_/N-10504) */
					if(queryString === '' && removeNstate.indexOf('?') != -1){
						queryString = removeNstate.substring(removeNstate.indexOf('?'), removeNstate.length);
					}
					if(contextPath === '' && (removeNstate.indexOf('N-') != -1 || removeNstate.indexOf('?'))){
						if(removeNstate.indexOf('N-') != -1 ){
							contextPath = removeNstate.substring(0,removeNstate.indexOf('N-'));
						}
						else{
							contextPath = removeNstate.substring(0,removeNstate.indexOf('?'));
						}
					}
					var startI = removeNstate.indexOf('N-') + 2;
					var endI = removeNstate.indexOf('?')!= -1  ? removeNstate.indexOf('?') : removeNstate.length;
					index = removeNstate.substring(startI, endI).split('+');

					if(common.length === 0){
						common = index;
					}
					else {
						common = $.grep(common, function(element) {
							return $.inArray(element, index ) !== -1;
						});
					}
				});

				// remove all active states from respective category's filters incl checkboxes
				$clearAll.parent().find('div').removeClass('active');
				$clearAll.parent().find('input[type=checkbox]').removeClass('active').removeAttr('checked');

				if (common.length > 0) {
					// building the clear-all url using context path, N values, queryStrings from above loop
					clearAllURL = contextPath + 'N-' + common.join('+') + queryString;
				}
				else {
					// there are no facets from other categories, just clear the hash but keep the URL parameters
					// this only happens for search pages
					clearAllURL = '/search?' + window.location.hash.split('?')[1];
				}

				// hide clear-all button
				$clearAll.removeClass('enabled').slideUp();
				window.location.hash = '#' + clearAllURL;
			})
			/* clicks on the show more link within the facet list should show all the hidden facets, then remove the link */
			// .on('click', '.facet-show-more', function() {
			// 	$(this).siblings(':hidden').show().end()
			// 			.closest('.facet-list').addClass('full-list').end()
			// 			.remove();
			// })
			;

			/* Mobile facet menu, cancel or apply button (facets are already applied) */
			$('.close-facets, .apply-facets').click(function(e) {
				if ($('.facet-sidebar').hasClass('open')){
					$('.facet-sidebar').removeClass('open').removeAttr('tabindex');
					$('.off-canvas-wrap').attr('aria-hidden', false);
					$('html').removeClass('no-scroll');
				}
			});
			
			if(typeof $('.applied-facet') === 'object' && $('.applied-facet') !== undefined && $('.applied-facet').length > 0){
				this.$element.find('.facet-list').each(function(){
					var $self = $(this);
					if ($self.attr('data-dim') === undefined || $(this).attr('data-dim') === 'category' ) {
						return;
					}
					$('.applied-facet').each(function(){
						var $appliedFacet = $(this);
						if ($self.attr('data-dim') == $appliedFacet.attr('data-dim')){
							var appliedFacetResult = $appliedFacet.find('.applied-facet-'+$appliedFacet.attr('data-dim'));
							$self.find('.facet').first().before(appliedFacetResult);
						}
					});
				});
			}			
		},
		/* When we initialize, hide the extra items in the facet list. They can be shown by clicking a show more button */
		// initializeShowMore: function($lists){
		// 	var self = this;
		// 	$lists = $lists || this.$element.find('.facet-list');
		// 	$lists.each(function(index, element){
		// 		var $list;
		// 		/* user has clicked the show more for this facet list. Honor it and don't re-collapse */
		// 		if (/full-list/.test(element.getAttribute("class"))){
		// 			return;
		// 		}
		// 		$list = $(element);
		// 		if ($list.find('.facet').length > options.show_more_threshold + 1) {
		// 			$list.find('.facet:gt(' + options.show_more_threshold + ')').hide();
		// 			if ($.find('.facet-show-more', element).length === 0) {
		// 				$list.append('<div class="facet-show-more" tabindex="0">See All</div>');
		// 			}
		// 		}
		// 	});
		// },
		applyFacets: function(response){
			// if the facets were not clicked, this iteration will mark them as clicked
			$(response).find('.applied-facet').each(function(){
				var $responseAppliedFacet = $(this),
						facetidRes = $responseAppliedFacet.attr('data-id'),
						$facetMatch = $('.facet[data-id="' + facetidRes + '"]');

				if (!$facetMatch.hasClass('active')) {
					// add active class
					$($facetMatch).addClass('active');

					// check the checkbox
					$facetMatch.find('[type="checkbox"]').prop('checked', true);

					// enable the clear all filter
					if (!$facetMatch.siblings('.clear-filter').hasClass('enabled')) {
						$facetMatch.siblings('.clear-filter').addClass('enabled');
					}
				}
			});
		},
		mergeRefinements: function(response) {
			var self = this,
					$response = $(response);
			
			//compare existing DOM with ajax response and merge. Disables irrelevant refinements
			$response.find('.facet-list').each(function(){
				
				var $responseRefinementMenu = $(this);


				
				$('.facet-list').each(function(){
					if ($responseRefinementMenu.attr('data-dim') === undefined || $(this).attr('id') === 'category' ) {
						return;
					}
					/* cloning to manipulate dom off canvas before inserting updated content */
					var $originalRefinementMenu = $(this),
							$originalRefinementMenuClone = $originalRefinementMenu.clone();

					
					//Iterate only if the facet type is the same
					if ($originalRefinementMenuClone.attr('data-dim') == $responseRefinementMenu.attr('data-dim')){
						$($responseRefinementMenu).find('.facet').each(function(index){
							var $responseFacet = $(this),
									isNewFacet = true;
							$($originalRefinementMenuClone).find('.facet').each(function(index){
								var $originalFacet = $(this);
								if ($originalFacet.attr('data-id') == $responseFacet.attr('data-id')){
									$originalFacet.replaceWith($responseFacet);
									//Marking all visited refinement links
									$responseFacet.attr('data-visited','y');
									isNewFacet = false;
								}
							});

							// If the domFacet was not assigned to any of the existing DOM, the responseFacet is new - hence append.
							if (isNewFacet) {
								$originalRefinementMenuClone.find('.facet').last().after($responseFacet);
								$responseFacet.attr('data-visited','y');
							}
						});

						/* Hide any extra items */
						// self.initializeShowMore($originalRefinementMenuClone);

						/* insert updated content */
							$originalRefinementMenu.replaceWith($originalRefinementMenuClone);
					}
				});
			});


			/*
			 * Loop through the facets, check for facets not visited in previous loop through the response facets - disable
			 * them as they are not present in the ajax response - hence not valid anymore. For all the active facets, update
			 * the attributes in the applied facets bar.
			 */
			$('.facet').each(function(index){
				var $originalFacet = $(this),
						$appliedFacet;
				if ($originalFacet.parent('div').attr('id') === 'category'){
					return;
				}
				if (!$originalFacet.hasClass('active')){
					if ($originalFacet.attr('data-visited') != 'y') {
						// making unavailable doesn't work properly with the data heirarchy we have. let's just remove it instead
						// $originalFacet.addClass('unavailable');
						$originalFacet.addClass('hide');
						$originalFacet.find('.swatch').removeAttr('tabindex');
						if ($originalFacet.find('[type="checkbox"]')) {
							$originalFacet.find('[type="checkbox"]').prop('disabled', true);
						}
					}
					else {
						// removing flag for next iteration
						$originalFacet.removeAttr('data-visited');
					}
				}
				else {
					$appliedFacet = $(options.applied_filter_container).find('[data-id="' + $originalFacet.attr('data-id') + '"]');
					if ($appliedFacet.length > 0) {
						$originalFacet.attr('data-nstate', $appliedFacet.attr('data-nstate'));
						$originalFacet.find(".ref-count").html(' ('+$appliedFacet.attr('data-count')+')');
						if($originalFacet.find('.swatch').length > 0) {
							$originalFacet.find(".icon-check").css('display', 'inline-block');
						}
					}
				}
			});
			
			this.$element.find('.accordion-container').each(function(){
				var prodAttrbs = $(this).find('.facet-list'),
				totalFacets = 0,
				visible = false;
				if (prodAttrbs.attr('data-dim') === 'Category'){
					return;
				}
				prodAttrbs.find('.facet').each(function(){
					if(!$(this).hasClass('hide')){
						totalFacets++;
						visible = true;
					}
				});
				if(totalFacets > 10){
					prodAttrbs.parents('.facet-body').addClass('scrollbar');
				}else{
					prodAttrbs.parents('.facet-body').removeClass('scrollbar');
				}
				if(visible){
					$(this).show();
				}else{
					$(this).hide();
				}
			});
		},
		update: function(response) {
			// Refresh left nav after merge
			this.applyFacets(response);
			this.mergeRefinements(response);
			if (KP.analytics) {
				KP.analytics.sendBrowsePageEvents();
				KP.analytics.sendProductViews();
			}
		}
	};

	/* Sort Menu Object*/
	function SortMenu(element) {
		this.$element = $(element);
		this.buttonTemplate = '{{text}} <span aria-hidden="true" class="icon icon-arrow-down"></span>';
		this.init();
	}
	SortMenu.prototype = {
		init : function () {
			// sort options - defined here because the facets should know about the sort user selected
			this.$element.on('click','a', function(e) {
				e.preventDefault();

				// if user clicks on already selected item, we don't need to do anything.
				if (/active/.test(e.target.getAttribute("class"))) {
					return;
				}

				var selectedSortValue = $(e.target).attr('data-sortvalue'),
						No = decodeURI(global[namespace].utilities.getURLParameter(selectedSortValue, 'No')),
						Nrpp = decodeURI(global[namespace].utilities.getURLParameter(selectedSortValue, 'Nrpp'));

				// fix page number before setting hash so product grid is correct in response
				if (No !== '' && Nrpp !== '') {
					selectedSortValue = selectedSortValue.replace('No=' + No,'No=' + Math.floor(parseInt(No) / parseInt(Nrpp)) * parseInt(Nrpp));
				}

				window.location.hash = '#' + selectedSortValue;
			});
		},
		update : function(response) {
			var currentHash = window.location.hash,
					Ns = decodeURI(global[namespace].utilities.getURLParameter(currentHash, 'Ns')),
					Nrpp = decodeURI(global[namespace].utilities.getURLParameter(currentHash, 'Nrpp'));

			this.$element.html($(response).find(options.sort_container).html());
			$('.category-sort, .category-items-per-page').dropdown();

			// display active sort/items-per-page in title
			$('#category-sort-menu a').removeClass('active');
			$('#items-per-page-menu a').removeClass('active');
			if (Ns !== '') {
				$('#category-sort-menu a[data-sortparam="' + Ns + '"]').addClass('active');
				this.setButtontext(this.$element.find('#category-sort-title'), this.$element.find('#category-sort-menu a.active').html());
			}
			if (Nrpp !== '') {
				$('#items-per-page-menu a[data-sortparam="' + Nrpp + '"]').addClass('active');
				this.setButtontext(this.$element.find('#items-per-page-title'), this.$element.find('#items-per-page-menu a.active').html());
			}
		},
		setButtontext : function ($button, text){
			if ($button && $button !== '' && text && text !== '') {
				$button.html(Mustache.render(this.buttonTemplate, {text: text.trim()}));
			}
		}
	};

	/* Product Grid Object */
	function ProductGrid(element) {
		this.$element = $(element);
		this.init();
	}
	ProductGrid.prototype = {
		init : function(){},
		update: function(response) {
			/* Update grid contents and initialize responsive images */
			this.$element.html($(response).find(options.product_grid_container).html());
			global.picturefill();

			var emptyResultsMessage = $(response).find('#null-filters-message').html();
			if (emptyResultsMessage !== '') {
				$('#null-filters-message').html(emptyResultsMessage).show();
			}
			else {
				$('#null-filters-message').empty().hide();
			}
		}
	};

	/* Applied Filters Object*/
	function AppliedFilters(element) {
		this.$element = $(element);
		this.init();
	}
	AppliedFilters.prototype = {
		init : function () {
			// ajax request to display the refined results.
			this.$element.on('click', '.applied-facet-item', function(e) {
				window.location.hash = '#' + $(this).attr('data-nstate');
			});
		},
		update: function(response) {
			// update the applied facets
			this.$element.html($(response).find('#applied-facet-breadcrumbs').html());
			$('#applied-facet-breadcrumbs').remove();
		}
	};

	/* Pagination Object */
	function Pagination(element) {
		this.$element = $(element);
		this.init();
	}
	Pagination.prototype = {
		init : function(){
			// pagination ajax
			$('.category .pagination, .search .pagination').on('click', function(event) {
				event.preventDefault();
				var $selector = $(event.target),
						paginationURL = '';
					if ($selector.hasClass('page-num')) {
						paginationURL = $selector.attr('href');
					}
					else {
						paginationURL = $selector.parent().attr('href');
					}
					
					if(typeof paginationURL === 'undefined' ){
						return;
					}
					
					window.location.hash = '#'+paginationURL;
					global[namespace].utilities.showLoader();

					$.ajax(paginationURL, {
						success: function(data) {
							global[namespace].utilities.hideLoader();
							$('.pagination').html($(data).find('.pagination').html());
							$('.category-product-grid').html($(data).find('.category-product-grid').html());
							/* load responsive images */
							global.picturefill();
							$('html, body').animate({scrollTop: 0}, 400);
						},
						error: function() {
							global[namespace].utilities.hideLoader();
						}
					});
			});
		},
		update: function(response) {
			this.$element.html($(response).find(options.pagination_container).html());
		}
	};

	/* Number of Results Object*/
	function NumberOfResults(element) {
		this.$element = $(element);
		// this.init();
	}
	NumberOfResults.prototype = {
		// init : function(){},
		update: function(response) {
			// update the total number of search results
			this.$element.html($(response).find('#total-num-recs').html());
		}
	};

	/* the Controller */
	function FilterController(){
		this.init();
	}
	FilterController.prototype = {
		init: function() {
			if (loggingDebug) {
				console.debug('init filter controller');
			}

			var self = this;
			this.appliedFilters = new AppliedFilters(options.applied_filter_container);
			this.productGrid = new ProductGrid(options.product_grid_container);
			this.pagination = new Pagination(options.pagination_container);
			this.sortMenu = new SortMenu(options.sort_container);
			this.filterMenu = new FilterMenu(options.filter_container);
			this.numResults = new NumberOfResults(options.num_results);

			/* Handle hash change */
			$(window).on('hashchange', function(e) {
				if (window.location.hash.indexOf('#') != -1){
					var hashUrl = window.location.hash.substring(1),
							pathname = window.location.pathname;
					if (hashUrl == pathname) {
						// reload page without hash to ensure back button is not an ajax request
						window.location = pathname;
					}
					else {
						//if(digitalData){
						//	digitalData.products = [];//re initialized
						//}
						self.makeEndecaRequest(hashUrl);
					}
				}
				else {
					//non hash url handle
					self.makeEndecaRequest(e.originalEvent.newURL);
				}
			});

			/* Fire the ajax request if the url has hash in it */
			$(window).on('load', function(e) {
				if(window.location.hash !== '' && (window.location.hash.indexOf('#') != -1)){
					$(window).trigger('hashchange');
				}
			});

		},
		makeEndecaRequest : function(url){
			var self = this;
			showLoader();
			$.ajax(url, {
				cache: false,
				success: function(data) {
					hideLoader();
					self.updatePageWithResults(data);
				},
				error: function() {
					hideLoader();
				}
			});
		},
		updatePageWithResults: function(data) {
			//Refresh product grid, sort options, pagination, breadcrumbs with ajax response
			this.pagination.update(data);
			this.sortMenu.update(data);
			this.productGrid.update(data);
			this.appliedFilters.update(data);
			this.filterMenu.update(data);
			this.numResults.update(data);
		}
	};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].FilterController = FilterController;

}(this, window.jQuery, "KP"));

/* =========================================================
 /* =========================================================
 * kp.loader.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Simple utility to show a loading screen. This informs the
 * user that a process is running and prevents them from
 * interacting with the UI.
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";
	var CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			loader = {
				init: function() {
					var loaderDiv = '<div class="loader"><div class="loader-backdrop fade"/><div class="loader-content"><img class="loader-animation" src="' + CONSTANTS.contextPath + '/resources/images/loading.gif" width="60" height="60" /><span class="loader-text">just a moment...</span></div></div>';
					this.$element = $(loaderDiv).appendTo('body');
				},
				showLoader: function() {
					this.$element.show();
				},
				hideLoader: function() {
					this.$element.hide();
				}
			};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].loader = loader;

	$(document).ready(function() {
		global[namespace].loader.init();
	});


}(this, window.jQuery, "KP"));

/* =========================================================
 * kp.modal.js
 * =========================================================
 * Function to launch modal. Typical usage is
 * <a href="yourlink.jsp" class="modal-trigger" data-target="modalId">Link</a>
 * Link will open href in modal specified by modal target.
 * Has fallback for cross protocol links which are opened in an iframe.
 *
 * @requires postmessage.js
 * @requires Modernizr.js
 * ========================================================= */

(function (global, $, namespace) {
	/*jshint validthis: true */
	"use strict";

	var Modal = function Modal(element, options) {
				this.init ('modal', element, options);
			},
			CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			ajaxError = '<div class="error">' + CONSTANTS.ajaxError + '</div>',
			transition = Modernizr.csstransitions;

	function bindings(bindType) {
		var delegateType = (bindType === "bind")?'delegate':'undelegate';
		this.$element[delegateType]('[data-dismiss="modal"], .cancel', 'click.dismiss.modal', $.proxy(this.hide, this));
	}

	function isExternal(url) {
		var match = url.match(/^([^:\/?#]+:)?(?:\/\/([^\/?#]*))?([^?#]+)?(\?[^#]*)?(#.*)?/);
		if (typeof match[1] === "string" && match[1].length > 0 && match[1].toLowerCase() !== location.protocol) {
			return true;
		}
		if (typeof match[2] === "string" && match[2].length > 0 && match[2].replace(new RegExp(":("+{"http:":80,"https:":443}[location.protocol]+")?$"), "") !== location.host) {
			return true;
		}
		return false;
	}

	function loadUrl (url) {
		var modal = this,
				ajaxOptions = {
					url: url,
					dataType: 'html',
					cache: false,
					success: function (pageData) {
						if (modal.isShown && modal.isContentShown) {
							modal.$element.one('contentHidden', function () {
								showLoadedContent.call(modal, pageData);
							});
						} else {
							showLoadedContent.call(modal, pageData);
						}
					},
					error: function () {
						showLoadedContent.call(modal, ajaxError);
					},
					complete : function () {
					}};
		if (isExternal(url)) {
			if (window['postMessage']) {
				// postMessage Proxy
				loadIframe.call(this, url);
			} else {
				window.location = url;
			}
		} else {
			$.ajax(ajaxOptions);
		}
		this.options.url = '';
	}

	/* iFrame Proxy */
	function loadIframe(url) {
		var modal = this,
				$iframe;

		if (url.indexOf('?') > 0) {
			url = url + '&proxy=true';
		} else {
			url = url + '?proxy=true';
		}

		modal.showLoader();

		if (document.getElementById('proxy')) {
			$iframe = $('#proxy');
		} else {
			$iframe = $('<iframe id="proxy" name="proxy" class="" style="visibility:hidden; float:left;" width="0" height="0" frameborder="0" vspace="0" hspace="0" allowtransparency="true" scrolling="no"></iframe>')
					.appendTo('body');

			pm.bind("setModalContent", function (data) {
				//handle response from iframe
				if (modal.isShown && modal.isContentShown) {
					modal.$element.one('contentHidden', function () {
						showLoadedContent.call(modal, data.content, {proxy: true});
					});
				} else {
					showLoadedContent.call(modal, data.content, {proxy: true});
				}
			});
		}
		$iframe.attr('src', url);
	}

	function loadContent (content){
		showLoadedContent.call(this, content);
		this.options.content = '';
	}

	function showLoadedContent(content, options) {
		var modal = this,
				$loaded = $(content).filter('.ajax-wrapper'),
				controller ='modal',
				action = $loaded.attr('data-action') || '',
				initOptions = {
					$modal : this.$element
				};
		if (options) {
			initOptions = $.extend(initOptions, options);
		}
		modal.$content.empty().html(content);
		global[namespace].init(controller, action, initOptions);
		showContent.call(this);
	}

	function showContent() {
		var modal = this,
				dims;
		modal.hideLoader();
		dims = getDimensions.call(modal);
		if (modal.isShown) {
			resize.call(modal, dims, function (){
				showModalContent.call(modal);
			});
		} else {
			resize.call(modal, dims, function () {
				showModal.call(modal);
			});
		}
	}

	function showModal() {
		var modal = this,
				transitionTimeout, dims;
		reposition.call(modal);
		modal.$modal.addClass('in');
		if (transition) {
			transitionTimeout = setTimeout(function () {
				stateChange.call(modal, 'shown.modal', 'isShown', true);
				bindings.call(modal, 'bind');
			}, 300);
		} else {
			stateChange.call(modal, 'shown.modal', 'isShown', true);
			bindings.call(modal, 'bind');
		}

	}

	function hideModal() {
		var modal = this,
				transitionTimeout;
		modal.$modal.removeClass('in');
		if (transition) {
			transitionTimeout = setTimeout(function () {
				stateChange.call(modal, 'hidden.modal', 'isShown', false);
				bindings.call(modal, 'unbind');
			}, 300);
		} else {
			stateChange.call(modal, 'hidden.modal', 'isShown', false);
			bindings.call(modal, 'unbind');
		}
	}

	function resize(dims, completeCallback) {
		var modal = this,
				getOffsetWidth,
				transitionTimeout,
				transitionEnd = function () {
					// after we've done the resize transformation. release the width and height restrictions
					// this will allow for dynamic content changed inside an already opened modal.
					modal.$modal.css({'height':'auto'
						/* ,'width':'auto' */
					});
					completeCallback();
				};
		if (transition && this.isShown) {
			if (dims.currentWidth !== dims.newWidth || dims.currentHeight !== dims.newHeight) {
				getOffsetWidth = modal.$element[0].offsetWidth; // force reflow
				//set starting dims
				modal.$modal.css({'width': dims.currentWidth, 'height': dims.currentHeight});
				transitionTimeout = setTimeout(transitionEnd, 300);
				//change dims
				modal.$modal.addClass('resize').css({
					/*'width': dims.newWidth, */
					'height': dims.newHeight
					/* ,'margin-left': -(dims.newWidth/2) */
				});
			} else {
				transitionEnd();
			}
		} else {
			modal.$modal.removeClass('resize')
					.css({
						/*'width': dims.newWidth, */
						'height': dims.newHeight
						/*,'margin-left': -(dims.newWidth/2)*/
					});

			transitionEnd();
		}
	}

	function reposition() {
		var modal = this,
				modalPosition,
				docHeight,
				modalHeight;

		docHeight = Math.max($(window).height(), document.documentElement.clientHeight);
		modalHeight = modal.$modal.height();
		modalPosition = (docHeight - modalHeight)/2 + $(document).scrollTop();

		if(docHeight > modalHeight){
			modal.$modal.css('top', modalPosition + 'px');
		} else {
			modal.$modal.css('top', ($(document).scrollTop() + 20) + 'px');
		}
	}

	function getDimensions($loaded) {
		var modal = this,
				dims = {};
		dims.currentWidth = modal.$modal.outerWidth();
		dims.currentHeight = modal.$modal.outerHeight();
		if ($loaded) {
			$loaded.appendTo(modal.$stage.show());
			dims.newWidth = modal.$stage.outerWidth(true) + modal.borderSize;
			dims.newHeight = modal.$stage.outerHeight(true) + modal.borderSize;
			modal.$stage.empty().hide();
		} else {
			dims.newWidth = modal.$content.outerWidth(true) + modal.borderSize;
			dims.newHeight = modal.$content.outerHeight(true) + modal.borderSize;
		}
		return dims;
	}

	function showModalContent() {
		var modal = this,
				transitionTimeout;
		modal.$element.trigger('contentShow.modal');
		modal.$content.addClass('in');

		if (transition) {
			transitionTimeout = setTimeout(function () {
				stateChange.call(modal, 'contentShown', 'isContentShown', true);
			}, 300);
		} else {
			stateChange.call(modal, 'contentShown', 'isContentShown', true);
		}
	}

	function hideModalContent() {
		var modal = this,
				transitionTimeout;
		modal.$element.trigger('contentHide.modal');
		modal.$content.removeClass('in');
		if (transition) {
			transitionTimeout = setTimeout(function () {
				stateChange.call(modal, 'contentHidden.modal', 'isContentShown', false);
			}, 300);
		} else {
			stateChange.call(modal, 'contentHidden.modal', 'isContentShown', false);
		}
	}

	function stateChange(event, statusName, status) {
		var modal = this;
		modal[statusName] = status;
		modal.$element.trigger(event);
	}


	//PUBLIC
	Modal.prototype = {
		constructor: Modal,
		init: function init(type, element, options) {
			if (loggingDebug) {
				console.debug('init modal with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.modalClass = this.options.modalClass;
			this.$modal = $('.modal-window', this.$element);
			this.$content = $('.modal-content', this.$element);
			this.$backdrop = $('.modal-backdrop', this.$element);
			this.$stage = $('<div style="position:absolute; left:-9999em; visibility:hidden; display:none" class="modal-content"></div>').appendTo('body');
			this.isLoaderShowing = false;
			this.isShown = false;
			this.isContentShown = true;
			this.borderSize = parseInt(this.$modal.css("borderLeftWidth"),10) + parseInt(this.$modal.css("borderRightWidth"),10);
		},
		toggle: function toggle() {
			return this[!this.isShown ? 'show' : 'hide']();
		},
		show: function show() {
			var $lastOverlay = $('.overlay-wrap:visible').last();

			this.$element.trigger('show.modal');

			// move this overlay after any visible overlays.
			if ($lastOverlay.size() > 0 && this.isShown === false) {
				this.$element.insertAfter($lastOverlay);
			}

			this.showLoader();

			if (this.options.url){
				// ajax content
				loadUrl.call(this, this.options.url);
			} else if (this.options.content) {
				// param content
				loadContent.call(this, this.options.content);
			} else {
				// assume content is already loaded
				showContent.call(this);
			}
		},
		hide: function hide (event) {
			this.$element.trigger('hide.modal');
			this.$element.hide();
			hideModal.call(this);
			this.$backdrop.removeClass('in');
			if (event) {
				event.preventDefault();
			}
		},
		reposition: function resize () {
			reposition.call(this);
		},
		showLoader: function () {
			if (this.isLoaderShowing) {
				return;
			}
			if (this.isShown) {
				hideModalContent.call(this);
				this.$modal.addClass('loading');
			} else {
				this.$element.show();
				this.$backdrop.addClass((this.options.url) ? 'loading in': 'in');
			}
			this.isLoaderShowing = true;
		},
		hideLoader: function () {
			if (!this.isLoaderShowing) {
				return;
			}
			if (this.isShown) {
				this.$modal.removeClass('loading');
			} else {
				this.$backdrop.removeClass('loading');
			}
			this.isLoaderShowing = false;
		}
	};

	$.fn.modal = function modal(option) {
		var el = this,
				options = $.extend({}, $.fn.modal.defaults, typeof option === 'object' && option);
		return el.each(function doModal() {
			var data = $.data(this, 'modal');
			if (!data) {
				$.data(this, 'modal', (data = new Modal(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
			if (typeof option === 'string') {
				data[option]();
			} else if (options.show) {
				data.show();
			}
		});
	};

	$.fn.modal.defaults = {
		show: true,
		id: 'modal-default'
	};

	$.fn.modal.Constructor = Modal;


	$(function () {
		$('body').off('modal').on('click.' + namespace + '.modal', '.modal-trigger', function openModalLink(e) {
			var $this = $(this),
					modalTarget = $this.attr('data-target') || $.fn.modal.defaults.id,
					modalSize = $this.attr('data-size') || '',
					$modalTarget = document.getElementById(modalTarget) ? $('#' + modalTarget) : global[namespace].utilities.createModal(modalTarget, modalSize),
					url = $this.attr('href'),
					option = {'url': url, 'size': modalSize};
			e.preventDefault();
			$modalTarget.modal(option);
		});
	});


}(this, window.jQuery, "KP"));

/* =========================================================
 * kp.offcanvas.js
 * =========================================================
 * This is based off of the Foundation offcanvas plugin, but
 * stripped down to the essentials for this implementation.
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Offcanvas = function Offcanvas(element, options) {
				this.init ('offcanvas', element, options);
			},
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			$menu = $('.off-canvas-menu'),
			$wrap = $('.off-canvas-wrap'),
			$window = $(window);

	function setOffCanvasHeight(isSideCart){
		// prevent the page from scrolling past the bottom of the off-canvas nav when it's open
		var offCanvasHeight = 0;
		if (isSideCart) {
			offCanvasHeight = $window.height();
			$('#side-cart').css('height', offCanvasHeight);
			$('.side-cart-items').css('height', offCanvasHeight - 128);
		}
		else {
			$menu.children('ul').each(function () {
				offCanvasHeight += $(this).outerHeight(true);
			});
		}
		$wrap.css('minHeight', $window.height()).css('height', offCanvasHeight);
	}

	function removeOffCanvasHeight(){
		$wrap.css('height', 'auto');
		if ($('#side-cart').length > 0) {
			$('#side-cart').removeAttr('style');
			$('.side-cart-items').removeAttr('style');
		}
	}

	//PUBLIC
	Offcanvas.prototype = {
		constructor: Offcanvas,
		init: function init(type, element, options) {

			var self = this,
					move_class = '',
					side_cart_class = 'side-cart-overlap';
			if (loggingDebug) {
				console.debug('init offcanvas with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			if (this.options.open_method === 'move') {
				move_class = 'move-right';
			} else if (this.options.open_method === 'overlap_single') {
				move_class = 'offcanvas-overlap-right';
			} else if (this.options.open_method === 'overlap') {
				move_class = 'offcanvas-overlap';
			}

			$('body').off('.offcanvas')
					.on('click.' + namespace + '.offcanvas', '.off-canvas-toggle', function (e) {
						// toggle the offcanvas menu
						self.toggle(move_class, this);
					})
					.on('click.' + namespace + '.offcanvas', '.side-cart-toggle', function (e) {
						// toggle the side cart
						self.toggle(side_cart_class, this);
					})
					.on('click.' + namespace + '.offcanvas', '.off-canvas-menu a', function (e) {
						var $parent = $(this).parent();
						if(self.options.close_on_click && !$parent.hasClass('has-submenu')){
							// close the menu
							self.hide.call(self, move_class);
						} else if ($parent.hasClass('has-submenu')) {
							e.preventDefault();
						}
					})
					.on('click.' + namespace + '.offcanvas', '.exit-off-canvas', function (e) {
						self.hide(move_class);
						self.hide(side_cart_class);
					});

			// when there's a click in the off-canvas-menu, re-calculate the off-canvas-menu height
			$menu.on('click', '.accordion-title', function(){
				// we need to wait for the accordion to open/close
				setTimeout(function(){
					setOffCanvasHeight();
				}, 500);
			});

		},
		toggle: function(class_name, trigger) {
			if (this.$element.is('.' + class_name)) {
				this.hide(class_name, trigger);
			} else {
				this.show(class_name, trigger);
			}
		},
		show: function(class_name, trigger) {
			this.$element.trigger('open').trigger('open.' + namespace + '.offcanvas');
			this.$element.addClass(class_name);
			if (trigger) {
				trigger.setAttribute('aria-expanded', 'true');
			} else {
				this.$element.find('.off-canvas-toggle').attr('aria-expanded', 'true');
			}
			if (class_name == 'side-cart-overlap') {
				setOffCanvasHeight(true);
			}
			else {
				setOffCanvasHeight();
			}
		},
		hide: function(class_name, trigger) {
			this.$element.trigger('close').trigger('close.' + namespace + '.offcanvas');
			this.$element.removeClass(class_name);
			if (trigger) {
				trigger.setAttribute('aria-expanded', 'false');
			} else {
				this.$element.find('.off-canvas-toggle').attr('aria-expanded', 'false');
			}
			removeOffCanvasHeight();
		}
	};

	$.fn.offcanvas = function offcanvas(option) {
		var el = this,
				options = $.extend({}, $.fn.offcanvas.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'offcanvas');
			if (!data) {
				$.data(this, 'offcanvas', (data = new Offcanvas(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.offcanvas.defaults = {
		open_method : 'overlap',
		close_on_click : true
	};

	$.fn.offcanvas.Constructor = Offcanvas;


	$(function () {
		$('[data-offcanvas]').offcanvas();
	});


}(this, window.jQuery, "KP"));

/* =========================================================
 * primaryNav.js
 * Handles a 3-level nav menu with the third level as a flyout.
 * Configurable to trigger on hover or click.
 * Requires modernizer for touch detection. (Recommend assuming touch and not using hover)
 * Created by KnowledgePath Solutions.
 * ========================================================= */

(function (global, $, Modernizr, namespace) {

	"use strict";

	var Primarynav = function (element, options) {
		this.init ('primarynav', element, options);
	};

	function elementHasClass(el, className) {
		if (el.classList) {
			return el.classList.contains(className);
		} else {
			return new RegExp('(^| )' + className + '( |$)', 'gi').test(el.className);
		}
	}

	function isMenuToggle(el) {
		// check if this link can toggle a menu
		if (!elementHasClass(el, 'locked')  && el.parentNode.querySelector('.primary-nav-menu') !== null) {
			return true;
		}
		return false;
	}

	Primarynav.prototype = {
		constructor : Primarynav,
		init: function init (type, element, options) {
			var self = this;
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			if (Modernizr.touchevents) {
				self.options.is_primary_hover = false;
			}

			/* Event binding */
			if (!self.options.is_primary_hover) {
				/* Primary nav click events */
				this.$element
						.off('primarynav')
						.on('click.' + namespace + '.primary-nav', '.primary-nav-button', function(e){
							if (!isMenuToggle(this)) {
								return;
							}
							var $menu = $(this).closest('.primary-nav-item');
							if ($menu.hasClass('active') && self.options.is_primary_button_clickable) {
								/* for touch devices, if already opened then follow link. */
								return;
							} else {
								self.toggle($menu);
								e.preventDefault();
								e.stopPropagation();
								return false;
							}
						});
				$('body').off('primarynav').on('click.' + namespace + '.primary-nav, touchstart.' + namespace + '.primary-nav', function (e) {
					//Will close any open item if the click does not originate from this menu
					if ($(e.target).closest(element).length > 0) {
						return;
					}
					self.close.call(self, self.$element.find('.primary-nav-item'));
				});
			} else {
				/* Primary nav hover events */
				this.$element
						.off('primarynav')
						.on('mouseenter.' + namespace + '.primary-nav', '.primary-nav-button', function (e) {
							if (!isMenuToggle(this)) {
								return;
							}
							var $menu;
							clearTimeout(self.timeout);
							$menu = $(this).closest('.primary-nav-item');
							self.timeout = setTimeout(function () {
								/* small delay before opening the menu means that if you briefly mouse-over another trigger on your
								 way to the menu contents, you won't close this menu and open a neighboring menu */
								self.close.call(self);
								self.open.call(self, $menu);
							},100);
							// close any open flyouts.

						})
						.on('mouseleave.' + namespace + '.primary-nav', function (e) {
							clearTimeout(self.timeout);
							self.timeout = setTimeout(function () {
								self.close.call(self);
							}.bind(this), 100);
						})
						.on('mouseenter.' + namespace + '.primary-nav', '.primary-nav-menu', function (e) {
							if (!isMenuToggle(this)) {
								return;
							}
							clearTimeout(self.timeout);
						})
						.on('mouseenter.' + namespace + '.primary-nav', '.keyword-search-form', function (e) {
							clearTimeout(self.timeout);
							self.timeout = setTimeout(function () {
								self.close.call(self);
							}.bind(this), 100);
						});
			}
		},
		toggle: function ($menu) {
			//return this[this.$element.hasClass('open') ? 'hide' : 'show'](1);
			this.close.call(this, this.$element.find('.primary-nav-item').not($menu));
			// hasClass('active') is false
			if ($menu.hasClass('active')) {
				this.close.call(this, $menu);
			} else {
				// in touch screen, touch primary nav gets you here
				this.open.call(this, $menu);
			}
		},
		close : function ($menu){
			if ($menu) {
				var $dropdown = $menu.find('.primary-nav-menu');
				$dropdown.hide();
				$menu.removeClass('active');
			} else {
				this.$element.find('.primary-nav-item').removeClass("active").find('.primary-nav-menu').hide();
			}
		},
		open : function ($menu){
			var $dropdown = $menu.find('.primary-nav-menu'),
					$topNavContainer = this.$element.find('nav'),
					menuLeft = $menu.offset().left,
					navLeft = $topNavContainer.offset().left,
					navWidth,
					leftPos, rightPos,
					forceLeft = false,
					subNavWidth;

			//clear out css values
			$dropdown.css("left","").css("right","");

			if (this.options.is_primary_full_width) {

				leftPos = -(menuLeft - navLeft) + 'px';
				rightPos = 'auto';

			} else {

				$topNavContainer = this.$element.find('nav');
				navWidth = $topNavContainer.width();
				subNavWidth = $dropdown.outerWidth();

				/* edge detection */
				if (navLeft + navWidth - menuLeft < subNavWidth) {
					forceLeft = true;
				}

				if (!forceLeft) {
					leftPos = '0px';
					rightPos = 'auto';
				} else {
					leftPos = 'auto';
					rightPos = '0px';
				}
			}

			$dropdown.css({"left" : leftPos, "right" : rightPos}).show();
			$menu.addClass('active');
		}
	};
	$.fn.primarynav = function (option) {
		return this.each(function () {
			var $this = $(this),
					data = $this.data('primarynav'),
					options = typeof option === 'object' && option;
			if (!data) {
				$this.data('primarynav', (data = new Primarynav(this, options)));
			} else {
				$.extend(data.options, options);
			}
			if (typeof option === 'string') {
				data[option]();
			}
		});
	};

	$.fn.primarynav.defaults = {
		is_primary_hover : true,
		is_primary_button_clickable: false,
		is_primary_full_width : false
	};

	$.fn.primarynav.Constructor = Primarynav;

	$(function () {
		$('[data-primarynav]').primarynav();
	});


}(this, window.jQuery, window.Modernizr, "KP"));

/* =========================================================
 * Flyout nav is a separate plugin. Makes it modular.
 * ========================================================= */

(function (global, $, Modernizr, namespace) {

	"use strict";

	var Flyoutnav = function (element, options) {
		this.init ('flyoutnav', element, options);
	};

	function elementHasClass(el, className) {
		if (el.classList) {
			return el.classList.contains(className);
		} else {
			return new RegExp('(^| )' + className + '( |$)', 'gi').test(el.className);
		}
	}

	Flyoutnav.prototype = {
		constructor : Flyoutnav,
		init: function init (type, element, options) {
			var self = this;
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			if (Modernizr.touchevents) {
				self.options.is_flyout_hover = false;
			}

			/* Event Binding */
			if (!self.options.is_flyout_hover) {
				/* flyout nav click events */
				this.$element
						.off('flyoutnav').on('click.' + namespace + '.flyoutnav', '.sub-nav-button', function(e){
							var flyout = $(this).parent().find('.sub-nav-menu');

							if (flyout.parent().hasClass('active') && self.options.is_flyout_button_clickable) {
								/* for touch devices, if already opened then follow link. */
								return;
							} else {
								self.toggle(flyout);
								e.preventDefault();
								e.stopPropagation();
								return false;
							}
						});

				$('body').off('flyoutnav').on('click.' + namespace + '.flyoutnav, touchstart.' + namespace + '.flyoutnav', function (e) {
					//Will close any open item if the click does not originate from this menu
					if ($(e.target).closest(element).length > 0) {
						return;
					}
					self.close.call(self);
				});

			} else {
				/* flyout nav hover events */
				this.$element
						.off('flyoutnav')
						.on('mouseenter.' + namespace + '.flyoutnav', '.sub-nav-button', function(e){
							var flyout = $(this).parent().find('.sub-nav-menu');
							clearTimeout(self.timeout);
							self.timeout = setTimeout(function () {
								/* small delay before opening the menu means that if you briefly mouse-over another trigger on your
								 way to the menu contents, you won't close this menu and open a neighboring menu */
								self.close.call(self);
								self.open.call(self, flyout);
							}, 50);
						}).on('mouseleave.' + namespace + '.flyoutnav', function (e) {
							clearTimeout(self.timeout);
							self.timeout = setTimeout(function () {
								self.close.call(self);
							}.bind(this), 50);
						}).on('mouseenter.' + namespace + '.flyoutnav', '.sub-nav-menu', function (e) {
							clearTimeout(self.timeout);
						});
			}

			//event listener

		},

		toggle: function(flyout){
			this.close.call(this);
			this.close.call(this, this.$element.find('.sub-nav-menu').not(flyout));
			if (flyout.parent().hasClass('active')) {
				this.close.call(this, flyout);
			} else {
				this.open.call(this,  flyout);
			}
		},
		open: function(flyout){
			var offset_parent = this.$element.offsetParent(),
					position = this.$element.offset(),
					left = 0,
					menuwidth = this.$element.width() + flyout.width(),
					menuheight = Math.max(flyout.outerHeight(), this.$element.outerHeight());
			position.top -= offset_parent.offset().top;
			position.left -= offset_parent.offset().left;
			left = position.left + this.$element.width();
			this.$element.css({ width: menuwidth, height: menuheight });
			flyout.attr('style', '').css({ top: 0, left: left, height: menuheight}).parent().addClass('active');
		},
		close: function(flyout){
			if (flyout) {
				this.$element.css({ width: '', height: '' });
				flyout.attr('style', '').parent().removeClass('active');
			} else {
				this.$element.css({ width: '', height: '' })
						.find('.sub-nav-menu').attr('style', '').parent().removeClass('active');
			}
		}
	};
	$.fn.flyoutnav = function (option) {
		return this.each(function () {
			var $this = $(this),
					data = $this.data('flyoutnav'),
					options = typeof option === 'object' && option;
			if (!data) {
				$this.data('flyoutnav', (data = new Flyoutnav(this, options)));
			} else {
				$.extend(data.options, options);
			}
			if (typeof option === 'string') {
				data[option]();
			}
		});
	};

	$.fn.flyoutnav.defaults = {
		is_flyout_hover : true,
		is_flyout_button_clickable: true
	};

	$.fn.flyoutnav.Constructor = Flyoutnav;

	$(function () {
		$('[data-flyoutnav]').flyoutnav();
	});


}(this, window.jQuery, window.Modernizr, "KP"));

/* =========================================================
 * product.js
 * Created by DMI.
 * ==========================================================
 * Functionality for the product display page including:
 * - attribute selection display and interaction
 * @requires collapse
 * @requires validate
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

		var CONSTANTS = global[namespace].constants,
				UTILITIES = global[namespace].utilities,
				TEMPLATES = global[namespace].templates;

	function getObjectSize(pObj) {
		var size = 0, key;
		if (typeof (pObj) === 'object') {
			for (key in pObj) {
				if (pObj.hasOwnProperty(key)) {
					size++;
				}
			}
		}
		else if (Array.isArray(pObj)) {
			size = pObj.length;
		}
		return size;
	}

	global[namespace].ProductData = function (data) {
		this.data = data || {};
		this.allOptions = {};
		this.allInventory = {};
		this.init();
	};

	global[namespace].ProductData.prototype = {
		init : function () {
			this.cleanSkuData();
			this.generateOptions();
		},
		cleanSkuData: function () {
			var variantSize = 0,
					cleanSkus = [],
					x,
					y,
					productSkus = this.data.skus,
					skuLength = productSkus.length,
					variantTypes = this.data.variantTypes,
					variantTypeLength = variantTypes.length;

			// loop though the skus and check that they have the proper amount of variants and that the
			// variants defined on the product exist on the skus.
			for (x = 0; x < skuLength; x++) {
				variantSize = getObjectSize(productSkus[x].variants);
				if (variantSize != variantTypeLength) {
					continue;
				}
				for (y = 0; y < variantTypeLength; y++) {
					if (typeof productSkus[x].variants[variantTypes[y].id] == 'undefined') {
						break;
					}
				}
				cleanSkus.push(productSkus[x]);
			}
			this.data.skus = cleanSkus;
		},
		hasSkus : function () {
			return this.data.skus.length > 0;
		},
		getSku: function (skuId) {
			var skus = this.data.skus,
					skuLength = skus.length,
					i;
			for (i = 0; i < skuLength; i++) {
				if (skus[i].catalogRefId == skuId) {
					return skus[i];
				}
			}
		},
		getSkus: function () {
			return this.data.skus;
		},
		getVariantTypes: function () {
			return this.data.variantTypes;
		},
		getVariantTypeName: function (variantId) {
			var x = 0,
					variantTypes = this.data.variantTypes,
					max = variantTypes.length,
					variantName = '';
			for (x; x < max; x++) {
				if (variantTypes[x].id == variantId) {
					variantName = variantTypes[x].displayName;
					break;
				}
			}
			return variantName;
		},
		getAllOptions : function () {
			return this.allOptions;
		},
		getAllInventory : function() {
			return this.allInventory;
		},
		getSizeChartUrl : function () {
			return this.data.sizeChartUrl.trim();
		},
		generateOptions: function () {
			// allOptions is an array of all the available variants by variant type. It is used during the
			// initialization of the display of the pickers. Using an array because object iteration is
			// unreliable between browsers.
			var skus = this.data.skus,
					variantTypes = this.data.variantTypes,
					variantTypeLength = variantTypes.length,
					variantType = '',
					variant = {},
					options = [],
					variantTypeId,
					skusLength = skus.length,
					optionList = [],
					optionData = [],
					variantSwatch,
					isSelected,
					inventoryStock = [],
					inventoryStockList = [],
					i,
					j;

			// for each type
			for (i = 0; i < variantTypeLength; i++) {
				variantType = (variantTypes[i].displayName).toLowerCase();
				variantTypeId = variantTypes[i].id;
				optionList = [];
				for (j = 0; j < skusLength; j++) {
					variant = skus[j].variants[variantTypeId];
					if(skus[j].inventory !== undefined && Number(skus[j].inventory) <= 0) {
						inventoryStock = [skus[j].catalogRefId, variant.id, variant.displayName, skus[j].inventory];
						inventoryStockList.push(inventoryStock);
					}
					variantSwatch = (variantType == 'color') ? '/images/swatch/' + this.data.productId + '/' + variant.displayName.toLowerCase().replace(/[^a-z]/ig, "_") + '.jpg' : '';
					if(skus[j].catalogRefId === this.data.prevSku){
						isSelected = true;
					}else {
						isSelected = false;
					}
					optionData = [variant.id, variant.displayName, variantSwatch, isSelected];
					optionList.push(optionData);
				}
				optionList = UTILITIES.dedup(optionList);
				options.push(optionList);
			}
			this.allOptions = options;
			this.allInventory = inventoryStockList;
		},
		getFilteredOptions: function (selectedOptions) {
			var variantTypes = this.data.variantTypes,
					variantTypeLength = variantTypes.length,
					tmpArray = [],
					allSkus = this.data.skus,
					filteredSkuArray,
					options = {},
					inventory = {},
					variantTypeId,
					optionKey = '',
					i,
					j,
					k,
					selectedVariantType;

			/*
			 * For each Variant Type selection, filter down the matching skus for the other selectors'
			 * selected elements. From the filtered sku set, get back all available options for this
			 * option type
			 */
			for (i = 0; i < variantTypeLength; i++) {
				variantTypeId = variantTypes[i].id;
				filteredSkuArray = allSkus;
				for (selectedVariantType in selectedOptions) {

					if (selectedVariantType != variantTypeId) {
						// look at each sku and see if this variant value is present
						for (j = 0; j < filteredSkuArray.length; j++) {
							if (filteredSkuArray[j].variants[selectedVariantType] !== undefined) {
								if (filteredSkuArray[j].variants[selectedVariantType].displayName == selectedOptions[selectedVariantType]) {
									tmpArray.push(filteredSkuArray[j]);
								}
							}
						}
						filteredSkuArray = tmpArray;
						tmpArray = [];
					}
				}

				for (k = 0; k < filteredSkuArray.length; k++) {
					if (filteredSkuArray[k].variants[variantTypeId] !== undefined) {
						optionKey = filteredSkuArray[k].variants[variantTypeId].displayName;
						if (options[variantTypeId] === undefined) {
							options[variantTypeId] = variantTypes[i];
							options[variantTypeId].variants = {};
							options[variantTypeId].variants[optionKey] = filteredSkuArray[k].variants[variantTypeId];
							options[variantTypeId].variants[optionKey]["stock"] = filteredSkuArray[k].inventory;
						}
						else if (options[variantTypeId].variants[optionKey] === undefined) {
							options[variantTypeId].variants[optionKey] = filteredSkuArray[k].variants[variantTypeId];
							options[variantTypeId].variants[optionKey]["stock"] = filteredSkuArray[k].inventory;
						}
					}
				}
			}

			return options;
		},
		getSkuVariants: function (skuId) {
			var skus = this.data.skus,
					variants = {},
					i,
					j;
			for (i = 0; i < skus.length; i++) {
				if (skus[i].catalogRefId == skuId) {
					for (j in skus[i].variants) {
						if (variants[j] === undefined) {
							variants[j] = skus[i].variants[j].id;
						}
					}
					break;
				}
			}
			return variants;
		},
		getFilteredSkus: function (selectedOptions) {
			var tempArray = [],
					filteredSkus = this.data.skus,
					variantType,
					i;

			for (variantType in selectedOptions) {
				for (i = 0; i < filteredSkus.length; i++) {
					if (filteredSkus[i].variants[variantType] !== undefined && filteredSkus[i].variants[variantType].displayName == selectedOptions[variantType]) {
						tempArray.push(filteredSkus[i]);
					}
				}
				filteredSkus = tempArray;
				tempArray = [];
			}
			return filteredSkus;
		}
	};

	global[namespace].ProductController = function (data, isModal) {
		this.productId = data.productId;
		this.catalogRefId = '';
		this.productData = new global[namespace].ProductData(data);
		this.$container = (isModal) ? $('#quickView-product-' + this.productId) : $('#product-' + this.productId);
		if (this.$container.length === 0) {
			return;
		}
		this.$skuPicker = this.$container.find('.product-form-pickers');
		this.$formCatalogRefId = this.$container.find('.input-selected-sku');
		this.options = {
			swatchClass : 'option-link ',
			selectedClass : 'active',
			disabledClass : 'disabled',
			dropdownClass: 'option-dropdown',
			optionSwatches: 'option-swatches'
		};

		this.init();

	};
	global[namespace].ProductController.prototype = {
		init : function () {
			var self = this,
					allOptions = {},
					variantTypes = [],
					variantTypeDisplayName = '',
					variantTypeName = '',
					variantTypeId,
					variantOptions = [],
					selectorGroupHtml = '',
					availableSkus = this.productData.data.skus,
					productOutOfStock=this.productData.data.productOutOfStock,
					bopisOnlyAvailable=this.productData.data.bopisOnlyAvailable,
					templateType = this.productData.data.template,
					sizeChartUrl = this.productData.getSizeChartUrl(),
					addToCart = false,
					$addToCartButton = $('.add-to-cart-submit'),
					$bopisOrderRadio = $('.bopis-order'),
					$shipHomeRadio = $('#shipping-order'),
					isEdsPPSOnly = this.productData.data.isEdsPPSOnly,
					isBopisOrder = this.productData.data.isBopisOrder,
					outOfStock,
					allInventoryStock = {},
					x,
					y,
					z;

			this.productData.cleanSkuData();
			if( (productOutOfStock=='true' && bopisOnlyAvailable != 'true') || (isEdsPPSOnly == 'true' && isBopisOrder =='true')) {
				$addToCartButton.addClass('disabled');
			}
			
			if (templateType === 'PICKER') {
				// normal pickers
				if (this.productData.hasSkus()){
					allOptions = this.productData.getAllOptions();
					variantTypes = this.productData.getVariantTypes();
					selectorGroupHtml = '';
					allInventoryStock = this.productData.getAllInventory();

					for (x=0; x < variantTypes.length; x++){

						variantTypeDisplayName = variantTypes[x].displayName;
						variantTypeName = variantTypeDisplayName.toLowerCase().replace(/[^a-z]/ig, "-");
						variantTypeId = variantTypes[x].id;
						variantOptions = self.removeDuplicates(allOptions[x], allOptions[x][1]);
						//variantOptions = Options[x];
						variantTypeDisplayName = 'Select ' + variantTypeDisplayName;

						if (variantTypeName == 'color') {
							// color swatches
							var options = [];
							for (y=0; y<variantOptions.length; y++) {
								options.push({type: variantTypeName, optionValue: variantOptions[y][1], optionId: variantOptions[y][1], imageSrc: variantOptions[y][2], isSelected: variantOptions[y][3]});
							}
							selectorGroupHtml = Mustache.render(TEMPLATES.templatePickerTypeSwatch, {title: variantTypeName, type: variantTypeName, typeId: variantTypeId, availableOptions: options});
							if (selectorGroupHtml !== '') {
									this.$skuPicker.append(selectorGroupHtml);
							}
						}
						else {
							// select dropdowns
							var options = [];
							options.push({type: variantTypeName, optionValue: variantTypeDisplayName, optionId: ''});
							if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
								for (y=0; y<variantOptions.length; y++) {
									options.push({type: variantTypeName, optionValue: variantOptions[y][1], optionId: variantOptions[y][1], isSelected: variantOptions[y][3], outOfStock: outOfStock});
								}
							}
							else{
								for (y=0; y<variantOptions.length; y++) {
									outOfStock = false;
									for(z = 0; z<allInventoryStock.length; z++){
										if(variantOptions[y][1] === allInventoryStock[z][2]){
											outOfStock = true;
											break;
										}
									}
									options.push({type: variantTypeName, optionValue: variantOptions[y][1], optionId: variantOptions[y][1], isSelected: variantOptions[y][3], outOfStock: outOfStock});
								}
							}
							selectorGroupHtml = Mustache.render(TEMPLATES.templatePickerTypeDropdown, {title: variantTypeDisplayName.toLowerCase(), type: variantTypeName, typeId: variantTypeId, availableOptions: options, mediaUrl: sizeChartUrl});
							if (selectorGroupHtml !== '') {
									this.$skuPicker.append(selectorGroupHtml);
							}
						}
					}

					if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
						addToCart = true;
					}else{
						for(var i = 0; i<availableSkus.length; i++){
							if(allInventoryStock !== undefined && Number(availableSkus[i].inventory) > 0){
								addToCart = true;
								break;
							}
						}
					}

					if(!addToCart) {
						$addToCartButton.addClass('disabled');
					}else{
						$addToCartButton.removeClass('disabled');
					}

					this.$selectors = $('div.product-options', this.$skuPicker);
					var $target = $('.'+this.options.dropdownClass),
					$targetOption = $target.find('.'+this.options.selectedClass),
					optionDropDownId = $targetOption.attr('data-id') || '',
					$targetSwatchOption = $('.'+this.options.optionSwatches).find('.'+this.options.selectedClass),
					optionSwatchId = $targetSwatchOption.attr('data-id') || '';
					if(optionDropDownId !== ''){
						this.changeAttribute($targetOption);
					}
					if(optionSwatchId !== ''){
						this.changeAttribute($targetSwatchOption);
					}
				}
				else {
					// No skus, sold out.
					return;
				}

				// action listeners (click/change)
				this.$skuPicker.on('click', '.' + self.options.swatchClass, function (e) {
					// listen for clicks on swatch templates
					var $target = $(e.currentTarget);
					self.changeAttribute($target);
				}).on('change', '.' + self.options.dropdownClass ,function(e){
					//listen for change if we use the dropdown template
					var $target = $(e.target.options[e.target.selectedIndex]);
					self.changeAttribute($target);
				});

			}

		},
		removeDuplicates :  function(originalArray, prop) {
			var newArray = [],
			lookupObject  = {},
			i;

			for(i in originalArray) {
				lookupObject[originalArray[i][1]] = originalArray[i];
			}

			// To ensure that any "selected" variant is not
			// being removed as a dupe
			for(i in originalArray) {
				if(originalArray[i][3]===true) {
					lookupObject[originalArray[i][1]] = originalArray[i];
				}
			}

			for(i in lookupObject) {
				newArray.push(lookupObject[i]);
			}
			return newArray;
		 },
		changeAttribute : function ($target) {
			var // autoSelect = true,
					optionType = '',
					optionId = '',
					triggerOptionType = $target.attr('data-typeid'),
					triggerOptionId = $target.attr('data-id'),
					selectedOptions = {},
					selectedClass = this.options.selectedClass;

			// select this variant
			this.selectVariant(triggerOptionType, triggerOptionId);

			// Generate SelectedOptions
			this.$selectors.each(function () {
				var $this = $(this);
						optionType = $this.attr('data-typeid');
						optionId =  $this.find('.' + selectedClass).attr('data-id') || '';
				if (optionId !== '') {
					selectedOptions[optionType] = optionId;
				}
			});

			this.updateSelectors({'selectedOptions': selectedOptions, 'triggerOptionType': triggerOptionType});
		},
		updateSelectors : function (options) {
			var self = this,
					selectedOptions = options.selectedOptions,
					// autoSelect = (options.autoSelect !== undefined) ? options.autoSelect : false,
					availableOptions = this.productData.getFilteredOptions(selectedOptions),
					variantTypes = this.productData.getVariantTypes(),
					availableSkus = this.productData.data.skus,
					optionId = '',
					skuSelected = true,
					isDisabled = false,
					inStock = true,
					skuEnabled = true,
					swatchClass = this.options.swatchClass,
					dropdownClass = this.options.dropdownClass,
					selectedClass = this.options.selectedClass,
					addToCart = false,
					$addToCartButton = $('.add-to-cart-submit'),
					$bopisOrderRadio = $('.bopis-order'),
					$shipHomeRadio = $('#shipping-order');

			this.$selectors.each(function(i) {
				var $selector = $(this),
						optionType = $selector.attr('data-typeid'),
						selectedValue = $selector.find('.' + selectedClass).attr('data-id'),
						enabledItems = 0,
						option;

				// update dropdowns
				$selector.find('option').each(function(index) {
					if (index > 0) {
						var $this = $(this);
						optionId = $(this).attr('data-id');
						isDisabled = (availableOptions[optionType] === undefined || availableOptions[optionType].variants[optionId] === undefined) ? true : false;

						if (isDisabled) {
							$this.addClass('hide').removeClass(selectedClass);
						}
						else {
							$this.removeClass('hide');
							addToCart = true;
							enabledItems++;
						}
					}
				});

				// update color swatches
				$selector.find('.' + swatchClass).each(function() {
					var $this = $(this);
					optionId = $(this).attr('data-id');
					isDisabled = (availableOptions[optionType] === undefined || availableOptions[optionType].variants[optionId] === undefined) ? true : false;

					if (isDisabled) {
						$this.addClass('hide').removeClass(selectedClass);
					}
					else {
						$this.removeClass('hide');
						enabledItems++;
					}
				});


				// auto-select the single item (only if there wasn't already a selection), then re-update in case it narrows other selectors.
				// if (enabledItems == 1 && selectedOptions[optionType] === undefined && autoSelect === true) {
				// 	for (option in availableOptions[optionType].variants) {
				// 		selectedOptions[optionType] = availableOptions[optionType].variants[option].id;
				// 	}
				// 	self.updateSelectors({'selectedOptions': selectedOptions, 'updateQty': updateQty});
				// 	return;
				// }

				//resets or selects item in this VariantGroup
				if (selectedOptions[optionType] === undefined) {
					self.selectVariant(optionType);
					skuSelected = false;
				}
				else {
					selectedValue = selectedOptions[optionType];
					self.selectVariant(optionType, selectedValue);
				}

			});

			if(!addToCart) {
				console.log('not addtocart');
				$addToCartButton.addClass('disabled');
			}else{
				console.log(' addtocart');
				$addToCartButton.removeClass('disabled');
			}

			// update sku information on page
			var $productSku = $('.product-sku'),
					$skuNumber = $('.sku-number'),
					$productSkuModel = $('.product-sku-model'),
					$modelNumber = $('.model-number'),
					$price = this.$container.find('.product-price'),
					priceHtml = '',
					$inventoryEmail = $('.no-inventory-email-trigger');

			if (skuSelected !== false) {
				var selectedSku = self.productData.getFilteredSkus(selectedOptions)[0],
						productId = this.productData.productId;
				this.catalogRefId = selectedSku.catalogRefId;

				$skuNumber.html(this.catalogRefId);
				$productSku.removeClass('hide');
				$modelNumber.html(selectedSku.modelNumber);
				if(Boolean(selectedSku.modelNumber)){
					$productSkuModel.removeClass('hide');
				}else{
					if(!$productSkuModel.hasClass('hide')){
						$productSkuModel.addClass('hide');
					}
				}
				if (selectedSku.hidePrice == 'true') {
					priceHtml = Mustache.render(TEMPLATES.templateHidePrice);
				} else {
					if (selectedSku.sale == 'true') {
						if (selectedSku.clearance == 'true') {
							priceHtml = Mustache.render(TEMPLATES.templateClearancePrice, {originalPrice: selectedSku.originalPrice.replace('$', ''), salePrice: selectedSku.salePrice.replace('$', '')});
						} else {
							priceHtml = Mustache.render(TEMPLATES.templateSalePrice, {originalPrice: selectedSku.originalPrice.replace('$', ''), salePrice: selectedSku.salePrice.replace('$', '')});
						}
					}
					else {
						priceHtml = Mustache.render(TEMPLATES.templateRegularPrice, {regularPrice: selectedSku.regularPrice.replace('$', '')});
					}
				}

				
				// enable/disable add to cart button
				if (selectedSku.inventory == '0' || selectedSku.inventory == null || selectedSku.inventory == '') {
					$addToCartButton.addClass('disabled');
					//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + this.catalogRefId);
				}
				else {
					$addToCartButton.removeClass('disabled');
					
					if(selectedSku.bopisOnlyAvailable == 'true' && selectedSku.productOutOfStock == 'true') {
						$shipHomeRadio.attr('disabled',true);
						$bopisOrderRadio.attr('checked', true);
					} else {
						$shipHomeRadio.attr('disabled',false);
					}
					//$inventoryEmail.addClass('hide');
				}
				
				if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
					var storeId = $('#bopis-store-id').val() !== '' ? $('#bopis-store-id').val() : $('#homestore').val();
					if(selectedSku !== '' && storeId !== '' && storeId !== undefined){
						$.ajax(CONSTANTS.contextPath + '/sitewide/json/updateMyHomeStoreSuccess.jsp?productId=' +this.productId + '&skuId=' + this.catalogRefId + '&storeId=' + storeId, {
							cache: false,
							dataType : 'json',
							success: function(data) {
								$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, data));
								if(data.eligible !== "true"){
									$addToCartButton.addClass('disabled');
									//document.getElementById('bopis-order').addClass('disabled');
									$bopisOrderRadio.attr('disabled',true);
									//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + this.catalogRefId);
								}else{
									$addToCartButton.removeClass('disabled disable-add-to-cart');
									$bopisOrderRadio.attr('disabled',false);
									//$inventoryEmail.addClass('hide');
								}
							},
							error: function() {
								console.log("error on update bopis location info");
							}
						});
					}
				}
			}
			else {
				var product = self.productData.data;
				this.catalogRefId = '';
				$productSku.addClass('hide');
				
				if (product.hidePrice == 'true') {
					priceHtml = Mustache.render(TEMPLATES.templateHidePrice);
				} else {
					if (product.priceRange == 'true') {
						priceHtml = Mustache.render(TEMPLATES.templateRegularPrice, {regularPrice: product.minPrice.replace('$', '') + ' - ' + product.maxPrice.replace('$', '')});
					}
					else {
						if (product.sale == 'true') {
							priceHtml = Mustache.render(TEMPLATES.templateSalePrice, {originalPrice: product.originalPrice.replace('$', ''), salePrice: product.salePrice.replace('$', '')});
						}
						else {
							priceHtml = Mustache.render(TEMPLATES.templateRegularPrice, {regularPrice: product.regularPrice.replace('$', '')});
						}
					}
				}
			}
			$price.html(priceHtml);
			this.$formCatalogRefId.val(this.catalogRefId);
		},
		selectVariant : function (variantTypeId, variantId) {
			var selectedClass = this.options.selectedClass,
					$selectedItem;

			if (variantId) {
				$selectedItem = this.$skuPicker.find('[data-id="' + variantId + '"]');

				// already selected? exit.
				if ($selectedItem.hasClass(selectedClass)) {
					return;
				}

				// change the selected class
				this.$skuPicker.find('[data-typeid=' + variantTypeId + ']').removeClass(selectedClass);
				$selectedItem.addClass(selectedClass);

				// clear errors
				this.$skuPicker.find('[data-typeid=' + variantTypeId + '] .product-option-errors').html('');
				UTILITIES.form.hideErrors($('#add-to-cart-form'));
			}
			else {
				// deselect all options for this variant type
				this.$skuPicker.find('[data-typeid=' + variantTypeId + ']').removeClass(selectedClass);
			}
		},
		showSelectionErrors : function() {
			var that = this,
					selectedClass = this.options.selectedClass;
			this.$selectors.each(function(i) {
				var $selector = $(this),
						variantId,
						variantName,
						errorMessage = '';

				if ($selector.find('.' + selectedClass).length === 0) {
					variantId = $selector.attr('data-typeid');
					variantName = that.productData.getVariantTypeName(variantId);
					if (variantName !== '') {
						errorMessage = 'Please select a ' + variantName;
					}
					else {
						errorMessage = 'Please select all options';
					}
					$selector.find('.product-option-errors').html(errorMessage);
				}
			});
		}
	};

})(this, window.jQuery, "KP");

/* =========================================================
 * =========================================================
 * profile.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Simple utility to request profile update asynchronously
 * and update cart and status display.
 *
 * The following rules control the ajax request
 *
 *  1) If user has a cookie, and it is valid for this session, use the information from the cookie.
 *  2) If the user has a cookie, but it is stale ( > sessionTimeout ), then make profile request and update the cookie.
 *  3) If the user has no cookie, do nothing.
 *
 *  For the following conditions, make this call to force a request for the new cookie
 *  getProfileStatus(true);
 *  4) If the user adds an item to their cart, make a fresh profile request, and update the cookie
 *  5) if the user changes any other profile data in the cookie (updates first name in profile for example) , make a fresh profile request, and update the cookie
 *
 *  For the following, make this call to force a request for a new cookie on the next page load (assumes user has cookie)
 *  resetProfileStatus();
 *  6) if the user logs in, update cookie on success
 *
 *  For the following, make this call to remove the cookie
 *  clearProfileCookie();
 *  7) if the user logs out, remove the cookie.
 *
 *  We have five user states as follows:
 *
 *  0 - Anonymous user
 *  This is an anonymous user who has not interacted with the site in a way in which we wish to persist their profile.
 *  That is, they haven’t added anything to their cart. They are browsing and not shopping.
 *
 *  1 - Guest User
 *  Also an anonymous user, but we wish to persist the profile. They have not signed in, so are shopping anonymously,
 *  but they have added something to their cart, so we have dropped a cookie to recognize them on their next visit.
 *
 *  2 - Persisted guest
 *  This guest user was recognized by a cookie dropped in their previous session. (see user status 1)
 *
 *  3 - Soft-logged-in registered
 *  This user has logged into their account in a previous session and then left the site without signing out of their
 *  account. We know who they are (profile, rewards, favorite store, cart contents), but they have not logged in during
 *  the current session.
 *
 *  4 - Hard-logged-in registered
 *  This user has logged in (or registered) during the current session.
 *
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";
	var CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			persistPages = [
				/* When the user visits these pages they are interacting with the site in a way we wish to persist their profile info */
				'^' + CONSTANTS.contextPath + '/account/login.jsp',				// account login / registration
				'^' + CONSTANTS.contextPath + '/checkout/login.jsp',			// checkout login
				'^' + CONSTANTS.contextPath + '/checkout/cart.jsp'				// cart page
			],
			profileController = {};

	// private functions
	function updateProfileStatus(profileData) {
		var jsessionId;
		if (profileData !== undefined) {
			setLoginStatus(profileData);
			updateCartQuantity(profileData);
			showUserStatus(profileData);
			if (!profileData.isTransient) {
				jsessionId = $.cookie('JSESSIONID');
				profileData.jsessionId = jsessionId;
				$.cookie("user-data", JSON.stringify(profileData), { expires: 30, path: '/' });
			}
		}
	}

	function updateCartQuantity(data) {
		$(".cart-count").text(data.cartCount);
		if (data.cartCount > 0) {
			if (loggingDebug) {
				console.log(namespace + '.profile cart count: ', data.cartCount);
			}
			$('.mini-cart-expanded').removeClass('empty');
		}
	}

	function setLoginStatus(data) {
		profileController.loginStatus = data.statusValue;
		var userType;
		if (profileController.loginStatus == 4) {
			userType = 'fully-authenticated';
		}
		else if (profileController.loginStatus == 3) {
			userType = 'partially-authenticated';
		}
		else {
			userType = 'guest';
		}
		$('html').removeClass('guest partially-authenticated fully-authenticated').addClass(userType);
	}

	function showUserStatus(data) {
		var greetingName = data.firstname;
		$('.js-username').html(greetingName);
	}

	function isPersistedInteraction() {
		var url = window.location.pathname,
				doPersist = false;

		if (persistPages) {
			for (var x = 0; x < persistPages.length; x++) {
				var pageRegEx = new RegExp(persistPages[x], 'i');
				if (pageRegEx.test(url)) {
					doPersist = true;
					break;
				}
			}
		}
		return doPersist;
	}

	profileController.getProfileStatus = function (hardRefresh) {
		var profileUrl = CONSTANTS.contextPath + '/sitewide/json/status.jsp',
				forceReload = hardRefresh || false,
				jsessionId,
				userData = {
					statusValue : 0,
					cartCount : 0,
					isTransient : true
				}; //anon status

		/*
		 If this function is called with the hardRefresh set to true, make profile request.
		 If the user data cookie session id doesn't match the jsessionid value, we will force a new status request.
		 */

		/* checking cookie freshness to see if we need to reload */
		if ($.cookie("user-data")) {
			userData = JSON.parse($.cookie("user-data"));
			jsessionId = $.cookie("JSESSIONID");
			if (userData.jsessionId != jsessionId) {
				forceReload = true;
			}
		}

		/* We're doing something where we need to persist the user info */
		if (isPersistedInteraction() === true) {
			forceReload = true;
		}

		if (forceReload) {
			$.ajax({
				url: profileUrl,
				dataType: 'json',
				cache: false,
				success: function (data) {
					updateProfileStatus(data);
				},
				error: function () {
					updateProfileStatus(userData);
				}
			});
		} else {
			updateProfileStatus(userData);
		}
	};

	profileController.resetProfileStatus = function(){
		//resets the cookie jsessionid value, which will force it to reload on the next page load
		var userData;
		if ($.cookie("user-data")) {
			userData = JSON.parse($.cookie("user-data"));
			userData.jsessionId = '';
			$.cookie("user-data", JSON.stringify(userData), { expires: 30, path: '/' });
		}
	};

	profileController.clearProfileCookie = function(){
		// completely remove the cookie. This will set the user back to an anon state until we make another profile request.
		$.removeCookie('user-data', { path: '/' });
	};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].profileController = profileController;


}(this, window.jQuery, "KP"));

/*global jQuery, KP, s*/
/*jslint regexp: true*/
/**
 * should not create globals
 * create a KP mediator if one does not exist then install it
 * create one subscription for init
 */

(function (global, $, namespace, profile) {
	"use strict";

	/* internals */
	var loading = false,
			loggingDebug = namespace && namespace.config.loggingDebug;

	/**
	 * default config
	 * @type {null}
	 */
	profile.config = null;

	profile.profileData = null;

	profile.loadProfile = function() {
		if (loggingDebug) {
			console.log('profile: starting loadProfile');
		}
		if (loading) return;
		$.ajax({
			url: this.config.profileServiceUrl,
			success: profile.loadSuccess,
			error: profile.loadError,
			dataType: json
		});
		loading = true;
	};

	profile.loadSuccess = function(data, textStatus, jqXHR) {
    jstestdriver.console.log('Load success');
		profile.profileData = data;
		loading = false;
		this.publish("profile/profile response")(profile.profileData);
		if (loggingDebug) {
			console.log('profile: profile loaded successfully. profile is ' + profile.profileData);
		}
	};

	profile.loadError = function() {
		if (loggingDebug) {
			console.log('profile: starting loadError');
		}
		loading = false;
		this.publish("profile/error")();
	};

	profile.logOut = function() {
		profile.profileData = null;
		if (loggingDebug) {
			console.log('profile: logOut, setting profile to' + profile.profileData);
		}
	};

	profile.getProfile = function(){
		if (loggingDebug) {
			console.log('profile: getProfile, current profile is' + profile.profileData);
		}
		if (profile.profileData == null) {
			if (loggingDebug) {
				console.log('profile: profile was null. sending request.');
			}
			 this.loadProfile();
		} else {
			this.publish("profile/profile response")(profile.profileData);
		}
	};

	/**
	 * profile should not add handlers when not configured
	 * profile should not apply handlers twice
	 * @param config [{Object}]
	 */
	profile.init = function (config) {
		if (loggingDebug) {
			console.log('profile: initializing...');
		}
		var prev = this.config;
		this.config = config;
		if (!prev && this.config) {
			//bind dom events here
			//$doc.on('do.something', function (event) {
			//		profile.logout();
			//});
			this.subscribe("profile/logout", this.logOut, this);
			this.subscribe("profile/profile request", this.getProfile, this);
		}
		this.publish("profile/initialized")(this.config);
	};
	/**
	 * create initialization subscriber
	 * @lends profile.publish
	 * @lends profile.subscribe
	 */
	if (namespace && namespace.Mediator) {
		if (!namespace.mediator) {
			namespace.mediator = new namespace.Mediator();
		}
		namespace.mediator.installTo(profile);
		profile.subscribe("profile/init", profile.init, profile);
	}


}(this, jQuery, KP, {}));

/* =========================================================
 /* =========================================================
 * kp.promocode.js
 * Created by KnowledgePath Solutions
 * ==========================================================
 * Utility to control promo code behavior. The promo code widget
 * allows users to enter promo codes for item discounts applied
 * at checkout. When the user adds their code it will appear in
 * applied promo area under the form and give an option to view
 * details about the code in a modal.
 * You will have the ability to set how many promo codes users
 * are allowed to add in defaults.
 * ========================================================= */

(function (global, $, namespace) {
	// "use strict";

	var CONSTANTS = global[namespace].constants;
	var Promocode = function Promocode(element, options) {
				this.init ('promo-code', element, options);
			};

	function refreshTotalsPromo(data) {
		var updatedTotals,
				updatedAppliedPromos,
				currentCartItemCIDs = [];

		// loop through cart items and see if we need to delete any items
		$.each($('.order-item'), function(){
			currentCartItemCIDs.push($(this).data('ciid'));
		});

		// loop through cart items
		$.each(data.cartItems, function(index){
			var $orderItem = $('.order-item[data-ciid="' + this.commerceItemId + '"]');
			if ($orderItem.length > 0) {
				// item is on page, update item totals
				$orderItem.find('.item-total').html(this.itemSubtotal);
				$orderItem.find('.promo-line-item-msg').html(Mustache.render(global[namespace].templates.lineItemPromotions, data.cartItems[index]));

				// remove the item from the array of original items
				currentCartItemCIDs.pop(this.commerceItemId);
			}
			else {
				// item is new, refresh page to get new cart items
				window.location.reload();
			}
		});

		// if the original items array still has values, that means something was deleted.
		if (currentCartItemCIDs.length > 0) {
			window.location.reload();
		}

		// update the order totals
		updatedTotals = Mustache.render(global[namespace].templates.orderTotals, data);
		$('.totals').html(updatedTotals);

		// update gift card totals in checkout payment sections
		$.each(data.appliedGiftCards, function(){
			$('.amount-' + this.number).html(this.amount);
		});

		// remove all global promos from the data so we only show coupons in the coupon area
		if (typeof data.appliedCouponPromos !== 'undefined' && data.appliedCouponPromos.length > 0) {
			var newOrderDiscount = [];
			for (var j=0; j<data.appliedCouponPromos.length; j++) {
				if (data.appliedCouponPromos[j].discountType == 'coupon') {
					data.appliedCouponPromos[j].couponDetails = encodeURIComponent(data.appliedCouponPromos[j].couponDetails);
					newOrderDiscount.push(data.appliedCouponPromos[j]);
				}
			}
			data.appliedCouponPromos = newOrderDiscount;
			updatedAppliedPromos = Mustache.render(global[namespace].templates.appliedCoupons, data);
			$('.promo-applied-area').html(updatedAppliedPromos);
		}

		global[namespace].utilities.hideLoader();
	}

	Promocode.prototype = {
		constructor: Promocode,
		init : function init(type, element, options){
			var self = this;

			this.options = $.extend({}, $.fn.promocode.defaults, options);
			this.$element = $(element);

			// initialize apply promo listener
			self.applyPromo();

			// initialize remove promo listener
			self.removeApplied();

		},

		applyPromo : function(){
			var couponUpdateOptions = {
				type: 'post',
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					global[namespace].utilities.hideLoader();
					if (statusText == 'success') {
						var pageSource = "Shopping Cart";
						if (responseText.success == 'true') {
							$('#promo-code-field').val('').removeClass('error').blur();
							$('.promo-code-msg').empty();
							$('.promo-form-fields').addClass('hide');
							refreshTotalsPromo(responseText);
							$('.shipping-method-fields').load(CONSTANTS.contextPath + '/checkout/includes/shippingMethodAJAX.jsp');
							
							// Fire tag to log coupon apply status.
							pageSource = $("#fromCheckout").val() == 'true' ? "Checkout" : "Shopping Cart";
							KP.analytics.trackEvent(pageSource, pageSource, 'Coupon Apply', $(".promo-applied .coupon-code").text().trim());
						}
						else {
							$('#promo-code-field').addClass('error');
							global[namespace].utilities.form.showErrors($form, responseText, undefined, global[namespace].templates.errorPromoMessageTemplate);
							// Fire tag to log coupon error status.
							pageSource = $("#fromCheckout").val() == 'true' ? "Checkout" : "Shopping Cart";
							KP.analytics.trackEvent(pageSource, pageSource, 'Coupon Error', $("#promo-code-field").val());
						}
					}
					else {
						console.log('Malformed JSON : missing statusText parameter:');
						global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
					}
				},
				error: function(xhr, statusText, exception, $form) {
					console.log('AJAX Error:');
					global[namespace].utilities.hideLoader();
				//	global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
				}
			};
			$('#promo-form').ajaxForm(couponUpdateOptions);
		},
		removeApplied : function(){
			var couponRemoveOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					global[namespace].utilities.hideLoader();
					if (statusText == 'success') {
						if (responseText.success == 'true') {
							refreshTotalsPromo(responseText);
							$('.shipping-method-fields').load(CONSTANTS.contextPath + '/checkout/includes/shippingMethodAJAX.jsp');
							$('.promo-applied-area').empty();
							$('.promo-form-fields').removeClass('hide');
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.showErrors($form, responseText);
						}
					}
					else {
						console.log('Malformed JSON : missing statusText parameter:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
					}
				},
				error: function(xhr, statusText, exception, $form) {
					console.log('AJAX Error:');
					global[namespace].utilities.hideLoader();
					global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
				}
			};
			$('#promo-remove-form').ajaxForm(couponRemoveOptions);

			$('body').on('click', '.remove-link', function() {
				$('#promo-remove-submit').trigger('click');
			});
		},
	};

	$.fn.promocode = function promocode(option) {
		var el = this,
				options = $.extend({}, $.fn.promocode.defaults, typeof option === 'object' && option),
				args = Array.prototype.slice.call( arguments, 1 );
		return el.each(function () {
			var data = $.data(this, 'promocode');
			if (!data) {
				$.data(this, 'promocode', (data = new Promocode(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				} else if (typeof option == 'string') {
					data[option].apply(data, args);
				}
			}
		});
	};

	$.fn.promocode.Constructor = Promocode;

	$(function () {
		$('[data-promocode]').promocode();
	});

}(this, window.jQuery, "KP"));

/* =========================================================
 * kp.quantify.js
 * =========================================================

 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Quantify = function Quantify(element, options) {
				this.init ('quantify', element, options);
			},
			CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;

	function updateStates(quantify, val) {
		quantify.$minus.removeClass('inactive').attr('tabindex', '0').attr('aria-disabled', false);
		quantify.$plus.removeClass('inactive').attr('tabindex', '0').attr('aria-disabled', false);
		if (val <= quantify.options.min_quantity){
			quantify.$minus.addClass('inactive').attr('tabindex', '-1').attr('aria-disabled', true);
		}
		if (val >= quantify.options.max_quantity){
			quantify.$plus.addClass('inactive').attr('tabindex', '-1').attr('aria-disabled', true);
		}
	}

	//PUBLIC
	Quantify.prototype = {
		constructor: Quantify,
		init: function init(type, element, options) {
			if (loggingDebug) {
				console.debug('init quantify with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}

			var self = this,
					minQuantity,
					maxQuantity,
					dataFree;

			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.$plus = this.$element.find('.plus-icon');
			this.$minus = this.$element.find('.minus-icon');
			this.$counter = this.$element.find('.counter');
			this.$totalQty = this.$element.find('#totalQuantity');
			this.$freeQty = this.$element.find('#freeQuantity');

			maxQuantity = this.$element.attr('data-max');
			if (typeof maxQuantity != 'undefined') {
				this.options.max_quantity = parseInt(maxQuantity);
			}
			
			// 2414 Adding this to handle free gift quantity
			// separate from manually added skus of the gift
			dataFree = this.$element.attr('data-free');
			if (typeof dataFree != 'undefined') {
				this.options.data_free = parseInt(dataFree);
			}
			
			minQuantity = this.$element.attr('data-min');
			if (typeof minQuantity != 'undefined') {
				this.options.min_quantity = parseInt(minQuantity);
			}

			if (isNaN(parseInt(self.$counter.val()))) {
				self.$counter.val(self.options.min_quantity);
			}

			this.$element.on('click', '.plus-icon', function(){
				self.increment();
			});

			this.$element.on('click', '.minus-icon', function(){
				if (isNaN(parseInt(self.$counter.val()))) {
					self.$counter.val(self.options.min_quantity);
					return;
				}
				self.decrement();
			});

			//prevent non numbers
			this.$counter.keypress(function(e) {
				var key_codes = [48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 0, 8];

				if ($.inArray(e.which, key_codes) == -1) {
					e.preventDefault();
				}
			});

			this.$counter.keyup(function(e) {
				var currentValue = parseInt(self.$counter.val());
				var totalQty = currentValue;
				if(parseInt(self.$freeQty.val()) > 0) {
					totalQty = currentValue + parseInt(self.$freeQty.val());
				}

				if (currentValue > self.options.max_quantity) {
					//no!
					self.$counter.val(self.options.max_quantity).change();
					totalQty = self.options.max_quantity - parseInt(self.options.data_free);
				}

				if (currentValue < self.options.min_quantity) {
					//no!
					self.$counter.val(self.options.min_quantity).change();
					totalQty = self.options.min_quantity + parseInt(self.options.data_free);
				}

				if (isNaN(currentValue)) {
					//no!
					self.$counter.val(self.options.min_quantity).change();
					totalQty = self.options.min_quantity + parseInt(self.options.data_free);
				}
				self.$totalQty.val(totalQty);
				updateStates(self, currentValue);
			}).keyup();

		},

		increment : function (){
			if (isNaN(this.$counter.val()) || this.$counter.val() === '') {
				this.$counter.val(0);
			}
			//
			var newQuantity =  parseInt(this.$counter.val())  + 1;
			var totalQty = parseInt(this.$totalQty.val()) + 1;
			//var totalQty = newQuantity + parseInt(this.options.data_free);
			if (newQuantity > this.options.max_quantity){
				return;
			}
			this.$counter.val(newQuantity).change();
			this.$totalQty.val(totalQty);
			updateStates(this, newQuantity);
			this.$element.trigger('increment');
		},
		decrement : function (){
			if (isNaN(this.$counter.val()) || this.$counter.val() === '') {
				this.$counter.val(0);
			}
			// + parseInt(this.options.data_free)
			var newQuantity =  parseInt(this.$counter.val())  - 1;
			//var totalQty = newQuantity + parseInt(this.options.data_free);
			var totalQty = parseInt(this.$totalQty.val()) - 1;
			
			if (newQuantity < this.options.min_quantity){
				return;
			}
			this.$counter.val(newQuantity).change();
			this.$totalQty.val(totalQty);
			updateStates(this, newQuantity);
			this.$element.trigger('decrement');
		}
	};

	$.fn.quantify = function quantify(option) {
		var el = this,
				options = $.extend({}, $.fn.quantify.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'quantify');
			if (!data) {
				$.data(this, 'quantify', (data = new Quantify(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.quantify.defaults = {
		max_quantity: 99,
		min_quantity: 0,
		data_free: 0
	};

	$.fn.quantify.Constructor = Quantify;

	$(function () {
		$('[data-quantify]').quantify();
	});

}(this, window.jQuery, "KP"));

/* =========================================================
 /* =========================================================
 * kp.tabs.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Utility to control tabs behavior. Tabs are a set of stacked
 * divs revealed by cliking title boxes at top.
 * ========================================================= */

$(function(){
	(function (global, $, namespace) {

		"use strict";

		var Tabs = function Tabs(element, options) {
					this.init ('tabs', element, options);
				};

		Tabs.prototype = {
			constructor: Tabs,
			init : function init(type, element, options){
				var self = this,
						elements = {},
						$element, $trigger, $target;

				if (element instanceof jQuery) {
					$element  = element;
				} else {
					$element = $(element);
				}

				this.options = $.extend({}, $.fn[type].defaults, options);
				this.$element = $(element);
				this.$trigger = this.$element.find('.tab-title');
				this.$target = this.$element.find('.tab-body');

				this.$element.on('click', '.tab-title', function(){
					self.changeTab(this);
				});
			},

			changeTab: function (el){
				this.$trigger.removeClass('active');
				this.$target.removeClass('active');

				var t = $(el).attr('aria-controls');
				$(el).addClass('active');
				$('#' + t).addClass('active');
			}
		};

		$.fn.tabs = function tabs(option) {
			var el = this,
					options = $.extend({}, $.fn.tabs.defaults, typeof option === 'object' && option),
					args = Array.prototype.slice.call( arguments, 1 );
			return el.each(function () {
				var data = $.data(this, 'tabs');
				if (!data) {
					$.data(this, 'tabs', (data = new Tabs(this, options)));
				} else {
					if (typeof option === 'object') {
						$.extend(data.options, option);
					} else if (typeof option == 'string') {
						data[option].apply(data, args);
					}
				}
			});
		};

		$.fn.tabs.Constructor = Tabs;


		$(function () {
			$('[data-tabs]').tabs();
		});

	}(this, window.jQuery, "KP"));
});


/* =========================================================
 /* =========================================================
 * kp.tooltip.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Tooltips are lightweight utilities activated by hovering
 * over an icon to get more info without needing a modal.
 * ========================================================= */

(function (global, $, namespace) {
	"use strict";

	var Tooltip = function Tooltip(element, options) {
			this.init ('tooltip', element, options);
		};

	//PUBLIC
	Tooltip.prototype = {
		constructor: Tooltip,
		init: function init(type, element, options) {
			// if (loggingDebug) {
			// 	console.debug('init tooltip with options:');
			// 	console.debug(Array.prototype.slice.call(arguments));
			// }
			var self = this;
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			self.create(this.$element);

			// set timer for displaying the tooltip
			function _startShow(elt, $this, immediate) {
				if (elt.timer) {
					return;
				}

				if (immediate) {
					elt.timer = null;
					self.showTip($this);
				} else {
					elt.timer = setTimeout(function () {
						elt.timer = null;
						self.showTip($this);
					}.bind(elt), self.options.hover_delay);
				}
			}

			function _startHide(elt, $this) {
				if (elt.timer) {
					clearTimeout(elt.timer);
					elt.timer = null;
				}
				self.hide($this);
			}

			$('body').off('.tooltip')
				.on('mouseenter.' + namespace + '.tooltip mouseleave.' + namespace + '.tooltip touchstart.' + namespace + '.tooltip', '[data-tooltip]', function (e) {
					var $this = $(this),
						is_touch = false;

					if ($this.hasClass('open')) {
						if (Modernizr.touchevents) {
							e.preventDefault();
						}
//						self.hide($this);
					}
					else {
						if (/enter|over/i.test(e.type)) {
							_startShow(this, $this);
						} else if (e.type === 'mouseout' || e.type === 'mouseleave') {
							_startHide(this, $this);
						} else {
							_startShow(this, $this, true);
						}
					}
				})
				.on('mouseleave.' + namespace + '.tooltip touchstart.' + namespace + '.tooltip', '[data-tooltip].open', function (e) {
					_startHide(this, $(this));
				});
		},

		showTip : function ($target) {
			return this.show($target);
		},

		getTip : function ($target) {
			var selector = this.selector($target),
				tip = null;

			if (selector) {
				tip = $('span[data-selector="' + selector + '"]' + this.options.tooltip_class);
			}
			return (typeof tip === 'object') ? tip : false;
		},

		// add unique data-selector & aria-describedby attributes
		selector : function ($target) {
			var dataSelector = $target.attr('data-selector');

			if (typeof dataSelector != 'string') {
				dataSelector = 'tooltip-' + Math.random().toString().substring(2,8);
				$target
				.attr('data-selector', dataSelector)
				.attr('aria-describedby', dataSelector);
			}
			return dataSelector;
		},

		create : function ($target) {
			var self = this,
				tip_template = this.options.tip_template;

			if (typeof this.options.tip_template === 'string' && window.hasOwnProperty(this.options.tip_template)) {
				tip_template = window[this.options.tip_template];
			}
			// add the class names set in inheritable_classes function
			var $tip = $(tip_template(this.selector($target), $('<div></div>').html($target.attr('title')).html())),
				classes = this.inheritable_classes($target);

			// add the tooltip to body
			$tip.addClass(classes).appendTo(this.options.append_to);

			if (Modernizr.touchevents) {
				$tip.on('touchstart.' + namespace + '.tooltip', function (e) {
					self.hide($target);
				});
			}
			// remove the default title hover behavior
			$target.removeAttr('title').attr('title', '');
		},

		// set the position of the tooltip depending on classes set in markup
		reposition : function (target, tip, classes) {
			var width, nub, nubHeight, nubWidth, column, objPos;

			tip.css('visibility', 'hidden').show();

			width = target.data('width');
			nub = tip.children('.nub');
			nubHeight = nub.outerHeight();
			nubWidth = nub.outerHeight();

			if ($('html').hasClass('mobile')) {
				tip.css({'width' : '100%'});
			} else {
				tip.css({'width' : (width) ? width : 'auto'});
			}

			objPos = function (obj, top, right, bottom, left, width) {
				return obj.css({
				'top' : (top) ? top : 'auto',
				'bottom' : (bottom) ? bottom : 'auto',
				'left' : (left) ? left : 'auto',
				'right' : (right) ? right : 'auto'
				}).end();
			};
			objPos(tip, (target.offset().top + target.outerHeight() + 10), 'auto', 'auto', target.offset().left);

			if ($('html').hasClass('mobile')) {
				objPos(tip, (target.offset().top + target.outerHeight() + 10), 'auto', 'auto', 12.5, $(this.scope).width());
				tip.addClass('tip-override');
				objPos(nub, -nubHeight, 'auto', 'auto', target.offset().left);
			} else {
				var left = target.offset().left;

				objPos(tip, (target.offset().top + target.outerHeight() + 10), 'auto', 'auto', left);
				// reset nub from small styles, if they've been applied
				if (nub.attr('style')) {
					nub.removeAttr('style');
				}

				tip.removeClass('tip-override');
				if (classes && classes.indexOf('tip-top') > -1) {
					objPos(tip, (target.offset().top - tip.outerHeight()), 'auto', 'auto', left)
					.removeClass('tip-override');
				} else if (classes && classes.indexOf('tip-left') > -1) {
					objPos(tip, (target.offset().top + (target.outerHeight() / 2) - (tip.outerHeight() / 2)), 'auto', 'auto', (target.offset().left - tip.outerWidth() - nubHeight))
					.removeClass('tip-override');
					nub.removeClass('rtl');
				} else if (classes && classes.indexOf('tip-right') > -1) {
					objPos(tip, (target.offset().top + (target.outerHeight() / 2) - (tip.outerHeight() / 2)), 'auto', 'auto', (target.offset().left + target.outerWidth() + nubHeight))
					.removeClass('tip-override');
					nub.removeClass('rtl');
				}
			}

			tip.css('visibility', 'visible').hide();
		},
		//sets the class names to use in the markup
		inheritable_classes : function ($target) {
			var inheritables = ['tip-top', 'tip-left', 'tip-bottom', 'tip-right', 'radius', 'round'],
				classes = $target.attr('class'),
				filtered = classes ? $.map(classes.split(' '), function (el, i) {
					if ($.inArray(el, inheritables) !== -1) {
					return el;
					}
				}).join(' ') : '';
			return $.trim(filtered);
		},

		show : function ($target) {
			var $tip = this.getTip($target);
			this.reposition($target, $tip, $target.attr('class'));
			$target.addClass('open');
			$tip.fadeIn(150);
		},

		hide : function ($target) {
			var $tip = this.getTip($target);
			$tip.fadeOut(150, function () {
				$target.removeClass('open');
			});
		},

		off : function () {
			var self = this;
			this.off('.' + namespace + '.tooltip');
			$(this.options.tooltip_class).each(function (i) {
				$('[' + self.attr_name() + ']').eq(i).attr('title', $(this).text());
			}).remove();
		},
	};

	$.fn.tooltip = function tooltip(option) {
		var el = this,
			options = $.extend({}, $.fn.tooltip.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'tooltip');
			if (!data) {
				$.data(this, 'tooltip', (data = new Tooltip(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.tooltip.defaults = {
		tooltip_class : '.tooltip',
		append_to : 'body',
		hover_delay : 200,
		show_on : 'all',
		tip_template : function (selector, content) {
			return '<span data-selector="' + selector + '" id="' + selector + '" class="' + 'tooltip' + '" role="tooltip" tabindex="-1">' + content + '<span class="nub"></span></span>';
		}
	};

	$.fn.tooltip.Constructor = Tooltip;

	$(function () {
		$('[data-tooltip]').on('focus', function(){
			$(this).trigger('mouseenter');
		});
		$('[data-tooltip]').on('blur', function(){
			$(this).trigger('mouseleave');
		});
		$('[data-tooltip]').tooltip();
	});

}(this, window.jQuery, "KP"));

/* =========================================================
 * kp.typeahead.js
 * =========================================================

 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Typeahead = function Typeahead(element, options) {
			this.init ('typeahead', element, options);
		},
		CONSTANTS = global[namespace].constants,
		loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;

	//PUBLIC
	Typeahead.prototype = {
		constructor: Typeahead,
		init: function init(type, element, options) {
			if (loggingDebug) {
				console.debug('init typeahead with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}

			var self = this,
				counter = 0,
				typeaheadResultsSelector = '.typeahead',
				typeaheadSuggestionsSelector = '.typeahead-suggestions',
				typeaheadDetailsSelector = '.typeahead-details',
				$results = $(typeaheadResultsSelector),
				$suggestions = $(typeaheadSuggestionsSelector),
				$details = $(typeaheadDetailsSelector),
				$body = $('body');

			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.$results = $(typeaheadResultsSelector);
			this.$suggestions = this.$results.children(typeaheadSuggestionsSelector);
			this.$details = this.$results.children(typeaheadDetailsSelector);

			this.$element.keyup(function(e) {
				var searchTerm = String($(this).val());
				if (searchTerm.length >= self.options.trigger_num_chars) {
					//no!
					$details.html('');
					self.showSuggestions(searchTerm, $suggestions, $details);
				}
				else {
					self.hideResults();
				}
			});

			$body.on('mouseenter', '.typeahead-suggestions li a', function(e){
				self.showDetails($(this).data('detail-url'), $details);
			});
			$body.on('mouseleave', '.typeahead-suggestions li a', function(e){
				self.showDetails($(this).data('detail-url'), $details);
			});
			$body.on('focus', '.typeahead-suggestions li a', function(){
				self.showDetails($(this).data('detail-url'), $details);
			});

			// make typeahead details keyboard accessible
			$body.on('keydown', '.typeahead-suggestions a', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				if (keycode == 9 && !e.shiftKey) {
					if ($('.typeahead-details a').length > 0) {
						e.preventDefault();
						$('.typeahead-details li:first a').focus();
					}
				}
			});
			$body.on('keydown', '.typeahead-details a:last', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				if (keycode == 9 && !e.shiftKey) {
					var $next = $('.typeahead-suggestions a[data-detail-url="' + $('.typeahead-details').attr('data-detail-url') + '"]').parent().next();
					if ($next.length > 0) {
						e.preventDefault();
						$next.find('a').focus();
					}
					else {
						e.preventDefault();
						$('.keyword-search .keyword-search-button').focus();
						self.hideResults();
					}
				}
			});
			$body.on('keydown', '.typeahead a', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				if (keycode == 13 || keycode == 32) {
					window.location.href = this.href;
				}
			});

		},
		hideResults : function (){
			this.$element.attr('aria-expanded', false);
			this.$results.addClass('hide');
			this.$suggestions.empty();
			this.$details.empty();
		},
		showSuggestions : function (searchTerm, $suggestions, $details) {
			var self = this,
				$results = this.$results,
				$element = this.$element;
			$.ajax({
//				url: global[namespace].constants.contextPath + '/sitewide/json/typeahead.jsp', // test URL
				url: global[namespace].constants.contextPath + '/typeahead/suggest/' + searchTerm + '.js',
				dataType: 'json',
				success: function(data){
					var query = data.searchTerm,
						queryBold = '<strong>' + query + '</strong>',
						suggestions = data.results,
						output = '',
						count = 0;

					// only show suggestions if there are some
					if (suggestions.length > 0) {
						// make query string bold in suggestions list
						$(suggestions).each(function(e){
							data.results[count].term = this.term.replace(new RegExp(query, 'g'), queryBold);
							count++;
						});

						// apply mustache template
						output = Mustache.render(global[namespace].templates.typeaheadSuggestionsTemplate, data);
						$suggestions.html(output);

						// call the first detailUrl
						if (suggestions.length > 0) {
							self.showDetails(suggestions[0].detailUrl, $details);
						}
						else {
							$details.html();
						}

						// hide results on click
						$('body').on('click', function(){
							self.hideResults();
						});

						$results.removeClass('hide');
						$element.attr('aria-expanded', true);
					}
				},
				error: function(data){
					console.log('error: ', data);
				}
			});
		},
		showDetails : function (detailUrl, $details) {
			var self = this;
			$.ajax({
				url: global[namespace].constants.contextPath + detailUrl,
				dataType: 'json',
				success: function(data){
					if (loggingDebug) {
						console.log(data);
					}

					var sections = data.sections,
						resultsTop = {},
						resultsBottom = [],
						outputTop = '',
						outputBottom = '';

					for (var i=0; i<sections.length; i++) {
						if ($(sections)[i].title == 'Products') {
							resultsTop = sections[i];
						}
						else {
							resultsBottom.push(sections[i]);
						}
					}

					// apply mustache template
					outputTop = Mustache.render(global[namespace].templates.typeaheadDetailsTopTemplate, resultsTop);
					outputBottom = Mustache.render(global[namespace].templates.typeaheadDetailsBottomTemplate, resultsBottom);
					$details.attr('data-detail-url', detailUrl);
					$details.html(outputTop + outputBottom);
				},
				error: function(data){
					console.log('error: ', data);
				}
			});
		}
	};

	$.fn.typeahead = function typeahead(option) {
		var el = this,
			options = $.extend({}, $.fn.typeahead.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'typeahead');
			if (!data) {
				$.data(this, 'typeahead', (data = new Typeahead(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.typeahead.defaults = {
		trigger_num_chars:3
	};

	$.fn.typeahead.Constructor = Typeahead;


	$(function () {
		$('[data-typeahead]').typeahead();
	});


}(this, window.jQuery, "KP"));

/* kp.validate.js
 *
 * Usage:
 *
 * For each field you wish validated, add a data-validation attribute with
 * a space-separated list of validation rules (see rules object for names).
 * Optionally add a data-fieldname attribute with a user-friendly field name
 * that the error messages can use.
 *
 * To validate a form call the following:
 *
 *   $('#exampleFormId').validate('validateForm');
 *
 * Find the status of the form with this:
 *
 *   $('#exampleFormId').data('validate').isValid;
 *
 * To validate a form on submit, add an event listener for the submission. In this
 * example, any form with the attribute data-toggle=validate will be validated when submitted.
 *
 * $body.on('submit.validate.data-api', '[data-toggle=validate]', function (e) {
 *   var $this = $(this);
 *   $this.validate('validateForm');
 *   return $this.data('validate').isValid;
 * });
 *
 */


(function (global, $, namespace){

	"use strict";

	function testPattern (value, pattern) {
		var regExp = new RegExp(pattern, "");
		return regExp.test(value);
	}

	function checkCountry($form) {
		var $country = $form.find('[name=country]'),
				countryValue = 'us';
		if ($country.length > 0) {
			countryValue = $country.val().toLowerCase();
		}
		return countryValue;
	}

	function removeLeadingTrailingSpaces(value) {
		var leadingTrailingWhiteSpaces = /^\s+|\s+$/g;
		value = value.replace(leadingTrailingWhiteSpaces,"");
		return value;
	}

	/**--------------------------
	 //* Validate Date Field script- By JavaScriptKit.com
	 //* For this script and 100s more, visit http://www.javascriptkit.com
	 //* This notice must stay intact for usage
	 ---------------------------**/
	function checkdate(input){
		var validformat=/^\d{2}\/\d{2}\/\d{4}$/; //Basic check for format validity
		var returnval = false;
		if (!validformat.test(input)) {
			returnval = false;
		} else { //Detailed check for valid date ranges
			var monthfield = input.split("/")[0];
			var dayfield = input.split("/")[1];
			var yearfield = input.split("/")[2];
			var dayobj = new Date(yearfield, monthfield - 1, dayfield);
			if ((dayobj.getMonth() + 1 != monthfield) || (dayobj.getDate() != dayfield) || (dayobj.getFullYear() != yearfield)){
				returnval = false;
			} else {
				returnval =true;
			}
		}
		return returnval;
	}

	var messages = global[namespace].constants.messages,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			Validate = function (element, options)  {
				this.init ('validate', element, options);
			},
			rules = {
				email : {
					check: function (value, field) {
						if (value) {
							return testPattern (value,"^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,})+$");
						}
						return true;
					},
					msg : function () {
						return messages.email;
					}
				},
				uspostal : {
					check: function (value, field) {
						if (value) {
							// allow +4 zips
							return testPattern (value, "^\\d{5}(-\\d{4})?$");
							// Restrict to only 5 digits
							// return testPattern (value, "^\\d{5}$");
						}
						return true;
					},
					msg : function () {
						return messages.uspostal;
					}
				},
				capostal : {
					check: function (value, field) {
						if (value) {
							return testPattern (value, "^[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJKLMNPRSTVWXYZ] \\d[ABCEGHJKLMNPRSTVWXYZ]\\d$");
						}
						return true;
					},
					msg : function () {
						return messages.capostal;
					}
				},
				postal : {
					check: function(value, field){
						var countryValue = checkCountry(this.$element);
						if (countryValue == "us") {
							return rules.uspostal.check(value, field);
						} else if (countryValue == "ca") {
							return rules.capostal.check(value.toUpperCase(), field);
						} else {
							return true;
						}
					},
					msg : function () {
						var countryValue = checkCountry(this.$element);
						if (countryValue == "us" || countryValue == "usa") {
							return rules.uspostal.msg();
						} else if (countryValue == "ca") {
							return rules.capostal.msg();
						}
					}
				},
				usorcapostal : {
					check: function (value, field) {
						if (value) {
							var catest = testPattern (value, "^[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJKLMNPRSTVWXYZ] \\d[ABCEGHJKLMNPRSTVWXYZ]\\d$");
							var ustest=	testPattern (value, "^\\d{5}$");
							if (!catest && !ustest) {
								return false;
							}
							else {
								return true;
							}
						}
						return true;
					},
					msg : function () {
						return messages.usorcapostal;
					}
				},
				phone : {
					check: function(value, field){
						var countryValue = checkCountry(this.$element);
						if (countryValue == "us") {
							return rules.usphone.check(value, field);
						}  else {
							return rules.internationalphone.check(value, field);
						}
					},
					msg : function () {
						var countryValue = checkCountry(this.$element);
						if (countryValue == "us") {
							return rules.usphone.msg();
						} else {
							return rules.internationalphone.msg();
						}
					}
				},
				usphone : {
					check: function(value, field) {
						if (value) {
							//return testPattern (value, "(?:(?:(\s*\(?([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\s*)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\)?\s*(?:[.-]\s*)?)([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\s*(?:[.-]\s*)?([0-9]{4})");
							// return testPattern (value, "[1-9][0-9]{9}");
							return testPattern (value, "^([0-9]){3}\-([0-9]){3}\-([0-9]){4}$");
						}
						return true;
					},
					msg : function () {
						return messages.usphone;
					}
				},
				// add test for international phone
				internationalphone : {
					check: function(value,field) {
						if(value) {
							// return testPattern (value, "\+?([0-9]{2})\)?([0-9]{4})([0-9]{4})")
							return testPattern (value, "(?:[0-9] ?){6,14}[0-9]");
						}
						return true;
					},
					msg : function () {
						return messages.internationalphone;
					}
				},
				alpha : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.alpha.named;
						}
						return messages.alpha.unnamed;
					}
				},
				numeric : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[0-9]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.numeric.named;
						}
						return messages.numeric.unnamed;
					}
				},
				alphanumeric : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z0-9]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.alphanumeric.named;
						}
						return messages.alphanumeric.unnamed;
					}
				},
				alphanumericspace : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z0-9 ]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.alphanumericspace.named;
						}
						return messages.alphanumericspace.unnamed;
					}
				},
				alphaspace : {
					check: function(value, field) {
						if (value) {
							// allow a-z, A-Z, spaces, and periods (.)
							return testPattern (value, "^[a-zA-Z \.]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.alphaspace.named;
						}
						return messages.alphaspace.unnamed;
					}
				},
				creditcard : {
					// accepts all numeric or masked card
					check: function(value, field) {
						var number,
								i, len,
								total = 0,
								doubled,
								digit;
						if (value) {
							number = value.replace(/ /g,'');
							if (isNaN(number)) {
								// ignore masked credit card number
								return testPattern (value, "^[X]+\\d{4}$");
							} else {
								// mod10 check on cc number
								len = number.length - 1;
								for (i = len; i >= 0; i--) {
									if ((len - i) % 2  === 0) {
										total += parseInt(number[i]);
									} else {
										doubled = 2 * number[i];
										while (doubled !== 0) {
											digit = doubled % 10;
											doubled = parseInt(doubled / 10);
											total += digit;
										}
									}
								}
								if (total % 10 === 0) {
									return true;
								} else {
									return false;
								}
							}
						}
						return true;
					},
					msg : function () {
						return messages.creditcard;
					}
				},
				required : {
					check: function(value, field) {
						if (field.attr('type') === 'checkbox') {
							if (field[0].checked) {
								return true;
							} else {
								return false;
							}
						}
						else {
							if ($.trim(value) !== '') {
								return true;
							} else {
								return false;
							}
						}
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.required.named;
						}
						return messages.required.unnamed;
					}
				},
				minlength : {
					check : function (value, field) {
						var minlength = field.attr('min-length');
						if ($.trim(value).length >= minlength) {
							return true;
						} else {
							return false;
						}
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.minlength.named;
						}
						return messages.minlength.unnamed;
					}
				},
				maxlength : {
					check : function (value, field) {
						var maxlength = field.attr('max-length');
						if ($.trim(value).length <= maxlength) {
							return true;
						} else {
							return false;
						}
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.maxlength.named;
						}
						return messages.maxlength.unnamed;
					}
				},
				matchPassword : {
					check : function (value, field) {
						var matchField = field.attr('data-matchfield'),
								matchValue = '';
						if (matchField) {
							matchValue = this.$element.find(matchField).val();
						}
						if ($.trim(value) == $.trim(matchValue)) {
							return true;
						} else {
							return false;
						}
					},
					msg : function () {
						return messages.matchPassword;
					}
				},
				matchEmail : {
					check : function (value, field) {
						var matchField = field.attr('data-matchfield'),
								matchValue = '';
						if (matchField) {
							matchValue = this.$element.find(matchField).val();
						}
						if ($.trim(value).toLowerCase() == $.trim(matchValue).toLowerCase()) {
							return true;
						} else {
							return false;
						}
					},
					msg : function () {
						return messages.matchEmail;
					}
				},
				nameField : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z0-9 \.\"'&:\/\-]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.nameField.named;
						}
						return messages.nameField.unnamed;
					}
				},
				nopobox : {
					check: function(value, field) {
			if (value) {
							return !(testPattern (value, "([P|p](OST|ost)*.*s*[O|o|0](ffice|FFICE)*.*s*[B|b][O|o|0][X|x]s*(\d.)*)"));
						}
						return true;
					},
					msg : function () {
						return messages.nopobox;
					}
				},
				qty : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[1-9][0-9]{0,2}$");
						}
						return true;
					},
					msg : function () {
						return messages.qty;
					}
				},
				password : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^.*(?=.{8,})(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$");
						}
						return true;
					},
					msg : function () {
						return messages.password;
					}
				},
				validDateOfBirth : {
					check: function(value, field) {
						return checkdate(value);
					},
					msg : function () {
						return messages.dateOfBirthDate;
					}
				},
				validDateOfBirthAge : {
					check: function(value, field) {
						var birthday = +new Date(value);
						var age = (Date.now() - birthday) / 31557600000;
						if(age < 13 || age > 120){
							return false;
						}
						return true;
					},
					msg : function () {
						return messages.dateOfBirthAge;
					}
				},
				name : {
					check: function(value, field) {
						if (value) {
							// allow alpha, period, space
							value = removeLeadingTrailingSpaces(value);
							field.val(value);
							return testPattern (value, "^[a-zA-Z. ]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.name.named;
						}
						return messages.name.unnamed;
					}
				},
				address : {
					check: function(value, field) {
						if (value) {
							// allow alpha, numeric, period, space
							value = removeLeadingTrailingSpaces(value);
							field.val(value);
							return testPattern (value, "^[0-9a-zA-Z., ]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.address.named;
						}
						return messages.address.unnamed;
					}
				},
				taxField : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z0-9.\",-\/\]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.taxField.named;
						}
						return messages.taxField.unnamed;
					}
				}
			};

	Validate.prototype = {
		constructor: Validate,
		init: function (type, element, options) {
			if (loggingDebug) {
				console.debug('init ' + type + ' with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}
			var self = this;
			var showErrors = false;
			this.$element = $(element);
			this.options = $.extend({}, $.fn[type].defaults, options, this.$element.data());
			this.isValid = false;
			this.fields = this.$element.find('[data-validation]');
			this.submit = this.$element.find('input[type="submit"]');
			if (!this.submit || this.submit.length === 0) {
				this.submit = this.$element.find('.primary.button');
			}

			if (this.options.live_validate) {
				this.$element.on('input.validate.data-api propertychange.validate.data-api change.validate.data-api focusout.validate.data-api', function (e) {
					self.validateField($(e.target));
				});
			}
			else if (this.options.blur_validate) {
				this.$element.on('propertychange.validate.data-api change.validate.data-api focusout.validate.data-api', function (e) {
					// trim leading / trailing whitespace from field (except passwords) then validate
					var $field = $(e.target);
					if ($field[0].type !== 'password') {
						self.validateField($field.val($field.val().trim()));
					}
					else {
						self.validateField($field);
					}
				});
			}

			if (this.options.submit_validate) {
				this.$element.on('submit.validate.data-api', function (e) {
					self.validateForm();
					return self.isValid;
				});
			}
		},
		/* validates every field that was marked with data-validation rules */
		validateForm : function (fieldArray, showErrors) {
			this.$element.trigger('validate');
			var fieldsToValidate = fieldArray || this.fields,
				x = 0, max = fieldsToValidate.length, $field,
				formErrors = {},
				fieldErrors = {}, fieldName;
			if (typeof showErrors === 'undefined'){
				showErrors = true;
			}
			this.isValid = true;
			for (x, max; x < max; x++) {
				$field = $(fieldsToValidate[x]);
				fieldErrors = this.checkField($field);
				if ($.isEmptyObject(fieldErrors) === false) {
					fieldName = $field.attr('name');
					formErrors[fieldName] = fieldErrors;
				}
			}
			if (showErrors) {
				this.showFormErrors(formErrors);
			}

			return this.isValid;
		},
		/* Validates a single field. */
		validateField : function ($field){
			var fieldErrors = this.checkField($field),
					fieldName,
					formErrors = {},
					errors = {},
					error;

			if ($.isEmptyObject(fieldErrors) === false) {
				fieldName = $field.attr('name');
				formErrors[fieldName] = fieldErrors;
				errors = this.cleanFieldErrors(formErrors);

				for (error in errors) {
					if (errors.hasOwnProperty(error)) {
						this.showFieldError(errors[error]);
					}
				}
			} else if (fieldErrors) {
				// fieldErrors will be undefined if the field has no rules.
				// so if fieldErrors is defined then the field has no errors.
				this.showFieldSuccess($field);
			}

		},
		/* run validation rules on a field and return the error messages */
		checkField: function($field){
			var value,
					types,
					fieldErrors = {},
					errors = [],
					rule;

			if (typeof $field.attr('data-validation') == 'undefined') {
				return;
			}

			if ($field.prop('disabled') || $field.hasClass('disabled') || !$field.is(':visible')) {
				return;
			}

			value = $field.val();
			types = $field.attr('data-validation').split(' ');

			// First run tests from validation
			for (var x = 0, max = types.length; x < max; x++) {
				rule = rules[types[x]];
				if (rule  && !rule.check.call(this, value, $field)) {
					errors.push(rule.msg.call(this, $field));
					break;
				}
			}

			if (errors.length > 0) {
				this.isValid = false;
				fieldErrors.field = $field;
				fieldErrors.errors = errors;
			}
			return fieldErrors;
		},
		/* given an error message object, display it on a field */
		showFieldError : function (errorObj) {
			var errorMessageHtml = '',
					errors = errorObj.errors,
					fields = errorObj.fields,
					$alertTarget,
					$field = fields[fields.length - 1],
					field = $field[0],
					//in the case there are multiple fields, will tie to last field in group
					fieldId = $field.attr('id'),
					iconLeftPosition;

			$alertTarget = this.getFieldTarget(fields);

			for (var e = 0, errorsLen = errors.length; e < errorsLen; e++) {
				errorMessageHtml += errors[e];
			}

			for (var f = 0, fieldsLen = fields.length; f < fieldsLen; f++) {
				this.clearFieldMessage(fields[f]);
				fields[f].addClass("error");
				fields[f].attr('aria-describedby', 'error-msg-' + fieldId);
				fields[f].attr('aria-invalid', 'true');
			}

			var decoded = global[namespace].utilities.decodeHTMLEntities(errorMessageHtml);
			$alertTarget.after(Mustache.render(this.options.template, {fieldId: fieldId , errorMessage: decoded}));
			$('label[for=' + fieldId + ']').addClass('error');
		},
		/* get the target for the field message */
		getFieldTarget : function(fields) {
			var parent,
					field;

			if (fields instanceof jQuery) {
				field = fields;
			} else {
				//if there are multiple fields with the same name, use the last one.
				field = fields[fields.length - 1];
			}

			parent = field.attr('data-parent');
			if (parent !== undefined) {
				return this.$element.find(parent);
			} else if (field.is('.inline-form *')){
				// when this is an inline form we want to display the error message after the button.
				return field.nextAll('.button');
			} else {
				return field;
			}
		},
		/* show success validation message for a field */
		showFieldSuccess : function ($field){
			var field = $field[0],
					$alertTarget,
					iconLeftPosition,
					top = 7;

			this.clearFieldMessage($field);
		},
		/* clear error and success messages form a field */
		clearFieldMessage : function ($field) {
			$field.removeClass("error success")
					.removeAttr('aria-describedby')
					.removeAttr('aria-invalid');
			this.$element.find('label[for=' + $field.attr('id') + ']').removeClass('error');
			this.$element.find('.validation-message-for-' + $field.attr('id')).remove();
		},
		/* clears errors and success messages */
		clearFormMessages : function(){
			var x = 0;
			var max = this.fields.length;
			var $field;
			for (x, max; x < max; x++) {
				$field = $(this.fields[x]);
				this.clearFieldMessage($field);
			}
		},
		/* remove only error messages from fields */
		clearFormErrors : function () {
			var that = this;

			this.$element.find('[data-validation].error').each(function(){
				var $field = $(this);
				that.clearFieldMessage($field);
			});

			global[namespace].errors = [];
		},
		/* Given an error message object show all field error messages in a form */
		showFormErrors : function (formErrors) {
			var errors = {},
					error,
					hasErrors = false;

			/*
			 When the live-validation is off, then the form may have some lingering error messages from the back end.
			 The front-end validation would pass when they user hits submit, but when we get more messages from the back end,
			 we need to remove any existing error messages since some of them may be resolved.
			 */
			this.clearFormErrors();

			errors = this.cleanFieldErrors(formErrors);

			for (error in errors) {
				if (errors.hasOwnProperty(error)) {
					hasErrors = true;
					this.showFieldError(errors[error]);
				}
			}
			if (hasErrors) {
				this.scrollToError();
			}

		},
		cleanFieldErrors : function(formErrors){
			/* this function will take care of duplicate errors (this can happen when we receive external error message from
			 back-end validation). It will associate a target element for the error using the parent element if one is defined
			 in the form. */
			var errors = {},
					fieldError,
					$field,
					fieldName,
					fieldErrors;

			for (fieldError in formErrors) {
				if (formErrors.hasOwnProperty(fieldError)) {

					fieldName = fieldError;

					if ($.isArray(formErrors[fieldError])) {
						$field = this.$element.find("[name='" + fieldError + "']");
						fieldErrors = formErrors[fieldError];
					} else {
						$field = formErrors[fieldError].field;
						fieldErrors = formErrors[fieldError].errors;
					}

					if (errors[fieldName] === undefined) {
						errors[fieldName] = {};
						errors[fieldName].fields = [];
						errors[fieldName].errors = [];
					}

					errors[fieldName].fields.push($field);
					errors[fieldName].errors = global[namespace].utilities.dedup(errors[fieldName].errors.concat(fieldErrors));
				}
			}
			return errors;

		},

		scrollToError : function (selector) {
			var viewHeight = 0,
					bodyOffset = 0,
					errorOffset,
					scrollSelector = selector || '.error';
			if (typeof ( window.innerWidth ) == 'number' ) {
				viewHeight = window.innerHeight;
			} else if ( document.documentElement && document.documentElement.clientHeight ) {
				viewHeight = document.documentElement.clientHeight;
			}
			if( typeof( window.pageYOffset ) == 'number' ) {
				bodyOffset = window.pageYOffset;
			} else if ( document.body && document.body.scrollTop ) {
				bodyOffset = document.body.scrollTop;
			}
			errorOffset = $(this.$element.find(scrollSelector).get(0)).offset();
			this.$element.find('input.error, select.error, textarea.error, .alert-box').get(0).focus();
			if (errorOffset && errorOffset.top){
				if (bodyOffset > errorOffset.top || errorOffset.top > viewHeight + bodyOffset) {
					window.scrollTo(0, errorOffset.top);
				}
			}
		}
	};


	/*  PLUGIN DEFINITION
	 * ============================== */

	$.fn.validate = function ( option ) {
		var args = Array.prototype.slice.call( arguments, 1 );
		return this.each(function () {
			var $this = $(this),
					data = $this.data('validate'),
					options = typeof option == 'object' && option;
			if (!data) {
				$this.data('validate', (data = new Validate(this, options)));
			}
			if (typeof option == 'string') {
				data[option].apply(data, args);
			}
		});
	};

	$.fn.validate.defaults = {
		template : '<div class="error field-error-text validation-message-for-{{fieldId}}" id="error-msg-{{fieldId}}" tabindex="-1">' +
			'{{{errorMessage}}}' +
			'</div>',
		live_validate: false,
		blur_validate: true,
		submit_validate: true
	};

	$.fn.validate.Constructor = Validate;

	$(function () {
		$('[data-validate]').validate();
	});


}(this, window.jQuery, "KP"));

(function (global, $, namespace) {

	"use strict";

	/* edge tests */
	function overleft(x, width, left) {
		return (x - (width + 2) / 2) < left;
	}
	function overright(x, width, right) {
		return (x + (width + 2) / 2) > right;
	}
	function overtop(y, height, top) {
		return (y - (height + 2) / 2) < top;
	}
	function overbottom(y, height, bottom) {
		return (y + (height + 2) / 2) > bottom;
	}

	// helper for browsers that can't measure unless we have dom insertion
	function measure (el){
		var $el = $(el).clone(false),
				dims = {};
		$el.css('visibility','hidden').css('position','absolute');
		$el.appendTo('body');
		dims.width = $el.width();
		dims.height = $el.height();
		$el.remove();
		return dims;
	}

	var Zoom = function (element, options) {
				this.init ('zoom', element, options);
			},
			CONSTANTS = global[namespace].constants,
			CONFIG = global[namespace].config,
			TEMPLATES = global[namespace].templates,
			$window = $(window);

	Zoom.prototype = {
		constructor: Zoom,
		init : function (type, element ,options) {
			var self = this,
					supportTouch = Modernizr.touchevents,
					startEvent = supportTouch ? "touchstart" : "mouseenter",
					stopEvent = supportTouch ? "touchend" : "mouseleave",
					moveEvent = supportTouch ? "touchmove" : "mousemove";

			this.$element = $(element);
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.zoomLoaded = false;
			this.loadtest = { main: false, magnified: false };
			this.isActive = false;
			this.isHovering = false;
			this.isDisabled = false;
			this.$main = $('.viewer-main');
			this.$mainImg = $('.viewer-main-image');
			this.$lens = $('<div class="zoom-lens"/>');
			this.$lensImg = this.$mainImg.clone();
			this.$magnifiedContainer = $('<div class="zoom-magnified"/>');
			this.$magnifiedImg = $('<img class="zoom-magnified-image fade" alt="high resolution image" data-pin-nopin="true"‰/>');
			this.smallimagedata = {};
			this.lensdata = {};
			this.scale = {};

			this.$lens.append(this.$lensImg);
			this.$magnifiedContainer.append(this.$magnifiedImg);
			this.$element.append(this.$magnifiedContainer);
			this.$main.append(this.$lens);

			this.imageLoadTimeout = null;

			// trigger the sizeMeasurement on page ready
			self.refreshMeasurements();

			// refresh the Image sizeMeasurement when window size changed.
			$window.resize(function() {
				self.refreshMeasurements();
			});

			// adding main image error event listener
			this.$mainImg.error(function(){
				self.isDisabled = true;
				var lPath = CONSTANTS.productImageRoot + '/unavailable/l.jpg',
						xlPath = CONSTANTS.productImageRoot + '/unavailable/xl.jpg';
				$('#ml-main-image').attr('srcset', lPath);
				$('#s-main-image').attr('srcset', xlPath);
				self.$mainImg.attr('src', lPath);
			});

			// adding load event listener
			this.$magnifiedImg.on('load', function() {
				var dims = measure(this),
						height = Math.round(dims.height);
				self.$magnifiedImg = $(this);
				self.$magnifiedContainer.append(self.$magnifiedImg);
				self.zoomLoaded = true;
				self.loadtest['magnified'] = true;
				self._hideLoader();
			});

			// when the main image loads
			this.$mainImg.on('load', function() {
				self.$lensImg.attr('src', self.$mainImg.attr('src'));
				self.refreshMeasurements();
				self.loadtest['main'] = true;
			});

			this.$main.on(startEvent, function(e) {
				if ($window.width() < CONFIG.mediumMin) {
					return;
				}
				self.activate(e);
				e.preventDefault();
			}).on(stopEvent, function() {
				if ($window.width() < CONFIG.mediumMin) {
					return;
				}
				self.deactivate();
			}).on(moveEvent, function(e){
				if ($window.width() < CONFIG.mediumMin) {
					return;
				}
				self._setImagePositions(e);
				e.preventDefault();
			});

			this.setImage(this.$mainImg.attr('data-id'), this.$mainImg.attr('data-image-name'));

		},
		activate : function(e) {
			this.isHovering = true;
			if (this.isActive){
				return;
			}
			if (this.isDisabled || !this.zoomLoaded) {
				this._waitForImageLoad(e);
				return;
			}
			this._setImagePositions(e);
			this.$magnifiedContainer.show();
			this.isActive = true;
			this.$lens.show();
			this.$mainImg.addClass('is-active');
			return false;
		},
		deactivate: function() {
			this.isHovering = false;
			if (this.isDisabled) {
				return;
			}
			this.$magnifiedContainer.hide();
			if (!this.isActive) {
				return;
			}
			this.isActive = false;
			this.$lens.hide();

			this.$mainImg.removeClass('is-active');
		},
		/* This function figures the ratio of the original image to the maginified image box to set the lens size
		 appropriately. Also sets the position on the fly out zoom container.
		 */
		refreshMeasurements : function () {
			var pos = {};
			this.smallimagedata = {
				w: this.$mainImg.width(),
				h: this.$mainImg.height()
			};
			pos.l = this.$mainImg.offset().left;
			pos.t = this.$mainImg.offset().top;
			pos.r = this.smallimagedata.w + pos.l;
			pos.b = this.smallimagedata.h + pos.t;
			this.smallimagedata.pos = pos;

			// calculate scale based on zoom height and small image ratio
			this.scale.y = this.options.zoomSize / this.smallimagedata.h;
			this.scale.x = ((this.smallimagedata.w/this.smallimagedata.h) * this.options.zoomSize) / this.smallimagedata.w;

			this.$magnifiedContainer.css('left', this.smallimagedata.w + 4);

			this._setLensScale();
		},
		setImage : function (productId, imageName) {
			var zoomurl = this._getImagePath(productId, imageName);
			this._showLoader();
			this._loadZoomImage(zoomurl);
		},
		_getImagePath: function (productId, imageName) {
			if (typeof productId !== 'undefined' && typeof imageName !== 'undefined') {
				return CONSTANTS.productImageRoot + '/'+ productId + '/z/' + imageName;
			}
			else {
				return CONSTANTS.productImageRoot + '/unavailable/z.jpg';
			}
		},
		_updateMainImage : function(imageName){

			// check to see if image is already selected
			if (this.$mainImg.attr('data-imageName') == imageName) {
				return;
			}

			this.$main.html($.mustache(TEMPLATES.productImageTemplate, {imageName : imageName}));
		},
		_loadZoomImage : function (url) {
			this.$magnifiedImg.attr('src', url);
		},
		_setLensScale: function() {
			this.lensdata.w = this.$magnifiedContainer.width() / this.scale.x;
			this.lensdata.h = this.$magnifiedContainer.height() / this.scale.y;
			this.$lens.css({
				width: this.lensdata.w + "px",
				height: this.lensdata.h + "px"
			});
			this.$lensImg.css({'height': this.smallimagedata.h, 'width': this.smallimagedata.w});
		},
		_setImagePositions: function(e) {
			if (this.isDisabled) {
				return;
			}
			/* “After deep debugging, I eventually discovered that the source of this problem was the
			 fix method in jquery’s event code. The fix method tries to copy the event object in
			 order to fix various cross browser issues. Unfortunately, it seems that mobile safari
			 does not allow the e.touches and e.changedTouches properties on event objects to be
			 copied to another object. This is weird and annoying. Luckily you can get around this
			 issue by using e.originalEvent.�?
			 -  http://www.the-xavi.com/articles/trouble-with-touch-events-jquery
			 */
			var touch = (Modernizr.touchevents) ? e.originalEvent.touches[0] || e.originalEvent.changedTouches[0] : e,
				x = touch.pageX,
				y = touch.pageY,
				lensWidth = this.lensdata.w,
				lensHeight = this.lensdata.h,
				stageLeft = this.smallimagedata.pos.l,
				stageBottom =  this.smallimagedata.pos.b,
				stageTop =  this.smallimagedata.pos.t,
				stageRight =  this.smallimagedata.pos.r,
				lensleft = x - stageLeft - (lensWidth) / 2,
				lenstop = y - stageTop - (lensHeight) / 2,
				self = this;
			/* edge detection */
			if (overleft(x, lensWidth, stageLeft)) {
				lensleft = 0;
			} else if (overright(x, lensWidth, stageRight)) {
				lensleft = this.smallimagedata.w - this.lensdata.w - 1;
			}
			if (overtop(y, lensHeight, stageTop)) {
				lenstop = 0;
			} else if (overbottom(y, lensHeight, stageBottom)) {
				lenstop = this.smallimagedata.h - this.lensdata.h - 1;
			}

			lensleft = parseInt(lensleft, 10);
			lenstop = parseInt(lenstop, 10);
			this.$lensImg.css({
				position: "absolute",
				top: -(lenstop + 1),
				left: -(lensleft + 1)
			});

			this.$lens.css({
				top: lenstop,
				left: lensleft
			});

			if (this.zoomLoaded) {
				this.$magnifiedImg.css({
					"left": Math.ceil(-this.scale.x * parseInt(lensleft, 10)),
					"top": Math.ceil(-this.scale.y * parseInt(lenstop, 10))
				});
			}
		},
		_showLoader: function () {
			if (this.isLoaderShowing) {
				return;
			}
			this.$magnifiedImg.removeClass('in');
			this.$magnifiedContainer.addClass('loading');
			this.isLoaderShowing = true;
		},
		_hideLoader: function () {
			if (!this.isLoaderShowing) {
				return;
			}
			this.$magnifiedContainer.removeClass('loading');
			this.$magnifiedImg.addClass('in');
			this.isLoaderShowing = false;
		},
		_waitForImageLoad: function (e) {
			var self = this;
			if (this.loadtest['main'] && this.loadtest['magnified']) {
				this.isDisabled = false;
				// for delayed activation if image is not loaded yet while hovering.
				if (this.isHovering) {
					this.activate(e);
				}
				this.loadtest['main'] = false;
				this.loadtest['magnified'] = false;
			} else {
				setTimeout(function() {
					self._waitForImageLoad(e);
				}, 400);
			}
		}
	};

	$.fn.zoom = function (option) {
		var args = Array.prototype.slice.call( arguments, 1 );
		return this.each(function () {
			var $this = $(this),
				data = $this.data('zoom'),
				options = typeof option === 'object' && option;
			if (!data) {
				$this.data('zoom', (data = new Zoom(this, options)));
			}
			if (typeof option === 'string') {
				data[option].apply(data, args);
			}
		});
	};

	$.fn.zoom.defaults = {
		viewerWidth: 364,
		viewerHeight: 364,
		zoomSize : 1000,
		lens: true,
		imageOpacity: 0.8,
		showEffect: "fadein",
		hideEffect: "hide",
		fadeinSpeed: "slow",
		fadeoutSpeed: "slow"
	};

	$.fn.zoom.Constructor = Zoom;

}(this, window.jQuery, "KP"));

/*!
 * The MAIN Controller
 */

(function (global, $, namespace) {
	//"use strict";

	var CONSTANTS = global[namespace].constants;
	var UTILITIES = global[namespace].utilities;
	var errorTemplate = global[namespace].templates.errorMessageTemplate;
	var loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;
	var $body = $('body');
	var $window = $(window);

	// basic form handler options
	var basicAjaxOptions = {
		dataType : 'json',
		beforeSubmit : function(arr, $form, options) {
			global[namespace].utilities.showLoader();
		},
		success: function(responseText, statusText, xhr, $form) {
			if (statusText == 'success') {
				if (responseText.success == 'true') {
					window.location = responseText.url;
				}
				else {
					global[namespace].utilities.hideLoader();
					global[namespace].utilities.form.showErrors($form, responseText);
				}
			}
			else {
				console.log('Malformed JSON : missing statusText parameter:');
				global[namespace].utilities.hideLoader();
				global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
			}
		},
		error: function(xhr, statusText, exception, $form) {
			console.log('AJAX Error:');
			global[namespace].utilities.hideLoader();
			global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
		}
	};

	/*
	 * Initilization code
	 * Based on Garber-Irish method of DOM-ready execution
	 * @see viget.com/inspire/extending-paul-irishs-comprehensive-dom-ready-execution/
	 *
	 * MAIN is an object that contains dom ready actions for pages.
	 * The format is {namespace}.{controller}.{action}
	 * Three functions are executed on dom ready:
	 * 1) common.init()	- not called for modals.
	 * 2) {controller}.init() or modal.init()
	 * 3) {controller}.{action}()
	 */

	var initFunctions = {
		init : function (pController, pAction, pOptions) {
			// Call the page-specific controller methods

			var body = document.body,
				mController = (pController === undefined) ? body.getAttribute('data-controller') : pController,
				mAction = (pAction === undefined) ? body.getAttribute('data-action') : pAction,
				mOptions = (pOptions === undefined) ? {} : pOptions;

			// UTILITIES.startSessionTimeout();

			if (mController !== 'modal' && mController !== 'proxy') {
				this.fire('common', 'init', mOptions);
			}
			this.fire(mController, 'init', mOptions);
			this.fire(mController, mAction, mOptions);
		},
		fire : function (controller, action, options) {
			var action = (action === undefined) ? 'init' : action;
			if (controller !== '' && this[controller] && typeof this[controller][action] === 'function') {
				if (loggingDebug) {
					console.log('calling:' + controller + '.' + action);
				}
				this[controller][action](options);
			}
		},
		common : {
			init : function(){

				// update user status and cart count
				global[namespace].profileController.getProfileStatus();

				// reset profile cookie on logout
				$('.sign-out-link').click(function(){
					global[namespace].profileController.clearProfileCookie();
				});

				// don't submit empty search
				$('#search-desktop').on('submit', function(e){
					if ($('#Ntt').val() === '') {
						e.preventDefault();
					}
				});

				$('#search-mobile').on('submit', function(e){
					if ($('#Ntt-mobile').val() === '') {
						e.preventDefault();
					}
				});

				// icon-close button clicks
				$body.on('click', '.icon-close', function(){
					$($(this).data('target')).slideUp(200);
				});

				// print button clicks
				$body.on('click', '.print', function(){
					window.print();
				});

				// footer accordions on small screen
				function toggleFooterMenus(){
					$('.footer-links-group h3').each(function(){
						var $this = $(this);

						if ($window.width() < global[namespace].config.mediumMin) {
							$this.siblings('ul').hide();
							$this.removeClass('active');
						}
						else {
							$this.siblings('ul').removeAttr('style');
							$this.removeClass('active');						}
					});
				}

				toggleFooterMenus();
				$window.resize($.throttle(250, toggleFooterMenus));

				$('.footer-links-group').on('click', 'h3', function(e){
					var $this = $(this);
					if ($window.width() < global[namespace].config.mediumMin) {
						$this.siblings('ul').slideToggle();
						$this.toggleClass('active');
					}
				});

				// dynamically add/handle vimeo thumbnails
				if (typeof Vimeo !== 'undefined') {
					function loadVimeoThumbnail($this) {
						var id = $this.data('vimeo-id');
						$.ajax({
							url: 'https://vimeo.com/api/v2/video/' + id + '.json',
							dataType: 'json',
							cache: false,
							success: function (data) {
								$this.html('<img src="' + data[0].thumbnail_medium + '" alt="Vimeo Thumbnail"/>');
								setTimeout(function() {
									$this.addClass('loaded');
								}, 50);
							},
							error: function () {
								$this.remove();
							}
						});
					}
					$('.vimeo-thumb').each(function() {
						loadVimeoThumbnail($(this));
					});
					$('.vimeo-modal').on('click', function(evt) {
						evt.preventDefault();
						evt.stopPropagation();
						// prevent modal from launching if click event
						// is actually the PDP slick carousel being swiped
						var $thumbs = $('.viewer-thumbnails');
						var slickObj = $thumbs.length && $thumbs[0].slick || {};
						var slidesToShow = slickObj.options && slickObj.options.slidesToShow || 1;
						var isSwipable = slickObj.slideCount > slidesToShow;
						var touchObj = slickObj && slickObj.touchObject || {};
						if (isSwipable && typeof touchObj.curX === 'undefined' && typeof touchObj.curY === 'undefined') {
							return;
						}
						var id = $(this).data('vimeo-id');
						var $modalTarget = document.getElementById('vimeo-modal') ? $('#vimeo-modal') : global[namespace].utilities.createModal('vimeo-modal');
						$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/vimeo.jsp?id=' + id });
					});
					$(document).on('hidden.modal', '#vimeo-modal', function() {
						// prevent audio from playing in the
						// background once user closes the modal
						$('#vimeo-modal-iframe').remove();
					});
					
				}

				// bopis - ship my order (pdp, cart, checkout)
				var shipMyOrderOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {

								// update header item counter
								global[namespace].profileController.getProfileStatus(true);

								if ($body.hasClass('checkout')) {
									return window.location = CONSTANTS.contextPath + '/checkout/cart.jsp';
								}

								// strip this query parameter, as it tends to override
								// the default functionality of the page, which is technically
								// incorrect once the user chooses to ship the order instead.
								var gStoreId = global[namespace].utilities.getStoreIdURLParam();
								if (gStoreId !== '') {
									if (window.location.pathname.indexOf('/store/detail/') === 0) {
										// ex: /store/detail/product-name/product-id/{PARAM}
										// 1.) remove last character if it is a slash
										// 2.) remove gStoreId if it is at the very end of the URL
										var path = window.location.pathname;
										if (path.substr(-1) === '/') {
											path = path.substr(0, path.length - 1);
										}
										if (path.substr(gStoreId.length * -1) === gStoreId) {
											path = path.replace('/' + gStoreId, '');
										}
										else {
											path = path.replace('/' + gStoreId + '/', '/');
										}
										path = path.replace('/store/detail/', '/detail/');
										return window.location.pathname = path;
									}
									else {
										// ex: /detail/product-name/product-id?gStoreId={PARAM}
										var search = window.location.search;
										search = search.replace('gStoreId=' + gStoreId, '');
										search = search.replace('?&', '?');
										return window.location.search = search;
									}
								}
								return window.location.reload();
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$body.on('click', '.ship-my-order', function(e){
					e.preventDefault();
					$('#ship-my-order-form').ajaxSubmit(shipMyOrderOptions);
				});

				var updateHomeStore = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						global[namespace].utilities.hideLoader();
						$('.home-store-menu').hide();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $modalTarget = document.getElementById('update-store-modal') ? $('#update-store-modal') : global[namespace].utilities.createModal('update-store-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/homeStoresModal.jsp'});
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "update home store:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "update home store: " + exception, $form);
					}
				};
				var makeToHomeStore = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						global[namespace].utilities.hideLoader();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								// update header item counter
								//global[namespace].profileController.getProfileStatus(true);
								var pageName = $('body').data('action');

								$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, responseText));
								$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, responseText));
								$('.home-store-menu').hide();

								if(pageName === 'product'){
									//$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisLocationInfoTemplate, responseText));
									$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText));
									if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
										$('.bopis-location-info').removeClass('hide');
									}
									$('#bopis-store-id').val(responseText.locationId);
									$('#bopis-zip-inventory').val(responseText.postalCode);
								}

								if(pageName === 'storeLocator'){
									$.ajax({
										url: CONSTANTS.contextPath + '/sitewide/json/storeLocationJson.jsp',
										dataType: 'json',
										cache: false,
										success: function (data) {
											console.log(data);
											var mffLocations = data.locations,
											numLocations = mffLocations.length,
											//templateStoreNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br><span class="store-open-info">Open until 9pm</span></br>{{address}}</br>{{city}}, {{state}} {{zip}}</br>{{phone}}</div>{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}">store details</a></div><hr class="divider"></div>{{/locations}}';
											templateStoreNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br>{{#storeClosingTime}}<span class="store-open-info">{{storeClosingTime}}</span>{{/storeClosingTime}}</br>{{address}}</br>{{city}}, {{state}} {{zip}}</br>{{^isComingSoon}}<span class="coming-store">{{phone}}{{/isComingSoon}}</span>{{#isComingSoon}}{{phone}}{{/isComingSoon}}</div>{{#isComingSoon}}{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}{{/isComingSoon}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}" class="button expand dark">store details</a></div><hr class="divider"></div>{{/locations}}';
											// render store locations
											$('.store-location-results').html(Mustache.render(templateStoreNav, data));
											$('.home-this-store').trigger('click');
										},
										error: function () {
											console.log("error");
										}
									});
								}

								if(pageName === 'storeDetail'){
									var templateStoreDet = '<button class="button expand primary-dark"><span class="icon icon-locator"></span>&nbsp;MY STORE</button>';
									$('.home-store-section').html(Mustache.render(templateStoreDet, responseText));
								}
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "change home store: " + exception, $form);
					}
				};

				$('body').on('click','.make-this-store', function(e){
					e.preventDefault();
					$('#homestore').val($(this).data('store-id'));
					var pageName = $('body').data('action'),
						label = $(this).text(),
						action = window.location.href;
					if(pageName === 'storeLocator' ||pageName === 'storeDetail'){
						if(KP.analytics){
							KP.analytics.trackEvent("Store Detail Button","Store Locator", label, action);
						}
					}
					$('#home-store-form').ajaxSubmit(makeToHomeStore);
				});

				var validateProductOptions = function(prodId) {
					var $container = $('#product-' + prodId);
					var $skuPicker = $container.find('.product-form-pickers');
					var $selectors = $('div.product-options', $skuPicker);
					$selectors.each(function(i) {
						var $selector = $(this),
								variantId,
								variantName,
								variantTypes,
								errorMessage = '',
								x=0,
								max;
						if ($selector.find('.active').length === 0) {
							variantId = $selector.attr('data-typeid');
							variantTypes = KP_PRODUCT[product].variantTypes;
							max = variantTypes.length;
							for (x; x < max; x++) {
								if (variantTypes[x].id == variantId) {
									variantName = variantTypes[x].displayName;
									if (variantName !== '') {
										errorMessage = 'Please select a ' + variantName;
									}
									else {
										errorMessage = 'Please select all options';
									}
								}
							}
							$selector.find('.product-option-errors').html(errorMessage);
						}
					});
				}

				var openBopisStoreModal = function($thisLink) {
					if ($('.product-pickers').hasClass('table-picker')) {
						$('.table-details').removeClass('active-sku');
						$thisLink.parents('.table-details').addClass('active-sku');
						var prodId = $thisLink.parents('.table-details').find('.table-product-id').val();
						var skuId = $thisLink.parents('.table-details').find('.table-sku-id').val();
					} else {
						var prodId = $('#productId').val().trim();
						var skuId = $('#catalogRefIds').val().trim();
						if (skuId === '') {
							validateProductOptions(prodId);
							return false;
						}
					}
					var storeid = $('#bopis-store-id').val() || null;
					var modalUrl = CONSTANTS.contextPath + '/browse/ajax/bopisStoreModal.jsp?skuId=' + skuId + (storeid ? '&bopisStore=' + storeid : '');
					var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
					$modalTarget.modal({'url': modalUrl});
				}

				$('body').on('click', '.select-store', function(e) {
					// select bopis option and attempt to add to cart
					// in order to bring up the find in-store modal
					if ($('.product-pickers').hasClass('table-picker')) {
						var $parent = $(this).parents('.add-to-cart-actions');
						$parent.find('.bopis-order').prop('checked', true);
						$parent.find('.add-to-cart-submit').trigger('click');
					} else {
						$('#bopis-order').prop('checked', true);
						$('.add-to-cart-submit').trigger('click');
					}
				});

				// bopis - change store
				$('body').on('click', '.change-store', function(e) {
					e.preventDefault();

					var pageName = $('body').data('action');
					$('#bopis-change-store').val('true');
					$('#bopis-from-product').val('false');

					if (pageName === 'product') {
						openBopisStoreModal($(this));
					} else {
						$('#bopis-from-product-inventory').val('false');
						var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
						$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
					}
				});

				// bopis - update store
				$('body').on('click', '.update-bopis-store', function(e) {
					e.preventDefault();
					$('#bopis-change-store').val('false');
					$('#bopis-from-product').val('false');
					openBopisStoreModal($(this));
				});

				// bopis - change store
				$('body').on('click', '.update-store', function(e) {
					e.preventDefault();
					if ($(window).width() > global[namespace].config.smallMax) {
						var page = $('body').attr('class');
					} else {
						var page = 'mobile';
					}
					var $modalTarget = document.getElementById('update-store-modal') ? $('#update-store-modal') : global[namespace].utilities.createModal('update-store-modal', 'small');
					$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/homeStoresModal.jsp?pageType='+page});
				});


				$('body').on('click','.home-store-toggle', function(e){
					e.preventDefault();
					$('.home-store-menu').toggle();
					if ($(window).width() < global[namespace].config.smallMax) {
						$('.off-canvas-wrap').addClass('offcanvas-overlap');
					}
				});

				//update change my store locations
				$('#update-search-form').ajaxForm(updateHomeStore);

				// small screen - turn global promotions into slick slider
				function initGlobalPromo(){
					if ($window.width() > global[namespace].config.smallMax) {
						// destroy slick slider
						if ($('.promo-bar-three').hasClass('slick-initialized')) {
							$('.promo-bar-three').slick('unslick');
						}
					}
					else {
						$('.promo-bar-three,.promo-bar-two').slick({
							dots: false,
							arrows: false,
							infinite: false,
							slidesToShow: 1,
							slidesToScroll: 1,
							autoplay: true,
							autoplaySpeed: 2000,
							fade: true,
							cssEase: 'linear'
						});
					}
				}
				initGlobalPromo();
				$window.resize($.throttle(250, initGlobalPromo));

				// hero slider
				$('.hero-slider:not(.hero-slider-with-promo)').slick({
					infinite: false,
					speed: 300,
					slidesToShow: 1,
					slidesToScroll: 1,
					infinite: true,
					dots: true,
					arrows: true,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: true
							}
						}
					]
				});

				// hero slider with promo stack
				$('.hero-slider-with-promo').slick({
					infinite: false,
					speed: 300,
					slidesToShow: 1,
					slidesToScroll: 1,
					infinite: true,
					dots: true,
					arrows: true,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: true
							}
						}
					]
				});

				// three promo slider
				$('.promo-slider-three').slick({
					dots: true,
					infinite: false,
					slidesToShow: 3,
					slidesToScroll: 3,
					arrows: true,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: false,
								slidesToShow: 2,
								slidesToScroll: 2
							}
						}
					]
				});

				// four promo slider
				$('.promo-slider-four').slick({
					dots: true,
					infinite: false,
					slidesToShow: 4,
					slidesToScroll: 4,
					arrows: true,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: false,
								slidesToShow: 2,
								slidesToScroll: 2
							}
						}
					]
				});

				// five promo slider
				$('.promo-slider-five').slick({
					dots: true,
					infinite: false,
					slidesToShow: 5,
					slidesToScroll: 5,
					arrows: true,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: false,
								slidesToShow: 3.5,
								slidesToScroll: 3
							}
						}
					]
				});

				// six promo slider
				$('.promo-slider-six').slick({
					dots: true,
					infinite: false,
					slidesToShow: 6,
					slidesToScroll: 6,
					arrows: true,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: false,
								slidesToShow: 3.5,
								slidesToScroll: 3
							}
						}
					]
				});

				// three product slider
				$('.product-slider-three').slick({
					dots: false,
					infinite: false,
					slidesToShow: 3,
					slidesToScroll: 3,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								slidesToShow: 2,
								slidesToScroll: 2
							}
						}
					]
				});

				// four product slider
				$('.product-slider-four').slick({
					dots: false,
					infinite: false,
					slidesToShow: 4,
					slidesToScroll: 4,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								slidesToShow: 2,
								slidesToScroll: 2
							}
						}
					]
				});

				// five product slider
				$('.product-slider-five').slick({
					dots: true,
					infinite: false,
					slidesToShow: 5,
					slidesToScroll: 5,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: false,
								slidesToShow: 3.5,
								slidesToScroll: 3
							}
						}
					]
				});


				// six product slider
				$('.product-slider-six').slick({
					dots: true,
					infinite: false,
					slidesToShow: 6,
					slidesToScroll: 6,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: false,
								slidesToShow: 3.5,
								slidesToScroll: 3
							}
						}
					]
				});
				
				function escapeSpecialCharInSearchTerm($obj, type){
					 var searchInput = $obj.find('.keyword-search-field'),
					 	isEscapeSpecialChars = searchInput.attr('data-escape-special-char');
					 if(isEscapeSpecialChars === 'true'){
				    	 var searchTerm = searchInput.val().trim();
				      	//console.log('before searchTerm : ' + searchTerm);
				    	 searchTerm = searchTerm.replace(/[^a-z0-9 ]/gi,' ');
				      	//console.log('after searchTerm : ' + searchTerm);
				    	 $obj.find('.keyword-search-field').val(searchTerm);
				     }
					 $obj.submit();
					 if (KP.analytics) {
				    	  KP.analytics.trackEvent("Site Search","Site Search", type, searchTerm);
				      }
				}
				
				$(".keyword-search-field").on('keydown', function(e){
					var keycode = (e.keyCode ? e.keyCode : e.which);
					// enter triggers submit
					if (keycode == 13) {
						e.preventDefault();
						e.stopPropagation();
						 var currentObj = $(this).parents('.keyword-search-form');
						escapeSpecialCharInSearchTerm(currentObj,"Enter");
					}
				});
				
				$('body').on('click','.keyword-search-button',function(e){
				      e.preventDefault();
				      var currentObj = $(this).parents('.keyword-search-form');
				      //var searchInput = $this.find('.keyword-search-field');
				      //var isEscapeSpecialChars = searchInput.attr('data-escape-special-char');
				      //console.log('isEscapeSpecialChars : ' + isEscapeSpecialChars);
				      //if(isEscapeSpecialChars === 'true'){
				      //	var searchTerm = searchInput.val().trim();
				      	//console.log('before searchTerm : ' + searchTerm);
				      //	searchTerm = searchTerm.replace(/[^a-z0-9 ]/gi,' ');
				      	//console.log('after searchTerm : ' + searchTerm);
				     // 	$this.find('.keyword-search-field').val(searchTerm);
				      //}
				     // $this.submit();
				      escapeSpecialCharInSearchTerm(currentObj,e.type);
			    });
				
			}
		},
		home : {
			init: function () {

				// update user status on sign out
				var url = location.href;
				if (UTILITIES.getURLParameter(url, 'DPSLogout') == 'true') {
					global[namespace].profileController.getProfileStatus(true);
				}

				// listrak email signup
				var $emailSignupForm = $('.email-signup-cartridge-form');
				$emailSignupForm.on('submit', function(e){
					e.preventDefault();
					var formid = this.getAttribute('id');
					if (UTILITIES.form.validate($('#'+formid))) {
						if (typeof _ltk !== 'undefined') {
							_ltk.SCA.CaptureEmail('email-cartridge');
						}

						// email signup success modal
						var $modalTarget = document.getElementById('email-signup-success-modal') ? $('#email-signup-success-modal') : global[namespace].utilities.createModal('email-signup-success-modal', 'x-small'),
							url = CONSTANTS.contextPath + '/sitewide/ajax/emailSignupSuccessModal.jsp',
							option = {'url': url};
						$modalTarget.modal(option);

						// clear email form
						$('#email-signup-success-modal').on('hide.modal', function(){
							var $emailCartridgeInput = $('#'+formid).find('#email-cartridge');
							if ($emailCartridgeInput.length > 0) {
								$emailCartridgeInput.val('');
							}
						});
						if(KP.analytics){
							KP.analytics.trackEvent('Email Sign Up Middle', 'Email Sign Up', "Email Sign Up", url);
						}
					}
				});

				// for storeDetails mobile nav
				var $leftCol = $('.two-column-left');
				$('.hide-sidebar, .icon-close').on('click', function(){
					$leftCol.hide();
				});
				$('.show-sidebar').on('click', function(){
					$leftCol.show();
				});
				
				if (KP.analytics) {
					KP.analytics.sendHomePageEvents();
				}
			},
			changePassword : function() {
				$('#change-password-form').ajaxForm(basicAjaxOptions);
			},
			giftCardBalance : function() {
				var gcBalanceOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
						if(KP.analytics){
							KP.analytics.trackEvent('CTA Button', 'Button', $form.find('.button').val(), $form.find('#gift-card-error-url').val());
						}
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();
								var $modalTarget = document.getElementById('gc-balance-modal') ? $('#gc-balance-modal') : global[namespace].utilities.createModal('gc-balance-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/gcBalanceModal.jsp?n=' + responseText.number + '&b=' + responseText.balance});
							}
							else {
								grecaptcha.reset();
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#check-balance-form').ajaxForm(gcBalanceOptions);

			},
			storeLocator : function() {

				// function to initialize google map click listeners
				function bindInfoWindow(marker, map, infowindow, html) {
					google.maps.event.addListener(marker, 'click', function() {
						infowindow.setContent(html);
						infowindow.open(map, marker);
					});
				}
				
			 // Sets the map on all markers in the array.
		      function setMapOnAll(map) {
		        for (var i = 0; i < markers.length; i++) {
		          markers[i].setMap(map);
		        }
		      }

		      // Removes the markers from the map, but keeps them in the array.
		      function clearMarkers() {
		        setMapOnAll(null);
		      }


				// initialize the google map

				function initMap() {

					var map = new google.maps.Map(document.getElementById('map'), {
								mapTypeControlOptions: {
									mapTypeIds: [] // remove the map type selector
								},
								streetViewControl: false
							}),
							bounds = new google.maps.LatLngBounds(),
							infowindow = new google.maps.InfoWindow(),
							icon = CONSTANTS.contextPath + '/resources/images/location-pin.png',
							markers = [];

					// add all the store markers to the map
					for (var i=0; i<numLocations; i++) {
						var location = mffLocations[i];

						// create marker
						markers[i] = new google.maps.Marker({
							position: new google.maps.LatLng(location.lat, location.lng),
							icon: icon,
							map: map
						});

						// display the map so it shows all the markers at once
						bounds.extend(markers[i].position);
						if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
							 var extendPoint1 = new google.maps.LatLng(bounds.getNorthEast().lat() + 0.01, bounds.getNorthEast().lng() + 0.01);
							 var extendPoint2 = new google.maps.LatLng(bounds.getNorthEast().lat() - 0.01, bounds.getNorthEast().lng() - 0.01);
							 bounds.extend(extendPoint1);
							 bounds.extend(extendPoint2);
						}
						map.fitBounds(bounds);

						// initialize click listeners
						bindInfoWindow(markers[i], map, infowindow, Mustache.render(templateInfoWindow, {store: location}));
					}

					// show marker when you click a store
					$('body').on('click','.location-details', function() {
						for (var i = 0; i < numLocations; i++){
							if (this.id == mffLocations[i].locationId) {
								var storeHours = mffLocations[i].storeHours,
										gasMartHours = mffLocations[i].gasMartHours,
										serviceCenterHours = mffLocations[i].serviceCenterHours,
										selectedMarker = markers[i];

								// show store info and center on map
								new google.maps.event.trigger(selectedMarker, 'click');
								map.setCenter(selectedMarker.getPosition());
								map.setZoom(13);

								//not required for new design
								//parseStoreHours(storeHours, gasMartHours, serviceCenterHours);

								break;
							}
						}
					});

					// show window when you click a home store
					$('body').on('click','.home-this-store', function() {
						for (var i = 0; i < numLocations; i++){
							if (this.id == mffLocations[i].locationId) {
								var selectedMarker = markers[i];

								// show store info and center on map
								new google.maps.event.trigger(selectedMarker, 'click');
								break;
							}
						}
					});

					//default home store info
					for (var i = 0; i < numLocations; i++){
						if (mffLocations[i].isHomeStore === "true") {
							var selectedMarker = markers[i];

							// show store info and center on map
							new google.maps.event.trigger(selectedMarker, 'click');
							break;
						}
					}


					// hide info window when you click the map
					google.maps.event.addListener(map, 'click', function() {
						infowindow.close();
					});

					// trigger click on locationId if in url
					var locationId = UTILITIES.getURLParameter(window.location.href, 'locationId') || '';
					if (locationId !== '') {
						for (var i = 0; i < numLocations; i++){
							if (locationId == mffLocations[i].locationId) {
								var storeHours = mffLocations[i].storeHours,
										gasMartHours = mffLocations[i].gasMartHours,
										serviceCenterHours = mffLocations[i].serviceCenterHours,
										selectedMarker = markers[i];

								// show store info and center on map
								google.maps.event.addListenerOnce(map, 'idle', function(){ // if map becomes idle before click trigger
									new google.maps.event.trigger(selectedMarker, 'click');
								});
								map.setCenter(selectedMarker.getPosition());
								map.setZoom(13);

								parseStoreHours(storeHours, gasMartHours, serviceCenterHours);

								break;
							}
						}
					}
					
					var storeDetailsList = {
						dataType : 'json',
						beforeSubmit : function(arr, $form, options) {
							global[namespace].utilities.form.hideErrors($form);
							global[namespace].utilities.showLoader();
						},
						success: function(responseText, statusText, xhr, $form) {
							if (statusText == 'success') {
								if (responseText.success == 'true') {
									global[namespace].utilities.hideLoader();
									// render store locations
									$('.store-location-results').html(Mustache.render(templateStoreNav, responseText));
									updateMap(responseText);
								}
								else {
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.showErrors($form, responseText);
								}
							}
							else {
								console.log('Malformed JSON : missing statusText parameter:');
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
							}
						},
						error: function(xhr, statusText, exception, $form) {
							console.log('AJAX Error:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
						}
					}
					
					function updateMap(responseText) {
						for (var i=0; i<numLocations; i++) {
							markers[i].setMap(null);
						}
						mffLocations = responseText.locations;
						numLocations = responseText.locations.length;
						// add all the store markers to the map
						for (var i=0; i<responseText.locations.length; i++) {
							var location = responseText.locations[i];

							// create marker
							markers[i] = new google.maps.Marker({
								position: new google.maps.LatLng(location.lat, location.lng),
								icon: icon,
								map: map
							});

							// display the map so it shows all the markers at once
							bounds.extend(markers[i].position);
							if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
								 var extendPoint1 = new google.maps.LatLng(bounds.getNorthEast().lat() + 0.01, bounds.getNorthEast().lng() + 0.01);
								 var extendPoint2 = new google.maps.LatLng(bounds.getNorthEast().lat() - 0.01, bounds.getNorthEast().lng() - 0.01);
								 bounds.extend(extendPoint1);
								 bounds.extend(extendPoint2);
							}
							map.fitBounds(bounds);

							// initialize click listeners
							bindInfoWindow(markers[i], map, infowindow, Mustache.render(templateInfoWindow, {store: location}));
						}
					}
					function fetchAllMatchingLocationIdStores(locationId){
						var resultStores=new Array();
						for (var i=0; i<numLocations; i++) {
							if (mffLocations[i].locationId == locationId){
								resultStores.push(mffLocations[i]);
							}
						}
						return resultStores;
					}
					function fetchAllMatchingStateCodeStores(stateCode){
						var resultStores=new Array();
						for (var i=0; i<numLocations; i++) {
							if (mffLocations[i].state.toLowerCase() == stateCode){
								resultStores.push(mffLocations[i]);
							}
						}
						return resultStores;
					}
					function fetchAllMatchingStateFullNameStores(state){
						var resultStores=new Array();
						for (var i=0; i<numLocations; i++) {
							if (mffLocations[i].stateFullName.toLowerCase() == state){
								resultStores.push(mffLocations[i]);
							}
						}
						return resultStores;
					}
					function fetchAllMatchingCityStores(city){
						var resultStores=new Array();
						for (var i=0; i<numLocations; i++) {
							if (mffLocations[i].city.toLowerCase() == city){
								resultStores.push(mffLocations[i]);
							}
						}
						return resultStores;
					}
					
					//
					//$('body').on("keyup",'#store-details-list-form', function(event) {
					$("#store-details-list-form").on('keydown', function(e){
						var keycode = (e.keyCode ? e.keyCode : e.which);
						// enter triggers submit
						if (keycode == 13) {
							e.preventDefault();
							e.stopPropagation();
							$('.store-search-button').trigger('click');
						}
					});
					
					// show marker when you click a store
					$('.store-search-button').on('click', function(e){
						e.preventDefault();
						mffLocations = KP_STORES.locations;
						numLocations = mffLocations.length;
						global[namespace].utilities.form.hideErrors($('#store-details-list-form'));
						var queryString = $('#store-locator-zip').val().toLowerCase();
						var resultStores=new Array();
						if (queryString === '') {
							// render all store locations
							$('.store-location-results').html(Mustache.render(templateStoreAllNav, KP_STORES));
							initMap();
						}else{
							if(!isNaN(queryString)){
								resultStores=fetchAllMatchingLocationIdStores(queryString);
								if(resultStores.length==0){
									$.ajax({
										url: CONSTANTS.contextPath + '/sitewide/json/storeLocationJson.jsp?zipcode='+queryString,
										dataType: 'json',
										cache: false,
										success: function (responseText) {
											if(responseText.locations.length==0){
												var responseText = {
														'errorMessages':['There are currently no stores in your area. Please try a different City, State, Store or Zip code.'],
														'InlineFormErrorSupport':"false"
														};
												global[namespace].utilities.form.showErrors($('#store-details-list-form'), responseText);
											}else{
												$('.store-location-results').html(Mustache.render(templateStoreNav, responseText));
												updateMap(responseText);
											}
										},
										error: function () {
											console.log("error");
											var responseText = {
													'errorMessages':['There are currently no stores in your area. Please try a different City, State, Store or Zip code.'],
													'InlineFormErrorSupport':"false"
													};
											global[namespace].utilities.form.showErrors($('#store-details-list-form'), responseText);
										}
									});
									return false;
								}
								
							}else{
								if(queryString.length==2){
									resultStores=fetchAllMatchingStateCodeStores(queryString);
								}else{
									resultStores = fetchAllMatchingStateFullNameStores(queryString);
									if(resultStores.length==0){
										resultStores=fetchAllMatchingCityStores(queryString);
									}
									
								}
								
							}
							if(resultStores.length>0){
								for(var i=0;i<resultStores.length;i++){
									resultStores[i].storeIndex=i+1;
								}
								var responseText = {"locations":resultStores};
								$('.store-location-results').html(Mustache.render(templateStoreAllNav, responseText));
								updateMap(responseText);
							}else{
								//$('.store-location-results').empty();
								var responseText = {
										'errorMessages':['There are currently no stores in your area. Please try a different City, State, Store or Zip code.'],
										'InlineFormErrorSupport':"false"
										};
								global[namespace].utilities.form.showErrors($('#store-details-list-form'), responseText);
							}
						}
						
						
						/*if (queryString === '') {
							$('.store-card').removeClass('hide');
						}
						else {
							for (var i=0; i<numLocations; i++) {
								var $store = $('#' + mffLocations[i].locationId);
								
								
								if (mffLocations[i].stateFullName.toLowerCase() == state || mffLocations[i].state.toLowerCase() == state || mffLocations[i].city.toLowerCase() == state || mffLocations[i].zip == state || mffLocations[i].locationId == state) {
									$store.parent().removeClass('hide');
									$('#store-locator-zip').val(state);
									$('.store-card').removeClass('hide');
									
									//var storeHours = mffLocations[i].storeHours,
									//gasMartHours = mffLocations[i].gasMartHours,
									//serviceCenterHours = mffLocations[i].serviceCenterHours,
									//selectedMarker = markers[i];

									// show store info and center on map
									//new google.maps.event.trigger(selectedMarker, 'click');
									//map.setCenter(selectedMarker.getPosition());
									//map.setZoom(13);
			
									//parseStoreHours(storeHours, gasMartHours, serviceCenterHours);
									
									// show marker when you click a store
									$('#store-details-list-form').ajaxSubmit(storeDetailsList);
								}
							}
						}*/
					});
				};
				

				var mffLocations = KP_STORES.locations,
						numLocations = mffLocations.length,
						templateStoreNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br>{{#storeClosingTime}}<span class="store-open-info">{{storeClosingTime}}</span></br>{{/storeClosingTime}}{{address}}<span class="right">{{distance}}&nbsp;mi</span></br>{{city}}, {{state}} {{zip}}</br>{{^isComingSoon}}<span class="coming-store">{{phone}}{{/isComingSoon}}</span>{{#isComingSoon}}{{phone}}{{/isComingSoon}}</div>{{#isComingSoon}}{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}{{/isComingSoon}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}" class="button expand dark">store details</a></div><hr class="divider"></div>{{/locations}}',
						templateStoreAllNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br>{{#storeClosingTime}}<span class="store-open-info">{{storeClosingTime}}</span></br>{{/storeClosingTime}}{{address}}</br>{{city}}, {{state}} {{zip}}</br>{{^isComingSoon}}<span class="coming-store">{{phone}}{{/isComingSoon}}</span>{{#isComingSoon}}{{phone}}{{/isComingSoon}}</div>{{#isComingSoon}}{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}{{/isComingSoon}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}" class="button expand dark">store details</a></div><hr class="divider"></div>{{/locations}}',
						templateInfoWindow = '{{#store}}<div><span class="map-bold">{{name}}</span><br>{{address}}<br>{{city}}, {{state}} {{zip}}<hr>{{phone}}<br><a href="' + CONSTANTS.contextPath  + '{{redirectUrl}}">View store details</a></div>{{/store}}';

				// render store locations
				$('.store-location-results').html(Mustache.render(templateStoreAllNav, KP_STORES));

				function parseStoreHours(storeHours, gasMartHours, serviceCenterHours) {
					var linesStore = storeHours.split(',') || '',
						linesGas = gasMartHours.split(',') || '',
						linesAuto = serviceCenterHours.split(',') || '',
						htmlStore = '',
						htmlGas = '',
						htmlAuto = '';

					function convertMilTime(timeString) {
						var hours24 = parseInt(timeString.substring(0, 2), 10),
							hours = ((hours24 + 11) % 12) + 1,
							amPm = hours24 > 11 ? 'pm' : 'am',
							minutes = timeString.substring(2);
						if (minutes == ':00') {
							minutes = '';
						}
						return hours + minutes + amPm;
					}

					// update store hours
					if (linesStore[0] !== '') {
						for (var j = 0; j < linesStore.length; j++) {
							// convert military to standard
							var timeForDay = linesStore[j].trim().split(' '),
									storeHours = timeForDay[1].split('-'),
									openTime = convertMilTime(storeHours[0]),
									closeTime = convertMilTime(storeHours[1]);
							if (j !== 0) {
								htmlStore += '<br>'
							}
							htmlStore += '<strong>' + timeForDay[0] + ': </strong> ' + openTime + ' - ' + closeTime;
						}
						$('#store-hours-title').removeClass('hide');
						$('#store-hours').html(htmlStore);
					}
					else {
						$('#store-hours-title').addClass('hide');
						$('#store-hours').html('');
					}

					// update gas mart hours
					if (linesGas[0] !== '') {
						for (var j = 0; j < linesGas.length; j++) {
							// convert military to standard
							var timeForDay = linesGas[j].trim().split(' '),
									storeHours = timeForDay[1].split('-'),
									openTime = convertMilTime(storeHours[0]),
									closeTime = convertMilTime(storeHours[1]);
							if (j !== 0) {
								htmlGas += '<br>'
							}
							htmlGas += '<strong>' + timeForDay[0] + ': </strong> ' + openTime + ' - ' + closeTime;
						}
						$('#gas-hours-title').removeClass('hide');
						$('#gas-hours').html(htmlGas);
					}
					else {
						$('#gas-hours-title').addClass('hide');
						$('#gas-hours').html('');
					}

					// update auto service center hours
					if (linesAuto[0] !== '') {
						for (var j = 0; j < linesAuto.length; j++) {
							// convert military to standard
							var timeForDay = linesAuto[j].trim().split(' '),
									storeHours = timeForDay[1].split('-'),
									openTime = convertMilTime(storeHours[0]),
									closeTime = convertMilTime(storeHours[1]);
							if (j !== 0) {
								htmlAuto += '<br>'
							}
							htmlAuto += '<strong>' + timeForDay[0] + ': </strong> ' + openTime + ' - ' + closeTime;
						}
						$('#auto-hours-title').removeClass('hide');
						$('#auto-hours').html(htmlAuto);
					}
					else {
						$('#auto-hours-title').addClass('hide');
						$('#auto-hours').html('');
					}
				}
				
				// show marker when you click a store
				$('#store-locator-state').on('change', function(e){
					e.preventDefault();
					var state = this.value;

					if (state === '') {
						$('.location-details').removeClass('hide');
					}
					else {
						for (var i=0; i<numLocations; i++) {
							var $store = $('#' + mffLocations[i].locationId);
							if (mffLocations[i].state == state) {
								$store.parent().removeClass('hide');
							}
							else {
								$store.parent().addClass('hide');
							}
						}
					}
				});

				// wait for page to load before initializing map
				setTimeout(function(){
					initMap();
				}, 500);

			},
			storeDetail : function() {
				// function to initialize google map click listeners
				function bindInfoWindow(marker, map, infowindow, html) {
					google.maps.event.addListener(marker, 'click', function() {
						infowindow.setContent(html);
						infowindow.open(map, marker);
					});
				}

				// initialize the google map

				function initMap() {

					var map = new google.maps.Map(document.getElementById('map'), {
							mapTypeControlOptions: {
								mapTypeIds: [] // remove the map type selector
							},
							streetViewControl: false
						}),
						bounds = new google.maps.LatLngBounds(),
						infowindow = new google.maps.InfoWindow(),
						icon = CONSTANTS.contextPath + '/resources/images/location-pin.png',
						markers = [];

						// add all the store markers to the map
						for (var i=0; i<numLocations; i++) {
							var location = mffLocations[i];
	
							// create marker
							markers[i] = new google.maps.Marker({
								position: new google.maps.LatLng(location.lat, location.lng),
								icon: icon,
								map: map
							});
	
							// display the map so it shows all the markers at once
							bounds.extend(markers[i].position);
							if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
								 var extendPoint1 = new google.maps.LatLng(bounds.getNorthEast().lat() + 0.01, bounds.getNorthEast().lng() + 0.01);
								 var extendPoint2 = new google.maps.LatLng(bounds.getNorthEast().lat() - 0.01, bounds.getNorthEast().lng() - 0.01);
								 bounds.extend(extendPoint1);
								 bounds.extend(extendPoint2);
							}
							map.fitBounds(bounds);
	
							// initialize click listeners
							bindInfoWindow(markers[i], map, infowindow, Mustache.render(templateInfoWindow, {store: location}));
					};
				}
				
				function removeSelectedStore(allStores,removeStoreId){
					var resultStores = new Array();
					for (var i=0; i<numLocations; i++) {
						if (mffLocations[i].locationId != removeStoreId) {
							resultStores.push(mffLocations[i]);
						}
					}
					return {"locations":resultStores};
				};
				
				var mffLocations = KP_STORES.locations,
				numLocations = mffLocations.length,
				templateStoreNav = '{{#locations}}<li><img alt="" src="/resources/images/location-pin.png" draggable="false"><div class="store-card" style="display: inline-block;"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{name}}</strong></br>{{address}}</br>{{city}}, {{state}} {{zip}}</br><div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}">store details&nbsp></a></div></div></div></li>{{/locations}}',
				templateInfoWindow = '{{#store}}<div><span class="map-bold">{{name}}</span><br>{{address}}<br>{{city}}, {{state}} {{zip}}<hr>{{phone}}<br><a href="' + CONSTANTS.contextPath  + '{{redirectUrl}}">View store details</a></div>{{/store}}';
				var filtered_KP_Stores = removeSelectedStore(KP_STORES,storeId);
				// render store locations
				if(numLocations > 0){
					$('.store-location-list').html(Mustache.render(templateStoreNav, filtered_KP_Stores));
				}else{
					//$('.store-location-list').html("<li>No Find nearest stores.</li>">
				}
				
				// wait for page to load before initializing map
				setTimeout(function(){
					initMap();
				}, 500);
			}
		},
		content : {
			init : function(){
				// mobile sidebar
				var $leftCol = $('.two-column-left');
				$('.hide-sidebar, .icon-close').on('click', function(){
					$leftCol.hide();
				});
				$('.show-sidebar').on('click', function(){
					$leftCol.show();
				});

				// contact us form subject selections
				$('.subject-select').change(function() {
					$('.option-div').hide();
					$('#' + $(this).val()).show();
				});

				// add selected text to subject field
				$('.sub-select').change(function(){
					var value=$(this).find("option:selected").val();
					var text = $(this).find("option:selected").text();
					$('#topic').val(value);
					var area = $('.subject-select').find('option:selected').text();
					if (text !== 'Select a topic'){
						$('.contact-us-subject').val(area + ' - ' + text);
					} else {
						$('.contact-us-subject').val('');
					}
				})


				// careers-center image responsive image mapping
				$('img[usemap]').rwdImageMaps();


				// on Static Content page, only open accordion section relative to the article being displayed
				var windowLoc = window.location.href;
				var n = windowLoc.lastIndexOf('/');
				var articleUrl = windowLoc.substring(n + 1);

				if (articleUrl !== '') {
					$('#staticContentAccordion a[href]').each(function(){
						var accordionData = $(this).attr('href');
						var x = accordionData.lastIndexOf('/');
						var accordionLink = accordionData.substring(x + 1);

						if (accordionLink == articleUrl) {
							// test if nested accordion and add appropriate classes
							if ($(this).parents('.has-submenu').length){
								$(this).parentsUntil('.top-level').siblings('.accordion-title').attr('aria-expanded', 'true').addClass('active');
								$(this).parents('.nest-target').css('display', 'block');
								$(this).closest('li').addClass('accordion-nav-active');
							} else {
								// make sure we don't add the active class to important selectors in the tree
								$(this).parentsUntil('.accordion-container').siblings('.accordion-title').not('.has-submenu .accordion-title, .content-acc-title').attr('aria-expanded', 'true').addClass('active');
								$(this).closest('.accordion-body').css('display', 'block');
								$(this).closest('li').addClass('accordion-nav-active');
							}
						}
					})
				}

			},
			staticContent : function(){
					$('#phone').mask('000-000-0000');

					var contactUsOptions = {
						dataType : 'json',
						beforeSubmit : function(arr, $form, options) {
							global[namespace].utilities.showLoader();
						},
						success: function(responseText, statusText, xhr, $form) {
							// re-apply phone mask
							$('#phone').mask('000-000-0000');

							if (statusText == 'success') {
								if (responseText.success == 'true') {
									global[namespace].utilities.hideLoader();
									var $modalTarget = document.getElementById('contact-us-success-modal') ? $('#contact-us-success-modal') : global[namespace].utilities.createModal('contact-us-success-modal', 'small');
									$modalTarget.modal({'url': CONSTANTS.contextPath + '/content/ajax/contactUsSuccessModal.jsp'});
									KP.analytics.trackEvent('Contact Us', 'Contact Us', 'click', 'submit');
								}
								else {
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.showErrors($form, responseText);
								}
							}
							else {
								console.log('Malformed JSON : missing statusText parameter:');
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
							}
						},
						error: function(xhr, statusText, exception, $form) {
							console.log('AJAX Error:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
						}
					},
					orderTrackingOptions = {
						dataType : 'json',
						beforeSubmit : function(arr, $form, options) {
							global[namespace].utilities.showLoader();
						},
						success: function(responseText, statusText, xhr, $form) {
							if (statusText == 'success') {
								if (responseText.success == 'true') {
									window.location = responseText.url;
								}
								else {
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.showErrors($form, responseText);
								}
							}
							else {
								console.log('Malformed JSON : missing statusText parameter:');
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
							}
						},
						error: function(xhr, statusText, exception, $form) {
							console.log('AJAX Error:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
						}
					};
				$('#contact-us-form').ajaxForm(contactUsOptions);
				$('#order-tracking-form').ajaxForm(orderTrackingOptions);

				// faq accordions
				var hash = window.location.hash;
				if (hash !== '') {
					var $hash = $(window.location.hash);
					$body.scrollTop($hash.offset().top);
					$hash.accordion('openAll');
				}

			},
			gardenCenter : function(){
				// open all Garden Center accordion sections on page load
				$('#gardenCenterAccordion').accordion('openAll');

				// listrak email signup
				var $emailSignupForm = $('#garden-center-email-signup-form');
				$emailSignupForm.on('submit', function(e){
					e.preventDefault();
					if (UTILITIES.form.validate($emailSignupForm)) {
						if (typeof _ltk !== 'undefined') {
							_ltk.SCA.CaptureEmail('garden-center-email');
						}

						// email signup success modal
						var $modalTarget = document.getElementById('email-signup-success-modal') ? $('#email-signup-success-modal') : global[namespace].utilities.createModal('email-signup-success-modal', 'x-small'),
							url = CONSTANTS.contextPath + '/sitewide/ajax/emailSignupSuccessModal.jsp',
							option = {'url': url};
						$modalTarget.modal(option);

						// clear email form
						$('#email-signup-success-modal').on('hide.modal', function(){
							var $emailCartridgeInput = $('#garden-center-email');
							if ($emailCartridgeInput.length > 0) {
								$emailCartridgeInput.val('');
							}
						});
					}
				});
			}
		},
		modal : {
			init : function(){
				// initialize plugins in case modal was loaded via ajax
				$('[data-tabs]').tabs();
				$('[data-accordion]').accordion();
				$('[data-validate]').validate();
			},
			avsModal : function() {

				var parsedJson = JSON.parse(avsJSON),
						suggested = parsedJson.suggestedAddress,
						entered = parsedJson.enteredAddress,
						$address1 = $('#address'),
						$address2 = $('#address2'),
						$city = $('#city'),
						$state = $('#state'),
						$zip = $('#zip'),
						$skipAVS = $('#skip-avs'),
						$formSubmit = $(parsedJson.submitId),
						$avsModal = $('#avsModal'),
						template = global[namespace].templates.avsTemplate,
						content = Mustache.render(template, parsedJson);
				$('.avs-modal .avs-grid').html(content);
				
				var pageName = $('body').data('action');

				// payment step avs
				if ($('.checkout-progress-payment').hasClass('in-progress')) {
					$address1 = $('#billing-address');
					$address2 = $('#billing-address2');
					$city = $('#billing-city');
					$state = $('#billing-state');
					$zip = $('#billing-zip');
					$skipAVS = $('#billing-skip-avs');
				}

				// reapply phone mask when modal closes
				$body.on('hide.modal', function(){
					var $phone = $('#phone') || '',
							$billingPhone = $('#billing-phone') || '';
					if ($phone !== '') {
						$phone.mask('000-000-0000');
					}
					if ($billingPhone !== '') {
						$billingPhone.mask('000-000-0000');
					}
				});

				// user clicks 'use as entered'
				$('.use-entered').on('click', function(e){
					e.preventDefault();

					// fill in hidden form values
					$address1.val(entered.address1);
					$address2.val(entered.address2);
					$city.val($('<div/>').html(entered.city).text());
					$state.val(entered.state);
					$zip.val(entered.postalCode);

					// skip avs on next submission
					$skipAVS.val(true);

					// submit hidden form
					$avsModal.modal('hide');
					if (KP.analytics && pageName == 'checkout') {
						KP.analytics.sendCheckoutOption("AVS Entered");
					}
					$formSubmit.click();
					
				});

				// user clicks 'use suggested'
				$('.use-suggested').on('click', function(e){
					e.preventDefault();

					// fill in hidden form values
					$address1.val(suggested.address1);
					$address2.val(suggested.address2);
					$city.val(suggested.city);
					$state.val(suggested.state);
					$zip.val(suggested.postalCode);

					// skip avs on next submission
					$skipAVS.val(true);

					// submit hidden form
					$avsModal.modal('hide');
					if (KP.analytics && pageName == 'checkout') {
						KP.analytics.sendCheckoutOption("AVS Suggested");
					}
					$formSubmit.click();
				});
			},
			backInStockModal : function(){
				var backInStockEmailOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $modalTarget = document.getElementById('back-in-stock-confirmation-modal') ? $('#back-in-stock-confirmation-modal') : UTILITIES.createModal('back-in-stock-confirmation-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/backInStockConfirmationModal.jsp'});
								global[namespace].utilities.hideLoader();
								$('#back-in-stock-modal').modal('hide');
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#back-in-stock-form').ajaxForm(backInStockEmailOptions);
			},
			bopisModal : function() {

				// prevent background from scrolling when modal is open
				// (CSS class works for all devices except iPhone, which needs a JS hack)
				var preventIphoneScroll = function(evt) {
					if ($(evt.target).parents('.scrollbar').length === 0) {
						evt.preventDefault();
						evt.stopPropagation();
						evt.stopImmediatePropagation();
					}
				};
				$('body').addClass('prevent-user-scroll').on('touchmove', preventIphoneScroll);

				// change store link should reload the page
				var changeStore = false;
				var pageName = $('body').data('action');

				if ($('#bopis-change-store').val() == 'true' || global[namespace].utilities.getURLParameter(window.location.href, 'changeStore') == 'true') {
					changeStore = true;
					$('#bopis-change-store').val('false');
				}
				// set form values
				$('#bopis-quantity-modal').val($('#bopis-quantity').val());
				$('#bopis-from-product-modal').val($('#bopis-from-product').val());
				$('#bopis-product-id-modal').val($('#bopis-product-id').val());

				if(pageName === 'product'){
					$('#bopis-from-product-modal').val('true');
					if($('.product-pickers').hasClass('table-picker')){
						$('#bopis-sku-id-modal').val($('.table-details.active-sku').find('.table-sku-id').val());
					}else{
						$('#bopis-sku-id-modal').val($('#catalogRefIds').val());
					}
				}else{
					$('#bopis-sku-id-modal').val($('#bopis-sku-id').val());
				}

				var setResultsListMaxheight = function() {
					var $modal = $('.modal .modal-window');
					var $scrollbar = $modal.find('.scrollbar').css('max-height', '0');
					if (!$scrollbar.length) {
						return;
					}
					var modalHeight = $modal.height();
					var targetHeight = $(window).height() - 10;
					if (matchMedia('(min-width: 768px)').matches) {
						targetHeight = targetHeight - 30;
					}
					if (modalHeight > targetHeight) {
						// failsafe: technically, this should never happen.
						// if it somehow does happen, allow the user to scroll.
						$('body').removeClass('prevent-user-scroll').off('touchmove', preventIphoneScroll);
						$scrollbar.css('max-height', '');
						return;
					}
					$scrollbar.css('max-height', (targetHeight - modalHeight));
				};

				var searchResults = {};
				var $bopisModal = $('#bopis-modal');
				var $bopisResults = $('.bopis-results');
				var $storeResults = $('.store-results');
				var $updateStoreModal = $('#update-store-modal');
				var $bopisStoreModal = $('#bopis-store-modal');
				var $addToCartButton = $('.add-to-cart-submit');
				var $inventoryEmail = $('.no-inventory-email-trigger');

				var bopisSearchOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						searchResults = responseText;
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $bsModal = $('.bopis-modal');
								$bopisResults.html(Mustache.render(global[namespace].templates.bopisSearchTemplate, responseText)).show(0);
								$bsModal.addClass('active');
								$bsModal.find('label').show();
								if(responseText.available === 'true') {
									$bsModal.find('.reserve-msg').removeClass('hide');
								}
								setResultsListMaxheight();
								$bopisModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							} else {
								// pass an errorTemplate in order to display errors after the form (instead of before)
								global[namespace].utilities.form.showErrors($form, responseText, undefined, errorTemplate);
								$bopisResults.empty();
								setResultsListMaxheight();
								$bopisModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							}
						} else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};

				var storeSearchOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						searchResults = responseText;
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								$storeResults.html(Mustache.render(global[namespace].templates.storeSearchTemplate, responseText)).show(0);
								$updateStoreModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
								$storeResults.empty();
								$updateStoreModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};

				var bopisStoreSearchOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						searchResults = responseText;
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $bsModal = $('.bopis-store-modal');
								$bopisResults.html(Mustache.render(global[namespace].templates.bopisStoreSearchTemplate, responseText)).show(0);
								$bsModal.addClass('active');
								$bsModal.find('label').show();
								$bsModal.find('h2').html('Find Store Availability');
								if(responseText.available === 'true') {
									$bsModal.find('.reserve-msg').removeClass('hide');
								}
								setResultsListMaxheight();
								$bopisModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							}
							else {
								// pass an errorTemplate in order to display errors after the form (instead of before)
								global[namespace].utilities.form.showErrors($form, responseText, undefined, errorTemplate);
								$bopisResults.empty();
								setResultsListMaxheight();
								$bopisModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};

				var addToCartOptions = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								// change store link should reload the page to get the correct...
								//    cart - max qty
								//    pdp  - add to cart button
								if (changeStore) {
									window.location = window.location.href.split('?')[0];
								}
								else {
									// show bopis store selected
									var selectedStore = {};
									for (var i=0; i<searchResults.stores.length; i++) {
										if (searchResults.stores[i].locationId == $('#bopis-store-id').val()) {
											selectedStore = searchResults.stores[i];
											if (responseText.bopisOnly !== 'true') {
												// if bopisOnly order, don't show the "ship my order instead" link
												selectedStore.bopisOnly = 'false';
											}
											break;
										}
									}

									// updated selected store info
									if ($('.bopis-location-info').length == 0) {
										$('#add-to-cart-form').append('<div class="bopis-location-info"></div>');
									}

									if($('.product-pickers').hasClass('table-picker')){
										$('.bopis-order').attr('checked',true);
										$('.bopis-location-info').removeClass('hide');
										$('.table-picker-details').each(function(index){
											var productId = $('#productId').val().trim(),
												skuId = $('#catalogRefIds-'+index).val().trim(),
												storeId = $('#bopis-store-id').val();
											$.ajax(CONSTANTS.contextPath + '/sitewide/json/updateMyHomeStoreTablePickerSuccess.jsp?productId=' + productId + '&storeId=' + storeId, {
												cache: false,
												dataType : 'json',
												success: function(responseText) {
													$('.table-picker-details').each(function(index){
														$('#bopis-location-info'+index).html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText.skus[index]));
														if(responseText.skus[index].eligible !== "true"){
															$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).addClass('disabled');
															//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
														}else{
															$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).removeClass('disabled disable-add-to-cart');
															//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).addClass('hide');
														}
													})
												},
												error: function() {
													console.log("error on update bopis location info");
												}
											});
										});
									}else{
										$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, selectedStore));
										if(selectedStore.eligible !== "true"){
											$addToCartButton.addClass('disable-add-to-cart');
											//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
										}else{
											$addToCartButton.removeClass('disabled disable-add-to-cart');
											//$inventoryEmail.addClass('hide');
										}
									}

									//$('.utility-home-store').html(Mustache.render(global[namespace].templates.headerStoreTemplate, selectedStore));
									$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, selectedStore));
									$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, selectedStore));
									//$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisLocationInfoTemplate, selectedStore));
									$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, selectedStore));
									$('#bopis-zip-inventory').val(selectedStore.postalCode);
									$('.ship-to-home').addClass('hide');

									// only add to cart if fromProduct == true
									if ($('#bopis-from-product-modal').val() == 'true') {
										// update header item counter
										global[namespace].profileController.getProfileStatus(true);

										// BZ 2523 - If there are validation errors during ship to home
										// and user goes the BOPIS route
										// the previous ship to home errors have to be cleared
										UTILITIES.form.hideErrors($('#add-to-cart-form'));
										// update side cart items
										$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
											if ($(window).width() > global[namespace].config.smallMax) {
												$('.desktop-header .side-cart-toggle').click();
											}
											else {
												$('.mobile-header .side-cart-toggle').click();
											}
										});
									}
								}
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);

								var selectedStore = {};
								for (var i=0; i<searchResults.stores.length; i++) {
									if (searchResults.stores[i].locationId == $('#bopis-store-id').val()) {
										selectedStore = searchResults.stores[i];
										if (responseText.bopisOnly !== 'true') {
											// if bopisOnly order, don't show the "ship my order instead" link
											selectedStore.bopisOnly = 'false';
										}
										break;
									}
								}

								// updated selected store info
								if ($('.bopis-location-info').length == 0) {
									$('#add-to-cart-form').append('<div class="bopis-location-info"></div>');
								}

								if($('.product-pickers').hasClass('table-picker')){
									$('.table-picker-details').each(function(index){
										var productId = $('#productId').val().trim(),
											skuId = $('#catalogRefIds-'+index).val().trim(),
											storeId = $('#bopis-store-id').val();
										$.ajax(CONSTANTS.contextPath + '/sitewide/json/updateMyHomeStoreTablePickerSuccess.jsp?productId=' + productId + '&storeId=' + storeId, {
											cache: false,
											dataType : 'json',
											success: function(responseText) {
												$('.table-picker-details').each(function(index){
													$('#bopis-location-info'+index).html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText.skus[index]));
													if(responseText.skus[index].eligible !== "true"){
														$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).addClass('disabled');
														//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
													}else{
														$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).removeClass('disabled disable-add-to-cart');
														//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).addClass('hide');
													}
												})
											},
											error: function() {
												console.log("error on update bopis location info");
											}
										});
									});
								}else{
									$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, selectedStore));
									if(selectedStore.eligible !== "true"){
										$addToCartButton.addClass('disable-add-to-cart');
										//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
									}else{
										$addToCartButton.removeClass('disabled disable-add-to-cart');
										//$inventoryEmail.addClass('hide');
									}
								}

								//$('.utility-home-store').html(Mustache.render(global[namespace].templates.headerStoreTemplate, selectedStore));
								$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, selectedStore));
								$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, selectedStore));
								//$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisLocationInfoTemplate, selectedStore));
								$('#bopis-zip-inventory').val(selectedStore.postalCode);
								$('.ship-to-home').addClass('hide');

							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
					}
				};

				var addToHomeStore = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						$updateStoreModal.modal('hide');
						global[namespace].utilities.hideLoader();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								// update header item counter
								//global[namespace].profileController.getProfileStatus(true);
								var pageName = $('body').data('action');
								//$('.utility-home-store').html(Mustache.render(global[namespace].templates.headerStoreTemplate, responseText));
								$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, responseText));
								$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, responseText));
								//$('.home-store-menu').hide();
								$('html, body').animate({scrollTop: 0}, 400);

								if(pageName === 'product'){
									if($('.product-pickers').hasClass('table-picker')){
										$('.table-picker-details').each(function(index){
											$('.table-picker-details').each(function(index){
												$('#bopis-location-info'+index).html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText.skus[index]));
												if(responseText.skus[index].eligible !== "true"){
													if($(this).find('#bopis-order'+index).is(':checked')){
														$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).addClass('disabled');
													}
													//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
												}else{
													$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).removeClass('disabled disable-add-to-cart');
													//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).addClass('hide');
												}
											})
										});
									}else{
										$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText));
										if(responseText.eligible !== "true"){
											if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
												$addToCartButton.addClass('disable-add-to-cart');
											}
											//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
										}else{
											$addToCartButton.removeClass('disabled disable-add-to-cart');
											//$inventoryEmail.addClass('hide');
										}
									}


									//if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
									//	$('.bopis-location-info').removeClass('hide');
									//}
									$('#bopis-store-id').val(responseText.locationId);
									$('#bopis-zip-inventory').val(responseText.postalCode);

									//}
								}
								if(pageName === 'account') {
									$('.home-store').html(Mustache.render(global[namespace].templates.accountStoreTemplate, responseText));
								};

								if(pageName === 'storeLocator'){
									$.ajax({
										url: CONSTANTS.contextPath + '/sitewide/json/storeLocationJson.jsp',
										dataType: 'json',
										cache: false,
										success: function (data) {
											var mffLocations = data.locations,
											numLocations = mffLocations.length,
											templateStoreNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br>{{#storeClosingTime}}<span class="store-open-info">{{storeClosingTime}}</span>{{/storeClosingTime}}</br>{{address}}</br>{{city}}, {{state}} {{zip}}</br>{{^isComingSoon}}<span class="coming-store">{{phone}}{{/isComingSoon}}</span>{{#isComingSoon}}{{phone}}{{/isComingSoon}}</div>{{#isComingSoon}}{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}{{/isComingSoon}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}" class="button expand dark">store details</a></div><hr class="divider"></div>{{/locations}}';
											// render store locations
											$('.store-location-results').html(Mustache.render(templateStoreNav, data));
											$('.home-this-store').trigger('click');
										},
										error: function () {
											console.log("error");
										}
									});
								};

								if(pageName === 'storeDetail'){
									var redirectURL = responseText.website;
									window.location.href = CONSTANTS.contextPath + redirectURL;
								};

							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
					}
				};

				var changeToBopisStore = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								if (changeStore) {
									window.location = window.location.href.split('?')[0];
								}else{
									// updated selected store info
									if ($('.bopis-location-info').length == 0) {
										$('#add-to-cart-form').append('<div class="bopis-location-info"></div>');
									}
									$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, responseText));
									$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, responseText));
									$('#bopis-zip-inventory').val(responseText.postalCode);

									if($('.product-pickers').hasClass('table-picker')){
										$('.table-picker-details').each(function(index){
											$('#bopis-location-info'+index).html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText.skus[index]));
											if(responseText.skus[index].eligible !== "true"){
													$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).addClass('disabled');
												//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
											}else{
												$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).removeClass('disabled disable-add-to-cart');
												//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).addClass('hide');
											}
										})
									}else{
										$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText));
										if(responseText.eligible !== "true"){
											$addToCartButton.addClass('disable-add-to-cart');
											//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
										}else{
											$addToCartButton.removeClass('disabled disable-add-to-cart');
											//$inventoryEmail.addClass('hide');
										}
									}
								}
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "update bopis store error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "update bopis store error: " + exception, $form);
					}
				};

				$('#bopis-search-form').ajaxForm(bopisSearchOptions);
				$('#store-search-form').ajaxForm(storeSearchOptions);
				$('#bopis-store-search-form').ajaxForm(bopisStoreSearchOptions);

				$bopisModal.on('click', '.choose-this-store', function(){
					$('#bopis-store-id').val($(this).data('bopis-store-id'));
					$('#select-bopis-store').ajaxSubmit(addToCartOptions);
				});
				$updateStoreModal.on('click', '.choose-store', function(e){
					e.preventDefault();
					$('#homestore').val($(this).data('store-id'));
					var pageName = $('body').data('action'),
						$this = $(this);
					if(pageName === 'product'){
						if($('.product-pickers').hasClass('table-picker')){
							$('#home-store-successUrl').val(CONSTANTS.contextPath + '/sitewide/json/updateMyHomeStoreTablePickerSuccess.jsp?storeId='+$(this).data('store-id')+'&productId='+$('#productId').val());
						}else{
							$('#home-store-successUrl').val($('#home-store-successUrl').val().split('?')[0]+'?storeId='+$this.data('store-id')+'&productId='+$('#productId').val()+'&skuId='+$('#catalogRefIds').val());
						}
						$('#home-store-form').ajaxSubmit(addToHomeStore);
					}else{
						$('#home-store-form').ajaxSubmit(addToHomeStore);
					}
				});
				$bopisModal.on('click', '.change-this-store', function(e){
					e.preventDefault();
					var $this = $(this);
					$('#bopis-store-id').val($this.data('change-bopis-store-id'));
					$('#select-bopis-store').ajaxSubmit(addToCartOptions);
				});
				$bopisModal.on('hide.modal', function(){
					$('body').removeClass('prevent-user-scroll').off('touchmove', preventIphoneScroll);
					$bopisModal.off('click', '.change-this-store');
					$bopisResults.hide(0);
				});
				$updateStoreModal.on('hide.modal', function(){
					$('body').removeClass('prevent-user-scroll').off('touchmove', preventIphoneScroll);
					$updateStoreModal.off('click', '.choose-store');
					$storeResults.hide(0);
				});
				// function to initialize google map click listeners
				function bindInfoWindow(marker, map, infowindow, html) {
					google.maps.event.addListener(marker, 'click', function() {
						infowindow.setContent(html);
						infowindow.open(map, marker);
					});
				}
				// initialize the google map
				function initMap() {
					var templateInfoWindow = '{{#store}}<div><span class="map-bold">{{name}}</span><br>{{address}}<br>{{city}}, {{state}} {{zip}}<hr>{{phone}}<br><a href="{{redirectUrl}}">View store details</a></div>{{/store}}';
					var storeLoc = {};
					storeLoc['address'] = $('.storeAddress').html();
					storeLoc['city'] = $('.storeCity').html();
					storeLoc['state'] = $(".storeState").html();
					storeLoc['zip'] = $(".storeZip").html();
					storeLoc['phone'] = $(".storePhone").html();
					storeLoc['name'] = $(".storeName").html();
					storeLoc['redirectUrl'] = $(".storeUrl").html();

				var map = new google.maps.Map(document.getElementById('storemap'), {
							mapTypeControlOptions: {
								mapTypeIds: [] // remove the map type selector
							},
							streetViewControl: false
						}),
						bounds = new google.maps.LatLngBounds(),
						infowindow = new google.maps.InfoWindow(),
						icon = '/resources/images/location-pin.png',
						markers = [];

					// create marker
					markers[0] = new google.maps.Marker({
						position: new google.maps.LatLng($(".storeLat").html(), $(".storeLng").html()),
						icon: icon,
						map: map
					});

					// display the map so it shows all the markers at once
					bounds.extend(markers[0].position);
					if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
						 var extendPoint1 = new google.maps.LatLng(bounds.getNorthEast().lat() + 0.01, bounds.getNorthEast().lng() + 0.01);
						 var extendPoint2 = new google.maps.LatLng(bounds.getNorthEast().lat() - 0.01, bounds.getNorthEast().lng() - 0.01);
						 bounds.extend(extendPoint1);
						 bounds.extend(extendPoint2);
					}
					map.fitBounds(bounds);

					// initialize click listeners
					bindInfoWindow(markers[0], map, infowindow, Mustache.render(templateInfoWindow, {store: storeLoc}));
				}

				setTimeout(function(){
					initMap();
				}, 500);
			},
			bopisNotificationModal : function() {

				var $bopisModal = $('#bopis-notification-modal');
				var $bopisResults = $bopisModal.find('.bopis-results');

				// insert results into modal body
				$bopisResults.html(Mustache.render(global[namespace].templates.bopisNotificationTemplate, global[namespace].searchResults)).show(0);
				$('#bopis-notification-modal').modal('reposition');

				var addToCartOptions = {
						dataType: 'json',
						beforeSubmit: function (arr, $form, options) {
							$form.find('.alert-box').remove();
							global[namespace].utilities.showLoader();
						},
						success: function (responseText, statusText, xhr, $form) {
							$bopisModal.modal('hide');
							global[namespace].utilities.hideLoader();
							if (statusText == 'success') {
								if (responseText.success == 'true') {
									// update header item counter
									global[namespace].profileController.getProfileStatus(true);

									// update side cart items
									$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
										if ($(window).width() > global[namespace].config.smallMax) {
											$('.desktop-header .side-cart-toggle').click();
										}
										else {
											$('.mobile-header .side-cart-toggle').click();
										}
									});

									// show bopis store selected
									var selectedStore = {},
											searchResults = global[namespace].searchResults;
									for (var i=0; i<searchResults.stores.length; i++) {
										if (searchResults.stores[i].locationId == $('#bopis-store-id').val()) {
											selectedStore = searchResults.stores[i];
											break;
										}
									}
									$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, selectedStore));
									$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, selectedStore));
									// updated selected store info
									//$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisLocationInfoTemplate, selectedStore));
									$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, selectedStore));
									$('#bopis-zip-inventory').val(selectedStore.postalCode);
									$('#bopis-edit-mode').val(searchResults.editMode);
									$('#bopis-removalIds').val(searchResults.removalCommerceIds);
									$('.ship-to-home').addClass('hide');

									// only add to cart if fromProduct == true
									if ($('#bopis-from-product-modal').val() == 'true') {
										// update header item counter
										global[namespace].profileController.getProfileStatus(true);

										// update side cart items
										$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
											if ($(window).width() > global[namespace].config.smallMax) {
												$('.desktop-header .side-cart-toggle').click();
											}
											else {
												$('.mobile-header .side-cart-toggle').click();
											}
										});
									}
								}
								else {
									global[namespace].utilities.form.showErrors($form, responseText);
								}
							}
							else {
								global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
							}
						},
						error: function (xhr, statusText, exception, $form) {
							$bopisModal.modal('hide');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
						}
					};

				$bopisModal.on('click', '.choose-this-store', function(){
					$('#bopis-store-id').val($(this).data('bopis-store-id'));
					$('.bopis-removalIds').val($('#removalIds').val());
					//$('.bopis-edit-mode').val($('#bopis-edit-mode-inventory').val());
					$('.bopis-edit-mode').val(false);
					$('#select-bopis-store').ajaxSubmit(addToCartOptions);
				});

				$bopisModal.on('hide.modal', function(){
					$bopisResults.hide(0);
				});
			},
			deleteAddressModal : function () {
				var $modal = $('#delete-address-modal'),
					deleteAddressOptions = {
						dataType : 'json',
						beforeSubmit : function(arr, $form, options) {
							global[namespace].utilities.showLoader();
						},
						success: function(responseText, statusText, xhr, $form) {
							if (statusText == 'success') {
								if (responseText.success == 'true') {
									window.location = responseText.url;
								}
								else {
									global[namespace].utilities.hideLoader();
									$modal.find('.cancel-button').addClass('expand').html('close');
									$modal.find('.modal-body, .delete-button').remove();
									global[namespace].utilities.form.showErrors($form, responseText);
									$modal.modal('reposition');
								}
							}
							else {
								console.log('Malformed JSON : missing statusText parameter:');
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
							}
						},
						error: function(xhr, statusText, exception, $form) {
							console.log('AJAX Error:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
						}
					};
				$('#delete-address-form').ajaxForm(deleteAddressOptions);
			},
			deletePaymentModal : function () {
				$('#delete-payment-form').ajaxForm(basicAjaxOptions);
			},
			emailProductModal : function(){
				var emailProductOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();
								$('#email-product-modal').modal('hide');
								var $modalTarget = document.getElementById('email-product-confirmation-modal') ? $('#email-product-confirmation-modal') : UTILITIES.createModal('email-product-confirmation-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/emailProductConfirmationModal.jsp'});
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#email-product-form').ajaxForm(emailProductOptions);
				
				$(".email-product-submit input:submit").on('click', function(){
					KP.analytics.trackEvent('Product Detail', 'Product Detail', "send", window.location.href);
				});
			},
			emailWishListModal : function(){
				var emailWishListOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$('.modal').modal('hide');
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();
								$('#email-product-modal').modal('hide');
								var $modalTarget = document.getElementById('email-wish-list-confirmation-modal') ? $('#email-wish-list-confirmation-modal') : UTILITIES.createModal('email-wish-list-confirmation-modal', 'x-small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/emailWishListConfirmationModal.jsp'});
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#email-wish-list-form').ajaxForm(emailWishListOptions);
			},
			fflModal : function () {

				// show item removal message if necessary
				if ($.cookie("user-data")) {
					userData = JSON.parse($.cookie("user-data"));
					cartCount = userData.cartCount;
					if (cartCount > 0) {
						$('.mixed-cart-note').removeClass('hide');
					}
				}

				// add to cart
				var addToCartOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$('.modal').modal('hide');
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {

								// update header item counter
								global[namespace].profileController.getProfileStatus(true);

								// update side cart items
								$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
									if ($(window).width() > global[namespace].config.smallMax) {
										$('.desktop-header .side-cart-toggle').click();
									}
									else {
										$('.mobile-header .side-cart-toggle').click();
									}
								});
								global[namespace].utilities.hideLoader();
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:	Missing statusText parameter", $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
					}
				};

				$('.add-ffl-to-cart-submit').on('click', function(e){
					e.preventDefault();
					e.stopPropagation();

					var prodId = $('#productId').val().trim(),
							skuId = $('#catalogRefIds').val().trim();

					// make sure there's a sku set
					if (skuId === '') {
						productControllers[prodId].showSelectionErrors();
					}
					else {
						if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
							var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
							$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
						}
						else {
							$('#add-to-cart-form').ajaxSubmit(addToCartOptions);
						}
					}
				});
			},
			gcBalanceModal : function () {
				$('#gc-balance-modal').on('hide.modal', function(){
					global[namespace].utilities.form.hideErrors($('#check-balance-form'));
					$('#gift-card-number').val('');
					$('#gc-pin').val('');
					grecaptcha.reset();
				});
			},
			ltlModal : function () {
				var $ltlModal = $('#ltl-modal');
				$ltlModal.find('.modal-body').html(Mustache.render(global[namespace].templates.ltlTemplate, JSON.parse(ltlJSON)));
				$ltlModal.modal('reposition');
			},
			quickViewModal : function() {

				// adding thumbnail image error event listener
				$('.viewer-thumb-image').error(function(){
					$(this).siblings('.th-image').attr('srcset', CONSTANTS.productImageRoot + '/unavailable/th.jpg');
				});

				// swap images on thumbnail image click
				$('.viewer-thumb-image').on('click', function (e) {
					var $this = $(this),
						imageName = $this.attr('data-image-name'),
						$mainImage = $('.viewer-main-image');

					// check to see if image is already selected
					if ($mainImage.attr('data-image-name') == imageName) {
						return;
					}
					else {
						var path = CONSTANTS.productImageRoot + '/'+ $this.attr('data-id'),
							lPath = path + '/l/' + imageName,
							xlPath = path + '/x/' + imageName,
							zPath = path + '/z/' + imageName;

						// set active main image
						$('#ml-main-image').attr('srcset', lPath);
						$('#s-main-image').attr('srcset', xlPath);
						$mainImage.attr('src', lPath);
						$mainImage.attr('data-image-name', imageName);

						// set active zoom image
						$('.zoom-magnified-image').attr('src', zPath);

						// set active thumbnail
						$('.viewer-thumb').removeClass('active');
						$this.parents('.viewer-thumb').addClass('active');
					}
				});

				// open accordion on page load
				$('#product-info-accordion').accordion('openAll');
			},
			sizeChartModal : function () {
				$('img').on('load', function(){
					$('#size-chart-modal').modal('reposition');
				});
			},
			giftCardInfoModal : function () {
				$('img').on('load', function(){
					$('#gift-card-info-modal').modal('reposition');
				});
			},
			loginModal : function() {
				addToWishListOptions = {
					dataType: 'json',
					resetForm: true,
					beforeSubmit: function (arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $modalTarget = document.getElementById('wish-list-confirmation-modal') ? $('#wish-list-confirmation-modal') : global[namespace].utilities.createModal('wish-list-confirmation-modal', 'medium');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/addToWishListConfirmation.jsp?productId=' + responseText.productId + '&skuId=' + responseText.skuId});

								if ($('.product-pickers').hasClass('table-picker')) {
									$('.table-details.active-sku').find('.add-to-wish-list').replaceWith('<span class="added-to-wish-list">Added To Wish List</span>');
								}else{
									$('.add-to-wish-list').replaceWith('<span class="added-to-wish-list">Added To Wish List</span>');
								}
								global[namespace].utilities.hideLoader();
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to wish list error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to wish list error: " + exception, $form);
					}
				};
				var profileOptions = {
						dataType : 'json',
						beforeSerialize : function($form) {
							$form.find('#phone').unmask();
						},
						beforeSubmit : function(arr, $form, options) {
							global[namespace].utilities.showLoader();
						},
						success: function(responseText, statusText, xhr, $form) {
							// re-apply phone mask
							$('#phone').mask('000-000-0000');

							if (statusText == 'success') {
								if (responseText.success == 'true') {
									$('.modal').modal('hide');
									global[namespace].profileController.getProfileStatus(true);
									$('.wishlist-section').html(Mustache.render(global[namespace].templates.appliedWishList, responseText));

									//window.location = responseText.url;
									$('#wishListId').val(responseText.wishlistId);
									$('#addItemToWishList').ajaxSubmit(addToWishListOptions);
								}
								else {
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.showErrors($form, responseText);
								}
							}
							else {
								console.log('Malformed JSON : missing statusText parameter:');
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
							}
						},
						error: function(xhr, statusText, exception, $form) {
							// re-apply phone mask
							$('#phone').mask('000-000-0000');
							console.log('AJAX Error:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
						}
					};
				$('#login-form').ajaxForm(profileOptions);
				$('#register-form').ajaxForm(profileOptions);
			}
		}
	};

	$.extend(global[namespace], initFunctions);

	$(document).ready(function () {
		global[namespace].init();
	})

})(this, window.jQuery, "KP");

/* =========================================================
 * kp.proxy.js
 * =========================================================
 * Some common functions for submitting forms through the proxy iframe.
 *
 * @requires postmessage.js
 * ========================================================= */
(function (global, $, namespace) {
	"use strict";

	var loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;

	var proxy = {
		init : function () {
			this.sendContent();
		},
		handleProxySubmit : function handleProxySubmit(e, formId) {
			if (loggingDebug) {
				console.log("proxy.handleProxySubmit called with the following parameters");
				console.log([e, formId])
			}
			var formData,
					formId = formId || e.target.id,
					pmData = {form: formId};

			formData =  form2js(e.target);
			pmData = $.extend(pmData, formData);
			pm({
				target: window.frames["proxy"],
				type: "postForm",
				data: pmData
			});
			e.preventDefault();
		},
		handlePostForm : function (data, submitId) {
			if (loggingDebug) {
				console.log("proxy.handlePostForm called with the following parameters");
				console.log([data, submitId])
			}
			var form = document.getElementById(data.form),
					$form,
					$submitBtn;
			if (form) {
				js2form(form, data);
				if (submitId) {
					$(submitId).click();
				} else {
					//HERE
					$form = $(form);
					$submitBtn = $form.find('input[type=submit]');
					if ($submitBtn.length > 0) {
						$submitBtn.click();
					} else {
						$form.submit();
					}
				}
			}
		},
		sendContent : function () {
			if (loggingDebug) {
				console.log("proxy.sendContent");
			}
			var pmData = {content: document.getElementById('proxyContent').innerHTML};
			pm({
				target: window.parent,
				type:"setModalContent",
				data: pmData
			});
		},
		sendReadyState : function () {
			if (loggingDebug) {
				console.log("proxy.sendReadyState");
			}
			pm({
				target: window.parent,
				type:"proxyIsReady",
				data: {'state': 'ready'}
			});
		}
	};

	global[namespace] = global[namespace] || {};

	global[namespace].proxy = proxy;


})(this, window.jQuery, "KP");

/*!
 * Documentation Init
 */
(function (global, $, namespace) {
	// "use strict";

	var loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;

	var documentation = {
		init : function () {
			// for the menu scroll feature (only preventDefault on nav menu links okay??)
			$('a[href*="#"]:not(".docs-section a")').click(function(e) {
				e.preventDefault();
				var target = this.hash;
				$target = $(target);

				$('html, body').stop().animate({
					'scrollTop': $target.offset().top - 20
				}, 900, 'swing', function () {
					window.location.hash = target;
				});
			});


			// to highlight the selected section in the side nav
			$(window).scroll(function() {
				var windscroll = $(window).scrollTop(),
						anchor = "";
				//loop through sections and test if on screen. if on screen set anchor to this id and break from loop
				$('.doc-wrapper	.docs-section').each(function(i) {
					var $this = $(this);
					anchor = "#" + $this.attr('id');
					if ($(this).position().top + $this.height() > windscroll) {
						return false;
					}
				});
				// anchor set, highlight nav.
				if (anchor !== '') {
					$('.side-nav a').removeClass('sidenav-active');
					$('.side-nav a[href$=' + anchor + ']').addClass('sidenav-active');
				}

			}).scroll();


			// Facets
			$('.link-facet-items, .facet-swatch, .tile-facet-items').click(function(){
				$(this).toggleClass('active');
			});

			//pagination click listener
			$('body').on('click', '.page-num', function(){
				$('.page-num').removeClass('active');
				$(this).addClass('active');
			});

			//swatch click listener
			$('body').on('click', '.swatch', function(event){
				// do not toggle if swatch is already seleceted
				if ( $(this).hasClass('active') ) {
					return;
				} else {
					$(this).removeClass('active').siblings().removeClass('active');
					$(this).toggleClass('active');
				}
			});

			// cart update quantity functionality
			$('.counter').on('change', function(){
				var clickedUnit, unitPrice, qty, calcPrice, calcPriceFixed;
				clickedUnit = $(this).parents('.order-item-section').siblings().find($('.unit-price-line'));
				unitPrice = parseFloat($(clickedUnit).text(),10);
				qty = $(this).val();
				calcPrice = unitPrice * qty;
				// make sure 2 decimal places
				calcPriceFixed = calcPrice.toFixed(2);
				$(this).parents('.order-item-section').siblings().find($('.calculated-price')).text(calcPriceFixed);
			});

			//Slick init
			$('.product-tile-slider').slick({
				dots: false,
				infinite: true,
				slidesToShow: 3,
				slidesToScroll: 3,
				responsive: [
					{
						breakpoint: global[namespace].config.largeMin,
						settings: {
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			/* Modal Examples */
			// HTML
			// Using an outside file to add modal HTML content
			$('.launch-example-modal').click(function(){
				var $modalTarget = document.getElementById('modal-example') ? $('#modal-example') : global[namespace].utilities.createModal('modal-example');
				$modalTarget.modal({
					'url': 'exampleModal.jsp',
				});
			});

			// Javascript
			// Using 'content' object to add modal html content
			$('.launch-jscontent-modal').click(function(){
				var $modalTarget = document.getElementById('modal-example') ? $('#modal-example') : global[namespace].utilities.createModal('modal-example');
				$modalTarget.modal({
					'content': '<div class="modal-header"><div class="title-bar"><h2 class="title">I\'m a Javascript Modal!</h2></div></div><div class="modal-body"><p>This content is added via a Javascript object.</p></div><div class="modal-footer"><a href="" data-dismiss="modal" class="button secondary">Close</a></div>'
				});
			});
			// Using open, close & toggle to open an existing modal
			$('.launch-toggle-modal').click(function(){
				var $modalTarget = document.getElementById('modal-example') ? $('#modal-example') : global[namespace].utilities.createModal('modal-example');
				$modalTarget.modal('toggle');
			});


			/* Loader Example */
			$('.launch-example-loader').click(function(){
				global[namespace].loader.showLoader();
				var loaderTimeout = setTimeout(function () {
					global[namespace].loader.hideLoader();
				}, 3000);
			});

			/* Accordion Example */

			var $exAccordion = $('#js-accordion-example').accordion();

			$('.js-accordion-example-open').click(function(){
				$exAccordion.accordion('open', $('#accordion-title-2'));
			});
			$('.js-accordion-example-close').click(function(){
				$exAccordion.accordion('close', $('#accordion-title-2'));
			});
			$('.js-accordion-example-close-all').click(function(){
				$exAccordion.accordion('closeAll');
			});

		}
	};

	global[namespace] = global[namespace] || {};

	global[namespace].documentation = documentation;

})(this, window.jQuery, "KP");

/*!
 * Account Init
 */
(function (global, $, namespace) {
	//"use strict";

	var loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			CONSTANTS = global[namespace].constants,
			$window = $(window),
			$body = $('body');

	// same height for all grid elements
	function equalizeGrid() {
		if ($window.width() > global[namespace].config.smallMax) {
			// equalize the grid heights
			var maxHeight = [],
					$card = $('.card');
			for (var i=0; i<($card.length/3); i++) {
				maxHeight[i] = 0;
			}
			$card.each(function(index){
				var height = $(this).outerHeight(),
						row = Math.floor(index / 3);
				if (height > maxHeight[row]) {
					maxHeight[row] = height;
				}
			});
			$card.each(function(index){
				$(this).outerHeight(maxHeight[Math.floor(index / 3)]);
			});
			$('.card-links').addClass('equalized');
		}
		else {
			// remove the equalization heights
			$('.card').each(function(index){
				$(this).removeAttr('style');
			});
			$('.card-links').removeClass('equalized');
		}
	}

	// handle a successful AVS AJAX request
	function handleAvsSuccess(responseText, statusText, xhr, $form){
		if (statusText == 'success') {
			if (responseText.success == 'true') {
				if (responseText.addressMatched == 'true') {
					window.location = responseText.url;
				}
				else {
					if (typeof avsJSON !== 'undefined') {
						avsJSON = JSON.stringify(responseText);
					}
					else {
						$body.append('<script>var avsJSON = \'' + JSON.stringify(responseText) + '\';</script>');
					}
					global[namespace].utilities.hideLoader();
					var $modalTarget = document.getElementById('avsModal') ? $('#avsModal') : global[namespace].utilities.createModal('avsModal', 'medium'),
							url = CONSTANTS.contextPath + '/account/ajax/avsModal.jsp',
							option = {'url': url};
					$modalTarget.modal(option);
				}
			}
			else {
				global[namespace].utilities.hideLoader();
				global[namespace].utilities.form.showErrors($form, responseText);
			}
		}
		else{
			console.log('Malformed JSON : missing statusText parameter:');
			global[namespace].utilities.hideLoader();
			global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
		}
	}

	// form handler options
	var basicAjaxOptions = {
		dataType : 'json',
		beforeSubmit : function(arr, $form, options) {
			global[namespace].utilities.showLoader();
		},
		success: function(responseText, statusText, xhr, $form) {
			if (statusText == 'success') {
				if (responseText.success == 'true') {
					window.location = responseText.url;
				}
				else {
					global[namespace].utilities.hideLoader();
					global[namespace].utilities.form.showErrors($form, responseText);
				}
			}
			else {
				console.log('Malformed JSON : missing statusText parameter:');
				global[namespace].utilities.hideLoader();
				global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
			}
		},
		error: function(xhr, statusText, exception, $form) {
			console.log('AJAX Error:');
			global[namespace].utilities.hideLoader();
			global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
		}
	};

	var account = {
			init : function() {
				equalizeGrid();
				$window.resize($.throttle(250, equalizeGrid));
				if (KP.analytics) {
					KP.analytics.sendAccountPageEvents();
				}
			},
			login : function() {
				var profileOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								// reset profile cookie on login
								global[namespace].profileController.resetProfileStatus();
								var action = $form.attr("id") == "register-form" ? "Create Account" : "Sign In";
								KP.analytics.trackEvent(action, action, action);
								if(action === "Sign In"){
									responseText.url = global[namespace].utilities.addURLParameter(responseText.url, 'tus', 'yes');
								}
								window.location = responseText.url;
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#login-form').ajaxForm(profileOptions);
				$('#register-form').ajaxForm(profileOptions);
			},
			account : function() {
				// ensure profile name gets updated
				global[namespace].profileController.getProfileStatus(true);

				// display welcome modal
				if (global[namespace].utilities.getURLParameter(window.location.href, 'new') == 'true') {
					var $modalTarget = document.getElementById('welcome-modal') ? $('#welcome-modal') : global[namespace].utilities.createModal('welcome-modal', 'x-small'),
						url = CONSTANTS.contextPath + '/account/ajax/welcomeModal.jsp',
						option = {'url': url};
					$modalTarget.modal(option);
				}
			},
			profile : function() {
				$('#phone').mask('000-000-0000');

				var profileOptions = {
					dataType : 'json',
					beforeSerialize : function($form) {
						$form.find('#phone').unmask();
					},
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');

						if (statusText == 'success') {
							if (responseText.success == 'true') {
								window.location = responseText.url;
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#profile-form').ajaxForm(profileOptions);
			},
			address : function() {
				$('#phone').mask('000-000-0000');

				var newAddressOptions = {
					dataType : 'json',
					beforeSerialize : function($form) {
						var $zip = $form.find('#zip');
						$form.find('#phone').unmask();
						$zip.val(global[namespace].utilities.hyphenateZip($zip.val()));
					},
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						if (KP.analytics && responseText.success == 'true' && responseText.addressMatched == 'true') {
							KP.analytics.trackEvent('My Account CTA', 'My Account', $("#address-submit").val(), $("#address-submit").val());
						}
						handleAvsSuccess(responseText, statusText, xhr, $form);
					},
					error: function(xhr, statusText, exception, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#address-form').ajaxForm(newAddressOptions);
			},
			payment : function() {
				$('#phone').mask('000-000-0000');

				// mff brand synchrony cards
				$('#card-type').on('change', function(){
					var $this = $(this),
							$month = $('#month'),
							$year = $('#year');
					if ($this.val() == 'millsCredit') {
						global[namespace].utilities.form.hideErrors($('#payment-form'));
						$month.val('12').addClass('disabled').attr('tabindex', '-1');
						$year.val('2049').addClass('disabled').attr('tabindex', '-1');
					}
					else {
						$month.val('').removeClass('disabled').removeAttr('tabindex');
						$year.val('').removeClass('disabled').removeAttr('tabindex');
					}
				});

				// new address drawer
				var $newPaymentAddressForm = $('.new-payment-address-form');
				if (document.getElementById('new-payment-address').checked) {
					$newPaymentAddressForm.show(0);
				}
				$('input[name="payment-address"]').click(function(){
					if (document.getElementById('new-payment-address').checked) {
						$newPaymentAddressForm.slideDown(250);
					}
					else {
						$newPaymentAddressForm.slideUp(250);
					}
				});

				// ajax form handler
				var paymentFormOptions = {
					dataType : 'json',
					beforeSerialize : function($form) {
						var $zip = $form.find('#postalCode');
						$form.find('#phoneNumber').unmask();
						$zip.val(global[namespace].utilities.hyphenateZip($zip.val()));
					},
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						
						if (KP.analytics) {
							KP.analytics.trackEvent('My Account CTA', 'My Account', $("#payment-form-submit").val(), $("#payment-form-submit").val());
						}
						handleAvsSuccess(responseText, statusText, xhr, $form);
					},
					error: function(xhr, statusText, exception, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#payment-form').ajaxForm(paymentFormOptions);
			},
			taxExemption : function() {
				var taxExemptionOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (KP.analytics) {
							KP.analytics.trackEvent('My Account CTA', 'My Account', $("#tax-exemption-submit").val(), $("#tax-exemption-submit").val());
						}
						handleAvsSuccess(responseText, statusText, xhr, $form);
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#tax-exemption-form').ajaxForm(taxExemptionOptions);
			},
			wishList : function() {

				// ajax form handler
				var removeItemOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								if ($('#wishlist-num-pages').val() > 1) {
									window.location.reload();
								}
								else {
									if (responseText.wishListCount > 0) {
										$form.slideUp(200);
										global[namespace].utilities.hideLoader();
									}
									else {
										// cart is empty, show appropriate message
										$('.wish-list-content').load(CONSTANTS.contextPath + '/account/fragments/wishListEmpty.jspf');
										$('.title-buttons').remove();
										global[namespace].utilities.hideLoader();
									}
								}
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};

				// remove item from wish list
				$body.on('click', '.item-remove', function(e){
					e.preventDefault();
					var formId = $(this).data('form-id').trim();
					$('#' + formId).ajaxSubmit(removeItemOptions);
				});
			},
			wishListPrint : function() {
				window.print();
			},
			changeEmail : function() {
				$('#change-email-form').ajaxForm(basicAjaxOptions);
			},
			changePassword : function() {
				$('#change-password-form').ajaxForm(basicAjaxOptions);
			},
			expressCheckout : function() {
				$('#express-checkout-form').ajaxForm(basicAjaxOptions);
			},
			orderTracking : function() {
				$('#order-tracking-form').ajaxForm(basicAjaxOptions);
			},
			passwordReset : function() {
				var passwordResetOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							global[namespace].utilities.hideLoader();
							if (responseText.success == 'true') {
								$('#password-reset-form').remove();
								$('.password-reset-errors').remove();
								$('.password-reset-message').html('<p>A password reset email has been sent to the email address provided.</p><a href="' + CONSTANTS.contextPath + '/account/account.jsp" class="button primary">Login</a>');
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
								grecaptcha.reset();
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#password-reset-form').ajaxForm(passwordResetOptions);
			}
		};

	global[namespace] = global[namespace] || {};

	global[namespace].account = account;

})(this, window.jQuery, "KP");

/*!
 * Browse Init
 */
(function (global, $, namespace) {
	//"use strict";

	var CONSTANTS = global[namespace].constants,
			TEMPLATES = global[namespace].templates,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			$body = $('body'),
			$window = $(window);

	var browse = {
		init : function () {
			// pagination click listener
			$body.on('click', '.page-num', function(){
				$('.page-num').removeClass('active');
				$(this).addClass('active');
			});
		},
		category : function() {

			// mobile refinements
			var $leftCol = $('.two-column-left'),
					$catDrop = $('.category-dropdowns');
			$('.hide-refinements').on('click', function(){
				$leftCol.hide();
				$catDrop.removeClass('open');
			});
			$('.show-refinements').on('click', function(){
				$leftCol.show();
				$catDrop.addClass('open');
			});

			//initalize filters
			var filterController = new global[namespace].FilterController();

			// three promo slider
			$('.promo-slider-three').slick({
				dots: true,
				infinite: false,
				slidesToShow: 3,
				slidesToScroll: 3,
				arrows: true,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// four promo slider
			$('.promo-slider-four').slick({
				dots: true,
				infinite: false,
				slidesToShow: 4,
				slidesToScroll: 4,
				arrows: true,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// five promo slider
			$('.promo-slider-five').slick({
				dots: true,
				infinite: false,
				slidesToShow: 5,
				slidesToScroll: 5,
				arrows: true,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 3.5,
							slidesToScroll: 3
						}
					}
				]
			});

			// six promo slider
			$('.promo-slider-six').slick({
				dots: true,
				infinite: false,
				slidesToShow: 6,
				slidesToScroll: 6,
				arrows: true,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 3.5,
							slidesToScroll: 3
						}
					}
				]
			});

			// three product slider
			$('.product-slider-three').slick({
				dots: false,
				infinite: false,
				slidesToShow: 3,
				slidesToScroll: 3,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// four product slider
			$('.product-slider-four').slick({
				dots: false,
				infinite: false,
				slidesToShow: 4,
				slidesToScroll: 4,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// five product slider
			$('.product-slider-five').slick({
				dots: true,
				infinite: false,
				slidesToShow: 5,
				slidesToScroll: 5,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 3.5,
							slidesToScroll: 3
						}
					}
				]
			});


			// six product slider
			$('.product-slider-six').slick({
				dots: true,
				infinite: false,
				slidesToShow: 6,
				slidesToScroll: 6,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 3.5,
							slidesToScroll: 3
						}
					}
				]
			});

			if (KP.analytics) {
				KP.analytics.sendBrowsePageEvents();
				$(document).on('click', '.product-grid a', function(event) {
					var pid = $(this).data("pid"),
						action = $(this).data("action");
					if (pid) {
						KP.analytics.sendProductClick(pid, action);
					}
				});
			}
		},
		department : function() {

			// mobile refinements
			var $leftCol = $('.two-column-left'),
					$catDrop = $('.category-dropdowns');
			$('.hide-refinements').on('click', function(){
				$leftCol.hide();
				$catDrop.removeClass('open');
			});
			$('.show-refinements').on('click', function(){
				$leftCol.show();
				$catDrop.addClass('open');
			});

			//initalize filters
			var filterController = new global[namespace].FilterController();

		},
		product : function() {

			// enable buttons
			$('.back-in-stock-modal-trigger, .ffl-modal-trigger, .add-to-cart-submit, .add-to-wish-list, .item-save-login, .change-store, .ship-my-order, .login-modal-trigger').removeClass('disabled');

			// initialize pickers
			var productControllers = {};
			if (typeof KP_PRODUCT !== 'undefined') {
				for (product in KP_PRODUCT) {
					productControllers[product] = new global[namespace].ProductController(KP_PRODUCT[product]);
				}
			}

			// if gStoreId query parameter exists, silently submit a backend request to
			// update the home store. this will prevent backend dependency issues when the
			// user interacts with the page (changing store, selecting product variant, etc.)
			var gStoreId = global[namespace].utilities.getStoreIdURLParam();
			if (gStoreId !== '') {
				$('#homestore').val(gStoreId);
				$('#home-store-form').ajaxSubmit({dataType:'json'});
			}

			// open accordion on page load
			$('#product-info-accordion').accordion('openAll');

			// gift card page
			if ($body.hasClass('gift-card')) {
				$('#gift-card-amount').on('keydown', function(e){
					var keycode = (e.keyCode ? e.keyCode : e.which);
					// enter triggers submit
					if (keycode == 13) {
						e.preventDefault();
						e.stopPropagation();
						$('.add-to-cart-submit').trigger('click');
					}
				});
			}

			// bopis table pickers - only allow 1 sku to be added at a time
			function disableQtyFields($fieldToStayEnabled){
				$('.table-qty').each(function(){
					var $this = $(this);
					$this.addClass('disabled').attr('disabled', true);
					$this.parents('tr').addClass('disabled');
					$fieldToStayEnabled.removeClass('disabled').removeAttr('disabled');
					$fieldToStayEnabled.parents('tr').removeClass('disabled');
				});
			}
			function enableQtyFields(clearQtys){
				$('.table-qty').each(function(){
					var $this = $(this),
						id = $(this).attr('id');
					$this.removeClass('disabled').removeAttr('disabled');
					$this.parents('tr').removeClass('disabled');
					if (clearQtys === true) {
						$this.val('0');
						if("span."+id !== undefined){
							if(id === $("span."+id).attr('class')){
								$("."+id).hide();
								$this.attr('type','tel');
							}
						}
					}else{
						if("span."+id !== undefined){
							if(id === $("span."+id).attr('class')){
								$("."+id).show();
								$this.attr('type','hidden');
							}
						}
					}
				});
			}
			$('.table-qty').click(function(e){
				$(this).select();
			});
			$('.table-qty').keyup(function(e){
				var $self = $(this);
				if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
					if ($self.val() > 0) {
						disableQtyFields($self);
					}
					else {
						enableQtyFields();
					}
				}
			});
			$('input[name^="order-type"]').click(function(){
				var $productPickers = $('.product-pickers'),
					$this = $(this);
				if($productPickers.hasClass('table-picker')){
					if($this.parents('.add-to-cart-actions').find('.bopis-order') !== null && $this.parents('.add-to-cart-actions').find('.bopis-order').is(':checked')) {
						enableQtyFields();
						//$('.bopis-location-info').removeClass('hide');
						$this.parents('.add-to-cart-actions').find('.bopis-location-info').removeClass('hide');
						if($('.alert-box') !== undefined && $('.alert-box').length > 0){
							//$('.alert-box').remove();
						}
						if($this.parents('.add-to-cart-actions').find('.bopis-store-unavailable').length > 0 ){
							$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').addClass('disabled');
						}else{
							$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').removeClass('disabled');
						}
					}else {
						enableQtyFields();
						//$('.bopis-location-info').addClass('hide');
						$this.parents('.add-to-cart-actions').find('.bopis-location-info').addClass('hide');
						//switch to home to remove the bopis error if exist
						if($('.alert-box') !== undefined && $('.alert-box').length > 0){
							//$('.alert-box').remove();
						}
						var index = $('.add-to-cart-actions').index($this.parents('.add-to-cart-actions'));
						var inventory = KP_PRODUCT[product].skus[index].inventory;
						if(inventory === "0"){
							$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').addClass('disabled');
						}else{
							$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').removeClass('disabled');
						}
					}
				}else if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
					enableQtyFields(true);
					$('.bopis-location-info').removeClass('hide');
					if($('.alert-box') !== undefined && $('.alert-box').length > 0){
						$('.alert-box').remove();
					}
					if($('.add-to-cart-actions').find('.bopis-store-unavailable').length > 0 ){
						$('.add-to-cart-actions').find('.add-to-cart-submit').addClass('disable-add-to-cart');
					}else{
						$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').removeClass('disable-add-to-cart');
					}
					// jjensen: i don't know why suresh had this in here, but it's not what we want. if you
					//   reinitialize the plugin it will recreate all the pickers and you'll have to delete
					//   them...also not what we want
					// if (typeof KP_PRODUCT !== 'undefined') {
					// 	for (product in KP_PRODUCT) {
					// 		productControllers[product] = new global[namespace].ProductController(KP_PRODUCT[product]);
					// 	}
					// }
				}
				else {
					enableQtyFields();
					$('.bopis-location-info').addClass('hide');
					//switch to home to remove the bopis error if exist
					if($('.alert-box') !== undefined && $('.alert-box').length > 0){
						$('.alert-box').remove();
					}
					//No inventory available for selected store
					$('.add-to-cart-actions').find('.add-to-cart-submit').removeClass('disable-add-to-cart');
					// jjensen: i don't know why suresh had this in here, but it's not what we want. if you
					//   reinitialize the plugin it will recreate all the pickers and you'll have to delete
					//   them...also not what we want
					// if (typeof KP_PRODUCT !== 'undefined') {
					// 	for (product in KP_PRODUCT) {
					// 		productControllers[product] = new global[namespace].ProductController(KP_PRODUCT[product]);
					// 	}
					// }
				}
			});

			// add to cart
			var addToCartOptions = {
					dataType: 'json',
					resetForm: true,
					beforeSerialize : function($form) {
						// gift card
						var $gcAmount = $('#gift-card-amount');

						global[namespace].utilities.form.hideErrors($form);
						$form.validate('clearFieldMessage', $gcAmount);

						if ($gcAmount.length > 0) {
							var ogAmount = $gcAmount.val(),
									newAmount = ogAmount.replace('.', '-').replace(/[^0-9\-]/g, '').replace('-', '.');
							if (newAmount.toString().indexOf('.') > 0) {
								$gcAmount.val(parseFloat(newAmount).toFixed(2));
							}
							if (newAmount < 2 || newAmount > 500 || newAmount !== ogAmount) {
								if (newAmount !== ogAmount) {
									$gcAmount.val(newAmount);
								}
								$gcAmount.focus();
								global[namespace].utilities.form.showInlineErrors({'formId': 'add-to-cart-form', 'fieldsWithErrors': [{'field': $gcAmount, 'errors': ['Please enter a value between $2 and $500']}]});
								return false;
							}
						}
					},
					beforeSubmit: function (arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {

								// update header item counter
								global[namespace].profileController.getProfileStatus(true);

								// update side cart items
								$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
									if ($window.width() > global[namespace].config.smallMax) {
										$('.desktop-header .side-cart-toggle').click();
									}
									else {
										$('.mobile-header .side-cart-toggle').click();
									}
								});

								$('.promo-line-item-msg').html(Mustache.render(TEMPLATES.lineItemPromotions, responseText));

								// clear inputs
								$('.table-qty').each(function(){
									$(this).val('0');
								});

								if($form.attr('id') === "select-bopis-store"){
									$('.ship-to-home').remove();
									$('.bopis-order').attr('checked',true);
									$('.bopis-location-info').removeClass('hide');
								}
								global[namespace].utilities.hideLoader();
								
								if(KP.analytics){
									if(responseText.productId != '')
									KP.analytics.sendAddProduct(responseText.productId);
								}
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
					}
				},
				// Cart Edit Options
				updateCartOptions = {
						dataType: 'json',
						resetForm: true,
						beforeSerialize : function($form) {
							// gift card
							var $gcAmount = $('#gift-card-amount');

							global[namespace].utilities.form.hideErrors($form);
							$form.validate('clearFieldMessage', $gcAmount);

							if ($gcAmount.length > 0) {
								var ogAmount = $gcAmount.val(),
										newAmount = ogAmount.replace('.', '-').replace(/[^0-9\-]/g, '').replace('-', '.');
								if (newAmount.toString().indexOf('.') > 0) {
									$gcAmount.val(parseFloat(newAmount).toFixed(2));
								}
								if (newAmount < 2 || newAmount > 500 || newAmount !== ogAmount) {
									if (newAmount !== ogAmount) {
										$gcAmount.val(newAmount);
									}
									$gcAmount.focus();
									global[namespace].utilities.form.showInlineErrors({'formId': 'add-to-cart-form', 'fieldsWithErrors': [{'field': $gcAmount, 'errors': ['Please enter a value between $2 and $500']}]});
									return false;
								}
							}
						},
						beforeSubmit: function (arr, $form, options) {
							global[namespace].utilities.showLoader();
						},
						success: function (responseText, statusText, xhr, $form) {
							if (statusText == 'success') {
								if (responseText.success == 'true') {

									// update header item counter
									global[namespace].profileController.getProfileStatus(true);

									// update sidecart
									$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp');

									// clear inputs
									$('.table-qty').each(function(){
										$(this).val('0');
									});
									global[namespace].utilities.hideLoader();
									window.location = responseText.url;
								}
								else {
									$('#quantity').val($('#prevQuantity').val());
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.showErrors($form, responseText);
								}
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
							}
						},
						error: function (xhr, statusText, exception, $form) {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
						}
					},
				addToWishListOptions = {
					dataType: 'json',
					resetForm: true,
					beforeSubmit: function (arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $modalTarget = document.getElementById('wish-list-confirmation-modal') ? $('#wish-list-confirmation-modal') : global[namespace].utilities.createModal('wish-list-confirmation-modal', 'medium');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/addToWishListConfirmation.jsp?productId=' + responseText.productId + '&skuId=' + responseText.skuId});

								if ($('.product-pickers').hasClass('table-picker')) {
									$('.table-details.active-sku').find('.add-to-wish-list').replaceWith('<span class="added-to-wish-list">Added To Wish List</span>');
								}else{
									$('.add-to-wish-list').replaceWith('<span class="added-to-wish-list">Added To Wish List</span>');
								}
								global[namespace].utilities.hideLoader();
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to wish list error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to wish list error: " + exception, $form);
					}
				};

			$('.add-to-cart-submit').on('click', function (e) {
				e.preventDefault();
				e.stopPropagation();

				var prodId = '',
						quantity = '',
						skuId = '',
						skuSet = false,
						$addToCartForm = $('#add-to-cart-form'),
						$productPickers = $('.product-pickers'),
						selectedStoreId = '',
						$this = $(this);

				if ($productPickers.hasClass('table-picker')) {
					/*$('.table-qty').each(function(){*/
					$('.table-details').removeClass("active-sku");
					if ($this.parents('.table').find('.table-qty').val() > 0) {
						skuSet = true;
						skuId = $this.parents('.table').find('.table-sku-id').val();
						quantity = $this.parents('.table').find('.table-qty').val()
						selectedStoreId = $('#bopis-store-id').val();
						$this.parents('.table').addClass('active-sku');
						global[namespace].utilities.form.hideErrors($addToCartForm);
						//return false;
					}
				/*});*/
				}
				else {
					prodId = $('#productId').val();
					skuId = $('#catalogRefIds').val();
					quantity = $('#quantity').val();
					selectedStoreId = $('#bopis-store-id').val();
					if (skuId !== '') {
						skuSet = true;
					}
				}

				// make sure there's a sku set
				if (skuSet) {

					// bopis add to cart

					if($this.parents('.add-to-cart-actions').find('.bopis-order') !== null && $this.parents('.add-to-cart-actions').find('.bopis-order').is(':checked')) {

						var edsPPSOnlyOrder = $('#eds-pps-only-inventory').val();
						var bopisOnlyItem = $('#bopis-order-inventory').val();
						// set skuId, quantity and fromProduct hidden bopis inputs
						$('#bopis-sku-id').val(skuId);
						$('#bopis-sku-id-inventory').val(skuId);
						$('#bopis-quantity').val(quantity);
						$('#bopis-quantity-inventory').val(quantity);
						$('#bopis-from-product').val('true');
						$('#bopis-from-product-inventory').val('true');

						if (selectedStoreId !== '' && edsPPSOnlyOrder != 'true') {
							// there's already a bopis store selected, check inventory at current store

							// inventory check
							$('#bopis-inventory-form').ajaxSubmit({
								dataType: 'json',
								beforeSubmit: function (arr, $form, options) {
									global[namespace].utilities.showLoader();
								},
								success: function (responseText, statusText, xhr, $form) {
									global[namespace].searchResults = responseText;

									if (statusText == 'success') {
										if (responseText.success == 'true') {

											// iterate through selected stores
											var inventoryAvailable = false;
											for (var i=0; i<global[namespace].searchResults.stores.length; i++) {
												if (global[namespace].searchResults.stores[i].locationId == selectedStoreId) {
													global[namespace].searchResults.current = global[namespace].searchResults.stores[i];
													if (global[namespace].searchResults.stores[i].eligible == 'true') {
														inventoryAvailable = true;
														break;
													}
												}
											}
											if (inventoryAvailable) {
												// there is sufficient inventory, submit add to cart
												$('#select-bopis-store').ajaxSubmit(addToCartOptions);
											}
											else {
												// not enough inventory, pop up bopis modal with available stores
												var $modalTarget = document.getElementById('bopis-notification-modal') ? $('#bopis-notification-modal') : global[namespace].utilities.createModal('bopis-notification-modal', 'small');
												$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisNotificationModal.jsp'});
												global[namespace].utilities.hideLoader();
											}
										}
										else {
											global[namespace].utilities.hideLoader();
											global[namespace].utilities.form.showErrors($form, responseText);
										}
									}
									else {
										global[namespace].utilities.hideLoader();
										global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
									}
								},
								error: function (xhr, statusText, exception, $form) {
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
								}
							});
						} else if (selectedStoreId !== '' && edsPPSOnlyOrder == 'true') {
							var $modalTarget = document.getElementById('eds-pps-only-notification-modal') ? $('#eds-pps-only-notification-modal') : global[namespace].utilities.createModal('eds-pps-only-notification-modal', 'small');
							$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/edsPPSNotificationModal.jsp?bopisOnlyItem='+bopisOnlyItem});
							global[namespace].utilities.hideLoader();
						}
						else {

							if(edsPPSOnlyOrder != 'true') {
								// there's not a bopis store selected, show the modal
								var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
							} else {
								var $modalTarget = document.getElementById('eds-pps-only-notification-modal') ? $('#eds-pps-only-notification-modal') : global[namespace].utilities.createModal('eds-pps-only-notification-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/edsPPSNotificationModal.jsp?bopisOnlyItem='+bopisOnlyItem});
								global[namespace].utilities.hideLoader();
							}

						}
					}
					else {
						// normal add to cart
						$addToCartForm.ajaxSubmit(addToCartOptions);
					}
				}
				else {
					if ($productPickers.hasClass('table-picker')) {
						global[namespace].utilities.form.showFormErrors($addToCartForm, {'errorMessages': 'Please enter a quantity for SKU'});
					}
					else {
						productControllers[prodId].showSelectionErrors();
					}
				}
			});

			// eds modal
			$('.eds-message .reveal-eds-modal').on('click', function(e) {
				e.preventDefault();
				var html = $('#eds-info-modal').html();
				var $modalTarget = document.getElementById('eds-modal') ? $('#eds-modal') : global[namespace].utilities.createModal('eds-modal', 'small');
  			  	$modalTarget.modal({ 'content': html });
			});

			// add to wish list
			$body.on('click','.add-to-wish-list, .login-modal-trigger', function (e) {
				e.preventDefault();
				e.stopPropagation();

				var skuId = '',
						skuSet = false,
						isTablePicker= $('.product-pickers').hasClass('table-picker'),
						$addToCartForm = $('#add-to-cart-form'),
						$this = $(this);

				if (isTablePicker) {
					/*$('.table-qty').each(function(){
						var $this = $(this);
						if ($this.val() > 0) {
							skuSet = true;
							skuId = $this.siblings('.table-sku-id').val();
							global[namespace].utilities.form.hideErrors($addToCartForm);
							return false;
						}
					});*/
					$('.table-details').removeClass("active-sku");
					if ($this.parents('.table').find('.table-qty').val() > 0) {
						skuSet = true;
						skuId = $this.parents('.table').find('.table-sku-id').val();
						quantity = $this.parents('.table').find('.table-qty').val();
						$this.parents('.table').addClass('active-sku');
						global[namespace].utilities.form.hideErrors($addToCartForm);
						//return false;
					}
				}
				else {
					skuId = $('#catalogRefIds').val();
					if (skuId !== '') {
						skuSet = true;
					}
				}

				// make sure there's a sku set
				if (skuSet) {
					$('#wish-list-sku').val(skuId);
					if($(this).hasClass('login-modal-trigger')){
						var $modalTarget = document.getElementById('login-modal') ? $('#login-modal') : global[namespace].utilities.createModal('login-modal', 'medium');
						$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/login-modal.jsp'});
					}else{
						$('#addItemToWishList').ajaxSubmit(addToWishListOptions);
					}
				}
				else {
					if (isTablePicker) {
						global[namespace].utilities.form.showFormErrors($addToCartForm, {'errorMessages': 'Please enter a quantity for SKU'});
					}
					else {
						//productControllers[prodId].showSelectionErrors();
						global[namespace].utilities.form.showFormErrors($addToCartForm, {'errorMessages': 'Please select a Length'});
					}
				}
			});

			// Edit Cart form
			$('.update-cart-submit').on('click', function (e) {
				e.preventDefault();
				e.stopPropagation();

				var skuId = '',
					quantity = 0,
					itemId=$('#removalIds').val(),
					editMode=$('#bopis-edit-mode-inventory').val(),
						skuSet = false,
						isTablePicker= $('.product-pickers').hasClass('table-picker'),
						$updateCart = $('#updateCart'),
						$this = $(this);

				if (isTablePicker) {
					/*$('.table-qty').each(function(){
						var $this = $(this);
						if ($this.val() > 0) {
							skuSet = true;
							quantity = $this.val();
							skuId = $this.siblings('.table-sku-id').val();
							global[namespace].utilities.form.hideErrors($updateCart);
							return false;
						}
					});*/
					if ($this.parents('.table').find('.table-qty').val() > 0) {
						skuSet = true;
						skuId = $this.parents('.table').find('.table-sku-id').val();
						quantity = $this.parents('.table').find('.table-qty').val()
						global[namespace].utilities.form.hideErrors($updateCart);
						//return false;
					}
				}
				else {
					skuId = $('#catalogRefIds').val();
					if (skuId !== '') {
						skuSet = true;
					}
				}
				console.log('update cart');
				// make sure there's a sku set
				if (skuSet) {
					// bopis add to cart
					if($this.parents('.add-to-cart-actions').find('.bopis-order') !== null && $this.parents('.add-to-cart-actions').find('.bopis-order').is(':checked')) {

						var selectedStoreId = $('#bopis-store-id').val();
						// set skuId, quantity and fromProduct hidden bopis inputs
						//skuId = $('#catalogRefIds').val();
						//quantity = $('#quantity').val();

						$('#bopis-sku-id').val(skuId);
						$('#bopis-sku-id-inventory').val(skuId);
						if(editMode) {
							$('#bopis-removalIds').val(itemId);
							$('#bopis-edit-mode').val(editMode);
						}
						$('#bopis-quantity').val(quantity);
						$('#bopis-quantity-inventory').val(quantity);
						$('#bopis-edit-mode').val(editMode);
						$('#bopis-from-product').val('true');
						$('#bopis-from-product-inventory').val('true');
						$('#bopis-removalIds').val(itemId);
						
						if (selectedStoreId !== '') {
							// there's already a bopis store selected, check inventory at current store

							// inventory check
							$('#bopis-inventory-form').ajaxSubmit({
								dataType: 'json',
								beforeSubmit: function (arr, $form, options) {
									global[namespace].utilities.showLoader();
								},
								success: function (responseText, statusText, xhr, $form) {
									global[namespace].searchResults = responseText;

									if (statusText == 'success') {
										if (responseText.success == 'true') {

											// iterate through selected stores
											var inventoryAvailable = false;
											for (var i=0; i<global[namespace].searchResults.stores.length; i++) {
												if (global[namespace].searchResults.stores[i].locationId == selectedStoreId) {
													global[namespace].searchResults.current = global[namespace].searchResults.stores[i];
													if (global[namespace].searchResults.stores[i].eligible == 'true') {
														inventoryAvailable = true;
														break;
													}
												}
											}
											if (inventoryAvailable) {
												// there is sufficient inventory, submit add to cart
												//$('#select-bopis-store').ajaxSubmit(addToCartOptions);
												$('#updateItemSku').val(skuId);
												var editQty = isTablePicker?quantity:$('#quantity').val();
												$('#editQuantity').val(editQty);
												$('#updateItem').ajaxSubmit(updateCartOptions);
											}
											else {
												// not enough inventory, pop up bopis modal with available stores
												var $modalTarget = document.getElementById('bopis-notification-modal') ? $('#bopis-notification-modal') : global[namespace].utilities.createModal('bopis-notification-modal', 'small');
												$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisNotificationModal.jsp'});
												global[namespace].utilities.hideLoader();
											}
										}
										else {
											global[namespace].utilities.hideLoader();
											global[namespace].utilities.form.showErrors($form, responseText);
										}
									}
									else {
										global[namespace].utilities.hideLoader();
										global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
									}
								},
								error: function (xhr, statusText, exception, $form) {
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
								}
							});
						}
						else {
							// there's not a bopis store selected, show the modal
							var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
							$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
						}
					} else {
						$('#updateItemSku').val(skuId);
						var editQty = isTablePicker?quantity:$('#quantity').val();
						$('#editQuantity').val(editQty);
						$('#updateItem').ajaxSubmit(updateCartOptions);
					}
				}
				else {
					if (isTablePicker) {
						global[namespace].utilities.form.showFormErrors($updateCart, {'errorMessages': 'Please enter a quantity for at least one SKU'});
					}
					else {
						productControllers[prodId].showSelectionErrors();
					}
				}
			});

			// initialize cross sells slider
			$('.cross-sells').slick({
				dots: false,
				infinite: false,
				slidesToShow: 4,
				slidesToScroll: 4,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// initialize recently viewed slider
			$('.recently-viewed').slick({
				dots: false,
				arrows: false,
				infinite: false,
				slidesToShow: 4,
				slidesToScroll: 4,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: true,
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// initialize product image zoom
			$('.product-image-viewer').zoom();

			// turn thumbnails into slick slider
			function initImageViewer() {
				// small-only settings
				var cssClass = 'small-slick';
				var wrongClass = 'medium-slick';
				var slickOpts = {
					dots: true,
					arrows: false,
					infinite: false,
					slidesToShow: 1,
					slidesToScroll: 1
				};
				// medium-up settings
				if ($window.width() > global[namespace].config.smallMax) {
					cssClass = 'medium-slick';
					wrongClass = 'small-slick';
					slickOpts = {
						dots: false,
						arrows: true,
						infinite: false,
						slidesToShow: 4,
						slidesToScroll: 2
					};
				}
				// slick slider is already
				// initialized at the correct size
				var $thumbs = $('.viewer-thumbnails').removeClass(wrongClass);
				if ($thumbs.hasClass(cssClass)) {
					return;
				}
				// initialize slick slider
				if ($thumbs.hasClass('slick-initialized')) {
					$thumbs.slick('unslick');
				}
				$thumbs.addClass(cssClass).slick(slickOpts);
			}

			// make sure vimeo thumbs are vertically centered
			function centerVimeoThumbs() {
				var targetHeight = $('.viewer-thumb').eq(0).height();
				$('.vimeo-thumb').each(function() {
					var $this = $(this);
					var margin = 0;
					var thisHeight = $this.find('img').height();
					if (thisHeight < targetHeight) {
						margin = (targetHeight - thisHeight) / 2;
					}
					$this.css('margin', margin + 'px 0px');
				});
			}

			initImageViewer();
			$window.load(centerVimeoThumbs);
			$window.resize($.throttle(250, initImageViewer));
			$window.resize($.throttle(250, centerVimeoThumbs));

			// adding thumbnail image error event listener
			$('.viewer-thumb-image').error(function(){
				$(this).siblings('.th-image').attr('srcset', CONSTANTS.productImageRoot + '/unavailable/th.jpg');
			});

			// swap images on thumbnail image click
			$('.viewer-thumb-image').on('click', function (e) {
				var $this = $(this);
				var imageName = $this.attr('data-image-name');
				var $mainImage = $('.viewer-main-image');

				//  set active thumbnail if the user clicked a video
				if ($this.find('.vimeo-modal').length > 0) {
					$('.viewer-thumb').removeClass('active');
					$this.parents('.viewer-thumb').addClass('active');
					return;
				}

				// check to see if image is already selected
				if ($mainImage.attr('data-image-name') == imageName) {
					return;
				}
				else {
					var path = CONSTANTS.productImageRoot + '/'+ $this.attr('data-id');
					var lPath = path + '/l/' + imageName;
					var xlPath = path + '/x/' + imageName;
					var zPath = path + '/z/' + imageName;

					// set active main image
					$('#ml-main-image').attr('srcset', lPath);
					$('#s-main-image').attr('srcset', xlPath);
					$mainImage.attr('src', lPath);
					$mainImage.attr('data-image-name', imageName);

					// set active zoom image
					$('.zoom-magnified-image').attr('src', zPath);

					// set active thumbnail
					$('.viewer-thumb').removeClass('active');
					$this.parents('.viewer-thumb').addClass('active');
				}
			});

			// gift card amounts
			$('.gift-card-button').on('click', function(e){
				e.preventDefault();
				$('#gift-card-amount').val($(this).data('amount'));
			});

		},
		search : function() {

			// mobile refinements
			var $leftCol = $('.two-column-left'),
				$catDrop = $('.category-dropdowns');
			$('.hide-refinements').on('click', function(){
				$leftCol.hide();
				$catDrop.removeClass('open');
			});
			$('.show-refinements').on('click', function(){
				$leftCol.show();
				$catDrop.addClass('open');
			});

			//initalize filters
			var filterController = new global[namespace].FilterController();
			
			if (KP.analytics) {
				KP.analytics.sendBrowsePageEvents();
				$(document).on('click', '.product-grid a', function(event) {
					var pid = $(this).data("pid"),
						action = $(this).data("action");
					if (pid) {
						KP.analytics.sendProductClick(pid, action);
					}
				});
			}

		}
	};

	global[namespace] = global[namespace] || {};

	global[namespace].browse = browse;

})(this, window.jQuery, "KP");

/*!
 * Checkout Init
 */
(function (global, $, namespace) {
	//"use strict";

	var CONSTANTS = global[namespace].constants,
			TEMPLATES = global[namespace].templates,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			$body = $('body');

	var checkout = {
		init : function () {},
		login : function() {
			var profileOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					if (statusText == 'success') {
						if (responseText.success == 'true') {
							// reset profile cookie on login
							global[namespace].profileController.resetProfileStatus();
							window.location = responseText.url;
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.showErrors($form, responseText);
						}
					}
					else {
						console.log('Malformed JSON : missing statusText parameter:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
					}
				},
				error: function(xhr, statusText, exception, $form) {
					console.log('AJAX Error:');
					global[namespace].utilities.hideLoader();
					global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
				}
			};
			$('#login-form').ajaxForm(profileOptions);
		},
		cart : function() {

			// enable buttons
			$('.change-store, .ship-my-order, .auto-remove-item-trigger').removeClass('disabled');

			// LTL / Oversize / Additional Shipping / Long and Light Modal
			if ($('#is-bopis-order').val() !== 'true') {
				var signatureRequired = $('#signatureRequired').val(),
						longLight = $('#longLight').val(),
						isOversize = $('#isOversize').val(),
						isLTLOrder = $('#isLTLOrder').val(),
						totalLTLWeight = $('#totalLTLWeight').val(),
						rangeLow = $('#rangeLow').val(),
						rangeHigh = $('#rangeHigh').val(),
						ltlShippingCharges = $('#ltlShippingCharges').val(),
						hasSurcharge = $('#hasSurcharge').val(),
						totalSurcharge = $('#totalSurcharge').val();

				// 2427 - Do not pop-up modal if ltlOrder but no surcharge because of free freight shipping
				if ((typeof signatureRequired !== 'undefined' && signatureRequired !== '0') || (typeof longLight !== 'undefined' && longLight !== '0') || (typeof isOversize !== 'undefined' && isOversize !== '0') || (typeof isLTLOrder !== 'undefined' && isLTLOrder !== 'false'  && typeof ltlShippingCharges !== 'undefined' && ltlShippingCharges != '0.0') || (typeof hasSurcharge !== 'undefined' && hasSurcharge !== 'false')) {
					var $modalTarget = document.getElementById('additional-shipping-modal') ? $('#additional-shipping-modal') : global[namespace].utilities.createModal('additional-shipping-modal', 'small');
					$modalTarget.modal({'url': CONSTANTS.contextPath + '/checkout/ajax/additionalShippingModal.jsp?signatureRequired=' + signatureRequired + '&longLight=' + longLight + '&isOversize=' + isOversize + '&isLTLOrder=' + isLTLOrder + '&totalLTLWeight=' + totalLTLWeight + '&rangeLow=' + rangeLow + '&rangeHigh=' + rangeHigh + '&ltlShippingCharges=' + ltlShippingCharges + '&hasSurcharge=' + hasSurcharge + '&totalSurcharge=' + totalSurcharge});
				}
			}
			else {
				// show change store modal
				if (global[namespace].utilities.getURLParameter(window.location.href, 'changeStore') == 'true') {
					var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
					$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
				}
			}

			// cart item form handlers
			var updateCartItemOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					$('.modal').modal('hide');
					global[namespace].utilities.form.hideErrors($('.error-container'));
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					if (statusText == 'success') {
						if (responseText.success == 'true') {

							// hide ajax loader
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.hideErrors($form);

							// update the cart qty on page and in cookie
							global[namespace].profileController.getProfileStatus(true);
							
							//analytics for remove item from cart
							if($form.attr('id') == "cartRemoveForm"){
								var productId = "",
									ciId = "";
								$('.order-item').each(function(){
										ciId = $(this).attr('data-ciid'),
											matched = false;
									// loop thru cart items in json - look for match in cart
									for (var i=0; i<responseText.cartItems.length; i++) {
										if (ciId == responseText.cartItems[i].commerceItemId) {
											matched = true;
											break;
										}
									}
									// if not matched, remove
									if (!matched) {
										var $orderItem = $('.order-item[data-ciid="' + ciId + '"]');
										productId = $orderItem.attr('data-prodId');
									}
								});
															
								if(KP.analytics){
									if(productId!='')
									KP.analytics.sendRemoveProduct(productId);
								}
							}

							// update sidecart
							$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp');

							if (responseText.cartCount > 0) {

								// loop through cart items
								var currentCartItemCIDs = [];
								$.each(responseText.cartItems, function(index){
									var $orderItem = $('.order-item[data-ciid="' + this.commerceItemId + '"]');
									// Changes related to 2414. See cartItem.jspf for details
									var $freeItem = $('.order-item[data-ciid="' + this.commerceItemId + '-FREE"]');

									if ($orderItem.length > 0) {
										
										// item is on page, update item totals
										//console.log("Currently processing item " + this.commerceItemId);
										$orderItem.find('.promo-line-item-msg').html(Mustache.render(TEMPLATES.lineItemPromotions, responseText.cartItems[index]));
										$orderItem.find('#quantityUpdate').val(this.itemQuantity);
										$orderItem.find('#totalQuantity').val(this.totalItemQuantity);
										$orderItem.find('.item-total').html(this.totalLinePrice);
										if(this.freeGift == 'true') {
											//console.log(this.commerceItemId + " is a gift");
											$orderItem.find('.item-quantity').addClass("hide"); 
											$orderItem.find('.item-price-unit').addClass("hide"); 
											//$orderItem.find('.item-total').removeClass("item-total"); 
											$orderItem.find('.item-actions').addClass("hide");
											$orderItem.find('.item-total').addClass("free");
											$orderItem.find('.change-quantity').attr('data-free',this.itemQuantity);
											
										} else {
											//console.log("current data free is " + $orderItem.find('.change-quantity').attr('data-free'));
											//console.log("Set data free to " + $freeItem.find('.change-quantity').attr('data-free'));
											//console.log(this.commerceItemId + " is NOT a gift");
											//console.log("Before update " + $orderItem.find('.change-quantity').attr('data-free'));
											//console.log("Setting to " + $freeItem.find('.change-quantity').attr('data-free'));
											$orderItem.find('.change-quantity').attr('data-free',$freeItem.find('.change-quantity').attr('data-free'));
											//console.log("After update " + $orderItem.find('.change-quantity').attr('data-free'));
											$orderItem.find('#freeQuantity').val($freeItem.find('.change-quantity').attr('data-free'));
											$orderItem.find('.item-quantity').removeClass("hide");
											$orderItem.find('.item-price-unit').removeClass("hide"); 
											$orderItem.find('.item-actions').removeClass("hide");
											$orderItem.find('.item-total').removeClass("free");
										}

										// remove the item from the array of original items
										currentCartItemCIDs.pop(this.commerceItemId);
									} else if ($freeItem.length > 0){
										$freeItem.find('.item-total').html(this.totalLinePrice);
										$freeItem.find('#quantityUpdate').val(this.itemQuantity);										
									}
									else {
										// item is new, refresh page to get new cart items
										window.location.reload();
									}
								});

								// check if cart item was removed (clicked - button and now qty is 0)
								$('.order-item').each(function(){
									var ciid = $(this).attr('data-ciid'),
											matched = false;
									// loop thru cart items in json - look for match in cart
									for (var i=0; i<responseText.cartItems.length; i++) {
										if (ciid == responseText.cartItems[i].commerceItemId) {
											$(this).removeAttr( 'style' );
											matched = true;
											break;
										}
									}
									// if not matched, remove
									if (!matched) {
										$('.order-item[data-ciid="' + ciid + '"]').slideUp(200);
									}
								});

								// update order totals
								$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

								// if cart is bopis only, remove "ship my order instead" link
								if (responseText.bopisOnly == 'true') {
									$('.auto-remove-item-trigger').remove();
									// if it's a mixed bopisOnly/non-bopisOnly cart, you should never have the ship-my-order link
									// $('.ship-my-order').remove();
								}

								// // update promotions
								// var updatedAppliedPromos = Mustache.render(TEMPLATES.appliedPromotionsCart, responseText);
								// $('#promo-applied-area').html(updatedAppliedPromos);
								// if (responseText.couponMessages.length > 0) {
								// 	$('#apply-promo').after(Mustache.render(TEMPLATES.couponMessageTemplate, responseText));
								// 	$('#cartPromoForm').show();
								// }

								// LTL / Oversize / Additional Shipping / Long and Light Modal
								if (responseText.isBopisOrder !== 'true') {
									var signatureRequired = responseText.signatureRequired,
											longLight = responseText.longLight,
											isOversize = responseText.isOversize,
											isLTLOrder = responseText.isLTLOrder,
											totalLTLWeight = responseText.totalLTLWeight,
											rangeLow = responseText.rangeLow,
											rangeHigh = responseText.rangeHigh,
											ltlShippingCharges = responseText.ltlShippingCharges,
											hasSurcharge = responseText.hasSurcharge,
											totalSurcharge = responseText.totalSurcharge,
											$surcharge = $('#totalSurcharge'),
											currentSurcharge = $surcharge.val();

									if (totalSurcharge !== currentSurcharge) {
										$surcharge.val(totalSurcharge);
									}
									else {
										hasSurcharge = 'false';
									}

									// 2427 - Do not pop-up modal if ltlOrder but no surcharge because of free freight shipping
									if ((typeof signatureRequired !== 'undefined' && signatureRequired !== '0') || (typeof longLight !== 'undefined' && longLight !== '0') || (typeof isOversize !== 'undefined' && isOversize !== '0') || (typeof isLTLOrder !== 'undefined' && isLTLOrder !== 'false' && typeof ltlShippingCharges !== 'undefined' && ltlShippingCharges != '0.0') || (typeof hasSurcharge !== 'undefined' && hasSurcharge !== 'false')) {
										var $modalTarget = document.getElementById('additional-shipping-modal') ? $('#additional-shipping-modal') : global[namespace].utilities.createModal('additional-shipping-modal', 'small');
										$modalTarget.modal({'url': CONSTANTS.contextPath + '/checkout/ajax/additionalShippingModal.jsp?signatureRequired=' + signatureRequired + '&longLight=' + longLight + '&isOversize=' + isOversize + '&isLTLOrder=' + isLTLOrder + '&totalLTLWeight=' + totalLTLWeight + '&rangeLow=' + rangeLow + '&rangeHigh=' + rangeHigh + '&ltlShippingCharges=' + ltlShippingCharges + '&hasSurcharge=' + hasSurcharge + '&totalSurcharge=' + totalSurcharge});
									}
								}

							}
							else {
								// cart is empty, show appropriate message
								$('.cart-content').load(CONSTANTS.contextPath + '/checkout/fragments/cartEmpty.jspf');
							}

							// if move wish list, show modal
							if (responseText.isWishList) {
								var $modalTarget = document.getElementById('wish-list-confirmation-modal') ? $('#wish-list-confirmation-modal') : global[namespace].utilities.createModal('wish-list-confirmation-modal', 'medium');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/addToWishListConfirmation.jsp?productId=' + responseText.productId + '&skuId=' + responseText.skuId});
							}
							
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.showErrors($form, responseText);
							// BZ 2523 - When item qty fails qty validation checks
							// the UI should display the prior qty. It is retaining the qty that failed the validation
							for(var i=0; i < responseText.quantities.length; i++) {
								$('input[name="' + responseText.quantities[i].itemId + '"]').val(responseText.quantities[i].qty);
							}
						}
					}
					else {
						console.log('Malformed JSON : missing statusText parameter:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
					}
				},
				error: function(xhr, statusText, exception, $form) {
					console.log('AJAX Error:');
					global[namespace].utilities.hideLoader();
					global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
				}
			};

			// remove item from cart
			$body.on('click', '.item-remove', function(e){
				var ciId = $(this).data('ciid').trim();
				var $orderItem = $('.order-item[data-ciid="' + ciId + '"]');
				
				// 2414 - when the free SKU is also added directly by user
				// it is still one commerce item in the order but displayed
				// in two lines on the cart UI. "Remove" removes the entire item
				// including the gift. This is not desired.
				// we send removalCommerceIds in the format id#qty to remove just the user added qty
				var userAddedQty = $orderItem.find('#quantityUpdate').val();
				$('#removeCommerceIds').val(ciId+"#"+userAddedQty);
				$('#cartRemoveForm').ajaxSubmit(updateCartItemOptions);
			});

			// move item to wish list
			$body.on('click', '.item-save', function(e){
				var ciId = $(this).data('ciid').trim();
				$('#moveItemId').val(ciId);
				$('#cartItemMoveToWishList').ajaxSubmit(updateCartItemOptions);
			});

			// hides/shows update link when using keyboard to update the quantity
			$('.counter').keyup(function(event){
				var counterObj = $(this),
						keycode = (event.keyCode ? event.keyCode : event.which),
						updateId = '#updateQty-' + this.getAttribute('name');
						// related to 2414
						updateId = updateId.replace('-display','');

				if ((keycode >= 48 && keycode <= 57) || keycode == 8 || keycode == 46) {
					// 48-57 - numbers
					// 8 - backspace
					// 46 - delete
					// 32 - spacebar
					if (counterObj.val() !== '') {
						$(updateId).fadeIn('fast').parents('.order-item').addClass('update-showing');
					}
					else {
						$(updateId).hide();
					}
				}
				else if (keycode == 13) {
					// 13 - enter
					$(updateId).find('a').click();
				}
				else {
					if (keycode !== 32) {
						$(updateId).hide();
					}
				}
			});

			// update item quantity in cart
			$('#cartUpdateForm').on('click', '.updateCartQty', function(e){
				e.preventDefault();
				$('#cartUpdateForm').ajaxSubmit(updateCartItemOptions);
				$('.update-qty').hide();
				$('.order-item').removeClass('update-showing');
			}).on('increment decrement', function(){
				$('#cartUpdateForm').ajaxSubmit(updateCartItemOptions);
				$('.update-qty').hide()
				$('.order-item').removeClass('update-showing');
			});

			// apply tax exemption
			var taxExemptionOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.form.hideErrors($form);
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
			$('#tax-exemption-select-form').ajaxForm(taxExemptionOptions);

			$('#tax-exemption').on('change', function(e){
				$('.apply-tax-exemption').click();
			});

		},
		checkout : function() {

			// sticky checkout progress bar
			var $progressBar = $('.checkout-progress'),
					$window = $(window);

			if ($window.width() < global[namespace].config.mediumMin) {
				$window.scroll($.throttle(200, function(e){
					if ($window.scrollTop() > 100) {
						$progressBar.addClass("fixed-progress");
					} else {
						$progressBar.removeClass("fixed-progress");
					}
				}));
			}
			else {
				$progressBar.removeClass("fixed-progress");
			}

			// mask phone numbers
			$('#phone').mask('000-000-0000');
			$('#billing-phone').mask('000-000-0000');

			// enable buttons
			$('.ship-my-order, .bopis-continue, .shipping-address-continue, .shipping-method-continue, .payment-continue, .place-order-btn').removeClass('disabled');

			// shipping
			var $newShippingAddress = $('.new-shipping-address'),
					$addressName = $('.address-name');
			$('input[name="shipping-address"]').click(function(){
				if (document.getElementById('shipping-address-new').checked) {
					$newShippingAddress.slideDown(250);
				}
				else {
					document.getElementById('save-shipping-address').checked = false;
					$addressName.slideUp(250);
					$newShippingAddress.slideUp(250);
				}
			});
			$('#save-shipping-address').click(function(){
				if (this.checked) {
					$addressName.slideDown(250);
				}
				else {
					$addressName.slideUp(250);
				}
			});

			// saturday delivery
			$body.on('click', 'input[name="shipping"]', function(){
				var shippingMethod = this.value,
						$saturdayDelivery = $('.saturday-delivery');

				document.getElementById('saturday-delivery').checked = false;
				$.ajax({
					url: CONSTANTS.contextPath + '/checkout/json/saturdayDeliveryJson.jsp',
					data: {shippingMethod: shippingMethod},
					cache: false,
					beforeSend: function(){
						global[namespace].utilities.showLoader();
					},
					success: function(data){
						if (data.isSatDayDelivery == 'true') {
							$saturdayDelivery.removeClass('hide');
						}
						else {
							$saturdayDelivery.addClass('hide');
						}
						global[namespace].utilities.hideLoader();
					},
					error: function(data){
						global[namespace].utilities.hideLoader();
					}
				});
			});

			var $checkoutLeftColumn = $('.checkout-left-column'),
				shippingAddressOptions = {
					dataType: 'json',
					beforeSerialize : function($form) {
						$form.find('#phone').unmask();
					},
					beforeSubmit: function (arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');

						if (statusText == 'success') {
							if (responseText.success == 'true') {
								if (responseText.addressMatched == 'true') {
									// avs passed

									// update order totals
									$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

									// update gift card totals in checkout payment sections
									$.each(responseText.appliedGiftCards, function(){
										$('.amount-' + this.number).html(this.amount);
									});

									// update review panels
									if (responseText.fflOrder == 'true'){
										// update ffl dealer review panel
										$('.shipping-address-review').load(CONSTANTS.contextPath + '/checkout/includes/fflDealerInfoReview.jsp');
									}
									else {
										// update shipping address review panel
										$('.shipping-address-review').load(CONSTANTS.contextPath + '/checkout/includes/shippingAddressReview.jsp');
										//$('.shipping-address-review').load(CONSTANTS.contextPath + '/checkout/includes/fflDealerInfoReview.jsp');
									}
									// show edit shipping address link
									$('.edit-shipping-address').show(0);

									// update shipping methods
									$('.shipping-method-fields').load(CONSTANTS.contextPath + '/checkout/includes/shippingMethodAJAX.jsp .shipping-method-radios', function(){
										// move on to shipping method step
										$('.shipping-address').slideUp(250, function(){
											$('.shipping-address-review-panel').slideDown(250, function(){
												$('.shipping-method').slideDown(250, function(){
													global[namespace].utilities.hideLoader();
													$body.scrollTop($('.shipping-method').offset().top);
												});
											});
										});
									});
									if (KP.analytics) {
										if(digitalData)
											digitalData.checkoutStep = 2;
										KP.analytics.sendCheckoutOption("shipping Method");
									}
									
								}
								else {
									// avs failed
									if (typeof avsJSON !== 'undefined') {
										avsJSON = JSON.stringify(responseText);
									}
									else {
										$body.append('<script>var avsJSON = \'' + JSON.stringify(responseText) + '\';</script>');
									}
									global[namespace].utilities.hideLoader();
									var $modalTarget = document.getElementById('avsModal') ? $('#avsModal') : global[namespace].utilities.createModal('avsModal', 'medium'),
										url = CONSTANTS.contextPath + '/checkout/ajax/avsModal.jsp',
										option = {'url': url};
									$modalTarget.modal(option);
								}
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				},
				shippingMethodOptions = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();

								// update shipping method in review panel
								$('.shipping-review-panel .shipping-method-review').load(CONSTANTS.contextPath + '/checkout/includes/shippingMethodReview.jsp');

								// update order totals
								$('#order-items-container').load(CONSTANTS.contextPath + '/checkout/includes/cartItems.jsp');
								$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

								// update gift card totals in checkout payment sections
								$.each(responseText.appliedGiftCards, function(){
									$('.amount-' + this.number).html(this.amount);
								});

								// hide credit card form if order is covered by gift cards
								if (responseText.orderCovered == 'true') {
									$('.payment-method-section').slideUp(250);
									$('.gift-card-info').slideUp(250);
									$('.credit-card-review').slideUp(250);
									$('.billing-address-review').slideUp(250);
								}
								else {
									$('.payment-method-section').slideDown(250);
									$('.gift-card-info').slideDown(250);
									$('.credit-card-review').slideDown(250);
									$('.billing-address-review').slideDown(250);
								}

								// mark step as complete
								$('.checkout-shipping').addClass('complete');
								$('.checkout-progress-shipping').removeClass('in-progress').addClass('complete');

								// close shipping form and show review panel
								$('.checkout-shipping').slideUp(250, function(){

									// open next incomplete step
									if ($('.checkout-payment').hasClass('complete') && responseText.orderCovered == 'true') {
										// open review because payment is complete
										$('.checkout-review').slideDown(250, function(){
											$body.scrollTop($('#checkout-review').offset().top);
											$('.checkout-payment').addClass('complete');
											$('.checkout-review').addClass('in-progress');
											$('.checkout-progress-payment').addClass('complete');
											$('.checkout-progress-review').addClass('in-progress');
											$('.promo-code-container').slideUp(250);
										});
										if (KP.analytics) {
											if(digitalData)
												digitalData.checkoutStep = 4;
											KP.analytics.sendCheckoutOption("review");
										}
									}
									else {
										$('.checkout-payment').slideDown(250, function(){
											$body.scrollTop($('.checkout-payment').offset().top);
											$('.checkout-payment').addClass('in-progress');
											$('.checkout-progress-payment').addClass('in-progress');
										});
										if (KP.analytics) {
											if(digitalData)
												digitalData.checkoutStep = 3;
											KP.analytics.sendCheckoutOption("payment");
										}
									}
								});
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
			$('#shipping-address-form').ajaxForm(shippingAddressOptions);
			$('#shipping-method-form').ajaxForm(shippingMethodOptions);

			// listener for edit shipping address while in shipping step
			$body.on('click', '.edit-shipping-address', function(e){
				e.preventDefault();

				// hide edit shipping address link
				$('.edit-shipping-address').hide(0);

				// reset avs flag
				$('#skip-avs').val(false);

				// show shipping address form
				$('.shipping-method').slideUp(250, function(){
					$('.checkout-shipping .shipping-address-review-panel').slideUp(250, function(){
						$('.shipping-address').slideDown(250, function(){
							$('#phone').mask('000-000-0000');
							$body.scrollTop($('.shipping-address').offset().top);
						});
					});
				});
			});

			// BOPIS
			var bopisPersonOptions = {
					dataType: 'json',
					beforeSerialize : function($form) {
						if ($('#pick-up-other').length > 0) {
							if (document.getElementById('pick-up-other').checked) {
								$('#bopis-name').val($('#bopis-name-other').val());
								$('#bopis-email').val($('#bopis-email-other').val());
							}
							else {
								$('#bopis-name').val($('#bopis-name-me').val());
								$('#bopis-email').val($('#bopis-email-me').val());
							}
						}
					},
					beforeSubmit: function (arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();

								// update shipping address review panel
								$('.bopis-person-review').load(CONSTANTS.contextPath + '/checkout/includes/bopisPersonReview.jsp');

								// update order totals
								$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

								// update gift card totals in checkout payment sections
								$.each(responseText.appliedGiftCards, function(){
									$('.amount-' + this.number).html(this.amount);
								});

								// mark step as complete
								$('.checkout-shipping').addClass('complete');
								$('.checkout-progress-shipping').removeClass('in-progress').addClass('complete');

								// close shipping form and show review panel
								$('.checkout-shipping').slideUp(250, function(){

									// open next incomplete step
									if ($('.checkout-payment').hasClass('complete')) {
										// open review because payment is complete
										$('.checkout-review').slideDown(250, function(){
											$body.scrollTop($('#checkout-review').offset().top);
											$('.checkout-payment').addClass('complete');
											$('.checkout-review').addClass('in-progress');
											$('.checkout-progress-payment').addClass('complete');
											$('.checkout-progress-review').addClass('in-progress');
											$('.promo-code-container').slideUp(250);
										});
										if (KP.analytics) {
											if(digitalData)
												digitalData.checkoutStep = 4;
											KP.analytics.sendCheckoutOption("review");
										}
									}
									else {
										$('.checkout-payment').slideDown(250, function(){
											$body.scrollTop($('.checkout-payment').offset().top);
											$('.checkout-payment').addClass('in-progress');
											$('.checkout-progress-payment').addClass('in-progress');
										});
										if (KP.analytics) {
											if(digitalData)
												digitalData.checkoutStep = 3;
											KP.analytics.sendCheckoutOption("payment");
										}
									}
								});
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
			$('#bopis-person-form').ajaxForm(bopisPersonOptions);

			// BOPIS
			var $pickUpMeForm = $('.pick-up-me-form'),
					$pickUpOtherForm = $('.pick-up-other-form');
			$('input[name="pick-up-person"]').click(function(){
				if (document.getElementById('pick-up-other').checked) {
					$pickUpMeForm.slideUp(250, function(){
						$pickUpOtherForm.slideDown(250);
					});
				}
				else {
					$pickUpOtherForm.slideUp(250, function(){
						$pickUpMeForm.slideDown(250);
					});
				}
			});

			$body.on('click', '.shipping-continue', function(e){

				// mark step as complete
				$('.checkout-shipping').addClass('complete');
				$('.checkout-progress-shipping').removeClass('in-progress').addClass('complete');

				// close shipping form and show review panel
				$('.checkout-shipping').slideUp(250, function(){

					// open next incomplete step
					if ($('.checkout-payment').hasClass('complete') && responseText.orderCovered == 'true') {
						// open review because payment is complete
						$('.checkout-review').slideDown(250, function(){
							$body.scrollTop($('#checkout-review').offset().top);
							$('.checkout-payment').addClass('complete');
							$('.checkout-review').addClass('in-progress');
							$('.checkout-progress-payment').addClass('complete');
							$('.checkout-progress-review').addClass('in-progress');
							$('.promo-code-container').slideUp(250);
						});
					}
					else {
						$('.checkout-payment').slideDown(250, function(){
							$body.scrollTop($('.checkout-payment').offset().top);
							$('.checkout-payment').addClass('in-progress');
							$('.checkout-progress-payment').addClass('in-progress');
						});
					}
				});
			});

			// edit shipping step once completed
			$body.on('click touchstart', '.checkout-review .edit-shipping, .checkout-progress-shipping.complete', function(e){
				e.preventDefault();

				// mark step as incomplete
				$('.checkout-shipping').removeClass('complete').addClass('in-progress');
				$('.checkout-payment').removeClass('in-progress');
				$('.checkout-review').removeClass('in-progress');
				$('.checkout-progress-shipping').removeClass('complete').addClass('in-progress');
				$('.checkout-progress-payment').removeClass('in-progress');
				$('.checkout-progress-review').removeClass('in-progress');

				// reset avs flag
				$('#skip-avs').val(false);

				// close review, gift card, and payment forms
				$('.checkout-review').slideUp(250, function(){
					$('.checkout-payment').slideUp(250, function(){
						$('.checkout-shipping').slideDown(250, function(){
							$body.scrollTop($('.checkout-shipping').offset().top);
							$('.promo-code-container').slideDown(250);
						});
					});
				});
			});

			// gift card
			var $gcForm = $('#gift-card-form'),
				$gcRestrictedText = $('#gift-card-restricted-text'),
				$gcInfo = $('.gift-card-info'),
				$gcReviewPanel = $('.gift-card-review-panel'),
				$ccReview = $('.credit-card-review'),
				$baReview = $('.billing-address-review'),
				$paymentMethod = $('.payment-method-section'),
				giftCardOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {

								// clear form
								$gcForm.find('input[type="tel"]').val('');
								global[namespace].utilities.form.hideErrors($gcForm);

								// mark gift card as applied for edit link style
								$('.gift-card-info').addClass('complete');
								$('.edit-payment').addClass('gc-applied');

								$gcForm.slideUp(250, function(){
									$gcReviewPanel.slideDown(250);
								});

								// update applied gift cards
								if (responseText.appliedGiftCards.length > 0) {
									$gcReviewPanel.html(Mustache.render(TEMPLATES.appliedGiftCards, responseText));
								}
								else {
									$gcReviewPanel.html('').slideUp(250);
								}

								// update order totals
								$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

								// update gift card totals in checkout payment sections
								$.each(responseText.appliedGiftCards, function(){
									$('.amount-' + this.number).html(this.amount);
								});

								// hide credit card form if order is covered by gift cards
								if (responseText.orderCovered == 'true') {
									$paymentMethod.slideUp(250);
									$gcInfo.slideUp(250);
									$ccReview.slideUp(250);
									$baReview.slideUp(250);
								}
								else {
									$paymentMethod.slideDown(250);
									$gcInfo.slideDown(250);
									$ccReview.slideDown(250);
									$baReview.slideDown(250);
								}

								// hide ajax loader
								global[namespace].utilities.hideLoader();
								$body.scrollTop($('#checkout-payment').offset().top);

							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};

			// gift card ajax form handler
			$gcForm.ajaxForm(giftCardOptions);

			// remove gift card from order
			$('body').on('click', '.gift-card-remove', function(){
				$('#giftCardId').val($(this).data('number'));
				$('#giftCardRemovalForm').ajaxSubmit(giftCardOptions);
			});

			// show gift card form
			$('.add-gift-card').click(function(e){
				e.preventDefault();
				$gcForm.slideToggle(250);
			});

			// BZ 2505 - show restricted GC text instead of the form
			$('.gift-card-restricted').click(function(e){
				e.preventDefault();
				$gcRestrictedText.slideToggle(250);
			});
			// payment method
			var $newPaymentMethod = $('.new-payment-method'),
					$paymentName = $('.payment-name'),
					$billingAddress = $('.billing-address'),
					$cvvInput = $('.cvv-input'),
					$paymentInfoForm = $('#payment-info-form');
			$('input[name="payment-method"]').click(function(){
				$cvvInput.val('');
				if (document.getElementById('payment-method-new').checked) {
					$newPaymentMethod.slideDown(250);
				}
				else {
					document.getElementById('save-payment-method').checked = false;
					$paymentName.slideUp(250);
					$newPaymentMethod.slideUp(250);
				}
			});
			$cvvInput.on('blur', function(){
				$('#cvv').val($(this).val());
			});
			
			//2619 - set the cvv val before enter button
			$cvvInput.keypress(function(event){
				var Obj = $(this),
				keycode = (event.keyCode ? event.keyCode : event.which);
				if (keycode == 13) {
					// 13 - enter
					$('#cvv').val($(this).val());
				}
			})
			$('#save-payment-method').click(function(){
				if (this.checked) {
					$paymentName.slideDown(250);
				}
				else {
					$paymentName.slideUp(250);
				}
			});
			$('#same-as-shipping').click(function(){
				if (this.checked) {
					$billingAddress.slideUp(250);
					// $billingAddress.find('input, select').val('').validate('clearFormErrors')
				}
				else {
					$billingAddress.slideDown(250);
				}
			});

			// mff brand synchrony cards
			$('#card-type').on('change', function(){
				var $this = $(this),
					$month = $('#month'),
					$year = $('#year');
				if ($this.val() == 'millsCredit') {
					global[namespace].utilities.form.hideErrors($paymentInfoForm);
					$month.val('12').addClass('disabled').attr('tabindex', '-1');
					$year.val('2049').addClass('disabled').attr('tabindex', '-1');
				}
				else {
					$month.val('').removeClass('disabled').removeAttr('tabindex');
					$year.val('').removeClass('disabled').removeAttr('tabindex');
				}
			});

			var paymentOptions = {
					dataType: 'json',
					beforeSerialize : function($form) {
						$form.find('#billing-phone').unmask();
					},
					beforeSubmit: function (arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#billing-phone').mask('000-000-0000');

						if (statusText == 'success') {
							if (responseText.success == 'true') {
								if (responseText.addressMatched == 'true') {
									// avs passed
									
									// update order totals
									$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

									// listrak email signup
									if (document.getElementById('promo-emails').checked) {
										if (typeof _ltk !== 'undefined') {
											_ltk.SCA.CaptureEmail('email');
										}
									}

									// update payment info in review panel
									$('.payment-info-review-panel').load(CONSTANTS.contextPath + '/checkout/includes/paymentMethodReview.jsp');
									// BZ 2505 - Refresh BOPIS Pickup section when payment details are changed
									$('.bopis-person-review').load(CONSTANTS.contextPath + '/checkout/includes/bopisPersonReview.jsp');
									// mark step as complete
									$('.checkout-payment').removeClass('in-progress').addClass('complete');
									$('.checkout-review').addClass('in-progress');
									$('.checkout-progress-payment').removeClass('in-progress').addClass('complete');
									$('.checkout-progress-review').addClass('in-progress');

									// close gift card and payment forms and show review panel
									$('.checkout-payment').slideUp(250, function(){
										$('.checkout-review').slideDown(250, function(){
											global[namespace].utilities.hideLoader();
											$body.scrollTop($('#checkout-review').offset().top);
											$('.promo-code-container').slideUp(250);
										});
									});
									
									if (KP.analytics) {
										if(digitalData)
											digitalData.checkoutStep = 4;
										KP.analytics.sendCheckoutOption("Review");
									}

								}
								else {
									// avs failed
									if (typeof avsJSON !== 'undefined') {
										avsJSON = JSON.stringify(responseText);
									}
									else {
										$body.append('<script>var avsJSON = \'' + JSON.stringify(responseText) + '\';</script>');
									}
									global[namespace].utilities.hideLoader();
									var $modalTarget = document.getElementById('avsModal') ? $('#avsModal') : global[namespace].utilities.createModal('avsModal', 'medium'),
										url = CONSTANTS.contextPath + '/checkout/ajax/avsModal.jsp',
										option = {'url': url};
									$modalTarget.modal(option);
								}
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							console.log('Malformed JSON : missing statusText parameter:');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						// re-apply phone mask
						$('#billing-phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
			$paymentInfoForm.ajaxForm(paymentOptions);

			$body.on('click touchstart', '.edit-payment, .checkout-progress-payment.complete', function(e){
				e.preventDefault();

				// mark step as incomplete
				$('.checkout-payment').removeClass('complete').addClass('in-progress');
				$('.checkout-review').removeClass('in-progress');
				$('.checkout-progress-payment').removeClass('complete').addClass('in-progress');
				$('.checkout-progress-review').removeClass('in-progress');

				// reset avs flag
				$('#billing-skip-avs').val(false);

				// close review and shipping forms
				$('.checkout-review').slideUp(250, function(){
					$('.checkout-shipping').slideUp(250, function(){
						$('.checkout-payment').slideDown(250, function(){
							$body.scrollTop($('.checkout-payment').offset().top);
							$('.promo-code-container').slideDown(250);
						});
					});
				});
			});

			// order review
			var submitOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					$checkoutLeftColumn.find('.alert-box').remove();
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					if (statusText == 'success') {
						if (responseText.success == 'true') {
							window.location = responseText.url;
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.showErrors($form, responseText);
						}
					}
					else {
						console.log('Malformed JSON : missing statusText parameter:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
					}
				},
				error: function(xhr, statusText, exception, $form) {
					console.log('AJAX Error:');
					global[namespace].utilities.hideLoader();
					global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
				}
			};
			$('#submitOrderForm').ajaxForm(submitOptions);

			// fake commit order button click
			$('#fake-commit-order').on('click', function(e){
				e.preventDefault();
				$('#commit-order').trigger('click');
			});

		},
		orderConfirmation : function() {

			// ensure profile name gets updated
			global[namespace].profileController.getProfileStatus(true);

			$('.create-account-button').click(function(e){
				$('.create-account-message').slideUp(250, function(){
					$('.create-account-form').slideDown(250);
				});
			});

			var profileOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					if (statusText == 'success') {
						if (responseText.success == 'true') {
							// reset profile cookie on login
							global[namespace].profileController.resetProfileStatus();
							window.location = responseText.url;
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.showErrors($form, responseText);
						}
					}
					else {
						console.log('Malformed JSON : missing statusText parameter:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
					}
				},
				error: function(xhr, statusText, exception, $form) {
					console.log('AJAX Error:');
					global[namespace].utilities.hideLoader();
					global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
				}
			};
			$('#register-form').ajaxForm(profileOptions);
		}
	};

	global[namespace] = global[namespace] || {};

	global[namespace].checkout = checkout;

})(this, window.jQuery, "KP");

