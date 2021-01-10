package it.baligh.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.baligh.webapp.entities.Promo;

@Repository
public interface PromoRepository extends JpaRepository<Promo, Long>
{
	Promo findByIdPromo(String IdPromo);	 
}
