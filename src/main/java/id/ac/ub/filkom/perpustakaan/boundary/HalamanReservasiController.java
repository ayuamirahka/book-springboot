package id.ac.ub.filkom.perpustakaan.boundary;

import id.ac.ub.filkom.perpustakaan.control.BuktiReservasi;
import id.ac.ub.filkom.perpustakaan.control.ReservasiGagalException;
import id.ac.ub.filkom.perpustakaan.control.ReservasiService;
import id.ac.ub.filkom.perpustakaan.entity.Buku;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * «boundary» HalamanReservasiController — Antarmuka web untuk use case «extend» Ajukan Reservasi.
 * Mengikuti pola BCE: Hanya menerima HTTP request, mendelegasikan ke ReservasiService, dan merender view.
 */
@Controller
@RequestMapping("/reservasi")
public class HalamanReservasiController {

    private final ReservasiService reservasiService; // «control»

    public HalamanReservasiController(ReservasiService reservasiService) {
        this.reservasiService = reservasiService;
    }

    private String idAnggotaLogin(HttpSession session) {
        String idAnggota = (String) session.getAttribute("idAnggota");
        if (idAnggota == null) {
            idAnggota = "A001";
            session.setAttribute("idAnggota", idAnggota);
        }
        return idAnggota;
    }

    /** Menampilkan form pengajuan reservasi atau daftar reservasi */
    @GetMapping
    public String tampilHalaman(@RequestParam(required = false) String idBuku,
                                HttpSession session,
                                Model model) {
        String idAnggota = idAnggotaLogin(session);
        model.addAttribute("idAnggota", idAnggota);

        if (idBuku != null && !idBuku.trim().isEmpty()) {
            try {
                Buku buku = reservasiService.getInfoBuku(idBuku);
                model.addAttribute("bukuTerpilih", buku);
            } catch (Exception ignored) {
            }
        }

        model.addAttribute("daftarReservasi", reservasiService.daftarSemuaReservasi());
        return "reservasi"; // templates/reservasi.html
    }

    /**
     * [1] ajukanReservasi(idBuku) dari aktor Anggota
     * Sequence: [1] -> [2] ReservasiService.ajukanReservasi() -> [9] tampilkan bukti
     */
    @PostMapping
    public String ajukanReservasi(@RequestParam String idBuku,
                                  HttpSession session,
                                  Model model) {
        String idAnggota = idAnggotaLogin(session);
        try {
            // [2] Panggil control
            BuktiReservasi bukti = reservasiService.ajukanReservasi(idAnggota, idBuku);
            // [9] Tampilkan halaman bukti reservasi
            model.addAttribute("bukti", bukti);
            return "bukti-reservasi"; // templates/bukti-reservasi.html
        } catch (ReservasiGagalException e) {
            // [10] Alur alternatif: Tangkap kegagalan dan tampilkan pesan kesalahan
            model.addAttribute("pesanError", e.getMessage());
            return tampilHalaman(idBuku, session, model);
        }
    }
}

