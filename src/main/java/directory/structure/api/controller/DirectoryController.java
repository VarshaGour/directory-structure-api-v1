package directory.structure.api.controller;

import directory.structure.api.service.DirectoryService;
import directory.structure.api.exceptionHandler.ParsingException;

import java.io.FileNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import directory.structure.api.util.Classification;
import io.vavr.control.Try;

@RestController
@RequestMapping("/api/directory")
public class DirectoryController {

    @Autowired
    private DirectoryService directoryService;

/**
 * Handles various directory-related operations based on the provided operation type.
 *
 * @param operation The type of operation to perform. Supported values are:
 *                  <ul>
 *                      <li><b>tree</b>: Returns the indented tree structure of the directory.</li>
 *                      <li><b>public-size</b>: Returns the total size of public files.</li>
 *                      <li><b>non-public-folder11</b>: Returns non-public files in folder 11.</li>
 *                      <li><b>secret-or-top-secret</b>: Returns files classified as Secret or Top Secret.</li>
 *                      <li><b>files-by-classification</b>: Returns files filtered by a specific classification type.</li>
 *                  </ul>
 * @param classificationType (Optional) The classification type to filter files by. 
 *                           Required only for the "files-by-classification" operation.
 * @return The result of the requested operation. The return type varies based on the operation:
 *         <ul>
 *             <li>For "tree": A string representing the indented tree structure.</li>
 *             <li>For "public-size": A numeric value representing the total size of public files.</li>
 *             <li>For "non-public-folder11": A string listing non-public files in folder 11.</li>
 *             <li>For "secret-or-top-secret": A string listing Secret or Top Secret files.</li>
 *             <li>For "files-by-classification": A string listing files filtered by the specified classification.</li>
 *         </ul>
 * @throws FileNotFoundException If a required file is not found.
 * @throws IllegalArgumentException If an invalid operation is provided or if the "classificationType"
 *                                  parameter is missing for the "files-by-classification" operation.
 * @throws ParsingException If there is an error parsing the file.
 * @throws RuntimeException For any other internal service errors.
 */

    @GetMapping("/find")
    public Object handleAction(
            @RequestParam("operation") String operation,
            @RequestParam(value = "classificationType", required = false) String classificationType) {
    
        return Try.of(() -> {
            switch (operation.toLowerCase()) {
                case "tree":
                    return directoryService.getIndentedTree();
                case "public-size":
                    return directoryService.getPublicFilesSize();
                case "non-public-folder11":
                    return directoryService.getNonPublicFilesInFolder11();
                case "secret-or-top-secret":
                    return directoryService.getSecretOrTopSecretFiles();
                case "files-by-classification":
                    if (classificationType == null) {
                        return "classificationType query parameter is required for this operation.";
                    }
                    return directoryService.getFilesByClassification(
                            Classification.fromValue(classificationType));
                default:
                    return "Invalid operation: " + operation ;
            }
        }).getOrElseGet(throwable -> {
            if (throwable instanceof FileNotFoundException) {
                return "Error: File not found. Please check the file path.";
            } else if (throwable instanceof IllegalArgumentException) {
                return "Error: " + throwable.getMessage();
            } else if (throwable instanceof ParsingException) {
                return "Error: Failed to parse the file. Please check the file format.";
            } else {
                return "Error: An internal service error occurred. Please try again later.";
            }
        });
    }
}
