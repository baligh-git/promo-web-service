package com.xantrix.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xantrix.webapp.entities.Promo;
import com.xantrix.webapp.repository.PromoRepository;

@Service
@Transactional(readOnly = true)
public class PromoServiceImpl implements PromoService
{

	@Autowired
	private PromoRepository promoRepository;
	
	@Override
	public List<Promo> SelTutti()
	{
		return promoRepository.findAll();
	}

	@Override
	public Promo SelByIdPromo(String IdPromo)
	{
		 return promoRepository.findByIdPromo(IdPromo);
	}
		
	@Override
	@Transactional
	public void InsPromo(Promo promo)
	{
		promoRepository.saveAndFlush(promo);
	}

	@Override
	@Transactional
	public void DelPromo(Promo promo)
	{
		promoRepository.delete(promo);
	}

}
