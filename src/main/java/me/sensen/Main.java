package me.sensen;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * @author Sensen
 */
public class Main {
    public static void main(String[] args) {

        try {
            String runtimePath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(1);
            if(args.length == 0) {
                if(runtimePath.endsWith(".jar")) {
                    Runtime.getRuntime().exec("cmd /c start cmd /k java -jar " + runtimePath + " run");
                    return;
                }
            }
        } catch (Exception ignored) {
        }

        confirm();
    }

    public static void confirm() {
        System.out.println("Would you like to sync TUF levels and save to Excel? (y or n)");
        Scanner confirmScr = new Scanner(System.in);
        String confirmScrMsg = confirmScr.next().toLowerCase();
        switch (confirmScrMsg) {
            case "y":
                get();
                break;
            case "n":
                System.exit(0);
                break;
            default:
                System.out.println("I don't know what it is! Please try again!");
                confirm();
                break;
        }
    }

    public static void get(){
        System.out.println("Syncing...");
        String respond = httpGet("https://be.tuforums.com/levels") + "\n";
        if (!read("result.json").isEmpty()) {
            String oldResult = read("result.json");
            if (oldResult.equals(respond)) {
                System.out.println("Nothing update!");
                confirm();
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String formattedDateTime = now.format(formatter);

            Path sourcePath = Paths.get("result.xlsx");
            Path targetPath = Paths.get("backup", formattedDateTime + ".xlsx");
            String backFilePath = "backup\\" + formattedDateTime + ".json";
            File backupFile = new File(backFilePath);
            backupFile.getParentFile().mkdirs();
            try {
                Files.move(sourcePath, targetPath);
            } catch (Exception e) {
                System.out.println("Something wrong, Please contact SensenPlayer! ID1");
            }
            write(oldResult, backFilePath);
            write(respond,"result.json");
            saveToExcel(respond);
            System.out.println("Success!");
            return;
        }
        saveToExcel(respond);
        write(respond,"result.json");

        System.out.println("Success!");
    }

    public static String httpGet(String url) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("Something wrong, Please contact SensenPlayer! ID2");
        }
        return result.toString();
    }

    public static void write(String cont, String path) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(Files.newOutputStream(new File(path).toPath()), StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(cont);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Something wrong, Please contact SensenPlayer! ID3");
        }
    }

    public static String read(String path) {
        File file = new File(path);
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder res = new StringBuilder();
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    res.append(line).append("\n");
                }
            } catch (IOException ignored) {
            }
            isr.close();
            return res.toString();
        } catch (IOException ignored) {
        }
        return "";
    }

    public static void saveToExcel(String jsonData) {

        // Parse JSON data
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("results");
        Type listType = new TypeToken<List<Level>>(){}.getType();
        List<Level> levelList = gson.fromJson(jsonArray, listType);

        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Information");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Song");
            headerRow.createCell(2).setCellValue("Artist");
            headerRow.createCell(3).setCellValue("Creator");
            headerRow.createCell(4).setCellValue("Diff");
            headerRow.createCell(5).setCellValue("Pgu_diff");
            // Populate data rows
            int rowIndex = 1;

            for (Level levels : levelList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(levels.getId());
                row.createCell(1).setCellValue(levels.getSong());
                row.createCell(2).setCellValue(levels.getArtist());
                row.createCell(3).setCellValue(levels.getCreator());
                row.createCell(4).setCellValue(levels.getDiff());
                row.createCell(5).setCellValue(levels.getPguDiff());
            }

            // Write the Excel file
            try (FileOutputStream outputStream = new FileOutputStream("result.xlsx")) {
                workbook.write(outputStream);
                System.out.println("Excel file has been created successfully.");
            }
        } catch (IOException e) {
            System.out.println("Something wrong, Please contact SensenPlayer! ID4");
        }
    }
}