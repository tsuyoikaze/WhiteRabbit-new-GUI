import java.awt.Event;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import org.ohdsi.utilities.ConceptIDFetcher;
import org.ohdsi.utilities.exception.DuplicateTargetException;
import org.ohdsi.utilities.exception.TypeMismatchException;
import org.ohdsi.whiteRabbit.ObjectExchange;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Cell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.application.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

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
	
	private ProgressBar progressBar;
	
	private ComboBox typeButton;

	private Stage primaryStage;
	private Stage newWindow;
	private Stage toSaveWindow;
	private Pane mainLayout;
	
	FileChooser chooser;
	
	private ListView<String> listviewSource;
	private ListView<String> listviewTarget;
	
	private TableView<myConceptTable> conceptTable;
	private TableColumn<myConceptTable, String> rightColumn;
	private TableColumn<myConceptTable, String> leftColumn;
	
	private Text confirmation;
	
	private Table currentSourceTable, currentTargetTable;
	private Field currentSourceField, currentTargetField;
	private Table trackCurrentTargetField;
	private int trackCurrent = 0;
	
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
	private String typeName;
	private String type;
	
	private MenuBar myMenuBar;
	private Menu fileMenu;
	private MenuItem saveFile;
	private MenuItem nextStep;
	private MenuItem closeFile;
	
	private TreeView srcTreeView;
	private TreeView targetTreeView;
	
	private List<Node> srcCells;
	private List<Node> targetCells;
	
	private TreeItem<Text> root;
	private TreeItem<Text> root_2;
	
	private double srcX;
	private double srcY;
	private double targetX;
	private double targetY;
	
	private Pane myPane;
	private Pane thePane;
	private Pane addPane;
	private ScrollPane myScrollPane;
	private HBox myHBox;
	private ProgressBar myProgressBar;
	
	private Pane mainPane_3;
	
	private double height;
	
	private LinkedList<Line> line_array;
	private LinkedList<TreeItem> src_expand_array;
	private LinkedList<TreeItem> target_expand_array;
	private LinkedList<TreeItem> src_all_array;
	private LinkedList<TreeItem> target_all_array;
	
	
	 
	
	
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
				Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen2.fxml")));
				//todo
//				listviewSource = (ListView<String>) newScene.lookup("#listView_src");
//				listviewTarget = (ListView<String>) newScene.lookup("#listView_target");
				confirmation = (Text) newScene.lookup("#confirm");
				myMenuBar = (MenuBar) newScene.lookup("#menu");
				fileMenu = myMenuBar.getMenus().get(0);
				saveFile = fileMenu.getItems().get(0);
				nextStep = fileMenu.getItems().get(1);
				closeFile = fileMenu.getItems().get(2);
//				
//				saveFile = new MenuItem("Save");
//				nextStep = new MenuItem("Next");
//				fileMenu.getItems().add(0, saveFile);
//				fileMenu.getItems().add(1, nextStep);
				thePane = (Pane) newScene.lookup("#thePane");
				myPane = (Pane) newScene.lookup("#myPane");
				myScrollPane = (ScrollPane) newScene.lookup("#myScrollPane");

				myHBox = (HBox) newScene.lookup("#myHBox");
//				myProgressBar = (ProgressBar) newScene.lookup("#myProgressBar");
				addPane = (Pane) newScene.lookup("#add_pane");
				progressBar = (ProgressBar) newScene.lookup("#progress_bar");

				
				thePane = (Pane)myScrollPane.getParent();
				myPane = (Pane) myScrollPane.getContent();

				srcTreeView = (TreeView) myPane.getChildren().get(2);
				targetTreeView = (TreeView) myPane.getChildren().get(3);
				
				line_array = new LinkedList<Line>();
				
				src_expand_array = new LinkedList<TreeItem>();
				target_expand_array = new LinkedList<TreeItem>();
				src_all_array = new LinkedList<TreeItem>();
				target_all_array = new LinkedList<TreeItem>();
				
				if (thePane == null) System.out.println("the pane is null");
				if (myPane == null) System.out.println("the pane is null1");
				if (myScrollPane == null) System.out.println("the pane is null2");
				myScrollPane.prefWidthProperty().bind(thePane.widthProperty());
				myScrollPane.prefHeightProperty().bind(thePane.heightProperty());
				myPane.prefWidthProperty().bind(thePane.widthProperty());
				myPane.prefHeightProperty().bind(thePane.heightProperty());
				myMenuBar.prefWidthProperty().bind(thePane.widthProperty());
				myMenuBar.prefHeightProperty().bind(thePane.heightProperty());
				myHBox.prefWidthProperty().bind(thePane.widthProperty());
				myHBox.prefHeightProperty().bind(thePane.heightProperty());
