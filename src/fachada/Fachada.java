package fachada;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import dao.DAO;
import dao.DAOLog;
import dao.DAOMensagem;
import dao.DAOUsuario;
import modelo.Administrador;
import modelo.Log;
import modelo.Mensagem;
import modelo.Usuario;

public class Fachada {
	private static DAOUsuario daousuario = new DAOUsuario();  
	private static DAOMensagem daomensagem = new DAOMensagem();  
	private static DAOLog daolog = new DAOLog();  

	private static Usuario usuariologado=null;


	public static void inicializar() {
		DAO.open();
	}

	public static void finalizar(){
		DAO.close();
	}

	public static List<Usuario> listarUsuarios() {
		// nao precisa estar logado
		return daousuario.readAll();	
	}
	
	public static List<Mensagem> listarMensagens() {
		// nao precisa estar logado
		return daomensagem.readAll();	
	}

	public static List<Log> listarLogs() {
		// nao precisa estar logado
		return daolog.readAll();	
	}
	
	public static List<Mensagem> buscarMensagens(String termo) throws  Exception{
		DAO.begin();
		List<Mensagem> retorno = DAOMensagem.queryBuscarM(termo);
		if(retorno != null) {
			DAO.commit();
			return retorno;
			
		}else {
			DAO.rollback();	
			throw new Exception ("Mensagem n?o encontrada");
		}
		
		
		/*
		 * nao precisa estar logado
		 * query no banco para obter mensagens do grupo que contenha
		 *  o termo (considerar case insensitive)
		 * 
		 */
	}

	public static Usuario criarUsuario(String nome, String senha) throws  Exception{
		// nao precisa estar logado
		DAO.begin();
		Usuario usuario = daousuario.read(nome+"/"+senha);	
		if(usuario != null) {
			DAO.rollback();	
			throw new Exception("criar usuario - usuario existente:" + nome);
		}

		usuario = new Usuario(nome+"/"+senha);
		daousuario.create(usuario);		
		DAO.commit();
		return usuario;
	}


	public static void login(String nome, String senha) throws Exception{		
		//verificar se ja existe um usuario logada
		if(usuariologado!=null)
			throw new Exception ("ja existe um usuario logado"+getLogado());

		DAO.begin();	
		Usuario u = daousuario.read(nome+"/"+senha);	
		if(u == null) {
			DAO.rollback();	
			throw new Exception("login - usuario inexistente:" + nome);
		}
		if(!u.ativo()) {
			DAO.rollback();	
			throw new Exception("login - usuario nao ativo:" + nome);
		}
		usuariologado = u;		//altera o logado na fachada

		Log log = new Log(usuariologado.getNome() + " - login");
		daolog.create(log);
		DAO.commit();
	}
	
	public static void logoff() {		
		DAO.begin();
		Log log = new Log(usuariologado.getNome() + " - logoff");
		daolog.create(log);
		DAO.commit();

		usuariologado = null; 		//altera o logado na fachada
	}

	public static Usuario getLogado() {
		return usuariologado;
	}

	public static Mensagem criarMensagem(String texto) throws Exception{
		DAO.begin();
		if (getLogado() != null) {
			
			Mensagem m = new Mensagem(usuariologado, texto);
			daomensagem.create(m);
			usuariologado.adicionar(m);
			DAO.commit();
			return m;
		
		}else {
			DAO.rollback();
			throw new Exception("O usu?rio n?o est? logado.");
		}
		
		
		/*
		 * tem que esta logado
		 * criar a mensagem, onde o criador ? a usuario logada
		 * adicionar esta mensagem na lista de mensagens de cada usuario do grupo,
		 * incluindo a do criador
		 * retornar mensagem criada
		 */

		//para gerar o novo id da mensagem utilize:
		//		int id = daomensagem.obterUltimoId();
		//		id++;
		//		Mensagem m = new Mensagem(id, usuariologado, texto);

	}

	public static List<Mensagem> listarMensagensUsuario() throws Exception{
		if (getLogado() != null) {
			return usuariologado.getMensagens();
		}else {
			throw new Exception("O usu?rio n?o est? logado.");
		}
		/*
		 * tem que esta logado
		 * retorna todas as mensagens do usuario logado
		 * 
		 */
	}

