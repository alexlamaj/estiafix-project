package estiafix.model;

public record Student(String username, String fullName, String building, int room, String phone) {
    public String firstName() {
        int space = fullName.indexOf(' ');
        return space > 0 ? fullName.substring(0, space) : fullName;
    }
}