//				srcTreeView.prefWidthProperty().bind(thePane.widthProperty());
//				srcTreeView.prefHeightProperty().bind(thePane.heightProperty());
//				targetTreeView.prefWidthProperty().bind(thePane.widthProperty());
//				targetTreeView.prefHeightProperty().bind(thePane.heightProperty());
				progressBar.prefWidthProperty().bind(thePane.widthProperty());
				progressBar.prefHeightProperty().bind(thePane.heightProperty());
//				addPane.prefWidthProperty().bind(thePane.widthProperty());
//				addPane.prefHeightProperty().bind(thePane.heightProperty());
				
				saveFile.setOnAction(new EventHandler<ActionEvent>(){

					@Override
					public void handle(ActionEvent arg0) {
						doSave(chooseFile(true, FILTER_GZ));
					}
					
				});
				
				primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					
					@Override
					public void handle(WindowEvent event) {
						

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
								}
							});
							
							
						
						} catch (IOException e) {
							// TODO Auto-generated catch block
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
								}
							});
							
							
						
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				});
				
				
				
//				double height_of_scrollPane = myScrollPane.getHeight();
//				myPane.setPrefHeight(height_of_scrollPane);
			
				
				//TODO: newly addin
				//srcTreeView = (TreeView) newScene.lookup("#treeView_1");
				//targetTreeView = (TreeView) newScene.lookup("#treeView_2");
				
//				System.out.println("1");
//				loadListViewSecondScreen();
//				System.out.println("2");
				
				
				
				primaryStage.setScene(newScene);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
//	public void exitApplication(ActionEvent event) {
//		System.out.println("$$$$$$$$$$");
//		Platform.exit();
//	}
//	
//	@Override
//	public void stop() {
//		
//	}
	
	
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
//				listviewSource.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//			        @Override
//			        public void handle(MouseEvent event) {
//			        	
//			        	if (listviewSource.getSelectionModel().getSelectedItem().startsWith("	")) {
//			        		List<Field> fields = currentSourceTable.getFields();
//			        		for (Field field : fields) {
//			        			if (("	" + field.getName()).equals(listviewSource.getSelectionModel().getSelectedItem())) {
//			        				currentSourceField = field;
//			        			}
//			        		}
//			        		return;
//			        	}
//			        	
//			            loadListViewWithDetail(listviewSource.getSelectionModel().getSelectedItem(), "source");
//			            
//			        }
//			        
//			    });
//				listviewTarget.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//			        @Override
//			        public void handle(MouseEvent event) {
//			        	
//			        	if (listviewTarget.getSelectionModel().getSelectedItem().startsWith("	")) {
//			        		List<Field> fields = currentTargetTable.getFields();
//			        		for (Field field : fields) {
//			        			if (("	" + field.getName()).equals(listviewTarget.getSelectionModel().getSelectedItem())) {
//			        				currentTargetField = field;
//			        				if (currentSourceField != null) {
//			        					if (ObjectExchange.etl.getTableToTableMapping().getSourceToTargetMap(currentSourceTable, currentTargetTable) == null) {
//			        						ObjectExchange.etl.getTableToTableMapping().addSourceToTargetMap(currentSourceTable, currentTargetTable);
//			        					}
//			        					ObjectExchange.etl.getFieldToFieldMapping(currentSourceTable, currentTargetTable).addSourceToTargetMap(currentSourceField, currentTargetField);
//			        					System.out.println("Clicked");
//			        					currentTargetField.setDisplayName(currentTargetField.getName() + " <" + currentSourceTable.getName() + "." + currentSourceField.getName() + ">");
//			        					currentSourceField = null;
//			        					loadListViewWithDetail(currentTargetTable.getName(), "target");
//			        				}
//			        				break;
//			        			}
//			        		}
//			        		return;
//			        	}
//			        	
//			            System.out.println("clicked on " + listviewTarget.getSelectionModel().getSelectedItem());
//			            loadListViewWithDetail(listviewTarget.getSelectionModel().getSelectedItem(), "target");
//			            listviewSource.getSelectionModel();
//			        }
//			        
//			    });
				
				
				// srcTreeView part (if put into onMouseClick, then only expand when click)
				loadTreeView();
				myPane.setMinHeight(height);
				//myPane.setPrefHeight(height);
				targetTreeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						
						
						
						
						Node node = event.getPickResult().getIntersectedNode();
						String name = "";
					    // Accept clicks only on node cells, and not on empty spaces of the TreeView
					    if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
