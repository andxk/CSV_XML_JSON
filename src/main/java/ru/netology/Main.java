package ru.netology;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;


public class Main {


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy<>();
            strategy.setColumnMapping(columnMapping);
            strategy.setType(Employee.class);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            List<Employee> employees = csv.parse();
//            employees.forEach(System.out::println);

            return employees;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }



    public static String listToJson(List<Employee> list) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .setPrettyPrinting()
                .create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
//        System.out.println(json);
        return json;
    }


    public static void writeString(String str, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(str);
            file.flush();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
//        System.out.println("Hello world!");

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String fileName = "data.csv";
        String jsonName = "data.json";

        List<Employee> employees = parseCSV(columnMapping, fileName);

        String json = listToJson(employees);

        writeString(json, jsonName);

    }
}