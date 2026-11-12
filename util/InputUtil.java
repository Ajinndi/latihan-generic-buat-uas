package util;

import java.util.Scanner;

public class InputUtil {
    public static String getString(Scanner sc) {
        return sc.nextLine();
    }

    // Validasi manual tanpa melempar NumberFormatException
    public static int getInt(Scanner sc) {
        while (true) {
            String input = sc.nextLine().trim();
            if (isAngka(input)) {
                return Integer.parseInt(input);
            }
            System.out.print("Input harus berupa angka yang valid. Masukkan ulang: ");
        }
    }

    public static double getDouble(Scanner sc) {
        while (true) {
            String input = sc.nextLine().trim();
            if (isDesimalValid(input)) {
                return Double.parseDouble(input);
            }
            System.out.print("Input harus berupa angka desimal yang valid. Masukkan ulang: ");
        }
    }

    // Method bantu pengecekan string murni angka
    private static boolean isAngka(String str) {
        if (str == null || str.isEmpty()) return false;
        int start = 0;
        if (str.charAt(0) == '-' || str.charAt(0) == '+') {
            if (str.length() == 1) return false;
            start = 1;
        }
        for (int i = start; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Method bantu pengecekan angka desimal
    private static boolean isDesimalValid(String str) {
        if (str == null || str.isEmpty()) return false;
        boolean adaTitik = false;
        int start = 0;
        if (str.charAt(0) == '-' || str.charAt(0) == '+') {
            if (str.length() == 1) return false;
            start = 1;
        }
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '.') {
                if (adaTitik) return false; // Titik tidak boleh lebih dari satu
                adaTitik = true;
            } else if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}

/*
================================================================================
PENJELASAN LENGKAP STRUKTUR DAN LOGIKA KODE `InputUtil.java`:
================================================================================

1. PENGHINDARAN EXCEPTION BAWAAN (`NumberFormatException`):
   - Alih-alih langsung memanggil `Integer.parseInt()` atau `Double.parseDouble()` 
     pada input mentah yang berisiko memicu *crash* (`NumberFormatException`) jika 
     pengguna salah memasukkan teks biasa, kelas ini membaca input sebagai `String` 
     terlebih dahulu lalu memeriksanya secara manual karakter per karakter.

2. METODE `getInt(Scanner sc)` & `getDouble(Scanner sc)`:
   - Menggunakan perulangan `while (true)` agar program terus meminta input ulang 
     kepada pengguna sampai data yang dimasukkan benar-benar valid.
   - Jika fungsi validasi bernilai `true`, barulah string dikonversi ke tipe data 
     angka yang sesuai.

3. LOGIKA VALIDASI BILANGAN BULAT (`isAngka(String str)`):
   - Mengecek apakah string kosong atau `null`.
   - Menangani tanda plus (`+`) atau minus (`-`) di awal karakter (misalnya angka negatif).
   - Melakukan iterasi menggunakan `Character.isDigit()` untuk memastikan setiap 
     karakter berikutnya murni berupa digit angka (0-9). Jika ada huruf atau simbol 
     lain, maka langsung mengembalikan nilai `false`.

4. LOGIKA VALIDASI BILANGAN DESIMAL (`isDesimalValid(String str)`):
   - Mirip dengan pengecekan angka bulat, namun dilengkapi penanganan titik (`.`).
   - Menggunakan variabel `adaTitik` untuk memastikan titik desimal hanya boleh muncul 
     maksimal satu kali dalam satu angka (mencegah input tidak valid seperti `12..34`).
================================================================================
*/