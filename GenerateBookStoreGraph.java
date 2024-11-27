
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
                    @prefix ogo: <http://www.semanticweb.org/Games/ontology> .
                    @prefix owl: <http://www.w3.org/2002/07/owl#> .
                    
                    <http://www.semanticweb.org/Games/ontology> rdf:type owl:Ontology ;
                        owl:imports <file://./ontoTurtle.ttl> .
                    """);

            String line;
            br.readLine(); // to skip the column titles line
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                String[] lineTokens = line.split(",");
               
                if (lineTokens.length > 12) {

                    String game = removeQuote(lineTokens[0]);
                    String name = removeQuote(lineTokens[1]);
                    String releaseDate = removeQuote(lineTokens[2]);
                    String developer = removeQuote(lineTokens[3]);
                    String publisher = removeQuote(lineTokens[4]);
                    String platform = removeQuote(lineTokens[5]);
                    String genre = removeQuote(lineTokens[6]);
                    String series = removeQuote(lineTokens[7]);
                    String countryOfOrigin = removeQuote(lineTokens[8]);
                    String mainSubject = removeQuote(lineTokens[9]);
                    String officialWebsite = removeQuote(lineTokens[10]);
                    String designer = removeQuote(lineTokens[11]);
                    String composer = removeQuote(lineTokens[12]);

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
                                ogo:%s a ogo:Game;
                                    ogo:name \"%s\"^^xsd:string;
                                    ogo:releaseDate \"%s\"^^xsd:date;
                                    ogo:platform %s;
                                    ogo:genre \"%s\"^^xsd:string;
                                    ogo:series \"%s\"^^xsd:string.
                                    \n
                                    """;
                        bw.write(authorTemplateRDF.formatted(gameUUID, name, releaseDate, platform,
                                genre, series));

                    }
                }
            }
        }
    }
    
    public static String removeQuote(String value){

        if(value != null && value.length() >0 ){
            if(value.charAt(0) == '"'){
                value = value.substring(1, value.length()-1);
            }
        }
        return value;

    }

    public static void main(String[] args) throws IOException {
        try {
            generateTurtle("video_Game.csv", "test.ttl");
            generateCompetition("competitions_video_20.csv", "test.ttl");
            System.out.println("RDF data has been generated");
        } catch (IOException e) {
            System.out.println("I/O Error while Generating RDF Data");
            e.printStackTrace();
        }
    }

    public static void generateCompetition(String csvFileName, String turtleFileName) throws IOException {
        try (
            BufferedReader br = Files.newBufferedReader(Paths.get(csvFileName));
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(turtleFileName), StandardOpenOption.APPEND)) {
            // using Files.newBufferedReader and Files.newBufferedWrite enforce charEncoding
            // to UTF-8
            // Files.newBufferedWriter(Paths.get(turtleFileName)) is equivalent to
            // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new
            // FileOutputStream(turtleFileName), StandardCharsets.UTF_8));

            // write prefixes


            String line;
            
            br.readLine(); // to skip the column titles line
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                System.out.println(line);
                String[] lineTokens = line.split(",");
                /*"game","name","releaseDate","developer","publisher","platform","genre","series",
                        "countryOfOrigin","mainSubject","officialWebsite","designer","composer","character","gameMode" */

                String name = removeQuote(lineTokens[0]);
                String date = removeQuote(lineTokens[1]);
                String place = removeQuote(lineTokens[2]);
                String game = removeQuote(lineTokens[3]);
                String partic = removeQuote(lineTokens[4]); //Irrevelant
                String winner = removeQuote(lineTokens[5]);
                String cashPrize = removeQuote(lineTokens[6]);


                UUID gameUUID = authors.get(game);
                UUID competUUID = UUID.nameUUIDFromBytes(name.getBytes());
                if (gameUUID != null) {
                    // this is the first occurence of this author
                    // create triples describing him
                    // store UUID in the map with authorFullName as key
                    /*"game","name","releaseDate","developer","publisher","platform","genre","series",
                    "countryOfOrigin","mainSubject","officialWebsite","designer","composer","character","gameMode" */

                    String competitionTemplateRDF = """
                            ogo:%s a ogo:Competition;
                                ogo:competitionName \"%s\"^^xsd:string;
                                ogo:date \"%s\"^^xsd:date;
                                ogo:country %s;
                                ogo:cashPrize \"%s\"^^xsd:float;
                                ogo:winner \"%s\"^^xsd:string;
                                ogo:basedOn ogo:%s.
                                \n
                                """;
                    bw.write(competitionTemplateRDF.formatted(competUUID, name, date, place,
                            cashPrize, winner, gameUUID));
                    String relationTemplateRDF = """
                            ogo:%s ogo:hasCompetition ogo:%s.
                                \n
                                """;
                    bw.write(relationTemplateRDF.formatted(gameUUID, competUUID));

                }
            }
        }
    }
}

