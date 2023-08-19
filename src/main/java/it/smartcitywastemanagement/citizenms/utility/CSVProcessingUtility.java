package it.smartcitywastemanagement.citizenms.utility;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class CSVProcessingUtility {


    public List<String[]> readCSVFile(MultipartFile file) throws CsvException, IOException{
        CSVReader csvReader = createCSVReader(file.getInputStream());
        return csvReader.readAll();
    }



    public CSVReader createCSVReader(InputStream inputStream) {
        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
        return new CSVReaderBuilder(new InputStreamReader(inputStream)).withCSVParser(csvParser).withSkipLines(1).build();
    }


}
