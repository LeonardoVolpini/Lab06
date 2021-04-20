package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO meteoDAO;
	private List<String> soluzione;
	
	public Model() {
		this.meteoDAO= new MeteoDAO();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		return this.meteoDAO.getUmiditaMediaMese(mese);
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		//this.meteoDAO.ge
		return "TODO!";
	}
	
	private void ricorsione(int livello, List<String> parziale) {
		if (livello==this.NUMERO_GIORNI_TOTALI) { //terminale
			if (isValid(parziale)) {
				this.soluzione= new ArrayList<String>(parziale);
			}
			
		} else {
			for () {
				
			}
		}
		
	}
	
	private boolean isValid(List<String>parziale) {
		return false;
	}

}
