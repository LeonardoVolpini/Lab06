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
	
	public Model() {
		this.meteoDAO= new MeteoDAO();
		this.costoSoluzMigliore=0;
		//this.giorniCons=1;
		this.soluzioneFinale= new ArrayList<>();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		return this.meteoDAO.getUmiditaMediaMese(mese);
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		List<Citta> citta= this.meteoDAO.getAllCitta();
		Map<Citta,List<Rilevamento>> rilevamenti= new HashMap<>();
		Map<Citta,Integer> giorniCitta= new HashMap<>();
		for (Citta c : citta) {
			rilevamenti.put(c, new ArrayList<Rilevamento>(this.meteoDAO.getAllRilevamentiLocalitaMese(mese, c.getNome())));
			giorniCitta.put(c, 0);
		}
		int tot=0;
		List<Citta> parziale = new ArrayList<Citta>();
		ricorsione(0,citta,parziale,tot,giorniCitta,rilevamenti);
		String ris= this.SoluzStringa(soluzioneFinale);
		return ris;
	}
	
	private void ricorsione(int livello, List<Citta> citta, List<Citta> parziale, int tot, Map<Citta,Integer> giorniCitta, Map<Citta,List<Rilevamento>> rilevamenti) {
		if (livello==(this.NUMERO_GIORNI_TOTALI-1)) { //terminale
			if (isValid(parziale, giorniCitta)) {
				//CHECK SE E' PREZZO MIGLIORE
				if (livello==0) { //prima esecuzione
					this.costoSoluzMigliore=tot;
				} else {
					if (tot<this.costoSoluzMigliore) {
						this.costoSoluzMigliore=tot;
						this.soluzioneFinale.clear();
						this.soluzioneFinale.addAll(parziale);
					}
				}
			}
		} 
		else {
			for (Citta c : citta) {
				parziale.add(c);
				giorniCitta.put(c, giorniCitta.get(c)+1); //oppure aggiono il counter
				tot+= rilevamenti.get(c).get(livello).getUmidita();
				if (livello>1) {
					if (!parziale.get(livello-1).equals(parziale.get(livello-2)))
						tot+=this.COST;
				}
				ricorsione(livello+1,citta,parziale,tot,giorniCitta,rilevamenti);
				parziale.remove(citta);
				
				//BACKTRACKING
				if (livello!=0 ) {
					if (livello>1) {
						if (!parziale.get(livello-1).equals(parziale.get(livello-2)))
							tot-=this.COST;
					}
					tot-= rilevamenti.get(c).get(livello).getUmidita();
					parziale.remove(livello-1);
					giorniCitta.put(c, giorniCitta.get(c)-1);
				}
			}
		}
		
	}
	
	private boolean isValid(List<Citta>parziale, Map<Citta,Integer> giorniCitta) {
		//NESSUNA CITTA CON PIU DI 6 GIORNI
		for (Citta c : giorniCitta.keySet()) {
			if (giorniCitta.get(c)>this.NUMERO_GIORNI_CITTA_MAX)
				return false;
		}
		
		//TUTTE LE CITTA PRESENTI NELLA SOLUZIONE
		for (Citta c: giorniCitta.keySet()) {
			if (giorniCitta.get(c)==0)
				return false;
		}
		
		//CHECK SUI GIORNI MINIMI CONSECUTIVI
		for (int i=1; i<this.NUMERO_GIORNI_TOTALI; i++) {
			if (i!=0) {
				if (parziale.get(i).equals(parziale.get(i-1)))
					this.giorniCons++;
				else {
					if (giorniCons<this.NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN)
						return false;
					this.giorniCons=1;
				}
			} 
			else
				this.giorniCons=1;
		}
		return true;
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
