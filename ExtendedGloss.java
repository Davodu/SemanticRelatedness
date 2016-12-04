
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.Morphology;




public class ExtendedGloss{
	
	public static void main(String[] args) {
		Synset []synset01; //helper
		Synset []synset02; //helper
		String glossA; //saves definition of synset1
		String glossB; //saves definition of synset2
		
		String synset1;
		String synset2;
		int initialOverlap; //gloss overlap of two inputed words
		
		
		File f = new File("/Users/daviesodu/Desktop/duke final/59014/hw3/WordNet-3.0/dict");
		System.setProperty("wordnet.database.dir", f.toString());
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		 
		Morphology id = Morphology.getInstance();
		
		//1. take in two concepts
		System.out.println("Enter first concept: ");
		synset1 = readWord();
		System.out.println("Enter second concept: ");
		synset2 = readWord();
		System.out.println("Synsets to be compared are \""+ synset1 + "\" and \"" + synset2+ "\"");
		System.out.println("-----------------------------------------");
		
		
		synset01 = database.getSynsets(synset1);
		glossA = synset01[0].getDefinition();
		synset02 = database.getSynsets(synset2);
		glossB = synset02[0].getDefinition();
		//System.out.println(glossA + "\n " + glossB );

		//2. find phrasal gloss overlaps between both words
		initialOverlap = glossOverlap(database,glossA, glossB);
			
		
		Synset[] synsetA = database.getSynsets(synset1, SynsetType.NOUN);
		Synset[] synsetB = database.getSynsets(synset2, SynsetType.NOUN);
		
		ArrayList <String> hypoA = getHyponymgloss(synsetA);
		ArrayList <String> hypoB = getHyponymgloss(synsetB);
		
		ArrayList <String> hyperA = getHypernymgloss(synsetA);
		ArrayList <String> hyperB = getHypernymgloss(synsetB);
		
		int scoreHyperAGlossB =0;
		for(String str: hyperA){
			scoreHyperAGlossB += glossOverlap(database, str, glossB);
		}
		
		int scoreGlossAHyperB = 0;
		for(String str: hyperB){
			scoreGlossAHyperB +=glossOverlap(database, str, glossA);
		}
		
		int scoreHypeAHypeB = score(database,hyperA,hyperB);
		int scoreHypoAHypoB = score(database, hypoA,hypoB);
		 
		int relatedness = initialOverlap + scoreGlossAHyperB+ scoreHypoAHypoB+ scoreHyperAGlossB + scoreGlossAHyperB;
		System.out.println("Relatedness("+synset1 + ","  + synset2 + ") = "+ relatedness);
		
		if(initialOverlap >= 4 && relatedness >=20){
			System.out.println("It seems the two words are quite related.");
		}else{
			System.out.println("Not sure how closely related these words are.");
		}
        
        
        //test
    /*    for(String str : hypoA){
        	System.out.println(str);
        }
        System.out.println("-------");
        for(String str : hypoB){
        	System.out.println(str);
        }*/
        
		
	}
	

	//method to take inputs as strings
	public static String readWord(){
		Scanner scanner = new Scanner(System.in);
		return scanner.next();
	}
	
	//method to find longest submatch in two sentences
	private static final String longestCommonSubstring(String S1, String S2){
	    int Start = 0;
	    int Max = 0;
	    String match;
	    String regex = "( a )|(\\s)|(the)|(this)|(that)|(ing)";//unnecessary matches we dont want to consider
	    String longestMatch;
	       
	    for (int i = 0; i < S1.length(); i++){
	        for (int j = 0; j < S2.length(); j++){
	            int x = 0;
	            while (S1.charAt(i + x) == S2.charAt(j + x)){
	                x++;
	                if (((i + x) >= S1.length()) || ((j + x) >= S2.length())) break;
	            }
	            if (x > Max){
	                Max = x;
	                Start = i;
	            }
	         }
	    }
	    match = S1.substring(Start, (Start + Max));
	   //remove unnecessary words
	   longestMatch = match.replaceAll(regex, " ");
	   return longestMatch;
	}
	
	
	
	//get word count by counting strings separated by space
	public static int wordCount(String str){
		return str.split("\\s+").length;
	}
	
	
	//phrasal gloss overlap between two words . This is equivalent to score(gloss(A), gloss(B))
	public static int glossOverlap(WordNetDatabase database, String def1, String def2){
		
		//synset01 = database.getSynsets(synset1, SynsetType.NOUN);
		String commonwords = longestCommonSubstring(def1, def2);
		//System.out.println(commonwords);	
		//return square of word count of the common words
		return (int) Math.pow(wordCount(commonwords),2);
	}
	
	
	//gets Hyponym gloss of synset and saves in an arraylist
	private static ArrayList<String> getHyponymgloss(Synset[] synset) {
		ArrayList <String> hypoA = new ArrayList<String>();
		HashSet hs = new HashSet();
		//get hyponyms for words
		NounSynset nounsynset;
		NounSynset [] hyponyms;
	
		//get all hyponym gloss of first word
		for(int i =0; i < synset.length; i++){
			nounsynset = (NounSynset) synset[i];
			hyponyms = nounsynset.getHyponyms();
			for(int j = 0; j< hyponyms.length; j++){
			hypoA.add(hyponyms[0].getDefinition());
			}
		}
		//delete multiple matches in hypoA
		hs.addAll(hypoA);
		hypoA.clear();
        hypoA.addAll(hs);
		return hypoA;
	}
	
	

	private static ArrayList<String> getHypernymgloss(Synset[] synset) {
		ArrayList <String> hyperA = new ArrayList<String>();
		HashSet hs = new HashSet();
		//get hyponyms for words
		NounSynset nounsynset;
		NounSynset [] hypernyms;
	
		//get all hyponym gloss of first word
		for(int i =0; i < synset.length; i++){
			nounsynset = (NounSynset) synset[i];
			hypernyms = nounsynset.getHypernyms();
			for(int j = 0; j< hypernyms.length; j++){
			hyperA.add(hypernyms[0].getDefinition());
			}
		}
		//delete multiple matches in hypoA
		hs.addAll(hyperA);
		hyperA.clear();
        hyperA.addAll(hs);
		return hyperA;
	}
	
	
	//Compute score score
	private static int score(WordNetDatabase database,ArrayList<String> hyperA, ArrayList<String> hyperB) {
		int sum=0;
		//end to end comparison of hyperA and hyperB 
				for(String strA: hyperA){
					for (String strB: hyperB ){
						sum += glossOverlap(database, strA, strB);
					}
				}
       return sum;
	}	
}
