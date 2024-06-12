import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OllamaApiService {
    /*
        Here we set the url to call the ollama API
        The url comes from the corresponding ollama docker desktop container
        Make sure ollama is running and that a model has been installed
        https://hub.docker.com/r/ollama/ollama

        start the container with gpu support
        docker run -d --gpus=all -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama

        run the model locally
        docker exec -it ollama ollama run llama3
    */
    private static final String BASE_URL = "http://localhost:11434/api";
    private static final String ENDPOINT = "/generate";
    private static final String MODEL = "llama3:latest";
    private final MongoDBService mongoDBService= new MongoDBService();
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper is a class from the Jackson library used for processing JSON un java.
    private static final Logger logger = LoggerFactory.getLogger(OllamaApiService.class);
    /*
    Logger is an interface from the SLF4J API which was very handy to track what is sent and received from the ollamaAPI
    https://www.slf4j.org/api/org/slf4j/Logger.html
    This was very useful since I was getting multiple errors when sending the request and receiving the data from ollama API
    */

    public String askGrammarQuestion(String question) throws Exception {
        /* Create HttpClient
        CloseableHttpClient is preferred over HttpClient for its automatic resource management capabilities, ensuring that resources are properly released after use.
        Using CloseableHttpClient in a try-with-resources block guarantees that the close() method is called automatically, even if an exception is thrown.
        This is a best practice to avoid resource leaks.
         */
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create HttpPost request
            HttpPost request = new HttpPost(BASE_URL + ENDPOINT);
            request.setHeader("Content-Type", "application/json");

            // Create JSON payload
            GrammarRequest grammarRequest = new GrammarRequest(question,MODEL);
            String jsonPayload = objectMapper.writeValueAsString(grammarRequest);
            request.setEntity(new StringEntity(jsonPayload));

            // Log the request payload
            logger.debug("Sending jasonPayload: {}", jsonPayload);

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                InputStreamReader stream = new InputStreamReader(response.getEntity().getContent()); // converts bytes to characters according to character encoding.
                BufferedReader reader = new BufferedReader(stream); //reads text from a character-input stream, buffering characters to provide efficient reading of characters.
                StringBuilder completeResponse = new StringBuilder(); //StringBuilder is a mutable sequence of characters used for creating and manipulating strings efficiently.
                String line;
                while ((line = reader.readLine()) != null) {
                    // Log the raw response chunk
                    logger.info("API Response Chunk: " + line);

                    // Deserialize response chunk
                    GrammarResponse grammarResponse = objectMapper.readValue(line, GrammarResponse.class);

                    // Accumulate response
                    completeResponse.append(grammarResponse.getResponse());

                    // Check for errors in the response
                    if (grammarResponse.getError() != null) {
                        logger.error("API Error: " + grammarResponse.getError());
                        throw new Exception("API Error: " + grammarResponse.getError());
                    }
                }
                String answer = completeResponse.toString();

                mongoDBService.saveInteraction(new Interaction(question, answer));

                return answer;
            }
        }catch (Exception e) {
            logger.error("Exception occurred while asking grammar question", e);
            throw e;
        }
    }
    public List<String> generateTags(String question) throws Exception {
        List<String> tags = new ArrayList<>();
        String prompt = "Acting as an expert linguist who has vast experience in tagging texts, " +
                "generate a list of descriptive tags for the following English grammar question. " +
                "The tags must include: the grammar topic, the gramamr rule/s, difficulty level and CEFR level. " +
                "If relevant, it could also include parts of speech, lingusitc term and any specific grammar terms used. " +
                "Avoid any tag that is too general or not specific to que given question. " +
                "Tags should reflect the topic of what is being asked. " +
                "Only output a list of tags separated by commas. " +
                "If the question is in Sapnish, analyze it in English. Question: " + question;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create HttpPost request
            HttpPost request = new HttpPost(BASE_URL + ENDPOINT);
            request.setHeader("Content-Type", "application/json");

            // Create JSON payload
            GrammarRequest grammarRequest = new GrammarRequest(prompt,MODEL);
            String jsonPayload = objectMapper.writeValueAsString(grammarRequest);
            request.setEntity(new StringEntity(jsonPayload));

            // Log the request payload
            //logger.debug("Sending jasonPayload: {}", jsonPayload);

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                InputStreamReader stream = new InputStreamReader(response.getEntity().getContent());
                BufferedReader reader = new BufferedReader(stream);
                StringBuilder completeResponse = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    // Log the raw response chunk
                    //logger.info("API Response Chunk: " + line);

                    // Deserialize response chunk
                    GrammarResponse grammarResponse = objectMapper.readValue(line, GrammarResponse.class);

                    // Accumulate response
                    completeResponse.append(grammarResponse.getResponse());


                    // Check for errors in the response
                    if (grammarResponse.getError() != null) {
                        logger.error("API Error: " + grammarResponse.getError());
                        throw new Exception("API Error: " + grammarResponse.getError());
                    }
                }
                String answer = completeResponse.toString();
                for(String word : answer.split(",")){
                    tags.add(word);

                }
                mongoDBService.updateInteractionWithTags(question, tags);
                return tags;
            }
        }catch (Exception e) {
            logger.error("Exception occurred while asking grammar question", e);
            throw e;
        }
    }

    /*
    I don't generally use inner classes so here is a brief explanation of the rationale behind of it:
    Encapsulation:
        -as inner classes, both are only used within the context of the OllamaApiService.
        -since these classes can only be used within the context of the OllamaApiServiceit would be promoting better modularity and encapsulation.
    Contextual Relevance:
        -GrammarRequest and GrammarResponse classes are smeant to be used only by the OllamaApiService.
        -as inner classes, it clearly indicates that their purpose being closely related to the OllamaApiService.
    Code Organization:
        -having these classes together within OllamaApiService keeps related code in one place.
        -in some way, it can make the code easier to understand and maintain.
    Serialization and Deserialization of JSON:
        -Jackson library can easily serialize and deserialize complex objects to and from JSON.
        -Having dedicated classes makes this process straightforward and less error-prone.
        -it is important to check the naming of the attributes used with the corresponding parameters in the API.
    Extensibility:
        -being able to add fields in an easy way is very convenient to efficiently handle API request or response in the future.

    ollama API Parameters:
        https://hub.docker.com/r/ollama/ollama
        model: (required) the model name
        prompt: the prompt to generate a response for
        images: (optional) a list of base64-encoded images (for multimodal models such as llava)
        Advanced parameters (optional):

        format: the format to return a response in. Currently the only accepted value is json
        options: additional model parameters listed in the documentation for the Modelfile such as temperature
        system: system message to (overrides what is defined in the Modelfile)
        template: the prompt template to use (overrides what is defined in the Modelfile)
        context: the context parameter returned from a previous request to /generate, this can be used to keep a short conversational memory
        stream: if false the response will be returned as a single response object, rather than a stream of objects
        raw: if true no formatting will be applied to the prompt. You may choose to use the raw parameter if you are specifying a full templated prompt in your request to the API
        keep_alive: controls how long the model will stay loaded into memory following the request (default: 5m)
     */

    // Inner class for request payload
    static class GrammarRequest {
        private final String model;
        private final String prompt;


        public GrammarRequest(String prompt, String model) {
            this.prompt = prompt;
            this.model = model;
        }
        public String getModel() {
            return model;
        }

        public String getPrompt() {
            return prompt;
        }


    }

    // Inner class for response payload
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GrammarResponse {
        private String response; // To capture the response field
        private String error; // Optional: to capture error messages
        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }


    }

}
