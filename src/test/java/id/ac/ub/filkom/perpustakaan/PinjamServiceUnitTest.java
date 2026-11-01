package id.ac.ub.filkom.perpustakaan;

import id.ac.ub.filkom.perpustakaan.control.Bukti;
import id.ac.ub.filkom.perpustakaan.control.PinjamGagalException;
import id.ac.ub.filkom.perpustakaan.control.PinjamService;
import id.ac.ub.filkom.perpustakaan.entity.Anggota;
import id.ac.ub.filkom.perpustakaan.entity.AnggotaRepository;
import id.ac.ub.filkom.perpustakaan.entity.Buku;
import id.ac.ub.filkom.perpustakaan.entity.BukuRepository;
import id.ac.ub.filkom.perpustakaan.entity.TransaksiPinjam;
import id.ac.ub.filkom.perpustakaan.entity.TransaksiPinjamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pengujian 4 skenario alur pinjam buku (Slide 54).
 */
class PinjamServiceUnitTest {

    private Map<String, Anggota> anggotaDb;
    private Map<String, Buku> bukuDb;
    private Map<String, TransaksiPinjam> transaksiDb;
    private PinjamService pinjamService;

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
                        if (entity instanceof Buku b) {
                            ((Map<String, Buku>) db).put(b.getIdBuku(), b);
                        } else if (entity instanceof Anggota a) {
                            ((Map<String, Anggota>) db).put(a.getIdAnggota(), a);
                        } else if (entity instanceof TransaksiPinjam t) {
                            ((Map<String, TransaksiPinjam>) db).put(t.getIdTransaksi(), t);
                        }
                        return entity;
                    } else if ("findAll".equals(name)) {
                        return new ArrayList<>(db.values());
                    }
                    return null;
                }
        );
    }

    @BeforeEach
    void setUp() {
        anggotaDb = new HashMap<>();
        bukuDb = new HashMap<>();
        transaksiDb = new HashMap<>();

        anggotaDb.put("A001", new Anggota("A001", "Budi Santoso", 2));
        anggotaDb.put("A002", new Anggota("A002", "Sari Dewi", 1));

        bukuDb.put("B001", new Buku("B001", "Pemrograman Java", 3));
        bukuDb.put("B002", new Buku("B002", "Rekayasa Perangkat Lunak", 0));
        bukuDb.put("B003", new Buku("B003", "Basis Data", 2));

        AnggotaRepository anggotaRepo = createRepo(AnggotaRepository.class, anggotaDb);
        BukuRepository bukuRepo = createRepo(BukuRepository.class, bukuDb);
        TransaksiPinjamRepository transaksiRepo = createRepo(TransaksiPinjamRepository.class, transaksiDb);

        pinjamService = new PinjamService(anggotaRepo, bukuRepo, transaksiRepo);
    }

    @Test
    void skenario1_stokTersedia() {
        Bukti bukti = pinjamService.pinjamBuku("A001", "B001");
        assertNotNull(bukti);
        assertEquals("Budi Santoso", bukti.getNamaAnggota());
        assertEquals("Pemrograman Java", bukti.getJudulBuku());
        assertEquals("DIPINJAM", bukti.getStatus());
        assertEquals(2, bukuDb.get("B001").getStok());
    }

    @Test
    void skenario2_stokHabis() {
        PinjamGagalException ex = assertThrows(PinjamGagalException.class, () ->
                pinjamService.pinjamBuku("A001", "B002")
        );
        assertEquals("Stok buku habis", ex.getMessage());
        assertTrue(ex.isBisaReservasi());
    }

    @Test
    void skenario3_melewatiBatas() {
        // A001 batas 2
        pinjamService.pinjamBuku("A001", "B001");
        pinjamService.pinjamBuku("A001", "B003");

        PinjamGagalException ex = assertThrows(PinjamGagalException.class, () ->
                pinjamService.pinjamBuku("A001", "B001")
        );
        assertEquals("Melewati batas pinjam", ex.getMessage());
        assertFalse(ex.isBisaReservasi());
    }

    @Test
    void skenario4_dataTidakValid() {
        PinjamGagalException ex = assertThrows(PinjamGagalException.class, () ->
                pinjamService.pinjamBuku("A001", "B999")
        );
        assertEquals("Buku tidak ditemukan", ex.getMessage());
        assertFalse(ex.isBisaReservasi());
    }
}

