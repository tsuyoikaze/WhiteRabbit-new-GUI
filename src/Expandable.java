import java.util.List;


public interface Expandable {
	public void expand(List<? extends Expandable> list);
	public void expand();
	public void collapse(List<? extends Expandable> list);
	public void collapse();
	public void toggle(List<? extends Expandable> list);
	public void shift(double y);
}
