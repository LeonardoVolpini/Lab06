package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO meteoDAO;
	private List<Citta> soluzioneFinale;
	private float costoSoluzMigliore;
	private int giorniCons;
	private boolean trovato;
	
	public Model() {
		this.meteoDAO= new MeteoDAO();
		this.giorniCons=1;
		trovato=true;
		this.soluzioneFinale= new ArrayList<>();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		return this.meteoDAO.getUmiditaMediaMese(mese);
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		List<Citta> citta= this.meteoDAO.getAllCitta();
		for (Citta c : citta) {
			c.setRilevamenti(this.meteoDAO.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		List<Citta> parziale = new ArrayList<Citta>();
		ricorsione(citta,parziale);
		String ris= this.SoluzStringa(soluzioneFinale);
		return ris;
	}
	
	private void ricorsione(List<Citta> citta, List<Citta> parziale) {
		if (parziale.size()==(this.NUMERO_GIORNI_TOTALI)) { //terminale
			//CHECK SE E' PREZZO MIGLIORE
			if (trovato) { //prima soluzione
				this.soluzioneFinale.addAll(parziale);
				this.costoSoluzMigliore=this.costoSoluzione(soluzioneFinale);
				trovato=false;
			} else {
				if (this.costoSoluzione(parziale)<this.costoSoluzMigliore) {
					this.soluzioneFinale.clear();
					this.soluzioneFinale.addAll(parziale);
					this.costoSoluzMigliore=this.costoSoluzione(this.soluzioneFinale);
				}
			}
		} 
		else {
			for (Citta c : citta) {
				if (isValid(parziale,c,citta)) {
					parziale.add(c);
					c.increaseCounter();
					ricorsione(citta,parziale);
					
					//BACKTRACKING
					parziale.remove(parziale.size()-1);
					c.decreaseCounter();
					}
				}
		}
	}
	
	private boolean isValid(List<Citta>parziale, Citta city, List<Citta> citta) {
		if(parziale.size()==0)
			return true;
		
		city.increaseCounter();
		//NESSUNA CITTA CON PIU DI 6 GIORNI
		for (Citta c : citta) {
			if (c.getCounter()>NUMERO_GIORNI_CITTA_MAX) {
				c.decreaseCounter();
				return false;
			}	
		}
		
		//TUTTE LE CITTA PRESENTI NELLA SOLUZIONE
		if (parziale.size()>=12) {
			for (Citta c: citta) {
				if (c.getCounter()==0) {
					city.decreaseCounter();
					return false;
				}
			}
		}

		//CHECK SUI GIORNI MINIMI CONSECUTIVI
		if (!parziale.isEmpty())
			if (city.equals(parziale.get(parziale.size()-1))) 
				giorniCons++;
			else {
				if (parziale.size()>2) {
					if(!parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2))) 
						return false;
					giorniCons=1;
				} else
					return false;
			}
		/*for (int i=0; i<parziale.size(); i++) {
			if (i!=0) {
				if (parziale.get(i).equals(parziale.get(i-1)))
					this.giorniCons++;
				else {
					if (this.giorniCons<NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN)
						return false;
					this.giorniCons=1;
				}
			}
		}*/
		
		city.decreaseCounter();
		return true;
	}
	
	public float costoSoluzione (List<Citta> soluzione) {
		float c =0;
		for (int i=0; i<soluzione.size(); i++) {
			if (i!=0 && soluzione.get(i).equals(soluzione.get(i-1)))
				c+= COST;
			c = c + soluzione.get(i).getRilevamenti().get(i).getUmidita();
		}
		return c;
	}

	public String SoluzStringa(List<Citta> soluzione) {
		String s ="";
		int i=1;
		for (Citta c : soluzione) {
			s+= i+") "+c.getNome()+"\n";
			i++;
		}
		s+="ed il costo totale e' di: "+this.costoSoluzMigliore+" euro";
		return s;
	}
	
	
}
