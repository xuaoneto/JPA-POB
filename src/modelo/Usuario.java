
package modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="usuario_20192370039")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Usuario {
	
	@Id
	private String nomesenha;			//  nome + / + senha
	
	@OneToMany(mappedBy="criador", orphanRemoval=true)
	private List<Mensagem> mensagens = new ArrayList<>();   //criadas por ele
	
	private boolean ativo = true;

	public Usuario() {}
	
	public Usuario(String nomesenha) {
		this.nomesenha = nomesenha;
	}
	
	public void delMensagem(Mensagem msg) {
		mensagens.remove(msg);
	}
	
	public String getNome() {
		return nomesenha.split("/")[0];
	}
	
	public void setNome(String nome) {
		this.nomesenha = nome;
	}

	public boolean ativo() {
		return ativo;
	}
	public void ativar() {
		ativo=true;
	}
	public void desativar() {
		ativo=false;
	}

	public List<Mensagem> getMensagens() {
		return mensagens;
	}

	public void adicionar(Mensagem me){
		mensagens.add(me);
	}
	public void remover(Mensagem me){
		mensagens.remove(me);
	}
		

	@Override
	public String toString() {
		String texto = "Nome=" + nomesenha ;

		texto += "\n  lista de Mensagens: ";
		if (mensagens.isEmpty())
			texto += "sem mensagens";
		else 	
			for(Mensagem m: mensagens) 
				texto += "\n  --> " + m ;

		return texto ;

	}

}