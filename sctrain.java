import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
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
		"had", "has", "have", "having",
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

	private static final String CONFUSION_START = ">>";
	private static final String CONFUSION_END = "<<";
	private static final int COLLOCATION_LENGTH = 4;

	private static final double LEARNING_RATE = 0.02;
	private static final double GRADIENT_TRESHOLD = 0.1;
	private static final double INITIAL_WEIGHT = 0.01;

	private String _word1 = null;
	private String _word2 = null;
	private String _trainFile = null;
	private String _modelFile = null;

	private Vector< Vector<String> > _datasets = null;
	private int[][] _featureVectors = null;
	private int[] _outcome = null;
	private double[] _weightVectors = null;

	private HashMap <String, Integer> _surroundingWordsFeature = null;
	private HashMap <String, Integer> _collocationsFeature = null;

	public sctrain(String word1, String word2, String trainFile, String modelFile) {
		setWord1(word1);
		setWord2(word2);
		setTrainFile(trainFile);
		setModelFile(modelFile);
		_datasets = new Vector< Vector<String> >();
		_surroundingWordsFeature = new HashMap <String, Integer>();
		_collocationsFeature = new HashMap <String, Integer>();
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

	private boolean retrieveDataSet(String trainFile) {
		try {
			File file = new File(trainFile);
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    for(String line; (line = br.readLine()) != null; ) {
		        // process the line.
		        String[] lineTokens = line.split(" ");
                for (int i = 0; i < lineTokens.length; i++) {
                    lineTokens[i] = lineTokens[i].toLowerCase().trim();
                }
		        Vector<String> vector = new Vector<String>(Arrays.asList(lineTokens));
		        _datasets.add(vector);
		    }
		    br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void initializeWeightVector(int numOfFeature) {
		_weightVectors = new double[numOfFeature + 1];
		Arrays.fill(_weightVectors, INITIAL_WEIGHT);
	}

	private void initializeFeatureVector(int numOfDataSets, int numOfFeature) {
		_featureVectors = new int[numOfDataSets][numOfFeature + 1];
		for (int i = 0; i < numOfDataSets; i++) {
            for (int j = 0; j < numOfFeature + 1; j++) {
                _featureVectors[i][j] = 0;
            }
        }
	}

	private void initializeOutcomeVector(int numOfDataSets) {
		_outcome = new int[numOfDataSets];
		Arrays.fill(_outcome, 0);
	}

	private boolean retrieveFeaturesFromDataSet() {
		int index = 1;
		index = retrieveSurroundingWordsFeature(index);
		index = retrieveCollocationsFeature(index);
		boolean isSuccess = index >= 0;
		if (isSuccess) {            
			initializeWeightVector(index);
			initializeFeatureVector(_datasets.size(), index);
			initializeOutcomeVector(index);
		} 
		return isSuccess;
	}

	private boolean isStopWord(String word) {
		word = word.trim();
		for (String stopWord : STOP_WORDS) {
			if (stopWord.equals(word)) {
				return true;
			}	
		}
		return false;
	}

	private int retrieveSurroundingWordsFeature(int index) {
		if (index < 0) {
			return index;
		}

		for (Vector <String> dataset : _datasets) {
			for (int i = 1; i < dataset.size(); i++) {
                String word = dataset.get(i);
				word = word.trim();
				word = word.toLowerCase();
				if (word.isEmpty()) {
					continue;
				} else if (Pattern.matches(REGEX_PUNCTUATION, word)) {
					continue;
				} else if (CONFUSION_START.equals(word)) {
					continue;
				} else if (CONFUSION_END.equals(word)) {
					continue;
				} else if (isStopWord(word)) {
					continue;
				} else if (_surroundingWordsFeature.containsKey(word)) {
					continue;
				} else {
					_surroundingWordsFeature.put(word, index);
					index++;
				}
			}
		}

		return index;
	}

	private int retrieveCollocationsFeature(int index) {
		if (index < 0) {
			return index;
		}

		for (Vector <String> dataset : _datasets) {
			int confussionWordIndex = -1;
			for (int i = 0; i < dataset.size(); i++) {
				String token = dataset.get(i).trim();
				if (CONFUSION_START.equals(token)) {
					confussionWordIndex = i;
					break;
				}
			}

			if (confussionWordIndex < 0) {
				//error
				System.out.println("for dataset " + dataset.get(0).trim() + 
					" : confussion word\'s index is not found");
				return confussionWordIndex;
			}

            StringBuilder sb = new StringBuilder();
			for (int i = confussionWordIndex - 1; 
                 i >= Math.max(confussionWordIndex - 1 - COLLOCATION_LENGTH, 1); i--) {
				String word = dataset.get(i).trim();
                sb.insert(0, word);
				String collocationWord = sb.toString();
				if (!_collocationsFeature.containsKey(collocationWord)) {
					_collocationsFeature.put(collocationWord, index);
					index ++;
				}
				sb.insert(0, " ");
			}

			StringBuffer collacationWordBuffer = new StringBuffer();
			for (int i = confussionWordIndex + 3; 
                 i <= Math.min(confussionWordIndex + 3 + COLLOCATION_LENGTH, dataset.size() - 1); i++) {
				String word = dataset.get(i).trim();
				collacationWordBuffer.append(word);
				String collocationWord = collacationWordBuffer.toString();
				if (!_collocationsFeature.containsKey(collocationWord)) {
					_collocationsFeature.put(collocationWord, index);
					index ++;
				}
				collacationWordBuffer.append(" ");
			}
		}
		return index;
	}

	private void computeFeatureVectorsAndOutcome() {
		for (int i = 0; i < _datasets.size(); i++) {
			Vector <String> dataset = _datasets.get(i);

			_featureVectors[i][0] = 1;
			int confuseIndexStart = -1, confuseIndexEnd = -1;
			for (int j = 0; j < dataset.size(); j++) {
				String word = dataset.get(j).trim();

				if (_surroundingWordsFeature.containsKey(word)) {
					Integer index = _surroundingWordsFeature.get(word);
					_featureVectors[i][index.intValue()] = 1;
				} else if (CONFUSION_START.equals(word) &&
						   CONFUSION_END.equals(dataset.get(j + 2).trim())) {
					confuseIndexStart = j;
					confuseIndexEnd = j + 2;

					String correctWord = dataset.get(j + 1).trim();
					if (_word1.equals(correctWord)) {
						_outcome[i] = 1;
					} else if (_word2.equals(correctWord)) {
						_outcome[i] = 0;
					} else {
						System.out.println("the guessing word is neither word 1 (" + _word1 +
							")nor 2 (" + _word2 + ")");
						System.exit(-1);
					}

					j += 2;
				} 
			}
            
            if (confuseIndexEnd < 0 || confuseIndexStart < 0) {
                System.exit(-1);
            }
            
            StringBuilder sb = new StringBuilder();
			for (int j = confuseIndexStart - 1; 
                 j >= Math.max(confuseIndexStart - 1 - COLLOCATION_LENGTH, 1); j--) {
				String word = dataset.get(j).trim();
				sb.insert(0, word);
				String collocationWord = sb.toString();
				if (_collocationsFeature.containsKey(collocationWord)) {
					Integer index = _collocationsFeature.get(collocationWord);
					_featureVectors[i][index.intValue()] = 1;
				}
				sb.insert(0, " ");
			}

            StringBuffer collacationWordBuffer = new StringBuffer();
			for (int j = confuseIndexEnd + 1; 
                 j <= Math.min(confuseIndexEnd + 1 + COLLOCATION_LENGTH, dataset.size() - 1); j++) {
				String word = dataset.get(j).trim();
				collacationWordBuffer.append(word);
				String collocationWord = collacationWordBuffer.toString();
				if (_collocationsFeature.containsKey(collocationWord)) {
					Integer index = _collocationsFeature.get(collocationWord);
					_featureVectors[i][index.intValue()] = 1;
				}
				collacationWordBuffer.append(" ");
			}

		}
	}

	private double derivativeFunction(int indexOfDataSet, int indexOfFeature, 
			                          double[] weightVector) {
		/**
		 *	xi * (y - z), z = 1/(1+e^(-w x))
		 */

		int xi = _featureVectors[indexOfDataSet][indexOfFeature];
		int y = _outcome[indexOfDataSet];

		double innerProductOfWeightAndFeature = 0.0;
		for (int i = 0; i < weightVector.length; i++) {
			innerProductOfWeightAndFeature += weightVector[i] * _featureVectors[indexOfDataSet][i];
		}

		double z = Math.pow(Math.E, innerProductOfWeightAndFeature * -1);
		z += 1;
		z = 1 / z;
        
        if (xi == 1) {
            //System.out.println(xi * (y - z));
        }
        
		return xi * (y - z);
	}

	private void computeStochasticGradientAscent() {
		int n = _weightVectors.length;
		int dataSetSize = _datasets.size();
		int indexOfDataSet = 0;
		while (true) {
            System.out.println(indexOfDataSet);

			boolean isEnded = true;

			double[] gradients = new double[n];
			for (int i = 0; i < n; i++) {
				gradients[i] = derivativeFunction(indexOfDataSet, i, _weightVectors);
                //System.out.println(gradients[i]);
				if (Math.abs(gradients[i]) > GRADIENT_TRESHOLD) {
					isEnded = false;
				}
			}

			if (isEnded) {
				break;
			}
			indexOfDataSet++;
			indexOfDataSet %= dataSetSize;

			for (int i = 0; i < n; i ++) {
				_weightVectors[i] = _weightVectors[i] + LEARNING_RATE * gradients[i];
			}
		}
	}

	@SuppressWarnings("unused")
	private void computeBatchGradientAscent() {
		int n = _weightVectors.length;
		int dataSetSize = _datasets.size();
		while (true) {
			boolean isEnded = true;

			double[] gradients = new double[n];
			for (int i = 0; i < n; i++) {
				gradients[i] = 0;
				for (int j = 0; j < dataSetSize; j++) {
					gradients[i] += derivativeFunction(j, i, _weightVectors);
				}
				gradients[i] /= dataSetSize;
				if (Math.abs(gradients[i]) > GRADIENT_TRESHOLD) {
					isEnded = false;
				}
			}

			if (isEnded) {
				break;
			}

			for (int i = 0; i < n; i ++) {
				_weightVectors[i] = _weightVectors[i] + LEARNING_RATE * gradients[i];
			}
		}
	}


	public boolean startTraining() {
		if (!retrieveDataSet(_trainFile)) {
			return false;
		} else if (!retrieveFeaturesFromDataSet()) {
			return false;
		} 

		computeFeatureVectorsAndOutcome();
        //printFeatureVectors();  
		computeStochasticGradientAscent();
		boolean isSucess = saveFeaturesAndWeights(_surroundingWordsFeature,
												  _collocationsFeature,
												  _weightVectors,
												  _modelFile);
		return isSucess;
	}
    
    private void printFeatureVectors() {
        for (int i = 0; i < _datasets.size(); i++) {
            for (int j = 0; j < _weightVectors.length; j++) {
                if (_featureVectors[i][j] == 0) {continue;}
                System.out.print(_featureVectors[i][j] + " ");
            }
            System.out.println("\n");
        }
        
        for (int i = 0; i < _outcome.length; i++) {
        if (_outcome[i] == 0){continue;}
            System.out.println("outcome:" + _outcome[i]);
        }
    }
	
	private boolean saveFeaturesAndWeights(HashMap<String, Integer> surroundingWords, 
										   HashMap<String, Integer> collocation,
										   double[] weightVector, String filename) {
		FileWriter fw;
		try {
			fw = new FileWriter(filename);
			fw.write("surroundingWords:");
			for(Map.Entry<String, Integer> entry : surroundingWords.entrySet()) {
				String key = entry.getKey();
				
				fw.write(" " + key);
			}
			
			fw.write("\ncollocation: ");
			for(Map.Entry<String, Integer> entry : collocation.entrySet()) {
				String key = entry.getKey();
				
				fw.write("---" + key);
			}
			
			fw.write("\nweights:");
			for (int i = 0; i < weightVector.length; i++) {
				fw.write(" ");
				Double weight = weightVector[i];
				fw.write(weight.toString());
			}
		 
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		 
		return true;
	}

	private static sctrain initSctrain(String[] args) {
		String inputFormat = "Input format: java sctrain word1 word2 train_file model_file";

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
        long startTime = System.currentTimeMillis();
		sctrain train = initSctrain(args);
		boolean isSuccess = train.startTraining();
		
		if (!isSuccess) {
			System.exit(-1);
		}
        
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total time: " + totalTime);
	}
}
