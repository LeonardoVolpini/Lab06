package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		String sql = "SELECT Umidita, Data FROM situazione WHERE Localita=? AND MONTH(DATA)=? ORDER BY data ASC";
		List<Rilevamento> ris= new ArrayList<Rilevamento>();
		try {
			Connection conn= ConnectDB.getConnection();
			PreparedStatement st= conn.prepareStatement(sql);
			st.setString(1, localita);
			st.setInt(2, mese);
			ResultSet rs= st.executeQuery();
			while (rs.next()) {
				Rilevamento r = new Rilevamento(localita,rs.getDate("Data"),rs.getInt("Umidita"));
				ris.add(r);
			}
			conn.close();
		} catch(SQLException e) {
			throw new RuntimeException("Errore DB",e);
		}
		return ris;
	}

	public List<Citta> getAllCitta() {
		String sql= "SELECT DISTINCT Localita FROM situazione";
		List<Citta> citta= new ArrayList<Citta>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st= conn.prepareStatement(sql);
			ResultSet rs= st.executeQuery();
			while (rs.next()) {
				Citta c= new Citta(rs.getString("Localita"));
				citta.add(c);
			}
			conn.close();
			return citta;
		}catch(SQLException e) {
			throw new RuntimeException("Errore DB",e);
		}
	}
	
	public String getUmiditaMediaMese (int mese) {
		String sql= "SELECT Localita, AVG(Umidita) AS media "
				+ "FROM situazione "
				+ "WHERE MONTH(DATA)=? "
				+ "GROUP BY Localita";
		String s="";
		try {
			Connection conn= ConnectDB.getConnection();
			PreparedStatement st= conn.prepareStatement(sql);
			st.setInt(1, mese);
			ResultSet rs= st.executeQuery();
			while (rs.next() ) {
				s+=rs.getString("Localita")+" "+rs.getFloat("media")+"\n";
			}
			conn.close(); 
		} catch(SQLException e) {
			throw new RuntimeException("Errore DB",e);
		}
		return s;
	}
}
