package controller;

import exception.InputTidakValidException;
import exception.MenuTidakDitemukanException;
import exception.StokTidakCukupException;
import model.EnumUkuran;
import model.Kopi;
import model.MenuItem;
import model.Roti;
import model.Teh;
import util.InputUtil;
import view.ObjectView;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ObjectController {
    private final Scanner sc = new Scanner(System.in);
    private final ObjectView view = new ObjectView();
    private final List<MenuItem> menuItems = new ArrayList<>();

    public ObjectController() {
        menuItems.add(new Kopi("M-01", "Kopi Susu", 18000, 5));
        menuItems.add(new Teh("M-02", "Teh Tarik", 12000, 10));
        menuItems.add(new Roti("M-03", "Roti Cokelat", 10000, 8));
    }

    public void jalan() {
        tampilkanMenuGeneric(menuItems);
        boolean berjalan = true;
        while (berjalan) {
            view.tampilkanMenuAwal();
            int pilihan = InputUtil.getInt(sc);
            switch (pilihan) {
                case 1:
                    tampilkanMenuGeneric(menuItems);
                    break;
                case 2:
                    prosesPesanan();
                    break;
                case 0:
                    berjalan = false;
                    view.tampilkanPesan("Terima kasih sudah berkunjung!");
                    break;
                default:
                    view.tampilkanPesan("Pilihan menu tidak tersedia.");
            }
        }
    }

    // Menggunakan Wildcard Generics (<? extends MenuItem>)
    private void tampilkanMenuGeneric(List<? extends MenuItem> listMenu) {
        view.tampilkanMenu(new ArrayList<>(listMenu));
    }

    private void prosesPesanan() {
        MenuItem menuTerpilih = pilihMenu();
        if (menuTerpilih == null) return;

        int jumlah = inputJumlahValid(menuTerpilih);
        if (jumlah <= 0) return;

        if (!inputKustomisasiValid(menuTerpilih)) return;

        double total = hitungTotal(menuTerpilih, jumlah);
        if (total < 0) return;

        menuTerpilih.kurangiStok(jumlah);
        view.tampilkanStruk(menuTerpilih, jumlah, total);
    }

    private MenuItem pilihMenu() {
        while (true) {
            String id = view.inputIdMenu(sc);
            try {
                return cariMenu(id);
            } catch (MenuTidakDitemukanException e) {
                view.tampilkanPesan(e.getMessage() + " Silakan masukkan ID lagi.");
            }
        }
    }

    private MenuItem cariMenu(String id) {
        return cariItemBerdasarkanId(menuItems, id);
    }

    // Menggunakan Generic Method dengan parameter tipe <T extends MenuItem>
    private <T extends MenuItem> T cariItemBerdasarkanId(List<T> items, String id) {
        for (T item : items) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        throw new MenuTidakDitemukanException("Menu dengan ID " + id + " tidak ada.");
    }

    private int inputJumlahValid(MenuItem item) {
        while (true) {
            try {
                int jumlah = view.inputJumlah(sc);
                item.validasiJumlahPesanan(jumlah);
                return jumlah;
            } catch (InputTidakValidException | StokTidakCukupException e) {
                view.tampilkanPesan(e.getMessage() + " Silakan masukkan jumlah lagi.");
            }
        }
    }

    private boolean inputKustomisasiValid(MenuItem item) {
        while (true) {
            List<Integer> customizations = new ArrayList<>();
            try {
                for (String namaKustomisasi : item.getCustomizationPrompts()) {
                    customizations.add(view.inputKustomisasi(sc, namaKustomisasi));
                }
                item.customize(customizations);
                return true;
            } catch (InputTidakValidException e) {
                view.tampilkanPesan(e.getMessage() + " Silakan masukkan ulang semua kustomisasi.");
            }
        }
    }

    private double hitungTotal(MenuItem item, int jumlah) {
        while (true) {
            view.tampilkanMetodeHarga(item);
            int pilihan = InputUtil.getInt(sc);

            try {
                switch (pilihan) {
                    case 1:
                        return item.hitungHargaTotal(jumlah);
                    case 2:
                        String kodePromo = view.inputKodePromo(sc);
                        if (!kodePromo.equalsIgnoreCase("HEMAT10")) {
                            view.tampilkanPesan("Kode promo tidak valid. Harga normal digunakan.");
                            return item.hitungHargaTotal(jumlah);
                        }
                        return item.hitungHargaTotal(jumlah, kodePromo);
                    case 3:
                        if (!item.bisaPilihUkuran()) {
                            view.tampilkanPesan("Ukuran hanya tersedia untuk minuman.");
                            break;
                        }
                        String pilihanUkuran = view.inputUkuran(sc).toUpperCase();
                        
                        // Validasi ukuran manual tanpa IllegalArgumentException
                        EnumUkuran ukuran = null;
                        if (pilihanUkuran.equals("SMALL")) {
                            ukuran = EnumUkuran.SMALL;
                        } else if (pilihanUkuran.equals("MEDIUM")) {
                            ukuran = EnumUkuran.MEDIUM;
                        } else if (pilihanUkuran.equals("LARGE")) {
                            ukuran = EnumUkuran.LARGE;
                        }

                        if (ukuran == null) {
                            view.tampilkanPesan("Ukuran tidak valid. Pilih SMALL, MEDIUM, atau LARGE.");
                            break;
                        }
                        
                        return item.hitungHargaTotal(jumlah, ukuran);
                    default:
                        view.tampilkanPesan("Pilihan perhitungan tidak tersedia.");
                }
            } catch (InputTidakValidException | StokTidakCukupException e) {
                view.tampilkanPesan(e.getMessage());
                return -1;
            }
        }
    }
}

