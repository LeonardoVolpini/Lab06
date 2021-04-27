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
	private List<Citta> citta;
	private double costoSoluz;
	
	public Model() {
		this.meteoDAO= new MeteoDAO();
		citta= this.meteoDAO.getAllCitta();
	}

	public List<Citta> getAllCitta(){
		return this.citta;
	}
	
	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		return this.meteoDAO.getUmiditaMediaMese(mese);
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		this.soluzioneFinale=null;
		for (Citta c : citta) {
			c.setRilevamenti(this.meteoDAO.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		List<Citta> parziale = new ArrayList<Citta>();
		ricorsione(parziale,0);
		String ris= this.SoluzStringa(soluzioneFinale);
		return ris;
	}
	
	private void ricorsione(List<Citta> parziale, int livello) {
		if (livello==NUMERO_GIORNI_TOTALI) { //terminale
			//CHECK SE E' PREZZO MIGLIORE
			double costo= this.costoSoluzione(parziale);
			if (this.soluzioneFinale==null || costo<this.costoSoluzione(soluzioneFinale)) { //null per il primo caso
				System.out.format("%f %s\n", costo, parziale);
				this.soluzioneFinale=new ArrayList<Citta>(parziale);
				this.costoSoluz=this.costoSoluzione(soluzioneFinale);
			}
		} 
		else {
			for (Citta c : citta) {
				if (isValid(parziale,c)) {
					parziale.add(c);
					//c.increaseCounter();
					ricorsione(parziale,livello+1);
					
					//BACKTRACKING
					parziale.remove(parziale.size()-1);
					//c.decreaseCounter();
					}
				}
		}
	}
	
	private boolean isValid(List<Citta>parziale, Citta city) {
		//NESSUNA CITTA CON PIU DI 6 GIORNI
		int count=0;
		for (Citta c : parziale) {
			if(c.equals(city))
				count++;
			}
		if (count>=NUMERO_GIORNI_CITTA_MAX)
			return false;
		
		if(parziale.size()==0)
			return true; //primo giorno va sempre bene qualsiasi citta
		
		if (parziale.size()==1 || parziale.size()==2)
			return parziale.get(parziale.size()-1).equals(city); //nel secondo e terzo giorno va bene solo se stessa citta del precedente
		
		
		
		if (parziale.get(parziale.size()-1).equals(city))
			return true; //posso rimanere nella citta uguale alla precedente. Rispetto gia il vincolo di 6 gg max per citta
		
		/*//TUTTE LE CITTA PRESENTI NELLA SOLUZIONE
		if (parziale.size()>=12) {
			for (Citta c: citta) {
				if (c.getCounter()==0) {
					city.decreaseCounter();
					return false;
				}
			}
		}*/

		//CHECK SUI GIORNI MINIMI CONSECUTIVI
		if (parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2))
			&& parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
			return true; //se cambio citta devo essere sicuro che le precedenti 3 siano uguali
		
		return false;
	}
	
	public Double costoSoluzione (List<Citta> soluzione) {
		double c =0.0;
		for (int i=1; i<=NUMERO_GIORNI_TOTALI; i++) {
			Citta citta= soluzione.get(i-1);
			c = c + (double)citta.getRilevamenti().get(i-1).getUmidita();
		}
		for (int i=2; i<=NUMERO_GIORNI_TOTALI; i++) {
			if (soluzione.get(i-1).equals(soluzione.get(i-2)))
				c+= COST;
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
		s+="ed il costo totale e' di: "+this.costoSoluz+" euro";
		return s;
	}
	
	
}
