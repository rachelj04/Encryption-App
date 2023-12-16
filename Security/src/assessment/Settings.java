package assessment;

public class Settings {

	private String menuColor;
	private String backgroundColor;
	
	
	public Settings(String s1, String s2) {
		super();
		this.menuColor = s1;
		this.backgroundColor = s2;
	}


	public String getMenuColor() {
		return menuColor;
	}


	public void setMenuColor(String menuColor) {
		this.menuColor = menuColor;
	}


	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	
	
}