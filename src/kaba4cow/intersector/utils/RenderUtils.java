package kaba4cow.intersector.utils;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.renderEngine.textures.TextureAtlas;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Easing;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.intersector.files.ContainerFile;
import kaba4cow.intersector.files.ContainerGroupFile;
import kaba4cow.intersector.files.GameFile;
import kaba4cow.intersector.files.InfosFile;
import kaba4cow.intersector.files.ShipFile;
import kaba4cow.intersector.files.TextureSetFile;
import kaba4cow.intersector.files.WeaponFile;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.Planet;
import kaba4cow.intersector.gameobjects.cargo.Cargo;
import kaba4cow.intersector.gameobjects.cargo.CargoObject;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.objectcomponents.ColliderComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.ContainerComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.PortComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.ThrustComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.WeaponComponent;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.renderEngine.ThrustModel;

public final class RenderUtils {

	private RenderUtils() {

	}

	public static void renderMachine(Vector3f machineColor,
			TexturedModel shipModel, TexturedModel glassModel, Matrix4f matrix,
			RendererContainer renderers) {
		renderers.getMachineRenderer().render(shipModel, machineColor, matrix);
		renderers.getMachineRenderer().render(glassModel, machineColor, matrix);
	}

	private static WeaponComponent tempWeaponComponent = null;
	private static WeaponFile tempWeaponFile = null;
	private static final Direction tempDirection = new Direction();
	private static final Matrix4f tempDirectionMatrix = new Matrix4f();
	private static final Vector3f tempPos = new Vector3f();
	private static final Vector3f tempSize = new Vector3f();
	private static final Vector3f tempOriginPoint = new Vector3f();

	public static void renderWeapons(Vector3f machineColor, String textureSet,
			float shipSize, Matrix4f shipMatrix, RendererContainer renderers,
			WeaponComponent... weapons) {
		renderWeapons(machineColor, textureSet, shipSize, shipMatrix,
				renderers, false, weapons);
	}

	public static void renderWeapons(Vector3f machineColor, String textureSet,
			float shipSize, Matrix4f shipMatrix, RendererContainer renderers,
			boolean renderTargetInfo, WeaponComponent... weapons) {
		if (shipMatrix == null || GameFile.isNull(textureSet)
				|| weapons == null || weapons.length == 0)
			return;
		Matrix4f matrix = null;
		float invShipSize = 1f / shipSize;
		String metalTexture = TextureSetFile.get(textureSet).getMetalTexture();
		TexturedModel model = null;
		for (int i = 0; i < weapons.length; i++) {
			tempWeaponComponent = weapons[i];
			tempWeaponFile = WeaponFile.get(tempWeaponComponent.weaponName);
			tempDirection.set(tempWeaponComponent.getDirection());
			Matrices.set(tempDirectionMatrix,
					tempDirection.getMatrix(null, true));
			tempPos.set(tempWeaponComponent.pos);
			Vectors.set(tempSize, tempWeaponFile.getSize() * invShipSize);
			tempOriginPoint.set(tempWeaponFile.getOriginPoint());
			Matrices.transform(tempDirectionMatrix, tempOriginPoint,
					tempOriginPoint);

			model = tempWeaponFile.getTexturedStaticModel(metalTexture);
			if (model != null) {
				matrix = new Matrix4f();
				matrix.translate(tempPos.negate(null));
				matrix.scale(tempSize);
				Matrix4f.mul(shipMatrix, matrix, matrix);
				Matrix4f.mul(matrix, tempDirectionMatrix, matrix);

				renderers.getMachineRenderer().render(model, machineColor,
						matrix);
			}

			model = tempWeaponFile.getTexturedYawModel(metalTexture);
			if (model != null) {
				matrix = new Matrix4f();
				matrix.translate(tempPos.negate(null));
				matrix.scale(tempSize);
				Matrix4f.rotate(tempWeaponComponent.yaw, tempDirection.getUp(),
						matrix, matrix);
				Matrix4f.mul(shipMatrix, matrix, matrix);
				Matrix4f.mul(matrix, tempDirectionMatrix, matrix);

				renderers.getMachineRenderer().render(model, machineColor,
						matrix);
			}

			model = tempWeaponFile.getTexturedPitchModel(metalTexture);
			if (model != null) {
				matrix = new Matrix4f();
				matrix.translate(tempPos.negate(null));
				matrix.scale(tempSize);
				Matrix4f.rotate(tempWeaponComponent.yaw, tempDirection.getUp(),
						matrix, matrix);
				matrix.translate(tempOriginPoint.negate(null));
				Matrix4f.rotate(tempWeaponComponent.pitch,
						tempDirection.getRight(), matrix, matrix);
				matrix.translate(tempOriginPoint);
				Matrix4f.mul(shipMatrix, matrix, matrix);
				Matrix4f.mul(matrix, tempDirectionMatrix, matrix);

				renderers.getMachineRenderer().render(model, machineColor,
						matrix);
			}

			if (renderTargetInfo) {
				matrix = new Matrix4f();
				matrix.translate(tempPos.negate(null));
				matrix.scale(tempSize);
				Matrix4f.mul(shipMatrix, matrix, matrix);
				Matrix4f.mul(matrix, tempDirectionMatrix, matrix);

				renderers.getDebugRenderer().render(
						matrix,
						tempWeaponComponent.copyTarget ? new Vector3f(0f,
								0.25f, 0f) : new Vector3f(0.25f, 0f, 0f));
			}
		}
	}

