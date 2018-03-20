package backend;

public class Assignment {

	private String name;
	private String category;
	private int denominator;
	
	public Assignment(String name, String category, int denominator){
		this.setName(name);
		this.setCategory(category);
		this.setDenominator(denominator);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getDenominator() {
		return denominator;
	}

	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}
}
