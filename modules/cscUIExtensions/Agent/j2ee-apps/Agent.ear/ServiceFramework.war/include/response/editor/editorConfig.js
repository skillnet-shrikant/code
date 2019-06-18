// Toolbar definitions
FCKConfig.ToolbarSets["Respond"] = [
  ['Undo','Redo','-','SelectAll','RemoveFormat'],
  ['Print','Spell'],
  ['Cut','Copy','Paste','PasteText','PasteWord'],
  ['Link','Unlink','Anchor'],
  '/',
  ['Style'],
  ['Bold','Italic','Underline'],
  ['OrderedList','UnorderedList','-','Outdent','Indent'],
  ['JustifyLeft','JustifyCenter','JustifyRight'],
  ['Image','Table','Rule','SpecialChar','TextColor','BGColor']
] ;

FCKConfig.SkinPath = '/agent/include/editor/respondSkin/';
FCKConfig.EditorAreaCSS = '/agent/css/editor.css';
FCKConfig.CustomStyles = {};
FCKConfig.StylesXmlPath = '/agent/include/editor/editorStyles.jsp' ;


FCKConfig.DefaultLanguage = window.parent.djConfig.locale.substring(0,2);
FCKConfig.AutoDetectLanguage = false;


var respondPluginPath = window.parent.applicationContextRoot + '/include/response/editor/plugins/';
var frameworkPluginPath = window.parent.applicationContextRoot + '/include/editor/plugins/';

FCKConfig.Plugins.Add( 'knowledgeutils', null, frameworkPluginPath) ;
FCKConfig.Plugins.Add( 'focusfix', null, frameworkPluginPath) ;
FCKConfig.Plugins.Add( 'spell', null, respondPluginPath) ;

FCKConfig.LinkBrowser = false;
FCKConfig.ImageBrowser = false;
FCKConfig.FlashBrowser = false;
FCKConfig.LinkUpload = false;
FCKConfig.ImageUpload = false;
FCKConfig.FlashUpload = false;