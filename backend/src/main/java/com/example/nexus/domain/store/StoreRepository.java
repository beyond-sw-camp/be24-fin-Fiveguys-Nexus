package com.example.nexus.domain.store;

import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByUserIdx(Long userIdx);

    List<Store> findByStoreNameContainingIgnoreCase(String keyword);

    Long user(User user);
}