	public static void apagarMensagens(int id) throws  Exception{
		DAO.begin();
		if (usuariologado != null) {
			List <Mensagem> msgs = daomensagem.readAll();
			for (int k = 0; k < msgs.size(); k++) {
			
				if(msgs.get(k).getId() == id) {
					
					if (Fachada.getLogado().getNome().equals(msgs.get(k).getCriador().getNome())) {
						daomensagem.delete(msgs.get(k));
						DAO.commit();
						
					}else {
						DAO.rollback();
						throw new Exception("Voc? so pode apagar mensagens suas.");
					}
				}
			
			}
			
		}else {
			DAO.rollback();
			throw new Exception("O usu?rio precisa estar logado.");
		}
		
		/*
		 * tem que esta logado
		 * recebe uma lista de numeros de id 
		 * (id ? um numero entre 1 a N, onde N ? a quatidade atual de mensagens do grupo)
		 * validar se ids s?o de mensagens criadas pelo usuario logado
		 * (um usuario nao pode apagar mensagens de outros usuarios)
		 * 
		 * remover cada mensagem da lista de mensagens do usuario logado
		 * apagar cada mensagem do banco 
		 */
	}

	public static void sairDoGrupo() throws  Exception{
		DAO.begin();
		if (getLogado() != null) {
			String msgS = usuariologado.getNome()+" saiu do grupo";
			criarMensagem(msgS);
			usuariologado.desativar();
			daousuario.update(usuariologado);
			DAO.commit();
		}else {
			DAO.rollback();
			throw new Exception("O usu?rio ja est? deslogado.");
		}
		/*
		 * tem que esta logado
		 * 
		 * criar a mensagem "fulano saiu do grupo"
		 * desativar o usuario logado e fazer logoff dele
		 */
	}

	//	public static int totalMensagensUsuario() throws Exception{
	//		/*
	//		 * tem que esta logado
	//		 * retorna total de mensagens criadas pelo usuario logado
	//		 * 
	//		 */
	//	}

	public static void esvaziar() throws Exception{
		DAO.clear();
	}

	/**************************************************************
	 * 
	 * NOVOS M?TODOS DA FACHADA PARA O PROJETO 2
	 * 
	 **************************************************************/

	public static Administrador criarAdministrador(String nome, String senha, String email) throws  Exception{
		// nao precisa estar logado
		DAO.begin();	
		Usuario u = daousuario.read(nome+"/"+senha);	
		if(u != null) {
			DAO.rollback();	
			throw new Exception("criar administrador - usuario ja existe:" + nome);
		}

		Administrador ad = new Administrador(nome+"/"+senha, email);
		daousuario.create(ad);		
		DAO.commit();
		return ad;
	}

	public static void solicitarAtivacao(String nome, String senha) throws  Exception{
		DAO.begin();	
		Usuario u = daousuario.read(nome+"/"+senha);
		if(!u.ativo()) {
			Fachada.enviarEmail("solicita??o de ativa??o", u.getNome());
			DAO.commit();
		}else {
			DAO.rollback();	
			throw new Exception("criar administrador - usuario ja existe:" + nome);
		}
		/*
		 * o usuario (nome+senha) tem que estar desativado
		 *  
		 * enviar um email para o administrador com a mensagem "nome solicita ativa??o"
		 * usar o m?todo Fachada.enviarEmail(...) 
		 * 
		 */
	}

	public static void solicitarExclusao(String nome, String senha) throws  Exception{
		DAO.begin();
		Usuario u = daousuario.read(nome+"/"+senha);
		if (u == null) {
			DAO.rollback();	
			throw new Exception("O usu?rio n?o existe");
		}
		Fachada.enviarEmail("solicita??o de exclus?o", u.getNome());
		DAO.commit();
		/*
		 * o usuario (nome+senha) tem que estar desativado
		 *  
		 * enviar um email para o administrador com a mensagem "nome solicita exclus?o"
		 * usar o m?todo Fachada.enviarEmail(...) 
		 * 
		 */
	}

