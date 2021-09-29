package modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="logs_20192370039")
public class Log {
	
	@Id 
	@Column(columnDefinition="TIMESTAMP")
	private LocalDateTime datahora;
	
	private String nome;
	
	public Log() {}
	
	public Log(String nome) {
		this.nome = nome;
		this.datahora = LocalDateTime.now();
	}
	
	public LocalDateTime getDatahora() {
		return datahora;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return "Log [datahora=" + datahora + ", nome=" + nome + "]";
	}
	
	
	
	
}
