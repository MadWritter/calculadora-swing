package git.jeanvictor.calculadora.visao;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import git.jeanvictor.calculadora.modelo.Memoria;
import git.jeanvictor.calculadora.modelo.MemoriaObservador;

@SuppressWarnings("serial")
public class Display extends JPanel implements MemoriaObservador{
	
	private JLabel label;
	
	/*
	 * Display onde mostra os resultados
	 * inseridos e o resultado das operações
	 * 
	 * label com os números setada para a direita
	 */
	public Display() {
		Memoria.getInstancia().adicionarObservador(this);
		setBackground(new Color(46, 49, 50));
		label = new JLabel(Memoria.getInstancia().getTextoAtual());
		label.setForeground(Color.WHITE);
		label.setFont(new Font("calibri", Font.PLAIN, 30));
		setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 20));
		add(label);
	}
	
	@Override
	public void valorAlterado(String valor) {
		label.setText(valor);
	}
}
