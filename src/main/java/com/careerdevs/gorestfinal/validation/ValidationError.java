package com.careerdevs.gorestfinal.validation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValidationError {

    private final HashMap<String, String> errors = new HashMap<>();

    public void addError (String key, String errorMsg) {
        errors.put(key, errorMsg);
    }

    public boolean hasError() {
        return errors.size() != 0;
    }

    @Override
    public String toString() {
        StringBuilder errorMessage = new StringBuilder("ValidationError:\n");
        for (Map.Entry<String, String> err : errors.entrySet()) {
            errorMessage.append(err.getKey()).append(": ").append(err.getValue()).append("\n");
        }
        return errorMessage.toString();
    }

    public String toJSONString () {

        return new JSONObject(errors).toString();

    }

}