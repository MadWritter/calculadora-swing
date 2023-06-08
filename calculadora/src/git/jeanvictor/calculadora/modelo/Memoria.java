package git.jeanvictor.calculadora.modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {
	
	private enum TipoComando {
		ZERAR, PORC, SINAL, NUMERO, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA;
	};
	
	private final List<MemoriaObservador> observadores = new ArrayList<>();
	
	private static final Memoria instancia = new Memoria();
	private TipoComando ultimaOperacao = null;
	private boolean substituir = false;
	private String textoAtual = "";
	private String textoBuffer = "";
	
	private Memoria() {}

	public static Memoria getInstancia() {
		return instancia;
	}
	
	public void adicionarObservador(MemoriaObservador o) {
		observadores.add(o);
	}

	public String getTextoAtual() {
		return textoAtual.isEmpty() ? "0" : textoAtual;
	}
	
	public void processarComando(String valor) {
		
		TipoComando tipoComando = detectarTipoComando(valor);
		if (tipoComando == null) {
			return;
		} else if (tipoComando == TipoComando.ZERAR) {
			textoAtual = "";
			textoBuffer = "";
			substituir = false;
			ultimaOperacao = null;
		} else if (tipoComando == TipoComando.SINAL && textoAtual.contains("-")) {
			textoAtual = textoAtual.substring(1);
		} else if (tipoComando == TipoComando.SINAL && !textoAtual.contains("-")) {
			textoAtual = "-" + textoAtual;
		} else if (tipoComando == TipoComando.NUMERO 
				|| tipoComando == TipoComando.VIRGULA) {
			textoAtual = substituir ? valor : textoAtual + valor;
			substituir = false;
		} else if (tipoComando == TipoComando.PORC && textoAtual.length() == 1) {
			textoAtual = "0,0" +  textoAtual;
		} else if (tipoComando == TipoComando.PORC && textoAtual.length() == 2) {
			textoAtual = "0," +  textoAtual;
		} else {
			substituir = true;
			textoAtual = obterResultadoOperacao();
			textoBuffer = textoAtual;
			ultimaOperacao = tipoComando;
		}
		observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
	}

	private String obterResultadoOperacao() {
		if (ultimaOperacao == null || ultimaOperacao == TipoComando.IGUAL) {
			return textoAtual;
		}
				
		double numeroBuffer =
				Double.parseDouble(textoBuffer.replace(",", "."));
		double numeroAtual =
				Double.parseDouble(textoAtual.replace(",", "."));
		
		double resultado = 0;
		String resultadoFinal = null;
		
		if (ultimaOperacao == TipoComando.SOMA) {
			resultado = numeroBuffer + numeroAtual;
		} else if (ultimaOperacao == TipoComando.SUB) {
			resultado = numeroBuffer - numeroAtual;
		} else if (ultimaOperacao == TipoComando.MULT) {
			resultado = numeroBuffer * numeroAtual;
		}  else if (ultimaOperacao == TipoComando.DIV) {
			resultado = numeroBuffer / numeroAtual;
		} else if (ultimaOperacao == TipoComando.PORC) {
			resultado = numeroBuffer / numeroAtual;
		} if (resultado > 0) {
			resultadoFinal = Double.toString(resultado).replace(".", ",");			
		} else if (resultado < 0 && resultado > 0.09) {
			resultadoFinal = Double.toString(resultado).substring(2);
		} else if (resultado < 0.0 && resultado > 0.009) {
			resultadoFinal = Double.toString(resultado).substring(3);			
		}
		
		boolean inteiro = resultadoFinal.endsWith(",0");
		return inteiro ? resultadoFinal.replace(",0", "") : resultadoFinal;
	}

	private TipoComando detectarTipoComando(String valor) {
		if (textoAtual.isEmpty() && valor == "0") {
			return null;
		}
		try {
			Integer.parseInt(valor);
			return TipoComando.NUMERO;
		} catch (NumberFormatException e) {
			if ("AC".equals(valor)) {
				return TipoComando.ZERAR;
			} else if("/".equals(valor)) {
				return TipoComando.DIV;
			} else if("X".equals(valor)) {
				return TipoComando.MULT;
			} else if("+".equals(valor)) {
				return TipoComando.SOMA;
			} else if("-".equals(valor)) {
				return TipoComando.SUB;
			} else if("%".equals(valor)) {
				return TipoComando.PORC;
			} else if("±".equals(valor)) {
				return TipoComando.SINAL;
			} else if("=".equals(valor)) {
				return TipoComando.IGUAL;
			} else if(",".equals(valor) && !textoAtual.contains(",")) {
				return TipoComando.VIRGULA;
			}
		}
		return null;
	}
}
