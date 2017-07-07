import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ohdsi.rabbitInAHat.dataModel.Field;
import org.ohdsi.rabbitInAHat.dataModel.ItemToItemMap;
import org.ohdsi.rabbitInAHat.dataModel.Table;
import org.ohdsi.whiteRabbit.ObjectExchange;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class SourceTableRectangle extends StackPane implements Expandable {
	
	public static final Color TABLE_STROKE_COLOR = Color.web("rgb(252, 146, 40)");
	public static final Color TABLE_FILL_COLOR = Color.web("rgb(247, 183, 119)");
	public static final Color FIELD_STROKE_COLOR = Color.web("rgb(253,172,90)");
	public static final Color FIELD_FILL_COLOR = Color.web("rgb(250,209,167)");
	public static final Color FIELD_STROKE_HIGHLIGHT_COLOR = Color.web("rgb(252,133,15)");
	public static final Color FIELD_FILL_HIGHLIGHT_COLOR = Color.web("rgb(246,170,95)");
	public static final double FIELD_INTERVAL = 5;
	public static final double FIELD_HEIGHT = 20;
	public static final double FIELD_WIDTH = 140;
	public static final double FIELD_X = 50;
	
	private double x=30, y, width=200, height=30;
	protected boolean isExpanded = false;
	Table table;
	public Rectangle rect;
	public Text text;
	public LinkedList<FieldRectangle> fieldRectList;
	
	public static Pane displayPane;
	public static LinkedList<SourceTableRectangle> sourceTableRects;
	public static FieldRectangle selectingField;
	public static SourceTableRectangle expandingTable;
	
 	protected class FieldRectangle extends StackPane {
		private double fx=FIELD_X, fy, fwidth=FIELD_WIDTH, fheight=FIELD_HEIGHT;
		Field field;
		public Rectangle frect;
		public Text ftext;
		
		private void setFieldMisc() {
			this.frect = new Rectangle(this.fx, this.fy, this.fwidth, this.fheight);
			this.frect.setFill(FIELD_FILL_COLOR);
			this.frect.setStroke(FIELD_STROKE_COLOR);
			this.frect.setStrokeWidth(1);
			this.frect.setArcWidth(3);
			this.frect.setArcHeight(3);
			
			this.ftext = new Text(fx, fy, field.getDisplayName());
			this.ftext.wrappingWidthProperty().bind(this.widthProperty());
			this.ftext.prefHeight(this.fheight);
			this.ftext.setTextAlignment(TextAlignment.CENTER);
			this.getChildren().addAll(this.frect, this.ftext);
		}
		
		public FieldRectangle(double fy, Field fd) {
			super();
			this.setLayoutX(fx);
			this.setLayoutY(fy);
			this.setWidth(200);
			this.field = fd;
			this.fy = fy;
			setFieldMisc();
			FieldRectangle self = this;
			this.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					if (selectingField != null) {
						selectingField.frect.setFill(FIELD_FILL_COLOR);
						selectingField.frect.setStroke(FIELD_STROKE_COLOR);
					}
					frect.setFill(FIELD_FILL_HIGHLIGHT_COLOR);
					frect.setStroke(FIELD_STROKE_HIGHLIGHT_COLOR);
					selectingField = self;
				}
				
			});
		}
	}
	
	
	private void setMisc() {
		rect = new Rectangle(this.x, this.y, this.width, this.height);
		rect.setFill(TABLE_FILL_COLOR);
		rect.setStroke(TABLE_STROKE_COLOR);
		rect.setStrokeWidth(1.5);
		rect.setArcWidth(5);
		rect.setArcHeight(5);
		
		text = new Text(x, y, table.getName());
		text.wrappingWidthProperty().bind(this.widthProperty());
		text.prefHeight(height);
		text.setTextAlignment(TextAlignment.CENTER);
		this.getChildren().addAll(rect, text);
	}
	
	public SourceTableRectangle(double y, Table tb) {
		super();
		this.setLayoutX(x);
		this.setLayoutY(y);
		this.setWidth(200);
		table = tb;
		this.y = y;
		setMisc();
		fieldRectList = new LinkedList<>();
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				toggle(sourceTableRects);
			}
			
		});
	}
	
	public void resetFieldSelection () {
		if (selectingField != null) {
			selectingField.frect.setFill(FIELD_FILL_COLOR);
			selectingField.frect.setStroke(FIELD_STROKE_COLOR);
			selectingField = null;
		}
	}
	
	public String toString() {
		return table.getName();
	}

	@Override
	public void expand(List<? extends Expandable> list) {
		int numFields = table.getFields().size();
		double deltaY = numFields * (FIELD_INTERVAL + FIELD_HEIGHT);
		displayPane.setPrefHeight(displayPane.getPrefHeight() + deltaY);
		Iterator<? extends Expandable> iter = list.iterator();
		while (iter.hasNext()) {
			SourceTableRectangle item = (SourceTableRectangle) iter.next();
			if (item.equals(this)) {
				break;
			}
			else {
				if (item.isExpanded) {
					item.collapse(list);
				}
			}
		}
		expand();
		while (iter.hasNext()) {
			SourceTableRectangle item = (SourceTableRectangle) iter.next();
			item.shift(deltaY);
			if (item.isExpanded) {
				item.collapse();
			}
		}
	}

	@Override
	public void collapse(List<? extends Expandable> list) {
		double deltaY = 0 - fieldRectList.size() * (FIELD_HEIGHT + FIELD_INTERVAL);
		displayPane.setPrefHeight(displayPane.getPrefHeight() + deltaY);
		expandingTable = null;
		collapse();
		Iterator<? extends Expandable> iter = list.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals(this)) {
				break;
			}
		}
		while (iter.hasNext()) {
			iter.next().shift(deltaY);
		}
	}

	@Override
	public void toggle(List<? extends Expandable> list) {
		if (isExpanded) {
			collapse(list);
		}
		else {
			expand(list);
		}
		
		Arrow.redraw();
	}

	@Override
	public void expand() {
		System.out.println("Expanding itself...");
		expandingTable = this;
		double currY = y + height;
		for (Field fd : table.getFields()) {
			System.out.println("Processing " + fd.getName());
			currY += FIELD_INTERVAL;
			FieldRectangle newNode = new FieldRectangle(currY, fd);
			fieldRectList.add(newNode);
			displayPane.getChildren().add(newNode);
			currY += FIELD_HEIGHT;
		}
		isExpanded = true;
	}

	@Override
	public void collapse() {
		for (FieldRectangle item : this.fieldRectList) {
			displayPane.getChildren().remove(item);
		}
		fieldRectList.clear();
		selectingField = null;
		isExpanded = false;
	}

	@Override
	public void shift(double y) {
		this.y += y;
		this.setLayoutY(this.getLayoutY() + y);
		rect.setY(rect.getY() + y);
		text.setY(text.getY() + y);
	}

}
