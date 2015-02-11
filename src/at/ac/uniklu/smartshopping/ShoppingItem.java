package at.ac.uniklu.smartshopping;

public class ShoppingItem {
	
	private String text;
	private Boolean isChecked;
	private String table;

	public ShoppingItem() {
		setText("");
		setChecked(false);
		setTable("");
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Boolean isChecked() {
		return isChecked;
	}

	public void setChecked(Boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

}
