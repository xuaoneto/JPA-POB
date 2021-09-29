package dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;


import modelo.Usuario;

public class DAOUsuario extends DAO <Usuario>{
	
	@Override
	public Usuario read(Object chave) {
		String nomesenha = (String) chave;
		try {
			TypedQuery<Usuario> q = manager.createQuery("select u from Usuario u where u.nomesenha =:n", Usuario.class);
			q.setParameter("n", nomesenha);
			return q.getSingleResult();
		}catch (NoResultException e){
			return null;	
		}
		
	}
	
}
