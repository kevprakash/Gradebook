package backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import helper.Statistics;
import helper.TwoDimensionalArray;

public class Gradebook {
	private TwoDimensionalArray grades;
	private ArrayList<Student> students = new ArrayList<Student>();
	private ArrayList<Assignment> assignments = new ArrayList<Assignment>();
	private ArrayList<String> categories = new ArrayList<String>();
	private ArrayList<Double> categoryWeights = new ArrayList<Double>();
	private final double[] gradeCategories = new double[]{.97, .93, .90, .87, .83, .80, .77, .73, .70, .67, .63, .60};
	
	
	public Gradebook(){
		setGrades(new TwoDimensionalArray(0,0,-1));
	}

	public ArrayList<String> getCategories(){
		return categories;
	}
	
	public ArrayList<Assignment> getAssignments() {
		return assignments;
	}

	public Assignment getAssignment(String name){
		for(int i = 0; i < assignments.size(); i++){
			if(assignments.get(i).getName().equals(name)){
				return assignments.get(i);
			}
		}
		return null;
	}
	
	public void setAssignments(ArrayList<Assignment> assignments) {
		this.assignments = assignments;
	}

	public TwoDimensionalArray getGrades() {
		return grades;
	}

	public void setGrades(TwoDimensionalArray grades) {
		this.grades = grades;
	}

	public ArrayList<Student> getStudents() {
		return students;
	}

	public void setStudents(ArrayList<Student> students) {
		this.students = students;
	}

	public void addAssignment(Assignment a){
		assignments.add(a);
		if(students.size() > 0){
			if(assignments.size() == 1){
				grades.appendRowsAndColumns(students.size(), 1, -1);
			}else{
				grades.appendColumn(-1);
			}
		}
	}
	
	public void addStudent(Student s) throws GradebookException{
		for(Student student : students){
			if(student.getID() == s.getID()){
				throw new GradebookException("Two Students Cannot Have the Same ID");
			}
		}
		students.add(s);
		if(assignments.size() > 0){
			if(students.size() == 1){
				grades.appendRowsAndColumns(1, assignments.size(), -1);
			}else{
				grades.appendRow(-1);
			}
		}
	}
	
	public void removeStudent(long ID) throws GradebookException{
		int index = findStudentByID(ID);
		if(index == -1){
			throw new GradebookException("No Student with ID: " + ID);
		}
		students.remove(index);
		if(grades.numOfRows() > 0)
			grades.removeRow(index);
	}
	
	public int findAssignment(String name) throws GradebookException{
		for(int i = 0; i < assignments.size(); i++){
			if(assignments.get(i).getName().equals(name)){
				return i;
			}
		}
			throw new GradebookException("No assignment with name: " + name);
	}
	
	public void removeAssignment(String name) throws GradebookException{
		int index = findAssignment(name);
		assignments.remove(index);
		if(grades.numOfColumns() > 0){
			grades.removeColumn(index);
		}
	}
	
	public ArrayList<String[]> getDataAsList(){
		if(assignments.isEmpty()){
			ArrayList<String[]> ret = new ArrayList<String[]>();
			for(int i = 0; i < students.size(); i++){
				ret.add(new String[]{students.get(i).getName(), students.get(i).getID() + ""});
			}
			return ret;
		}
		
		
		ArrayList<String[]> ret = grades.toStringLists();
		for(int i = 0; i < ret.size(); i++){
			String[] vals = ret.get(i);
			String[] temp = new String[vals.length + 2];
			temp[0] = students.get(i).getName();
			temp[1] = students.get(i).getID() + "";
			for(int j = 0; j < vals.length; j++){
				temp[j+2] = vals[j];
			}
			ret.set(i, temp);
		}
		return ret;
	}

	public int findStudentByID(long ID) throws GradebookException{
		for(int i = 0; i < students.size(); i++){
			if(students.get(i).getID() == ID){
				return i;
			}
		}
		throw new GradebookException("No Student with ID " + ID);
	}
	
	public void changeWeight(int index, double newWeight){
		categoryWeights.set(index, newWeight);
	}
	
	public void printGrades(){
		ArrayList<String[]> s = getDataAsList();
		for(String[] sa : s){
			System.out.println(Arrays.toString(sa));
		}
		System.out.println(grades);
	}
	
