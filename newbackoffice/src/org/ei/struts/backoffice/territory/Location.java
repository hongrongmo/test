package org.ei.struts.backoffice.territory;


public abstract class Location extends Item implements Expression
{
    public static final Location CANADA           = Country.CANADA;
    public static final Location US               = Country.US;
    public static final Location BENELUX          = LocationGroup.BENELUX;

    public static final Location ALABAMA          = new USState("AL","('alabama','ala','al')");
    public static final Location ALASKA           = new USState("AK","('alaska','alaska','ak')");
    public static final Location ARIZONA          = new USState("AZ","('arizona','ariz','az')");
    public static final Location ARKANSAS         = new USState("AR","('arkansas','ark','ar')");
    public static final Location CALIFORNIA       = new USState("CA","('california','calif','ca')");
    public static final Location COLORADO         = new USState("CO","('colorado','colo','co')");
    public static final Location CONNECTICUT      = new USState("CT","('connecticut','conn','ct')");
    public static final Location DELAWARE         = new USState("DE","('delaware','del','de')");
    public static final Location DC               = new USState("DC","('dist of columbia','dc','dc')");
    public static final Location FLORIDA          = new USState("FL","('florida','fla','fl')");
    public static final Location GEORGIA          = new USState("GA","('georgia','ga','ga')");
    public static final Location HAWAII           = new USState("HI","('hawaii','hawaii','hi')");
    public static final Location IDAHO            = new USState("ID","('idaho','idaho','id')");
    public static final Location ILLINOIS         = new USState("IL","('illinois','ill','il')");
    public static final Location INDIANA          = new USState("IN","('indiana','ind','in')");
    public static final Location IOWA             = new USState("IA","('iowa','iowa','ia')");
    public static final Location KANSAS           = new USState("KS","('kansas','kans','ks')");
    public static final Location KENTUCKY         = new USState("KY","('kentucky','ky','ky')");
    public static final Location LOUISIANA        = new USState("LA","('louisiana','la','la')");
    public static final Location MAINE            = new USState("ME","('maine','maine','me')");
    public static final Location MARYLAND         = new USState("MD","('maryland','md','md')");
    public static final Location MASSACHUSETTS    = new USState("MA","('massachusetts','mass','ma')");
    public static final Location MICHIGAN         = new USState("MI","('michigan','mich','mi')");
    public static final Location MINNESOTA        = new USState("MN","('minnesota','minn','mn')");
    public static final Location MISSISSIPPI      = new USState("MS","('mississippi','miss','ms')");
    public static final Location MISSOURI         = new USState("MO","('missouri','mo','mo')");
    public static final Location MONTANA          = new USState("MT","('montana','mont','mt')");
    public static final Location NEBRASKA         = new USState("NE","('nebraska','nebr','ne')");
    public static final Location NEVADA           = new USState("NV","('nevada','nev','nv')");
    public static final Location NEW_HAMPSHIRE    = new USState("NH","('new hampshire','nh','nh')");
    public static final Location NEW_JERSEY       = new USState("NJ","('new jersey','nj','nj')");
    public static final Location NEW_MEXICO       = new USState("NM","('new mexico','nm','nm')");
    public static final Location NEW_YORK         = new USState("NY","('new york','ny','ny')");
    public static final Location NORTH_CAROLINA   = new USState("NC","('north carolina','nc','nc')");
    public static final Location NORTH_DAKOTA     = new USState("ND","('north dakota','nd','nd')");
    public static final Location OHIO             = new USState("OH","('ohio','ohio','oh')");
    public static final Location OKLAHOMA         = new USState("OK","('oklahoma','okla','ok')");
    public static final Location OREGON           = new USState("OR","('oregon','ore','or')");
    public static final Location PENNSYLVANIA     = new USState("PA","('pennsylvania','pa','pa')");
    public static final Location RHODE_ISLAND     = new USState("RI","('rhode island','ri','ri')");
    public static final Location SOUTH_CAROLINA   = new USState("SC","('south carolina','sc','sc')");
    public static final Location SOUTH_DAKOTA     = new USState("SD","('south dakota','sd','sd')");
    public static final Location TENNESSEE        = new USState("TN","('tennessee','tenn','tn')");
    public static final Location TEXAS            = new USState("TX","('texas','tex','tx')");
    public static final Location UTAH             = new USState("UT","('utah','utah','ut')");
    public static final Location VERMONT          = new USState("VT","('vermont','vt','vt')");
    public static final Location VIRGINIA         = new USState("VA","('virginia','va','va')");
    public static final Location WASHINGTON       = new USState("WA","('washington','wash','wa')");
    public static final Location WEST_VIRGINIA    = new USState("WV","('west virginia','wva','wv')");
    public static final Location WISCONSIN        = new USState("WI","('wisconsin','wis','wi')");
    public static final Location WYOMING          = new USState("WY","('wyoming','wyo','wy')");



}