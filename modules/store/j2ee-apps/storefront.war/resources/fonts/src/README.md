
# Icon Font Instructions

This directory contains the source svg files for the icon font.

To create a new icon for the set, use Illustrator to create a vector version of the icon. The icon artboard size is 48 x 48. Most icons are 36 pixels high. Once you have exported your new svg, you will add it to the icon set in IcoMoon.

IcoMoon:

<https://icomoon.io/app/>

In the IconMoon app, you can import *selection.json* using the **Import Icons** button (or via Main Menu > Manage Projects) to retrieve your icon selection.

After importing the existing font set, import your new svg into the icon set. You can do this by selecting the menu button in the title bar for the font set. Select **Import to Set** and select your new svg.

Once it is in, highlight all the fonts in the set and click the **Generate Font** button at the bottom of the screen.  This will take you to a summary screen.

NOTE: Make sure IcoMoon has not modified the characters assigned to the existing icons.

NOTE: Illustrator automatically puts the file name at the beginning of the exported icons (ex: icon-arrow.down.svg). Be sure you don't have that prefix in the font name on this screen.

Click download at the bottom right of the screen to get the fonts and styles:

- Rename the font to biglots-icons
- Copy the new font files font into the fonts directory
- Save the Illustrator source file into the font/src directory.
- Save the selection.json file into the font/src
- Copy your new icon styles into the icon partial.

## Reference
Working with artboards in Illustrator:

<https://helpx.adobe.com/illustrator/using/using-multiple-artboards.html>

Export multiple artboards into svg files:

<http://creativedroplets.com/export-svg-for-the-web-with-illustrator-cc/>
