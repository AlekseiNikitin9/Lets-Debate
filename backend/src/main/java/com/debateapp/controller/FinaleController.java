// src/main/java/com/debateapp/controller/FinaleController.java
package com.debateapp.controller;

import com.debateapp.model.ChatRoom;
import com.debateapp.model.Game;
import com.debateapp.repository.GameRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/finale")
public class FinaleController {

    @Autowired
    private GameRepository gameRepository;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/{code}/score")
    public ResponseEntity<?> scoreFinale(@PathVariable String code) {
        Game game = gameRepository.findByCode(code).orElseThrow();
        ChatRoom room = game.getChatRoom();

        String p1 = game.getPlayer1();
        String p2 = game.getPlayer2();
        String chatLog = room != null && room.getChatHistory() != null ? room.getChatHistory() : "(no chat)";

        // Build a strict JSON-format prompt so frontend can parse reliably
        String prompt = """
        You are scoring a two-player debate based on the FULL chat transcript.

        Players:
        - Player 1: %s
        - Player 2: %s

        Scoring rules (total must be 100):
        - If Player 1 did much better: e.g., 70 for P1 and 30 for P2.
        - If tied: 50/50.
        - If one destroyed the other or the other said almost nothing: can be 90/10.

        Consider clarity, evidence, logic, rebuttals, and consistency. Be firm but fair. In the verdict be sure to refer
        to players by their names (not Player 1 and Player 2, but their actual names given in the prompt). If you notice 
        that the chat transcript is empty or is not informative enough to evaluate both players - say so in the verdict
        and give both players a tie.

        Chat transcript (verbatim):
        %s

        OUTPUT STRICTLY AS JSON with fields:
        {
          "p1Score": <integer 0-100>,
          "p2Score": <integer 0-100>,
          "verdict": "<1-2 sentence natural language explanation>"
        }
        Ensure p1Score + p2Score = 100.
        """.formatted(p1, p2, chatLog);

        try {
            String result = callOpenAI(prompt);

            // Try to parse what the model returned; if it isn't valid JSON, fall back to a neutral tie.
            JsonNode root;
            try {
                root = mapper.readTree(result);
            } catch (Exception badJson) {
                root = mapper.readTree("{\"p1Score\":50,\"p2Score\":50,\"verdict\":\"Tie (fallback).\"}");
            }

            int p1Score = root.path("p1Score").asInt(50);
            int p2Score = root.path("p2Score").asInt(50);
            String verdict = root.path("verdict").asText("Tie.");

            // Safety: normalize to 100 total
            int total = Math.max(1, p1Score + p2Score);
            if (total != 100) {
                double scale = 100.0 / total;
                p1Score = (int)Math.round(p1Score * scale);
                p2Score = 100 - p1Score;
            }

            return ResponseEntity.ok(Map.of(
                    "p1", p1,
                    "p2", p2,
                    "p1Score", p1Score,
                    "p2Score", p2Score,
                    "verdict", verdict
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "AI scoring failed"
            ));
        }
    }

    private String callOpenAI(String prompt) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        ArrayNode messages = mapper.createArrayNode();
        messages.add(mapper.createObjectNode().put("role","system")
                .put("content","You are a strict, fair debate judge. Output ONLY JSON when asked."));
        messages.add(mapper.createObjectNode().put("role","user").put("content", prompt));

        ObjectNode requestBodyJson = mapper.createObjectNode();
        requestBodyJson.put("model", "gpt-4o");
        requestBodyJson.set("messages", messages);
        requestBodyJson.put("temperature", 0.2);
        requestBodyJson.put("max_tokens", 300);

        // Force JSON mode so the model *must* return a JSON object
        ObjectNode rf = requestBodyJson.putObject("response_format");
        rf.put("type", "json_object");

        String body = mapper.writeValueAsString(requestBodyJson);
        System.out.println("[Finale] OpenAI request: " + body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[Finale] OpenAI status: " + response.statusCode());
        System.out.println("[Finale] OpenAI raw body: " + response.body());

        JsonNode root = mapper.readTree(response.body());
        String content = root.path("choices").get(0).path("message").path("content").asText();
        System.out.println("[Finale] OpenAI content: " + content); // should be pure JSON in JSON mode
        return content;
    }

}
