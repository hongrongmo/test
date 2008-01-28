package org.ei.data.georef.runtime;

import org.ei.domain.DataDictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;


public class GRFDataDictionary implements DataDictionary
{
    public GRFDataDictionary()
    {

    }

    public Hashtable getClassCodes() {
        return new Hashtable();
    }

    public Hashtable getTreatments() {
        return new Hashtable();
    }

    public String getClassCodeTitle(String classCode) {
        return "";
    }

    public Hashtable getAuthorityCodes() {
        return new Hashtable();
    }

    public Map getCategories() {

      Map categories = new Hashtable();
      categories.put("01","Mineralogy");
      categories.put("01A","General mineralogy (Includes mineralogical methods, regional studies, mineral collecting)");
      categories.put("01B","Mineralogy of silicates");
      categories.put("01C","Mineralogy of non-silicates");
      categories.put("02","Geochemistry");
      categories.put("02A","General geochemistry (Includes geochemical methods)");
      categories.put("02B","Geochemistry of water");
      categories.put("02C","Geochemistry of rocks, soils, and sediments");
      categories.put("02D","Isotope geochemistry");
      categories.put("03","Geochronology (Includes absolute age and relative age)");
      categories.put("04","Extraterrestrial geology");
      categories.put("05","Igneous and metamorphic petrology");
      categories.put("05A","Igneous and metamorphic petrology (Includes igneous rocks, metamorphic rocks, inclusions, intrusions, lava, magmas, metamorphism, metasomatism, meteor craters, phase equilibria, pre-Quaternary volcanism)");
      categories.put("05B","Petrology of meteorites and tektites");
      categories.put("06","Sedimentary petrology");
      categories.put("06A","Sedimentary petrology (Includes clay mineralogy, diagenesis, heavy minerals, reefs, sedimentary rocks, sedimentary structures, sedimentation, sediments, weathering)");
      categories.put("06B","Petrology of coal");
      categories.put("07","Oceanography (Includes modern marine sedimentation)");
      categories.put("08","General paleontology (Includes life origin, ichnofossils (if not related to a specific fossil group), problematic fossils, and studies which fall under more than one paleontologic category)");
      categories.put("09","Paleobotany");
      categories.put("10","Invertebrate paleontology");
      categories.put("11","Vertebrate paleontology");
      categories.put("12","Stratigraphy (Includes pre-Quaternary stratigraphy, biostratigraphy, lithostratigraphy, magnetostratigraphy, paleogeography, archaeology, changes of level, paleoclimatology, lithofacies, paleoecology, biogeography)");
      categories.put("13","Areal geology (Includes regional studies, guidebooks, and studies which fall under 3 or more categories)");
      categories.put("14","Geologic maps (Note ,Other specific maps are found under the relevant category)");
      categories.put("15","Miscellaneous (Includes mathematical geology, general geoscience education, annual reports of geologic surveys and associations, history, geology as a profession, forensic geology)");
      categories.put("16","Structural geology (Includes deformation, structural analysis, tectonics, neotectonics, salt tectonics, epeirogeny, faults, folds, foliation, fractures, geosynclines, isostasy, lineation, orogeny)");
      categories.put("17","General geophysics");
      categories.put("17A","General geophysics (Includes physical properties of rocks and minerals)");
      categories.put("17B","Geophysics of minerals and rocks, Includes phase transitions, high pressure-temperature studies of rocks and minerals (applied to core and mantle composition)");
      categories.put("18","Solid-earth geophysics (Includes tectonophysics, crust, mantle, core, application of seismicity, plate tectonics, paleomagnetism, heat flow, isostasy, sea-floor spreading, magnetic field, gravity field, Earth’s orbit and rotation)");
      categories.put("19","Seismology (Includes earthquakes, seismicity, explosions, elastic waves, seismic sources)");
      categories.put("20","Applied geophysics (Includes acoustical surveys, Earth-current surveys, electrical surveys, electromagnetic surveys, gravity surveys, infrared surveys, magnetic surveys, magnetotelluric surveys, seismic surveys, geodesy, heat flow, remote sensing, well-logging)");
      categories.put("21","Hydrogeology (Includes water resources)");
      categories.put("22","Environmental geology (Includes conservation, ecology, geologic hazards, impact statements, land use, pollution (including water pollution and soil pollution), reclamation, waste disposal)");
      categories.put("23","Geomorphology (Includes erosion, mass movements, meteor craters, cryptoexplosion features, eolian features, erosion features, fluvial features, frost action, lacustrine features, shore features, solution features, volcanic features)");
      categories.put("24","Quaternary geology (Includes Quaternary geomorphology, Quaternary glacial geology and glacial features, Quaternary stratigraphy, Quaternary archaeology, Quaternary volcanoes, Quaternary climate, Quaternary sediments, Quaternary changes of level)");
      categories.put("25","Soils");
      categories.put("26","Economic geology, general");
      categories.put("26A","Economic geology, general, deposits (Includes mining geology)");
      categories.put("26B","Economic geology, general, economics");
      categories.put("27","Economic geology of ore deposits");
      categories.put("27A","Economic geology, geology of ore deposits (Includes uranium ores)");
      categories.put("27B","Economic geology, economics of ore deposits (Includes uranium ores)");
      categories.put("28","Economic geology of nonmetal deposits");
      categories.put("28A","Economic geology, geology of nonmetal deposits");
      categories.put("28B","Economic geology, economics of nonmetal deposits");
      categories.put("29","Economic geology of energy sources");
      categories.put("29A","Economic geology, geology of energy sources (Includes petroleum (oil and gas), coal, and other energy sources)");
      categories.put("29B","Economic geology, economics of energy sources (Includes petroleum (oil and gas), coal, and other energy sources)");
      categories.put("30","Engineering geology (Includes rock mechanics, soil mechanics, waste disposal, reclamation, dams, earthquakes, explosions, foundations, geologic hazards, highways, land subsidence, marine installations, nuclear facilities, permafrost, reservoirs, shorelines, slope stability, soil mechanics, tunnels, underground installations, waterways)");

      return categories;
    }


    public Map getDocumenttypes() {

      Map doctypes = new Hashtable();

      doctypes.put("S","Serial");
      doctypes.put("B","Book");
      doctypes.put("R","Report");
      doctypes.put("C","Conference document");
      doctypes.put("M","Map");
      doctypes.put("T","Thesis or dissertation");

      return doctypes;
    }


    public Map getBibliographiccodes() {

      Map bibliographiccodes = new Hashtable();

      bibliographiccodes.put("A","Analytic level");
      bibliographiccodes.put("M","Monographic level");
      bibliographiccodes.put("C","Collective level");
      bibliographiccodes.put("S","Serial level");

      return bibliographiccodes;
    }

}