	public static void renderThrusts(ThrustModel thrustModel, float brightness,
			float scaleZ, float shipSize, Matrix4f shipMatrix,
			RendererContainer renderers, ThrustComponent... thrusts) {
		if (brightness <= 0f || shipSize <= 0f || shipMatrix == null
				|| thrustModel == null || thrusts == null
				|| thrusts.length == 0)
			return;
		Matrix4f matrix = null;
		float thrustSpeed = thrustModel.getTexture().getSpeed();
		float thrustBrightness = Easing.EASE_OUT_EXPO.getValue(brightness);
		float thrustScale = Maths.max(thrustBrightness, 0.5f) + 0.5f;
		for (int i = 0; i < thrusts.length; i++) {
			tempPos.set(thrusts[i].pos);
			Vectors.set(tempSize, thrusts[i].size);
			tempSize.z *= scaleZ * thrustScale;
			matrix = new Matrix4f();
			matrix.translate(tempPos.negate(null));
			matrix.scale(tempSize);
			matrix.rotate(thrusts[i].rotation, Vectors.FORWARD);
			Matrix4f.mul(shipMatrix, matrix, matrix);
			renderers.getThrustRenderer().render(thrustModel, matrix,
					Maths.limit(thrustBrightness), thrustSpeed);
		}
	}

	public static void renderCargos(boolean random, float shipSize,
			Matrix4f shipMatrix, RendererContainer renderers,
			ContainerComponent... cargos) {
		if (shipSize <= 0f || shipMatrix == null || cargos == null
				|| cargos.length == 0)
			return;
		Matrix4f matrix = null;
		ContainerComponent cargo = null;
		ContainerGroupFile cargoGroupFile = null;
		ContainerFile cargoFile = null;
		float invShipSize = 1f / shipSize;
		for (int i = 0; i < cargos.length; i++) {
			cargo = cargos[i];
			cargoGroupFile = cargo.containerGroupFile;
			if (random)
				cargoFile = ContainerFile.get(cargoGroupFile.getContainer(i));
			else
				cargoFile = ContainerFile.get(cargoGroupFile.getContainer(0));
			matrix = cargo.getDirection().getMatrix(cargo.pos, true,
					cargoGroupFile.getSize() * invShipSize);
			Matrix4f.mul(shipMatrix, matrix, matrix);

			renderers.getModelRenderer().render(
					cargoFile.getTexturedModel(i
							% cargoFile.getTextures().length), null, matrix);
		}
	}

