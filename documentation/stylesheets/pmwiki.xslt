<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
<xsl:output method='text' encoding='utf-8'/>

<xsl:template name="str-replace">
  <xsl:param name="string"/>
  <xsl:param name="sub"/>
  <xsl:param name="rep"/>
  <xsl:choose>
    <xsl:when test="contains($string,$sub)">
      <xsl:value-of select="concat(substring-before($string,$sub),$rep)"/>
      <xsl:call-template name="str-replace">
        <xsl:with-param name="string" select="substring-after($string,$sub)"/>
        <xsl:with-param name="sub" select="$sub"/>
        <xsl:with-param name="rep" select="$rep"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$string"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="clean-text">
		<xsl:param name="source" />
		<xsl:call-template name="str-replace">
	        <xsl:with-param name="sub" select="'&#10;'"/>
	        <xsl:with-param name="rep" select="'\\'"/>
       		<xsl:with-param name="string">
			<xsl:value-of select="$source"/>
		    </xsl:with-param>
		</xsl:call-template>
</xsl:template>



<xsl:template match="functiongroups">
!List of functions
<xsl:for-each select="functiongroup">
!!<xsl:value-of select="@name"/>
<xsl:for-each select="function">
!!!<xsl:value-of select="@name"/>
!!!!Description
<xsl:value-of select="@description"/>\\
<xsl:if test="@fixedParameterCount = 'true'">
This function [+has+] fixed parameter count.\\
</xsl:if>
<xsl:if test="@fixedParameterCount = 'false'">
This function [+hasn't+] fixed parameter count.\\
</xsl:if>Return type is [+&lt;<xsl:value-of select="@returnType"/>&gt;+]
!!!!Parameter
<xsl:if test="count(parameter) = 0">"none"</xsl:if><xsl:for-each select="parameter">
[+&lt;<xsl:value-of select="@type"/>&gt;+] <xsl:if test="@fixedParameterCount = 'true'">(optional)</xsl:if> <xsl:value-of select="@name"/> : <xsl:value-of select="@description"/>\\
</xsl:for-each>
<xsl:if test="count(example) &gt; 0">
!!!!Example
<xsl:call-template name="clean-text"><xsl:with-param name="source" select="example"/></xsl:call-template>\\
</xsl:if>
----
</xsl:for-each>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>