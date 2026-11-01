package id.ac.ub.filkom.perpustakaan;

import id.ac.ub.filkom.perpustakaan.control.StokService;
import id.ac.ub.filkom.perpustakaan.entity.Buku;
import id.ac.ub.filkom.perpustakaan.entity.BukuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test alur control StokService (Use Case: Kelola Stok Buku - Aktor: Pustakawan).
 */
class StokServiceUnitTest {

    private Map<String, Buku> bukuDb;
    private StokService stokService;

    @SuppressWarnings("unchecked")
    private BukuRepository createBukuRepo(Map<String, Buku> db) {
        return (BukuRepository) Proxy.newProxyInstance(
                BukuRepository.class.getClassLoader(),
                new Class<?>[]{BukuRepository.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("findById".equals(name)) {
                        return Optional.ofNullable(db.get(args[0]));
                    } else if ("save".equals(name)) {
                        Buku b = (Buku) args[0];
                        db.put(b.getIdBuku(), b);
                        return b;
                    } else if ("findAll".equals(name)) {
                        return new ArrayList<>(db.values());
                    } else if ("existsById".equals(name)) {
                        return db.containsKey(args[0]);
                    }
                    return null;
                }
        );
    }

    @BeforeEach
    void setUp() {
        bukuDb = new HashMap<>();
        bukuDb.put("B001", new Buku("B001", "Pemrograman Java", 3));
        bukuDb.put("B002", new Buku("B002", "Rekayasa Perangkat Lunak", 0));
        bukuDb.put("B003", new Buku("B003", "Basis Data", 2));

        BukuRepository repo = createBukuRepo(bukuDb);
        stokService = new StokService(repo);
    }

    @Test
    void tambahStok_berhasil() {
        Buku hasil = stokService.tambahStok("B002", 5);
        assertEquals(5, hasil.getStok());
        assertEquals(5, bukuDb.get("B002").getStok());
    }

    @Test
    void tambahStok_jumlahTidakValid() {
        assertThrows(IllegalArgumentException.class, () -> stokService.tambahStok("B001", 0));
        assertThrows(IllegalArgumentException.class, () -> stokService.tambahStok("B001", -3));
    }

    @Test
    void tambahStok_bukuTidakDitemukan() {
        assertThrows(IllegalArgumentException.class, () -> stokService.tambahStok("B999", 5));
    }

    @Test
    void ubahStok_berhasil() {
        Buku hasil = stokService.ubahStok("B001", 15);
        assertEquals(15, hasil.getStok());
        assertEquals(15, bukuDb.get("B001").getStok());
    }

    @Test
    void ubahStok_negatifDitolak() {
        assertThrows(IllegalArgumentException.class, () -> stokService.ubahStok("B001", -1));
    }

    @Test
    void tambahBukuBaru_berhasil() {
        Buku baru = stokService.tambahBukuBaru("B004", "Kecerdasan Buatan", 4);
        assertNotNull(baru);
        assertEquals("B004", baru.getIdBuku());
        assertEquals("Kecerdasan Buatan", baru.getJudul());
        assertEquals(4, baru.getStok());
        assertTrue(bukuDb.containsKey("B004"));
    }

    @Test
    void tambahBukuBaru_idDuplikatDitolak() {
        assertThrows(IllegalArgumentException.class, () ->
                stokService.tambahBukuBaru("B001", "Buku Duplikat", 5));
    }
}

