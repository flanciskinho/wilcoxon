package com.flanciskinho.statistic.wilcoxon;

import org.apache.commons.math3.distribution.NormalDistribution;

import com.flanciskinho.statistic.wilcoxon.exceptions.BadValueException;
import com.flanciskinho.statistic.wilcoxon.exceptions.DimensionMismatchException;
import com.flanciskinho.statistic.wilcoxon.exceptions.NoDataException;
import com.flanciskinho.statistic.wilcoxon.exceptions.NotNumberException;

public class SignRank {
	
	private double p;
	
	private double alpha = 0.05;
	
	private double []x;
	private double []y;
	
	private double []diffxy;
	private double []epsdiff;
	private TiedRank tiedRank;
	
	private int    []iPos;
	private double w;
	private double w2;
	
	private MethodEnum method;
	private TailEnum tail;
	
	public SignRank(double []x, double []y) {
		this(x, y, 0.05);
	}
	
	public SignRank(double []x, double []y, double alpha) {
		//this(x, y, alpha, TailEnum.BOTH, x.length <= 15? MethodEnum.EXACT: MethodEnum.APPROXIMATE);
		this(x, y, alpha, TailEnum.BOTH, MethodEnum.APPROXIMATE);
	}
	
	public SignRank(double []x, double []y, double alpha, TailEnum tail, MethodEnum method) {
		if (x.length == 0 || y.length == 0)
			throw new NoDataException();
		if (x.length != y.length)
			throw new DimensionMismatchException(x.length, y.length);
		
		if (alpha <= 0 || alpha >= 1)
			throw new BadValueException(0.0, 1.0);
		
		this.x = x;
		this.y = y;
		this.alpha = alpha;
		this.tail = tail;
		this.method = method;
		
		calculateDiffxy();
		calculateEpsdiff();
		
		checkNaN();		
		removeZeros();
		
		if (this.x.length == 0)
			throw new NoDataException("no data available");
		
		calculateiPos();

		tiedRank = new TiedRank(diffxy, epsdiff);
		
		calculateW();
		
		w2 = diffxy.length*(diffxy.length+1)/2 - w; 
		
		calculateMethod();
	}
	
	private void calculateW() {
		double tieRank[] = tiedRank.getTieRank();
		
		w = 0.0;
		for (int cnt = 0; cnt < tieRank.length; cnt++)
			if (iPos[cnt] == 1) {
				w += tieRank[cnt];
			}
	}
	
	private void calculateMethod() {
		double z = 0;
		double n = diffxy.length;
		
/*
		if (this.method == MethodEnum.EXACT) {
			if (this.tail == TailEnum.BOTH) {
				
			} else if (this.tail == TailEnum.RIGTH) {
				
			} else if (this.tail == TailEnum.LEFT) {
				
			} else {
				throw new UnsupportedOperationException(this.tail.toString() + ": not implemented");
			}
		} else
*/
		if (this.method == MethodEnum.APPROXIMATE) {
			NormalDistribution nd = new NormalDistribution(0, 1);
			
			if (this.tail == TailEnum.BOTH) {
				z = (w-n*(n+1)/4) / Math.sqrt((n*(n+1)*(2*n+1) - tiedRank.getTieAdj())/24.0);
				p = 2*nd.cumulativeProbability(-Math.abs(z));
			} else if (this.tail == TailEnum.RIGTH) {
				z = (w-n*(n+1)/4 - 0.5) / Math.sqrt((n*(n+1)*(2*n+1) - tiedRank.getTieAdj())/24.0);
				p = nd.cumulativeProbability(-z);
			} else if (this.tail == TailEnum.LEFT) {
				z = (w-n*(n+1)/4 + 0.5) / Math.sqrt((n*(n+1)*(2*n+1) - tiedRank.getTieAdj())/24.0);
				p = nd.cumulativeProbability(z);
			} else {
				throw new UnsupportedOperationException(this.tail.toString() + ": not implemented");
			}
for (int cnt = 0; cnt < tiedRank.getTieRank().length; cnt++)
	System.out.format("%5.2f\n", tiedRank.getTieRank()[cnt]);
System.out.println("w: "+w);
System.out.println("n: "+n);
System.out.println("t: "+tiedRank.getTieAdj());
System.out.println("z: "+z);
		} else {
			throw new UnsupportedOperationException(this.method.toString() + ": not implemented");
		}
		
	}
	
	private void calculateDiffxy() {
		diffxy = new double[x.length];
		
		for (int cnt = 0; cnt < x.length; cnt++)
			diffxy[cnt] = x[cnt] - y[cnt];
	}
	
	private void calculateEpsdiff() {
		epsdiff = new double[x.length];
		
		for (int cnt = 0; cnt < x.length; cnt++) {
			epsdiff[cnt] = Math.ulp(x[cnt])+Math.ulp(y[cnt]);
		}
	}
	
	private void calculateiPos() {
		iPos = new int[x.length];
		
		for (int cnt = 0; cnt < x.length; cnt++) {
			iPos[cnt] = diffxy[cnt] > 0? 1: 0;
		}
	}
	
