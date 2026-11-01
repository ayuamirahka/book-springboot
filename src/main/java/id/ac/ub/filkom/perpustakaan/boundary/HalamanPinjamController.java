package id.ac.ub.filkom.perpustakaan.boundary;

import id.ac.ub.filkom.perpustakaan.control.Bukti;
import id.ac.ub.filkom.perpustakaan.control.PinjamGagalException;
import id.ac.ub.filkom.perpustakaan.control.PinjamService;
import id.ac.ub.filkom.perpustakaan.entity.Anggota;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pinjam")
public class HalamanPinjamController {

    private final PinjamService pinjamService; // «control»

    public HalamanPinjamController(PinjamService pinjamService) {
        this.pinjamService = pinjamService;
    }

    private String idAnggotaLogin(HttpSession session) {
        String idAnggota = (String) session.getAttribute("idAnggota");
        if (!pinjamService.adaAnggota(idAnggota)) {
            idAnggota = "A001";
            session.setAttribute("idAnggota", idAnggota);
        }
        return idAnggota;
    }

    /** Langkah 1-2 scenario: tampilkan daftar buku beserta stok.
     * Parameter anggota hanya untuk mengganti akun simulasi; alur transaksi tetap sama.
     */
    @GetMapping
    public String tampilHalaman(@RequestParam(name = "anggota", required = false) String anggota,
                                 HttpSession session, Model model) {
        if (anggota != null && !anggota.isBlank() && pinjamService.adaAnggota(anggota)) {
            session.setAttribute("idAnggota", anggota);
        }

        String idAnggota = idAnggotaLogin(session);
        Anggota anggotaAktif = pinjamService.cariAnggota(idAnggota);
        model.addAttribute("idAnggota", idAnggota);
        model.addAttribute("anggotaAktif", anggotaAktif);
        model.addAttribute("daftarAnggota", pinjamService.daftarAnggota());
        model.addAttribute("jumlahPinjamanAktif", pinjamService.jumlahPinjamanAktif(idAnggota));
        model.addAttribute("batasPinjam", anggotaAktif.getBatasPinjam());
        model.addAttribute("daftarBuku", pinjamService.daftarBuku());
        return "pinjam"; // templates/pinjam.html
    }

    /** [1] pilihBuku(idBuku) dari aktor Anggota. */
    @PostMapping
    public String pilihBuku(@RequestParam String idBuku,
                            HttpSession session, Model model) {
        String idAnggota = idAnggotaLogin(session);
        try {
            // [2] pinjamBuku(idAnggota, idBuku)
            Bukti bukti = pinjamService.pinjamBuku(idAnggota, idBuku);
            return tampilBukti(bukti, model); // [12] tampilkan bukti
        } catch (PinjamGagalException e) { // [13] pesanHabis
            model.addAttribute("pesan", e.getMessage());
            model.addAttribute("bisaReservasi", e.isBisaReservasi());
            model.addAttribute("idBukuGagal", idBuku);
            return tampilHalaman(null, session, model); // [14] pesan & reservasi
        }
    }

    /** +tampilBukti() pada class diagram 8a. */
    private String tampilBukti(Bukti bukti, Model model) {
        model.addAttribute("bukti", bukti);
        return "bukti"; // templates/bukti.html
    }
}
