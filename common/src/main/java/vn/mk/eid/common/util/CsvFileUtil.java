package vn.mk.eid.common.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * csv
 *
 * @author mk.com.vn
 * @date 2018/7/24 16:53
 */
public class CsvFileUtil {
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withEscape('\\').withQuoteMode(QuoteMode.NONE);
    private static final String CHARSET_NAME = "utf-8";

    /**
     * csv
     *
     * @param pathName
     * @param headers
     * @param records
     * @param isAppend
     */
    public static boolean writerCsvFile(String pathName,String fileName, String[] headers, List<List<Object>> records, Boolean isAppend) {
        File file = new File(pathName);
        if(!file.exists()){
            file.mkdirs();
        }
        boolean result = false;
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(pathName+fileName, isAppend), CHARSET_NAME)) {
            CSVPrinter csvPrinter = new CSVPrinter(outputStreamWriter, CSV_FORMAT);
            csvPrinter.printRecord(Arrays.asList(headers));
            for (List<Object> record : records) {
                csvPrinter.printRecord(record);
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * csv
     *
     * @param pathName
     * @param fileName
     * @param headers
     * @param records
     */
    public static boolean writerCsvFile(String pathName,String fileName, String[] headers, List<List<Object>> records) {
        return writerCsvFile(pathName,fileName, headers, records, true);
    }

    /**
     * csv json
     * @param line csv
     * @param header csv
     * @return json
     */
    public static String csvLineToJson(String line, String[] header) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t").append("{").append("\n");
        String[] tmp = line.split(",");
        for (int j = 0; j < tmp.length; j++) {
            sb.append("\t").append("\t\"").append(header[j]).append("\":\"").append(tmp[j]).append("\"");
            if (j < tmp.length - 1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        sb.append("\t}\n");
        return sb.toString();
    }
}
