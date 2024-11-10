package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Tag {
    private final String title;
    private long id;

    public Tag(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
