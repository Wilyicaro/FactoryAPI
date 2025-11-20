package wily.factoryapi.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public interface ModInfo {
    Collection<String> getAuthors();

    Optional<String> getHomepage();

    Optional<String> getIssues();

    Optional<String> getSources();

    Collection<String> getCredits();

    Collection<String> getLicense();

    String getDescription();

    Optional<String> getLogoFile(int i);

    Optional<Path> findResource(String s);

    default boolean containsResource(String s) {
        return findResource(s).isPresent();
    }

    default InputStream openResource(String s) throws IOException {
        return Files.newInputStream(findResource(s).orElseThrow());
    }

    String getId();

    String getVersion();

    String getName();

    default boolean isHidden(){
        return false;
    }
}
