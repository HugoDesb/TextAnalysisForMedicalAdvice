package pretreatement.Extractor.Extractors;

import common.document.TextDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import pretreatement.Extractor.Summaries.SummaryHAS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExtractorHAS extends Extractor{

    public ExtractorHAS(File f) {
        super(f);
    }

    /*
    public static TextDocument extract(File file) {
        try {
            return withPDFBox(file, getTargetTXTPath(file));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    */

    /**
     * Extract text from pdf file (standard)
     * @return an array of text blocks
     */
    public ArrayList<String> extract() {
        ArrayList<String> hop = new ArrayList<>();
        PDFParser parser;
        try {
            /**
             * Get text from appropriate document range (without annexes and summary)
             * Also reads abbrev to replace them in the text
             */
            parser = new PDFParser(new RandomAccessFile(super.f, "r"));
            parser.parse();
            PDDocument contentDocument = PDDocument.load(super.f);
            SummaryHAS summary = readSummary(contentDocument);
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setStartPage(summary.getContentBoundaries()[0]);
            textStripper.setEndPage(summary.getContentBoundaries()[1]);

            //Strip text
            String text = textStripper.getText(contentDocument);

            // replace all abbreviations by their values
            for (String key: ExtractorHAS.getAbbrevs(contentDocument, summary.getAbbrevPageNumber()).keySet()) {
                text = text.replaceAll(key, ExtractorHAS.getAbbrevs(contentDocument, summary.getAbbrevPageNumber()).get(key));
            }

            text = text.replaceAll(ExtractorHAS.getTitle(contentDocument), "");


            String [] lineByLine = text.split("\\n");

            System.out.println(lineByLine);
            text = "";
            for (int i = 0; i< lineByLine.length; i++) {
                if(lineByLine[i].matches("Recommandation.[0-9].*")){
                    lineByLine[i] = "";
                    int j = i+1;
                    while(lineByLine[j].trim().isEmpty()){
                        j++;
                    }
                    lineByLine[j] = lineByLine[j].replace(lineByLine[j].split(" ")[0]+" ", "");
                }
                text += "\n"+lineByLine[i];
            }

            //System.out.println(text);

            hop.add(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hop;
    }

    /**
     * Extract the Summary of an HAS reco file
     * @param pdDoc
     * @return
     * @throws IOException
     */
    public SummaryHAS readSummary(PDDocument pdDoc) throws IOException {
        String text = getSummaryText(2);
        return new SummaryHAS(text);
    }

    public static String getTitle(PDDocument doc) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(1);
        String text = stripper.getText(doc);
        String [] hip = text.split("\\n");
        for (int i = 0; i<hip.length; i++) {
            if(hip[i].contains("RECOMMANDATION DE BONNE PRATIQUE")){
                int j = i+1;
                boolean started = false;
                String title = "";
                while(!hip[j].trim().isEmpty() || !started){
                    if(!hip[j].trim().isEmpty()){
                        started = true;
                        title += hip[j]+" ";
                    }
                    j++;
                }
                return title.trim();
            }
        }
        return "";
    }

    public static Map<String, String> getAbbrevs(PDDocument doc, int page) throws IOException {
        Map<String, String> hop = new HashMap<>();

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(page+1);
        stripper.setEndPage(page+1);
        String text = stripper.getText(doc);

        boolean start = false;
        for (String line: text.split("\\n")) {
            if(start && !line.isEmpty()){
                String [] elements = line.split(" ");
                String value = elements[1];
                for (int i = 2; i < elements.length; i++) {
                    value += " "+elements[i];
                }
                hop.put(elements[0], value );
            }
            if(line.contains("Libellé") && !start){
                start = true;
            }
        }

        return hop;
    }

    protected TextDocument.Builder readContent(PDDocument doc) throws IOException {
        //choose relevant pages
        SummaryHAS summary = readSummary(doc);
        PDDocument contentDocument = new PDDocument();
        int [] boundaries = summary.getContentBoundaries();
        for(int i = boundaries[0]; i<boundaries[1]; i++){
            contentDocument.addPage(doc.getPage(i));
        }

        PDFTextStripper textStripper = new PDFTextStripper();
        String contentText = textStripper.getText(contentDocument);

        return extractContent(contentText);
    }

    private static TextDocument.Builder extractContent(String contentText) {
        String [] textLines = contentText.split("\n");

        TextDocument.Builder builder = new TextDocument.Builder();

        String toAdd = "";

        for (String line : textLines) {
            String [] sentences = line.split("\\.\\s");
            for (String sentence: sentences) {
                //ligne non vide
                if(!sentence.equals("")){
                    // First character is a UPPERCASE
                    if(sentence.matches("^[ABCDEFGHIJKLMNOPQRSTUVWXYZÉÈÊÔŒÎÏËÇÆÂÀÙŸ].*")){
                        // We can consider it's a new sentence
                        //  add previous line
                        // TODO : be a Sentence : builder.addLine(toAdd + ".");
                        //store new line
                        toAdd = sentence;
                        // first character is NOT an UPPERCASE
                    } else {
                        toAdd += sentence;
                    }
                }
            }
        }
        return builder;
    }

}
