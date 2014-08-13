package org.apache.mahout.cf.taste.hadoop.als;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.math.hadoop.DistributedRowMatrix.MatrixEntryWritable;

public class UpdateUorMCombiner extends Reducer<IntWritable, ALSContributionWritable, 
		IntWritable, ALSContributionWritable> {

	private int numFeatures;

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		Configuration conf = context.getConfiguration();
		numFeatures = conf.getInt(ParallelALSFactorizationJob.NUM_FEATURES, -1);

		Preconditions.checkArgument(numFeatures > 0,
				"numFeatures must be greater then 0!");
	}

	
	@Override
	protected void reduce(IntWritable key,
			Iterable<ALSContributionWritable> values,
			Context ctx) throws IOException, InterruptedException {
		
			Matrix combinedA = new DenseMatrix(numFeatures, numFeatures);
			Matrix combinedb = new DenseMatrix(numFeatures, 1);
			
			for (ALSContributionWritable contribution: values) {
				combinedA = combinedA.plus(contribution.getA());
				combinedb = combinedb.plus(contribution.getb());
			}	
			
			ctx.write(key, new ALSContributionWritable(combinedA, combinedb));
	}

	
}