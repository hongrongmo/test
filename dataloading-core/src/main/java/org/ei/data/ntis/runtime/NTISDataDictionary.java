package org.ei.data.ntis.runtime;

import java.util.Hashtable;

import org.ei.domain.DataDictionary;

public class NTISDataDictionary implements DataDictionary {

    private Hashtable<String, String> classCodes;

    public String getClassCodeTitle(String classCode) {
        if (classCode != null) {
            return (String) classCodes.get(classCode.toUpperCase());
        } else
            return null;
    }

    public Hashtable<String, String> getClassCodes() {
        return this.classCodes;
    }

    public Hashtable<String, String> getTreatments() {
        return null;
    }

    public NTISDataDictionary() {

        // System.out.println(" new NTIS DataDictionary");
        classCodes = new Hashtable<String, String>();
        classCodes.put("71A", "Ablative Materials and Ablation");
        classCodes.put("63A", "Acoustic Detection");
        classCodes.put("46A", "Acoustics");
        classCodes.put("71B", "Adhesives and Sealants");
        classCodes.put("70", "Administration and Management");
        classCodes.put("51A", "Aerodynamics");
        classCodes.put("51B", "Aeronautics");
        classCodes.put("51F", "Aeronautics -Test Facilities and Equipment");
        classCodes.put("51", "Aeronautics and Aerodynamics");
        classCodes.put("55A", "Aeronomy");
        classCodes.put("98A", "Agricultural Chemistry");
        classCodes.put("98B", "Agricultural Economics");
        classCodes.put("98C", "Agricultural Equipment, Facilities,and Operations");
        classCodes.put("98G", "Agricultural Resource Surveys");
        classCodes.put("98", "Agriculture and Food");
        classCodes.put("98D", "Agronomy, Horticulture,and Plant Pathology");
        classCodes.put("75A", "Air and Space-Launched Missiles");
        classCodes.put("68A", "Air Pollution and Control");
        classCodes.put("85A", "Air Transportation");
        classCodes.put("51C", "Aircraft");
        classCodes.put("72B", "Algebra, Analysis, Geometry,and Mathematical Logic");
        classCodes.put("79A", "Ammunition, Explosives,and Pyrotechnics");
        classCodes.put("99A", "Analytical Chemistry");
        classCodes.put("57A", "Anatomy");
        classCodes.put("98E", "Animal Husbandry and Veterinary Medicine");
        classCodes.put("49A", "Antennas");
        classCodes.put("74A", "Antiaircraft Defense Systems");
        classCodes.put("74B", "Antimissile Defense Systems");
        classCodes.put("74C", "Antisubmarine Warfare");
        classCodes.put("89B", "Architectural Design and Environmental Engineering");
        classCodes.put("79B", "Armor");
        classCodes.put("54A", "Astrogeology");
        classCodes.put("84A", "Astronautics");
        classCodes.put("54", "Astronomy and Astrophysics");
        classCodes.put("54D", "Astronomy and Astrophysics Cosmic Ray Research");
        classCodes.put("54B", "Astronomy and Celestial Mechanics");
        classCodes.put("54C", "Astrophysics");
        classCodes.put("55", "Atmospheric Sciences");
        classCodes.put("55B", "Atmospheric Sciences Dynamic Meteorology");
        classCodes.put("51E", "Avionics");
        classCodes.put("96F", "Banking and Finance");
        classCodes.put("99D", "Basic and Synthetic Chemistry");
        classCodes.put("97M", "Batteries and Components");
        classCodes.put("92", "Behavior and Society");
        classCodes.put("57B", "Biochemistry");
        classCodes.put("47D", "Biological Oceanography");
        classCodes.put("95C", "Biomedical Instrumentation and Bioengineering");
        classCodes.put("95", "Biomedical Technology and Human Factors Engineering");
        classCodes.put("95F", "Bionics and Artificial Intelligence");
        classCodes.put("79C", "Bombs");
        classCodes.put("57C", "Botany");
        classCodes.put("89C", "Building Construction Management and Techniques");
        classCodes.put("89G", "Building Construction Materials, Components, and Equipment");
        classCodes.put("89H", "Building Equipment, Furnishings, and Maintenance");
        classCodes.put("89", "Building Industry Technology");
        classCodes.put("89E", "Building Standards and Codes");
        classCodes.put("89D", "Building Structural Analyses");
        classCodes.put("96G", "Business - Foreign Industry Development and Economics");
        classCodes.put("96C", "Business - International Commerce, Marketing, and Economics");
        classCodes.put("96", "Business and Economics");
        classCodes.put("96A", "Business Domestic Commerce, Marketing,and Economics");
        classCodes.put("71C", "Carbon and Graphite");
        classCodes.put("48I", "Cartography");
        classCodes.put("71D", "Ceramics, Refractories, and Glass");
        classCodes.put("74D", "Chemical, Biological,and Radiological Warfare");
        classCodes.put("99", "Chemistry");
        classCodes.put("99F", "Chemistry - Physical and Theoretical");
        classCodes.put("99E", "Chemistry - Photo and Radiation");
        classCodes.put("49B", "Circuits");
        classCodes.put("50B", "Civil Engineering");
        classCodes.put("50", "Civil Engineering (Heading)");
        classCodes.put("50C", "Civil Engineering Construction Equipment,Materials, and Supplies");
        classCodes.put("57D", "Clinical Chemistry");
        classCodes.put("57E", "Clinical Medicine");
        classCodes.put("71E", "Coatings, Colorants, and Finishes");
        classCodes.put("79D", "Combat Vehicles");
        classCodes.put("81A", "Combustion and Ignition");
        classCodes.put("81B", "Combustion, Engines and Propellants Electric and Ion Propulsion");
        classCodes.put("81", "Combustion, Engines, and Propellants");
        classCodes.put("45C", "Common Carrier and Satellite");
        classCodes.put("45", "Communication");
        classCodes.put("45E", "Communication Graphics");
        classCodes.put("45G", "Communication and Information Theory");
        classCodes.put("45A", "Communication Policies, Regulations,and Studies");
        classCodes.put("45D", "Communications - Sociopolitical");
        classCodes.put("45F", "Communications - Verbal");
        classCodes.put("71F", "Composite Materials");
        classCodes.put("41A", "Computer Aided design (CAD)");
        classCodes.put("41B", "Computer Aided Manufacturing (CAM)");
        classCodes.put("62C", "Computer Control Systems and Control Theory");
        classCodes.put("62A", "Computer Hardware");
        classCodes.put("62D", "Computer Information Processing Standards");
        classCodes.put("62E", "Computer Information Theory");
        classCodes.put("62B", "Computer Software");
        classCodes.put("62", "Computers, Control, and Information Theory");
        classCodes.put("96D", "Consumer Affairs");
        classCodes.put("71G", "Corrosion and Corrosion Inhibition");
        classCodes.put("57F", "Cytology, Genetics, and Molecular Biology");
        classCodes.put("57G", "Dentistry");
        classCodes.put("63", "Detection and Countermeasures");
        classCodes.put("79E", "Detonations, explosion effects, and ballistics");
        classCodes.put("47B", "Dynamic Oceanography");
        classCodes.put("57H", "Ecology");
        classCodes.put("92D", "Education, Law, and Humanities");
        classCodes.put("71H", "Elastomers");
        classCodes.put("97I", "Electric Power Production");
        classCodes.put("97E", "Electric Power Transmission");
        classCodes.put("63B", "Electromagnetic and Acoustic Countermeasures");
        classCodes.put("49C", "Electromechanical Devices");
        classCodes.put("49D", "Electron Tubes");
        classCodes.put("49G", "Electronic Resistive, Capacitive, and Inductive Components");
        classCodes.put("49", "Electrotechnology");
        classCodes.put("97", "Energy");
        classCodes.put("97R", "Energy Environmental Studies");
        classCodes.put("97G", "Energy Policies, Regulations, and Studies");
        classCodes.put("97A", "Energy Reserves");
        classCodes.put("97B", "Energy Use, Supply, and Demand");
        classCodes.put("97L", "Engine Studies (Energy Related)");
        classCodes.put("41K", "Engineering Materials");
        classCodes.put("94E", "Environmental Engineering");
        classCodes.put("68G", "Environmental Health and Safety");
        classCodes.put("68H", "Environmental Impact Statements");
        classCodes.put("68", "Environmental Pollution and Control");
        classCodes.put("71I", "Fibers and Textiles");
        classCodes.put("98F", "Fisheries and Aquaculture");
        classCodes.put("46B", "Fluid Mechanics");
        classCodes.put("98H", "Food Technology");
        classCodes.put("48D", "Forestry");
        classCodes.put("81C", "Fuel and Propellant Tanks");
        classCodes.put("97F", "Fuel Conversion Processes");
        classCodes.put("97K", "Fuels");
        classCodes.put("77A", "Fusion Devices (Thermonuclear)");
        classCodes.put("48F", "Geology and Geophysics");
        classCodes.put("97P", "Geothermal Energy");
        classCodes.put("85F", "Global Navigation Systems");
        classCodes.put("90D", "Government Inventions - Biology and Medicine");
        classCodes.put("90B", "Government Inventions - Chemistry");
        classCodes.put("90F", "Government Inventions - Electrotechnology");
        classCodes.put("90J", "Government Inventions - Food Technology");
        classCodes.put("90G", "Government Inventions - Instruments");
        classCodes.put("90A", "Government Inventions - Mechanical Devices and Equipment");
        classCodes.put("90E", "Government Inventions - Metallurgy");
        classCodes.put("90C", "Government Inventions - Nuclear Technology");
        classCodes.put("90H", "Government Inventions - Optics and Lasers");
        classCodes.put("90I", "Government Inventions - Ordnance");
        classCodes.put("90", "Government Inventions for Licensing");
        classCodes.put("79G", "Guns");
        classCodes.put("44", "Health Care");
        classCodes.put("44B", "Health Care Agency Administrative and Financial Management");
        classCodes.put("44D", "Health Care Assessment and Quality Assurance");
        classCodes.put("44C", "Health Care Community and Population Characteristics");
        classCodes.put("44T", "Health Care Data and Information Systems");
        classCodes.put("44U", "Health Care Delivery Organization and Management");
        classCodes.put("44R", "Health Care Economics and Sociology");
        classCodes.put("44G", "Health Care Environmental and Occupational Factors");
        classCodes.put("44F", "Health Care forecasting Methodology");
        classCodes.put("44S", "Health Care Legislation and Regulations");
        classCodes.put("44E", "Health Care measurement Methodology");
        classCodes.put("44L", "Health Care Needs and Demands");
        classCodes.put("44H", "Health Care Technology");
        classCodes.put("44N", "Health Care Utilization");
        classCodes.put("44J", "Health Delivery Plans, Projects, and Studies");
        classCodes.put("44P", "Health Education and Manpower Training");
        classCodes.put("44A", "Health Planning Methodology");
        classCodes.put("44M", "Health Resources");
        classCodes.put("44K", "Health Services");
        classCodes.put("44Q", "Health-Related Costs");
        classCodes.put("97J", "Heating and Cooling Systems");
        classCodes.put("50A", "Highway Engineering");
        classCodes.put("82A", "Holography");
        classCodes.put("95D", "Human Factors Engineering");
        classCodes.put("94I", "Hydraulic and Pneumatic Equipment");
        classCodes.put("47G", "Hydrography");
        classCodes.put("48G", "Hydrology and Limnology");
        classCodes.put("57J", "Immunology");
        classCodes.put("94", "Industrial and Mechanical Engineering");
        classCodes.put("94C", "Industrial and Mechanical Engineering Plant Design and Maintenance");
        classCodes.put("94A", "Industrial and Mechanical Engineering Production Planning and Process Controls");
        classCodes.put("94B", "Industrial and Mechanical Engineering Quality Control and Reliability");
        classCodes.put("99B", "Industrial Chemistry and Chemical Process Engineering");
        classCodes.put("94D", "Industrial Job Environment");
        classCodes.put("94K", "Industrial Laboratory and Test Facility Design and Operation");
        classCodes.put("94H", "Industrial Safety Engineering");
        classCodes.put("63C", "Infrared and Ultraviolet Detection");
        classCodes.put("92E", "International Relations");
        classCodes.put("70A", "Inventory Control");
        classCodes.put("71J", "Iron and Iron Alloys");
        classCodes.put("77B", "Isotopes");
        classCodes.put("81D", "Jet and Gas Turbine Engines");
        classCodes.put("41F", "Joining");
        classCodes.put("88C", "Library and Information Science Marketing and User Services");
        classCodes.put("88D", "Library and Information Science Personnel");
        classCodes.put("88", "Library and Information Sciences");
        classCodes.put("88A", "Library and Information Sciences Operations and Planning");
        classCodes.put("88B", "Library Information Systems");
        classCodes.put("95E", "Life Support Systems");
        classCodes.put("74E", "Logistics, Military Facilities, and Supplies");
        classCodes.put("71K", "Lubricants and Hydraulic Fluids");
        classCodes.put("63D", "Magnetic Detection");
        classCodes.put("70C", "Management Information Systems");
        classCodes.put("70B", "Management Practice");
        classCodes.put("84C", "Manned Spacecraft");
        classCodes.put("41I", "Manufacturing Job Environment");
        classCodes.put("41N", "Manufacturing Computer Software");
        classCodes.put("41O", "Manufacturing Domestic Commerce, Marketing, and Economics");
        classCodes.put("41M", "Manufacturing Optics and Lasers");
        classCodes.put("41H", "Manufacturing Plant Design and Maintenance");
        classCodes.put("94G", "Manufacturing Processes and Materials Handling");
        classCodes.put("41D", "Manufacturing Productivity");
        classCodes.put("41G", "Manufacturing Quality Control and Reliability");
        classCodes.put("41P", "Manufacturing Research Program Administration and Technology Transfer");
        classCodes.put("41", "Manufacturing Technology");
        classCodes.put("41J", "Manufacturing Tooling, Machinery, and Tools");
        classCodes.put("41E", "Manufacturing, Planning, Processing, and Control");
        classCodes.put("85G", "Marine and Waterway Transportation");
        classCodes.put("47A", "Marine Engineering");
        classCodes.put("47E", "Marine Geophysics and Geology");
        classCodes.put("71L", "Materials Degradation and Fouling");
        classCodes.put("71", "Materials Sciences");
        classCodes.put("72", "Mathematical Sciences");
        classCodes.put("57", "Medicine and Biology");
        classCodes.put("57I", "Medicine and Biology Electrophysiology");
        classCodes.put("55C", "Meteorological Data Collection, Analysis, and Weather Forecasting");
        classCodes.put("55D", "Meteorological Instruments and Instrument Platforms");
        classCodes.put("85C", "Metropolitan Rail Transportation");
        classCodes.put("57K", "Microbiology");
        classCodes.put("74F", "Military Intelligence");
        classCodes.put("74G", "Military Operations, Strategy, and Tactics");
        classCodes.put("74", "Military Sciences");
        classCodes.put("48A", "Mineral Industries");
        classCodes.put("96E", "Minority Enterprises");
        classCodes.put("97O", "Miscellaneous Energy Conversion and Storage");
        classCodes.put("71M", "Miscellaneous Materials");
        classCodes.put("75B", "Missile Guidance and Control Systems");
        classCodes.put("75C", "Missile Launching and Support Systems");
        classCodes.put("75", "Missile Technology");
        classCodes.put("75D", "Missile Tracking Systems");
        classCodes.put("75E", "Missile Trajectories and Reentry Dynamics");
        classCodes.put("75F", "Missile Warheads and Fuses");
        classCodes.put("48B", "Natural Resource Management");
        classCodes.put("48C", "Natural Resource Surveys");
        classCodes.put("48", "Natural Resources and Earth Sciences");
        classCodes.put("76C", "Navigation and Guidance System Components");
        classCodes.put("76A", "Navigation Control Devices and Equipment");
        classCodes.put("76B", "Navigation Guidance Systems");
        classCodes.put("76D", "Navigation Systems");
        classCodes.put("76", "Navigation, Guidance, and Control");
        classCodes.put("68B", "Noise Pollution and Control");
        classCodes.put("94J", "Nondestructive Testing");
        classCodes.put("71N", "Nonferrous Metals and Alloys");
        classCodes.put("77C", "Nuclear Auxiliary Power Systems");
        classCodes.put("63E", "Nuclear Explosion Detection");
        classCodes.put("77D", "Nuclear Explosions and Devices");
        classCodes.put("77E", "Nuclear Instrumentation");
        classCodes.put("81I", "Nuclear Propulsion");
        classCodes.put("77H", "Nuclear Reactor Engineering and Nuclear Power Plants");
        classCodes.put("77I", "Nuclear Reactor Fuels and Fuel Processing");
        classCodes.put("77J", "Nuclear Reactor Materials");
        classCodes.put("77K", "Nuclear Reactor Physics");
        classCodes.put("77", "Nuclear Science and Technology");
        classCodes.put("97Q", "Nuclear Technology Selected Studies");
        classCodes.put("74H", "Nuclear Warfare");
        classCodes.put("57L", "Nutrition");
        classCodes.put("57M", "Occupational Therapy, Physical Therapy, and Rehabilitation");
        classCodes.put("47", "Ocean Technology and Engineering");
        classCodes.put("47F", "Oceanographic Vessels, Instruments, and Platforms");
        classCodes.put("72E", "Operations Research");
        classCodes.put("63F", "Optical Detection");
        classCodes.put("46C", "Optics and Lasers");
        classCodes.put("49E", "Optoelectronic Devices and Systems");
        classCodes.put("79", "Ordnance");
        classCodes.put("79F", "Ordnance - Fire Control and Bombing Systems");
        classCodes.put("51D", "Parachutes and Decelerators");
        classCodes.put("57N", "Parasitology");
        classCodes.put("74I", "Passive Defense Systems");
        classCodes.put("57O", "Pathology");
        classCodes.put("62F", "Pattern Recognition and Image Processing");
        classCodes.put("63G", "Personnel Detection");
        classCodes.put("57P", "Pest Control");
        classCodes.put("68E", "Pesticides Pollution and Control");
        classCodes.put("57Q", "Pharmacology and Pharmacological Chemistry");
        classCodes.put("82B", "Photographic Techniques and Equipment");
        classCodes.put("82", "Photography and Recording Devices");
        classCodes.put("47C", "Physical and Chemical Oceanography");
        classCodes.put("55E", "Physical Meteorology");
        classCodes.put("46", "Physics");
        classCodes.put("57S", "Physiology");
        classCodes.put("85E", "Pipeline Transportation");
        classCodes.put("46G", "Plasma Physics");
        classCodes.put("71O", "Plastics");
        classCodes.put("99C", "Polymer Chemistry");
        classCodes.put("49F", "Power and Signal Transmission Devices");
        classCodes.put("43A", "Problem Solving for State and Local Governments - Finance");
        classCodes.put("43B", "Problem Solving for State and Local Governments-Economic and Community Development");
        classCodes.put("43F", "Problem Solving for State and Local Governments Environment");
        classCodes.put("43", "Problem Solving Information for State and Local Governments");
        classCodes.put("70G", "Productivity");
        classCodes.put("95A", "Prosthetics and Mechanical Organs");
        classCodes.put("95G", "Protective Equipment");
        classCodes.put("57T", "Psychiatry");
        classCodes.put("92B", "Psychology");
        classCodes.put("70F", "Public Administration and Government");
        classCodes.put("57U", "Public Health and Industrial Medicine");
        classCodes.put("68F", "Radiation Pollution and Control");
        classCodes.put("77F", "Radiation Shielding, Protection, and Safety");
        classCodes.put("45B", "Radio and Television Equipment");
        classCodes.put("63H", "Radio Frequency Detection");
        classCodes.put("46H", "Radio Frequency Waves");
        classCodes.put("77G", "Radioactive Wastes and Radioactivity");
        classCodes.put("57V", "Radiobiology");
        classCodes.put("85I", "Railroad Transportation");
        classCodes.put("81J", "Reciprocating and Rotating Combustion Engines");
        classCodes.put("82C", "Recording Devices");
        classCodes.put("88E", "Reference Materials");
        classCodes.put("71P", "Refractory Metals and Alloys");
        classCodes.put("70E", "Research Program Administration and Technology Transfer");
        classCodes.put("85H", "Road Transportation");
        classCodes.put("41C", "Robotics/Robots");
        classCodes.put("81G", "Rocket Engines and Motors");
        classCodes.put("81H", "Rocket Propellants");
        classCodes.put("79H", "Rockets");
        classCodes.put("63I", "Seismic Detection");
        classCodes.put("49H", "Semiconductor Devices");
        classCodes.put("48H", "Snow, Ice, and Permafrost");
        classCodes.put("92C", "Social Concerns");
        classCodes.put("50D", "Soil and Rock Mechanics");
        classCodes.put("48E", "Soil Sciences");
        classCodes.put("97N", "Solar Energy");
        classCodes.put("46D", "Solid State Physics");
        classCodes.put("68C", "Solid Wastes Pollution and Control");
        classCodes.put("71Q", "Solvents, Cleaners, and Abrasives");
        classCodes.put("84B", "Space Extraterrestrial Exploration");
        classCodes.put("84E", "Space Launch Vehicles and Support Equipment");
        classCodes.put("84F", "Space Safety");
        classCodes.put("84", "Space Technology");
        classCodes.put("84D", "Spacecraft Trajectories and Flight Mechanics");
        classCodes.put("43G", "State and Local Governments - Transportation");
        classCodes.put("43E", "State and Local Government Energy");
        classCodes.put("43C", "State and Local Governments - Human Resources");
        classCodes.put("43D", "State and Local Governments Police, Fire, and Emergency Services");
        classCodes.put("72F", "Statistical Analysis");
        classCodes.put("57W", "Stress Physiology");
        classCodes.put("46E", "Structural Mechanics");
        classCodes.put("75G", "Surface-Launched Missiles");
        classCodes.put("57X", "Surgery");
        classCodes.put("95B", "Tissue Preservation and Storage");
        classCodes.put("94F", "Tooling, Machinery, and Tools");
        classCodes.put("57Y", "Toxicology");
        classCodes.put("85", "Transportation");
        classCodes.put("85D", "Transportation Safety");
        classCodes.put("41L", "Tribology");
        classCodes.put("47H", "Underwater Construction and Habitats");
        classCodes.put("79I", "Underwater Ordnance");
        classCodes.put("75H", "Underwater-Launched Missiles");
        classCodes.put("84G", "Unmanned Spacecraft");
        classCodes.put("91G", "Urban Administration and Planning");
        classCodes.put("91", "Urban and Regional Technology and Development");
        classCodes.put("91D", "Urban and Regional Technology Communications");
        classCodes.put("91J", "Urban Economic Studies");
        classCodes.put("91I", "Urban Emergency Services and Planning");
        classCodes.put("91A", "Urban Environmental Management and Planning");
        classCodes.put("91C", "Urban Fire Services, Law Enforcement, and Criminal Justice");
        classCodes.put("91F", "Urban Health Services");
        classCodes.put("91E", "Urban Housing");
        classCodes.put("91L", "Urban Recreation");
        classCodes.put("91H", "Urban Regional Administration and Planning");
        classCodes.put("91K", "Urban Social Services");
        classCodes.put("91B", "Urban Transportation and Traffic Planning");
        classCodes.put("68D", "Water Pollution and Control");
        classCodes.put("55F", "Weather Modification");
        classCodes.put("71R", "Wood and Paper Products");
        classCodes.put("57Z", "Zoology");
    }

    public Hashtable<String, String> getAuthorityCodes() {
        return null;
    }

    public String getTreatmentTitle(String mTreatmentCode) {
        return null;
    }
}