package com.flanciskinho.statistic.wilcoxon;

public enum TailEnum {
	BOTH {
		@Override
		public String toString() {
			return "Both";
		}
	},
	RIGTH {
		@Override
		public String toString() {
			return "Right";
		}
	},
	LEFT{
		@Override
		public String toString() {
			return "Left";
		}
	};

	private TailEnum() {}
	
	public abstract String toString();
}
