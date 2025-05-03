package test.main.java.directory.structure.api.controller;

import directory.structure.api.service.DirectoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DirectoryController.class)
class DirectoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DirectoryService directoryService;

    @Test
    void testGetIndentedTree() throws Exception {
        // Mock the service response
        Mockito.when(directoryService.getIndentedTree()).thenReturn("Indented Tree Structure");

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/directory/find")
                        .param("operation", "tree"))
                .andExpect(status().isOk())
                .andExpect(content().string("Indented Tree Structure"));
    }

    @Test
    void testGetPublicFilesSize() throws Exception {
        // Mock the service response
        Mockito.when(directoryService.getPublicFilesSize()).thenReturn(123L);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/directory/find")
                        .param("operation", "public-size"))
                .andExpect(status().isOk())
                .andExpect(content().string("123"));
    }

    @Test
    void testGetNonPublicFilesInFolder11() throws Exception {
        // Mock the service response
        Mockito.when(directoryService.getNonPublicFilesInFolder11()).thenReturn("Non-Public Files");

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/directory/find")
                        .param("operation", "non-public-folder11"))
                .andExpect(status().isOk())
                .andExpect(content().string("Non-Public Files"));
    }

    @Test
    void testGetFilesByClassification() throws Exception {
        // Mock the service response
        Mockito.when(directoryService.getFilesByClassification(eq("SECRET"))).thenReturn("Secret Files");

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/directory/find")
                        .param("operation", "files-by-classification")
                        .param("classificationType", "SECRET"))
                .andExpect(status().isOk())
                .andExpect(content().string("Secret Files"));
    }

    @Test
    void testInvalidOperation() throws Exception {
        // Perform the GET request with an invalid operation
        mockMvc.perform(get("/api/directory/find")
                        .param("operation", "invalid-operation"))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid operation: invalid-operation"));
    }

    @Test
    void testMissingClassificationType() throws Exception {
        // Perform the GET request without classificationType for "files-by-classification"
        mockMvc.perform(get("/api/directory/find")
                        .param("operation", "files-by-classification"))
                .andExpect(status().isOk())
                .andExpect(content().string("classificationType query parameter is required for this operation."));
    }

    @Test
    void testFileNotFound() throws Exception {
        // Mock the service to throw FileNotFoundException
        Mockito.when(directoryService.getIndentedTree()).thenThrow(new java.io.FileNotFoundException("File not found"));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/directory/find")
                        .param("operation", "tree"))
                .andExpect(status().isOk())
                .andExpect(content().string("Error: File not found. Please check the file path."));
    }

    @Test
    void testParsingException() throws Exception {
        // Mock the service to throw ParsingException
        Mockito.when(directoryService.getIndentedTree()).thenThrow(new directory.structure.api.exceptionHandler.ParsingException("Parsing error"));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/directory/find")
                        .param("operation", "tree"))
                .andExpect(status().isOk())
                .andExpect(content().string("Error: Failed to parse the file. Please check the file format."));
    }

    @Test
    void testInternalServiceError() throws Exception {
        // Mock the service to throw a generic exception
        Mockito.when(directoryService.getIndentedTree()).thenThrow(new RuntimeException("Internal error"));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/directory/find")
                        .param("operation", "tree"))
                .andExpect(status().isOk())
                .andExpect(content().string("Error: An internal service error occurred. Please try again later."));
    }
}