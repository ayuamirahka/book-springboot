package id.ac.ub.filkom.perpustakaan.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Akses tabel buku (implementasi otomatis Spring Data JPA). */
public interface BukuRepository extends JpaRepository<Buku, String> {

    // Query turunan dari nama method:
    // SELECT * FROM buku WHERE judul LIKE %kata%
    List<Buku> findByJudulContainingIgnoreCase(String kata);
}

