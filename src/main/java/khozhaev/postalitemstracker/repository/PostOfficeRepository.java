package khozhaev.postalitemstracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import khozhaev.postalitemstracker.model.PostOffice;

public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {
}
