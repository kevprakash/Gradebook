package helper;

import java.util.ArrayList;
import java.util.Arrays;

public class TwoDimensionalArray {

	private int[][] values = new int[0][0];

	public TwoDimensionalArray(int xSize, int ySize, int fillValue) {
		appendRowsAndColumns(ySize, xSize, fillValue);
	}

	public TwoDimensionalArray(int xSize, int ySize, int[][] fillValues){
		this(xSize, ySize, -1);
		for(int i = 0; i < Math.min(values.length, fillValues.length); i++){
			for(int j = 0; j < Math.min(values[0].length, fillValues[0].length); j++){
				values[i][j] = fillValues[i][j];
			}
		}
	}
	
	public int get(int x, int y) {
		return values[x][y];
	}

	public void set(int x, int y, int newValue) {
		values[x][y] = newValue;
	}

	public void appendRowsAndColumns(int numOfRows, int numOfColumns, int fillValue) {
		int[][] newValues = new int[values.length + numOfColumns][values.length > 0 ? values[0].length + numOfRows : numOfRows];
		for (int i = 0; i < newValues.length; i++) {
			for (int j = 0; j < newValues[i].length; j++) {
				if(i < values.length && j < values[i].length){
					newValues[i][j] = get(i, j);
				}else{
					newValues[i][j] = fillValue;
				}
			}
		}
		//System.out.println(this);
		values = newValues;
		//System.out.println(this);
	}

	public void appendRows(int numOfRows, int fillValue) {
		appendRowsAndColumns(numOfRows, 0, fillValue);
	}

	public void appendColumns(int numOfColumns, int fillValue) {
		appendRowsAndColumns(0, numOfColumns, fillValue);
	}

	public void appendRow(int fillValue) {
		appendRows(1, fillValue);
	}

	public void appendColumn(int fillValue) {
		appendColumns(1, fillValue);
	}

	public void removeRowsAndColumns(int startRow, int endRow, int startColumn, int endColumn) {
		int[][] newValues = new int[values.length  - (endColumn - startColumn)][values.length > 0 ? values[0].length - (endRow - startRow) : 0];
		/*if(newValues.length > 0){
			System.out.println(newValues.length + "|" + newValues[0].length);
		}else{
			System.out.println("Empty");
		}*/
		for (int i = 0; i < newValues.length; i++) {
			if (i < startColumn) {
				for (int j = 0; j < newValues[i].length; j++) {
					if (j < startRow) {
						newValues[i][j] = values[i][j];
					} else {
						newValues[i][j] = values[i][j + (endRow - startRow)];
					}
				}
			} else {
				for (int j = 0; j < newValues[i].length; j++) {
					if (j < startRow) {
						newValues[i][j] = values[i + (endColumn - startColumn)][j];
					} else {
						newValues[i][j] = values[i + (endColumn - startColumn)][j + (endRow - startRow)];
					}
				}
			}

		}
		//System.out.println(this);
		values = newValues;
		//System.out.println(this);
	}

	public void removeRows(int startRow, int endRow){
		removeRowsAndColumns(startRow, endRow, 0, 0);
	}
	
	public void removeColumns(int startColumn, int endColumn){
		removeRowsAndColumns(0, 0, startColumn, endColumn);
	}
	
	public void removeRow(int index){
		removeRows(index, index + 1);
	}
	
	public void removeColumn(int index){
		removeColumns(index, index + 1);
	}
	
	public int numOfRows(){
		return values.length > 0 ? values[0].length : 0;
	}
	
	public int numOfColumns(){
		return values.length;
	}
	
	public int[] getRow(int index){
		int[] ret = new int[numOfColumns()];
		for(int i = 0; i < ret.length; i++){
			ret[i] = values[i][index];
		}
		return ret;
	}
	
	public int[] getColumn(int index){
		return values[index];
	}
	
	public void shiftRow(int startIndex, int endIndex){
		int increment = startIndex < endIndex ? 1 : -1;
		for(int i = startIndex; i != endIndex; i += increment){
			swapRows(i, i + increment);
		}
	}
	
	public void shiftColumn(int startIndex, int endIndex){
		int increment = startIndex < endIndex ? 1 : -1;
		for(int i = startIndex; i != endIndex; i += increment){
			swapColumns(i, i + increment);
		}
	}
	
	public void swapRows(int rowIndex1, int rowIndex2){
		int[] r1 = getRow(rowIndex1);
		int[] r2 = getRow(rowIndex2);
		int rowLength = numOfColumns();
		for(int i = 0; i < rowLength; i++){
			values[i][rowIndex1] = r2[i];
			values[i][rowIndex2] = r1[i];
		}
	}
	
	public void swapColumns(int columnIndex1, int columnIndex2){
		int[] c1 = getColumn(columnIndex1);
		int[] c2 = getColumn(columnIndex2);
		values[columnIndex1] = c2;
		values[columnIndex2] = c1;
	}

	/** WARNING: Fairly slow, use sparingly**/
	public void insertRow(int index, int fillValue){
		appendRow(fillValue);
		shiftRow(numOfRows()-1, index);
	}
	
	/** WARNING: Fairly slow, use sparingly**/
	public void insertColumn(int index, int fillValue){
		appendColumn(fillValue);
		shiftColumn(numOfColumns()-1, index);
	}

	public ArrayList<String[]> toStringLists(){
		ArrayList<String[]> ret = new ArrayList<String[]>();
		//System.out.println(numOfRows());
		for(int i = 0; i < numOfRows(); i++){
			String[] temp = new String[numOfColumns()];
			for(int j = 0; j < numOfColumns(); j++){
				if(values[j][i] != -1){
					temp[j] = values[j][i] + "";
				}else{
					temp[j] = "-";
				}
			}
			ret.add(temp);
		}
		return ret;
	}

	public String toString(){
		String ret = "";
		for(int[] row : values){
			ret += Arrays.toString(row) + "\n";
		}
		return ret;
	}
}
