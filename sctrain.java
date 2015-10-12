import java.util.regex.Pattern;


public class sctrain {
	private static final int INDEX_WORD1 = 0;
	private static final int INDEX_WORD2 = 1;
	private static final int INDEX_TRAIN_FILE = 2;
	private static final int INDEX_MODEL_FILE = 3;
	private static final int NUM_OF_ARGUMENTS = 4;


	//reference: https://javaextreme.wordpress.com/category/java-j2se/java-string/remove-stop-words-from-a-string/
	private static final String[] STOP_WORDS = {
		"without", "see", "unless", "due", "also", "must", "might", "like", "will", "may", 
		"can", "much", "every", "the", "in", "other", "this", "the", "many", "any", "an", "or", 
		"for", "in", "an", "an ", "is", "a", "about", "above", "after", "again", "against", "all", 
		"am", "an", "and", "any", "are", "\'t", "as", "at", "be", "because", "been", "before",
		 "being", "below", "between", "both", "but", "by", "cannot", "could",
		"did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further", 
		"had", "hadn’t", "has", "have", "having",
		"he", "\'d", "\'ll", "\'s", "her", "here", "hers", "herself", "him", "himself", "his", 
		"how", "i", "\'m", "\'ve", "if", "in", "into", "is",
		"it", "its", "itself", "let", "me", "more", "most", "n\'t", "my", "myself", "no", 
		"nor", "not", "of", "off", "on", "once", "only", "ought", "our", "ours", "ourselves",
		"out", "over", "own", "same", "she", "should", "so", "some", "such", "than",
		"that", "their", "theirs", "them", "themselves", "then", "there", "these", "they", "\'re",
		"this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", 
		"were", "what", "when", "where", "which", "while", "who", "whom",
		"why", "with", "would", "you", "your", "yours", "yourself", "yourselves",
	};

	private static final String REGEX_PUNCTUATION = "\\p{Punct}";

	private String _word1;
	private String _word2;
	private String _trainFile;
	private String _modelFile;

	public sctrain(String word1, String word2, String trainFile, String modelFile) {
		setWord1(word1);
		setWord2(word2);
		setTrainFile(trainFile);
		setModelFile(modelFile);
	}

	private void setWord1(String word1) {
		_word1 = word1;
	}

	private void setWord2(String word2) {
		_word2 = word2;
	}

	private void setTrainFile(String trainFile) {
		_trainFile = trainFile;
	}

	private void setModelFile(String modelFile) {
		_modelFile = modelFile;
	}

	public void startTraining() {
		
	}

	private static sctrain initSctrain(String[] args) {
		String inputFormat = "Input format: java sctrain word1 word2 train_file model_file"

		if (args.length > NUM_OF_ARGUMENTS) {
			System.out.println("Error: not enough arguments");
			System.out.println(inputFormat);
			System.exit(-1);
		} else if (args.length < NUM_OF_ARGUMENTS) {
			System.out.println("Error: too many arguments");
			System.out.println(inputFormat);
			System.exit(-1);
		} else {
			//correct, do ntg
		}

		String word1 = args[INDEX_WORD1];
		String word2 = args[INDEX_WORD2];
		String trainFile = args[INDEX_TRAIN_FILE];
		String modelFile = args[INDEX_MODEL_FILE];
		return new sctrain(word1, word2, trainFile, modelFile);
	}

	public static void main(String[] args) {
		sctrain train = initSctrain(args);
		sctrain.startTraining();
	}

}