public class Classroom implements Persistable {
    private String roomId;
    private String roomName;
    private int capacity;

    public Classroom(String roomId, String roomName, int capacity) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
    }

    public String getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }

    public String toFileString() {
        return roomId + "|" + roomName + "|" + capacity;
    }

    public static Classroom fromFileString(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 3) return null;
            return new Classroom(parts[0], parts[1], Integer.parseInt(parts[2]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
