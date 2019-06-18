/*
 * This plug-in adds simple helper functions for editor plug-ins
 */

function getResource(key) {
  return window.parent.getResource(key);
}

/*
 * Replace source button with one without the text
 */

var atgSourceButton = new FCKToolbarButton( 'Source'  , FCKLang.Source, null, null, true, true, 1 );
FCKToolbarItems.RegisterItem( 'Source', atgSourceButton ) ;
