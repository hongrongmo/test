package org.ei.data.geobase.runtime;

import org.ei.domain.DataDictionary;
import java.util.Hashtable;


public class GEODataDictionary
    implements DataDictionary
{
    private Hashtable classCodes;
    private Hashtable treatmentCodes;

    public String getClassCodeTitle(String classCode)
    {
        return (String)classCodes.get(classCode);
    }

    public Hashtable getClassCodes()
    {
        return this.classCodes;
    }
    public Hashtable getTreatments()
    {
        return this.treatmentCodes;
    }

    public GEODataDictionary()
    {
        classCodes = new Hashtable();
        classCodes.put("74.1.3",	"Crop and livestock production - technical aspects");
		classCodes.put("74.1.2",	"Crop and livestock production - general");
		classCodes.put("74.1.1",	"Agricultural policy and theory");
		classCodes.put("70.13.3",	"Livestock");
		classCodes.put("70.13.2",	"Arable");
		classCodes.put("70.13.1",	"Policy");
		classCodes.put("71.6.11.2",	"Groundwater quality: phreatic zone");
		classCodes.put("71.6.11.1",	"Groundwater quality: vadose zone");
		classCodes.put("71.4.9",	"Sedimentary environments - oceanic");
		classCodes.put("71.4.8",	"Sedimentary environments - coastal and shallow marine");
		classCodes.put("71.4.7",	"Sedimentary environments - terrestrial");
		classCodes.put("71.4.6",	"Sedimentary geochemistry");
		classCodes.put("71.4.5",	"Sediments and sedimentary processes - diagenesis");
		classCodes.put("74.9",		"SMALL BUSINESS PROMOTION AND THE INFORMAL SECTOR");
		classCodes.put("73.10.11",	"PALAEOECOLOGY");
		classCodes.put("71.4.4",	"Sediments and sedimentary processes - deposition");
		classCodes.put("74.8",		"INDUSTRY, INDUSTRIAL POLICY");
		classCodes.put("73.10.10",	"EVOLUTIONARY ECOLOGY");
		classCodes.put("71.4.3",	"Sediments and sedimentary processes - transport");
		classCodes.put("74.7",		"PRIVATE INVESTMENT FLOWS, MULTINATIONALS");
		classCodes.put("71.4.2",	"Sediments and sedimentary processes - physical properties");
		classCodes.put("74.6",		"FINANCIAL SECTOR, DEBT");
		classCodes.put("71.4.1",	"Methods, equipment and programs");
		classCodes.put("74.5",		"ADJUSTMENT POLICIES, ECONOMIC REFORM");
		classCodes.put("74.4",		"COMPARATIVE DEVELOPMENT STRATEGIES");
		classCodes.put("73.11",		"GENERAL THEORY AND METHODS");
		classCodes.put("74.3",		"ECONOMIC CONDITIONS AND GENERAL TEXTS");
		classCodes.put("73.10",		"EVOLUTION AND PALAEOECOLOGY");
		classCodes.put("74.2",		"ENVIRONMENT AND DEVELOPMENT");
		classCodes.put("74.1",		"AGRICULTURE AND RURAL DEVELOPMENT");
		classCodes.put("77.9.9",	"Construction methods");
		classCodes.put("77.9.8",	"Earth retaining structures");
		classCodes.put("77.9.7",	"Slopes");
		classCodes.put("77.9.6",	"Offshore structures");
		classCodes.put("70.5.6",	"Planning History");
		classCodes.put("77.9.5",	"Hydraulic structures");
		classCodes.put("70.5.5",	"Social History");
		classCodes.put("77.9.4",	"Dams and embankments");
		classCodes.put("70.5.4",	"Economic History");
		classCodes.put("77.9.3",	"Foundations and piles");
		classCodes.put("77.9.2",	"Base courses and pavements");
		classCodes.put("77.9.1",	"Geological factors");
		classCodes.put("77.1.3",	"Earthquake mechanisms and effects");
		classCodes.put("77.1.2",	"Environmental effects");
		classCodes.put("77.1.1",	"Tectonics and structural geology");
		classCodes.put("72.10.3",	"Plate tectonics");
		classCodes.put("72.10.2",	"Regional structure and tectonics");
		classCodes.put("72.10.1",	"Deformation mechanisms and processes");
		classCodes.put("76.2.9",	"Particulates");
		classCodes.put("76.2.8",	"Atmospheric chemistry");
		classCodes.put("76.2.7",	"Clouds, fog, precipitation");
		classCodes.put("76.2.6",	"Air masses, Atmospheric motion");
		classCodes.put("76.2.5",	"Air-sea interaction");
		classCodes.put("76.2.4",	"Radiation");
		classCodes.put("76.2.3",	"Climate, climate change");
		classCodes.put("76.2.2",	"Regional and general");
		classCodes.put("76.2.1",	"Apparatus and methods");
		classCodes.put("76.3.12.2",	"Carbon chemistry: carbonate systems");
		classCodes.put("76.3.12.1",	"Carbon chemistry: organic compounds");
		classCodes.put("73.3.2.7",	"Marine ecology: distributions and systematics");
		classCodes.put("73.3.2.6",	"Marine ecology: community structure and function");
		classCodes.put("73.3.2.5",	"Marine ecology: interspecific interactions");
		classCodes.put("73.3.2.4",	"Marine ecology: production and nutrients");
		classCodes.put("73.3.2.3",	"Marine ecology: population ecology");
		classCodes.put("73.3.2.2",	"Marine ecology: autecology and behaviour");
		classCodes.put("73.3.2.1",	"Marine ecology: general studies");
		classCodes.put("71.6.14",	"Water resources and management");
		classCodes.put("71.6.13",	"Land use, forestry and agriculture");
		classCodes.put("71.6.12",	"Glacial hydrology");
		classCodes.put("71.6.11",	"Groundwater quality");
		classCodes.put("71.6.10",	"Groundwater processes");
		classCodes.put("70.16.1",	"Tourism and Recreation");
		classCodes.put("72.6.5",	"Palaeogeography and palaeoclimatology");
		classCodes.put("72.6.4",	"Tertiary");
		classCodes.put("72.6.3",	"Mesozoic");
		classCodes.put("72.6.2",	"Palaeozoic");
		classCodes.put("72.6.1",	"Precambrian");
		classCodes.put("71.7.9",	"Dynamic meteorology");
		classCodes.put("71.7.8",	"Air-sea interaction");
		classCodes.put("71.7.7",	"Boundary layer meteorology");
		classCodes.put("71.7.6",	"Synoptic meteorology");
		classCodes.put("77.10.12",	"Influence of dynamic loads");
		classCodes.put("71.7.5",	"Regional weather patterns");
		classCodes.put("77.9",		"SURFACE STRUCTURES");
		classCodes.put("77.10.11",	"Subsidence, caving and rockbursts");
		classCodes.put("71.7.4",	"Optics");
		classCodes.put("77.8",		"ROCK AND SOIL REINFORCEMENT AND SUPPORT");
		classCodes.put("77.10.10",	"Groundwater problems and environmental effects");
		classCodes.put("71.7.3",	"Atmospheric electricity");
		classCodes.put("77.7",		"ROCK BREAKAGE AND EXCAVATION");
		classCodes.put("71.7.2",	"Radiation");
		classCodes.put("77.6",		"ANALYSIS TECHNIQUES AND DESIGN METHODS");
		classCodes.put("71.7.1",	"Instrumentation");
		classCodes.put("77.5",		"SITE INVESTIGATION AND FIELD OBSERVATION");
		classCodes.put("77.4",		"IN SITU STRESS");
		classCodes.put("77.3",		"PROPERTIES OF ROCKS AND SOILS");
		classCodes.put("77.2",		"HYDROGEOLOGY");
		classCodes.put("77.1",		"GEOLOGY");
		classCodes.put("77.0",		"GENERAL");
		classCodes.put("70.8.3",	"Planning");
		classCodes.put("70.8.2",	"Economic");
		classCodes.put("70.8.1",	"Descriptive");
		classCodes.put("77.4.2",	"Measurement of in situ stress");
		classCodes.put("77.4.1",	"Causes and nature of in situ stress");
		classCodes.put("72.13.5",	"Hydrogeology");
		classCodes.put("72.13.4",	"Engineering seismology");
		classCodes.put("72.13.3",	"Underground excavations");
		classCodes.put("72.13.2",	"Rock mechanics");
		classCodes.put("72.13.1",	"Soil mechanics");
		classCodes.put("76.5.4",	"Nekton");
		classCodes.put("76.5.3",	"Planktonic ecosystems");
		classCodes.put("76.5.2",	"Regional and general");
		classCodes.put("76.2.10",	"Storms, disturbances, cyclones");
		classCodes.put("76.5.1",	"Apparatus and methods");
		classCodes.put("70.4.2.2",	"Fuel and Energy: Others");
		classCodes.put("70.4.2.1",	"Fuel and Energy: Hydrocarbons");
		classCodes.put("73.10.10.4","Life History Strategies");
		classCodes.put("73.10.10.3","Adaptation");
		classCodes.put("73.10.10.2","Population Genetics");
		classCodes.put("73.10.10.1","Evolution and Speciation");
		classCodes.put("71.8.7.2",	"Cartography");
		classCodes.put("71.8.7.1",	"Map curatorship and history");
		classCodes.put("73.4.7.4.6","Animal ecology: birds: distributions and systematics");
		classCodes.put("73.4.7.4.5","Animal ecology: birds: communities");
		classCodes.put("73.4.7.4.4","Animal ecology: birds: interspecific interactions");
		classCodes.put("73.4.7.4.3","Animal ecology: birds: population ecology");
		classCodes.put("73.4.7.4.2","Animal ecology: birds: autecology and behaviour");
		classCodes.put("73.8.4",	"Restoration ecology");
		classCodes.put("73.8.3",	"Regional");
		classCodes.put("73.4.7.4.1","Animal ecology: birds: general");
		classCodes.put("73.8.2",	"Species");
		classCodes.put("73.8.1",	"General");
		classCodes.put("71.6.9.3",	"Surface water quality: lakes and reservoirs");
		classCodes.put("71.6.9.2",	"Surface water quality: rivers and streams");
		classCodes.put("71.6.9.1",	"Surface water quality: runoff and soil water");
		classCodes.put("70.16",		"RECREATIONAL GEOGRAPHY");
		classCodes.put("70.15",		"TRANSPORT AND COMMUNICATIONS");
		classCodes.put("70.14",		"INDUSTRY AND SERVICES");
		classCodes.put("70.13",		"AGRICULTURE");
		classCodes.put("70.11.7",	"Environmental Economics");
		classCodes.put("70.12",		"EXCHANGE AND DEVELOPMENT");
		classCodes.put("70.11.6",	"Environmental Law");
		classCodes.put("70.11",		"ENVIRONMENTAL PLANNING");
		classCodes.put("70.11.5",	"Waste Management and Pollution Policy");
		classCodes.put("70.10",		"NATIONAL, REGIONAL AND COMMUNITY PLANNING");
		classCodes.put("70.11.4",	"Coastal and Marine Management");
		classCodes.put("70.11.3",	"National and Local Planning");
		classCodes.put("70.11.2",	"Global and Regional Planning");
		classCodes.put("70.11.1",	"Environmental Assessment and Monitoring");
		classCodes.put("72.1.5",	"Clay minerals");
		classCodes.put("72.1.4",	"Properties of minerals");
		classCodes.put("72.1.3",	"Experimental mineralogy");
		classCodes.put("72.1.2",	"Crystallography");
		classCodes.put("72.1.1",	"Techniques");
		classCodes.put("76.15.4",	"Wave and tidal power");
		classCodes.put("76.15.3",	"Outfalls");
		classCodes.put("76.15.2",	"Ports and Harbours");
		classCodes.put("71.2.9",	"Slopes");
		classCodes.put("76.15.1",	"Coastal protection measures");
		classCodes.put("71.2.8",	"Landforms of other planets");
		classCodes.put("71.2.7",	"Neotectonics and structural control");
		classCodes.put("71.2.6",	"Regional physiography");
		classCodes.put("71.2.5",	"Arid landforms");
		classCodes.put("71.2.4",	"Volcanic landforms");
		classCodes.put("72.9",		"REGIONAL GEOLOGY");
		classCodes.put("71.2.3",	"Coastal landforms");
		classCodes.put("72.8",		"PALAEONTOLOGY");
		classCodes.put("71.2.2",	"Fluvial landforms");
		classCodes.put("72.7",		"THE QUATERNARY");
		classCodes.put("71.2.1",	"Weathering and related landforms");
		classCodes.put("72.6",		"STRATIGRAPHY");
		classCodes.put("72.5",		"SEDIMENTARY GEOLOGY");
		classCodes.put("72.4",		"IGNEOUS AND METAMORPHIC GEOLOGY");
		classCodes.put("72.3",		"GEOCHRONOLOGY");
		classCodes.put("72.2",		"GEOCHEMISTRY");
		classCodes.put("72.1",		"MINERALOGY");
		classCodes.put("901",		"Geobase: Related Topics");
		classCodes.put("77.7.6",	"Machine abrasion and wear");
		classCodes.put("77.7.5",	"Machine excavation");
		classCodes.put("77.7.4",	"Crushing and grinding");
		classCodes.put("70.3.5",	"Perception");
		classCodes.put("77.7.3",	"Blasting");
		classCodes.put("70.3.4",	"Medical Geography");
		classCodes.put("77.7.2",	"Drilling");
		classCodes.put("70.3.3",	"Pollution");
		classCodes.put("77.7.1",	"Rock fracture under dynamic stress");
		classCodes.put("70.3.2",	"Nature - Society Relations");
		classCodes.put("70.3.1",	"Natural Hazards");
		classCodes.put("72.16.3",	"Geothermal energy");
		classCodes.put("72.16.2",	"Hydrocarbons");
		classCodes.put("72.16.1",	"Coal");
		classCodes.put("73.4.7.5",	"Animal ecology: mammals");
		classCodes.put("73.4.7.4",	"Animal ecology: birds");
		classCodes.put("73.4.7.3",	"Animal ecology: amphibians and reptiles");
		classCodes.put("73.4.7.2",	"Animal ecology: invertebrates");
		classCodes.put("73.4.7.1",	"Animal ecology: general");
		classCodes.put("71.3.10",	"Glaciology");
		classCodes.put("74.2.6",	"Pollution and waste recycling");
		classCodes.put("74.2.5",	"Marine and coastal environment");
		classCodes.put("74.13.4",	"Fisheries and aquatic resources");
		classCodes.put("74.2.4",	"Protected areas, wildlife conservation");
		classCodes.put("74.13.3",	"Irrigation and drainage - technical aspects");
		classCodes.put("74.2.3",	"Land degradation, reclamation and erosion control");
		classCodes.put("74.13.2",	"Water supply and sanitation");
		classCodes.put("74.2.2",	"Concepts and issues in environmental planning");
		classCodes.put("74.13.1",	"Water resources development - general");
		classCodes.put("74.2.1",	"Agriculture, resource development and the environment");
		classCodes.put("70.9.3.4",	"Urban Planning: Architecture and Greenspace");
		classCodes.put("70.9.3.3",	"Urban Planning: Renewal and Conservation");
		classCodes.put("70.9.3.2",	"Urban Planning: Projects and Policies");
		classCodes.put("70.9.3.1",	"Urban Planning: Planning Processes");
		classCodes.put("73.3.4",	"Freshwater ecology");
		classCodes.put("73.3.3",	"Tidal and estuarine ecology");
		classCodes.put("73.3.2",	"Marine ecology");
		classCodes.put("73.3.1",	"General studies");
		classCodes.put("76.4.13",	"Economic geology");
		classCodes.put("76.4.12",	"Structural geology and tectonics");
		classCodes.put("76.4.11",	"Earth's heat");
		classCodes.put("76.4.10",	"Gravity, electricity and magnetism");
		classCodes.put("70.14.5",	"Science and Technology");
		classCodes.put("70.14.4",	"Services");
		classCodes.put("70.14.3",	"Secondary");
		classCodes.put("70.14.2",	"Primary");
		classCodes.put("70.14.1",	"Policy");
		classCodes.put("74.41.3",	"Conflict, protest, human rights");
		classCodes.put("74.41.2",	"Political economy, class and state analysis");
		classCodes.put("74.41.1",	"Political systems and political change");
		classCodes.put("72.4.4",	"Hydrothermal systems");
		classCodes.put("72.4.3",	"Metamorphic petrology and processes");
		classCodes.put("72.4.2",	"Volcanology");
		classCodes.put("72.4.1",	"Igneous petrology and processes");
		classCodes.put("71.5.9",	"Soil water");
		classCodes.put("71.5.8",	"Physical properties");
		classCodes.put("77.11",		"SUBJECTS PERIPHERAL TO GEOMECHANICS");
		classCodes.put("77.10",		"EXCAVATIONS");
		classCodes.put("71.5.3",	"Genesis and formation");
		classCodes.put("71.5.2",	"Methods");
		classCodes.put("71.5.1",	"Regional and survey");
		classCodes.put("70.6.4",	"Migration");
		classCodes.put("70.6.3",	"Fertility");
		classCodes.put("70.6.2",	"Change");
		classCodes.put("70.6.1",	"Distribution");
		classCodes.put("77.9.11",	"Influence of dynamic loads");
		classCodes.put("77.9.10",	"Groundwater problems and environmental effects");
		classCodes.put("74.1.10",	"Social, economic and political aspects of rural change");
		classCodes.put("72.11.9",	"Remote sensing");
		classCodes.put("77.2.3",	"Fluid flow measurement and modelling");
		classCodes.put("72.11.8",	"Well logging");
		classCodes.put("77.2.2",	"Chemical and physical changes");
		classCodes.put("72.11.7",	"Mathematical and computing methods");
		classCodes.put("77.2.1",	"Groundwater");
		classCodes.put("72.11.6",	"Earth's heat");
		classCodes.put("72.11.5",	"Electricity");
		classCodes.put("72.11.4",	"Palaeomagnetism");
		classCodes.put("72.11.3",	"Magnetism");
		classCodes.put("72.11.2",	"Gravity");
		classCodes.put("72.11.1",	"Earth's rotation, shape and structure");
		classCodes.put("72.18",		"GENERAL TEXTS");
		classCodes.put("72.17",		"EXTRATERRESTRIAL GEOLOGY");
		classCodes.put("72.16",		"ENERGY SOURCES");
		classCodes.put("72.15",		"ECONOMIC GEOLOGY");
		classCodes.put("72.14",		"ENVIRONMENTAL GEOLOGY");
		classCodes.put("72.13",		"ENGINEERING GEOLOGY");
		classCodes.put("72.12",		"SEISMOLOGY");
		classCodes.put("76.3.9",	"Particulates and colloids");
		classCodes.put("72.11",		"GEOPHYSICS");
		classCodes.put("72.10",		"STRUCTURAL GEOLOGY AND TECTONICS");
		classCodes.put("76.3.7",	"Dissolved gases");
		classCodes.put("76.3.6",	"Radioactivity");
		classCodes.put("76.3.5",	"Physical chemistry");
		classCodes.put("76.3.4",	"Metals, metalloids and metallo-organics");
		classCodes.put("76.3.3",	"Chemical properties");
		classCodes.put("76.3.2",	"Regional and general");
		classCodes.put("76.3.1",	"Apparatus and methods");
		classCodes.put("71.8.5.4",	"Remote sensing: numerical and image analysis");
		classCodes.put("71.8.5.3",	"Remote sensing: geodesy");
		classCodes.put("71.8.5.2",	"Remote sensing: secondary data capture");
		classCodes.put("71.8.5.1",	"Remote sensing: primary data capture");
		classCodes.put("76.5.3.2",	"zooplankton, micronekton");
		classCodes.put("76.5.3.1",	"Planktonic ecosystems: phytoplankton, seston, detritus");
		classCodes.put("73.3.3.7",	"Tidal and estuarine ecology: distributions and systematics");
		classCodes.put("73.3.3.6",	"Tidal and estuarine ecology: community structure and function");
		classCodes.put("73.3.3.5",	"Tidal and estuarine ecology: interspecific interactions");
		classCodes.put("73.3.3.4",	"Tidal and estuarine ecology: production and nutrients");
		classCodes.put("73.3.3.3",	"Tidal and estuarine ecology: population ecology");
		classCodes.put("73.4.7.2.6","Animal ecology: invertebrates: distributions and systematics");
		classCodes.put("73.3.3.2",	"Tidal and estuarine ecology: autecology and behaviour");
		classCodes.put("73.4.7.2.5","Animal ecology: invertebrates: communities");
		classCodes.put("77.10.9",	"Construction methods");
		classCodes.put("73.3.3.1",	"Tidal and estuarine ecology: general studies");
		classCodes.put("73.4.7.2.4","Animal ecology: invertebrates: interspecific interactions");
		classCodes.put("77.10.8",	"Radioactive waste disposal");
		classCodes.put("73.4.7.2.3","Animal ecology: invertebrates: population ecology");
		classCodes.put("73.6.5",	"Chemical control");
		classCodes.put("77.10.7",	"General underground storage");
		classCodes.put("73.4.7.2.2","Animal ecology: invertebrates: autecology and behaviour");
		classCodes.put("73.6.4",	"Biological and integrated control");
		classCodes.put("77.10.6",	"Landfills");
		classCodes.put("73.4.7.2.1","Animal ecology: invertebrates: general");
		classCodes.put("73.6.3",	"Diseases");
		classCodes.put("77.10.5",	"Mines and quarries");
		classCodes.put("73.6.2",	"Pests");
		classCodes.put("77.10.4",	"Tunnels");
		classCodes.put("73.6.1",	"Weeds");
		classCodes.put("77.10.3",	"Excavation failure mechanisms");
		classCodes.put("77.10.2",	"Stresses around openings");
		classCodes.put("77.10.1",	"Geological factors");
		classCodes.put("72.7.9",	"Periglacial");
		classCodes.put("72.7.8",	"Sea-level and river terraces");
		classCodes.put("72.7.7",	"The Holocene");
		classCodes.put("72.7.6",	"Glacial landforms and sediments");
		classCodes.put("72.7.5",	"Mid-latitude and extra-glacial");
		classCodes.put("72.7.4",	"Tropics and subtropics");
		classCodes.put("72.7.3",	"Oceans");
		classCodes.put("72.7.2",	"Palaeoclimatology");
		classCodes.put("72.7.1",	"General and chronology");
		classCodes.put("71.8.8",	"Applications");
		classCodes.put("71.8.7",	"Mapping");
		classCodes.put("71.8.6",	"GIS");
		classCodes.put("71.8.5",	"Remote sensing");
		classCodes.put("76.13.3",	"Case studies");
		classCodes.put("76.13.2",	"Commercial species");
		classCodes.put("71.5.15",	"Erosion and conservation");
		classCodes.put("76.13.1",	"Methods and management");
		classCodes.put("71.5.14",	"Contamination and remediation");
		classCodes.put("71.5.13",	"Biota");
		classCodes.put("71.5.12",	"Organic matter");
		classCodes.put("71.5.11",	"Chemistry");
		classCodes.put("71.5.10",	"Mineralogy");
		classCodes.put("70.9",		"URBAN STUDIES");
		classCodes.put("70.8",		"RURAL STUDIES");
		classCodes.put("70.7",		"PEOPLE AND REGIONS");
		classCodes.put("70.6",		"POPULATION");
		classCodes.put("70.5",		"HISTORICAL GEOGRAPHY");
		classCodes.put("70.4",		"ENVIRONMENTAL RESOURCES AND RESOURCES MANAGEMENT");
		classCodes.put("70.3",		"ENVIRONMENT");
		classCodes.put("70.2",		"METHODOLOGY AND TECHNIQUES");
		classCodes.put("70.1",		"THEORETICAL GEOGRAPHY");
		classCodes.put("70.9.3",	"Urban Planning");
		classCodes.put("70.9.2",	"Economic");
		classCodes.put("70.9.1",	"Descriptive");
		classCodes.put("77.5.8",	"Risk assessment");
		classCodes.put("77.5.7",	"Monitoring rock and soil mass performance");
		classCodes.put("77.5.6",	"Suggested testing methods and standards");
		classCodes.put("77.5.5",	"Processing and interpretation of data");
		classCodes.put("77.5.4",	"Borehole and core logging");
		classCodes.put("70.1.4",	"Planning");
		classCodes.put("77.5.3",	"Geophysical techniques");
		classCodes.put("70.1.3",	"Historical Geography");
		classCodes.put("77.5.2",	"Structural and geotechnical mapping");
		classCodes.put("77.5.1",	"Remote sensing and photographic techniques");
		classCodes.put("70.1.2",	"Social Geography");
		classCodes.put("70.1.1",	"Economic Geography");
		classCodes.put("72.14.3",	"Pollution and waste management - radioactive");
		classCodes.put("72.14.2",	"Pollution and waste management - non-radioactive");
		classCodes.put("72.14.1",	"Reclamation and conservation");
		classCodes.put("76.6.7",	"Pollution control and remediation");
		classCodes.put("74.41",		"POLITICS");
		classCodes.put("74.40",		"INSTITUTIONAL FRAMEWORK AND ADMINISTRATION");
		classCodes.put("76.6.5",	"Effects of pollution and environmental disturbance");
		classCodes.put("76.6.4",	"Biological uptake");
		classCodes.put("76.6.3",	"Pollutant transport");
		classCodes.put("76.6.2",	"Regional and general");
		classCodes.put("76.6.1",	"Apparatus and methods");
		classCodes.put("74.39",		"CULTURE AND DEVELOPMENT");
		classCodes.put("74.38",		"DEVELOPMENT THEORY AND CONCEPTS");
		classCodes.put("70.4.3.2",	"Water Resources: Planning");
		classCodes.put("74.37",		"TECHNOLOGY AND SCIENCE POLICY");
		classCodes.put("70.4.3.1",	"Water Resources: Economic");
		classCodes.put("74.36",		"TRANSPORT AND COMMUNICATIONS");
		classCodes.put("74.35",		"TRADE AND TRADE POLICY");
		classCodes.put("74.34",		"INTERNATIONAL RELATIONS, INTERNATIONAL FINANCIAL INSTITUTIONS");
		classCodes.put("74.33",		"PROJECT DESIGN AND APPRAISAL");
		classCodes.put("74.32",		"AID FLOWS AND POLICIES");
		classCodes.put("74.31",		"NONGOVERNMENTAL ORGANISATIONS AND PEOPLE'S PARTICIPATION");
		classCodes.put("71.8.8.5",	"Applications: human");
		classCodes.put("74.30",		"WOMEN AND GENDER ISSUES");
		classCodes.put("71.8.8.4",	"Applications: hydrosphere");
		classCodes.put("71.8.8.3",	"Applications: biosphere");
		classCodes.put("71.8.8.2",	"Applications: lithosphere");
		classCodes.put("71.8.8.1",	"Applications: atmosphere");
		classCodes.put("74.29",		"ETHNICITY AND FOURTH WORLD ISSUES");
		classCodes.put("76.1.11",	"Properties and processes");
		classCodes.put("74.28",		"POVERTY, WELFARE AND INCOME DISTRIBUTION");
		classCodes.put("76.1.10",	"Ice");
		classCodes.put("74.27",		"SOCIAL POLICY, OTHER SOCIAL SERVICES");
		classCodes.put("74.26",		"HOUSING");
		classCodes.put("74.25",		"EDUCATION AND TRAINING");
		classCodes.put("74.24",		"NUTRITION");
		classCodes.put("74.23",		"FOOD, FOOD SUPPLY");
		classCodes.put("71.7.9.2",	"Dynamic meteorology: mesoscale");
		classCodes.put("74.22",		"HEALTH, HEALTH SYSTEMS AND SERVICES");
		classCodes.put("71.7.9.1",	"Dynamic meteorology: large scale");
		classCodes.put("74.21",		"HAZARDS AND DISASTER PLANNING");
		classCodes.put("74.20",		"REGIONAL AND SPATIAL DEVELOPMENT AND PLANNING");
		classCodes.put("73.4.7.5.6","Animal ecology: mammals: distributions and systematics");
		classCodes.put("73.4.7.5.5","Animal ecology: mammals: communities");
		classCodes.put("73.4.7.5.4","Animal ecology: mammals: interspecific interactions");
		classCodes.put("73.4.7.5.3","Animal ecology: mammals: population ecology");
		classCodes.put("73.4.7.5.2","Animal ecology: mammals: autecology and behaviour");
		classCodes.put("73.9.4",	"Fisheries and aquatic resources");
		classCodes.put("73.4.7.5.1","Animal ecology: mammals: general");
		classCodes.put("73.9.3",	"Forestry");
		classCodes.put("73.9.2",	"Pasture and livestock");
		classCodes.put("73.9.1",	"Agriculture");
		classCodes.put("74.19",		"URBANISATION AND URBAN PLANNING");
		classCodes.put("74.18",		"MIGRATION, REFUGEES, RESETTLEMENT. LABOUR MIGRATION");
		classCodes.put("72.5.10",	"Sequence stratigraphy");
		classCodes.put("74.17",		"DEMOGRAPHY, POPULATION POLICY");
		classCodes.put("74.16",		"ENERGY");
		classCodes.put("74.15",		"HYDROCARBONS AND MINING");
		classCodes.put("76.16.1.2",	"Surface installations: fixed");
		classCodes.put("74.14",		"FORESTRY");
		classCodes.put("76.16.1.1",	"Surface installations: floating");
		classCodes.put("74.13",		"WATER");
		classCodes.put("74.12",		"LABOUR AND MANAGEMENT, EMPLOYMENT POLICY");
		classCodes.put("74.11",		"TOURISM");
		classCodes.put("74.10",		"PARALLEL AND ILLEGAL ECONOMY");
		classCodes.put("70.12.7",	"Socioeconomic Indicators");
		classCodes.put("70.12.6",	"Labour and Income");
		classCodes.put("70.12.5",	"Capital and Investments");
		classCodes.put("70.12.4",	"National Trade and Consumption");
		classCodes.put("70.12.3",	"International Aid and Investment");
		classCodes.put("70.12.2",	"International Trade and Economic Association");
		classCodes.put("70.12.1",	"Economic Development");
		classCodes.put("71.6.10.2",	"Groundwater processes: phreatic zone");
		classCodes.put("71.6.10.1",	"Groundwater processes: vadose zone");
		classCodes.put("72.2.5",	"Economic geochemistry");
		classCodes.put("72.2.4",	"Hydrochemistry");
		classCodes.put("72.2.3",	"Sedimentary geochemistry");
		classCodes.put("72.2.2",	"Metamorphic geochemistry");
		classCodes.put("72.2.1",	"Igneous geochemistry");
		classCodes.put("76.16.6",	"Maintenance");
		classCodes.put("76.16.5",	"Operations");
		classCodes.put("76.16.4",	"Well technology");
		classCodes.put("76.16.3",	"Subsea installations");
		classCodes.put("76.16.2",	"Pipeline systems and risers");
		classCodes.put("71.3.9",	"Periglacial");
		classCodes.put("76.16.1",	"Surface installations");
		classCodes.put("71.3.8",	"Sea level");
		classCodes.put("71.3.7",	"The Holocene");
		classCodes.put("71.3.6",	"Glacial landforms and sediments");
		classCodes.put("71.3.5",	"Mid-latitude and extra-glacial");
		classCodes.put("73.9",		"ECONOMIC ECOLOGY");
		classCodes.put("71.3.4",	"Tropics and sub-tropics");
		classCodes.put("73.8",		"NATURE CONSERVATION");
		classCodes.put("71.3.3",	"Oceans");
		classCodes.put("73.7",		"POLLUTION");
		classCodes.put("71.3.2",	"Palaeoclimatology");
		classCodes.put("73.6",		"WEEDS, PESTS AND DISEASES");
		classCodes.put("71.3.1",	"Chronology");
		classCodes.put("73.5",		"GENERAL MICROBIAL ECOLOGY");
		classCodes.put("73.4",		"TERRESTRIAL ECOLOGY");
		classCodes.put("73.3",		"AQUATIC ECOLOGY");
		classCodes.put("73.2",		"GLOBAL ECOLOGY");
		classCodes.put("73.1",		"GENERAL");
		classCodes.put("77.8.7",	"Soil compaction");
		classCodes.put("77.8.6",	"Soil stabilisation");
		classCodes.put("77.8.5",	"Reinforced earth and geosynthetics");
		classCodes.put("77.8.4",	"Grouting");
		classCodes.put("70.4.5",	"Fisheries");
		classCodes.put("77.8.3",	"Direct rock support methods");
		classCodes.put("70.4.4",	"Forest Resources");
		classCodes.put("70.4.3",	"Water Resources");
		classCodes.put("77.8.2",	"Shotcrete");
		classCodes.put("77.8.1",	"Bolts and anchors");
		classCodes.put("70.4.2",	"Fuel and Energy");
		classCodes.put("70.4.1",	"General Resources");
		classCodes.put("72.17.3",	"Meteorites");
		classCodes.put("72.17.2",	"Other planets");
		classCodes.put("72.17.1",	"Moon");
		classCodes.put("76.1.8",	"Waves and oscillations");
		classCodes.put("76.1.7",	"Tides and sea level");
		classCodes.put("76.1.6",	"Water masses and fronts");
		classCodes.put("76.1.5",	"Circulation");
		classCodes.put("76.1.4",	"Currents");
		classCodes.put("76.1.3",	"Nearshore dynamics");
		classCodes.put("76.1.2",	"Regional and general");
		classCodes.put("76.1.1",	"Apparatus and methods");
		classCodes.put("71.7.16",	"Atmospheric pollution");
		classCodes.put("71.7.15",	"Aerosols");
		classCodes.put("71.7.14",	"Atmospheric chemistry");
		classCodes.put("71.7.13",	"Climate change");
		classCodes.put("71.7.12",	"Weather and climate forecasting");
		classCodes.put("71.7.11",	"Rainfall processes");
		classCodes.put("71.7.10",	"Convection and cloud microphysics");
		classCodes.put("73.4.7",	"Animal ecology");
		classCodes.put("73.4.6",	"Plant ecology");
		classCodes.put("73.4.5",	"Fungi and fungal associations");
		classCodes.put("73.4.4",	"Pollination and seed dispersal");
		classCodes.put("73.4.3",	"Herbivory");
		classCodes.put("73.4.2",	"Soil studies");
		classCodes.put("73.4.1",	"General studies");
		classCodes.put("70.15.2",	"Transport Planning");
		classCodes.put("70.15.1",	"Transport and Communicatons");
		classCodes.put("71.2.12",	"Anthropogenic landforms");
		classCodes.put("71.2.11",	"Karst");
		classCodes.put("71.2.10",	"Soil mechanics");
		classCodes.put("72.5.9",	"Applied sedimentology");
		classCodes.put("72.5.8",	"Sedimentary environments - oceanic");
		classCodes.put("72.5.7",	"Sedimentary environments - coastal and shallow marine");
		classCodes.put("72.5.6",	"Sedimentary environments - terrestrial");
		classCodes.put("72.5.5",	"Sediments and sedimentary processes - diagenesis");
		classCodes.put("72.5.4",	"Sediments and sedimentary processes - deposition");
		classCodes.put("72.5.3",	"Sediments and sedimentary processes - transport");
		classCodes.put("72.5.2",	"Sediments and sedimentary processes - physical properties");
		classCodes.put("72.5.1",	"Methods, equipment and programs");
		classCodes.put("71.6.9",	"Surface water quality");
		classCodes.put("71.6.8",	"Wetlands and estuaries");
		classCodes.put("71.6.7",	"Lakes and reservoirs");
		classCodes.put("71.6.6",	"Channel hydraulics and sediment transport");
		classCodes.put("71.6.5",	"Runoff, streamflow and basins");
		classCodes.put("71.6.4",	"Evaporation and transpiration");
		classCodes.put("71.6.3",	"Interception, throughfall and stemflow");
		classCodes.put("71.6.2",	"Precipitation quality");
		classCodes.put("76.6",		"POLLUTION and ENVIRONMENTAL ISSUES");
		classCodes.put("76.11.6",	"Recreation and tourism");
		classCodes.put("71.6.1",	"Precipitation assessment");
		classCodes.put("76.5",		"BIOLOGICAL OCEANOGRAPHY and MARINE ECOLOGY");
		classCodes.put("76.11.5",	"Urban and coastline development");
		classCodes.put("76.4",		"MARINE GEOLOGY and GEOPHYSICS");
		classCodes.put("76.11.4",	"Coastal protection");
		classCodes.put("76.3",		"CHEMICAL OCEANOGRAPHY");
		classCodes.put("76.11.3",	"Waste disposal");
		classCodes.put("76.2",		"MARINE METEOROLOGY");
		classCodes.put("76.11.2",	"Conservation and habitat improvement");
		classCodes.put("76.1",		"PHYSICAL OCEANOGRAPHY");
		classCodes.put("76.11.1",	"Regional and general");
		classCodes.put("76.3.13",	"Sediment geochemistry");
		classCodes.put("76.3.12",	"Carbon chemistry");
		classCodes.put("76.3.11",	"Nutrients");
		classCodes.put("70.7.4",	"Political");
		classCodes.put("70.7.3",	"Regional");
		classCodes.put("70.7.2",	"Gender Studies");
		classCodes.put("70.7.1",	"Cultural");
		classCodes.put("77.3.9",	"Dynamic properties");
		classCodes.put("77.3.8",	"Compression, swelling and consolidation");
		classCodes.put("77.3.7",	"Permeability, porosity and capillarity");
		classCodes.put("77.3.6",	"Physico-chemical properties");
		classCodes.put("77.3.5",	"Surface properties");
		classCodes.put("77.3.4",	"Time dependent behaviour");
		classCodes.put("77.3.3",	"Fracture processes");
		classCodes.put("77.3.2",	"Deformation and strength characteristics");
		classCodes.put("77.3.1",	"Composition, structure, texture and density");
		classCodes.put("76.18",		"REGIONAL and SYNOPTIC STUDIES");
		classCodes.put("72.12.4",	"Earthquakes");
		classCodes.put("76.17",		"FLUID MECHANICS");
		classCodes.put("72.12.3",	"Applied seismology");
		classCodes.put("76.16",		"OFFSHORE ENGINEERING");
		classCodes.put("76.15",		"COASTAL ENGINEERING");
		classCodes.put("72.12.2",	"Seismometry");
		classCodes.put("76.14",		"MARINE TECHNOLOGY");
		classCodes.put("72.12.1",	"Seismic theory");
		classCodes.put("76.13",		"MARICULTURE and FISHERIES");
		classCodes.put("76.12",		"MARINE POLICY and LAW");
		classCodes.put("76.11",		"ENVIRONMENTAL and COASTAL ZONE MANAGEMENT");
		classCodes.put("76.4.9",	"Earth's rotation, shape and structure");
		classCodes.put("76.4.8",	"Igneous and metamorphic geology");
		classCodes.put("76.4.7",	"Hydrothermal activity");
		classCodes.put("76.4.6",	"Geochronology, stratigraphy and palaeontology");
		classCodes.put("76.4.5",	"Depositional environments");
		classCodes.put("76.4.4",	"Sediments and sedimentary processes");
		classCodes.put("76.4.3",	"Coasts, reefs and atolls");
		classCodes.put("76.4.2",	"Regional and general");
		classCodes.put("76.4.1",	"Apparatus and methods");
		classCodes.put("77",		"GEOMECHANICS ABSTRACTS");
		classCodes.put("76",		"OCEANOGRAPHIC LITERATURE REVIEW");
		classCodes.put("74",		"INTERNATIONAL DEVELOPMENT ABSTRACTS");
		classCodes.put("73",		"ECOLOGICAL ABSTRACTS");
		classCodes.put("72",		"GEOLOGICAL ABSTRACTS");
		classCodes.put("71",		"GEOGRAPHICAL ABSTRACTS: PHYSICAL GEOGRAPHY");
		classCodes.put("70",		"GEOGRAPHICAL ABSTRACTS: HUMAN GEOGRAPHY");
		classCodes.put("71.8.6.3",	"GIS: software and systems");
		classCodes.put("71.8.6.2",	"GIS: error, accuracy, quality, legislation");
		classCodes.put("71.8.6.1",	"GIS: methodology");
		classCodes.put("73.3.4.7",	"Freshwater ecology: distributions and systematics");
		classCodes.put("73.3.4.6",	"Freshwater ecology: community structure and function");
		classCodes.put("73.3.4.5",	"Freshwater ecology: interspecific interactions");
		classCodes.put("73.3.4.4",	"Freshwater ecology: production and nutrients");
		classCodes.put("73.3.4.3",	"Freshwater ecology: population ecology");
		classCodes.put("73.3.4.2",	"Freshwater ecology: autecology and behaviour");
		classCodes.put("73.3.4.1",	"Freshwater ecology: general studies");
		classCodes.put("73.7.6",	"Radiation");
		classCodes.put("73.7.5",	"Air pollution");
		classCodes.put("73.7.4",	"Eutrophication and thermal pollution");
		classCodes.put("73.7.3",	"Heavy metals");
		classCodes.put("73.7.2",	"Oil and chemical pollution");
		classCodes.put("73.7.1",	"General pollution");
		classCodes.put("77.11.2",	"Fracture mechanics");
		classCodes.put("77.11.1",	"Snow and ice mechanics");
		classCodes.put("77.3.10",	"Identification and classification");
		classCodes.put("72.8.8",	"Palaeoecology and palaeobiogeography");
		classCodes.put("72.8.7",	"Micropalaeontology");
		classCodes.put("70.10.7",	"Participation and Decision-Making");
		classCodes.put("72.8.6",	"Vertebrates");
		classCodes.put("70.10.6",	"Planning Law and Practice");
		classCodes.put("72.8.5",	"Invertebrates");
		classCodes.put("70.10.5",	"Housing");
		classCodes.put("72.8.4",	"Palaeobotany");
		classCodes.put("70.10.4",	"Social Planning");
		classCodes.put("72.8.3",	"Precambrian life");
		classCodes.put("70.10.3",	"Sub-regional and Structure Planning");
		classCodes.put("72.8.2",	"Ichnology");
		classCodes.put("70.10.2",	"Regional and Economic Planning");
		classCodes.put("72.8.1",	"General palaeontology and evolution");
		classCodes.put("70.10.1",	"National and Land-Use Planning");
		classCodes.put("76.14.6",	"Biotechnology");
		classCodes.put("76.14.5",	"Information systems and mapping");
		classCodes.put("76.14.4",	"Navigation and positioning systems");
		classCodes.put("76.14.3",	"Remote sensing");
		classCodes.put("76.14.2",	"Vessels");
		classCodes.put("76.14.1",	"Multidisciplinary field equipment");
		classCodes.put("71.1.5",	"International programmes");
		classCodes.put("71.1.4",	"Modelling and numerical methods");
		classCodes.put("71.9",		"GENERAL TEXTS");
		classCodes.put("71.1.3",	"Biogeochemical cycles");
		classCodes.put("71.8",		"REMOTE SENSING, GIS AND MAPPING");
		classCodes.put("71.1.2",	"Pollution");
		classCodes.put("71.7",		"METEOROLOGY AND CLIMATOLOGY");
		classCodes.put("71.1.1",	"Global change");
		classCodes.put("71.6",		"HYDROLOGY");
		classCodes.put("71.5",		"SOILS");
		classCodes.put("71.4",		"SEDIMENTOLOGY");
		classCodes.put("71.3",		"THE QUATERNARY");
		classCodes.put("71.2",		"LANDFORMS");
		classCodes.put("71.1",		"SYNOPTIC GEOGRAPHY");
		classCodes.put("70.2.6",	"GIS, Remote Sensing");
		classCodes.put("70.2.5",	"Planning - General");
		classCodes.put("70.2.4",	"Education");
		classCodes.put("77.6.3",	"Modelling and numerical methods");
		classCodes.put("77.6.2",	"Stress analysis");
		classCodes.put("70.2.3",	"Social Geography - General");
		classCodes.put("77.6.1",	"Rock block analysis");
		classCodes.put("70.2.2",	"Spatial Analysis, Location Theory");
		classCodes.put("70.2.1",	"Economic Geography - General");
		classCodes.put("72.15.3",	"Water resources");
		classCodes.put("72.15.2",	"Non-metals");
		classCodes.put("72.15.1",	"Metals");
		classCodes.put("71.4.10",	"Applied sedimentology");
		classCodes.put("76.5.19",	"Bacteriology, virology and parasitology");
		classCodes.put("76.5.18",	"Birds");
		classCodes.put("76.5.17",	"Marine mammals");
		classCodes.put("76.5.16",	"Macroalgal, seagrass and mangrove communities");
		classCodes.put("76.5.15",	"Estuarine, lagoonal and marsh communities");
		classCodes.put("73.4.6.7",	"Plant ecology: distributions and systematics");
		classCodes.put("76.5.14",	"Benthic and hydrothermal communities");
		classCodes.put("73.4.6.6",	"Plant ecology: communities");
		classCodes.put("76.5.13",	"Reef communities");
		classCodes.put("73.4.6.5",	"Plant ecology: interspecific interactions");
		classCodes.put("73.4.6.4",	"Plant ecology: production and nutrients");
		classCodes.put("73.4.6.3",	"Plant ecology: population ecology");
		classCodes.put("73.4.6.2",	"Plant ecology: autecology");
		classCodes.put("73.4.6.1",	"Plant ecology: general");
		classCodes.put("74.1.9",	"Rural finance");
		classCodes.put("74.1.8",	"Irrigated agriculture");
		classCodes.put("74.1.7",	"Information systems, climatic and soil conditions");
		classCodes.put("74.1.6",	"Agricultural research and extension");
		classCodes.put("74.1.5",	"Pest, disease and weed control");
		classCodes.put("74.1.4",	"Agroforestry and intercropping");
    }
    
    public Hashtable getAuthorityCodes()
    {
    	return null;
    }
    
    public String getTreatmentTitle(String mTreatmentCode)
    {
    	return null;
    }
}