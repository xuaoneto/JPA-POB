package dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import modelo.Mensagem;

public class DAOMensagem extends DAO <Mensagem>{
	public static List<Mensagem> queryBuscarM(String termo) {
		 try{
			 TypedQuery<Mensagem> q = manager.createQuery
				("select m from Mensagem m where m.texto like '%"+termo+"%'", Mensagem.class);
        	return q.getResultList();
        	}
        catch (NoResultException e){
            return null;
            }
	}

	@Override
	public Mensagem read(Object chave) {
		try{
			int id = (int) chave;
			TypedQuery<Mensagem> q = manager.createQuery("select m from Mensagem m where m.id =:c", Mensagem.class);
			q.setParameter("c", id);
			return q.getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}
}