//					        name = (String) ((TreeItem)targetTreeView.getSelectionModel().getSelectedItem()).getValue();
					        name = ((Text) ((TreeItem)targetTreeView.getSelectionModel().getSelectedItem()).getValue()).getText();
					        TreeItem curr = (TreeItem)targetTreeView.getSelectionModel().getSelectedItem();
					        TreeItem parent = ((TreeItem)targetTreeView.getSelectionModel().getSelectedItem()).getParent();
					        if (((TreeItem)targetTreeView.getSelectionModel().getSelectedItem()).getParent() != root) {
					        	System.out.println("Node click: " + name);
					        	
					        	if (event.getClickCount() == 2) {
									//if in the linked list
					        		if (target_expand_array.indexOf(curr) == -1) {
					        			target_expand_array.add(curr);
					        		}
					        		else {
					        			target_expand_array.remove(curr);
					        		}
					        		reLoadTreeExpand();
								}
					        	//find the currentSourceTable
					        	int flag = 0;
					        	List<Table> targetTable = ObjectExchange.etl.getTargetDatabase().getTables();
					        	for (Table t : targetTable) {
					        		if (flag == 1) {
					        			break;
					        		}
					        		for (int i = 0; i < t.getFields().size(); i++) {
					        			if (name.equals(t.getFields().get(i).getDisplayName()) && ((Text)parent.getValue()).getText().equals(t.getName())) {
					        				
					        				currentTargetTable = t;
					        				currentTargetField = t.getFields().get(i);
					        				if (trackCurrent == 0) {
					        					trackCurrentTargetField = currentTargetTable;
					        					trackCurrent = 1;
					        				}
					        				if (currentSourceField != null) {
					        					if (ObjectExchange.etl.getTableToTableMapping().getSourceToTargetMap(currentSourceTable, currentTargetTable) == null) {
					        						ObjectExchange.etl.getTableToTableMapping().addSourceToTargetMap(currentSourceTable, currentTargetTable);
					        					}
					        					ObjectExchange.etl.getFieldToFieldMapping(currentSourceTable, currentTargetTable).addSourceToTargetMap(currentSourceField, currentTargetField);
					        					System.out.println("Clicked");
					        					currentTargetField.setDisplayName(currentTargetField.getName() + " <" + currentSourceTable.getName() + "." + currentSourceField.getName() + ">");
					        					currentSourceField = null;
					        					
					        					//Testing
									        	System.out.println("currentTargetTable name is " + currentTargetTable.getName());
						        				
									        	ObservableList<TreeItem> firstLevel= targetTreeView.getRoot().getChildren();
						        				for (TreeItem treeItem : firstLevel) {
						        					Text myText = (Text) treeItem.getValue();
						        					System.out.println("myText is " + myText.getText());
						        					if (currentTargetTable.getName().equals(myText.getText())) {
						        						ObservableList<TreeItem> secondLevel= treeItem.getChildren();
						        						for (TreeItem subTreeItem : secondLevel) {
						        							Text subText = (Text) subTreeItem.getValue();
						        							System.out.println("subTreeItem is " + subText.getText());
						        							System.out.println("currentTargetField is " + currentTargetField.getName());
						        							String theText = currentTargetField.getName();
						        							int idx = theText.indexOf("<");
						        							if (idx != -1) {
						        								theText = theText.substring(0, idx-1);
						        							}
						        							
						        							String theText2 = subText.getText();
						        							int idx2 = theText2.indexOf("<");
						        							if (idx2 != -1) {
						        								theText2 = theText2.substring(0, idx2-1);
						        							}
						        							System.out.println("theText is " + theText + "with length " + theText.length());
						        							System.out.println("theText2 is " + theText2 + "with length " + theText2.length());
						        							if (theText.equals(theText2)) {
						        								targetX = subText.localToScene(subText.getBoundsInLocal()).getMinX() - 10;
						        								targetY = subText.localToScene(subText.getBoundsInLocal()).getMaxY() + 12;
						        								System.out.print("Reach this line");
						        								break;
						        							}
						        						}
						        					}
						        				}
						        				
						        				//Testing ends
					        					
					        					
					        					reLoadTreeView(currentTargetField);
					        					if (trackCurrent == 2) {
					        						targetY -= trackCurrentTargetField.getFields().size() * 25;
					        						System.out.println("I want to see " + trackCurrentTargetField.getFields().size());
					        						trackCurrentTargetField = currentTargetTable;
					        						Line line = new Line();
					        						line.setStartX(srcX);
					        						line.setStartY(srcY);
					        						line.setEndX(targetX);
					        						line.setEndY(targetY);
					        						line_array.add(line);
					        						myPane.getChildren().add(line);
					        					}
					        					//loadListViewWithDetail(currentTargetTable.getName(), "target");
					        				}
					        				flag = 1;
					        				break;
					        			}
					        		}
					        	}
					        	
					        	
					        	
					        	
					        	
					        }
					    }
					}

					
				});
				
				
				srcTreeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						Node node = event.getPickResult().getIntersectedNode();
						System.err.println("Event Type is: " + ((TreeItem)((TreeItem)srcTreeView.getSelectionModel().getSelectedItem())));
						
						String name = "";
					    // Accept clicks only on node cells, and not on empty spaces of the TreeView
					    if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
