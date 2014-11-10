package org.ei.ane.entitlements;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.ane.entitlements.UserEntitlement.ENTITLEMENT_TYPE;
import org.ei.bulletins.BulletinConfig;
import org.ei.domain.DatabaseConfig;

import com.elsevier.webservices.schemas.csas.constants.types.v7.EntitlementsStatusCodeType;
import com.elsevier.webservices.schemas.csas.types.v13.ContentEntitlementsMethodLevelRespType;
import com.elsevier.webservices.schemas.csas.types.v13.EntitlementsResponseStatusType;
import com.elsevier.webservices.schemas.csas.types.v13.GetContentEntitlementsRespPayloadType;

public final class UserEntitlementsBuilder {
    private final static Logger log4j = Logger.getLogger(UserEntitlementsBuilder.class);

    private UserEntitlementsBuilder() {
    }

    /**
     * Build a unique set of entitlements from a CS payload
     * 
     * @param payload
     * @return
     */
    public static Set<UserEntitlement> fromEntitlementsPayload(GetContentEntitlementsRespPayloadType payload) {
        
        // Ensure payload is not null
        if (payload == null) {
            log4j.warn("No payload!");
            return Collections.EMPTY_SET; 
        } 
        
        // Ensure payload satus is OK
        EntitlementsResponseStatusType status = payload.getStatus();
        if (!EntitlementsStatusCodeType.OK.equals(status.getStatusCode())) {
            log4j.warn("Payload status is '" + status.getStatusCode() + ".  Message: '" + status.getStatusDetail().get(0).toString() + "'");
            return Collections.EMPTY_SET; 
        } 
        
        // Ensure something was returned!
        List<ContentEntitlementsMethodLevelRespType> entitlementsMethods = payload.getEntitlementsMethod();
        if (entitlementsMethods.isEmpty()) {
            log4j.warn("No entitlements in payload!");
            return Collections.EMPTY_SET; 
        }
        
        // Itereate through entitlements to convert to UserEntitlement objects
        Set<UserEntitlement> entitlements = new HashSet<UserEntitlement>();
        for (ContentEntitlementsMethodLevelRespType entitlementsMethod : entitlementsMethods) {
            if (!entitlementsMethod.getItemLevelValues().isEmpty()) {
                // Set the entitlement type
                ENTITLEMENT_TYPE entitlementType = ENTITLEMENT_TYPE.NONE;
                if (!GenericValidator.isBlankOrNull(entitlementsMethod.getMethodLevelValues())) { 
                    if (entitlementsMethod.getMethodLevelValues().contains(EntitlementsServiceImpl.CONTENT_TYPE_DATABASE)) {
                        entitlementType = ENTITLEMENT_TYPE.DATABASE;
                    } else if (entitlementsMethod.getMethodLevelValues().contains(EntitlementsServiceImpl.CONTENT_TYPE_BULLETIN)) {
                        entitlementType = ENTITLEMENT_TYPE.BULLETIN;
                    }
                }

                // Set the entitlement name
                for (String itemLevelValue : entitlementsMethod.getItemLevelValues()) {
                    // Should come in the form 'rval=[dbname]~promoted=[Y|N]"
                    String[] items = itemLevelValue.split("~|=");
                    if (items.length >= 2) {
                        if ("rval".equals(items[0].trim())) {
                            UserEntitlement ent = null;
                            if (entitlementType == ENTITLEMENT_TYPE.DATABASE) {
                                ent = findDatabaseFromName(items[1].trim().toUpperCase());
                            } else if (entitlementType == ENTITLEMENT_TYPE.BULLETIN) {
                                ent = findBulletinFromName(items[1].trim().toUpperCase());
                            } else {
                                ent = new UserEntitlement(entitlementType, items[1].trim().toUpperCase());
                            }
                            
                            if (ent != null) {
                                entitlements.add(ent);
                            }
                        }
                    } else {
                        log4j.warn("Item level value may not be correctly formatted: '" + itemLevelValue + "'");
                    }
                }
            } else {
                log4j.warn("Empty ItemLevelValue array!");
            }
        }        
        
        return entitlements;

    }
    
