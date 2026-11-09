package placeprep.storage;

import placeprep.model.AppData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class DataStore {
    private static final Path DATA_FILE = Path.of("data", "placeprep.dat");

    private DataStore() { }

    public static AppData load() throws IOException, ClassNotFoundException {
        if (!Files.exists(DATA_FILE)) {
            return new AppData();
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(DATA_FILE))) {
            Object object = input.readObject();
            if (object instanceof AppData appData) {
                return appData;
            }
            throw new IOException("The saved data file has an unexpected format.");
        }
    }

    public static void save(AppData data) throws IOException {
        Path parent = DATA_FILE.getParent();
        Files.createDirectories(parent);
        Path temporaryFile = parent.resolve("placeprep.tmp");

        try (ObjectOutputStream output =
                     new ObjectOutputStream(Files.newOutputStream(temporaryFile))) {
            output.writeObject(data);
        }

        try {
            Files.move(temporaryFile, DATA_FILE,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporaryFile, DATA_FILE,
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
