package pretreatement.Filter;

import common.config.Config;
import common.document.Sentence;

import java.io.*;
import java.util.ArrayList;

public class Filter {

    public String regexSelect;
    public String regexDelete;

    public Filter(String language){
        String folder = Config.getInstance().getProp("pretreatment.termes_declencheurs_path")+"termes_declencheurs_";
        File f ;
        if(language.equals("french")){
            f = new File(folder+"fr");
        }else{
            f = new File(folder+"en");
        }

        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            String regexSelect = "";
            while(line != null){
                line.replaceAll(" ", "\\s");
                regexSelect += "(^"+line+".*)|(.*\\s+"+line+".*)|";
                line = br.readLine();
            }
            this.regexSelect = regexSelect.substring(0,regexSelect.length()-1);
            this.regexDelete = "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Sentence> filter(ArrayList<Sentence> lines) {
        ArrayList<Sentence> ret = new ArrayList<>();
        for (Sentence line : lines) {
            if(line.getText().toLowerCase().matches(regexSelect) && !line.getText().toLowerCase().matches(regexDelete)){
                ret.add(line);
            }
        }
        return ret;
    }
}

