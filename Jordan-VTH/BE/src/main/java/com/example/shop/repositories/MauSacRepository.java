package com.example.shop.repositories;

import com.example.shop.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MauSacRepository extends JpaRepository<MauSac, String> {
    @Query(value = "SELECT MAX(CAST(SUBSTRING(ma, 3) AS UNSIGNED)) as maxMa\n" +
            "FROM mau_sac",nativeQuery = true)
    String getMaxMa();

    @Query(value = "SELECT ms FROM MauSac ms WHERE ms.deleted = 1")
    List<MauSac> getAll();
    MauSac findByMaMau(String maMau);

    @Query(value = "SELECT id FROM shopvth.mau_sac\n" +
            "WHERE ten = :tenMau",nativeQuery = true)
    String findIdByMauSac(@Param("tenMau")String tenMau);
}
