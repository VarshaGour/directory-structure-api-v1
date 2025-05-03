package directory.structure.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DirectoryStructure {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("parentId")
    private Integer parentId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("size")
    private String size;

    @JsonProperty("classification")
    private String classification;

    @JsonProperty("checksum")
    private String checksum;

    // Default Constructor
    public DirectoryStructure() {
    }

    // Parameterized Constructor
    public DirectoryStructure(Integer id, Integer parentId, String name, String type, String size,
            String classification, String checksum) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.size = size;
        this.classification = classification;
        this.checksum = checksum;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}