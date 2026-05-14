/**
 * Tüm kullanıcı giriş doğrulama kurallarını tek bir yerde toplar.
 * Sayısal aralık kontrolleri (not, yıl, kredi) ve metin boşluk kontrolleri burada yapılır.
 * Hata durumunda IllegalArgumentException fırlatır; çağıran taraf mesajı doğrudan kullanıcıya gösterebilir.
 */
public class ValidationUtils {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean allFieldsFilled(String... fields) {
        for (String f : fields) {
            if (isNullOrEmpty(f)) return false;
        }
        return true;
    }

    /** @throws IllegalArgumentException geçersiz sayı veya [0, 100] aralığı dışındaysa */
    public static double parseScore(String s) {
        double val;
        try {
            val = Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + s + "' is not a valid number.");
        }
        if (val < 0 || val > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100. Got: " + val);
        }
        return val;
    }

    /** @throws IllegalArgumentException geçersiz sayı veya sıfır/negatifse */
    public static int parsePositiveInt(String s) {
        int val;
        try {
            val = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + s + "' is not a valid integer.");
        }
        if (val <= 0) {
            throw new IllegalArgumentException("Value must be a positive number. Got: " + val);
        }
        return val;
    }

    /** @throws IllegalArgumentException yıl 1–4 aralığında değilse */
    public static int parseYear(String s) {
        int val;
        try {
            val = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Year must be a number between 1 and 4.");
        }
        if (val < 1 || val > 4) {
            throw new IllegalArgumentException("Year must be between 1 and 4. Got: " + val);
        }
        return val;
    }

    /** Not değerini "87.5" formatında döndürür. */
    public static String formatScore(double score) {
        return String.format("%.1f", score);
    }
}
