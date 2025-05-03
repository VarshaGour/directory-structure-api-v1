# Directory Structure API

The **Directory Structure API** is a Spring Boot application that provides functionality to parse a directory structure from a CSV file, generate an indented tree representation, and classify files based on their sensitivity (e.g., Top Secret, Secret, etc.).

---

## Features

- Parse directory structure data from a CSV file.
- Generate an indented tree representation of the directory.
- Retrieve the total size of public files.
- List non-public files not located in a specific folder.
- Filter files by classification (e.g., Secret, Top Secret).
- Handle errors gracefully, including file not found, parsing errors, and internal service errors.

---

## Setup Instructions

1. **Clone the Repository**
   ```
   git clone <repository-url>
   cd assignment-project
   ```

2. **Build the Project**
    ```
   gradlew build
   ```

3. **Run the Application**
   Start the Spring Boot application:
   ```
   gradlew bootRun
   ```

## Usage

- The application reads the `directory-structure.csv` file to populate the directory structure.
- It provides various functionalities to generate tree structures and classify files based on their sensitivity (Top secret, Secret, etc.).
- The output can be verified against the provided text files in the resources directory.

## Testing

Unit tests are included for the `DirectoryService` and `DirectoryControllerIntegrationTest` classes to ensure the functionality works as expected. Run the tests using:
```
gradle test
gradle integrationTest
```

## API Endpoints
 ```
 Get Directory Information
 Endpoint: /api/directory/find
 Method: GET
 Query Parameters:

 operation (required): The type of operation to perform. Supported values:
 tree: Returns the indented tree structure of the directory.
 public-size: Returns the total size of public files.
 non-public-folder11: Returns non-public files in folder 11.
 secret-or-top-secret: Returns files classified as Secret or Top Secret.
 files-by-classification: Returns files filtered by a specific classification type.
 classificationType (optional): Required for files-by-classification.

```