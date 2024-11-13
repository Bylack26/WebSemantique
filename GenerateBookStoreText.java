package com.artemisbookstore.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

/**
 * This program generates a text file from the ArtemisBookstore CSV data
 * stored in data/artemisBookstoreData-v1.csv. The generated turtle data
 * is stored in data/generated/artemisBookstoreData-v1.txt
 *
 * @author Philippe Genoud - LIG Steamer - Université Grenoble Alpes
 */
public class GenerateBookStoreText {

    /**
     *
     * @param csvFileName    name of the input CSV file
     * @param textFilName name of the output text file
     *
     * @throws java.io.IOException
     */
    public static void generateTurtle(String csvFileName, String textFilName) throws IOException {

        try (BufferedReader br = Files.newBufferedReader(Paths.get(csvFileName));
                BufferedWriter bw = Files.newBufferedWriter(Paths.get(textFilName))) {
            // using Files.newBufferedReader and Files.newBufferedWriter enforce
            // charEncodingto UTF-8.
            // Files.newBufferedWriter(Paths.get(turtleFileName)) is equivalent to
            // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new
            // FileOutputStream(turtleFileName), StandardCharsets.UTF_8));

            String line;
            br.readLine(); // to skip the column titles line
            while ((line = br.readLine()) != null) {
                // split the line in using the ';' (semicolon)
                String[] lineTokens = line.split(";");

                String authorFamilyName = lineTokens[0];
                String authorGivenName = lineTokens[1];
                String bookTitle = lineTokens[2];
                int pagesNb = Integer.parseInt(lineTokens[3]);
                String isbn = lineTokens[4];
                String publisherName = lineTokens[5];

                String outputLine = """
                        -------------------------------------------------------------------------------------------------------
                        Auteur [nom:%s, prénom:%s]
                        Editeur [nom: %s]
                        Livre [titre: %s, nbre pages: %d, isbn: %s]
                            """
                        .formatted(authorGivenName, authorFamilyName, publisherName, bookTitle, pagesNb, isbn);
                // System.out.println(outputLine);
                bw.write(outputLine);
            } // end of file
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            genTurtle("data/artemisBookstoreData-v1.csv", "data/generated/artemisBookstoreData-v1-en.ttl");
            System.out.println("\nText data has been generated");
        } catch (IOException e) {
            System.out.println("I/O Error while Generating Text Data");
            e.printStackTrace();
        }
    }

    public static void genTurtle(String csvFileName, String textFilName) throws IOException{
        try (BufferedReader br = Files.newBufferedReader(Paths.get(csvFileName));
                BufferedWriter bw = Files.newBufferedWriter(Paths.get(textFilName))) {
            bw.write(GenerateBookStoreText.prefixes());
            bw.write("");

            HashMap<String,UUID> authorMap = new HashMap<>();
            HashMap<String, UUID>editorMap = new HashMap<>();
            String line;
            br.readLine(); // to skip the column titles line
            while ((line = br.readLine()) != null) {
                // split the line in using the ';' (semicolon)
                String[] lineTokens = line.split(";");

                String authorFamilyName = lineTokens[0];
                String authorGivenName = lineTokens[1];
                String bookTitle = lineTokens[2];
                int pagesNb = Integer.parseInt(lineTokens[3]);
                String isbn = lineTokens[4];
                String publisherName = lineTokens[5];


                //Process the line

                //Process author
                String fullname = (authorFamilyName + authorGivenName);
                UUID uuidAuthor;
                UUID uuidEditor;

                
                if(authorMap.containsKey(fullname)){
                    uuidAuthor = authorMap.get(fullname);
                }else{
                    uuidAuthor = UUID.nameUUIDFromBytes(fullname.getBytes());
                    authorMap.put(fullname, uuidAuthor);
                    String newAuthor = 
                        """
                        abr:%s a abo:Writer;
                        \tfoaf:givenName \"%s\"@en ;
                        \tfoaf:familyName \"%s\"@en ;
                        \tfoaf:name \"%s %s\"@en .
                        """
                        .formatted(uuidAuthor.toString(), authorGivenName, authorFamilyName, authorGivenName, authorFamilyName);
                    bw.write(newAuthor);
                }

                //Process editor
                if(editorMap.containsKey(publisherName)){
                    //si l'éditeur existe déjà
                    uuidEditor = editorMap.get(publisherName);
                }else{
                    uuidEditor = UUID.nameUUIDFromBytes(publisherName.getBytes());
                    editorMap.put(publisherName, uuidEditor);
                    String newEditor = 
                        """
                        abr:%s a abo:Publisher;
                        \tfoaf:name \"%s\"@en .
                        """
                        .formatted(uuidEditor.toString(), publisherName);
                    bw.write(newEditor);
                }

                //Process book
                UUID uuidBook =UUID.nameUUIDFromBytes(bookTitle.getBytes());
                String newBook =
                """
                abr:%s a abo:Book ;
                \tdcterms:title \"%s\"@en ;
                \tabo:author abr:%s ;
                \tabo:isbn \"%s\" ;
                \tabo:pages \"%d\"^^xds:int ;
                \tdcterms:publisher abr:%s .
                """
                .formatted(uuidBook.toString(), bookTitle, uuidAuthor.toString(), isbn, pagesNb, uuidEditor.toString());
                bw.write(newBook);

            } // end of file
        }
        catch(Exception e){
            System.out.println("Ya un bleme " +  e);
        }
    }

    public static String prefixes(){
        String rdfPrefix = "";
        String prefix = "@Prefix";
        rdfPrefix += prefix +  " abo: <http://www.artemisbookstore.com/ontology#> .\n";
        rdfPrefix += prefix + " abr: <http://www.artemisbookstore.com/resource> . \n";
        rdfPrefix += prefix + " dcterms: <http://purl.org/dc/terms/> . \n";
        rdfPrefix += prefix + " foaf: <http://xmlns.com/foaf/0.1/> . \n";
        rdfPrefix += prefix + " xsd: <http://www.w3.org/2001/XMLSchema#> . \n";


        return rdfPrefix;
    }

}