    /**
     * Get a static database UserEntitlement from the name
     * @param name
     */
    private static UserEntitlement findDatabaseFromName(String name) {
        if (name.toUpperCase().equals(DatabaseConfig.CPX_PREF.toUpperCase())) {
            return UserEntitlement.CPX_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.C84_PREF.toUpperCase())) {
            return UserEntitlement.C84_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.CBF_PREF.toUpperCase())) {
            return UserEntitlement.CBF_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.INS_PREF.toUpperCase())) {
            return UserEntitlement.INS_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.IBF_PREF.toUpperCase())) {
            return UserEntitlement.IBF_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.IBS_PREF.toUpperCase())) {
            return UserEntitlement.IBS_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.NTI_PREF.toUpperCase())) {
            return UserEntitlement.NTI_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.PCH_PREF.toUpperCase())) {
            return UserEntitlement.PCH_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.CHM_PREF.toUpperCase())) {
            return UserEntitlement.CHM_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.CBN_PREF.toUpperCase())) {
            return UserEntitlement.CBN_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.ELT_PREF.toUpperCase())) {
            return UserEntitlement.ELT_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.EPT_PREF.toUpperCase())) {
            return UserEntitlement.EPT_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.GEO_PREF.toUpperCase())) {
            return UserEntitlement.GEO_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.GRF_PREF.toUpperCase())) {
            return UserEntitlement.GRF_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.UPA_PREF.toUpperCase())) {
            return UserEntitlement.UPA_ENTITLEMENT;
        } else if (name.toUpperCase().equals(DatabaseConfig.EUP_PREF.toUpperCase())) {
            return UserEntitlement.EUP_ENTITLEMENT;
        } else {
            log4j.warn("No static entitlement found for '" + name + "'");
        }
        
        return null;
    }

    /**
     * Get a static bulletin UserEntitlement from the name
     * @param name
     */
    private static UserEntitlement findBulletinFromName(String name) {
        if (name.toUpperCase().equals(BulletinConfig.LIT_AUTO.toUpperCase())) {
            return UserEntitlement.LIT_AUTO_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.LIT_CATZEO.toUpperCase())) {
            return UserEntitlement.LIT_CATZEO_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.LIT_FUELREF.toUpperCase())) {
            return UserEntitlement.LIT_FUELREF_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.LIT_HEALTHENV.toUpperCase())) {
            return UserEntitlement.LIT_HEALTHENV_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.LIT_NATGAS.toUpperCase())) {
            return UserEntitlement.LIT_NATGAS_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.LIT_OILFIELD.toUpperCase())) {
            return UserEntitlement.LIT_OILFIELD_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.LIT_PETROREF.toUpperCase())) {
            return UserEntitlement.LIT_PETROREF_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.LIT_PETROSUB.toUpperCase())) {
            return UserEntitlement.LIT_PETROSUB_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.LIT_TRANSSTOR.toUpperCase())) {
            return UserEntitlement.LIT_TRANSSTOR_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.LIT_TRIB.toUpperCase())) {
            return UserEntitlement.LIT_TRIB_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_CATZEO.toUpperCase())) {
            return UserEntitlement.PAT_CATZEO_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_CHEM.toUpperCase())) {
            return UserEntitlement.PAT_CHEM_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_ENVTRANSSTOR.toUpperCase())) {
            return UserEntitlement.PAT_ENVTRANSSTOR_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_FUELREF.toUpperCase())) {
            return UserEntitlement.PAT_FUELREF_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_OILFIELD.toUpperCase())) {
            return UserEntitlement.PAT_OILFIELD_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_POLYMERS.toUpperCase())) {
            return UserEntitlement.PAT_POLYMERS_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_PETROPROC.toUpperCase())) {
            return UserEntitlement.PAT_PETROPROC_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_PETROSPEC.toUpperCase())) {
            return UserEntitlement.PAT_PETROSPEC_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_PETROSUB.toUpperCase())) {
            return UserEntitlement.PAT_PETROSUB_ENTITLEMENT;
        } else if (name.toUpperCase().equals(BulletinConfig.PAT_TRIB.toUpperCase())) {
            return UserEntitlement.PAT_TRIB_ENTITLEMENT;
        } else {
            log4j.warn("No static entitlement found for '" + name + "'");
        }
        
        return null;
    }
}
