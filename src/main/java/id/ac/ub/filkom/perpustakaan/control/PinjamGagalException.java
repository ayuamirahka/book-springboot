package id.ac.ub.filkom.perpustakaan.control;

public class PinjamGagalException extends RuntimeException {

    private final boolean bisaReservasi;

    public PinjamGagalException(String message, boolean bisaReservasi) {
        super(message);
        this.bisaReservasi = bisaReservasi;
    }

    public static PinjamGagalException tidakDitemukan(String entity) {
        return new PinjamGagalException(entity + " tidak ditemukan", false);
    }

    public static PinjamGagalException melewatiBatas() {
        return new PinjamGagalException("Melewati batas pinjam", false);
    }

    public static PinjamGagalException stokHabis() {
        return new PinjamGagalException("Stok buku habis", true);
    }

    public boolean isBisaReservasi() {
        return bisaReservasi;
    }
}

