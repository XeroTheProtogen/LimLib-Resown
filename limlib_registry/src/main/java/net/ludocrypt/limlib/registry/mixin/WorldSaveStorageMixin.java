package net.ludocrypt.limlib.registry.mixin;

import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

import net.ludocrypt.limlib.registry.registration.LimlibWorld;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.ServerRegistryLayer;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.storage.WorldSaveStorage;

@Mixin(WorldSaveStorage.class)
public class WorldSaveStorageMixin {

	@ModifyVariable(method = "Lnet/minecraft/world/storage/WorldSaveStorage;readGeneratorProperties(Lcom/mojang/serialization/Dynamic;Lcom/mojang/datafixers/DataFixer;I)Lcom/mojang/serialization/DataResult;", at = @At(value = "STORE"), ordinal = 1)
	private static <T> Dynamic<T> limlib$readGeneratorProperties$datafix(Dynamic<T> in, Dynamic<T> levelData, DataFixer dataFixer, int version) {
		Dynamic<T> dynamic = in;
		for (Entry<RegistryKey<LimlibWorld>, LimlibWorld> entry : LimlibWorld.LIMLIB_WORLD.getEntries()) {
			dynamic = limlib$addDimension(entry.getKey(), entry.getValue(), dynamic);
		}
		return dynamic;
	}

	@Unique
	@SuppressWarnings("unchecked")
	private static <T> Dynamic<T> limlib$addDimension(RegistryKey<LimlibWorld> key, LimlibWorld world, Dynamic<T> in) {
		Dynamic<T> dimensions = in.get("dimensions").orElseEmptyMap();
		if (!dimensions.get(key.getValue().toString()).result().isPresent()) {
			Map<Dynamic<T>, Dynamic<T>> dimensionsMap = Maps.newHashMap(dimensions.getMapValues().result().get());

			DynamicRegistryManager registryManager = ServerRegistryLayer.createLayeredManager().getCompositeManager();

			dimensionsMap.put(dimensions.createString(key.getValue().toString()), new Dynamic<T>(dimensions.getOps(), (T) DimensionOptions.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryManager), world.getDimensionOptionsSupplier().apply(Pair.of(registryManager.getLookup(RegistryKeys.DIMENSION_TYPE).get(), registryManager.getLookup(RegistryKeys.BIOME).get()))).result().get()));
			in = in.set("dimensions", in.createMap(dimensionsMap));
		}
		return in;
	}

}
