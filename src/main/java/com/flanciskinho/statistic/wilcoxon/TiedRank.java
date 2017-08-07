package com.flanciskinho.statistic.wilcoxon;


public class TiedRank {
	private double []absdiff;
	private double []epsdiff;
	private double []rank;
	private int    []rowidx;
	
	private double []tieRank;

	private double tieAdj;
	
	private double []getAbsDiff(double []diff) {
		double []data = new double[diff.length];
		
		for (int cnt = 0; cnt < data.length; cnt++)
			data[cnt] = Math.abs(diff[cnt]);
		
		return data;
	}
	
	private void shellSort() {
		for (int cnt = 0; cnt < rowidx.length; cnt++)
			rowidx[cnt] = cnt;
		
		
		
		int j;
		double tmpAbs, tmpEps;
		int tmpRow;
	    for( int gap = absdiff.length / 2; gap > 0; gap /= 2 ) {
	      for( int i = gap; i < absdiff.length; i++ ) {
	         tmpAbs = absdiff[ i ];
	         tmpEps = epsdiff[ i ];
	         tmpRow = rowidx [ i ];
	         for( j = i; j >= gap && new Double(tmpAbs).compareTo( absdiff[ j - gap ] ) < 0; j -= gap ) {
	           absdiff[ j ] = absdiff[ j - gap ];
	           epsdiff[j] = epsdiff[j-gap];
	           rowidx[j] = rowidx[j-gap];
	         }
	         absdiff[ j ] = tmpAbs;
	         epsdiff[ j ] = tmpEps;
	         rowidx [ j ] = tmpRow;
	      }
	    }
		
	}
	
	public TiedRank(double []diff, double []epsdiff) {
		this.absdiff = getAbsDiff(diff);
		this.epsdiff = epsdiff;
	
		//Start tmp variables
		rowidx = new int[absdiff.length];
		rank   = new double[absdiff.length];
		for (int cnt = 0; cnt < rowidx.length; cnt++) {
			rowidx[cnt] = cnt;
			rank  [cnt] = cnt+1;
		}
		
		tr();
		
		//for (int cnt = 0; cnt < rowidx.length; cnt++)
			//System.out.format("%d %2.0f %2.0f %3.1f\n", (int) rowidx[cnt], absdiff[cnt], epsdiff[cnt], rank[cnt]);

		setTieRank();
		
		//for (int cnt = 0; cnt < tieRank.length; cnt++)
			//System.out.format("%2d %4.2f\n", cnt, tieRank[cnt]);
	}

	// Se calcula el rank
	private void tr() {
		shellSort();
		
		tieAdj = 0.0;
		
		int ntied;
		
		int tmp, aux;
		double v;
		int cnt;
		for (cnt = 0; cnt < absdiff.length; ) {
//System.out.format("id=%3d abs=%5.4f\n",cnt, absdiff[cnt]);

			for (aux = cnt+1; aux < absdiff.length; aux++) {
//System.out.format("\tid=%3d abs=%5.4f\n",aux, absdiff[aux]);				
				if (absdiff[cnt]+epsdiff[cnt] < absdiff[aux]-epsdiff[aux])
					break;
			}
			
			ntied = aux-cnt;
			if (ntied != 1) {
				tieAdj += ((double) ntied*(ntied-1)*(ntied+1)) /2.0; 
				
				v = 0.0;
				for (tmp = cnt+1; tmp <= aux; tmp++)
					v += tmp;
				
//System.out.format("\t\t%f = %d/%d (aux=%d, cnt=%d)\n", v/((double) ntied), v, ntied, aux, cnt);
				v = v/(double) (ntied);
				for (tmp = cnt; tmp < aux; tmp++) {
					rank[tmp] = v;
				}
			} else {
//System.out.format("\t\t%d\n", cnt+1);
				rank[cnt] = cnt+1;
			}
			
//System.out.println();
			
			cnt = aux;
		}

//for (cnt = 0; cnt < absdiff.length; cnt++)
//	System.out.format("%31.30f\n",absdiff[cnt]+epsdiff[cnt]);
for (cnt = 0; cnt < absdiff.length; cnt++)
	System.out.format("id:%2d %5f r:%4.2f %2d\n", cnt, absdiff[cnt], rank[cnt], rowidx[cnt]);

		
	}
	
	private void setTieRank() {
		tieRank = new double[rank.length];
		
		for (int cnt = 0; cnt < rank.length; cnt++)
			tieRank[rowidx[cnt]] = rank[cnt];
	}
	
	public double[] getTieRank() {
		return tieRank;
	}
	
	public double getTieAdj() {
		return this.tieAdj;
	}
	
	public static void main(String args[]) {
		double []v1 = {180.0, 210.0, 195.0, 220.0, 210.0, 190.0, 225.0, 215.0};
		double []v2 = {185.0, 225.0, 215.0, 245.0, 200.0, 220.0, 235.0, 250.0};
		
		double []d1 = new double[v1.length];
		double []d2 = new double[v2.length];
		
		for (int cnt = 0; cnt < v1.length; cnt++) {
			d1[cnt] = v1[cnt] - v2[cnt];
			d2[cnt] = Math.ulp(v1[cnt]) + Math.ulp(v2[cnt]);
		}
		
		new TiedRank(d1,d2);
	}
		
}
