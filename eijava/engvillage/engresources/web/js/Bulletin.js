function viewBulletin(url){

	w_window = window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=750,height=600');
	w_window.focus();
}
var db;
var cy;
function refreshCategories(){

	var frm = document.forms[0];
	var dbgroup = frm.db;
	var sval = '';
	var litcr = frm.litcr.value + ';';
	var patcr = frm.patcr.value + ';';
	
	
	if(dbgroup[0].checked){
		db = '1';
		sval = litcr;
	}
	else if (dbgroup[1].checked){
		db = '2';
		sval = patcr;
	}
	
	frm.cy.options.length = 0;
	
	arrCartridges = new Array();
	
      begin=0;
      end=sval.indexOf(";");
      tokenCount = 0;
         
      if(sval.indexOf(";",begin)>0){
      
      while(sval.indexOf(";",begin)>0) {
      arrCartridges[tokenCount] =  sval.substring(begin,end);
      tokenCount += 1;
      begin=end+1;
      end=sval.indexOf(";",begin);
      arrCartridges.length = tokenCount;
      	}
      }  else {
      	arrCartridges[0] = sval;
      }
      
      
      
 	if(db == '1') {//LIT
		for(var i=0; i<arrCartridges.length+1; i++){
		cy = arrCartridges[i];
		
		    if(cy == 'A')
				frm.cy.options[i]= new Option("Automotive","automotive");	
			else if (cy == 'CZL')
				frm.cy.options[i]= new Option("Catalysts/Zeolites","catalysys-zeolites:catalysts-zeolites");	
			else if (cy == 'FRL')
				frm.cy.options[i]= new Option("Fuel Reformulation","fuel_reformation:fuel_reformulation");
			else if (cy == 'HE')
				frm.cy.options[i]= new Option("Health and Environment","health_environment");
			else if (cy == 'NG')
				frm.cy.options[i]= new Option("Natural Gas","natural_gas");
			else if (cy == 'OCL')
				frm.cy.options[i]= new Option("Oilfield Chemicals","oilfield_chemicals");
			else if (cy == 'PRP')
				frm.cy.options[i]= new Option("Petroleum Refining and Petrochemicals","petroleum_refining_petrochemicals");
			else if (cy == 'PS_L')
				frm.cy.options[i]= new Option("Petroleum Substitutes","petroleum_substitutes");
			else if (cy == 'TS')
				frm.cy.options[i]= new Option("Transportation and Storage","transportation_storage");
			else if (cy == 'TL')
				frm.cy.options[i]= new Option("Tribology","tribology");	
		}
		
	} else if(db == '2'){
	
	for(var i=0; i<arrCartridges.length+1; i++){
		
		cy = arrCartridges[i];
		
			if (cy == 'CZP')
				frm.cy.options[i]= new Option("Catalysts/Zeolites","catalysys-zeolites:catalysts-zeolites");	
			else if (cy == 'CP')
				frm.cy.options[i]= new Option("Chemical Products","chemical_products");	
			else if (cy == 'ETS')
				frm.cy.options[i]= new Option("Environment, Transport and Storage","environment_transport_storage");
			else if (cy == 'FRP')
				frm.cy.options[i]= new Option("Fuel Reformulation","fuel_reformation:fuel_reformulation");
			else if (cy == 'OCP')
				frm.cy.options[i]= new Option("Oilfield Chemicals","oilfield_chemicals");
			else if (cy == 'PP')
				frm.cy.options[i]= new Option("Petroleum Processes","petroleum_processes");
			else if (cy == 'PSP')
				frm.cy.options[i]= new Option("Petroleum and Specialty Products     ","petroleum_speciality_products");
			else if (cy == 'PS_P')
				frm.cy.options[i]= new Option("Petroleum Substitutes","petroleum_substitutes");
			else if (cy == 'POL')
				frm.cy.options[i]= new Option("Polymers","polymers");
			else if (cy == 'TP')
				frm.cy.options[i]= new Option("Tribology","tribology");
	}
		
	}
		frm.yr.options[10] = new Option("2001","2001");
		frm.yr.options[9] = new Option("2002","2002");
		frm.yr.options[8] = new Option("2003","2003");
		frm.yr.options[7] = new Option("2004","2004");
		frm.yr.options[6] = new Option("2005","2005");
		frm.yr.options[5] = new Option("2006","2006");
		frm.yr.options[4] = new Option("2007","2007");
		frm.yr.options[3] = new Option("2008","2008");
		frm.yr.options[2] = new Option("2009","2009");
		frm.yr.options[1] = new Option("2010","2010");
		frm.yr.options[0] = new Option("2011","2011");
		frm.yr.selectedIndex = 0;
}
function refreshYears(){

	var frm = document.forms[0];
	var category = frm.cy[frm.cy.selectedIndex].value;
	var selectedyr = frm.yr.selectedIndex;
	
	
	frm.yr.options.length = 0;
	
	
	if(category =='polymers'){
		frm.yr.options[1] = new Option("2001","2001");
		frm.yr.options[0] = new Option("2002","2002");
		frm.yr.selectedIndex = 0;
	} else {
		frm.yr.options[10] = new Option("2001","2001");
		frm.yr.options[9] = new Option("2002","2002");
		frm.yr.options[8] = new Option("2003","2003");
		frm.yr.options[7] = new Option("2004","2004");
		frm.yr.options[6] = new Option("2005","2005");
		frm.yr.options[5] = new Option("2006","2006");
		frm.yr.options[4] = new Option("2007","2007");
		frm.yr.options[3] = new Option("2008","2008");
		frm.yr.options[2] = new Option("2009","2009");
		frm.yr.options[1] = new Option("2010","2010");
		frm.yr.options[0] = new Option("2011","2011");
		frm.yr.selectedIndex = selectedyr;
	}
	
					
}

	

				
				
	
	
	



