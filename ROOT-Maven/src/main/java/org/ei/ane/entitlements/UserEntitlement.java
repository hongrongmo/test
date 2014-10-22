package org.ei.ane.entitlements;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.validator.GenericValidator;
import org.ei.bulletins.BulletinConfig;
import org.ei.domain.DatabaseConfig;

public final class UserEntitlement implements Serializable{
    
    private static final long serialVersionUID = -6999611481728761218L;
	private ENTITLEMENT_TYPE entitlementType;
    private String name;
    private int mask;
    
    public static final String DATABASE_ENTITLEMENT_TYPE = "database";
    public static final String BULLETIN_ENTITLEMENT_TYPE = "bulletin";
    public static final String REFEREX_ENTITLEMENT_TYPE = "referex";

    public static final UserEntitlement CPX_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.CPX_PREF);
    public static final UserEntitlement C84_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.C84_PREF);
    public static final UserEntitlement CBF_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.CBF_PREF);
    public static final UserEntitlement CBN_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.CBN_PREF);
    public static final UserEntitlement CHM_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.CHM_PREF);
    public static final UserEntitlement ELT_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.ELT_PREF);
    public static final UserEntitlement EPT_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.EPT_PREF);
    public static final UserEntitlement GEO_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.GEO_PREF);
    public static final UserEntitlement GRF_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.GRF_PREF);
    public static final UserEntitlement INS_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.INS_PREF);
    public static final UserEntitlement IBF_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.IBF_PREF);
    public static final UserEntitlement IBS_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.IBS_PREF);
    public static final UserEntitlement NTI_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.NTI_PREF);
    public static final UserEntitlement PCH_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.PCH_PREF);
    public static final UserEntitlement UPT_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.UPT_PREF);
    public static final UserEntitlement UPA_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.UPA_PREF);
    public static final UserEntitlement EUP_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.EUP_PREF);
    public static final UserEntitlement PAG_ENTITLEMENT = new UserEntitlement(ENTITLEMENT_TYPE.DATABASE, DatabaseConfig.PAG_PREF);
    
    public static final UserEntitlement LIT_AUTO_ENTITLEMENT =          new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_AUTO);
    public static final UserEntitlement LIT_CATZEO_ENTITLEMENT =        new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_CATZEO);
    public static final UserEntitlement LIT_FUELREF_ENTITLEMENT =       new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_FUELREF);
    public static final UserEntitlement LIT_HEALTHENV_ENTITLEMENT =     new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_HEALTHENV);
    public static final UserEntitlement LIT_NATGAS_ENTITLEMENT =        new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_NATGAS);
    public static final UserEntitlement LIT_OILFIELD_ENTITLEMENT =      new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_OILFIELD);
    public static final UserEntitlement LIT_PETROREF_ENTITLEMENT =      new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_PETROREF);
    public static final UserEntitlement LIT_PETROSUB_ENTITLEMENT =      new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_PETROSUB);
    public static final UserEntitlement LIT_TRANSSTOR_ENTITLEMENT =     new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_TRANSSTOR);
    public static final UserEntitlement LIT_TRIB_ENTITLEMENT =          new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.LIT_TRIB);
    
    public static final UserEntitlement PAT_CATZEO_ENTITLEMENT =        new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_CATZEO);
    public static final UserEntitlement PAT_CHEM_ENTITLEMENT =          new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_CHEM);
    public static final UserEntitlement PAT_ENVTRANSSTOR_ENTITLEMENT =  new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_ENVTRANSSTOR);
    public static final UserEntitlement PAT_FUELREF_ENTITLEMENT =       new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_FUELREF);
    public static final UserEntitlement PAT_OILFIELD_ENTITLEMENT =      new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_OILFIELD);
    public static final UserEntitlement PAT_POLYMERS_ENTITLEMENT =      new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_POLYMERS);
    public static final UserEntitlement PAT_PETROPROC_ENTITLEMENT =     new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_PETROPROC);
    public static final UserEntitlement PAT_PETROSPEC_ENTITLEMENT =     new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_PETROSPEC);
    public static final UserEntitlement PAT_PETROSUB_ENTITLEMENT =      new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_PETROSUB);
    public static final UserEntitlement PAT_TRIB_ENTITLEMENT =          new UserEntitlement(ENTITLEMENT_TYPE.BULLETIN, BulletinConfig.PAT_TRIB);

    public enum ENTITLEMENT_TYPE {
        NONE(""),
        DATABASE(DATABASE_ENTITLEMENT_TYPE),
        BULLETIN(BULLETIN_ENTITLEMENT_TYPE),
        REFEREX(REFEREX_ENTITLEMENT_TYPE);
        
        private ENTITLEMENT_TYPE(String type) {
            
        }

        public static ENTITLEMENT_TYPE fromString(String value) {
            if (DATABASE_ENTITLEMENT_TYPE.equals(value)) {
                return ENTITLEMENT_TYPE.DATABASE;
            } else if (BULLETIN_ENTITLEMENT_TYPE.equals(value)) {
                return ENTITLEMENT_TYPE.BULLETIN;
            } else if (REFEREX_ENTITLEMENT_TYPE.equals(value)) {
                return ENTITLEMENT_TYPE.REFEREX;
            } else {
                return ENTITLEMENT_TYPE.NONE;
            }
        }
    }
    

    /**
     * Constructor with defaults
     * 
     * @param type
     * @param name
     */
    public UserEntitlement(String type, String name) {
        this.entitlementType = ENTITLEMENT_TYPE.fromString(type);
        this.name = name;
        this.mask = mask(name);
    }
    
    /**
     * Constructor with defaults
     * 
     * @param type
     * @param name
     */
    public UserEntitlement(ENTITLEMENT_TYPE type, String name) {
        this.entitlementType = type;
        this.name = name;
        this.mask = mask(name);
    }
    
    private int mask(String name) {
        if (!GenericValidator.isBlankOrNull(name)) {
            if (name.toUpperCase().equals(DatabaseConfig.CPX_PREF.toUpperCase())) {
                return DatabaseConfig.CPX_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.C84_PREF.toUpperCase())) {
                return DatabaseConfig.C84_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.CBF_PREF.toUpperCase())) {
                return DatabaseConfig.CBF_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.INS_PREF.toUpperCase())) {
                return DatabaseConfig.INS_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.IBF_PREF.toUpperCase())) {
                return DatabaseConfig.IBF_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.IBS_PREF.toUpperCase())) {
                return DatabaseConfig.IBS_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.NTI_PREF.toUpperCase())) {
                return DatabaseConfig.NTI_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.PCH_PREF.toUpperCase())) {
                return DatabaseConfig.PCH_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.CHM_PREF.toUpperCase())) {
                return DatabaseConfig.CHM_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.CBN_PREF.toUpperCase())) {
                return DatabaseConfig.CBN_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.ELT_PREF.toUpperCase())) {
                return DatabaseConfig.ELT_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.EPT_PREF.toUpperCase())) {
                return DatabaseConfig.EPT_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.GEO_PREF.toUpperCase())) {
                return DatabaseConfig.GEO_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.GRF_PREF.toUpperCase())) {
                return DatabaseConfig.GRF_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.UPA_PREF.toUpperCase())) {
                return DatabaseConfig.UPA_MASK;
            } else if (name.toUpperCase().equals(DatabaseConfig.EUP_PREF.toUpperCase())) {
                return DatabaseConfig.EUP_MASK;
            }
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return this.name + ":" + this.entitlementType.toString() + ":" + this.mask;
    }
    
    public ENTITLEMENT_TYPE getEntitlementType() {
        return this.entitlementType;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(name).append(entitlementType.toString()).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (!(obj instanceof UserEntitlement)) { return false; }
        
        UserEntitlement check = (UserEntitlement) obj;
        return new EqualsBuilder().append(name, check.name).append(entitlementType.toString(), check.entitlementType.toString()).isEquals();
    }
    
    public void setEntitlementType(ENTITLEMENT_TYPE entitlementType) {
        this.entitlementType = entitlementType;
    }
    
    public void setEntitlementType(String entitlementTypeStr) {
        this.entitlementType = ENTITLEMENT_TYPE.fromString(entitlementTypeStr);
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getMask() {
        return mask;
    }

}
