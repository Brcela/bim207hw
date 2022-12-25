import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class Main {
    public static void main(String[] args) throws IOException {
        // Check if a URL is provided as an argument
        if (args.length < 1) {
            System.out.println("Please provide a URL as an argument." +
                    "'java -jar target\\bim207hw.jar https://opennlp.apache.org/books-tutorials-and-talks.html'");
            return;
        }
        // Get the URL from the command line argument
        String url = args[0];

        // Use JSoup to extract the body text from the web page
        Document doc = Jsoup.connect(url).get();
        String text = doc.body().text();

        // Load the sentence detector model
        InputStream sentenceModelInputStream = Main.class.getResourceAsStream("/en-sent.bin");
        SentenceModel sentenceModel = new SentenceModel(sentenceModelInputStream);
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);

        // Split the text into sentences
        String[] sentences = sentenceDetector.sentDetect(text);

        // Load the tokenizer model
        InputStream tokenizerModelInputStream = Main.class.getResourceAsStream("/en-token.bin");
        TokenizerModel tokenizerModel = new TokenizerModel(tokenizerModelInputStream);
        TokenizerME tokenizer = new TokenizerME(tokenizerModel);

        // Load the name finder model
        InputStream nameFinderModelInputStream = Main.class.getResourceAsStream("/en-ner-person.bin");
        TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(nameFinderModelInputStream);
        NameFinderME nameFinder = new NameFinderME(nameFinderModel);

        // Extract names from each sentence
        for (String sentence : sentences) {
            // Tokenize the sentence
            String[] tokens = tokenizer.tokenize(sentence);

            // Find names in the sentence
            Span[] names = nameFinder.find(tokens);

            // Print the names
            for (Span name : names) {
                System.out.println(tokens[name.getStart()] + " " + tokens[name.getStart() + 1]);           }
        }
    }
}
