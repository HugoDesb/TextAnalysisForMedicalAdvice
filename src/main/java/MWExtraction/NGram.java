package MWExtraction;

import tagging.RNNTagger.RNNTag;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Objects;

public class NGram {

    private int capacity;
    private int length;
    private ArrayList<RNNTag> grams;

    /**
     * Create a NGram with specific length
     * @param length
     */
    public NGram(int length) {
        if(length<= 0){
            throw new InvalidParameterException("length must be > 0. Because A Ngrams must have at least one element");
        }
        this.length = length;
        this.capacity = 0;
        grams = new ArrayList<>();
    }

    /**
     * Create a Ngram with specific length and grams
     * @param grams
     */
    public NGram(ArrayList<RNNTag> grams) {
        if(1 <= grams.size()){
            throw new InvalidParameterException("grams must at least contain 1-gram");
        }
        this.capacity = grams.size();
        this.length = grams.size();
        this.grams = grams;
    }

    /**
     * Get the element at the specified index
     * @param i
     * @return
     */
    public RNNTag get(int i){
        if(i<0 || i>= length){
            throw new IndexOutOfBoundsException("min==0 and max==length-1=="+(length-1));
        }

        return grams.get(i);
    }

    /**
     * Returns whether longerGram contains this NGram
     * @param longerGram
     * @return
     */
    public boolean isIn(NGram longerGram){
        if(longerGram.length() <= length){
            throw new InvalidParameterException("The parameter NGram must shorter than "+(length-1));
        }
        return longerGram.contains(this);
    }

    /**
     * Returns
     * @param shorterNGram
     * @return
     */
    public boolean contains(NGram shorterNGram){
        if(shorterNGram.length() >= length){
            throw new InvalidParameterException("The parameter NGram must shorter than "+(length-1));
        }
        NGram temp;
        boolean ret = false;
        for (int i= 0; i<=(length-shorterNGram.length); i++) {
            temp = new NGram(shorterNGram.length);
            boolean hop = true;
            for (int j = 0; j < shorterNGram.length-1; j++) {
                hop = hop && get(j+i).equals(shorterNGram.get(j));
            }
            ret = ret || hop;
        }
        return ret;
    }

    /**
     * Adds the next Gram
     * @param gram
     * @return
     */
    public boolean addGram(RNNTag gram){
        if(capacity == length){
            System.out.println("The NGram is already full");
            return false;
        }else{
            grams.add(gram);
            capacity++;
            return true;
        }
    }

    /**
     * Autogenerated equals function
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NGram nGram = (NGram) o;
        return capacity == nGram.capacity &&
                length == nGram.length &&
                Objects.equals(grams, nGram.grams);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(capacity, length, grams);
    }

    /**
     * Get length
     * @return
     */
    public int length(){
        return length;
    }

}
