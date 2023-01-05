package kaba4cow.engine.renderEngine.renderers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kaba4cow.engine.assets.Models;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.shaders.ParticleShader;
import kaba4cow.engine.renderEngine.textures.ParticleTexture;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.utils.GLUtils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class ParticleRenderer extends AbstractRenderer {

	private Map<ParticleTexture, LinkedList<Particle>> map = new HashMap<ParticleTexture, LinkedList<Particle>>();

	public ParticleRenderer(Renderer renderer) {
		super(renderer, ParticleShader.get());
	}

	public void render(Particle particle) {
		ParticleTexture texture = particle.getTexture();
		LinkedList<Particle> list = map.get(texture);
		if (list == null) {
			list = new LinkedList<Particle>();
			map.put(texture, list);
		}
		list.add(particle);
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		Matrix4f viewMatrix = renderer.getViewMatrix();
		ParticleShader shader = getShader();
		Vector3f textureInfo = new Vector3f();
		Vector3f defaultTint = new Vector3f(1f, 1f, 1f);
		for (ParticleTexture texture : map.keySet()) {
			if (texture == null)
				continue;
			GLUtils.activeTexture(0);
			GLUtils.bindTexture2D(texture.getTexture());
			if (texture.isAdditive())
				GLUtils.additiveBlending();
			else
				GLUtils.alphaBlending();
			LinkedList<Particle> list = map.get(texture);
			while (!list.isEmpty()) {
				Particle particle = list.removeFirst();
				textureInfo.x = texture.getNumberOfRows();
				textureInfo.y = particle.getBlendFactor();
				textureInfo.z = particle.getBrightness();
				shader.modelViewMatrix.loadValue(createModelViewMatrix(
						particle, viewMatrix));
				shader.texInfo.loadValue(textureInfo);
				shader.texOffset1.loadValue(particle.getTexOffset1());
				shader.texOffset2.loadValue(particle.getTexOffset2());
				shader.tint.loadValue(particle.getTint() == null ? defaultTint
						: particle.getTint());
				GLUtils.drawArraysTriangleStrip(Models.getParticleQuad()
						.getVertexCount());
			}
		}
		map.clear();
		finishRendering();
	}

	@Override
	protected void startRendering() {
		shader.start();
		if (Renderer.USE_PROJ_VIEW_MATRICES)
			getShader().projectionMatrix.loadValue(renderer
					.getProjectionMatrix());
		else
			getShader().projectionMatrix.loadValue(new Matrix4f());
		GLUtils.bindVertexArray(Models.getParticleQuad().getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableBlending();
		GLUtils.disableDepthMask();
	}

	@Override
	protected void finishRendering() {
		GLUtils.enableDepthMask();
		GLUtils.disableBlending();
		GLUtils.disableVertexAttribArray(0);
		GLUtils.unbindVertexArray();
		shader.stop();
	}

	@Override
	public ParticleShader getShader() {
		return (ParticleShader) shader;
	}

	private Matrix4f createModelViewMatrix(Particle particle,
			Matrix4f viewMatrix) {
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.translate(particle.getPos(), modelMatrix, modelMatrix);
		modelMatrix.m00 = viewMatrix.m00;
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate(particle.getRotation(), Vectors.FORWARD, modelMatrix,
				modelMatrix);
		Matrices.scale(modelMatrix, particle.getScale());
		return Matrix4f.mul(viewMatrix, modelMatrix, null);
	}

}
