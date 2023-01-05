package kaba4cow.engine.assets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.Vertex;
import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.consts.Const;
import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.toolbox.maths.Maths;

public class Loaders {

	private static final Map<Integer, List<Integer>> vaos = new HashMap<Integer, List<Integer>>();
	private static final List<AbstractShader> shaders = new ArrayList<AbstractShader>();
	private static final List<Integer> textures = new ArrayList<Integer>();

	private static final LinkedList<Integer> texturesToDelete = new LinkedList<Integer>();

	public static void update() {
		if (!texturesToDelete.isEmpty()) {
			Integer texture = texturesToDelete.removeFirst();
			GL11.glDeleteTextures(texture);
			textures.remove(texture);
		}
	}

	public static void cleanUp() {
		Printer.println("CLEANING UP " + vaos.size() + " VAOS");
		for (Integer vao : vaos.keySet()) {
			GL30.glDeleteVertexArrays(vao);
			List<Integer> vbos = vaos.get(vao);
			for (int i = 0; i < vbos.size(); i++)
				GL15.glDeleteBuffers(vbos.get(i));
		}
		Printer.println("CLEANING UP " + shaders.size() + " SHADERS");
		for (int i = 0; i < shaders.size(); i++)
			shaders.get(i).cleanUp();
		Printer.println("CLEANING UP " + textures.size() + " TEXTURES");
		for (int i = 0; i < textures.size(); i++)
			GL11.glDeleteTextures(textures.get(i));
	}

	public static void deleteVAO(int vao) {
		if (!vaos.containsKey(vao))
			return;
		GL30.glDeleteVertexArrays(vao);
		List<Integer> vbos = vaos.get(vao);
		for (Integer vbo : vbos)
			GL15.glDeleteBuffers(vbo);
		vbos.clear();
		vaos.remove(vao);
	}

	public static void deleteTexture(int texture) {
		if (!textures.contains(texture))
			return;
		texturesToDelete.add(texture);
	}

	public static int vaos() {
		return vaos.size();
	}

	public static int shaders() {
		return shaders.size();
	}

	public static int textures() {
		return textures.size();
	}

	public static RawModel loadToVAO(String file) {
		ModelData modelData = getModelData(file);
		if (modelData == null)
			return null;
		return loadToVAO(modelData);
	}

	public static RawModel loadToVAO(File file) {
		ModelData modelData = getModelData(file);
		if (modelData == null)
			return null;
		return loadToVAO(modelData);
	}

	public static ModelData getModelData(String file) {
		return getModelData(new File("resources/" + file + ".obj"));
	}

