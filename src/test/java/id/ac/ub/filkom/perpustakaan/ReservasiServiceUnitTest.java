package id.ac.ub.filkom.perpustakaan;

import id.ac.ub.filkom.perpustakaan.control.BuktiReservasi;
import id.ac.ub.filkom.perpustakaan.control.ReservasiGagalException;
import id.ac.ub.filkom.perpustakaan.control.ReservasiService;
import id.ac.ub.filkom.perpustakaan.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test alur control ReservasiService (Use Case: «extend» Ajukan Reservasi).
 */
class ReservasiServiceUnitTest {

    private Map<String, Anggota> anggotaDb;
    private Map<String, Buku> bukuDb;
    private Map<String, Reservasi> reservasiDb;
    private ReservasiService reservasiService;

    @SuppressWarnings("unchecked")
    private <T> T createRepo(Class<T> repoInterface, Map<String, ?> db) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class<?>[]{repoInterface},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("findById".equals(name)) {
                        return Optional.ofNullable(db.get(args[0]));
                    } else if ("save".equals(name)) {
                        Object entity = args[0];
                        if (entity instanceof Reservasi r) {
                            ((Map<String, Reservasi>) db).put(r.getIdReservasi(), r);
                        }
                        return entity;
                    } else if ("findAll".equals(name)) {
                        return new ArrayList<>(db.values());
                    } else if ("existsByAnggotaIdAnggotaAndBukuIdBukuAndStatus".equals(name)) {
                        String idAnggota = (String) args[0];
                        String idBuku = (String) args[1];
                        String status = (String) args[2];
                        return ((Map<String, Reservasi>) db).values().stream()
                                .anyMatch(r -> r.getAnggota().getIdAnggota().equals(idAnggota)
                                        && r.getBuku().getIdBuku().equals(idBuku)
                                        && r.getStatus().equals(status));
                    }
                    return null;
                }
        );
    }

    @BeforeEach
    void setUp() {
        anggotaDb = new HashMap<>();
        bukuDb = new HashMap<>();
        reservasiDb = new HashMap<>();

        anggotaDb.put("A001", new Anggota("A001", "Budi Santoso", 2));
        bukuDb.put("B001", new Buku("B001", "Pemrograman Java", 3));
        bukuDb.put("B002", new Buku("B002", "Rekayasa Perangkat Lunak", 0)); // stok habis

        AnggotaRepository anggotaRepo = createRepo(AnggotaRepository.class, anggotaDb);
        BukuRepository bukuRepo = createRepo(BukuRepository.class, bukuDb);
        ReservasiRepository reservasiRepo = createRepo(ReservasiRepository.class, reservasiDb);

        reservasiService = new ReservasiService(anggotaRepo, bukuRepo, reservasiRepo);
    }

    @Test
    void ajukanReservasi_berhasil_ketika_stok_habis() {
        BuktiReservasi bukti = reservasiService.ajukanReservasi("A001", "B002");

        assertNotNull(bukti);
        assertTrue(bukti.getIdReservasi().startsWith("R"));
        assertEquals("Budi Santoso", bukti.getNamaAnggota());
        assertEquals("Rekayasa Perangkat Lunak", bukti.getJudulBuku());
        assertEquals(Reservasi.MENUNGGU, bukti.getStatus());
        assertEquals(1, reservasiDb.size());
    }

    @Test
    void ajukanReservasi_gagal_ketika_stok_masih_ada() {
        ReservasiGagalException ex = assertThrows(ReservasiGagalException.class, () ->
                reservasiService.ajukanReservasi("A001", "B001")
        );
        assertTrue(ex.getMessage().contains("masih tersedia"));
        assertEquals(0, reservasiDb.size());
    }

    @Test
    void ajukanReservasi_gagal_ketika_sudah_pernah_reservasi_aktif() {
        reservasiService.ajukanReservasi("A001", "B002");

        ReservasiGagalException ex = assertThrows(ReservasiGagalException.class, () ->
                reservasiService.ajukanReservasi("A001", "B002")
        );
        assertTrue(ex.getMessage().contains("sudah memiliki reservasi aktif"));
    }

    @Test
    void ajukanReservasi_gagal_buku_tidak_ditemukan() {
        ReservasiGagalException ex = assertThrows(ReservasiGagalException.class, () ->
                reservasiService.ajukanReservasi("A001", "B999")
        );
        assertTrue(ex.getMessage().contains("Buku tidak ditemukan"));
    }
}

