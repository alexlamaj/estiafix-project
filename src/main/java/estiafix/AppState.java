package estiafix;

import estiafix.model.Category;
import estiafix.model.Student;
import estiafix.model.FaultReport;

import java.util.Optional;

public final class AppState {

    private static final AppState INSTANCE = new AppState();

    private final Student currentStudent = new Student("marina", "Μαρίνα Κ.", "B", 214, "6912345678");

    private Category selectedCategory;
    private FaultReport lastSubmitted;
    private String flashMessage;

    private AppState() {}

    public static AppState get() {
        return INSTANCE;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public FaultReport getLastSubmitted() {
        return lastSubmitted;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void setLastSubmitted(FaultReport lastSubmitted) {
        this.lastSubmitted = lastSubmitted;
    }

    public void setFlashMessage(String flashMessage) {
        this.flashMessage = flashMessage;
    }

    public Optional<String> takeFlashMessage() {
        String message = flashMessage;
        flashMessage = null;
        return Optional.onNullable(message);
    }

}
