package com.flanciskinho.statistic.wilcoxon.exceptions;

public class NoDataException extends RuntimeException {

	public NoDataException(String msg) {
		super(msg);
	}
	
	public NoDataException() {
		this("");
	}
}
