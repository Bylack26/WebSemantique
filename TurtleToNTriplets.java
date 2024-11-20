import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.*;

public class TurtleToNTriplets {

    public static void main(String[] args) {
        // Vérification des arguments
        if (args.length != 2) {
            System.out.println("Usage: java TurtleToNTriplets <inputFile> <outputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // Chargement du fichier Turtle
            Model model = FileManager.get().loadModel(inputFile);

            // Ouverture d'un writer pour le fichier de sortie
            FileWriter writer = new FileWriter(outputFile);

            // Écriture des triplets en N-Triples (.ttl)
            RDFDataMgr.write(writer, model, RDFFormat.NTRIPLES);

            // Fermeture du writer
            writer.close();
            System.out.println("Conversion réussie ! Les triplets N-Triplets ont été écrits dans " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}