package directory.structure.api.service;

import directory.structure.api.model.DirectoryStructure;
import directory.structure.api.util.Classification;
import directory.structure.api.parser.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

@Service
public class DirectoryService {

        @Autowired
        private final CsvParser csvParser;

        private List<DirectoryStructure> directoryList;

        public DirectoryService(CsvParser csvParser) {
                this.csvParser = csvParser;
        }

        /**
         * Loads the directory structure from the CSV file and caches it at the time of
         * service startup.
         */
        @PostConstruct
        public void initializeCache() {
                loadDirectoryStructure("directory-structure.csv");
        }

        /**
         * Loads the directory structure from the specified CSV file.
         *
         * @param fileName The name of the CSV file to load.
         */
        public void loadDirectoryStructure(String fileName) {
                directoryList = csvParser.parseCsv(fileName);
        }

        /**
         * Generates an indented tree structure representing the directory hierarchy.
         *
         * <p>
         * The tree structure is built using the directory data loaded from the CSV
         * file.
         * Each directory's size is calculated as the sum of the sizes of all its
         * children.
         * Child nodes are listed alphabetically, and the tree is indented based on the
         * depth
         * of each node in the hierarchy.
         * </p>
         *
         * @return A string containing the indented tree structure. Each line represents
         *         a node
         *         in the tree, formatted with its name, type, size, and additional
         *         attributes
         *         (e.g., classification and checksum for files).
         * @throws IllegalStateException If no root element (a node with
         *                               {@code parentId == null}) is found.
         */
        public String getIndentedTree() {
                // Build a map of parentId to children for easy traversal
                Map<Integer, List<DirectoryStructure>> childrenMap = directoryList.stream()
                                .collect(Collectors.groupingBy(d -> d.getParentId() == null ? -1 : d.getParentId()));

                // Find the root element (parentId == null)
                DirectoryStructure root = directoryList.stream()
                                .filter(d -> d.getParentId() == null)
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("No root element found"));

                // Recursive method to build the tree
                StringBuilder treeBuilder = new StringBuilder();
                buildTree(root, childrenMap, treeBuilder, 0);

