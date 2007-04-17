<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
<xsl:template match="functiongroups">
!List of functions
<xsl:for-each select="functiongroup">
!!<xsl:value-of select="@name"/>
<xsl:for-each select="function">
!!!<xsl:value-of select="@name"/>
!!!!Description
<xsl:value-of select="@description"/>
<xsl:if test="@fixedParameterCount = 'true'">
This function [+has+] fixed parameter count.
</xsl:if>
<xsl:if test="@fixedParameterCount = 'false'">
This function [+hasn't+] fixed parameter count.
</xsl:if>Return type is [+<xsl:value-of select="@returnType"/>+]
!!!!Parameter<xsl:for-each select="parameter">
[+<xsl:value-of select="@type"/>+] <xsl:if test="@fixedParameterCount = 'true'">(optional)</xsl:if> : <xsl:value-of select="@description"/>
</xsl:for-each>
</xsl:for-each>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>