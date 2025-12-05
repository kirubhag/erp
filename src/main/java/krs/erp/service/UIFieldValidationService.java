package krs.erp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import krs.erp.enums.UIFieldType;
import krs.erp.model.ErpField;

/**
 * Service for validating field values based on UI field type
 * Provides server-side validation for all UI field types
 */
@Service
public class UIFieldValidationService {

    /**
     * Validate a field value against its UI type configuration
     * 
     * @param field The field configuration
     * @param value The value to validate
     * @return Validation result with isValid flag and error messages
     */
    public Map<String, Object> validateFieldValue(ErpField field, Object value) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();

        // Check required field
        if (field.getIsRequired() && (value == null || value.toString().trim().isEmpty())) {
            errors.add(field.getFieldLabel() + " is required");
            result.put("isValid", false);
            result.put("errors", errors);
            return result;
        }

        // If value is null/empty and not required, it's valid
        if (value == null || value.toString().trim().isEmpty()) {
            result.put("isValid", true);
            result.put("errors", errors);
            return result;
        }

        // Get UI field type
        UIFieldType uiFieldType = field.getUIFieldType();
        if (uiFieldType == null) {
            result.put("isValid", true);
            result.put("errors", errors);
            return result;
        }

        // Validate based on UI type
        switch (uiFieldType) {
            case SINGLE_LINE_TEXT:
            case MULTI_LINE_TEXT:
                validateTextLength(field, value.toString(), errors);
                break;

            case EMAIL:
                validateEmail(field, value.toString(), errors);
                break;

            case PHONE:
                validatePhone(field, value.toString(), errors);
                break;

            case URL:
                validateUrl(field, value.toString(), errors);
                break;

            case NUMBER:
            case LONG_INTEGER:
                validateInteger(field, value.toString(), errors);
                break;

            case DECIMAL:
            case CURRENCY:
                validateDecimal(field, value.toString(), errors);
                break;

            case PERCENT:
                validatePercent(field, value.toString(), errors);
                break;

            case DATE:
                validateDate(field, value.toString(), errors);
                break;

            case DATETIME:
                validateDateTime(field, value.toString(), errors);
                break;

            case CHECKBOX:
                validateBoolean(field, value.toString(), errors);
                break;

            case PICKLIST:
            case RADIO:
                validatePicklist(field, value.toString(), errors);
                break;

            case MULTI_SELECT:
                validateMultiSelect(field, value.toString(), errors);
                break;

            default:
                // For other types, just check max length if applicable
                if (field.getMaxLength() != null) {
                    validateTextLength(field, value.toString(), errors);
                }
                break;
        }

        // Apply custom validation pattern if exists
        if (field.getValidationPattern() != null && !field.getValidationPattern().isEmpty()) {
            validatePattern(field, value.toString(), errors);
        }

        result.put("isValid", errors.isEmpty());
        result.put("errors", errors);
        return result;
    }

    private void validateTextLength(ErpField field, String value, List<String> errors) {
        if (field.getMaxLength() != null && value.length() > field.getMaxLength()) {
            errors.add(field.getFieldLabel() + " must not exceed " + field.getMaxLength() + " characters");
        }
    }

    private void validateEmail(ErpField field, String value, List<String> errors) {
        String emailPattern = field.getValidationPattern() != null ? field.getValidationPattern()
                : "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (!Pattern.matches(emailPattern, value)) {
            errors.add(field.getFieldLabel() + " must be a valid email address");
        }
    }

    private void validatePhone(ErpField field, String value, List<String> errors) {
        String phonePattern = field.getValidationPattern() != null ? field.getValidationPattern()
                : "^[+]?[0-9\\-\\s\\(\\)]{7,20}$";

        if (!Pattern.matches(phonePattern, value)) {
            errors.add(field.getFieldLabel() + " must be a valid phone number");
        }
    }

    private void validateUrl(ErpField field, String value, List<String> errors) {
        String urlPattern = field.getValidationPattern() != null ? field.getValidationPattern()
                : "^https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)$";

        if (!Pattern.matches(urlPattern, value)) {
            errors.add(field.getFieldLabel() + " must be a valid URL");
        }
    }

    private void validateInteger(ErpField field, String value, List<String> errors) {
        try {
            Long.parseLong(value);
        } catch (NumberFormatException e) {
            errors.add(field.getFieldLabel() + " must be a valid integer");
        }
    }

    private void validateDecimal(ErpField field, String value, List<String> errors) {
        try {
            BigDecimal decimal = new BigDecimal(value);

            // Check decimal places if specified
            if (field.getDecimalPlaces() != null) {
                int scale = decimal.scale();
                if (scale > field.getDecimalPlaces()) {
                    errors.add(field.getFieldLabel() + " must have at most " +
                            field.getDecimalPlaces() + " decimal places");
                }
            }
        } catch (NumberFormatException e) {
            errors.add(field.getFieldLabel() + " must be a valid decimal number");
        }
    }

    private void validatePercent(ErpField field, String value, List<String> errors) {
        try {
            BigDecimal percent = new BigDecimal(value);
            if (percent.compareTo(BigDecimal.ZERO) < 0 || percent.compareTo(new BigDecimal("100")) > 0) {
                errors.add(field.getFieldLabel() + " must be between 0 and 100");
            }
        } catch (NumberFormatException e) {
            errors.add(field.getFieldLabel() + " must be a valid percentage");
        }
    }

    private void validateDate(ErpField field, String value, List<String> errors) {
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            errors.add(field.getFieldLabel() + " must be a valid date");
        }
    }

    private void validateDateTime(ErpField field, String value, List<String> errors) {
        try {
            LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            errors.add(field.getFieldLabel() + " must be a valid date and time");
        }
    }

    private void validateBoolean(ErpField field, String value, List<String> errors) {
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false") &&
                !value.equals("1") && !value.equals("0")) {
            errors.add(field.getFieldLabel() + " must be true or false");
        }
    }

    private void validatePicklist(ErpField field, String value, List<String> errors) {
        if (field.getPicklistOptions() != null && !field.getPicklistOptions().isEmpty()) {
            String[] options = field.getPicklistOptions().split(",");
            boolean found = false;
            for (String option : options) {
                if (option.trim().equalsIgnoreCase(value.trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                errors.add(field.getFieldLabel() + " must be one of the configured options");
            }
        }
    }

    private void validateMultiSelect(ErpField field, String value, List<String> errors) {
        if (field.getPicklistOptions() != null && !field.getPicklistOptions().isEmpty()) {
            String[] options = field.getPicklistOptions().split(",");
            String[] selectedValues = value.split(",");

            for (String selectedValue : selectedValues) {
                boolean found = false;
                for (String option : options) {
                    if (option.trim().equalsIgnoreCase(selectedValue.trim())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    errors.add(field.getFieldLabel() + " contains invalid option: " + selectedValue.trim());
                    break;
                }
            }
        }
    }

    private void validatePattern(ErpField field, String value, List<String> errors) {
        try {
            if (!Pattern.matches(field.getValidationPattern(), value)) {
                errors.add(field.getFieldLabel() + " format is invalid");
            }
        } catch (Exception e) {
            // Invalid regex pattern - skip validation
        }
    }
}