                return treeBuilder.toString();
        }

        private void buildTree(DirectoryStructure node, Map<Integer, List<DirectoryStructure>> childrenMap,
                        StringBuilder treeBuilder, int level) {
                // Indent based on the level
                treeBuilder.append(" ".repeat(level * 2))
                                .append("name = ").append(node.getName())
                                .append(", type = ").append(node.getType())
                                .append(", size = ").append(calculateSize(node, childrenMap));

                if ("File".equalsIgnoreCase(node.getType())) {
                        treeBuilder.append(", classification = ").append(node.getClassification())
                                        .append(", checksum = ").append(node.getChecksum());
                }

                treeBuilder.append("\n");

                // Get children and sort them alphabetically by name
                List<DirectoryStructure> children = childrenMap.getOrDefault(node.getId(), new ArrayList<>());
                children.sort(Comparator.comparing(DirectoryStructure::getName));

                // Recursively build the tree for each child
                for (DirectoryStructure child : children) {
                        buildTree(child, childrenMap, treeBuilder, level + 1);
                }
        }

        private int calculateSize(DirectoryStructure node, Map<Integer, List<DirectoryStructure>> childrenMap) {
                if ("File".equalsIgnoreCase(node.getType())) {
                        return Integer.parseInt(node.getSize());
                }

                // Sum the sizes of all children
                return childrenMap.getOrDefault(node.getId(), new ArrayList<>()).stream()
                                .mapToInt(child -> calculateSize(child, childrenMap))
                                .sum();
        }

        /**
         * Retrieves a list of file names classified as either "Secret" or "Top secret".
         *
         * <p>
         * This method filters the directory structure to include only files with the
         * classification "Secret" or "Top secret". The resulting file names are sorted
         * alphabetically and returned as a single string, with each file name on a new
         * line.
         * </p>
         *
         * @return A string containing the names of all files classified as "Secret" or
         *         "Top secret", sorted alphabetically, with each name on a new line.
         */
        public String getSecretOrTopSecretFiles() {
                return directoryList.stream()
                                .filter(d -> "Secret".equalsIgnoreCase(d.getClassification())
                                                || "Top secret".equalsIgnoreCase(d.getClassification()))
                                .sorted(Comparator.comparing(DirectoryStructure::getName)) // Sort alphabetically by
                                                                                           // name
                                .map(d -> String.format(
                                                "name = %s, type = %s, size = %s, classification = %s, checksum = %s",
                                                d.getName(), d.getType(), d.getSize(), d.getClassification(),
                                                d.getChecksum())) // Format the output
                                .collect(Collectors.joining("\n"));
        }

        /**
         * Calculates the total size of all files classified as "Public".
         *
         * <p>
         * This method filters the directory structure to include only files with the
         * classification "Public". It then calculates the total size of these files by
         * summing their sizes.
         * </p>
         *
         * @return The total size of all "Public" files as a long value.
         */
        public long getPublicFilesSize() {
                return directoryList.stream()
                                .filter(d -> "Public".equalsIgnoreCase(d.getClassification()))
                                .mapToLong(d -> Long.parseLong(d.getSize()))
                                .sum();
        }

        /**
         * Retrieves a list of non-public files located under "folder11" and its
         * subfolders.
         *
         * <p>
         * This method filters the directory structure to include only files that meet
         * the following criteria:
         * <ul>
         * <li>The file is located under "folder11" or any of its child folders.</li>
         * <li>The file type is "File".</li>
         * <li>The classification is not "Public".</li>
         * </ul>
         * The resulting files are sorted alphabetically by their names and formatted as
         * a string, where each file's details are on a new line.
         * </p>
         *
         * @return A string containing the details of non-public files under "folder11"
         *         and its subfolders, sorted alphabetically, with each file's details
         *         formatted as:
         *
         *         <pre>
         *         name = [file name], type = [file type], size = [file size],
         *         classification = [file classification], checksum = [file checksum]
         *         </pre>
         */

        public String getNonPublicFilesInFolder11() {
                // Find the ID of "folder11"
                Integer folder11Id = directoryList.stream()
                                .filter(d -> "folder11".equalsIgnoreCase(d.getName())
                                                && "directory".equalsIgnoreCase(d.getType()))
                                .map(DirectoryStructure::getId)
                                .findFirst()
                                .orElse(null);

                if (folder11Id == null) {
                        return ""; // Return empty if "folder11" is not found
                }

                // Collect all file nodes under "folder11" and its subfolders
                return directoryList.stream()
                                .filter(d -> isDescendantOfFolder(d, folder11Id) // Check if the file is under
                                                                                 // "folder11"
                                                && "File".equalsIgnoreCase(d.getType()) // Ensure it's a file
                                                && !"Public".equalsIgnoreCase(d.getClassification())) // Exclude
                                                                                                      // "Public"
                                                                                                      // classification
                                .sorted(Comparator.comparing(DirectoryStructure::getName)) // Sort alphabetically by
                                                                                           // name
                                .map(d -> String.format(
                                                "name = %s, type = %s, size = %s, classification = %s, checksum = %s",
                                                d.getName(), d.getType(), d.getSize(), d.getClassification(),
                                                d.getChecksum())) // Format the output
                                .collect(Collectors.joining("\n")); // Join details with a newline
        }

        // Helper method to check if a node is a descendant of a given folder
        private boolean isDescendantOfFolder(DirectoryStructure node, Integer folderId) {
                if (node.getParentId() == null) {
                        return false; // Root node, not a descendant
                }
                if (node.getParentId().equals(folderId)) {
                        return true; // Direct child of the folder
                }
                // Recursively check the parent
                return directoryList.stream()
                                .filter(d -> d.getId().equals(node.getParentId()))
                                .anyMatch(parent -> isDescendantOfFolder(parent, folderId));
        }

        /**
         * Retrieves a list of files based on the specified classification.
         *
         * <p>
         * This method filters the directory structure to include only files that match
         * the given classification.
         * The resulting files are sorted alphabetically by their names and formatted as
         * a string, where each file's
         * details are on a new line.
         * </p>
         *
         * @param classification The classification to filter files by. This determines
         *                       which files will be included
         *                       in the result based on their classification value.
         * @return A string containing the details of files matching the specified
         *         classification, sorted alphabetically,
         *         with each file's details formatted as:
         *
         *         <pre>
         *         name = [file name], type = [file type], size = [file size],
         *         classification = [file classification], checksum = [file checksum]
         *         </pre>
         */

        public String getFilesByClassification(Classification classification) {
                return directoryList.stream()
                                .filter(d -> classification.getValue().contains(d.getClassification())
                                                && "File".equalsIgnoreCase(d.getType()))
                                .sorted(Comparator.comparing(DirectoryStructure::getName)) // Sort alphabetically by
                                                                                           // name
                                .map(d -> String.format(
                                                "name = %s, type = %s, size = %s, classification = %s, checksum = %s",
                                                d.getName(), d.getType(), d.getSize(), d.getClassification(),
                                                d.getChecksum())) // Format the
                                // output
                                .collect(Collectors.joining("\n"));
        }

}