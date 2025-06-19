package ru.netology;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


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


    public static List<Employee> parseXML(String fileName) {

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new File(fileName));
//            Node root = doc.getDocumentElement();
//            System.out.println( "Корневой элемент: " + root.getNodeName());
            List<Employee> list = new ArrayList<>();
            NodeList nodeList = doc.getElementsByTagName("employee");

            for (int i = 0; i < nodeList.getLength(); i++) {
                NodeList empFields = nodeList.item(i).getChildNodes();
                Employee employee = new Employee();
                for (int j = 0; j < empFields.getLength(); j++) {
                    Node field = empFields.item(j);
                    if (Node.ELEMENT_NODE == field.getNodeType()) {
//                        System.out.println(field.getNodeName() +" " + field.getTextContent());
                        switch (field.getNodeName()) {
                            case "id" : employee.id = Integer.parseInt(field.getTextContent()); break;
                            case "age" : employee.age = Integer.parseInt(field.getTextContent()); break;
                            case "firstName" : employee.firstName = field.getTextContent(); break;
                            case "lastName" : employee.lastName = field.getTextContent(); break;
                            case "country" : employee.country = field.getTextContent(); break;
                        }
                    }
                }
                list.add(employee);
            }
            return list;

        } catch (ParserConfigurationException | IOException | SAXException e) {
//            throw new RuntimeException(e);
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


    // Чтение JSON из файла
    public static String readString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }


    public static List<Employee> jsonToList(String json) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        List<Employee> list = new ArrayList<>();

        // Это работает без всяких JSONArray
/*
        try {
            Type listType = new TypeToken<List<Employee>>() {}.getType();
            list = gson.fromJson(json, listType);
        } catch (JsonSyntaxException e) {
            System.out.println(e.getMessage());
        }
*/

        // Этот способ требуется по условиям задачи
        try {
            JSONParser parser = new JSONParser();
            JSONArray jarray = (JSONArray) parser.parse(json);
            for (Object o : jarray) {
                Employee emp = gson.fromJson(o.toString(), Employee.class);
                list.add(emp);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return list;
    }



    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        final String csvFileName = "data.csv";
        final String jsonFromCsvName = "data.json";
        final String xmlFileName = "data.xml";
        final String jsonFromXmlName = "data2.json";
        final String jsonFileName = "new_data.json";

        // Задача 1
        List<Employee> employees = parseCSV(columnMapping, csvFileName);
        String json = listToJson(employees);
        writeString(json, jsonFromCsvName);

        // Задача 2
        employees = parseXML(xmlFileName);
        json = listToJson(employees);
        writeString(json, jsonFromXmlName);

        // Задача 3
        json = readString(jsonFileName);
        employees = jsonToList(json);
        employees.forEach(System.out::println);

    }
}

