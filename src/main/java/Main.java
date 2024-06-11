import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        OllamaApiService service = new OllamaApiService();
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.print("Ask an English grammar question (or type 'exit' to quit): ");
            String question = scanner.nextLine();
            if(question.equalsIgnoreCase("exit")){
                break;
            }
            try{
                String answer = service.askGrammarQuestion(question);
                List<String> tags = service.generateTags(question);
                System.out.println("Then answer is: "+ answer);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to get a response from Ollama API.");
            }
        }
        scanner.close();
    }
}
