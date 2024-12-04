
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
                    @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
                    @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

                    <http://www.semanticweb.org/Games/ontology> rdf:type owl:Ontology ;
                        owl:imports <file://./ontoTurtle.ttl> .
                    """);

            String line;
            br.readLine(); // to skip the column titles line
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                //System.out.println(line);
                String[] lineTokens = line.split(",");
               
                if (lineTokens.length > 8) {

                    String game = removeQuote(lineTokens[0]);
                    String name = removeQuote(lineTokens[1]);
                    String releaseDate = removeQuote(lineTokens[2]);
                    String developer = removeQuote(lineTokens[3]);
                    String publisher = removeQuote(lineTokens[4]);
                    String platform = removeQuote(lineTokens[5]);
                    String genre = removeQuote(lineTokens[6]);
                    String series = removeQuote(lineTokens[7]);

                    UUID gameUUID = authors.get(name);
                    //System.out.println("Game exist: " + gameUUID);
                    if (gameUUID == null) {
                        // this is the first occurence of this author
                        // create triples describing him
                        gameUUID = UUID.nameUUIDFromBytes(name.getBytes());
                        // store UUID in the map with authorFullName as key
                        authors.put(name, gameUUID);
                        /*"game","name","releaseDate","developer","publisher","platform","genre","series",
                        "countryOfOrigin","mainSubject","officialWebsite","designer","composer","character","gameMode" */
                        System.out.println(name);
                        String authorTemplateRDF = """
                                ogo:%s a ogo:Game;
                                    rdfs:label \"%s\"^^xsd:string;
                                    ogo:releaseDate \"%s\"^^xsd:date;
                                    ogo:platform <%s>;
                                    ogo:genre <%s>;
                                    ogo:series <%s>.
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
            generateTurtle("game_compet.csv", "test.ttl");
            generateCompetition("competitions_video_20.csv", "test.ttl");
            generatePublisher("publisherr.csv", "test.ttl");
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
                    if(cashPrize.isEmpty()){
                        cashPrize = "0";
                    }
                    String competitionTemplateRDF = """
                            ogo:%s a ogo:Competition;
                                rdfs:label \"%s\"^^xsd:string;
                                ogo:date \"%s\"^^xsd:date;
                                ogo:country \"%s\"^^xsd:string;
                                ogo:cashPrize \"%s\"^^xsd:float;
                                ogo:winner \"%s\"^^xsd:string;
                                ogo:basedOn ogo:%s .
                                \n
                                """;
                    bw.write(competitionTemplateRDF.formatted(competUUID, name, date, place,
                            cashPrize, winner, gameUUID));
                    String relationTemplateRDF = """
                            ogo:%s ogo:hasCompetition ogo:%s .
                                \n
                                """;
                    bw.write(relationTemplateRDF.formatted(gameUUID, competUUID));

                }
            }
        }
    }

    public static void generatePublisher(String csvFileName, String turtleFileName) throws IOException {
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
            HashMap<String, UUID> publisherHashMap = new HashMap<>();
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                
                String[] lineTokens = line.split(",");
                /*"game","name","releaseDate","developer","publisher","platform","genre","series",
                        "countryOfOrigin","mainSubject","officialWebsite","designer","composer","character","gameMode" */

                String name = removeQuote(lineTokens[0]);
                String game = removeQuote(lineTokens[1]);
                String founder = removeQuote(lineTokens[2]);
                String date = removeQuote(lineTokens[3]);

                UUID gameUUID = authors.get(game);
                UUID publiUUID = UUID.nameUUIDFromBytes(name.getBytes());
                
                if (gameUUID != null) {
                    if(!publisherHashMap.containsKey(name)){
                        String competitionTemplateRDF = """
                            ogo:%s a ogo:Publisher;
                                rdfs:label \"%s\"^^xsd:string;
                                ogo:fundation \"%s\"^^xsd:date
                        """;
                        if(!founder.isEmpty()){
                            competitionTemplateRDF += """
                                    ;
                                    ogo:foundedBy <%s>
                                    """;
                        }
                        competitionTemplateRDF += ".\n";
                        bw.write(competitionTemplateRDF.formatted(publiUUID, name, date, founder));         
                        publisherHashMap.put(name, publiUUID);
                    }
                    String relationTemplateRDF = """
                            ogo:%s ogo:publishedBy ogo:%s .
                                \n
                                """;

                                
                    bw.write(relationTemplateRDF.formatted(gameUUID, publiUUID));
                    String publisherHasPublisehd = """
                                ogo:%s ogo:hasPublished ogo:%s .
                                \n
                                """;
                    bw.write(publisherHasPublisehd.formatted(publiUUID, gameUUID ));
                }else{
                    if(!publisherHashMap.containsKey(name)){
                        String competitionTemplateRDF = """
                            ogo:%s a ogo:Publisher;
                                rdfs:label \"%s\"^^xsd:string;
                                ogo:fundation \"%s\"^^xsd:date
                                """;
                                if(!founder.isEmpty()){
                                    competitionTemplateRDF += """ 
                                            ;
                                            ogo:foundedBy <%s>.
                                            \n
                                            """;
                                            
                                }else{
                                    competitionTemplateRDF += """
                                            .
                                            \n
                                            """;
                                }
                                bw.write(competitionTemplateRDF.formatted(publiUUID, name, date,founder));
                        
                        publisherHashMap.put(name, publiUUID);
                    }
                    
                }
            }
        }
    }
}

