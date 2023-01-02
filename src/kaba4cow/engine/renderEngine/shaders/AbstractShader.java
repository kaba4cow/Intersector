package kaba4cow.engine.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.consts.Const;
import kaba4cow.engine.renderEngine.shaders.uniforms.Uniform;
import kaba4cow.engine.toolbox.Loaders;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.engine.utils.ProgramUtils;

import org.lwjgl.opengl.GL20;

public abstract class AbstractShader {

	public static final String SOURCE_PATH = "/kaba4cow/engine/shaders/";

	private final int programID;

	public AbstractShader(String vertexName, String fragmentName,
			boolean loadUserShaders, Const... consts) {
		vertexName = getFullName(vertexName + "Vertex.glsl", loadUserShaders);
		fragmentName = getFullName(fragmentName + "Fragment.glsl",
				loadUserShaders);
		int vertexShaderID = Loaders.loadShader(vertexName,
				GLUtils.VERTEX_SHADER, consts);
		int fragmentShaderID = Loaders.loadShader(fragmentName,
				GLUtils.FRAGMENT_SHADER, consts);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		init();
		GL20.glValidateProgram(programID);
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		Loaders.addShader(this);
	}

	public AbstractShader(String name, boolean loadUserShaders, Const... consts) {
		this(name, name, loadUserShaders, consts);
	}

	public static String getFullName(String name, boolean loadUserShaders) {
		return (loadUserShaders ? ProgramUtils.getShaderLocation()
				: SOURCE_PATH) + name;
	}

	protected abstract void init();

	protected void bindAttributes(String... attributes) {
		for (int i = 0; i < attributes.length; i++)
			GL20.glBindAttribLocation(programID, i, attributes[i]);
		GL20.glLinkProgram(programID);
	}

	protected void storeUniformLocations(Uniform... uniforms) {
		for (int i = 0; i < uniforms.length; i++)
			uniforms[i].storeLocation(programID);
	}

	protected void storeUniformLocations(Uniform[]... uniformArrays) {
		for (int i = 0; i < uniformArrays.length; i++) {
			Uniform[] uniforms = uniformArrays[i];
			for (int j = 0; j < uniforms.length; j++)
				uniforms[j].storeLocation(programID);
		}
	}

	public void connectTextureUnits() {

	}

	public void start() {
		GL20.glUseProgram(programID);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	public void cleanUp() {
		stop();
		GL20.glDeleteProgram(programID);
	}

}
