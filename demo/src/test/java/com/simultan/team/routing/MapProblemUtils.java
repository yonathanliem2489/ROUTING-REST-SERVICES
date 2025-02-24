package com.simultan.team.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class MapProblemUtils {

  public static final String IF_STATEMENT = "__IF__[";
  public static final String THEN_STATEMENT = "]__THEN__[";
  public static final String ELSE_STATEMENT = "]__ELSE__[";
  public static final String END_STATEMENT = "]__END__";
  public static final String EQ_STATEMENT = "]__EQ__[";
  public static final String NEQ_STATEMENT = "]__NEQ__[";
  public static final String CONTAINS_STATEMENT = "]__CONTAINS__[";
  public static final String PREFIX = "__PREFIX__[";
  public static final String END_PREFIX = "]__END_PREFIX__";
  public static final String EXIST_STATEMENT = "]__EXIST__[";

  public static final String[] EQ_STATEMENTS= {
      IF_STATEMENT,
      EQ_STATEMENT,
      THEN_STATEMENT,
      ELSE_STATEMENT,
      END_STATEMENT
  };

  public static final String[] NEQ_STATEMENTS= {
      IF_STATEMENT,
      NEQ_STATEMENT,
      THEN_STATEMENT,
      ELSE_STATEMENT,
      END_STATEMENT
  };

  public static final String[] CONTAINS_STATEMENTS = {
      IF_STATEMENT,
      CONTAINS_STATEMENT,
      THEN_STATEMENT,
      ELSE_STATEMENT,
      END_STATEMENT
  };

  public List<Map<String, Object>> transformDataWithNestedKey(Map<String, Object> inputData,
      Map<String, String> mappingSpec, String nestedKey) {

    List<Map<String, Object>> masterArrayData = new ArrayList<>();
    if(inputData.containsKey(nestedKey)) {
      masterArrayData = (List<Map<String, Object>>) inputData.get(nestedKey);
    }

    List<Map<String, Object>> data = new ArrayList<>();
    masterArrayData.forEach(masterData -> {
      Map<String, Object> mapping = new HashMap<>();
      mappingSpec.forEach((attributeName, code) -> {
          mapping.put(attributeName, extractVariable(code, nestedKey, masterData, inputData));
      });
      data.add(mapping);
    });

    return data;
  }

  public List<Map<String, Object>> transformData(List<Map<String, Object>> inputData,
      Map<String, String> mappingSpec) {
    List<Map<String, Object>> dataMapping = new ArrayList<>();
    inputData.forEach(data -> {
      Map<String, Object> mapping = new HashMap<>();
      mappingSpec.forEach((attributeName, code) -> {
        mapping.put(attributeName, generateData(null, null, data, code));
      });
      dataMapping.add(mapping);
    });

    return dataMapping;
  }

  private Object generateData(String nestedKey, Map<String, Object> inputData, Map<String, Object> data, String code) {

    // if it's statement
    if(code.contains(IF_STATEMENT)) {
      return generateStatement(nestedKey, inputData, data, code);
    }

    // check if code contains array object mapping
    if(code.contains(".$.")) {
      String[] codeParts = code.split("\\.\\$\\.", 2);
      List<Map<String, Object>> maps = (List<Map<String, Object>>) data.get(codeParts[0]);
      List<Object> arrayData = new ArrayList<>();
      maps.forEach(map -> arrayData.add(generateData(nestedKey, inputData, map, codeParts[1])));
      return arrayData;
    }

    // check if code have nested object mapping
    String[] codeParts = code.split("\\.", 2);
    if(codeParts.length > 1) {
      return generateData(nestedKey, inputData, (Map<String, Object>) data.get(codeParts[0]), codeParts[1]);
    }

    // check if code is the latest object
    return data.get(code);
  }

  private Object generateStatement(String nestedKey, Map<String, Object> inputData, Map<String, Object> data, String code) {
    if(code.contains(EQ_STATEMENT)) {
      return generateEQStatement(nestedKey, inputData, data, code);
    }

    if(code.contains(CONTAINS_STATEMENT)) {
      return generateContainsStatement(nestedKey, inputData, data, code);
    }

    return StringUtils.EMPTY;
  }

  private Object generateContainsStatement(String nestedKey, Map<String, Object> inputData, Map<String, Object> data, String code) {
    String statement = StringUtils.EMPTY;
    String variableMatcher = StringUtils.EMPTY;
    String thenValue = StringUtils.EMPTY;
    String elseValue = StringUtils.EMPTY;

    String codeStatement = code;
    for (String exactStatement : CONTAINS_STATEMENTS) {
      String[] statementParts = codeStatement.split(Pattern.quote(exactStatement));
      if(exactStatement.equalsIgnoreCase(CONTAINS_STATEMENT)) {
        statement = statementParts[0];
      } else if(exactStatement.equalsIgnoreCase(THEN_STATEMENT)) {
        variableMatcher = statementParts[0];
      } else if(exactStatement.equalsIgnoreCase(ELSE_STATEMENT)) {
        thenValue = statementParts[0];
      }
      else if(exactStatement.equalsIgnoreCase(END_STATEMENT)) {
        elseValue = statementParts[0];
      }

      if(!exactStatement.equalsIgnoreCase(END_STATEMENT)) {
        codeStatement = statementParts[1];
      }
    }

    if(!statement.isEmpty() && !variableMatcher.isEmpty() && !thenValue.isEmpty() && !elseValue.isEmpty()) {
      String sanitizeStatement = statement.replace(nestedKey + ".$.", "");
      String sanitizeVariableMatcher = variableMatcher.replaceAll("^\"+|\"+$", "");
      if(data.containsKey(sanitizeStatement) && String.valueOf(data.get(sanitizeStatement)).equals(sanitizeVariableMatcher)) {
        return extractVariable(thenValue, nestedKey, data, inputData);
      } else {
        return extractVariable(elseValue, nestedKey, data, inputData);
      }
    }

    return StringUtils.EMPTY;
  }

  private Object generateEQStatement(String nestedKey, Map<String, Object> inputData, Map<String, Object> data, String code) {
    String statement = StringUtils.EMPTY;
    String variableMatcher = StringUtils.EMPTY;
    String thenValue = StringUtils.EMPTY;
    String elseValue = StringUtils.EMPTY;

    String codeStatement = code;
    for (String exactStatement : EQ_STATEMENTS) {
      String[] statementParts = codeStatement.split(Pattern.quote(exactStatement));
      if(exactStatement.equalsIgnoreCase(EQ_STATEMENT)) {
        statement = statementParts[0];
      } else if(exactStatement.equalsIgnoreCase(THEN_STATEMENT)) {
        variableMatcher = statementParts[0];
      } else if(exactStatement.equalsIgnoreCase(ELSE_STATEMENT)) {
        thenValue = statementParts[0];
      }
      else if(exactStatement.equalsIgnoreCase(END_STATEMENT)) {
        elseValue = statementParts[0];
      }

      if(!exactStatement.equalsIgnoreCase(END_STATEMENT)) {
        codeStatement = statementParts[1];
      }
    }

    if(!statement.isEmpty() && !variableMatcher.isEmpty() && !thenValue.isEmpty() && !elseValue.isEmpty()) {
      String sanitizeStatement = statement.replace(nestedKey + ".$.", "");
      String sanitizeVariableMatcher = variableMatcher.replaceAll("^\"+|\"+$", "");
      if(data.containsKey(sanitizeStatement) && String.valueOf(data.get(sanitizeStatement)).equals(sanitizeVariableMatcher)) {
        return extractVariable(thenValue, nestedKey, data, inputData);
      } else {
        return extractVariable(elseValue, nestedKey, data, inputData);
      }
    }

    return StringUtils.EMPTY;
  }

  private Object extractVariable(String code, String nestedKey,
      Map<String, Object> masterData, Map<String, Object> inputData) {

    Pattern pattern = Pattern.compile("\\\\\".*?\\\\\"|__IF__.*?__END__|[^\\\\\"]+");
    Matcher matcher = pattern.matcher(code);
    List<String> combineVariables = new ArrayList<>();
    while (matcher.find()) {
      combineVariables.add(matcher.group());
    }
    StringBuilder value = new StringBuilder();
    AtomicReference<Object> resultList = new AtomicReference<>(null);
    combineVariables.forEach(combineVariable -> {
      boolean status = false;

      // this is statement
      if(combineVariable.contains(IF_STATEMENT)) {
        Object generateData = generateData(nestedKey, inputData, masterData, combineVariable);
        if(generateData instanceof List<?>) {
          resultList.set(generateData);
        } else {
          value.append(generateData);
        }

        status = true;
      } else {
        // this is for array object mapping and statement
        if(combineVariable.contains(nestedKey)) {
          combineVariable = combineVariable.replace(nestedKey + ".$.", "");
          Object generateData = generateData(nestedKey, inputData, masterData, combineVariable);
          if(generateData instanceof List<?>) {
            resultList.set(generateData);
          } else {
            value.append(generateData);
          }

          status = true;
        }

        // this is non array object mapping
        if(inputData.containsKey(combineVariable)) {
          value.append(inputData.get(combineVariable));
          status = true;
        }
      }

      // if status false, it's not match for any condition. Apply static variable
      if(!status) {
        value.append(combineVariable);
      }
    });

    return Objects.nonNull(resultList.get()) ? resultList.get() : detectAndConvert(value.toString());
  }


  private static Object detectAndConvert(String input) {
    if (input == null || input.trim().isEmpty()) {
      return input; // Jika null atau kosong, tetap String
    }

    // Cek apakah mengandung huruf atau karakter selain angka dan titik
    if (!input.matches("[-+]?\\d*\\.?\\d+")) {
      return input; // Jika ada huruf atau karakter lain, tetap sebagai String
    }

    try {
      // Jika angka bulat dan tidak memiliki titik, cek apakah bisa jadi Integer atau Long
      if (!input.contains(".")) {
        long longValue = Long.parseLong(input);
        if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
          return (int) longValue; // Jika dalam range Integer, kembalikan sebagai Integer
        }
        return longValue; // Jika lebih besar dari Integer, kembalikan sebagai Long
      }

      // Jika mengandung titik, cek apakah bisa dikonversi ke Double
      return Double.parseDouble(input);
    } catch (NumberFormatException e) {
      return input; // Jika gagal parsing, tetap sebagai String
    }
  }

}
