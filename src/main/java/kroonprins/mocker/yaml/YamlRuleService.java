package kroonprins.mocker.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import kroonprins.mocker.RuleService;
import kroonprins.mocker.model.Rule;
import kroonprins.mocker.util.Glob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Service
public class YamlRuleService implements RuleService {
    private final ObjectMapper yaml;
    // TODO Jackson2JsonDecoder works with Flux => can be used?

    private final String rulesBaseDir;
    private final String rulesGlobPattern;

    public YamlRuleService(
            @Value("${mocker.rules.base:rules}") String rulesBaseDir,
            @Value("${mocker.rules.glob:*.yaml}") String rulesGlobPattern) {
        this.yaml = new ObjectMapper(new YAMLFactory());
        this.rulesBaseDir = rulesBaseDir;
        this.rulesGlobPattern = rulesGlobPattern;
    }

    public Flux<Rule> produceRules() {
        return Flux.fromStream(
                Glob.apply(rulesBaseDir, rulesGlobPattern).stream()
                        .map(this::readFile)
                        .flatMap(Optional::stream)
                        .map(this::fromString)
                        .flatMap(Optional::stream)
        );
    }

    public Optional<String> readFile(Path path) {
        try {
            return Optional.of(Files.readString(path));
        } catch (IOException exc) {
            log.warn("Error reading file %s", path, exc);
            return Optional.empty();
        }
    }

    public Optional<Rule> fromString(String yamlRuleAsString) {
        try {
            return Optional.of(yaml.readValue(yamlRuleAsString, Rule.class));
        } catch (IOException exc) {
            log.warn("Error reading rule {}", yamlRuleAsString, exc);
            return Optional.empty();
        }
    }
}
