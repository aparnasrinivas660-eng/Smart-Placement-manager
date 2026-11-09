package placeprep.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String category;
    private final String prompt;
    private final List<String> options;
    private final int correctIndex;

    public Question(String id, String category, String prompt,
                    List<String> options, int correctIndex) {
        this.id = id;
        this.category = category;
        this.prompt = prompt;
        this.options = new ArrayList<>(options);
        this.correctIndex = correctIndex;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return new ArrayList<>(options); }
    public int getCorrectIndex() { return correctIndex; }
}
