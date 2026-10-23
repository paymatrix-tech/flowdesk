package com.flowdesk.api.exception;

/**
 * Exception thrown when there is an error during CDC (Change Data Capture) processing.
 * This typically occurs when there are issues with:
 * <ul>
 *   <li>Deserializing incoming CDC events</li>
 *   <li>Processing the CDC operation (INSERT/UPDATE/DELETE)</li>
 *   <li>Transforming the data for the target system</li>
 *   <li>Handling unexpected data formats or states</li>
 * </ul>
 */
public class CdcEventProcessingException extends RuntimeException {

    public CdcEventProcessingException(String message) {
        super(message);
    }

    public CdcEventProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
