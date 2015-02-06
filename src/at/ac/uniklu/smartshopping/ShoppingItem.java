package at.ac.uniklu.smartshopping;

public class ShoppingItem {
	
	private String text;
	private Boolean isChecked;

	public ShoppingItem() {
		setText("");
		setChecked(false);
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

}
