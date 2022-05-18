public interface GrainList<T> {
	public boolean add(T item);
	public boolean remove(T item);
	public boolean contains(T item);
}
