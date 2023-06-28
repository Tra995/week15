package pet.store.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pet.store.entity.PetStore;

@Repository
public interface PetStoreDao extends JpaRepository<PetStore, Long> {
}