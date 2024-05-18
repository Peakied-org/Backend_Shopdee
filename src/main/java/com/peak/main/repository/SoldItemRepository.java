package com.peak.main.repository;

import com.peak.main.model.SoldItem;
import org.springframework.data.jpa.repository.JpaRepository;;

public interface SoldItemRepository extends JpaRepository<SoldItem, Long> {
}
