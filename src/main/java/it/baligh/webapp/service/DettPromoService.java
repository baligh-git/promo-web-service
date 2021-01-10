package it.baligh.webapp.service;

import java.util.List;

import it.baligh.webapp.entities.DettPromo;
 
public interface DettPromoService
{	
	List<DettPromo> SelDettPromoByCodFid(String CodFid);
	
	List<DettPromo> SelDettPromoByCode(String Codice);
	
}
