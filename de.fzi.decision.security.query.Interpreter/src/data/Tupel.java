package data;

public class Tupel<T,U> {

	private T first;
	private U second;
	
	public Tupel(T first, U second) {
		super();
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public U getSecond() {
		return second;
	}

	public void setSecond(U second) {
		this.second = second;
	}
	
	
}
