package app.model.enums;

/**
 * Represents the status of a lending record in the system.
 * This enum is used to track the current state of a lending transaction.
 *
 * The possible statuses are:
 * - BORROWED: Indicates that the item has been borrowed and is still in possession of the borrower.
 * - RETURNED: Indicates that the borrowed item has been successfully returned.
 * - OVERDUE: Indicates that the borrowed item is overdue for return.
 */
public enum LendingRecordStatus {
    BORROWED, RETURNED, OVERDUE
}

