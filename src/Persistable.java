/**
 * Persistable interface — Method Abstraction & Reusability
 *
 * All data model classes implement this interface to guarantee
 * a consistent contract for file-based serialization.
 * This enforces a common design pattern across all model classes
 * and allows DataStore to handle them generically (reusability).
 */
public interface Persistable {
    /**
     * Converts the object to a pipe-delimited string for file storage.
     * @return serialized string representation
     */
    String toFileString();
}
