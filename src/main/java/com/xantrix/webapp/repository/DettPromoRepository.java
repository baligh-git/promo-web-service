package com.xantrix.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xantrix.webapp.entities.DettPromo;
 
 

public interface DettPromoRepository extends JpaRepository<DettPromo, Long>
{
	@Query(value = "CALL Sp_SelActivePromoFid(:codfid)", nativeQuery = true)
	List<DettPromo> findDettPromoByCodFid(@Param("codfid") String CodFid);
	
	@Query(value = "CALL Sp_SelActivePromoArt(:codart,:tipo)", nativeQuery = true)
	List<DettPromo> findDettPromoByCodArt(@Param("codart") String Codice, @Param("tipo") int Tipo);
	
	@Modifying
	@Query(value = "UPDATE dettpromo SET OGGETTO = :oggetto WHERE ID = :id", nativeQuery = true)
	void UpdOggettoPromo(@Param("oggetto") String Oggetto, @Param("id") Long Id);
	
	@Modifying
	@Query(value = "DELETE FROM dettpromo WHERE ID = :id", nativeQuery = true)
	void DelRowPromo(@Param("id") Long Id);
}
