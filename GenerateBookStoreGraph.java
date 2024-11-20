
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This program generates a Turtle file from the ArtemisBookstore CSV File
 *
 * @author Philippe Genoud - LIG Steamer - Université Grenoble Alpes
 */
public class GenerateBookStoreGraph {

    // Maps to remember generated UUID for authors and publishers
    private static final Map<String, UUID> authors = new HashMap<>();
    private static final Map<String, UUID> publishers = new HashMap<>();

    /**
     *
     * @param csvFileName    name of the input CSV file
     * @param turtleFileName name of the output turtle file
     *
     * @throws java.io.IOException
     */
    public static void generateTurtle(String csvFileName, String turtleFileName) throws IOException {

        try (
                BufferedReader br = Files.newBufferedReader(Paths.get(csvFileName));
                BufferedWriter bw = Files.newBufferedWriter(Paths.get(turtleFileName))) {
            // using Files.newBufferedReader and Files.newBufferedWrite enforce charEncoding
            // to UTF-8
            // Files.newBufferedWriter(Paths.get(turtleFileName)) is equivalent to
            // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new
            // FileOutputStream(turtleFileName), StandardCharsets.UTF_8));

            // write prefixes
            bw.write("""
                    @prefix abo: <http://artemisBookstore.com/ontology#> .
                    @prefix abr: <http://artemisBookstore.com/resource/> .
                    @prefix dcterms: <http://purl.org/dc/terms/> .
                    @prefix foaf: <http://xmlns.com/foaf/0.1/> .
                    @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

                    """);

            String line;
            br.readLine(); // to skip the column titles line
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                String[] lineTokens = line.split(",");
                /*"game","name","releaseDate","developer","publisher","platform","genre","series",
                        "countryOfOrigin","mainSubject","officialWebsite","designer","composer","character","gameMode" */

                String game = lineTokens[0];
                String name = lineTokens[1];
                String releaseDate = lineTokens[2];
                String developer = lineTokens[3];
                String publisher = lineTokens[4];
                String platform = lineTokens[5];
                String genre = lineTokens[6];
                String series = lineTokens[7];
                String countryOfOrigin = lineTokens[8];
                String mainSubject = lineTokens[9];
                String officialWebsite = lineTokens[10];
                String designer = lineTokens[11];
                String composer = lineTokens[12];

                UUID gameUUID = authors.get(game);
                if (gameUUID == null) {
                    // this is the first occurence of this author
                    // create triples describing him
                    gameUUID = UUID.randomUUID();
                    // store UUID in the map with authorFullName as key
                    authors.put(game, gameUUID);
                    /*"game","name","releaseDate","developer","publisher","platform","genre","series",
                    "countryOfOrigin","mainSubject","officialWebsite","designer","composer","character","gameMode" */

                    String authorTemplateRDF = """
                            resource:%s a ontology:game;
                                   auPif:name %s;
                                   auPif:releaseDate %s;
                                   auPif:developer %s;
                                   auPif:publisher %s;
                                   auPif:platform %s;
                                   auPif:genre %s;
                                   auPif:series %s;
                                   auPif:countryOfOrigin %s;
                                   auPif:mainSubject %s;
                                   auPif:officialWebsite %s;
                                   auPif:designer %s;
                                   auPif:composer %s;
                                   \n
                                """;
                    bw.write(authorTemplateRDF.formatted(gameUUID, name, releaseDate, developer, publisher, platform,
                            genre, series, countryOfOrigin, mainSubject, officialWebsite, designer, composer));

                }
            }
        }
    }
    
    public static removeQuote(String value){

        if(value != null){
            
        }

    }

    public static void main(String[] args) throws IOException {
        try {
            generateTurtle("video_Game.csv", "test.ttl");
            System.out.println("RDF data has been generated");
        } catch (IOException e) {
            System.out.println("I/O Error while Generating RDF Data");
            e.printStackTrace();
        }
    }

}

