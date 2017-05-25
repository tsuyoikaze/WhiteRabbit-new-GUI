import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.ohdsi.databases.DBConnector;
import org.ohdsi.databases.DbType;
import org.ohdsi.rabbitInAHat.ETLSQLGenerator;
import org.ohdsi.rabbitInAHat.dataModel.Database;
import org.ohdsi.rabbitInAHat.dataModel.ETL;
import org.ohdsi.rabbitInAHat.dataModel.Field;
import org.ohdsi.rabbitInAHat.dataModel.MappableItem;
import org.ohdsi.rabbitInAHat.dataModel.Mapping;
import org.ohdsi.rabbitInAHat.dataModel.Table;
import org.ohdsi.utilities.exception.DuplicateTargetException;
import org.ohdsi.utilities.exception.TypeMismatchException;
import org.ohdsi.whiteRabbit.ObjectExchange;

import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Cell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.application.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Main extends Application{
	
	private static final String FXML_MAIN_SCREEN_PATH = "screens/view/Screen2.fxml";
	private static final ExtensionFilter FILTER_XLSX = new FileChooser.ExtensionFilter("Excel spreadsheet", "*.xlsx");
	private static final String FXML_WELCOME_SCREEN_PATH = "screens/view/Screen1.fxml";
	private static final String DEFAULT_CDM_CSV_PATH = "src/org/ohdsi/rabbitInAHat/dataModel/CDMV5.0.1.backup.csv";
	private Button newProjectBtn;
	private Button openExcelBtn;
	private Button openProjectBtn;

	private Stage primaryStage;
	private Stage newWindow;
	private BorderPane mainLayout;
	
	FileChooser chooser;
	
	private ListView<String> listviewSource;
	private ListView<String> listviewTarget;
	
	private TableView<myConceptTable> conceptTable;
	private TableColumn<myConceptTable, String> rightColumn;
	private TableColumn<myConceptTable, String> leftColumn;
	
	private Text confirmation;
	
	private Table currentSourceTable, currentTargetTable;
	private Field currentSourceField, currentTargetField;
	
	 
	
	
	private String chooseFile(boolean saveMode, ExtensionFilter filter) {
		
		String result = null;

		if (chooser == null) {
			chooser = new FileChooser();
		}
		
		chooser.setSelectedExtensionFilter(filter);
		
		File selectedFile = saveMode ? chooser.showSaveDialog(primaryStage) : chooser.showOpenDialog(primaryStage);
		//System.out.println("Reach here");
		return selectedFile.getAbsolutePath();
	}
	
	@SuppressWarnings("unchecked")
	private void doOpenScanReport(String filename) {
		//System.out.println("Reach here1");
		if (filename != null) {
			
			mainLayout.setCursor(Cursor.WAIT);
			ETL etl = new ETL();
			try {
				InputStream dbfile = new FileInputStream(DEFAULT_CDM_CSV_PATH);
				
				etl.setSourceDatabase(Database.generateModelFromScanReport(filename));
				etl.setTargetDatabase(Database.generateModelFromCSV(dbfile, "CDM V5.0.1"));
				//tableMappingPanel.setMapping(etl.getTableToTableMapping());
				//TODO render mapping
				ObjectExchange.etl = etl;
			}
			 catch (Exception e) {
				e.printStackTrace();
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Invalid File Format");
				alert.showAndWait();
			}
			
			 mainLayout.setCursor(Cursor.DEFAULT);
			 try {
				Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource(FXML_MAIN_SCREEN_PATH)));
				//todo
				listviewSource = (ListView<String>) newScene.lookup("#listView_src");
				listviewTarget = (ListView<String>) newScene.lookup("#listView_target");
				confirmation = (Text) newScene.lookup("#confirm");
				
				System.out.println("1");
				loadListViewSecondScreen();
				System.out.println("2");
				
				
				
				primaryStage.setScene(newScene);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void start(final Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		//this.primaryStage.setTitle("Rabbit Catcher");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource(FXML_WELCOME_SCREEN_PATH));
		mainLayout = loader.load();
		Scene scene = new Scene(mainLayout);
		newProjectBtn = (Button) scene.lookup("#new_project_btn");
		openExcelBtn = (Button) scene.lookup("#open_excel_btn");
		//System.err.println(openExcelBtn);
		openProjectBtn = (Button) scene.lookup("#open_project_btn");
		System.out.println(newProjectBtn);
		
		
		//TODO should not have this in future
		newProjectBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent arg0) {
				new FXMLLoader();
				try {
					
					Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource(FXML_MAIN_SCREEN_PATH)));
					primaryStage.setScene(newScene);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		openExcelBtn.setOnMouseEntered(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				openExcelBtn.setCursor(Cursor.HAND);
			}
			
		});
		
		openExcelBtn.setOnMouseExited(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				openExcelBtn.setCursor(Cursor.DEFAULT);
			}
			
		});

		openExcelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent arg0) {
				doOpenScanReport(chooseFile(false, FILTER_XLSX));
				listviewSource.setOnMouseClicked(new EventHandler<MouseEvent>() {

			        @Override
			        public void handle(MouseEvent event) {
			        	
			        	if (listviewSource.getSelectionModel().getSelectedItem().startsWith("	")) {
			        		List<Field> fields = currentSourceTable.getFields();
			        		for (Field field : fields) {
			        			if (("	" + field.getName()).equals(listviewSource.getSelectionModel().getSelectedItem())) {
			        				currentSourceField = field;
			        			}
			        		}
			        		return;
			        	}
			        	
			            loadListViewWithDetail(listviewSource.getSelectionModel().getSelectedItem(), "source");
			            
			        }
			        
			    });
				listviewTarget.setOnMouseClicked(new EventHandler<MouseEvent>() {

			        @Override
			        public void handle(MouseEvent event) {
			        	
			        	if (listviewTarget.getSelectionModel().getSelectedItem().startsWith("	")) {
			        		List<Field> fields = currentTargetTable.getFields();
			        		for (Field field : fields) {
			        			if (("	" + field.getName()).equals(listviewTarget.getSelectionModel().getSelectedItem())) {
			        				currentTargetField = field;
			        				if (currentSourceField != null) {
			        					if (ObjectExchange.etl.getTableToTableMapping().getSourceToTargetMap(currentSourceTable, currentTargetTable) == null) {
			        						ObjectExchange.etl.getTableToTableMapping().addSourceToTargetMap(currentSourceTable, currentTargetTable);
			        					}
			        					ObjectExchange.etl.getFieldToFieldMapping(currentSourceTable, currentTargetTable).addSourceToTargetMap(currentSourceField, currentTargetField);
			        					System.out.println("Clicked");
			        					currentTargetField.setDisplayName(currentTargetField.getName() + " <" + currentSourceTable.getName() + "." + currentSourceField.getName() + ">");
			        					currentSourceField = null;
			        					loadListViewWithDetail(currentTargetTable.getName(), "target");
			        				}
			        				break;
			        			}
			        		}
			        		return;
			        	}
			        	
			            System.out.println("clicked on " + listviewTarget.getSelectionModel().getSelectedItem());
			            loadListViewWithDetail(listviewTarget.getSelectionModel().getSelectedItem(), "target");
			            listviewSource.getSelectionModel();
			        }
			        
			    });
				confirmation.setOnMouseClicked(new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent event) {
						try {
							newWindow = new Stage();
							Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen3.fxml")));
							conceptTable = (TableView) newScene.lookup("#conceptTable");
							
							ObservableList<myConceptTable> data = FXCollections.observableArrayList();
							
							
							Map<Field, Field> myMap = extractAllFIeldsRequiringConceptID();
							System.out.println("size: " + myMap.size());
							for (Map.Entry<Field, Field> entry : myMap.entrySet())
							{
								
								data.add(new myConceptTable(entry.getKey().getTable().getName() + "." + entry.getKey().getName(), entry.getValue().getTable().getName() + "." + entry.getValue().getName()));
							    System.out.println(entry.getKey() + "/" + entry.getValue());
							}
								
							
							newWindow.setTitle("Confirmation");
							newWindow.setScene(newScene);
							
							
							rightColumn = (TableColumn<myConceptTable, String>) conceptTable.getColumns().get(0);
							rightColumn.setCellValueFactory(new PropertyValueFactory<myConceptTable, String>("srcName"));
						
							
							leftColumn = (TableColumn<myConceptTable, String>) conceptTable.getColumns().get(1);
							leftColumn.setCellValueFactory(new PropertyValueFactory<myConceptTable, String>("mapName"));
							System.out.println(data.size());
							
							
							conceptTable.setItems(data);
							
							newWindow.show();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}

				});
			}
			
		});
		//openExcelBtn.addComponentListener(this);
		
		
		openProjectBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent arg0) {
				System.out.println("out");
				
			}
			
		});
		
		
		
		
		
		
		
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Rabbit Catcher");
		primaryStage.show();
	}
	
	
	public static class myConceptTable {
		
		private final SimpleStringProperty srcName;
		private final SimpleStringProperty mapName;
		
		private myConceptTable(String srcName, String mapName) {
			this.srcName = new SimpleStringProperty(srcName);
			this.mapName = new SimpleStringProperty(mapName);
		}
		
		public String getSrcName() {
	        return srcName.get();
	    }
	    public void setSrcName(String srcName) {
	        this.srcName.set(srcName);
	    }
	        
	    public String getMapName() {
	        return mapName.get();
	    }
	    public void setMapName(String mapName) {
	        this.mapName.set(mapName);
	    }
	}
	
	private void loadListViewSecondScreen() {
		//listview.setEditable(true);
		ObservableList<String> observableListSource = FXCollections.observableArrayList();
		ObservableList<String> observableListTarget = FXCollections.observableArrayList();
		
		List<Table> sourceTable = ObjectExchange.etl.getSourceDatabase().getTables();
		for (Table t : sourceTable) {
			observableListSource.add(t.getName());
		}
		
		List<Table> targetTable = ObjectExchange.etl.getTargetDatabase().getTables();
		for (Table t : targetTable) {
			observableListTarget.add(t.getName());
		}
		listviewSource.setItems(observableListSource);
		listviewTarget.setItems(observableListTarget);
	}
	
	private void loadListViewWithDetail(String click, String version) {
		if (version == "source") {
			
			currentSourceTable = ObjectExchange.etl.getSourceDatabase().getTableByName(click);
			
			ObservableList<String> observableListSource = FXCollections.observableArrayList();
		
			List<Table> sourceTableDetail = ObjectExchange.etl.getSourceDatabase().getTables();
			for (Table t: sourceTableDetail) {
				observableListSource.add(t.getName());
				if (t.getName().equals(click)) {
					int i = 0;
					for (i = 0; i < t.getFields().size(); i++) {
						observableListSource.add("	" + t.getFields().get(i).getDisplayName());
					}
			
				}
			
			}
			listviewSource.setItems(observableListSource);
		}
		else {
			ObservableList<String> observableListTarget = FXCollections.observableArrayList();
			
			currentTargetTable = ObjectExchange.etl.getTargetDatabase().getTableByName(click);
		
			List<Table> targetTableDetail = ObjectExchange.etl.getTargetDatabase().getTables();
			for (Table t: targetTableDetail) {
				observableListTarget.add(t.getName());
				if (t.getName().equals(click)) {
					System.out.println("Here");
					int i = 0;
					for (i = 0; i < t.getFields().size(); i++) {
						observableListTarget.add("	" + t.getFields().get(i).getDisplayName());
						
					}
			
				}
			
			}
			listviewTarget.setItems(observableListTarget);
		}
	}
	
	
	
	

	
	public static void main(String[] args) {
		launch(args);

	}
	
	private Map<Field, Field> extractAllFIeldsRequiringConceptID () {
		Map<Field, Field> result = new LinkedHashMap<>();
		List<Mapping<Field>> allMaps = ObjectExchange.etl.getAllMaps();
		System.out.println("All maps: " + allMaps.size());
		for (Mapping<Field> map : allMaps) {
			List<MappableItem> targetFields = map.getTargetItems();
			System.out.println("ggg: " + targetFields.toString());
			for (MappableItem targetField : targetFields) {
				if (map.getSourceItemsFromTarget(targetField).isEmpty()) {
					continue;
				}
				System.out.println("XXX: " + targetField + "\t" + ((Field) targetField).getConceptIDTable());
				MappableItem sourceField = map.getSourceItemsFromTarget(targetField).get(0);
				if (((Field) targetField).getConceptIDTable() != null && !((Field) targetField).getConceptIDTable().equals("")) {
					result.put((Field)sourceField, (Field)targetField);
				}
			}
		}
	
		return result;
	}
	
	private List<Pair<String, String>> doScanTable (DbType type, String address, String username, String password, String database, Table table, Field field, Table targetTable) throws SQLException {
		LinkedList<Pair<String, String>> result = new LinkedList<>();
		Connection con = DBConnector.connect(address, address, username, password, type);
		ResultSet set = con.prepareStatement("SELECT" + ETLSQLGenerator.getUniqueSourceField(table, targetTable).getName() + "," + field.getName() + " FROM " + table.getName() + ";").executeQuery();
		while (set.next()) {
			result.add(new Pair<>(set.getString(1), set.getString(2)));
		}
		return result;
	}
	
	private void doSaveSQL (String filename, String conceptIDSQL) throws FileNotFoundException {
		//TODO
		if (filename != null) {
			ETL.FileFormat fileFormat = ETL.FileFormat.SQL;
			
			String comment = null;
			String createTable = null;
			String mapString = null;
			
			boolean needConcept = false;
			List<Field> conceptList = new ArrayList<>();
			
			Mapping<Table> tableMap = ObjectExchange.etl.getTableToTableMapping();
			List<MappableItem> list = tableMap.getTargetItems();
			for (MappableItem targetTable : list) {
				
				List<MappableItem> sourceList = tableMap.getSourceItemsFromTarget(targetTable);
				
				
				for (MappableItem sourceTable : sourceList) {	
					List<Field> fields = ETLSQLGenerator.castToTable(targetTable).getFields();

					for (Field targetField : fields) {
						if (targetField.getName().toLowerCase().contains("concept")) {
							needConcept = true;
							conceptList.add(targetField);
						}
					}
				}
				
			}
			
			try {
				comment = ETLSQLGenerator.HEADER_COMMENT;
				createTable = ETLSQLGenerator.getCreateTable();
				mapString = ETLSQLGenerator.getMap();
			}
			catch (TypeMismatchException e) {
				JOptionPane.showMessageDialog(null, "The data types in current mapping does not match. Please only match fields with the same data type.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			catch (DuplicateTargetException e) {
				JOptionPane.showMessageDialog(null, "At least one target field is associated with multiple source fields. Please remove extra mappings and try again.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			PrintWriter writer = new PrintWriter(filename);
			writer.write(comment);
			writer.write(createTable);
			writer.write(mapString);
			writer.write(conceptIDSQL);
			writer.close();
		}
	}

}
