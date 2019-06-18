<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>
  
  <xsl:param name="panelId"/>
  
  <xsl:template match="/">
    <xsl:apply-templates select="framework-template/panel-definition" />
  </xsl:template>
  
  <xsl:template match="panel-definition">
     <!-- Search for panel-definition which id is equal to passed panelId -->
     <xsl:if test="normalize-space(panel-id/text()) = $panelId">
		<xsl:apply-templates mode="escape"/>
	 </xsl:if>
  </xsl:template>
  
  <!-- Have to escape xml manually, usage of c:out instead cause errors
       Not only escapes xml, but also makes reasonable indentation
  -->
  <xsl:template match="*" mode="escape">
    <xsl:param name="indent" select="0"/>
	
	<!-- Indentation before every nested block 
	     We can't use xsl:for-each select="0 to $indent",
		 since it xsl 1.0 and we have to use template call
	-->
	<xsl:call-template name="indentationLoop">
      <xsl:with-param name="count" select="$indent"/>
    </xsl:call-template>
	
    <!-- Begin opening tag -->
    <xsl:text>&lt;</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text>&gt;</xsl:text>
	<br />
	
    <!-- Content (child elements, text nodes, and PIs) -->
    <xsl:apply-templates select="node()" mode="escape" >
	  <xsl:with-param name="indent" select="$indent + 1"/>
	</xsl:apply-templates>
	<br />
	
	<xsl:call-template name="indentationLoop">
      <xsl:with-param name="count" select="$indent"/>
    </xsl:call-template>

    <!-- Closing tag -->
    <xsl:text>&lt;/</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text>&gt;</xsl:text>
	<br />
  </xsl:template>
  
  <xsl:template match="text()" mode="escape">
    <xsl:param name="indent" />
	<xsl:call-template name="indentationLoop">
      <xsl:with-param name="count" select="$indent"/>
    </xsl:call-template>
	
	<span class="definitionFileElementContent">
	  <xsl:value-of select="." />	
	</span>
  </xsl:template>
  
  <xsl:template name="indentationLoop">
    <xsl:param name="count"/>
    <xsl:param name="iteration" select="0" />
	
	<!-- &#160; is &nbsp; in xsl -->
	<xsl:text>&#160;&#160;</xsl:text>

    <xsl:if test="$iteration &lt; $count">
      <xsl:call-template name="indentationLoop">
        <xsl:with-param name="count" select="$count"/>
        <xsl:with-param name="iteration" select="$iteration + 1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
<!-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/xsl/panelDefinitionExtractor.xsl#1 $$Change: 946917 $-->
