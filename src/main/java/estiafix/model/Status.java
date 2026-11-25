package estiafix.model;

import java.util.List;

public enum Status {

    SUBMITTED("Καταχωρίστηκε", "status-submitted"),
    SCHEDULED("Προγραμματίστηκε", "status-scheduled"),
    IN_PROGRESS("Σε εξέλιξη", "status-in-progress"),
    COMPLETED("Ολοκληρώθηκε", "status-completed"),
    CANCELED("Ακυρώθηκε", "status-canceled");

    private final String label;
    private final String styleClass;

    Status(String label, String styleClass) {
        this.label = label;
        this.styleClass = styleClass;
    }

    public String getLabel() {
        return label;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public boolean isOpen() {
        return this == SUBMITTED || this == SCHEDULED || this == IN_PROGRESS;
    }

    public boolean canBeCanceled() {
        return this == SUBMITTED || this == SCHEDULED;
    }

    public List<Status> nextSteps() {
        return switch (this) {
            case SUBMITTED -> List.of(SCHEDULED, IN_PROGRESS, COMPLETED, CANCELED);
            case SCHEDULED -> List.of(IN_PROGRESS, COMPLETED, CANCELED);
            case IN_PROGRESS -> List.of(SCHEDULED, COMPLETED, CANCELED);
            case COMPLETED, CANCELED -> List.of();
        };
    }

    @Override
    public String toString() {
        return label;
    }

}
