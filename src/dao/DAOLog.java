package dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import modelo.Log;


public class DAOLog extends DAO <Log>{
	@Override
	public Log read(Object chave) {
		try{
			String nome = (String) chave;
			TypedQuery<Log> q = manager.createQuery("select l from Log l where l.nome =:n", Log.class);
			q.setParameter("n", nome);
		
			return q.getSingleResult();
		}catch(NoResultException e){
			return null;	
		}
		
	}
}
