package ativ06;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class MongoDBService {
	static Logger root = (Logger) LoggerFactory
			.getLogger(Logger.ROOT_LOGGER_NAME);

	static {
		root.setLevel(Level.OFF);
	}

	/***
	 * Conecta com o MongoDB
	 * @param url - mongodb:// + nome do servicdor
	 * @return Conexão com o MongoDB
	 */
	private static MongoClient conectar(String url) {
		try {
			MongoClient client = MongoClients.create(url);
			return client;
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	/***
	 * Lista todos os produtos da loja
	 * @param db - Conexão com o Base
	 */
	public void listaProdutos(MongoDatabase db) {
		try {
			MongoCollection<Document> collection = db.getCollection("produtos");

			Iterable<Document> produtos = collection.find();
			for (Document produto : produtos) {
				String nome = produto.getString("nome");
				String descricao = produto.getString("descricao");
				String valor = produto.getString("valor");
				String estado = produto.getString("estado");
				System.out.println(nome + " -- " + descricao + " -- " + valor + " -- " + estado);
			}
		}
		catch (Throwable e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		}
	}
	/***
	 * Insere um novo produto na tabela Produto
	 * @param db - Conexão com o BD
	 * @param nome
	 * @param descricao
	 * @param valor
	 * @param estado
	 */
	public void insereProduto(MongoDatabase db, String nome, String descricao, String valor, String estado) {
		try {
			MongoCollection<Document> collection = db.getCollection("produtos");
			Document doc = new Document("nome", nome)
					.append("nome", nome)
					.append("descricao", descricao)
					.append("valor", valor)
					.append("estado", estado);

			collection.insertOne(doc);
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/***
	 * Altera valor produto
	 * @param db - Conexão com o BD
	 * @param nome - Nome do produto a ser alterado
	 * @param valor
	 */
	public void alteraValorProduto(MongoDatabase db, String nome, String valor) {
		try {
			MongoCollection<Document> collection = db.getCollection("produtos");
			Document newDoc = new Document("$set", new Document("valor", valor));
			collection.updateOne(eq("nome", nome), newDoc);
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/***
	 * Apaga um produto
	 * @param db - Conexão com o BD
	 * @param idProduto - Id do produto a ser apagado
	 */
	public void apagaProduto(MongoDatabase db, String nome) {
		try {
			MongoCollection<Document> collection = db.getCollection("produtos");

			collection.deleteOne(eq("nome", nome));
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("Conectando com o MongoDB e com a base loja");

		MongoDBService loja = new MongoDBService();
		MongoClient client = conectar("mongodb://172.17.0.2:27017");
		MongoDatabase db = client.getDatabase("loja");

		if(db != null) {
			System.out.println("Lista Original de Produtos");
			// Lista os produtos da Loja
			loja.listaProdutos(db);

			// Insere novo produto
			loja.insereProduto(db, "Prod7", "Bla Bla", "500.0", "Bla Bla");
			System.out.println("Lista com Novo Produto");
			// Lista os produtos da Loja
			loja.listaProdutos(db);

			// Altera valor do produto
			loja.alteraValorProduto(db, "Prod7", "400.0");
			System.out.println("Lista com Valor do Produto Alterado");
			// Lista com produto alterado
			loja.listaProdutos(db);

			System.out.println("Apaga Produto Número 7");
			// Apaga produto
			loja.apagaProduto(db, "Prod7");
			System.out.println("Volta a Lista Original de Produtos");
			// Lista os produtos da Loja
			loja.listaProdutos(db);
			
			// Fecha conexão com banco de dados
			try {
				client.close();
			}
			catch (MongoException e) {
				System.out.println("Erro ao fechar conexâo: " + e);
				e.printStackTrace();
			}
		}
	}
}
