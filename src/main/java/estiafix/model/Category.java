package estiafix.model;

public enum Category {

    HEATING("Θέρμανση"),
    PLUMBING("Υδραυλικά"),
    ELECTRICAL("Ηλεκτρολογικά"),
    DOORS_FURNITURE("Πόρτες και Έπιπλα"),
    INTERNET("Internet"),
    OTHER("Άλλο");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

}
