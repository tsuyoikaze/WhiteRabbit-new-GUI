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
import org.ohdsi.utilities.ConceptIDFetcher;
import org.ohdsi.utilities.exception.DuplicateTargetException;
import org.ohdsi.utilities.exception.TypeMismatchException;
import org.ohdsi.whiteRabbit.ObjectExchange;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{
	
	private static final String FXML_MAIN_SCREEN_PATH = "screens/view/Screen2.fxml";
	private static final ExtensionFilter FILTER_XLSX = new FileChooser.ExtensionFilter("Excel spreadsheet", "*.xlsx");
	private static final ExtensionFilter FILTER_SQL = new FileChooser.ExtensionFilter("SQL File", "*.sql");
	private static final ExtensionFilter FILTER_JSON = new FileChooser.ExtensionFilter("JavaScript Object Notation", "*.json");
	private static final ExtensionFilter FILTER_GZ = new FileChooser.ExtensionFilter("GZip Compressed file", "*.tar.gz");
	private static final String FXML_WELCOME_SCREEN_PATH = "screens/view/Screen1.fxml";
	private static final String DEFAULT_CDM_CSV_PATH = "src/org/ohdsi/rabbitInAHat/dataModel/CDMV5.0.1.backup.csv";
	private Button newProjectBtn;
	private Button openExcelBtn;
	private Button openProjectBtn;
	private Button okayButton;
	private Button inputButton;
	private Button nextButton;
	private Button manualEnterButton;
	private Button ignoreButton;
	private Button saveSQL;
	
	private Button yesButton;
	private Button noButton;
	private Button cancelButton;
	
	private ProgressBar progressBar;
	
	private ComboBox<String> typeButton;

	private Stage primaryStage;
	private Stage newWindow;
	private Stage toSaveWindow;
	private Pane mainLayout;
	
	FileChooser chooser;
	
	private TableView<myConceptTable> conceptTable;
	private TableColumn<myConceptTable, String> rightColumn;
	private TableColumn<myConceptTable, String> leftColumn;
	
	private TextField passwordField;
	private TextField userNameField;
	private TextField dbNameField;
	private TextField ipField;
	private Text showType;
	private Text showIP;
	private Text showScanningPath;
	
	private String password;
	private String username;
	private String dbName;
	private String ipName;
	private String type;
	private boolean isClosed;
	
	private MenuBar myMenuBar;
	private Menu fileMenu;
	private MenuItem saveFile;
	private MenuItem nextStep;
	private MenuItem closeFile;
	
	private Pane myPane;
	private Pane thePane;
	private ScrollPane myScrollPane;
	private HBox myHBox;
	
	private double height;
	
	public static LinkedList<SourceTableRectangle> sourceTableRects = new LinkedList<>();
	public static LinkedList<TargetTableRectangle> targetTableRects = new LinkedList<>();
	 
	
	
	private String chooseFile(boolean saveMode, ExtensionFilter filter) {
		
		if (chooser == null) {
			chooser = new FileChooser();
		}
		
		chooser.setSelectedExtensionFilter(filter);
		
		File selectedFile = saveMode ? chooser.showSaveDialog(primaryStage) : chooser.showOpenDialog(primaryStage);
		
		return selectedFile.getAbsolutePath();
	}
	
	private void doOpenScanReport(String filename) {
		
		if (filename != null) {
			
			mainLayout.setCursor(Cursor.WAIT);
			ETL etl = new ETL();
			try {
				InputStream dbfile = new FileInputStream(DEFAULT_CDM_CSV_PATH);
				
				etl.setSourceDatabase(Database.generateModelFromScanReport(filename));
				etl.setTargetDatabase(Database.generateModelFromCSV(dbfile, "CDM V5.0.1"));
				
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
				Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen2.fxml")));
				
				myMenuBar = (MenuBar) newScene.lookup("#menu");
				fileMenu = myMenuBar.getMenus().get(0);
				saveFile = fileMenu.getItems().get(0);
				nextStep = fileMenu.getItems().get(1);
				closeFile = fileMenu.getItems().get(2);

				thePane = (Pane) newScene.lookup("#thePane");
				myPane = (Pane) newScene.lookup("#myPane");
				myScrollPane = (ScrollPane) newScene.lookup("#myScrollPane");
				myHBox = (HBox) newScene.lookup("#myHBox");
				progressBar = (ProgressBar) newScene.lookup("#progress_bar");

				
				thePane = (Pane)myScrollPane.getParent();
				myPane = (Pane) myScrollPane.getContent();

				myScrollPane.prefWidthProperty().bind(thePane.widthProperty());
				myScrollPane.prefHeightProperty().bind(thePane.heightProperty().subtract(20));
				myPane.prefWidthProperty().bind(myScrollPane.widthProperty());
				
				myMenuBar.prefWidthProperty().bind(thePane.widthProperty());
				myMenuBar.prefHeightProperty().bind(thePane.heightProperty());
				myHBox.prefWidthProperty().bind(thePane.widthProperty());
				myHBox.prefHeightProperty().bind(thePane.heightProperty());

				progressBar.prefWidthProperty().bind(thePane.widthProperty());

				loadRectangle();
				
				
				
				
				saveFile.setOnAction(new EventHandler<ActionEvent>(){

					@Override
					public void handle(ActionEvent arg0) {
						doSave(chooseFile(true, FILTER_GZ));
					}
					
				});
				
				primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					
					@Override
					public void handle(WindowEvent event) {
						
						if (isClosed) {
							return;
						}
						else {
							event.consume();
						}
						

						try {
							
							Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen8.fxml")));
							toSaveWindow = new Stage();
							yesButton = (Button) newScene.lookup("#yes_button");
							noButton = (Button) newScene.lookup("#no_button");
							cancelButton = (Button) newScene.lookup("#cancel_button");
							
							toSaveWindow.setTitle("");
							toSaveWindow.setScene(newScene);
							toSaveWindow.show();
							
							noButton.setOnAction(new EventHandler<ActionEvent>() {
								
								@Override
								public void handle(ActionEvent args) {
									toSaveWindow.close();
									if (newWindow != null) {
										isClosed = true;
										newWindow.close();
									}
									primaryStage.close();
								}
							});
							
							yesButton.setOnAction(new EventHandler<ActionEvent>() {
								
								@Override
								public void handle(ActionEvent args) {
									toSaveWindow.close();
									if (newWindow != null) {
										isClosed = true;
										newWindow.close();
									}
									if (primaryStage != null) {
										doSave(chooseFile(true, FILTER_GZ));
										
										primaryStage.close();
									}
								}
							});
							
							cancelButton.setOnAction(new EventHandler<ActionEvent>(){

								@Override
								public void handle(ActionEvent arg0) {
									toSaveWindow.close();
								}
								
							});
							
							
							
						
						} catch (IOException e) {
							e.printStackTrace();
						}
		                
					}
				});
				
				closeFile.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent args) {
						
						try {
							
							Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen8.fxml")));
							toSaveWindow = new Stage();
							yesButton = (Button) newScene.lookup("#yes_button");
							noButton = (Button) newScene.lookup("#no_button");
							
							toSaveWindow.setTitle("Exit Confirmation");
							toSaveWindow.setScene(newScene);
							toSaveWindow.show();
							
							noButton.setOnAction(new EventHandler<ActionEvent>() {
								
								@Override
								public void handle(ActionEvent args) {
									toSaveWindow.close();
									if (newWindow != null) {
										newWindow.close();
									}
									primaryStage.close();
								}
							});
							
							yesButton.setOnAction(new EventHandler<ActionEvent>() {
								
								@Override
								public void handle(ActionEvent args) {
									//save the process
									
									toSaveWindow.close();
									if (newWindow != null) {
										newWindow.close();
									}
									if (primaryStage != null) {
										doSave(chooseFile(true, FILTER_GZ));
										
										primaryStage.close();
									}
								}
							});
							
							
						
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				});
				
				primaryStage.setScene(newScene);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void start(final Stage primaryStage) throws IOException {
		
		this.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource(FXML_WELCOME_SCREEN_PATH));
		mainLayout = loader.load();
		Scene scene = new Scene(mainLayout);
		newProjectBtn = (Button) scene.lookup("#new_project_btn");
		openExcelBtn = (Button) scene.lookup("#open_excel_btn");
		openProjectBtn = (Button) scene.lookup("#open_project_btn");
		
		
		//TODO should not have this in future
		newProjectBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent arg0) {
				new FXMLLoader();
				try {
					
					Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource(FXML_MAIN_SCREEN_PATH)));
					primaryStage.setScene(newScene);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		openExcelBtn.setOnMouseEntered(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				openExcelBtn.setCursor(Cursor.HAND);
			}
			
		});
		
		openExcelBtn.setOnMouseExited(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				openExcelBtn.setCursor(Cursor.DEFAULT);
			}
			
		});

		openExcelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent arg0) {
				doOpenScanReport(chooseFile(false, FILTER_XLSX));
				myPane.setMinHeight(height);
				  nextStep.setOnAction(new EventHandler<ActionEvent>() {
					  
					@SuppressWarnings("unchecked")
					@Override
					  public void handle(ActionEvent event) {	
						try {
							newWindow = new Stage();
							Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen3.fxml")));
							conceptTable = (TableView<myConceptTable>) newScene.lookup("#conceptTable");
							okayButton = (Button) newScene.lookup("#okay_button");

							conceptTable.getColumns().get(0).prefWidthProperty().bind(conceptTable.widthProperty().divide(2));
							conceptTable.getColumns().get(1).prefWidthProperty().bind(conceptTable.widthProperty().divide(2));
							
							ObservableList<myConceptTable> data = FXCollections.observableArrayList();
							
							
							Map<Field, Field> myMap = extractAllFIeldsRequiringConceptID();
							ObjectExchange.conceptIDFieldMap = myMap;
							for (Map.Entry<Field, Field> entry : myMap.entrySet())
							{
								data.add(new myConceptTable(entry.getKey().toString(), entry.getValue().toString()));
							}
								
							
							newWindow.setTitle("Confirmation");
							newWindow.setScene(newScene);
							
							
							rightColumn = (TableColumn<myConceptTable, String>) conceptTable.getColumns().get(0);
							rightColumn.setCellValueFactory(new PropertyValueFactory<myConceptTable, String>("srcName"));
						
							
							leftColumn = (TableColumn<myConceptTable, String>) conceptTable.getColumns().get(1);
							leftColumn.setCellValueFactory(new PropertyValueFactory<myConceptTable, String>("mapName"));
							if (data.size() == 0) {
								data.add(new myConceptTable(" ", " "));
							}
							
							
							conceptTable.setItems(data);
							
							newWindow.show();
							progressBar.setProgress(0.67);
							newWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {

								@Override
								public void handle(WindowEvent arg0) {
									progressBar.setProgress(0.33);
								}
								
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						
						
						okayButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
							public void handle(MouseEvent arg0) {
								
								try {
									Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen4_try_2.fxml")));
									//TODO change to password field
									passwordField = (TextField) newScene.lookup("#password_field");
									userNameField = (TextField) newScene.lookup("#username_field");
									dbNameField = (TextField) newScene.lookup("#dbname_field");
									ipField = (TextField) newScene.lookup("#ip_field");
									inputButton = (Button) newScene.lookup("#ok_button");
									typeButton = (ComboBox<String>) newScene.lookup("#combo_type");
									typeButton.setValue("MYSQL");
									ObservableList<String> typeList = FXCollections.observableArrayList();
									typeList.add("MSSQL");
									typeList.add("ORACLE");
									typeList.add("POSTGRESQL");
									typeList.add("MSACCESS");
									typeList.add("REDSHIFT");
									typeButton.setItems(typeList);
									
									
									
									newWindow.setScene(newScene);
								} catch (IOException e) {
									e.printStackTrace();
								}
								
								inputButton.setOnAction(new EventHandler<ActionEvent>() {
									public void handle (ActionEvent e) {
										if (passwordField.getText() != null) {
											password = passwordField.getText();
										}
										if (userNameField.getText() != null) {
											username = userNameField.getText();
										}
										if (dbNameField.getText() != null) {
											dbName = dbNameField.getText();
										}
										if (ipField.getText() != null) {
											ipName = ipField.getText();
										}
										type = (String) typeButton.getValue();
										
										try {
											Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen5_try_2.fxml")));
											nextButton = (Button) newScene.lookup("#next_button");
											showType = (Text) newScene.lookup("#showType");
											showIP = (Text) newScene.lookup("#showIP");
											showScanningPath = (Text) newScene.lookup("#showPath");
											showIP.setText(ipName);
											showType.setText(type);
											
											newWindow.setScene(newScene);
											DbType myDBType = new DbType(type);
											for (Map.Entry<Field, Field> item : ObjectExchange.conceptIDFieldMap.entrySet()) {
												showScanningPath.setText(item.getKey().getTable().getName() + "." + item.getKey().getName());
												List<org.ohdsi.utilities.collections.Pair<String, String>> list = doScanTable(myDBType, ipName, "", username, password, dbName, item.getKey().getTable(), item.getKey(), item.getValue().getTable());
												ObjectExchange.dbScanResult = list;
												LinkedList<String> l = new LinkedList<>();
												for (org.ohdsi.utilities.collections.Pair<String, String> item2 : list) {
													if (!l.contains(item2.getItem2())) {
														l.add(item2.getItem2());
													}
												}
												Map<String, Integer> conceptIDMap = ConceptIDFetcher.fetchConceptIDs(l, "src/org/ohdsi/rabbitInAHat/dataModel/CONCEPT_TRUNCATED.csv.gz");
												ObjectExchange.conceptIDDataMap = conceptIDMap;
												ObjectExchange.conceptIDString += ETLSQLGenerator.getConceptIDMap(item.getValue(), conceptIDMap, list);
												
											}
											
										
													TableView<myConceptTable> manualEnterTable = null;
													try {
														newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen6_try.fxml")));
														manualEnterTable = (TableView<myConceptTable>) newScene.lookup("#conceptTable");
														manualEnterButton = (Button) newScene.lookup("#manually_enter");
														ignoreButton = (Button) newScene.lookup("#ignore_button");
														newWindow.setScene(newScene);
													} catch (IOException e1) {
														e1.printStackTrace();
													}
													
													ObservableList<myConceptTable> data = FXCollections.observableArrayList();
													List<org.ohdsi.utilities.collections.Pair<String, String>> dbScanResult = ObjectExchange.dbScanResult;
													
													for (org.ohdsi.utilities.collections.Pair<String, String> entry : dbScanResult)
													{
														String item = entry.getItem2();
														if (!ObjectExchange.conceptIDDataMap.containsKey(item)) {															
															data.add(new myConceptTable(item, ""));
														}
													}
													
													((TableColumn<myConceptTable, String>) manualEnterTable.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<myConceptTable, String>("mapName"));
													((TableColumn<myConceptTable, String>) manualEnterTable.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<myConceptTable, String>("srcName"));
													
													manualEnterTable.setItems(data);
													manualEnterTable.setEditable(true);
													
													final TableView<myConceptTable> finalManualEnterTable = manualEnterTable;
													
													manualEnterButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

														@Override
														public void handle(MouseEvent arg0) {
															for (myConceptTable obj : (ObservableList<myConceptTable>) finalManualEnterTable.getItems()) {
																ObjectExchange.conceptIDDataMap.put(obj.getSrcName(), Integer.parseInt(obj.getMapName()));
															}
															ignoreButton.getOnMouseClicked().handle(null);
															
														}
														
													});
													
													ignoreButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

														public void handle(MouseEvent arg0) {
															newWindow.close();
															try {
																Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen7_try.fxml")));
																saveSQL = (Button) newScene.lookup("#save_as_sql");
																
																primaryStage.setScene(newScene);
															} catch (IOException e) {
																e.printStackTrace();
															}
															
															saveSQL.setOnMouseClicked(new EventHandler<MouseEvent>() {

																public void handle(MouseEvent arg0) {
																	try {
																		doSaveSQL(chooseFile(true, FILTER_SQL), ObjectExchange.conceptIDString);
																	} catch (FileNotFoundException e) {
																		e.printStackTrace();
																	}
																	
																}
																
															});
															
														}
														
													});
											
											//nextButton.fire();
										}catch (SQLException e1) {
											e1.printStackTrace();
										} catch (IOException e1) {
											e1.printStackTrace();
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
										
									}
								});
								primaryStage.show();
								
								
							
							}
						
						});
						
					}
					
					
					

				});
			}
			
		});
		
		//TODO not yet implemented
		//openExcelBtn.addComponentListener(this);
		
		openProjectBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent arg0) {
				//TODO not yet implemented
			}
			
		});
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Rabbit Catcher");
		primaryStage.show();
	}
	
	private void loadRectangle() {
		//put src table into myPane
		int yVal = 50;
		
		int maxHeight = 0;
		
		SourceTableRectangle.displayPane = myPane;
		SourceTableRectangle.sourceTableRects = sourceTableRects;
		TargetTableRectangle.displayPane = myPane;
		TargetTableRectangle.targetTableRects = targetTableRects;
		
		List<Table> sourceTable = ObjectExchange.etl.getSourceDatabase().getTables();
		for (Table t : sourceTable) {
			SourceTableRectangle newNode = new SourceTableRectangle(yVal, t);
			sourceTableRects.add(newNode);
			myPane.getChildren().add(newNode);
			yVal += 30 + 10;
		}
		
		maxHeight = Math.max(maxHeight, yVal + 90);
		
		yVal = 50;
		List<Table> targetTable = ObjectExchange.etl.getTargetDatabase().getTables();
		for (Table t : targetTable) {
			TargetTableRectangle newNode = new TargetTableRectangle(yVal, t);
			targetTableRects.add(newNode);
			myPane.getChildren().add(newNode);
			yVal += 30 + 10;
		}
		
		maxHeight = Math.max(maxHeight, yVal + 60);
		
		myPane.setPrefHeight(maxHeight);
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
	
	public static void main(String[] args) {
		launch(args);

	}
	
	private Map<Field, Field> extractAllFIeldsRequiringConceptID () {
		Map<Field, Field> result = new LinkedHashMap<>();
		List<Mapping<Field>> allMaps = ObjectExchange.etl.getAllMaps();
		for (Mapping<Field> map : allMaps) {
			List<MappableItem> targetFields = map.getTargetItems();
			for (MappableItem targetField : targetFields) {
				if (map.getSourceItemsFromTarget(targetField).isEmpty()) {
					continue;
				}
				MappableItem sourceField = map.getSourceItemsFromTarget(targetField).get(0);
				if (((Field) targetField).getConceptIDTable() != null && !((Field) targetField).getConceptIDTable().equals("")) {
					result.put((Field)sourceField, (Field)targetField);
				}
			}
		}
	
		return result;
	}
	
	private List<org.ohdsi.utilities.collections.Pair<String, String>> doScanTable (DbType type, String address, String domain, String username, String password, String database, Table table, Field field, Table targetTable) throws SQLException {
		LinkedList<org.ohdsi.utilities.collections.Pair<String, String>> result = new LinkedList<>();
		Connection con = DBConnector.connect(address, domain, username, password, type, database);
		ResultSet set = con.createStatement().executeQuery("SELECT " + ETLSQLGenerator.getUniqueSourceField(table, targetTable).getName() + "," + field.getName() + " FROM " + table.getName() + ";");
		
		//progress bar
		int count = 0;
		int toUse = 0;
		while (set.next()) {
			count++;
		}
		set = con.createStatement().executeQuery("SELECT " + ETLSQLGenerator.getUniqueSourceField(table, targetTable).getName() + "," + field.getName() + " FROM " + table.getName() + ";");
		while (set.next()) {
			result.add(new org.ohdsi.utilities.collections.Pair<>(set.getString(1), set.getString(2)));
			
			//progress bar
			progressBar.setProgress(toUse/count);
			toUse++;
		}
		return result;
	}
	
	private void doSaveSQL (String filename, String conceptIDSQL) throws FileNotFoundException {
		
		//TODO double check
		if (filename != null) {
			ETL.FileFormat fileFormat = ETL.FileFormat.SQL;
			
			String comment = null;
			String createTable = null;
			String mapString = null;
			
			List<Field> conceptList = new ArrayList<>();
			
			Mapping<Table> tableMap = ObjectExchange.etl.getTableToTableMapping();
			List<MappableItem> list = tableMap.getTargetItems();
			for (MappableItem targetTable : list) {
				
				List<MappableItem> sourceList = tableMap.getSourceItemsFromTarget(targetTable);
				
				
				for (@SuppressWarnings("unused") MappableItem sourceTable : sourceList) {	
					List<Field> fields = ETLSQLGenerator.castToTable(targetTable).getFields();

					for (Field targetField : fields) {
						if (targetField.getName().toLowerCase().contains("concept")) {
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
	
	private void doSave(String filename) {
		if (filename != null) {
			ETL.FileFormat fileFormat = filename.endsWith("json.gz") ? ETL.FileFormat.GzipJson : filename.endsWith("json") ? ETL.FileFormat.Json
					: ETL.FileFormat.Binary;
			ObjectExchange.etl.save(filename, fileFormat);
		}
	}

}
