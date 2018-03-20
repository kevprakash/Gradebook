package helper;

import java.util.Arrays;

public class Statistics {

	private Statistics(){}
	
	public static double average(double[] input){
		if(input.length == 0){
			return 0;
		}
		double sum = 0;
		for(double d : input){
			sum = sum + d;
		}
		return sum/input.length;
	}
	
	public static double weightedAverage(double[] input, double[] weights){
		double sum = 0;
		for(int i = 0; i < input.length; i++){
			sum = sum + (input[i] * weights[i]);
		}
		double weightSum = 0;
		for(double weight : weights){
			weightSum = weightSum + weight;
		}
		return sum/weightSum ;
	}
	
	public static double median(double[] input){
		Arrays.sort(input);
		if(input.length%2 == 0){
			return (input[input.length/2] + input[input.length/2 - 1])/2;
		}else{
			return input[input.length/2];
		}
	}
	
	public static double standardDeviation(double[] input){
		double summation = 0;
		double avg = average(input);
		for(double d : input){
			summation = summation + Math.pow(d - avg, 2);
		}
		return Math.sqrt(summation/(input.length - 1));
	}

	/** Returns (in order) smallest value, 1st quartile, median, 3rd quartile, largest value**/
	public static double[] quartiles(double[] input){
		double[] ret = new double[5];
		if(input.length == 1){
			return new double[]{input[0], input[0], input[0], input[0], input[0]};
		}
		Arrays.sort(input);
		ret[0] = input[0];
		ret[4] = input[input.length - 1];
		ret[2] = median(input);
		ret[1] = median(Arrays.copyOfRange(input, 0, input.length/2));
		ret[3] = median(Arrays.copyOfRange(input, input.length/2, input.length));
		return ret;
	}
}
