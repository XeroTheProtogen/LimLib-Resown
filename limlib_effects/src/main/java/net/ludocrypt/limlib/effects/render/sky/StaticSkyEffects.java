package net.ludocrypt.limlib.effects.render.sky;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.SkyProperties;

/**
 * A Sky effects controller
 * <p>
 * This is a simplification of the base {@link SkyEffects} class, where each
 * setting is a static, non-changing value
 */
public class StaticSkyEffects extends SkyEffects {

	public static final Codec<StaticSkyEffects> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.FLOAT.optionalFieldOf("cloud_height").stable().forGetter((effects) -> {
			return effects.cloudHeight;
		}), Codec.BOOL.fieldOf("alternate_sky_color").stable().forGetter((effects) -> {
			return effects.alternateSkyColor;
		}), Codec.STRING.fieldOf("sky_type").stable().forGetter((effects) -> {
			return effects.skyType;
		}), Codec.BOOL.fieldOf("brighten_lighting").stable().forGetter((effects) -> {
			return effects.brightenLighting;
		}), Codec.BOOL.fieldOf("darkened").stable().forGetter((effects) -> {
			return effects.darkened;
		}), Codec.BOOL.fieldOf("thick_fog").stable().forGetter((effects) -> {
			return effects.thickFog;
		}), Codec.FLOAT.fieldOf("sky_shading").stable().forGetter((effects) -> {
			return effects.skyShading;
		})).apply(instance, instance.stable(StaticSkyEffects::new));
	});

	private final Optional<Float> cloudHeight;
	private final boolean alternateSkyColor;
	private final String skyType;
	private final boolean brightenLighting;
	private final boolean darkened;
	private final boolean thickFog;
	private final float skyShading;

	public StaticSkyEffects(Optional<Float> cloudHeight, boolean alternateSkyColor, String skyType, boolean brightenLighting, boolean darkened, boolean thickFog, float skyShading) {
		this.cloudHeight = cloudHeight;
		this.alternateSkyColor = alternateSkyColor;
		this.skyType = skyType;
		this.brightenLighting = brightenLighting;
		this.darkened = darkened;
		this.thickFog = thickFog;
		this.skyShading = skyShading;
	}

	public Codec<? extends SkyEffects> getCodec() {
		return CODEC;
	}

	public float getCloudHeight() {
		return cloudHeight.orElse(Float.NaN);
	}

	public boolean hasAlternateSkyColor() {
		return alternateSkyColor;
	}

	public String getSkyType() {
		return skyType;
	}

	public boolean shouldBrightenLighting() {
		return brightenLighting;
	}

	public boolean isDarkened() {
		return darkened;
	}

	public boolean hasThickFog() {
		return thickFog;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public SkyProperties getSkyProperties() {
		return SkyPropertiesCreator.create(getCloudHeight(), hasAlternateSkyColor(), getSkyType(), shouldBrightenLighting(), isDarkened(), hasThickFog());
	}

	@Override
	public float getSkyShading() {
		return skyShading;
	}

}