/*
================================================================================
PENJELASAN LENGKAP STRUKTUR DAN LOGIKA KODE DI ATAS:
================================================================================

1. PENERAPAN GENERICS (Wildcard & Type Parameter):
   - `private void tampilkanMenuGeneric(List<? extends MenuItem> listMenu)`
     Menggunakan **Bounded Wildcard (`? extends MenuItem`)**. Artinya, method ini 
     bisa menerima tipe *List* apa saja yang isinya merupakan kelas `MenuItem` 
     atau sub-kelas dari `MenuItem` (seperti `Kopi`, `Teh`, `Roti`). Hal ini membuat 
     method lebih fleksibel.
   - `private <T extends MenuItem> T cariItemBerdasarkanId(List<T> items, String id)`
     Menggunakan **Generic Method (`<T extends MenuItem>`)**. Tipe `T` ditentukan secara 
     dinamis berdasarkan list yang dikirimkan, dan memastikan bahwa method ini 
     hanya bisa mengembalikan objek yang merupakan turunan dari `MenuItem`.

2. PENGHAPUSAN `NumberFormatException` (Validasi Input Manual):
   - Sebelumnya, konversi string ke angka menggunakan `Integer.parseInt()` secara 
     langsung di dalam blok `try-catch (NumberFormatException e)`. 
   - Pada versi ini, mekanisme tersebut dipindah ke `InputUtil.getInt(sc)` yang 
     melakukan pengecekan manual karakter string per karakter menggunakan fungsi 
     seperti `Character.isDigit()`. Karena input dijamin sudah berupa angka yang valid 
     sebelum dikonversi, program tidak akan pernah melempar `NumberFormatException`.

3. PENGHAPUSAN `IllegalArgumentException` (Validasi Ukuran Manual):
   - Sebelumnya, konversi string ukuran menggunakan `EnumUkuran.valueOf(pilihanUkuran)` 
     yang akan melempar `IllegalArgumentException` jika teks yang dimasukkan pengguna 
     tidak cocok dengan nilai Enum (misalnya salah ketik).
   - Pada versi ini, digunakan percabangan `if-else` manual untuk mencocokkan string 
     (`SMALL`, `MEDIUM`, `LARGE`). Jika input pengguna tidak valid, variabel `ukuran` 
     dibiarkan bernilai `null` dan sistem menampilkan pesan error secara elegan tanpa 
     perlu menangkap *exception* bawaan.

4. ALUR UTAMA KONTROL (`jalan()` & `prosesPesanan()`):
   - Mengatur menu interaktif menggunakan *looping while* dan *switch-case*.
   - Mengelola validasi bertingkat mulai dari pemilihan menu, validasi jumlah dan sisa stok, 
     proses input kustomisasi dinamis, hingga perhitungan total harga (baik standar, 
     menggunakan kode promo, maupun penyesuaian ukuran).
================================================================================
*/