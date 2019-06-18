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
