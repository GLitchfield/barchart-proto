<!-- -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:output method="text" encoding="UTF-8" />

	<xsl:param name="java_package" />
	<xsl:param name="java_outer_classname" />

	<xsl:template match="/">
	
		package barchart; // non-java package name space

		option java_package = "<xsl:value-of select="$java_package"/>";
		option java_outer_classname = "<xsl:value-of select="$java_outer_classname"/>";
		option java_multiple_files = true;
		option java_generate_equals_and_hash = true;
		
		option optimize_for = SPEED;

		// note: enum entries are global on proto file level
		enum Type { // FIX MESSAGE TYPE
	    <xsl:for-each select="fix/messages/message">
			Type<xsl:value-of select="@name" /> = <xsl:value-of select="100 + position()" /> ; // type="<xsl:value-of select="@msgtype" />"
	    </xsl:for-each>		 
		}

		// base dir for import is a location of current proto file  
		import "google/descriptor.proto";

		extend google.protobuf.MessageOptions {
		  optional string TypeCode = 50001;
		  optional int32 TypeIndex = 50002;
		}

		message Header{ // FIX HEADER
	    <xsl:for-each select="fix/header">
			<xsl:apply-templates select="field|group|component" />
	    </xsl:for-each>		 
		}

		message Trailer{ // FIX TRAILER
	    <xsl:for-each select="fix/trailer">
			<xsl:apply-templates select="field|group|component" />
	    </xsl:for-each>		 
		}

		message Base { // FIX MESSAGE BASE
			optional Type type = 1; // TYPE
			optional Header header = 2; // HEADER
			optional Trailer trailer = 3; // TRAILER 
			extensions 100 to max; // BODY POLYMORPH
		}

		message Decimal {
			optional sint64 mantissa = 1;
			optional sint32 exponent = 2;
		}

		<xsl:apply-templates select="fix/components/component" />

		<xsl:apply-templates select="fix/messages/message" />

	</xsl:template>

	<xsl:template match="fix/components/component">
		message <xsl:value-of select="@name" /> { // COMPONENT
		<xsl:apply-templates select="field|group|component" />
		}
	</xsl:template>

	<xsl:template match="fix/messages/message">
		message <xsl:value-of select="@name" /> { // MESSAGE
		option(TypeCode) = "<xsl:value-of select="@msgtype" />" ;
		option(TypeIndex) = <xsl:value-of select="100 + position()" /> ;
		<xsl:apply-templates select="field|group|component" />
		}
		extend Base {
		optional <xsl:value-of select="@name" /> message<xsl:value-of select="@name" /> = <xsl:value-of select="100 + position()" /> ; // MESSAGE TYPE INDEX
		}
	</xsl:template>

	<xsl:template match="field">
		optional <xsl:call-template name="field-type" /><xsl:text> </xsl:text>
		<xsl:value-of select="@name" /> = <xsl:call-template name="field-number" /> ; // <xsl:call-template name="field-comment" />
	</xsl:template>

	<xsl:template match="group">
		message Group<xsl:value-of select="@name" /> { // GROUP
		<xsl:apply-templates select="field|group|component" />
		}
		repeated Group<xsl:value-of select="@name" /><xsl:text> </xsl:text>
		<xsl:value-of select="@name" /> = <xsl:call-template name="field-number" /> ; // GROUP
	</xsl:template>

	<xsl:template match="component">
		optional <xsl:value-of select="@name" /><xsl:text> </xsl:text>
		<xsl:value-of select="@name" /> = <xsl:value-of select="10000 + position()" /> ; // COMPONENT
	</xsl:template>

	<xsl:template name="field-number">
		<xsl:variable name="name" select="@name" />
		<xsl:value-of select="/fix/fields/field[@name=$name]/@number" />
	</xsl:template>

	<xsl:template name="field-comment">
		<xsl:variable name="name" select="@name" />
		<xsl:value-of select="/fix/fields/field[@name=$name]/@type" />
	</xsl:template>
	
	<xsl:template name="field-type">
		<xsl:variable name="name" select="@name" />
		<xsl:variable name="type" select="/fix/fields/field[@name=$name]/@type" />
		<xsl:choose>
        <xsl:when test="$type = 'BOOLEAN'">
			<xsl:text>bool</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'CHAR'">
			<xsl:text>sint32</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'INT'">
			<xsl:text>sint32</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'QTY'">
			<xsl:text>sint64</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'NUMINGROUP'">
			<xsl:text>int32</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'SEQNUM'">
			<xsl:text>int32</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'LENGTH'">
			<xsl:text>int32</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'PRICE'">
			<xsl:text>Decimal</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'AMT'">
			<xsl:text>Decimal</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'PRICEOFFSET'">
			<xsl:text>Decimal</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'FLOAT'">
			<xsl:text>Decimal</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'PERCENTAGE'">
			<xsl:text>Decimal</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'UTCTIMESTAMP'">
			<xsl:text>int64</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'UTCDATEONLY'">
			<xsl:text>int64</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'UTCTIMEONLY'">
			<xsl:text>int64</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'LOCALMKTDATE'">
			<xsl:text>int64</xsl:text>
        </xsl:when>
        <xsl:when test="$type = 'MONTHYEAR'">
			<xsl:text>int64</xsl:text>
        </xsl:when>
        <xsl:otherwise>
			<xsl:text>string</xsl:text>
        </xsl:otherwise>
        </xsl:choose>
	</xsl:template>

</xsl:stylesheet>
