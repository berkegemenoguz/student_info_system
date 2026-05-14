/**
 * Pipe-delimited dosya formatına dönüştürme sözleşmesi.
 * DataStore, bu arayüzü uygulayan her modeli tek bir generic saveToFile() ile kaydeder.
 */
public interface Persistable {
    String toFileString();
}