	public static void renderColliders(float shipSize, Matrix4f shipMatrix,
			RendererContainer renderers, ColliderComponent... colliders) {
		if (shipSize <= 0f || shipMatrix == null || colliders == null
				|| colliders.length == 0)
			return;
		Matrix4f tempMatrix = null;
		Vector3f colorWeak = new Vector3f(0.5f, 0f, 0f);
		Vector3f colorStrong = new Vector3f(0f, 0.5f, 0f);
		Vector3f color = null;
		for (int i = 0; i < colliders.length; i++) {
			tempPos.set(colliders[i].pos);
			Vectors.set(tempSize, colliders[i].size);
			tempMatrix = new Matrix4f();
			tempMatrix.translate(tempPos.negate(null));
			tempMatrix.scale(tempSize);
			Matrix4f.mul(shipMatrix, tempMatrix, tempMatrix);
			color = Maths.blend(colorStrong, colorWeak, Maths.map(
					colliders[i].strength, ColliderComponent.MIN_STRENGTH,
					ColliderComponent.MAX_STRENGTH, 0f, 1f), null);
			renderers.getDebugRenderer().render(tempMatrix, color);
		}
	}

	public static void renderPorts(Vector3f machineColor, String textureSet,
			float shipSize, Matrix4f shipMatrix, RendererContainer renderers,
			PortComponent... ports) {
		if (shipSize <= 0f || shipMatrix == null || ports == null
				|| ports.length == 0)
			return;
		Matrix4f matrix = null;
		PortComponent port = null;
		Vector3f colorVisible = new Vector3f(0f, 0.25f, 0.05f);
		Vector3f colorInvisible = new Vector3f(0.25f, 0.05f, 0f);
		Vector3f color = null;
		Direction direction = Direction.INIT;
		float invShipSize = 1f / shipSize;
		for (int i = 0; i < ports.length; i++) {
			port = ports[i];
			matrix = direction
					.getMatrix(port.pos, true, port.min * invShipSize);
			Matrix4f.mul(shipMatrix, matrix, matrix);
			color = ports[i].visible ? colorVisible : colorInvisible;
			renderers.getDebugRenderer().render(matrix, color);
			matrix = direction
					.getMatrix(port.pos, true, port.max * invShipSize);
			Matrix4f.mul(shipMatrix, matrix, matrix);
			color = ports[i].visible ? colorVisible : colorInvisible;
			renderers.getDebugRenderer().render(matrix, color);

			List<ShipFile> ships = ShipFile.getList(port.min, port.max);
			if (ships.isEmpty())
				continue;
			ShipFile ship = ships
					.get((int) ((2f * GameUtils.getTime() + i) % ships.size()));
			matrix = direction.getMatrix(port.pos, true, ship.getSize()
					* invShipSize);
			matrix.rotate(port.rotation, direction.getUp());
			Matrix4f.mul(shipMatrix, matrix, matrix);
			renderMachine(machineColor, ship.getMetalTexturedModel(textureSet),
					ship.getGlassTexturedModel(textureSet, ship.isUseLight()),
					matrix, renderers);
		}
	}

	public static void renderDebugPoints(float scale, Vector3f color,
			Matrix4f matrix, RendererContainer renderers, Vector3f... points) {
		if (matrix == null || points == null || points.length == 0)
			return;
		Matrix4f pointMatrix = null;
		Vector3f pointsPos = new Vector3f();
		Vector3f pointScale = new Vector3f(scale, scale, scale);
		for (int i = 0; i < points.length; i++) {
			pointsPos.set(points[i]);
			pointMatrix = new Matrix4f();
			pointMatrix.translate(pointsPos.negate(null));
			pointMatrix.scale(pointScale);
			Matrix4f.mul(matrix, pointMatrix, pointMatrix);
			renderers.getDebugRenderer().render(pointMatrix, color);
		}
	}

