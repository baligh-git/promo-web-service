package com.xantrix.webapp.controller;

import java.util.List;
import java.util.OptionalDouble;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.entities.DettPromo;
import com.xantrix.webapp.entities.Promo;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.DettPromoService;
import com.xantrix.webapp.service.PromoService;

@RestController
@RequestMapping("/promo")
public class PromoController
{
	private static final Logger logger = LoggerFactory.getLogger(PromoController.class);

	@Autowired
	private PromoService promoService;
	
	@Autowired
	private DettPromoService dettPromoService;
	
	@Autowired
	private ResourceBundleMessageSource errMessage;
	
	
	@RequestMapping(value = "/cerca/prezzo/{codart}", method = RequestMethod.GET)
	public OptionalDouble getPriceCodArt(@PathVariable("codart") String CodArt)  
	{
		OptionalDouble Prezzo = null;
		
		List<DettPromo> dettPromo = dettPromoService.SelDettPromoByCodArt(CodArt, 1);
		
		if (dettPromo != null)
		{
			Prezzo = dettPromo.
					stream().mapToDouble(v -> Double.parseDouble(v.getOggetto())).min();
			
			logger.info("Prezzo Promo Articolo: " + Prezzo);
		
		}
		
		return Prezzo;	
	}
	
	@RequestMapping(value = "/cerca/tutti", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Promo>> listAllPromo()
			throws NotFoundException
	{           
		logger.info("****** Otteniamo tutte le promozioni *******");

		List<Promo> promo = promoService.SelTutti();

		if (promo.isEmpty())
		{
			String ErrMsg = String.format("Non è stata trovata alcuna promozione!");
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}

		logger.info("Numero dei record: " + promo.size());
		
		return new ResponseEntity<List<Promo>>(promo, HttpStatus.OK);
	}

	@RequestMapping(value = "/cerca/id/{idpromo}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Promo> listPromoById(@PathVariable("idpromo") String IdPromo) 
			throws NotFoundException
	{
		logger.info("****** Otteniamo la promozione con Id: " + IdPromo + "*******");

		Promo promo = promoService.SelByIdPromo(IdPromo);
		
		if (promo == null)
		{
			String ErrMsg = String.format("La promozione %s non è stata trovata!", IdPromo);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}
		
		return new ResponseEntity<Promo>(promo, HttpStatus.OK);
	}
	
	//http://localhost:8061/promo/codice?anno=2018&codice=PP08
	@RequestMapping(value = "/cerca/codice", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Promo> listPromoByCodice(@RequestParam("anno") String Anno,
			@RequestParam("codice") String Codice) 
					throws NotFoundException
	{
		logger.info("****** Otteniamo la promozione con Codice: " + Codice + "*******");

		Promo promo = promoService.SelByCodice(Integer.valueOf(Anno), Codice);

		if (promo == null)
		{
			String ErrMsg = String.format("La promozione %s dell'anno %s non è stata trovata!", Codice, Anno);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}

		return new ResponseEntity<Promo>(promo, HttpStatus.OK);
	}

	@RequestMapping(value = "/cerca/attive", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Promo>> listPromoActive() 
			throws NotFoundException
	{
		logger.info("****** Otteniamo la Promozione Attive*******");

		List<Promo> promo = promoService.SelActivePromo();

		if (promo == null)
		{
			String ErrMsg = "Non esistono promozioni attive alla data corrente";
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}

		return new ResponseEntity<List<Promo>>(promo, HttpStatus.OK);
	}

	// ------------------- INSERT PROMO ------------------------------------
	@RequestMapping(value = "/promo/inserisci", method = RequestMethod.POST)
	public ResponseEntity<Promo> createPromo(@Valid @RequestBody Promo promo, BindingResult bindingResult,
			UriComponentsBuilder ucBuilder) 
					throws BindingException
	{
		logger.info("Creiamo una nuova Promo con id " + promo.getIdPromo());
		
		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(MsgErr);

			throw new BindingException(MsgErr);
		}

		promoService.InsPromo(promo);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/promo/id/{idPromo}").buildAndExpand(promo.getIdPromo()).toUri());

		return new ResponseEntity<Promo>(headers, HttpStatus.CREATED);
	}

	// ------------------- DELETE PROMO ----------------------------------
	@RequestMapping(value = "/elimina/{idpromo}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePromo(@PathVariable("idpromo") String IdPromo) 
			throws NotFoundException
	{
		logger.info("Eliminiamo la promo con id " + IdPromo);

		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		Promo promo = promoService.SelByIdPromo(IdPromo);

		if (promo == null)
		{
			String ErrMsg = "ERRORE: Impossibile trovare la promo con id " + IdPromo;
			
			logger.warn(ErrMsg);
			 
			throw new NotFoundException(ErrMsg);
		}

		promoService.DelPromo(promo);

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Eliminazione Promozione" + IdPromo + " Eseguita Con Successo");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
	}
	
	
	

}