	public double[] getGradeCategories() {
		return gradeCategories;
	}
	
	public String getLetterGrade(double grade){
		String[] letters = new String[] { "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-" };
		for(int i = 0; i < gradeCategories.length; i++){
			if(grade >= gradeCategories[i]){
				return letters[i];
			}
		}
		return "F";
	}
	
	public double getStudentFinalGrade(Student s){
		int index = -1;
		for(int i = 0; i < students.size(); i++){
			if(students.get(i) == s){
				index = i;
				break;
			}
		}
		int[] studentGrades = grades.getRow(index);
		ArrayList<Double> percentages = new ArrayList<Double>();
		ArrayList<Integer> weightsIndices = new ArrayList<Integer>();
		for(int i = 0; i < assignments.size(); i++){
			Assignment a = assignments.get(i);
			if(categories.contains(a.getCategory()) && grades.get(i, index) != -1){
				int categoryIndex = -1;
				for(int j = 0; j < categories.size(); j++){
					if(categories.get(j).equals(a.getCategory())){
						categoryIndex = j;
						break;
					}
				}
				percentages.add(((double)studentGrades[i])/assignments.get(i).getDenominator());
				weightsIndices.add(categoryIndex);
			}
		}
		ArrayList<ArrayList<Double>> categorizedPercentages = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < categoryWeights.size(); i++){
			categorizedPercentages.add(new ArrayList<Double>());
		}
		for(int i = 0; i < percentages.size(); i++){
			categorizedPercentages.get(weightsIndices.get(i)).add(percentages.get(i));
		}
		int valuesLength = 0;
		for(int i = 0; i < categorizedPercentages.size(); i++){
			if(categorizedPercentages.get(i).size() > 0){
				valuesLength++;
			}
		}
		double[] weightedValues = new double[valuesLength];
		int skip = 0;
		for(int i = 0; i < weightedValues.length; i++){
			while(categorizedPercentages.get(i + skip).size() == 0){
				skip++;
			}
			double[] temp = new double[categorizedPercentages.get(i + skip).size()];
			for(int j = 0; j < temp.length; j++){
				temp[j] = categorizedPercentages.get(i + skip).get(j);
			}
			weightedValues[i] = Statistics.average(temp);
		}
		double[] weights = new double[valuesLength];
		int skip2 = 0;
		for(int i  = 0; i < weights.length; i++){
			while(categorizedPercentages.get(i + skip2).size() == 0){
				skip2++;
			}
			weights[i] = categoryWeights.get(i + skip2);
		}
		return Statistics.weightedAverage(weightedValues, weights);
	}

	public ArrayList<String[]> categoriesAsStringList(){
		ArrayList<String[]> ret = new ArrayList<String[]>();
		for(int i = 0; i < categories.size(); i++){
			ret.add(new String[]{categories.get(i), categoryWeights.get(i) + ""});
		}
		return ret;
	}

	public boolean addCategory(String name, double weight){
		if(categories.contains(name)){
			return false;
		}
		categories.add(name);
		categoryWeights.add(weight);
		return true;
	}

	public double[] getAssignmentStatistics(Assignment a){
		int column = -1;
		for(int i = 0; i < assignments.size(); i++){
			if(assignments.get(i).equals(a)){
				column = i;
				break;
			}
		}
		//Average, standard deviation, quartiles
		double[] retVals = new double[7];
		int[] gradeValues = grades.getColumn(column);
		int length = 0;
		for(int i = 0; i < gradeValues.length; i++){
			if(gradeValues[i] != -1){
				length++;
			}
		}
		if(length == 0){
			return new double[]{0, 0, 0, 0, 0, 0, 0};
		}
		double[] percentages = new double[length];
		int skip = 0;
		for(int i = 0; i < gradeValues.length; i++){
			if(gradeValues[i] == -1){
				skip++;
			}else{
				percentages[i - skip] = ((double)gradeValues[i])/a.getDenominator();
			}
		}
		retVals[0] = Statistics.average(percentages);
		retVals[1] = Statistics.standardDeviation(percentages);
		double[] quartiles = Statistics.quartiles(percentages);
		for(int i = 0; i < quartiles.length; i++){
			retVals[i + 2] = quartiles[i];
		}
		return retVals;
	}

	public void save(String className) throws IOException{
		Path file = Paths.get(className + ".txt");
		ArrayList<String> output = new ArrayList<String>();
		output.add("¿Categories:");
		for(int i = 0; i < categories.size(); i++){
			output.add(categories.get(i) + "|" + categoryWeights.get(i));
		}
		output.add("¿Assignments:");
		for(int i = 0; i < assignments.size(); i++){
			Assignment a = assignments.get(i);
			output.add(a.getName() + "|" + a.getCategory() + "|" + a.getDenominator());
		}
		output.add("¿Students:");
		for(int i = 0; i < students.size(); i++){
			Student s = students.get(i);
			output.add(s.getName() + "|" +s.getID());
		}
		output.add("¿Grades:");
		for(int i = 0; i < grades.numOfRows(); i++){
			String rowText = "";
			for(int j = 0; j < grades.numOfColumns(); j++){
				rowText += grades.get(j, i) + " ";
			}
			output.add(rowText);
		}
		output.add("¿END");
		Files.write(file, output, Charset.forName("UTF-8"));
	}

	public void load(String className) throws IOException, NumberFormatException, GradebookException{
		Path file = Paths.get(className + ".txt");
		List<String> input = Files.readAllLines(file, Charset.forName("UTF-8"));
		int state = -1;
		int rowIndex = 0;
		for(String line : input){
			if(line.charAt(0) == '¿'){
				switch(line){
				case "¿Categories:":
					state = 0;
					break;
				case "¿Assignments:":
					state = 1;
					break;
				case "¿Students:":
					state = 2;
					break;
				case "¿Grades:":
					state = 3;
					break;
				case "¿END":
					break;
				}
			}else{
				switch(state){
				case 0:
					String[] importCategory = line.split("\\|");
					categories.add(importCategory[0]);
					categoryWeights.add(Double.parseDouble(importCategory[1]));
					break;
				case 1:
					String[] importAssignment = line.split("\\|");
					addAssignment(new Assignment(importAssignment[0], importAssignment[1], Integer.parseInt(importAssignment[2])));
					break;
				case 2:
					String[] importStudent = line.split("\\|");
					addStudent(new Student(importStudent[0], Integer.parseInt(importStudent[1])));
					break;
				case 3:
					String[] importGradeRow = line.split("\\s+");
					for(int i = 0; i < importGradeRow.length; i++){
						grades.set(i, rowIndex, Integer.parseInt(importGradeRow[i]));
					}
					rowIndex++;
					break;
				}
			}
		}
	}
	
	public void importXML(File file){
		try {
		    OPCPackage fs = OPCPackage.open(new FileInputStream(file));
		    XSSFWorkbook wb = new XSSFWorkbook(fs);
		    XSSFSheet sheet = wb.getSheetAt(0);
		    XSSFRow row;
		    XSSFCell cell;

		    int rows;
		    rows = sheet.getPhysicalNumberOfRows();

		    int cols = 0;
		    int tmp = 0;

		    // This trick ensures that we get the data properly even if it doesn't start from first few rows
		    for(int i = 0; i < 10 || i < rows; i++) {
		        row = sheet.getRow(i);
		        if(row != null) {
		            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
		            if(tmp > cols) cols = tmp;
		        }
		    }

		    int[][] inputData = new int[cols][rows];
		    for(int r = 0; r < rows; r++) {
		        row = sheet.getRow(r);
		        if(row != null) {
		            for(int c = 0; c < cols; c++) {
		                cell = row.getCell(c);
		                if(cell != null) {
		                    inputData[c][r] = (int)cell.getNumericCellValue();
		                }else{
		                	inputData[c][r] = -1;
		                }
		            }
		        }
		    }
		    grades = new TwoDimensionalArray(assignments.size(), students.size(), inputData);
		    wb.close();
		} catch(Exception ioe) {
		    ioe.printStackTrace();
		}
	}

	public double[] getClassStatistics(){
		double[] ret = new double[7];
		double[] grades = new double[students.size()];
		for(int i = 0; i < grades.length; i++){
			grades[i] = getStudentFinalGrade(students.get(i));
		}
		ret[0] = Statistics.average(grades);
		ret[1] = Statistics.standardDeviation(grades);
		double[] quartiles = Statistics.quartiles(grades);
		for(int i = 0; i < 5; i++){
			ret[i + 2] = quartiles[i];
		}
		return ret;
	}
}
