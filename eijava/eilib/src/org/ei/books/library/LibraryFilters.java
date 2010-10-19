package org.ei.books.library;

public class LibraryFilters
{
	private static LibraryFilters uniqueInstance;

	public static synchronized LibraryFilters getInstance() throws Exception
	{
		if (uniqueInstance == null)
		{
			uniqueInstance = new LibraryFilters();
		}

		return uniqueInstance;
	}

	public final String[] CIV = { "Civil and Structural Engineering",
			"Environmental Science (General)", "Environmental Engineering",
			"Energy (General)", "Waste Management and Disposal",
			"Geotechnical Engineering and Engineering Geology",
			"Geography, Planning and Development",
			"Water Science and Technology",
			"Business, Management and Accounting (General)",
			"Management, Monitoring, Policy and Law",
			"Public Health and Health Policy", "Mechanical Engineering",
			"Mechanics of Materials", "Pollution", "Geology",
			"Industrial and Manufacturing Engineering",
			"Safety, Risk, Reliability and Quality", "Transportation",
			"Automotive Engineering", "Electrical and Electronic Engineering",
			"Energy Engineering and Power Technology",
			"Fluid Flow and Transfer Processes", "Metals and Alloys",
			"Renewable Energy, Sustainability and the Environment",
			"Aerospace Engineering", "Agronomy and Crop Science",
			"Geochemistry and Petrology", "Global and Planetary Change",
			"Health, Toxicology and Mutagenesis", "Law",
			"Nuclear Energy and Engineering", "Ocean Engineering",
			"Organizational Behavior and Human Resource Management",
			"Safety Research" };

	public final String[] COM = { "Software", "Computer Science Applications",
			"Information Systems", "Computational Theory and Mathematics",
			"Management of Technology and Innovation",
			"Computer Graphics and Computer-Aided Design",
			"Computer Networks and Communications", "Artificial Intelligence",
			"Mathematics (General)", "Human-Computer Interaction",
			"Business, Management and Accounting (General)",
			"Electrical and Electronic Engineering",
			"Hardware and Architecture", "Fluid Flow and Transfer Processes",
			"Logic", "Computer Vision and Pattern Recognition",
			"Psychology (General)", "Applied Mathematics",
			"Biochemistry, Genetics and Molecular Biology (General)",
			"Biomedical Engineering",
			"Economics, Econometrics and Finance (General)",
			"Geometry and Topology", "Information Systems and Management",
			"Management Information Systems", "Signal Processing",
			"Social Psychology", "Statistics, Probability and Uncertainty",
			"Theoretical Computer Science", "Algebra and Number Theory",
			"Cognitive Neuroscience", "Computers in Earth Sciences",
			"Decision Sciences (General)",
			"Ecology, Evolution, Behavior and Systematics",
			"Safety, Risk, Reliability and Quality", "Strategy and Management" };

	public final String[] SEC = { "Computer Networks and Communications",
			"Software", "Management of Technology and Innovation",
			"Computer Science Applications", "Hardware and Architecture",
			"Computational Theory and Mathematics", "Information Systems",
			"Safety Research", "Business, Management and Accounting (General)",
			"Electrical and Electronic Engineering", "Education", "Law",
			"Information Systems and Management", "Marketing",
			"Signal Processing", "Strategy and Management", "Transportation",
			"Accounting", "Aerospace Engineering", "Applied Mathematics",
			"Artificial Intelligence",
			"Economics, Econometrics and Finance (General)",
			"Management Information Systems",
			"Management Science and Operations Research",
			"Mechanical Engineering" };

	public final String[] CHE = { "Petrochemical Engineering",
			"Chemical Health and Safety", "Industrial Health and Safety",
			"Environmental Engineering", "Colloid and Surface Chemistry",
			"Geochemistry and Petrology", "Fluid Flow and Transfer Processes",
			"Geology", "Geotechnical Engineering and Engineering Geology",
			"Process Chemistry and Technology", "Pipeline Technology",
			"Catalysis", "Filtration and Separation", "Geophysics",
			"Industrial and Manufacturing Engineering",
			"Oil Shales and Tar Sands", "Pharmaceutical Science",
			"Safety, Risk, Reliability and Quality", "Biotechnology",
			"Physical and Theoretical Chemistry", "Process Control",
			"Process Design", "Production Management",
			"Surfaces and Interfaces", "Bioengineering",
			"Manufacturing and Instrumentation", "Materials Science",
			"Safety Research", "Biochemistry", "Biomedical Engineering",
			"Control Engineering", "Control of Electrical Systems",
			"Design Engineering", "Fuel Technology", "Materials Chemistry" };

	public final String[] ELE = { "Communications and Signal Processing",
			"Electromagnetics", "Microelectronics",
			"Computer Networks and Communications",
			"Electronic, Optical and Magnetic Materials", "Software",
			"Hardware and Architecture", "Control and Systems Engineering",
			"Signal Processing", "Telecommunications", "Circuit Design",
			"Optical Engineering / Optics",
			"Atomic and Molecular Physics, and Optics", "Embedded Systems",
			"Industrial and Manufacturing Engineering",
			"Electrical Power and Distribution",
			"Management of Technology and Innovation",
			"Manufacturing and Instrumentation", "Biomedical Engineering",
			"Computer Graphics and Computer-Aided Design",
			"Control of Electrical Systems",
			"Energy Engineering and Power Technology",
			"Computational Mechanics",
			"Computer Vision and Pattern Recognition",
			"Electronic Devices and Materials",
			"Electronics Manufacturing and Testing", "Applied Mathematics",
			"Circuit Theory and Analysis", "Computer Interfacing",
			"Control Engineering", "Information Systems",
			"Safety, Risk, Reliability and Quality",
			"Computer Science Applications", "Audio Electronics",
			"Computational Theory and Mathematics",
			"Electrical Machines and Drives" };

	public final String[] MAT = { "Industrial and Manufacturing Engineering",
			"Safety, Risk, Reliability and Quality", "Mechanics of Materials",
			"Civil and Structural Engineering", "Automotive Engineering",
			"Marine Engineering", "Electrical and Electronic Engineering",
			"Materials Engineering", "Production Management",
			"Metals and Alloys", "Materials Science",
			"Metallurgy and Metals Processing", "Aerospace Engineering",
			"Control and Systems Engineering", "Naval Architecture",
			"Ocean Engineering", "Surfaces, Coatings and Films",
			"Electronic, Optical and Magnetic Materials",
			"Fluid Flow and Transfer Processes", "Design Engineering",
			"Plant Maintenance", "Polymers and Plastics",
			"Aeronautical Engineering", "Biomedical Engineering",
			"Chemical Engineering", "Environmental Engineering",
			"Environmental Science (General)",
			"Quality Systems / Quality Assurance", "Safety Research",
			"Aircraft Design", "Ceramics and Composites",
			"Surfaces and Interfaces", "Computer Aided Design",
			"Industrial Health and Safety",
			"Management of Technology and Innovation" };
}
