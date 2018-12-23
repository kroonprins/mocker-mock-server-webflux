package kroonprins.mocker.util;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

// TODO there must be a library out there somewhere :'(
public class Glob {

        public static class Finder
                extends SimpleFileVisitor<Path> {

            private final PathMatcher matcher;
            private List<Path> matches = new ArrayList<>();

            Finder(String pattern) {
                matcher = FileSystems.getDefault()
                        .getPathMatcher("glob:" + pattern);
            }

            void find(Path file) {
                Path name = file.getFileName();
                if (name != null && matcher.matches(name)) {
                    matches.add(file);
                }
            }

            List<Path> done() {
                return matches;
            }

            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs) {
                find(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                                                     BasicFileAttributes attrs) {
                find(dir);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file,
                                                   IOException exc) {
                return CONTINUE;
            }
        }

        @SneakyThrows
        public static List<Path> apply(String startingDir, String globPattern) {
            Finder finder = new Finder(globPattern);
            Files.walkFileTree(Paths.get(startingDir), finder);
            return finder.done();
        }
}