	public static void ativarUsuario(String nome) throws  Exception{
		DAO.begin();
		if(usuariologado instanceof Administrador) {
			Usuario u = daousuario.read(nome);
			if (!u.ativo()) {
				u.ativar();
				String msgE = u.getNome()+" entrou no grupo";
				Fachada.criarMensagem(msgE);
				DAO.commit();
			}else {
				DAO.rollback();	
				throw new Exception("O usu?rio n?o est? desativado");
			}
			
		}else {
			DAO.rollback();	
			throw new Exception("O usu?rio n?o ? administrador");
		}
		/*
		 * o usuario logado tem que ser um administrador  e o usuario a 
		 * ser ativado (nome) tem que estar desativado 
		 *  
		 * ativar o usuario 
		 * criar a mensagem "nome entrou no grupo"
		 * 
		 */
	}

	public static void apagarUsuario(String nome) throws  Exception{
		DAO.begin();
		if(usuariologado instanceof Administrador) {
			Usuario u = daousuario.read(nome);
			if (u == null) {
				DAO.rollback();	
				throw new Exception("O usu?rio n?o existe");
			}else {
				if(u instanceof Administrador) {
					DAO.rollback();	
					throw new Exception("O usu?rio ? um administrador");
				}else {
					if(!u.ativo()) {
						String msgE = u.getNome()+" foi excluido do sistema";
						List<Mensagem> mensagens = u.getMensagens();
				
						for(int k=0; k<mensagens.size(); k++) {
							Fachada.apagarMensagens(u.getMensagens().get(k).getId());
						}
						daousuario.delete(u);
						Fachada.criarMensagem(msgE);
						DAO.commit();
					}else {
						DAO.rollback();	
						throw new Exception("O usu?rio est? ativo");
					}
				}
			}
		}else {
			DAO.rollback();	
			throw new Exception("O usu?rio n?o ? administrador");
		}
		/*
		 * o usuario logado tem que ser um administrador  e o usuario a 
		 * ser apagado tem que estar desativado (e n?o pode ser do tipo Administrador)
		 *  
		 * apagar as mensagens do usuario e apagar o usuario 
		 * criar a mensagem "nome foi excluido do sistema"
		 */
	}


	/**************************************************************
	 * 
	 * M?TODO PARA ENVIAR EMAIL, USANDO UMA CONTA (SMTP) DO GMAIL
	 * ELE ABRE UMA JANELA PARA PEDIR A SENHA DO EMAIL DO EMITENTE
	 * ELE USA A BIBLIOTECA JAVAMAIL (ver pom.xml)
	 * Lembrar de: 
	 * 1. desligar antivirus e de 
	 * 2. ativar opcao "Acesso a App menos seguro" na conta do gmail
	 * 
	 **************************************************************/
	public static void enviarEmail(String assunto, String mensagem) {
		try {
			/*
			 * ********************************************************
			 * Obs: lembrar de desligar antivirus e 
			 * de ativar "Acesso a App menos seguro" na conta do gmail
			 * 
			 * pom.xml contem a dependencia javax.mail
			 * 
			 * ********************************************************
			 */
			//configurar emails
			String emailorigem = "xxxxxxxxxxxxx@gmail.com";
			String senhaorigem = pegarSenha();
			String emaildestino = "yyyyyyyyyyyy@gmail.com";

			//Gmail
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");

			Session session;
			session = Session.getInstance(props,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(emailorigem, senhaorigem);
				}
			});

			MimeMessage message = new MimeMessage(session);
			message.setSubject(assunto);		
			message.setFrom(new InternetAddress(emailorigem));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emaildestino));
			message.setText(mensagem);   // usar "\n" para quebrar linhas
			Transport.send(message);

			//System.out.println("enviado com sucesso");

		} catch (MessagingException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * JANELA PARA DIGITAR A SENHA DO EMAIL
	 */
	public static String pegarSenha(){
		JPasswordField field = new JPasswordField(10);
		field.setEchoChar('*'); 
		JPanel painel = new JPanel();
		painel.add(new JLabel("Entre com a senha do email:"));
		painel.add(field);
		JOptionPane.showMessageDialog(null, painel, "Senha", JOptionPane.PLAIN_MESSAGE);
		String texto = new String(field.getPassword());
		return texto.trim();
	}
}

