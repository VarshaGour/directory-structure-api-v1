package directory.structure.api.service;

import directory.structure.api.util.Classification;
import directory.structure.api.model.DirectoryStructure;
import directory.structure.api.parser.CsvParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DirectoryServiceTest {

    @InjectMocks
    private DirectoryService directoryService;

    @Mock
    private CsvParser csvParser;

    private List<DirectoryStructure> mockDirectoryList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock directory data
        mockDirectoryList = Arrays.asList(
                new DirectoryStructure(1, null, "folder1", "Directory", "100", Classification.PUBLIC.getValue(),
                        "checksum1"),
                new DirectoryStructure(2, 1, "file1", "File", "10", Classification.SECRET.getValue(), "checksum2"),
                new DirectoryStructure(3, 1, "file6", "File", "60", Classification.SECRET.getValue(), "checksum3"),
                new DirectoryStructure(4, 1, "file8", "File", "80", Classification.SECRET.getValue(), "checksum4"),
                new DirectoryStructure(5, 1, "file9", "File", "90", Classification.TOP_SECRET.getValue(), "checksum5"));

        // Mock the CsvParser to return the mock directory list
        when(csvParser.parseCsv("directory-structure.csv")).thenReturn(mockDirectoryList);

        // Initialize the directory list in the service
        directoryService.loadDirectoryStructure("directory-structure.csv");
    }

    private String readFileContent(String filePath) throws IOException {
        return Files.readString(Path.of(filePath)).trim();
    }

    @Test
    void testGetNonPublicFilesInFolder11() throws IOException {
        String expectedContent = readFileContent("src/main/resources/non-public-folder11.csv");
        String actualContent = directoryService.getNonPublicFilesInFolder11();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testGetFilesByClassificationSecretOrTopSecret() throws IOException {
        String expectedContent = readFileContent("src/main/resources/secret-or-top-secret.csv");
        String actualContent = directoryService.getSecretOrTopSecretFiles();
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testGetFilesByClassificationSecret() throws IOException {
        String expectedContent = readFileContent("src/main/resources/secret.csv");
        String actualContent = directoryService.getFilesByClassification(Classification.SECRET);
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testGetFilesByClassificationTopSecret() throws IOException {
        String expectedContent = readFileContent("src/main/resources/top-secret.csv");
        String actualContent = directoryService.getFilesByClassification(Classification.TOP_SECRET);
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testGetIndentedTree() throws IOException {
        String expectedContent = readFileContent("src/main/resources/tree.csv");
        String actualContent = directoryService.getIndentedTree();
        assertEquals(expectedContent, actualContent);
    }
}