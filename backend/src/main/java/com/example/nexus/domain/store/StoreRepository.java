package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store,Long> {
}
