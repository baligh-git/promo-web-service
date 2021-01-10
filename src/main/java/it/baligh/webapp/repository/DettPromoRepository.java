package it.baligh.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.baligh.webapp.entities.DettPromo;
 
public interface DettPromoRepository extends JpaRepository<DettPromo, Long>
{
	@Query(value = "CALL Sp_SelPromoActive", nativeQuery = true)
	List<DettPromo> findDettPromoActive();

}