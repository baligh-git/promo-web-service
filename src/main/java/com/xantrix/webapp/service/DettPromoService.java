package com.xantrix.webapp.service;

import java.util.List;

import com.xantrix.webapp.entities.DettPromo;
 
public interface DettPromoService
{	
	List<DettPromo> SelDettPromoByCodFid(String CodFid);
	
	List<DettPromo> SelDettPromoByCode(String Codice);
	
}
