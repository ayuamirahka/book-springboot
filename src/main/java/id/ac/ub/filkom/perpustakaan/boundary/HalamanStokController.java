package id.ac.ub.filkom.perpustakaan.boundary;

import id.ac.ub.filkom.perpustakaan.control.StokService;
import id.ac.ub.filkom.perpustakaan.entity.Buku;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * «boundary» HalamanStokController — Antarmuka web untuk use case Kelola Stok Buku (aktor: Pustakawan).
 * Mengikuti pola BCE: Hanya menerima request, memanggil control (StokService), dan memilih view.
 */
@Controller
@RequestMapping("/stok")
public class HalamanStokController {

    private final StokService stokService; // «control»

    public HalamanStokController(StokService stokService) {
        this.stokService = stokService;
    }

    /** Menampilkan halaman daftar stok buku dan form pengelolaan */
    @GetMapping
    public String tampilHalaman(Model model) {
        List<Buku> daftarBuku = stokService.daftarSemuaBuku();
        model.addAttribute("daftarBuku", daftarBuku);
        return "stok"; // templates/stok.html
    }

    /**
     * [1] kelolaStok: Pustakawan menambah stok buku (Restock)
     * Sequence: [1] -> [2] -> ... -> [10]
     */
    @PostMapping("/tambah")
    public String tambahStok(@RequestParam String idBuku,
                             @RequestParam int jumlah,
                             RedirectAttributes redirectAttributes) {
        try {
            // [2] tambahStok(idBuku, jumlah) ke StokService
            Buku buku = stokService.tambahStok(idBuku, jumlah);
            // [10] tampilkan pesan sukses
            redirectAttributes.addFlashAttribute("pesanSukses",
                    "Berhasil menambah stok buku '" + buku.getJudul() + "' sebanyak " + jumlah + ". Total stok saat ini: " + buku.getStok());
        } catch (Exception e) {
            // [12] alur alternatif: tangkap exception & tampilkan pesan gagal
            redirectAttributes.addFlashAttribute("pesanError", e.getMessage());
        }
        return "redirect:/stok";
    }

    /**
     * [1b] kelolaStok: Pustakawan mengubah nilai stok buku secara langsung
     */
    @PostMapping("/ubah")
    public String ubahStok(@RequestParam String idBuku,
                           @RequestParam int stokBaru,
                           RedirectAttributes redirectAttributes) {
        try {
            // [2b] ubahStok(idBuku, stokBaru) ke StokService
            Buku buku = stokService.ubahStok(idBuku, stokBaru);
            redirectAttributes.addFlashAttribute("pesanSukses",
                    "Berhasil mengubah stok buku '" + buku.getJudul() + "' menjadi " + buku.getStok());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("pesanError", e.getMessage());
        }
        return "redirect:/stok";
    }

    /**
     * [1c] Pustakawan menambahkan judul buku baru
     */
    @PostMapping("/buku-baru")
    public String tambahBukuBaru(@RequestParam String idBuku,
                                 @RequestParam String judul,
                                 @RequestParam int stok,
                                 RedirectAttributes redirectAttributes) {
        try {
            Buku bukuBaru = stokService.tambahBukuBaru(idBuku, judul, stok);
            redirectAttributes.addFlashAttribute("pesanSukses",
                    "Berhasil menambahkan buku baru: '" + bukuBaru.getJudul() + "' (" + bukuBaru.getIdBuku() + ") dengan stok " + bukuBaru.getStok());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("pesanError", e.getMessage());
        }
        return "redirect:/stok";
    }
}

