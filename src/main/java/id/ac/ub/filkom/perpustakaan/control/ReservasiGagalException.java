package id.ac.ub.filkom.perpustakaan.control;

public class ReservasiGagalException extends RuntimeException {

    public ReservasiGagalException(String message) {
        super(message);
    }

    public static ReservasiGagalException stokMasihAda() {
        return new ReservasiGagalException("Stok buku masih tersedia, silakan lakukan peminjaman langsung.");
    }

    public static ReservasiGagalException sudahReservasi() {
        return new ReservasiGagalException("Anda sudah memiliki reservasi aktif yang sedang menunggu untuk buku ini.");
    }

    public static ReservasiGagalException tidakDitemukan(String entitas) {
        return new ReservasiGagalException(entitas + " tidak ditemukan.");
    }
}

