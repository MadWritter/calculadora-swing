package git.jeanvictor.calculadora.modelo;

@FunctionalInterface
public interface MemoriaObservador {

	void valorAlterado(String valor);
}
