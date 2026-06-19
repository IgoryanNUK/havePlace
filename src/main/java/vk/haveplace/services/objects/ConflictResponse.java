package vk.haveplace.services.objects;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class ConflictResponse<T> {
    private List<T> conflictObjects;

    public ConflictResponse() {
        conflictObjects = new ArrayList<>();
    }

    public ConflictResponse(T conflictObject) {
        conflictObjects = new ArrayList<>();
        conflictObjects.add(conflictObject);
    }

    public ConflictResponse(Collection<T> conflictObjects) {
        this.conflictObjects = new ArrayList<>();
        this.conflictObjects.addAll(conflictObjects);
    }

    public void addConflict(T conflictObject) {
        conflictObjects.add(conflictObject);
    }
}
