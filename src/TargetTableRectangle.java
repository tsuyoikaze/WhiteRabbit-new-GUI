import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ohdsi.rabbitInAHat.dataModel.Field;
import org.ohdsi.rabbitInAHat.dataModel.Table;
import org.ohdsi.whiteRabbit.ObjectExchange;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TargetTableRectangle extends StackPane implements Expandable {
	
	public static final Color TABLE_STROKE_COLOR = Color.web("rgb(164,164,248)");
	public static final Color TABLE_FILL_COLOR = Color.web("rgb(183,183,247)");
	public static final Color FIELD_STROKE_COLOR = Color.web("rgb(211,211,252)");
	public static final Color FIELD_FILL_COLOR = Color.web("rgb(229,229,252)");
	public static final Color FIELD_STROKE_HIGHLIGHT_COLOR = Color.web("rgb(140,140,246)");
	public static final Color FIELD_FILL_HIGHLIGHT_COLOR = Color.web("rgb(160,160,244)");
	public static final double FIELD_INTERVAL = 5;
	public static final double FIELD_HEIGHT = 20;
	public static final double FIELD_WIDTH = 140;
	public static final double FIELD_X = 350;
	
	private double x=370, y, width=200, height=30;
	protected boolean isExpanded = false;
	Table table;
	public Rectangle rect;
	public Text text;
	public LinkedList<FieldRectangle> fieldRectList;
	
	public static Pane displayPane;
	public static LinkedList<TargetTableRectangle> targetTableRects;
	public static FieldRectangle selectingField;
	public static TargetTableRectangle expandingTable;
	public static LinkedList<Arrow> arrowList = new LinkedList<>();
	
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
			
			this.ftext = new Text(fx, fy, field.getName());
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
					
					if (TargetTableRectangle.selectingField != null && SourceTableRectangle.selectingField != null) {
						if (ObjectExchange.etl.getTableToTableMapping().getSourceToTargetMap(SourceTableRectangle.selectingField.field.getTable(), TargetTableRectangle.selectingField.field.getTable()) == null) {
							ObjectExchange.etl.getTableToTableMapping().addSourceToTargetMap(SourceTableRectangle.selectingField.field.getTable(), TargetTableRectangle.selectingField.field.getTable());
						}
						ObjectExchange.etl.getFieldToFieldMapping(SourceTableRectangle.selectingField.field.getTable(), TargetTableRectangle.selectingField.field.getTable()).addSourceToTargetMap(SourceTableRectangle.selectingField.field, TargetTableRectangle.selectingField.field);
						TargetTableRectangle.selectingField.field.setDisplayName(TargetTableRectangle.selectingField.field.getName() + " <" + SourceTableRectangle.selectingField.field.getTable().getName() + "." + SourceTableRectangle.selectingField.field.getName() + ">");

						if (Arrow.displayPane == null) {
							Arrow.displayPane = displayPane;
						}
						arrowList.add(new Arrow(SourceTableRectangle.selectingField.getBoundsInParent().getMaxX(), (SourceTableRectangle.selectingField.getBoundsInParent().getMinY() + 0.5 * SourceTableRectangle.selectingField.getHeight()), TargetTableRectangle.selectingField.getBoundsInParent().getMinX(), (TargetTableRectangle.selectingField.getBoundsInParent().getMinY() + 0.5 * TargetTableRectangle.selectingField.getHeight()), SourceTableRectangle.selectingField.field, TargetTableRectangle.selectingField.field));
						SourceTableRectangle.expandingTable.resetFieldSelection();
						TargetTableRectangle.expandingTable.resetFieldSelection();
					}
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
	
	public TargetTableRectangle(double y, Table tb) {
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
				toggle(targetTableRects);
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
			TargetTableRectangle item = (TargetTableRectangle) iter.next();
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
			TargetTableRectangle item = (TargetTableRectangle) iter.next();
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
		expandingTable = this;
		double currY = y + height;
		for (Field fd : table.getFields()) {
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