	private void checkNaN() {
		for (int cnt = 0; cnt < diffxy.length; cnt++) {
			if (Double.isNaN(diffxy[cnt]))
				throw new NotNumberException();
				
			if (Double.isNaN(epsdiff[cnt]))
				throw new NotNumberException();
		}
	}
	private void removeZeros() {
		int cnt, aux;
		for (cnt = aux = 0; cnt < x.length; cnt++) {
			if (Math.abs(diffxy[cnt]) <= epsdiff[cnt]) {
				continue;
			}
			aux++;
		}
//System.out.println(aux+","+cnt);
		
		double []tmpX = new double[aux];
		double []tmpY = new double[aux];
		double []tmpD = new double[aux];
		double []tmpE = new double[aux];
		for (cnt = aux = 0; cnt < x.length; cnt++) {
			if (Math.abs(diffxy[cnt]) <= epsdiff[cnt]) {
				continue;
			}
			tmpX[aux] = x[cnt];
			tmpY[aux] = y[cnt];
			tmpD[aux] = diffxy[cnt];
			tmpE[aux] = epsdiff[cnt];
			aux++;
		}
		
		this.x = tmpX;
		this.y = tmpY;
		this.diffxy  = tmpD;
		this.epsdiff = tmpE;
		
	}
	
	public double getP() {
		return p;
	}



	
	public static void main(String args[]) {
	//	double []d1 = {180.0, 210.0, 195.0, 220.0, 210.0, 190.0, 225.0, 215.0};
	//	double []d2 = {185.0, 225.0, 215.0, 245.0, 200.0, 220.0, 235.0, 250.0};

//		double d1[] = {0.0411,0.0439,0.0440,0.0673,0.0292,0.0205,0.0322,0.0205,0.0205,0.0877};
//		double d2[] = {0.0352,0.0556,0.0469,0.0585,0.1608,0.0146,0.0439,0.0263,0.0146,0.0175};
		
		double []d1 = {0.0411, 0.0439, 0.0440, 0.0673, 0.0292, 0.0205, 0.0322, 0.0205, 0.0205, 0.0877, 0.0293, 0.0936, 0.0733, 0.0468, 0.0760, 0.0673, 0.0439, 0.0731, 0.0673, 0.0263, 0.0997, 0.1228, 0.0880, 0.0468, 0.0497, 0.0205, 0.0205, 0.0439, 0.0205, 0.0117, 0.0235, 0.0263, 0.0323, 0.0322, 0.0175, 0.0322, 0.0497, 0.0292, 0.0322, 0.0146, 0.0323, 0.0292, 0.0352, 0.0497, 0.0643, 0.0234, 0.0351, 0.0439, 0.0234, 0.0292, 0.0293, 0.0497, 0.0587, 0.1082, 0.0175, 0.0497, 0.0292, 0.0497, 0.0497, 0.0175, 0.0587, 0.0468, 0.0352, 0.0439, 0.0731, 0.0614, 0.0497, 0.0702, 0.0614, 0.0205, 0.6540, 0.6462, 0.6510, 0.6667, 0.6140, 0.6520, 0.6784, 0.6520, 0.6520, 0.6491, 0.0733, 0.1199, 0.0293, 0.0439, 0.0760, 0.0614, 0.0292, 0.0614, 0.0614, 0.0175, 0.0938, 0.0409, 0.0323, 0.0468, 0.0146, 0.0497, 0.0234, 0.0585, 0.0497, 0.0058};
		double []d2 = {0.0352, 0.0556, 0.0469, 0.0585, 0.1608, 0.0146, 0.0439, 0.0263, 0.0146, 0.0175, 0.0938, 0.0234, 0.0411, 0.0234, 0.0263, 0.0322, 0.0322, 0.0351, 0.0322, 0.0146, 0.0411, 0.0439, 0.0645, 0.0380, 0.0497, 0.0380, 0.0497, 0.0585, 0.0380, 0.0439, 0.1085, 0.0263, 0.0381, 0.0497, 0.0088, 0.0263, 0.0234, 0.0380, 0.0263, 0.0789, 0.0323, 0.0497, 0.0293, 0.0263, 0.1082, 0.0409, 0.0292, 0.0643, 0.0409, 0.1140, 0.0293, 0.0760, 0.0323, 0.0351, 0.0263, 0.0322, 0.0351, 0.0585, 0.0322, 0.0146, 0.0323, 0.0673, 0.0909, 0.0439, 0.0497, 0.0322, 0.0234, 0.0468, 0.0322, 0.0263, 0.0352, 0.0526, 0.0352, 0.0468, 0.0614, 0.0175, 0.0351, 0.0234, 0.0175, 0.0468, 0.0792, 0.0526, 0.0381, 0.0409, 0.0731, 0.0351, 0.0322, 0.0351, 0.0351, 0.0263, 0.0323, 0.0322, 0.0381, 0.0263, 0.0205, 0.0614, 0.0380, 0.0789, 0.0614, 0.0292};
		
		double alpha = 0.05;
		MethodEnum method = MethodEnum.APPROXIMATE;
		TailEnum tail = TailEnum.BOTH;
		
		SignRank sr = new SignRank(d1, d2, alpha, tail, method);
		System.out.format("p-value: %e", sr.getP());
	}
	
}
