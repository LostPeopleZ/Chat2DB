package ai.chat2db.community.domain.core.impl.ai;

import ai.chat2db.community.domain.api.model.ai.ModelConfigTestResponse;
import ai.chat2db.community.domain.api.model.request.ai.AiChatRuntimeResolveRequest;
import ai.chat2db.community.domain.api.model.request.ai.AiModelConfigSaveRequest;
import ai.chat2db.community.domain.core.converter.AiModelConfigConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AiModelConfigServiceImplBaseUrlTest {

    private static final long USER_ID = 42L;

    @TempDir
    Path tempDirectory;

    @ParameterizedTest
    @CsvSource({
            "https://example.com/v1, https://example.com",
            "https://example.com/v1/, https://example.com",
            "https://example.com, https://example.com",
            "https://example.com/, https://example.com",
            "https://example.com/compatible-mode/v1, https://example.com/compatible-mode",
            "https://example.com/v1beta, https://example.com/v1beta"
    })
    void openAiRuntimeBaseUrlToleratesTrailingV1(String configured, String expected) {
        assertEquals(expected, resolveBaseUrl("OPENAI", configured));
    }

    @Test
    void claudeRuntimeBaseUrlToleratesTrailingV1() {
        assertEquals("https://claude.example.com", resolveBaseUrl("CLAUDE", "https://claude.example.com/v1"));
    }

    @Test
    void minimaxRuntimeBaseUrlToleratesTrailingV1() {
        assertEquals("https://minimax.example.com", resolveBaseUrl("MINIMAX", "https://minimax.example.com/v1"));
    }

    @Test
    void kimiRuntimeBaseUrlToleratesTrailingV1() {
        assertEquals("https://kimi.example.com", resolveBaseUrl("KIMI", "https://kimi.example.com/v1"));
    }

    @Test
    void kimiRuntimeBaseUrlFallsBackToMoonshotDefault() {
        assertEquals("https://api.moonshot.cn", resolveBaseUrl("KIMI", null));
    }

    @Test
    void workbuddyRuntimeBaseUrlToleratesTrailingV1() {
        assertEquals("http://127.0.0.1:8000", resolveBaseUrl("WORKBUDDY2", "http://127.0.0.1:8000/v1"));
    }

    @Test
    void workbuddyRuntimeBaseUrlIsRequired() {
        AiChatRuntimeResolveRequest request = new AiChatRuntimeResolveRequest();
        request.setProvider("WORKBUDDY2");
        request.setModel("local-model");
        request.setBaseUrl("   ");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service().resolveRuntimeModel(request));
        assertEquals("baseUrl is required for WorkBuddy2", error.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "OPENAI, https://api.openai.com/v1/chat/completions",
            "MINIMAX, https://api.minimax.io/v1/chat/completions",
            "KIMI, https://api.moonshot.cn/v1/chat/completions"
    })
    void connectionTestUsesProviderDefaultBaseUrl(String provider, String expectedEndpoint) {
        assertEquals(expectedEndpoint, testEndpoint(provider, null));
    }

    @Test
    void minimaxConnectionTestKeepsCustomBaseUrl() {
        assertEquals("https://api.minimax.chat/v1/chat/completions",
                testEndpoint("MINIMAX", "https://api.minimax.chat/v1"));
    }

    @Test
    void workbuddyConnectionTestKeepsCustomBaseUrlWithoutApiKey() {
        assertEquals("http://127.0.0.1:8000/v1/chat/completions",
                testEndpoint("WORKBUDDY2", "http://127.0.0.1:8000/v1"));
    }

    @Test
    void workbuddyConnectionTestRequiresBaseUrl() {
        assertNull(testEndpoint("WORKBUDDY2", "   "));
    }

    private String resolveBaseUrl(String provider, String baseUrl) {
        AiChatRuntimeResolveRequest request = new AiChatRuntimeResolveRequest();
        request.setProvider(provider);
        request.setModel("gpt-test");
        request.setApiKey("sk-test-1234567890");
        request.setBaseUrl(baseUrl);
        return service().resolveRuntimeModel(request).getBaseUrl();
    }

    private String testEndpoint(String provider, String baseUrl) {
        AiModelConfigSaveRequest request = new AiModelConfigSaveRequest();
        request.setProvider(provider);
        request.setModel("test-model");
        request.setBaseUrl(baseUrl);
        ModelConfigTestResponse response = service().testModelConfig(request);
        return response.getEndpoint();
    }

    private AiModelConfigServiceImpl service() {
        return new AiModelConfigServiceImpl(new ObjectMapper().findAndRegisterModules(), new AiModelConfigConverter(),
                () -> USER_ID, tempDirectory.resolve("ai-model-configs.json"), null);
    }
}