	public static void renderDebugPoints(float scale, Matrix4f matrix,
			RendererContainer renderers, Vector3f... points) {
		renderDebugPoints(scale, new Vector3f(0.5f, 0f, 0f), matrix, renderers,
				points);
	}

	public static void renderHologram(String colorInfo,
			TextureAtlas colorTexture, RawModel model, float brightness,
			float scale, Vector2f texOffset, Matrix4f matrix,
			RendererContainer renderers) {
		if (scale <= 0f || brightness <= 0f || matrix == null || model == null)
			return;
		Vector3f color = InfosFile.holograms.data().node(colorInfo)
				.getVector3();
		renderHologram(color, colorTexture, model, brightness, scale,
				texOffset, matrix, renderers);
	}

	public static void renderHologram(Vector3f color,
			TextureAtlas colorTexture, RawModel model, float brightness,
			float scale, Vector2f texOffset, Matrix4f matrix,
			RendererContainer renderers) {
		if (scale <= 0f || brightness <= 0f || matrix == null || model == null)
			return;
		renderers.getHologramRenderer().render(model, color, colorTexture,
				matrix, brightness, scale, texOffset);
	}

	public static void renderHologram(String colorInfo, Cubemap colorCube,
			RawModel model, float brightness, float scale, Vector2f texOffset,
			Matrix4f matrix, RendererContainer renderers) {
		if (scale <= 0f || brightness <= 0f || matrix == null || model == null)
			return;
		Vector3f color = InfosFile.holograms.data().node(colorInfo)
				.getVector3();
		renderHologram(color, colorCube, model, brightness, scale, texOffset,
				matrix, renderers);
	}

	public static void renderHologram(Vector3f color, Cubemap colorCube,
			RawModel model, float brightness, float scale, Vector2f texOffset,
			Matrix4f matrix, RendererContainer renderers) {
		if (scale <= 0f || brightness <= 0f || matrix == null || model == null)
			return;
		renderers.getHologramRenderer().render(model, color, colorCube, matrix,
				brightness, scale, texOffset);
	}

	public static void renderHologramMachine(Vector3f color,
			TexturedModel shipModel, TexturedModel glassModel,
			float brightness, float scale, Matrix4f matrix,
			RendererContainer renderers) {
		if (shipModel != null)
			renderHologram(color, shipModel.getTexture(),
					shipModel.getRawModel(), brightness, scale, null, matrix,
					renderers);
		if (glassModel != null)
			renderHologram(color, glassModel.getTexture(),
					glassModel.getRawModel(), brightness, scale, null, matrix,
					renderers);
	}

	public static void renderHologramMachine(String colorInfo,
			TexturedModel shipModel, TexturedModel glassModel,
			float brightness, float scale, Matrix4f matrix,
			RendererContainer renderers) {
		Vector3f color = InfosFile.holograms.data().node(colorInfo)
				.getVector3();
		renderHologramMachine(color, shipModel, glassModel, brightness, scale,
				matrix, renderers);
	}

