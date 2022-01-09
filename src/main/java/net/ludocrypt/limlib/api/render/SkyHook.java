package net.ludocrypt.limlib.api.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public abstract class SkyHook {

	public abstract void renderSky(WorldRenderer worldRenderer, MinecraftClient client, MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta);

	public static class RegularSky extends SkyHook {
		@Override
		public void renderSky(WorldRenderer worldRenderer, MinecraftClient client, MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta) {
		}
	}

	public static class SkyboxSky extends SkyHook {

		public final Identifier identifier;

		public SkyboxSky(Identifier identifier) {
			this.identifier = identifier;
		}

		@Override
		public void renderSky(WorldRenderer worldRenderer, MinecraftClient client, MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());

			Vec3d color = client.world.getSkyColor(client.gameRenderer.getCamera().getPos(), tickDelta).multiply(255);
			int r = (int) Math.floor(color.x);
			int g = (int) Math.floor(color.y);
			int b = (int) Math.floor(color.z);
			int a = 255;
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

			for (int i = 0; i < 6; ++i) {
				matrices.push();
				if (i == 0) {
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				}

				if (i == 1) {
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				}

				if (i == 2) {
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
				}

				if (i == 3) {
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
				}

				if (i == 4) {
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
				}

				Matrix4f matrix4f = matrices.peek().getPositionMatrix();

				RenderSystem.setShaderTexture(0, new Identifier(identifier.toString() + "_" + i + ".png"));
				bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(r, g, b, a).next();
				bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 1.0F).color(r, g, b, a).next();
				bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(1.0F, 1.0F).color(r, g, b, a).next();
				bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(1.0F, 0.0F).color(r, g, b, a).next();
				bufferBuilder.end();
				BufferRenderer.draw(bufferBuilder);
				matrices.pop();
			}

			RenderSystem.depthMask(true);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}

	}

}
