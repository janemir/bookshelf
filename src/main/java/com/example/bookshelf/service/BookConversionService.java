package com.example.bookshelf.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.*;

@Service
public class BookConversionService {

    @Async
    public void convertBookToHtml(String filePath) {
        File inputFile = new File(filePath);
        String htmlPath = filePath + ".html";
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(htmlPath))) {
            writer.println("<html><body><pre>");
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
            writer.println("</pre></body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}