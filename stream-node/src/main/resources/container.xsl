<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" />


<xsl:template match="/">

<html>
<head>
	<link rel="stylesheet" href="/css/style.css" />
</head>
<body>
  <div class="header"></div>
  <div class="content">
  <div class="container">
  	<div class="metainf">
	  	<h1>Container <xsl:value-of select="@id" /></h1>
  	</div>
	<xsl:apply-templates/>
  </div>	
  </div>
</body>
</html>
</xsl:template>

<xsl:template match="Stream|stream|DataStream">
	<div class="stream">
		<div class="header">
			<span class="name">Stream <xsl:value-of select="@id" /></span>
		</div>
		<div class="attributes">
			<xsl:for-each select="@*">
				<span class="attribute"><xsl:value-of select="name()"/></span> = <span class="attributeValue"><xsl:value-of select="."/></span>
				<xsl:if test="position() != last()">, </xsl:if>
			</xsl:for-each>
		</div>
		
		<div>
			<xsl:for-each select="*">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
		</div>
	</div>
</xsl:template>

<xsl:template match="Process|process">
	<div class="process">
		<div class="header">
			<span class="name">Process <xsl:value-of select="@id" /></span>, input: <xsl:value-of select="@input"/>
		</div>
		
		<div>
			<xsl:for-each select="*">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
		</div>
	</div>
	
</xsl:template>

<xsl:template match="container">
<div class="configSections">
	<xsl:apply-templates/>
</div>
</xsl:template>


<xsl:template match="Process//*|process//*">
	<div class="processor">
		<div class="name">
			<xsl:value-of select="name()"/>
		</div>
		<div class="attributes">
			<xsl:for-each select="@*">
				<span class="attribute"><xsl:value-of select="name()"/></span> = <span class="attributeValue"><xsl:value-of select="."/></span>
				<xsl:if test="position() != last()">, </xsl:if>
			</xsl:for-each>
		</div>
			<xsl:for-each select="*">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
	</div>
</xsl:template>

</xsl:stylesheet>