<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
<xsl:output method='text' encoding='iso-8859-1'/>
<xsl:strip-space elements="*" />

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
        <xsl:with-param name="sub" select="'&gt;'"/><xsl:with-param name="rep" select="'$&gt;$'"/>
        <xsl:with-param name="string">
			<xsl:call-template name="str-replace">
	        <xsl:with-param name="sub" select="'{'"/><xsl:with-param name="rep" select="'\{'"/>
	        <xsl:with-param name="string">
				<xsl:call-template name="str-replace">
		        <xsl:with-param name="sub" select="'}'"/><xsl:with-param name="rep" select="'\}'"/>
		        <xsl:with-param name="string">
					<xsl:call-template name="str-replace">
			        <xsl:with-param name="sub" select="'&lt;'"/><xsl:with-param name="rep" select="'$&lt;$'"/>
			        <xsl:with-param name="string">
				        <xsl:call-template name="str-replace">
				        <xsl:with-param name="sub" select="'^'"/><xsl:with-param name="rep" select="'\^'"/>
				        <xsl:with-param name="string">
							<xsl:call-template name="str-replace">
					        <xsl:with-param name="sub" select="'$'"/><xsl:with-param name="rep" select="'\$'"/>
			        		<xsl:with-param name="string">
								<xsl:call-template name="str-replace">
						        <xsl:with-param name="sub" select="'_'"/><xsl:with-param name="rep" select="'\_'"/>
				        		<xsl:with-param name="string">
									<xsl:value-of select="$source"/>
						        </xsl:with-param></xsl:call-template>
					        </xsl:with-param></xsl:call-template>
				        </xsl:with-param></xsl:call-template>					        
			        </xsl:with-param></xsl:call-template>
		        </xsl:with-param></xsl:call-template>
	        </xsl:with-param></xsl:call-template>
        </xsl:with-param></xsl:call-template>
		
		
		

</xsl:template>
<xsl:template match="functiongroups">
\documentclass[a4paper,12pt]{scrartcl}
\usepackage[latin1]{inputenc} % encoding
\begin{document}
\tableofcontents
\newpage
\section{Function groups}
<xsl:for-each select="functiongroup">
\subsection{<xsl:call-template name="clean-text"><xsl:with-param name="source" select="@name"/></xsl:call-template>}
<xsl:for-each select="function">
\subsubsection{<xsl:call-template name="clean-text"><xsl:with-param name="source" select="@name"/></xsl:call-template>}
\paragraph{Description}
<xsl:call-template name="clean-text"><xsl:with-param name="source" select="@description"/></xsl:call-template>\\
<xsl:if test="@fixedParameterCount = 'true'">This function \textbf{has} fixed parameter count.</xsl:if><xsl:if test="@fixedParameterCount = 'false'">This function \textbf{hasn't} fixed parameter count.</xsl:if>\\
Return type is \textbf{$&lt;$<xsl:call-template name="clean-text"><xsl:with-param name="source" select="@returnType"/></xsl:call-template>$&gt;$}
\paragraph{Parameter}
<xsl:if test="count(parameter) = 0">\textit{none}</xsl:if>
<xsl:if test="count(parameter) &gt; 0">
\begin{description}
<xsl:for-each select="parameter">
\item[<xsl:call-template name="clean-text"><xsl:with-param name="source" select="@type"/></xsl:call-template>
<xsl:if test="@fixedParameterCount = 'true'">(optional)</xsl:if>\space\textit{<xsl:call-template name="clean-text"><xsl:with-param name="source" select="@name"/></xsl:call-template>}] : <xsl:call-template name="clean-text"><xsl:with-param name="source" select="@description"/></xsl:call-template>
</xsl:for-each>
\end{description}
</xsl:if>
<xsl:if test="count(example) &gt; 0">
\paragraph{Example}
\begin{verbatim}
<xsl:value-of select="example"/>
\end{verbatim}
</xsl:if>
</xsl:for-each>
</xsl:for-each>
\end{document}
</xsl:template>
</xsl:stylesheet>