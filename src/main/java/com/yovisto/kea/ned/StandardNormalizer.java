package com.yovisto.kea.ned;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Assert;

import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.ScoredTerm;
import com.yovisto.kea.commons.ScoredCandidate;

public class StandardNormalizer implements Normalizer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2895902489582702029L;

	@Override
	public List<ScoredTerm> normalize(List<ScoredTerm> scoredTerms, Parameters params) {

		for (ScoredTerm term : scoredTerms) {
			// get the dimension;
			int rows = 0;
			int cols = 0;
			rows = term.getScoredCandidates().size();
			for (ScoredCandidate uri : term.getScoredCandidates()) {
				for (String method : uri.getScores().keySet()) {
					Score score = uri.getScores().get(method);
					cols = cols + score.getVector().size();
				}
				break;
			}
			
//			if (!(rows > 0 && cols > 0)){
//				L.info("stop");
//			}

			Assert.assertTrue("Normalization failed because of empty score or candidate list.", rows > 0 && cols > 0);
//			L.info(rows + "x" + cols);

			// create the matrix
			RealMatrix matrix = MatrixUtils.createRealMatrix(rows, cols);

			// now fill it
			int row = 0;
			rows = term.getScoredCandidates().size();
			for (ScoredCandidate uri : term.getScoredCandidates()) {
				int col = 0;
				for (String method : uri.getScores().keySet()) {
					Score score = uri.getScores().get(method);					
					Assert.assertNotNull("Score for method '" + method + "' is null.", score);
					Assert.assertNotNull("Score vector for method '" + method + "' is null.", score.getVector());
					for (double d : score.getVector()) {
						matrix.setEntry(row, col, d);
						col++;
					}
				}
				row++;
			}

			//L.info("Origin:       " + matrix);

			RealMatrix maxMatrix = MatrixUtils.createRealMatrix(cols, cols);
			maxMatrix.scalarMultiply(0.0);
			// now create maximum matrix (first row with maximum, the other 0)
			for (int c = 0; c < matrix.getColumnDimension(); c++) {
				double max = 0.0;
				for (double d : matrix.getColumn(c)) {
					max = Math.max(d, max);
				}
				if (max != 0.0) {
					maxMatrix.setEntry(c, c, 1.0 / max);
				} else {
					maxMatrix.setEntry(c, c, 0.0);
				}

			}

			// L.info("Normalized: " +maxMatrix);

			RealMatrix maxNormalizedMatrix = matrix.multiply(maxMatrix);
			//L.info(maxNormalizedMatrix);
			// now put everything back to the scores

			row = 0;

			for (ScoredCandidate uri : term.getScoredCandidates()) {
				int col = 0;
				for (String method : uri.getScores().keySet()) {
					Score score = uri.getScores().get(method);
					List<Double> normalizedVector = new ArrayList<Double>();
					for (int c = 0; c < score.getVector().size(); c++) {
						normalizedVector.add(maxNormalizedMatrix.getEntry(row, col));
						score.setNormalizedVector(normalizedVector);
						score.setNormalizedValue(getMeanValue(normalizedVector));
						col++;
					}
				}
				row++;
			}

		}

		return scoredTerms;
	}

	private double getMeanValue(List<Double> normalizedVector) {
		double num = 0;
		double sum = 0;
		for (double v : normalizedVector) {
			sum = sum + v;
			num++;
		}
		return sum / num;
	}
}