//					        name = (String) ((TreeItem)srcTreeView.getSelectionModel().getSelectedItem()).getValue();
					    	
//
//					    	System.out.println("Selected TreeItem: " + targetTreeView.getSelectionModel().getSelectedItem());
//					    	System.out.println("Selected TreeItem: " + targetTreeView.getSelectionModel().getSelectedIndex());
					    	name = ((Text) ((TreeItem)srcTreeView.getSelectionModel().getSelectedItem()).getValue()).getText();
					    	TreeItem curr = (TreeItem)srcTreeView.getSelectionModel().getSelectedItem();
					    	TreeItem parent = ((TreeItem)srcTreeView.getSelectionModel().getSelectedItem()).getParent();
					    	
					    	
					    	System.out.println("name of the field is " + name);
					    	
					        //newly added for arrows
					    	if (event.getClickCount() == 2) {
								//if in the linked list
				        		if (src_expand_array.indexOf(curr) == -1) {
				        			src_expand_array.add(curr);
				        		}
				        		else {
				        			src_expand_array.remove(curr);
				        		}
				        		reLoadTreeExpand();
							}
					        
					        if (((TreeItem)srcTreeView.getSelectionModel().getSelectedItem()).getParent() != root) {
					        	System.out.println("Node click: " + name);
					        	 
					        	//find the currentSourceTable
					        	int flag = 0;
					        	List<Table> sourceTable = ObjectExchange.etl.getSourceDatabase().getTables();
					        	for (Table t : sourceTable) {
					        		System.out.println("parent is " + ((Text)parent.getValue()).getText());
					        		if (flag == 1) {
					        			break;
					        		}
					        		for (int i = 0; i < t.getFields().size(); i++) {
					        			if (name.equals(t.getFields().get(i).getDisplayName()) && ((Text)parent.getValue()).getText().equals(t.getName())) {
					        				
					        				
					        				currentSourceTable = t;
					        				currentSourceField = t.getFields().get(i);
					        				flag = 1;
					        				break;
					        			}
					        		}
					        	}
					        	
		        				//Testing
		        				ObservableList<TreeItem> firstLevel= srcTreeView.getRoot().getChildren();
		        				for (TreeItem treeItem : firstLevel) {
		        					Text myText = (Text) treeItem.getValue();
		        					System.out.println("my src test is " + myText.getText());
		        					System.out.println("current source table is " + currentSourceTable);
		        					if (currentSourceTable.getName().equals(myText.getText())) {
		        						ObservableList<TreeItem> secondLevel= treeItem.getChildren();
		        						for (TreeItem subTreeItem : secondLevel) {
		        							Text subText = (Text) subTreeItem.getValue();
		        							System.out.println("my src subtest is " + subText.getText() + "with length " + subText.getText().length());
		        							System.out.println("current source field is " + currentSourceField);
		        							
		        							String theText = currentSourceField.getName();
		        							int idx = theText.indexOf(".");
		        							if (idx != -1) {
		        								theText = theText.substring(idx, theText.length()-1);
		        							}
		        							System.out.println("theText is " + theText + "with length " + theText.length());
		        						
		        							
		        							
		        							if (theText.equals(subText.getText())) {
		        								srcX = subText.localToScene(subText.getBoundsInLocal()).getMaxX() + 10;
		        								srcY = subText.localToScene(subText.getBoundsInLocal()).getMaxY() + 112;
		        								System.out.println("Reach the src line and srcX and srcY are " + srcX + "\t" + srcY);
		        								break;
		        							}
		        						}
		        					}
		        				}
		        				
		        				//Testing ends
					        }
					    }
					    
					    
					}
				});
				
				
				
				//confirmation.setOnMouseClicked(new EventHandler<MouseEvent>() {
				  nextStep.setOnAction(new EventHandler<ActionEvent>() {
					  
					@Override
					//public void handle(MouseEvent event) {
					  public void handle(ActionEvent event) {	
						try {
							newWindow = new Stage();
							Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen3.fxml")));
							conceptTable = (TableView) newScene.lookup("#conceptTable");
							okayButton = (Button) newScene.lookup("#okay_button");
							mainPane_3 = (Pane) newScene.lookup("#thePane");
							
//							conceptTable.prefHeightProperty().bind(mainPane_3.heightProperty());
//							conceptTable.prefWidthProperty().bind(mainPane_3.widthProperty());
							
							ObservableList<myConceptTable> data = FXCollections.observableArrayList();
							
							
							Map<Field, Field> myMap = extractAllFIeldsRequiringConceptID();
							ObjectExchange.conceptIDFieldMap = myMap;
							System.out.println("size: " + myMap.size());
							for (Map.Entry<Field, Field> entry : myMap.entrySet())
							{
								System.out.println("Entering the correct function");
								data.add(new myConceptTable(entry.getKey().toString(), entry.getValue().toString()));
							    System.out.println(entry.getKey() + "/" + entry.getValue());
							}
								
							
							newWindow.setTitle("Confirmation");
							newWindow.setScene(newScene);
							
							
							rightColumn = (TableColumn<myConceptTable, String>) conceptTable.getColumns().get(0);
							rightColumn.setCellValueFactory(new PropertyValueFactory<myConceptTable, String>("srcName"));
						
							
							leftColumn = (TableColumn<myConceptTable, String>) conceptTable.getColumns().get(1);
							leftColumn.setCellValueFactory(new PropertyValueFactory<myConceptTable, String>("mapName"));
//							System.out.println(data.size());
							if (data.size() == 0) {
								data.add(new myConceptTable(" ", " "));
							}
							
							
							conceptTable.setItems(data);
							
							newWindow.show();
							progressBar.setProgress(0.67);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						okayButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						
							MenuItem menuItem1;
							MenuItem menuItem2;
							MenuItem menuItem3;
							MenuItem menuItem4;
							MenuItem menuItem5;
							MenuItem menuItem6;
							public void handle(MouseEvent arg0) {
								
								//TODO:
								
								
								
								try {
									Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen4_try_2.fxml")));
									passwordField = (TextField) newScene.lookup("#password_field");
									userNameField = (TextField) newScene.lookup("#username_field");
									dbNameField = (TextField) newScene.lookup("#dbname_field");
									ipField = (TextField) newScene.lookup("#ip_field");
									inputButton = (Button) newScene.lookup("#ok_button");
									typeButton = (ComboBox) newScene.lookup("#combo_type");
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
									// TODO Auto-generated catch block
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
										System.out.println("here is the answer: " +  password +  "  "+ username +  "  "+ dbName+ "   "+ ipName);
									
										
										
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
											int percent = 100;
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
											
											nextButton.setOnAction(new EventHandler<ActionEvent>() {
												/* (non-Javadoc)
												 * @see javafx.event.EventHandler#handle(javafx.event.Event)
												 */
												@SuppressWarnings("unchecked")
												public void handle (ActionEvent e) {
													TableView manualEnterTable = null;
													try {
														Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen6_try.fxml")));
														//TODO: add in table
														manualEnterTable = (TableView) newScene.lookup("#conceptTable");
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
															ObservableList<String> itemList = FXCollections.observableArrayList();
															
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
																		//TODO Handle File Not Found
																	}
																	
																}
																
															});
															
														}
														
													});
													
												}
												
											});
											
											System.out.println("done");
											nextButton.fire();
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
	
	private void reLoadTreeExpand() {
		
		//remove all lines
		int length = line_array.size();
		for (int i = 0; i < length; i++) {
			Line theLine = line_array.removeFirst();
			myPane.getChildren().remove(theLine);
		}
		
		//prepare to add new lines
		List<Table> targetTable = ObjectExchange.etl.getTargetDatabase().getTables();
		System.out.println(target_expand_array.size());
		for (Table t : targetTable) {
			
			//check if the current target is expanded
			int curr_target_expand = 0;
			int curr_src_expand = 0;
			for (int i = 0; i < target_expand_array.size(); i++) {
				System.out.println("t.getName is " + t.getName());
				System.out.println("target expand array is " + ((Text)((TreeItem)target_expand_array.get(i)).getValue()).getText());
				//if the current target is expanded
				if (((Text)((TreeItem)target_expand_array.get(i)).getValue()).getText().equals(t.getName())) {
					curr_target_expand = 1;
					break;
				}
			}
			
			System.out.println("curr_target_expand" + curr_target_expand);
			//check if has connection with src
			int get_src_table_name = 0;
			String src_table_name = "";
			for (int i = 0; i < t.getFields().size(); i++) {
				System.out.println("target table name is " + t.getFields().get(i).getDisplayName());
				if (t.getFields().get(i).getDisplayName().contains("<")) {
					get_src_table_name = 1;
					src_table_name = t.getFields().get(i).getDisplayName();
					src_table_name = src_table_name.substring(src_table_name.indexOf("<") + 1);
					src_table_name = src_table_name.substring(0, src_table_name.indexOf("."));
				}
			}
			
			System.out.println("get_src_table_name is " + get_src_table_name);
			//no connection
			if (get_src_table_name == 0) {
				break;
			} 
			
			
			//if the current target is not expanded
			TreeItem target_treeItem = null;
			TreeItem src_treeItem = null;
			if (curr_target_expand == 0) {
				
				//find the TreeItem for target
				for (int j = 0; j < target_all_array.size(); j++) {
					if (((Text)((TreeItem)target_all_array.get(j)).getValue()).getText().equals(t.getName())) {
						target_treeItem = target_all_array.get(j);
					}
				}
				for (int j = 0; j < src_all_array.size(); j++) {
					if (((Text)((TreeItem)src_all_array.get(j)).getValue()).getText().equals(src_table_name)) {
						src_treeItem = src_all_array.get(j);
					}
				}
				//draw line between table names
				Line line = new Line();
				line.setStartX(((Text)src_treeItem.getValue()).localToScene(((Text)src_treeItem.getValue()).getBoundsInLocal()).getMaxX() + 10);
				line.setStartY(((Text)src_treeItem.getValue()).localToScene(((Text)src_treeItem.getValue()).getBoundsInLocal()).getMaxY() + 112);
				line.setEndX(((Text)target_treeItem.getValue()).localToScene(((Text)target_treeItem.getValue()).getBoundsInLocal()).getMinX() - 10);
				line.setEndY(((Text)target_treeItem.getValue()).localToScene(((Text)target_treeItem.getValue()).getBoundsInLocal()).getMaxY() + 12);
				line_array.add(line);
				myPane.getChildren().add(line);
				
				
			} else {
				
				//if the current target is expanded
				
				
				//check if the current src is expanded
				int src_expand = 0;
				for (int i = 0; i < src_expand_array.size(); i++) {
					if (((Text)((TreeItem)src_expand_array.get(i)).getValue()).getText().equals(src_table_name)) {
						src_expand = 1;
						break;
					}
					
				}
				
				System.out.print("Checking if src is expanded " + src_expand);
				//if not expand
				if (src_expand == 0) {
					for (int j = 0; j < target_all_array.size(); j++) {
						if (((Text)((TreeItem)target_all_array.get(j)).getValue()).getText().equals(t.getName())) {
							target_treeItem = target_all_array.get(j);
					
						}
					}
					for (int j = 0; j < src_all_array.size(); j++) {
						if (((Text)((TreeItem)src_all_array.get(j)).getValue()).getText().equals(src_table_name)) {
							src_treeItem = src_all_array.get(j);
						}
					}
					//draw line between table names
					Line line = new Line();
					line.setStartX(((Text)src_treeItem.getValue()).localToScene(0, 0).getX());
					line.setStartY(((Text)src_treeItem.getValue()).localToScene(0, 0).getY() - 170);
					line.setEndX(((Text)target_treeItem.getValue()).localToScene(0, 0).getX());
					line.setEndY(((Text)target_treeItem.getValue()).localToScene(0, 0).getY() - 70);
					line_array.add(line);
					myPane.getChildren().add(line);
				} else {
					//if the src table is expanded
					
					for (int j = 0; j < target_all_array.size(); j++) {
						if (((Text)((TreeItem)target_all_array.get(j)).getValue()).getText().equals(t.getName())) {
							target_treeItem = target_all_array.get(j);
					
						}
					}
					for (int j = 0; j < src_all_array.size(); j++) {
						if (((Text)((TreeItem)src_all_array.get(j)).getValue()).getText().equals(src_table_name)) {
							src_treeItem = src_all_array.get(j);
						}
					}
					
					String src_item_name = "";
					for (int k = 0; k < target_treeItem.getChildren().size(); k++) {
						System.out.println("target_item name is " + ((Text)((TreeItem)target_treeItem.getChildren().get(k)).getValue()).getText());
						System.out.println("target table name is " + t.getFields().get(k).getDisplayName());
						if (t.getFields().get(k).getDisplayName().contains("<")) {
						//if (((Text)((TreeItem)target_treeItem.getChildren().get(k)).getValue()).getText().contains("<")) {
							src_item_name = t.getFields().get(k).getDisplayName();
							//src_item_name = ((Text)((TreeItem)target_treeItem.getChildren().get(k)).getValue()).getText();
							src_item_name = src_item_name.substring(src_item_name.indexOf(".") + 1);
							System.out.println("src_item_name is " + src_item_name);
							src_item_name = src_item_name.substring(0, src_item_name.indexOf(">"));
							System.out.println("src_item_name now is " + src_item_name);
							System.out.println("src_item_name is " + src_item_name);
							for (int l = 0; l < src_treeItem.getChildren().size(); l++) {
								System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								System.out.println("src_treeItem is " + ((Text)((TreeItem)src_treeItem.getChildren().get(l)).getValue()).getText());
								if (((Text)((TreeItem)src_treeItem.getChildren().get(l)).getValue()).getText().equals(src_item_name)) {
									System.out.println("Reach this line");
									Line line = new Line();
									Text text1 = ((Text)((TreeItem)src_treeItem.getChildren().get(l)).getValue());
									Text text2 = ((Text)((TreeItem)target_treeItem.getChildren().get(k)).getValue());
									
									System.out.println("text1 is " + text1.getText());
									System.out.println("text2 is " + text2.getText());
									
									
									line.setStartX(text1.localToScene(0, 0).getX());
									line.setStartY(text1.localToScene(0, 0).getY());
									line.setEndX(text2.localToScene(0, 0).getX());
									line.setEndY(text2.localToScene(0, 0).getY());
									System.out.println("start x is " + text1.localToScene(0, 0).getX() + "start y is " + (text1.localToScene(0, 0).getY() - 170) + "end x is " + text2.localToScene(0, 0).getX() + "end y is " + (text2.localToScene(0, 0).getY() - 70));
									line_array.add(line);
									myPane.getChildren().add(line);
								}
							}
						}
					}
					
					
				}
			}
			
			
			
			
		}
	}
	

	private void reLoadTreeView(Field currentSourceField) {
		List<Table> targetTable = ObjectExchange.etl.getTargetDatabase().getTables();
		Text target = new Text("Target");
		root_2 = new TreeItem<>(target);
		root_2.setExpanded(true);
		int flag = 0;
		for (Table t : targetTable) {
			//newly added
			Text parent = new Text(t.getName());
			TreeItem<Text> item = new TreeItem<>(parent);
			//newly added end
			
			
//			TreeItem<String> item = new TreeItem<>(t.getName());
			root_2.getChildren().add(item);
			for (int i = 0; i < t.getFields().size(); i++) {
				if (flag == 0) {
					if (t.getFields().get(i).equals(currentSourceField)) {
						item.setExpanded(true);
						flag = 1;
					}
					else {
						item.setExpanded(false);
					}
				}
				//newly added
				Text myText = new Text(t.getFields().get(i).getDisplayName());
				TreeItem<Text> sub = new TreeItem<>(myText);
				item.getChildren().add(sub);
				//newly added end
//				
//				TreeItem<String> sub = new TreeItem<>(t.getFields().get(i).getDisplayName());
//				item.getChildren().add(sub);
			}
			
		}
		targetTreeView.setRoot(root_2);
		
		srcY -= 170;
		targetY -= 70;
		System.out.println("srcX is " + srcX + "  srcY is " + srcY + " targetX is " + targetX + "  targetY is " + targetY);
		System.out.println("trackCurrentTargetField is " + trackCurrentTargetField + " currentTargetTable is " + currentTargetTable);
		
		if (trackCurrentTargetField == currentTargetTable) {
			trackCurrent = 1;
			//adding line demo
			Line line = new Line();
			line.setStartX(srcX);
			line.setStartY(srcY);
			line.setEndX(targetX);
			line.setEndY(targetY);
			line_array.add(line);
			myPane.getChildren().add(line);
		}
		else {
			
			trackCurrent = 2;
			int length = line_array.size();
			for (int i = 0; i < length; i++) {
				Line theLine = line_array.removeFirst();
				myPane.getChildren().remove(theLine);
			}
			
		}
		
		
//		if (myPane != null) myPane.getChildren().add(line);
//		else System.out.println("This is null");
	}
	
	private void loadTreeView() {
		//source
		
		int count_parent = 0;
		int count_child = 0;
		int toUse = 0;
		
		List<Table> targetTable = ObjectExchange.etl.getTargetDatabase().getTables();
		Text target = new Text("Target");
		root_2 = new TreeItem<>(target);
		root_2.setExpanded(true);
		for (Table t : targetTable) {
			//newly added
			Text parent = new Text(t.getName());
			TreeItem<Text> item = new TreeItem<>(parent);
			target_all_array.add(item);
			count_parent++;
			//newly added end
			
//			TreeItem<String> item = new TreeItem<>(t.getName());
			root_2.getChildren().add(item);
			item.setExpanded(false);
			for (int i = 0; i < t.getFields().size(); i++) {
				//newly added
				toUse++;
				Text myText = new Text(t.getFields().get(i).getDisplayName());
				TreeItem<Text> sub = new TreeItem<>(myText);
				item.getChildren().add(sub);
				//newly added end
				
				
//				TreeItem<String> sub = new TreeItem<>(t.getFields().get(i).getDisplayName());
//				item.getChildren().add(sub);
			}
			if (toUse > count_child) count_child = toUse;
			
		}
		height = count_parent + count_child;
		height = height * 5;
		System.out.println("height is " + height);
		
		System.out.println("count_parent and count_child are " + count_parent + " " + count_child);
//		targetTreeView.setPrefHeight(height);
		targetTreeView.setMinHeight(height);
		
		targetTreeView.setRoot(root_2);
		
		List<Table> sourceTable = ObjectExchange.etl.getSourceDatabase().getTables();
		Text source = new Text("Source");
		root = new TreeItem<>(source);
		root.setExpanded(true);
		for (Table t : sourceTable) {
			//newly added
			Text parent = new Text(t.getName());
			TreeItem<Text> item = new TreeItem<>(parent);
			src_all_array.add(item);
			//newly added end
			
//			TreeItem<String> item = new TreeItem<>(t.getName());
			root.getChildren().add(item);
			item.setExpanded(false);
			for (int i = 0; i < t.getFields().size(); i++) {
				//newly added
				Text myText = new Text(t.getFields().get(i).getDisplayName());
				TreeItem<Text> sub = new TreeItem<>(myText);
				item.getChildren().add(sub);
				
				//newly added end
				
//				TreeItem<String> sub = new TreeItem<>(t.getFields().get(i).getDisplayName());
//				item.getChildren().add(sub);
				
			}
			
		}
		
//		srcTreeView.setPrefHeight(height);
		srcTreeView.setMinHeight(height);
		
		srcTreeView.setRoot(root);
		
		
		
		//adding line demo
//		Line line = new Line();
//		line.setStartX(0);
//		line.setStartY(0);
//		line.setEndX(100);
//		line.setEndY(100);
//		if (myPane != null) myPane.getChildren().add(line);
//		else System.out.println("This is null");
		

		
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
	
//	private void loadListViewSecondScreen() {
//		//listview.setEditable(true);
//		ObservableList<String> observableListSource = FXCollections.observableArrayList();
//		ObservableList<String> observableListTarget = FXCollections.observableArrayList();
//		
//		List<Table> sourceTable = ObjectExchange.etl.getSourceDatabase().getTables();
//		for (Table t : sourceTable) {
//			observableListSource.add(t.getName());
//		}
//		
//		List<Table> targetTable = ObjectExchange.etl.getTargetDatabase().getTables();
//		for (Table t : targetTable) {
//			observableListTarget.add(t.getName());
//		}
//		listviewSource.setItems(observableListSource);
//		listviewTarget.setItems(observableListTarget);
//	}
//	
//	private void loadListViewWithDetail(String click, String version) {
//		if (version == "source") {
//			
//			currentSourceTable = ObjectExchange.etl.getSourceDatabase().getTableByName(click);
//			
//			ObservableList<String> observableListSource = FXCollections.observableArrayList();
//		
//			List<Table> sourceTableDetail = ObjectExchange.etl.getSourceDatabase().getTables();
//			for (Table t: sourceTableDetail) {
//				observableListSource.add(t.getName());
//				if (t.getName().equals(click)) {
//					int i = 0;
//					for (i = 0; i < t.getFields().size(); i++) {
//						observableListSource.add("	" + t.getFields().get(i).getDisplayName());
//					}
//			
//				}
//			
//			}
//			listviewSource.setItems(observableListSource);
//		}
//		else {
//			ObservableList<String> observableListTarget = FXCollections.observableArrayList();
//			
//			currentTargetTable = ObjectExchange.etl.getTargetDatabase().getTableByName(click);
//		
//			List<Table> targetTableDetail = ObjectExchange.etl.getTargetDatabase().getTables();
//			for (Table t: targetTableDetail) {
//				observableListTarget.add(t.getName());
//				if (t.getName().equals(click)) {
//					System.out.println("Here");
//					int i = 0;
//					for (i = 0; i < t.getFields().size(); i++) {
//						observableListTarget.add("	" + t.getFields().get(i).getDisplayName());
//						
//					}
//			
//				}
//				
//			
//			}
//			listviewTarget.setItems(observableListTarget);
//		}
//	}
	
	
	
	

	
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
	
	private List<org.ohdsi.utilities.collections.Pair<String, String>> doScanTable (DbType type, String address, String domain, String username, String password, String database, Table table, Field field, Table targetTable) throws SQLException {
		LinkedList<org.ohdsi.utilities.collections.Pair<String, String>> result = new LinkedList<>();
		Connection con = DBConnector.connect(address, domain, username, password, type, database);
		System.out.println("source table: " + field.getTable() + "\tsource field: " + field + "\ttarget table: " + targetTable + "\tcon: " + con);
		System.out.println(ETLSQLGenerator.getUniqueSourceField(table, targetTable));
		ResultSet set = con.createStatement().executeQuery("SELECT " + ETLSQLGenerator.getUniqueSourceField(table, targetTable).getName() + "," + field.getName() + " FROM " + table.getName() + ";");
		
		//progress bar
		int count = 0;
		int toUse = 0;
		while (set.next()) {
			count++;
		}
		
		while (set.next()) {
			result.add(new org.ohdsi.utilities.collections.Pair<>(set.getString(1), set.getString(2)));
			
			//progress bar
			progressBar.setProgress(toUse/count);
			toUse++;
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
	
	private void doSave(String filename) {
		if (filename != null) {
			ETL.FileFormat fileFormat = filename.endsWith("json.gz") ? ETL.FileFormat.GzipJson : filename.endsWith("json") ? ETL.FileFormat.Json
					: ETL.FileFormat.Binary;
			ObjectExchange.etl.save(filename, fileFormat);
		}
	}

}
