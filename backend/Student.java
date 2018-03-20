package backend;

public class Student {

	private String name;
	private long ID;
	
	public Student(String name, long ID){
		this.setName(name);
		this.setID(ID);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}
}
