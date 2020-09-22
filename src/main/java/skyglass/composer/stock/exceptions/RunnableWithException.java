package skyglass.composer.stock.exceptions;

@FunctionalInterface
public interface RunnableWithException<E extends Exception> {

	void run() throws E;

}
