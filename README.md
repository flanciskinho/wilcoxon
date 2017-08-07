# Wilcoxon

Wilcoxon rank sum test

# Description

This test returns the p-value of a two-sided Wilcoxon rank sum test.
This tests the null hypothesis that data in x and y are samples from continuous distributions with equal medians, against the alternative that they are not.

The test assumes that the two samples are independent. x and y can have different lengths. 


# Use

```java
	double []x; // dataset 1
	double []y; // dataset 2
	
	double alpha = 0.05; // Significance level scalar value in the range 0 to 1
	
	// get the info for x and y arrays
	    
	double p = new SignRank(x, y, alpha).getP()
``` 	
	
# Create a jar

```bash
mvn package	
```	