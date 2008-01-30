package org.ei.data.georef.runtime;

import org.ei.domain.Key;
import org.ei.domain.Keys;
import org.ei.domain.RIS;

import java.util.Arrays;


    public class RISView extends CitationView {

        private Key[] keys = new Key[]{Keys.RIS_TY,
                                        Keys.RIS_LA,
                                        Keys.RIS_N1,
                                        Keys.RIS_TI,
                                        Keys.RIS_T1,
                                        Keys.RIS_BT,
                                        Keys.RIS_JO,
                                        Keys.RIS_T3,
                                        Keys.RIS_AUS,
                                        Keys.RIS_AD,
                                        Keys.RIS_EDS,
                                        Keys.RIS_VL,
                                        Keys.RIS_IS,
                                        Keys.RIS_PY,
                                        Keys.RIS_AN,
                                        Keys.RIS_SP,
                                        Keys.RIS_EP,
                                        Keys.RIS_SN,
                                        Keys.RIS_S1,
                                        Keys.RIS_MD,
                                        Keys.RIS_CY,
                                        Keys.RIS_PB,
                                        Keys.RIS_N2,
                                        Keys.RIS_KW,
                                        Keys.RIS_CVS,
                                        Keys.RIS_FLS,
                                        Keys.RIS_DO,
                                        Keys.BIB_TY };

        public String getFormat() { return RIS.RIS_FORMAT; }
    }