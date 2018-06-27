<?xml version="1.0" encoding="UTF-8"?>
<!-- XSL to Create XML to be used for CARS template XSL transformation -->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output method="xml" indent="no" />
	<xsl:param name="APP_DOMAIN"/>
	<xsl:param name="APP_PRODUCT_NAME"/>
	<xsl:param name="CARS_MIME_RESP"/>
	<xsl:param name="PRODUCT_SPECIFIC_LINKS"/>
	<xsl:param name="TEXT_ZONE"/>
	
	<xsl:param name="SETTINGS_LINKS_TOP"/>
	<xsl:param name="SETTINGS_LINKS_BOTTOM"/>
	<xsl:param name="LOGIN_TOP_LINKS"/>
	<xsl:param name="LOGOUT_TOP_LINKS"/>
	<xsl:param name="LOGIN_FULL_REG_TZ"/>
	<xsl:param name="REGISTRATION_CONFIRMATION_EMAIL_MESSAGE_LIST"/>
	<xsl:param name="REGISTRATION_CONFIRMATION_EMAIL_TXT_COLOR"/>
	<xsl:param name="REGISTRATION_CONFIRMATION_EMAIL_BAR_COLOR"/>
	<xsl:param name="PRIVACY_POLICY_URL"/>
	<xsl:param name="USER_AGREEMENT_URL"/>
	<xsl:param name="APP_DEFAULT_IMAGE"/>
	<xsl:param name="APP_HIDE_IMAGE"/>
	<xsl:param name="APP_SHOW_IMAGE"/>
	<xsl:param name="APP_URL_EXTN"/>
	<xsl:param name="REG_ID_ASSOC_CANCEL"/>
	<xsl:param name="FGT_PWD_EMAIL_PROD_URI"/>
	<xsl:param name="LOGIN_FULL_CANCEL_URI"/>
	<xsl:param name="APP_PREV_URI"/>
	<xsl:param name="APP_HOME_URI"/>
	<xsl:param name="PATH_CHOICE_NOTE_TEXT"/>
    <xsl:param name="CARS_NOBODY_LOGIN_FULL_TZ"/>
	
    <xsl:param name="CARS_EMAIL_SETUP_TOP_TZ"/>
    <xsl:param name="CARS_EMAIL_SETUP_BOT_TZ"/>
    <xsl:param name="CARS_REG_ID_ASSOCIATION_REGISTER_TZ"/>

	<xsl:param name="APP_LEARN_MORE_URL"/>

	<xsl:template match="/">
	<root>
		<apps:apps xmlns:apps="http://apps-services.elsevier.com/apps/server">
			<apps:params>
				<xsl:if test="$APP_PREV_URI != ''">
					<apps:app_previous_uri>
						<xsl:value-of select="$APP_PREV_URI"/>
					</apps:app_previous_uri>
				</xsl:if>
				<xsl:if test="$APP_HOME_URI != ''">
					<apps:app_user_home_uri>
						<xsl:value-of select="$APP_HOME_URI"/>
					</apps:app_user_home_uri>
				</xsl:if>
				<xsl:if test="$APP_DOMAIN != ''">
					<apps:app_domain>
						<xsl:value-of select="$APP_DOMAIN"/>
					</apps:app_domain>
				</xsl:if>
				<xsl:if test="$APP_URL_EXTN != ''">
					<apps:app_controller_ext>
						<xsl:value-of select="$APP_URL_EXTN"/>
					</apps:app_controller_ext>
				</xsl:if>
				<xsl:if test="$APP_PRODUCT_NAME != ''">
					<apps:app_product_name>
						<xsl:value-of select="$APP_PRODUCT_NAME"/>
					</apps:app_product_name>
				</xsl:if>
				<xsl:if test="$LOGIN_TOP_LINKS != ''">
					<apps:login_links>
						<xsl:value-of select="$LOGIN_TOP_LINKS"/>
					</apps:login_links>
				</xsl:if>
				<xsl:if test="$LOGOUT_TOP_LINKS != ''">
					<apps:logout_links>
						<xsl:value-of select="$LOGOUT_TOP_LINKS"/>
					</apps:logout_links>
				</xsl:if>
				<xsl:if test="$LOGIN_FULL_REG_TZ != ''">
					<apps:cars_login_full_register_tz>
						<xsl:value-of select="$LOGIN_FULL_REG_TZ"/>
					</apps:cars_login_full_register_tz>
				</xsl:if>
				<xsl:if test="$REGISTRATION_CONFIRMATION_EMAIL_MESSAGE_LIST != ''">
					<apps:message>
						<xsl:value-of select="$REGISTRATION_CONFIRMATION_EMAIL_MESSAGE_LIST"/>
					</apps:message>
				</xsl:if>
				<xsl:if test="$PATH_CHOICE_NOTE_TEXT != ''">
					<apps:message_list>
						<apps:message>
							<xsl:value-of select="$PATH_CHOICE_NOTE_TEXT"/>
						</apps:message>
					</apps:message_list>
				</xsl:if>
				<xsl:if test="$REGISTRATION_CONFIRMATION_EMAIL_TXT_COLOR != ''">
					<apps:confirmation_text_color>
						<xsl:value-of select="$REGISTRATION_CONFIRMATION_EMAIL_TXT_COLOR"/>
					</apps:confirmation_text_color>
				</xsl:if>
				<xsl:if test="$REGISTRATION_CONFIRMATION_EMAIL_BAR_COLOR != ''">
					<apps:confirmation_bar_color>
						<xsl:value-of select="$REGISTRATION_CONFIRMATION_EMAIL_BAR_COLOR"/>
					</apps:confirmation_bar_color>
				</xsl:if>
				<xsl:if test="$REG_ID_ASSOC_CANCEL != ''">
					<apps:app_cancel_link>
						<xsl:value-of select="$REG_ID_ASSOC_CANCEL"/>
					</apps:app_cancel_link>
				</xsl:if>
				<xsl:if test="$PRIVACY_POLICY_URL != ''">
					<apps:reg_privacy_policy_uri>
						<xsl:value-of select="$PRIVACY_POLICY_URL"/>
					</apps:reg_privacy_policy_uri>
				</xsl:if>
				<xsl:if test="$USER_AGREEMENT_URL != ''">
					<apps:reg_user_agrmt_uri>
						<xsl:value-of select="$USER_AGREEMENT_URL"/>
					</apps:reg_user_agrmt_uri>
				</xsl:if>
				<xsl:if test="$APP_DEFAULT_IMAGE != ''">
					<apps:app_default_image>
						<xsl:value-of select="$APP_DEFAULT_IMAGE"/>
					</apps:app_default_image>
				</xsl:if>
				<xsl:if test="$APP_HIDE_IMAGE != ''">
					<apps:app_hide_image>
						<xsl:value-of select="$APP_HIDE_IMAGE"/>
					</apps:app_hide_image>
					<apps:login_expand_img>
						<xsl:value-of select="$APP_HIDE_IMAGE"/>
					</apps:login_expand_img>
				</xsl:if>
				<xsl:if test="$APP_SHOW_IMAGE != ''">
					<apps:login_collapse_img>
						<xsl:value-of select="$APP_SHOW_IMAGE"/>
					</apps:login_collapse_img>
				</xsl:if>
				<xsl:if test="$FGT_PWD_EMAIL_PROD_URI != ''">
					<apps:fgt_pwd_email_prod_uri>
						<xsl:value-of select="$FGT_PWD_EMAIL_PROD_URI"/>
					</apps:fgt_pwd_email_prod_uri>
				</xsl:if>
				<xsl:if test="$PRODUCT_SPECIFIC_LINKS != ''">
					<apps:product_specific_links>
						<xsl:value-of select="$PRODUCT_SPECIFIC_LINKS"/>
					</apps:product_specific_links>
				</xsl:if>
				<xsl:if test="$SETTINGS_LINKS_TOP != ''">
					<apps:product_specific_links_top>
						<xsl:value-of select="$SETTINGS_LINKS_TOP"/>
					</apps:product_specific_links_top>
				</xsl:if>
				<xsl:if test="$SETTINGS_LINKS_BOTTOM != ''">
					<apps:product_specific_links_bottom>
						<xsl:value-of select="$SETTINGS_LINKS_BOTTOM"/>
					</apps:product_specific_links_bottom>
				</xsl:if>
				<xsl:if test="$TEXT_ZONE != ''">
					<xsl:value-of select="$TEXT_ZONE" disable-output-escaping="yes"/>
				</xsl:if>
				<xsl:if test="$LOGIN_FULL_CANCEL_URI != ''">
					<apps:login_full_cancel_uri>
						<xsl:value-of select="$LOGIN_FULL_CANCEL_URI"/>
					</apps:login_full_cancel_uri>
				</xsl:if>
                <xsl:if test="$CARS_EMAIL_SETUP_TOP_TZ != ''">
                    <apps:cars_email_setup_top_tz>
                        <xsl:value-of select="$CARS_EMAIL_SETUP_TOP_TZ"/>
                    </apps:cars_email_setup_top_tz>
                </xsl:if>
                <xsl:if test="$CARS_EMAIL_SETUP_BOT_TZ != ''">
                    <apps:cars_email_setup_bot_tz>
                        <xsl:value-of select="$CARS_EMAIL_SETUP_BOT_TZ"/>
                    </apps:cars_email_setup_bot_tz>
                </xsl:if>
                <xsl:if test="$CARS_REG_ID_ASSOCIATION_REGISTER_TZ != ''">
                    <apps:cars_reg_id_association_register_tz>
                        <xsl:value-of select="$CARS_REG_ID_ASSOCIATION_REGISTER_TZ"/>
                    </apps:cars_reg_id_association_register_tz>
                </xsl:if>  
                <xsl:if test="$APP_LEARN_MORE_URL != ''">
                    <apps:app_learn_more_url>
                        <xsl:value-of select="$APP_LEARN_MORE_URL"/>
                    </apps:app_learn_more_url>
                </xsl:if>  
                <xsl:if test="$CARS_NOBODY_LOGIN_FULL_TZ != ''">
                    <apps:cars_nobody_login_full_tz>
                        <xsl:value-of select="$CARS_NOBODY_LOGIN_FULL_TZ"/>
                    </apps:cars_nobody_login_full_tz>
                </xsl:if>  
			</apps:params>
		</apps:apps>
		<!-- Include cars response xml here -->
		<xsl:if test="$CARS_MIME_RESP != ''">
			<xsl:value-of select="$CARS_MIME_RESP"/>
		</xsl:if>
	</root>
	</xsl:template>
</xsl:stylesheet>