package eu.stratosphere.test.exampleRecordPrograms;

import eu.stratosphere.api.common.Plan;
import eu.stratosphere.api.common.operators.CollectionDataSource;
import eu.stratosphere.api.common.operators.FileDataSink;
import eu.stratosphere.api.java.record.io.CsvOutputFormat;
import eu.stratosphere.api.java.record.operators.MapOperator;
import eu.stratosphere.api.java.record.operators.ReduceOperator;
import eu.stratosphere.configuration.Configuration;
import eu.stratosphere.example.java.record.wordcount.WordCount;
import eu.stratosphere.types.IntValue;
import eu.stratosphere.types.StringValue;

public class WordCountCollectionCase extends WordCountITCase {

	/*
	 * modify the input format from file into collection
	 */
	public class WordCountCollection extends WordCount {
		
		public Plan getPlan(String arg1, String arg2) {
			// parse job parameters
			int numSubTasks   = Integer.parseInt(arg1);
			String output    = arg2;

			CollectionDataSource source = new CollectionDataSource(TEXT.split("\n"));
			MapOperator mapper = MapOperator.builder(new TokenizeLine())
				.input(source)
				.name("Tokenize Lines")
				.build();
			ReduceOperator reducer = ReduceOperator.builder(CountWords.class, StringValue.class, 0)
				.input(mapper)
				.name("Count Words")
				.build();
			FileDataSink out = new FileDataSink(new CsvOutputFormat(), output, reducer, "Word Counts");
			CsvOutputFormat.configureRecordFormat(out)
				.recordDelimiter('\n')
				.fieldDelimiter(' ')
				.field(StringValue.class, 0)
				.field(IntValue.class, 1);
			
			Plan plan = new Plan(out, "WordCount Example");
			plan.setDefaultParallelism(numSubTasks);
			return plan;
		}
	}
	
	public WordCountCollectionCase(Configuration config) {
		super(config);
	}

	
	@Override
	protected Plan getTestJob() {
		WordCountCollection wc = new WordCountCollection();
		/*
		 * split the test sentence into an array
		 */
		return wc.getPlan(config.getString("WordCountTest#NumSubtasks", "1"),
				resultPath);
	}
}
