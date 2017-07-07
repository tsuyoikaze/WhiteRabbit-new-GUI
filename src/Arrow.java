import org.ohdsi.rabbitInAHat.dataModel.Field;
import org.ohdsi.rabbitInAHat.dataModel.ItemToItemMap;
import org.ohdsi.rabbitInAHat.dataModel.MappableItem;
import org.ohdsi.rabbitInAHat.dataModel.Table;
import org.ohdsi.whiteRabbit.ObjectExchange;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;

public class Arrow {
	
	private CubicCurve curve;
	private Line l1, l2;
	private MappableItem source, target;
	
	public static Arrow selectingArrow = null;
	
	public static Pane displayPane = null;
	
	public Arrow(double startX, double startY, double controlX1, double controlY1, double controlX2, double controlY2, double endX, double endY) {
		curve = new CubicCurve(startX, startY, controlX1, controlY1, controlX2, controlY2, endX, endY);
		curve.setFill(Color.TRANSPARENT);
		l1 = new Line(endX, endY, endX - 6, endY - 6);
		l2 = new Line(endX, endY, endX - 6, endY + 6);
		curve.setStrokeWidth(1);
		l1.setStrokeWidth(1);
		l2.setStrokeWidth(1);
		curve.setStroke(Color.web("#444444"));
		l1.setStroke(Color.web("#444444"));
		l2.setStroke(Color.web("#444444"));
		
		Arrow self = this;
		
		EventHandler<MouseEvent> clickListener = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				if (selectingArrow != null) {
					selectingArrow.curve.setStroke(Color.web("#444444"));
					selectingArrow.l1.setStroke(Color.web("#444444"));
					selectingArrow.l2.setStroke(Color.web("#444444"));
					curve.setStrokeWidth(1);
					l1.setStrokeWidth(1);
					l2.setStrokeWidth(1);
				}
				curve.requestFocus();
				curve.setStroke(Color.web("#222222"));
				l1.setStroke(Color.web("#222222"));
				l2.setStroke(Color.web("#222222"));
				curve.setStrokeWidth(1.8);
				l1.setStrokeWidth(1.8);
				l2.setStrokeWidth(1.8);
				selectingArrow = self;
			}
		};
		
		EventHandler<KeyEvent> removeListener = new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent e) {
				
				System.out.println("IN");
				
				if (e.getCode() == KeyCode.DELETE) {
					if (source instanceof Field && target instanceof Field) {
						ObjectExchange.etl.getFieldToFieldMapping(((Field) source).getTable(), ((Field) target).getTable()).removeSourceToTargetMap(source, target);
						erase();
					}
				}
			}
			
		};
		
		curve.setOnMouseClicked(clickListener);
		l1.setOnMouseClicked(clickListener);
		l2.setOnMouseClicked(clickListener);
		
		curve.setOnKeyPressed(removeListener);
		l1.setOnKeyPressed(removeListener);
		l2.setOnKeyPressed(removeListener);
	}
	
	public Arrow(double startX, double startY, double endX, double endY, MappableItem source, MappableItem target) {
		this(startX, startY, endX - 10, startY, startX + 10, endY, endX, endY);
		this.source = source;
		this.target = target;
		draw();
	}
	
	public void draw() {
		displayPane.getChildren().addAll(curve, l1, l2);
	}
	
	public void erase() {
		displayPane.getChildren().removeAll(curve, l1, l2);
	}
	
	public static void redraw() {
		
		System.out.println("Redrawing picture");
		
		for (Arrow arrow : TargetTableRectangle.arrowList) {
			arrow.erase();
		}
		TargetTableRectangle.arrowList.clear();
		
		if (SourceTableRectangle.expandingTable == null || TargetTableRectangle.expandingTable == null) {
			
			for (ItemToItemMap map : ObjectExchange.etl.getTableToTableMapping().getSourceToTargetMaps()) {
				Table sourceTable = (Table) map.getSourceItem();
				Table targetTable = (Table) map.getTargetItem();
				SourceTableRectangle sourceTableR = null;
				TargetTableRectangle targetTableR = null;
				for (SourceTableRectangle curr : Main.sourceTableRects) {
					if (curr.table == sourceTable) {
						sourceTableR = curr;
						break;
					}
				}
				
				for (TargetTableRectangle curr : Main.targetTableRects) {
					if (curr.table == targetTable) {
						targetTableR = curr;
						break;
					}
				}
				
				if (sourceTableR != null && targetTableR != null) {
					double startX = sourceTableR.getBoundsInParent().getMaxX();
					double startY = sourceTableR.getBoundsInParent().getMinY() + 0.5 * sourceTableR.getHeight();
					double endX = targetTableR.getBoundsInParent().getMinX();
					double endY = targetTableR.getBoundsInParent().getMinY() + 0.5 * targetTableR.getHeight();
					TargetTableRectangle.arrowList.add(new Arrow(startX, startY, endX, endY, sourceTable, targetTable));
				}
			}
		}
		else {
			Table sourceTable = SourceTableRectangle.expandingTable.table;
			Table targetTable = TargetTableRectangle.expandingTable.table;
			for (ItemToItemMap map : ObjectExchange.etl.getFieldToFieldMapping(sourceTable, targetTable).getSourceToTargetMaps()) {
				Field sourceField = (Field) map.getSourceItem();
				Field targetField = (Field) map.getTargetItem();
				SourceTableRectangle.FieldRectangle sourceFieldR = null;
				TargetTableRectangle.FieldRectangle targetFieldR = null;
				for (SourceTableRectangle.FieldRectangle curr : SourceTableRectangle.expandingTable.fieldRectList) {
					if (curr.field == sourceField) {
						sourceFieldR = curr;
						break;
					}
				}
				for (TargetTableRectangle.FieldRectangle curr : TargetTableRectangle.expandingTable.fieldRectList) {
					if (curr.field == targetField) {
						targetFieldR = curr;
						break;
					}
				}
				
				if (sourceFieldR != null && targetFieldR != null) {
					double startX = sourceFieldR.getBoundsInParent().getMaxX();
					double startY = sourceFieldR.getBoundsInParent().getMinY() + 0.5 * sourceFieldR.getHeight();
					double endX = targetFieldR.getBoundsInParent().getMinX();
					double endY = targetFieldR.getBoundsInParent().getMinY() + 0.5 * targetFieldR.getHeight();
					TargetTableRectangle.arrowList.add(new Arrow(startX, startY, endX, endY, sourceField, targetField));
				}
			}
		}
	}
}