package id.ac.ub.filkom.perpustakaan.control;

import id.ac.ub.filkom.perpustakaan.entity.Reservasi;

import java.time.LocalDate;

/**
 * Tipe kembalian (DTO) bukti reservasi untuk boundary/tampilan.
 */
public class BuktiReservasi {

    private final String idReservasi;
    private final String idAnggota;
    private final String namaAnggota;
    private final String idBuku;
    private final String judulBuku;
    private final LocalDate tanggalReservasi;
    private final String status;

    public BuktiReservasi(String idReservasi,
                          String idAnggota,
                          String namaAnggota,
                          String idBuku,
                          String judulBuku,
                          LocalDate tanggalReservasi,
                          String status) {
        this.idReservasi = idReservasi;
        this.idAnggota = idAnggota;
        this.namaAnggota = namaAnggota;
        this.idBuku = idBuku;
        this.judulBuku = judulBuku;
        this.tanggalReservasi = tanggalReservasi;
        this.status = status;
    }

    public static BuktiReservasi dari(Reservasi r) {
        return new BuktiReservasi(
                r.getIdReservasi(),
                r.getAnggota().getIdAnggota(),
                r.getAnggota().getNama(),
                r.getBuku().getIdBuku(),
                r.getBuku().getJudul(),
                r.getTanggalReservasi(),
                r.getStatus()
        );
    }

    public String getIdReservasi() {
        return idReservasi;
    }

    public String getIdAnggota() {
        return idAnggota;
    }

    public String getNamaAnggota() {
        return namaAnggota;
    }

    public String getIdBuku() {
        return idBuku;
    }

    public String getJudulBuku() {
        return judulBuku;
    }

    public LocalDate getTanggalReservasi() {
        return tanggalReservasi;
    }

    public String getStatus() {
        return status;
    }
}

