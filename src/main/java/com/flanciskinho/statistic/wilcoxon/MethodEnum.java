package com.flanciskinho.statistic.wilcoxon;

public enum MethodEnum {
	APPROXIMATE {
		@Override
		public String toString() {
			return "Approximate";
		}
	}
/*
	, EXACT {
		@Override
		public String toString() {
			return "Exact";
		}
	}
*/
	;
	
	private MethodEnum() {
		
	}
	
	public abstract String toString();
}
