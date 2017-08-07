package com.flanciskinho.statistic.wilcoxon.exceptions;

public class DimensionMismatchException extends RuntimeException {

	public DimensionMismatchException(int l1, int l2) {
		super("sizes "+l1+", "+l2);
	}
}
