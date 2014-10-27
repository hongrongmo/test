<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY reg "&#174;">
]>
<!-- XSL to hold the generic confirmation email template. 
      The header and footer from configuration file if needed and body is from 
      Customer System (cars response) 
      
      For all email templates the header and footer information may not come from 
      CS. Like for Registration confirmation mail the entire header, body, 
      and footer comes from CS. but for forgotten password email only body 
      contents is coming from CS and header and footer is taken from Web -->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <xsl:param name="CARS_MAIL_FOOTER"/>
      <xsl:param name="MAIL_BODY"/>
      
      <xsl:template match="/">
            <html>
                  <xsl:if test="$MAIL_BODY != ''">
                        <xsl:value-of select="$MAIL_BODY"/>
                  </xsl:if>
                  <xsl:if test="$CARS_MAIL_FOOTER != ''">
                        <xsl:value-of select="$CARS_MAIL_FOOTER"/>
                  </xsl:if>
            </html>
      </xsl:template>
</xsl:stylesheet>
