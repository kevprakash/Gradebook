package ui;

import backend.Assignment;
import backend.Gradebook;
import backend.GradebookException;
import backend.Student;

import javafx.scene.control.Button;

import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("restriction")
public class GradebookUI extends Application {

	private TableView<String[]> table = new TableView<String[]>();
	private TableView<String[]> weightTable = new TableView<String[]>();
	private TableView<String[]> gradeCategoryTable = new TableView<String[]>();
	private ObservableList<String> categories = FXCollections.observableArrayList();
	private VBox vbox = new VBox();
	private Gradebook gradebook = new Gradebook();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane root = new StackPane();

		// GRADE TABLE------------------------------------------------------------------------------------------
		ObservableList<String[]> data = FXCollections.observableArrayList();

		table = new TableView();

		TableColumn names = new TableColumn("Name");
		names.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
				return new SimpleStringProperty((p.getValue()[0]));
			}
		});
		TableColumn IDs = new TableColumn("ID");
		IDs.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
				return new SimpleStringProperty((p.getValue()[1]));
			}
		});
		names.setPrefWidth(150);
		IDs.setPrefWidth(150);
		table.getColumns().addAll(names, IDs);
		for (int i = 0; i < gradebook.getAssignments().size(); i++) {
			TableColumn column = new TableColumn(gradebook.getAssignments().get(i).getName());
			final int colNo = i;
			column.setCellFactory(TextFieldTableCell.forTableColumn());
			column.setOnEditCommit(new EventHandler<CellEditEvent<String[], String>>() {
				@Override
				public void handle(CellEditEvent<String[], String> t) {
					try {
						long ID = Long.parseLong(t.getTableView().getItems().get(t.getTablePosition().getRow())[1]);
						int row = gradebook.findStudentByID(ID);
						gradebook.getGrades().set(t.getTablePosition().getColumn() - 2, row,
								Integer.parseInt(t.getNewValue()));
					} catch (Exception e) {

					}
				}
			});
			column.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
					return new SimpleStringProperty((p.getValue()[colNo + 2]));
				}
			});
			column.setPrefWidth(150);
			table.getColumns().add(column);
		}

		table.setItems(data);
		table.setEditable(true);
		// GRADE TABLE END -----------------------------------------------------------------------------------

		// GRADE CATEGORY TABLE ------------------------------------------------------------------------------
		gradeCategoryTable = new TableView();
		TableColumn letterGrade = new TableColumn("Grade");
		TableColumn gradePercentColumn = new TableColumn("Percent");
		String[] letters = new String[] { "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-" };
		double[] gradePercentDouble = gradebook.getGradeCategories();
		ArrayList<String[]> gradeCategoryStringList = new ArrayList<String[]>();
		for (int i = 0; i < gradePercentDouble.length; i++) {
			if (gradePercentDouble[i] != 0) {
				gradeCategoryStringList.add(new String[] { letters[i], (gradePercentDouble[i] * 100) + "" });
			} else {
				gradeCategoryStringList.add(new String[] { letters[i], "-" });
			}
		}
		ObservableList<String[]> gradeCategoryData = FXCollections.observableArrayList();
		letterGrade.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
				return new SimpleStringProperty(param.getValue()[0]);
			}

		});
		gradePercentColumn
				.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
						return new SimpleStringProperty(param.getValue()[1]);
					}

				});
		gradePercentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		gradePercentColumn.setOnEditCommit(new EventHandler<CellEditEvent<String[], String>>() {
			@Override
			public void handle(CellEditEvent<String[], String> t) {
				int row = t.getTablePosition().getRow();
				try {
					double newValue = Double.parseDouble(t.getNewValue()) / 100;
					if ((row == 0 || gradebook.getGradeCategories()[row - 1] > newValue)
							&& (row == 11 || gradebook.getGradeCategories()[row + 1] < newValue)) {
						gradebook.getGradeCategories()[row] = newValue;
					}
					refreshGradeCategoryTable();
				} catch (Exception e) {
					refreshGradeCategoryTable();
				}
			}
		});
		gradeCategoryData.addAll(gradeCategoryStringList);
		letterGrade.setPrefWidth(150);
		letterGrade.setSortable(false);
		gradePercentColumn.setPrefWidth(150);
		gradePercentColumn.setSortable(false);
		gradeCategoryTable.getColumns().addAll(letterGrade, gradePercentColumn);
		gradeCategoryTable.setItems(gradeCategoryData);
		gradeCategoryTable.setEditable(true);
		// GRADE CATEGORY TABLE END --------------------------------------------------------------------------

		// WEIGHTS TABLE---------------------------------------------------------------------------------------
		weightTable = new TableView();
		TableColumn weightNameColumn = new TableColumn("Name");
		TableColumn weightPercent = new TableColumn("Percent");
		weightTable.getColumns().addAll(weightNameColumn, weightPercent);
		ObservableList<String[]> weightData = FXCollections.observableArrayList();
		weightData.addAll(gradebook.categoriesAsStringList());
		weightNameColumn
				.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
						return new SimpleStringProperty(param.getValue()[0]);
					}

				});
		weightPercent.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
				return new SimpleStringProperty(
						new DecimalFormat(".##").format(Double.parseDouble(param.getValue()[1]) * 100));
			}

		});
		weightPercent.setCellFactory(TextFieldTableCell.forTableColumn());
		weightPercent.setOnEditCommit(new EventHandler<CellEditEvent<String[], String>>() {

			@Override
			public void handle(CellEditEvent<String[], String> event) {
				try {
					int index = event.getTablePosition().getRow();
					gradebook.changeWeight(index, Double.parseDouble(event.getNewValue()) / 100);
				} catch (Exception e) {

				}
			}

		});
		weightTable.setEditable(true);
		weightTable.setItems(weightData);
		weightTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		weightNameColumn.setSortable(false);
		weightPercent.setSortable(false);
		
		HBox weightBox = new HBox();
		
		VBox weightBoxLeft = new VBox();
		
		HBox addWeightBox = new HBox();
		Button addCategory = new Button("Add Category");
		TextField categoryName = new TextField();
		categoryName.setPromptText("Category Name");
		TextField weightTextField = new TextField();
		weightTextField.setPromptText("Weight");
		addCategory.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					gradebook.addCategory(categoryName.getText(), Double.parseDouble(weightTextField.getText()) / 100);
				} catch (Exception e) {
					
				}
				categoryName.clear();
				weightTextField.clear();
				refreshAssignmentCategoryTable();
			}
		});
		addWeightBox.getChildren().addAll(addCategory, categoryName, weightTextField);
		addWeightBox.setSpacing(5);
		
		VBox classStatsBox = new VBox();
		Button getClassStatistics = new Button("Class Statistics");
		Label classStatistics = new Label();
		classStatistics.setText("-");
		getClassStatistics.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				double[] stats = gradebook.getClassStatistics();
				classStatistics.setText("Class average: " + stats[0]*100 + "%\nClass Standard Deviation: " + stats[1]*100 + "\nHighest Grade: " + stats[6]*100 + "%\n3rd Quartile: " + stats[5]*100 + "%\nMedian: " + stats[4]*100 + "%\n1st Quartile: " + stats[3]*100 + "%\nLowest Grade: " + stats[2]*100 + "%");
			}
			
		});
		classStatsBox.getChildren().addAll(getClassStatistics, classStatistics);
		classStatsBox.setSpacing(5);
		
		weightBoxLeft.getChildren().addAll(addWeightBox, classStatsBox);
		weightBoxLeft.setSpacing(5);
		
		weightBox.getChildren().addAll(weightTable, weightBoxLeft);
		weightBox.setSpacing(5);
		// WEIGHTS TABLE END ----------------------------------------------------------------------------------

		// ASSIGNMENT INFO-------------------------------------------------------------------------------------
		HBox assignmentModifyBox = new HBox();
		
		VBox assignmentBoxLeft = new VBox();
		VBox assignmentBoxRight = new VBox();
		
		HBox assignmentInfoBox = new HBox();
		Button assignmentInfoButton = new Button("Get Assignment Info");
		TextField assignmentRetrieveName = new TextField();
		assignmentRetrieveName.setPromptText("Assignment Name");
		Label assignmentInfoText = new Label("-");
		assignmentInfoButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				try {
					Assignment a = gradebook.getAssignment(assignmentRetrieveName.getText());
					double[] stats = gradebook.getAssignmentStatistics(a);
					for (int i = 0; i < stats.length; i++) {
						stats[i] = stats[i] * 100;
					}
					assignmentInfoText.setText("Name: " + a.getName() + "\nCategory: " + a.getCategory() + "\nOut of: "
							+ a.getDenominator() + "\nAverage Score: " + stats[0] + "%" + "\nStandard Deviation: "
							+ stats[1] + "\nMinimum Score: " + stats[2] + "%" + "\n1st Quartile: " + stats[3] + "%"
							+ "\nMedian: " + stats[4] + "%" + "\n3rd Quartile: " + stats[5] + "%" + "\nMax Score: "
							+ stats[6] + "%");
				} catch (Exception ex) {
					assignmentInfoText.setText("Invalid Input, try again");
					ex.printStackTrace();
				}
			}
		});
		assignmentInfoBox.getChildren().addAll(assignmentInfoButton, assignmentRetrieveName);
		assignmentBoxLeft.setSpacing(5);
		assignmentBoxLeft.getChildren().addAll(assignmentInfoBox, assignmentInfoText);
		
		HBox assignmentCurveBox = new HBox();
		Button curve = new Button("Curve to: ");
		TextField newDenominator = new TextField();
		newDenominator.setPromptText("New Denominator");
		newDenominator.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					newDenominator.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		curve.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				try {
					Assignment a = gradebook.getAssignment(assignmentRetrieveName.getText());
					a.setDenominator(Integer.parseInt(newDenominator.getText()));
					assignmentInfoText.setText(a.getName() + " curved to out of: " + a.getDenominator());
				} catch (Exception ex) {
					assignmentInfoText.setText("Invalid Input, try again");
					ex.printStackTrace();
				}
			}
		});
		assignmentCurveBox.getChildren().addAll(curve, newDenominator);
		assignmentCurveBox.setSpacing(5);
		
		HBox changeCategoryBox = new HBox();
		Button changeCategoryButton = new Button("Change Category");
		categories.addAll(gradebook.getCategories());
		final ComboBox categoryChange = new ComboBox(categories);
		changeCategoryButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				if(categoryChange.getValue() != null){
					try {
						Assignment a = gradebook.getAssignment(assignmentRetrieveName.getText());
						a.setCategory((String)categoryChange.getValue());
						assignmentInfoText.setText(a.getName() + " category changed to: " + (String)categoryChange.getValue());
					} catch (Exception ex) {
						assignmentInfoText.setText("Invalid Input, try again");
						ex.printStackTrace();
					}
				}
			}
			
		});
		changeCategoryBox.getChildren().addAll(changeCategoryButton, categoryChange);
		changeCategoryBox.setSpacing(5);
		
		Button removeAssignmentButton = new Button("Remove Assignment");
		removeAssignmentButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				try {
					gradebook.removeAssignment(assignmentRetrieveName.getText());
					assignmentInfoText.setText("Deleted assignment: " + assignmentRetrieveName.getText());
				} catch (GradebookException e) {
					assignmentInfoText.setText("Failed to delete assignment");
				}
				refreshGradeTable();
			};
		
		});
		
		assignmentBoxRight.setSpacing(5);
		assignmentBoxRight.getChildren().addAll(assignmentCurveBox, changeCategoryBox, removeAssignmentButton);
		assignmentModifyBox.setSpacing(5);
		assignmentModifyBox.getChildren().addAll(assignmentBoxLeft, assignmentBoxRight);
		// ASSIGNMENT INFO END---------------------------------------------------------------------------------

		// BUTTONS--------------------------------------------------------------------------------------------
		VBox addButtons = new VBox();

		HBox studentBox = new HBox();
		final TextField studentName = new TextField();
		studentName.setPromptText("Student Name");
		final TextField studentID = new TextField();
		studentID.setPromptText("Student ID");
		Label addStudentStatus = new Label("");
		studentID.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					studentID.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		final Button addStudent = new Button("Add Student");
		addStudent.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					gradebook.addStudent(new Student(studentName.getText(), Long.parseLong(studentID.getText())));
					addStudentStatus.setText("Added Student: " + studentName.getText());
				} catch (GradebookException e) {
					addStudentStatus.setText("Cannot add two Students with the same ID");
				} catch (Exception ex) {
					addStudentStatus.setText("Failed to add Student");
				}
				studentName.clear();
				studentID.clear();
				refreshGradeTable();
			}

		});
		studentBox.getChildren().addAll(addStudent, studentName, studentID, addStudentStatus);
		studentBox.setSpacing(5);
		
		HBox assignmentBox = new HBox();
		final TextField assignmentName = new TextField();
		assignmentName.setPromptText("Assignment Name");
		final ComboBox category = new ComboBox(categories);
		final TextField outOf = new TextField();
		outOf.setPromptText("Out of");
		outOf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					outOf.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		final Button addAssignment = new Button("Add Assignment");
		addAssignment.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (category.getValue() != null) {
					try {
						gradebook.addAssignment(new Assignment(assignmentName.getText(), (String) category.getValue(),
								Integer.parseInt(outOf.getText())));
					} catch (Exception e) {

					}
				}
				assignmentName.clear();
				outOf.clear();
				refreshGradeTable();
			}

		});
		assignmentBox.getChildren().addAll(addAssignment, assignmentName, category, outOf);
		assignmentBox.setSpacing(5);
		
		addButtons.getChildren().addAll(studentBox, assignmentBox);
		addButtons.setPadding(new Insets(0, 10, 10, 0));
		addButtons.setSpacing(5);

		VBox studentGradeDisplayBox = new VBox();
		HBox getGradeBox = new HBox();
		final Button getStudentGrade = new Button("Get Student Grade");
		final Label display = new Label("-");
		final TextField IDInput = new TextField();
		IDInput.setPromptText("Student ID");
		IDInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					IDInput.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		getStudentGrade.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					int index = gradebook.findStudentByID(Long.parseLong(IDInput.getText()));
					Student s = gradebook.getStudents().get(index);
					double studentGrade = gradebook.getStudentFinalGrade(s);
					display.setText("Name: " + s.getName() + "\nID: " + s.getID() + "\nGrade: " + (studentGrade * 100)
							+ "\nLetter Grade: " + gradebook.getLetterGrade(studentGrade));
				} catch (GradebookException e) {
					display.setText("No Student with ID: " + IDInput.getText());
				} catch (NumberFormatException e) {
					display.setText("Please input an ID");
				}
				IDInput.clear();
			}

		});
		Button removeStudent = new Button("Remove Student");
		removeStudent.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					gradebook.removeStudent(Long.parseLong(IDInput.getText()));
				} catch (GradebookException e) {
					display.setText("No Student with ID: " + IDInput.getText());
				} catch (NumberFormatException e) {
					display.setText("Please input an ID");
				}
				IDInput.clear();
				refreshGradeTable();
			}
		});
		
		
		
		Button importButton = new Button("Import XML");
		importButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				File file = new FileChooser().showOpenDialog(primaryStage);
				if(file != null){
					gradebook.importXML(file);
				}
				refreshGradeTable();
			}
			
		});
		
		HBox IOButtons = new HBox();
		TextField className = new TextField();
		className.setPromptText("Class Name");
		Button saveButton = new Button("Save");
		Label IOStatus = new Label();
		IOStatus.setText("-");
		saveButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				try {
					gradebook.save(className.getText());
					IOStatus.setText("Successfully saved: " + className.getText());
				} catch (IOException e) {
					IOStatus.setText("Failed to save: " + className.getText());
					e.printStackTrace();
				}
			}
			
		});
		Button loadButton = new Button("Load");
		loadButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				try {
					gradebook = new Gradebook();
					gradebook.load(className.getText());
					refreshGradeTable();
					refreshGradeCategoryTable();
					refreshAssignmentCategoryTable();
					IOStatus.setText("Successfully loaded: " + className.getText());
				} catch (Exception e) {
					IOStatus.setText("Failed to save: " + className.getText());
					e.printStackTrace();
				}
			}
			
		});
		IOButtons.getChildren().addAll(saveButton, loadButton, className, IOStatus);
		IOButtons.setSpacing(5);
		
		getGradeBox.getChildren().addAll(getStudentGrade, removeStudent, IDInput);
		getGradeBox.setSpacing(5);
		studentGradeDisplayBox.getChildren().addAll(getGradeBox, display);
		studentGradeDisplayBox.setSpacing(5);
		addButtons.getChildren().addAll(importButton, IOButtons, studentGradeDisplayBox, assignmentModifyBox);
		// BUTTONS END ------------------------------------------------------------------------------------------

		HBox bottom = new HBox();
		bottom.getChildren().addAll(addButtons, gradeCategoryTable, weightBox);
		bottom.setSpacing(5);

		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.getChildren().addAll(table, bottom);

		root.getChildren().addAll(vbox);
		primaryStage.setScene(new Scene(root, 1650, 850));
		primaryStage.show();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void refreshGradeTable() {
		while (table.getColumns().size() - 2 < gradebook.getAssignments().size()) {
			int i = table.getColumns().size() - 2;
			TableColumn column = new TableColumn(gradebook.getAssignments().get(i).getName());
			final int colNo = i;
			column.setCellFactory(TextFieldTableCell.forTableColumn());
			column.setOnEditCommit(new EventHandler<CellEditEvent<String[], String>>() {
				@Override
				public void handle(CellEditEvent<String[], String> t) {
					try {
						long ID = Long.parseLong(t.getTableView().getItems().get(t.getTablePosition().getRow())[1]);
						int row = gradebook.findStudentByID(ID);
						gradebook.getGrades().set(t.getTablePosition().getColumn() - 2, row,
								Integer.parseInt(t.getNewValue()));
					} catch (Exception e) {

					}
				}
			});
			column.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
					return new SimpleStringProperty((p.getValue()[colNo + 2]));
				}
			});
			column.setPrefWidth(150);
			table.getColumns().add(column);
		}
		
		if(table.getColumns().size() > gradebook.getAssignments().size()){
			table.getColumns().clear();
			TableColumn names = new TableColumn("Name");
			names.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
					return new SimpleStringProperty((p.getValue()[0]));
				}
			});
			TableColumn IDs = new TableColumn("ID");
			IDs.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
					return new SimpleStringProperty((p.getValue()[1]));
				}
			});
			names.setPrefWidth(150);
			IDs.setPrefWidth(150);
			table.getColumns().addAll(names, IDs);
			for (int i = 0; i < gradebook.getAssignments().size(); i++) {
				TableColumn column = new TableColumn(gradebook.getAssignments().get(i).getName());
				final int colNo = i;
				column.setCellFactory(TextFieldTableCell.forTableColumn());
				column.setOnEditCommit(new EventHandler<CellEditEvent<String[], String>>() {
					@Override
					public void handle(CellEditEvent<String[], String> t) {
						try {
							long ID = Long.parseLong(t.getTableView().getItems().get(t.getTablePosition().getRow())[1]);
							int row = gradebook.findStudentByID(ID);
							gradebook.getGrades().set(t.getTablePosition().getColumn() - 2, row,
									Integer.parseInt(t.getNewValue()));
						} catch (Exception e) {

						}
					}
				});
				column.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
						return new SimpleStringProperty((p.getValue()[colNo + 2]));
					}
				});
				column.setPrefWidth(150);
				table.getColumns().add(column);
			}
			table.setEditable(true);
		}

		ObservableList<String[]> data = FXCollections.observableArrayList();
		data.addAll(gradebook.getDataAsList());
		table.setItems(data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void refreshGradeCategoryTable() {
		gradeCategoryTable = new TableView();
		TableColumn letterGrade = new TableColumn("Grade");
		TableColumn gradePercentColumn = new TableColumn("Percent");
		String[] letters = new String[] { "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-" };
		double[] gradePercentDouble = gradebook.getGradeCategories();
		ArrayList<String[]> gradeCategoryStringList = new ArrayList<String[]>();
		for (int i = 0; i < gradePercentDouble.length; i++) {
			if (gradePercentDouble[i] != 0) {
				gradeCategoryStringList.add(new String[] { letters[i], (gradePercentDouble[i] * 100) + "" });
			} else {
				gradeCategoryStringList.add(new String[] { letters[i], "-" });
			}
		}
		ObservableList<String[]> gradeCategoryData = FXCollections.observableArrayList();
		letterGrade.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
				return new SimpleStringProperty(param.getValue()[0]);
			}

		});
		gradePercentColumn
				.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<String[], String> param) {
						return new SimpleStringProperty(param.getValue()[1]);
					}

				});
		gradePercentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		gradePercentColumn.setOnEditCommit(new EventHandler<CellEditEvent<String[], String>>() {
			@Override
			public void handle(CellEditEvent<String[], String> t) {
				int row = t.getTablePosition().getRow();
				try {
					double newValue = Double.parseDouble(t.getNewValue()) / 100;
					if ((row == 0 || gradebook.getGradeCategories()[row - 1] > newValue)
							&& (row == 11 || gradebook.getGradeCategories()[row + 1] < newValue)) {
						gradebook.getGradeCategories()[row] = newValue;
					}
					refreshGradeCategoryTable();
				} catch (Exception e) {
					refreshGradeCategoryTable();
				}
			}
		});
		gradeCategoryData.addAll(gradeCategoryStringList);
		letterGrade.setPrefWidth(150);
		letterGrade.setSortable(false);
		gradePercentColumn.setPrefWidth(150);
		gradePercentColumn.setSortable(false);
		gradeCategoryTable.getColumns().addAll(letterGrade, gradePercentColumn);
		gradeCategoryTable.setItems(gradeCategoryData);
		gradeCategoryTable.setEditable(true);
	}

	public void refreshAssignmentCategoryTable() {
		ObservableList<String[]> weightData = FXCollections.observableArrayList();
		weightData.addAll(gradebook.categoriesAsStringList());
		categories.clear();
		categories.addAll(gradebook.getCategories());
		weightTable.setItems(weightData);
		weightTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	public static void main(String[] args){
		launch(args);
	}
}
