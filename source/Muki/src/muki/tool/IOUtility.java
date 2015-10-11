/**
 *  Copyright 2015 Gabriel Casarini
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package muki.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * This is a utility class that centralizes the management of files and resources
 */
public class IOUtility {

	public IOUtility() {		
	}

	/**
	 * Retorna la ruta completa del fichero cuyo nombre corresponde al
	 * parametro. Notar que el fichero debe estar accesible en el classpath de
	 * la aplicacion
	 */
	public String getAbsolutePathForLocalResource(String resourceName) {
		URL url = this.getClass().getResource(resourceName);
		String fullPath = url.getFile();
		return fullPath;
	}

	/**
	 * Retorna true si el fichero indicado por al ruta existe; retorna false en
	 * caso contrario.
	 */
	public boolean existsFile(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * Borra el fichero indicado en la ruta.
	 */
	public void deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * Crea un nuevo directorio en la ruta indicada.
	 */
	public void createDirectory(String directoryPath) {
		File output = new File(directoryPath);
		output.mkdirs();
	}

	/**
	 * Borra el directorio apuntado por la ruta. El directorio puede contener
	 * subdirectorios o ficheros.
	 */
	public boolean deleteDirectory(String directoryPath) {
		return this.deleteDirectory(new File(directoryPath));
	}

	/**
	 * Borra el directorio apuntado por la ruta. El directorio puede contener
	 * subdirectorios o ficheros.
	 */
	public boolean deleteDirectory(File directory) {
		if (directory.isDirectory()) {
			String[] children = directory.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = this.deleteDirectory(new File(directory, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return directory.delete();
	}

	/**
	 * Lee el contenido de un fichero de texto y lo retorna en string.
	 */
	public String readTextFile(String fileName) {
		StringBuilder contents = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String str;
			while ((str = in.readLine()) != null) {
				contents.append(str);
				contents.append("\n");
			}
			in.close();
			return contents.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Guarda un string en un fichero de texto, en la ruta indicada.
	 */
	public void writeTextFile(String fileName, String data) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			out.write(data);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lee el contenido de un fichero binario desde el disco y lo retorna en un
	 * array de bytes.
	 */
	public byte[] readBinaryFile(String fileName) throws IOException {
		File file = new File(fileName);
		FileInputStream is = new FileInputStream(file);

		long length = file.length();
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("El fichero no se ha leido completamente: " + file.getName());
		}
		is.close();
		return bytes;
	}

	/**
	 * Descarga el recurso apuntado por la URL y retorna el contenido en un
	 * array de bytes.
	 */
	public byte[] readBytes(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();

		byte[] bytes = new byte[connection.getContentLength()];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("El fichero no se ha leido completamente: " + url.toString());
		}
		is.close();
		return bytes;
	}

	/**
	 * Descarga un objeto desde la URL del parametro
	 */
	public Object readObject(URL url) throws IOException, ClassNotFoundException {
		byte[] data = this.readBytes(url);
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bis);
		Object resultado = in.readObject();
		return resultado;
	}

	/**
	 * Guarda el contenido binario en un fichero, en la ruta indicada.
	 */
	public void writeBinaryFile(String fileName, byte[] data) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(data);
		fos.close();
	}

	/**
	 * Serializa el objeto y lo envia a la URL destino. Retorna otro objeto
	 * con la respuesta enviada por el servidor.
	 * Notar que la respuesta NO es el codigo de error (404, 500, 200, etc)
	 */
	public Object writeObject(URL url, Object object) throws IOException, ClassNotFoundException {
		// Serializar objeto recibido por parametro
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(object);
		out.close();

		// Abrir conexion y enviar el objeto serializado
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestProperty("Content-Type", "application/octet-stream");
		OutputStream os = connection.getOutputStream();
		os.write(bos.toByteArray());
		os.flush();
		
		// Leer el codigo de error (404, 4010, 500, 200, etc)
		int status = connection.getResponseCode();
		
		// Leer la respuesta enviada
		InputStream is = connection.getInputStream();
		byte[] bytes = new byte[connection.getContentLength()];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("El fichero no se ha leido completamente: " + url.toString());
		}
		is.close();
		
		// Instanciar objeto con los bytes leidos
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream in = new ObjectInputStream(bis);
		Object respuesta = in.readObject();
		connection.disconnect();
		return respuesta;
	}

	/**
	 * Lee un fichero de propiedades disponible en un jar dentro del classpath
	 * o bien de un fichero en el classpath mismo.
	 * y retorna el contenido
	 */
	public Properties getProperties(String rutaFichero) throws IOException {
		InputStream in = IOUtility.class.getClassLoader().getResourceAsStream(rutaFichero);
		Properties props = new java.util.Properties();
		props.load(in);
		return props;
	}
		
}