	public static void renderHologramWeapons(Vector3f color, String textureSet,
			float brightness, float shipSize, float size, Matrix4f shipMatrix,
			RendererContainer renderers, WeaponComponent... weapons) {
		if (shipMatrix == null || weapons == null || weapons.length == 0)
			return;
		Matrix4f matrix = null;
		float invShipSize = 1f / shipSize;
		String metalTexture = TextureSetFile.get(textureSet).getMetalTexture();
		TexturedModel model = null;
		for (int i = 0; i < weapons.length; i++) {
			tempWeaponComponent = weapons[i];
			tempWeaponFile = WeaponFile.get(tempWeaponComponent.weaponName);
			tempDirection.set(tempWeaponComponent.getDirection());
			Matrices.set(tempDirectionMatrix,
					tempDirection.getMatrix(null, true));
			tempPos.set(tempWeaponComponent.pos);
			Vectors.set(tempSize, tempWeaponFile.getSize() * invShipSize);
			tempOriginPoint.set(tempWeaponFile.getOriginPoint());
			Matrices.transform(tempDirectionMatrix, tempOriginPoint,
					tempOriginPoint);

			model = tempWeaponFile.getTexturedStaticModel(metalTexture);
			if (model != null) {
				matrix = new Matrix4f();
				matrix.translate(tempPos.negate(null));
				matrix.scale(tempSize);
				Matrix4f.mul(shipMatrix, matrix, matrix);
				Matrix4f.mul(matrix, tempDirectionMatrix, matrix);

				renderHologram(color, model.getTexture(), model.getRawModel(),
						brightness, size, null, matrix, renderers);
			}

			model = tempWeaponFile.getTexturedYawModel(metalTexture);
			if (model != null) {
				matrix = new Matrix4f();
				matrix.translate(tempPos.negate(null));
				matrix.scale(tempSize);
				Matrix4f.rotate(tempWeaponComponent.yaw, tempDirection.getUp(),
						matrix, matrix);
				Matrix4f.mul(shipMatrix, matrix, matrix);
				Matrix4f.mul(matrix, tempDirectionMatrix, matrix);

				renderHologram(color, model.getTexture(), model.getRawModel(),
						brightness, size, null, matrix, renderers);
			}

			model = tempWeaponFile.getTexturedPitchModel(metalTexture);
			if (model != null) {
				matrix = new Matrix4f();
				matrix.translate(tempPos.negate(null));
				matrix.scale(tempSize);
				Matrix4f.rotate(tempWeaponComponent.yaw, tempDirection.getUp(),
						matrix, matrix);
				matrix.translate(tempOriginPoint.negate(null));
				Matrix4f.rotate(tempWeaponComponent.pitch,
						tempDirection.getRight(), matrix, matrix);
				matrix.translate(tempOriginPoint);
				Matrix4f.mul(shipMatrix, matrix, matrix);
				Matrix4f.mul(matrix, tempDirectionMatrix, matrix);

				renderHologram(color, model.getTexture(), model.getRawModel(),
						brightness, size, null, matrix, renderers);
			}
		}
	}

	public static void renderHologramWeapons(String colorInfo,
			String textureSet, float brightness, float shipSize, float size,
			Matrix4f shipMatrix, RendererContainer renderers,
			WeaponComponent... weapons) {
		Vector3f color = InfosFile.holograms.data().node(colorInfo)
				.getVector3();
		renderHologramWeapons(color, textureSet, brightness, shipSize, size,
				shipMatrix, renderers, weapons);
	}

	public static void renderHologramObject(GameObject object, Vector3f color,
			float brightness, Matrix4f matrix, RendererContainer renderers) {
		if (object instanceof Machine) {
			Machine machine = (Machine) object;
			RenderUtils.renderHologramMachine(color, machine.getMetalModel(),
					machine.getGlassModel(), brightness, 1f, matrix, renderers);
			RenderUtils.renderHologramWeapons(color, machine.getTextureSet(),
					brightness, machine.getSize(), 1f, matrix, renderers,
					machine.getWeapons());
		} else if (object instanceof Planet) {
			Planet spaceBody = (Planet) object;
			RenderUtils.renderHologram(color, spaceBody.getTerrain(),
					Planet.getModel(1f), brightness, 1f, null, matrix,
					renderers);
		} else if (object instanceof CargoObject) {
			CargoObject cargo = (CargoObject) object;
			if (cargo instanceof Cargo)
				matrix.scale(new Vector3f(0.5f, 0.5f, 0.5f));
			TexturedModel model = cargo.getModel();
			RenderUtils.renderHologram(color, model.getTexture(),
					model.getRawModel(), brightness, 1f, null, matrix,
					renderers);
		}
	}

	public static void renderHologramObject(GameObject object,
			String colorInfo, float brightness, Matrix4f matrix,
			RendererContainer renderers) {
		Vector3f color = InfosFile.holograms.data().node(colorInfo)
				.getVector3();
		renderHologramObject(object, color, brightness, matrix, renderers);
	}

}
