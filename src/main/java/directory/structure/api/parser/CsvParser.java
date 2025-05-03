package directory.structure.api.parser;

import directory.structure.api.model.DirectoryStructure;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class CsvParser {


    /**
     * Parses a CSV file and maps its rows to a list of {@link DirectoryStructure} objects.
     *
     * <p>
     * This method reads the specified CSV file from the classpath, maps its rows to
     * {@link DirectoryStructure} objects using the Jackson CSV library, and returns the resulting list.
     * The CSV file is expected to have a header row that matches the fields of the
     * {@link DirectoryStructure} class.
     * </p>
     *
     * @param fileName The name of the CSV file to parse. The file must be located in the classpath.
     * @return A list of {@link DirectoryStructure} objects parsed from the CSV file.
     * @throws RuntimeException If the CSV file cannot be found, read, or parsed.
     */

    public List<DirectoryStructure> parseCsv(String fileName) {
        try {
            // Load the CSV file as an InputStream
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            // Define the CSV schema based on the DirectoryStructure class
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            // Create a CsvMapper instance
            CsvMapper csvMapper = new CsvMapper();

            // Map the CSV rows to DirectoryStructure objects
            MappingIterator<DirectoryStructure> iterator = csvMapper.readerFor(DirectoryStructure.class)
                    .with(schema)
                    .readValues(inputStream);

            // Return the list of DirectoryStructure objects
            return iterator.readAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse CSV file: " + fileName, e);
        }
    }
}