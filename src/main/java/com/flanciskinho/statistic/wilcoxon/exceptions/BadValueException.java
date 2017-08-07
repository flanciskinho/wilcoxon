package com.flanciskinho.statistic.wilcoxon.exceptions;

public class BadValueException extends RuntimeException {
	public BadValueException() {
		super();
	}
	
	public BadValueException(Number min, Number max) {
		super("min value: "+min.toString() +" max value:" + max.toString());
	}
}
