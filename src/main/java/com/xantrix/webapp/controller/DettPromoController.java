package com.xantrix.webapp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.entities.DettPromo;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.DettPromoService;

@RestController
@RequestMapping("/dettpromo")
public class DettPromoController
{
	private static final Logger logger = LoggerFactory.getLogger(DettPromoController.class);

	@Autowired
	private DettPromoService dettPromoService;

	@RequestMapping(value = "/cerca/fidelity/{idfidelity}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<DettPromo>> listDettPromoByCodFid(@PathVariable("idfidelity") String IdFidelity)
			throws NotFoundException
	{
		logger.info("****** Otteniamo le promozioni della Fidelity: " + IdFidelity + "*******");

		List<DettPromo> dettPromo = dettPromoService.SelDettPromoByCodFid(IdFidelity);

		logger.info("Numero Record: " + dettPromo.size());

		if (dettPromo.size() == 0)
		{

			String ErrMsg = String.format("Non è stata trovata alcuna promozione per la fidelity %s!", IdFidelity);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}

		return new ResponseEntity<List<DettPromo>>(dettPromo, HttpStatus.OK);
	}
	
	//http://localhost:5061/dettpromo/cerca/codice?tipo=1&codice=051203401
	@RequestMapping(value = "/cerca/codice", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<DettPromo>> listDettPromoByCodice(@RequestParam("tipo") int Tipo,
			@RequestParam("codice") String Codice)
			throws NotFoundException
	{
		logger.info("****** Otteniamo le promozioni della Codice: " + Codice + "*******");

		List<DettPromo> dettPromo = dettPromoService.SelDettPromoByCodArt(Codice, Tipo);

		logger.info("Numero Record: " + dettPromo.size());

		if (dettPromo.size() == 0)
		{

			String ErrMsg = String.format("Non è stata trovata alcuna promozione per il codice %s!", Codice);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}

		return new ResponseEntity<List<DettPromo>>(dettPromo, HttpStatus.OK);
	}

	// ------------------- UPDATE DETTPROMO ------------------------------------
	@RequestMapping(value = "/dettpromo/modifica", method = RequestMethod.PUT)
	public ResponseEntity<DettPromo> updateDettPromo(@RequestParam("id") Long Id,
			@RequestParam("oggetto") String Oggetto)
	{
		logger.info("Modifichiamo il dettaglio promo " + Id);

		dettPromoService.UpdDettPromo(Id, Oggetto);

		return new ResponseEntity<DettPromo>(HttpStatus.CREATED);
	}

	// ------------------- DELETE DETTPROMO ------------------------------------
	@RequestMapping(value = "/dettpromo/elimina/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteDettPromo(@PathVariable("id") Long Id)
	{
		logger.info("*******ELIMINAZIONE RIGA PROMO " + Id + "*******");

		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		dettPromoService.DelRowPromo(Id);

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Eliminazione Eseguita Con Successo");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
	}
}
