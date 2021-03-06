
package modelo;

import java.time.LocalDateTime;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity 
@Table(name="mensagem_20192370039")
@Cacheable(false)
public class Mensagem {
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
	
	@Column(columnDefinition="TIMESTAMP")
    private LocalDateTime datahora;
    
    @ManyToOne
    private Usuario criador;
    
    private String texto;	//formato "yyyyMMdd"
    
    public Mensagem() {}
                
    public Mensagem(Usuario criador, String texto) {
		this.criador = criador;
		this.texto = texto;
		this.datahora = LocalDateTime.now();
	}
                           
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getData() {
        return datahora;
    }
    public void setData(LocalDateTime data) {
        this.datahora = data;
    }
    
	
    public Usuario getCriador() {
		return criador;
	}

	public void setCriador(Usuario criador) {
		this.criador = criador;
	}

	@Override
    public String toString() {
        return  "id=" + id + ", criador=" + criador.getNome() +
                ", datahora=" + datahora +"\ntexto=" + texto ;
    }   
}