	public static ModelData getModelData(File file) {
		Printer.println("LOADING MODEL: " + file.getAbsolutePath());
		try {
			FileInputStream in = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			List<Vertex> vertices = new ArrayList<Vertex>();
			List<Vector2f> textures = new ArrayList<Vector2f>();
			List<Vector3f> normals = new ArrayList<Vector3f>();
			List<Integer> indices = new ArrayList<Integer>();
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);
				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					break;
				}
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, vertices, indices);
				processVertex(vertex2, vertices, indices);
				processVertex(vertex3, vertices, indices);
				line = reader.readLine();
			}
			reader.close();
			removeUnusedVertices(vertices);
			float[] verticesArray = new float[vertices.size() * 3];
			float[] texturesArray = new float[vertices.size() * 2];
			float[] normalsArray = new float[vertices.size() * 3];
			convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray);
			int[] indicesArray = convertIndicesListToArray(indices);
			return new ModelData(verticesArray, texturesArray, normalsArray, indicesArray);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
		} else
			dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++)
			indicesArray[i] = indices.get(i);
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals,
			float[] verticesArray, float[] texturesArray, float[] normalsArray) {
		float furthestPoint = 0f;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			furthestPoint = Maths.max(furthestPoint, currentVertex.getLength());

			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;

			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;

			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex,
			List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}
		}
	}

	private static void removeUnusedVertices(List<Vertex> vertices) {
		for (int i = 0; i < vertices.size(); i++) {
			Vertex vertex = vertices.get(i);
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

	public static RawModel loadToVAO(ModelData modelData) {
		int vao = createVAO();
		bindIndicesBuffer(vao, modelData.indices);
		storeDataInAttributeList(vao, 0, 3, modelData.vertices);
		storeDataInAttributeList(vao, 1, 2, modelData.textures);
		storeDataInAttributeList(vao, 2, 3, modelData.normals);
		unbindVAO();
		return new RawModel(vao, modelData.indices.length);
	}

	public static RawModel loadToVAO(float[] positions, float[] normals, float[] uvs, int[] indices) {
		int vao = createVAO();
		bindIndicesBuffer(vao, indices);
		storeDataInAttributeList(vao, 0, 3, positions);
		storeDataInAttributeList(vao, 1, 2, uvs);
		storeDataInAttributeList(vao, 2, 3, normals);
		unbindVAO();
		return new RawModel(vao, indices.length);
	}

	public static RawModel loadToVAO(float[] positions, int dimensions) {
		int vao = createVAO();
		storeDataInAttributeList(vao, 0, dimensions, positions);
		unbindVAO();
		return new RawModel(vao, positions.length / dimensions);
	}

	public static int loadToVAO(float[] positions, float[] uvs) {
		int vao = createVAO();
		storeDataInAttributeList(vao, 0, 2, positions);
		storeDataInAttributeList(vao, 1, 2, uvs);
		unbindVAO();
		return vao;
	}

	private static int createVAO() {
		int vao = GL30.glGenVertexArrays();
		vaos.put(vao, new ArrayList<Integer>());
		GL30.glBindVertexArray(vao);
		return vao;
	}

	public static int createEmptyVBO(int vao, int floatCount) {
		int vbo = GL15.glGenBuffers();
		vaos.get(vao).add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}

	private static void storeDataInAttributeList(int vao, int attributeNumber, int coordinateSize, float[] data) {
		int vbo = GL15.glGenBuffers();
		vaos.get(vao).add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private static void bindIndicesBuffer(int vao, int[] indices) {
		int vboID = GL15.glGenBuffers();
		vaos.get(vao).add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
	}

	private static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private static void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	public static int loadShader(String file, int type, Const[] consts) {
		Printer.println("LOADING SHADER: " + file);
		StringBuilder source = new StringBuilder();
		try {
			InputStream in = AbstractShader.class.getResourceAsStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			boolean checkedConsts = consts == null || consts.length == 0;
			while (true) {
				source.append(line).append("\n");
				if (!checkedConsts && line.startsWith("#version")) {
					for (int i = 0; i < consts.length; i++)
						source.append(consts[i].getString()).append("\n");
					checkedConsts = true;
				}
				line = reader.readLine();
				if (line == null)
					break;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, source);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not compile shader: " + file);
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 1024));
			return 0;
		}
		return shaderID;
	}

	public static void addShader(AbstractShader shader) {
		if (shader != null)
			shaders.add(shader);
	}

	public static int loadTexture(String file, boolean linearSampling) {
		Printer.println("LOADING TEXTURE: " + file);
		try {
			FileInputStream in = new FileInputStream(new File("resources/" + file + ".png"));
			Texture texture = TextureLoader.getTexture("PNG", in);
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.5f);
			int sampling = linearSampling ? GL11.GL_LINEAR : GL11.GL_FASTEST;
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, sampling);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, sampling);
			int textureID = texture.getTextureID();
			textures.add(textureID);
			return textureID;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static int createTexture(int size, boolean linearSampling) {
		int textureID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL13.GL_TEXTURE0, 0, GL11.GL_RGBA, size, size, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		textures.add(textureID);
		return textureID;
	}

	public static int loadFontTexture(String file) {
		Printer.println("LOADING FONT TEXTURE: " + file);
		try {
			FileInputStream in = new FileInputStream(new File("resources/" + file + ".png"));
			Texture texture = TextureLoader.getTexture("PNG", in);
			int textureID = texture.getTextureID();
			textures.add(textureID);
			return textureID;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static int loadCubemapTexture(String... files) {
		String string = new String();
		for (int i = 0; i < files.length; i++)
			string += " " + files[i];
		Printer.println("LOADING CUBEMAP TEXTURES:" + string);
		int textureID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
		for (int i = 0; i < 6; i++) {
			TextureData data = decodeTextureFile(files[i % files.length]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.width, data.height, 0,
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.buffer);
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		textures.add(textureID);
		return textureID;
	}

	public static int createCubemapTexture(int size) {
		int textureID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
		for (int i = 0; i < 6; i++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, size, size, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		textures.add(textureID);
		return textureID;
	}

	private static TextureData decodeTextureFile(String file) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(new File("resources/" + file + ".png"));
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new TextureData(buffer, width, height);
	}

	private static class TextureData {

		private final int width;
		private final int height;
		private final ByteBuffer buffer;

		public TextureData(ByteBuffer buffer, int width, int height) {
			this.buffer = buffer;
			this.width = width;
			this.height = height;
		}

	}

	public static class ModelData {

		public float[] vertices;
		public float[] textures;
		public float[] normals;
		public int[] indices;

		public ModelData(float[] vertices, float[] textures, float[] normals, int[] indices) {
			this.vertices = vertices;
			this.textures = textures;
			this.normals = normals;
			this.indices = indices;
		}

	}

}
